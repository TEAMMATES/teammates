package teammates.ui.output;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import teammates.storage.entity.FeedbackSessionLog;

/**
 * The API output format for logs on all feedback sessions in a course.
 */
public class FeedbackSessionLogsData implements ApiOutput {

    private final Map<String, List<FeedbackSessionLogData>> feedbackSessionLogs;

    public FeedbackSessionLogsData(List<FeedbackSessionLog> groupedEntries) {
        this.feedbackSessionLogs = new LinkedHashMap<>();
        for (FeedbackSessionLog logEntry : groupedEntries) {
            String sessionId = logEntry.getSessionId().toString();
            List<FeedbackSessionLogData> fsLogEntryDatas =
                    this.feedbackSessionLogs.computeIfAbsent(sessionId, k -> new ArrayList<>());
            fsLogEntryDatas.add(new FeedbackSessionLogData(logEntry));
        }
    }

    public Map<String, List<FeedbackSessionLogData>> getFeedbackSessionLogs() {
        return feedbackSessionLogs;
    }
}
