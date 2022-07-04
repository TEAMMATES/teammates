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

    @Override
    public ActionResult execute() {
        Instant currentTime = Instant.now();
        Map<String, Map<String, Long>> studentLatestLogs = new HashMap<>();
        List<FeedbackSessionLogEntryAttributes> validLogEntries = new ArrayList<>();
        List<FeedbackSessionLogEntryAttributes> allLogEntries =
                logsProcessor.getFeedbackSessionLogs(null, null,
                        currentTime.minus(15, ChronoUnit.MINUTES).toEpochMilli(), currentTime.toEpochMilli(), null);

        for (FeedbackSessionLogEntryAttributes logEntry : allLogEntries) {
            String studentEmail = logEntry.getStudentEmail();
            String logType = logEntry.getFeedbackSessionLogType();
            Long logTimestamp = logEntry.getTimestamp();
            Map<String, Long> studentLog = studentLatestLogs.getOrDefault(studentEmail, new HashMap<>());
            boolean isValid = true;

            if (studentLog.containsKey(logType)) {
                Long lastStudentLogTimestamp = studentLog.get(logType);

                if (Math.abs(lastStudentLogTimestamp - logTimestamp) < 2 * 1000) {
                    isValid = false;
                }
            }

            if (isValid) {
                validLogEntries.add(logEntry);

                studentLog.put(logType, logTimestamp);
                studentLatestLogs.put(studentEmail, studentLog);
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
