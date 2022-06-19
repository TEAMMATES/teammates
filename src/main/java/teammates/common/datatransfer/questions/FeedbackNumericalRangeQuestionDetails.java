package teammates.common.datatransfer.questions;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class FeedbackNumericalRangeQuestionDetails extends FeedbackQuestionDetails {
    static final String QUESTION_TYPE_NAME = "Numerical-range question";
    static final String NUMRANGE_ERROR_MIN_MAX = "Minimum value must be < maximum value for " + QUESTION_TYPE_NAME + ".";
    static final String NUMRANGE_ERROR_STEP = "Step value must be > 0 for " + QUESTION_TYPE_NAME + ".";
    static final String NUMRANGE_ERROR_OUT_OF_RANGE = " is out of the range for " + QUESTION_TYPE_NAME + ".";

    private int minScale;
    private int maxScale;
    private double step;

    public FeedbackNumericalRangeQuestionDetails() {
        this(null);
    }

    public FeedbackNumericalRangeQuestionDetails(String questionText) {
        super(FeedbackQuestionType.NUMRANGE, questionText);
        this.minScale = 1;
        this.maxScale = 5;
        this.step = 0.5;
    }

    @Override
    public boolean shouldChangesRequireResponseDeletion(
            FeedbackQuestionDetails newDetails) {
        FeedbackNumericalRangeQuestionDetails newNumScaleDetails =
                (FeedbackNumericalRangeQuestionDetails) newDetails;

        return this.minScale != newNumScaleDetails.minScale
                || this.maxScale != newNumScaleDetails.maxScale
                || this.step != newNumScaleDetails.step;
    }

    @Override
    public List<String> validateQuestionDetails() {
        List<String> errors = new ArrayList<>();
        if (minScale >= maxScale) {
            errors.add(NUMRANGE_ERROR_MIN_MAX);
        }
        if (step <= 0) {
            errors.add(NUMRANGE_ERROR_STEP);
        }
        return errors;
    }

    @Override
    public List<String> validateResponsesDetails(List<FeedbackResponseDetails> responses, int numRecipients) {
        List<String> errors = new ArrayList<>();

        for (FeedbackResponseDetails response : responses) {
            FeedbackNumericalRangeResponseDetails details = (FeedbackNumericalRangeResponseDetails) response;

            // out of range
            boolean isStartOutOfRange = details.getStart() < minScale || details.getStart() > maxScale;
            boolean isEndOutOfRange = details.getEnd() < minScale || details.getEnd() > maxScale;
            if (isStartOutOfRange) {
                errors.add("Start: " + details.getStart() + NUMRANGE_ERROR_OUT_OF_RANGE
                        + "(min=" + minScale + ", max=" + maxScale + ")");
            }
            if (isEndOutOfRange) {
                errors.add("End: " + details.getEnd() + NUMRANGE_ERROR_OUT_OF_RANGE
                        + "(min=" + minScale + ", max=" + maxScale + ")");
            }

            // when the answer is within range but not one of the possible values
            BigDecimal minval = BigDecimal.valueOf(this.minScale);
            BigDecimal start = BigDecimal.valueOf(details.getStart());
            BigDecimal end = BigDecimal.valueOf(details.getEnd());
            BigDecimal stepBd = BigDecimal.valueOf(step);
            BigDecimal remainderOfStart = start.subtract(minval).remainder(stepBd);
            BigDecimal remainderOfEnd =  end.subtract(minval).remainder(stepBd);
            boolean isStartMultipleOfStep = remainderOfStart.compareTo(BigDecimal.ZERO) == 0;
            boolean isEndMultipleOfStep = remainderOfEnd.compareTo(BigDecimal.ZERO) == 0;
            if (!isStartMultipleOfStep && !isStartOutOfRange) {
                double posValSmall = start.subtract(remainderOfStart).setScale(5, RoundingMode.HALF_UP).doubleValue();
                double posValBig = start.subtract(remainderOfStart).add(stepBd).setScale(5, RoundingMode.HALF_UP).doubleValue();

                errors.add("Please enter a valid start value. The two nearest valid values are "
                        + posValSmall + " and " + posValBig + ".");
            }
            if (!isEndMultipleOfStep && !isEndOutOfRange) {
                double posValSmall = end.subtract(remainderOfEnd).setScale(5, RoundingMode.HALF_UP).doubleValue();
                double posValBig = end.subtract(remainderOfEnd).add(stepBd).setScale(5, RoundingMode.HALF_UP).doubleValue();

                errors.add("Please enter a valid end value. The two nearest valid values are "
                        + posValSmall + " and " + posValBig + ".");
            }

            // when the answer is within range and is possible value but not start value greater than end value
            if (details.getStart() > details.getEnd()) {
                errors.add("Start value must be < end value.");
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
