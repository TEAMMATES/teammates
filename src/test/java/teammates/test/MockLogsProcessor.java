package teammates.test;

import java.util.List;

import teammates.common.datatransfer.ErrorLogEntry;
import teammates.logic.api.LogsProcessor;

/**
 * Allows mocking of {@link LogsProcessor}.
 */
public class MockLogsProcessor extends LogsProcessor {

    @Override
    public List<ErrorLogEntry> getRecentErrorLogs() {
        // TODO
    }

}
