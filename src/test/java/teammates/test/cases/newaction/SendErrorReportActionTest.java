package teammates.test.cases.newaction;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.ui.newcontroller.JsonResult;
import teammates.ui.newcontroller.SendErrorReportAction;

/**
 * SUT: {@link SendErrorReportAction}.
 */
public class SendErrorReportActionTest extends BaseActionTest<SendErrorReportAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.ERROR_REPORT;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @Override
    @Test
    protected void testExecute() throws Exception {
        String subject = "Email subject";
        String requestId = "REQUESTID";
        String content = "Email content";

        gaeSimulation.logoutUser();

        ______TS("Not enough parameters");

        verifyHttpParameterFailure();
        verifyHttpParameterFailure(Const.ParamsNames.ERROR_FEEDBACK_EMAIL_SUBJECT, subject);
        verifyHttpParameterFailure(Const.ParamsNames.ERROR_FEEDBACK_REQUEST_ID, requestId);

        ______TS("Normal case");

        String[] params = {
                Const.ParamsNames.ERROR_FEEDBACK_EMAIL_SUBJECT, subject,
                Const.ParamsNames.ERROR_FEEDBACK_REQUEST_ID, requestId,
        };
        SendErrorReportAction a = getAction(content, params);
        JsonResult r = getJsonResult(a);

        String expectedLogMessage = "====== USER FEEDBACK ABOUT ERROR ======" + System.lineSeparator()
                + "USER: Non-logged in user" + System.lineSeparator()
                + "REQUEST ID: " + requestId + System.lineSeparator()
                + "SUBJECT: " + subject + System.lineSeparator()
                + "CONTENT: " + content;

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());
        assertEquals(expectedLogMessage, a.getUserErrorReportLogMessage());
    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyAnyUserCanAccess();
    }

}
