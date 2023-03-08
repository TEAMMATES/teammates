package teammates.storage.sqlentity.questions;

import teammates.common.datatransfer.questions.FeedbackRankRecipientsQuestionDetails;
import teammates.storage.sqlentity.FeedbackQuestion;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;

/**
 * Represents a rank recipients question.
 */
@Entity
public class FeedbackRankRecipientsQuestion extends FeedbackQuestion {

    @Column(nullable = false)
    @Convert(converter = FeedbackRankRecipientsQuestionDetailsConverter.class)
    private FeedbackRankRecipientsQuestionDetails questionDetails;

    protected FeedbackRankRecipientsQuestion() {
        // required by Hibernate
    }

    @Override
    public String toString() {
        return "FeedbackRankRecipientsQuestion [id=" + super.getId()
                + ", createdAt=" + super.getCreatedAt() + ", updatedAt=" + super.getUpdatedAt() + "]";
    }

    public void setFeedBackQuestionDetails(FeedbackRankRecipientsQuestionDetails questionDetails) {
        this.questionDetails = questionDetails;
    }

    public FeedbackRankRecipientsQuestionDetails getFeedbackQuestionDetails() {
        return questionDetails;
    }

    /**
     * Converter for FeedbackRankaRecipientsQuestion specific attributes.
     */
    @Converter
    public static class FeedbackRankRecipientsQuestionDetailsConverter
            extends JsonConverter<FeedbackRankRecipientsQuestionDetails> {
    }
}
