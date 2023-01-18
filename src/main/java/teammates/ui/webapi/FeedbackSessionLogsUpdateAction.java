package teammates.ui.webapi;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.attributes.FeedbackSessionLogEntryAttributes;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Logger;

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
        Map<String, Map<String, List<FeedbackSessionLogEntryAttributes>>> studentToLogs =
                new HashMap<>();
        List<FeedbackSessionLogEntryAttributes> validLogEntries = new ArrayList<>();
        List<FeedbackSessionLogEntryAttributes> allLogEntries =
                logsProcessor.getFeedbackSessionLogs(null, null,
                        currentTime.minus(15, ChronoUnit.MINUTES).toEpochMilli(), currentTime.toEpochMilli(), null);

        // Arrange logs based on student email and log type.
        for (FeedbackSessionLogEntryAttributes logEntry : allLogEntries) {
            String studentEmail = logEntry.getStudentEmail();
            String logType = logEntry.getFeedbackSessionLogType();
            Map<String, List<FeedbackSessionLogEntryAttributes>> studentLog =
                    studentToLogs.getOrDefault(studentEmail, new HashMap<>());

            List<FeedbackSessionLogEntryAttributes> currList =
                    studentLog.getOrDefault(logType, new ArrayList<>());

            currList.add(logEntry);
            studentLog.put(logType, currList);
            studentToLogs.put(studentEmail, studentLog);
        }

        for (Map.Entry<String, Map<String, List<FeedbackSessionLogEntryAttributes>>>
                studentToLog : studentToLogs.entrySet()) {
            Map<String, List<FeedbackSessionLogEntryAttributes>> studentLog =
                    studentToLog.getValue();
            int windowSize = 0;

            for (Map.Entry<String, List<FeedbackSessionLogEntryAttributes>>
                    logEntries : studentLog.entrySet()) {
                List<FeedbackSessionLogEntryAttributes> logs = logEntries.getValue();
                FeedbackSessionLogEntryAttributes startLog = logs.get(0);

                for (FeedbackSessionLogEntryAttributes currLog : logs) {
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
            logic.createFeedbackSessionLogs(validLogEntries);
        } catch (InvalidParametersException e) {
            log.severe("Unexpected error", e);
        }

        return new JsonResult("Successful");
    }
}
