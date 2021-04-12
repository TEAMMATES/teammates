package teammates.common.datatransfer.questions;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;

public class FeedbackNumericalScaleQuestionDetails extends FeedbackQuestionDetails {

    static final String QUESTION_TYPE_NAME = "Numerical-scale question";
    static final String NUMSCALE_ERROR_MIN_MAX = "Minimum value must be < maximum value for " + QUESTION_TYPE_NAME + ".";
    static final String NUMSCALE_ERROR_STEP = "Step value must be > 0 for " + QUESTION_TYPE_NAME + ".";
    static final String NUMSCALE_ERROR_OUT_OF_RANGE = " is out of the range for " + QUESTION_TYPE_NAME + ".";

    private int minScale;
    private int maxScale;
    private double step;

    public FeedbackNumericalScaleQuestionDetails() {
        this(null);
    }

    public FeedbackNumericalScaleQuestionDetails(String questionText) {
        super(FeedbackQuestionType.NUMSCALE, questionText);
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
            errors.add(NUMSCALE_ERROR_MIN_MAX);
        }
        if (step <= 0) {
            errors.add(NUMSCALE_ERROR_STEP);
        }
        return errors;
    }

    @Override
    public List<String> validateResponsesDetails(List<FeedbackResponseDetails> responses, int numRecipients) {
        List<String> errors = new ArrayList<>();

        for (FeedbackResponseDetails response : responses) {
            FeedbackNumericalScaleResponseDetails details = (FeedbackNumericalScaleResponseDetails) response;

            // out of range
            boolean isAnswerOutOfRange = details.getAnswer() < minScale || details.getAnswer() > maxScale;
            if (isAnswerOutOfRange) {
                errors.add(details.getAnswerString() + NUMSCALE_ERROR_OUT_OF_RANGE
                        + "(min=" + minScale + ", max=" + maxScale + ")");
            }

            // when the answer is within range but not one of the possible values
            BigDecimal minval = BigDecimal.valueOf(this.minScale);
            BigDecimal answer = BigDecimal.valueOf(details.getAnswer());
            BigDecimal stepBd = BigDecimal.valueOf(step);
            BigDecimal remainder = answer.subtract(minval).remainder(stepBd);
            boolean isAnsMultipleOfStep = remainder.compareTo(BigDecimal.ZERO) == 0;
            if (!isAnsMultipleOfStep && !isAnswerOutOfRange) {
                double posValSmall = answer.subtract(remainder).setScale(5, RoundingMode.HALF_UP).doubleValue();
                double posValBig = answer.subtract(remainder).add(stepBd).setScale(5, RoundingMode.HALF_UP).doubleValue();

                errors.add("Please enter a valid value. The two nearest valid values are "
                        + posValSmall + " and " + posValBig + ".");
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
