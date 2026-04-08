package teammates.sqllogic.api;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

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
import teammates.sqllogic.core.CoursesLogic;
import teammates.sqllogic.core.DeadlineExtensionsLogic;
import teammates.sqllogic.core.EmailTemplatesLogic;
import teammates.sqllogic.core.FeedbackSessionsLogic;
import teammates.sqllogic.core.UsersLogic;
import teammates.storage.sqlapi.CoursesDb;
import teammates.storage.sqlapi.DeadlineExtensionsDb;
import teammates.storage.sqlapi.EmailTemplatesDb;
import teammates.storage.sqlapi.FeedbackSessionsDb;
import teammates.storage.sqlapi.UsersDb;
import teammates.storage.sqlentity.Account;
import teammates.storage.sqlentity.AccountRequest;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.EmailTemplate;
import teammates.storage.sqlentity.FeedbackSession;
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
    private DeadlineExtensionsDb mockDeadlineExtensionsDb;
    private CoursesDb mockCoursesDb;
    private FeedbackSessionsDb mockFeedbackSessionsDb;

    @BeforeMethod
    void setUpLogicDependencies() {
        mockEmailTemplatesDb = Mockito.mock(EmailTemplatesDb.class);
        EmailTemplatesLogic.inst().initLogicDependencies(mockEmailTemplatesDb);

        mockUsersDb = Mockito.mock(UsersDb.class);
        Mockito.when(mockUsersDb.getInstructorsForCourse(Mockito.anyString()))
                .thenReturn(new java.util.ArrayList<>());
        UsersLogic.inst().initLogicDependencies(mockUsersDb, null, null, null, null);

        mockDeadlineExtensionsDb = Mockito.mock(DeadlineExtensionsDb.class);
        DeadlineExtensionsLogic.inst().initLogicDependencies(mockDeadlineExtensionsDb, null);

        mockCoursesDb = Mockito.mock(CoursesDb.class);
        CoursesLogic.inst().initLogicDependencies(mockCoursesDb, null, null, null);

        mockFeedbackSessionsDb = Mockito.mock(FeedbackSessionsDb.class);
        FeedbackSessionsLogic.inst().initLogicDependencies(mockFeedbackSessionsDb, null, null, null, null);
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

    @Test
    void testGenerateLoginEmail_noDbTemplate_usesConfigurableEmailTemplateDefaults() {
        Mockito.when(mockEmailTemplatesDb.getEmailTemplate("LOGIN")).thenReturn(null);

        EmailWrapper email = sqlEmailGenerator.generateLoginEmail("jyn@rebel.org", "https://auth.example.com/login?token=xyz");

        assertEquals("jyn@rebel.org", email.getRecipient());
        assertEquals(EmailType.LOGIN, email.getType());
        assertEquals(ConfigurableEmailTemplate.LOGIN.getDefaultSubject(), email.getSubject());
        assertEquals(Config.SUPPORT_EMAIL, email.getBcc());
        assertFalse(email.getContent().contains("${"));
    }

    @Test
    void testGenerateLoginEmail_withDbTemplate_usesCustomSubjectAndBody() {
        EmailTemplate customTemplate = new EmailTemplate(
                "LOGIN",
                "Custom login subject",
                "<p>Click <a href=\"${loginLink}\">${loginLink}</a> to log in.</p>");
        Mockito.when(mockEmailTemplatesDb.getEmailTemplate("LOGIN")).thenReturn(customTemplate);

        EmailWrapper email = sqlEmailGenerator.generateLoginEmail("chirrut@jedha.org", "https://auth.example.com/login?token=abc");

        assertEquals("chirrut@jedha.org", email.getRecipient());
        assertEquals(EmailType.LOGIN, email.getType());
        assertEquals("Custom login subject", email.getSubject());
        assertEquals(Config.SUPPORT_EMAIL, email.getBcc());
        assertFalse(email.getContent().contains("${"));
    }

    @Test
    void testGenerateUserCourseRegisteredEmail_noDbTemplate_usesConfigurableEmailTemplateDefaults() {
        Mockito.when(mockEmailTemplatesDb.getEmailTemplate("USER_COURSE_REGISTER")).thenReturn(null);

        Course course = new Course("CS2101", "Effective Communication", "UTC", "NUS");
        EmailWrapper email = sqlEmailGenerator.generateUserCourseRegisteredEmail(
                "Bodhi Rook", "bodhi@rogue-one.org", "bodhirook", false, course);

        String expectedSubject = Templates.populateTemplate(
                ConfigurableEmailTemplate.USER_COURSE_REGISTER.getDefaultSubject(),
                "${courseName}", "Effective Communication",
                "${courseId}", "CS2101");

        assertEquals("bodhi@rogue-one.org", email.getRecipient());
        assertEquals(EmailType.USER_COURSE_REGISTER, email.getType());
        assertEquals(expectedSubject, email.getSubject());
        assertFalse(email.getContent().contains("${"));
    }

    @Test
    void testGenerateUserCourseRegisteredEmail_withDbTemplate_usesCustomSubjectAndBody() {
        EmailTemplate customTemplate = new EmailTemplate(
                "USER_COURSE_REGISTER",
                "You are registered for ${courseName} [${courseId}]",
                "<p>Hello ${userName}, you joined as ${userType} in ${courseName}. GoogleId: ${googleId}</p>");
        Mockito.when(mockEmailTemplatesDb.getEmailTemplate("USER_COURSE_REGISTER")).thenReturn(customTemplate);

        Course course = new Course("CS3281", "Thematic Systems Projects I", "UTC", "NUS");
        EmailWrapper email = sqlEmailGenerator.generateUserCourseRegisteredEmail(
                "Cassian Andor", "cassian@rogue-one.org", "cassianandor", false, course);

        assertEquals("cassian@rogue-one.org", email.getRecipient());
        assertEquals(EmailType.USER_COURSE_REGISTER, email.getType());
        assertEquals("You are registered for Thematic Systems Projects I [CS3281]", email.getSubject());
        assertFalse(email.getContent().contains("${"));
    }

    @Test
    void testGenerateStudentCourseRejoinEmail_noDbTemplate_usesConfigurableEmailTemplateDefaults() {
        Mockito.when(mockEmailTemplatesDb.getEmailTemplate("STUDENT_COURSE_REJOIN_AFTER_GOOGLE_ID_RESET"))
                .thenReturn(null);

        Course course = new Course("CS1010", "Programming Methodology", "UTC", "NUS");
        Student student = new Student(course, "K-2SO", "k2so@rogue-one.org", "");

        EmailWrapper email = sqlEmailGenerator.generateStudentCourseRejoinEmailAfterGoogleIdReset(course, student);

        String expectedSubject = Templates.populateTemplate(
                ConfigurableEmailTemplate.STUDENT_COURSE_REJOIN_AFTER_GOOGLE_ID_RESET.getDefaultSubject(),
                "${courseName}", "Programming Methodology",
                "${courseId}", "CS1010");

        assertEquals("k2so@rogue-one.org", email.getRecipient());
        assertEquals(EmailType.STUDENT_COURSE_REJOIN_AFTER_GOOGLE_ID_RESET, email.getType());
        assertEquals(expectedSubject, email.getSubject());
        assertFalse(email.getContent().contains("${"));
    }

    @Test
    void testGenerateStudentCourseRejoinEmail_withDbTemplate_usesCustomSubjectAndBody() {
        EmailTemplate customTemplate = new EmailTemplate(
                "STUDENT_COURSE_REJOIN_AFTER_GOOGLE_ID_RESET",
                "Rejoin ${courseName} [${courseId}]",
                "<p>Hi ${userName}, click <a href=\"${joinUrl}\">${joinUrl}</a> to rejoin ${courseName}.</p>");
        Mockito.when(mockEmailTemplatesDb.getEmailTemplate("STUDENT_COURSE_REJOIN_AFTER_GOOGLE_ID_RESET"))
                .thenReturn(customTemplate);

        Course course = new Course("CS2030S", "Programming Methodology II", "UTC", "NUS");
        Student student = new Student(course, "Saw Gerrera", "saw@partisans.org", "");

        EmailWrapper email = sqlEmailGenerator.generateStudentCourseRejoinEmailAfterGoogleIdReset(course, student);

        assertEquals("saw@partisans.org", email.getRecipient());
        assertEquals(EmailType.STUDENT_COURSE_REJOIN_AFTER_GOOGLE_ID_RESET, email.getType());
        assertEquals("Rejoin Programming Methodology II [CS2030S]", email.getSubject());
        assertFalse(email.getContent().contains("${"));
    }

    @Test
    void testGenerateInstructorCourseRejoinEmail_noDbTemplate_usesConfigurableEmailTemplateDefaults() {
        Mockito.when(mockEmailTemplatesDb.getEmailTemplate("INSTRUCTOR_COURSE_REJOIN_AFTER_GOOGLE_ID_RESET"))
                .thenReturn(null);

        Course course = new Course("CS4231", "Parallel and Distributed Algorithms", "UTC", "NUS");
        Instructor instructor = new Instructor(course, "Baze Malbus", "baze@jedha.org",
                true, "Baze", InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
                new InstructorPrivileges(InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER.getRoleName()));

        EmailWrapper email = sqlEmailGenerator.generateInstructorCourseRejoinEmailAfterGoogleIdReset(instructor, course);

        String expectedSubject = Templates.populateTemplate(
                ConfigurableEmailTemplate.INSTRUCTOR_COURSE_REJOIN_AFTER_GOOGLE_ID_RESET.getDefaultSubject(),
                "${courseName}", "Parallel and Distributed Algorithms",
                "${courseId}", "CS4231");

        assertEquals("baze@jedha.org", email.getRecipient());
        assertEquals(EmailType.INSTRUCTOR_COURSE_REJOIN_AFTER_GOOGLE_ID_RESET, email.getType());
        assertEquals(expectedSubject, email.getSubject());
        assertFalse(email.getContent().contains("${"));
    }

    @Test
    void testGenerateInstructorCourseRejoinEmail_withDbTemplate_usesCustomSubjectAndBody() {
        EmailTemplate customTemplate = new EmailTemplate(
                "INSTRUCTOR_COURSE_REJOIN_AFTER_GOOGLE_ID_RESET",
                "Instructor rejoin ${courseName} [${courseId}]",
                "<p>Hi ${userName}, click <a href=\"${joinUrl}\">${joinUrl}</a> to rejoin ${courseName}.</p>");
        Mockito.when(mockEmailTemplatesDb.getEmailTemplate("INSTRUCTOR_COURSE_REJOIN_AFTER_GOOGLE_ID_RESET"))
                .thenReturn(customTemplate);

        Course course = new Course("CS5250", "Advanced Operating Systems", "UTC", "NUS");
        Instructor instructor = new Instructor(course, "Orson Krennic", "krennic@deathstar.org",
                true, "Krennic", InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
                new InstructorPrivileges(InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER.getRoleName()));

        EmailWrapper email = sqlEmailGenerator.generateInstructorCourseRejoinEmailAfterGoogleIdReset(instructor, course);

        assertEquals("krennic@deathstar.org", email.getRecipient());
        assertEquals(EmailType.INSTRUCTOR_COURSE_REJOIN_AFTER_GOOGLE_ID_RESET, email.getType());
        assertEquals("Instructor rejoin Advanced Operating Systems [CS5250]", email.getSubject());
        assertFalse(email.getContent().contains("${"));
    }

    @Test
    void testGenerateNewAccountRequestAdminAlertEmail_noDbTemplate_usesConfigurableEmailTemplateDefaults() {
        Mockito.when(mockEmailTemplatesDb.getEmailTemplate("NEW_ACCOUNT_REQUEST_ADMIN_ALERT")).thenReturn(null);

        AccountRequest accountRequest = new AccountRequest(
                "galen@eadu.org", "Galen Erso", "Eadu Research Station",
                AccountRequestStatus.PENDING, "I have a message for you.");

        EmailWrapper email = sqlEmailGenerator.generateNewAccountRequestAdminAlertEmail(accountRequest);

        assertEquals(Config.SUPPORT_EMAIL, email.getRecipient());
        assertEquals(EmailType.NEW_ACCOUNT_REQUEST_ADMIN_ALERT, email.getType());
        assertEquals(ConfigurableEmailTemplate.NEW_ACCOUNT_REQUEST_ADMIN_ALERT.getDefaultSubject(), email.getSubject());
        assertFalse(email.getContent().contains("${"));
    }

    @Test
    void testGenerateNewAccountRequestAdminAlertEmail_withDbTemplate_usesCustomSubjectAndBody() {
        EmailTemplate customTemplate = new EmailTemplate(
                "NEW_ACCOUNT_REQUEST_ADMIN_ALERT",
                "Custom admin alert subject",
                "<p>New request from ${name} at ${institute}. "
                        + "<a href=\"${adminAccountRequestsPageUrl}\">Review</a></p>");
        Mockito.when(mockEmailTemplatesDb.getEmailTemplate("NEW_ACCOUNT_REQUEST_ADMIN_ALERT"))
                .thenReturn(customTemplate);

        AccountRequest accountRequest = new AccountRequest(
                "jyn@jedha.org", "Jyn Erso", "Rebel Alliance",
                AccountRequestStatus.PENDING, null);

        EmailWrapper email = sqlEmailGenerator.generateNewAccountRequestAdminAlertEmail(accountRequest);

        assertEquals(Config.SUPPORT_EMAIL, email.getRecipient());
        assertEquals(EmailType.NEW_ACCOUNT_REQUEST_ADMIN_ALERT, email.getType());
        assertEquals("Custom admin alert subject", email.getSubject());
        assertFalse(email.getContent().contains("${"));
    }

    @Test
    void testGenerateFeedbackSessionPublishedEmails_noDbTemplate_usesConfigurableEmailTemplateDefaults() {
        Mockito.when(mockEmailTemplatesDb.getEmailTemplate("FEEDBACK_PUBLISHED")).thenReturn(null);

        Course course = new Course("CS2103T", "Software Engineering", "UTC", "NUS");
        FeedbackSession session = new FeedbackSession("Project Feedback", course, "prof@nus.edu.sg",
                "instructions", Instant.now().minusSeconds(7200), Instant.now().plusSeconds(3600),
                Instant.now().minusSeconds(3600), Instant.now().plusSeconds(7200),
                Duration.ofMinutes(15), true, false, true);
        Student student = new Student(course, "Wedge Antilles", "wedge@xwing.org", "");

        List<EmailWrapper> emails = sqlEmailGenerator.generateFeedbackSessionPublishedEmails(
                session, List.of(student), List.of(), List.of());

        assertEquals(1, emails.size());
        EmailWrapper email = emails.get(0);
        String expectedSubject = Templates.populateTemplate(
                ConfigurableEmailTemplate.FEEDBACK_PUBLISHED.getDefaultSubject(),
                "${courseName}", "Software Engineering",
                "${feedbackSessionName}", "Project Feedback");
        assertEquals("wedge@xwing.org", email.getRecipient());
        assertEquals(EmailType.FEEDBACK_PUBLISHED, email.getType());
        assertEquals(expectedSubject, email.getSubject());
        assertFalse(email.getContent().contains("${"));
    }

    @Test
    void testGenerateFeedbackSessionPublishedEmails_withDbTemplate_usesCustomSubjectAndBody() {
        EmailTemplate customTemplate = new EmailTemplate(
                "FEEDBACK_PUBLISHED",
                "Results for ${feedbackSessionName} in ${courseName} are out",
                "<p>Hi ${userName}, results for ${feedbackSessionName} are published. "
                        + "<a href=\"${reportUrl}\">View</a></p>");
        Mockito.when(mockEmailTemplatesDb.getEmailTemplate("FEEDBACK_PUBLISHED")).thenReturn(customTemplate);

        Course course = new Course("CS3230", "Design and Analysis of Algorithms", "UTC", "NUS");
        FeedbackSession session = new FeedbackSession("Final Feedback", course, "prof@nus.edu.sg",
                "instructions", Instant.now().minusSeconds(7200), Instant.now().plusSeconds(3600),
                Instant.now().minusSeconds(3600), Instant.now().plusSeconds(7200),
                Duration.ofMinutes(15), true, false, true);
        Student student = new Student(course, "Biggs Darklighter", "biggs@xwing.org", "");

        List<EmailWrapper> emails = sqlEmailGenerator.generateFeedbackSessionPublishedEmails(
                session, List.of(student), List.of(), List.of());

        assertEquals(1, emails.size());
        EmailWrapper email = emails.get(0);
        assertEquals("biggs@xwing.org", email.getRecipient());
        assertEquals(EmailType.FEEDBACK_PUBLISHED, email.getType());
        assertEquals("Results for Final Feedback in Design and Analysis of Algorithms are out", email.getSubject());
        assertFalse(email.getContent().contains("${"));
    }

    @Test
    void testGenerateFeedbackSessionUnpublishedEmails_noDbTemplate_usesConfigurableEmailTemplateDefaults() {
        Mockito.when(mockEmailTemplatesDb.getEmailTemplate("FEEDBACK_UNPUBLISHED")).thenReturn(null);

        Course course = new Course("CS4261", "Algorithmic Mechanism Design", "UTC", "NUS");
        FeedbackSession session = new FeedbackSession("Midterm Survey", course, "prof@nus.edu.sg",
                "instructions", Instant.now().minusSeconds(7200), Instant.now().plusSeconds(3600),
                Instant.now().minusSeconds(3600), Instant.now().plusSeconds(7200),
                Duration.ofMinutes(15), true, false, true);
        Student student = new Student(course, "Lando Calrissian", "lando@cloudcity.org", "");

        List<EmailWrapper> emails = sqlEmailGenerator.generateFeedbackSessionUnpublishedEmails(
                session, List.of(student), List.of(), List.of());

        assertEquals(1, emails.size());
        EmailWrapper email = emails.get(0);
        String expectedSubject = Templates.populateTemplate(
                ConfigurableEmailTemplate.FEEDBACK_UNPUBLISHED.getDefaultSubject(),
                "${courseName}", "Algorithmic Mechanism Design",
                "${feedbackSessionName}", "Midterm Survey");
        assertEquals("lando@cloudcity.org", email.getRecipient());
        assertEquals(EmailType.FEEDBACK_UNPUBLISHED, email.getType());
        assertEquals(expectedSubject, email.getSubject());
        assertFalse(email.getContent().contains("${"));
    }

    @Test
    void testGenerateFeedbackSessionUnpublishedEmails_withDbTemplate_usesCustomSubjectAndBody() {
        EmailTemplate customTemplate = new EmailTemplate(
                "FEEDBACK_UNPUBLISHED",
                "Results for ${feedbackSessionName} in ${courseName} have been unpublished",
                "<p>Hi ${userName}, results for ${feedbackSessionName} are no longer available.</p>");
        Mockito.when(mockEmailTemplatesDb.getEmailTemplate("FEEDBACK_UNPUBLISHED")).thenReturn(customTemplate);

        Course course = new Course("CS5330", "Pattern Recognition Systems", "UTC", "NUS");
        FeedbackSession session = new FeedbackSession("Peer Review", course, "prof@nus.edu.sg",
                "instructions", Instant.now().minusSeconds(7200), Instant.now().plusSeconds(3600),
                Instant.now().minusSeconds(3600), Instant.now().plusSeconds(7200),
                Duration.ofMinutes(15), true, false, true);
        Student student = new Student(course, "Nien Nunb", "nien@resistance.org", "");

        List<EmailWrapper> emails = sqlEmailGenerator.generateFeedbackSessionUnpublishedEmails(
                session, List.of(student), List.of(), List.of());

        assertEquals(1, emails.size());
        EmailWrapper email = emails.get(0);
        assertEquals("nien@resistance.org", email.getRecipient());
        assertEquals(EmailType.FEEDBACK_UNPUBLISHED, email.getType());
        assertEquals("Results for Peer Review in Pattern Recognition Systems have been unpublished", email.getSubject());
        assertFalse(email.getContent().contains("${"));
    }

    @Test
    void testGenerateFeedbackSessionOpenedEmails_noDbTemplate_usesConfigurableEmailTemplateDefaults() {
        Mockito.when(mockEmailTemplatesDb.getEmailTemplate("FEEDBACK_OPENED")).thenReturn(null);

        Course course = new Course("CS1231", "Discrete Structures", "UTC", "NUS");
        FeedbackSession session = new FeedbackSession("Quiz Feedback", course, "prof@nus.edu.sg",
                "instructions", Instant.now().minusSeconds(7200), Instant.now().plusSeconds(3600),
                Instant.now().minusSeconds(3600), Instant.now().plusSeconds(7200),
                Duration.ofMinutes(15), true, false, true);
        Student student = new Student(course, "Ello Asty", "ello@resistance.org", "");

        List<EmailWrapper> emails = sqlEmailGenerator.generateFeedbackSessionOpenedEmails(
                session, List.of(student), List.of(), List.of());

        assertEquals(1, emails.size());
        EmailWrapper email = emails.get(0);
        String expectedSubject = Templates.populateTemplate(
                ConfigurableEmailTemplate.FEEDBACK_OPENED.getDefaultSubject(),
                "${courseName}", "Discrete Structures",
                "${feedbackSessionName}", "Quiz Feedback");
        assertEquals("ello@resistance.org", email.getRecipient());
        assertEquals(EmailType.FEEDBACK_OPENED, email.getType());
        assertEquals(expectedSubject, email.getSubject());
        assertFalse(email.getContent().contains("${"));
    }

    @Test
    void testGenerateFeedbackSessionOpenedEmails_withDbTemplate_usesCustomSubjectAndBody() {
        EmailTemplate customTemplate = new EmailTemplate(
                "FEEDBACK_OPENED",
                "${feedbackSessionName} in ${courseName} is now open",
                "<p>Hi ${userName}, ${feedbackSessionName} is open. "
                        + "<a href=\"${submitUrl}\">Submit here</a></p>");
        Mockito.when(mockEmailTemplatesDb.getEmailTemplate("FEEDBACK_OPENED")).thenReturn(customTemplate);

        Course course = new Course("CS2040S", "Data Structures and Algorithms", "UTC", "NUS");
        FeedbackSession session = new FeedbackSession("Team Feedback", course, "prof@nus.edu.sg",
                "instructions", Instant.now().minusSeconds(7200), Instant.now().plusSeconds(3600),
                Instant.now().minusSeconds(3600), Instant.now().plusSeconds(7200),
                Duration.ofMinutes(15), true, false, true);
        Student student = new Student(course, "Snap Wexley", "snap@resistance.org", "");

        List<EmailWrapper> emails = sqlEmailGenerator.generateFeedbackSessionOpenedEmails(
                session, List.of(student), List.of(), List.of());

        assertEquals(1, emails.size());
        EmailWrapper email = emails.get(0);
        assertEquals("snap@resistance.org", email.getRecipient());
        assertEquals(EmailType.FEEDBACK_OPENED, email.getType());
        assertEquals("Team Feedback in Data Structures and Algorithms is now open", email.getSubject());
        assertFalse(email.getContent().contains("${"));
    }

    @Test
    void testGenerateFeedbackSessionReminderEmails_noDbTemplate_usesConfigurableDefaults() {
        Mockito.when(mockEmailTemplatesDb.getEmailTemplate("FEEDBACK_SESSION_REMINDER")).thenReturn(null);

        Course course = new Course("CS2103T", "Software Engineering", "UTC", "NUS");
        FeedbackSession session = new FeedbackSession("Sprint Review", course, "prof@nus.edu.sg",
                "instructions", Instant.now().minusSeconds(7200), Instant.now().plusSeconds(3600),
                Instant.now().minusSeconds(3600), Instant.now().plusSeconds(7200),
                Duration.ofMinutes(15), true, false, true);
        Student student = new Student(course, "Ahsoka Tano", "ahsoka@togruta.org", "");

        List<EmailWrapper> emails = sqlEmailGenerator.generateFeedbackSessionReminderEmails(
                session, List.of(student), List.of(), null);

        assertEquals(1, emails.size());
        EmailWrapper email = emails.get(0);
        String expectedSubject = Templates.populateTemplate(
                ConfigurableEmailTemplate.FEEDBACK_SESSION_REMINDER.getDefaultSubject(),
                "${courseName}", "Software Engineering",
                "${feedbackSessionName}", "Sprint Review");
        assertEquals("ahsoka@togruta.org", email.getRecipient());
        assertEquals(EmailType.FEEDBACK_SESSION_REMINDER, email.getType());
        assertEquals(expectedSubject, email.getSubject());
        assertFalse(email.getContent().contains("${"));
    }

    @Test
    void testGenerateFeedbackSessionReminderEmails_withDbTemplate_usesCustomSubjectAndBody() {
        EmailTemplate customTemplate = new EmailTemplate(
                "FEEDBACK_SESSION_REMINDER",
                "Reminder: ${feedbackSessionName} in ${courseName} is still open",
                "<p>Hi ${userName}, don't forget ${feedbackSessionName}. "
                        + "<a href=\"${submitUrl}\">${submitUrl}</a></p>"
                        + "${instructorPreamble}${additionalContactInformation}");
        Mockito.when(mockEmailTemplatesDb.getEmailTemplate("FEEDBACK_SESSION_REMINDER"))
                .thenReturn(customTemplate);

        Course course = new Course("CS3219", "Software Engineering Principles", "UTC", "NUS");
        FeedbackSession session = new FeedbackSession("Team Survey", course, "prof@nus.edu.sg",
                "instructions", Instant.now().minusSeconds(7200), Instant.now().plusSeconds(3600),
                Instant.now().minusSeconds(3600), Instant.now().plusSeconds(7200),
                Duration.ofMinutes(15), true, false, true);
        Student student = new Student(course, "Captain Rex", "rex@501st.org", "");

        List<EmailWrapper> emails = sqlEmailGenerator.generateFeedbackSessionReminderEmails(
                session, List.of(student), List.of(), null);

        assertEquals(1, emails.size());
        EmailWrapper email = emails.get(0);
        assertEquals("rex@501st.org", email.getRecipient());
        assertEquals(EmailType.FEEDBACK_SESSION_REMINDER, email.getType());
        assertEquals("Reminder: Team Survey in Software Engineering Principles is still open", email.getSubject());
        assertFalse(email.getContent().contains("${"));
    }

    @Test
    void testGenerateFeedbackSessionClosingSoonEmails_noDbTemplate_usesConfigurableDefaults() {
        Mockito.when(mockEmailTemplatesDb.getEmailTemplate("FEEDBACK_CLOSING_SOON")).thenReturn(null);

        Course course = new Course("CS4225", "Big Data Systems", "UTC", "NUS");
        FeedbackSession session = new FeedbackSession("Peer Feedback", course, "prof@nus.edu.sg",
                "instructions", Instant.now().minusSeconds(7200), Instant.now().plusSeconds(3600),
                Instant.now().minusSeconds(3600), Instant.now().plusSeconds(7200),
                Duration.ofMinutes(15), true, false, true);
        Student student = new Student(course, "Cad Bane", "bane@bounty.org", "");

        List<EmailWrapper> emails = sqlEmailGenerator.generateFeedbackSessionClosingSoonEmails(
                session, List.of(student), List.of(), List.of());

        assertEquals(1, emails.size());
        EmailWrapper email = emails.get(0);
        String expectedSubject = Templates.populateTemplate(
                ConfigurableEmailTemplate.FEEDBACK_CLOSING_SOON.getDefaultSubject(),
                "${courseName}", "Big Data Systems",
                "${feedbackSessionName}", "Peer Feedback");
        assertEquals("bane@bounty.org", email.getRecipient());
        assertEquals(EmailType.FEEDBACK_CLOSING_SOON, email.getType());
        assertEquals(expectedSubject, email.getSubject());
        assertFalse(email.getContent().contains("${"));
    }

    @Test
    void testGenerateFeedbackSessionClosingSoonEmails_withDbTemplate_usesCustomSubjectAndBody() {
        EmailTemplate customTemplate = new EmailTemplate(
                "FEEDBACK_CLOSING_SOON",
                "${feedbackSessionName} in ${courseName} is closing soon",
                "<p>Hi ${userName}, ${feedbackSessionName} closes soon. "
                        + "<a href=\"${submitUrl}\">${submitUrl}</a></p>"
                        + "${instructorPreamble}${additionalContactInformation}");
        Mockito.when(mockEmailTemplatesDb.getEmailTemplate("FEEDBACK_CLOSING_SOON"))
                .thenReturn(customTemplate);

        Course course = new Course("CS5228", "Knowledge Discovery", "UTC", "NUS");
        FeedbackSession session = new FeedbackSession("Exit Survey", course, "prof@nus.edu.sg",
                "instructions", Instant.now().minusSeconds(7200), Instant.now().plusSeconds(3600),
                Instant.now().minusSeconds(3600), Instant.now().plusSeconds(7200),
                Duration.ofMinutes(15), true, false, true);
        Student student = new Student(course, "Hondo Ohnaka", "hondo@pirates.org", "");

        List<EmailWrapper> emails = sqlEmailGenerator.generateFeedbackSessionClosingSoonEmails(
                session, List.of(student), List.of(), List.of());

        assertEquals(1, emails.size());
        EmailWrapper email = emails.get(0);
        assertEquals("hondo@pirates.org", email.getRecipient());
        assertEquals(EmailType.FEEDBACK_CLOSING_SOON, email.getType());
        assertEquals("Exit Survey in Knowledge Discovery is closing soon", email.getSubject());
        assertFalse(email.getContent().contains("${"));
    }

    @Test
    void testGenerateFeedbackSessionOpeningSoonEmails_noDbTemplate_usesConfigurableDefaults() {
        Mockito.when(mockEmailTemplatesDb.getEmailTemplate("FEEDBACK_OPENING_SOON")).thenReturn(null);

        Course course = new Course("CS6101", "Exploration of Computer Science Research", "UTC", "NUS");
        FeedbackSession session = new FeedbackSession("Research Feedback", course, "prof@nus.edu.sg",
                "instructions", Instant.now().plusSeconds(3600), Instant.now().plusSeconds(7200),
                Instant.now().minusSeconds(3600), Instant.now().plusSeconds(10800),
                Duration.ofMinutes(15), true, false, true);
        Instructor coOwner = new Instructor(course, "Mace Windu", "mace@jedi.org",
                true, "Mace", InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
                new InstructorPrivileges(InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER.getRoleName()));
        Account account = new Account("macewindu", "Mace Windu", "mace@jedi.org");
        coOwner.setAccount(account);

        List<EmailWrapper> emails = sqlEmailGenerator.generateFeedbackSessionOpeningSoonEmails(
                session, List.of(coOwner));

        assertEquals(1, emails.size());
        EmailWrapper email = emails.get(0);
        String expectedSubject = Templates.populateTemplate(
                ConfigurableEmailTemplate.FEEDBACK_OPENING_SOON.getDefaultSubject(),
                "${courseName}", "Exploration of Computer Science Research",
                "${feedbackSessionName}", "Research Feedback");
        assertEquals("mace@jedi.org", email.getRecipient());
        assertEquals(EmailType.FEEDBACK_OPENING_SOON, email.getType());
        assertEquals(expectedSubject, email.getSubject());
        assertFalse(email.getContent().contains("${"));
    }

    @Test
    void testGenerateFeedbackSessionOpeningSoonEmails_withDbTemplate_usesCustomSubjectAndBody() {
        EmailTemplate customTemplate = new EmailTemplate(
                "FEEDBACK_OPENING_SOON",
                "${feedbackSessionName} in ${courseName} opens soon",
                "<p>Hi ${userName}, ${feedbackSessionName} opens soon.</p>${additionalNotes}");
        Mockito.when(mockEmailTemplatesDb.getEmailTemplate("FEEDBACK_OPENING_SOON"))
                .thenReturn(customTemplate);

        Course course = new Course("CS6220", "Distributed Machine Learning", "UTC", "NUS");
        FeedbackSession session = new FeedbackSession("ML Survey", course, "prof@nus.edu.sg",
                "instructions", Instant.now().plusSeconds(3600), Instant.now().plusSeconds(7200),
                Instant.now().minusSeconds(3600), Instant.now().plusSeconds(10800),
                Duration.ofMinutes(15), true, false, true);
        Instructor coOwner = new Instructor(course, "Kit Fisto", "kit@jedi.org",
                true, "Kit", InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
                new InstructorPrivileges(InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER.getRoleName()));
        Account account = new Account("kitfisto", "Kit Fisto", "kit@jedi.org");
        coOwner.setAccount(account);

        List<EmailWrapper> emails = sqlEmailGenerator.generateFeedbackSessionOpeningSoonEmails(
                session, List.of(coOwner));

        assertEquals(1, emails.size());
        EmailWrapper email = emails.get(0);
        assertEquals("kit@jedi.org", email.getRecipient());
        assertEquals(EmailType.FEEDBACK_OPENING_SOON, email.getType());
        assertEquals("ML Survey in Distributed Machine Learning opens soon", email.getSubject());
        assertFalse(email.getContent().contains("${"));
    }

    @Test
    void testGenerateFeedbackSessionClosedEmails_noDbTemplate_usesConfigurableDefaults() {
        Mockito.when(mockEmailTemplatesDb.getEmailTemplate("FEEDBACK_CLOSED")).thenReturn(null);

        Course course = new Course("CS6285", "Semantic Networks", "UTC", "NUS");
        FeedbackSession session = new FeedbackSession("Final Survey", course, "prof@nus.edu.sg",
                "instructions", Instant.now().minusSeconds(7200), Instant.now().minusSeconds(3600),
                Instant.now().minusSeconds(7200), Instant.now().plusSeconds(3600),
                Duration.ofMinutes(15), true, false, true);
        Instructor coOwner = new Instructor(course, "Aayla Secura", "aayla@jedi.org",
                true, "Aayla", InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
                new InstructorPrivileges(InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER.getRoleName()));
        Account account = new Account("aaylasecura", "Aayla Secura", "aayla@jedi.org");
        coOwner.setAccount(account);

        List<EmailWrapper> emails = sqlEmailGenerator.generateFeedbackSessionClosedEmails(
                session, List.of(coOwner));

        assertEquals(1, emails.size());
        EmailWrapper email = emails.get(0);
        String expectedSubject = Templates.populateTemplate(
                ConfigurableEmailTemplate.FEEDBACK_CLOSED.getDefaultSubject(),
                "${courseName}", "Semantic Networks",
                "${feedbackSessionName}", "Final Survey");
        assertEquals("aayla@jedi.org", email.getRecipient());
        assertEquals(EmailType.FEEDBACK_CLOSED, email.getType());
        assertEquals(expectedSubject, email.getSubject());
        assertFalse(email.getContent().contains("${"));
    }

    @Test
    void testGenerateFeedbackSessionClosedEmails_withDbTemplate_usesCustomSubjectAndBody() {
        EmailTemplate customTemplate = new EmailTemplate(
                "FEEDBACK_CLOSED",
                "${feedbackSessionName} in ${courseName} is now closed",
                "<p>Hi ${userName}, ${feedbackSessionName} is closed.</p>${additionalNotes}");
        Mockito.when(mockEmailTemplatesDb.getEmailTemplate("FEEDBACK_CLOSED"))
                .thenReturn(customTemplate);

        Course course = new Course("CS6210", "Advanced OS", "UTC", "NUS");
        FeedbackSession session = new FeedbackSession("Exit Poll", course, "prof@nus.edu.sg",
                "instructions", Instant.now().minusSeconds(7200), Instant.now().minusSeconds(3600),
                Instant.now().minusSeconds(7200), Instant.now().plusSeconds(3600),
                Duration.ofMinutes(15), true, false, true);
        Instructor coOwner = new Instructor(course, "Shaak Ti", "shaak@jedi.org",
                true, "Shaak", InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
                new InstructorPrivileges(InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER.getRoleName()));
        Account account = new Account("shaakti", "Shaak Ti", "shaak@jedi.org");
        coOwner.setAccount(account);

        List<EmailWrapper> emails = sqlEmailGenerator.generateFeedbackSessionClosedEmails(
                session, List.of(coOwner));

        assertEquals(1, emails.size());
        EmailWrapper email = emails.get(0);
        assertEquals("shaak@jedi.org", email.getRecipient());
        assertEquals(EmailType.FEEDBACK_CLOSED, email.getType());
        assertEquals("Exit Poll in Advanced OS is now closed", email.getSubject());
        assertFalse(email.getContent().contains("${"));
    }

    @Test
    void testGenerateDeadlineExtensionGrantedEmail_noDbTemplate_usesConfigurableDefaults() {
        Mockito.when(mockEmailTemplatesDb.getEmailTemplate("DEADLINE_EXTENSION_GRANTED")).thenReturn(null);

        Course course = new Course("CS2106", "Operating Systems", "UTC", "NUS");
        FeedbackSession session = new FeedbackSession("OS Survey", course, "prof@nus.edu.sg",
                "instructions", Instant.now().minusSeconds(7200), Instant.now().plusSeconds(7200),
                Instant.now().minusSeconds(3600), Instant.now().plusSeconds(10800),
                Duration.ofMinutes(15), true, false, true);
        Student student = new Student(course, "Barriss Offee", "barriss@jedi.org", "");
        Mockito.when(mockUsersDb.getStudentForEmail("CS2106", "barriss@jedi.org")).thenReturn(student);

        List<EmailWrapper> emails = sqlEmailGenerator.generateDeadlineGrantedEmails(
                course, session, Map.of("barriss@jedi.org", Instant.now().plusSeconds(14400)), false);

        assertEquals(1, emails.size());
        EmailWrapper email = emails.get(0);
        String expectedSubject = Templates.populateTemplate(
                ConfigurableEmailTemplate.DEADLINE_EXTENSION_GRANTED.getDefaultSubject(),
                "${courseName}", "Operating Systems",
                "${feedbackSessionName}", "OS Survey");
        assertEquals("barriss@jedi.org", email.getRecipient());
        assertEquals(EmailType.DEADLINE_EXTENSION_GRANTED, email.getType());
        assertEquals(expectedSubject, email.getSubject());
        assertFalse(email.getContent().contains("${"));
    }

    @Test
    void testGenerateDeadlineExtensionGrantedEmail_withDbTemplate_usesCustomSubjectAndBody() {
        EmailTemplate customTemplate = new EmailTemplate(
                "DEADLINE_EXTENSION_GRANTED",
                "Deadline extension for ${feedbackSessionName} in ${courseName}",
                "<p>Hi ${userName}, your deadline for ${feedbackSessionName} is extended. "
                        + "Old: ${oldEndTime}, New: ${newEndTime}. "
                        + "<a href=\"${submitUrl}\">${submitUrl}</a></p>${additionalContactInformation}");
        Mockito.when(mockEmailTemplatesDb.getEmailTemplate("DEADLINE_EXTENSION_GRANTED"))
                .thenReturn(customTemplate);

        Course course = new Course("CS3103", "Computer Networks", "UTC", "NUS");
        FeedbackSession session = new FeedbackSession("Net Survey", course, "prof@nus.edu.sg",
                "instructions", Instant.now().minusSeconds(7200), Instant.now().plusSeconds(7200),
                Instant.now().minusSeconds(3600), Instant.now().plusSeconds(10800),
                Duration.ofMinutes(15), true, false, true);
        Student student = new Student(course, "Luminara Unduli", "luminara@mirialan.org", "");
        Mockito.when(mockUsersDb.getStudentForEmail("CS3103", "luminara@mirialan.org")).thenReturn(student);

        List<EmailWrapper> emails = sqlEmailGenerator.generateDeadlineGrantedEmails(
                course, session, Map.of("luminara@mirialan.org", Instant.now().plusSeconds(14400)), false);

        assertEquals(1, emails.size());
        EmailWrapper email = emails.get(0);
        assertEquals("luminara@mirialan.org", email.getRecipient());
        assertEquals(EmailType.DEADLINE_EXTENSION_GRANTED, email.getType());
        assertEquals("Deadline extension for Net Survey in Computer Networks", email.getSubject());
        assertFalse(email.getContent().contains("${"));
    }

    @Test
    void testGenerateDeadlineExtensionUpdatedEmail_noDbTemplate_usesConfigurableDefaults() {
        Mockito.when(mockEmailTemplatesDb.getEmailTemplate("DEADLINE_EXTENSION_UPDATED")).thenReturn(null);

        Course course = new Course("CS3241", "Computer Graphics", "UTC", "NUS");
        FeedbackSession session = new FeedbackSession("Graphics Survey", course, "prof@nus.edu.sg",
                "instructions", Instant.now().minusSeconds(7200), Instant.now().plusSeconds(7200),
                Instant.now().minusSeconds(3600), Instant.now().plusSeconds(10800),
                Duration.ofMinutes(15), true, false, true);
        Student student = new Student(course, "Even Piell", "even@jedi.org", "");
        Mockito.when(mockUsersDb.getStudentForEmail("CS3241", "even@jedi.org")).thenReturn(student);
        Instant oldDeadline = Instant.now().plusSeconds(3600);
        Instant newDeadline = Instant.now().plusSeconds(14400);

        List<EmailWrapper> emails = sqlEmailGenerator.generateDeadlineUpdatedEmails(
                course, session, Map.of("even@jedi.org", newDeadline), Map.of("even@jedi.org", oldDeadline), false);

        assertEquals(1, emails.size());
        EmailWrapper email = emails.get(0);
        String expectedSubject = Templates.populateTemplate(
                ConfigurableEmailTemplate.DEADLINE_EXTENSION_UPDATED.getDefaultSubject(),
                "${courseName}", "Computer Graphics",
                "${feedbackSessionName}", "Graphics Survey");
        assertEquals("even@jedi.org", email.getRecipient());
        assertEquals(EmailType.DEADLINE_EXTENSION_UPDATED, email.getType());
        assertEquals(expectedSubject, email.getSubject());
        assertFalse(email.getContent().contains("${"));
    }

    @Test
    void testGenerateDeadlineExtensionUpdatedEmail_withDbTemplate_usesCustomSubjectAndBody() {
        EmailTemplate customTemplate = new EmailTemplate(
                "DEADLINE_EXTENSION_UPDATED",
                "Updated deadline for ${feedbackSessionName} in ${courseName}",
                "<p>Hi ${userName}, your deadline changed. "
                        + "Old: ${oldEndTime}, New: ${newEndTime}. "
                        + "<a href=\"${submitUrl}\">${submitUrl}</a></p>${additionalContactInformation}");
        Mockito.when(mockEmailTemplatesDb.getEmailTemplate("DEADLINE_EXTENSION_UPDATED"))
                .thenReturn(customTemplate);

        Course course = new Course("CS3343", "Software Testing", "UTC", "NUS");
        FeedbackSession session = new FeedbackSession("Testing Survey", course, "prof@nus.edu.sg",
                "instructions", Instant.now().minusSeconds(7200), Instant.now().plusSeconds(7200),
                Instant.now().minusSeconds(3600), Instant.now().plusSeconds(10800),
                Duration.ofMinutes(15), true, false, true);
        Student student = new Student(course, "Coleman Kcaj", "kcaj@jedi.org", "");
        Mockito.when(mockUsersDb.getStudentForEmail("CS3343", "kcaj@jedi.org")).thenReturn(student);
        Instant oldDeadline = Instant.now().plusSeconds(3600);
        Instant newDeadline = Instant.now().plusSeconds(14400);

        List<EmailWrapper> emails = sqlEmailGenerator.generateDeadlineUpdatedEmails(
                course, session, Map.of("kcaj@jedi.org", newDeadline), Map.of("kcaj@jedi.org", oldDeadline), false);

        assertEquals(1, emails.size());
        EmailWrapper email = emails.get(0);
        assertEquals("kcaj@jedi.org", email.getRecipient());
        assertEquals(EmailType.DEADLINE_EXTENSION_UPDATED, email.getType());
        assertEquals("Updated deadline for Testing Survey in Software Testing", email.getSubject());
        assertFalse(email.getContent().contains("${"));
    }

    @Test
    void testGenerateDeadlineExtensionRevokedEmail_noDbTemplate_usesConfigurableDefaults() {
        Mockito.when(mockEmailTemplatesDb.getEmailTemplate("DEADLINE_EXTENSION_REVOKED")).thenReturn(null);

        Course course = new Course("CS4243", "Computer Vision", "UTC", "NUS");
        FeedbackSession session = new FeedbackSession("Vision Survey", course, "prof@nus.edu.sg",
                "instructions", Instant.now().minusSeconds(7200), Instant.now().plusSeconds(7200),
                Instant.now().minusSeconds(3600), Instant.now().plusSeconds(10800),
                Duration.ofMinutes(15), true, false, true);
        Student student = new Student(course, "Depa Billaba", "depa@jedi.org", "");
        Mockito.when(mockUsersDb.getStudentForEmail("CS4243", "depa@jedi.org")).thenReturn(student);
        Instant revokedDeadline = Instant.now().plusSeconds(14400);

        List<EmailWrapper> emails = sqlEmailGenerator.generateDeadlineRevokedEmails(
                course, session, Map.of("depa@jedi.org", revokedDeadline), false);

        assertEquals(1, emails.size());
        EmailWrapper email = emails.get(0);
        String expectedSubject = Templates.populateTemplate(
                ConfigurableEmailTemplate.DEADLINE_EXTENSION_REVOKED.getDefaultSubject(),
                "${courseName}", "Computer Vision",
                "${feedbackSessionName}", "Vision Survey");
        assertEquals("depa@jedi.org", email.getRecipient());
        assertEquals(EmailType.DEADLINE_EXTENSION_REVOKED, email.getType());
        assertEquals(expectedSubject, email.getSubject());
        assertFalse(email.getContent().contains("${"));
    }

    @Test
    void testGenerateDeadlineExtensionRevokedEmail_withDbTemplate_usesCustomSubjectAndBody() {
        EmailTemplate customTemplate = new EmailTemplate(
                "DEADLINE_EXTENSION_REVOKED",
                "Deadline extension revoked for ${feedbackSessionName} in ${courseName}",
                "<p>Hi ${userName}, your extension was revoked. "
                        + "Old: ${oldEndTime}, New: ${newEndTime}. "
                        + "<a href=\"${submitUrl}\">${submitUrl}</a></p>${additionalContactInformation}");
        Mockito.when(mockEmailTemplatesDb.getEmailTemplate("DEADLINE_EXTENSION_REVOKED"))
                .thenReturn(customTemplate);

        Course course = new Course("CS4246", "AI Planning", "UTC", "NUS");
        FeedbackSession session = new FeedbackSession("AI Survey", course, "prof@nus.edu.sg",
                "instructions", Instant.now().minusSeconds(7200), Instant.now().plusSeconds(7200),
                Instant.now().minusSeconds(3600), Instant.now().plusSeconds(10800),
                Duration.ofMinutes(15), true, false, true);
        Student student = new Student(course, "Plo Koon", "plo@jedi.org", "");
        Mockito.when(mockUsersDb.getStudentForEmail("CS4246", "plo@jedi.org")).thenReturn(student);
        Instant revokedDeadline = Instant.now().plusSeconds(14400);

        List<EmailWrapper> emails = sqlEmailGenerator.generateDeadlineRevokedEmails(
                course, session, Map.of("plo@jedi.org", revokedDeadline), false);

        assertEquals(1, emails.size());
        EmailWrapper email = emails.get(0);
        assertEquals("plo@jedi.org", email.getRecipient());
        assertEquals(EmailType.DEADLINE_EXTENSION_REVOKED, email.getType());
        assertEquals("Deadline extension revoked for AI Survey in AI Planning", email.getSubject());
        assertFalse(email.getContent().contains("${"));
    }

    @Test
    void testGenerateStudentCourseLinksRegeneratedEmail_noDbTemplate_usesConfigurableDefaults() {
        Mockito.when(mockEmailTemplatesDb.getEmailTemplate("STUDENT_COURSE_LINKS_REGENERATED")).thenReturn(null);

        Course course = new Course("CS5331", "Web Information Retrieval", "UTC", "NUS");
        Student student = new Student(course, "Quinlan Vos", "quinlan@jedi.org", "");
        Mockito.when(mockCoursesDb.getCourse("CS5331")).thenReturn(course);
        Mockito.when(mockUsersDb.getStudentForEmail("CS5331", "quinlan@jedi.org")).thenReturn(student);
        Mockito.when(mockFeedbackSessionsDb.getFeedbackSessionEntitiesForCourse("CS5331"))
                .thenReturn(new java.util.ArrayList<>());

        EmailWrapper email = sqlEmailGenerator.generateFeedbackSessionSummaryOfCourse(
                "CS5331", "quinlan@jedi.org", EmailType.STUDENT_COURSE_LINKS_REGENERATED);

        String expectedSubject = Templates.populateTemplate(
                ConfigurableEmailTemplate.STUDENT_COURSE_LINKS_REGENERATED.getDefaultSubject(),
                "${courseName}", "Web Information Retrieval",
                "${courseId}", "CS5331");
        assertEquals("quinlan@jedi.org", email.getRecipient());
        assertEquals(EmailType.STUDENT_COURSE_LINKS_REGENERATED, email.getType());
        assertEquals(expectedSubject, email.getSubject());
        assertFalse(email.getContent().contains("${"));
    }

    @Test
    void testGenerateStudentCourseLinksRegeneratedEmail_withDbTemplate_usesCustomSubjectAndBody() {
        EmailTemplate customTemplate = new EmailTemplate(
                "STUDENT_COURSE_LINKS_REGENERATED",
                "Your links for ${courseName} [${courseId}] updated",
                "<p>Hi ${userName}, your links for ${courseName} are updated. "
                        + "${joinFragment}<br>${linksFragment}</p>${additionalContactInformation}");
        Mockito.when(mockEmailTemplatesDb.getEmailTemplate("STUDENT_COURSE_LINKS_REGENERATED"))
                .thenReturn(customTemplate);

        Course course = new Course("CS5340", "Uncertainty Modelling", "UTC", "NUS");
        Student student = new Student(course, "Sharad Srinivasan", "sharad@nus.edu.sg", "");
        Mockito.when(mockCoursesDb.getCourse("CS5340")).thenReturn(course);
        Mockito.when(mockUsersDb.getStudentForEmail("CS5340", "sharad@nus.edu.sg")).thenReturn(student);
        Mockito.when(mockFeedbackSessionsDb.getFeedbackSessionEntitiesForCourse("CS5340"))
                .thenReturn(new java.util.ArrayList<>());

        EmailWrapper email = sqlEmailGenerator.generateFeedbackSessionSummaryOfCourse(
                "CS5340", "sharad@nus.edu.sg", EmailType.STUDENT_COURSE_LINKS_REGENERATED);

        assertEquals("sharad@nus.edu.sg", email.getRecipient());
        assertEquals(EmailType.STUDENT_COURSE_LINKS_REGENERATED, email.getType());
        assertEquals("Your links for Uncertainty Modelling [CS5340] updated", email.getSubject());
        assertFalse(email.getContent().contains("${"));
    }

    @Test
    void testGenerateInstructorCourseLinksRegeneratedEmail_noDbTemplate_usesConfigurableDefaults() {
        Mockito.when(mockEmailTemplatesDb.getEmailTemplate("INSTRUCTOR_COURSE_LINKS_REGENERATED")).thenReturn(null);

        Course course = new Course("CS5346", "Information Retrieval", "UTC", "NUS");
        Instructor instructor = new Instructor(course, "Sifo Dyas", "sifo@jedi.org",
                true, "Sifo", InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
                new InstructorPrivileges(InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER.getRoleName()));
        Mockito.when(mockCoursesDb.getCourse("CS5346")).thenReturn(course);
        Mockito.when(mockUsersDb.getInstructorForEmail("CS5346", "sifo@jedi.org")).thenReturn(instructor);
        Mockito.when(mockFeedbackSessionsDb.getFeedbackSessionEntitiesForCourse("CS5346"))
                .thenReturn(new java.util.ArrayList<>());

        EmailWrapper email = sqlEmailGenerator.generateFeedbackSessionSummaryOfCourse(
                "CS5346", "sifo@jedi.org", EmailType.INSTRUCTOR_COURSE_LINKS_REGENERATED);

        String expectedSubject = Templates.populateTemplate(
                ConfigurableEmailTemplate.INSTRUCTOR_COURSE_LINKS_REGENERATED.getDefaultSubject(),
                "${courseName}", "Information Retrieval",
                "${courseId}", "CS5346");
        assertEquals("sifo@jedi.org", email.getRecipient());
        assertEquals(EmailType.INSTRUCTOR_COURSE_LINKS_REGENERATED, email.getType());
        assertEquals(expectedSubject, email.getSubject());
        assertFalse(email.getContent().contains("${"));
    }

    @Test
    void testGenerateInstructorCourseLinksRegeneratedEmail_withDbTemplate_usesCustomSubjectAndBody() {
        EmailTemplate customTemplate = new EmailTemplate(
                "INSTRUCTOR_COURSE_LINKS_REGENERATED",
                "Instructor links for ${courseName} [${courseId}] updated",
                "<p>Hi ${userName}, your instructor links for ${courseName} are updated. "
                        + "${joinFragment}<br>${linksFragment}</p>${additionalContactInformation}");
        Mockito.when(mockEmailTemplatesDb.getEmailTemplate("INSTRUCTOR_COURSE_LINKS_REGENERATED"))
                .thenReturn(customTemplate);

        Course course = new Course("CS5422", "Human Computer Interaction", "UTC", "NUS");
        Instructor instructor = new Instructor(course, "Tera Sinube", "tera@jedi.org",
                true, "Tera", InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
                new InstructorPrivileges(InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER.getRoleName()));
        Mockito.when(mockCoursesDb.getCourse("CS5422")).thenReturn(course);
        Mockito.when(mockUsersDb.getInstructorForEmail("CS5422", "tera@jedi.org")).thenReturn(instructor);
        Mockito.when(mockFeedbackSessionsDb.getFeedbackSessionEntitiesForCourse("CS5422"))
                .thenReturn(new java.util.ArrayList<>());

        EmailWrapper email = sqlEmailGenerator.generateFeedbackSessionSummaryOfCourse(
                "CS5422", "tera@jedi.org", EmailType.INSTRUCTOR_COURSE_LINKS_REGENERATED);

        assertEquals("tera@jedi.org", email.getRecipient());
        assertEquals(EmailType.INSTRUCTOR_COURSE_LINKS_REGENERATED, email.getType());
        assertEquals("Instructor links for Human Computer Interaction [CS5422] updated", email.getSubject());
        assertFalse(email.getContent().contains("${"));
    }

    @Test
    void testGenerateStudentEmailChangedEmail_noDbTemplate_usesConfigurableDefaults() {
        Mockito.when(mockEmailTemplatesDb.getEmailTemplate("STUDENT_EMAIL_CHANGED")).thenReturn(null);

        Course course = new Course("CS5424", "Distributed Systems", "UTC", "NUS");
        Student student = new Student(course, "Jocasta Nu", "jocasta@jediarchives.org", "");
        Mockito.when(mockCoursesDb.getCourse("CS5424")).thenReturn(course);
        Mockito.when(mockUsersDb.getStudentForEmail("CS5424", "jocasta@jediarchives.org")).thenReturn(student);
        Mockito.when(mockFeedbackSessionsDb.getFeedbackSessionEntitiesForCourse("CS5424"))
                .thenReturn(new java.util.ArrayList<>());

        EmailWrapper email = sqlEmailGenerator.generateFeedbackSessionSummaryOfCourse(
                "CS5424", "jocasta@jediarchives.org", EmailType.STUDENT_EMAIL_CHANGED);

        String expectedSubject = Templates.populateTemplate(
                ConfigurableEmailTemplate.STUDENT_EMAIL_CHANGED.getDefaultSubject(),
                "${courseName}", "Distributed Systems",
                "${courseId}", "CS5424");
        assertEquals("jocasta@jediarchives.org", email.getRecipient());
        assertEquals(EmailType.STUDENT_EMAIL_CHANGED, email.getType());
        assertEquals(expectedSubject, email.getSubject());
        assertFalse(email.getContent().contains("${"));
    }

    @Test
    void testGenerateStudentEmailChangedEmail_withDbTemplate_usesCustomSubjectAndBody() {
        EmailTemplate customTemplate = new EmailTemplate(
                "STUDENT_EMAIL_CHANGED",
                "Email changed — links for ${courseName} [${courseId}]",
                "<p>Hi ${userName}, your email changed. Here are your links for ${courseName}. "
                        + "${joinFragment}<br>${linksFragment}</p>${additionalContactInformation}");
        Mockito.when(mockEmailTemplatesDb.getEmailTemplate("STUDENT_EMAIL_CHANGED"))
                .thenReturn(customTemplate);

        Course course = new Course("CS5461", "High Performance Computing", "UTC", "NUS");
        Student student = new Student(course, "Dooku Comes", "dooku@sith.org", "");
        Mockito.when(mockCoursesDb.getCourse("CS5461")).thenReturn(course);
        Mockito.when(mockUsersDb.getStudentForEmail("CS5461", "dooku@sith.org")).thenReturn(student);
        Mockito.when(mockFeedbackSessionsDb.getFeedbackSessionEntitiesForCourse("CS5461"))
                .thenReturn(new java.util.ArrayList<>());

        EmailWrapper email = sqlEmailGenerator.generateFeedbackSessionSummaryOfCourse(
                "CS5461", "dooku@sith.org", EmailType.STUDENT_EMAIL_CHANGED);

        assertEquals("dooku@sith.org", email.getRecipient());
        assertEquals(EmailType.STUDENT_EMAIL_CHANGED, email.getType());
        assertEquals("Email changed — links for High Performance Computing [CS5461]", email.getSubject());
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
