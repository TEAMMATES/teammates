package teammates.logic.external;

import org.testng.annotations.Test;
import teammates.common.datatransfer.FeedbackSessionLogEntry;
import teammates.common.datatransfer.logs.GeneralLogEntry;
import teammates.common.datatransfer.logs.LogSeverity;
import teammates.common.datatransfer.logs.QueryLogsParams;

import java.time.Instant;
import java.time.temporal.ChronoField;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class LocalLoggingServiceTest {

    private final LogService localLoggingService;

    LocalLoggingServiceTest() {
        this.localLoggingService = new LocalLoggingService();
    }

    @Test
    public void testQueryLogs() {
        QueryLogsParams.Builder builder = QueryLogsParams.builder(
                Instant.now().getLong(ChronoField.INSTANT_SECONDS),
                Instant.now().plusSeconds(300).getLong(ChronoField.INSTANT_SECONDS)
        );
        builder.withOrder("asc");
        builder.withSeverityLevel(LogSeverity.INFO);
        builder.withMinSeverity(LogSeverity.INFO);
        QueryLogsParams queryLogsParams = builder.build();

        List<GeneralLogEntry> result = localLoggingService.queryLogs(queryLogsParams).getLogEntries();

        // Verify the results as needed
        assertEquals(0, result.size());
    }

    @Test
    public void testCreateFeedbackSessionLog() {
        String courseId = "CS101";
        String email = "student@example.com";
        String fsName = "Feedback Session 1";
        String fslType = "Created";

        localLoggingService.createFeedbackSessionLog(courseId, email, fsName, fslType);
        QueryLogsParams.Builder builder = QueryLogsParams.builder(123456, 456890);
        List<GeneralLogEntry> result = localLoggingService.queryLogs(builder.build()).getLogEntries();

        // Verify the results as needed
        assertEquals(0, result.size());
    }

    @Test
    public void testGetFeedbackSessionLogs() {
        String courseId = "CS101";
        String email = "student@example.com";
        String fsName = "Feedback Session 1";
        long startTime = 0;
        long endTime = System.currentTimeMillis();

        List<FeedbackSessionLogEntry> feedbackSessionLogs = localLoggingService.getFeedbackSessionLogs(courseId, email, startTime, endTime, fsName);

        // Verify the results as needed
        assertEquals(1, feedbackSessionLogs.size());
    }
}
