package teammates.common.datatransfer.questions;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.util.Const;
import teammates.common.util.Logger;
import teammates.common.util.StringHelper;

public class FeedbackNumericalScaleResponseDetails extends FeedbackResponseDetails {

    private static final Logger log = Logger.getLogger();

    private double answer;

    public FeedbackNumericalScaleResponseDetails() {
        super(FeedbackQuestionType.NUMSCALE);
    }

    @Override
    public void extractResponseDetails(FeedbackQuestionType questionType,
            FeedbackQuestionDetails questionDetails, String[] answer) {
        try {
            double numscaleAnswer = Double.parseDouble(answer[0]);
            setAnswer(numscaleAnswer);
        } catch (NumberFormatException e) {
            log.severe("Failed to parse numscale answer to double - " + answer[0]);
            throw e;
        }
    }

    /**
     * Returns answer in double form.
     */
    public double getAnswer() {
        return answer;
    }

    @Override
    public String getAnswerString() {
        return StringHelper.toDecimalFormatString(answer);
    }

    @Override
    public String getAnswerCsv(FeedbackQuestionDetails questionDetails) {
        return getAnswerString();
    }

    @Override
    public List<String> validateResponseDetails(FeedbackQuestionAttributes correspondingQuestion) {

        List<String> errors = new ArrayList<>();
        int minScale = ((FeedbackNumericalScaleQuestionDetails) correspondingQuestion
                .getQuestionDetails()).getMinScale();

        int maxScale = ((FeedbackNumericalScaleQuestionDetails) correspondingQuestion
                .getQuestionDetails()).getMaxScale();

        double step = ((FeedbackNumericalScaleQuestionDetails) correspondingQuestion
                .getQuestionDetails()).getStep();

        // out of range
        boolean isAnswerOutOfRange = answer < minScale || answer > maxScale;
        if (isAnswerOutOfRange) {
            errors.add(getAnswerString() + Const.FeedbackQuestion.NUMSCALE_ERROR_OUT_OF_RANGE
                    + "(min=" + minScale + ", max=" + maxScale + ")");
        }

        // when the answer is within range but not one of the possible values
        double remainder = (answer - minScale) % step;
        boolean isAnswerNotAPossibleValueWithinRange = remainder != 0.0 && !isAnswerOutOfRange;

        if (isAnswerNotAPossibleValueWithinRange) {
            double nextPossibleValueLessThanCurrent = answer - remainder;
            double nextPossibleValueGreaterThanCurrent = nextPossibleValueLessThanCurrent + step;
            errors.add("Please enter a valid value. The two nearest valid values are "
                    + nextPossibleValueLessThanCurrent + " and " + nextPossibleValueGreaterThanCurrent + ".");
        }
        return errors;
    }

    private void setAnswer(double answer) {
        this.answer = answer;
    }

}
