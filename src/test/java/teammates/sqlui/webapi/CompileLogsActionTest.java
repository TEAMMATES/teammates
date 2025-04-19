package teammates.sqlui.webapi;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.HashMap;

import org.testng.annotations.Test;

import teammates.common.datatransfer.logs.GeneralLogEntry;
import teammates.common.datatransfer.logs.LogSeverity;
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
    private static final long DISTANT_TIMESTAMP = Instant.now().minusSeconds(7 * 60).toEpochMilli();
    private static final long RECENT_TIMESTAMP = Instant.now().minusSeconds(30).toEpochMilli();

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
    void testAccessControl() {
        verifyOnlyAdminsCanAccess();
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
                DISTANT_TIMESTAMP, "Error message 1", null);
        mockLogsProcessor.insertWarningLog("warninglogtrace1", "warningloginsertid1", sourceLocation,
                RECENT_TIMESTAMP, "Warning message 1", null);

        CompileLogsAction action = getAction();
        action.execute();

        verifyNoEmailsSent();
    }

    @Test
    void testExecute_recentErrorLogs_emailSent() {
        GeneralLogEntry logEntry = new GeneralLogEntry(
                LogSeverity.ERROR,
                "errorlogtrace1",
                "errorloginsertid1",
                new HashMap<>(),
                sourceLocation,
                RECENT_TIMESTAMP
        );
        logEntry.setMessage("Error message 1");
        logEntry.setDetails(null);

        mockLogsProcessor.insertErrorLog(logEntry.getTrace(), logEntry.getInsertId(), logEntry.getSourceLocation(),
                logEntry.getTimestamp(), logEntry.getMessage(), logEntry.getDetails());

        EmailWrapper stubEmailWrapper;
        stubEmailWrapper = new EmailWrapper();
        stubEmailWrapper.setRecipient(Config.SUPPORT_EMAIL);
        stubEmailWrapper.setSubject(String.format(EmailType.SEVERE_LOGS_COMPILATION.getSubject(), Config.APP_VERSION));

        // use any() since the expected argument is a response from logs query
        when(mockEmailGenerator.generateCompiledLogsEmail(any())).thenReturn(stubEmailWrapper);
        CompileLogsAction action = getAction();
        action.execute();

        verifyNumberOfEmailsSent(1);

        EmailWrapper emailSent = mockEmailSender.getEmailsSent().get(0);
        assertEquals(String.format(EmailType.SEVERE_LOGS_COMPILATION.getSubject(), Config.APP_VERSION),
                emailSent.getSubject());
        assertEquals(Config.SUPPORT_EMAIL, emailSent.getRecipient());
    }
}
