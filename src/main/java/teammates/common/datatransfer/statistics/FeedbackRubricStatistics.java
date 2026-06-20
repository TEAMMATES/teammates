package teammates.common.datatransfer.statistics;

import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.Nullable;

import teammates.common.datatransfer.questions.FeedbackQuestionType;

/**
 * Rubric question statistics for session results (shared for both course-wide and per-recipient views).
 */
public class FeedbackRubricStatistics extends FeedbackQuestionResultsStatistics {
    private List<String> subQuestions = new ArrayList<>();
    private List<String> choices = new ArrayList<>();
    private boolean hasWeights;
    private List<RubricSubQuestionRow> rows = new ArrayList<>();
    private List<RubricSubQuestionRow> rowsExcludeSelf = new ArrayList<>();
    private List<RubricPerRecipientStats> perRecipientStats = new ArrayList<>();

    public FeedbackRubricStatistics() {
        super(FeedbackQuestionType.RUBRIC);
    }

    public FeedbackRubricStatistics(FeedbackQuestionResultsStatisticsView view) {
        super(FeedbackQuestionType.RUBRIC, view);
    }

    public List<String> getSubQuestions() {
        return subQuestions;
    }

    public void setSubQuestions(List<String> subQuestions) {
        this.subQuestions = subQuestions;
    }

    public List<String> getChoices() {
        return choices;
    }

    public void setChoices(List<String> choices) {
        this.choices = choices;
    }

    public boolean isHasWeights() {
        return hasWeights;
    }

    public void setHasWeights(boolean hasWeights) {
        this.hasWeights = hasWeights;
    }

    public List<RubricSubQuestionRow> getRows() {
        return rows;
    }

    public void setRows(List<RubricSubQuestionRow> rows) {
        this.rows = rows;
    }

    public List<RubricSubQuestionRow> getRowsExcludeSelf() {
        return rowsExcludeSelf;
    }

    public void setRowsExcludeSelf(List<RubricSubQuestionRow> rowsExcludeSelf) {
        this.rowsExcludeSelf = rowsExcludeSelf;
    }

    public List<RubricPerRecipientStats> getPerRecipientStats() {
        return perRecipientStats;
    }

    public void setPerRecipientStats(List<RubricPerRecipientStats> perRecipientStats) {
        this.perRecipientStats = perRecipientStats;
    }

    /**
     * One row in the rubric summary table, corresponding to one sub-question.
     */
    public static class RubricSubQuestionRow {
        private String subQuestion;
        private List<RubricChoiceCell> cells = new ArrayList<>();
        @Nullable
        private Double weightAverage;

        public String getSubQuestion() {
            return subQuestion;
        }

        public void setSubQuestion(String subQuestion) {
            this.subQuestion = subQuestion;
        }

        public List<RubricChoiceCell> getCells() {
            return cells;
        }

        public void setCells(List<RubricChoiceCell> cells) {
            this.cells = cells;
        }

        @Nullable
        public Double getWeightAverage() {
            return weightAverage;
        }

        public void setWeightAverage(@Nullable Double weightAverage) {
            this.weightAverage = weightAverage;
        }
    }

    /**
     * One cell in a rubric choice column, reused across summary, per-criterion, and overall tables.
     * {@code weight} is null when weights are not assigned; in the overall table it represents
     * the average weight for that choice column across sub-questions.
     */
    public static class RubricChoiceCell {
        private double percentage;
        private int count;
        @Nullable
        private Double weight;

        public double getPercentage() {
            return percentage;
        }

        public void setPercentage(double percentage) {
            this.percentage = percentage;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        @Nullable
        public Double getWeight() {
            return weight;
        }

        public void setWeight(@Nullable Double weight) {
            this.weight = weight;
        }
    }

    /**
     * Per-recipient rubric statistics (course-wide view only).
     */
    public static class RubricPerRecipientStats {
        private String recipientName;
        @Nullable
        private String recipientEmail;
        private String recipientTeam;
        private List<RubricPerCriterionRow> perCriterionRows = new ArrayList<>();
        private List<RubricChoiceCell> overallCells = new ArrayList<>();
        @Nullable
        private Double overallTotal;
        @Nullable
        private Double overallAverage;
        private List<Double> subQuestionAverages = new ArrayList<>();

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

        public List<RubricPerCriterionRow> getPerCriterionRows() {
            return perCriterionRows;
        }

        public void setPerCriterionRows(List<RubricPerCriterionRow> perCriterionRows) {
            this.perCriterionRows = perCriterionRows;
        }

        public List<RubricChoiceCell> getOverallCells() {
            return overallCells;
        }

        public void setOverallCells(List<RubricChoiceCell> overallCells) {
            this.overallCells = overallCells;
        }

        @Nullable
        public Double getOverallTotal() {
            return overallTotal;
        }

        public void setOverallTotal(@Nullable Double overallTotal) {
            this.overallTotal = overallTotal;
        }

        @Nullable
        public Double getOverallAverage() {
            return overallAverage;
        }

        public void setOverallAverage(@Nullable Double overallAverage) {
            this.overallAverage = overallAverage;
        }

        public List<Double> getSubQuestionAverages() {
            return subQuestionAverages;
        }

        public void setSubQuestionAverages(List<Double> subQuestionAverages) {
            this.subQuestionAverages = subQuestionAverages;
        }
    }

    /**
     * One row in the per-recipient per-criterion table, corresponding to one sub-question for one recipient.
     */
    public static class RubricPerCriterionRow {
        private String subQuestion;
        private List<RubricChoiceCell> cells = new ArrayList<>();
        @Nullable
        private Double total;
        @Nullable
        private Double average;

        public String getSubQuestion() {
            return subQuestion;
        }

        public void setSubQuestion(String subQuestion) {
            this.subQuestion = subQuestion;
        }

        public List<RubricChoiceCell> getCells() {
            return cells;
        }

        public void setCells(List<RubricChoiceCell> cells) {
            this.cells = cells;
        }

        @Nullable
        public Double getTotal() {
            return total;
        }

        public void setTotal(@Nullable Double total) {
            this.total = total;
        }

        @Nullable
        public Double getAverage() {
            return average;
        }

        public void setAverage(@Nullable Double average) {
            this.average = average;
        }
    }
}
