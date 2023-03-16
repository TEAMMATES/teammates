package teammates.storage.sqlentity.questions;

import java.util.List;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.questions.FeedbackMsqQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackSession;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;

/**
 * Represents an msq question.
 */
@Entity
public class FeedbackMsqQuestion extends FeedbackQuestion {

    @Column(nullable = false)
    @Convert(converter = FeedbackMsqQuestionDetailsConverter.class)
    private FeedbackMsqQuestionDetails questionDetails;

    protected FeedbackMsqQuestion() {
        // required by Hibernate
    }

    public FeedbackMsqQuestion(
            FeedbackSession feedbackSession, Integer questionNumber,
            String description, FeedbackQuestionType questionType,
            FeedbackParticipantType giverType, FeedbackParticipantType recipientType,
            Integer numOfEntitiesToGiveFeedbackTo, List<FeedbackParticipantType> showResponsesTo,
            List<FeedbackParticipantType> showGiverNameTo, List<FeedbackParticipantType> showRecipientNameTo,
            FeedbackQuestionDetails feedbackQuestionDetails
    ) {
        super(feedbackSession, questionNumber, description, questionType, giverType, recipientType,
                numOfEntitiesToGiveFeedbackTo, showResponsesTo, showGiverNameTo, showRecipientNameTo);
        setFeedBackQuestionDetails((FeedbackMsqQuestionDetails) feedbackQuestionDetails);
    }

    @Override
    public FeedbackQuestionDetails getQuestionDetailsCopy() {
        return questionDetails.getDeepCopy();
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
