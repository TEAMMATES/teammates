package teammates.ui.output;

import java.util.UUID;

import teammates.storage.entity.DeadlineExtension;

/**
 * The API output format of a single {@link DeadlineExtension}.
 */
public class DeadlineExtensionData implements ApiOutput {

    private final UUID feedbackSessionId;
    private final UUID userId;
    private final Long userDeadlineExtension;

    public DeadlineExtensionData(DeadlineExtension de) {
        this.feedbackSessionId = de.getFeedbackSession().getId();
        this.userId = de.getUserId();
        this.userDeadlineExtension = de.getEndTime().toEpochMilli();
    }

    public UUID getFeedbackSessionId() {
        return feedbackSessionId;
    }

    public UUID getUserId() {
        return userId;
    }

    public Long getUserDeadlineExtension() {
        return userDeadlineExtension;
    }
}
