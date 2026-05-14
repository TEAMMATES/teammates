package teammates.storage.entity.questions;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;

import teammates.common.datatransfer.participanttypes.QuestionGiverType;
import teammates.common.datatransfer.participanttypes.QuestionRecipientType;
import teammates.common.datatransfer.participanttypes.ViewerType;
import teammates.common.datatransfer.questions.FeedbackNumericalScaleQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.storage.entity.FeedbackQuestion;

/**
 * Represents a numerical scale question.
 */
@Entity
public class FeedbackNumericalScaleQuestion extends FeedbackQuestion {

    @Column(nullable = false, columnDefinition = "TEXT")
    @Convert(converter = FeedbackNumericalScaleQuestionDetailsConverter.class)
    private FeedbackNumericalScaleQuestionDetails questionDetails;

    protected FeedbackNumericalScaleQuestion() {
        // required by Hibernate
    }

    public FeedbackNumericalScaleQuestion(
            Integer questionNumber,
            String description, QuestionGiverType giverType, QuestionRecipientType recipientType,
            Integer numOfEntitiesToGiveFeedbackTo, List<ViewerType> showResponsesTo,
            List<ViewerType> showGiverNameTo, List<ViewerType> showRecipientNameTo,
            FeedbackQuestionDetails feedbackQuestionDetails
    ) {
        super(questionNumber, description, giverType, recipientType,
                numOfEntitiesToGiveFeedbackTo, showResponsesTo, showGiverNameTo, showRecipientNameTo);
        setFeedBackQuestionDetails((FeedbackNumericalScaleQuestionDetails) feedbackQuestionDetails);
    }

    @Override
    public FeedbackQuestionType getQuestionType() {
        return FeedbackQuestionType.NUMSCALE;
    }

    @Override
    public FeedbackQuestionDetails getQuestionDetailsCopy() {
        return questionDetails.getDeepCopy();
    }

    @Override
    public FeedbackNumericalScaleQuestion makeDeepCopy() {
        return new FeedbackNumericalScaleQuestion(
                this.getQuestionNumber(), this.getDescription(), this.getGiverType(),
                this.getRecipientType(), this.getNumOfEntitiesToGiveFeedbackTo(), new ArrayList<>(this.getShowResponsesTo()),
                new ArrayList<>(this.getShowGiverNameTo()), new ArrayList<>(this.getShowRecipientNameTo()),
                this.questionDetails.getDeepCopy()
        );
    }

    @Override
    public void setQuestionDetails(FeedbackQuestionDetails questionDetails) {
        this.questionDetails = (FeedbackNumericalScaleQuestionDetails) questionDetails;
    }

    @Override
    public String toString() {
        return "FeedbackNumericalScaleQuestion [id=" + super.getId()
                + ", createdAt=" + super.getCreatedAt() + ", updatedAt=" + super.getUpdatedAt() + "]";
    }

    public void setFeedBackQuestionDetails(FeedbackNumericalScaleQuestionDetails questionDetails) {
        this.questionDetails = questionDetails;
    }

    public FeedbackNumericalScaleQuestionDetails getFeedbackQuestionDetails() {
        return questionDetails;
    }

    /**
     * Converter for FeedbackNumericalScaleQuestion specific attributes.
     */
    @Converter
    public static class FeedbackNumericalScaleQuestionDetailsConverter
            extends FeedbackQuestionDetailsConverter {
    }
}
