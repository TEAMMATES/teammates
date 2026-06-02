package teammates.storage.entity.responses;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;

import teammates.common.datatransfer.questions.FeedbackConstantSumRecipientsResponseDetails;
import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.ResponseGiver;
import teammates.storage.entity.ResponseRecipient;

/**
 * Represents a feedback constant sum recipients response.
 */
@Entity
public class FeedbackConstantSumRecipientsResponse extends FeedbackResponse {

    @Column(nullable = false, columnDefinition = "TEXT")
    @Convert(converter = FeedbackConstantSumRecipientsResponseDetailsConverter.class)
    private FeedbackConstantSumRecipientsResponseDetails answer;

    protected FeedbackConstantSumRecipientsResponse() {
        // required by Hibernate
    }

    public FeedbackConstantSumRecipientsResponse(
            ResponseGiver giver, ResponseRecipient recipient,
            FeedbackResponseDetails responseDetails, @Nullable String giverComment
    ) {
        super(giver, recipient, giverComment);
        this.setAnswer((FeedbackConstantSumRecipientsResponseDetails) responseDetails);
    }

    public FeedbackConstantSumRecipientsResponseDetails getAnswer() {
        return answer;
    }

    public void setAnswer(FeedbackConstantSumRecipientsResponseDetails answer) {
        this.answer = answer;
    }

    @Override
    public FeedbackResponseDetails getFeedbackResponseDetailsCopy() {
        return answer.getDeepCopy();
    }

    @Override
    public void setFeedbackResponseDetails(FeedbackResponseDetails responseDetails) {
        setAnswer(castResponseDetails(responseDetails, FeedbackConstantSumRecipientsResponseDetails.class));
    }

    @Override
    public String toString() {
        return "FeedbackConstantSumRecipientsResponse [id=" + super.getId()
                + ", createdAt=" + super.getCreatedAt() + ", updatedAt=" + super.getUpdatedAt() + "]";
    }

    /**
     * Converter for FeedbackConstantSumRecipientsResponse specific attributes.
     */
    @Converter
    public static class FeedbackConstantSumRecipientsResponseDetailsConverter
            extends FeedbackResponseDetailsConverter {
    }
}
