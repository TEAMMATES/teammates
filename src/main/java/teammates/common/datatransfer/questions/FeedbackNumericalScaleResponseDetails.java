package teammates.common.datatransfer.questions;

import teammates.common.util.StringHelper;

public class FeedbackNumericalScaleResponseDetails extends FeedbackResponseDetails {

    private double answer;

    public FeedbackNumericalScaleResponseDetails() {
        super(FeedbackQuestionType.NUMSCALE);
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
