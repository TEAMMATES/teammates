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
import teammates.common.datatransfer.questions.FeedbackRankRecipientsQuestionDetails;
import teammates.storage.entity.FeedbackQuestion;

/**
 * Represents a rank recipients question.
 */
@Entity
public class FeedbackRankRecipientsQuestion extends FeedbackQuestion {

    @Column(nullable = false, columnDefinition = "TEXT")
    @Convert(converter = FeedbackRankRecipientsQuestionDetailsConverter.class)
    private FeedbackRankRecipientsQuestionDetails questionDetails;

    protected FeedbackRankRecipientsQuestion() {
        // required by Hibernate
    }

    public FeedbackRankRecipientsQuestion(
            Integer questionNumber,
            String description, QuestionGiverType giverType, QuestionRecipientType recipientType,
            Integer numOfEntitiesToGiveFeedbackTo, List<ViewerType> showResponsesTo,
            List<ViewerType> showGiverNameTo, List<ViewerType> showRecipientNameTo,
            FeedbackQuestionDetails feedbackQuestionDetails
    ) {
        super(questionNumber, description, giverType, recipientType,
                numOfEntitiesToGiveFeedbackTo, showResponsesTo, showGiverNameTo, showRecipientNameTo);
        setFeedBackQuestionDetails((FeedbackRankRecipientsQuestionDetails) feedbackQuestionDetails);
    }

    @Override
    public FeedbackQuestionType getQuestionType() {
        return FeedbackQuestionType.RANK_RECIPIENTS;
    }

    @Override
    public FeedbackQuestionDetails getQuestionDetailsCopy() {
        return questionDetails.getDeepCopy();
    }

    @Override
    public FeedbackRankRecipientsQuestion makeDeepCopy() {
        return new FeedbackRankRecipientsQuestion(
                this.getQuestionNumber(), this.getDescription(), this.getGiverType(),
                this.getRecipientType(), this.getNumOfEntitiesToGiveFeedbackTo(), new ArrayList<>(this.getShowResponsesTo()),
                new ArrayList<>(this.getShowGiverNameTo()), new ArrayList<>(this.getShowRecipientNameTo()),
                this.questionDetails.getDeepCopy()
        );
    }

    @Override
    public void setQuestionDetails(FeedbackQuestionDetails questionDetails) {
        this.questionDetails = (FeedbackRankRecipientsQuestionDetails) questionDetails;
    }

    @Override
    public String toString() {
        return "FeedbackRankRecipientsQuestion [id=" + super.getId()
                + ", createdAt=" + super.getCreatedAt() + ", updatedAt=" + super.getUpdatedAt() + "]";
    }

    public void setFeedBackQuestionDetails(FeedbackRankRecipientsQuestionDetails questionDetails) {
        this.questionDetails = questionDetails;
    }

    public FeedbackRankRecipientsQuestionDetails getFeedbackQuestionDetails() {
        return questionDetails;
    }

    /**
     * Converter for FeedbackRankaRecipientsQuestion specific attributes.
     */
    @Converter
    public static class FeedbackRankRecipientsQuestionDetailsConverter
            extends FeedbackQuestionDetailsConverter {
    }
}
