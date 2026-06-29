package teammates.ui.request;

import java.util.UUID;

import teammates.common.datatransfer.SessionKeyType;
import teammates.ui.exception.InvalidHttpRequestBodyException;

/**
 * The request body for checking access to a student session link.
 */
public class SessionKeyAccessRequest extends BasicRequest {
    private UUID feedbackSessionId;
    private String key;
    private SessionKeyType type;

    @Override
    public void validate() throws InvalidHttpRequestBodyException {
        validateTrue(feedbackSessionId != null, "Feedback session ID cannot be null");
        validateTrue(key != null, "Key cannot be null");
        validateTrue(type != null, "Session key type cannot be null");
    }

    public UUID getFeedbackSessionId() {
        return feedbackSessionId;
    }

    public void setFeedbackSessionId(UUID feedbackSessionId) {
        this.feedbackSessionId = feedbackSessionId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public SessionKeyType getType() {
        return type;
    }

    public void setType(SessionKeyType type) {
        this.type = type;
    }
}
