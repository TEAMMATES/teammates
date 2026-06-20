package teammates.logic.statistics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import jakarta.annotation.Nullable;

import teammates.common.datatransfer.SessionResultsBundle;
import teammates.common.datatransfer.questions.FeedbackRubricQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackRubricResponseDetails;
import teammates.common.datatransfer.statistics.FeedbackQuestionResultsStatisticsView;
import teammates.common.datatransfer.statistics.FeedbackRubricStatistics;
import teammates.common.datatransfer.statistics.FeedbackRubricStatistics.RubricChoiceCell;
import teammates.common.datatransfer.statistics.FeedbackRubricStatistics.RubricPerCriterionRow;
import teammates.common.datatransfer.statistics.FeedbackRubricStatistics.RubricPerRecipientStats;
import teammates.common.datatransfer.statistics.FeedbackRubricStatistics.RubricSubQuestionRow;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackResponse;

/**
 * Calculates rubric question statistics for results pages.
 */
public class FeedbackRubricQuestionStatisticsCalculator implements
        FeedbackQuestionStatisticsCalculator<FeedbackRubricStatistics, FeedbackRubricStatistics> {

    private static final int RUBRIC_ANSWER_NOT_CHOSEN = -1;

    @Override
    public FeedbackRubricStatistics calculateCourseWide(
            FeedbackQuestion question, List<FeedbackResponse> responses, SessionResultsBundle bundle) {
        FeedbackRubricQuestionDetails details =
                (FeedbackRubricQuestionDetails) question.getQuestionDetailsCopy();
        return calculate(details, responses, FeedbackQuestionResultsStatisticsView.COURSE_WIDE);
    }

    @Override
    public FeedbackRubricStatistics calculateForRecipient(
            FeedbackQuestion question, List<FeedbackResponse> responses, SessionResultsBundle bundle,
            UUID recipientId) {
        FeedbackRubricQuestionDetails details =
                (FeedbackRubricQuestionDetails) question.getQuestionDetailsCopy();
        return calculate(details, responses, FeedbackQuestionResultsStatisticsView.RECIPIENT);
    }

    private FeedbackRubricStatistics calculate(
            FeedbackRubricQuestionDetails details, List<FeedbackResponse> responses,
            FeedbackQuestionResultsStatisticsView view) {
        List<String> subQuestions = details.getRubricSubQuestions();
        List<String> choices = details.getRubricChoices();
        List<List<Double>> weights = details.getRubricWeightsForEachCell();
        boolean hasWeights = details.isHasAssignedWeights();
        int numSubQ = subQuestions.size();
        int numChoices = choices.size();

        int[][] answers = new int[numSubQ][numChoices];
        int[][] answersExcludeSelf = new int[numSubQ][numChoices];

        for (FeedbackResponse response : responses) {
            FeedbackRubricResponseDetails rd =
                    (FeedbackRubricResponseDetails) response.getFeedbackResponseDetailsCopy();
            boolean isSelf = isSelfResponse(response);
            List<Integer> responseAnswers = rd.getAnswer();
            for (int i = 0; i < responseAnswers.size() && i < numSubQ; i++) {
                int subAnswer = responseAnswers.get(i);
                if (subAnswer == RUBRIC_ANSWER_NOT_CHOSEN || subAnswer >= numChoices) {
                    continue;
                }
                answers[i][subAnswer]++;
                if (!isSelf) {
                    answersExcludeSelf[i][subAnswer]++;
                }
            }
        }

        FeedbackRubricStatistics statistics = new FeedbackRubricStatistics(view);
        statistics.setSubQuestions(subQuestions);
        statistics.setChoices(choices);
        statistics.setHasWeights(hasWeights);
        statistics.setRows(buildSubQuestionRows(subQuestions, weights, hasWeights, answers));
        statistics.setRowsExcludeSelf(buildSubQuestionRows(subQuestions, weights, hasWeights, answersExcludeSelf));

        if (view == FeedbackQuestionResultsStatisticsView.COURSE_WIDE && hasWeights) {
            statistics.setPerRecipientStats(buildPerRecipientStats(details, responses, weights));
        }

        return statistics;
    }

    private static List<RubricSubQuestionRow> buildSubQuestionRows(
            List<String> subQuestions, List<List<Double>> weights, boolean hasWeights, int[][] answers) {
        List<RubricSubQuestionRow> rows = new ArrayList<>();
        for (int r = 0; r < answers.length; r++) {
            int rowSum = rowSum(answers[r]);
            RubricSubQuestionRow row = new RubricSubQuestionRow();
            row.setSubQuestion(subQuestions.get(r));
            row.setCells(buildChoiceCells(weights, hasWeights, answers[r], rowSum, r));
            if (hasWeights) {
                row.setWeightAverage(subQuestionWeightAverage(weights, answers[r], r));
            }
            rows.add(row);
        }
        return rows;
    }

    private static List<RubricChoiceCell> buildChoiceCells(
            List<List<Double>> weights, boolean hasWeights, int[] row, int rowSum, int r) {
        List<RubricChoiceCell> cells = new ArrayList<>();
        for (int c = 0; c < row.length; c++) {
            RubricChoiceCell cell = new RubricChoiceCell();
            cell.setCount(row[c]);
            cell.setPercentage(rowSum == 0 ? 0.0 : roundToTwoDecimals(100.0 * row[c] / rowSum));
            if (hasWeights && r < weights.size() && c < weights.get(r).size()) {
                cell.setWeight(weights.get(r).get(c));
            }
            cells.add(cell);
        }
        return cells;
    }

    private static List<RubricPerRecipientStats> buildPerRecipientStats(
            FeedbackRubricQuestionDetails details, List<FeedbackResponse> responses,
            List<List<Double>> weights) {
        List<String> subQuestions = details.getRubricSubQuestions();
        int numSubQ = subQuestions.size();
        int numChoices = details.getRubricChoices().size();

        Map<String, PerRecipientAccumulator> accumulators = new LinkedHashMap<>();

        for (FeedbackResponse response : responses) {
            if (response.getRecipient().isNoSpecificRecipient()) {
                continue;
            }
            String key = response.getRecipient().getKey();
            PerRecipientAccumulator acc = accumulators.computeIfAbsent(key, k -> {
                String email = response.getRecipient().isRecipientUser()
                        ? response.getRecipient().getRecipientUser().getEmail() : null;
                return new PerRecipientAccumulator(
                        response.getRecipient().getDisplayName(),
                        email,
                        response.getRecipient().getTeamName(),
                        numSubQ, numChoices);
            });

            FeedbackRubricResponseDetails rd =
                    (FeedbackRubricResponseDetails) response.getFeedbackResponseDetailsCopy();
            List<Integer> responseAnswers = rd.getAnswer();
            for (int i = 0; i < responseAnswers.size() && i < numSubQ; i++) {
                int subAnswer = responseAnswers.get(i);
                if (subAnswer == RUBRIC_ANSWER_NOT_CHOSEN || subAnswer >= numChoices) {
                    continue;
                }
                acc.answers[i][subAnswer]++;
                if (i < weights.size() && subAnswer < weights.get(i).size()) {
                    Double w = weights.get(i).get(subAnswer);
                    if (w != null) {
                        acc.subQTotalChosenWeight[i] =
                                roundToFiveDecimals(acc.subQTotalChosenWeight[i] + w);
                        acc.areSubQWeightsAllNull[i] = false;
                    }
                }
            }
        }

        List<Double> choiceWeightsAverage = buildChoiceWeightsAverage(weights, numChoices);

        List<RubricPerRecipientStats> result = new ArrayList<>();
        for (PerRecipientAccumulator acc : accumulators.values()) {
            RubricPerRecipientStats stats = new RubricPerRecipientStats();
            stats.setRecipientName(acc.recipientName);
            stats.setRecipientEmail(acc.recipientEmail);
            stats.setRecipientTeam(acc.recipientTeam);
            stats.setPerCriterionRows(buildPerCriterionRows(subQuestions, weights, acc));
            stats.setOverallCells(buildOverallCells(weights, choiceWeightsAverage, acc.answers));
            stats.setOverallTotal(overallTotal(acc));
            stats.setOverallAverage(overallAverage(acc, weights));
            stats.setSubQuestionAverages(buildSubQuestionAverages(weights, acc.answers, subQuestions.size()));
            result.add(stats);
        }
        return result;
    }

    private static List<RubricPerCriterionRow> buildPerCriterionRows(
            List<String> subQuestions, List<List<Double>> weights, PerRecipientAccumulator acc) {
        List<RubricPerCriterionRow> rows = new ArrayList<>();
        for (int r = 0; r < acc.answers.length; r++) {
            int rowSum = rowSum(acc.answers[r]);
            RubricPerCriterionRow row = new RubricPerCriterionRow();
            row.setSubQuestion(subQuestions.get(r));
            row.setCells(buildChoiceCells(weights, true, acc.answers[r], rowSum, r));
            row.setTotal(acc.areSubQWeightsAllNull[r] ? null : acc.subQTotalChosenWeight[r]);
            row.setAverage(subQuestionWeightAverage(weights, acc.answers[r], r));
            rows.add(row);
        }
        return rows;
    }

    private static List<RubricChoiceCell> buildOverallCells(
            List<List<Double>> weights, List<Double> choiceWeightsAverage, int[][] answers) {
        int numChoices = answers.length == 0 ? 0 : answers[0].length;
        int[] colSums = columnSums(answers);
        int total = 0;
        for (int v : colSums) {
            total += v;
        }
        List<RubricChoiceCell> cells = new ArrayList<>();
        for (int c = 0; c < numChoices; c++) {
            RubricChoiceCell cell = new RubricChoiceCell();
            cell.setCount(colSums[c]);
            cell.setPercentage(total == 0 ? 0.0 : roundToTwoDecimals(100.0 * colSums[c] / total));
            cell.setWeight(c < choiceWeightsAverage.size() ? choiceWeightsAverage.get(c) : null);
            cells.add(cell);
        }
        return cells;
    }

    private static List<Double> buildSubQuestionAverages(
            List<List<Double>> weights, int[][] answers, int numSubQ) {
        List<Double> averages = new ArrayList<>();
        for (int r = 0; r < numSubQ; r++) {
            averages.add(subQuestionWeightAverage(weights, answers[r], r));
        }
        return averages;
    }

    /** Average of the question weights for each choice column across sub-questions. */
    private static List<Double> buildChoiceWeightsAverage(List<List<Double>> weights, int numChoices) {
        double[] sums = new double[numChoices];
        int[] counts = new int[numChoices];
        for (List<Double> row : weights) {
            for (int c = 0; c < row.size() && c < numChoices; c++) {
                Double w = row.get(c);
                if (w != null) {
                    sums[c] += w;
                    counts[c]++;
                }
            }
        }
        List<Double> result = new ArrayList<>();
        for (int c = 0; c < numChoices; c++) {
            result.add(counts[c] == 0 ? null : roundToTwoDecimals(sums[c] / counts[c]));
        }
        return result;
    }

    @Nullable
    private static Double overallTotal(PerRecipientAccumulator acc) {
        boolean allNull = true;
        for (boolean b : acc.areSubQWeightsAllNull) {
            if (!b) {
                allNull = false;
                break;
            }
        }
        if (allNull) {
            return null;
        }
        double sum = 0;
        for (int i = 0; i < acc.subQTotalChosenWeight.length; i++) {
            if (!acc.areSubQWeightsAllNull[i]) {
                sum += acc.subQTotalChosenWeight[i];
            }
        }
        return roundToTwoDecimals(sum);
    }

    @Nullable
    private static Double overallAverage(PerRecipientAccumulator acc, List<List<Double>> weights) {
        Double total = overallTotal(acc);
        if (total == null) {
            return null;
        }
        int totalResponses = 0;
        for (int r = 0; r < acc.answers.length; r++) {
            for (int c = 0; c < acc.answers[r].length; c++) {
                if (r < weights.size() && c < weights.get(r).size() && weights.get(r).get(c) != null) {
                    totalResponses += acc.answers[r][c];
                }
            }
        }
        return totalResponses == 0 ? null : roundToTwoDecimals(total / totalResponses);
    }

    @Nullable
    private static Double subQuestionWeightAverage(List<List<Double>> weights, int[] row, int r) {
        if (r >= weights.size()) {
            return null;
        }
        List<Double> rowWeights = weights.get(r);
        int validCount = 0;
        double weightedSum = 0;
        for (int c = 0; c < row.length && c < rowWeights.size(); c++) {
            Double w = rowWeights.get(c);
            if (w != null) {
                validCount += row[c];
                weightedSum += row[c] * w;
            }
        }
        return validCount == 0 ? null : roundToTwoDecimals(weightedSum / validCount);
    }

    private static int rowSum(int[] row) {
        int sum = 0;
        for (int v : row) {
            sum += v;
        }
        return sum;
    }

    private static int[] columnSums(int[][] matrix) {
        int numCols = matrix.length == 0 ? 0 : matrix[0].length;
        int[] sums = new int[numCols];
        for (int[] row : matrix) {
            for (int c = 0; c < row.length && c < numCols; c++) {
                sums[c] += row[c];
            }
        }
        return sums;
    }

    private static boolean isSelfResponse(FeedbackResponse response) {
        UUID giverUserId = response.getGiver().getGiverUserId();
        UUID recipientUserId = response.getRecipient().getRecipientUserId();
        return giverUserId != null && giverUserId.equals(recipientUserId);
    }

    private static double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private static double roundToFiveDecimals(double value) {
        return Math.round(value * 100000.0) / 100000.0;
    }

    private static class PerRecipientAccumulator {
        final String recipientName;
        @Nullable
        final String recipientEmail;
        final String recipientTeam;
        final int[][] answers;
        final boolean[] areSubQWeightsAllNull;
        final double[] subQTotalChosenWeight;

        PerRecipientAccumulator(String recipientName, @Nullable String recipientEmail,
                String recipientTeam, int numSubQ, int numChoices) {
            this.recipientName = recipientName;
            this.recipientEmail = recipientEmail;
            this.recipientTeam = recipientTeam;
            this.answers = new int[numSubQ][numChoices];
            this.areSubQWeightsAllNull = new boolean[numSubQ];
            Arrays.fill(this.areSubQWeightsAllNull, true);
            this.subQTotalChosenWeight = new double[numSubQ];
        }
    }
}
