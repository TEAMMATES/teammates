package teammates.storage.sqlentity.questions;

import teammates.common.datatransfer.questions.FeedbackMcqQuestionDetails;
import teammates.storage.sqlentity.FeedbackQuestion;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;

/**
 * Represents an mcq question.
 */
@Entity
public class FeedbackMcqQuestion extends FeedbackQuestion {

    @Column(nullable = false)
    @Convert(converter = FeedbackMcqQuestionDetailsConverter.class)
    private FeedbackMcqQuestionDetails questionDetails;

    protected FeedbackMcqQuestion() {
        // required by Hibernate
    }

    @Override
    public String toString() {
        return "FeedbackMcqQuestion [id=" + super.getId()
                + ", createdAt=" + super.getCreatedAt() + ", updatedAt=" + super.getUpdatedAt() + "]";
    }

    public void setFeedBackQuestionDetails(FeedbackMcqQuestionDetails questionDetails) {
        this.questionDetails = questionDetails;
    }

    public FeedbackMcqQuestionDetails getFeedbackQuestionDetails() {
        return questionDetails;
    }

    /**
     * Converter for FeedbackMcqQuestion specific attributes.
     */
    @Converter
    public static class FeedbackMcqQuestionDetailsConverter
            extends JsonConverter<FeedbackMcqQuestionDetails> {
    }
}
