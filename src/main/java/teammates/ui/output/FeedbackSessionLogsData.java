package teammates.ui.output;

import java.util.List;

/**
 * The API output format for logs on all feedback sessions in a course.
 */
public class FeedbackSessionLogsData extends ApiOutput {

    private final List<FeedbackSessionLog> feedbackSessionLogs;

    public FeedbackSessionLogsData(List<FeedbackSessionLog> feedbackSessionLogs) {
        this.feedbackSessionLogs = feedbackSessionLogs;
    }

    public List<FeedbackSessionLog> getFeedbackResponseLogs() {
        return feedbackSessionLogs;
    }
}
