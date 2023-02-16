package teammates.storage.sqlentity.questions;

import teammates.storage.sqlentity.FeedbackQuestion;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

/**
 * Represents a FeedbackTextQuestion entity.
 */
@Entity
public class FeedbackTextQuestion extends FeedbackQuestion {

    @Column(nullable = true)
    private Integer recommendedLength;

    @Column(nullable = false)
    private boolean shouldAllowRichText;

    protected FeedbackTextQuestion() {
        // required by Hibernate
    }

    public Integer getRecommendedLength() {
        return recommendedLength;
    }

    public void setRecommendedLength(Integer recommendedLength) {
        this.recommendedLength = recommendedLength;
    }

    public boolean getShouldAllowRichText() {
        return shouldAllowRichText;
    }

    public void setShouldAllowRichText(Boolean shouldAllowRichText) {
        this.shouldAllowRichText = shouldAllowRichText;
    }

    @Override
    public String toString() {
        return "FeedbackTextQuestion [id=" + super.getId() + ", recommendedLength=" + recommendedLength
                + ", shouldAllowRichText=" + shouldAllowRichText
                + ", createdAt=" + super.getCreatedAt() + ", updatedAt=" + super.getUpdatedAt() + "]";
    }
}
