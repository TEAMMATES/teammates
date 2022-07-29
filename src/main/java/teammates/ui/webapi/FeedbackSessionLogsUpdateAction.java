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
    private static final int minWindowPeriod = 2 * 1000;
    private static final int maxWindowSize = 4;

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

        for (String studentEmail : studentToLogs.keySet()) {
            Map<String, List<FeedbackSessionLogEntryAttributes>> studentLog =
                    studentToLogs.get(studentEmail);

            int windowStartIndex = 0;
            int windowSize = 0;
            for (String logType : studentLog.keySet()) {
                List<FeedbackSessionLogEntryAttributes> logs = studentLog.get(logType);

                for (int i = 0; i < logs.size(); i++) {
                    FeedbackSessionLogEntryAttributes startLog = logs.get(windowStartIndex);
                    FeedbackSessionLogEntryAttributes currLog = logs.get(i);

                    if (currLog.getTimestamp() - startLog.getTimestamp() <= minWindowPeriod) {
                        windowSize++;

                        // If the window size exceeds the max value
                        // we only take the first log of the window.
                        if (windowSize == maxWindowSize) {
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
