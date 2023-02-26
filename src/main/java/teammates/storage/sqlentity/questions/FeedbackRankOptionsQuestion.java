package teammates.storage.sqlentity.questions;

import teammates.common.datatransfer.questions.FeedbackRankOptionsQuestionDetails;
import teammates.storage.sqlentity.FeedbackQuestion;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;

/**
 * Represents a rank options question.
 */
@Entity
public class FeedbackRankOptionsQuestion extends FeedbackQuestion {

    @Column(nullable = false)
    @Convert(converter = FeedbackRankOptionsQuestionDetailsConverter.class)
    private FeedbackRankOptionsQuestionDetails questionDetails;

    protected FeedbackRankOptionsQuestion() {
        // required by Hibernate
    }

    @Override
    public String toString() {
        return "FeedbackRankOptionsQuestion [id=" + super.getId()
                + ", createdAt=" + super.getCreatedAt() + ", updatedAt=" + super.getUpdatedAt() + "]";
    }

    public void setFeedBackQuestionDetails(FeedbackRankOptionsQuestionDetails questionDetails) {
        this.questionDetails = questionDetails;
    }

    public FeedbackRankOptionsQuestionDetails getFeedbackQuestionDetails() {
        return questionDetails;
    }

    /**
     * Converter for FeedbackRankOptionsQuestion specific attributes.
     */
    @Converter
    public static class FeedbackRankOptionsQuestionDetailsConverter
            extends JsonConverter<FeedbackRankOptionsQuestionDetails> {
    }
}
