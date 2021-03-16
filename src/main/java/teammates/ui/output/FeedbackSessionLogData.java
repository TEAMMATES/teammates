package teammates.ui.output;

import java.util.List;
import java.util.stream.Collectors;

import teammates.common.datatransfer.FeedbackSessionLogEntry;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;

/**
 * The response log of a single feedback session.
 */
public class FeedbackSessionLogData {
    private final FeedbackSessionData feedbackSessionData;
    private final List<FeedbackSessionLogEntryData> feedbackSessionLogEntries;

    public FeedbackSessionLogData(FeedbackSessionData feedbackSessionData,
                              List<FeedbackSessionLogEntryData> feedbackSessionLogEntries) {
        this.feedbackSessionData = feedbackSessionData;
        this.feedbackSessionLogEntries = feedbackSessionLogEntries;
    }

    public FeedbackSessionLogData(FeedbackSessionAttributes feedbackSession, List<FeedbackSessionLogEntry> logEntries) {
        FeedbackSessionData fsData = new FeedbackSessionData(feedbackSession);
        List<FeedbackSessionLogEntryData> fsLogEntryDatas = logEntries.stream()
                .map(FeedbackSessionLogEntryData::new)
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
    public List<FeedbackSessionLogEntryData> getfeedbackSessionLogEntries() {
        return feedbackSessionLogEntries;
    }
}
