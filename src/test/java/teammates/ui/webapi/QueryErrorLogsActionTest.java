package teammates.ui.webapi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.ErrorLogEntry;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.ui.output.MessageOutput;

/**
 * SUT: {@link QueryErrorLogsAction}.
 */
public class QueryErrorLogsActionTest extends BaseActionTest<QueryErrorLogsAction> {
    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.ERROR_LOGS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    @Test
    protected void testExecute() throws Exception {
        ______TS("No error logs");
        JsonResult actionOutput = getJsonResult(getAction());
        assertEquals(HttpStatus.SC_OK, actionOutput.getStatusCode());

        MessageOutput actualJsonResult = (MessageOutput) actionOutput.getOutput();
        List<ErrorLogEntry> expectedErrorLogs = new ArrayList<>();
        String expectedJsonResult = JsonUtils.toJson(expectedErrorLogs);
        assertEquals(expectedJsonResult, actualJsonResult.getMessage());

        ______TS("All error logs is within 24 hours");
        mockLogsProcessor.insertErrorLog("Test error message 1", "ERROR");
        mockLogsProcessor.insertErrorLog("Test error message 2", "ERROR");

        actionOutput = getJsonResult(getAction());
        assertEquals(HttpStatus.SC_OK, actionOutput.getStatusCode());

        actualJsonResult = (MessageOutput) actionOutput.getOutput();
        expectedErrorLogs = Arrays.asList(new ErrorLogEntry("Test error message 1", "ERROR"),
                new ErrorLogEntry("Test error message 2", "ERROR"));
        expectedJsonResult = JsonUtils.toJson(expectedErrorLogs);
        assertEquals(expectedJsonResult, actualJsonResult.getMessage());
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        verifyOnlyAdminCanAccess();
    }
}
