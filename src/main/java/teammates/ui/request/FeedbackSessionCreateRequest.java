package teammates.ui.request;

import javax.annotation.Nullable;

/**
 * The request body format for creation of feedback session.
 */
public class FeedbackSessionCreateRequest extends FeedbackSessionBasicRequest {
    private String feedbackSessionName;
    @Nullable
    private String toCopyCourseId;
    @Nullable
    private String oldSessionName;

    public String getFeedbackSessionName() {
        return feedbackSessionName;
    }

    public String getToCopyCourseId() {
        return toCopyCourseId;
    }

    public String getOldSessionName() {
        return oldSessionName;
    }

    public void setFeedbackSessionName(String feedbackSessionName) {
        this.feedbackSessionName = feedbackSessionName;
    }

    public void setToCopyCourseId(String toCopyCourseId) {
        this.toCopyCourseId = toCopyCourseId;
    }

    public void setOldSessionName(String oldSessionName) {
        this.oldSessionName = oldSessionName;
    }

    @Override
    public void validate() {
        super.validate();

        assertTrue(feedbackSessionName != null, "Session name cannot be null");
        assertTrue(!feedbackSessionName.isEmpty(), "Session name cannot be empty");
    }
}
