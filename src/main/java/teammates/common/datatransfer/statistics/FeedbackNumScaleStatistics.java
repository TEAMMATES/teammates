package teammates.common.datatransfer.statistics;

import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.Nullable;

import teammates.common.datatransfer.questions.FeedbackQuestionType;

/**
 * Numerical scale question statistics for session results (shared for both course-wide and per-recipient views).
 */
public class FeedbackNumScaleStatistics extends FeedbackQuestionResultsStatistics {
    private List<NumScaleRecipientRow> rows = new ArrayList<>();

    public FeedbackNumScaleStatistics() {
        super(FeedbackQuestionType.NUMSCALE);
    }

    public FeedbackNumScaleStatistics(FeedbackQuestionResultsStatisticsView view) {
        super(FeedbackQuestionType.NUMSCALE, view);
    }

    public List<NumScaleRecipientRow> getRows() {
        return rows;
    }

    public void setRows(List<NumScaleRecipientRow> rows) {
        this.rows = rows;
    }

    /**
     * One row in the numerical scale summary table, corresponding to one recipient.
     */
    public static class NumScaleRecipientRow {
        private String recipientName;
        @Nullable
        private String recipientEmail;
        private String recipientTeam;
        private boolean isCurrentRecipient;
        @Nullable
        private Double average;
        @Nullable
        private Double min;
        @Nullable
        private Double max;
        @Nullable
        private Double averageExcludingSelf;

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

        public boolean isIsCurrentRecipient() {
            return isCurrentRecipient;
        }

        public void setIsCurrentRecipient(boolean isCurrentRecipient) {
            this.isCurrentRecipient = isCurrentRecipient;
        }

        @Nullable
        public Double getAverage() {
            return average;
        }

        public void setAverage(@Nullable Double average) {
            this.average = average;
        }

        @Nullable
        public Double getMin() {
            return min;
        }

        public void setMin(@Nullable Double min) {
            this.min = min;
        }

        @Nullable
        public Double getMax() {
            return max;
        }

        public void setMax(@Nullable Double max) {
            this.max = max;
        }

        @Nullable
        public Double getAverageExcludingSelf() {
            return averageExcludingSelf;
        }

        public void setAverageExcludingSelf(@Nullable Double averageExcludingSelf) {
            this.averageExcludingSelf = averageExcludingSelf;
        }
    }
}
