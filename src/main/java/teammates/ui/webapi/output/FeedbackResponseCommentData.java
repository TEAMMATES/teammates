package teammates.ui.webapi.output;

import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;

/**
 * The API output format of {@link teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes}.
 */
public class FeedbackResponseCommentData extends ApiOutput {

    private long feedbackResponseCommentId;

    public FeedbackResponseCommentData(FeedbackResponseCommentAttributes frc) {
        this.feedbackResponseCommentId = frc.getId();
        // TODO
    }

    public long getFeedbackResponseCommentId() {
        return feedbackResponseCommentId;
    }

}
