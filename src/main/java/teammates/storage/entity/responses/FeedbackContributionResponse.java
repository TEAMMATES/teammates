package teammates.storage.entity.responses;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;

import teammates.common.datatransfer.questions.FeedbackContributionResponseDetails;
import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.Section;

/**
 * Represents a feedback contribution response.
 */
@Entity
public class FeedbackContributionResponse extends FeedbackResponse {

    @Column(nullable = false, columnDefinition = "TEXT")
    @Convert(converter = FeedbackContributionResponseDetailsConverter.class)
    private FeedbackContributionResponseDetails answer;

    protected FeedbackContributionResponse() {
        // required by Hibernate
    }

    public FeedbackContributionResponse(
            FeedbackQuestion feedbackQuestion, String giver,
            Section giverSection, String recipient, Section recipientSection,
            FeedbackResponseDetails responseDetails
    ) {
        super(feedbackQuestion, giver, giverSection, recipient, recipientSection);
        this.setAnswer((FeedbackContributionResponseDetails) responseDetails);
    }

    public FeedbackContributionResponseDetails getAnswer() {
        return answer;
    }

    public void setAnswer(FeedbackContributionResponseDetails answer) {
        this.answer = answer;
    }

    @Override
    public FeedbackResponseDetails getFeedbackResponseDetailsCopy() {
        return answer.getDeepCopy();
    }

    @Override
    public String toString() {
        return "FeedbackContributionResponse [id=" + super.getId()
                + ", createdAt=" + super.getCreatedAt() + ", updatedAt=" + super.getUpdatedAt() + "]";
    }

    /**
     * Converter for FeedbackContributionResponse specific attributes.
     */
    @Converter
    public static class FeedbackContributionResponseDetailsConverter
            extends FeedbackResponseDetailsConverter {
    }
}
