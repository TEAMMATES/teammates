package teammates.common.datatransfer;

import teammates.common.util.Const;
import teammates.common.util.Sanitizer;

public class FeedbackContributionResponseDetails extends FeedbackAbstractResponseDetails {
    /**This is the claimed points from giver to recipient.
    */
    private int answer;
    
    public FeedbackContributionResponseDetails() {
        super(FeedbackQuestionType.CONTRIB);
        answer = Const.CONTRIB_NOT_SUBMITTED;
    }
    
    public FeedbackContributionResponseDetails(String answer) {
        super(FeedbackQuestionType.CONTRIB);
        this.answer = Integer.parseInt(answer);
    }
    
    public FeedbackContributionResponseDetails(int answer) {
        super(FeedbackQuestionType.CONTRIB);
        this.answer = answer;
    }

    public int getAnswer() {
        return answer;
    }
    
    @Override
    public String getAnswerString() {
        return Integer.toString(answer);
    }

    @Override
    public String getAnswerHtml(FeedbackAbstractQuestionDetails questionDetails) {
        return Sanitizer.sanitizeForHtml(getAnswerString());
    }

    @Override
    public String getAnswerCsv(FeedbackAbstractQuestionDetails questionDetails) {
        return Sanitizer.sanitizeForCsv(getAnswerString());
    }
}
