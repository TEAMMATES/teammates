package teammates.storage.sqlentity.responses;

import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.common.datatransfer.questions.FeedbackTextResponseDetails;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackResponse;
import teammates.storage.sqlentity.Section;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

/**
 * Represents a text response.
 */
@Entity
public class FeedbackTextResponse extends FeedbackResponse {

    @Column(nullable = false)
    private String answer;

    protected FeedbackTextResponse() {
        // required by Hibernate
    }

    public FeedbackTextResponse(
        FeedbackQuestion feedbackQuestion, String giver,
        Section giverSection, String receiver, Section receiverSection,
        FeedbackResponseDetails responseDetails
    ) {
        super(feedbackQuestion, giver, giverSection, receiver, receiverSection);
        this.setAnswer(((FeedbackTextResponseDetails) responseDetails).getAnswer());
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    @Override
    public FeedbackResponseDetails getFeedbackResponseDetailsCopy() {
        return new FeedbackTextResponseDetails(answer);
    }

    @Override
    public String toString() {
        return "FeedbackTextResponse [id=" + super.getId()
            + ", createdAt=" + super.getCreatedAt() + ", updatedAt=" + super.getUpdatedAt() + "]";
    }
}
