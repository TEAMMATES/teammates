package teammates.ui.output;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import teammates.common.datatransfer.FeedbackSessionLogEntry;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;

/**
 * The API output format for logs on all feedback sessions in a course.
 */
public class FeedbackSessionLogsData extends ApiOutput {

    private final List<FeedbackSessionLogData> feedbackSessionLogs;

    public FeedbackSessionLogsData(Map<String, List<FeedbackSessionLogEntry>> groupedEntries,
            Map<String, StudentAttributes> studentsMap, Map<String, FeedbackSessionAttributes> sessionsMap) {
        this.feedbackSessionLogs = groupedEntries.entrySet().stream()
                .map(entry -> {
                    FeedbackSessionAttributes feedbackSession = sessionsMap.get(entry.getKey());
                    List<FeedbackSessionLogEntry> logEntries = entry.getValue();
                    return new FeedbackSessionLogData(feedbackSession, logEntries, studentsMap);
                })
                .collect(Collectors.toList());
    }

    public List<FeedbackSessionLogData> getFeedbackSessionLogs() {
        return feedbackSessionLogs;
    }
}
