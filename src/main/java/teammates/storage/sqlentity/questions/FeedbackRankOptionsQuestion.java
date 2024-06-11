package teammates.storage.sqlentity.questions;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackRankOptionsQuestionDetails;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackSession;

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
            FeedbackSession feedbackSession, Integer questionNumber,
            String description, FeedbackParticipantType giverType, FeedbackParticipantType recipientType,
            Integer numOfEntitiesToGiveFeedbackTo, List<FeedbackParticipantType> showResponsesTo,
            List<FeedbackParticipantType> showGiverNameTo, List<FeedbackParticipantType> showRecipientNameTo,
            FeedbackQuestionDetails feedbackQuestionDetails) {
        super(feedbackSession, questionNumber, description, giverType, recipientType,
                numOfEntitiesToGiveFeedbackTo, showResponsesTo, showGiverNameTo, showRecipientNameTo);
        setFeedBackQuestionDetails((FeedbackRankOptionsQuestionDetails) feedbackQuestionDetails);
    }

    @Override
    public FeedbackQuestionDetails getQuestionDetailsCopy() {
        return questionDetails.getDeepCopy();
    }

    @Override
    public FeedbackRankOptionsQuestion makeDeepCopy(FeedbackSession newFeedbackSession) {
        return new FeedbackRankOptionsQuestion(
                newFeedbackSession, this.getQuestionNumber(), this.getDescription(), this.getGiverType(),
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
