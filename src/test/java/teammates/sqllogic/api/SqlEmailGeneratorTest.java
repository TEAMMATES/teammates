package teammates.sqllogic.api;

import java.io.IOException;

import org.mockito.Mockito;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.common.datatransfer.InstructorPermissionRole;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.util.Config;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.common.util.Templates;
import teammates.sqllogic.core.EmailTemplatesLogic;
import teammates.sqllogic.core.UsersLogic;
import teammates.storage.sqlapi.EmailTemplatesDb;
import teammates.storage.sqlapi.UsersDb;
import teammates.storage.sqlentity.Account;
import teammates.storage.sqlentity.AccountRequest;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.EmailTemplate;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.test.BaseTestCase;
import teammates.test.EmailChecker;
import teammates.ui.webapi.ConfigurableEmailTemplate;

/**
 * SUT: {@link SqlEmailGenerator}.
 */
public class SqlEmailGeneratorTest extends BaseTestCase {
    private final SqlEmailGenerator sqlEmailGenerator = SqlEmailGenerator.inst();
    private EmailTemplatesDb mockEmailTemplatesDb;
    private UsersDb mockUsersDb;

    @BeforeMethod
    void setUpLogicDependencies() {
        mockEmailTemplatesDb = Mockito.mock(EmailTemplatesDb.class);
        EmailTemplatesLogic.inst().initLogicDependencies(mockEmailTemplatesDb);

        mockUsersDb = Mockito.mock(UsersDb.class);
        Mockito.when(mockUsersDb.getInstructorsForCourse(Mockito.anyString()))
                .thenReturn(new java.util.ArrayList<>());
        UsersLogic.inst().initLogicDependencies(mockUsersDb, null, null, null, null);
    }

    @Test
    void testGenerateNewAccountRequestAdminAlertEmail_withComments_generatesSuccessfully() throws IOException {
        AccountRequest accountRequest = new AccountRequest("chosen-one@jedi.org", "Anakin Skywalker", "Jedi Order",
                AccountRequestStatus.PENDING,
                "I don't like sand. It's coarse and rough and irritating... and it gets everywhere.");
        EmailWrapper email = sqlEmailGenerator.generateNewAccountRequestAdminAlertEmail(accountRequest);
        verifyEmail(email, Config.SUPPORT_EMAIL, EmailType.NEW_ACCOUNT_REQUEST_ADMIN_ALERT,
                "TEAMMATES (Action Needed): New Account Request Received",
                "/adminNewAccountRequestAlertEmailWithComments.html");
    }

    @Test
    void testGenerateNewAccountRequestAdminAlertEmail_withNoComments_generatesSuccessfully() throws IOException {
        AccountRequest accountRequest = new AccountRequest("maul@sith.org", "Maul", "Sith Order",
                AccountRequestStatus.PENDING, null);
        EmailWrapper email = sqlEmailGenerator.generateNewAccountRequestAdminAlertEmail(accountRequest);
        verifyEmail(email, Config.SUPPORT_EMAIL, EmailType.NEW_ACCOUNT_REQUEST_ADMIN_ALERT,
                "TEAMMATES (Action Needed): New Account Request Received",
                "/adminNewAccountRequestAlertEmailWithNoComments.html");
    }

    @Test
    void testGenerateNewAccountRequestAcknowledgementEmail_withComments_generatesSuccessfully() throws IOException {
        AccountRequest accountRequest = new AccountRequest("darth-vader@sith.org", "Darth Vader", "Sith Order",
                AccountRequestStatus.PENDING,
                "I Am Your Father");
        EmailWrapper email = sqlEmailGenerator.generateNewAccountRequestAcknowledgementEmail(accountRequest);
        verifyEmail(email, "darth-vader@sith.org", EmailType.NEW_ACCOUNT_REQUEST_ACKNOWLEDGEMENT,
                "TEAMMATES: Acknowledgement of Instructor Account Request",
                "/instructorNewAccountRequestAcknowledgementEmailWithComments.html");
    }

    @Test
    void testGenerateNewAccountRequestAcknowledgementEmail_withNoComments_generatesSuccessfully() throws IOException {
        AccountRequest accountRequest = new AccountRequest("maul@sith.org", "Maul", "Sith Order",
                AccountRequestStatus.PENDING, null);
        EmailWrapper email = sqlEmailGenerator.generateNewAccountRequestAcknowledgementEmail(accountRequest);
        verifyEmail(email, "maul@sith.org", EmailType.NEW_ACCOUNT_REQUEST_ACKNOWLEDGEMENT,
                "TEAMMATES: Acknowledgement of Instructor Account Request",
                "/instructorNewAccountRequestAcknowledgementEmailWithNoComments.html");
    }

    @Test
    void testGenerateNewInstructorAccountJoinEmail_noDbTemplate_usesConfigurableEmailTemplateDefaults() {
        Mockito.when(mockEmailTemplatesDb.getEmailTemplate("NEW_INSTRUCTOR_ACCOUNT_WELCOME")).thenReturn(null);

        EmailWrapper email = sqlEmailGenerator.generateNewInstructorAccountJoinEmail(
                "luke@jedi.org", "Luke Skywalker", "https://teammates.example.com/join?key=abc");

        String expectedSubject = Templates.populateTemplate(
                ConfigurableEmailTemplate.NEW_INSTRUCTOR_ACCOUNT_WELCOME.getDefaultSubject(),
                "${userName}", "Luke Skywalker",
                "${joinUrl}", "https://teammates.example.com/join?key=abc");

        assertEquals("luke@jedi.org", email.getRecipient());
        assertEquals(EmailType.NEW_INSTRUCTOR_ACCOUNT, email.getType());
        assertEquals(expectedSubject, email.getSubject());
        assertEquals(Config.SUPPORT_EMAIL, email.getBcc());
        assertFalse(email.getContent().contains("${"));
    }

    @Test
    void testGenerateNewInstructorAccountJoinEmail_withDbTemplate_usesCustomSubjectAndBody() {
        EmailTemplate customTemplate = new EmailTemplate(
                "NEW_INSTRUCTOR_ACCOUNT_WELCOME",
                "Welcome ${userName} — your custom subject",
                "<p>Join here: <a href=\"${joinUrl}\">${joinUrl}</a></p>");
        Mockito.when(mockEmailTemplatesDb.getEmailTemplate("NEW_INSTRUCTOR_ACCOUNT_WELCOME"))
                .thenReturn(customTemplate);

        EmailWrapper email = sqlEmailGenerator.generateNewInstructorAccountJoinEmail(
                "leia@rebellion.org", "Leia Organa", "https://teammates.example.com/join?key=xyz");

        assertEquals("leia@rebellion.org", email.getRecipient());
        assertEquals(EmailType.NEW_INSTRUCTOR_ACCOUNT, email.getType());
        assertEquals("Welcome Leia Organa — your custom subject", email.getSubject());
        assertEquals(Config.SUPPORT_EMAIL, email.getBcc());
        assertFalse(email.getContent().contains("${"));
    }

    @Test
    void testGenerateNewAccountRequestAcknowledgementEmail_noDbTemplate_usesConfigurableEmailTemplateDefaults() {
        Mockito.when(mockEmailTemplatesDb.getEmailTemplate("NEW_ACCOUNT_REQUEST_ACKNOWLEDGEMENT")).thenReturn(null);

        AccountRequest accountRequest = new AccountRequest(
                "han@rebellion.org", "Han Solo", "Rebel Alliance",
                AccountRequestStatus.PENDING, "I know.");

        EmailWrapper email = sqlEmailGenerator.generateNewAccountRequestAcknowledgementEmail(accountRequest);

        String expectedSubject = ConfigurableEmailTemplate.NEW_ACCOUNT_REQUEST_ACKNOWLEDGEMENT.getDefaultSubject();

        assertEquals("han@rebellion.org", email.getRecipient());
        assertEquals(EmailType.NEW_ACCOUNT_REQUEST_ACKNOWLEDGEMENT, email.getType());
        assertEquals(expectedSubject, email.getSubject());
        assertFalse(email.getContent().contains("${"));
    }

    @Test
    void testGenerateNewAccountRequestAcknowledgementEmail_withDbTemplate_usesCustomSubjectAndBody() {
        EmailTemplate customTemplate = new EmailTemplate(
                "NEW_ACCOUNT_REQUEST_ACKNOWLEDGEMENT",
                "Custom ack subject",
                "<p>Hello ${name}, we received your request from ${institute}.</p>");
        Mockito.when(mockEmailTemplatesDb.getEmailTemplate("NEW_ACCOUNT_REQUEST_ACKNOWLEDGEMENT"))
                .thenReturn(customTemplate);

        AccountRequest accountRequest = new AccountRequest(
                "lando@cloudcity.org", "Lando Calrissian", "Cloud City",
                AccountRequestStatus.PENDING, null);

        EmailWrapper email = sqlEmailGenerator.generateNewAccountRequestAcknowledgementEmail(accountRequest);

        assertEquals("lando@cloudcity.org", email.getRecipient());
        assertEquals(EmailType.NEW_ACCOUNT_REQUEST_ACKNOWLEDGEMENT, email.getType());
        assertEquals("Custom ack subject", email.getSubject());
        assertFalse(email.getContent().contains("${"));
    }

    @Test
    void testGenerateStudentCourseJoinEmail_noDbTemplate_usesConfigurableEmailTemplateDefaults() {
        Mockito.when(mockEmailTemplatesDb.getEmailTemplate("STUDENT_COURSE_JOIN")).thenReturn(null);

        Course course = new Course("CS1101S", "Programming Methodology", "UTC", "NUS");
        Student student = new Student(course, "Rey Skywalker", "rey@resistance.org", "");

        EmailWrapper email = sqlEmailGenerator.generateStudentCourseJoinEmail(course, student);

        String expectedSubject = Templates.populateTemplate(
                ConfigurableEmailTemplate.STUDENT_COURSE_JOIN.getDefaultSubject(),
                "${courseName}", "Programming Methodology",
                "${courseId}", "CS1101S");

        assertEquals("rey@resistance.org", email.getRecipient());
        assertEquals(EmailType.STUDENT_COURSE_JOIN, email.getType());
        assertEquals(expectedSubject, email.getSubject());
        assertFalse(email.getContent().contains("${joinFragment}"));
        assertFalse(email.getContent().contains("${courseId}"));
    }

    @Test
    void testGenerateStudentCourseJoinEmail_withDbTemplate_usesCustomSubjectAndBody() {
        EmailTemplate customTemplate = new EmailTemplate(
                "STUDENT_COURSE_JOIN",
                "Join ${courseName} [${courseId}] — custom",
                "<p>Hi ${userName}, click <a href=\"${joinUrl}\">${joinUrl}</a> to join ${courseName}.</p>");
        Mockito.when(mockEmailTemplatesDb.getEmailTemplate("STUDENT_COURSE_JOIN"))
                .thenReturn(customTemplate);

        Course course = new Course("CS2103T", "Software Engineering", "UTC", "NUS");
        Student student = new Student(course, "Finn", "finn@resistance.org", "");

        EmailWrapper email = sqlEmailGenerator.generateStudentCourseJoinEmail(course, student);

        assertEquals("finn@resistance.org", email.getRecipient());
        assertEquals(EmailType.STUDENT_COURSE_JOIN, email.getType());
        assertEquals("Join Software Engineering [CS2103T] — custom", email.getSubject());
        assertFalse(email.getContent().contains("${"));
    }

    @Test
    void testGenerateInstructorCourseJoinEmail_noDbTemplate_usesConfigurableEmailTemplateDefaults() {
        Mockito.when(mockEmailTemplatesDb.getEmailTemplate("INSTRUCTOR_COURSE_JOIN")).thenReturn(null);

        Course course = new Course("CS3230", "Design and Analysis of Algorithms", "UTC", "NUS");
        Instructor instructor = new Instructor(course, "Poe Dameron", "poe@resistance.org",
                true, "Poe", InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
                new InstructorPrivileges(InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER.getRoleName()));
        Account inviter = new Account("bb8", "BB-8", "bb8@resistance.org");

        EmailWrapper email = sqlEmailGenerator.generateInstructorCourseJoinEmail(inviter, instructor, course);

        String expectedSubject = Templates.populateTemplate(
                ConfigurableEmailTemplate.INSTRUCTOR_COURSE_JOIN.getDefaultSubject(),
                "${courseName}", "Design and Analysis of Algorithms",
                "${courseId}", "CS3230");

        assertEquals("poe@resistance.org", email.getRecipient());
        assertEquals(EmailType.INSTRUCTOR_COURSE_JOIN, email.getType());
        assertEquals(expectedSubject, email.getSubject());
        assertFalse(email.getContent().contains("${joinFragment}"));
        assertFalse(email.getContent().contains("${courseId}"));
    }

    @Test
    void testGenerateInstructorCourseJoinEmail_withDbTemplate_usesCustomSubjectAndBody() {
        EmailTemplate customTemplate = new EmailTemplate(
                "INSTRUCTOR_COURSE_JOIN",
                "You're invited to ${courseName} [${courseId}]",
                "<p>Hi ${userName}, ${inviterName} (${inviterEmail}) invites you. "
                        + "<a href=\"${joinUrl}\">${joinUrl}</a></p>");
        Mockito.when(mockEmailTemplatesDb.getEmailTemplate("INSTRUCTOR_COURSE_JOIN"))
                .thenReturn(customTemplate);

        Course course = new Course("CS4215", "Programming Language Concepts", "UTC", "NUS");
        Instructor instructor = new Instructor(course, "Rose Tico", "rose@resistance.org",
                true, "Rose", InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
                new InstructorPrivileges(InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER.getRoleName()));
        Account inviter = new Account("generalholdo", "General Holdo", "holdo@resistance.org");

        EmailWrapper email = sqlEmailGenerator.generateInstructorCourseJoinEmail(inviter, instructor, course);

        assertEquals("rose@resistance.org", email.getRecipient());
        assertEquals(EmailType.INSTRUCTOR_COURSE_JOIN, email.getType());
        assertEquals("You're invited to Programming Language Concepts [CS4215]", email.getSubject());
        assertFalse(email.getContent().contains("${"));
    }

    private void verifyEmail(EmailWrapper email, String expectedRecipientEmailAddress, EmailType expectedEmailType,
            String expectedSubject, String expectedEmailContentFilePathname) throws IOException {
        assertEquals(expectedRecipientEmailAddress, email.getRecipient());
        assertEquals(Config.EMAIL_SENDEREMAIL, email.getSenderEmail());
        assertEquals(Config.EMAIL_SENDERNAME, email.getSenderName());
        assertEquals(Config.EMAIL_REPLYTO, email.getReplyTo());
        assertEquals(expectedEmailType, email.getType());
        assertEquals(expectedSubject, email.getSubject());
        String emailContent = email.getContent();
        EmailChecker.verifyEmailContent(emailContent, expectedEmailContentFilePathname);
        verifyEmailContentHasNoPlaceholders(emailContent);
    }

    private void verifyEmail(EmailWrapper email, String expectedRecipientEmailAddress, EmailType expectedEmailType,
            String expectedSubject, String expectedBcc, String expectedEmailContentFilePathname) throws IOException {
        assertEquals(expectedRecipientEmailAddress, email.getRecipient());
        assertEquals(Config.EMAIL_SENDEREMAIL, email.getSenderEmail());
        assertEquals(Config.EMAIL_SENDERNAME, email.getSenderName());
        assertEquals(Config.EMAIL_REPLYTO, email.getReplyTo());
        assertEquals(expectedEmailType, email.getType());
        assertEquals(expectedSubject, email.getSubject());
        assertEquals(expectedBcc, email.getBcc());
        String emailContent = email.getContent();
        EmailChecker.verifyEmailContent(emailContent, expectedEmailContentFilePathname);
        verifyEmailContentHasNoPlaceholders(emailContent);
    }

    private void verifyEmailContentHasNoPlaceholders(String emailContent) {
        assertFalse(emailContent.contains("${"));
    }
}
