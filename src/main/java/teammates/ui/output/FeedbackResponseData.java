package teammates.ui.output;

import java.util.UUID;

import jakarta.annotation.Nullable;

import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.storage.sqlentity.FeedbackResponse;

/**
 * The API output format of {@link FeedbackResponse}.
 */
public class FeedbackResponseData extends ApiOutput {

    private final UUID feedbackResponseId;

    private final String giverIdentifier;

    private final String recipientIdentifier;

    private final FeedbackResponseDetails responseDetails;

    @Nullable
    private FeedbackResponseCommentData giverComment;

    public FeedbackResponseData(FeedbackResponse feedbackResponse) {
        this.feedbackResponseId = feedbackResponse.getId();
        this.giverIdentifier = feedbackResponse.getGiver();
        this.recipientIdentifier = feedbackResponse.getRecipient();
        this.responseDetails = feedbackResponse.getFeedbackResponseDetailsCopy();
    }

    public UUID getFeedbackResponseId() {
        return feedbackResponseId;
    }

    public String getGiverIdentifier() {
        return giverIdentifier;
    }

    public String getRecipientIdentifier() {
        return recipientIdentifier;
    }

    public FeedbackResponseDetails getResponseDetails() {
        return responseDetails;
    }

    public FeedbackResponseCommentData getGiverComment() {
        return giverComment;
    }

    public void setGiverComment(FeedbackResponseCommentData giverComment) {
        this.giverComment = giverComment;
    }

}
