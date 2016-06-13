package teammates.test.cases.logic;

import java.io.IOException;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.UserType;
import teammates.common.exception.TeammatesException;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.StringHelper;
import teammates.common.util.TimeHelper;
import teammates.common.util.Templates.EmailTemplates;
import teammates.logic.core.CoursesLogic;
import teammates.logic.core.EmailGenerator;
import teammates.logic.core.FeedbackSessionsLogic;
import teammates.logic.core.InstructorsLogic;
import teammates.logic.core.StudentsLogic;
import teammates.test.cases.BaseComponentTestCase;
import teammates.test.driver.AssertHelper;

public class EmailGeneratorTest extends BaseComponentTestCase {
    
    private String from;
    private String replyTo;
    
    @BeforeClass
    public void classSetUp() throws Exception {
        printTestClassHeader();
        removeAndRestoreTypicalDataInDatastore();
        
        String fromEmail = "Admin@" + Config.getAppId() + ".appspotmail.com";
        String fromName = "TEAMMATES Admin";
        replyTo = "teammates@comp.nus.edu.sg";
        InternetAddress internetAddress = new InternetAddress(fromEmail, fromName);
        from = internetAddress.toString();
    }
    
    @Test
    public void testGenerateFeedbackEmailBase() throws IOException, MessagingException {
        
        FeedbackSessionAttributes session = new FeedbackSessionAttributes();
        session.setFeedbackSessionName("Feedback Session Name");
        session.setEndTime(TimeHelper.getDateOffsetToCurrentTime(0));
        
        CourseAttributes course = new CourseAttributes("course-id", "Course Name");
        
        StudentAttributes student = new StudentAttributes();
        student.name = "Student Name";
        student.key = "skxxxxxxxxxks";
        student.email = "student@email.tmt";
        
        @SuppressWarnings("deprecation")
        InstructorAttributes instructor =
                new InstructorAttributes("googleId1", "courseId2", "name", "instructr@email.tmt");
        
        ______TS("generic template, student yet to join");
        
        String template = EmailTemplates.USER_FEEDBACK_SESSION;
        MimeMessage email = new EmailGenerator().generateFeedbackSessionEmailBaseForStudents(
                course, session, student, template, "[Course: %s][Feedback Session: %s]");
        
        // check receiver
        assertEquals(student.email, email.getAllRecipients()[0].toString());
        
        // check sender
        assertEquals(from, email.getFrom()[0].toString());
        
        //check replyTo
        assertEquals(replyTo, email.getReplyTo()[0].toString());
        
        // check subject
        assertEquals("[Course: Course Name][Feedback Session: Feedback Session Name]", email.getSubject());
        
        // check email body
        String encryptedKey = StringHelper.encrypt(student.key);
        
        String submitUrl = Config.getAppUrl(Const.ActionURIs.STUDENT_FEEDBACK_SUBMISSION_EDIT_PAGE)
                                 .withCourseId(course.getId())
                                 .withSessionName(session.getFeedbackSessionName())
                                 .withRegistrationKey(encryptedKey)
                                 .withStudentEmail(student.email)
                                 .toAbsoluteString();
        
        String deadline = TimeHelper.formatTime12H(session.getEndTime());
        
        String emailBody = email.getContent().toString();
        
        AssertHelper.assertContainsRegex(
                "Hello " + student.name + "{*}${status}{*}" + course.getId() + "{*}" + course.getName()
                        + "{*}" + session.getFeedbackSessionName() + "{*}" + deadline
                        + "{*}" + submitUrl + "{*}" + submitUrl,
                emailBody);
        
        printEmail(email);
        
        ______TS("published template, student yet to join");
        
        template = EmailTemplates.USER_FEEDBACK_SESSION_PUBLISHED;
        email = new EmailGenerator().generateFeedbackSessionEmailBaseForStudents(course, session, student, template, "");
        
        emailBody = email.getContent().toString();
        
        assertFalse(emailBody.contains(submitUrl));
        
        String reportUrl = Config.getAppUrl(Const.ActionURIs.STUDENT_FEEDBACK_RESULTS_PAGE)
                                 .withCourseId(course.getId())
                                 .withSessionName(session.getFeedbackSessionName())
                                 .withRegistrationKey(encryptedKey)
                                 .withStudentEmail(student.email)
                                 .toAbsoluteString();
        
        AssertHelper.assertContainsRegex(
                "Hello " + student.name + "{*}is now open for viewing{*}" + course.getId()
                        + "{*}" + course.getName() + "{*}" + session.getFeedbackSessionName()
                        + "{*}" + reportUrl + "{*}" + reportUrl,
                emailBody);
        
        printEmail(email);
        
        ______TS("generic template, student joined");
        
        student.googleId = "student1id"; // set student id to make him "joined"
        template = EmailTemplates.USER_FEEDBACK_SESSION;
        
        email = new EmailGenerator().generateFeedbackSessionEmailBaseForStudents(course, session, student, template, "");
        
        emailBody = email.getContent().toString();
        
        AssertHelper.assertContainsRegex(
                "Hello " + student.name + "{*}" + course.getId() + "{*}" + course.getName()
                        + "{*}" + session.getFeedbackSessionName() + "{*}" + deadline
                        + "{*}" + submitUrl + "{*}" + submitUrl,
                emailBody);
        
        printEmail(email);
        
        ______TS("published template, student joined");
        
        template = EmailTemplates.USER_FEEDBACK_SESSION_PUBLISHED;
        email = new EmailGenerator().generateFeedbackSessionEmailBaseForStudents(course, session, student, template, "");
        
        emailBody = email.getContent().toString();
        
        AssertHelper.assertContainsRegex(
                "Hello " + student.name + "{*}is now open for viewing{*}" + course.getId()
                        + "{*}" + course.getName() + "{*}" + session.getFeedbackSessionName()
                        + "{*}" + reportUrl + "{*}" + reportUrl,
                emailBody);
        
        printEmail(email);
        
        ______TS("generic template, sent to instructors");
        
        template = EmailTemplates.USER_FEEDBACK_SESSION;
        email = new EmailGenerator().generateFeedbackSessionEmailBaseForInstructors(course, session, instructor, template, "");
        
        emailBody = email.getContent().toString();
        
        assertFalse(emailBody.contains("${joinFragment}"));
        
        AssertHelper.assertContainsRegex(
                "Hello " + instructor.name + "{*}"
                        + "The email below has been sent to students of course: " + course.getId()
                        + "{*}" + course.getId() + "{*}" + course.getName()
                        + "{*}" + session.getFeedbackSessionName() + "{*}" + deadline
                        + "{*}{The student's unique submission url appears here}"
                        + "{*}{The student's unique submission url appears here}",
                emailBody);
        
        printEmail(email);
        
        ______TS("published template, sent to instructors");
        
        template = EmailTemplates.USER_FEEDBACK_SESSION_PUBLISHED;
        email = new EmailGenerator().generateFeedbackSessionEmailBaseForInstructors(course, session, instructor, template, "");
        
        emailBody = email.getContent().toString();
        
        assertFalse(emailBody.contains("${joinFragment}"));
        
        AssertHelper.assertContainsRegex(
                "Hello " + instructor.name + "{*}"
                        + "The email below has been sent to students of course: " + course.getId()
                        + "{*}is now open for viewing{*}" + course.getId() + "{*}" + course.getName()
                        + "{*}" + session.getFeedbackSessionName()
                        + "{*}{The student's unique results url appears here}"
                        + "{*}{The student's unique results url appears here}",
                emailBody);
        
        printEmail(email);
        
    }
    
    @Test
    public void testSystemCrashReportEmailContent() throws IOException, MessagingException {
        
        AssertionError error = new AssertionError("invalid parameter");
        String stackTrace = TeammatesException.toStringWithStackTrace(error);
        String requestMethod = "GET";
        String requestUserAgent = "user-agent";
        String requestPath = "/page/studentHome";
        String requestUrl = "/page/studentHome/";
        String requestParam = "{}";
        UserType userType = new UserType("Not logged in");
        
        MimeMessage email =
                new EmailGenerator().generateSystemErrorEmail(requestMethod, requestUserAgent, requestPath,
                                                              requestUrl, requestParam, userType, error);
        
        // check receiver
        String recipient = Config.SUPPORT_EMAIL;
        assertEquals(recipient, email.getAllRecipients()[0].toString());
        
        // check sender
        assertEquals(from, email.getFrom()[0].toString());
        
        // check email body
        String emailBody = email.getContent().toString();
        AssertHelper.assertContainsRegex(
                "<b>Error Message</b><br/><pre><code>" + error.getMessage()
                        + "</code></pre>"
                        + "<br/><b>Actual user</b>" + "Not logged in"
                        + "<br/><b>Request Method</b>" + requestMethod
                        + "<br/><b>User Agent</b>" + requestUserAgent
                        + "<br/><b>Request Url</b>" + requestUrl
                        + "<br/><b>Request Path</b>" + requestPath
                        + "<br/><b>Request Parameters</b>" + requestParam
                        + "<br/><b>Stack Trace</b><pre><code>" + stackTrace + "</code></pre>",
                emailBody);
    }
    
    @Test
    public void testGenerateStudentCourseJoinEmail() throws IOException, MessagingException {
        
        CourseAttributes course = new CourseAttributes("course-id", "Course Name");
        
        StudentAttributes student = new StudentAttributes();
        student.name = "Student Name";
        student.key = "skxxxxxxxxxks";
        student.email = "student@email.tmt";
        
        MimeMessage email = new EmailGenerator().generateStudentCourseJoinEmail(course, student);
        
        // check receiver
        assertEquals(student.email, email.getAllRecipients()[0].toString());
        
        // check sender
        assertEquals(from, email.getFrom()[0].toString());
        
        //check replyTo
        assertEquals(replyTo, email.getReplyTo()[0].toString());
        
        // check subject
        assertEquals(String.format(EmailType.STUDENT_COURSE_JOIN.getSubject(), course.getName(), course.getId()),
                     email.getSubject());
        
        // check email body
        String joinUrl = Config.getAppUrl(student.getRegistrationUrl()).toAbsoluteString();
        String emailBody = email.getContent().toString();
        
        AssertHelper.assertContainsRegex(
                "Hello " + student.name + "{*}course <i>" + course.getName() + "{*}" + joinUrl + "{*}" + joinUrl + "{*}",
                emailBody);
        
        assertFalse(emailBody.contains("$"));
        
        printEmail(email);
    }
    
    private void printEmail(MimeMessage email) throws MessagingException, IOException {
        print("Here's the generated email (for your eyeballing pleasure):");
        print(".............[Start of email]..............");
        print("Subject: " + email.getSubject());
        print("Body:");
        print(email.getContent().toString());
        print(".............[End of email]................");
    }
    
    @Test
    public void testGenerateFeedbackSessionEmails() throws Exception {
        StudentsLogic studentsLogic = StudentsLogic.inst();
        InstructorsLogic instructorsLogic = InstructorsLogic.inst();
        CoursesLogic coursesLogic = CoursesLogic.inst();
        FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
        
        FeedbackSessionAttributes session = fsLogic.getFeedbackSession("First feedback session", "idOfTypicalCourse1");
        
        CourseAttributes course = coursesLogic.getCourse(session.getCourseId());
        
        List<StudentAttributes> students = studentsLogic.getStudentsForCourse(session.getCourseId());
        List<InstructorAttributes> instructors = instructorsLogic.getInstructorsForCourse(session.getCourseId());
        
        StudentAttributes student1 = new StudentAttributes();
        student1.email = "student5InCourse1@gmail.tmt";
        
        StudentAttributes student2 = new StudentAttributes();
        student2.email = "student2InCourse1@gmail.tmt";
        
        StudentAttributes student3 = new StudentAttributes();
        student3.email = "student3InCourse1@gmail.tmt";
        
        @SuppressWarnings("deprecation")
        InstructorAttributes instructor1 =
                new InstructorAttributes("googleId", "courseId1", "name", "instructor1@course1.tmt");
        
        @SuppressWarnings("deprecation")
        InstructorAttributes instructor2 =
                new InstructorAttributes("googleId", "courseId1", "name", "instructor2@course1.tmt");
        
        ______TS("feedback session opening emails");
        
        List<MimeMessage> emails = new EmailGenerator().generateFeedbackSessionOpeningEmails(session);
        assertEquals(10, emails.size());
        
        String subject = String.format(EmailType.FEEDBACK_OPENING.getSubject(),
                                       course.getName(), session.getFeedbackSessionName());
        String status = "is now open";
        verifyEmail(student1.email, emails.get(0), subject, status);
        verifyEmail(student2.email, emails.get(1), subject, status);
        
        ______TS("feedback session reminders");
        
        emails = new EmailGenerator().generateFeedbackSessionReminderEmails(course, session, students, instructors, instructors);
        assertEquals(15, emails.size());
        
        subject = String.format(EmailType.FEEDBACK_SESSION_REMINDER.getSubject(),
                                course.getName(), session.getFeedbackSessionName());
        status = "is still open for submissions";
        verifyEmail(instructor1.email, emails.get(1), subject, status);
        verifyEmail(instructor2.email, emails.get(2), subject, status);
        
        ______TS("feedback session closing alerts");
        
        emails = new EmailGenerator().generateFeedbackSessionClosingEmails(session);
        assertEquals(8, emails.size());
        
        subject = String.format(EmailType.FEEDBACK_CLOSING.getSubject(),
                                course.getName(), session.getFeedbackSessionName());
        status = "is closing soon";
        verifyEmail(student1.email, emails.get(0), subject, status);
        verifyEmail(student3.email, emails.get(1), subject, status);
        String ignoreEmailMsg = "You may ignore this email if you have already submitted feedback.";
        verifyEmail(student1.email, emails.get(0), subject, ignoreEmailMsg);
        verifyEmail(student3.email, emails.get(1), subject, ignoreEmailMsg);
        
        ______TS("feedback session published alerts");
        
        emails = new EmailGenerator().generateFeedbackSessionPublishedEmails(session);
        assertEquals(10, emails.size());
        
        subject = String.format(EmailType.FEEDBACK_PUBLISHED.getSubject(),
                                course.getName(), session.getFeedbackSessionName());
        status = "The feedback responses for the following feedback session is now open for viewing.";
        verifyEmail(student1.email, emails.get(0), subject, status);
        verifyEmail(student2.email, emails.get(1), subject, status);
    }
    
    private void verifyEmail(String recipient, MimeMessage email, String subject, String textInEmail)
            throws MessagingException, IOException {
        assertEquals(recipient, email.getAllRecipients()[0].toString());
        assertEquals(subject, email.getSubject());
        String emailBody = email.getContent().toString();
        assertTrue(emailBody.contains(textInEmail));
        assertFalse(emailBody.contains("$"));
    }
    
    @AfterClass
    public void classTearDown() {
        printTestClassFooter();
    }
    
}
