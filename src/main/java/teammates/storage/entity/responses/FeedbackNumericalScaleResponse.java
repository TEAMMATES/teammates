package teammates.storage.entity.responses;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;

import teammates.common.datatransfer.questions.FeedbackNumericalScaleResponseDetails;
import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.ResponseGiver;
import teammates.storage.entity.ResponseRecipient;

/**
 * Represents a feedback numerical scale response.
 */
@Entity
public class FeedbackNumericalScaleResponse extends FeedbackResponse {

    @Column(nullable = false, columnDefinition = "TEXT")
    @Convert(converter = FeedbackNumericalScaleResponseDetailsConverter.class)
    private FeedbackNumericalScaleResponseDetails answer;

    protected FeedbackNumericalScaleResponse() {
        // required by Hibernate
    }

    public FeedbackNumericalScaleResponse(
            ResponseGiver giver, ResponseRecipient recipient,
            FeedbackResponseDetails responseDetails
    ) {
        super(giver, recipient);
        this.setAnswer((FeedbackNumericalScaleResponseDetails) responseDetails);
    }

    public FeedbackNumericalScaleResponseDetails getAnswer() {
        return answer;
    }

    public void setAnswer(FeedbackNumericalScaleResponseDetails answer) {
        this.answer = answer;
    }

    @Override
    public FeedbackResponseDetails getFeedbackResponseDetailsCopy() {
        return answer.getDeepCopy();
    }

    @Override
    public String toString() {
        return "FeedbackTextResponse [id=" + super.getId()
                + ", createdAt=" + super.getCreatedAt() + ", updatedAt=" + super.getUpdatedAt() + "]";
    }

    /**
     * Converter for FeedbackNumericalScaleQuestion specific attributes.
     */
    @Converter
    public static class FeedbackNumericalScaleResponseDetailsConverter
            extends FeedbackResponseDetailsConverter {
    }
}
