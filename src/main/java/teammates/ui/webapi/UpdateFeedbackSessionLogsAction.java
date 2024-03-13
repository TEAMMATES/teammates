package teammates.ui.webapi;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.FeedbackSessionLogEntry;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Logger;
import teammates.common.util.TimeHelper;
import teammates.storage.sqlentity.FeedbackSessionLog;

/**
 * Process feedback session logs in the past defined time period and store in
 * the database.
 */
public class UpdateFeedbackSessionLogsAction extends AdminOnlyAction {

    static final int COLLECTION_TIME_PERIOD = 60; // represents one hour
    static final long SPAM_FILTER = 2000L; // in ms
    private static final Logger log = Logger.getLogger();

    @Override
    public JsonResult execute() {
        List<FeedbackSessionLog> filteredLogs = new ArrayList<>();

        Instant endTime = TimeHelper.getInstantNearestHourBefore(Instant.now());
        Instant startTime = endTime.minus(COLLECTION_TIME_PERIOD, ChronoUnit.MINUTES);

        List<FeedbackSessionLogEntry> logEntries = logsProcessor.getFeedbackSessionLogs(null, null,
                startTime.toEpochMilli(), endTime.toEpochMilli(), null);

        Map<String, Map<String, Long>> lastSavedByStudentAndType = new HashMap<>();
        for (FeedbackSessionLogEntry logEntry : logEntries) {
            String email = logEntry.getStudentEmail();
            String fbSessionName = logEntry.getFeedbackSessionName();
            String type = logEntry.getFeedbackSessionLogType();
            Long timestamp = logEntry.getTimestamp();

            lastSavedByStudentAndType.putIfAbsent(email, new HashMap<>());
            Long lastSaved = lastSavedByStudentAndType.get(email).getOrDefault(type, 0L);

            if (Math.abs(timestamp - lastSaved) > SPAM_FILTER) {
                lastSavedByStudentAndType.get(email).put(type, timestamp);
                FeedbackSessionLog fslEntity = new FeedbackSessionLog(email, fbSessionName, type,
                        Instant.ofEpochMilli(timestamp));
                filteredLogs.add(fslEntity);
            }
        }

        try {
            sqlLogic.createFeedbackSessionLogs(filteredLogs);
        } catch (InvalidParametersException | EntityAlreadyExistsException e) {
            log.severe("Unexpected error", e);
        }

        return new JsonResult("Successful");
    }
}
