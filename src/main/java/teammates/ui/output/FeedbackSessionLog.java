package teammates.ui.output;

import java.util.List;

/**
 * The response log of a single feedback session.
 */
public class FeedbackSessionLog {
    private final FeedbackSessionData feedbackSessionData;

    private final List<FeedbackSessionLogEntry> feedbackSessionLogEntries;

    public FeedbackSessionLog(FeedbackSessionData feedbackSessionData,
                              List<FeedbackSessionLogEntry> feedbackSessionLogEntries) {
        this.feedbackSessionData = feedbackSessionData;
        this.feedbackSessionLogEntries = feedbackSessionLogEntries;
    }

    public FeedbackSessionData getFeedbackSessionData() {
        return feedbackSessionData;
    }

    public List<FeedbackSessionLogEntry> getStudentResponseAccessLogs() {
        return feedbackSessionLogEntries;
    }
}
