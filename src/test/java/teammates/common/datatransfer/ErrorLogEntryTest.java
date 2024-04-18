package teammates.common.datatransfer;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.logs.ExceptionLogDetails;
import teammates.common.datatransfer.logs.GeneralLogEntry;
import teammates.common.datatransfer.logs.InstanceLogDetails;
import teammates.common.datatransfer.logs.LogSeverity;
import teammates.common.datatransfer.logs.SourceLocation;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link ErrorLogEntry}.
 */
public class ErrorLogEntryTest extends BaseTestCase {

    @Test
    public void testFromLogEntry_noLogDetails_shouldGetTextPayload() {
        GeneralLogEntry logEntry = createTypicalLogEntry();
        logEntry.setMessage("Test message");

        ErrorLogEntry errorLogEntry = ErrorLogEntry.fromLogEntry(logEntry);
        assertEquals("Test message", errorLogEntry.getMessage());
        assertEquals("ERROR", errorLogEntry.getSeverity());
        assertEquals("traceid", errorLogEntry.getTraceId());
    }

    @Test
    public void testFromLogEntry_logDetailsNotException_shouldGetSerializedPayload() {
        InstanceLogDetails instanceLogDetails = new InstanceLogDetails();
        instanceLogDetails.setInstanceId("instanceid123");
        instanceLogDetails.setInstanceEvent("STARTUP");

        GeneralLogEntry logEntry = createTypicalLogEntry();
        logEntry.setDetails(instanceLogDetails);

        ErrorLogEntry errorLogEntry = ErrorLogEntry.fromLogEntry(logEntry);
        assertEquals("{\n"
                + "  \"instanceId\": \"instanceid123\",\n"
                + "  \"instanceEvent\": \"STARTUP\",\n"
                + "  \"event\": \"INSTANCE_LOG\"\n"
                + "}", errorLogEntry.getMessage());
        assertEquals("ERROR", errorLogEntry.getSeverity());
        assertEquals("traceid", errorLogEntry.getTraceId());
    }

    @Test
    public void testFromLogEntry_exceptionLogDetails_shouldGetPrettyPrintedLog() {
        ExceptionLogDetails exceptionLogDetails = new ExceptionLogDetails();
        exceptionLogDetails.setMessage("ActionMappingException caught by WebApiServlet");
        exceptionLogDetails.setExceptionClasses(List.of(
                "teammates.common.exception.ActionMappingException"
        ));
        exceptionLogDetails.setExceptionMessages(List.of(
                "Resource with URI /webapi/404 is not found."
        ));
        exceptionLogDetails.setExceptionStackTraces(List.of(
                List.of(
                        "teammates.ui.webapi.ActionFactory.getAction(ActionFactory.java:168)",
                        "teammates.ui.webapi.ActionFactory.getAction(ActionFactory.java:163)",
                        "teammates.ui.webapi.WebApiServlet.invokeServlet(WebApiServlet.java:67)",
                        "teammates.ui.webapi.WebApiServlet.doGet(WebApiServlet.java:44)",
                        "jakarta.servlet.http.HttpServlet.service(HttpServlet.java:687)",
                        "jakarta.servlet.http.HttpServlet.service(HttpServlet.java:790),",
                        "jakarta.servlet.http.HttpServlet.service(HttpServlet.java:790),",
                        "..."
                )
        ));

        GeneralLogEntry logEntry = createTypicalLogEntry();
        logEntry.setDetails(exceptionLogDetails);

        String expectedMessage = String.join("\n", List.of(
                "ActionMappingException caught by WebApiServlet",
                "caused by teammates.common.exception.ActionMappingException: Resource with URI /webapi/404 is not found.",
                "    at teammates.ui.webapi.ActionFactory.getAction(ActionFactory.java:168)",
                "    at teammates.ui.webapi.ActionFactory.getAction(ActionFactory.java:163)",
                "    at teammates.ui.webapi.WebApiServlet.invokeServlet(WebApiServlet.java:67)",
                "    at teammates.ui.webapi.WebApiServlet.doGet(WebApiServlet.java:44)",
                "    at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:687)",
                "    at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:790),",
                "    at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:790),",
                "    at ...",
                ""
        ));

        ErrorLogEntry errorLogEntry = ErrorLogEntry.fromLogEntry(logEntry);
        assertEquals(expectedMessage, errorLogEntry.getMessage());
        assertEquals("ERROR", errorLogEntry.getSeverity());
        assertEquals("traceid", errorLogEntry.getTraceId());
    }

    private GeneralLogEntry createTypicalLogEntry() {
        return new GeneralLogEntry(LogSeverity.ERROR, "traceid", "insertid", new HashMap<>(),
                new SourceLocation("file1", 100L, "func1"), Instant.now().toEpochMilli());
    }

}
