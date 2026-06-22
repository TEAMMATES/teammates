package teammates.common.datatransfer.statistics;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.questions.FeedbackQuestionType;

/**
 * Constant sum options question statistics for session results.
 */
public class FeedbackConstsumOptionsStatistics extends FeedbackQuestionResultsStatistics {
    private List<ConstsumOptionRow> options = new ArrayList<>();

    public FeedbackConstsumOptionsStatistics() {
        super(FeedbackQuestionType.CONSTSUM_OPTIONS);
    }

    public FeedbackConstsumOptionsStatistics(FeedbackQuestionResultsStatisticsView view) {
        super(FeedbackQuestionType.CONSTSUM_OPTIONS, view);
    }

    public List<ConstsumOptionRow> getOptions() {
        return options;
    }

    public void setOptions(List<ConstsumOptionRow> options) {
        this.options = options;
    }

    /**
     * One row in the constant sum options summary table, corresponding to one option.
     */
    public static class ConstsumOptionRow {
        private String option;
        private List<Integer> pointsReceived = new ArrayList<>();
        private int total;
        private double average;

        public String getOption() {
            return option;
        }

        public void setOption(String option) {
            this.option = option;
        }

        public List<Integer> getPointsReceived() {
            return pointsReceived;
        }

        public void setPointsReceived(List<Integer> pointsReceived) {
            this.pointsReceived = pointsReceived;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public double getAverage() {
            return average;
        }

        public void setAverage(double average) {
            this.average = average;
        }
    }
}
