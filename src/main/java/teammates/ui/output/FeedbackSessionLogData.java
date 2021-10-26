package teammates.ui.output;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import teammates.common.datatransfer.FeedbackSessionLogEntry;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;

/**
 * The response log of a single feedback session.
 */
public class FeedbackSessionLogData {
    private final FeedbackSessionData feedbackSessionData;
    private final List<FeedbackSessionLogEntryData> feedbackSessionLogEntries;

    public FeedbackSessionLogData(FeedbackSessionAttributes feedbackSession, List<FeedbackSessionLogEntry> logEntries,
            Map<String, StudentAttributes> studentsMap) {
        FeedbackSessionData fsData = new FeedbackSessionData(feedbackSession);
        List<FeedbackSessionLogEntryData> fsLogEntryDatas = logEntries.stream()
                .map(log -> new FeedbackSessionLogEntryData(log, studentsMap.get(log.getStudentEmail())))
                .collect(Collectors.toList());
        this.feedbackSessionData = fsData;
        this.feedbackSessionLogEntries = fsLogEntryDatas;
    }

    public FeedbackSessionData getFeedbackSessionData() {
        return feedbackSessionData;
    }

    /**
     * Returns all feedback session log entries.
     */
    public List<FeedbackSessionLogEntryData> getFeedbackSessionLogEntries() {
        return feedbackSessionLogEntries;
    }
}
