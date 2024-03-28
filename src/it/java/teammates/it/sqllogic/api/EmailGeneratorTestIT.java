package teammates.it.sqllogic.api;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.SqlDataBundle;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.util.Config;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.common.util.HibernateUtil;
import teammates.common.util.TimeHelper;
import teammates.it.test.BaseTestCaseWithSqlDatabaseAccess;
import teammates.sqllogic.api.SqlEmailGenerator;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Student;
import teammates.test.EmailChecker;

/**
 * SUT: {@link SqlEmailGenerator}.
 */
public class EmailGeneratorTestIT extends BaseTestCaseWithSqlDatabaseAccess {

    private final SqlEmailGenerator emailGenerator = SqlEmailGenerator.inst();

    private SqlDataBundle dataBundle;

    @Override
    @BeforeClass
    public void setupClass() {
        super.setupClass();
    }

    @Override
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        dataBundle = loadSqlDataBundle("/SqlEmailGeneratorTest.json");

        FeedbackSession session1InCourse3 = dataBundle.feedbackSessions.get("session1InCourse3");
        FeedbackSession session2InCourse3 = dataBundle.feedbackSessions.get("session2InCourse3");
        FeedbackSession session1InCourse4 = dataBundle.feedbackSessions.get("session1InCourse4");
        FeedbackSession session2InCourse4 = dataBundle.feedbackSessions.get("session2InCourse4");
        // opened and unpublished.
        session1InCourse3.setStartTime(TimeHelper.getInstantDaysOffsetFromNow(-20));
        dataBundle.feedbackSessions.put("session1InCourse3", session1InCourse3);

        // closed and unpublished
        session2InCourse3.setStartTime(TimeHelper.getInstantDaysOffsetFromNow(-19));
        session2InCourse3.setEndTime(TimeHelper.getInstantDaysOffsetFromNow(-1));
        dataBundle.feedbackSessions.put("session2InCourse3", session2InCourse3);

        // opened and published.
        session1InCourse4.setStartTime(TimeHelper.getInstantDaysOffsetFromNow(-19));
        session1InCourse4.setResultsVisibleFromTime(TimeHelper.getInstantDaysOffsetFromNow(-1));
        dataBundle.feedbackSessions.put("session1InCourse4", session1InCourse4);

        // closed and published
        session2InCourse4.setStartTime(TimeHelper.getInstantDaysOffsetFromNow(-18));
        session2InCourse4.setEndTime(TimeHelper.getInstantDaysOffsetFromNow(-1));
        session2InCourse4.setResultsVisibleFromTime(TimeHelper.getInstantDaysOffsetFromNow(-1));
        dataBundle.feedbackSessions.put("session2InCourse4", session2InCourse4);

        persistDataBundle(dataBundle);
        HibernateUtil.flushSession();
        HibernateUtil.clearSession();
    }

    @Test
    public void testGenerateSessionLinksRecoveryEmail() throws Exception {

        // To remove after migrating to postgres
        String nonExistentStudent = "";
        Map<CourseAttributes, StringBuilder> emptyFragmentList = new HashMap<>();
        ______TS("invalid email address");

        EmailWrapper email = emailGenerator.generateSessionLinksRecoveryEmailForStudent(
                "non-existing-student", nonExistentStudent, emptyFragmentList);
        String subject = EmailType.SESSION_LINKS_RECOVERY.getSubject();

        verifyEmail(email, "non-existing-student", subject,
                "/sessionLinksRecoveryNonExistingStudentEmail.html");

        ______TS("no sessions found");

        Student student1InCourse1 = dataBundle.students.get("student1InCourse1");

        email = emailGenerator.generateSessionLinksRecoveryEmailForStudent(
                student1InCourse1.getEmail(), nonExistentStudent, emptyFragmentList);
        subject = EmailType.SESSION_LINKS_RECOVERY.getSubject();

        verifyEmail(email, student1InCourse1.getEmail(), subject,
                "/sessionLinksRecoveryNoSessionsFoundEmail.html");

        ______TS("Typical case: found opened or closed but unpublished Sessions");

        Student student1InCourse3 = dataBundle.students.get("student1InCourse3");

        email = emailGenerator.generateSessionLinksRecoveryEmailForStudent(
                student1InCourse3.getEmail(), nonExistentStudent, emptyFragmentList);

        subject = EmailType.SESSION_LINKS_RECOVERY.getSubject();

        verifyEmail(email, student1InCourse3.getEmail(), subject,
                "/sessionLinksRecoveryOpenedOrClosedButUnpublishedSessions.html");

        ______TS("Typical case: found opened or closed and  published Sessions");

        Student student1InCourse4 = dataBundle.students.get("student1InCourse4");

        email = emailGenerator.generateSessionLinksRecoveryEmailForStudent(
                student1InCourse4.getEmail(), nonExistentStudent, emptyFragmentList);

        subject = EmailType.SESSION_LINKS_RECOVERY.getSubject();

        verifyEmail(email, student1InCourse4.getEmail(), subject,
                "/sessionLinksRecoveryOpenedOrClosedAndpublishedSessions.html");

    }

    private void verifyEmail(EmailWrapper email, String recipient, String subject, String emailContentFilePath)
            throws Exception {
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
}
