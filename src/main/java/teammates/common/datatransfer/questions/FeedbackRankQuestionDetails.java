package teammates.common.datatransfer.questions;

import java.util.Objects;

import teammates.common.util.Const;

/**
 * Contains common abstractions between rank options and rank recipients questions.
 */
public abstract class FeedbackRankQuestionDetails extends FeedbackQuestionDetails {

    int minOptionsToBeRanked;
    int maxOptionsToBeRanked;
    boolean areDuplicatesAllowed;

    FeedbackRankQuestionDetails(FeedbackQuestionType questionType, String questionText) {
        super(questionType, questionText);
        minOptionsToBeRanked = Const.POINTS_NO_VALUE;
        maxOptionsToBeRanked = Const.POINTS_NO_VALUE;
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

    public boolean isAreDuplicatesAllowed() {
        return areDuplicatesAllowed;
    }

    public void setAreDuplicatesAllowed(boolean areDuplicatesAllowed) {
        this.areDuplicatesAllowed = areDuplicatesAllowed;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        FeedbackRankQuestionDetails other = (FeedbackRankQuestionDetails) obj;
        return getQuestionType() == other.getQuestionType()
                && Objects.equals(getQuestionText(), other.getQuestionText())
                && minOptionsToBeRanked == other.minOptionsToBeRanked
                && maxOptionsToBeRanked == other.maxOptionsToBeRanked
                && areDuplicatesAllowed == other.areDuplicatesAllowed;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                getQuestionType(),
                getQuestionText(),
                minOptionsToBeRanked,
                maxOptionsToBeRanked,
                areDuplicatesAllowed);
    }
}
