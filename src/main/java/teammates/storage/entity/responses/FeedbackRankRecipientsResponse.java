package teammates.storage.entity.responses;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;

import teammates.common.datatransfer.questions.FeedbackRankRecipientsResponseDetails;
import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.ResponseGiver;
import teammates.storage.entity.ResponseRecipient;

/**
 * Represents a feedback rank recipients response.
 */
@Entity
public class FeedbackRankRecipientsResponse extends FeedbackResponse {

    @Column(nullable = false, columnDefinition = "TEXT")
    @Convert(converter = FeedbackRankRecipientsResponseDetailsConverter.class)
    private FeedbackRankRecipientsResponseDetails answer;

    protected FeedbackRankRecipientsResponse() {
        // required by Hibernate
    }

    public FeedbackRankRecipientsResponse(
            ResponseGiver giver, ResponseRecipient recipient,
            FeedbackResponseDetails responseDetails, @Nullable String giverComment
    ) {
        super(giver, recipient, giverComment);
        this.setAnswer((FeedbackRankRecipientsResponseDetails) responseDetails);
    }

    public FeedbackRankRecipientsResponseDetails getAnswer() {
        return answer;
    }

    public void setAnswer(FeedbackRankRecipientsResponseDetails answer) {
        this.answer = answer;
    }

    @Override
    public FeedbackResponseDetails getFeedbackResponseDetailsCopy() {
        return answer.getDeepCopy();
    }

    @Override
    public void setFeedbackResponseDetails(FeedbackResponseDetails responseDetails) {
        setAnswer(castResponseDetails(responseDetails, FeedbackRankRecipientsResponseDetails.class));
    }

    @Override
    public String toString() {
        return "FeedbackRankRecipientsResponse [id=" + super.getId()
                + ", createdAt=" + super.getCreatedAt() + ", updatedAt=" + super.getUpdatedAt() + "]";
    }

    /**
     * Converter for FeedbackRankRecipientsResponse specific attributes.
     */
    @Converter
    public static class FeedbackRankRecipientsResponseDetailsConverter
            extends FeedbackResponseDetailsConverter {
    }
}
