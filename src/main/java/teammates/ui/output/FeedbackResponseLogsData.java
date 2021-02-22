package teammates.ui.output;

import java.util.List;

/**
 * The API output format for logs on student's feedback session access and response.
 */
public class FeedbackResponseLogsData extends ApiOutput {

    private final List<FeedbackResponseLog> feedbackResponseLogs;

    public FeedbackResponseLogsData(List<FeedbackResponseLog> feedbackResponseLogs) {
        this.feedbackResponseLogs = feedbackResponseLogs;
    }

    public List<FeedbackResponseLog> getFeedbackResponseLogs() {
        return feedbackResponseLogs;
    }
}
