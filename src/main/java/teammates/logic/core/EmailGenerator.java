package teammates.logic.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

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
import teammates.common.util.StringHelper;
import teammates.common.util.Templates;
import teammates.common.util.TimeHelper;
import teammates.common.util.Templates.EmailTemplates;
import teammates.common.util.Utils;

/**
 * Handles operations related to generating emails to be sent from provided templates.
 * @see EmailTemplates
 * @see EmailType
 */
public class EmailGenerator {
    
    private static final Logger log = Utils.getLogger();
    private static final CommentsLogic commentsLogic = CommentsLogic.inst();
    private static final CoursesLogic coursesLogic = CoursesLogic.inst();
    private static final FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
    private static final InstructorsLogic instructorsLogic = InstructorsLogic.inst();
    private static final StudentsLogic studentsLogic = StudentsLogic.inst();
    
    private final String senderEmail;
    private final String senderName;
    private final String replyTo;
    
    public EmailGenerator() {
        senderEmail = "Admin@" + Config.getAppId() + ".appspotmail.com";
        senderName = "TEAMMATES Admin";
        replyTo = "teammates@comp.nus.edu.sg";
    }
    
    /**
     * Generates the feedback session opening emails for the given {@code session}.
     */
    public List<MimeMessage> generateFeedbackSessionOpeningEmails(FeedbackSessionAttributes session)
            throws MessagingException, IOException {
        
        String template = EmailTemplates.USER_FEEDBACK_SESSION;
        
        CourseAttributes course = coursesLogic.getCourse(session.getCourseId());
        List<InstructorAttributes> instructors = instructorsLogic.getInstructorsForCourse(session.getCourseId());
        List<StudentAttributes> students = fsLogic.isFeedbackSessionForStudentsToAnswer(session)
                                           ? studentsLogic.getStudentsForCourse(session.getCourseId())
                                           : new ArrayList<StudentAttributes>();
        
        List<MimeMessage> emails = generateFeedbackSessionEmailBases(course, session, students, instructors, template,
                                                                     EmailType.FEEDBACK_OPENING.getSubject());
        for (MimeMessage email : emails) {
            email.setContent(email.getContent().toString().replace("${status}", "is now open"), "text/html");
        }
        return emails;
    }
    
    /**
     * Generates the feedback session reminder emails for the given {@code session} for {@code students}
     * and {@code instructorsToRemind}. In addition, the emails will also be forwarded to {@code instructorsToNotify}.
     */
    public List<MimeMessage> generateFeedbackSessionReminderEmails(
            FeedbackSessionAttributes session, List<StudentAttributes> students,
            List<InstructorAttributes> instructorsToRemind, List<InstructorAttributes> instructorsToNotify)
                    throws MessagingException, IOException {
        
        CourseAttributes course = coursesLogic.getCourse(session.getCourseId());
        String template = EmailTemplates.USER_FEEDBACK_SESSION;
        List<MimeMessage> emails =
                generateFeedbackSessionEmailBasesForInstructorReminders(course, session, instructorsToRemind, template,
                                                                        EmailType.FEEDBACK_SESSION_REMINDER.getSubject());
        emails.addAll(generateFeedbackSessionEmailBases(course, session, students, instructorsToNotify, template,
                                                        EmailType.FEEDBACK_SESSION_REMINDER.getSubject()));
        
        for (MimeMessage email : emails) {
            email.setContent(email.getContent().toString().replace("${status}", "is still open for submissions"),
                             "text/html");
        }
        return emails;
    }
    
    private List<MimeMessage> generateFeedbackSessionEmailBasesForInstructorReminders(
            CourseAttributes course, FeedbackSessionAttributes session, List<InstructorAttributes> instructors,
            String template, String subject)
                    throws MessagingException, IOException {
        
        List<MimeMessage> emails = new ArrayList<MimeMessage>();
        for (InstructorAttributes instructor : instructors) {
            emails.add(generateFeedbackSessionEmailBaseForInstructorReminders(course, session, instructor,
                                                                              template, subject));
        }
        return emails;
    }
    
    private MimeMessage generateFeedbackSessionEmailBaseForInstructorReminders(
            CourseAttributes course, FeedbackSessionAttributes session, InstructorAttributes instructor,
            String template, String subject)
                    throws MessagingException, IOException {
        
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
                "${reportUrl}", reportUrl);
        
        MimeMessage email = getEmptyEmailAddressedToEmail(instructor.email);
        email.setSubject(String.format(subject, course.getName(), session.getFeedbackSessionName()));
        email.setContent(emailBody, "text/html");
        return email;
    }
    
    /**
     * Generates the feedback session closing emails for the given {@code session}.
     */
    public List<MimeMessage> generateFeedbackSessionClosingEmails(FeedbackSessionAttributes session)
            throws MessagingException, IOException {
        
        List<StudentAttributes> students = new ArrayList<StudentAttributes>();
        
        if (fsLogic.isFeedbackSessionForStudentsToAnswer(session)) {
            List<StudentAttributes> studentsForCourse = studentsLogic.getStudentsForCourse(session.getCourseId());
            
            for (StudentAttributes student : studentsForCourse) {
                try {
                    if (!fsLogic.isFeedbackSessionFullyCompletedByStudent(session.getFeedbackSessionName(),
                            session.getCourseId(), student.email)) {
                        students.add(student);
                    }
                } catch (EntityDoesNotExistException e) {
                    log.warning("Course " + session.getCourseId() + " does not exist or "
                                + "session " + session.getFeedbackSessionName() + " does not exist");
                    // Course or session cannot be found for one student => it will be the case for all students
                    // Do not waste time looping through all students
                    break;
                }
            }
        }
        
        String template = EmailTemplates.USER_FEEDBACK_SESSION_CLOSING;
        CourseAttributes course = coursesLogic.getCourse(session.getCourseId());
        List<InstructorAttributes> instructors = instructorsLogic.getInstructorsForCourse(session.getCourseId());
        
        List<MimeMessage> emails = generateFeedbackSessionEmailBases(course, session, students, instructors, template,
                                                                     EmailType.FEEDBACK_CLOSING.getSubject());
        for (MimeMessage email : emails) {
            email.setContent(email.getContent().toString().replace("${status}", "is closing soon"), "text/html");
        }
        return emails;
    }
    
    /**
     * Generates the feedback session published emails for the given {@code session}.
     */
    public List<MimeMessage> generateFeedbackSessionPublishedEmails(FeedbackSessionAttributes session)
            throws MessagingException, IOException {
        
        String template = EmailTemplates.USER_FEEDBACK_SESSION_PUBLISHED;
        
        CourseAttributes course = coursesLogic.getCourse(session.getCourseId());
        List<InstructorAttributes> instructors = instructorsLogic.getInstructorsForCourse(session.getCourseId());
        List<StudentAttributes> students = fsLogic.isFeedbackSessionViewableToStudents(session)
                                           ? studentsLogic.getStudentsForCourse(session.getCourseId())
                                           : new ArrayList<StudentAttributes>();
        
        List<MimeMessage> emails = generateFeedbackSessionEmailBases(course, session, students, instructors, template,
                                                                     EmailType.FEEDBACK_PUBLISHED.getSubject());
        return emails;
    }
    
    private List<MimeMessage> generateFeedbackSessionEmailBases(
            CourseAttributes course, FeedbackSessionAttributes session, List<StudentAttributes> students,
            List<InstructorAttributes> instructors, String template, String subject)
                    throws MessagingException, IOException {
        
        List<MimeMessage> emails = new ArrayList<MimeMessage>();
        for (StudentAttributes student : students) {
            emails.add(generateFeedbackSessionEmailBaseForStudents(course, session, student, template, subject));
        }
        for (InstructorAttributes instructor : instructors) {
            emails.add(generateFeedbackSessionEmailBaseForInstructors(course, session, instructor, template, subject));
        }
        return emails;
    }
    
    private MimeMessage generateFeedbackSessionEmailBaseForStudents(
            CourseAttributes course, FeedbackSessionAttributes session, StudentAttributes student, String template,
            String subject)
                    throws MessagingException, IOException {
        
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
                "${reportUrl}", reportUrl);
        
        MimeMessage email = getEmptyEmailAddressedToEmail(student.email);
        email.setSubject(String.format(subject, course.getName(), session.getFeedbackSessionName()));
        email.setContent(emailBody, "text/html");
        return email;
    }
    
    private MimeMessage generateFeedbackSessionEmailBaseForInstructors(
            CourseAttributes course, FeedbackSessionAttributes session, InstructorAttributes instructor,
            String template, String subject)
                    throws MessagingException, IOException {
        
        String emailBody = Templates.populateTemplate(template,
                "${userName}", instructor.name,
                "${courseName}", course.getName(),
                "${courseId}", course.getId(),
                "${feedbackSessionName}", session.getFeedbackSessionName(),
                "${deadline}", TimeHelper.formatTime12H(session.getEndTime()),
                "${instructorFragment}",
                        "The email below has been sent to students of course: " + course.getId() + ".<p/><br/>",
                "${submitUrl}", "{The student's unique submission url appears here}",
                "${reportUrl}", "{The student's unique results url appears here}");
        
        MimeMessage email = getEmptyEmailAddressedToEmail(instructor.email);
        email.setSubject(String.format(subject, course.getName(), session.getFeedbackSessionName()));
        email.setContent(emailBody, "text/html");
        return email;
    }
    
    /**
     * Generates the comments notification emails for the given {@code courseId}.
     */
    public List<MimeMessage> generatePendingCommentsClearedEmails(String courseId)
            throws MessagingException, IOException {
        
        Set<String> recipients;
        try {
            recipients = commentsLogic.getRecipientEmailsForSendingComments(courseId);
        } catch (EntityDoesNotExistException e) {
            log.severe("Recipient emails for pending comments in course : " + courseId + " could not be fetched");
            recipients = new HashSet<String>();
        }
        
        List<MimeMessage> emails = new ArrayList<MimeMessage>();
        CourseAttributes course = coursesLogic.getCourse(courseId);
        String template = EmailTemplates.USER_PENDING_COMMENTS_CLEARED;
        
        for (String recipientEmail : recipients) {
            StudentAttributes student = studentsLogic.getStudentForEmail(courseId, recipientEmail);
            if (student == null) {
                continue;
            }
            MimeMessage email = generatePendingCommentsClearedEmailBaseForStudent(course, student, template);
            emails.add(email);
        }
        return emails;
    }
    
    private MimeMessage generatePendingCommentsClearedEmailBaseForStudent(
            CourseAttributes course, StudentAttributes student, String template)
                    throws MessagingException, IOException {
        
        String commentsPageUrl = Config.getAppUrl(Const.ActionURIs.STUDENT_COMMENTS_PAGE)
                                       .withCourseId(course.getId())
                                       .toAbsoluteString();
        
        String emailBody = Templates.populateTemplate(
                isYetToJoinCourse(student) ? fillUpStudentJoinFragment(student, template)
                                           : template.replace("${joinFragment}", ""),
                "${userName}", student.name,
                "${courseName}", course.getName(),
                "${courseId}", course.getId(),
                "${commentsPageUrl}", commentsPageUrl);
        
        MimeMessage email = getEmptyEmailAddressedToEmail(student.email);
        email.setSubject(String.format(EmailType.PENDING_COMMENT_CLEARED.getSubject(),
                                       course.getName(), course.getId()));
        email.setContent(emailBody, "text/html");
        return email;
    }
    
    private boolean isYetToJoinCourse(StudentAttributes student) {
        return student.googleId == null || student.googleId.isEmpty();
    }
    
    /**
     * Generates the new instructor account join email for the given {@code instructor}.
     */
    public MimeMessage generateNewInstructorAccountJoinEmail(InstructorAttributes instructor,
                                                             String shortName, String institute)
            throws AddressException, MessagingException, IOException {
        
        String joinUrl = generateNewInstructorAccountJoinLink(instructor, institute);
        
        String emailBody = Templates.populateTemplate(EmailTemplates.NEW_INSTRUCTOR_ACCOUNT_WELCOME,
                "${userName}", shortName,
                "${joinUrl}", joinUrl);
        
        MimeMessage email = getEmptyEmailAddressedToEmail(instructor.email);
        email = addBccRecipientToEmail(email, Config.SUPPORT_EMAIL);
        email.setSubject(String.format(EmailType.NEW_INSTRUCTOR_ACCOUNT.getSubject(), shortName));
        email.setContent(emailBody, "text/html");
        return email;
    }
    
    /**
     * Generates the join link to be sent to the account requester's email.
     */
    public String generateNewInstructorAccountJoinLink(InstructorAttributes instructor, String institute) {
        return instructor == null
               ? ""
               : Config.getAppUrl(Const.ActionURIs.INSTRUCTOR_COURSE_JOIN)
                       .withRegistrationKey(StringHelper.encrypt(instructor.key))
                       .withInstructorInstitution(institute)
                       .toAbsoluteString();
    }
    
    private MimeMessage addBccRecipientToEmail(MimeMessage email, String newAddress)
            throws AddressException, MessagingException {
        email.addRecipient(Message.RecipientType.BCC, new InternetAddress(newAddress));
        return email;
    }
    
    /**
     * Generates the course join email for the given {@code student} in {@code course}.
     */
    public MimeMessage generateStudentCourseJoinEmail(CourseAttributes course, StudentAttributes student)
            throws AddressException, MessagingException, IOException {
        
        String emailBody = Templates.populateTemplate(
                fillUpStudentJoinFragment(student, EmailTemplates.USER_COURSE_JOIN),
                "${userName}", student.name,
                "${courseName}", course.getName());
        
        MimeMessage email = getEmptyEmailAddressedToEmail(student.email);
        email.setSubject(String.format(EmailType.STUDENT_COURSE_JOIN.getSubject(),
                                       course.getName(), course.getId()));
        email.setContent(emailBody, "text/html");
        return email;
    }
    
    /**
     * Generates the course re-join email for the given {@code student} in {@code course}.
     */
    public MimeMessage generateStudentCourseRejoinEmailAfterGoogleIdReset(
            CourseAttributes course, StudentAttributes student)
                    throws AddressException, MessagingException, IOException {
        
        String emailBody = Templates.populateTemplate(
                fillUpStudentRejoinAfterGoogleIdResetFragment(student, EmailTemplates.USER_COURSE_JOIN),
                "${userName}", student.name,
                "${courseName}", course.getName());
        
        MimeMessage email = getEmptyEmailAddressedToEmail(student.email);
        email.setSubject(String.format(EmailType.STUDENT_COURSE_REJOIN_AFTER_GOOGLE_ID_RESET.getSubject(),
                                       course.getName(), course.getId()));
        email.setContent(emailBody, "text/html");
        return email;
    }
    
    /**
     * Generates the course join email for the given {@code instructor} in {@code course}.
     */
    public MimeMessage generateInstructorCourseJoinEmail(CourseAttributes course, InstructorAttributes instructor)
            throws AddressException, MessagingException, IOException {
        
        String emailBody = Templates.populateTemplate(
                fillUpInstructorJoinFragment(instructor, EmailTemplates.USER_COURSE_JOIN),
                "${userName}", instructor.name,
                "${courseName}", course.getName());
        
        MimeMessage email = getEmptyEmailAddressedToEmail(instructor.email);
        email.setSubject(String.format(EmailType.INSTRUCTOR_COURSE_JOIN.getSubject(),
                                       course.getName(), course.getId()));
        email.setContent(emailBody, "text/html");
        return email;
    }
    
    private String fillUpStudentJoinFragment(StudentAttributes student, String emailBody) {
        String joinUrl = student == null
                         ? "{The join link unique for each student appears here}"
                         : Config.getAppUrl(student.getRegistrationUrl()).toAbsoluteString();
        
        return Templates.populateTemplate(emailBody,
                "${joinFragment}", EmailTemplates.FRAGMENT_STUDENT_COURSE_JOIN,
                "${joinUrl}", joinUrl);
    }
    
    private String fillUpStudentRejoinAfterGoogleIdResetFragment(StudentAttributes student, String emailBody) {
        String joinUrl = student == null
                         ? "{The join link unique for each student appears here}"
                         : Config.getAppUrl(student.getRegistrationUrl()).toAbsoluteString();
        
        return Templates.populateTemplate(emailBody,
                "${joinFragment}", EmailTemplates.FRAGMENT_STUDENT_COURSE_REJOIN_AFTER_GOOGLE_ID_RESET,
                "${joinUrl}", joinUrl);
    }
    
    private String fillUpInstructorJoinFragment(InstructorAttributes instructor, String emailBody) {
        String joinUrl = instructor == null
                         ? ""
                         : Config.getAppUrl(Const.ActionURIs.INSTRUCTOR_COURSE_JOIN)
                                 .withRegistrationKey(StringHelper.encrypt(instructor.key))
                                 .toAbsoluteString();
        
        return Templates.populateTemplate(emailBody,
                "${joinFragment}", EmailTemplates.FRAGMENT_INSTRUCTOR_COURSE_JOIN,
                "${joinUrl}", joinUrl);
    }
    
    /**
     * Generates the system error report email for the given {@code error}.
     */
    public MimeMessage generateSystemErrorEmail(
            String requestMethod, String requestUserAgent, String requestPath, String requestUrl,
            String requestParams, UserType userType, Throwable error)
                    throws AddressException, MessagingException, IOException {
        
        String errorMessage = error.getMessage();
        String stackTrace = TeammatesException.toStringWithStackTrace(error);
        
        // If the error doesn't contain a short description, retrieve the first line of stack trace.
        // truncate stack trace at first "at" string
        if (errorMessage == null) {
            int msgTruncateIndex = stackTrace.indexOf("at");
            if (msgTruncateIndex > 0) {
                errorMessage = stackTrace.substring(0, msgTruncateIndex);
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
        
        MimeMessage email = getEmptyEmailAddressedToEmail(Config.SUPPORT_EMAIL);
        email.setSubject(String.format(EmailType.ADMIN_SYSTEM_ERROR.getSubject(), Config.getAppVersion(), errorMessage));
        email.setContent(emailBody, "text/html");
        return email;
    }
    
    /**
     * Generates the logs compilation email for the given {@code logs}.
     */
    public MimeMessage generateCompiledLogsEmail(String logs)
            throws AddressException, MessagingException, IOException {
        
        String emailBody = logs.replace("\n", "<br>");
        
        MimeMessage email = getEmptyEmailAddressedToEmail(Config.SUPPORT_EMAIL);
        email.setSubject(String.format(EmailType.SEVERE_LOGS_COMPILATION.getSubject(), Config.getAppVersion()));
        email.setContent(emailBody, "text/html");
        return email;
    }
    
    /**
     * Generates a generic email with the specified {@code content}, {@code subject}, and {@code recipient}.
     */
    public MimeMessage generateAdminEmail(String content, String subject, String recipient)
            throws MessagingException, IOException {
        MimeMessage email = getEmptyEmailAddressedToEmail(recipient);
        email.setSubject(subject);
        email.setContent(content, "text/html");
        return email;
    }
    
    private MimeMessage getEmptyEmailAddressedToEmail(String recipient)
            throws MessagingException, AddressException, IOException {
        Session session = Session.getDefaultInstance(new Properties(), null);
        MimeMessage email = new MimeMessage(session);
        email.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
        email.setFrom(new InternetAddress(senderEmail, senderName));
        email.setReplyTo(new Address[] { new InternetAddress(replyTo) });
        return email;
    }
    
}
