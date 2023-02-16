package teammates.storage.sqlentity.questions;

import teammates.storage.sqlentity.FeedbackQuestion;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

/**
 * Represents a FeedbackNumericalScaleQuestion entity.
 */
@Entity
public class FeedbackNumericalScaleQuestion extends FeedbackQuestion {

    @Column(nullable = false)
    private Integer minScale;

    @Column(nullable = false)
    private Integer maxScale;

    @Column(nullable = false)
    private Double step;

    protected FeedbackNumericalScaleQuestion() {
        // required by Hibernate
    }

    public Integer getMinScale() {
        return minScale;
    }

    public void setMinScale(Integer minScale) {
        this.minScale = minScale;
    }

    public Integer getMaxScale() {
        return maxScale;
    }

    public void setMaxScale(Integer maxScale) {
        this.maxScale = maxScale;
    }

    public Double getStep() {
        return step;
    }

    public void setStep(Double step) {
        this.step = step;
    }

    @Override
    public String toString() {
        return "FeedbackNumericalScaleQuestion [id=" + super.getId() + ", minScale=" + minScale
                + ", maxScale=" + maxScale + ", step=" + step
                + ", createdAt=" + super.getCreatedAt() + ", updatedAt=" + super.getUpdatedAt() + "]";
    }
}
