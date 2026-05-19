package teammates.storage.entity.responses;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;

import teammates.common.datatransfer.questions.FeedbackContributionResponseDetails;
import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.ResponseGiver;
import teammates.storage.entity.ResponseRecipient;

/**
 * Represents a feedback contribution response.
 */
@Entity
public class FeedbackContributionResponse extends FeedbackResponse {

    @Column(nullable = false, columnDefinition = "TEXT")
    @Convert(converter = FeedbackContributionResponseDetailsConverter.class)
    private FeedbackContributionResponseDetails answer;

    protected FeedbackContributionResponse() {
        // required by Hibernate
    }

    public FeedbackContributionResponse(
            ResponseGiver giver, ResponseRecipient recipient,
            FeedbackResponseDetails responseDetails
    ) {
        super(giver, recipient);
        this.setAnswer((FeedbackContributionResponseDetails) responseDetails);
    }

    public FeedbackContributionResponseDetails getAnswer() {
        return answer;
    }

    public void setAnswer(FeedbackContributionResponseDetails answer) {
        this.answer = answer;
    }

    @Override
    public FeedbackResponseDetails getFeedbackResponseDetailsCopy() {
        return answer.getDeepCopy();
    }

    @Override
    public String toString() {
        return "FeedbackContributionResponse [id=" + super.getId()
                + ", createdAt=" + super.getCreatedAt() + ", updatedAt=" + super.getUpdatedAt() + "]";
    }

    /**
     * Converter for FeedbackContributionResponse specific attributes.
     */
    @Converter
    public static class FeedbackContributionResponseDetailsConverter
            extends FeedbackResponseDetailsConverter {
    }
}
