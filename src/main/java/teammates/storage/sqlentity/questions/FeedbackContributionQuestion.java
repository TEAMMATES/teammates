package teammates.storage.sqlentity.questions;

import teammates.common.datatransfer.questions.FeedbackContributionQuestionDetails;
import teammates.storage.sqlentity.FeedbackQuestion;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;

/**
 * Represents a contribution question.
 */
@Entity
public class FeedbackContributionQuestion extends FeedbackQuestion {

    @Column(nullable = false)
    @Convert(converter = FeedbackContributionQuestionDetailsConverter.class)
    private FeedbackContributionQuestionDetails questionDetails;

    protected FeedbackContributionQuestion() {
        // required by Hibernate
    }

    @Override
    public String toString() {
        return "FeedbackContributionQuestion [id=" + super.getId()
                + ", createdAt=" + super.getCreatedAt() + ", updatedAt=" + super.getUpdatedAt() + "]";
    }

    public void setFeedBackQuestionDetails(FeedbackContributionQuestionDetails questionDetails) {
        this.questionDetails = questionDetails;
    }

    public FeedbackContributionQuestionDetails getFeedbackQuestionDetails() {
        return questionDetails;
    }

    /**
     * Converter for FeedbackContributionQuestion specific attributes.
     */
    @Converter
    public static class FeedbackContributionQuestionDetailsConverter
            extends JsonConverter<FeedbackContributionQuestionDetails> {
    }
}
