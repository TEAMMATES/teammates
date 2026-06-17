package teammates.logic.api;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import teammates.common.util.Config;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.common.util.LinksUtil;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.Templates;
import teammates.common.util.Templates.EmailTemplates;
import teammates.common.util.TimeHelper;
import teammates.logic.core.DeadlineExtensionsLogic;
import teammates.logic.core.FeedbackSessionsLogic;
import teammates.logic.core.UsersLogic;
import teammates.storage.entity.AccountVerificationRequest;
import teammates.storage.entity.Course;
import teammates.storage.entity.DeadlineExtension;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.storage.entity.User;

/**
 * Handles operations related to generating emails to be sent from provided templates.
 *
 * @see EmailTemplates
 * @see EmailType
 * @see EmailWrapper
 */
public final class EmailGenerator {
    // feedback action strings
    private static final String FEEDBACK_ACTION_SUBMIT_EDIT_OR_VIEW = "submit, edit or view";
    private static final String FEEDBACK_ACTION_VIEW = "view";
    private static final String FEEDBACK_ACTION_SUBMIT_OR_UPDATE =
                            ", in case you have not submitted yet or wish to update your submission. ";
    private static final String HTML_NO_ACTION_REQUIRED = "<mark>No action is required if you have already submitted</mark>";

    // status-related strings
    private static final String FEEDBACK_STATUS_SESSION_OPEN = "is still open for submissions"
                                            + FEEDBACK_ACTION_SUBMIT_OR_UPDATE + HTML_NO_ACTION_REQUIRED;
    private static final String FEEDBACK_STATUS_SESSION_OPENED = "is now open";
    private static final String FEEDBACK_STATUS_SESSION_CLOSING_SOON = "is closing soon"
                                            + FEEDBACK_ACTION_SUBMIT_OR_UPDATE + HTML_NO_ACTION_REQUIRED;
    private static final String FEEDBACK_STATUS_SESSION_CLOSED = "is now closed for submission";
    private static final String FEEDBACK_STATUS_SESSION_OPENING_SOON = "is due to open soon";

    private static final String DATETIME_DISPLAY_FORMAT = "EEE, dd MMM yyyy, hh:mm a z";

    private static final EmailGenerator instance = new EmailGenerator();

    private final DeadlineExtensionsLogic deLogic = DeadlineExtensionsLogic.inst();
    private final FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
    private final UsersLogic usersLogic = UsersLogic.inst();

    private EmailGenerator() {
        // prevent initialization
    }

    public static EmailGenerator inst() {
        return instance;
    }

    /**
     * Generate Feedback Session Opened emails.
     */
    public List<EmailWrapper> generateFeedbackSessionOpenedEmails(FeedbackSession session) {
        return generateFeedbackSessionOpenedOrClosingSoonEmails(session, EmailType.FEEDBACK_OPENED);
    }

    private List<EmailWrapper> generateFeedbackSessionOpenedOrClosingSoonEmails(
            FeedbackSession session, EmailType emailType) {
        Course course = session.getCourse();
        boolean isEmailNeededForStudents = fsLogic.isFeedbackSessionForUserTypeToAnswer(session, false);
        boolean isEmailNeededForInstructors = fsLogic.isFeedbackSessionForUserTypeToAnswer(session, true);
        List<Instructor> instructorsToNotify = isEmailNeededForStudents
                ? usersLogic.getCoOwnersForCourse(course.getId())
                : new ArrayList<>();
        List<Student> students = isEmailNeededForStudents
                ? usersLogic.getStudentsForCourse(course.getId())
                : new ArrayList<>();
        List<Instructor> instructors = isEmailNeededForInstructors
                ? usersLogic.getInstructorsForCourse(course.getId())
                : new ArrayList<>();

        if (emailType == EmailType.FEEDBACK_CLOSING_SOON) {
            Set<DeadlineExtension> deadlines = session.getDeadlineExtensions();
            Set<UUID> userIds = deadlines.stream()
                    .map(d -> d.getUser().getId())
                    .collect(Collectors.toSet());

            // student.
            students = students.stream()
                    .filter(x -> !userIds.contains(x.getId()))
                    .collect(Collectors.toList());

            // instructor.
            instructors = instructors.stream()
                    .filter(x -> !userIds.contains(x.getId()))
                    .collect(Collectors.toList());
        }

        String status = emailType == EmailType.FEEDBACK_OPENED
                ? FEEDBACK_STATUS_SESSION_OPENED
                : FEEDBACK_STATUS_SESSION_CLOSING_SOON;

        String template = emailType == EmailType.FEEDBACK_OPENED
                ? EmailTemplates.USER_FEEDBACK_SESSION_OPENED.replace("${status}", status)
                : EmailTemplates.USER_FEEDBACK_SESSION.replace("${status}", status);

        return generateFeedbackSessionEmailBases(course, session, students, instructors, instructorsToNotify, template,
                emailType, FEEDBACK_ACTION_SUBMIT_EDIT_OR_VIEW);
    }

    /**
     * Generates the feedback session opening soon emails for the given {@code session}.
     *
     * <p>This is useful for e.g. in case the feedback session opening info was set wrongly.
     */
    public List<EmailWrapper> generateFeedbackSessionOpeningSoonEmails(FeedbackSession session) {
        return generateFeedbackSessionOpeningSoonOrClosedEmails(session, EmailType.FEEDBACK_OPENING_SOON);
    }

    private List<EmailWrapper> generateFeedbackSessionOpeningSoonOrClosedEmails(
            FeedbackSession session, EmailType emailType) {
        Course course = session.getCourse();
        // Notify only course co-owners
        List<Instructor> coOwners = usersLogic.getCoOwnersForCourse(course.getId());
        return coOwners.stream()
                .map(coOwner -> generateFeedbackSessionEmailBaseForCoowner(course, session, coOwner, emailType))
                .collect(Collectors.toList());
    }

    private EmailWrapper generateFeedbackSessionEmailBaseForCoowner(
            Course course, FeedbackSession session, Instructor coOwner, EmailType emailType) {
        String additionalNotes;
        String status;
        if (emailType == EmailType.FEEDBACK_OPENING_SOON) {
            String editUrl = LinksUtil.getInstructorSessionEditUrl(session.getId());
            // If instructor has not joined the course, populate additional notes with information to join course.
            if (coOwner.isRegistered()) {
                additionalNotes = fillUpEditFeedbackSessionDetailsFragment(editUrl);
            } else {
                additionalNotes = fillUpJoinCourseBeforeEditFeedbackSessionDetailsFragment(editUrl,
                        LinksUtil.getInstructorCourseJoinUrl(coOwner.getRegKey()));
            }
            status = FEEDBACK_STATUS_SESSION_OPENING_SOON;
        } else {
            String reportUrl = LinksUtil.getInstructorSessionReportUrl(session.getId());
            additionalNotes = fillUpViewResponsesDetailsFragment(reportUrl);
            status = FEEDBACK_STATUS_SESSION_CLOSED;
        }

        Instant startTime = TimeHelper.getMidnightAdjustedInstantBasedOnZone(
                session.getStartTime(), session.getCourse().getTimeZone(), false);
        Instant endTime = TimeHelper.getMidnightAdjustedInstantBasedOnZone(
                session.getEndTime(), session.getCourse().getTimeZone(), false);
        String emailBody = Templates.populateTemplate(EmailTemplates.OWNER_FEEDBACK_SESSION,
                "${status}", status,
                "${userName}", SanitizationHelper.sanitizeForHtml(coOwner.getName()),
                "${courseName}", SanitizationHelper.sanitizeForHtml(course.getName()),
                "${courseId}", SanitizationHelper.sanitizeForHtml(course.getId()),
                "${feedbackSessionName}", SanitizationHelper.sanitizeForHtml(session.getName()),
                "${deadline}", SanitizationHelper.sanitizeForHtml(
                        TimeHelper.formatInstant(endTime, session.getCourse().getTimeZone(), DATETIME_DISPLAY_FORMAT)),
                "${sessionInstructions}", session.getInstructionsString(),
                "${startTime}", SanitizationHelper.sanitizeForHtml(
                        TimeHelper.formatInstant(startTime, session.getCourse().getTimeZone(), DATETIME_DISPLAY_FORMAT)),
                "${additionalNotes}", additionalNotes);

        EmailWrapper email = getEmptyEmailAddressedToEmail(coOwner.getEmail());
        email.setType(emailType);
        email.setSubjectFromType(course.getName(), session.getName());
        email.setContent(emailBody);
        return email;
    }

    /**
     * Generates the fragment for instructions on how to edit details for feedback session at {@code editUrl}.
     */
    private String fillUpEditFeedbackSessionDetailsFragment(String editUrl) {
        return Templates.populateTemplate(EmailTemplates.FRAGMENT_OPENING_SOON_EDIT_DETAILS,
                "${sessionEditUrl}", editUrl);
    }

    /**
     * Generates the fragment for instructions on how to view responses for feedback session at {@code reportUrl}.
     */
    private String fillUpViewResponsesDetailsFragment(String reportUrl) {
        return Templates.populateTemplate(EmailTemplates.FRAGMENT_CLOSED_VIEW_RESPONSES,
                "${reportUrl}", reportUrl);
    }

    /**
     * Generates the fragment for instructions on how to edit details for feedback session at {@code editUrl} and
     * how to join the course at {@code joinUrl}.
     */
    private String fillUpJoinCourseBeforeEditFeedbackSessionDetailsFragment(String editUrl, String joinUrl) {
        return Templates.populateTemplate(EmailTemplates.FRAGMENT_OPENING_SOON_JOIN_COURSE_BEFORE_EDIT_DETAILS,
                "${sessionEditUrl}", editUrl,
                "${joinUrl}", joinUrl
        );
    }

    /**
     * Generates the feedback session reminder emails for the given {@code session} for {@code students}
     * and {@code instructorsToRemind}. In addition, the emails will also be forwarded to {@code instructorsToNotify}.
     */
    public List<EmailWrapper> generateFeedbackSessionReminderEmails(
            FeedbackSession session, List<Student> students,
            List<Instructor> instructorsToRemind, Instructor instructorToNotify) {

        Course course = session.getCourse();
        String template = EmailTemplates.USER_FEEDBACK_SESSION.replace("${status}", FEEDBACK_STATUS_SESSION_OPEN);
        List<Instructor> instructorToNotifyAsList = new ArrayList<>();
        if (instructorToNotify != null) {
            instructorToNotifyAsList.add(instructorToNotify);
        }

        return generateFeedbackSessionEmailBases(course, session, students, instructorsToRemind, instructorToNotifyAsList,
                template, EmailType.FEEDBACK_SESSION_REMINDER, FEEDBACK_ACTION_SUBMIT_EDIT_OR_VIEW);
    }

    /**
     * Generates the email containing the summary of the feedback sessions
     * email for the given {@code user}.
     *
     * @param user - User to send feedback session summary to
     * @param emailType - The email type which corresponds to the reason behind why the links are being resent
     */
    public EmailWrapper generateFeedbackSessionSummaryOfCourse(User user, EmailType emailType) {
        Objects.requireNonNull(user);
        if (emailType != EmailType.STUDENT_EMAIL_CHANGED
                && emailType != EmailType.STUDENT_COURSE_LINKS_REGENERATED
                && emailType != EmailType.INSTRUCTOR_COURSE_LINKS_REGENERATED) {
            throw new IllegalArgumentException("Unsupported email type: " + emailType);
        }

        String userEmail = user.getEmail();
        Course course = user.getCourse();

        boolean isYetToJoinCourse = user.getAccount() == null;
        String userKey = user.getRegKey();
        String userName = user.getName();

        String joinUrl;
        String joinFragmentTemplate;
        if (user instanceof Instructor) {
            joinUrl = LinksUtil.getInstructorCourseJoinUrl(userKey);
            joinFragmentTemplate = EmailTemplates.FRAGMENT_INSTRUCTOR_COURSE_REJOIN_AFTER_REGKEY_RESET;
        } else if (user instanceof Student) {
            joinUrl = LinksUtil.getStudentCourseJoinUrl(userKey);
            joinFragmentTemplate = emailType == EmailType.STUDENT_EMAIL_CHANGED
                    ? EmailTemplates.FRAGMENT_STUDENT_COURSE_JOIN
                    : EmailTemplates.FRAGMENT_STUDENT_COURSE_REJOIN_AFTER_REGKEY_RESET;
        } else {
            throw new IllegalArgumentException("Unsupported user type: " + user.getClass().getSimpleName());
        }

        List<FeedbackSession> sessions = new ArrayList<>();
        List<FeedbackSession> fsInCourse = fsLogic.getFeedbackSessionsForCourse(course.getId());

        for (FeedbackSession fs : fsInCourse) {
            if (fs.isOpenedEmailSent() || fs.isPublishedEmailSent()) {
                sessions.add(fs);
            }
        }

        StringBuilder linksFragmentValue = new StringBuilder(1000);
        String joinFragmentValue = isYetToJoinCourse
                ? Templates.populateTemplate(joinFragmentTemplate,
                        "${joinUrl}", joinUrl,
                        "${courseName}", SanitizationHelper.sanitizeForHtml(course.getName()),
                        "${coOwnersEmails}", generateCoOwnersEmailsLine(course.getId()),
                        "${supportEmail}", Config.SUPPORT_EMAIL)
                : "";

        for (FeedbackSession fs : sessions) {
            String submitUrlHtml = "(Feedback session is not yet opened)";
            String reportUrlHtml = "(Feedback session is not yet published)";

            if (fs.isOpened() || fs.isClosed()) {
                String submitUrl = user instanceof Instructor
                        ? LinksUtil.getInstructorSessionSubmitUrl(fs.getId(), userKey)
                        : LinksUtil.getStudentSessionSubmitUrl(fs.getId(), userKey);
                submitUrlHtml = "<a href=\"" + submitUrl + "\">" + submitUrl + "</a>";
            }

            if (fs.isPublished()) {
                String reportUrl = user instanceof Instructor
                        ? LinksUtil.getInstructorSessionResultsUrl(fs.getId(), userKey)
                        : LinksUtil.getStudentSessionResultsUrl(fs.getId(), userKey);
                reportUrlHtml = "<a href=\"" + reportUrl + "\">" + reportUrl + "</a>";
            }

            Instant endTime = TimeHelper.getMidnightAdjustedInstantBasedOnZone(
                    fs.getEndTime(), fs.getCourse().getTimeZone(), false);
            linksFragmentValue.append(Templates.populateTemplate(
                    EmailTemplates.FRAGMENT_SINGLE_FEEDBACK_SESSION_LINKS,
                    "${feedbackSessionName}", fs.getName(),
                    "${deadline}", TimeHelper.formatInstant(endTime, fs.getCourse().getTimeZone(), DATETIME_DISPLAY_FORMAT)
                            + (fs.isClosed() ? " (Passed)" : ""),
                    "${submitUrl}", submitUrlHtml,
                    "${reportUrl}", reportUrlHtml));
        }

        if (linksFragmentValue.isEmpty()) {
            linksFragmentValue.append("No links found.");
        }

        String additionalContactInformation = getAdditionalContactInformationFragment(course, user instanceof Instructor);
        String resendLinksTemplate = emailType == EmailType.STUDENT_EMAIL_CHANGED
                ? Templates.EmailTemplates.USER_FEEDBACK_SESSION_RESEND_ALL_LINKS
                : Templates.EmailTemplates.USER_REGKEY_REGENERATION_RESEND_ALL_COURSE_LINKS;

        String emailBody = Templates.populateTemplate(resendLinksTemplate,
                "${userName}", SanitizationHelper.sanitizeForHtml(userName),
                "${userEmail}", userEmail,
                "${courseName}", SanitizationHelper.sanitizeForHtml(course.getName()),
                "${courseId}", course.getId(),
                "${joinFragment}", joinFragmentValue,
                "${linksFragment}", linksFragmentValue.toString(),
                "${additionalContactInformation}", additionalContactInformation);

        EmailWrapper email = getEmptyEmailAddressedToEmail(userEmail);
        email.setContent(emailBody);
        email.setType(emailType);
        email.setSubjectFromType(course.getName(), course.getId());
        return email;
    }

    /**
     * Generates the feedback session closing soon emails for the given {@code session}.
     *
     * <p>Students and instructors with deadline extensions are not notified.
     */
    public List<EmailWrapper> generateFeedbackSessionClosingSoonEmails(FeedbackSession session) {
        return generateFeedbackSessionOpenedOrClosingSoonEmails(session, EmailType.FEEDBACK_CLOSING_SOON);
    }

    /**
     * Generates the feedback session closed emails for the given {@code session}.
     */
    public List<EmailWrapper> generateFeedbackSessionClosedEmails(FeedbackSession session) {
        return generateFeedbackSessionOpeningSoonOrClosedEmails(session, EmailType.FEEDBACK_CLOSED);
    }

    /**
     * Generates the feedback session closing soon emails for users with deadline extensions.
    */
    public List<EmailWrapper> generateFeedbackSessionClosingWithExtensionEmails(
            FeedbackSession session, List<DeadlineExtension> deadlineExtensions) {
        Course course = session.getCourse();

        boolean isEmailNeededForStudents =
                !deadlineExtensions.isEmpty() && fsLogic.isFeedbackSessionForUserTypeToAnswer(session, false);
        boolean isEmailNeededForInstructors =
                !deadlineExtensions.isEmpty() && fsLogic.isFeedbackSessionForUserTypeToAnswer(session, true);

        List<User> usersWithExtensions = deadlineExtensions.stream()
                .map(DeadlineExtension::getUser)
                .toList();

        String template = EmailTemplates.USER_FEEDBACK_SESSION.replace("${status}", FEEDBACK_STATUS_SESSION_CLOSING_SOON);
        EmailType type = EmailType.FEEDBACK_CLOSING_SOON;
        String feedbackAction = FEEDBACK_ACTION_SUBMIT_EDIT_OR_VIEW;
        List<EmailWrapper> emails = new ArrayList<>();
        for (User user : usersWithExtensions) {
            if (isEmailNeededForStudents && user instanceof Student student) {
                emails.addAll(generateFeedbackSessionEmailBases(course, session, Collections.singletonList(student),
                        Collections.emptyList(), Collections.emptyList(), template, type, feedbackAction));
            }
            if (isEmailNeededForInstructors && user instanceof Instructor instructor) {
                emails.addAll(generateFeedbackSessionEmailBases(course, session, Collections.emptyList(),
                        Collections.singletonList(instructor), Collections.emptyList(), template, type,
                        feedbackAction));
            }
        }
        return emails;
    }

    /**
     * Generates the feedback session published emails for the given {@code session}.
     */
    public List<EmailWrapper> generateFeedbackSessionPublishedEmails(FeedbackSession session) {
        return generateFeedbackSessionPublishedOrUnpublishedEmails(session, EmailType.FEEDBACK_PUBLISHED);
    }

    /**
     * Generates the feedback session published emails for the given {@code students} and
     * {@code instructors} in {@code session}.
     */
    public List<EmailWrapper> generateFeedbackSessionPublishedEmails(FeedbackSession session,
            List<Student> students, List<Instructor> instructors,
            List<Instructor> instructorsToNotify) {
        return generateFeedbackSessionPublishedOrUnpublishedEmails(
                session, students, instructors, instructorsToNotify, EmailType.FEEDBACK_PUBLISHED);
    }

    /**
     * Generates the feedback session unpublished emails for the given {@code session}.
     */
    public List<EmailWrapper> generateFeedbackSessionUnpublishedEmails(FeedbackSession session) {
        return generateFeedbackSessionPublishedOrUnpublishedEmails(session, EmailType.FEEDBACK_UNPUBLISHED);
    }

    private List<EmailWrapper> generateFeedbackSessionPublishedOrUnpublishedEmails(
            FeedbackSession session, EmailType emailType) {
        boolean isEmailNeededForStudents = fsLogic.isFeedbackSessionViewableToUserType(session, false);
        boolean isEmailNeededForInstructors = fsLogic.isFeedbackSessionViewableToUserType(session, true);
        List<Instructor> instructorsToNotify = isEmailNeededForStudents
                ? usersLogic.getCoOwnersForCourse(session.getCourseId())
                : new ArrayList<>();
        List<Student> students = isEmailNeededForStudents
                ? usersLogic.getStudentsForCourse(session.getCourseId())
                : new ArrayList<>();
        List<Instructor> instructors = isEmailNeededForInstructors
                ? usersLogic.getInstructorsForCourse(session.getCourseId())
                : new ArrayList<>();

        return generateFeedbackSessionPublishedOrUnpublishedEmails(
                session, students, instructors, instructorsToNotify, emailType);
    }

    private List<EmailWrapper> generateFeedbackSessionPublishedOrUnpublishedEmails(
            FeedbackSession session, List<Student> students,
            List<Instructor> instructors, List<Instructor> instructorsToNotify, EmailType emailType) {
        Course course = session.getCourse();
        String template;
        String action;
        if (emailType == EmailType.FEEDBACK_PUBLISHED) {
            template = EmailTemplates.USER_FEEDBACK_SESSION_PUBLISHED;
            action = FEEDBACK_ACTION_VIEW;
        } else {
            template = EmailTemplates.USER_FEEDBACK_SESSION_UNPUBLISHED;
            action = FEEDBACK_ACTION_SUBMIT_EDIT_OR_VIEW;
        }

        return generateFeedbackSessionEmailBases(course, session, students, instructors, instructorsToNotify, template,
                emailType, action);
    }

    private List<EmailWrapper> generateFeedbackSessionEmailBases(
            Course course, FeedbackSession session, List<Student> students,
            List<Instructor> instructors, List<Instructor> instructorsToNotify, String template,
            EmailType type, String feedbackAction) {
        StringBuilder studentAdditionalContactBuilder = new StringBuilder();
        StringBuilder instructorAdditionalContactBuilder = new StringBuilder();
        studentAdditionalContactBuilder.append(getAdditionalContactInformationFragment(course, false));
        instructorAdditionalContactBuilder.append(getAdditionalContactInformationFragment(course, true));

        List<EmailWrapper> emails = new ArrayList<>();
        for (Student student : students) {
            emails.add(generateFeedbackSessionEmailBaseForStudents(course, session, student,
                    template, type, feedbackAction, studentAdditionalContactBuilder.toString()));
        }
        for (Instructor instructor : instructors) {
            emails.add(generateFeedbackSessionEmailBaseForInstructors(course, session, instructor,
                    template, type, feedbackAction, instructorAdditionalContactBuilder.toString()));
        }
        for (Instructor instructor : instructorsToNotify) {
            emails.add(generateFeedbackSessionEmailBaseForNotifiedInstructors(course, session, instructor,
                    template, type, feedbackAction, studentAdditionalContactBuilder.toString()));
        }
        return emails;
    }

    private EmailWrapper generateFeedbackSessionEmailBaseForStudents(
            Course course, FeedbackSession session, Student student, String template,
            EmailType type, String feedbackAction, String additionalContactInformation) {
        String submitUrl = LinksUtil.getStudentSessionSubmitUrl(session.getId(), student.getRegKey());
        String reportUrl = LinksUtil.getStudentSessionResultsUrl(session.getId(), student.getRegKey());
        Instant deadline = deLogic.getDeadlineForUser(session, student);

        Instant endTime = TimeHelper.getMidnightAdjustedInstantBasedOnZone(
                deadline, session.getCourse().getTimeZone(), false);
        String emailBody = Templates.populateTemplate(template,
                "${userName}", SanitizationHelper.sanitizeForHtml(student.getName()),
                "${courseName}", SanitizationHelper.sanitizeForHtml(course.getName()),
                "${courseId}", SanitizationHelper.sanitizeForHtml(course.getId()),
                "${feedbackSessionName}", SanitizationHelper.sanitizeForHtml(session.getName()),
                "${deadline}", SanitizationHelper.sanitizeForHtml(
                        TimeHelper.formatInstant(endTime, session.getCourse().getTimeZone(), DATETIME_DISPLAY_FORMAT))
                        + (session.getEndTime().equals(deadline) ? "" : " (after extension)"),
                "${instructorPreamble}", "",
                "${sessionInstructions}", session.getInstructionsString(),
                "${submitUrl}", submitUrl,
                "${reportUrl}", reportUrl,
                "${feedbackAction}", feedbackAction,
                "${additionalContactInformation}", additionalContactInformation);

        EmailWrapper email = getEmptyEmailAddressedToEmail(student.getEmail());
        email.setType(type);
        email.setSubjectFromType(course.getName(), session.getName());
        email.setContent(emailBody);
        return email;
    }

    private EmailWrapper generateFeedbackSessionEmailBaseForInstructors(
            Course course, FeedbackSession session, Instructor instructor,
            String template, EmailType type, String feedbackAction, String additionalContactInformation) {
        String submitUrl = LinksUtil.getInstructorSessionSubmitUrl(session.getId(), instructor.getRegKey());
        String reportUrl = LinksUtil.getInstructorSessionResultsUrl(session.getId(), instructor.getRegKey());
        Instant deadline = deLogic.getDeadlineForUser(session, instructor);

        Instant endTime = TimeHelper.getMidnightAdjustedInstantBasedOnZone(
                deadline, session.getCourse().getTimeZone(), false);
        String emailBody = Templates.populateTemplate(template,
                "${userName}", SanitizationHelper.sanitizeForHtml(instructor.getName()),
                "${courseName}", SanitizationHelper.sanitizeForHtml(course.getName()),
                "${courseId}", SanitizationHelper.sanitizeForHtml(course.getId()),
                "${feedbackSessionName}", SanitizationHelper.sanitizeForHtml(session.getName()),
                "${deadline}", SanitizationHelper.sanitizeForHtml(
                        TimeHelper.formatInstant(endTime, session.getCourse().getTimeZone(), DATETIME_DISPLAY_FORMAT))
                        + (session.getEndTime().equals(deadline) ? "" : " (after extension)"),
                "${instructorPreamble}", "",
                "${sessionInstructions}", session.getInstructionsString(),
                "${submitUrl}", submitUrl,
                "${reportUrl}", reportUrl,
                "${feedbackAction}", feedbackAction,
                "${additionalContactInformation}", additionalContactInformation);

        EmailWrapper email = getEmptyEmailAddressedToEmail(instructor.getEmail());
        email.setType(type);
        email.setSubjectFromType(course.getName(), session.getName());
        email.setContent(emailBody);
        return email;
    }

    private EmailWrapper generateFeedbackSessionEmailBaseForNotifiedInstructors(
            Course course, FeedbackSession session, Instructor instructor,
            String template, EmailType type, String feedbackAction, String additionalContactInformation) {

        Instant endTime = TimeHelper.getMidnightAdjustedInstantBasedOnZone(
                session.getEndTime(), session.getCourse().getTimeZone(), false);
        String emailBody = Templates.populateTemplate(template,
                "${userName}", SanitizationHelper.sanitizeForHtml(instructor.getName()),
                "${courseName}", SanitizationHelper.sanitizeForHtml(course.getName()),
                "${courseId}", SanitizationHelper.sanitizeForHtml(course.getId()),
                "${feedbackSessionName}", SanitizationHelper.sanitizeForHtml(session.getName()),
                "${deadline}", SanitizationHelper.sanitizeForHtml(
                        TimeHelper.formatInstant(endTime, session.getCourse().getTimeZone(), DATETIME_DISPLAY_FORMAT)),
                "${instructorPreamble}", fillUpInstructorPreamble(course, session),
                "${sessionInstructions}", session.getInstructionsString(),
                "${submitUrl}", "{in the actual email sent to the students, this will be the unique link}",
                "${reportUrl}", "{in the actual email sent to the students, this will be the unique link}",
                "${feedbackAction}", feedbackAction,
                "${additionalContactInformation}", additionalContactInformation);

        EmailWrapper email = getEmptyEmailAddressedToEmail(instructor.getEmail());
        email.setType(type);
        email.setIsCopy(true);
        email.setSubjectFromType(course.getName(), session.getName());
        email.setContent(emailBody);
        return email;
    }

    /**
     * Generates the new instructor account join email for the given {@code instructor}.
     */
    public EmailWrapper generateNewInstructorAccountJoinEmail(
            String instructorEmail, String instructorName, String joinUrl) {

        String emailBody = Templates.populateTemplate(EmailTemplates.NEW_INSTRUCTOR_ACCOUNT_WELCOME,
                "${userName}", SanitizationHelper.sanitizeForHtml(instructorName),
                "${joinUrl}", joinUrl);

        EmailWrapper email = getEmptyEmailAddressedToEmail(instructorEmail);
        email.setBcc(Config.SUPPORT_EMAIL);
        email.setType(EmailType.NEW_INSTRUCTOR_ACCOUNT);
        email.setSubjectFromType(SanitizationHelper.sanitizeForHtml(instructorName));
        email.setContent(emailBody);
        return email;
    }

    /**
     * Generates the course join email for the given {@code student} in {@code course}.
     */
    public EmailWrapper generateStudentCourseJoinEmail(Course course, Student student) {

        String emailBody = Templates.populateTemplate(
                fillUpStudentJoinFragment(student),
                "${userName}", SanitizationHelper.sanitizeForHtml(student.getName()),
                "${courseName}", SanitizationHelper.sanitizeForHtml(course.getName()),
                "${coOwnersEmails}", generateCoOwnersEmailsLine(course.getId()),
                "${supportEmail}", Config.SUPPORT_EMAIL);

        EmailWrapper email = getEmptyEmailAddressedToEmail(student.getEmail());
        email.setType(EmailType.STUDENT_COURSE_JOIN);
        email.setSubjectFromType(course.getName(), course.getId());
        email.setContent(emailBody);
        return email;
    }

    /**
     * Generates the course re-join email for the given {@code student} in {@code course}.
     */
    public EmailWrapper generateStudentCourseRejoinEmailAfterUnlinkAccount(
            Course course, Student student) {

        String emailBody = Templates.populateTemplate(
                fillUpStudentRejoinAfterUnlinkAccountFragment(student),
                "${userName}", SanitizationHelper.sanitizeForHtml(student.getName()),
                "${courseName}", SanitizationHelper.sanitizeForHtml(course.getName()),
                "${coOwnersEmails}", generateCoOwnersEmailsLine(course.getId()),
                "${supportEmail}", Config.SUPPORT_EMAIL);

        EmailWrapper email = getEmptyEmailAddressedToEmail(student.getEmail());
        email.setType(EmailType.STUDENT_COURSE_REJOIN_AFTER_UNLINK_ACCOUNT);
        email.setSubjectFromType(course.getName(), course.getId());
        email.setContent(emailBody);
        return email;
    }

    /**
     * Generates the course join email for the given {@code instructor} in {@code course}.
     * Also specifies contact information of {@code inviter}.
     */
    public EmailWrapper generateInstructorCourseJoinEmail(Instructor inviter,
            Instructor instructor, Course course) {

        String emailBody = Templates.populateTemplate(
                fillUpInstructorJoinFragment(instructor),
                "${userName}", SanitizationHelper.sanitizeForHtml(instructor.getName()),
                "${courseName}", SanitizationHelper.sanitizeForHtml(course.getName()),
                "${inviterName}", SanitizationHelper.sanitizeForHtml(inviter.getName()),
                "${inviterEmail}", SanitizationHelper.sanitizeForHtml(inviter.getEmail()),
                "${supportEmail}", Config.SUPPORT_EMAIL);

        EmailWrapper email = getEmptyEmailAddressedToEmail(instructor.getEmail());
        email.setType(EmailType.INSTRUCTOR_COURSE_JOIN);
        email.setSubjectFromType(course.getName(), course.getId());
        email.setContent(emailBody);
        return email;
    }

    /**
     * Generates the course re-join email for the given {@code instructor} in {@code course}.
     */
    public EmailWrapper generateInstructorCourseRejoinEmailAfterUnlinkAccount(
            Instructor instructor, Course course) {

        String emailBody = Templates.populateTemplate(
                fillUpInstructorRejoinAfterUnlinkAccountFragment(instructor),
                "${userName}", SanitizationHelper.sanitizeForHtml(instructor.getName()),
                "${courseName}", SanitizationHelper.sanitizeForHtml(course.getName()),
                "${supportEmail}", Config.SUPPORT_EMAIL);

        EmailWrapper email = getEmptyEmailAddressedToEmail(instructor.getEmail());
        email.setType(EmailType.INSTRUCTOR_COURSE_REJOIN_AFTER_UNLINK_ACCOUNT);
        email.setSubjectFromType(course.getName(), course.getId());
        email.setContent(emailBody);
        return email;
    }

    /**
     * Generates the email to alert the admin of the new {@code accountVerificationRequest}.
     */
    public EmailWrapper generateNewAccountVerificationRequestAdminAlertEmail(
            AccountVerificationRequest accountVerificationRequest) {
        String name = accountVerificationRequest.getName();
        String institute = accountVerificationRequest.getInstitute().getName();
        String emailAddress = accountVerificationRequest.getEmail();
        String comments = accountVerificationRequest.getComments();
        if (comments == null) {
            comments = "";
        }
        String adminAccountVerificationRequestsPageUrl = LinksUtil.getAdminHomePageUrl();
        String[] templateKeyValuePairs = new String[] {
                "${name}", name,
                "${institute}", institute,
                "${emailAddress}", emailAddress,
                "${comments}", comments,
                "${adminAccountVerificationRequestsPageUrl}", adminAccountVerificationRequestsPageUrl,
        };
        String content = Templates.populateTemplate(
                EmailTemplates.ADMIN_NEW_ACCOUNT_VERIFICATION_REQUEST_ALERT, templateKeyValuePairs);
        EmailWrapper email = getEmptyEmailAddressedToEmail(Config.SUPPORT_EMAIL);
        email.setType(EmailType.NEW_ACCOUNT_VERIFICATION_REQUEST_ADMIN_ALERT);
        email.setSubjectFromType();
        email.setContent(content);
        return email;
    }

    /**
     * Generates the acknowledgement email to be sent to the person who submitted {@code accountVerificationRequest}.
     */
    public EmailWrapper generateNewAccountVerificationRequestAcknowledgementEmail(
            AccountVerificationRequest accountVerificationRequest) {
        String name = SanitizationHelper.sanitizeForHtml(accountVerificationRequest.getName());
        String institute = SanitizationHelper.sanitizeForHtml(accountVerificationRequest.getInstitute().getName());
        String emailAddress = SanitizationHelper.sanitizeForHtml(accountVerificationRequest.getEmail());
        String comments = SanitizationHelper.sanitizeForHtml(accountVerificationRequest.getComments());
        if (comments == null) {
            comments = "";
        }
        String[] templateKeyValuePairs = new String[] {
                "${name}", name,
                "${institute}", institute,
                "${emailAddress}", emailAddress,
                "${comments}", comments,
                "${supportEmail}", Config.SUPPORT_EMAIL,
        };
        String content = Templates.populateTemplate(
                EmailTemplates.INSTRUCTOR_NEW_ACCOUNT_VERIFICATION_REQUEST_ACKNOWLEDGEMENT, templateKeyValuePairs);
        EmailWrapper email = getEmptyEmailAddressedToEmail(emailAddress);
        email.setType(EmailType.NEW_ACCOUNT_VERIFICATION_REQUEST_ACKNOWLEDGEMENT);
        email.setSubjectFromType();
        email.setContent(content);
        return email;
    }

    /**
     * Generates the email to be sent to instructor when their account verification request has been rejected by admin.
     */
    public EmailWrapper generateAccountVerificationRequestRejectionEmail(
            AccountVerificationRequest accountVerificationRequest, String title, String content) {
        EmailWrapper email = getEmptyEmailAddressedToEmail(accountVerificationRequest.getEmail());
        email.setType(EmailType.ACCOUNT_VERIFICATION_REQUEST_REJECTION);
        email.setBcc(Config.SUPPORT_EMAIL);
        email.setSubjectFromType(SanitizationHelper.sanitizeTitle(title));
        email.setContent(SanitizationHelper.sanitizeForRichText(content));

        return email;
    }

    /**
     * Generates the course registered email for the user with the given details in {@code course}.
     */
    public EmailWrapper generateUserCourseRegisteredEmail(
            String name, String emailAddress, String googleId, boolean isInstructor, Course course) {
        String emailBody = Templates.populateTemplate(EmailTemplates.USER_COURSE_REGISTER,
                "${userName}", SanitizationHelper.sanitizeForHtml(name),
                "${userType}", isInstructor ? "an instructor" : "a student",
                "${courseId}", SanitizationHelper.sanitizeForHtml(course.getId()),
                "${courseName}", SanitizationHelper.sanitizeForHtml(course.getName()),
                "${googleId}", SanitizationHelper.sanitizeForHtml(googleId),
                "${appUrl}", isInstructor
                        ? LinksUtil.getInstructorHomePageUrl()
                        : LinksUtil.getStudentHomePageUrl(),
                "${supportEmail}", Config.SUPPORT_EMAIL);

        EmailWrapper email = getEmptyEmailAddressedToEmail(emailAddress);
        email.setType(EmailType.USER_COURSE_REGISTER);
        email.setSubjectFromType(course.getName(), course.getId());
        email.setContent(emailBody);
        return email;
    }

    private String fillUpStudentJoinFragment(Student student) {
        return Templates.populateTemplate(EmailTemplates.USER_COURSE_JOIN,
            "${joinFragment}", EmailTemplates.FRAGMENT_STUDENT_COURSE_JOIN,
            "${joinUrl}", LinksUtil.getStudentCourseJoinUrl(student.getRegKey()));
    }

    private String fillUpStudentRejoinAfterUnlinkAccountFragment(Student student) {
        return Templates.populateTemplate(EmailTemplates.USER_COURSE_JOIN,
            "${joinFragment}", EmailTemplates.FRAGMENT_STUDENT_COURSE_REJOIN_AFTER_UNLINK_ACCOUNT,
            "${joinUrl}", LinksUtil.getStudentCourseJoinUrl(student.getRegKey()),
            "${supportEmail}", Config.SUPPORT_EMAIL);
    }

    private String fillUpInstructorJoinFragment(Instructor instructor) {
        return Templates.populateTemplate(EmailTemplates.USER_COURSE_JOIN,
            "${joinFragment}", EmailTemplates.FRAGMENT_INSTRUCTOR_COURSE_JOIN,
            "${joinUrl}", LinksUtil.getInstructorCourseJoinUrl(instructor.getRegKey()));
    }

    private String fillUpInstructorRejoinAfterUnlinkAccountFragment(Instructor instructor) {
        return Templates.populateTemplate(EmailTemplates.USER_COURSE_JOIN,
            "${joinFragment}", EmailTemplates.FRAGMENT_INSTRUCTOR_COURSE_REJOIN_AFTER_UNLINK_ACCOUNT,
            "${joinUrl}", LinksUtil.getInstructorCourseJoinUrl(instructor.getRegKey()),
            "${supportEmail}", Config.SUPPORT_EMAIL);
    }

    private String fillUpInstructorPreamble(Course course, FeedbackSession session) {
        return Templates.populateTemplate(EmailTemplates.FRAGMENT_INSTRUCTOR_COPY_PREAMBLE,
            "${courseId}", SanitizationHelper.sanitizeForHtml(course.getId()),
                "${courseName}", SanitizationHelper.sanitizeForHtml(course.getName()),
                "${feedbackSessionName}", SanitizationHelper.sanitizeForHtml(session.getName()),
                "${sessionsRecoveryLink}", LinksUtil.getSessionLinkRecoveryUrl());
    }

    private EmailWrapper getEmptyEmailAddressedToEmail(String recipient) {
        EmailWrapper email = new EmailWrapper();
        email.setRecipient(recipient);
        email.setSenderEmail(Config.EMAIL_SENDEREMAIL);
        email.setSenderName(Config.EMAIL_SENDERNAME);
        email.setReplyTo(Config.EMAIL_REPLYTO);
        return email;
    }

    private String generateCoOwnersEmailsLine(String courseId) {
        List<Instructor> coOwners = usersLogic.getCoOwnersForCourse(courseId);
        if (coOwners.isEmpty()) {
            return "(No contactable instructors found)";
        }
        StringBuilder coOwnersEmailsLine = new StringBuilder();
        for (Instructor coOwner : coOwners) {
            coOwnersEmailsLine
                    .append(SanitizationHelper.sanitizeForHtml(coOwner.getName()))
                    .append(" (")
                    .append(coOwner.getEmail())
                    .append("), ");
        }
        return coOwnersEmailsLine.substring(0, coOwnersEmailsLine.length() - 2);
    }

    /**
     * Generates additional contact information for User Email Templates.
     * @return The contact information after replacing the placeholders.
     */
    private String getAdditionalContactInformationFragment(Course course, boolean isInstructor) {
        String particulars = isInstructor ? "instructor data (e.g. wrong permission, misspelled name)"
                : "team/student data (e.g. wrong team, misspelled name)";
        return Templates.populateTemplate(EmailTemplates.FRAGMENT_SESSION_ADDITIONAL_CONTACT_INFORMATION,
                "${particulars}", particulars,
                "${coOwnersEmails}", generateCoOwnersEmailsLine(course.getId()),
                "${supportEmail}", Config.SUPPORT_EMAIL);
    }
}
