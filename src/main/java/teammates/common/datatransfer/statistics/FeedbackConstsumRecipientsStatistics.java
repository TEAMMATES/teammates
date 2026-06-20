package teammates.common.datatransfer.statistics;

import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.Nullable;

import teammates.common.datatransfer.questions.FeedbackQuestionType;

/**
 * Constant sum recipients question statistics for session results (shared for both course-wide and per-recipient views).
 */
public class FeedbackConstsumRecipientsStatistics extends FeedbackQuestionResultsStatistics {
    private List<ConstsumRecipientRow> rows = new ArrayList<>();

    public FeedbackConstsumRecipientsStatistics() {
        super(FeedbackQuestionType.CONSTSUM_RECIPIENTS);
    }

    public FeedbackConstsumRecipientsStatistics(FeedbackQuestionResultsStatisticsView view) {
        super(FeedbackQuestionType.CONSTSUM_RECIPIENTS, view);
    }

    public List<ConstsumRecipientRow> getRows() {
        return rows;
    }

    public void setRows(List<ConstsumRecipientRow> rows) {
        this.rows = rows;
    }

    /**
     * One row in the constant sum recipients summary table, corresponding to one recipient.
     */
    public static class ConstsumRecipientRow {
        private String recipientName;
        @Nullable
        private String recipientEmail;
        private String recipientTeam;
        private boolean isCurrentRecipient;
        private List<Integer> pointsReceived = new ArrayList<>();
        private int total;
        private double average;
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

        public boolean isCurrentRecipient() {
            return isCurrentRecipient;
        }

        public void setIsCurrentRecipient(boolean isCurrentRecipient) {
            this.isCurrentRecipient = isCurrentRecipient;
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

        @Nullable
        public Double getAverageExcludingSelf() {
            return averageExcludingSelf;
        }

        public void setAverageExcludingSelf(@Nullable Double averageExcludingSelf) {
            this.averageExcludingSelf = averageExcludingSelf;
        }
    }
}
