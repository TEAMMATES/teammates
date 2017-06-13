package teammates.common.datatransfer.questions;

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
    public String getAnswerHtmlInstructorView(FeedbackQuestionDetails questionDetails) {
        return getAnswerString();
    }

    @Override
    public String getAnswerCsv(FeedbackQuestionDetails questionDetails) {
        return getAnswerString();
    }

    private void setAnswer(double answer) {
        this.answer = answer;
    }

}
