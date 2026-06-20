package teammates.common.datatransfer.statistics;

import teammates.common.datatransfer.questions.FeedbackQuestionType;

/**
 * Base type for feedback question statistics shown on results pages.
 */
public class FeedbackQuestionResultsStatistics {
    private final FeedbackQuestionType questionType;
    private final FeedbackQuestionResultsStatisticsView statisticsView;

    protected FeedbackQuestionResultsStatistics(FeedbackQuestionType questionType) {
        this(questionType, FeedbackQuestionResultsStatisticsView.COURSE_WIDE);
    }

    protected FeedbackQuestionResultsStatistics(
            FeedbackQuestionType questionType, FeedbackQuestionResultsStatisticsView statisticsView) {
        this.questionType = questionType;
        this.statisticsView = statisticsView;
    }

    public FeedbackQuestionType getQuestionType() {
        return questionType;
    }

    public FeedbackQuestionResultsStatisticsView getStatisticsView() {
        return statisticsView;
    }
}
