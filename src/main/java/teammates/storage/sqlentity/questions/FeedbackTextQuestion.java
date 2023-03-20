package teammates.storage.sqlentity.questions;

import java.util.List;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackTextQuestionDetails;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackSession;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;

/**
 * Represents a text question.
 */
@Entity
public class FeedbackTextQuestion extends FeedbackQuestion {

    @Column(nullable = false)
    @Convert(converter = FeedbackTextQuestionDetailsConverter.class)
    private FeedbackTextQuestionDetails questionDetails;

    protected FeedbackTextQuestion() {
        // required by Hibernate
    }

    public FeedbackTextQuestion(
            FeedbackSession feedbackSession, Integer questionNumber,
            String description, FeedbackParticipantType giverType, FeedbackParticipantType recipientType,
            Integer numOfEntitiesToGiveFeedbackTo, List<FeedbackParticipantType> showResponsesTo,
            List<FeedbackParticipantType> showGiverNameTo, List<FeedbackParticipantType> showRecipientNameTo,
            FeedbackQuestionDetails feedbackQuestionDetails
    ) {
        super(feedbackSession, questionNumber, description, giverType, recipientType,
                numOfEntitiesToGiveFeedbackTo, showResponsesTo, showGiverNameTo, showRecipientNameTo);
        setFeedBackQuestionDetails((FeedbackTextQuestionDetails) feedbackQuestionDetails);
    }

    @Override
    public FeedbackQuestionDetails getQuestionDetailsCopy() {
        return questionDetails.getDeepCopy();
    }

    @Override
    public String toString() {
        return "FeedbackTextQuestion [id=" + super.getId() + ", createdAt=" + super.getCreatedAt()
                + ", updatedAt=" + super.getUpdatedAt() + "]";
    }

    public void setFeedBackQuestionDetails(FeedbackTextQuestionDetails questionDetails) {
        this.questionDetails = questionDetails;
    }

    public FeedbackTextQuestionDetails getFeedbackQuestionDetails() {
        return questionDetails;
    }

    /**
     * Converter for FeedbackTextQuestion specific attributes.
     */
    @Converter
    public static class FeedbackTextQuestionDetailsConverter
            extends FeedbackQuestionDetailsConverter {
    }
}
