package teammates.common.datatransfer.questions;

import teammates.common.util.Const;

/**
 * Contains specific structure and processing logic for rank recipients feedback responses.
 */
public class FeedbackRankRecipientsResponseDetails extends FeedbackResponseDetails {
    private int answer;

    public FeedbackRankRecipientsResponseDetails() {
        super(FeedbackQuestionType.RANK_RECIPIENTS);
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof FeedbackRankRecipientsResponseDetails other)) {
            return false;
        }
        return getQuestionType() == other.getQuestionType()
                && answer == other.answer;
    }

    @Override
    public int hashCode() {
        return 31 * getQuestionType().hashCode() + Integer.hashCode(answer);
    }
}
