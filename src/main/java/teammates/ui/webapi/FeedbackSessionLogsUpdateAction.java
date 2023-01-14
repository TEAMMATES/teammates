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
 * Action: Sync feedback session logs from GCloud logging service.
 */
public class FeedbackSessionLogsUpdateAction extends AdminOnlyAction {

    private static final Logger log = Logger.getLogger();
    private static final int MIN_WINDOW_PERIOD = 2 * 1000;
    private static final int MAX_WINDOW_SIZE = 4;

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

            List<FeedbackSessionLogEntryAttributes> currList;

            if (studentLog.containsKey(logType)) {
                currList = studentLog.get(logType);
            } else {
                currList = new ArrayList<>();
            }

            currList.add(logEntry);
            studentLog.put(logType, currList);
            studentToLogs.put(studentEmail, studentLog);
        }

        for (Map.Entry<String, Map<String, List<FeedbackSessionLogEntryAttributes>>>
                studentToLog : studentToLogs.entrySet()) {
            Map<String, List<FeedbackSessionLogEntryAttributes>> studentLog =
                    studentToLog.getValue();

            int windowStartIndex = 0;
            int windowSize = 0;
            for (Map.Entry<String, List<FeedbackSessionLogEntryAttributes>>
                    logEntries : studentLog.entrySet()) {
                List<FeedbackSessionLogEntryAttributes> logs = logEntries.getValue();

                for (int i = 0; i < logs.size(); i++) {
                    FeedbackSessionLogEntryAttributes startLog = logs.get(windowStartIndex);
                    FeedbackSessionLogEntryAttributes currLog = logs.get(i);

                    if (currLog.getTimestamp() - startLog.getTimestamp() <= MIN_WINDOW_PERIOD) {
                        windowSize++;
                        // If the window size exceeds the max value
                        // we only take the first log of the window.
                        if (windowSize == MAX_WINDOW_SIZE) {
                            startLog.setRemarks("This window has " + windowSize + " log(s)");
                            validLogEntries.add(startLog);
                            windowStartIndex = i + 1;
                        }
                    } else {
                        windowSize = 1;
                        validLogEntries.add(logs.get(windowStartIndex));
                        windowStartIndex++;
                    }
                }
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
