package teammates.test.cases.logic;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.assertFalse;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.appengine.labs.repackaged.org.json.JSONException;

import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.TeammatesException;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.common.util.EmailTemplates;
import teammates.common.util.TimeHelper;
import teammates.common.util.Url;
import teammates.logic.core.CoursesLogic;
import teammates.logic.core.Emails;
import teammates.logic.core.FeedbackSessionsLogic;
import teammates.logic.core.InstructorsLogic;
import teammates.logic.core.StudentsLogic;
import teammates.test.cases.BaseComponentTestCase;
import teammates.test.cases.ui.browsertests.SystemErrorEmailReportTest;
import teammates.test.driver.AssertHelper;
import teammates.test.driver.TestProperties;
import teammates.googleSendgridJava.Sendgrid;

public class EmailsTest extends BaseComponentTestCase {
    
    private String from; // For MimeMessage testing
    private String fromEmail; // For Sendgrid testing
    private String fromName;  // For Sendgrid testing
    private String replyTo;
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        setGeneralLoggingLevel(Level.WARNING);
        setLogLevelOfClass(Emails.class, Level.FINE);
        setConsoleLoggingLevel(Level.FINE);
    }

    @BeforeMethod
    public void caseSetUp() throws ServletException, IOException {
        /* For Sendgrid testing */
        fromEmail = "Admin@" + Config.inst().getAppId() + ".appspotmail.com";
        fromName = "TEAMMATES Admin";
        replyTo = "teammates@comp.nus.edu.sg";
        
        /* For MimeMessage testing */
        InternetAddress internetAddress = new InternetAddress(fromEmail, fromName);
        from = internetAddress.toString();
    }

    @Test
    public void testGetEmailInfo() throws MessagingException, JSONException {
        String email = "receiver@gmail.tmt";
        String from = "sender@gmail.tmt";
        String subject = "email subject";

        ______TS("Sendgrid message");
        Sendgrid message = new Sendgrid(Config.SENDGRID_USERNAME, Config.SENDGRID_PASSWORD);

        message.addTo(email);
        message.setFrom(from);
        message.setSubject(subject);
        message.setHtml("<h1>email body</h1>");

        assertEquals("[Email sent]to=receiver@gmail.tmt|from=sender@gmail.tmt|subject=email subject",
                                        Emails.getEmailInfo(message));

        ______TS("MimeMessage");
        Session session = Session.getDefaultInstance(new Properties(), null);
        MimeMessage mimeMessage = new MimeMessage(session);

        mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
        mimeMessage.setFrom(new InternetAddress(from));
        mimeMessage.setSubject(subject);
        mimeMessage.setContent("<h1>email body</h1>", "text/html");

        assertEquals("[Email sent]to=receiver@gmail.tmt|from=sender@gmail.tmt|subject=email subject",
                                        Emails.getEmailInfo(mimeMessage));

    }
    
    @Test
    public void testGenerateFeedbackEmailBase() throws IOException,
            MessagingException, GeneralSecurityException {

        FeedbackSessionAttributes fsa = new FeedbackSessionAttributes();
        fsa.feedbackSessionName = "Feedback Session Name";
        fsa.endTime = TimeHelper.getDateOffsetToCurrentTime(0);

        CourseAttributes c = new CourseAttributes();
        c.id = "course-id";
        c.name = "Course Name";

        StudentAttributes s = new StudentAttributes();
        s.name = "Student Name";
        s.key = "skxxxxxxxxxks";
        s.email = "student@email.tmt";

        @SuppressWarnings("deprecation")
        InstructorAttributes i = new InstructorAttributes("googleId1", "courseId2", "name", "instructr@email.tmt");

        ______TS("generic template, student yet to join");

        String template = EmailTemplates.USER_FEEDBACK_SESSION;
        MimeMessage email = new Emails().generateFeedbackSessionEmailBaseForStudents(c, fsa, s,
                template);

        // check receiver
        assertEquals(s.email, email.getAllRecipients()[0].toString());

        // check sender
        assertEquals(from, email.getFrom()[0].toString());
        
        //check replyTo
        assertEquals(replyTo, email.getReplyTo()[0].toString());

        // check subject
        assertEquals(
                "${subjectPrefix} [Course: Course Name][Feedback Session: Feedback Session Name]",
                email.getSubject());

        // check email body
        String encryptedKey = StringHelper.encrypt(s.key);

        String submitUrl = new Url(Config.APP_URL + Const.ActionURIs.STUDENT_FEEDBACK_SUBMISSION_EDIT_PAGE)
                            .withCourseId(c.id)
                            .withSessionName(fsa.feedbackSessionName)
                            .withRegistrationKey(encryptedKey)
                            .withStudentEmail(s.email)
                            .toString();

        String deadline = TimeHelper.formatTime(fsa.endTime);

        String emailBody = email.getContent().toString();

        AssertHelper.assertContainsRegex("Hello " + s.name
                + "{*}${status}{*}" + c.id + "{*}" + c.name + "{*}"
                + fsa.feedbackSessionName + "{*}" + deadline + "{*}" + submitUrl + "{*}"
                + submitUrl, emailBody);

        printEmail(email);

        ______TS("published template, student yet to join");

        template = EmailTemplates.USER_FEEDBACK_SESSION_PUBLISHED;
        email = new Emails().generateFeedbackSessionEmailBaseForStudents(c, fsa, s, template);

        emailBody = email.getContent().toString();

        assertFalse(emailBody.contains(submitUrl));

        String reportUrl = new Url(Config.APP_URL + Const.ActionURIs.STUDENT_FEEDBACK_RESULTS_PAGE)
                            .withCourseId(c.id)
                            .withSessionName(fsa.feedbackSessionName)
                            .withRegistrationKey(encryptedKey)
                            .withStudentEmail(s.email)
                            .toString();

        AssertHelper.assertContainsRegex("Hello " + s.name
                + "{*}is now open for viewing{*}" + c.id + "{*}"
                + c.name + "{*}" + fsa.feedbackSessionName + "{*}" + reportUrl + "{*}"
                + reportUrl, emailBody);

        printEmail(email);

        ______TS("generic template, student joined");

        s.googleId = "student1id"; // set student id to make him "joined"
        template = EmailTemplates.USER_FEEDBACK_SESSION;

        email = new Emails().generateFeedbackSessionEmailBaseForStudents(c, fsa, s, template);

        emailBody = email.getContent().toString();

        AssertHelper.assertContainsRegex("Hello " + s.name + "{*}" + c.id + "{*}" + c.name
                + "{*}" + fsa.feedbackSessionName + "{*}" + deadline + "{*}" + submitUrl + "{*}"
                + submitUrl, emailBody);

        printEmail(email);

        ______TS("published template, student joined");

        template = EmailTemplates.USER_FEEDBACK_SESSION_PUBLISHED;
        email = new Emails().generateFeedbackSessionEmailBaseForStudents(c, fsa, s, template);

        emailBody = email.getContent().toString();

        AssertHelper.assertContainsRegex("Hello " + s.name
                + "{*}is now open for viewing{*}" + c.id + "{*}" + c.name
                + "{*}" + fsa.feedbackSessionName + "{*}" + reportUrl + "{*}" + reportUrl,
                emailBody);

        printEmail(email);
        
        ______TS("generic template, sent to instructors");
        
        template = EmailTemplates.USER_FEEDBACK_SESSION;
        email = new Emails().generateFeedbackSessionEmailBaseForInstructors(c, fsa, i, template);

        emailBody = email.getContent().toString();

        assertFalse(emailBody.contains("${joinFragment}"));
        
        AssertHelper.assertContainsRegex("Hello " + i.name + "{*}"
                + "The email below has been sent to students of course: " + c.id
                + "{*}" + c.id + "{*}" + c.name
                + "{*}" + fsa.feedbackSessionName + "{*}" + deadline 
                + "{*}{The student's unique submission url appears here}"
                + "{*}{The student's unique submission url appears here}"
                , emailBody);

        printEmail(email);
        
        ______TS("published template, sent to instructors");
        
        template = EmailTemplates.USER_FEEDBACK_SESSION_PUBLISHED;
        email = new Emails().generateFeedbackSessionEmailBaseForInstructors(c, fsa, i, template);

        emailBody = email.getContent().toString();

        assertFalse(emailBody.contains("${joinFragment}"));
        
        AssertHelper.assertContainsRegex("Hello " + i.name + "{*}"
                + "The email below has been sent to students of course: " + c.id
                + "{*}is now open for viewing{*}" + c.id + "{*}" + c.name
                + "{*}" + fsa.feedbackSessionName 
                + "{*}{The student's unique results url appears here}"
                + "{*}{The student's unique results url appears here}",
                emailBody);

        printEmail(email);
        
    }

    @Test
    public void testGenerateStudentCourseJoinEmail() throws IOException,
            MessagingException, GeneralSecurityException {

        CourseAttributes c = new CourseAttributes();
        c.id = "course-id";
        c.name = "Course Name";

        StudentAttributes s = new StudentAttributes();
        s.name = "Student Name";
        s.key = "skxxxxxxxxxks";
        s.email = "student@email.tmt";

        MimeMessage email = new Emails().generateStudentCourseJoinEmail(c, s);

        // check receiver
        assertEquals(s.email, email.getAllRecipients()[0].toString());

        // check sender
        assertEquals(from, email.getFrom()[0].toString());
        
        //check replyTo
        assertEquals(replyTo, email.getReplyTo()[0].toString());
        
        // check subject
        assertEquals(
                "TEAMMATES: Invitation to join course [Course Name][Course ID: course-id]",
                email.getSubject());

        // check email body
        String joinUrl = s.getRegistrationUrl();
        String emailBody = email.getContent().toString();

        AssertHelper.assertContainsRegex("Hello " + s.name + "{*}course <i>" + c.name
                + "{*}" + joinUrl + "{*}" + joinUrl + "{*}", emailBody);
        
        assertFalse(emailBody.contains("$"));

        printEmail(email);
    }

    private void printEmail(MimeMessage email) throws MessagingException,
            IOException {
        print("Here's the generated email (for your eyeballing pleasure):");
        print(".............[Start of email]..............");
        print("Subject: " + email.getSubject());
        print("Body:");
        print(email.getContent().toString());
        print(".............[End of email]................");
    }
    
    @Test
    public void testGenerateFeedbackSessionEmails() throws Exception {

        removeAndRestoreTypicalDataInDatastore();
        
        List<StudentAttributes> students = new ArrayList<StudentAttributes>();
        List<InstructorAttributes> instructors = new ArrayList<InstructorAttributes>();
        
        StudentsLogic studentsLogic = StudentsLogic.inst();
        InstructorsLogic instructorsLogic = InstructorsLogic.inst();
        CoursesLogic coursesLogic = CoursesLogic.inst();
        FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
        
        FeedbackSessionAttributes fsa = fsLogic.getFeedbackSession("First feedback session", "idOfTypicalCourse1");
        
        CourseAttributes c = coursesLogic.getCourse(fsa.courseId);
        
        students = studentsLogic.getStudentsForCourse(fsa.courseId);
        instructors = instructorsLogic.getInstructorsForCourse(fsa.courseId);
        
        StudentAttributes s1 = new StudentAttributes();
        s1.email = "student5InCourse1@gmail.tmt";

        StudentAttributes s2 = new StudentAttributes();
        s2.email = "student2InCourse1@gmail.tmt";
        
        StudentAttributes s3 = new StudentAttributes();
        s3.email = "student3InCourse1@gmail.tmt";

        @SuppressWarnings("deprecation")
        InstructorAttributes i1 = new InstructorAttributes("googleId", "courseId1", "name", "instructor1@course1.tmt");

        @SuppressWarnings("deprecation")
        InstructorAttributes i2 = new InstructorAttributes("googleId", "courseId1", "name", "instructor2@course1.tmt");
        
        ______TS("feedback session opening emails");

        List<MimeMessage> emails = new Emails()
                .generateFeedbackSessionOpeningEmails(fsa);
        assertEquals(10, emails.size());

        String prefix = Emails.SUBJECT_PREFIX_FEEDBACK_SESSION_OPENING;
        String status = "is now open";
        verifyEmail(s1, emails.get(0), prefix, status);
        verifyEmail(s2, emails.get(1), prefix, status);
        
        ______TS("feedback session reminders");

        emails = new Emails().generateFeedbackSessionReminderEmails(c, fsa, students, instructors, instructors);
        assertEquals(15, emails.size());

        prefix = Emails.SUBJECT_PREFIX_FEEDBACK_SESSION_REMINDER;
        status = "is still open for submissions";
        verifyEmail(i1, emails.get(1), prefix, status);
        verifyEmail(i2, emails.get(2), prefix, status);
        
        ______TS("feedback session closing alerts");

        emails = new Emails().generateFeedbackSessionClosingEmails(fsa);
        assertEquals(8, emails.size());

        prefix = Emails.SUBJECT_PREFIX_FEEDBACK_SESSION_CLOSING;
        status = "is closing soon";
        verifyEmail(s1, emails.get(0), prefix, status);
        verifyEmail(s3, emails.get(1), prefix, status);
        String ignoreEmailMsg = "You may ignore this email if you have already submitted feedback.";
        verifyEmail(s1, emails.get(0), prefix, ignoreEmailMsg);
        verifyEmail(s3, emails.get(1), prefix, ignoreEmailMsg);

        ______TS("feedback session published alerts");

        emails = new Emails().generateFeedbackSessionPublishedEmails(fsa);
        assertEquals(10, emails.size());

        prefix = Emails.SUBJECT_PREFIX_FEEDBACK_SESSION_PUBLISHED;
        status = "The feedback responses for the following feedback session is now open for viewing.";
        verifyEmail(s1, emails.get(0), prefix, status);
        verifyEmail(s2, emails.get(1), prefix, status);
    }
    
    @Test
    public void testSystemCrashReportEmailContent() throws IOException,
            MessagingException {

        
        AssertionError error = new AssertionError("invalid parameter");
        StackTraceElement s1 = new StackTraceElement(
                SystemErrorEmailReportTest.class.getName(), 
                "testSystemCrashReportEmailContent", 
                "SystemErrorEmailReportTest.java", 
                89);
        error.setStackTrace(new StackTraceElement[] {s1});
        String stackTrace = TeammatesException.toStringWithStackTrace(error);
        String requestPath = "/page/studentHome";
        String requestParam = "{}";

        MimeMessage email = new Emails().generateSystemErrorEmail(
                error, 
                requestPath, requestParam,
                TestProperties.inst().TEAMMATES_VERSION);

        // check receiver
        String recipient = Config.SUPPORT_EMAIL;
        assertEquals(recipient, email.getAllRecipients()[0].toString());

        // check sender
        assertEquals(from, email.getFrom()[0].toString());
        
            
        // check email body
        String emailBody = email.getContent().toString();
        AssertHelper.assertContainsRegex(
                "<b>Error Message</b><br/><pre><code>" + error.getMessage()
                + "</code></pre><br/><b>Request Path</b>" + requestPath 
                + "<br/><b>Request Parameters</b>" + requestParam
                + "<br/><b>Stack Trace</b><pre><code>" + stackTrace + "</code></pre>",
                emailBody);
    }

    private void verifyEmail(StudentAttributes s, MimeMessage email,
            String prefix, String textInEmail) throws MessagingException,
            IOException {
        assertEquals(s.email, email.getAllRecipients()[0].toString());
        assertTrue(email.getSubject().contains(prefix));
        String emailBody = email.getContent().toString();
        assertTrue(emailBody.contains(textInEmail));
        assertFalse(emailBody.contains("$"));
    }
    
    private void verifyEmail(InstructorAttributes i, MimeMessage email,
            String prefix, String textInEmail) throws MessagingException,
            IOException {
        assertEquals(i.email, email.getAllRecipients()[0].toString());
        assertTrue(email.getSubject().contains(prefix));
        String emailBody = email.getContent().toString();
        assertTrue(emailBody.contains(textInEmail));
        assertFalse(emailBody.contains("$"));
    }

    @Test
    public void testNoExceptionThrownWhenNoMessagesToSend() {
        new Emails().sendEmails(new ArrayList<MimeMessage>());
    }
    
    @Test
    public void testParseMimeMessageToSendgrid() throws MessagingException, JSONException, IOException {
        FeedbackSessionAttributes fsa = new FeedbackSessionAttributes();
        fsa.feedbackSessionName = "Feedback Session Name";
        fsa.endTime = TimeHelper.getDateOffsetToCurrentTime(0);

        CourseAttributes c = new CourseAttributes();
        c.id = "course-id";
        c.name = "Course Name";

        StudentAttributes s = new StudentAttributes();
        s.name = "Student Name";
        s.key = "skxxxxxxxxxks";
        s.email = "student@email.tmt";

        ______TS("Generate feedback email base");

        String template = EmailTemplates.USER_FEEDBACK_SESSION;
        MimeMessage email = new Emails().generateFeedbackSessionEmailBaseForStudents(
                                        c, fsa, s, template);
        Sendgrid sendgridEmail = new Emails().parseMimeMessageToSendgrid(email);

        testEmailAttributes(email, sendgridEmail);

        ______TS("Generate student course join email");
        email = new Emails().generateStudentCourseJoinEmail(c, s);
        sendgridEmail = new Emails().parseMimeMessageToSendgrid(email);

        testEmailAttributes(email, sendgridEmail);

        ______TS("System crash report email");
        AssertionError error = new AssertionError("invalid parameter");
        StackTraceElement s1 = new StackTraceElement(
                                        SystemErrorEmailReportTest.class.getName(),
                                        "testSystemCrashReportEmailContent",
                                        "SystemErrorEmailReportTest.java",
                                        89);
        error.setStackTrace(new StackTraceElement[] { s1 });
        String requestPath = "/page/studentHome";
        String requestParam = "{}";

        email = new Emails().generateSystemErrorEmail(
                                        error, requestPath, requestParam,
                                        TestProperties.inst().TEAMMATES_VERSION);
        sendgridEmail = new Emails().parseMimeMessageToSendgrid(email);

        testEmailAttributes(email, sendgridEmail);
    }

    private void testEmailAttributes(MimeMessage email, Sendgrid sendgridEmail) throws MessagingException,
                                    IOException {
        // check receiver
        assertEquals(email.getAllRecipients()[0].toString(), sendgridEmail.getTos().get(0));

        // check sender
        assertEquals(new Emails().extractSenderEmail(email.getFrom()[0].toString()), sendgridEmail.getFrom());
        
        //check replyTo
        assertEquals(email.getReplyTo()[0].toString(), sendgridEmail.getReplyTo());

        // check subject
        assertEquals(email.getSubject(), sendgridEmail.getSubject());

        // check email body
        assertEquals(email.getContent().toString(), sendgridEmail.getHtml());
    }

    @AfterClass()
    public static void classTearDown() throws Exception {
        printTestClassFooter();
        setLogLevelOfClass(Emails.class, Level.WARNING);
        setConsoleLoggingLevel(Level.WARNING);
    }
}
