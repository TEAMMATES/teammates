package teammates.storage.sqlentity.questions;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.questions.FeedbackMcqQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackSession;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;

/**
 * Represents an mcq question.
 */
@Entity
public class FeedbackMcqQuestion extends FeedbackQuestion {

    @Column(nullable = false)
    @Convert(converter = FeedbackMcqQuestionDetailsConverter.class)
    private FeedbackMcqQuestionDetails questionDetails;

    protected FeedbackMcqQuestion() {
        // required by Hibernate
    }

    public FeedbackMcqQuestion(
            FeedbackSession feedbackSession, Integer questionNumber,
            String description, FeedbackParticipantType giverType, FeedbackParticipantType recipientType,
            Integer numOfEntitiesToGiveFeedbackTo, List<FeedbackParticipantType> showResponsesTo,
            List<FeedbackParticipantType> showGiverNameTo, List<FeedbackParticipantType> showRecipientNameTo,
            FeedbackQuestionDetails feedbackQuestionDetails
    ) {
        super(feedbackSession, questionNumber, description, giverType, recipientType,
                numOfEntitiesToGiveFeedbackTo, showResponsesTo, showGiverNameTo, showRecipientNameTo);
        setFeedBackQuestionDetails((FeedbackMcqQuestionDetails) feedbackQuestionDetails);
    }

    @Override
    public FeedbackQuestionDetails getQuestionDetailsCopy() {
        return questionDetails.getDeepCopy();
    }

    @Override
    public FeedbackMcqQuestion makeDeepCopy(FeedbackSession newFeedbackSession) {
        FeedbackMcqQuestion copy = new FeedbackMcqQuestion(
            newFeedbackSession, this.getQuestionNumber(), this.getDescription(), this.getGiverType(),
            this.getRecipientType(), this.getNumOfEntitiesToGiveFeedbackTo(), new ArrayList<>(this.getShowResponsesTo()),
            new ArrayList<>(this.getShowGiverNameTo()), new ArrayList<>(this.getShowRecipientNameTo()),
            new FeedbackMcqQuestionDetails(this.questionDetails.getQuestionText())
            );
        return copy;
    }

    @Override
    public String toString() {
        return "FeedbackMcqQuestion [id=" + super.getId()
                + ", createdAt=" + super.getCreatedAt() + ", updatedAt=" + super.getUpdatedAt() + "]";
    }

    public void setFeedBackQuestionDetails(FeedbackMcqQuestionDetails questionDetails) {
        this.questionDetails = questionDetails;
    }

    public FeedbackMcqQuestionDetails getFeedbackQuestionDetails() {
        return questionDetails;
    }

    /**
     * Converter for FeedbackMcqQuestion specific attributes.
     */
    @Converter
    public static class FeedbackMcqQuestionDetailsConverter
            extends FeedbackQuestionDetailsConverter {
    }
}
