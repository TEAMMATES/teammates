package teammates.common.datatransfer;

import teammates.common.util.Const;
import teammates.common.util.Sanitizer;
import teammates.common.util.Utils;

public class FeedbackContributionResponseDetails extends FeedbackAbstractResponseDetails {
    /**This is the claimed points from giver to recipient.
    */
    private int answer;
    
    public FeedbackContributionResponseDetails() {
        super(FeedbackQuestionType.CONTRIB);
        answer = Const.POINTS_NOT_SUBMITTED;
    }
    
    public FeedbackContributionResponseDetails(int answer) {
        super(FeedbackQuestionType.CONTRIB);
        this.answer = answer;
    }    
    
    @Override
    public boolean extractResponseDetails(FeedbackQuestionType questionType,
            FeedbackAbstractQuestionDetails questionDetails, String[] answer) {
        try {
            int contribAnswer = Integer.parseInt(answer[0]);
            setAnswer(contribAnswer);
            return true;
        } catch (NumberFormatException e) {
            Utils.getLogger().severe("Failed to parse contrib answer to integer - " + answer[0]);
            return false;
        }
    }

    /**
     * Get answer in integer form
     * @return
     */
    public int getAnswer() {
        return answer;
    }
    
    @Override
    public String getAnswerString() {
        return Integer.toString(answer);
    }
    
    // Not used for contribution question, due to calculations required. See FeedbackSessionResultsBundle.
    @Override
    public String getAnswerHtml(FeedbackAbstractQuestionDetails questionDetails) {
        return FeedbackContributionQuestionDetails.convertToEqualShareFormatHtml(getAnswer());
    }

    // Not used for contribution question, due to calculations required. See FeedbackSessionResultsBundle.
    @Override
    public String getAnswerCsv(FeedbackAbstractQuestionDetails questionDetails) {
        return Sanitizer.sanitizeForCsv(FeedbackContributionQuestionDetails.convertToEqualShareFormat(getAnswer()));
    }

    private void setAnswer(int answer) {
        this.answer = answer;
    }
}
