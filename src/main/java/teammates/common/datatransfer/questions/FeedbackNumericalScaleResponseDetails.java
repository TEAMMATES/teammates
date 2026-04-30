package teammates.common.datatransfer.questions;

import java.util.Objects;

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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof FeedbackNumericalScaleResponseDetails other)) {
            return false;
        }
        return getQuestionType() == other.getQuestionType()
                && Double.compare(answer, other.answer) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getQuestionType(), answer);
    }
}
