package teammates.storage.sqlentity.responses;

import teammates.storage.sqlentity.FeedbackResponse;

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

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    @Override
    public String toString() {
        return "FeedbackTextResponse [id=" + super.getId()
            + ", createdAt=" + super.getCreatedAt() + ", updatedAt=" + super.getUpdatedAt() + "]";
    }
}
