package teammates.logic.api;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.AppUrl;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.common.util.Logger;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StringHelper;
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
public class EmailGenerator {
    // status-related strings
    private static final String FEEDBACK_STATUS_SESSION_OPEN = "is still open for submissions";
    private static final String FEEDBACK_STATUS_SESSION_OPENING = "is now open";
    private static final String FEEDBACK_STATUS_SESSION_CLOSING = "is closing soon";
    private static final String FEEDBACK_STATUS_SESSION_CLOSED =
            "is now closed. You can still view your submission by going to the link sent earlier, "
            + "but you will not be able to edit existing responses or submit new responses";

    // feedback action strings
    private static final String FEEDBACK_ACTION_SUBMIT_EDIT_OR_VIEW = "submit, edit or view";
    private static final String FEEDBACK_ACTION_VIEW = "view";
    private static final String HTML_NO_ACTION_REQUIRED =
            "<p>No action is required if you have already submitted.</p>" + System.lineSeparator();

    private static final Logger log = Logger.getLogger();
    private static final CoursesLogic coursesLogic = CoursesLogic.inst();
    private static final FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
    private static final InstructorsLogic instructorsLogic = InstructorsLogic.inst();
    private static final StudentsLogic studentsLogic = StudentsLogic.inst();

    private static final String DATETIME_DISPLAY_FORMAT = "EEE, dd MMM yyyy, hh:mm a z";

    /**
     * Generates the feedback session opening emails for the given {@code session}.
     */
    public List<EmailWrapper> generateFeedbackSessionOpeningEmails(FeedbackSessionAttributes session) {

        String template = EmailTemplates.USER_FEEDBACK_SESSION;

        CourseAttributes course = coursesLogic.getCourse(session.getCourseId());
        boolean isEmailNeeded = fsLogic.isFeedbackSessionForStudentsToAnswer(session);
        List<InstructorAttributes> instructors = isEmailNeeded
                                                 ? instructorsLogic.getInstructorsForCourse(session.getCourseId())
                                                 : new ArrayList<>();
        List<StudentAttributes> students = isEmailNeeded
                                           ? studentsLogic.getStudentsForCourse(session.getCourseId())
                                           : new ArrayList<>();

        List<EmailWrapper> emails = generateFeedbackSessionEmailBases(course, session, students, instructors, template,
                                                                      EmailType.FEEDBACK_OPENING.getSubject());
        for (EmailWrapper email : emails) {
            email.setContent(email.getContent().replace("${status}", FEEDBACK_STATUS_SESSION_OPENING));
        }
        return emails;
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
        String additionalContactInformation = HTML_NO_ACTION_REQUIRED + getAdditionalContactInformationFragment(course);
        List<InstructorAttributes> instructorToNotifyAsList = new ArrayList<>();
        instructorToNotifyAsList.add(instructorToNotify);

        List<EmailWrapper> emails =
                generateFeedbackSessionEmailBasesForInstructorReminders(course, session, instructorsToRemind, template,
                        EmailType.FEEDBACK_SESSION_REMINDER.getSubject(), additionalContactInformation);
        emails.addAll(generateFeedbackSessionEmailBases(course, session, students, instructorToNotifyAsList, template,
                                                        EmailType.FEEDBACK_SESSION_REMINDER.getSubject(),
                                                        FEEDBACK_ACTION_SUBMIT_EDIT_OR_VIEW, additionalContactInformation));

        return emails;
    }

    /**
     * Generates the feedback session reminder emails for the given {@code student}.
     */
    public EmailWrapper generateFeedbackSessionStudentReminderEmail(
            FeedbackSessionAttributes session, StudentAttributes student) {

        CourseAttributes course = coursesLogic.getCourse(session.getCourseId());
        String template = EmailTemplates.USER_FEEDBACK_SESSION.replace("${status}", FEEDBACK_STATUS_SESSION_OPEN);
        String additionalContactInformation = HTML_NO_ACTION_REQUIRED + getAdditionalContactInformationFragment(course);

        return generateFeedbackSessionEmailBaseForStudents(course, session, student, template,
                EmailType.FEEDBACK_SESSION_REMINDER.getSubject(),
                FEEDBACK_ACTION_SUBMIT_EDIT_OR_VIEW, additionalContactInformation);
    }

    /**
     * Generates the email containing the summary of the feedback sessions
     * email for the given {@code courseId} for {@code student}.
     * @param courseId - ID of the course
     * @param studentEmail - Email of student to send feedback session summary to
     * @param resendLinksTemplate - The email template including the reason behind why the links are being resent
     */
    public EmailWrapper generateFeedbackSessionSummaryOfCourse(
            String courseId, String studentEmail, String resendLinksTemplate) {

        CourseAttributes course = coursesLogic.getCourse(courseId);
        StudentAttributes student = studentsLogic.getStudentForEmail(courseId, studentEmail);

        List<FeedbackSessionAttributes> sessions = new ArrayList<>();
        List<FeedbackSessionAttributes> fsInCourse = fsLogic.getFeedbackSessionsForCourse(courseId);

        for (FeedbackSessionAttributes fsa : fsInCourse) {
            if (fsa.isSentOpenEmail() || fsa.isSentPublishedEmail()) {
                sessions.add(fsa);
            }
        }

        StringBuffer linksFragmentValue = new StringBuffer(1000);
        String joinUrl = Config.getFrontEndAppUrl(student.getRegistrationUrl()).toAbsoluteString();

        String joinFragmentValue = isYetToJoinCourse(student)
                                   ? Templates.populateTemplate(EmailTemplates.FRAGMENT_STUDENT_COURSE_JOIN,
                                           "${joinUrl}", joinUrl,
                                           "${courseName}", SanitizationHelper.sanitizeForHtml(course.getName()),
                                           "${coOwnersEmails}", generateCoOwnersEmailsLine(course.getId()))
                                   : "";

        for (FeedbackSessionAttributes fsa : sessions) {

            String submitUrlHtml = "(Feedback session is not yet opened)";
            String reportUrlHtml = "(Feedback session is not yet published)";

            if (fsa.isOpened() || fsa.isClosed()) {
                String submitUrl = Config.getFrontEndAppUrl(Const.WebPageURIs.SESSION_SUBMISSION_PAGE)
                        .withCourseId(course.getId())
                        .withSessionName(fsa.getFeedbackSessionName())
                        .withRegistrationKey(StringHelper.encrypt(student.key))
                        .withStudentEmail(student.email)
                        .toAbsoluteString();
                submitUrlHtml = "<a href=\"" + submitUrl + "\">" + submitUrl + "</a>";
            }

            if (fsa.isPublished()) {
                String reportUrl = Config.getFrontEndAppUrl(Const.WebPageURIs.SESSION_RESULTS_PAGE)
                        .withCourseId(course.getId())
                        .withSessionName(fsa.getFeedbackSessionName())
                        .withRegistrationKey(StringHelper.encrypt(student.key))
                        .withStudentEmail(student.email)
                        .toAbsoluteString();
                reportUrlHtml = "<a href=\"" + reportUrl + "\">" + reportUrl + "</a>";
            }

            linksFragmentValue.append(Templates.populateTemplate(
                    EmailTemplates.FRAGMENT_SINGLE_FEEDBACK_SESSION_LINKS,
                    "${feedbackSessionName}", fsa.getFeedbackSessionName(),
                    "${deadline}", TimeHelper.formatInstant(fsa.getEndTime(), fsa.getTimeZone(), DATETIME_DISPLAY_FORMAT)
                            + (fsa.isClosed() ? " (Passed)" : ""),
                    "${submitUrl}", submitUrlHtml,
                    "${reportUrl}", reportUrlHtml));
        }

        if (linksFragmentValue.length() == 0) {
            linksFragmentValue.append("No links found.");
        }

        String additionalContactInformation = getAdditionalContactInformationFragment(course);

        String emailBody = Templates.populateTemplate(resendLinksTemplate,
                "${userName}", SanitizationHelper.sanitizeForHtml(student.name),
                "${userEmail}", student.email,
                "${courseName}", SanitizationHelper.sanitizeForHtml(course.getName()),
                "${courseId}", course.getId(),
                "${joinFragment}", joinFragmentValue,
                "${linksFragment}", linksFragmentValue.toString(),
                "${additionalContactInformation}", additionalContactInformation);

        EmailWrapper email = getEmptyEmailAddressedToEmail(student.email);
        email.setContent(emailBody);

        // Set appropriate email subject, depending on the email template
        if (resendLinksTemplate.equals(Templates.EmailTemplates.USER_FEEDBACK_SESSION_RESEND_ALL_LINKS)) {
            email.setSubject(String.format(EmailType.STUDENT_EMAIL_CHANGED.getSubject(), course.getName(), course.getId()));
        } else if (resendLinksTemplate.equals(Templates.EmailTemplates.USER_REGKEY_REGENERATION_RESEND_ALL_COURSE_LINKS)) {
            email.setSubject(String.format(EmailType.STUDENT_COURSE_LINKS_REGENERATED.getSubject(),
                                                                                    course.getName(), course.getId()));
        }

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

    private List<EmailWrapper> generateFeedbackSessionEmailBasesForInstructorReminders(
            CourseAttributes course, FeedbackSessionAttributes session, List<InstructorAttributes> instructors,
            String template, String subject, String additionalContactInformation) {

        List<EmailWrapper> emails = new ArrayList<>();
        for (InstructorAttributes instructor : instructors) {
            emails.add(generateFeedbackSessionEmailBaseForInstructorReminders(course, session, instructor,
                    template, subject, additionalContactInformation));
        }
        return emails;
    }

    private EmailWrapper generateSessionLinksRecoveryEmailForNonExistentStudent(String recoveryEmailAddress) {
        String emailBody;
        String subject = EmailType.SESSION_LINKS_RECOVERY.getSubject();
        String recoveryUrl = Config.getFrontEndAppUrl(Const.WebPageURIs.SESSIONS_LINK_RECOVERY_PAGE).toAbsoluteString();
        emailBody = Templates.populateTemplate(
                EmailTemplates.SESSION_LINKS_RECOVERY_EMAIL_NOT_FOUND,
                "${userEmail}", SanitizationHelper.sanitizeForHtml(recoveryEmailAddress),
                "${supportEmail}", Config.SUPPORT_EMAIL,
                "${teammateHomePageLink}", Config.getFrontEndAppUrl("/").toAbsoluteString(),
                "${sessionsRecoveryLink}", recoveryUrl);
        EmailWrapper email = getEmptyEmailAddressedToEmail(recoveryEmailAddress);
        email.setSubject(subject);
        email.setContent(emailBody);
        return email;
    }

    private EmailWrapper generateSessionLinksRecoveryEmailForExistingStudent(String recoveryEmailAddress,
                                                                             List<StudentAttributes> studentsForEmail) {
        String emailBody;
        String subject = EmailType.SESSION_LINKS_RECOVERY.getSubject();

        Instant endTime = Instant.now();
        Instant startTime = endTime.minus(Duration.ofDays(180));
        Map<String, StringBuilder> linkFragmentsMap = new HashMap<>();
        String studentName = null;

        List<FeedbackSessionAttributes> sessions = fsLogic.getAllFeedbackSessionsWithinTimeRange(startTime, endTime);

        for (FeedbackSessionAttributes session : sessions) {
            String courseId = session.getCourseId();
            CourseAttributes course = coursesLogic.getCourse(courseId);
            List<StudentAttributes> students = studentsForEmail.stream().filter(
                    each -> each.course.equals(courseId)).collect(Collectors.toList());
            StringBuilder linksFragmentValue;
            if (linkFragmentsMap.containsKey(courseId)) {
                linksFragmentValue = linkFragmentsMap.get(courseId);
            } else {
                linksFragmentValue = new StringBuilder(5000);
            }

            if (students.size() != 1) {
                continue;
            }

            StudentAttributes student = students.get(0);
            studentName = student.getName();
            String submitUrlHtml = "";
            String reportUrlHtml = "";

            if (session.isOpened() || session.isClosed()) {
                String submitUrl = Config.getFrontEndAppUrl(Const.WebPageURIs.SESSION_SUBMISSION_PAGE)
                        .withCourseId(course.getId())
                        .withSessionName(session.getFeedbackSessionName())
                        .withRegistrationKey(StringHelper.encrypt(student.key))
                        .withStudentEmail(student.email)
                        .toAbsoluteString();
                submitUrlHtml = "[<a href=\"" + submitUrl + "\">submission link</a>]";
            }

            if (session.isPublished()) {
                String reportUrl = Config.getFrontEndAppUrl(Const.WebPageURIs.SESSION_RESULTS_PAGE)
                        .withCourseId(course.getId())
                        .withSessionName(session.getFeedbackSessionName())
                        .withRegistrationKey(StringHelper.encrypt(student.key))
                        .withStudentEmail(student.email)
                        .toAbsoluteString();
                reportUrlHtml = "[<a href=\"" + reportUrl + "\">result link</a>]";
            }

            linksFragmentValue.append(Templates.populateTemplate(
                    EmailTemplates.FRAGMENT_SESSION_LINKS_RECOVERY_ACCESS_LINKS_BY_SESSION,
                    "${sessionName}", session.getFeedbackSessionName(),
                    "${submitUrl}", submitUrlHtml,
                    "${reportUrl}", reportUrlHtml));

            linkFragmentsMap.putIfAbsent(courseId, linksFragmentValue);
        }

        String recoveryUrl = Config.getFrontEndAppUrl(Const.WebPageURIs.SESSIONS_LINK_RECOVERY_PAGE).toAbsoluteString();
        if (linkFragmentsMap.isEmpty()) {
            emailBody = Templates.populateTemplate(
                    EmailTemplates.SESSION_LINKS_RECOVERY_ACCESS_LINKS_NONE,
                    "${teammateHomePageLink}", Config.getFrontEndAppUrl("/").toAbsoluteString(),
                    "${userEmail}", SanitizationHelper.sanitizeForHtml(recoveryEmailAddress),
                    "${supportEmail}", Config.SUPPORT_EMAIL,
                    "${sessionsRecoveryLink}", recoveryUrl);
        } else {
            StringBuilder courseFragments = new StringBuilder(10000);
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

        EmailWrapper email = getEmptyEmailAddressedToEmail(recoveryEmailAddress);
        email.setSubject(subject);
        email.setContent(emailBody);
        return email;
    }

    private EmailWrapper generateFeedbackSessionEmailBaseForInstructorReminders(
            CourseAttributes course, FeedbackSessionAttributes session, InstructorAttributes instructor,
            String template, String subject, String additionalContactInformation) {

        String submitUrl = Config.getFrontEndAppUrl(Const.WebPageURIs.INSTRUCTOR_SESSION_SUBMISSION_PAGE)
                .withCourseId(course.getId())
                .withSessionName(session.getFeedbackSessionName())
                .toAbsoluteString();

        String reportUrl = Config.getFrontEndAppUrl(Const.WebPageURIs.INSTRUCTOR_SESSION_RESULTS_PAGE)
                .withCourseId(course.getId())
                .withSessionName(session.getFeedbackSessionName())
                .toAbsoluteString();

        String emailBody = Templates.populateTemplate(template,
                "${userName}", SanitizationHelper.sanitizeForHtml(instructor.name),
                "${courseName}", SanitizationHelper.sanitizeForHtml(course.getName()),
                "${courseId}", SanitizationHelper.sanitizeForHtml(course.getId()),
                "${feedbackSessionName}", SanitizationHelper.sanitizeForHtml(session.getFeedbackSessionName()),
                "${deadline}", SanitizationHelper.sanitizeForHtml(
                        TimeHelper.formatInstant(session.getEndTime(), session.getTimeZone(), DATETIME_DISPLAY_FORMAT)),
                "${instructorFragment}", "",
                "${sessionInstructions}", session.getInstructionsString(),
                "${submitUrl}", submitUrl,
                "${reportUrl}", reportUrl,
                "${feedbackAction}", FEEDBACK_ACTION_SUBMIT_EDIT_OR_VIEW,
                "${additionalContactInformation}", additionalContactInformation);

        EmailWrapper email = getEmptyEmailAddressedToEmail(instructor.email);
        email.setSubject(String.format(subject, course.getName(), session.getFeedbackSessionName()));
        email.setContent(emailBody);
        return email;
    }

    /**
     * Generates the feedback session closing emails for the given {@code session}.
     */
    public List<EmailWrapper> generateFeedbackSessionClosingEmails(FeedbackSessionAttributes session) {

        List<StudentAttributes> students = new ArrayList<>();
        boolean isEmailNeeded = fsLogic.isFeedbackSessionForStudentsToAnswer(session);

        if (isEmailNeeded) {
            List<StudentAttributes> studentsForCourse = studentsLogic.getStudentsForCourse(session.getCourseId());

            for (StudentAttributes student : studentsForCourse) {
                try {
                    if (!fsLogic.isFeedbackSessionAttemptedByStudent(session.getFeedbackSessionName(),
                            session.getCourseId(), student.email)) {
                        students.add(student);
                    }
                } catch (EntityDoesNotExistException e) {
                    log.severe("Course " + session.getCourseId() + " does not exist or "
                               + "session " + session.getFeedbackSessionName() + " does not exist");
                    // Course or session cannot be found for one student => it will be the case for all students
                    // Do not waste time looping through all students
                    break;
                }
            }
        }

        String template = EmailTemplates.USER_FEEDBACK_SESSION.replace("${status}", FEEDBACK_STATUS_SESSION_CLOSING);
        CourseAttributes course = coursesLogic.getCourse(session.getCourseId());
        List<InstructorAttributes> instructors = isEmailNeeded
                                                 ? instructorsLogic.getInstructorsForCourse(session.getCourseId())
                                                 : new ArrayList<>();
        String additionalContactInformation = HTML_NO_ACTION_REQUIRED + getAdditionalContactInformationFragment(course);
        return generateFeedbackSessionEmailBases(course, session, students, instructors, template,
                EmailType.FEEDBACK_CLOSING.getSubject(), FEEDBACK_ACTION_SUBMIT_EDIT_OR_VIEW, additionalContactInformation);
    }

    /**
     * Generates the feedback session closed emails for the given {@code session}.
     */
    public List<EmailWrapper> generateFeedbackSessionClosedEmails(FeedbackSessionAttributes session) {

        CourseAttributes course = coursesLogic.getCourse(session.getCourseId());
        boolean isEmailNeededForStudents = false;
        try {
            isEmailNeededForStudents = fsLogic.isFeedbackSessionHasQuestionForStudents(
                    session.getFeedbackSessionName(), session.getCourseId());
        } catch (EntityDoesNotExistException e) {
            log.severe("Course " + session.getCourseId() + " does not exist or "
                    + "session " + session.getFeedbackSessionName() + " does not exist");
        }
        List<InstructorAttributes> instructors = isEmailNeededForStudents
                                                 ? instructorsLogic.getInstructorsForCourse(session.getCourseId())
                                                 : new ArrayList<>();
        List<StudentAttributes> studentsForCourse = isEmailNeededForStudents
                                           ? studentsLogic.getStudentsForCourse(session.getCourseId())
                                           : new ArrayList<>();
        ArrayList<StudentAttributes> studentsToEmail = new ArrayList<>();
        for (StudentAttributes student : studentsForCourse) {
            try {
                if (!fsLogic.isFeedbackSessionAttemptedByStudent(session.getFeedbackSessionName(),
                        session.getCourseId(), student.email)) {
                    studentsToEmail.add(student);
                }
            } catch (EntityDoesNotExistException e) {
                log.severe("Course " + session.getCourseId() + " does not exist or "
                        + "session " + session.getFeedbackSessionName() + " does not exist");
                // Course or session cannot be found for one student => it will be the case for all students
                // Do not waste time looping through all students
                break;
            }
        }

        String template = EmailTemplates.USER_FEEDBACK_SESSION.replace("${status}", FEEDBACK_STATUS_SESSION_CLOSED);
        String additionalContactInformation = getAdditionalContactInformationFragment(course);
        return generateFeedbackSessionEmailBases(course, session, studentsToEmail, instructors, template,
                EmailType.FEEDBACK_CLOSED.getSubject(), FEEDBACK_ACTION_VIEW, additionalContactInformation);
    }

    /**
     * Generates the feedback session published emails for the given {@code session}.
     */
    public List<EmailWrapper> generateFeedbackSessionPublishedEmails(FeedbackSessionAttributes session) {

        boolean isEmailNeeded = fsLogic.isFeedbackSessionViewableToStudents(session);
        List<InstructorAttributes> instructors = isEmailNeeded
                                                 ? instructorsLogic.getInstructorsForCourse(session.getCourseId())
                                                 : new ArrayList<>();
        List<StudentAttributes> students = isEmailNeeded
                                           ? studentsLogic.getStudentsForCourse(session.getCourseId())
                                           : new ArrayList<>();
        return generateFeedbackSessionPublishedEmails(session, students, instructors);
    }

    /**
     * Generates the feedback session published emails for the given {@code students} and
     * {@code instructors} in {@code session}.
     */
    public List<EmailWrapper> generateFeedbackSessionPublishedEmails(FeedbackSessionAttributes session,
            List<StudentAttributes> students, List<InstructorAttributes> instructors) {

        String template = EmailTemplates.USER_FEEDBACK_SESSION_PUBLISHED;

        CourseAttributes course = coursesLogic.getCourse(session.getCourseId());

        String additionalContactInformation = getAdditionalContactInformationFragment(course);
        return generateFeedbackSessionEmailBases(course, session, students, instructors, template,
                EmailType.FEEDBACK_PUBLISHED.getSubject(), FEEDBACK_ACTION_VIEW, additionalContactInformation);
    }

    /**
     * Generates the feedback session published emails for the given {@code session}.
     */
    public List<EmailWrapper> generateFeedbackSessionUnpublishedEmails(FeedbackSessionAttributes session) {

        String template = EmailTemplates.USER_FEEDBACK_SESSION_UNPUBLISHED;

        CourseAttributes course = coursesLogic.getCourse(session.getCourseId());
        boolean isEmailNeeded = fsLogic.isFeedbackSessionViewableToStudents(session);
        List<InstructorAttributes> instructors = isEmailNeeded
                                                 ? instructorsLogic.getInstructorsForCourse(session.getCourseId())
                                                 : new ArrayList<>();
        List<StudentAttributes> students = isEmailNeeded
                                           ? studentsLogic.getStudentsForCourse(session.getCourseId())
                                           : new ArrayList<>();

        return generateFeedbackSessionEmailBases(course, session, students, instructors, template,
                EmailType.FEEDBACK_UNPUBLISHED.getSubject());
    }

    private List<EmailWrapper> generateFeedbackSessionEmailBases(
            CourseAttributes course, FeedbackSessionAttributes session, List<StudentAttributes> students,
            List<InstructorAttributes> instructors, String template, String subject) {
        String additionalContactInformation = getAdditionalContactInformationFragment(course);
        return generateFeedbackSessionEmailBases(course, session, students, instructors, template, subject,
                FEEDBACK_ACTION_SUBMIT_EDIT_OR_VIEW, additionalContactInformation);
    }

    private List<EmailWrapper> generateFeedbackSessionEmailBases(
            CourseAttributes course, FeedbackSessionAttributes session, List<StudentAttributes> students,
            List<InstructorAttributes> instructors, String template, String subject, String feedbackAction,
            String additionalContactInformation) {

        List<EmailWrapper> emails = new ArrayList<>();
        for (StudentAttributes student : students) {
            emails.add(generateFeedbackSessionEmailBaseForStudents(course, session, student,
                    template, subject, feedbackAction, additionalContactInformation));
        }
        for (InstructorAttributes instructor : instructors) {
            emails.add(generateFeedbackSessionEmailBaseForInstructors(course, session, instructor,
                    template, subject, feedbackAction, additionalContactInformation));
        }
        return emails;
    }

    private EmailWrapper generateFeedbackSessionEmailBaseForStudents(
            CourseAttributes course, FeedbackSessionAttributes session, StudentAttributes student, String template,
            String subject, String feedbackAction, String additionalContactInformation) {

        String submitUrl = Config.getFrontEndAppUrl(Const.WebPageURIs.SESSION_SUBMISSION_PAGE)
                .withCourseId(course.getId())
                .withSessionName(session.getFeedbackSessionName())
                .withRegistrationKey(StringHelper.encrypt(student.key))
                .withStudentEmail(student.email)
                .toAbsoluteString();

        String reportUrl = Config.getFrontEndAppUrl(Const.WebPageURIs.SESSION_RESULTS_PAGE)
                .withCourseId(course.getId())
                .withSessionName(session.getFeedbackSessionName())
                .withRegistrationKey(StringHelper.encrypt(student.key))
                .withStudentEmail(student.email)
                .toAbsoluteString();

        String emailBody = Templates.populateTemplate(template,
                "${userName}", SanitizationHelper.sanitizeForHtml(student.name),
                "${courseName}", SanitizationHelper.sanitizeForHtml(course.getName()),
                "${courseId}", SanitizationHelper.sanitizeForHtml(course.getId()),
                "${feedbackSessionName}", SanitizationHelper.sanitizeForHtml(session.getFeedbackSessionName()),
                "${deadline}", SanitizationHelper.sanitizeForHtml(
                        TimeHelper.formatInstant(session.getEndTime(), session.getTimeZone(), DATETIME_DISPLAY_FORMAT)),
                "${instructorFragment}", "",
                "${sessionInstructions}", session.getInstructionsString(),
                "${submitUrl}", submitUrl,
                "${reportUrl}", reportUrl,
                "${feedbackAction}", feedbackAction,
                "${additionalContactInformation}", additionalContactInformation);

        EmailWrapper email = getEmptyEmailAddressedToEmail(student.email);
        email.setSubject(String.format(subject, course.getName(), session.getFeedbackSessionName()));
        email.setContent(emailBody);
        return email;
    }

    /**
     * Generates the preamble for emails that are sent to students of course {@code courseId}
     * to be shown in the email copies that are sent to the instructors of the same course.
     */
    private String generateInstructorPreamble(String courseId, String courseName) {

        String courseIdentifier = "[" + SanitizationHelper.sanitizeForHtml(courseId) + "] "
                + SanitizationHelper.sanitizeForHtml(courseName);

        return "<p>The email below has been sent to students of course: "
            + courseIdentifier + ".<br>" + System.lineSeparator()
            + "<br>" + System.lineSeparator()
            + "=== Email message as seen by the students ===</p>" + System.lineSeparator();
    }

    private EmailWrapper generateFeedbackSessionEmailBaseForInstructors(
            CourseAttributes course, FeedbackSessionAttributes session, InstructorAttributes instructor,
            String template, String subject, String feedbackAction, String additionalContactInformation) {

        String instructorFragment = generateInstructorPreamble(course.getId(), course.getName());

        String emailBody = Templates.populateTemplate(template,
                "${userName}", SanitizationHelper.sanitizeForHtml(instructor.name),
                "${courseName}", SanitizationHelper.sanitizeForHtml(course.getName()),
                "${courseId}", SanitizationHelper.sanitizeForHtml(course.getId()),
                "${feedbackSessionName}", SanitizationHelper.sanitizeForHtml(session.getFeedbackSessionName()),
                "${deadline}", SanitizationHelper.sanitizeForHtml(
                        TimeHelper.formatInstant(session.getEndTime(), session.getTimeZone(), DATETIME_DISPLAY_FORMAT)),
                "${instructorFragment}", instructorFragment,
                "${sessionInstructions}", session.getInstructionsString(),
                "${submitUrl}", "{in the actual email sent to the students, this will be the unique link}",
                "${reportUrl}", "{in the actual email sent to the students, this will be the unique link}",
                "${feedbackAction}", feedbackAction,
                "${additionalContactInformation}", additionalContactInformation);

        EmailWrapper email = getEmptyEmailAddressedToEmail(instructor.email);
        email.setSubject(String.format(subject, course.getName(), session.getFeedbackSessionName()));
        email.setContent(emailBody);
        return email;
    }

    private boolean isYetToJoinCourse(StudentAttributes student) {
        return student.googleId == null || student.googleId.isEmpty();
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
        email.setSubject(String.format(EmailType.NEW_INSTRUCTOR_ACCOUNT.getSubject(),
                SanitizationHelper.sanitizeForHtml(instructorName)));
        email.setContent(emailBody);
        return email;
    }

    /**
     * Generates the course join email for the given {@code student} in {@code course}.
     */
    public EmailWrapper generateStudentCourseJoinEmail(CourseAttributes course, StudentAttributes student) {

        String emailBody = Templates.populateTemplate(
                fillUpStudentJoinFragment(student, EmailTemplates.USER_COURSE_JOIN),
                "${userName}", SanitizationHelper.sanitizeForHtml(student.name),
                "${courseName}", SanitizationHelper.sanitizeForHtml(course.getName()),
                "${coOwnersEmails}", generateCoOwnersEmailsLine(course.getId()),
                "${supportEmail}", Config.SUPPORT_EMAIL);

        EmailWrapper email = getEmptyEmailAddressedToEmail(student.email);
        email.setSubject(String.format(EmailType.STUDENT_COURSE_JOIN.getSubject(),
                                       course.getName(), course.getId()));
        email.setContent(emailBody);
        return email;
    }

    /**
     * Generates the course re-join email for the given {@code student} in {@code course}.
     */
    public EmailWrapper generateStudentCourseRejoinEmailAfterGoogleIdReset(
            CourseAttributes course, StudentAttributes student) {

        String emailBody = Templates.populateTemplate(
                fillUpStudentRejoinAfterGoogleIdResetFragment(student, EmailTemplates.USER_COURSE_JOIN),
                "${userName}", SanitizationHelper.sanitizeForHtml(student.name),
                "${courseName}", SanitizationHelper.sanitizeForHtml(course.getName()),
                "${coOwnersEmails}", generateCoOwnersEmailsLine(course.getId()),
                "${supportEmail}", Config.SUPPORT_EMAIL);

        EmailWrapper email = getEmptyEmailAddressedToEmail(student.email);
        email.setSubject(String.format(EmailType.STUDENT_COURSE_REJOIN_AFTER_GOOGLE_ID_RESET.getSubject(),
                                       course.getName(), course.getId()));
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
                fillUpInstructorJoinFragment(instructor, EmailTemplates.USER_COURSE_JOIN),
                "${userName}", SanitizationHelper.sanitizeForHtml(instructor.getName()),
                "${courseName}", SanitizationHelper.sanitizeForHtml(course.getName()),
                "${inviterName}", SanitizationHelper.sanitizeForHtml(inviter.getName()),
                "${inviterEmail}", SanitizationHelper.sanitizeForHtml(inviter.getEmail()),
                "${supportEmail}", Config.SUPPORT_EMAIL);

        EmailWrapper email = getEmptyEmailAddressedToEmail(instructor.getEmail());
        email.setSubject(String.format(EmailType.INSTRUCTOR_COURSE_JOIN.getSubject(),
                                       course.getName(), course.getId()));
        email.setContent(emailBody);
        return email;
    }

    /**
     * Generates the course re-join email for the given {@code instructor} in {@code course}.
     */
    public EmailWrapper generateInstructorCourseRejoinEmailAfterGoogleIdReset(
            InstructorAttributes instructor, CourseAttributes course, String institute) {

        String emailBody = Templates.populateTemplate(
                fillUpInstructorRejoinAfterGoogleIdResetFragment(instructor, EmailTemplates.USER_COURSE_JOIN, institute),
                "${userName}", SanitizationHelper.sanitizeForHtml(instructor.getName()),
                "${courseName}", SanitizationHelper.sanitizeForHtml(course.getName()),
                "${supportEmail}", Config.SUPPORT_EMAIL);

        EmailWrapper email = getEmptyEmailAddressedToEmail(instructor.getEmail());
        email.setSubject(String.format(EmailType.INSTRUCTOR_COURSE_REJOIN_AFTER_GOOGLE_ID_RESET.getSubject(),
                course.getName(), course.getId()));
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
        email.setSubject(String.format(EmailType.USER_COURSE_REGISTER.getSubject(),
                course.getName(), course.getId()));
        email.setContent(emailBody);
        return email;
    }

    private String fillUpStudentJoinFragment(StudentAttributes student, String emailBody) {
        String joinUrl = Config.getFrontEndAppUrl(student.getRegistrationUrl()).toAbsoluteString();

        return Templates.populateTemplate(emailBody,
                "${joinFragment}", EmailTemplates.FRAGMENT_STUDENT_COURSE_JOIN,
                "${joinUrl}", joinUrl);
    }

    private String fillUpStudentRejoinAfterGoogleIdResetFragment(StudentAttributes student, String emailBody) {
        String joinUrl = Config.getFrontEndAppUrl(student.getRegistrationUrl()).toAbsoluteString();

        return Templates.populateTemplate(emailBody,
                "${joinFragment}", EmailTemplates.FRAGMENT_STUDENT_COURSE_REJOIN_AFTER_GOOGLE_ID_RESET,
                "${joinUrl}", joinUrl,
                "${supportEmail}", Config.SUPPORT_EMAIL);
    }

    private String fillUpInstructorJoinFragment(InstructorAttributes instructor, String emailBody) {
        String joinUrl = Config.getFrontEndAppUrl(Const.WebPageURIs.JOIN_PAGE)
                .withRegistrationKey(StringHelper.encrypt(instructor.key))
                .withEntityType(Const.EntityType.INSTRUCTOR)
                .toAbsoluteString();

        return Templates.populateTemplate(emailBody,
                "${joinFragment}", EmailTemplates.FRAGMENT_INSTRUCTOR_COURSE_JOIN,
                "${joinUrl}", joinUrl);
    }

    private String fillUpInstructorRejoinAfterGoogleIdResetFragment(
            InstructorAttributes instructor, String emailBody, String institute) {
        AppUrl url = Config.getFrontEndAppUrl(Const.WebPageURIs.JOIN_PAGE)
                .withRegistrationKey(StringHelper.encrypt(instructor.key))
                .withEntityType(Const.EntityType.INSTRUCTOR);
        if (institute != null) {
            url = url.withInstructorInstitution(institute);
        }
        String joinUrl = url.toAbsoluteString();

        return Templates.populateTemplate(emailBody,
                "${joinFragment}", EmailTemplates.FRAGMENT_INSTRUCTOR_COURSE_REJOIN_AFTER_GOOGLE_ID_RESET,
                "${joinUrl}", joinUrl,
                "${supportEmail}", Config.SUPPORT_EMAIL);
    }

    /**
     * Generates the logs compilation email for the given {@code logs}.
     */
    public EmailWrapper generateCompiledLogsEmail(List<String> logMessages, List<String> logLevels) {
        StringBuilder emailBody = new StringBuilder();
        for (int i = 0; i < logMessages.size(); i++) {
            emailBody.append(generateSevereErrorLogLine(i, logMessages.get(i), logLevels.get(i)));
        }

        EmailWrapper email = getEmptyEmailAddressedToEmail(Config.SUPPORT_EMAIL);
        email.setSubject(String.format(EmailType.SEVERE_LOGS_COMPILATION.getSubject(), Config.APP_VERSION));
        email.setContent(emailBody.toString());
        return email;
    }

    private String generateSevereErrorLogLine(int index, String logMessage, String logLevel) {
        return Templates.populateTemplate(
                EmailTemplates.SEVERE_ERROR_LOG_LINE,
                "${index}", String.valueOf(index),
                "${errorType}", logLevel,
                "${errorMessage}", logMessage);
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
    private String getAdditionalContactInformationFragment(CourseAttributes course) {
        return Templates.populateTemplate(EmailTemplates.FRAGMENT_SESSION_ADDITIONAL_CONTACT_INFORMATION,
                "${coOwnersEmails}", generateCoOwnersEmailsLine(course.getId()),
                "${supportEmail}", Config.SUPPORT_EMAIL);
    }
}
