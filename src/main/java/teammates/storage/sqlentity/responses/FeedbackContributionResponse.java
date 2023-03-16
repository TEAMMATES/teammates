package teammates.storage.sqlentity.responses;

import teammates.common.datatransfer.questions.FeedbackContributionResponseDetails;
import teammates.storage.sqlentity.FeedbackResponse;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;

/**
 * Represents a feedback contribution response.
 */
@Entity
public class FeedbackContributionResponse extends FeedbackResponse {

    @Column(nullable = false)
    @Convert(converter = FeedbackContributionResponseDetailsConverter.class)
    private FeedbackContributionResponseDetails answer;

    protected FeedbackContributionResponse() {
        // required by Hibernate
    }

    public FeedbackContributionResponseDetails getAnswer() {
        return answer;
    }

    public void setAnswer(FeedbackContributionResponseDetails answer) {
        this.answer = answer;
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
            extends FeedbackQuestionDetailsConverter {
    }
}
