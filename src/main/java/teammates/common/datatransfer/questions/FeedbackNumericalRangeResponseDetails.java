package teammates.common.datatransfer.questions;

import teammates.common.util.Const;
import teammates.common.util.StringHelper;

public class FeedbackNumericalRangeResponseDetails extends FeedbackResponseDetails {
    private double start;
    private double end;

    public FeedbackNumericalRangeResponseDetails() {
        super(FeedbackQuestionType.NUMRANGE);
        start = Const.POINTS_NOT_SUBMITTED;
        end = Const.POINTS_NOT_SUBMITTED;
    }

    //Todo
    @Override
    public String getAnswerString() {
        return StringHelper.toDecimalFormatString(start);
//                + " - " + StringHelper.toDecimalFormatString(end);
    }

    public double getStart() {
        return start;
    }

    public void setStart(double start) {
        this.start = start;
    }

    public double getEnd() {
        return end;
    }

    public void setEnd(double end) {
        this.end = end;
    }
}
