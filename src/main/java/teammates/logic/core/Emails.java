package teammates.logic.core;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.jsoup.Jsoup;

import com.google.appengine.labs.repackaged.org.json.JSONException;

import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.TeammatesException;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.Const.SystemParams;
import teammates.common.util.EmailLogEntry;
import teammates.common.util.StringHelper;
import teammates.common.util.EmailTemplates;
import teammates.common.util.TimeHelper;
import teammates.common.util.Url;
import teammates.common.util.Utils;
import teammates.googleSendgridJava.Sendgrid;

/**
 * Handles operations related to sending e-mails.
 */
public class Emails {
    //TODO: methods in this class throw too many exceptions. Reduce using a wrapper exception?
    private static Logger log = Utils.getLogger();

    public static final String SUBJECT_PREFIX_FEEDBACK_SESSION_OPENING = "TEAMMATES: Feedback session now open";
    public static final String SUBJECT_PREFIX_FEEDBACK_SESSION_REMINDER = "TEAMMATES: Feedback session reminder";
    public static final String SUBJECT_PREFIX_FEEDBACK_SESSION_CLOSING = "TEAMMATES: Feedback session closing soon";
    public static final String SUBJECT_PREFIX_FEEDBACK_SESSION_PUBLISHED = "TEAMMATES: Feedback session results published";
    public static final String SUBJECT_PREFIX_PENDING_COMMENTS_CLEARED = "TEAMMATES: You have new comments";
    public static final String SUBJECT_PREFIX_STUDENT_COURSE_JOIN = "TEAMMATES: Invitation to join course";
    public static final String SUBJECT_PREFIX_STUDENT_COURSE_REJOIN_AFTER_GOOGLE_ID_RESET = "TEAMMATES: Your account has been reset for course";
    public static final String SUBJECT_PREFIX_INSTRUCTOR_COURSE_JOIN = "TEAMMATES: Invitation to join course as an instructor";
    public static final String SUBJECT_PREFIX_ADMIN_SYSTEM_ERROR = "TEAMMATES (%s): New System Exception: %s";
    public static final String SUBJECT_PREFIX_NEW_INSTRUCTOR_ACCOUNT = "TEAMMATES: Welcome to TEAMMATES!";
            
    public static enum EmailType {
        FEEDBACK_CLOSING,
        FEEDBACK_OPENING,
        FEEDBACK_PUBLISHED,
        PENDING_COMMENT_CLEARED
    };
    
    private String senderEmail;
    private String senderName;
    private String replyTo;

    public Emails() {
        senderEmail = "Admin@" + Config.inst().getAppId() + ".appspotmail.com";
        senderName = "TEAMMATES Admin";
        replyTo = "teammates@comp.nus.edu.sg";
    }

    public static String getEmailInfo(MimeMessage message)
            throws MessagingException {
        StringBuilder messageInfo = new StringBuilder();
        messageInfo.append("[Email sent]");
        messageInfo
                .append("to="
                        + message.getRecipients(Message.RecipientType.TO)[0]
                                .toString());
        messageInfo.append("|from=" + message.getFrom()[0].toString());
        messageInfo.append("|subject=" + message.getSubject());
        return messageInfo.toString();
    }
    
    public static String getEmailInfo(Sendgrid message) {
        StringBuilder messageInfo = new StringBuilder();
        messageInfo.append("[Email sent]");
        messageInfo.append("to=" + message.getTos().get(0));
        messageInfo.append("|from=" + message.getFrom());
        messageInfo.append("|subject=" + message.getSubject());
        return messageInfo.toString();
    }
    
    public void addFeedbackSessionReminderToEmailsQueue(FeedbackSessionAttributes feedback,
            EmailType typeOfEmail) {
        
        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put(ParamsNames.EMAIL_FEEDBACK, feedback.feedbackSessionName);
        paramMap.put(ParamsNames.EMAIL_COURSE, feedback.courseId);
        paramMap.put(ParamsNames.EMAIL_TYPE, typeOfEmail.toString());
        
        TaskQueuesLogic taskQueueLogic = TaskQueuesLogic.inst();
        taskQueueLogic.createAndAddTask(SystemParams.EMAIL_TASK_QUEUE,
                Const.ActionURIs.EMAIL_WORKER, paramMap);
    }
    
    public void addCommentReminderToEmailsQueue(String courseId, EmailType typeOfEmail) {
        
        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put(ParamsNames.EMAIL_COURSE, courseId);
        paramMap.put(ParamsNames.EMAIL_TYPE, typeOfEmail.toString());
        
        TaskQueuesLogic taskQueueLogic = TaskQueuesLogic.inst();
        taskQueueLogic.createAndAddTask(SystemParams.EMAIL_TASK_QUEUE,
                Const.ActionURIs.EMAIL_WORKER, paramMap);
    }
    
    public List<MimeMessage> generateFeedbackSessionOpeningEmails(FeedbackSessionAttributes session) 
                    throws EntityDoesNotExistException, MessagingException, IOException {
        
        String template = EmailTemplates.USER_FEEDBACK_SESSION;
        StudentsLogic studentsLogic = StudentsLogic.inst();
        CoursesLogic coursesLogic = CoursesLogic.inst();
        InstructorsLogic instructorsLogic = InstructorsLogic.inst();
        FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
        
        CourseAttributes course = coursesLogic
                .getCourse(session.courseId);
        List<InstructorAttributes> instructors = instructorsLogic
                .getInstructorsForCourse(session.courseId);
        List<StudentAttributes> students;
        
        if (fsLogic.isFeedbackSessionForStudentsToAnswer(session)) {
            students = studentsLogic.getStudentsForCourse(session.courseId);
        } else {
            students = new ArrayList<StudentAttributes>();
        }
        
        List<MimeMessage> emails = generateFeedbackSessionEmailBases(course,
                session, students, instructors, template);
        
        for (MimeMessage email : emails) {
            email.setSubject(email.getSubject().replace("${subjectPrefix}",
                    SUBJECT_PREFIX_FEEDBACK_SESSION_OPENING));
            email.setContent(
                    email.getContent().toString()
                            .replace("${status}", "is now open"), "text/html");
        }
        
        return emails;
    }
    
    public List<MimeMessage> generateFeedbackSessionReminderEmails(
            CourseAttributes course, 
            FeedbackSessionAttributes session,
            List<StudentAttributes> students,
            List<InstructorAttributes> instructorsToRemind,
            List<InstructorAttributes> instructorsToNotify) 
                    throws MessagingException, IOException {

        String template = EmailTemplates.USER_FEEDBACK_SESSION;
        List<MimeMessage> emails = generateFeedbackSessionEmailBasesForInstructorReminders(
                course, session, instructorsToRemind, template);
        emails.addAll(generateFeedbackSessionEmailBases(course,
                session, students, instructorsToNotify, template));
        
        for (MimeMessage email : emails) {
            email.setSubject(email.getSubject().replace("${subjectPrefix}",
                    SUBJECT_PREFIX_FEEDBACK_SESSION_REMINDER));
            email.setContent(
                    email.getContent()
                            .toString()
                            .replace("${status}",
                                    "is still open for submissions"),
                    "text/html");
        }
        return emails;
    }
    
    public List<MimeMessage> generateFeedbackSessionClosingEmails(
            FeedbackSessionAttributes session)
                    throws MessagingException, IOException, EntityDoesNotExistException {
        
        StudentsLogic studentsLogic = StudentsLogic.inst();
        CoursesLogic coursesLogic = CoursesLogic.inst();
        InstructorsLogic instructorsLogic = InstructorsLogic.inst();
        FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
        String template = EmailTemplates.USER_FEEDBACK_SESSION_CLOSING;
        List<MimeMessage> emails = null;
        
        CourseAttributes course = coursesLogic
                .getCourse(session.courseId);
        List<InstructorAttributes> instructors = instructorsLogic
                .getInstructorsForCourse(session.courseId);
        List<StudentAttributes> students = new ArrayList<StudentAttributes>();

        if (fsLogic.isFeedbackSessionForStudentsToAnswer(session)) {
            List<StudentAttributes> allStudents = studentsLogic.
                    getStudentsForCourse(session.courseId);

            for (StudentAttributes student : allStudents) {
                if (!fsLogic.isFeedbackSessionFullyCompletedByStudent(
                        session.feedbackSessionName, session.courseId,
                        student.email)) {
                    students.add(student);
                }
            }
        }
        emails = generateFeedbackSessionEmailBases(
                course, session, students, instructors, template);
        for (MimeMessage email : emails) {
            email.setSubject(email.getSubject().replace("${subjectPrefix}",
                    SUBJECT_PREFIX_FEEDBACK_SESSION_CLOSING));
            email.setContent(
                    email.getContent().toString()
                            .replace("${status}", "is closing soon"),
                    "text/html");
        }
        return emails;
    }
    
    public List<MimeMessage> generatePendingCommentsClearedEmails(String courseId, Set<String> recipients) 
            throws EntityDoesNotExistException, MessagingException, UnsupportedEncodingException{
        CourseAttributes course = CoursesLogic.inst().getCourse(courseId);
        List<StudentAttributes> students = StudentsLogic.inst().getStudentsForCourse(courseId);
        Map<String, StudentAttributes> emailStudentTable = new HashMap<String, StudentAttributes>();
        for (StudentAttributes s : students) {
            emailStudentTable.put(s.email, s);
        }
        
        String template = EmailTemplates.USER_PENDING_COMMENTS_CLEARED;
        
        ArrayList<MimeMessage> emails = new ArrayList<MimeMessage>();
        for (String recipientEmail : recipients) {
            StudentAttributes s = emailStudentTable.get(recipientEmail);
            if(s == null) continue;
            emails.add(generatePendingCommentsClearedEmailBaseForStudent(course, s,
                    template));
        }
        for (MimeMessage email : emails) {
            email.setSubject(email.getSubject().replace("${subjectPrefix}",
                    SUBJECT_PREFIX_PENDING_COMMENTS_CLEARED));
        }
        return emails;
    }
    
    public MimeMessage generatePendingCommentsClearedEmailBaseForStudent(CourseAttributes course,
            StudentAttributes student, String template) 
                    throws MessagingException, UnsupportedEncodingException{
        MimeMessage message = getEmptyEmailAddressedToEmail(student.email);

        message.setSubject(String
                .format("${subjectPrefix} [Course: %s]",
                        course.id));

        String emailBody = template;

        if (isYetToJoinCourse(student)) {
            emailBody = fillUpStudentJoinFragment(student, emailBody);
        } else {
            emailBody = emailBody.replace("${joinFragment}", "");
        }
        
        emailBody = emailBody.replace("${userName}", student.name);
        emailBody = emailBody.replace("${courseName}", course.name);
        emailBody = emailBody.replace("${courseId}", course.id);
        
        String commentsPageUrl = Config.APP_URL
                + Const.ActionURIs.STUDENT_COMMENTS_PAGE;
        commentsPageUrl = Url.addParamToUrl(commentsPageUrl, Const.ParamsNames.COURSE_ID,
                course.id);
        emailBody = emailBody.replace("${commentsPageUrl}", commentsPageUrl);

        message.setContent(emailBody, "text/html");
        return message;
    }
    
    public List<MimeMessage> generateFeedbackSessionPublishedEmails(
            FeedbackSessionAttributes session)
                    throws MessagingException, IOException, EntityDoesNotExistException {
        
        StudentsLogic studentsLogic = StudentsLogic.inst();
        CoursesLogic coursesLogic = CoursesLogic.inst();
        InstructorsLogic instructorsLogic = InstructorsLogic.inst();
        FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
        String template = EmailTemplates.USER_FEEDBACK_SESSION_PUBLISHED;
        List<MimeMessage> emails = null;

        CourseAttributes course = coursesLogic
                .getCourse(session.courseId);
        List<StudentAttributes> students;
        List<InstructorAttributes> instructors = instructorsLogic
                .getInstructorsForCourse(session.courseId);
        
        if (fsLogic.isFeedbackSessionViewableToStudents(session)) {
            students = studentsLogic.getStudentsForCourse(session.courseId);
        } else {
            students = new ArrayList<StudentAttributes>();
        }
        emails = generateFeedbackSessionEmailBases(course,
                session, students, instructors, template);
        
        for (MimeMessage email : emails) {
            email.setSubject(email.getSubject().replace("${subjectPrefix}",
                    SUBJECT_PREFIX_FEEDBACK_SESSION_PUBLISHED));
        }
        return emails;
    }
    
    public List<MimeMessage> generateFeedbackSessionEmailBases(
            CourseAttributes course,
            FeedbackSessionAttributes session, 
            List<StudentAttributes> students,
            List<InstructorAttributes> instructors,
            String template) 
                    throws MessagingException, UnsupportedEncodingException {
        
        ArrayList<MimeMessage> emails = new ArrayList<MimeMessage>();
        for (StudentAttributes s : students) {
            emails.add(generateFeedbackSessionEmailBaseForStudents(course, session, s,
                    template));
        }
        for (InstructorAttributes i : instructors) {
            emails.add(generateFeedbackSessionEmailBaseForInstructors(course,
                    session, i, template));
        }
        return emails;
    }
    
    public List<MimeMessage> generateFeedbackSessionEmailBasesForInstructorReminders(
            CourseAttributes course,
            FeedbackSessionAttributes session, 
            List<InstructorAttributes> instructors,
            String template) 
                    throws MessagingException, UnsupportedEncodingException {
        
        ArrayList<MimeMessage> emails = new ArrayList<MimeMessage>();
        for (InstructorAttributes i : instructors) {
            emails.add(generateFeedbackSessionEmailBaseForInstructorReminders(course, session, i,
                    template));
        }
        return emails;
    }

    public MimeMessage generateFeedbackSessionEmailBaseForStudents(
            CourseAttributes c,
            FeedbackSessionAttributes fs, 
            StudentAttributes s,
            String template)
                    throws MessagingException, UnsupportedEncodingException {

        MimeMessage message = getEmptyEmailAddressedToEmail(s.email);

        message.setSubject(String
                .format("${subjectPrefix} [Course: %s][Feedback Session: %s]",
                        c.name, fs.feedbackSessionName));

        String emailBody = template;

        emailBody = emailBody.replace("${userName}", s.name);
        emailBody = emailBody.replace("${courseName}", c.name);
        emailBody = emailBody.replace("${courseId}", c.id);
        emailBody = emailBody.replace("${feedbackSessionName}", fs.feedbackSessionName);
        emailBody = emailBody.replace("${joinFragment}", "");
        emailBody = emailBody.replace("${deadline}",
                TimeHelper.formatTime(fs.endTime));
        emailBody = emailBody.replace("${instructorFragment}", "");
        
        String submitUrl = new Url(Config.APP_URL + Const.ActionURIs.STUDENT_FEEDBACK_SUBMISSION_EDIT_PAGE)
                            .withCourseId(c.id)
                            .withSessionName(fs.feedbackSessionName)
                            .withRegistrationKey(StringHelper.encrypt(s.key))
                            .withStudentEmail(s.email)
                            .toString();
        emailBody = emailBody.replace("${submitUrl}", submitUrl);

        String reportUrl = new Url(Config.APP_URL + Const.ActionURIs.STUDENT_FEEDBACK_RESULTS_PAGE)
                            .withCourseId(c.id)
                            .withSessionName(fs.feedbackSessionName)
                            .withRegistrationKey(StringHelper.encrypt(s.key))
                            .withStudentEmail(s.email)
                            .toString();
        emailBody = emailBody.replace("${reportUrl}", reportUrl);

        message.setContent(emailBody, "text/html");

        return message;
    }

    public MimeMessage generateFeedbackSessionEmailBaseForInstructors(
            CourseAttributes c,
            FeedbackSessionAttributes fs, 
            InstructorAttributes i,
            String template)
                    throws MessagingException, UnsupportedEncodingException {

        MimeMessage message = getEmptyEmailAddressedToEmail(i.email);

        message.setSubject(String
                .format("${subjectPrefix} [Course: %s][Feedback Session: %s]",
                        c.name, fs.feedbackSessionName));

        String emailBody = template;

        emailBody = emailBody.replace("${joinFragment}", "");
        emailBody = emailBody.replace("${userName}", i.name);
        emailBody = emailBody.replace("${courseName}", c.name);
        emailBody = emailBody.replace("${courseId}", c.id);
        emailBody = emailBody.replace("${feedbackSessionName}", fs.feedbackSessionName);
        emailBody = emailBody.replace("${deadline}",
                TimeHelper.formatTime(fs.endTime));
        emailBody = emailBody.replace("${instructorFragment}", "The email below has been sent to students of course: "+c.id+".<p/><br/>");
        
        String submitUrl = "{The student's unique submission url appears here}";
        emailBody = emailBody.replace("${submitUrl}", submitUrl);

        String reportUrl = "{The student's unique results url appears here}";
        emailBody = emailBody.replace("${reportUrl}", reportUrl);

        message.setContent(emailBody, "text/html");

        return message;
    }
    
    public MimeMessage generateFeedbackSessionEmailBaseForInstructorReminders(
            CourseAttributes c,
            FeedbackSessionAttributes fs, 
            InstructorAttributes i,
            String template)
                    throws MessagingException, UnsupportedEncodingException {

        MimeMessage message = getEmptyEmailAddressedToEmail(i.email);

        message.setSubject(String
                .format("${subjectPrefix} [Course: %s][Feedback Session: %s]",
                        c.name, fs.feedbackSessionName));

        String emailBody = template;

        emailBody = emailBody.replace("${joinFragment}", "");
        emailBody = emailBody.replace("${userName}", i.name);
        emailBody = emailBody.replace("${courseName}", c.name);
        emailBody = emailBody.replace("${courseId}", c.id);
        emailBody = emailBody.replace("${feedbackSessionName}", fs.feedbackSessionName);
        emailBody = emailBody.replace("${deadline}",
                TimeHelper.formatTime(fs.endTime));
        emailBody = emailBody.replace("${instructorFragment}", "");
        
        String submitUrl = Config.APP_URL
                + Const.ActionURIs.INSTRUCTOR_FEEDBACK_SUBMISSION_EDIT_PAGE;
        submitUrl = Url.addParamToUrl(submitUrl, Const.ParamsNames.COURSE_ID,
                c.id);
        submitUrl = Url.addParamToUrl(submitUrl,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName);
        emailBody = emailBody.replace("${submitUrl}", submitUrl);

        String reportUrl = Config.APP_URL
                + Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_PAGE;
        reportUrl = Url.addParamToUrl(reportUrl, Const.ParamsNames.COURSE_ID,
                c.id);
        reportUrl = Url.addParamToUrl(reportUrl,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName);
        emailBody = emailBody.replace("${reportUrl}", reportUrl);

        message.setContent(emailBody, "text/html");

        return message;
    }
    
    public MimeMessage generateStudentCourseJoinEmail(
            CourseAttributes course, StudentAttributes student) 
                    throws AddressException, MessagingException, UnsupportedEncodingException {

        MimeMessage message = getEmptyEmailAddressedToEmail(student.email);
        message.setSubject(String.format(SUBJECT_PREFIX_STUDENT_COURSE_JOIN
                + " [%s][Course ID: %s]", course.name, course.id));

        String emailBody = EmailTemplates.USER_COURSE_JOIN;
        emailBody = fillUpStudentJoinFragment(student, emailBody);
        emailBody = emailBody.replace("${userName}", student.name);
        emailBody = emailBody.replace("${courseName}", course.name);

        message.setContent(emailBody, "text/html");
        return message;
    }
    
    public MimeMessage generateAdminEmail(String content, String subject, String sendTo) throws MessagingException, UnsupportedEncodingException 
    {

        MimeMessage message = getEmptyEmailAddressedToEmail(sendTo);
        message.setSubject(subject);

        message.setContent(content, "text/html");
        return message;
    }
    

    public MimeMessage generateStudentCourseRejoinEmailAfterGoogleIdReset(
            CourseAttributes course, StudentAttributes student) 
                    throws AddressException, MessagingException, UnsupportedEncodingException {

        MimeMessage message = getEmptyEmailAddressedToEmail(student.email);
        message.setSubject(String.format(SUBJECT_PREFIX_STUDENT_COURSE_REJOIN_AFTER_GOOGLE_ID_RESET
                + " [%s][Course ID: %s]", course.name, course.id));

        String emailBody = EmailTemplates.USER_COURSE_JOIN;
        emailBody = fillUpStudentRejoinAfterGoogleIdResetFragment(student, emailBody);
        emailBody = emailBody.replace("${userName}", student.name);
        emailBody = emailBody.replace("${courseName}", course.name);

        message.setContent(emailBody, "text/html");
        return message;
    }
    
    public MimeMessage generateNewInstructorAccountJoinEmail(InstructorAttributes instructor,String shortName, String institute) 
                             throws AddressException,MessagingException,UnsupportedEncodingException {

        MimeMessage messageToUser = getEmptyEmailAddressedToEmail(instructor.email);
        messageToUser = addBccRecipientToEmail(messageToUser, Config.SUPPORT_EMAIL);
        
        messageToUser.setSubject(String.format(SUBJECT_PREFIX_NEW_INSTRUCTOR_ACCOUNT + " " + shortName));      
        String joinUrl = generateNewInstructorAccountJoinLink(instructor, institute);
        
        String emailBody = EmailTemplates.NEW_INSTRCUTOR_ACCOUNT_WELCOME;
        emailBody = emailBody.replace("${userName}", shortName);
        emailBody = emailBody.replace("${joinUrl}",joinUrl);
        messageToUser.setContent(emailBody, "text/html");

        return messageToUser;

    }
    
    
    @Deprecated
    /**
     * Generate the join link to be sent to the account requester's email
     * This method should only be used in adminHomePage for easy manual testing purpose
     */
    public String generateNewInstructorAccountJoinLink(InstructorAttributes instructor, String institute){
        
        String joinUrl = "";
        if (instructor != null) {
            String key = StringHelper.encrypt(instructor.key);
            joinUrl = Config.APP_URL + Const.ActionURIs.INSTRUCTOR_COURSE_JOIN;
            joinUrl = Url.addParamToUrl(joinUrl, Const.ParamsNames.REGKEY, key);
            joinUrl = Url.addParamToUrl(joinUrl, Const.ParamsNames.INSTRUCTOR_INSTITUTION, institute);
        }
        
        return joinUrl;
    }
    
    
    public MimeMessage generateInstructorCourseJoinEmail(
            CourseAttributes course, InstructorAttributes instructor) 
                    throws AddressException, MessagingException, UnsupportedEncodingException {
        
        MimeMessage message = getEmptyEmailAddressedToEmail(instructor.email);    
        message.setSubject(String.format(SUBJECT_PREFIX_INSTRUCTOR_COURSE_JOIN
                + " [%s][Course ID: %s]", course.name, course.id));

        String emailBody = EmailTemplates.USER_COURSE_JOIN;
        emailBody = fillUpInstructorJoinFragment(instructor, emailBody);
        emailBody = emailBody.replace("${userName}", instructor.name);
        emailBody = emailBody.replace("${courseName}", course.name);

        message.setContent(emailBody, "text/html");  
        return message;
    }
    
    public MimeMessage generateSystemErrorEmail(
            Throwable error,
            String requestPath, 
            String requestParam, 
            String version)
            throws AddressException, MessagingException, UnsupportedEncodingException {
        
        //TODO: remove version parameter?
        
        Session session = Session.getDefaultInstance(new Properties(), null);
        MimeMessage message = new MimeMessage(session);
        String errorMessage = error.getMessage();
        String stackTrace = TeammatesException.toStringWithStackTrace(error);
    
        // if the error doesn't contain a short description,
        // retrieve the first line of stack trace.
        // truncate stack trace at first "at" string
        if (errorMessage == null) {
            int msgTruncateIndex = stackTrace.indexOf("at");
            if (msgTruncateIndex > 0) {
                errorMessage = stackTrace.substring(0, msgTruncateIndex);
            } else {
                errorMessage = "";
            }
        }
        String recipient = Config.SUPPORT_EMAIL;
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(
                recipient));
        message.setFrom(new InternetAddress(senderEmail, senderName));
        message.setSubject(String.format(SUBJECT_PREFIX_ADMIN_SYSTEM_ERROR,
                version, errorMessage));
    
        String emailBody = EmailTemplates.SYSTEM_ERROR;
    
        emailBody = emailBody.replace("${requestPath}", requestPath);
        emailBody = emailBody.replace("${requestParameters}", requestParam);
        emailBody = emailBody.replace("${errorMessage}", errorMessage);
        emailBody = emailBody.replace("${stackTrace}", stackTrace);
        message.setContent(emailBody, "text/html");
    
        return message;
    }

    public MimeMessage generateCompiledLogsEmail(String logs)
            throws AddressException, MessagingException, UnsupportedEncodingException {
        
        MimeMessage message = getEmptyEmailAddressedToEmail(Config.SUPPORT_EMAIL);
        message.setSubject("Severe Error Logs Compilation");

        String emailBody = logs;

        message.setContent(emailBody, "text/html");
        return message;
    }
    
    public void sendEmails(List<MimeMessage> messages) {
        if (messages.isEmpty()) {
            return;
        }
        
        // Equally spread out the emails to be sent over 1 hour
        int numberOfEmailsSent = 0;
        int emailIntervalMillis = (1000 * 60 * 60) / messages.size();

        // Sets interval to a maximum of 5 seconds if the interval is too large
        int maxIntervalMillis = 5000;
        emailIntervalMillis = emailIntervalMillis > maxIntervalMillis ? maxIntervalMillis : emailIntervalMillis;

        for (MimeMessage m : messages) {
            try {
                long emailDelayTimer = numberOfEmailsSent * emailIntervalMillis;
                addEmailToTaskQueue(m, emailDelayTimer);
                numberOfEmailsSent++;
            } catch (MessagingException e) {
                log.severe("Error in sending : " + m.toString()
                        + " Cause : " + e.getMessage());
            }
        }

    }

    public void addEmailToTaskQueue(MimeMessage message, long emailDelayTimer) throws MessagingException {
        try {
            HashMap<String, String> paramMap = new HashMap<String, String>();
            paramMap.put(ParamsNames.EMAIL_SUBJECT, message.getSubject());
            paramMap.put(ParamsNames.EMAIL_CONTENT, message.getContent().toString());
            paramMap.put(ParamsNames.EMAIL_SENDER, message.getFrom()[0].toString());
            paramMap.put(ParamsNames.EMAIL_RECEIVER, message.getRecipients(Message.RecipientType.TO)[0].toString());
            paramMap.put(ParamsNames.EMAIL_REPLY_TO_ADDRESS, message.getReplyTo()[0].toString());
            
            TaskQueuesLogic taskQueueLogic = TaskQueuesLogic.inst();
            taskQueueLogic.createAndAddDeferredTask(SystemParams.SEND_EMAIL_TASK_QUEUE,
                    Const.ActionURIs.SEND_EMAIL_WORKER, paramMap, emailDelayTimer);
        } catch (Exception e) {
            log.severe("Error when adding email to task queue: " + e.getMessage());
        } 
        
    }
    
    public void sendEmailWithLogging(MimeMessage message) throws MessagingException, JSONException, IOException {
        sendEmail(message, true);
    }
    
    public void sendEmailWithoutLogging(MimeMessage message) throws MessagingException, JSONException, IOException {
        sendEmail(message, false);
    }
    
    /**
     * Sends email through GAE irrespective of config properties
     * Does not generate log report
     * @param message
     * @throws MessagingException
     */
    public void forceSendEmailThroughGaeWithoutLogging(MimeMessage message) throws MessagingException {
        sendUsingGae(message);
    }
    
    /**
     * Sends email through GAE irrespective of config properties
     * Generates log report
     * @param message
     * @throws MessagingException
     */
    public void forceSendEmailThroughGaeWithLogging(MimeMessage message) throws MessagingException {
        sendUsingGae(message);
        generateLogReport(message);
    }

    /**
     * This method sends the email and has an option to log its receiver, subject and content 
     * @param message
     * @param isWithLogging
     * @throws MessagingException
     * @throws IOException 
     * @throws JSONException 
     */
    private void sendEmail(MimeMessage message, boolean isWithLogging) throws MessagingException, JSONException, IOException {
        if (Config.isUsingSendgrid()) {
            sendUsingSendgrid(message);
            
            if (isWithLogging) {
                generateLogReport(parseMimeMessageToSendgrid(message));
            }           
        } else {
            sendUsingGae(message);
            
            if (isWithLogging) {
                generateLogReport(message);
            }
        }          
    }
    
    private void sendUsingGae(MimeMessage message) throws MessagingException {
        log.info(getEmailInfo(message));
        Transport.send(message);
    }

    private void sendUsingSendgrid(MimeMessage message) throws MessagingException, JSONException, IOException {
        Sendgrid email = parseMimeMessageToSendgrid(message);
        log.info(getEmailInfo(email));
        
        try {               
            email.send();
        } catch (Exception e) {
            log.severe("Sendgrid failed, sending with GAE mail");
            Transport.send(message);  
        }
    }
    
    private void generateLogReport(Sendgrid message) {
        try {
            EmailLogEntry newEntry = new EmailLogEntry(message);
            String emailLogInfo = newEntry.generateLogMessage();
            log.log(Level.INFO, emailLogInfo);
        } catch (Exception e) {
            log.severe("Failed to generate log for email: " + getEmailInfo(message));
            e.printStackTrace();
        }
    }
    
    private void generateLogReport(MimeMessage message) throws MessagingException {
        try {
            EmailLogEntry newEntry = new EmailLogEntry(message);
            String emailLogInfo = newEntry.generateLogMessage();
            log.log(Level.INFO, emailLogInfo);
        } catch (Exception e) {
            log.severe("Failed to generate log for email: " + getEmailInfo(message));
            e.printStackTrace();
        }
    }
    
    public MimeMessage sendErrorReport(String path, String params, Throwable error) {
        MimeMessage email = null;
        try {
            email = generateSystemErrorEmail(error, path, params,
                    Config.inst().getAppVersion());
            forceSendEmailThroughGaeWithoutLogging(email);
            log.severe("Sent crash report: " + Emails.getEmailInfo(email));
        } catch (Exception e) {
            log.severe("Error in sending crash report: "
                    + (email == null ? "" : email.toString()));
        }
    
        return email;
    }

    public MimeMessage sendLogReport(MimeMessage message) {
        MimeMessage email = null;
        try {
            forceSendEmailThroughGaeWithoutLogging(message);
        } catch (Exception e) {
            log.severe("Error in sending log report: "
                    + (email == null ? "" : email.toString()));
        }
    
        return email;
    }
    
    private String fillUpStudentJoinFragment(StudentAttributes s, String emailBody) {
        emailBody = emailBody.replace("${joinFragment}",
                EmailTemplates.FRAGMENT_STUDENT_COURSE_JOIN);

        String joinUrl;
        if (s != null) {    
            joinUrl = s.getRegistrationUrl();
        } else {
            joinUrl = "{The join link unique for each student appears here}";
        }

        emailBody = emailBody.replace("${joinUrl}", joinUrl);
        return emailBody;
    }
    
    
    private String fillUpStudentRejoinAfterGoogleIdResetFragment(StudentAttributes s, String emailBody) {
        emailBody = emailBody.replace("${joinFragment}",
                EmailTemplates.FRAGMENT_STUDENT_COURSE_REJOIN_AFTER_GOOGLE_ID_RESET);

        String joinUrl;
        if (s != null) {    
            joinUrl = s.getRegistrationUrl();
        } else {
            joinUrl = "{The join link unique for each student appears here}";
        }

        emailBody = emailBody.replace("${joinUrl}", joinUrl);
        return emailBody;
    }
    
    
    private String fillUpInstructorJoinFragment(InstructorAttributes instructor, String emailBody) {
        emailBody = emailBody.replace("${joinFragment}",
                EmailTemplates.FRAGMENT_INSTRUCTOR_COURSE_JOIN);

        String joinUrl = "";
        if (instructor != null) {
            String key;
            key = StringHelper.encrypt(instructor.key);
    
            joinUrl = Config.APP_URL + Const.ActionURIs.INSTRUCTOR_COURSE_JOIN;
            joinUrl = Url.addParamToUrl(joinUrl, Const.ParamsNames.REGKEY, key);
        }

        emailBody = emailBody.replace("${joinUrl}", joinUrl);
        return emailBody;
    }

    private MimeMessage getEmptyEmailAddressedToEmail(String email)
            throws MessagingException, AddressException,
            UnsupportedEncodingException {
        Session session = Session.getDefaultInstance(new Properties(), null);
        MimeMessage message = new MimeMessage(session);

        message.addRecipient(Message.RecipientType.TO, new InternetAddress(
                email));
        message.setFrom(new InternetAddress(senderEmail, senderName));
        message.setReplyTo(new Address[] { new InternetAddress(replyTo) });
        return message;
    }
    
    
    private MimeMessage addBccRecipientToEmail(MimeMessage mail, String newAddress) throws AddressException, MessagingException{
        
        mail.addRecipient(Message.RecipientType.BCC, new InternetAddress(newAddress));     
        return mail;
    }
    
    private boolean isYetToJoinCourse(StudentAttributes s) {
        return s.googleId == null || s.googleId.isEmpty();
    }

    /**
     * Generate email recipient list for the automated reminders sent.
     * Used for AdminActivityLog
     */
    public static ArrayList<Object> extractRecipientsList(ArrayList<MimeMessage> emails){
    
        ArrayList<Object> data = new ArrayList<Object>();
        
        try{
            for (int i = 0; i < emails.size(); i++){
                Address[] recipients = emails.get(i).getRecipients(Message.RecipientType.TO);
                for (int j = 0; j < recipients.length; j++){
                    data.add(recipients[j]);
                }
            }
        } catch (Exception e){
            throw new RuntimeException("Unexpected exception during generation of log messages for automated reminders",e);
        }
        
        return data;
    }
    
    public Sendgrid parseMimeMessageToSendgrid(MimeMessage message) throws MessagingException, JSONException, IOException {
        Sendgrid email = new Sendgrid(Config.SENDGRID_USERNAME, Config.SENDGRID_PASSWORD);
        
        for (int i = 0; i < message.getRecipients(Message.RecipientType.TO).length; i++) {
            email.addTo(message.getRecipients(Message.RecipientType.TO)[i].toString());
        }
        
        String from = extractSenderEmail(message.getFrom()[0].toString());
        String html = message.getContent().toString();
        
        email.setFrom(from)
             .setSubject(message.getSubject())
             .setHtml(html)
             .setText(Jsoup.parse(html).text());
        
        if (message.getRecipients(Message.RecipientType.BCC) != null 
                                        && message.getRecipients(Message.RecipientType.BCC).length > 0) {
            email.setBcc(message.getRecipients(Message.RecipientType.BCC)[0].toString());
        }
        
        if (message.getReplyTo() != null && message.getReplyTo().length > 0) {
            email.setReplyTo(message.getReplyTo()[0].toString());
        }
        
        return email;
    }

    /**
     * Extracts sender email from the string with name and email in the format: Name <Email>
     * @param from String with sender information in the format: Name <Email>
     * @return Sender email
     */
    public String extractSenderEmail(String from) {
        if (from.contains("<") && from.contains(">")) {
            from = from.substring(from.indexOf("<") + 1, from.indexOf(">"));
        }
        return from;
    }
}