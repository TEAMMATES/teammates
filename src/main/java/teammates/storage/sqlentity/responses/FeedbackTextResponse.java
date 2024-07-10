package teammates.storage.sqlentity.responses;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;

import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.common.datatransfer.questions.FeedbackTextResponseDetails;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackResponse;
import teammates.storage.sqlentity.Section;

/**
 * Represents a text response.
 */
@Entity
public class FeedbackTextResponse extends FeedbackResponse {

    @Column(nullable = false)
    @Convert(converter = FeedbackTextResponseDetailsConverter.class)
    private FeedbackTextResponseDetails answer;

    protected FeedbackTextResponse() {
        // required by Hibernate
    }

    public FeedbackTextResponse(
            FeedbackQuestion feedbackQuestion, String giver,
            Section giverSection, String recipient, Section recipientSection,
            FeedbackResponseDetails responseDetails
    ) {
        super(feedbackQuestion, giver, giverSection, recipient, recipientSection);
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
        return answer;
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
