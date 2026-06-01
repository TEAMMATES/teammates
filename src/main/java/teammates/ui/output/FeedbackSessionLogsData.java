package teammates.ui.output;

import java.util.List;
import java.util.Map;

import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.FeedbackSessionLog;
import teammates.storage.entity.User;

/**
 * The API output format for logs on all feedback sessions in a course.
 */
public class FeedbackSessionLogsData extends ApiOutput {

    private final List<FeedbackSessionLogData> feedbackSessionLogs;

    public FeedbackSessionLogsData(Map<String, List<FeedbackSessionLog>> groupedEntries,
            Map<String, User> usersMap, Map<String, FeedbackSession> sessionsMap) {
        this.feedbackSessionLogs = groupedEntries.entrySet().stream()
                .map(entry -> {
                    FeedbackSession feedbackSession = sessionsMap.get(entry.getKey());
                    List<FeedbackSessionLog> logEntries = entry.getValue();
                    return new FeedbackSessionLogData(feedbackSession, logEntries, usersMap);
                })
                .toList();
    }

    public List<FeedbackSessionLogData> getFeedbackSessionLogs() {
        return feedbackSessionLogs;
    }
}
