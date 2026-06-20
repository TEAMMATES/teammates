package teammates.common.datatransfer.statistics;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.questions.FeedbackQuestionType;

/**
 * Rank options question statistics for session results.
 */
public class FeedbackRankOptionsStatistics extends FeedbackQuestionResultsStatistics {
    private List<RankOptionsOptionRow> options = new ArrayList<>();

    public FeedbackRankOptionsStatistics() {
        super(FeedbackQuestionType.RANK_OPTIONS);
    }

    public List<RankOptionsOptionRow> getOptions() {
        return options;
    }

    public void setOptions(List<RankOptionsOptionRow> options) {
        this.options = options;
    }

    /**
     * One row in the rank options summary table, corresponding to one option.
     */
    public static class RankOptionsOptionRow {
        private String option;
        private List<Integer> ranksReceived = new ArrayList<>();
        private Integer overallRank;

        public String getOption() {
            return option;
        }

        public void setOption(String option) {
            this.option = option;
        }

        public List<Integer> getRanksReceived() {
            return ranksReceived;
        }

        public void setRanksReceived(List<Integer> ranksReceived) {
            this.ranksReceived = ranksReceived;
        }

        public Integer getOverallRank() {
            return overallRank;
        }

        public void setOverallRank(Integer overallRank) {
            this.overallRank = overallRank;
        }
    }
}
