package teammates.storage.entity.responses;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;

import teammates.common.datatransfer.questions.FeedbackMsqResponseDetails;
import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.ResponseGiver;
import teammates.storage.entity.ResponseRecipient;

/**
 * Represents a feedback msq response.
 */
@Entity
public class FeedbackMsqResponse extends FeedbackResponse {

    @Column(nullable = false, columnDefinition = "TEXT")
    @Convert(converter = FeedbackMsqResponseDetailsConverter.class)
    private FeedbackMsqResponseDetails answer;

    protected FeedbackMsqResponse() {
        // required by Hibernate
    }

    public FeedbackMsqResponse(
            ResponseGiver giver, ResponseRecipient recipient,
            FeedbackResponseDetails responseDetails
    ) {
        super(giver, recipient);
        this.setAnswer((FeedbackMsqResponseDetails) responseDetails);
    }

    public FeedbackMsqResponseDetails getAnswer() {
        return answer;
    }

    public void setAnswer(FeedbackMsqResponseDetails answer) {
        this.answer = answer;
    }

    @Override
    public FeedbackResponseDetails getFeedbackResponseDetailsCopy() {
        return answer.getDeepCopy();
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
