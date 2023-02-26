package teammates.storage.sqlentity.questions;

import teammates.common.datatransfer.questions.FeedbackRubricQuestionDetails;
import teammates.storage.sqlentity.FeedbackQuestion;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;

/**
 * Represents a rubric question.
 */
@Entity
public class FeedbackRubricQuestion extends FeedbackQuestion {

    @Column(nullable = false)
    @Convert(converter = FeedbackRubricQuestionDetailsConverter.class)
    private FeedbackRubricQuestionDetails questionDetails;

    protected FeedbackRubricQuestion() {
        // required by Hibernate
    }

    @Override
    public String toString() {
        return "FeedbackRubricQuestion [id=" + super.getId()
                + ", createdAt=" + super.getCreatedAt() + ", updatedAt=" + super.getUpdatedAt() + "]";
    }

    public void setFeedBackQuestionDetails(FeedbackRubricQuestionDetails questionDetails) {
        this.questionDetails = questionDetails;
    }

    public FeedbackRubricQuestionDetails getFeedbackQuestionDetails() {
        return questionDetails;
    }

    /**
     * Converter for FeedbackRubricQuestion specific attributes.
     */
    @Converter
    public static class FeedbackRubricQuestionDetailsConverter
            extends JsonConverter<FeedbackRubricQuestionDetails> {
    }
}
