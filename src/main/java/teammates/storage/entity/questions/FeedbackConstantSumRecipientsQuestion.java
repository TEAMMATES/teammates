package teammates.storage.entity.questions;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;

import teammates.common.datatransfer.participanttypes.QuestionGiverType;
import teammates.common.datatransfer.participanttypes.QuestionRecipientType;
import teammates.common.datatransfer.questions.FeedbackConstantSumRecipientsQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.datatransfer.visibility.FeedbackVisibilityType;
import teammates.storage.entity.FeedbackQuestion;

/**
 * Represents a constant sum recipients question.
 */
@Entity
public class FeedbackConstantSumRecipientsQuestion extends FeedbackQuestion {

    @Column(nullable = false, columnDefinition = "TEXT")
    @Convert(converter = FeedbackConstantSumRecipientsQuestionDetailsConverter.class)
    private FeedbackConstantSumRecipientsQuestionDetails questionDetails;

    protected FeedbackConstantSumRecipientsQuestion() {
        // required by Hibernate
    }

    public FeedbackConstantSumRecipientsQuestion(
            Integer questionNumber,
            String description, QuestionGiverType giverType, QuestionRecipientType recipientType,
            Integer numOfEntitiesToGiveFeedbackTo, List<FeedbackVisibilityType> showResponsesTo,
            List<FeedbackVisibilityType> showGiverNameTo, List<FeedbackVisibilityType> showRecipientNameTo,
            FeedbackQuestionDetails feedbackQuestionDetails
    ) {
        super(questionNumber, description, giverType, recipientType,
                numOfEntitiesToGiveFeedbackTo, showResponsesTo, showGiverNameTo, showRecipientNameTo);
        setFeedBackQuestionDetails((FeedbackConstantSumRecipientsQuestionDetails) feedbackQuestionDetails);
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
    public FeedbackConstantSumRecipientsQuestion makeDeepCopy() {
        return new FeedbackConstantSumRecipientsQuestion(
                this.getQuestionNumber(), this.getDescription(), this.getGiverType(),
                this.getRecipientType(), this.getNumOfEntitiesToGiveFeedbackTo(), new ArrayList<>(this.getShowResponsesTo()),
                new ArrayList<>(this.getShowGiverNameTo()), new ArrayList<>(this.getShowRecipientNameTo()),
                this.questionDetails.getDeepCopy()
        );
    }

    @Override
    public void setQuestionDetails(FeedbackQuestionDetails questionDetails) {
        this.questionDetails = (FeedbackConstantSumRecipientsQuestionDetails) questionDetails;
    }

    @Override
    public String toString() {
        return "FeedbackConstantSumRecipientsQuestion [id=" + super.getId()
                + ", createdAt=" + super.getCreatedAt() + ", updatedAt=" + super.getUpdatedAt() + "]";
    }

    public void setFeedBackQuestionDetails(FeedbackConstantSumRecipientsQuestionDetails questionDetails) {
        this.questionDetails = questionDetails;
    }

    public FeedbackConstantSumRecipientsQuestionDetails getFeedbackQuestionDetails() {
        return questionDetails;
    }

    /**
     * Converter for FeedbackConstantSumRecipientsQuestion specific attributes.
     */
    @Converter
    public static class FeedbackConstantSumRecipientsQuestionDetailsConverter
            extends FeedbackQuestionDetailsConverter {
    }
}
