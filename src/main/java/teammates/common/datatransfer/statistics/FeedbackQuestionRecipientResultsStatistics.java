package teammates.common.datatransfer.statistics;

import teammates.common.datatransfer.questions.FeedbackQuestionType;

/**
 * Base type for recipient-specific feedback question statistics shown on results pages.
 */
public class FeedbackQuestionRecipientResultsStatistics {
    private final FeedbackQuestionType questionType;
    private final FeedbackQuestionResultsStatisticsView statisticsView;

    protected FeedbackQuestionRecipientResultsStatistics(FeedbackQuestionType questionType) {
        this.questionType = questionType;
        this.statisticsView = FeedbackQuestionResultsStatisticsView.RECIPIENT;
    }

    public FeedbackQuestionType getQuestionType() {
        return questionType;
    }

    public FeedbackQuestionResultsStatisticsView getStatisticsView() {
        return statisticsView;
    }
}
