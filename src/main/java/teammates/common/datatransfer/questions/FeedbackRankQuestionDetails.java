package teammates.common.datatransfer.questions;

public abstract class FeedbackRankQuestionDetails extends FeedbackQuestionDetails {

    static final transient int NO_VALUE = Integer.MIN_VALUE;
    protected int minOptionsToBeRanked;
    protected int maxOptionsToBeRanked;
    private boolean areDuplicatesAllowed;

    FeedbackRankQuestionDetails(FeedbackQuestionType questionType) {
        super(questionType);
        minOptionsToBeRanked = NO_VALUE;
        maxOptionsToBeRanked = NO_VALUE;
    }

    public boolean isAreDuplicatesAllowed() {
        return areDuplicatesAllowed;
    }

}
