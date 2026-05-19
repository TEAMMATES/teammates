package teammates.storage.entity.responses;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;

import teammates.common.datatransfer.questions.FeedbackMcqResponseDetails;
import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.ResponseGiver;
import teammates.storage.entity.ResponseRecipient;

/**
 * Represents a feedback mcq response.
 */
@Entity
public class FeedbackMcqResponse extends FeedbackResponse {

    @Column(nullable = false, columnDefinition = "TEXT")
    @Convert(converter = FeedbackMcqResponseDetailsConverter.class)
    private FeedbackMcqResponseDetails answer;

    protected FeedbackMcqResponse() {
        // required by Hibernate
    }

    public FeedbackMcqResponse(
            ResponseGiver giver, ResponseRecipient recipient,
            FeedbackResponseDetails responseDetails
    ) {
        super(giver, recipient);
        this.setAnswer((FeedbackMcqResponseDetails) responseDetails);
    }

    public FeedbackMcqResponseDetails getAnswer() {
        return answer;
    }

    public void setAnswer(FeedbackMcqResponseDetails answer) {
        this.answer = answer;
    }

    @Override
    public FeedbackResponseDetails getFeedbackResponseDetailsCopy() {
        return answer.getDeepCopy();
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
            extends FeedbackResponseDetailsConverter {
    }
}
