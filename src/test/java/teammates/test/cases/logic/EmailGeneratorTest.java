package teammates.test.cases.logic;

import java.io.IOException;
import java.util.List;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.UserType;
import teammates.common.util.Config;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.logic.core.CoursesLogic;
import teammates.logic.core.EmailGenerator;
import teammates.logic.core.FeedbackSessionsLogic;
import teammates.logic.core.InstructorsLogic;
import teammates.logic.core.StudentsLogic;
import teammates.test.cases.BaseComponentTestCase;
import teammates.test.driver.EmailChecker;

/**
 * SUT: {@link EmailGenerator}
 */
public class EmailGeneratorTest extends BaseComponentTestCase {
    
    private static final CoursesLogic coursesLogic = CoursesLogic.inst();
    private static final FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
    private static final InstructorsLogic instructorsLogic = InstructorsLogic.inst();
    private static final StudentsLogic studentsLogic = StudentsLogic.inst();
    
    /** indicates if the test-run is to use GodMode */
    private static boolean isGodModeEnabled;
    
    @BeforeClass
    public void classSetUp() throws Exception {
        printTestClassHeader();
        removeAndRestoreTypicalDataInDatastore();
        if (isGodModeEnabled) {
            System.setProperty("godmode", "true");
        }
    }
    
    @Test
    public void testGenerateFeedbackSessionEmails() throws IOException {
        FeedbackSessionAttributes session = fsLogic.getFeedbackSession("First feedback session", "idOfTypicalCourse1");
        
        CourseAttributes course = coursesLogic.getCourse(session.getCourseId());
        
        List<StudentAttributes> students = studentsLogic.getStudentsForCourse(session.getCourseId());
        List<InstructorAttributes> instructors = instructorsLogic.getInstructorsForCourse(session.getCourseId());
        
        StudentAttributes student1 = studentsLogic.getStudentForEmail(course.getId(), "student1InCourse1@gmail.tmt");
        
        InstructorAttributes instructor1 =
                instructorsLogic.getInstructorForEmail(course.getId(), "instructor1@course1.tmt");
        
        ______TS("feedback session opening emails");
        
        List<EmailWrapper> emails = new EmailGenerator().generateFeedbackSessionOpeningEmails(session);
        assertEquals(10, emails.size());
        
        String subject = String.format(EmailType.FEEDBACK_OPENING.getSubject(),
                                       course.getName(), session.getFeedbackSessionName());
        
        boolean hasStudent1ReceivedEmail = false;
        boolean hasInstructor1ReceivedEmail = false;
        for (EmailWrapper email : emails) {
            if (email.getRecipient().equals(student1.email)) {
                verifyEmail(email, student1.email, subject, "/sessionOpeningEmailForStudent.html");
                hasStudent1ReceivedEmail = true;
            } else if (email.getRecipient().equals(instructor1.email)) {
                verifyEmail(email, instructor1.email, subject, "/sessionOpeningEmailForInstructor.html");
                hasInstructor1ReceivedEmail = true;
            }
        }
        assertTrue(hasStudent1ReceivedEmail && hasInstructor1ReceivedEmail);
        
        ______TS("feedback session reminders");
        
        emails = new EmailGenerator().generateFeedbackSessionReminderEmails(session, students, instructors, instructors);
        assertEquals(15, emails.size());
        
        subject = String.format(EmailType.FEEDBACK_SESSION_REMINDER.getSubject(),
                                course.getName(), session.getFeedbackSessionName());
        
        hasStudent1ReceivedEmail = false;
        hasInstructor1ReceivedEmail = false;
        for (EmailWrapper email : emails) {
            if (email.getRecipient().equals(student1.email)) {
                verifyEmail(email, student1.email, subject, "/sessionReminderEmailForStudent.html");
                hasStudent1ReceivedEmail = true;
            } else if (email.getRecipient().equals(instructor1.email)
                       && email.getContent().contains("The email below has been sent to students of course:")) {
                verifyEmail(email, instructor1.email, subject, "/sessionReminderEmailForInstructor.html");
                hasInstructor1ReceivedEmail = true;
            }
        }
        assertTrue(hasStudent1ReceivedEmail && hasInstructor1ReceivedEmail);
        
        ______TS("feedback session closing alerts");
        
        emails = new EmailGenerator().generateFeedbackSessionClosingEmails(session);
        assertEquals(8, emails.size());
        
        subject = String.format(EmailType.FEEDBACK_CLOSING.getSubject(),
                                course.getName(), session.getFeedbackSessionName());
        
        // student1 has completed the feedback session and closing alert is only sent for those who are
        // yet to complete, so we resort to student5
        StudentAttributes student5 = studentsLogic.getStudentForEmail(course.getId(), "student5InCourse1@gmail.tmt");
        
        hasStudent1ReceivedEmail = false;
        boolean hasStudent5ReceivedEmail = false;
        hasInstructor1ReceivedEmail = false;
        for (EmailWrapper email : emails) {
            if (email.getRecipient().equals(student5.email)) {
                verifyEmail(email, student5.email, subject, "/sessionClosingEmailForStudent.html");
                hasStudent5ReceivedEmail = true;
            } else if (email.getRecipient().equals(instructor1.email)) {
                verifyEmail(email, instructor1.email, subject, "/sessionClosingEmailForInstructor.html");
                hasInstructor1ReceivedEmail = true;
            }
        }
        assertTrue(!hasStudent1ReceivedEmail && hasStudent5ReceivedEmail && hasInstructor1ReceivedEmail);
        
        ______TS("feedback session published alerts");
        
        emails = new EmailGenerator().generateFeedbackSessionPublishedEmails(session);
        assertEquals(10, emails.size());
        
        subject = String.format(EmailType.FEEDBACK_PUBLISHED.getSubject(),
                                course.getName(), session.getFeedbackSessionName());
        
        hasStudent1ReceivedEmail = false;
        hasInstructor1ReceivedEmail = false;
        for (EmailWrapper email : emails) {
            if (email.getRecipient().equals(student1.email)) {
                verifyEmail(email, student1.email, subject, "/sessionPublishedEmailForStudent.html");
                hasStudent1ReceivedEmail = true;
            } else if (email.getRecipient().equals(instructor1.email)) {
                verifyEmail(email, instructor1.email, subject, "/sessionPublishedEmailForInstructor.html");
                hasInstructor1ReceivedEmail = true;
            }
        }
        assertTrue(hasStudent1ReceivedEmail && hasInstructor1ReceivedEmail);
        
        ______TS("no email alerts sent for sessions not answerable/viewable for students");
        
        FeedbackSessionAttributes privateSession =
                fsLogic.getFeedbackSession("Private feedback session", "idOfTypicalCourse2");
        
        emails = new EmailGenerator().generateFeedbackSessionOpeningEmails(privateSession);
        assertTrue(emails.isEmpty());
        
        emails = new EmailGenerator().generateFeedbackSessionClosingEmails(privateSession);
        assertTrue(emails.isEmpty());
        
        emails = new EmailGenerator().generateFeedbackSessionPublishedEmails(privateSession);
        assertTrue(emails.isEmpty());
        
    }
    
    @Test
    public void testGenerateStudentCourseJoinEmail() throws IOException {
        
        CourseAttributes course = new CourseAttributes("course-id", "Course Name");
        
        StudentAttributes student = new StudentAttributes();
        student.name = "Student Name";
        student.key = "skxxxxxxxxxks";
        student.email = "student@email.tmt";
        
        EmailWrapper email = new EmailGenerator().generateStudentCourseJoinEmail(course, student);
        String subject = String.format(EmailType.STUDENT_COURSE_JOIN.getSubject(), course.getName(), course.getId());
        
        verifyEmail(email, student.email, subject, "/studentCourseJoinEmail.html");
    }
    
    @Test
    public void testSystemCrashReportEmailContent() throws IOException {
        
        AssertionError error = new AssertionError("invalid parameter");
        String requestMethod = "GET";
        String requestUserAgent = "user-agent";
        String requestPath = "/page/studentHome";
        String requestUrl = "/page/studentHome/";
        String requestParam = "{}";
        UserType userType = new UserType("Not logged in");
        
        EmailWrapper email =
                new EmailGenerator().generateSystemErrorEmail(requestMethod, requestUserAgent, requestPath,
                                                              requestUrl, requestParam, userType, error);
        
        // The stack trace is different depending on the environment in which the test is run at.
        // As a workaround, after the last common line, change all the stack trace to "..."
        String lastCommonLineRegex =
                "(?s)(at org\\.testng\\.TestRunner\\.run\\(TestRunner\\.java:617\\)\\s*)at.*?(\\s*</code>)";
        String modifiedContent = email.getContent().replaceAll(lastCommonLineRegex, "$1...$2");
        email.setContent(modifiedContent);
        
        String subject = String.format(EmailType.ADMIN_SYSTEM_ERROR.getSubject(),
                                       Config.getAppVersion(), error.getMessage());
        
        verifyEmail(email, Config.SUPPORT_EMAIL, subject, "/systemCrashReportEmail.html");
    }
    
    private void verifyEmail(EmailWrapper email, String recipient, String subject, String emailContentFilePath)
            throws IOException {
        // check recipient
        assertEquals(recipient, email.getRecipient());
        
        // check subject
        assertEquals(subject, email.getSubject());
        
        // check sender name
        assertEquals(Config.EMAIL_SENDERNAME, email.getSenderName());
        
        // check sender email
        assertEquals(Config.EMAIL_SENDEREMAIL, email.getSenderEmail());
        
        // check reply to address
        assertEquals(Config.EMAIL_REPLYTO, email.getReplyTo());
        
        String emailContent = email.getContent();
        
        // check email body for expected content
        EmailChecker.verifyEmailContent(emailContent, emailContentFilePath);
        
        // check email body for no left placeholders
        assertFalse(emailContent.contains("${"));
    }
    
    @AfterClass
    public void classTearDown() {
        printTestClassFooter();
    }
    
}
