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
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.datatransfer.questions.FeedbackRubricQuestionDetails;
import teammates.storage.entity.FeedbackQuestion;

/**
 * Represents a rubric question.
 */
@Entity
public class FeedbackRubricQuestion extends FeedbackQuestion {

    @Column(nullable = false, columnDefinition = "TEXT")
    @Convert(converter = FeedbackRubricQuestionDetailsConverter.class)
    private FeedbackRubricQuestionDetails questionDetails;

    protected FeedbackRubricQuestion() {
        // required by Hibernate
    }

    public FeedbackRubricQuestion(
            Integer questionNumber,
            String description, QuestionGiverType giverType, QuestionRecipientType recipientType,
            Integer numOfEntitiesToGiveFeedbackTo, List<ViewerType> showResponsesTo,
            List<ViewerType> showGiverNameTo, List<ViewerType> showRecipientNameTo,
            FeedbackQuestionDetails feedbackQuestionDetails
    ) {
        super(questionNumber, description, giverType, recipientType,
                numOfEntitiesToGiveFeedbackTo, showResponsesTo, showGiverNameTo, showRecipientNameTo);
        setFeedBackQuestionDetails((FeedbackRubricQuestionDetails) feedbackQuestionDetails);
    }

    @Override
    public FeedbackQuestionType getQuestionType() {
        return FeedbackQuestionType.RUBRIC;
    }

    @Override
    public FeedbackQuestionDetails getQuestionDetailsCopy() {
        return questionDetails.getDeepCopy();
    }

    @Override
    public FeedbackRubricQuestion makeDeepCopy() {
        return new FeedbackRubricQuestion(
                this.getQuestionNumber(), this.getDescription(), this.getGiverType(),
                this.getRecipientType(), this.getNumOfEntitiesToGiveFeedbackTo(), new ArrayList<>(this.getShowResponsesTo()),
                new ArrayList<>(this.getShowGiverNameTo()), new ArrayList<>(this.getShowRecipientNameTo()),
                this.questionDetails.getDeepCopy()
        );
    }

    @Override
    public void setQuestionDetails(FeedbackQuestionDetails questionDetails) {
        this.questionDetails = (FeedbackRubricQuestionDetails) questionDetails;
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
            extends FeedbackQuestionDetailsConverter {
    }
}
