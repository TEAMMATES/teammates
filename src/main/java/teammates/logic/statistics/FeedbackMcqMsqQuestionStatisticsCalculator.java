package teammates.logic.statistics;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import teammates.common.datatransfer.SessionResultsBundle;
import teammates.common.datatransfer.participanttypes.QuestionRecipientType;
import teammates.common.datatransfer.questions.FeedbackMcqQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackMcqResponseDetails;
import teammates.common.datatransfer.questions.FeedbackMsqQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackMsqResponseDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.datatransfer.statistics.FeedbackMcqMsqCourseWideStatistics;
import teammates.common.datatransfer.statistics.FeedbackMcqMsqRecipientStatistics;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackResponse;

/**
 * Calculates MCQ/MSQ question statistics for results pages.
 */
public class FeedbackMcqMsqQuestionStatisticsCalculator implements
        FeedbackQuestionStatisticsCalculator<FeedbackMcqMsqCourseWideStatistics, FeedbackMcqMsqRecipientStatistics> {

    private static final String MSQ_ANSWER_NONE_OF_THE_ABOVE = "";

    @Override
    public FeedbackMcqMsqCourseWideStatistics calculateCourseWide(
            FeedbackQuestion question, List<FeedbackResponse> responses, SessionResultsBundle bundle) {
        FeedbackQuestionDetails details = question.getQuestionDetailsCopy();
        List<String> optionLabels = buildOptionLabels(details);
        Map<String, Double> weightMap = isWeighted(details) ? buildWeightMap(details) : Map.of();
        Map<String, Integer> counts = buildFrequencyCounts(optionLabels, details, responses);
        int totalAnswerCount = counts.values().stream().mapToInt(Integer::intValue).sum();

        FeedbackMcqMsqCourseWideStatistics statistics =
                new FeedbackMcqMsqCourseWideStatistics(question.getQuestionType());
        statistics.setHasWeights(isWeighted(details));
        statistics.setHasAnswers(totalAnswerCount > 0);
        statistics.setRows(buildOptionRows(optionLabels, weightMap, counts, totalAnswerCount));
        if (isWeighted(details) && totalAnswerCount > 0) {
            statistics.setPerRecipientRows(buildPerRecipientRows(optionLabels, details, weightMap, responses));
        }
        return statistics;
    }

    @Override
    public FeedbackMcqMsqRecipientStatistics calculateForRecipient(
            FeedbackQuestion question, List<FeedbackResponse> responses, SessionResultsBundle bundle, UUID recipientId) {
        FeedbackQuestionDetails details = question.getQuestionDetailsCopy();
        List<String> optionLabels = buildOptionLabels(details);
        Map<String, Double> weightMap = isWeighted(details) ? buildWeightMap(details) : Map.of();
        Map<String, Integer> counts = buildFrequencyCounts(optionLabels, details, responses);
        int totalAnswerCount = counts.values().stream().mapToInt(Integer::intValue).sum();

        FeedbackMcqMsqRecipientStatistics statistics =
                new FeedbackMcqMsqRecipientStatistics(question.getQuestionType());
        statistics.setHasWeights(isWeighted(details));
        statistics.setHasAnswers(totalAnswerCount > 0);
        statistics.setRows(buildOptionRows(optionLabels, weightMap, counts, totalAnswerCount));
        return statistics;
    }

    private boolean isWeighted(FeedbackQuestionDetails details) {
        if (details instanceof FeedbackMcqQuestionDetails mcq) {
            return mcq.isHasAssignedWeights();
        }
        if (details instanceof FeedbackMsqQuestionDetails msq) {
            return msq.isHasAssignedWeights();
        }
        throw new IllegalArgumentException("Unsupported question type: " + details.getClass());
    }

    private List<String> buildOptionLabels(FeedbackQuestionDetails details) {
        List<String> labels;
        boolean otherEnabled;
        if (details instanceof FeedbackMcqQuestionDetails mcq) {
            labels = new ArrayList<>(mcq.getMcqChoices());
            otherEnabled = mcq.isOtherEnabled();
        } else if (details instanceof FeedbackMsqQuestionDetails msq) {
            labels = new ArrayList<>(msq.getMsqChoices());
            otherEnabled = msq.isOtherEnabled();
        } else {
            throw new IllegalArgumentException("Unsupported question type: " + details.getClass());
        }
        if (otherEnabled) {
            labels.add("Other");
        }
        return labels;
    }

    private Map<String, Double> buildWeightMap(FeedbackQuestionDetails details) {
        List<String> choices;
        List<Double> choiceWeights;
        double otherWeight;
        boolean otherEnabled;
        if (details instanceof FeedbackMcqQuestionDetails mcq) {
            choices = mcq.getMcqChoices();
            choiceWeights = mcq.getMcqWeights();
            otherWeight = mcq.getMcqOtherWeight();
            otherEnabled = mcq.isOtherEnabled();
        } else if (details instanceof FeedbackMsqQuestionDetails msq) {
            choices = msq.getMsqChoices();
            choiceWeights = msq.getMsqWeights();
            otherWeight = msq.getMsqOtherWeight();
            otherEnabled = msq.isOtherEnabled();
        } else {
            throw new IllegalArgumentException("Unsupported question type: " + details.getClass());
        }
        Map<String, Double> weights = new LinkedHashMap<>();
        for (int i = 0; i < choices.size(); i++) {
            weights.put(choices.get(i), choiceWeights.get(i));
        }
        if (otherEnabled) {
            weights.put("Other", otherWeight);
        }
        return weights;
    }

    private Map<String, Integer> buildFrequencyCounts(
            List<String> optionLabels, FeedbackQuestionDetails details, List<FeedbackResponse> responses) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (String label : optionLabels) {
            counts.put(label, 0);
        }
        for (FeedbackResponse response : responses) {
            if (details instanceof FeedbackMcqQuestionDetails) {
                FeedbackMcqResponseDetails responseDetails =
                        (FeedbackMcqResponseDetails) response.getFeedbackResponseDetailsCopy();
                String key = responseDetails.isOther() ? "Other" : responseDetails.getAnswer();
                counts.merge(key, 1, Integer::sum);
            } else if (details instanceof FeedbackMsqQuestionDetails msq) {
                FeedbackMsqResponseDetails responseDetails =
                        (FeedbackMsqResponseDetails) response.getFeedbackResponseDetailsCopy();
                accumulateMsqCounts(msq, responseDetails, counts);
            }
        }
        return counts;
    }

    /**
     * Accumulates option counts for a single MSQ response, handling "Other" and "None of the above".
     */
    static void accumulateMsqCounts(
            FeedbackMsqQuestionDetails details,
            FeedbackMsqResponseDetails responseDetails,
            Map<String, Integer> counts) {
        if (responseDetails.isOther()) {
            counts.merge("Other", 1, Integer::sum);
        }
        for (String answer : responseDetails.getAnswers()) {
            if (MSQ_ANSWER_NONE_OF_THE_ABOVE.equals(answer)) {
                continue;
            }
            // skip "Other" text if generateOptionsFor allows dynamic options
            if (!details.getMsqChoices().contains(answer)
                    && details.getGenerateOptionsFor() == QuestionRecipientType.NONE) {
                continue;
            }
            counts.merge(answer, 1, Integer::sum);
        }
    }

    /**
     * Builds option summary rows from counts and optional weights.
     */
    static List<FeedbackMcqMsqCourseWideStatistics.McqMsqOptionRow> buildOptionRows(
            List<String> optionLabels, Map<String, Double> weightMap, Map<String, Integer> counts,
            int totalAnswerCount) {
        double totalWeightedCount = 0;
        for (String option : optionLabels) {
            Double weight = weightMap.get(option);
            if (weight != null) {
                totalWeightedCount += weight * counts.getOrDefault(option, 0);
            }
        }

        List<FeedbackMcqMsqCourseWideStatistics.McqMsqOptionRow> rows = new ArrayList<>();
        for (String option : optionLabels) {
            FeedbackMcqMsqCourseWideStatistics.McqMsqOptionRow row =
                    new FeedbackMcqMsqCourseWideStatistics.McqMsqOptionRow();
            row.setOption(option);
            int count = counts.getOrDefault(option, 0);
            row.setCount(count);

            double percentage = totalAnswerCount == 0 ? 0 : 100.0 * count / totalAnswerCount;
            row.setPercentage(roundToTwoDecimals(percentage));

            Double weight = weightMap.get(option);
            row.setWeight(weight);
            if (weight != null) {
                double weightedPercentage = totalWeightedCount == 0
                        ? 0 : 100.0 * weight * count / totalWeightedCount;
                row.setWeightedPercentage(roundToTwoDecimals(weightedPercentage));
            }
            rows.add(row);
        }
        return rows;
    }

    private List<FeedbackMcqMsqCourseWideStatistics.McqMsqPerRecipientRow> buildPerRecipientRows(
            List<String> optionLabels, FeedbackQuestionDetails details,
            Map<String, Double> weightMap, List<FeedbackResponse> responses) {
        Map<String, FeedbackMcqMsqCourseWideStatistics.McqMsqPerRecipientRow> rowsByKey = new LinkedHashMap<>();

        for (FeedbackResponse response : responses) {
            if (response.getRecipient().getRecipientUserId() == null) {
                continue;
            }

            String recipientKey = response.getRecipient().getKey();
            FeedbackMcqMsqCourseWideStatistics.McqMsqPerRecipientRow row =
                    rowsByKey.computeIfAbsent(recipientKey, k -> {
                        FeedbackMcqMsqCourseWideStatistics.McqMsqPerRecipientRow r =
                                new FeedbackMcqMsqCourseWideStatistics.McqMsqPerRecipientRow();
                        r.setRecipientName(response.getRecipient().getDisplayName());
                        r.setRecipientTeam(response.getRecipient().getTeamName());
                        for (String label : optionLabels) {
                            r.getResponseCountPerOption().put(label, 0);
                        }
                        return r;
                    });

            if (details instanceof FeedbackMcqQuestionDetails) {
                FeedbackMcqResponseDetails responseDetails =
                        (FeedbackMcqResponseDetails) response.getFeedbackResponseDetailsCopy();
                String chosenOption = responseDetails.isOther() ? "Other" : responseDetails.getAnswer();
                row.getResponseCountPerOption().merge(chosenOption, 1, Integer::sum);
            } else if (details instanceof FeedbackMsqQuestionDetails msq) {
                FeedbackMsqResponseDetails responseDetails =
                        (FeedbackMsqResponseDetails) response.getFeedbackResponseDetailsCopy();
                accumulateMsqCounts(msq, responseDetails, row.getResponseCountPerOption());
            }
        }

        for (FeedbackMcqMsqCourseWideStatistics.McqMsqPerRecipientRow row : rowsByKey.values()) {
            double total = 0;
            int numAnswers = 0;
            for (Map.Entry<String, Integer> entry : row.getResponseCountPerOption().entrySet()) {
                Double weight = weightMap.get(entry.getKey());
                int count = entry.getValue();
                if (weight != null) {
                    total += weight * count;
                }
                numAnswers += count;
            }
            row.setTotal(roundToFiveDecimals(total));
            row.setAverage(numAnswers == 0 ? 0 : roundToTwoDecimals(total / numAnswers));
        }

        return new ArrayList<>(rowsByKey.values());
    }

    private static double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private static double roundToFiveDecimals(double value) {
        return Math.round(value * 100000.0) / 100000.0;
    }
}
