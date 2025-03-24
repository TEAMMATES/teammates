package teammates.sqlui.webapi;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.ui.output.MessageOutput;
import teammates.ui.request.ErrorReportRequest;
import teammates.ui.request.InvalidHttpRequestBodyException;
import teammates.ui.webapi.JsonResult;
import teammates.ui.webapi.SendErrorReportAction;

/**
 * SUT: {@link SendErrorReportAction}.
 */
public class SendErrorReportActionTest extends BaseActionTest<SendErrorReportAction> {
    private static final String GOOGLE_ID = "user-googleId";
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

    @Test
    void testExecute_notLoggedInTypicalCase_success() {
        logoutUser();

        ErrorReportRequest report = new ErrorReportRequest(REQUEST_ID, SUBJECT,
                CONTENT);
        SendErrorReportAction action = getAction(report, PARAMS);
        JsonResult result = getJsonResult(action);
        MessageOutput output = (MessageOutput) result.getOutput();

        String expectedLogMessage = "====== USER FEEDBACK ABOUT ERROR ======" + System.lineSeparator()
                + "USER: Non-logged in user" + System.lineSeparator()
                + "REQUEST ID: " + REQUEST_ID + System.lineSeparator()
                + "SUBJECT: " + SUBJECT + System.lineSeparator()
                + "CONTENT: " + CONTENT;

        assertEquals(expectedLogMessage,
                action.getUserErrorReportLogMessage(report));
        assertEquals("Error report successfully sent", output.getMessage());
    }

    @Test
    void testExecute_loggedInTypicalCase_success() {
        loginAsUnregistered(GOOGLE_ID);

        ErrorReportRequest report = new ErrorReportRequest(REQUEST_ID, SUBJECT,
                CONTENT);
        SendErrorReportAction action = getAction(report, PARAMS);
        JsonResult result = getJsonResult(action);
        MessageOutput output = (MessageOutput) result.getOutput();

        String expectedLogMessage = "====== USER FEEDBACK ABOUT ERROR ======" + System.lineSeparator()
                + "USER: " + GOOGLE_ID + System.lineSeparator()
                + "REQUEST ID: " + REQUEST_ID + System.lineSeparator()
                + "SUBJECT: " + SUBJECT + System.lineSeparator()
                + "CONTENT: " + CONTENT;

        assertEquals(expectedLogMessage,
                action.getUserErrorReportLogMessage(report));
        assertEquals("Error report successfully sent", output.getMessage());
    }

    @Test
    void testExecute_nullRequestBody_throwsInvalidHttpRequestBodyException() {
        InvalidHttpRequestBodyException e = verifyHttpRequestBodyFailure(null, PARAMS);
        assertEquals("The request body is null", e.getMessage());
    }

    @Test
    void testExecute_nullRequestId_throwsInvalidHttpRequestBodyException() {
        ErrorReportRequest reportWithNullRequestId = new ErrorReportRequest(null, SUBJECT, CONTENT);
        InvalidHttpRequestBodyException e = verifyHttpRequestBodyFailure(reportWithNullRequestId, PARAMS);
        assertEquals("requestId cannot be null", e.getMessage());
    }

    @Test
    void testExecute_nullSubject_throwsInvalidHttpRequestBodyException() {
        ErrorReportRequest reportWithNullSubject = new ErrorReportRequest(REQUEST_ID, null, CONTENT);
        InvalidHttpRequestBodyException e = verifyHttpRequestBodyFailure(reportWithNullSubject, PARAMS);
        assertEquals("subject cannot be null", e.getMessage());
    }

    @Test
    void testExecute_nullContent_throwsInvalidHttpRequestBodyException() {
        ErrorReportRequest reportWithNullContent = new ErrorReportRequest(REQUEST_ID, SUBJECT, null);
        InvalidHttpRequestBodyException e = verifyHttpRequestBodyFailure(reportWithNullContent, PARAMS);
        assertEquals("content cannot be null", e.getMessage());
    }

    @Test
    void testAccessControl_admin_canAccess() {
        loginAsAdmin();
        verifyCanAccess();
    }

    @Test
    void testAccessControl_maintainer_canAccess() {
        loginAsMaintainer();
        verifyCanAccess();
    }

    @Test
    void testAccessControl_instructor_canAccess() {
        loginAsInstructor(GOOGLE_ID);
        verifyCanAccess();
    }

    @Test
    void testAccessControl_student_canAccess() {
        loginAsStudent(GOOGLE_ID);
        verifyCanAccess();
    }

    @Test
    void testAccessControl_unregistered_canAccess() {
        loginAsUnregistered(GOOGLE_ID);
        verifyCanAccess();
    }

    @Test
    void testAccessControl_loggedOut_canAccess() {
        logoutUser();
        verifyCanAccess();
    }
}
