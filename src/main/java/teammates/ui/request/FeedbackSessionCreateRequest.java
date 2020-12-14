package teammates.ui.request;

/**
 * The request body format for creation of feedback session.
 */
public class FeedbackSessionCreateRequest extends FeedbackSessionBasicRequest {
    private String feedbackSessionName;

    public String getFeedbackSessionName() {
        return feedbackSessionName;
    }

    public void setFeedbackSessionName(String feedbackSessionName) {
        this.feedbackSessionName = feedbackSessionName;
    }

    @Override
    public void validate() {
        super.validate();

        assertTrue(feedbackSessionName != null, "Session name cannot be null");
        assertTrue(!feedbackSessionName.isEmpty(), "Session name cannot be empty");
    }
}
