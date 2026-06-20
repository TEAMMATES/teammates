package teammates.common.datatransfer.statistics;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import teammates.common.datatransfer.questions.FeedbackQuestionType;

/**
 * Rank recipients question statistics for session results.
 */
public class FeedbackRankRecipientsStatistics extends FeedbackQuestionResultsStatistics {
    private List<RankRecipientsRow> rows = new ArrayList<>();

    public FeedbackRankRecipientsStatistics() {
        super(FeedbackQuestionType.RANK_RECIPIENTS);
    }

    public List<RankRecipientsRow> getRows() {
        return rows;
    }

    public void setRows(List<RankRecipientsRow> rows) {
        this.rows = rows;
    }

    /**
     * One row in the rank recipients summary table, corresponding to one recipient.
     */
    public static class RankRecipientsRow {
        private String recipientName;
        @Nullable
        private String recipientEmail;
        private String recipientTeam;
        private List<Integer> ranksReceived = new ArrayList<>();
        @Nullable
        private Integer selfRank;
        @Nullable
        private Integer overallRank;
        @Nullable
        private Integer rankExcludingSelf;
        @Nullable
        private Integer rankInTeam;
        @Nullable
        private Integer rankInTeamExcludingSelf;

        public String getRecipientName() {
            return recipientName;
        }

        public void setRecipientName(String recipientName) {
            this.recipientName = recipientName;
        }

        @Nullable
        public String getRecipientEmail() {
            return recipientEmail;
        }

        public void setRecipientEmail(@Nullable String recipientEmail) {
            this.recipientEmail = recipientEmail;
        }

        public String getRecipientTeam() {
            return recipientTeam;
        }

        public void setRecipientTeam(String recipientTeam) {
            this.recipientTeam = recipientTeam;
        }

        public List<Integer> getRanksReceived() {
            return ranksReceived;
        }

        public void setRanksReceived(List<Integer> ranksReceived) {
            this.ranksReceived = ranksReceived;
        }

        @Nullable
        public Integer getSelfRank() {
            return selfRank;
        }

        public void setSelfRank(@Nullable Integer selfRank) {
            this.selfRank = selfRank;
        }

        @Nullable
        public Integer getOverallRank() {
            return overallRank;
        }

        public void setOverallRank(@Nullable Integer overallRank) {
            this.overallRank = overallRank;
        }

        @Nullable
        public Integer getRankExcludingSelf() {
            return rankExcludingSelf;
        }

        public void setRankExcludingSelf(@Nullable Integer rankExcludingSelf) {
            this.rankExcludingSelf = rankExcludingSelf;
        }

        @Nullable
        public Integer getRankInTeam() {
            return rankInTeam;
        }

        public void setRankInTeam(@Nullable Integer rankInTeam) {
            this.rankInTeam = rankInTeam;
        }

        @Nullable
        public Integer getRankInTeamExcludingSelf() {
            return rankInTeamExcludingSelf;
        }

        public void setRankInTeamExcludingSelf(@Nullable Integer rankInTeamExcludingSelf) {
            this.rankInTeamExcludingSelf = rankInTeamExcludingSelf;
        }
    }
}
