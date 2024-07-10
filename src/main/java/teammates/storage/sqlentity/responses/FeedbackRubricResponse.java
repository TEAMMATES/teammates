package teammates.storage.sqlentity.responses;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;

import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.common.datatransfer.questions.FeedbackRubricResponseDetails;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackResponse;
import teammates.storage.sqlentity.Section;

/**
 * Represents a feedback rubric response.
 */
@Entity
public class FeedbackRubricResponse extends FeedbackResponse {

    @Column(nullable = false)
    @Convert(converter = FeedbackRubricResponseDetailsConverter.class)
    private FeedbackRubricResponseDetails answer;

    protected FeedbackRubricResponse() {
        // required by Hibernate
    }

    public FeedbackRubricResponse(
            FeedbackQuestion feedbackQuestion, String giver,
            Section giverSection, String recipient, Section recipientSection,
            FeedbackResponseDetails responseDetails
    ) {
        super(feedbackQuestion, giver, giverSection, recipient, recipientSection);
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
