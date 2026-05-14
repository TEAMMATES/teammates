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
import teammates.common.datatransfer.questions.FeedbackRankOptionsQuestionDetails;
import teammates.storage.entity.FeedbackQuestion;

/**
 * Represents a rank options question.
 */
@Entity
public class FeedbackRankOptionsQuestion extends FeedbackQuestion {

    @Column(nullable = false, columnDefinition = "TEXT")
    @Convert(converter = FeedbackRankOptionsQuestionDetailsConverter.class)
    private FeedbackRankOptionsQuestionDetails questionDetails;

    protected FeedbackRankOptionsQuestion() {
        // required by Hibernate
    }

    public FeedbackRankOptionsQuestion(
            Integer questionNumber,
            String description, QuestionGiverType giverType, QuestionRecipientType recipientType,
            Integer numOfEntitiesToGiveFeedbackTo, List<ViewerType> showResponsesTo,
            List<ViewerType> showGiverNameTo, List<ViewerType> showRecipientNameTo,
            FeedbackQuestionDetails feedbackQuestionDetails) {
        super(questionNumber, description, giverType, recipientType,
                numOfEntitiesToGiveFeedbackTo, showResponsesTo, showGiverNameTo, showRecipientNameTo);
        setFeedBackQuestionDetails((FeedbackRankOptionsQuestionDetails) feedbackQuestionDetails);
    }

    @Override
    public FeedbackQuestionType getQuestionType() {
        return FeedbackQuestionType.RANK_OPTIONS;
    }

    @Override
    public FeedbackQuestionDetails getQuestionDetailsCopy() {
        return questionDetails.getDeepCopy();
    }

    @Override
    public FeedbackRankOptionsQuestion makeDeepCopy() {
        return new FeedbackRankOptionsQuestion(
                this.getQuestionNumber(), this.getDescription(), this.getGiverType(),
                this.getRecipientType(), this.getNumOfEntitiesToGiveFeedbackTo(),
                new ArrayList<>(this.getShowResponsesTo()), new ArrayList<>(this.getShowGiverNameTo()),
                new ArrayList<>(this.getShowRecipientNameTo()),
                this.questionDetails.getDeepCopy());
    }

    @Override
    public void setQuestionDetails(FeedbackQuestionDetails questionDetails) {
        this.questionDetails = (FeedbackRankOptionsQuestionDetails) questionDetails;
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
            extends FeedbackQuestionDetailsConverter {
    }
}
