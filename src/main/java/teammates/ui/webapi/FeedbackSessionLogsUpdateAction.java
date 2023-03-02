package teammates.ui.webapi;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Logger;
import teammates.storage.sqlentity.FeedbackSessionLogEntry;

/**
 * Cron job: Sync feedback session logs from GCloud logging service.
 */
public class FeedbackSessionLogsUpdateAction extends AdminOnlyAction {

    /**
     * Minimum gap period between 2 logs.
      */
    protected static final int MIN_WINDOW_PERIOD = 2 * 1000;
    private static final Logger log = Logger.getLogger();

    @Override
    public ActionResult execute() {
        Instant currentTime = Instant.now();
        Map<String, Map<String, List<FeedbackSessionLogEntry>>> studentToLogs =
                new HashMap<>();
        List<FeedbackSessionLogEntry> validLogEntries = new ArrayList<>();
        List<FeedbackSessionLogEntry> allLogEntries =
                logsProcessor.getFeedbackSessionLogs(
                        null, null, currentTime.minus(15, ChronoUnit.MINUTES).toEpochMilli(),
                        currentTime.toEpochMilli(), null);

        // Arrange logs based on student email and log type.
        for (FeedbackSessionLogEntry logEntry : allLogEntries) {
            String studentEmail = logEntry.getStudentEmail();
            String logType = logEntry.getFeedbackSessionLogType();
            Map<String, List<FeedbackSessionLogEntry>> studentLog =
                    studentToLogs.getOrDefault(studentEmail, new HashMap<>());

            List<FeedbackSessionLogEntry> currList =
                    studentLog.getOrDefault(logType, new ArrayList<>());

            currList.add(logEntry);
            studentLog.put(logType, currList);
            studentToLogs.put(studentEmail, studentLog);
        }

        for (Map.Entry<String, Map<String, List<FeedbackSessionLogEntry>>>
                studentToLog : studentToLogs.entrySet()) {
            Map<String, List<FeedbackSessionLogEntry>> studentLog =
                    studentToLog.getValue();
            int windowSize = 0;

            for (Map.Entry<String, List<FeedbackSessionLogEntry>>
                    logEntries : studentLog.entrySet()) {
                List<FeedbackSessionLogEntry> logs = logEntries.getValue();
                FeedbackSessionLogEntry startLog = logs.get(0);

                for (FeedbackSessionLogEntry currLog : logs) {
                    if (currLog.getTimestamp() - startLog.getTimestamp() < MIN_WINDOW_PERIOD) {
                        windowSize++;
                    } else {
                        startLog.setWindowSize(windowSize);
                        windowSize = 1;

                        validLogEntries.add(startLog);
                        startLog = currLog;
                    }
                }

                // this is the last window
                startLog.setWindowSize(windowSize);
                validLogEntries.add(startLog);
            }
        }

        try {
            sqlLogic.createFeedbackSessionLogs(validLogEntries);
        } catch (InvalidParametersException e) {
            log.severe("Unexpected error", e);
        }

        return new JsonResult("Successful");
    }
}
