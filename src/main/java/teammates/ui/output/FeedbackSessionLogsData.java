package teammates.ui.output;

import java.util.List;

/**
 * The API output format for logs on all feedback sessions in a course.
 */
public class FeedbackSessionLogsData extends ApiOutput {

    private final List<FeedbackSessionLogData> feedbackSessionLogs;

    public FeedbackSessionLogsData(List<FeedbackSessionLogData> feedbackSessionLogs) {
        this.feedbackSessionLogs = feedbackSessionLogs;
    }

    public List<FeedbackSessionLogData> getFeedbackResponseLogs() {
        return feedbackSessionLogs;
    }
}
