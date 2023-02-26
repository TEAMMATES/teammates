package teammates.storage.sqlentity.questions;

import teammates.common.datatransfer.questions.FeedbackMsqQuestionDetails;
import teammates.storage.sqlentity.FeedbackQuestion;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;

/**
 * Represents an msq question.
 */
@Entity
public class FeedbackMsqQuestion extends FeedbackQuestion {

    @Column(nullable = false)
    @Convert(converter = FeedbackMsqQuestionDetailsConverter.class)
    private FeedbackMsqQuestionDetails questionDetails;

    protected FeedbackMsqQuestion() {
        // required by Hibernate
    }

    @Override
    public String toString() {
        return "FeedbackMsqQuestion [id=" + super.getId()
                + ", createdAt=" + super.getCreatedAt() + ", updatedAt=" + super.getUpdatedAt() + "]";
    }

    public void setFeedBackQuestionDetails(FeedbackMsqQuestionDetails questionDetails) {
        this.questionDetails = questionDetails;
    }

    public FeedbackMsqQuestionDetails getFeedbackQuestionDetails() {
        return questionDetails;
    }

    /**
     * Converter for FeedbackMsqQuestion specific attributes.
     */
    @Converter
    public static class FeedbackMsqQuestionDetailsConverter
            extends JsonConverter<FeedbackMsqQuestionDetails> {
    }
}
