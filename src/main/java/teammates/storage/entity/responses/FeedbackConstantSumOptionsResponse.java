package teammates.storage.entity.responses;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;

import teammates.common.datatransfer.questions.FeedbackConstantSumOptionsResponseDetails;
import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.ResponseGiver;
import teammates.storage.entity.ResponseRecipient;

/**
 * Represents a feedback constant sum options response.
 */
@Entity
public class FeedbackConstantSumOptionsResponse extends FeedbackResponse {

    @Column(nullable = false, columnDefinition = "TEXT")
    @Convert(converter = FeedbackConstantSumOptionsResponseDetailsConverter.class)
    private FeedbackConstantSumOptionsResponseDetails answer;

    protected FeedbackConstantSumOptionsResponse() {
        // required by Hibernate
    }

    public FeedbackConstantSumOptionsResponse(
            ResponseGiver giver, ResponseRecipient recipient,
            FeedbackResponseDetails responseDetails, @Nullable String giverComment
    ) {
        super(giver, recipient, giverComment);
        this.setAnswer((FeedbackConstantSumOptionsResponseDetails) responseDetails);
    }

    public FeedbackConstantSumOptionsResponseDetails getAnswer() {
        return answer;
    }

    public void setAnswer(FeedbackConstantSumOptionsResponseDetails answer) {
        this.answer = answer;
    }

    @Override
    public FeedbackResponseDetails getFeedbackResponseDetailsCopy() {
        return answer.getDeepCopy();
    }

    @Override
    public void setFeedbackResponseDetails(FeedbackResponseDetails responseDetails) {
        setAnswer(castResponseDetails(responseDetails, FeedbackConstantSumOptionsResponseDetails.class));
    }

    @Override
    public String toString() {
        return "FeedbackConstantSumOptionsResponse [id=" + super.getId()
                + ", createdAt=" + super.getCreatedAt() + ", updatedAt=" + super.getUpdatedAt() + "]";
    }

    /**
     * Converter for FeedbackConstantSumOptionsResponse specific attributes.
     */
    @Converter
    public static class FeedbackConstantSumOptionsResponseDetailsConverter
            extends FeedbackResponseDetailsConverter {
    }
}
