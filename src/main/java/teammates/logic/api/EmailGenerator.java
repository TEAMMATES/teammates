package teammates.logic.api;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import teammates.common.datatransfer.ErrorLogEntry;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.DeadlineExtensionAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.common.util.RequestTracer;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.Templates;
import teammates.common.util.Templates.EmailTemplates;
import teammates.common.util.TimeHelper;
import teammates.logic.core.CoursesLogic;
import teammates.logic.core.FeedbackSessionsLogic;
import teammates.logic.core.InstructorsLogic;
import teammates.logic.core.StudentsLogic;

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
    private static final String FEEDBACK_STATUS_SESSION_OPENING = "is now open";
    private static final String FEEDBACK_STATUS_SESSION_CLOSING = "is closing soon"
                                        + FEEDBACK_ACTION_SUBMIT_OR_UPDATE + HTML_NO_ACTION_REQUIRED;
    private static final String FEEDBACK_STATUS_SESSION_CLOSED = "is now closed for submission";
    private static final String FEEDBACK_STATUS_SESSION_OPENING_SOON = "is due to open soon";

    private static final String DATETIME_DISPLAY_FORMAT = "EEE, dd MMM yyyy, hh:mm a z";

    private static final long SESSION_LINK_RECOVERY_DURATION_IN_DAYS = 90;

    private static final EmailGenerator instance = new EmailGenerator();

    private final CoursesLogic coursesLogic = CoursesLogic.inst();
    private final FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
    private final InstructorsLogic instructorsLogic = InstructorsLogic.inst();
    private final StudentsLogic studentsLogic = StudentsLogic.inst();

    private EmailGenerator() {
        // prevent initialization
    }

    public static EmailGenerator inst() {
        return instance;
    }

    /**
     * Generates the feedback session opening emails for the given {@code session}.
     */
    public List<EmailWrapper> generateFeedbackSessionOpeningEmails(FeedbackSessionAttributes session) {
        return generateFeedbackSessionOpeningOrClosingEmails(session, EmailType.FEEDBACK_OPENING);
    }

    private List<EmailWrapper> generateFeedbackSessionOpeningOrClosingEmails(
            FeedbackSessionAttributes session, EmailType emailType) {
        CourseAttributes course = coursesLogic.getCourse(session.getCourseId());
        boolean isEmailNeededForStudents = fsLogic.isFeedbackSessionForUserTypeToAnswer(session, false);
        boolean isEmailNeededForInstructors = fsLogic.isFeedbackSessionForUserTypeToAnswer(session, true);
        List<InstructorAttributes> instructorsToNotify = isEmailNeededForStudents
                ? instructorsLogic.getCoOwnersForCourse(session.getCourseId())
                : new ArrayList<>();
        List<StudentAttributes> students = isEmailNeededForStudents
                ? studentsLogic.getStudentsForCourse(session.getCourseId())
                : new ArrayList<>();
        List<InstructorAttributes> instructors = isEmailNeededForInstructors
                ? instructorsLogic.getInstructorsForCourse(session.getCourseId())
                : new ArrayList<>();

        if (emailType == EmailType.FEEDBACK_CLOSING) {
            Map<String, Instant> studentDeadlines = session.getStudentDeadlines();
            students = students.stream()
                    .filter(x -> !studentDeadlines.containsKey(x.getEmail()))
                    .collect(Collectors.toList());

            Map<String, Instant> instructorDeadlines = session.getInstructorDeadlines();
            instructors = instructors.stream()
                    .filter(x -> !instructorDeadlines.containsKey(x.getEmail()))
                    .collect(Collectors.toList());
        }

        String status = emailType == EmailType.FEEDBACK_OPENING
                ? FEEDBACK_STATUS_SESSION_OPENING
                : FEEDBACK_STATUS_SESSION_CLOSING;

        String template = emailType == EmailType.FEEDBACK_OPENING
                ? EmailTemplates.USER_FEEDBACK_SESSION_OPENING.replace("${status}", status)
                : EmailTemplates.USER_FEEDBACK_SESSION.replace("${status}", status);

        return generateFeedbackSessionEmailBases(course, session, students, instructors, instructorsToNotify, template,
                emailType, FEEDBACK_ACTION_SUBMIT_EDIT_OR_VIEW);
    }

    /**
     * Generates the feedback session opening soon emails for the given {@code session}.
     *
     * <p>This is useful for e.g. in case the feedback session opening info was set wrongly.
     */
    public List<EmailWrapper> generateFeedbackSessionOpeningSoonEmails(FeedbackSessionAttributes session) {
        return generateFeedbackSessionOpeningSoonOrClosedEmails(session, EmailType.FEEDBACK_OPENING_SOON);
    }

    private List<EmailWrapper> generateFeedbackSessionOpeningSoonOrClosedEmails(
            FeedbackSessionAttributes session, EmailType emailType) {
        CourseAttributes course = coursesLogic.getCourse(session.getCourseId());
        // Notify only course co-owners
        List<InstructorAttributes> coOwners = instructorsLogic.getCoOwnersForCourse(session.getCourseId());
        return coOwners.stream()
                .map(coOwner -> generateFeedbackSessionEmailBaseForCoowner(course, session, coOwner, emailType))
                .collect(Collectors.toList());
    }

    private EmailWrapper generateFeedbackSessionEmailBaseForCoowner(
            CourseAttributes course, FeedbackSessionAttributes session, InstructorAttributes coOwner, EmailType emailType) {
        String additionalNotes;
        String status;
        if (emailType == EmailType.FEEDBACK_OPENING_SOON) {
            String editUrl = Config.getFrontEndAppUrl(Const.WebPageURIs.INSTRUCTOR_SESSION_EDIT_PAGE)
                    .withCourseId(course.getId())
                    .withSessionName(session.getFeedbackSessionName())
                    .toAbsoluteString();
            // If instructor has not joined the course, populate additional notes with information to join course.
            if (coOwner.isRegistered()) {
                additionalNotes = fillUpEditFeedbackSessionDetailsFragment(editUrl);
            } else {
                additionalNotes = fillUpJoinCourseBeforeEditFeedbackSessionDetailsFragment(editUrl,
                        getInstructorCourseJoinUrl(coOwner));
            }
            status = FEEDBACK_STATUS_SESSION_OPENING_SOON;
        } else {
            String reportUrl = Config.getFrontEndAppUrl(Const.WebPageURIs.INSTRUCTOR_SESSION_REPORT_PAGE)
                    .withCourseId(course.getId())
                    .withSessionName(session.getFeedbackSessionName())
                    .toAbsoluteString();
            additionalNotes = fillUpViewResponsesDetailsFragment(reportUrl);
            status = FEEDBACK_STATUS_SESSION_CLOSED;
        }

        Instant startTime = TimeHelper.getMidnightAdjustedInstantBasedOnZone(
                session.getStartTime(), session.getTimeZone(), false);
        Instant endTime = TimeHelper.getMidnightAdjustedInstantBasedOnZone(
                session.getEndTime(), session.getTimeZone(), false);
        String emailBody = Templates.populateTemplate(EmailTemplates.OWNER_FEEDBACK_SESSION,
                "${status}", status,
                "${userName}", SanitizationHelper.sanitizeForHtml(coOwner.getName()),
                "${courseName}", SanitizationHelper.sanitizeForHtml(course.getName()),
                "${courseId}", SanitizationHelper.sanitizeForHtml(course.getId()),
                "${feedbackSessionName}", SanitizationHelper.sanitizeForHtml(session.getFeedbackSessionName()),
                "${deadline}", SanitizationHelper.sanitizeForHtml(
                        TimeHelper.formatInstant(endTime, session.getTimeZone(), DATETIME_DISPLAY_FORMAT)),
                "${sessionInstructions}", session.getInstructionsString(),
                "${startTime}", SanitizationHelper.sanitizeForHtml(
                        TimeHelper.formatInstant(startTime, session.getTimeZone(), DATETIME_DISPLAY_FORMAT)),
                "${additionalNotes}", additionalNotes);

        EmailWrapper email = getEmptyEmailAddressedToEmail(coOwner.getEmail());
        email.setType(emailType);
        email.setSubjectFromType(course.getName(), session.getFeedbackSessionName());
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
            FeedbackSessionAttributes session, List<StudentAttributes> students,
            List<InstructorAttributes> instructorsToRemind, InstructorAttributes instructorToNotify) {

        CourseAttributes course = coursesLogic.getCourse(session.getCourseId());
        String template = EmailTemplates.USER_FEEDBACK_SESSION.replace("${status}", FEEDBACK_STATUS_SESSION_OPEN);
        List<InstructorAttributes> instructorToNotifyAsList = new ArrayList<>();
        if (instructorToNotify != null) {
            instructorToNotifyAsList.add(instructorToNotify);
        }

        return generateFeedbackSessionEmailBases(course, session, students, instructorsToRemind, instructorToNotifyAsList,
                template, EmailType.FEEDBACK_SESSION_REMINDER, FEEDBACK_ACTION_SUBMIT_EDIT_OR_VIEW);
    }

    /**
     * Generates the email containing the summary of the feedback sessions
     * email for the given {@code courseId} for {@code userEmail}.
     * @param courseId - ID of the course
     * @param userEmail - Email of student to send feedback session summary to
     * @param emailType - The email type which corresponds to the reason behind why the links are being resent
     */
    public EmailWrapper generateFeedbackSessionSummaryOfCourse(
            String courseId, String userEmail, EmailType emailType) {
        assert emailType == EmailType.STUDENT_EMAIL_CHANGED
                || emailType == EmailType.STUDENT_COURSE_LINKS_REGENERATED
                || emailType == EmailType.INSTRUCTOR_COURSE_LINKS_REGENERATED;

        CourseAttributes course = coursesLogic.getCourse(courseId);
        boolean isInstructor = emailType == EmailType.INSTRUCTOR_COURSE_LINKS_REGENERATED;
        StudentAttributes student = null;
        InstructorAttributes instructor = null;
        if (isInstructor) {
            instructor = instructorsLogic.getInstructorForEmail(courseId, userEmail);
        } else {
            student = studentsLogic.getStudentForEmail(courseId, userEmail);
        }

        List<FeedbackSessionAttributes> sessions = new ArrayList<>();
        List<FeedbackSessionAttributes> fsInCourse = fsLogic.getFeedbackSessionsForCourse(courseId);

        for (FeedbackSessionAttributes fsa : fsInCourse) {
            if (fsa.isSentOpenEmail() || fsa.isSentPublishedEmail()) {
                sessions.add(fsa);
            }
        }

        StringBuilder linksFragmentValue = new StringBuilder(1000);
        String joinUrl = Config.getFrontEndAppUrl(
                isInstructor ? instructor.getRegistrationUrl() : student.getRegistrationUrl()).toAbsoluteString();
        boolean isYetToJoinCourse = isInstructor ? isYetToJoinCourse(instructor) : isYetToJoinCourse(student);
        String joinFragmentTemplate = isInstructor
                ? EmailTemplates.FRAGMENT_INSTRUCTOR_COURSE_REJOIN_AFTER_REGKEY_RESET
                : emailType == EmailType.STUDENT_EMAIL_CHANGED
                        ? EmailTemplates.FRAGMENT_STUDENT_COURSE_JOIN
                        : EmailTemplates.FRAGMENT_STUDENT_COURSE_REJOIN_AFTER_REGKEY_RESET;

        String joinFragmentValue = isYetToJoinCourse
                ? Templates.populateTemplate(joinFragmentTemplate,
                        "${joinUrl}", joinUrl,
                        "${courseName}", SanitizationHelper.sanitizeForHtml(course.getName()),
                        "${coOwnersEmails}", generateCoOwnersEmailsLine(course.getId()),
                        "${supportEmail}", Config.SUPPORT_EMAIL)
                : "";

        for (FeedbackSessionAttributes fsa : sessions) {
            String submitUrlHtml = "(Feedback session is not yet opened)";
            String reportUrlHtml = "(Feedback session is not yet published)";

            String userKey = isInstructor ? instructor.getKey() : student.getKey();

            if (fsa.isOpened() || fsa.isClosed()) {
                String submitUrl = Config.getFrontEndAppUrl(Const.WebPageURIs.SESSION_SUBMISSION_PAGE)
                        .withCourseId(course.getId())
                        .withSessionName(fsa.getFeedbackSessionName())
                        .withRegistrationKey(userKey)
                        .withEntityType(isInstructor ? Const.EntityType.INSTRUCTOR : "")
                        .toAbsoluteString();
                submitUrlHtml = "<a href=\"" + submitUrl + "\">" + submitUrl + "</a>";
            }

            if (fsa.isPublished()) {
                String reportUrl = Config.getFrontEndAppUrl(Const.WebPageURIs.SESSION_RESULTS_PAGE)
                        .withCourseId(course.getId())
                        .withSessionName(fsa.getFeedbackSessionName())
                        .withRegistrationKey(userKey)
                        .withEntityType(isInstructor ? Const.EntityType.INSTRUCTOR : "")
                        .toAbsoluteString();
                reportUrlHtml = "<a href=\"" + reportUrl + "\">" + reportUrl + "</a>";
            }

            Instant endTime = TimeHelper.getMidnightAdjustedInstantBasedOnZone(
                    fsa.getEndTime(), fsa.getTimeZone(), false);
            linksFragmentValue.append(Templates.populateTemplate(
                    EmailTemplates.FRAGMENT_SINGLE_FEEDBACK_SESSION_LINKS,
                    "${feedbackSessionName}", fsa.getFeedbackSessionName(),
                    "${deadline}", TimeHelper.formatInstant(endTime, fsa.getTimeZone(), DATETIME_DISPLAY_FORMAT)
                            + (fsa.isClosed() ? " (Passed)" : ""),
                    "${submitUrl}", submitUrlHtml,
                    "${reportUrl}", reportUrlHtml));
        }

        if (linksFragmentValue.length() == 0) {
            linksFragmentValue.append("No links found.");
        }

        String additionalContactInformation = getAdditionalContactInformationFragment(course, isInstructor);
        String resendLinksTemplate = emailType == EmailType.STUDENT_EMAIL_CHANGED
                ? Templates.EmailTemplates.USER_FEEDBACK_SESSION_RESEND_ALL_LINKS
                : Templates.EmailTemplates.USER_REGKEY_REGENERATION_RESEND_ALL_COURSE_LINKS;

        String userName = isInstructor ? instructor.getName() : student.getName();
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
     * Generates for the student an recovery email listing the links to submit/view responses for all feedback sessions
     * under {@code recoveryEmailAddress} in the past 180 days. If no student with {@code recoveryEmailAddress} is
     * found, generate an email stating that there is no such student in the system. If no feedback sessions are found,
     * generate an email stating no feedback sessions found.
     */
    public EmailWrapper generateSessionLinksRecoveryEmailForStudent(String recoveryEmailAddress) {
        List<StudentAttributes> studentsForEmail = studentsLogic.getAllStudentsForEmail(recoveryEmailAddress);

        if (studentsForEmail.isEmpty()) {
            return generateSessionLinksRecoveryEmailForNonExistentStudent(recoveryEmailAddress);
        } else {
            return generateSessionLinksRecoveryEmailForExistingStudent(recoveryEmailAddress, studentsForEmail);
        }
    }

    private EmailWrapper generateSessionLinksRecoveryEmailForNonExistentStudent(String recoveryEmailAddress) {
        String recoveryUrl = Config.getFrontEndAppUrl(Const.WebPageURIs.SESSIONS_LINK_RECOVERY_PAGE).toAbsoluteString();
        String emailBody = Templates.populateTemplate(
                EmailTemplates.SESSION_LINKS_RECOVERY_EMAIL_NOT_FOUND,
                "${userEmail}", SanitizationHelper.sanitizeForHtml(recoveryEmailAddress),
                "${supportEmail}", Config.SUPPORT_EMAIL,
                "${teammateHomePageLink}", Config.getFrontEndAppUrl("/").toAbsoluteString(),
                "${sessionsRecoveryLink}", recoveryUrl);
        EmailWrapper email = getEmptyEmailAddressedToEmail(recoveryEmailAddress);
        email.setType(EmailType.SESSION_LINKS_RECOVERY);
        email.setSubjectFromType();
        email.setContent(emailBody);
        return email;
    }

    private EmailWrapper generateSessionLinksRecoveryEmailForExistingStudent(String recoveryEmailAddress,
                                                                             List<StudentAttributes> studentsForEmail) {
        String emailBody;

        var searchStartTime = TimeHelper.getInstantDaysOffsetBeforeNow(SESSION_LINK_RECOVERY_DURATION_IN_DAYS);
        Map<String, StringBuilder> linkFragmentsMap = new HashMap<>();
        String studentName = null;

        for (var student : studentsForEmail) {
            RequestTracer.checkRemainingTime();
            // Query students' courses first
            // as a student will likely be in only a small number of courses.
            var course = coursesLogic.getCourse(student.getCourse());
            var courseId = course.getId();

            StringBuilder linksFragmentValue;
            if (linkFragmentsMap.containsKey(courseId)) {
                linksFragmentValue = linkFragmentsMap.get(courseId);
            } else {
                linksFragmentValue = new StringBuilder(5000);
            }

            studentName = student.getName();

            for (var session : fsLogic.getFeedbackSessionsForCourseStartingAfter(courseId, searchStartTime)) {
                RequestTracer.checkRemainingTime();
                var submitUrlHtml = "";
                var reportUrlHtml = "";

                if (session.isOpened() || session.isClosed()) {
                    var submitUrl = Config.getFrontEndAppUrl(Const.WebPageURIs.SESSION_SUBMISSION_PAGE)
                            .withCourseId(course.getId())
                            .withSessionName(session.getFeedbackSessionName())
                            .withRegistrationKey(student.getKey())
                            .toAbsoluteString();
                    submitUrlHtml = "[<a href=\"" + submitUrl + "\">submission link</a>]";
                }

                if (session.isPublished()) {
                    var reportUrl = Config.getFrontEndAppUrl(Const.WebPageURIs.SESSION_RESULTS_PAGE)
                            .withCourseId(course.getId())
                            .withSessionName(session.getFeedbackSessionName())
                            .withRegistrationKey(student.getKey())
                            .toAbsoluteString();
                    reportUrlHtml = "[<a href=\"" + reportUrl + "\">result link</a>]";
                }

                if (submitUrlHtml.isEmpty() && reportUrlHtml.isEmpty()) {
                    continue;
                }

                linksFragmentValue.append(Templates.populateTemplate(
                        EmailTemplates.FRAGMENT_SESSION_LINKS_RECOVERY_ACCESS_LINKS_BY_SESSION,
                        "${sessionName}", session.getFeedbackSessionName(),
                        "${submitUrl}", submitUrlHtml,
                        "${reportUrl}", reportUrlHtml));

                linkFragmentsMap.putIfAbsent(courseId, linksFragmentValue);
            }
        }

        var recoveryUrl = Config.getFrontEndAppUrl(Const.WebPageURIs.SESSIONS_LINK_RECOVERY_PAGE).toAbsoluteString();
        if (linkFragmentsMap.isEmpty()) {
            emailBody = Templates.populateTemplate(
                    EmailTemplates.SESSION_LINKS_RECOVERY_ACCESS_LINKS_NONE,
                    "${teammateHomePageLink}", Config.getFrontEndAppUrl("/").toAbsoluteString(),
                    "${userEmail}", SanitizationHelper.sanitizeForHtml(recoveryEmailAddress),
                    "${supportEmail}", Config.SUPPORT_EMAIL,
                    "${sessionsRecoveryLink}", recoveryUrl);
        } else {
            var courseFragments = new StringBuilder(10000);
            linkFragmentsMap.forEach((courseId, linksFragments) -> {
                String courseBody = Templates.populateTemplate(
                        EmailTemplates.FRAGMENT_SESSION_LINKS_RECOVERY_ACCESS_LINKS_BY_COURSE,
                        "${sessionFragment}", linksFragments.toString(),
                        "${courseName}", coursesLogic.getCourse(courseId).getName());
                courseFragments.append(courseBody);
            });
            emailBody = Templates.populateTemplate(
                    EmailTemplates.SESSION_LINKS_RECOVERY_ACCESS_LINKS,
                    "${userName}", SanitizationHelper.sanitizeForHtml(studentName),
                    "${linksFragment}", courseFragments.toString(),
                    "${userEmail}", SanitizationHelper.sanitizeForHtml(recoveryEmailAddress),
                    "${teammateHomePageLink}", Config.getFrontEndAppUrl("/").toAbsoluteString(),
                    "${supportEmail}", Config.SUPPORT_EMAIL,
                    "${sessionsRecoveryLink}", recoveryUrl);
        }

        var email = getEmptyEmailAddressedToEmail(recoveryEmailAddress);
        email.setType(EmailType.SESSION_LINKS_RECOVERY);
        email.setSubjectFromType();
        email.setContent(emailBody);
        return email;
    }

    /**
     * Generates the feedback session closing emails for the given {@code session}.
     *
     * <p>Students and instructors with deadline extensions are not notified.
     */
    public List<EmailWrapper> generateFeedbackSessionClosingEmails(FeedbackSessionAttributes session) {
        return generateFeedbackSessionOpeningOrClosingEmails(session, EmailType.FEEDBACK_CLOSING);
    }

    /**
     * Generates the feedback session closed emails for the given {@code session}.
     */
    public List<EmailWrapper> generateFeedbackSessionClosedEmails(FeedbackSessionAttributes session) {
        return generateFeedbackSessionOpeningSoonOrClosedEmails(session, EmailType.FEEDBACK_CLOSED);
    }

    /**
    * Generates the feedback session closing emails for users with deadline extensions.
    */
    public List<EmailWrapper> generateFeedbackSessionClosingWithExtensionEmails(
            FeedbackSessionAttributes session, List<DeadlineExtensionAttributes> deadlineExtensions) {
        String courseId = session.getCourseId();
        CourseAttributes course = coursesLogic.getCourse(courseId);
        List<DeadlineExtensionAttributes> studentDeadlines =
                deadlineExtensions.stream().filter(x -> !x.getIsInstructor()).collect(Collectors.toList());
        List<DeadlineExtensionAttributes> instructorDeadlines =
                deadlineExtensions.stream().filter(x -> x.getIsInstructor()).collect(Collectors.toList());

        boolean isEmailNeededForStudents =
                !studentDeadlines.isEmpty() && fsLogic.isFeedbackSessionForUserTypeToAnswer(session, false);
        boolean isEmailNeededForInstructors =
                !instructorDeadlines.isEmpty() && fsLogic.isFeedbackSessionForUserTypeToAnswer(session, true);

        List<StudentAttributes> students = new ArrayList<>();
        if (isEmailNeededForStudents) {
            for (DeadlineExtensionAttributes studentDeadline : studentDeadlines) {
                StudentAttributes student = studentsLogic.getStudentForEmail(courseId, studentDeadline.getUserEmail());
                if (student != null) {
                    students.add(student);
                }
            }
        }

        List<InstructorAttributes> instructors = new ArrayList<>();
        if (isEmailNeededForInstructors) {
            for (DeadlineExtensionAttributes instructorDeadline : instructorDeadlines) {
                InstructorAttributes instructor =
                        instructorsLogic.getInstructorForEmail(courseId, instructorDeadline.getUserEmail());
                if (instructor != null) {
                    instructors.add(instructor);
                }
            }
        }

        String template = EmailTemplates.USER_FEEDBACK_SESSION.replace("${status}", FEEDBACK_STATUS_SESSION_CLOSING);
        EmailType type = EmailType.FEEDBACK_CLOSING;
        String feedbackAction = FEEDBACK_ACTION_SUBMIT_EDIT_OR_VIEW;
        List<EmailWrapper> emails = new ArrayList<>();
        for (StudentAttributes student : students) {
            FeedbackSessionAttributes studentSession = session.getCopyForStudent(student.getEmail());
            emails.addAll(generateFeedbackSessionEmailBases(course, studentSession, Collections.singletonList(student),
                    Collections.emptyList(), Collections.emptyList(), template, type, feedbackAction));
        }
        for (InstructorAttributes instructor : instructors) {
            FeedbackSessionAttributes instructorSession = session.getCopyForInstructor(instructor.getEmail());
            emails.addAll(generateFeedbackSessionEmailBases(course, instructorSession, Collections.emptyList(),
                    Collections.singletonList(instructor), Collections.emptyList(), template, type, feedbackAction));
        }
        return emails;
    }

    /**
     * Generates the feedback session published emails for the given {@code session}.
     */
    public List<EmailWrapper> generateFeedbackSessionPublishedEmails(FeedbackSessionAttributes session) {
        return generateFeedbackSessionPublishedOrUnpublishedEmails(session, EmailType.FEEDBACK_PUBLISHED);
    }

    /**
     * Generates the feedback session published emails for the given {@code students} and
     * {@code instructors} in {@code session}.
     */
    public List<EmailWrapper> generateFeedbackSessionPublishedEmails(FeedbackSessionAttributes session,
            List<StudentAttributes> students, List<InstructorAttributes> instructors,
            List<InstructorAttributes> instructorsToNotify) {
        return generateFeedbackSessionPublishedOrUnpublishedEmails(
                session, students, instructors, instructorsToNotify, EmailType.FEEDBACK_PUBLISHED);
    }

    /**
     * Generates the feedback session unpublished emails for the given {@code session}.
     */
    public List<EmailWrapper> generateFeedbackSessionUnpublishedEmails(FeedbackSessionAttributes session) {
        return generateFeedbackSessionPublishedOrUnpublishedEmails(session, EmailType.FEEDBACK_UNPUBLISHED);
    }

    private List<EmailWrapper> generateFeedbackSessionPublishedOrUnpublishedEmails(
            FeedbackSessionAttributes session, EmailType emailType) {
        boolean isEmailNeededForStudents = fsLogic.isFeedbackSessionViewableToUserType(session, false);
        boolean isEmailNeededForInstructors = fsLogic.isFeedbackSessionViewableToUserType(session, true);
        List<InstructorAttributes> instructorsToNotify = isEmailNeededForStudents
                ? instructorsLogic.getCoOwnersForCourse(session.getCourseId())
                : new ArrayList<>();
        List<StudentAttributes> students = isEmailNeededForStudents
                ? studentsLogic.getStudentsForCourse(session.getCourseId())
                : new ArrayList<>();
        List<InstructorAttributes> instructors = isEmailNeededForInstructors
                ? instructorsLogic.getInstructorsForCourse(session.getCourseId())
                : new ArrayList<>();

        return generateFeedbackSessionPublishedOrUnpublishedEmails(
                session, students, instructors, instructorsToNotify, emailType);
    }

    private List<EmailWrapper> generateFeedbackSessionPublishedOrUnpublishedEmails(
            FeedbackSessionAttributes session, List<StudentAttributes> students,
            List<InstructorAttributes> instructors, List<InstructorAttributes> instructorsToNotify, EmailType emailType) {
        CourseAttributes course = coursesLogic.getCourse(session.getCourseId());
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

    /**
     * Generates deadline extension granted emails.
     */
    public List<EmailWrapper> generateDeadlineGrantedEmails(CourseAttributes course,
            FeedbackSessionAttributes session, Map<String, Instant> createdDeadlines, boolean areInstructors) {
        return createdDeadlines.entrySet()
                .stream()
                .map(entry ->
                        generateDeadlineExtensionEmail(course, session,
                                session.getEndTime(), entry.getValue(), EmailType.DEADLINE_EXTENSION_GRANTED,
                                entry.getKey(), areInstructors))
                .collect(Collectors.toList());
    }

    /**
     * Generates deadline extension updated emails.
     */
    public List<EmailWrapper> generateDeadlineUpdatedEmails(CourseAttributes course, FeedbackSessionAttributes session,
            Map<String, Instant> updatedDeadlines, Map<String, Instant> oldDeadlines, boolean areInstructors) {
        return updatedDeadlines.entrySet()
                .stream()
                .map(entry ->
                        generateDeadlineExtensionEmail(course, session,
                                oldDeadlines.get(entry.getKey()), entry.getValue(), EmailType.DEADLINE_EXTENSION_UPDATED,
                                entry.getKey(), areInstructors))
                .collect(Collectors.toList());
    }

    /**
     * Generates deadline extension revoked emails.
     */
    public List<EmailWrapper> generateDeadlineRevokedEmails(CourseAttributes course,
            FeedbackSessionAttributes session, Map<String, Instant> revokedDeadlines, boolean areInstructors) {
        return revokedDeadlines.entrySet()
                .stream()
                .map(entry ->
                        generateDeadlineExtensionEmail(course, session,
                                entry.getValue(), session.getEndTime(), EmailType.DEADLINE_EXTENSION_REVOKED,
                                entry.getKey(), areInstructors))
                .collect(Collectors.toList());
    }

    private EmailWrapper generateDeadlineExtensionEmail(
            CourseAttributes course, FeedbackSessionAttributes session, Instant oldEndTime, Instant endTime,
            EmailType emailType, String userEmail, boolean isInstructor) {
        String status;

        switch (emailType) {
        case DEADLINE_EXTENSION_GRANTED:
            status = "You have been granted a deadline extension for the following feedback session.";
            break;
        case DEADLINE_EXTENSION_UPDATED:
            status = "Your deadline for the following feedback session has been updated.";
            break;
        case DEADLINE_EXTENSION_REVOKED:
            status = "Your deadline extension for the following feedback session has been revoked.";
            break;
        default:
            throw new AssertionError("Invalid email type: " + emailType);
        }

        String additionalContactInformation = getAdditionalContactInformationFragment(course, isInstructor);
        Instant oldEndTimeFormatted =
                TimeHelper.getMidnightAdjustedInstantBasedOnZone(oldEndTime, session.getTimeZone(), false);
        Instant newEndTimeFormatted =
                TimeHelper.getMidnightAdjustedInstantBasedOnZone(endTime, session.getTimeZone(), false);
        String template = EmailTemplates.USER_DEADLINE_EXTENSION
                .replace("${status}", status)
                .replace("${oldEndTime}", SanitizationHelper.sanitizeForHtml(
                        TimeHelper.formatInstant(oldEndTimeFormatted, session.getTimeZone(), DATETIME_DISPLAY_FORMAT)))
                .replace("${newEndTime}", SanitizationHelper.sanitizeForHtml(
                        TimeHelper.formatInstant(newEndTimeFormatted, session.getTimeZone(), DATETIME_DISPLAY_FORMAT)));
        String feedbackAction = FEEDBACK_ACTION_SUBMIT_EDIT_OR_VIEW;

        if (isInstructor) {
            InstructorAttributes instructor = instructorsLogic.getInstructorForEmail(course.getId(), userEmail);
            if (instructor == null) {
                return null;
            }
            return generateFeedbackSessionEmailBaseForInstructors(
                    course, session, instructor, template, emailType, feedbackAction, additionalContactInformation);
        } else {
            StudentAttributes student = studentsLogic.getStudentForEmail(course.getId(), userEmail);
            if (student == null) {
                return null;
            }
            return generateFeedbackSessionEmailBaseForStudents(
                    course, session, student, template, emailType, feedbackAction, additionalContactInformation);
        }
    }

    private List<EmailWrapper> generateFeedbackSessionEmailBases(
            CourseAttributes course, FeedbackSessionAttributes session, List<StudentAttributes> students,
            List<InstructorAttributes> instructors, List<InstructorAttributes> instructorsToNotify, String template,
            EmailType type, String feedbackAction) {
        StringBuilder studentAdditionalContactBuilder = new StringBuilder();
        StringBuilder instructorAdditionalContactBuilder = new StringBuilder();
        studentAdditionalContactBuilder.append(getAdditionalContactInformationFragment(course, false));
        instructorAdditionalContactBuilder.append(getAdditionalContactInformationFragment(course, true));

        List<EmailWrapper> emails = new ArrayList<>();
        for (StudentAttributes student : students) {
            emails.add(generateFeedbackSessionEmailBaseForStudents(course, session, student,
                    template, type, feedbackAction, studentAdditionalContactBuilder.toString()));
        }
        for (InstructorAttributes instructor : instructors) {
            emails.add(generateFeedbackSessionEmailBaseForInstructors(course, session, instructor,
                    template, type, feedbackAction, instructorAdditionalContactBuilder.toString()));
        }
        for (InstructorAttributes instructor : instructorsToNotify) {
            emails.add(generateFeedbackSessionEmailBaseForNotifiedInstructors(course, session, instructor,
                    template, type, feedbackAction, studentAdditionalContactBuilder.toString()));
        }
        return emails;
    }

    private EmailWrapper generateFeedbackSessionEmailBaseForStudents(
            CourseAttributes course, FeedbackSessionAttributes session, StudentAttributes student, String template,
            EmailType type, String feedbackAction, String additionalContactInformation) {
        String submitUrl = Config.getFrontEndAppUrl(Const.WebPageURIs.SESSION_SUBMISSION_PAGE)
                .withCourseId(course.getId())
                .withSessionName(session.getFeedbackSessionName())
                .withRegistrationKey(student.getKey())
                .toAbsoluteString();

        String reportUrl = Config.getFrontEndAppUrl(Const.WebPageURIs.SESSION_RESULTS_PAGE)
                .withCourseId(course.getId())
                .withSessionName(session.getFeedbackSessionName())
                .withRegistrationKey(student.getKey())
                .toAbsoluteString();

        Instant endTime = TimeHelper.getMidnightAdjustedInstantBasedOnZone(
                session.getDeadline(), session.getTimeZone(), false);
        String emailBody = Templates.populateTemplate(template,
                "${userName}", SanitizationHelper.sanitizeForHtml(student.getName()),
                "${courseName}", SanitizationHelper.sanitizeForHtml(course.getName()),
                "${courseId}", SanitizationHelper.sanitizeForHtml(course.getId()),
                "${feedbackSessionName}", SanitizationHelper.sanitizeForHtml(session.getFeedbackSessionName()),
                "${deadline}", SanitizationHelper.sanitizeForHtml(
                        TimeHelper.formatInstant(endTime, session.getTimeZone(), DATETIME_DISPLAY_FORMAT))
                        + (session.getUserEmail() == null ? "" : " (after extension)"),
                "${instructorPreamble}", "",
                "${sessionInstructions}", session.getInstructionsString(),
                "${submitUrl}", submitUrl,
                "${reportUrl}", reportUrl,
                "${feedbackAction}", feedbackAction,
                "${additionalContactInformation}", additionalContactInformation);

        EmailWrapper email = getEmptyEmailAddressedToEmail(student.getEmail());
        email.setType(type);
        email.setSubjectFromType(course.getName(), session.getFeedbackSessionName());
        email.setContent(emailBody);
        return email;
    }

    private EmailWrapper generateFeedbackSessionEmailBaseForInstructors(
            CourseAttributes course, FeedbackSessionAttributes session, InstructorAttributes instructor,
            String template, EmailType type, String feedbackAction, String additionalContactInformation) {
        String submitUrl = Config.getFrontEndAppUrl(Const.WebPageURIs.SESSION_SUBMISSION_PAGE)
                .withCourseId(course.getId())
                .withSessionName(session.getFeedbackSessionName())
                .withRegistrationKey(instructor.getKey())
                .withEntityType(Const.EntityType.INSTRUCTOR)
                .toAbsoluteString();

        String reportUrl = Config.getFrontEndAppUrl(Const.WebPageURIs.SESSION_RESULTS_PAGE)
                .withCourseId(course.getId())
                .withSessionName(session.getFeedbackSessionName())
                .withRegistrationKey(instructor.getKey())
                .withEntityType(Const.EntityType.INSTRUCTOR)
                .toAbsoluteString();

        Instant endTime = TimeHelper.getMidnightAdjustedInstantBasedOnZone(
                session.getDeadline(), session.getTimeZone(), false);
        String emailBody = Templates.populateTemplate(template,
                "${userName}", SanitizationHelper.sanitizeForHtml(instructor.getName()),
                "${courseName}", SanitizationHelper.sanitizeForHtml(course.getName()),
                "${courseId}", SanitizationHelper.sanitizeForHtml(course.getId()),
                "${feedbackSessionName}", SanitizationHelper.sanitizeForHtml(session.getFeedbackSessionName()),
                "${deadline}", SanitizationHelper.sanitizeForHtml(
                        TimeHelper.formatInstant(endTime, session.getTimeZone(), DATETIME_DISPLAY_FORMAT))
                        + (session.getUserEmail() == null ? "" : " (after extension)"),
                "${instructorPreamble}", "",
                "${sessionInstructions}", session.getInstructionsString(),
                "${submitUrl}", submitUrl,
                "${reportUrl}", reportUrl,
                "${feedbackAction}", feedbackAction,
                "${additionalContactInformation}", additionalContactInformation);

        EmailWrapper email = getEmptyEmailAddressedToEmail(instructor.getEmail());
        email.setType(type);
        email.setSubjectFromType(course.getName(), session.getFeedbackSessionName());
        email.setContent(emailBody);
        return email;
    }

    private EmailWrapper generateFeedbackSessionEmailBaseForNotifiedInstructors(
            CourseAttributes course, FeedbackSessionAttributes session, InstructorAttributes instructor,
            String template, EmailType type, String feedbackAction, String additionalContactInformation) {
        Instant endTime = TimeHelper.getMidnightAdjustedInstantBasedOnZone(
                session.getEndTime(), session.getTimeZone(), false);
        String emailBody = Templates.populateTemplate(template,
                "${userName}", SanitizationHelper.sanitizeForHtml(instructor.getName()),
                "${courseName}", SanitizationHelper.sanitizeForHtml(course.getName()),
                "${courseId}", SanitizationHelper.sanitizeForHtml(course.getId()),
                "${feedbackSessionName}", SanitizationHelper.sanitizeForHtml(session.getFeedbackSessionName()),
                "${deadline}", SanitizationHelper.sanitizeForHtml(
                        TimeHelper.formatInstant(endTime, session.getTimeZone(), DATETIME_DISPLAY_FORMAT)),
                "${instructorPreamble}", fillUpInstructorPreamble(course),
                "${sessionInstructions}", session.getInstructionsString(),
                "${submitUrl}", "{in the actual email sent to the students, this will be the unique link}",
                "${reportUrl}", "{in the actual email sent to the students, this will be the unique link}",
                "${feedbackAction}", feedbackAction,
                "${additionalContactInformation}", additionalContactInformation);

        EmailWrapper email = getEmptyEmailAddressedToEmail(instructor.getEmail());
        email.setType(type);
        email.setIsCopy(true);
        email.setSubjectFromType(course.getName(), session.getFeedbackSessionName());
        email.setContent(emailBody);
        return email;
    }

    private boolean isYetToJoinCourse(StudentAttributes student) {
        return student.getGoogleId() == null || student.getGoogleId().isEmpty();
    }

    private boolean isYetToJoinCourse(InstructorAttributes instructor) {
        return instructor.getGoogleId() == null || instructor.getGoogleId().isEmpty();
    }

    /**
     * Generates the login email for the given user.
     */
    public EmailWrapper generateLoginEmail(String userEmail, String loginLink) {

        String emailBody = Templates.populateTemplate(EmailTemplates.LOGIN_EMAIL,
                "${userEmail}", SanitizationHelper.sanitizeForHtml(userEmail),
                "${loginLink}", loginLink);

        EmailWrapper email = getEmptyEmailAddressedToEmail(userEmail);
        email.setBcc(Config.SUPPORT_EMAIL);
        email.setType(EmailType.LOGIN);
        email.setSubjectFromType();
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
    public EmailWrapper generateStudentCourseJoinEmail(CourseAttributes course, StudentAttributes student) {

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
    public EmailWrapper generateStudentCourseRejoinEmailAfterGoogleIdReset(
            CourseAttributes course, StudentAttributes student) {

        String emailBody = Templates.populateTemplate(
                fillUpStudentRejoinAfterGoogleIdResetFragment(student),
                "${userName}", SanitizationHelper.sanitizeForHtml(student.getName()),
                "${courseName}", SanitizationHelper.sanitizeForHtml(course.getName()),
                "${coOwnersEmails}", generateCoOwnersEmailsLine(course.getId()),
                "${supportEmail}", Config.SUPPORT_EMAIL);

        EmailWrapper email = getEmptyEmailAddressedToEmail(student.getEmail());
        email.setType(EmailType.STUDENT_COURSE_REJOIN_AFTER_GOOGLE_ID_RESET);
        email.setSubjectFromType(course.getName(), course.getId());
        email.setContent(emailBody);
        return email;
    }

    /**
     * Generates the course join email for the given {@code instructor} in {@code course}.
     * Also specifies contact information of {@code inviter}.
     */
    public EmailWrapper generateInstructorCourseJoinEmail(AccountAttributes inviter,
            InstructorAttributes instructor, CourseAttributes course) {

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
    public EmailWrapper generateInstructorCourseRejoinEmailAfterGoogleIdReset(
            InstructorAttributes instructor, CourseAttributes course) {

        String emailBody = Templates.populateTemplate(
                fillUpInstructorRejoinAfterGoogleIdResetFragment(instructor),
                "${userName}", SanitizationHelper.sanitizeForHtml(instructor.getName()),
                "${courseName}", SanitizationHelper.sanitizeForHtml(course.getName()),
                "${supportEmail}", Config.SUPPORT_EMAIL);

        EmailWrapper email = getEmptyEmailAddressedToEmail(instructor.getEmail());
        email.setType(EmailType.INSTRUCTOR_COURSE_REJOIN_AFTER_GOOGLE_ID_RESET);
        email.setSubjectFromType(course.getName(), course.getId());
        email.setContent(emailBody);
        return email;
    }

    /**
     * Generates the course registered email for the user with the given details in {@code course}.
     */
    public EmailWrapper generateUserCourseRegisteredEmail(
            String name, String emailAddress, String googleId, boolean isInstructor, CourseAttributes course) {
        String emailBody = Templates.populateTemplate(EmailTemplates.USER_COURSE_REGISTER,
                "${userName}", SanitizationHelper.sanitizeForHtml(name),
                "${userType}", isInstructor ? "an instructor" : "a student",
                "${courseId}", SanitizationHelper.sanitizeForHtml(course.getId()),
                "${courseName}", SanitizationHelper.sanitizeForHtml(course.getName()),
                "${googleId}", SanitizationHelper.sanitizeForHtml(googleId),
                "${appUrl}", isInstructor
                        ? Config.getFrontEndAppUrl(Const.WebPageURIs.INSTRUCTOR_HOME_PAGE).toAbsoluteString()
                        : Config.getFrontEndAppUrl(Const.WebPageURIs.STUDENT_HOME_PAGE).toAbsoluteString(),
                "${supportEmail}", Config.SUPPORT_EMAIL);

        EmailWrapper email = getEmptyEmailAddressedToEmail(emailAddress);
        email.setType(EmailType.USER_COURSE_REGISTER);
        email.setSubjectFromType(course.getName(), course.getId());
        email.setContent(emailBody);
        return email;
    }

    private String fillUpStudentJoinFragment(StudentAttributes student) {
        String joinUrl = Config.getFrontEndAppUrl(student.getRegistrationUrl()).toAbsoluteString();

        return Templates.populateTemplate(EmailTemplates.USER_COURSE_JOIN,
                "${joinFragment}", EmailTemplates.FRAGMENT_STUDENT_COURSE_JOIN,
                "${joinUrl}", joinUrl);
    }

    private String fillUpStudentRejoinAfterGoogleIdResetFragment(StudentAttributes student) {
        String joinUrl = Config.getFrontEndAppUrl(student.getRegistrationUrl()).toAbsoluteString();

        return Templates.populateTemplate(EmailTemplates.USER_COURSE_JOIN,
                "${joinFragment}", EmailTemplates.FRAGMENT_STUDENT_COURSE_REJOIN_AFTER_GOOGLE_ID_RESET,
                "${joinUrl}", joinUrl,
                "${supportEmail}", Config.SUPPORT_EMAIL);
    }

    private String getInstructorCourseJoinUrl(InstructorAttributes instructor) {
        return Config.getFrontEndAppUrl(instructor.getRegistrationUrl()).toAbsoluteString();
    }

    private String fillUpInstructorJoinFragment(InstructorAttributes instructor) {
        return Templates.populateTemplate(EmailTemplates.USER_COURSE_JOIN,
                "${joinFragment}", EmailTemplates.FRAGMENT_INSTRUCTOR_COURSE_JOIN,
                "${joinUrl}", getInstructorCourseJoinUrl(instructor));
    }

    private String fillUpInstructorRejoinAfterGoogleIdResetFragment(InstructorAttributes instructor) {
        String joinUrl = Config.getFrontEndAppUrl(instructor.getRegistrationUrl()).toAbsoluteString();

        return Templates.populateTemplate(EmailTemplates.USER_COURSE_JOIN,
                "${joinFragment}", EmailTemplates.FRAGMENT_INSTRUCTOR_COURSE_REJOIN_AFTER_GOOGLE_ID_RESET,
                "${joinUrl}", joinUrl,
                "${supportEmail}", Config.SUPPORT_EMAIL);
    }

    private String fillUpInstructorPreamble(CourseAttributes course) {
        return Templates.populateTemplate(EmailTemplates.FRAGMENT_INSTRUCTOR_COPY_PREAMBLE,
                "${courseId}", SanitizationHelper.sanitizeForHtml(course.getId()),
                "${courseName}", SanitizationHelper.sanitizeForHtml(course.getName()));
    }

    /**
     * Generates the logs compilation email for the given {@code logs}.
     */
    public EmailWrapper generateCompiledLogsEmail(List<ErrorLogEntry> logs) {
        StringBuilder emailBody = new StringBuilder();
        for (int i = 0; i < logs.size(); i++) {
            emailBody.append(generateSevereErrorLogLine(i, logs.get(i).getMessage(),
                    logs.get(i).getSeverity(), logs.get(i).getTraceId()));
        }

        EmailWrapper email = getEmptyEmailAddressedToEmail(Config.SUPPORT_EMAIL);
        email.setType(EmailType.SEVERE_LOGS_COMPILATION);
        email.setSubjectFromType(Config.APP_VERSION);
        email.setContent(emailBody.toString());
        return email;
    }

    private String generateSevereErrorLogLine(int index, String logMessage, String logLevel, String traceId) {
        return Templates.populateTemplate(
                EmailTemplates.SEVERE_ERROR_LOG_LINE,
                "${index}", String.valueOf(index),
                "${errorType}", logLevel,
                "${errorMessage}", logMessage.replaceAll("\n", "\n<br>"),
                "${traceId}", traceId);
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
        List<InstructorAttributes> coOwners = instructorsLogic.getCoOwnersForCourse(courseId);
        if (coOwners.isEmpty()) {
            return "(No contactable instructors found)";
        }
        StringBuilder coOwnersEmailsLine = new StringBuilder();
        for (InstructorAttributes coOwner : coOwners) {
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
    private String getAdditionalContactInformationFragment(CourseAttributes course, boolean isInstructor) {
        String particulars = isInstructor ? "instructor data (e.g. wrong permission, misspelled name)"
                : "team/student data (e.g. wrong team, misspelled name)";
        return Templates.populateTemplate(EmailTemplates.FRAGMENT_SESSION_ADDITIONAL_CONTACT_INFORMATION,
                "${particulars}", particulars,
                "${coOwnersEmails}", generateCoOwnersEmailsLine(course.getId()),
                "${supportEmail}", Config.SUPPORT_EMAIL);
    }
}
