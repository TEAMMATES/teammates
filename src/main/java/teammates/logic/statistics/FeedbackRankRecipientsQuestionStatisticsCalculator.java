package teammates.logic.statistics;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.SessionResultsBundle;
import teammates.common.datatransfer.questions.FeedbackRankRecipientsResponseDetails;
import teammates.common.datatransfer.statistics.FeedbackRankRecipientsStatistics;
import teammates.common.datatransfer.statistics.FeedbackRankRecipientsStatistics.RankRecipientsRow;
import teammates.common.util.Const;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.ResponseRecipient;
import teammates.storage.entity.Student;
import teammates.storage.entity.User;

/**
 * Calculates rank recipients question statistics for results pages.
 */
public class FeedbackRankRecipientsQuestionStatisticsCalculator implements
        FeedbackQuestionStatisticsCalculator<FeedbackRankRecipientsStatistics, FeedbackRankRecipientsStatistics> {

    @Override
    public FeedbackRankRecipientsStatistics calculateCourseWide(
            FeedbackQuestion question, List<FeedbackResponse> responses, SessionResultsBundle bundle) {
        Map<String, Accumulator> accumulators = new LinkedHashMap<>();

        for (FeedbackResponse response : responses) {
            FeedbackRankRecipientsResponseDetails rd =
                    (FeedbackRankRecipientsResponseDetails) response.getFeedbackResponseDetailsCopy();
            int answer = rd.getAnswer();
            if (answer == Const.POINTS_NOT_SUBMITTED) {
                continue;
            }

            String key = response.getRecipient().getKey();
            Accumulator acc = accumulators.computeIfAbsent(key, k -> new Accumulator(response.getRecipient()));
            acc.ranksReceived.add(answer);

            boolean isSelf = response.getGiver().getKey().equals(response.getRecipient().getKey());
            if (isSelf) {
                acc.selfRank = answer;
            } else {
                acc.ranksExcludingSelf.add(answer);
            }
        }

        // Compute overall ranks across all recipients
        Map<String, List<Integer>> ranksPerKey = new LinkedHashMap<>();
        for (Map.Entry<String, Accumulator> entry : accumulators.entrySet()) {
            ranksPerKey.put(entry.getKey(), entry.getValue().ranksReceived);
        }
        Map<String, Integer> overallRanks = FeedbackRankOptionsQuestionStatisticsCalculator.computeOverallRanks(ranksPerKey);

        Map<String, List<Integer>> ranksExcludingSelfPerKey = new LinkedHashMap<>();
        for (Map.Entry<String, Accumulator> entry : accumulators.entrySet()) {
            ranksExcludingSelfPerKey.put(entry.getKey(), entry.getValue().ranksExcludingSelf);
        }
        Map<String, Integer> ranksExcludingSelf =
                FeedbackRankOptionsQuestionStatisticsCalculator.computeOverallRanks(ranksExcludingSelfPerKey);

        // Compute team ranks for student recipients only, grouped by team
        Map<String, List<String>> keysByTeam = new LinkedHashMap<>();
        for (Map.Entry<String, Accumulator> entry : accumulators.entrySet()) {
            if (!(entry.getValue().recipient.getRecipientUser() instanceof Student)) {
                continue;
            }
            String team = entry.getValue().recipient.getTeamName();
            keysByTeam.computeIfAbsent(team, t -> new ArrayList<>()).add(entry.getKey());
        }

        Map<String, Integer> teamRanks = new LinkedHashMap<>();
        Map<String, Integer> teamRanksExcludingSelf = new LinkedHashMap<>();
        for (List<String> teamKeys : keysByTeam.values()) {
            Map<String, List<Integer>> teamRanksPerKey = new LinkedHashMap<>();
            Map<String, List<Integer>> teamRanksExcludingSelfPerKey = new LinkedHashMap<>();
            for (String k : teamKeys) {
                teamRanksPerKey.put(k, accumulators.get(k).ranksReceived);
                teamRanksExcludingSelfPerKey.put(k, accumulators.get(k).ranksExcludingSelf);
            }
            teamRanks.putAll(FeedbackRankOptionsQuestionStatisticsCalculator.computeOverallRanks(teamRanksPerKey));
            teamRanksExcludingSelf.putAll(
                    FeedbackRankOptionsQuestionStatisticsCalculator.computeOverallRanks(teamRanksExcludingSelfPerKey));
        }

        // Build rows sorted by team name then display name
        List<RankRecipientsRow> rows = accumulators.entrySet().stream()
                .sorted(Comparator
                        .comparing((Map.Entry<String, Accumulator> e) -> e.getValue().recipient.getTeamName())
                        .thenComparing(e -> e.getValue().recipient.getDisplayName()))
                .map(entry -> {
                    String key = entry.getKey();
                    Accumulator acc = entry.getValue();
                    List<Integer> sorted = new ArrayList<>(acc.ranksReceived);
                    sorted.sort(Integer::compareTo);

                    RankRecipientsRow row = new RankRecipientsRow();
                    row.setRecipientName(acc.recipient.getDisplayName());
                    row.setRecipientEmail(acc.recipient.isRecipientUser()
                            ? acc.recipient.getRecipientUser().getEmail() : null);
                    row.setRecipientTeam(acc.recipient.getTeamName());
                    row.setRanksReceived(sorted);
                    row.setSelfRank(acc.selfRank);
                    row.setOverallRank(overallRanks.get(key));
                    row.setRankExcludingSelf(ranksExcludingSelf.get(key));
                    row.setRankInTeam(teamRanks.get(key));
                    row.setRankInTeamExcludingSelf(teamRanksExcludingSelf.get(key));
                    return row;
                })
                .toList();

        FeedbackRankRecipientsStatistics statistics = new FeedbackRankRecipientsStatistics();
        statistics.setRows(rows);
        return statistics;
    }

    @Override
    public FeedbackRankRecipientsStatistics calculateForRecipient(
            FeedbackQuestion question, List<FeedbackResponse> responses, SessionResultsBundle bundle, User recipient) {
        // Rank recipients questions do not have recipient-specific statistics
        return null;
    }

    private static class Accumulator {
        final ResponseRecipient recipient;
        final List<Integer> ranksReceived = new ArrayList<>();
        final List<Integer> ranksExcludingSelf = new ArrayList<>();
        Integer selfRank;

        Accumulator(ResponseRecipient recipient) {
            this.recipient = recipient;
        }
    }
}
