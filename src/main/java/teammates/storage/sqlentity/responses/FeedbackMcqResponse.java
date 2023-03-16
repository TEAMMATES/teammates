package teammates.storage.sqlentity.responses;

import teammates.common.datatransfer.questions.FeedbackMcqResponseDetails;
import teammates.storage.sqlentity.FeedbackResponse;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;

/**
 * Represents a feedback mcq response.
 */
@Entity
public class FeedbackMcqResponse extends FeedbackResponse {

    @Column(nullable = false)
    @Convert(converter = FeedbackMcqResponseDetailsConverter.class)
    private FeedbackMcqResponseDetails answer;

    protected FeedbackMcqResponse() {
        // required by Hibernate
    }

    public FeedbackMcqResponseDetails getAnswer() {
        return answer;
    }

    public void setAnswer(FeedbackMcqResponseDetails answer) {
        this.answer = answer;
    }

    @Override
    public String toString() {
        return "FeedbackMcqResponse [id=" + super.getId()
            + ", createdAt=" + super.getCreatedAt() + ", updatedAt=" + super.getUpdatedAt() + "]";
    }

    /**
     * Converter for FeedbackMcqResponse specific attributes.
     */
    @Converter
    public static class FeedbackMcqResponseDetailsConverter
            extends FeedbackQuestionDetailsConverter {
    }
}
