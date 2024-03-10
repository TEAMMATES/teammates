package teammates.storage.sqlentity.responses;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;

import teammates.common.datatransfer.questions.FeedbackConstantSumResponseDetails;
import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackResponse;
import teammates.storage.sqlentity.Section;

/**
 * Represents a feedback constant sum response.
 */
@Entity
public class FeedbackConstantSumResponse extends FeedbackResponse {

    @Column(nullable = false)
    @Convert(converter = FeedbackConstantSumResponseDetailsConverter.class)
    private FeedbackConstantSumResponseDetails answer;

    protected FeedbackConstantSumResponse() {
        // required by Hibernate
    }

    public FeedbackConstantSumResponse(
            FeedbackQuestion feedbackQuestion, String giver,
            Section giverSection, String recipient, Section recipientSection,
            FeedbackResponseDetails responseDetails
    ) {
        super(feedbackQuestion, giver, giverSection, recipient, recipientSection);
        this.setAnswer((FeedbackConstantSumResponseDetails) responseDetails);
    }

    public FeedbackConstantSumResponseDetails getAnswer() {
        return answer;
    }

    public void setAnswer(FeedbackConstantSumResponseDetails answer) {
        this.answer = answer;
    }

    @Override
    public FeedbackResponseDetails getFeedbackResponseDetailsCopy() {
        return answer.getDeepCopy();
    }

    @Override
    public String toString() {
        return "FeedbackConstantSumResponse [id=" + super.getId()
            + ", createdAt=" + super.getCreatedAt() + ", updatedAt=" + super.getUpdatedAt() + "]";
    }

    /**
     * Converter for FeedbackConstantSumResponse specific attributes.
     */
    @Converter
    public static class FeedbackConstantSumResponseDetailsConverter
            extends FeedbackResponseDetailsConverter {
    }
}
