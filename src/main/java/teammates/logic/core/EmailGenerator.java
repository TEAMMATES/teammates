package teammates.logic.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import teammates.common.util.StringHelper;
import teammates.common.util.Templates;
import teammates.common.util.TimeHelper;
import teammates.common.util.Templates.EmailTemplates;
import teammates.common.util.Utils;

public class EmailGenerator {
    
    public static final String SUBJECT_PREFIX_FEEDBACK_SESSION_OPENING = "TEAMMATES: Feedback session now open";
    public static final String SUBJECT_PREFIX_FEEDBACK_SESSION_REMINDER = "TEAMMATES: Feedback session reminder";
    public static final String SUBJECT_PREFIX_FEEDBACK_SESSION_CLOSING = "TEAMMATES: Feedback session closing soon";
    public static final String SUBJECT_PREFIX_FEEDBACK_SESSION_PUBLISHED = "TEAMMATES: Feedback session results published";
    
    public static final String SUBJECT_PREFIX_PENDING_COMMENTS_CLEARED = "TEAMMATES: You have new comments";
    
    public static final String SUBJECT_PREFIX_NEW_INSTRUCTOR_ACCOUNT = "TEAMMATES: Welcome to TEAMMATES!";
    
    public static final String SUBJECT_PREFIX_STUDENT_COURSE_JOIN = "TEAMMATES: Invitation to join course";
    public static final String SUBJECT_PREFIX_STUDENT_COURSE_REJOIN_AFTER_GOOGLE_ID_RESET =
            "TEAMMATES: Your account has been reset for course";
    public static final String SUBJECT_PREFIX_INSTRUCTOR_COURSE_JOIN = "TEAMMATES: Invitation to join course as an instructor";
    
    private static final String SUBJECT_PREFIX_ADMIN_SYSTEM_ERROR = "TEAMMATES (%s): New System Exception: %s";
    
    private static final String SUBJECT_PREFIX_SEVERE_LOGS_COMPILATION = "TEAMMATES (%s): Severe Error Logs Compilation";
    
    private static final Logger log = Utils.getLogger();
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
    
    public List<MimeMessage> generateFeedbackSessionOpeningEmails(FeedbackSessionAttributes session)
            throws MessagingException, IOException {
        
        String template = EmailTemplates.USER_FEEDBACK_SESSION;
        
        CourseAttributes course = coursesLogic.getCourse(session.getCourseId());
        List<InstructorAttributes> instructors = instructorsLogic.getInstructorsForCourse(session.getCourseId());
        List<StudentAttributes> students = fsLogic.isFeedbackSessionForStudentsToAnswer(session)
                                           ? studentsLogic.getStudentsForCourse(session.getCourseId())
                                           : new ArrayList<StudentAttributes>();
        
        List<MimeMessage> emails = generateFeedbackSessionEmailBases(course, session, students, instructors, template);
        for (MimeMessage email : emails) {
            email.setSubject(email.getSubject().replace("${subjectPrefix}", SUBJECT_PREFIX_FEEDBACK_SESSION_OPENING));
            email.setContent(email.getContent().toString().replace("${status}", "is now open"), "text/html");
        }
        return emails;
    }
    
    public List<MimeMessage> generateFeedbackSessionReminderEmails(
            CourseAttributes course, FeedbackSessionAttributes session, List<StudentAttributes> students,
            List<InstructorAttributes> instructorsToRemind, List<InstructorAttributes> instructorsToNotify)
                    throws MessagingException, IOException {
        
        String template = EmailTemplates.USER_FEEDBACK_SESSION;
        List<MimeMessage> emails =
                generateFeedbackSessionEmailBasesForInstructorReminders(course, session, instructorsToRemind, template);
        emails.addAll(generateFeedbackSessionEmailBases(course, session, students, instructorsToNotify, template));
        
        for (MimeMessage email : emails) {
            email.setSubject(email.getSubject().replace("${subjectPrefix}", SUBJECT_PREFIX_FEEDBACK_SESSION_REMINDER));
            email.setContent(email.getContent().toString().replace("${status}", "is still open for submissions"),
                             "text/html");
        }
        return emails;
    }
    
    private List<MimeMessage> generateFeedbackSessionEmailBasesForInstructorReminders(
            CourseAttributes course, FeedbackSessionAttributes session, List<InstructorAttributes> instructors,
            String template)
                    throws MessagingException, IOException {
        
        List<MimeMessage> emails = new ArrayList<MimeMessage>();
        for (InstructorAttributes instructor : instructors) {
            emails.add(generateFeedbackSessionEmailBaseForInstructorReminders(course, session, instructor, template));
        }
        return emails;
    }
    
    private MimeMessage generateFeedbackSessionEmailBaseForInstructorReminders(
            CourseAttributes course, FeedbackSessionAttributes session, InstructorAttributes instructor,
            String template)
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
        email.setSubject(String.format("${subjectPrefix} [Course: %s][Feedback Session: %s]",
                                       course.getName(), session.getFeedbackSessionName()));
        email.setContent(emailBody, "text/html");
        return email;
    }
    
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
        
        List<MimeMessage> emails = generateFeedbackSessionEmailBases(course, session, students, instructors, template);
        for (MimeMessage email : emails) {
            email.setSubject(email.getSubject().replace("${subjectPrefix}", SUBJECT_PREFIX_FEEDBACK_SESSION_CLOSING));
            email.setContent(email.getContent().toString().replace("${status}", "is closing soon"), "text/html");
        }
        return emails;
    }
    
    public List<MimeMessage> generateFeedbackSessionPublishedEmails(FeedbackSessionAttributes session)
            throws MessagingException, IOException {
        
        String template = EmailTemplates.USER_FEEDBACK_SESSION_PUBLISHED;
        
        CourseAttributes course = coursesLogic.getCourse(session.getCourseId());
        List<InstructorAttributes> instructors = instructorsLogic.getInstructorsForCourse(session.getCourseId());
        List<StudentAttributes> students = fsLogic.isFeedbackSessionViewableToStudents(session)
                                           ? studentsLogic.getStudentsForCourse(session.getCourseId())
                                           : new ArrayList<StudentAttributes>();
        
        List<MimeMessage> emails = generateFeedbackSessionEmailBases(course, session, students, instructors, template);
        for (MimeMessage email : emails) {
            email.setSubject(email.getSubject().replace("${subjectPrefix}", SUBJECT_PREFIX_FEEDBACK_SESSION_PUBLISHED));
        }
        return emails;
    }
    
    private List<MimeMessage> generateFeedbackSessionEmailBases(
            CourseAttributes course, FeedbackSessionAttributes session, List<StudentAttributes> students,
            List<InstructorAttributes> instructors, String template)
                    throws MessagingException, IOException {
        
        List<MimeMessage> emails = new ArrayList<MimeMessage>();
        for (StudentAttributes student : students) {
            emails.add(generateFeedbackSessionEmailBaseForStudents(course, session, student, template));
        }
        for (InstructorAttributes instructor : instructors) {
            emails.add(generateFeedbackSessionEmailBaseForInstructors(course, session, instructor, template));
        }
        return emails;
    }
    
    public MimeMessage generateFeedbackSessionEmailBaseForStudents(
            CourseAttributes course, FeedbackSessionAttributes session, StudentAttributes student, String template)
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
        email.setSubject(String.format("${subjectPrefix} [Course: %s][Feedback Session: %s]",
                                       course.getName(), session.getFeedbackSessionName()));
        email.setContent(emailBody, "text/html");
        return email;
    }
    
    public MimeMessage generateFeedbackSessionEmailBaseForInstructors(
            CourseAttributes course, FeedbackSessionAttributes session, InstructorAttributes instructor,
            String template)
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
        email.setSubject(String.format("${subjectPrefix} [Course: %s][Feedback Session: %s]",
                                       course.getName(), session.getFeedbackSessionName()));
        email.setContent(emailBody, "text/html");
        return email;
    }
    
    public List<MimeMessage> generatePendingCommentsClearedEmails(String courseId, Set<String> recipients)
            throws MessagingException, IOException {
        
        CourseAttributes course = coursesLogic.getCourse(courseId);
        List<StudentAttributes> students = studentsLogic.getStudentsForCourse(courseId);
        Map<String, StudentAttributes> emailStudentTable = new HashMap<String, StudentAttributes>();
        for (StudentAttributes student : students) {
            emailStudentTable.put(student.email, student);
        }
        
        String template = EmailTemplates.USER_PENDING_COMMENTS_CLEARED;
        
        List<MimeMessage> emails = new ArrayList<MimeMessage>();
        for (String recipientEmail : recipients) {
            StudentAttributes student = emailStudentTable.get(recipientEmail);
            if (student == null) {
                continue;
            }
            MimeMessage email = generatePendingCommentsClearedEmailBaseForStudent(course, student, template);
            email.setSubject(email.getSubject().replace("${subjectPrefix}", SUBJECT_PREFIX_PENDING_COMMENTS_CLEARED));
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
        email.setSubject(String.format("${subjectPrefix} [Course: %s]", course.getId()));
        email.setContent(emailBody, "text/html");
        return email;
    }
    
    private boolean isYetToJoinCourse(StudentAttributes student) {
        return student.googleId == null || student.googleId.isEmpty();
    }
    
    public MimeMessage generateNewInstructorAccountJoinEmail(InstructorAttributes instructor,
                                                             String shortName, String institute)
            throws AddressException, MessagingException, IOException {
        
        String joinUrl = generateNewInstructorAccountJoinLink(instructor, institute);
        
        String emailBody = Templates.populateTemplate(EmailTemplates.NEW_INSTRUCTOR_ACCOUNT_WELCOME,
                "${userName}", shortName,
                "${joinUrl}", joinUrl);
        
        MimeMessage email = getEmptyEmailAddressedToEmail(instructor.email);
        email = addBccRecipientToEmail(email, Config.SUPPORT_EMAIL);
        email.setSubject(String.format(SUBJECT_PREFIX_NEW_INSTRUCTOR_ACCOUNT + " " + shortName));
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
    
    public MimeMessage generateStudentCourseJoinEmail(CourseAttributes course, StudentAttributes student)
            throws AddressException, MessagingException, IOException {
        
        String emailBody = Templates.populateTemplate(
                fillUpStudentJoinFragment(student, EmailTemplates.USER_COURSE_JOIN),
                "${userName}", student.name,
                "${courseName}", course.getName());
        
        MimeMessage email = getEmptyEmailAddressedToEmail(student.email);
        email.setSubject(String.format(SUBJECT_PREFIX_STUDENT_COURSE_JOIN + " [%s][Course ID: %s]",
                                       course.getName(), course.getId()));
        email.setContent(emailBody, "text/html");
        return email;
    }
    
    public MimeMessage generateStudentCourseRejoinEmailAfterGoogleIdReset(
            CourseAttributes course, StudentAttributes student)
                    throws AddressException, MessagingException, IOException {
        
        String emailBody = Templates.populateTemplate(
                fillUpStudentRejoinAfterGoogleIdResetFragment(student, EmailTemplates.USER_COURSE_JOIN),
                "${userName}", student.name,
                "${courseName}", course.getName());
        
        MimeMessage email = getEmptyEmailAddressedToEmail(student.email);
        email.setSubject(String.format(SUBJECT_PREFIX_STUDENT_COURSE_REJOIN_AFTER_GOOGLE_ID_RESET
                                               + " [%s][Course ID: %s]",
                                       course.getName(), course.getId()));
        email.setContent(emailBody, "text/html");
        return email;
    }
    
    public MimeMessage generateInstructorCourseJoinEmail(CourseAttributes course, InstructorAttributes instructor)
            throws AddressException, MessagingException, IOException {
        
        String emailBody = Templates.populateTemplate(
                fillUpInstructorJoinFragment(instructor, EmailTemplates.USER_COURSE_JOIN),
                "${userName}", instructor.name,
                "${courseName}", course.getName());
        
        MimeMessage email = getEmptyEmailAddressedToEmail(instructor.email);
        email.setSubject(String.format(SUBJECT_PREFIX_INSTRUCTOR_COURSE_JOIN + " [%s][Course ID: %s]",
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
        email.setSubject(String.format(SUBJECT_PREFIX_ADMIN_SYSTEM_ERROR, Config.getAppVersion(), errorMessage));
        email.setContent(emailBody, "text/html");
        return email;
    }
    
    public MimeMessage generateCompiledLogsEmail(String logs)
            throws AddressException, MessagingException, IOException {
        
        String emailBody = logs.replace("\n", "<br>");
        
        MimeMessage email = getEmptyEmailAddressedToEmail(Config.SUPPORT_EMAIL);
        email.setSubject(String.format(SUBJECT_PREFIX_SEVERE_LOGS_COMPILATION, Config.getAppVersion()));
        email.setContent(emailBody, "text/html");
        return email;
    }
    
    public MimeMessage generateAdminEmail(String content, String subject, String sendTo)
            throws MessagingException, IOException {
        MimeMessage email = getEmptyEmailAddressedToEmail(sendTo);
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
