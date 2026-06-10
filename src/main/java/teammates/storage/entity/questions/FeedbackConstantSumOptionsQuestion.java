package teammates.storage.entity.questions;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;

import teammates.common.datatransfer.participanttypes.QuestionGiverType;
import teammates.common.datatransfer.participanttypes.QuestionRecipientType;
import teammates.common.datatransfer.questions.FeedbackConstantSumOptionsQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.datatransfer.visibility.FeedbackVisibilityType;
import teammates.storage.entity.FeedbackQuestion;

/**
 * Represents a constant sum options question.
 */
@Entity
public class FeedbackConstantSumOptionsQuestion extends FeedbackQuestion {

    @Column(nullable = false, columnDefinition = "TEXT")
    @Convert(converter = FeedbackConstantSumOptionsQuestionDetailsConverter.class)
    private FeedbackConstantSumOptionsQuestionDetails questionDetails;

    protected FeedbackConstantSumOptionsQuestion() {
        // required by Hibernate
    }

    public FeedbackConstantSumOptionsQuestion(
            Integer questionNumber,
            String description, QuestionGiverType giverType, QuestionRecipientType recipientType,
            Integer numOfEntitiesToGiveFeedbackTo, List<FeedbackVisibilityType> showResponsesTo,
            List<FeedbackVisibilityType> showGiverNameTo, List<FeedbackVisibilityType> showRecipientNameTo,
            FeedbackQuestionDetails feedbackQuestionDetails
    ) {
        super(questionNumber, description, giverType, recipientType,
                numOfEntitiesToGiveFeedbackTo, showResponsesTo, showGiverNameTo, showRecipientNameTo);
        setFeedBackQuestionDetails((FeedbackConstantSumOptionsQuestionDetails) feedbackQuestionDetails);
    }

    @Override
    public FeedbackQuestionType getQuestionType() {
        return questionDetails.getQuestionType();
    }

    @Override
    public FeedbackQuestionDetails getQuestionDetailsCopy() {
        return questionDetails.getDeepCopy();
    }

    @Override
    public FeedbackConstantSumOptionsQuestion makeDeepCopy() {
        return new FeedbackConstantSumOptionsQuestion(
                this.getQuestionNumber(), this.getDescription(), this.getGiverType(),
                this.getRecipientType(), this.getNumOfEntitiesToGiveFeedbackTo(), new ArrayList<>(this.getShowResponsesTo()),
                new ArrayList<>(this.getShowGiverNameTo()), new ArrayList<>(this.getShowRecipientNameTo()),
                this.questionDetails.getDeepCopy()
        );
    }

    @Override
    public void setQuestionDetails(FeedbackQuestionDetails questionDetails) {
        this.questionDetails = (FeedbackConstantSumOptionsQuestionDetails) questionDetails;
    }

    @Override
    public String toString() {
        return "FeedbackConstantSumOptionsQuestion [id=" + super.getId()
                + ", createdAt=" + super.getCreatedAt() + ", updatedAt=" + super.getUpdatedAt() + "]";
    }

    public void setFeedBackQuestionDetails(FeedbackConstantSumOptionsQuestionDetails questionDetails) {
        this.questionDetails = questionDetails;
    }

    public FeedbackConstantSumOptionsQuestionDetails getFeedbackQuestionDetails() {
        return questionDetails;
    }

    /**
     * Converter for FeedbackConstantSumOptionsQuestion specific attributes.
     */
    @Converter
    public static class FeedbackConstantSumOptionsQuestionDetailsConverter
            extends FeedbackQuestionDetailsConverter {
    }
}
