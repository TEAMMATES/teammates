package teammates.storage.entity.responses;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;

import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.common.datatransfer.questions.FeedbackRubricResponseDetails;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.ResponseGiver;
import teammates.storage.entity.ResponseRecipient;

/**
 * Represents a feedback rubric response.
 */
@Entity
public class FeedbackRubricResponse extends FeedbackResponse {

    @Column(nullable = false, columnDefinition = "TEXT")
    @Convert(converter = FeedbackRubricResponseDetailsConverter.class)
    private FeedbackRubricResponseDetails answer;

    protected FeedbackRubricResponse() {
        // required by Hibernate
    }

    public FeedbackRubricResponse(
            ResponseGiver giver, ResponseRecipient recipient,
            FeedbackResponseDetails responseDetails, @Nullable String giverComment
    ) {
        super(giver, recipient, giverComment);
        this.setAnswer((FeedbackRubricResponseDetails) responseDetails);
    }

    public FeedbackRubricResponseDetails getAnswer() {
        return answer;
    }

    public void setAnswer(FeedbackRubricResponseDetails answer) {
        this.answer = answer;
    }

    @Override
    public FeedbackResponseDetails getFeedbackResponseDetailsCopy() {
        return answer.getDeepCopy();
    }

    @Override
    public void setFeedbackResponseDetails(FeedbackResponseDetails responseDetails) {
        setAnswer(castResponseDetails(responseDetails, FeedbackRubricResponseDetails.class));
    }

    @Override
    public String toString() {
        return "FeedbackRubricResponse [id=" + super.getId()
                + ", createdAt=" + super.getCreatedAt() + ", updatedAt=" + super.getUpdatedAt() + "]";
    }

    /**
     * Converter for FeedbackRubricResponse specific attributes.
     */
    @Converter
    public static class FeedbackRubricResponseDetailsConverter
            extends FeedbackResponseDetailsConverter {
    }
}
