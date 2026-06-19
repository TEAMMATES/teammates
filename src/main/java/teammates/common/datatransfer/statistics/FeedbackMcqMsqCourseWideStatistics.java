package teammates.common.datatransfer.statistics;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.annotation.Nullable;

import teammates.common.datatransfer.questions.FeedbackQuestionType;

/**
 * Course-wide MCQ/MSQ statistics for session results.
 */
public class FeedbackMcqMsqCourseWideStatistics extends FeedbackQuestionResultsStatistics {
    private boolean hasAnswers;
    private boolean hasWeights;
    private List<McqMsqOptionRow> rows = new ArrayList<>();
    private List<McqMsqPerRecipientRow> perRecipientRows = new ArrayList<>();

    public FeedbackMcqMsqCourseWideStatistics(FeedbackQuestionType questionType) {
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

    public List<McqMsqOptionRow> getRows() {
        return rows;
    }

    public void setRows(List<McqMsqOptionRow> rows) {
        this.rows = rows;
    }

    public List<McqMsqPerRecipientRow> getPerRecipientRows() {
        return perRecipientRows;
    }

    public void setPerRecipientRows(List<McqMsqPerRecipientRow> perRecipientRows) {
        this.perRecipientRows = perRecipientRows;
    }

    /**
     * One row in the MCQ/MSQ option summary table.
     */
    public static class McqMsqOptionRow {
        private String option;
        @Nullable
        private Double weight;
        private int count;
        private double percentage;
        @Nullable
        private Double weightedPercentage;

        public String getOption() {
            return option;
        }

        public void setOption(String option) {
            this.option = option;
        }

        @Nullable
        public Double getWeight() {
            return weight;
        }

        public void setWeight(@Nullable Double weight) {
            this.weight = weight;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public double getPercentage() {
            return percentage;
        }

        public void setPercentage(double percentage) {
            this.percentage = percentage;
        }

        @Nullable
        public Double getWeightedPercentage() {
            return weightedPercentage;
        }

        public void setWeightedPercentage(@Nullable Double weightedPercentage) {
            this.weightedPercentage = weightedPercentage;
        }
    }

    /**
     * One row in the per-recipient MCQ/MSQ statistics table (only present when weights are enabled).
     */
    public static class McqMsqPerRecipientRow {
        private String recipientName;
        private String recipientTeam;
        private Map<String, Integer> responseCountPerOption = new LinkedHashMap<>();
        private double total;
        private double average;

        public String getRecipientName() {
            return recipientName;
        }

        public void setRecipientName(String recipientName) {
            this.recipientName = recipientName;
        }

        public String getRecipientTeam() {
            return recipientTeam;
        }

        public void setRecipientTeam(String recipientTeam) {
            this.recipientTeam = recipientTeam;
        }

        public Map<String, Integer> getResponseCountPerOption() {
            return responseCountPerOption;
        }

        public void setResponseCountPerOption(Map<String, Integer> responseCountPerOption) {
            this.responseCountPerOption = responseCountPerOption;
        }

        public double getTotal() {
            return total;
        }

        public void setTotal(double total) {
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
