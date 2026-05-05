package teammates.storage.entity.responses;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;

import teammates.common.datatransfer.questions.FeedbackConstantSumResponseDetails;
import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.Section;

/**
 * Represents a feedback constant sum response.
 */
@Entity
public class FeedbackConstantSumResponse extends FeedbackResponse {

    @Column(nullable = false, columnDefinition = "TEXT")
    @Convert(converter = FeedbackConstantSumResponseDetailsConverter.class)
    private FeedbackConstantSumResponseDetails answer;

    protected FeedbackConstantSumResponse() {
        // required by Hibernate
    }

    public FeedbackConstantSumResponse(
            String giver, Section giverSection, String recipient, Section recipientSection,
            FeedbackResponseDetails responseDetails
    ) {
        super(giver, giverSection, recipient, recipientSection);
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
