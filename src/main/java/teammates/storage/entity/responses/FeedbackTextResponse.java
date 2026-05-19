package teammates.storage.entity.responses;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;

import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.common.datatransfer.questions.FeedbackTextResponseDetails;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.ResponseGiver;
import teammates.storage.entity.ResponseRecipient;

/**
 * Represents a text response.
 */
@Entity
public class FeedbackTextResponse extends FeedbackResponse {

    @Column(nullable = false, columnDefinition = "TEXT")
    @Convert(converter = FeedbackTextResponseDetailsConverter.class)
    private FeedbackTextResponseDetails answer;

    protected FeedbackTextResponse() {
        // required by Hibernate
    }

    public FeedbackTextResponse(
            ResponseGiver giver, ResponseRecipient recipient,
            FeedbackResponseDetails responseDetails
    ) {
        super(giver, recipient);
        this.setAnswer((FeedbackTextResponseDetails) responseDetails);
    }

    public FeedbackTextResponseDetails getAnswer() {
        return answer;
    }

    public void setAnswer(FeedbackTextResponseDetails answer) {
        this.answer = answer;
    }

    @Override
    public FeedbackResponseDetails getFeedbackResponseDetailsCopy() {
        return getAnswer();
    }

    @Override
    public String toString() {
        return "FeedbackTextResponse [id=" + super.getId()
                + ", createdAt=" + super.getCreatedAt() + ", updatedAt=" + super.getUpdatedAt() + "]";
    }

    /**
     * Converter for FeedbackMcqResponse specific attributes.
     */
    @Converter
    public static class FeedbackTextResponseDetailsConverter
            extends FeedbackResponseDetailsConverter {
    }
}
