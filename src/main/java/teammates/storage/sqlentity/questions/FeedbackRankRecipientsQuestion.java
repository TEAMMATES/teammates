package teammates.storage.sqlentity.questions;

import java.util.List;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.datatransfer.questions.FeedbackRankRecipientsQuestionDetails;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackSession;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;

/**
 * Represents a rank recipients question.
 */
@Entity
public class FeedbackRankRecipientsQuestion extends FeedbackQuestion {

    @Column(nullable = false)
    @Convert(converter = FeedbackRankRecipientsQuestionDetailsConverter.class)
    private FeedbackRankRecipientsQuestionDetails questionDetails;

    protected FeedbackRankRecipientsQuestion() {
        // required by Hibernate
    }

    public FeedbackRankRecipientsQuestion(
            FeedbackSession feedbackSession, Integer questionNumber,
            String description, FeedbackQuestionType questionType,
            FeedbackParticipantType giverType, FeedbackParticipantType recipientType,
            Integer numOfEntitiesToGiveFeedbackTo, List<FeedbackParticipantType> showResponsesTo,
            List<FeedbackParticipantType> showGiverNameTo, List<FeedbackParticipantType> showRecipientNameTo,
            FeedbackQuestionDetails feedbackQuestionDetails
    ) {
        super(feedbackSession, questionNumber, description, questionType, giverType, recipientType,
                numOfEntitiesToGiveFeedbackTo, showResponsesTo, showGiverNameTo, showRecipientNameTo);
        setFeedBackQuestionDetails((FeedbackRankRecipientsQuestionDetails) feedbackQuestionDetails);
    }

    @Override
    public FeedbackQuestionDetails getQuestionDetailsCopy() {
        return questionDetails.getDeepCopy();
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
