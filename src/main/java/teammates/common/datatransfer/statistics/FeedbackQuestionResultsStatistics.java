package teammates.common.datatransfer.statistics;

import teammates.common.datatransfer.questions.FeedbackQuestionType;

/**
 * Base type for course-wide feedback question statistics shown on results pages.
 */
public class FeedbackQuestionResultsStatistics {
    private final FeedbackQuestionType questionType;
    private final FeedbackQuestionResultsStatisticsView statisticsView;

    protected FeedbackQuestionResultsStatistics(FeedbackQuestionType questionType) {
        this.questionType = questionType;
        this.statisticsView = FeedbackQuestionResultsStatisticsView.COURSE_WIDE;
    }

    public FeedbackQuestionType getQuestionType() {
        return questionType;
    }

    public FeedbackQuestionResultsStatisticsView getStatisticsView() {
        return statisticsView;
    }
}
