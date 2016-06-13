package teammates.test.cases.logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.UserType;
import teammates.common.util.Config;
import teammates.common.util.Templates.EmailTemplates;
import teammates.common.util.TimeHelper;
import teammates.logic.core.EmailGenerator;
import teammates.logic.core.Emails;
import teammates.logic.core.Sendgrid;
import teammates.test.cases.BaseComponentTestCase;
import teammates.test.cases.ui.browsertests.SystemErrorEmailReportTest;

import com.google.appengine.labs.repackaged.org.json.JSONException;

public class EmailsTest extends BaseComponentTestCase {
    
    @BeforeClass
    public static void classSetUp() {
        printTestClassHeader();
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
    public void testNoExceptionThrownWhenNoMessagesToSend() {
        new Emails().sendEmails(new ArrayList<MimeMessage>());
    }
    
    @Test
    public void testParseMimeMessageToSendgrid() throws MessagingException, JSONException, IOException {
        FeedbackSessionAttributes fsa = new FeedbackSessionAttributes();
        fsa.setFeedbackSessionName("Feedback Session Name");
        fsa.setEndTime(TimeHelper.getDateOffsetToCurrentTime(0));

        CourseAttributes c = new CourseAttributes("course-id", "Course Name");

        StudentAttributes s = new StudentAttributes();
        s.name = "Student Name";
        s.key = "skxxxxxxxxxks";
        s.email = "student@email.tmt";

        ______TS("Generate feedback email base");

        String template = EmailTemplates.USER_FEEDBACK_SESSION;
        MimeMessage email = new EmailGenerator().generateFeedbackSessionEmailBaseForStudents(c, fsa, s, template, "");
        Sendgrid sendgridEmail = new Emails().parseMimeMessageToSendgrid(email);

        testEmailAttributes(email, sendgridEmail);

        ______TS("Generate student course join email");
        email = new EmailGenerator().generateStudentCourseJoinEmail(c, s);
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
        String requestMethod = "GET";
        String requestUserAgent = "user-agent";
        String requestPath = "/page/studentHome";
        String requestUrl = "/page/studentHome/";
        String requestParam = "{}";
        UserType userType = new UserType("");

        email = new EmailGenerator().generateSystemErrorEmail(requestMethod, requestUserAgent, requestPath,
                                                              requestUrl, requestParam, userType, error);
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

    @AfterClass
    public static void classTearDown() {
        printTestClassFooter();
    }
}
