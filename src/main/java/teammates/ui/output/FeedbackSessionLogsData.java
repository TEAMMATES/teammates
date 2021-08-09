package teammates.ui.output;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import teammates.common.datatransfer.FeedbackSessionLogEntry;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;

/**
 * The API output format for logs on all feedback sessions in a course.
 */
public class FeedbackSessionLogsData extends ApiOutput {

    private final List<FeedbackSessionLogData> feedbackSessionLogs;

    public FeedbackSessionLogsData(Map<FeedbackSessionAttributes, List<FeedbackSessionLogEntry>> groupedEntries) {
        List<FeedbackSessionLogData> fsLogDatas = groupedEntries.entrySet().stream()
                .map(entry -> {
                    FeedbackSessionAttributes feedbackSession = entry.getKey();
                    List<FeedbackSessionLogEntry> logEntries = entry.getValue();
                    return new FeedbackSessionLogData(feedbackSession, logEntries);
                })
                .collect(Collectors.toList());
        this.feedbackSessionLogs = fsLogDatas;
    }

    public List<FeedbackSessionLogData> getFeedbackSessionLogs() {
        return feedbackSessionLogs;
    }
}
