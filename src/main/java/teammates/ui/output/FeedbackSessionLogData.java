package teammates.ui.output;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.FeedbackSessionLog;
import teammates.storage.entity.Student;

/**
 * The response log of a single feedback session.
 */
public class FeedbackSessionLogData {
    private final FeedbackSessionData feedbackSessionData;
    private final List<FeedbackSessionLogEntryData> feedbackSessionLogEntries;

    public FeedbackSessionLogData(FeedbackSession feedbackSession, List<FeedbackSessionLog> logEntries,
            Map<String, Student> studentsMap) {
        FeedbackSessionData fsData = new FeedbackSessionData(feedbackSession);
        List<FeedbackSessionLogEntryData> fsLogEntryDatas = logEntries.stream()
                .map(log -> {
                    Student student = studentsMap.get(log.getStudent().getEmail());
                    return new FeedbackSessionLogEntryData(log, student);
                })
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
