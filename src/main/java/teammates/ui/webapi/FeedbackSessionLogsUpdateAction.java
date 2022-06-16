package teammates.ui.webapi;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import teammates.common.datatransfer.attributes.FeedbackSessionLogEntryAttributes;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Logger;

/**
 * Action: Sync feedback session logs from GCloud logging service.
 */
public class FeedbackSessionLogsUpdateAction extends AdminOnlyAction {

    private static final Logger log = Logger.getLogger();
    private static Instant lastLogTimestamp = Instant.now();

    @Override
    public ActionResult execute() {
        HashMap<String, HashMap<String, Long>> studentLatestLogs = new HashMap<>();
        List<FeedbackSessionLogEntryAttributes> validLogEntries = new ArrayList<>();
        List<FeedbackSessionLogEntryAttributes> allLogEntries =
                logsProcessor.getFeedbackSessionLogs(null, null, lastLogTimestamp.toEpochMilli(), Long.MAX_VALUE, null);

        for (FeedbackSessionLogEntryAttributes logEntry : allLogEntries) {
            String studentEmail = logEntry.getStudentEmail();
            String logType = logEntry.getFeedbackSessionLogType();
            Long logTimestamp = logEntry.getTimestamp();
            HashMap<String, Long> studentLog = new HashMap<>();
            boolean isValid = true;

            if (studentLatestLogs.containsKey(studentEmail)) {
                studentLog = studentLatestLogs.get(studentEmail);

                if (studentLog.containsKey(logType)) {
                    Long lastLogTimestamp = studentLog.get(logType);

                    if (Math.abs(lastLogTimestamp - logTimestamp) < 2 * 1000) {
                        isValid = false;
                    }
                }
            }

            if (isValid) {
                validLogEntries.add(logEntry);

                studentLog.put(logType, logTimestamp);
                studentLatestLogs.put(studentEmail, studentLog);
            }
        }


        lastLogTimestamp = Instant.now();

        try {
            List<FeedbackSessionLogEntryAttributes> createdEntries = logic.createFeedbackSessionLogs(validLogEntries);
            System.out.println("______________________________________________________");
            for (FeedbackSessionLogEntryAttributes entry : createdEntries) {
                System.out.println(entry.toString());
            }
            System.out.println("______________________________________________________");
        } catch (InvalidParametersException e) {
            log.severe("Unexpected error", e);
        }

        return new JsonResult("Successful");
    }

}
