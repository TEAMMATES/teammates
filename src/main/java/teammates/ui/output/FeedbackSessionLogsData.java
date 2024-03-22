package teammates.ui.output;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import teammates.common.datatransfer.FeedbackSessionLogEntry;

/**
 * The API output format for logs on all feedback sessions in a course.
 */
public class FeedbackSessionLogsData extends ApiOutput {

    private final List<FeedbackSessionLogData> feedbackSessionLogs;

    // Remove generic types after migration is done (i.e. can just use FeedbackSession and Student)
    public <S, T> FeedbackSessionLogsData(Map<String, List<FeedbackSessionLogEntry>> groupedEntries,
            Map<String, S> studentsMap, Map<String, T> sessionsMap) {
        this.feedbackSessionLogs = groupedEntries.entrySet().stream()
                .map(entry -> {
                    T feedbackSession = sessionsMap.get(entry.getKey());
                    List<FeedbackSessionLogEntry> logEntries = entry.getValue();
                    return new FeedbackSessionLogData(feedbackSession, logEntries, studentsMap);
                })
                .collect(Collectors.toList());
    }

    public List<FeedbackSessionLogData> getFeedbackSessionLogs() {
        return feedbackSessionLogs;
    }
}
