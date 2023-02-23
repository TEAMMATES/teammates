package teammates.storage.sqlentity.questions;

import teammates.common.datatransfer.questions.FeedbackTextQuestionDetails;
import teammates.storage.sqlentity.FeedbackQuestion;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;

/**
 * Represents a FeedbackTextQuestion entity.
 */
@Entity
public class FeedbackTextQuestion extends FeedbackQuestion {

    @Column(nullable = false)
    @Convert(converter = FeedbackTextQuestionDetailsConverter.class)
    private String questionDetails;

    @Transient
    private FeedbackTextQuestionDetailsConverter converter = new FeedbackTextQuestionDetailsConverter();

    protected FeedbackTextQuestion() {
        // required by Hibernate
    }

    @Override
    public String toString() {
        return "FeedbackTextQuestion [id=" + super.getId() + ", createdAt=" + super.getCreatedAt()
                + ", updatedAt=" + super.getUpdatedAt() + "]";
    }

    public void setFeedBackQuestionDetails(FeedbackTextQuestionDetails questionDetails) {
        this.questionDetails = converter.convertToDatabaseColumn(questionDetails);
    }

    public FeedbackTextQuestionDetails getFeedbackQuestionDetails() {
        return converter.convertToEntityAttribute(questionDetails);
    }

    /**
     * Converter for FeedbackTextQuestion specific attributes.
     */
    @Converter
    public static class FeedbackTextQuestionDetailsConverter
            extends JsonConverter<FeedbackTextQuestionDetails> {
    }
}
