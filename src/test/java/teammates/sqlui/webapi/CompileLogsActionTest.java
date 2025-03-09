package teammates.sqlui.webapi;

import java.time.Instant;

import org.testng.annotations.Test;

import teammates.common.datatransfer.logs.SourceLocation;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.ui.webapi.CompileLogsAction;

/**
 * SUT: {@link CompileLogsAction}.
 */
public class CompileLogsActionTest extends BaseActionTest<CompileLogsAction> {
    private static final String GOOGLE_ID = "user-googleId";
    private static final long TIMESTAMP_TOO_DISTANT = Instant.now().minusSeconds(7 * 60).toEpochMilli();
    private static final long CORRECT_TIMESTAMP = Instant.now().minusSeconds(30).toEpochMilli();

    private SourceLocation sourceLocation = new SourceLocation("file5", 5L, "func5");

    @Override
    protected String getActionUri() {
        return Const.CronJobURIs.AUTOMATED_LOG_COMPILATION;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Test
    void testSpecificAccessControl_admin_canAccess() {
        loginAsAdmin();
        verifyCanAccess();
    }

    @Test
    void testSpecificAccessControl_instructor_cannotAccess() {
        loginAsInstructor(GOOGLE_ID);
        verifyCannotAccess();
    }

    @Test
    void testSpecificAccessControl_student_cannotAccess() {
        loginAsStudent(GOOGLE_ID);
        verifyCannotAccess();
    }

    @Test
    void testSpecificAccessControl_loggedOut_cannotAccess() {
        logoutUser();
        verifyCannotAccess();
    }

    @Test
    void testExecute_noLogs_noEmailSent() {
        CompileLogsAction action = getAction();
        action.execute();

        verifyNoEmailsSent();
    }

    @Test
    void testExecute_noRecentErrorLogs_noEmailSent() {
        mockLogsProcessor.insertErrorLog("errorlogtrace1", "errorloginsertid1", sourceLocation,
                TIMESTAMP_TOO_DISTANT, "Error message 1", null);
        mockLogsProcessor.insertWarningLog("warninglogtrace1", "warningloginsertid1", sourceLocation,
                CORRECT_TIMESTAMP, "Warning message 1", null);

        CompileLogsAction action = getAction();
        action.execute();

        verifyNoEmailsSent();
    }

    @Test
    void testExecute_recentErrorLogs_emailSent() {
        mockLogsProcessor.insertErrorLog("errorlogtrace1", "errorloginsertid1", sourceLocation,
                CORRECT_TIMESTAMP, "Error message 1", null);

        CompileLogsAction action = getAction();
        action.execute();

        verifyNumberOfEmailsSent(1);

        EmailWrapper emailSent = mockEmailSender.getEmailsSent().get(0);
        assertEquals(String.format(EmailType.SEVERE_LOGS_COMPILATION.getSubject(), Config.APP_VERSION),
                emailSent.getSubject());
        assertEquals(Config.SUPPORT_EMAIL, emailSent.getRecipient());
    }
}
