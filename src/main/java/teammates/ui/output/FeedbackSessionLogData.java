package teammates.ui.output;

import java.util.List;

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

    public FeedbackSessionData getFeedbackSessionData() {
        return feedbackSessionData;
    }

    public List<FeedbackSessionLogEntryData> getStudentResponseAccessLogs() {
        return feedbackSessionLogEntries;
    }
}
