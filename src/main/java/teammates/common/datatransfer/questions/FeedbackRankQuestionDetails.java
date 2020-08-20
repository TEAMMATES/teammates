package teammates.common.datatransfer.questions;

public abstract class FeedbackRankQuestionDetails extends FeedbackQuestionDetails {

    static final transient int NO_VALUE = Integer.MIN_VALUE;
    protected int minOptionsToBeRanked;
    protected int maxOptionsToBeRanked;
    protected boolean areDuplicatesAllowed;

    FeedbackRankQuestionDetails(FeedbackQuestionType questionType) {
        super(questionType);
        minOptionsToBeRanked = NO_VALUE;
        maxOptionsToBeRanked = NO_VALUE;
    }

    public int getMinOptionsToBeRanked() {
        return minOptionsToBeRanked;
    }

    public void setMinOptionsToBeRanked(int minOptionsToBeRanked) {
        this.minOptionsToBeRanked = minOptionsToBeRanked;
    }

    public int getMaxOptionsToBeRanked() {
        return maxOptionsToBeRanked;
    }

    public void setMaxOptionsToBeRanked(int maxOptionsToBeRanked) {
        this.maxOptionsToBeRanked = maxOptionsToBeRanked;
    }

    public boolean areDuplicatesAllowed() {
        return areDuplicatesAllowed;
    }

    public void setAreDuplicatesAllowed(boolean areDuplicatesAllowed) {
        this.areDuplicatesAllowed = areDuplicatesAllowed;
    }
}
