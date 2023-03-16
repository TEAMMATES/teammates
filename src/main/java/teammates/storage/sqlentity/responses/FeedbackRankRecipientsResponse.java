package teammates.storage.sqlentity.responses;

import teammates.common.datatransfer.questions.FeedbackRankRecipientsResponseDetails;
import teammates.storage.sqlentity.FeedbackResponse;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;

/**
 * Represents a feedback rank recipients response.
 */
@Entity
public class FeedbackRankRecipientsResponse extends FeedbackResponse {

    @Column(nullable = false)
    @Convert(converter = FeedbackRankRecipientsResponseDetailsConverter.class)
    private FeedbackRankRecipientsResponseDetails answer;

    protected FeedbackRankRecipientsResponse() {
        // required by Hibernate
    }

    public FeedbackRankRecipientsResponseDetails getAnswer() {
        return answer;
    }

    public void setAnswer(FeedbackRankRecipientsResponseDetails answer) {
        this.answer = answer;
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
            extends FeedbackQuestionDetailsConverter {
    }
}
