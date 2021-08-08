package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;

/**
 * SUT: {@link CompileLogsAction}.
 */
public class CompileLogsActionTest extends BaseActionTest<CompileLogsAction> {

    @Override
    protected String getActionUri() {
        return Const.CronJobURIs.AUTOMATED_LOG_COMPILATION;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    @Test
    protected void testExecute() {

        ______TS("No email should be sent if there are no logs");

        CompileLogsAction action = getAction();
        action.execute();

        verifyNoEmailsSent();

        ______TS("Email should be sent if there are logs");

        // The class does not check for the log severity as it is assumed that the logs service
        // will filter them correctly

        mockLogsProcessor.insertErrorLog("Test info message", "INFO", "123456");
        mockLogsProcessor.insertErrorLog("Test warning message", "WARNING", "abcdef");

        action = getAction();
        action.execute();

        verifyNumberOfEmailsSent(1);

        EmailWrapper emailSent = mockEmailSender.getEmailsSent().get(0);
        assertEquals(String.format(EmailType.SEVERE_LOGS_COMPILATION.getSubject(), Config.APP_VERSION),
                emailSent.getSubject());
        assertEquals(Config.SUPPORT_EMAIL, emailSent.getRecipient());
    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyOnlyAdminCanAccess();
    }

}
