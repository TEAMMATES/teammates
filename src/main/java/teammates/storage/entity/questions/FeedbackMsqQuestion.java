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
import teammates.common.datatransfer.questions.FeedbackMsqQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.storage.entity.FeedbackQuestion;

/**
 * Represents an msq question.
 */
@Entity
public class FeedbackMsqQuestion extends FeedbackQuestion {

    @Column(nullable = false, columnDefinition = "TEXT")
    @Convert(converter = FeedbackMsqQuestionDetailsConverter.class)
    private FeedbackMsqQuestionDetails questionDetails;

    protected FeedbackMsqQuestion() {
        // required by Hibernate
    }

    public FeedbackMsqQuestion(
            Integer questionNumber,
            String description, QuestionGiverType giverType, QuestionRecipientType recipientType,
            Integer numOfEntitiesToGiveFeedbackTo, List<ViewerType> showResponsesTo,
            List<ViewerType> showGiverNameTo, List<ViewerType> showRecipientNameTo,
            FeedbackQuestionDetails feedbackQuestionDetails
    ) {
        super(questionNumber, description, giverType, recipientType,
                numOfEntitiesToGiveFeedbackTo, showResponsesTo, showGiverNameTo, showRecipientNameTo);
        setFeedBackQuestionDetails((FeedbackMsqQuestionDetails) feedbackQuestionDetails);
    }

    @Override
    public FeedbackQuestionType getQuestionType() {
        return FeedbackQuestionType.MSQ;
    }

    @Override
    public FeedbackQuestionDetails getQuestionDetailsCopy() {
        return questionDetails.getDeepCopy();
    }

    @Override
    public FeedbackMsqQuestion makeDeepCopy() {
        return new FeedbackMsqQuestion(
                this.getQuestionNumber(), this.getDescription(), this.getGiverType(),
                this.getRecipientType(), this.getNumOfEntitiesToGiveFeedbackTo(), new ArrayList<>(this.getShowResponsesTo()),
                new ArrayList<>(this.getShowGiverNameTo()), new ArrayList<>(this.getShowRecipientNameTo()),
                this.questionDetails.getDeepCopy()
        );
    }

    @Override
    public void setQuestionDetails(FeedbackQuestionDetails questionDetails) {
        this.questionDetails = (FeedbackMsqQuestionDetails) questionDetails;
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
            extends FeedbackQuestionDetailsConverter {
    }
}
