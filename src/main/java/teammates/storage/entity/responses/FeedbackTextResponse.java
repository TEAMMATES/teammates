package teammates.storage.entity.responses;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;

import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.common.datatransfer.questions.FeedbackTextResponseDetails;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.Section;

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
            String giver, Section giverSection, String recipient, Section recipientSection,
            FeedbackResponseDetails responseDetails
    ) {
        super(giver, giverSection, recipient, recipientSection);
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
