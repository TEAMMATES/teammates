package teammates.storage.sqlentity.responses;

import teammates.common.datatransfer.questions.FeedbackMsqResponseDetails;
import teammates.storage.sqlentity.FeedbackResponse;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;

/**
 * Represents a feedback msq response.
 */
@Entity
public class FeedbackMsqResponse extends FeedbackResponse {

    @Column(nullable = false)
    @Convert(converter = FeedbackMsqResponseDetailsConverter.class)
    private FeedbackMsqResponseDetails answer;

    protected FeedbackMsqResponse() {
        // required by Hibernate
    }

    public FeedbackMsqResponseDetails getAnswer() {
        return answer;
    }

    public void setAnswer(FeedbackMsqResponseDetails answer) {
        this.answer = answer;
    }

    @Override
    public String toString() {
        return "FeedbackMsqResponse [id=" + super.getId()
            + ", createdAt=" + super.getCreatedAt() + ", updatedAt=" + super.getUpdatedAt() + "]";
    }

    /**
     * Converter for FeedbackMsqResponse specific attributes.
     */
    @Converter
    public static class FeedbackMsqResponseDetailsConverter
            extends FeedbackResponseDetailsConverter {
    }
}
