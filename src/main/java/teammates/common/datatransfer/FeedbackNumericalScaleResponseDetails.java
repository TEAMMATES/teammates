package teammates.common.datatransfer;

import teammates.common.util.StringHelper;
import teammates.common.util.Utils;

public class FeedbackNumericalScaleResponseDetails extends
        FeedbackAbstractResponseDetails {
    private double answer;
    
    public FeedbackNumericalScaleResponseDetails() {
        super(FeedbackQuestionType.NUMSCALE);
    }
    
    @Override
    public boolean extractResponseDetails(FeedbackQuestionType questionType,
            FeedbackAbstractQuestionDetails questionDetails, String[] answer) {
        try {
            double numscaleAnswer = Double.parseDouble(answer[0]);
            setAnswer(numscaleAnswer);
            return true;
        } catch (NumberFormatException e) {
            Utils.getLogger().severe("Failed to parse numscale answer to double - " + answer[0]);
            return false;
        }
    }

    /**
     * @return answer in double form
     */
    public double getAnswer() {
        return answer;
    }

    @Override
    public String getAnswerString() {
        return StringHelper.toDecimalFormatString(answer);
    }

    @Override
    public String getAnswerHtml(FeedbackAbstractQuestionDetails questionDetails) {
        return getAnswerString();
    }

    @Override
    public String getAnswerCsv(FeedbackAbstractQuestionDetails questionDetails) {
        return getAnswerString();
    }

    private void setAnswer(double answer) {
        this.answer = answer;
    }

}
