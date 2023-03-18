package teammates.storage.sqlentity.questions;

import java.util.List;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackRubricQuestionDetails;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackSession;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;

/**
 * Represents a rubric question.
 */
@Entity
public class FeedbackRubricQuestion extends FeedbackQuestion {

    @Column(nullable = false)
    @Convert(converter = FeedbackRubricQuestionDetailsConverter.class)
    private FeedbackRubricQuestionDetails questionDetails;

    protected FeedbackRubricQuestion() {
        // required by Hibernate
    }

    public FeedbackRubricQuestion(
            FeedbackSession feedbackSession, Integer questionNumber,
            String description, FeedbackParticipantType giverType, FeedbackParticipantType recipientType,
            Integer numOfEntitiesToGiveFeedbackTo, List<FeedbackParticipantType> showResponsesTo,
            List<FeedbackParticipantType> showGiverNameTo, List<FeedbackParticipantType> showRecipientNameTo,
            FeedbackQuestionDetails feedbackQuestionDetails
    ) {
        super(feedbackSession, questionNumber, description, giverType, recipientType,
                numOfEntitiesToGiveFeedbackTo, showResponsesTo, showGiverNameTo, showRecipientNameTo);
        setFeedBackQuestionDetails((FeedbackRubricQuestionDetails) feedbackQuestionDetails);
    }

    @Override
    public FeedbackQuestionDetails getQuestionDetailsCopy() {
        return questionDetails.getDeepCopy();
    }

    @Override
    public String toString() {
        return "FeedbackRubricQuestion [id=" + super.getId()
                + ", createdAt=" + super.getCreatedAt() + ", updatedAt=" + super.getUpdatedAt() + "]";
    }

    public void setFeedBackQuestionDetails(FeedbackRubricQuestionDetails questionDetails) {
        this.questionDetails = questionDetails;
    }

    public FeedbackRubricQuestionDetails getFeedbackQuestionDetails() {
        return questionDetails;
    }

    /**
     * Converter for FeedbackRubricQuestion specific attributes.
     */
    @Converter
    public static class FeedbackRubricQuestionDetailsConverter
            extends FeedbackQuestionDetailsConverter {
    }
}
