package teammates.logic.statistics;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import teammates.common.datatransfer.SessionResultsBundle;
import teammates.common.datatransfer.questions.FeedbackRankOptionsQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackRankOptionsResponseDetails;
import teammates.common.datatransfer.statistics.FeedbackRankOptionsStatistics;
import teammates.common.datatransfer.statistics.FeedbackRankOptionsStatistics.RankOptionsOptionRow;
import teammates.common.util.Const;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.User;

/**
 * Calculates rank options question statistics for results pages.
 */
public class FeedbackRankOptionsQuestionStatisticsCalculator implements
        FeedbackQuestionStatisticsCalculator<FeedbackRankOptionsStatistics, FeedbackRankOptionsStatistics> {

    @Override
    public FeedbackRankOptionsStatistics calculateCourseWide(
            FeedbackQuestion question, List<FeedbackResponse> responses, SessionResultsBundle bundle) {
        FeedbackRankOptionsQuestionDetails details =
                (FeedbackRankOptionsQuestionDetails) question.getQuestionDetailsCopy();
        List<String> optionNames = details.getOptions();

        Map<String, List<Integer>> ranksPerOption = new LinkedHashMap<>();
        for (String option : optionNames) {
            ranksPerOption.put(option, new ArrayList<>());
        }

        for (FeedbackResponse response : responses) {
            FeedbackRankOptionsResponseDetails rd =
                    (FeedbackRankOptionsResponseDetails) response.getFeedbackResponseDetailsCopy();
            List<Integer> normalized = normalizeRanks(rd.getAnswers());
            for (int i = 0; i < optionNames.size() && i < normalized.size(); i++) {
                int rank = normalized.get(i);
                if (rank != Const.POINTS_NOT_SUBMITTED) {
                    ranksPerOption.get(optionNames.get(i)).add(rank);
                }
            }
        }

        Map<String, Integer> overallRanks = computeOverallRanks(ranksPerOption);

        List<RankOptionsOptionRow> optionRows = new ArrayList<>();
        for (String option : optionNames) {
            List<Integer> ranks = ranksPerOption.get(option);
            ranks.sort(Integer::compareTo);

            RankOptionsOptionRow row = new RankOptionsOptionRow();
            row.setOption(option);
            row.setRanksReceived(new ArrayList<>(ranks));
            row.setOverallRank(overallRanks.get(option));
            optionRows.add(row);
        }

        FeedbackRankOptionsStatistics statistics = new FeedbackRankOptionsStatistics();
        statistics.setOptions(optionRows);
        return statistics;
    }

    @Override
    public FeedbackRankOptionsStatistics calculateForRecipient(
            FeedbackQuestion question, List<FeedbackResponse> responses, SessionResultsBundle bundle, User recipient) {
        // Rank options questions do not have recipient-specific statistics
        return null;
    }

    /**
     * Normalizes ranks in a single response, mapping non-consecutive ranks to consecutive 1, 2, 3, etc.
     * NOT_SUBMITTED values pass through unchanged.
     */
    static List<Integer> normalizeRanks(List<Integer> ranks) {
        Map<Integer, Integer> mapping = new TreeMap<>();
        int normalized = 1;
        for (int rank : ranks.stream().filter(r -> r != Const.POINTS_NOT_SUBMITTED).sorted().distinct().toList()) {
            mapping.put(rank, normalized++);
        }
        List<Integer> result = new ArrayList<>();
        for (int rank : ranks) {
            result.add(rank == Const.POINTS_NOT_SUBMITTED ? Const.POINTS_NOT_SUBMITTED : mapping.get(rank));
        }
        return result;
    }

    /**
     * Assigns overall ranks to each key based on the average of ranks received.
     * Keys with no responses are omitted. Ties share the same rank.
     */
    static Map<String, Integer> computeOverallRanks(Map<String, List<Integer>> ranksPerKey) {
        Map<String, Double> averages = new LinkedHashMap<>();
        for (Map.Entry<String, List<Integer>> entry : ranksPerKey.entrySet()) {
            List<Integer> ranks = entry.getValue();
            if (ranks.isEmpty()) {
                continue;
            }
            double sum = ranks.stream().mapToInt(Integer::intValue).sum();
            averages.put(entry.getKey(), sum / ranks.size());
        }

        List<String> sortedKeys = averages.entrySet().stream()
                .sorted(Comparator.comparingDouble(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .toList();

        Map<String, Integer> result = new LinkedHashMap<>();
        for (int i = 0; i < sortedKeys.size(); i++) {
            String key = sortedKeys.get(i);
            if (i == 0) {
                result.put(key, 1);
            } else {
                String prev = sortedKeys.get(i - 1);
                if (averages.get(key).equals(averages.get(prev))) {
                    result.put(key, result.get(prev));
                } else {
                    result.put(key, i + 1);
                }
            }
        }
        return result;
    }
}
