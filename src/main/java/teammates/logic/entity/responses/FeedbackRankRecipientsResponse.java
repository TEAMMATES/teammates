package teammates.logic.entity.responses;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;

import teammates.common.datatransfer.questions.FeedbackRankRecipientsResponseDetails;
import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.logic.entity.FeedbackQuestion;
import teammates.logic.entity.FeedbackResponse;
import teammates.logic.entity.Section;

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
            FeedbackQuestion feedbackQuestion, String giver,
            Section giverSection, String recipient, Section recipientSection,
            FeedbackResponseDetails responseDetails
    ) {
        super(feedbackQuestion, giver, giverSection, recipient, recipientSection);
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
