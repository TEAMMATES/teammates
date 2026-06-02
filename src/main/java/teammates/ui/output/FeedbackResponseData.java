package teammates.ui.output;

import java.util.UUID;

import jakarta.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;

import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.storage.entity.FeedbackResponse;

/**
 * The API output format of {@link FeedbackResponse}.
 */
public class FeedbackResponseData implements ApiOutput {

    private final UUID feedbackResponseId;

    private final String giverIdentifier;

    private final String recipientIdentifier;

    private final FeedbackResponseDetails responseDetails;

    @Nullable
    private String giverComment;

    @JsonCreator
    private FeedbackResponseData(UUID feedbackResponseId, String giverIdentifier,
            String recipientIdentifier, FeedbackResponseDetails responseDetails, @Nullable String giverComment) {
        this.feedbackResponseId = feedbackResponseId;
        this.giverIdentifier = giverIdentifier;
        this.recipientIdentifier = recipientIdentifier;
        this.responseDetails = responseDetails;
        this.giverComment = giverComment;
    }

    public FeedbackResponseData(FeedbackResponse feedbackResponse) {
        this.feedbackResponseId = feedbackResponse.getId();
        this.giverIdentifier = feedbackResponse.getGiver().getIdentifier();
        this.recipientIdentifier = feedbackResponse.getRecipient().getIdentifier();
        this.responseDetails = feedbackResponse.getFeedbackResponseDetailsCopy();
        this.giverComment = feedbackResponse.getGiverComment();
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

    public String getGiverComment() {
        return giverComment;
    }

    public void setGiverComment(String giverComment) {
        this.giverComment = giverComment;
    }

}
