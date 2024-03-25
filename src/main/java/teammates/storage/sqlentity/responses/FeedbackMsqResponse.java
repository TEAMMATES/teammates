package teammates.storage.sqlentity.responses;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;

import teammates.common.datatransfer.questions.FeedbackMsqResponseDetails;
import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackResponse;
import teammates.storage.sqlentity.Section;

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

    public FeedbackMsqResponse(
            FeedbackQuestion feedbackQuestion, String giver,
            Section giverSection, String recipient, Section recipientSection,
            FeedbackResponseDetails responseDetails
    ) {
        super(feedbackQuestion, giver, giverSection, recipient, recipientSection);
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
