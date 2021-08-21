package teammates.common.datatransfer.questions;

import teammates.common.util.Const;

/**
 * Contains specific structure and processing logic for contribution feedback responses.
 */
public class FeedbackContributionResponseDetails extends FeedbackResponseDetails {

    /**
     * This is the claimed points from giver to recipient.
     */
    private int answer;

    public FeedbackContributionResponseDetails() {
        super(FeedbackQuestionType.CONTRIB);
        answer = Const.POINTS_NOT_SUBMITTED;
    }

    @Override
    public String getAnswerString() {
        return Integer.toString(answer);
    }

    public int getAnswer() {
        return answer;
    }

    public void setAnswer(int answer) {
        this.answer = answer;
    }
}
