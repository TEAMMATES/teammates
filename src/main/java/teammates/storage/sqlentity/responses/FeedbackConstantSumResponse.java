package teammates.storage.sqlentity.responses;

import teammates.common.datatransfer.questions.FeedbackConstantSumResponseDetails;
import teammates.storage.sqlentity.FeedbackResponse;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;

/**
 * Represents a feedback constant sum response.
 */
@Entity
public class FeedbackConstantSumResponse extends FeedbackResponse {

    @Column(nullable = false)
    @Convert(converter = FeedbackConstantSumResponseDetailsConverter.class)
    private FeedbackConstantSumResponseDetails answer;

    protected FeedbackConstantSumResponse() {
        // required by Hibernate
    }

    public FeedbackConstantSumResponseDetails getAnswer() {
        return answer;
    }

    public void setAnswer(FeedbackConstantSumResponseDetails answer) {
        this.answer = answer;
    }

    @Override
    public String toString() {
        return "FeedbackConstantSumResponse [id=" + super.getId()
            + ", createdAt=" + super.getCreatedAt() + ", updatedAt=" + super.getUpdatedAt() + "]";
    }

    /**
     * Converter for FeedbackConstantSumResponse specific attributes.
     */
    @Converter
    public static class FeedbackConstantSumResponseDetailsConverter
            extends JsonConverter<FeedbackConstantSumResponseDetails> {
    }
}
