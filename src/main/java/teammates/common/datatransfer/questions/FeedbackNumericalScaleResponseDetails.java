package teammates.common.datatransfer.questions;

import teammates.common.util.Const;
import teammates.common.util.StringHelper;

/**
 * Contains specific structure and processing logic for numerical scale feedback responses.
 */
public class FeedbackNumericalScaleResponseDetails extends FeedbackResponseDetails {

    private double answer;

    public FeedbackNumericalScaleResponseDetails() {
        super(FeedbackQuestionType.NUMSCALE);
        answer = Const.POINTS_NOT_SUBMITTED;
    }

    @Override
    public String getAnswerString() {
        return StringHelper.toDecimalFormatString(answer);
    }

    public double getAnswer() {
        return answer;
    }

    public void setAnswer(double answer) {
        this.answer = answer;
    }
}
