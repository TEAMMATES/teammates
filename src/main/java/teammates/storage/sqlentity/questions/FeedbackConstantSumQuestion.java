package teammates.storage.sqlentity.questions;

import java.util.List;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.questions.FeedbackConstantSumQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackSession;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;

/**
 * Represents a constant sum question.
 */
@Entity
public class FeedbackConstantSumQuestion extends FeedbackQuestion {

    @Column(nullable = false)
    @Convert(converter = FeedbackConstantSumQuestionDetailsConverter.class)
    private FeedbackConstantSumQuestionDetails questionDetails;

    protected FeedbackConstantSumQuestion() {
        // required by Hibernate
    }

    public FeedbackConstantSumQuestion(
            FeedbackSession feedbackSession, Integer questionNumber,
            String description, FeedbackParticipantType giverType, FeedbackParticipantType recipientType,
            Integer numOfEntitiesToGiveFeedbackTo, List<FeedbackParticipantType> showResponsesTo,
            List<FeedbackParticipantType> showGiverNameTo, List<FeedbackParticipantType> showRecipientNameTo,
            FeedbackQuestionDetails feedbackQuestionDetails
    ) {
        super(feedbackSession, questionNumber, description, giverType, recipientType,
                numOfEntitiesToGiveFeedbackTo, showResponsesTo, showGiverNameTo, showRecipientNameTo);
        setFeedBackQuestionDetails((FeedbackConstantSumQuestionDetails) feedbackQuestionDetails);
    }

    @Override
    public FeedbackQuestionDetails getQuestionDetailsCopy() {
        return questionDetails.getDeepCopy();
    }

    @Override
    public String toString() {
        return "FeedbackConstantSumQuestion [id=" + super.getId()
                + ", createdAt=" + super.getCreatedAt() + ", updatedAt=" + super.getUpdatedAt() + "]";
    }

    public void setFeedBackQuestionDetails(FeedbackConstantSumQuestionDetails questionDetails) {
        this.questionDetails = questionDetails;
    }

    public FeedbackConstantSumQuestionDetails getFeedbackQuestionDetails() {
        return questionDetails;
    }

    /**
     * Converter for FeedbackConstantSumQuestion specific attributes.
     */
    @Converter
    public static class FeedbackConstantSumQuestionDetailsConverter
            extends FeedbackQuestionDetailsConverter {
    }
}
