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
    public List<String> validateResponsesDetails(List<FeedbackResponseDetails> responses) {
        List<String> errors = new ArrayList<>();

        for (FeedbackResponseDetails response : responses) {
            FeedbackNumericalScaleResponseDetails details = (FeedbackNumericalScaleResponseDetails) response;

            // out of range
            boolean isAnswerOutOfRange = details.getAnswer() < minScale || details.getAnswer() > maxScale;
            if (isAnswerOutOfRange) {
                errors.add(details.getAnswerString() + Const.FeedbackQuestion.NUMSCALE_ERROR_OUT_OF_RANGE
                        + "(min=" + minScale + ", max=" + maxScale + ")");
            }

            // when the answer is within range but not one of the possible values
            double remainder = Double.valueOf(String.format("%.5f", (details.getAnswer() - minScale) % step));
            boolean isAnswerNotAPossibleValueWithinRange = remainder != 0.0 && !isAnswerOutOfRange;

            if (isAnswerNotAPossibleValueWithinRange) {
                double nextPossibleValueLessThanCurrent = details.getAnswer() - remainder;
                double nextPossibleValueGreaterThanCurrent = nextPossibleValueLessThanCurrent + step;
                errors.add("Please enter a valid value. The two nearest valid values are "
                        + nextPossibleValueLessThanCurrent + " and " + nextPossibleValueGreaterThanCurrent + ".");
            }
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
