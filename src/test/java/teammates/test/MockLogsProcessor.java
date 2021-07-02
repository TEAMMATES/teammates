package teammates.test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.ErrorLogEntry;
import teammates.common.datatransfer.FeedbackSessionLogEntry;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.logic.api.LogsProcessor;

/**
 * Allows mocking of {@link LogsProcessor}.
 */
public class MockLogsProcessor extends LogsProcessor {

    private List<ErrorLogEntry> errorLogs = new ArrayList<>();
    private List<FeedbackSessionLogEntry> feedbackSessionLogs = new ArrayList<>();

    /**
     * Simulates insertion of error logs.
     */
    public void insertErrorLog(String message, String severity) {
        errorLogs.add(new ErrorLogEntry(message, severity));
    }

    /**
     * Simulates insertion of feedback session logs.
     */
    public void insertFeedbackSessionLog(StudentAttributes student, FeedbackSessionAttributes fs,
            String fslType, long timestamp) {
        feedbackSessionLogs.add(new FeedbackSessionLogEntry(student, fs, fslType, timestamp));
    }

    @Override
    public List<ErrorLogEntry> getRecentErrorLogs() {
        return errorLogs;
    }

    @Override
    public void createFeedbackSessionLog(String courseId, String email, String fsName, String fslType) {
        // No-op
    }

    @Override
    public List<FeedbackSessionLogEntry> getFeedbackSessionLogs(String courseId, String email,
            Instant startTime, Instant endTime, String fsName) {
        return feedbackSessionLogs;
    }

}
