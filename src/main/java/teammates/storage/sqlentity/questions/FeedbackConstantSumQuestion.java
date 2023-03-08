package teammates.storage.sqlentity.questions;

import teammates.common.datatransfer.questions.FeedbackConstantSumQuestionDetails;
import teammates.storage.sqlentity.FeedbackQuestion;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;

/**
 * Represents a constant sum question.
 */
@Entity
public class FeedbackConstantSumQuestion extends FeedbackQuestion {

    @Column(nullable = false)
    @Convert(converter = FeedbackConstantSumQuestionDetailsConverter.class)
    private FeedbackConstantSumQuestionDetails questionDetails;

    protected FeedbackConstantSumQuestion() {
        // required by Hibernate
    }

    @Override
    public String toString() {
        return "FeedbackConstantSumQuestion [id=" + super.getId()
                + ", createdAt=" + super.getCreatedAt() + ", updatedAt=" + super.getUpdatedAt() + "]";
    }

    public void setFeedBackQuestionDetails(FeedbackConstantSumQuestionDetails questionDetails) {
        this.questionDetails = questionDetails;
    }

    public FeedbackConstantSumQuestionDetails getFeedbackQuestionDetails() {
        return questionDetails;
    }

    /**
     * Converter for FeedbackConstantSumQuestion specific attributes.
     */
    @Converter
    public static class FeedbackConstantSumQuestionDetailsConverter
            extends JsonConverter<FeedbackConstantSumQuestionDetails> {
    }
}
