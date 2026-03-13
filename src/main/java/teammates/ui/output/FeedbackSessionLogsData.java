package teammates.ui.output;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.FeedbackSessionLog;
import teammates.storage.sqlentity.Student;

/**
 * The API output format for logs on all feedback sessions in a course.
 */
public class FeedbackSessionLogsData extends ApiOutput {

    private final List<FeedbackSessionLogData> feedbackSessionLogs;

    public FeedbackSessionLogsData(Map<String, List<FeedbackSessionLog>> groupedEntries,
            Map<String, Student> studentsMap, Map<String, FeedbackSession> sessionsMap) {
        this.feedbackSessionLogs = groupedEntries.entrySet().stream()
                .map(entry -> {
                    FeedbackSession feedbackSession = sessionsMap.get(entry.getKey());
                    List<FeedbackSessionLog> logEntries = entry.getValue();
                    return new FeedbackSessionLogData(feedbackSession, logEntries, studentsMap);
                })
                .collect(Collectors.toList());
    }

    public List<FeedbackSessionLogData> getFeedbackSessionLogs() {
        return feedbackSessionLogs;
    }
}
