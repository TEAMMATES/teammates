package teammates.common.datatransfer.statistics;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.questions.FeedbackQuestionType;

/**
 * Recipient-specific MCQ/MSQ statistics for session results.
 */
public class FeedbackMcqMsqRecipientStatistics extends FeedbackQuestionRecipientResultsStatistics {
    private boolean hasAnswers;
    private boolean hasWeights;
    private List<FeedbackMcqMsqCourseWideStatistics.McqMsqOptionRow> rows = new ArrayList<>();

    public FeedbackMcqMsqRecipientStatistics(FeedbackQuestionType questionType) {
        super(questionType);
    }

    public boolean isHasAnswers() {
        return hasAnswers;
    }

    public void setHasAnswers(boolean hasAnswers) {
        this.hasAnswers = hasAnswers;
    }

    public boolean isHasWeights() {
        return hasWeights;
    }

    public void setHasWeights(boolean hasWeights) {
        this.hasWeights = hasWeights;
    }

    public List<FeedbackMcqMsqCourseWideStatistics.McqMsqOptionRow> getRows() {
        return rows;
    }

    public void setRows(List<FeedbackMcqMsqCourseWideStatistics.McqMsqOptionRow> rows) {
        this.rows = rows;
    }
}
