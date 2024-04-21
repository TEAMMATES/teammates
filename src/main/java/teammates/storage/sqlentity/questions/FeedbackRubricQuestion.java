package teammates.storage.sqlentity.questions;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackRubricQuestionDetails;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackSession;

/**
 * Represents a rubric question.
 */
@Entity
public class FeedbackRubricQuestion extends FeedbackQuestion {

    @Column(nullable = false, columnDefinition = "TEXT")
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
    public FeedbackRubricQuestion makeDeepCopy(FeedbackSession newFeedbackSession) {
        return new FeedbackRubricQuestion(
                newFeedbackSession, this.getQuestionNumber(), this.getDescription(), this.getGiverType(),
                this.getRecipientType(), this.getNumOfEntitiesToGiveFeedbackTo(), new ArrayList<>(this.getShowResponsesTo()),
                new ArrayList<>(this.getShowGiverNameTo()), new ArrayList<>(this.getShowRecipientNameTo()),
                new FeedbackRubricQuestionDetails(this.questionDetails.getQuestionText())
        );
    }

    @Override
    public void setQuestionDetails(FeedbackQuestionDetails questionDetails) {
        this.questionDetails = (FeedbackRubricQuestionDetails) questionDetails;
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
