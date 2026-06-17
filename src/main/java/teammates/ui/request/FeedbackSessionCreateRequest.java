package teammates.ui.request;

import java.util.UUID;

import jakarta.annotation.Nullable;

import teammates.ui.exception.InvalidHttpRequestBodyException;

/**
 * The request body format for creation of feedback session.
 */
public class FeedbackSessionCreateRequest extends FeedbackSessionBasicRequest {
    private String feedbackSessionName;
    @Nullable
    private UUID toCopySessionId;

    public String getFeedbackSessionName() {
        return feedbackSessionName;
    }

    public UUID getToCopySessionId() {
        return toCopySessionId;
    }

    public void setFeedbackSessionName(String feedbackSessionName) {
        this.feedbackSessionName = feedbackSessionName;
    }

    public void setToCopySessionId(UUID toCopySessionId) {
        this.toCopySessionId = toCopySessionId;
    }

    @Override
    public void validate() throws InvalidHttpRequestBodyException {
        super.validate();

        validateTrue(feedbackSessionName != null, "Session name cannot be null");
        validateTrue(!feedbackSessionName.isEmpty(), "Session name cannot be empty");
    }
}
