package teammates.logic.core;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    
    Sendgrid mail = new Sendgrid(Const.SystemParams.SENDGRID_USERNAME, Const.SystemParams.SENDGRID_PASSWORD);

    public Emails() {
        senderEmail = "Admin@" + Config.inst().getAppId() + ".appspotmail.com";
        senderName = "TEAMMATES Admin";
        replyTo = "teammates@comp.nus.edu.sg";
    }

    public static String getEmailInfo(Sendgrid message) {
        StringBuilder messageInfo = new StringBuilder();
        messageInfo.append("[Email sent]");
        messageInfo.append("to=" + message.getTos().get(0));
        
        if (message.getFromName() == null) {
            messageInfo.append("|from=" + message.getFrom());
        } else {
            messageInfo.append("|from=" + message.getFromName() + " <" + message.getFrom() + ">");
        }
        
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
    
    public List<Sendgrid> generateFeedbackSessionOpeningEmails(FeedbackSessionAttributes session) 
                    throws EntityDoesNotExistException, IOException {
        
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
        
        List<Sendgrid> emails = generateFeedbackSessionEmailBases(course,
                session, students, instructors, template);
        
        for (Sendgrid email : emails) {
            email.setSubject(email.getSubject().replace("${subjectPrefix}",
                    SUBJECT_PREFIX_FEEDBACK_SESSION_OPENING));
            email.setHtml(email.getHtml().replace("${status}", "is now open"));
        }
        
        return emails;
    }
    
    public List<Sendgrid> generateFeedbackSessionReminderEmails(
            CourseAttributes course, 
            FeedbackSessionAttributes session,
            List<StudentAttributes> students,
            List<InstructorAttributes> instructorsToRemind,
            List<InstructorAttributes> instructorsToNotify) 
                    throws IOException {

        String template = EmailTemplates.USER_FEEDBACK_SESSION;
        List<Sendgrid> emails = generateFeedbackSessionEmailBasesForInstructorReminders(
                course, session, instructorsToRemind, template);
        emails.addAll(generateFeedbackSessionEmailBases(course,
                session, students, instructorsToNotify, template));
        
        for (Sendgrid email : emails) {
            email.setSubject(email.getSubject().replace("${subjectPrefix}",
                    SUBJECT_PREFIX_FEEDBACK_SESSION_REMINDER));
            email.setHtml(email.getHtml().replace("${status}",
                                    "is still open for submissions"));
        }
        return emails;
    }
    
    public List<Sendgrid> generateFeedbackSessionClosingEmails(
            FeedbackSessionAttributes session)
                    throws IOException, EntityDoesNotExistException {
        
        StudentsLogic studentsLogic = StudentsLogic.inst();
        CoursesLogic coursesLogic = CoursesLogic.inst();
        InstructorsLogic instructorsLogic = InstructorsLogic.inst();
        FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
        String template = EmailTemplates.USER_FEEDBACK_SESSION_CLOSING;
        List<Sendgrid> emails = null;
        
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
        for (Sendgrid email : emails) {
            email.setSubject(email.getSubject().replace("${subjectPrefix}",
                    SUBJECT_PREFIX_FEEDBACK_SESSION_CLOSING));
            email.setHtml(
                    email.getHtml()
                            .replace("${status}", "is closing soon"));
        }
        return emails;
    }
    
    public List<Sendgrid> generatePendingCommentsClearedEmails(String courseId, Set<String> recipients) 
            throws EntityDoesNotExistException, UnsupportedEncodingException{
        CourseAttributes course = CoursesLogic.inst().getCourse(courseId);
        List<StudentAttributes> students = StudentsLogic.inst().getStudentsForCourse(courseId);
        Map<String, StudentAttributes> emailStudentTable = new HashMap<String, StudentAttributes>();
        for (StudentAttributes s : students) {
            emailStudentTable.put(s.email, s);
        }
        
        String template = EmailTemplates.USER_PENDING_COMMENTS_CLEARED;
        
        ArrayList<Sendgrid> emails = new ArrayList<Sendgrid>();
        for (String recipientEmail : recipients) {
            StudentAttributes s = emailStudentTable.get(recipientEmail);
            if(s == null) continue;
            emails.add(generatePendingCommentsClearedEmailBaseForStudent(course, s,
                    template));
        }
        for (Sendgrid email : emails) {
            email.setSubject(email.getSubject().replace("${subjectPrefix}",
                    SUBJECT_PREFIX_PENDING_COMMENTS_CLEARED));
        }
        return emails;
    }
    
    public Sendgrid generatePendingCommentsClearedEmailBaseForStudent(CourseAttributes course,
            StudentAttributes student, String template) 
                    throws UnsupportedEncodingException{
        Sendgrid message = getEmptyEmailAddressedToEmail(student.email);

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

        message.setHtml(emailBody);
        return message;
    }
    
    public List<Sendgrid> generateFeedbackSessionPublishedEmails(
            FeedbackSessionAttributes session)
                    throws EntityDoesNotExistException, UnsupportedEncodingException {
        
        StudentsLogic studentsLogic = StudentsLogic.inst();
        CoursesLogic coursesLogic = CoursesLogic.inst();
        InstructorsLogic instructorsLogic = InstructorsLogic.inst();
        FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
        String template = EmailTemplates.USER_FEEDBACK_SESSION_PUBLISHED;
        List<Sendgrid> emails = null;

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
        
        for (Sendgrid email : emails) {
            email.setSubject(email.getSubject().replace("${subjectPrefix}",
                    SUBJECT_PREFIX_FEEDBACK_SESSION_PUBLISHED));
        }
        return emails;
    }
    
    public List<Sendgrid> generateFeedbackSessionEmailBases(
            CourseAttributes course,
            FeedbackSessionAttributes session, 
            List<StudentAttributes> students,
            List<InstructorAttributes> instructors,
            String template) 
                    throws UnsupportedEncodingException {
        
        ArrayList<Sendgrid> emails = new ArrayList<Sendgrid>();
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
    
    public List<Sendgrid> generateFeedbackSessionEmailBasesForInstructorReminders(
            CourseAttributes course,
            FeedbackSessionAttributes session, 
            List<InstructorAttributes> instructors,
            String template) 
                    throws UnsupportedEncodingException {
        
        ArrayList<Sendgrid> emails = new ArrayList<Sendgrid>();
        for (InstructorAttributes i : instructors) {
            emails.add(generateFeedbackSessionEmailBaseForInstructorReminders(course, session, i,
                    template));
        }
        return emails;
    }

    public Sendgrid generateFeedbackSessionEmailBaseForStudents(
            CourseAttributes c,
            FeedbackSessionAttributes fs, 
            StudentAttributes s,
            String template)
                    throws UnsupportedEncodingException {

        Sendgrid message = getEmptyEmailAddressedToEmail(s.email);

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

        message.setHtml(emailBody);

        return message;
    }

    public Sendgrid generateFeedbackSessionEmailBaseForInstructors(
            CourseAttributes c,
            FeedbackSessionAttributes fs, 
            InstructorAttributes i,
            String template)
                    throws UnsupportedEncodingException {

        Sendgrid message = getEmptyEmailAddressedToEmail(i.email);

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

        message.setHtml(emailBody);

        return message;
    }
    
    public Sendgrid generateFeedbackSessionEmailBaseForInstructorReminders(
            CourseAttributes c,
            FeedbackSessionAttributes fs, 
            InstructorAttributes i,
            String template)
                    throws UnsupportedEncodingException {

        Sendgrid message = getEmptyEmailAddressedToEmail(i.email);

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

        message.setHtml(emailBody);

        return message;
    }
    
    public Sendgrid generateStudentCourseJoinEmail(
            CourseAttributes course, StudentAttributes student) 
                    throws UnsupportedEncodingException {

        Sendgrid message = getEmptyEmailAddressedToEmail(student.email);
        message.setSubject(String.format(SUBJECT_PREFIX_STUDENT_COURSE_JOIN
                + " [%s][Course ID: %s]", course.name, course.id));

        String emailBody = EmailTemplates.USER_COURSE_JOIN;
        emailBody = fillUpStudentJoinFragment(student, emailBody);
        emailBody = emailBody.replace("${userName}", student.name);
        emailBody = emailBody.replace("${courseName}", course.name);

        message.setHtml(emailBody);
        return message;
    }
    
    public Sendgrid generateAdminEmail(String content, String subject, String sendTo) throws UnsupportedEncodingException 
    {

        Sendgrid message = getEmptyEmailAddressedToEmail(sendTo);
        message.setSubject(subject);

        message.setHtml(content);
        return message;
    }
    

    public Sendgrid generateStudentCourseRejoinEmailAfterGoogleIdReset(
            CourseAttributes course, StudentAttributes student) 
                    throws UnsupportedEncodingException {

        Sendgrid message = getEmptyEmailAddressedToEmail(student.email);
        message.setSubject(String.format(SUBJECT_PREFIX_STUDENT_COURSE_REJOIN_AFTER_GOOGLE_ID_RESET
                + " [%s][Course ID: %s]", course.name, course.id));

        String emailBody = EmailTemplates.USER_COURSE_JOIN;
        emailBody = fillUpStudentRejoinAfterGoogleIdResetFragment(student, emailBody);
        emailBody = emailBody.replace("${userName}", student.name);
        emailBody = emailBody.replace("${courseName}", course.name);

        message.setHtml(emailBody);
        return message;
    }
    
    public Sendgrid generateNewInstructorAccountJoinEmail(InstructorAttributes instructor,String shortName, String institute) 
                             throws UnsupportedEncodingException, JSONException {

        Sendgrid messageToUser = getEmptyEmailAddressedToEmail(instructor.email);
        messageToUser = setBccRecipientToEmail(messageToUser, Config.SUPPORT_EMAIL);
        
        messageToUser.setSubject(String.format(SUBJECT_PREFIX_NEW_INSTRUCTOR_ACCOUNT + " " + shortName));      
        String joinUrl = generateNewInstructorAccountJoinLink(instructor, institute);
        
        String emailBody = EmailTemplates.NEW_INSTRCUTOR_ACCOUNT_WELCOME;
        emailBody = emailBody.replace("${userName}", shortName);
        emailBody = emailBody.replace("${joinUrl}",joinUrl);
        messageToUser.setHtml(emailBody);

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
    
    
    public Sendgrid generateInstructorCourseJoinEmail(
            CourseAttributes course, InstructorAttributes instructor) 
                    throws UnsupportedEncodingException {
        
        Sendgrid message = getEmptyEmailAddressedToEmail(instructor.email);    
        message.setSubject(String.format(SUBJECT_PREFIX_INSTRUCTOR_COURSE_JOIN
                + " [%s][Course ID: %s]", course.name, course.id));

        String emailBody = EmailTemplates.USER_COURSE_JOIN;
        emailBody = fillUpInstructorJoinFragment(instructor, emailBody);
        emailBody = emailBody.replace("${userName}", instructor.name);
        emailBody = emailBody.replace("${courseName}", course.name);

        message.setHtml(emailBody);  
        return message;
    }
    
    public Sendgrid generateSystemErrorEmail(
            Throwable error,
            String requestPath, 
            String requestParam, 
            String version)
            throws UnsupportedEncodingException {
        
        //TODO: remove version parameter?
        
        Sendgrid message = new Sendgrid(Const.SystemParams.SENDGRID_USERNAME, Const.SystemParams.SENDGRID_PASSWORD);
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
        message.addTo(recipient);
        message.setFrom(senderEmail);
        message.setFromName(senderName);
        message.setSubject(String.format(SUBJECT_PREFIX_ADMIN_SYSTEM_ERROR,
                version, errorMessage));
    
        String emailBody = EmailTemplates.SYSTEM_ERROR;
    
        emailBody = emailBody.replace("${requestPath}", requestPath);
        emailBody = emailBody.replace("${requestParameters}", requestParam);
        emailBody = emailBody.replace("${errorMessage}", errorMessage);
        emailBody = emailBody.replace("${stackTrace}", stackTrace);
        message.setHtml(emailBody);
    
        return message;
    }

    public Sendgrid generateCompiledLogsEmail(String logs)
            throws UnsupportedEncodingException {
        
        Sendgrid message = getEmptyEmailAddressedToEmail(Config.SUPPORT_EMAIL);
        message.setSubject("Severe Error Logs Compilation");

        String emailBody = logs;

        message.setHtml(emailBody);
        return message;
    }
    
    public void sendEmails(List<Sendgrid> messages) {
        if (messages.isEmpty()) {
            return;
        }
        
        // Equally spread out the emails to be sent over 1 hour
        int numberOfEmailsSent = 0;
        int emailIntervalMillis = (1000 * 60 * 60) / messages.size();

        // Sets interval to a maximum of 5 seconds if the interval is too large
        int maxIntervalMillis = 5000;
        emailIntervalMillis = emailIntervalMillis > maxIntervalMillis ? maxIntervalMillis : emailIntervalMillis;

        for (Sendgrid m : messages) {
            try {
                long emailDelayTimer = numberOfEmailsSent * emailIntervalMillis;
                addEmailToTaskQueue(m, emailDelayTimer);
                numberOfEmailsSent++;
            } catch (Exception e) {
                log.severe("Error in sending : " + m.toString()
                        + " Cause : " + e.getMessage());
            }
        }

    }

    public void addEmailToTaskQueue(Sendgrid message, long emailDelayTimer) {
        try {
            HashMap<String, String> paramMap = new HashMap<String, String>();
            paramMap.put(ParamsNames.EMAIL_SUBJECT, message.getSubject());
            paramMap.put(ParamsNames.EMAIL_CONTENT, message.getHtml());
            paramMap.put(ParamsNames.EMAIL_SENDER, message.getFrom());
            paramMap.put(ParamsNames.EMAIL_RECEIVER, message.getTos().get(0));
            paramMap.put(ParamsNames.EMAIL_REPLY_TO_ADDRESS, message.getReplyTo());
            
            TaskQueuesLogic taskQueueLogic = TaskQueuesLogic.inst();
            taskQueueLogic.createAndAddDeferredTask(SystemParams.SEND_EMAIL_TASK_QUEUE,
                    Const.ActionURIs.SEND_EMAIL_WORKER, paramMap, emailDelayTimer);
        } catch (Exception e) {
            log.severe("Error when adding email to task queue: " + e.getMessage());
        } 
        
    }

    public void sendEmail(Sendgrid message) throws JSONException {
        log.info(getEmailInfo(message));
        if (message.getText() == null) {
            message.setText(generatePlainTextFromHtml(message.getHtml()));
        }
        message.send();        
    }
    
    
    /**
     * This method sends the email as well as logs its receiver, subject and content 
     * @param message
     * @throws JSONException 
     */
    public void sendAndLogEmail(Sendgrid message) throws JSONException {
        log.info(getEmailInfo(message));
        System.out.println("before sending");
        if (message.getText() == null) {
            message.setText(generatePlainTextFromHtml(message.getHtml()));
        }
        message.send();
        System.out.println("after sending");
        
        try {
            EmailLogEntry newEntry = new EmailLogEntry(message);
            String emailLogInfo = newEntry.generateLogMessage();
            log.log(Level.INFO, emailLogInfo);
        } catch (Exception e) {
            log.severe("Failed to generate log for email: " + getEmailInfo(message));
            e.printStackTrace();
        }
        
    }

    public Sendgrid sendErrorReport(String path, String params, Throwable error) {
        Sendgrid email = null;
        try {
            email = generateSystemErrorEmail(error, path, params,
                    Config.inst().getAppVersion());
            sendEmail(email);
            log.severe("Sent crash report: " + Emails.getEmailInfo(email));
        } catch (Exception e) {
            log.severe("Error in sending crash report: "
                    + (email == null ? "" : email.toString()));
        }
    
        return email;
    }

    public Sendgrid sendLogReport(Sendgrid message) {
        Sendgrid email = null;
        try {
            sendEmail(message);
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

    private Sendgrid getEmptyEmailAddressedToEmail(String email)
            throws UnsupportedEncodingException {
        
        Sendgrid message = new Sendgrid(Const.SystemParams.SENDGRID_USERNAME, Const.SystemParams.SENDGRID_PASSWORD);
        
        message.addTo(email);
        message.setFrom(senderEmail);
        message.setFromName(senderName);
        message.setReplyTo(replyTo);
        
        return message;
    }
    
    
    private Sendgrid setBccRecipientToEmail(Sendgrid mail, String newAddress) throws JSONException { 
        mail.setBcc(newAddress);
        return mail;
    }
    
    private boolean isYetToJoinCourse(StudentAttributes s) {
        return s.googleId == null || s.googleId.isEmpty();
    }

    /**
     * Generate email recipient list for the automated reminders sent.
     * Used for AdminActivityLog
     */
    public static ArrayList<Object> extractRecipientsList(ArrayList<Sendgrid> emails){
    
        ArrayList<Object> data = new ArrayList<Object>();
        
        try{
            for (int i = 0; i < emails.size(); i++){
                ArrayList<String> recipients = emails.get(i).getTos();
                for (int j = 0; j < recipients.size(); j++){
                    data.add(recipients.get(j));
                }
            }
        } catch (Exception e){
            throw new RuntimeException("Unexpected exception during generation of log messages for automated reminders",e);
        }
        
        return data;
    }
    
    private String generatePlainTextFromHtml(String html) {
        return Jsoup.parse(html).text();
    }
}