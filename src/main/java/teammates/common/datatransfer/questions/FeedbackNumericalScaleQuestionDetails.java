package teammates.common.datatransfer.questions;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.util.Const;

public class FeedbackNumericalScaleQuestionDetails extends FeedbackQuestionDetails {
    private int minScale;
    private int maxScale;
    private double step;

    public FeedbackNumericalScaleQuestionDetails() {
        super(FeedbackQuestionType.NUMSCALE);
        this.minScale = 1;
        this.maxScale = 5;
        this.step = 0.5;
    }

    @Override
    public boolean shouldChangesRequireResponseDeletion(
            FeedbackQuestionDetails newDetails) {
        FeedbackNumericalScaleQuestionDetails newNumScaleDetails =
                (FeedbackNumericalScaleQuestionDetails) newDetails;

        return this.minScale != newNumScaleDetails.minScale
               || this.maxScale != newNumScaleDetails.maxScale
               || this.step != newNumScaleDetails.step;
    }

    @Override
    public List<String> validateQuestionDetails() {
        List<String> errors = new ArrayList<>();
        if (minScale >= maxScale) {
            errors.add(Const.FeedbackQuestion.NUMSCALE_ERROR_MIN_MAX);
        }
        if (step <= 0) {
            errors.add(Const.FeedbackQuestion.NUMSCALE_ERROR_STEP);
        }
        return errors;
    }

    @Override
    public boolean isFeedbackParticipantCommentsOnResponsesAllowed() {
        return false;
    }

    @Override
    public String validateGiverRecipientVisibility(FeedbackQuestionAttributes feedbackQuestionAttributes) {
        return "";
    }

    public int getMinScale() {
        return minScale;
    }

    public void setMinScale(int minScale) {
        this.minScale = minScale;
    }

    public int getMaxScale() {
        return maxScale;
    }

    public void setMaxScale(int maxScale) {
        this.maxScale = maxScale;
    }

    public double getStep() {
        return step;
    }

    public void setStep(double step) {
        this.step = step;
    }
}
