package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.ui.request.ErrorReportRequest;

/**
 * SUT: {@link SendErrorReportAction}.
 */
public class SendErrorReportActionTest extends BaseActionTest<SendErrorReportAction> {
    private static final String REQUEST_ID = "REQUESTID";
    private static final String SUBJECT = "Email subject";
    private static final String CONTENT = "Email content";
    private static final String[] PARAMS = {};

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
    protected void testExecute() {
        logoutUser();

        ______TS("Normal case: valid report with all fields populated");
        ErrorReportRequest report = new ErrorReportRequest(REQUEST_ID, SUBJECT, CONTENT);
        SendErrorReportAction action = getAction(report, PARAMS);
        getJsonResult(action);

        String expectedLogMessage = "====== USER FEEDBACK ABOUT ERROR ======" + System.lineSeparator()
                + "USER: Non-logged in user" + System.lineSeparator()
                + "REQUEST ID: " + REQUEST_ID + System.lineSeparator()
                + "SUBJECT: " + SUBJECT + System.lineSeparator()
                + "CONTENT: " + CONTENT;

        assertEquals(expectedLogMessage, action.getUserErrorReportLogMessage(report));

        ______TS("Failure: Invalid report with null requestId");
        ErrorReportRequest badReport = new ErrorReportRequest(null, SUBJECT, CONTENT);
        verifyHttpRequestBodyFailure(badReport, PARAMS);

        ______TS("Failure: Invalid report with null SUBJECT");
        badReport = new ErrorReportRequest(REQUEST_ID, null, CONTENT);
        verifyHttpRequestBodyFailure(badReport, PARAMS);

        ______TS("Failure: Invalid report with null CONTENT");
        badReport = new ErrorReportRequest(REQUEST_ID, SUBJECT, null);
        verifyHttpRequestBodyFailure(badReport, PARAMS);
    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyAnyUserCanAccess();
    }

}
