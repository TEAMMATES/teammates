package teammates.storage.sqlentity.responses;

import teammates.common.datatransfer.questions.FeedbackNumericalScaleResponseDetails;
import teammates.storage.sqlentity.FeedbackResponse;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;

/**
 * Represents a feedback numerical scale response.
 */
@Entity
public class FeedbackNumericalScaleResponse extends FeedbackResponse {

    @Column(nullable = false)
    @Convert(converter = FeedbackNumericalScaleResponseDetailsConverter.class)
    private FeedbackNumericalScaleResponseDetails answer;

    protected FeedbackNumericalScaleResponse() {
        // required by Hibernate
    }

    public FeedbackNumericalScaleResponseDetails getAnswer() {
        return answer;
    }

    public void setAnswer(FeedbackNumericalScaleResponseDetails answer) {
        this.answer = answer;
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
            extends JsonConverter<FeedbackNumericalScaleResponseDetails> {
    }
}
