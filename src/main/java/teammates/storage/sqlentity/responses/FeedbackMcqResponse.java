package teammates.storage.sqlentity.responses;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;

import teammates.common.datatransfer.questions.FeedbackMcqResponseDetails;
import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackResponse;
import teammates.storage.sqlentity.Section;

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

    public FeedbackMcqResponse(
            FeedbackQuestion feedbackQuestion, String giver,
            Section giverSection, String recipient, Section recipientSection,
            FeedbackResponseDetails responseDetails
    ) {
        super(feedbackQuestion, giver, giverSection, recipient, recipientSection);
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
