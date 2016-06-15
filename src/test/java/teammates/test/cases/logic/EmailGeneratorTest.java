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
import teammates.logic.core.CoursesLogic;
import teammates.logic.core.EmailGenerator;
import teammates.logic.core.FeedbackSessionsLogic;
import teammates.logic.core.InstructorsLogic;
import teammates.logic.core.StudentsLogic;
import teammates.test.cases.BaseComponentTestCase;
import teammates.test.driver.AssertHelper;

/**
 * SUT: {@link EmailGenerator}
 */
public class EmailGeneratorTest extends BaseComponentTestCase {
    
    private static final CoursesLogic coursesLogic = CoursesLogic.inst();
    private static final FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
    private static final InstructorsLogic instructorsLogic = InstructorsLogic.inst();
    private static final StudentsLogic studentsLogic = StudentsLogic.inst();
    
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
    public void testGenerateFeedbackSessionEmails() throws Exception {
        FeedbackSessionAttributes session = fsLogic.getFeedbackSession("First feedback session", "idOfTypicalCourse1");
        
        CourseAttributes course = coursesLogic.getCourse(session.getCourseId());
        
        List<StudentAttributes> students = studentsLogic.getStudentsForCourse(session.getCourseId());
        List<InstructorAttributes> instructors = instructorsLogic.getInstructorsForCourse(session.getCourseId());
        
        StudentAttributes student1 = studentsLogic.getStudentForEmail(course.getId(), "student5InCourse1@gmail.tmt");
        StudentAttributes student2 = studentsLogic.getStudentForEmail(course.getId(), "student2InCourse1@gmail.tmt");
        StudentAttributes student3 = studentsLogic.getStudentForEmail(course.getId(), "student3InCourse1@gmail.tmt");
        
        InstructorAttributes instructor1 =
                instructorsLogic.getInstructorForEmail(course.getId(), "instructor1@course1.tmt");
        InstructorAttributes instructor2 =
                instructorsLogic.getInstructorForEmail(course.getId(), "instructor2@course1.tmt");
        
        ______TS("feedback session opening emails");
        
        List<MimeMessage> emails = new EmailGenerator().generateFeedbackSessionOpeningEmails(session);
        assertEquals(10, emails.size());
        
        String subject = String.format(EmailType.FEEDBACK_OPENING.getSubject(),
                                       course.getName(), session.getFeedbackSessionName());
        
        String submitUrl = Config.getAppUrl(Const.ActionURIs.STUDENT_FEEDBACK_SUBMISSION_EDIT_PAGE)
                                 .withCourseId(course.getId())
                                 .withSessionName(session.getFeedbackSessionName())
                                 .withRegistrationKey(StringHelper.encrypt(student1.key))
                                 .withStudentEmail(student1.email)
                                 .toAbsoluteString();
        
        String textInEmail = "Hello " + student1.name + "{*}The following feedback session is now open"
                             + "{*}" + course.getId() + "{*}" + course.getName()
                             + "{*}" + session.getFeedbackSessionName()
                             + "{*}" + submitUrl + "{*}" + submitUrl;
        
        verifyEmail(emails.get(0), student1.email, subject, textInEmail);
        
        verifyEmail(emails.get(1), student2.email, subject, "The following feedback session is now open");
        
        ______TS("feedback session reminders");
        
        emails = new EmailGenerator().generateFeedbackSessionReminderEmails(session, students, instructors, instructors);
        assertEquals(15, emails.size());
        
        subject = String.format(EmailType.FEEDBACK_SESSION_REMINDER.getSubject(),
                                course.getName(), session.getFeedbackSessionName());
        
        submitUrl = Config.getAppUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_SUBMISSION_EDIT_PAGE)
                          .withCourseId(course.getId())
                          .withSessionName(session.getFeedbackSessionName())
                          .toAbsoluteString();
        
        textInEmail = "Hello " + instructor1.name + "{*}The following feedback session is "
                      + "still open for submissions{*}" + course.getId() + "{*}" + course.getName()
                      + "{*}" + session.getFeedbackSessionName() + "{*}" + submitUrl + "{*}" + submitUrl;
        
        verifyEmail(emails.get(1), instructor1.email, subject, textInEmail);
        
        verifyEmail(emails.get(2), instructor2.email, subject,
                    "The following feedback session is still open for submissions");
        
        ______TS("feedback session closing alerts");
        
        emails = new EmailGenerator().generateFeedbackSessionClosingEmails(session);
        assertEquals(8, emails.size());
        
        subject = String.format(EmailType.FEEDBACK_CLOSING.getSubject(),
                                course.getName(), session.getFeedbackSessionName());
        
        submitUrl = Config.getAppUrl(Const.ActionURIs.STUDENT_FEEDBACK_SUBMISSION_EDIT_PAGE)
                          .withCourseId(course.getId())
                          .withSessionName(session.getFeedbackSessionName())
                          .withRegistrationKey(StringHelper.encrypt(student1.key))
                          .withStudentEmail(student1.email)
                          .toAbsoluteString();
        
        textInEmail = "Hello " + student1.name + "{*}The following feedback session is closing soon"
                      + "{*}" + course.getId() + "{*}" + course.getName()
                      + "{*}" + session.getFeedbackSessionName()
                      + "{*}" + submitUrl + "{*}" + submitUrl
                      + "{*}You may ignore this email if you have already submitted feedback.";
        
        verifyEmail(emails.get(0), student1.email, subject, textInEmail);
        
        verifyEmail(emails.get(1), student3.email, subject, "The following feedback session is closing soon");
        verifyEmail(emails.get(1), student3.email, subject,
                    "You may ignore this email if you have already submitted feedback.");
        
        ______TS("feedback session published alerts");
        
        emails = new EmailGenerator().generateFeedbackSessionPublishedEmails(session);
        assertEquals(10, emails.size());
        
        subject = String.format(EmailType.FEEDBACK_PUBLISHED.getSubject(),
                                course.getName(), session.getFeedbackSessionName());
        
        String reportUrl = Config.getAppUrl(Const.ActionURIs.STUDENT_FEEDBACK_RESULTS_PAGE)
                                 .withCourseId(course.getId())
                                 .withSessionName(session.getFeedbackSessionName())
                                 .withRegistrationKey(StringHelper.encrypt(student1.key))
                                 .withStudentEmail(student1.email)
                                 .toAbsoluteString();
        
        textInEmail = "Hello " + student1.name + "{*}The feedback responses for the following "
                      + "feedback session is now open for viewing{*}" + course.getId()
                      + "{*}" + course.getName() + "{*}" + session.getFeedbackSessionName()
                      + "{*}" + reportUrl + "{*}" + reportUrl;
        
        verifyEmail(emails.get(0), student1.email, subject, textInEmail);
        
        verifyEmail(emails.get(1), student2.email, subject,
                    "The feedback responses for the following feedback session is now open for viewing");
    }
    
    @Test
    public void testGenerateStudentCourseJoinEmail() throws IOException, MessagingException {
        
        CourseAttributes course = new CourseAttributes("course-id", "Course Name");
        
        StudentAttributes student = new StudentAttributes();
        student.name = "Student Name";
        student.key = "skxxxxxxxxxks";
        student.email = "student@email.tmt";
        
        MimeMessage email = new EmailGenerator().generateStudentCourseJoinEmail(course, student);
        String subject = String.format(EmailType.STUDENT_COURSE_JOIN.getSubject(), course.getName(), course.getId());
        
        // check email body
        String joinUrl = Config.getAppUrl(student.getRegistrationUrl()).toAbsoluteString();
        String textInEmail = "Hello " + student.name + "{*}" + course.getName()
                             + "{*}" + joinUrl + "{*}" + joinUrl + "{*}";
        
        verifyEmail(email, student.email, subject, textInEmail);
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
        
        String textInEmail = "<b>Error Message</b><br><pre><code>" + error.getMessage()
                             + "</code></pre>"
                             + "<br><b>Actual user</b>" + "Not logged in"
                             + "<br><b>Request Method</b>" + requestMethod
                             + "<br><b>User Agent</b>" + requestUserAgent
                             + "<br><b>Request Url</b>" + requestUrl
                             + "<br><b>Request Path</b>" + requestPath
                             + "<br><b>Request Parameters</b>" + requestParam
                             + "<br><b>Stack Trace</b><pre><code>" + stackTrace + "</code></pre>";
        
        String subject = String.format(EmailType.ADMIN_SYSTEM_ERROR.getSubject(),
                                       Config.getAppVersion(), error.getMessage());
        
        verifyEmail(email, Config.SUPPORT_EMAIL, subject, textInEmail);
    }
    
    private void verifyEmail(MimeMessage email, String recipient, String subject, String textInEmail)
            throws MessagingException, IOException {
        // check recipient
        assertEquals(recipient, email.getAllRecipients()[0].toString());
        
        // check subject
        assertEquals(subject, email.getSubject());
        
        // check sender
        assertEquals(from, email.getFrom()[0].toString());
        
        //check replyTo
        assertEquals(replyTo, email.getReplyTo()[0].toString());
        
        String emailBody = email.getContent().toString();
        
        // check email body for expected content
        AssertHelper.assertContainsRegex(textInEmail, emailBody);
        
        // check email body for no left placeholders
        assertFalse(emailBody.contains("${"));
    }
    
    @AfterClass
    public void classTearDown() {
        printTestClassFooter();
    }
    
}
