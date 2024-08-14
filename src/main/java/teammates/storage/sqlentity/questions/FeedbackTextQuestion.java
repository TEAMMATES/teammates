package teammates.storage.sqlentity.questions;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackTextQuestionDetails;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackSession;

/**
 * Represents a text question.
 */
@Entity
public class FeedbackTextQuestion extends FeedbackQuestion {

    @Column(nullable = false, columnDefinition = "TEXT")
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
    public FeedbackTextQuestion makeDeepCopy(FeedbackSession newFeedbackSession) {
        return new FeedbackTextQuestion(
                newFeedbackSession, this.getQuestionNumber(), this.getDescription(), this.getGiverType(),
                this.getRecipientType(), this.getNumOfEntitiesToGiveFeedbackTo(), new ArrayList<>(this.getShowResponsesTo()),
                new ArrayList<>(this.getShowGiverNameTo()), new ArrayList<>(this.getShowRecipientNameTo()),
                new FeedbackTextQuestionDetails(this.questionDetails.getQuestionText())
        );
    }

    @Override
    public void setQuestionDetails(FeedbackQuestionDetails questionDetails) {
        this.questionDetails = (FeedbackTextQuestionDetails) questionDetails;
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
