package teammates.storage.sqlentity.responses;

import teammates.common.datatransfer.questions.FeedbackRubricResponseDetails;
import teammates.storage.sqlentity.FeedbackResponse;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;

/**
 * Represents a feedback rubric response.
 */
@Entity
public class FeedbackRubricResponse extends FeedbackResponse {

    @Column(nullable = false)
    @Convert(converter = FeedbackRubricResponseDetailsConverter.class)
    private FeedbackRubricResponseDetails answer;

    protected FeedbackRubricResponse() {
        // required by Hibernate
    }

    public FeedbackRubricResponseDetails getAnswer() {
        return answer;
    }

    public void setAnswer(FeedbackRubricResponseDetails answer) {
        this.answer = answer;
    }

    @Override
    public String toString() {
        return "FeedbackRubricResponse [id=" + super.getId()
            + ", createdAt=" + super.getCreatedAt() + ", updatedAt=" + super.getUpdatedAt() + "]";
    }

    /**
     * Converter for FeedbackRubricResponse specific attributes.
     */
    @Converter
    public static class FeedbackRubricResponseDetailsConverter
            extends JsonConverter<FeedbackRubricResponseDetails> {
    }
}
