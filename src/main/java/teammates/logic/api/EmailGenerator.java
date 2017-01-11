package teammates.logic.api;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.UserType;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.TeammatesException;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.common.util.Logger;
import teammates.common.util.StringHelper;
import teammates.common.util.Templates;
import teammates.common.util.Templates.EmailTemplates;
import teammates.common.util.TimeHelper;
import teammates.logic.core.CommentsLogic;
import teammates.logic.core.CoursesLogic;
import teammates.logic.core.FeedbackSessionsLogic;
import teammates.logic.core.InstructorsLogic;
import teammates.logic.core.StudentsLogic;

import com.google.appengine.api.log.AppLogLine;

/**
 * Handles operations related to generating emails to be sent from provided templates.
 * @see EmailTemplates
 * @see EmailType
 * @see EmailWrapper
 */
public class EmailGenerator {
    
    private static final Logger log = Logger.getLogger();
    private static final CommentsLogic commentsLogic = CommentsLogic.inst();
    private static final CoursesLogic coursesLogic = CoursesLogic.inst();
    private static final FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
    private static final InstructorsLogic instructorsLogic = InstructorsLogic.inst();
    private static final StudentsLogic studentsLogic = StudentsLogic.inst();
    
    /**
     * Generates the feedback session opening emails for the given {@code session}.
     */
    public List<EmailWrapper> generateFeedbackSessionOpeningEmails(FeedbackSessionAttributes session) {
        
        String template = EmailTemplates.USER_FEEDBACK_SESSION;
        
        CourseAttributes course = coursesLogic.getCourse(session.getCourseId());
        boolean isEmailNeeded = fsLogic.isFeedbackSessionForStudentsToAnswer(session);
        List<InstructorAttributes> instructors = isEmailNeeded
                                                 ? instructorsLogic.getInstructorsForCourse(session.getCourseId())
                                                 : new ArrayList<InstructorAttributes>();
        List<StudentAttributes> students = isEmailNeeded
                                           ? studentsLogic.getStudentsForCourse(session.getCourseId())
                                           : new ArrayList<StudentAttributes>();
        
        List<EmailWrapper> emails = generateFeedbackSessionEmailBases(course, session, students, instructors, template,
                                                                      EmailType.FEEDBACK_OPENING.getSubject());
        for (EmailWrapper email : emails) {
            email.setContent(email.getContent().replace("${status}", "is now open"));
        }
        return emails;
    }
    
    /**
     * Generates the feedback session reminder emails for the given {@code session} for {@code students}
     * and {@code instructorsToRemind}. In addition, the emails will also be forwarded to {@code instructorsToNotify}.
     */
    public List<EmailWrapper> generateFeedbackSessionReminderEmails(
            FeedbackSessionAttributes session, List<StudentAttributes> students,
            List<InstructorAttributes> instructorsToRemind, List<InstructorAttributes> instructorsToNotify) {
        
        CourseAttributes course = coursesLogic.getCourse(session.getCourseId());
        String template = EmailTemplates.USER_FEEDBACK_SESSION;
        List<EmailWrapper> emails =
                generateFeedbackSessionEmailBasesForInstructorReminders(course, session, instructorsToRemind, template,
                                                                        EmailType.FEEDBACK_SESSION_REMINDER.getSubject());
        emails.addAll(generateFeedbackSessionEmailBases(course, session, students, instructorsToNotify, template,
                                                        EmailType.FEEDBACK_SESSION_REMINDER.getSubject()));
        
        for (EmailWrapper email : emails) {
            email.setContent(email.getContent().replace("${status}", "is still open for submissions"));
        }
        return emails;
    }
    
    /**
     * Generates the feedback submission confirmation email for the given {@code session} for {@code student}
     */
    public EmailWrapper generateFeedbackSubmissionConfirmationEmailForStudent(
            FeedbackSessionAttributes session, StudentAttributes student, Calendar timestamp) {
        
        CourseAttributes course = coursesLogic.getCourse(session.getCourseId());
        String submitUrl = Config.getAppUrl(Const.ActionURIs.STUDENT_FEEDBACK_SUBMISSION_EDIT_PAGE)
                .withCourseId(course.getId())
                .withSessionName(session.getFeedbackSessionName())
                .withRegistrationKey(StringHelper.encrypt(student.key))
                .withStudentEmail(student.email)
                .toAbsoluteString();
        return generateSubmissionConfirmationEmail(course, session, submitUrl, student.name, student.email, timestamp);
    }

    /**
     * Generates the feedback submission confirmation email for the given {@code session} for {@code instructor}.
     */
    public EmailWrapper generateFeedbackSubmissionConfirmationEmailForInstructor(
            FeedbackSessionAttributes session, InstructorAttributes instructor, Calendar timestamp) {
        
        CourseAttributes course = coursesLogic.getCourse(session.getCourseId());
        String submitUrl = Config.getAppUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_SUBMISSION_EDIT_PAGE)
                .withCourseId(course.getId())
                .withSessionName(session.getFeedbackSessionName())
                .toAbsoluteString();

        return generateSubmissionConfirmationEmail(course, session, submitUrl, instructor.name, instructor.email, timestamp);
    }
    
    private List<EmailWrapper> generateFeedbackSessionEmailBasesForInstructorReminders(
            CourseAttributes course, FeedbackSessionAttributes session, List<InstructorAttributes> instructors,
            String template, String subject) {
        
        List<EmailWrapper> emails = new ArrayList<EmailWrapper>();
        for (InstructorAttributes instructor : instructors) {
            emails.add(generateFeedbackSessionEmailBaseForInstructorReminders(course, session, instructor,
                                                                              template, subject));
        }
        return emails;
    }
    
    private EmailWrapper generateSubmissionConfirmationEmail(
            CourseAttributes course, FeedbackSessionAttributes session, String submitUrl,
            String userName, String userEmail, Calendar timestamp) {
        Calendar time = TimeHelper.convertToUserTimeZone(timestamp, session.getTimeZone());
        String template = EmailTemplates.USER_FEEDBACK_SUBMISSION_CONFIRMATION;
        String subject = EmailType.FEEDBACK_SUBMISSION_CONFIRMATION.getSubject();
        
        String emailBody = Templates.populateTemplate(template,
                "${userName}", userName,
                "${courseName}", course.getName(),
                "${courseId}", course.getId(),
                "${feedbackSessionName}", session.getFeedbackSessionName(),
                "${deadline}", TimeHelper.formatTime12H(session.getEndTime()),
                "${submitUrl}", submitUrl,
                "${timeStamp}", TimeHelper.formatTime12H(time.getTime()),
                "${supportEmail}", Config.SUPPORT_EMAIL);
        
        EmailWrapper email = getEmptyEmailAddressedToEmail(userEmail);
        email.setSubject(String.format(subject, course.getName(), session.getFeedbackSessionName()));
        email.setContent(emailBody);
        return email;
        
    }
    
    private EmailWrapper generateFeedbackSessionEmailBaseForInstructorReminders(
            CourseAttributes course, FeedbackSessionAttributes session, InstructorAttributes instructor,
            String template, String subject) {
        
        String submitUrl = Config.getAppUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_SUBMISSION_EDIT_PAGE)
                                 .withCourseId(course.getId())
                                 .withSessionName(session.getFeedbackSessionName())
                                 .toAbsoluteString();
        
        String reportUrl = Config.getAppUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_PAGE)
                                 .withCourseId(course.getId())
                                 .withSessionName(session.getFeedbackSessionName())
                                 .toAbsoluteString();
        
        String emailBody = Templates.populateTemplate(template,
                "${userName}", instructor.name,
                "${courseName}", course.getName(),
                "${courseId}", course.getId(),
                "${feedbackSessionName}", session.getFeedbackSessionName(),
                "${deadline}", TimeHelper.formatTime12H(session.getEndTime()),
                "${instructorFragment}", "",
                "${submitUrl}", submitUrl,
                "${reportUrl}", reportUrl,
                "${supportEmail}", Config.SUPPORT_EMAIL);
        
        EmailWrapper email = getEmptyEmailAddressedToEmail(instructor.email);
        email.setSubject(String.format(subject, course.getName(), session.getFeedbackSessionName()));
        email.setContent(emailBody);
        return email;
    }
    
    /**
     * Generates the feedback session closing emails for the given {@code session}.
     */
    public List<EmailWrapper> generateFeedbackSessionClosingEmails(FeedbackSessionAttributes session) {
        
        List<StudentAttributes> students = new ArrayList<StudentAttributes>();
        boolean isEmailNeeded = fsLogic.isFeedbackSessionForStudentsToAnswer(session);
        
        if (isEmailNeeded) {
            List<StudentAttributes> studentsForCourse = studentsLogic.getStudentsForCourse(session.getCourseId());
            
            for (StudentAttributes student : studentsForCourse) {
                try {
                    if (!fsLogic.isFeedbackSessionFullyCompletedByStudent(session.getFeedbackSessionName(),
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
        
        String template = EmailTemplates.USER_FEEDBACK_SESSION_CLOSING;
        CourseAttributes course = coursesLogic.getCourse(session.getCourseId());
        List<InstructorAttributes> instructors = isEmailNeeded
                                                 ? instructorsLogic.getInstructorsForCourse(session.getCourseId())
                                                 : new ArrayList<InstructorAttributes>();
        
        List<EmailWrapper> emails = generateFeedbackSessionEmailBases(course, session, students, instructors, template,
                                                                      EmailType.FEEDBACK_CLOSING.getSubject());
        for (EmailWrapper email : emails) {
            email.setContent(email.getContent().replace("${status}", "is closing soon"));
        }
        return emails;
    }
    
    /**
     * Generates the feedback session closed emails for the given {@code session}.
     * @throws EntityDoesNotExistException
     */
    public List<EmailWrapper> generateFeedbackSessionClosedEmails(FeedbackSessionAttributes session) {
        
        if (session.isPrivateSession()) {
            return new ArrayList<EmailWrapper>();
        }
        
        CourseAttributes course = coursesLogic.getCourse(session.getCourseId());
        boolean isEmailNeededForStudents = false;
        try {
            isEmailNeededForStudents = fsLogic.isFeedbackSessionHasQuestionForStudents(
                    session.getFeedbackSessionName(), session.getCourseId());
        } catch (EntityDoesNotExistException e) {
            log.severe("Course " + session.getCourseId() + " does not exist or "
                    + "session " + session.getFeedbackSessionName() + " does not exist");
        }
        List<InstructorAttributes> instructors = instructorsLogic.getInstructorsForCourse(session.getCourseId());
        List<StudentAttributes> students = isEmailNeededForStudents
                                           ? studentsLogic.getStudentsForCourse(session.getCourseId())
                                           : new ArrayList<StudentAttributes>();
        return generateFeedbackSessionClosedEmail(course, session, instructors, students);
    }
    
    /**
     * Generates the feedback session published emails for the given {@code session}.
     */
    public List<EmailWrapper> generateFeedbackSessionPublishedEmails(FeedbackSessionAttributes session) {
        
        String template = EmailTemplates.USER_FEEDBACK_SESSION_PUBLISHED;
        
        CourseAttributes course = coursesLogic.getCourse(session.getCourseId());
        boolean isEmailNeeded = fsLogic.isFeedbackSessionViewableToStudents(session);
        List<InstructorAttributes> instructors = isEmailNeeded
                                                 ? instructorsLogic.getInstructorsForCourse(session.getCourseId())
                                                 : new ArrayList<InstructorAttributes>();
        List<StudentAttributes> students = isEmailNeeded
                                           ? studentsLogic.getStudentsForCourse(session.getCourseId())
                                           : new ArrayList<StudentAttributes>();
        
        List<EmailWrapper> emails = generateFeedbackSessionEmailBases(course, session, students, instructors, template,
                                                                      EmailType.FEEDBACK_PUBLISHED.getSubject());
        return emails;
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
                                                 : new ArrayList<InstructorAttributes>();
        List<StudentAttributes> students = isEmailNeeded
                                           ? studentsLogic.getStudentsForCourse(session.getCourseId())
                                           : new ArrayList<StudentAttributes>();
        
        List<EmailWrapper> emails = generateFeedbackSessionEmailBases(course, session, students, instructors, template,
                                                                      EmailType.FEEDBACK_UNPUBLISHED.getSubject());
        return emails;
    }
    
    private List<EmailWrapper> generateFeedbackSessionEmailBases(
            CourseAttributes course, FeedbackSessionAttributes session, List<StudentAttributes> students,
            List<InstructorAttributes> instructors, String template, String subject) {
        
        List<EmailWrapper> emails = new ArrayList<EmailWrapper>();
        for (StudentAttributes student : students) {
            emails.add(generateFeedbackSessionEmailBaseForStudents(course, session, student, template, subject));
        }
        for (InstructorAttributes instructor : instructors) {
            emails.add(generateFeedbackSessionEmailBaseForInstructors(course, session, instructor, template, subject));
        }
        return emails;
    }
    
    private EmailWrapper generateFeedbackSessionEmailBaseForStudents(
            CourseAttributes course, FeedbackSessionAttributes session, StudentAttributes student, String template,
            String subject) {
        
        String submitUrl = Config.getAppUrl(Const.ActionURIs.STUDENT_FEEDBACK_SUBMISSION_EDIT_PAGE)
                                 .withCourseId(course.getId())
                                 .withSessionName(session.getFeedbackSessionName())
                                 .withRegistrationKey(StringHelper.encrypt(student.key))
                                 .withStudentEmail(student.email)
                                 .toAbsoluteString();
        
        String reportUrl = Config.getAppUrl(Const.ActionURIs.STUDENT_FEEDBACK_RESULTS_PAGE)
                                 .withCourseId(course.getId())
                                 .withSessionName(session.getFeedbackSessionName())
                                 .withRegistrationKey(StringHelper.encrypt(student.key))
                                 .withStudentEmail(student.email)
                                 .toAbsoluteString();
        
        String emailBody = Templates.populateTemplate(template,
                "${userName}", student.name,
                "${courseName}", course.getName(),
                "${courseId}", course.getId(),
                "${feedbackSessionName}", session.getFeedbackSessionName(),
                "${deadline}", TimeHelper.formatTime12H(session.getEndTime()),
                "${instructorFragment}", "",
                "${submitUrl}", submitUrl,
                "${reportUrl}", reportUrl,
                "${supportEmail}", Config.SUPPORT_EMAIL);
        
        EmailWrapper email = getEmptyEmailAddressedToEmail(student.email);
        email.setSubject(String.format(subject, course.getName(), session.getFeedbackSessionName()));
        email.setContent(emailBody);
        return email;
    }
    
    private EmailWrapper generateFeedbackSessionEmailBaseForInstructors(
            CourseAttributes course, FeedbackSessionAttributes session, InstructorAttributes instructor,
            String template, String subject) {
        
        String emailBody = Templates.populateTemplate(template,
                "${userName}", instructor.name,
                "${courseName}", course.getName(),
                "${courseId}", course.getId(),
                "${feedbackSessionName}", session.getFeedbackSessionName(),
                "${deadline}", TimeHelper.formatTime12H(session.getEndTime()),
                "${instructorFragment}",
                        "The email below has been sent to students of course: " + course.getId()
                        + ".<p/><br>" + Const.EOL + "<br>" + Const.EOL
                        + "=== Email message as seen by the students ===<br>" + Const.EOL,
                "${submitUrl}", "{in the actual email sent to the students, this will be the unique link}",
                "${reportUrl}", "{in the actual email sent to the students, this will be the unique link}",
                "${supportEmail}", Config.SUPPORT_EMAIL);
        
        EmailWrapper email = getEmptyEmailAddressedToEmail(instructor.email);
        email.setSubject(String.format(subject, course.getName(), session.getFeedbackSessionName()));
        email.setContent(emailBody);
        return email;
    }
    
    private List<EmailWrapper> generateFeedbackSessionClosedEmail(
            CourseAttributes course, FeedbackSessionAttributes session,
            List<InstructorAttributes> instructors, List<StudentAttributes> students) {

        List<EmailWrapper> emails = new ArrayList<EmailWrapper>();

        for (InstructorAttributes instructor : instructors) {
            emails.add(generateFeedbackSessionClosedEmail(course, session, instructor.name, instructor.email));
        }
        for (StudentAttributes student : students) {
            emails.add(generateFeedbackSessionClosedEmail(course, session, student.name, student.email));
        }
        
        return emails;
    }
    
    private EmailWrapper generateFeedbackSessionClosedEmail(CourseAttributes course,
            FeedbackSessionAttributes session, String userName, String userEmail) {
        String template = EmailTemplates.USER_FEEDBACK_SESSION_CLOSED;
        String subject = EmailType.FEEDBACK_CLOSED.getSubject();

        String emailBody = Templates.populateTemplate(template,
                "${userName}", userName,
                "${courseName}", course.getName(),
                "${courseId}", course.getId(),
                "${feedbackSessionName}", session.getFeedbackSessionName(),
                "${deadline}", TimeHelper.formatTime12H(session.getEndTime()),
                "${supportEmail}", Config.SUPPORT_EMAIL);

        EmailWrapper email = getEmptyEmailAddressedToEmail(userEmail);
        email.setSubject(String.format(subject, course.getName(), session.getFeedbackSessionName()));
        email.setContent(emailBody);
        return email;
    }
    
    /**
     * Generates the comments notification emails for the given {@code courseId}.
     */
    public List<EmailWrapper> generatePendingCommentsClearedEmails(String courseId) {
        
        Set<String> recipients;
        try {
            recipients = commentsLogic.getRecipientEmailsForSendingComments(courseId);
        } catch (EntityDoesNotExistException e) {
            log.severe("Recipient emails for pending comments in course : " + courseId + " could not be fetched");
            recipients = new HashSet<String>();
        }
        
        List<EmailWrapper> emails = new ArrayList<EmailWrapper>();
        CourseAttributes course = coursesLogic.getCourse(courseId);
        String template = EmailTemplates.USER_PENDING_COMMENTS_CLEARED;
        
        for (String recipientEmail : recipients) {
            StudentAttributes student = studentsLogic.getStudentForEmail(courseId, recipientEmail);
            if (student == null) {
                continue;
            }
            EmailWrapper email = generatePendingCommentsClearedEmailBaseForStudent(course, student, template);
            emails.add(email);
        }
        return emails;
    }
    
    private EmailWrapper generatePendingCommentsClearedEmailBaseForStudent(
            CourseAttributes course, StudentAttributes student, String template) {
        
        String commentsPageUrl = Config.getAppUrl(Const.ActionURIs.STUDENT_COMMENTS_PAGE)
                                       .withCourseId(course.getId())
                                       .toAbsoluteString();
        
        String emailBody = Templates.populateTemplate(
                isYetToJoinCourse(student) ? fillUpStudentJoinFragment(student, template)
                                           : template.replace("${joinFragment}", ""),
                "${userName}", student.name,
                "${courseName}", course.getName(),
                "${courseId}", course.getId(),
                "${commentsPageUrl}", commentsPageUrl,
                "${supportEmail}", Config.SUPPORT_EMAIL);
        
        EmailWrapper email = getEmptyEmailAddressedToEmail(student.email);
        email.setSubject(String.format(EmailType.PENDING_COMMENT_CLEARED.getSubject(),
                                       course.getName(), course.getId()));
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
            String instructorEmail, String instructorShortName, String joinUrl) {
        
        String emailBody = Templates.populateTemplate(EmailTemplates.NEW_INSTRUCTOR_ACCOUNT_WELCOME,
                "${userName}", instructorShortName,
                "${joinUrl}", joinUrl);
        
        EmailWrapper email = getEmptyEmailAddressedToEmail(instructorEmail);
        email.setBcc(Config.SUPPORT_EMAIL);
        email.setSubject(String.format(EmailType.NEW_INSTRUCTOR_ACCOUNT.getSubject(), instructorShortName));
        email.setContent(emailBody);
        return email;
    }
    
    /**
     * Generates the course join email for the given {@code student} in {@code course}.
     */
    public EmailWrapper generateStudentCourseJoinEmail(CourseAttributes course, StudentAttributes student) {
        
        String emailBody = Templates.populateTemplate(
                fillUpStudentJoinFragment(student, EmailTemplates.USER_COURSE_JOIN),
                "${userName}", student.name,
                "${courseName}", course.getName(),
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
                "${userName}", student.name,
                "${courseName}", course.getName(),
                "${supportEmail}", Config.SUPPORT_EMAIL);
        
        EmailWrapper email = getEmptyEmailAddressedToEmail(student.email);
        email.setSubject(String.format(EmailType.STUDENT_COURSE_REJOIN_AFTER_GOOGLE_ID_RESET.getSubject(),
                                       course.getName(), course.getId()));
        email.setContent(emailBody);
        return email;
    }
    
    /**
     * Generates the course join email for the given {@code instructor} in {@code course}.
     */
    public EmailWrapper generateInstructorCourseJoinEmail(CourseAttributes course, InstructorAttributes instructor) {
        
        String emailBody = Templates.populateTemplate(
                fillUpInstructorJoinFragment(instructor, EmailTemplates.USER_COURSE_JOIN),
                "${userName}", instructor.name,
                "${courseName}", course.getName(),
                "${supportEmail}", Config.SUPPORT_EMAIL);
        
        EmailWrapper email = getEmptyEmailAddressedToEmail(instructor.email);
        email.setSubject(String.format(EmailType.INSTRUCTOR_COURSE_JOIN.getSubject(),
                                       course.getName(), course.getId()));
        email.setContent(emailBody);
        return email;
    }
    
    private String fillUpStudentJoinFragment(StudentAttributes student, String emailBody) {
        String joinUrl = Config.getAppUrl(student.getRegistrationUrl()).toAbsoluteString();
        
        return Templates.populateTemplate(emailBody,
                "${joinFragment}", EmailTemplates.FRAGMENT_STUDENT_COURSE_JOIN,
                "${joinUrl}", joinUrl);
    }
    
    private String fillUpStudentRejoinAfterGoogleIdResetFragment(StudentAttributes student, String emailBody) {
        String joinUrl = Config.getAppUrl(student.getRegistrationUrl()).toAbsoluteString();
        
        return Templates.populateTemplate(emailBody,
                "${joinFragment}", EmailTemplates.FRAGMENT_STUDENT_COURSE_REJOIN_AFTER_GOOGLE_ID_RESET,
                "${joinUrl}", joinUrl,
                "${supportEmail}", Config.SUPPORT_EMAIL);
    }
    
    private String fillUpInstructorJoinFragment(InstructorAttributes instructor, String emailBody) {
        String joinUrl = Config.getAppUrl(Const.ActionURIs.INSTRUCTOR_COURSE_JOIN)
                               .withRegistrationKey(StringHelper.encrypt(instructor.key))
                               .toAbsoluteString();
        
        return Templates.populateTemplate(emailBody,
                "${joinFragment}", EmailTemplates.FRAGMENT_INSTRUCTOR_COURSE_JOIN,
                "${joinUrl}", joinUrl);
    }
    
    /**
     * Generates the system error report email for the given {@code error}.
     */
    public EmailWrapper generateSystemErrorEmail(
            String requestMethod, String requestUserAgent, String requestPath, String requestUrl,
            String requestParams, UserType userType, Throwable error) {
        
        String errorMessage = error.getMessage();
        String stackTrace = TeammatesException.toStringWithStackTrace(error);
        
        // If the error doesn't contain a short description, retrieve the first line of stack trace.
        // truncate stack trace at first "at" string
        if (errorMessage == null) {
            int msgTruncateIndex = stackTrace.indexOf("at");
            if (msgTruncateIndex > 0) {
                errorMessage = stackTrace.substring(0, msgTruncateIndex).trim();
            } else {
                errorMessage = "";
            }
        }
        
        String actualUser = userType == null || userType.id == null ? "Not logged in" : userType.id;
        
        String emailBody = Templates.populateTemplate(EmailTemplates.SYSTEM_ERROR,
                "${actualUser}", actualUser,
                "${requestMethod}", requestMethod,
                "${requestUserAgent}", requestUserAgent,
                "${requestUrl}", requestUrl,
                "${requestPath}", requestPath,
                "${requestParameters}", requestParams,
                "${errorMessage}", errorMessage,
                "${stackTrace}", stackTrace);
        
        EmailWrapper email = getEmptyEmailAddressedToEmail(Config.SUPPORT_EMAIL);
        email.setSubject(String.format(EmailType.ADMIN_SYSTEM_ERROR.getSubject(), Config.getAppVersion(), errorMessage));
        email.setContent(emailBody);
        return email;
    }
    
    /**
     * Generates the logs compilation email for the given {@code logs}.
     */
    public EmailWrapper generateCompiledLogsEmail(List<AppLogLine> logs) {
        StringBuilder emailBody = new StringBuilder();
        for (int i = 0; i < logs.size(); i++) {
            emailBody.append(generateSevereErrorLogLine(i, logs.get(i)));
        }
        
        EmailWrapper email = getEmptyEmailAddressedToEmail(Config.SUPPORT_EMAIL);
        email.setSubject(String.format(EmailType.SEVERE_LOGS_COMPILATION.getSubject(), Config.getAppVersion()));
        email.setContent(emailBody.toString());
        return email;
    }
    
    private String generateSevereErrorLogLine(int index, AppLogLine logLine) {
        return Templates.populateTemplate(
                EmailTemplates.SEVERE_ERROR_LOG_LINE,
                "${index}", String.valueOf(index),
                "${errorType}", logLine.getLogLevel().toString(),
                "${errorMessage}", logLine.getLogMessage().replace("\n", "<br>"));
    }
    
    /**
     * Generates a generic email with the specified {@code content}, {@code subject}, and {@code recipient}.
     */
    public EmailWrapper generateAdminEmail(String content, String subject, String recipient) {
        EmailWrapper email = getEmptyEmailAddressedToEmail(recipient);
        email.setSubject(subject);
        email.setContent(content);
        return email;
    }
    
    private EmailWrapper getEmptyEmailAddressedToEmail(String recipient) {
        EmailWrapper email = new EmailWrapper();
        email.setRecipient(recipient);
        email.setSenderEmail(Config.EMAIL_SENDEREMAIL);
        email.setSenderName(Config.EMAIL_SENDERNAME);
        email.setReplyTo(Config.EMAIL_REPLYTO);
        return email;
    }
    
}
