package teammates.ui.webapi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import com.google.cloud.logging.LogEntry;
import com.google.cloud.logging.Payload;
import com.google.cloud.logging.Severity;

import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.ui.output.MessageOutput;

/**
 * SUT: {@link QueryInfoLogsAction}.
 */
public class QueryInfoLogsActionTest extends BaseActionTest<QueryInfoLogsAction> {
    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.INFO_LOGS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    @Test
    protected void testExecute() throws Exception {
        ______TS("No info logs");
        JsonResult actionOutput = getJsonResult(getAction());
        assertEquals(HttpStatus.SC_OK, actionOutput.getStatusCode());

        MessageOutput actualJsonResult = (MessageOutput) actionOutput.getOutput();
        List<LogEntry> expectedErrorLogs = new ArrayList<>();
        String expectedJsonResult = JsonUtils.toJson(expectedErrorLogs);
        assertEquals(expectedJsonResult, actualJsonResult.getMessage());

        ______TS("There are info logs for retrieval");
        mockLogsProcessor.insertInfoLogs("Test info message 1");
        mockLogsProcessor.insertInfoLogs("Test info message 2");

        actionOutput = getJsonResult(getAction());
        assertEquals(HttpStatus.SC_OK, actionOutput.getStatusCode());

        actualJsonResult = (MessageOutput) actionOutput.getOutput();

        String payload1 = "Test info message 1";
        LogEntry infoLogEntry1 = LogEntry.newBuilder(Payload.StringPayload.of(payload1))
                .setSeverity(Severity.INFO)
                .build();
        String payload2 = "Test info message 2";
        LogEntry infoLogEntry2 = LogEntry.newBuilder(Payload.StringPayload.of(payload2))
                .setSeverity(Severity.INFO)
                .build();

        expectedErrorLogs = Arrays.asList(infoLogEntry1, infoLogEntry2);
        expectedJsonResult = JsonUtils.toJson(expectedErrorLogs);
        assertEquals(expectedJsonResult, actualJsonResult.getMessage());
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        verifyOnlyAdminCanAccess();
    }
}
