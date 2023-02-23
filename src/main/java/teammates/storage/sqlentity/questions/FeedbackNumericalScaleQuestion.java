package teammates.storage.sqlentity.questions;

import teammates.common.datatransfer.questions.FeedbackNumericalScaleQuestionDetails;
import teammates.storage.sqlentity.FeedbackQuestion;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;

/**
 * Represents a FeedbackNumericalScaleQuestion entity.
 */
@Entity
public class FeedbackNumericalScaleQuestion extends FeedbackQuestion {

    @Column(nullable = false)
    @Convert(converter = FeedbackNumericalScaleQuestionDetailsConverter.class)
    private String questionDetails;

    @Transient
    private FeedbackNumericalScaleQuestionDetailsConverter converter = new FeedbackNumericalScaleQuestionDetailsConverter();

    protected FeedbackNumericalScaleQuestion() {
        // required by Hibernate
    }

    @Override
    public String toString() {
        return "FeedbackNumericalScaleQuestion [id=" + super.getId()
                + ", createdAt=" + super.getCreatedAt() + ", updatedAt=" + super.getUpdatedAt() + "]";
    }

    public void setFeedBackQuestionDetails(FeedbackNumericalScaleQuestionDetails questionDetails) {
        this.questionDetails = converter.convertToDatabaseColumn(questionDetails);
    }

    public FeedbackNumericalScaleQuestionDetails getFeedbackQuestionDetails() {
        return converter.convertToEntityAttribute(questionDetails);
    }

    /**
     * Converter for FeedbackNumericalScaleQuestion specific attributes.
     */
    @Converter
    public static class FeedbackNumericalScaleQuestionDetailsConverter
            extends JsonConverter<FeedbackNumericalScaleQuestionDetails> {
    }
}
