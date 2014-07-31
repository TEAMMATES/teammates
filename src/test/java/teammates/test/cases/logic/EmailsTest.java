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

import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.EvaluationAttributes;
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

public class EmailsTest extends BaseComponentTestCase {
    
    private String from;
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
        
        InternetAddress internetAddress = new InternetAddress("Admin@"
                + Config.inst().getAppId() + ".appspotmail.com",
                "TEAMMATES Admin");
        from = internetAddress.toString();
        replyTo = "teammates@comp.nus.edu.sg";
    }

    @Test
    public void testGetEmailInfo() throws MessagingException {

        Session session = Session.getDefaultInstance(new Properties(), null);
        MimeMessage message = new MimeMessage(session);

        String email = "receiver@gmail.com";
        String from = "sender@gmail.com";

        message.addRecipient(Message.RecipientType.TO, new InternetAddress(
                email));

        message.setFrom(new InternetAddress(from));
        String subject = "email subject";
        message.setSubject(subject);
        message.setContent("<h1>email body</h1>", "text/html");

        assertEquals(
                "[Email sent]to=receiver@gmail.com|from=sender@gmail.com|subject=email subject",
                Emails.getEmailInfo(message));
    }

    @Test
    public void testGenerateEvaluationEmailBase() throws IOException,
            MessagingException, GeneralSecurityException {

        EvaluationAttributes e = new EvaluationAttributes();
        e.name = "Evaluation Name";
        e.endTime = TimeHelper.getDateOffsetToCurrentTime(0);

        CourseAttributes c = new CourseAttributes();
        c.id = "course-id";
        c.name = "Course Name";

        StudentAttributes s = new StudentAttributes();
        s.name = "Student Name";
        s.key = "skxxxxxxxxxks";
        s.email = "student@email.com";
        
        InstructorAttributes i = new InstructorAttributes("googleId2", "courseId3", "name", "instructr@email.com");

        ______TS("generic template, student yet to join");

        String template = EmailTemplates.USER_EVALUATION_;
        MimeMessage email = new Emails().generateEvaluationEmailBaseForStudent(c, e, s,
                template);

        // check receiver
        assertEquals(s.email, email.getAllRecipients()[0].toString());

        // check sender
        assertEquals(from, email.getFrom()[0].toString());
        
        //check replyTo
        assertEquals(replyTo, email.getReplyTo()[0].toString());

        // check subject
        assertEquals(
                "${subjectPrefix} [Course: Course Name][Evaluation: Evaluation Name]",
                email.getSubject());

        // check email body
        String encryptedKey = StringHelper.encrypt(s.key);
        String joinUrl = s.getRegistrationUrl();

        String submitUrl = Config.APP_URL
                + Const.ActionURIs.STUDENT_EVAL_SUBMISSION_EDIT_PAGE;
        submitUrl = Url.addParamToUrl(submitUrl, Const.ParamsNames.COURSE_ID, c.id);
        submitUrl = Url.addParamToUrl(submitUrl, Const.ParamsNames.EVALUATION_NAME,
                e.name);

        String deadline = TimeHelper.formatTime(e.endTime);

        String emailBody = email.getContent().toString();

        AssertHelper.assertContainsRegex("Hello " + s.name + "{*}course <i>" + c.name
                + "{*}" + joinUrl + "{*}" + joinUrl + "{*}"  
                + "{*}${status}{*}" + c.id + "{*}" + c.name + "{*}"
                + e.name + "{*}" + deadline + "{*}" + submitUrl + "{*}"
                + submitUrl, emailBody);

        printEmail(email);

        ______TS("published template, student yet to join");

        template = EmailTemplates.USER_EVALUATION_PUBLISHED;
        email = new Emails().generateEvaluationEmailBaseForStudent(c, e, s, template);

        emailBody = email.getContent().toString();

        assertTrue(emailBody.contains(encryptedKey));
        assertFalse(emailBody.contains(submitUrl));

        String reportUrl = Config.APP_URL
                + Const.ActionURIs.STUDENT_EVAL_RESULTS_PAGE;
        reportUrl = Url.addParamToUrl(reportUrl, Const.ParamsNames.COURSE_ID, c.id);
        reportUrl = Url.addParamToUrl(reportUrl, Const.ParamsNames.EVALUATION_NAME,
                e.name);

        AssertHelper.assertContainsRegex("Hello " + s.name + "{*}course <i>" + c.name
                + "{*}" + joinUrl + "{*}" + joinUrl + "{*}"  
                + "{*}is now ready for viewing{*}" + c.id + "{*}"
                + c.name + "{*}" + e.name + "{*}" + reportUrl + "{*}"
                + reportUrl, emailBody);

        printEmail(email);

        ______TS("generic template, student joined");

        s.googleId = "student1id"; // set student id to make him "joined"
        template = EmailTemplates.USER_EVALUATION_;

        email = new Emails().generateEvaluationEmailBaseForStudent(c, e, s, template);

        emailBody = email.getContent().toString();

        assertFalse(emailBody.contains(encryptedKey));
        AssertHelper.assertContainsRegex("Hello " + s.name + "{*}" + c.id + "{*}" + c.name
                + "{*}" + e.name + "{*}" + deadline + "{*}" + submitUrl + "{*}"
                + submitUrl, emailBody);

        printEmail(email);

        ______TS("published template, student joined");

        template = EmailTemplates.USER_EVALUATION_PUBLISHED;
        email = new Emails().generateEvaluationEmailBaseForStudent(c, e, s, template);

        emailBody = email.getContent().toString();

        assertFalse(emailBody.contains(encryptedKey));

        AssertHelper.assertContainsRegex("Hello " + s.name
                + "{*}is now ready for viewing{*}" + c.id + "{*}" + c.name
                + "{*}" + e.name + "{*}" + reportUrl + "{*}" + reportUrl,
                emailBody);

        printEmail(email);

        ______TS("generic template, sent to instructors");
        
        template = EmailTemplates.USER_EVALUATION_;
        email = new Emails().generateEvaluationEmailBaseForInstructor(c, e, i, template);

        emailBody = email.getContent().toString();

        assertFalse(emailBody.contains("${joinFragment}"));
        
        AssertHelper.assertContainsRegex("Hello " + i.name + "{*}"
                + "The email below has been sent to students of course: " + c.id
                + "{*}" + c.id + "{*}" + c.name
                + "{*}" + e.name + "{*}" + deadline + "{*}" + submitUrl + "{*}"
                + submitUrl, emailBody);

        printEmail(email);
        
        ______TS("published template, sent to instructors");
        
        template = EmailTemplates.USER_EVALUATION_PUBLISHED;
        email = new Emails().generateEvaluationEmailBaseForInstructor(c, e, i, template);

        emailBody = email.getContent().toString();

        assertFalse(emailBody.contains("${joinFragment}"));
        
        AssertHelper.assertContainsRegex("Hello " + i.name + "{*}"
                + "The email below has been sent to students of course: " + c.id
                + "{*}is now ready for viewing{*}" + c.id + "{*}" + c.name
                + "{*}" + e.name + "{*}" + reportUrl + "{*}" + reportUrl,
                emailBody);

        printEmail(email);
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
        s.email = "student@email.com";
        
        InstructorAttributes i = new InstructorAttributes("googleId1", "courseId2", "name", "instructr@email.com");

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
        s.email = "student@email.com";

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
    public void testGenerateEvaluationEmails() throws MessagingException,
            IOException {
        List<StudentAttributes> students = new ArrayList<StudentAttributes>();
        List<InstructorAttributes> instructors = new ArrayList<InstructorAttributes>();

        EvaluationAttributes e = new EvaluationAttributes();
        e.name = "Evaluation Name";
        e.endTime = TimeHelper.getDateOffsetToCurrentTime(0);

        CourseAttributes c = new CourseAttributes();
        c.id = "course-id";
        c.name = "Course Name";

        StudentAttributes s1 = new StudentAttributes();
        s1.name = "Student1 Name";
        s1.key = "skxxxxxxxxxks1";
        s1.email = "student1@email.com";
        students.add(s1);

        StudentAttributes s2 = new StudentAttributes();
        s2.name = "Student2 Name";
        s2.key = "skxxxxxxxxxks2";
        s2.email = "student2@email.com";
        students.add(s2);
        
        InstructorAttributes i1 = new InstructorAttributes("googleId", "CourseId", "Instructor1 Name", "instructor1@email.com");
        instructors.add(i1);
        
        InstructorAttributes i2 = new InstructorAttributes("anotherId", "courseId2",  "Instructor2 Name",  "instructor2@email.com");
        instructors.add(i2);
        
        ______TS("evaluation opening emails");

        List<MimeMessage> emails = new Emails()
                .generateEvaluationOpeningEmails(c, e, students, instructors);
        assertEquals(4, emails.size());

        String prefix = Emails.SUBJECT_PREFIX_STUDENT_EVALUATION_OPENING;
        String status = "is now open";
        verifyEmail(s1, emails.get(0), prefix, status);
        verifyEmail(s2, emails.get(1), prefix, status);
        verifyEmail(i1, emails.get(2), prefix, status);
        verifyEmail(i2, emails.get(3), prefix, status);
        
        ______TS("evaluation reminders");

        emails = new Emails().generateEvaluationReminderEmails(c, e, students, instructors);
        assertEquals(4, emails.size());

        prefix = Emails.SUBJECT_PREFIX_STUDENT_EVALUATION_REMINDER;
        status = "is still open for submissions";
        verifyEmail(s1, emails.get(0), prefix, status);
        verifyEmail(s2, emails.get(1), prefix, status);
        verifyEmail(i1, emails.get(2), prefix, status);
        verifyEmail(i2, emails.get(3), prefix, status);
        
        ______TS("evaluation closing alerts");

        emails = new Emails().generateEvaluationClosingEmails(c, e, students, instructors);
        assertEquals(4, emails.size());

        prefix = Emails.SUBJECT_PREFIX_STUDENT_EVALUATION_CLOSING;
        status = "is closing soon";
        verifyEmail(s1, emails.get(0), prefix, status);
        verifyEmail(s2, emails.get(1), prefix, status);
        verifyEmail(i1, emails.get(2), prefix, status);
        verifyEmail(i2, emails.get(3), prefix, status);

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
        s1.email = "student5InCourse1@gmail.com";

        StudentAttributes s2 = new StudentAttributes();
        s2.email = "student2InCourse1@gmail.com";
        
        StudentAttributes s3 = new StudentAttributes();
        s3.email = "student3InCourse1@gmail.com";
        
        InstructorAttributes i1 = new InstructorAttributes("googleId", "courseId1", "name", "instructor1@course1.com");
        
        InstructorAttributes i2 = new InstructorAttributes("googleId", "courseId1", "name", "instructor2@course1.com");
        
        ______TS("feedback session opening emails");

        List<MimeMessage> emails = new Emails()
                .generateFeedbackSessionOpeningEmails(fsa);
        assertEquals(9, emails.size());

        String prefix = Emails.SUBJECT_PREFIX_FEEDBACK_SESSION_OPENING;
        String status = "is now open";
        verifyEmail(s1, emails.get(0), prefix, status);
        verifyEmail(s2, emails.get(1), prefix, status);
        
        ______TS("feedback session reminders");

        emails = new Emails().generateFeedbackSessionReminderEmails(c, fsa, students, instructors, instructors);
        assertEquals(13, emails.size());

        prefix = Emails.SUBJECT_PREFIX_FEEDBACK_SESSION_REMINDER;
        status = "is still open for submissions";
        verifyEmail(i1, emails.get(1), prefix, status);
        verifyEmail(i2, emails.get(2), prefix, status);
        
        ______TS("feedback session closing alerts");

        emails = new Emails().generateFeedbackSessionClosingEmails(fsa);
        assertEquals(7, emails.size());

        prefix = Emails.SUBJECT_PREFIX_FEEDBACK_SESSION_CLOSING;
        status = "is closing soon";
        verifyEmail(s1, emails.get(0), prefix, status);
        verifyEmail(s3, emails.get(1), prefix, status);

        ______TS("feedback session published alerts");

        emails = new Emails().generateFeedbackSessionPublishedEmails(fsa);
        assertEquals(9, emails.size());

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
            String prefix, String status) throws MessagingException,
            IOException {
        assertEquals(s.email, email.getAllRecipients()[0].toString());
        assertTrue(email.getSubject().contains(prefix));
        String emailBody = email.getContent().toString();
        assertTrue(emailBody.contains(status));
        assertFalse(emailBody.contains("$"));
    }
    
    private void verifyEmail(InstructorAttributes i, MimeMessage email,
            String prefix, String status) throws MessagingException,
            IOException {
        assertEquals(i.email, email.getAllRecipients()[0].toString());
        assertTrue(email.getSubject().contains(prefix));
        String emailBody = email.getContent().toString();
        assertTrue(emailBody.contains(status));
        assertFalse(emailBody.contains("$"));
    }


    @AfterClass()
    public static void classTearDown() throws Exception {
        printTestClassFooter();
        setLogLevelOfClass(Emails.class, Level.WARNING);
        setConsoleLoggingLevel(Level.WARNING);
    }
}
