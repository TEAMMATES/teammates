package teammates.ui.request;

import jakarta.annotation.Nullable;

/**
 * The request body format for creation of feedback session.
 */
public class FeedbackSessionCreateRequest extends FeedbackSessionBasicRequest {
    private String feedbackSessionName;
    @Nullable
    private String toCopyCourseId;
    @Nullable
    private String toCopySessionName;

    public String getFeedbackSessionName() {
        return feedbackSessionName;
    }

    public String getToCopyCourseId() {
        return toCopyCourseId;
    }

    public String getToCopySessionName() {
        return toCopySessionName;
    }

    public void setFeedbackSessionName(String feedbackSessionName) {
        this.feedbackSessionName = feedbackSessionName;
    }

    public void setToCopyCourseId(String toCopyCourseId) {
        this.toCopyCourseId = toCopyCourseId;
    }

    public void setToCopySessionName(String toCopySessionName) {
        this.toCopySessionName = toCopySessionName;
    }

    @Override
    public void validate() throws InvalidHttpRequestBodyException {
        super.validate();

        assertTrue(feedbackSessionName != null, "Session name cannot be null");
        assertTrue(!feedbackSessionName.isEmpty(), "Session name cannot be empty");
        assertTrue(toCopyCourseId == null || toCopySessionName != null,
                "Session name to be copied from cannot be null if course ID to be copied from is not null");
    }
}
