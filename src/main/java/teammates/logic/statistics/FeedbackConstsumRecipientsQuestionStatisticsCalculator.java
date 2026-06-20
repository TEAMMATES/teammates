package teammates.logic.statistics;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Nullable;

import teammates.common.datatransfer.SessionResultsBundle;
import teammates.common.datatransfer.questions.FeedbackConstantSumRecipientsResponseDetails;
import teammates.common.datatransfer.statistics.FeedbackConstsumRecipientsStatistics;
import teammates.common.datatransfer.statistics.FeedbackConstsumRecipientsStatistics.ConstsumRecipientRow;
import teammates.common.datatransfer.statistics.FeedbackQuestionResultsStatisticsView;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.ResponseRecipient;
import teammates.storage.entity.Student;
import teammates.storage.entity.Team;
import teammates.storage.entity.User;

/**
 * Calculates constant sum recipients question statistics for results pages.
 */
public class FeedbackConstsumRecipientsQuestionStatisticsCalculator implements
        FeedbackQuestionStatisticsCalculator<FeedbackConstsumRecipientsStatistics, FeedbackConstsumRecipientsStatistics> {

    @Override
    public FeedbackConstsumRecipientsStatistics calculateCourseWide(
            FeedbackQuestion question, List<FeedbackResponse> responses, SessionResultsBundle bundle) {
        return calculate(responses, FeedbackQuestionResultsStatisticsView.COURSE_WIDE, null);
    }

    @Override
    public FeedbackConstsumRecipientsStatistics calculateForRecipient(
            FeedbackQuestion question, List<FeedbackResponse> responses, SessionResultsBundle bundle,
            User recipient) {
        Objects.requireNonNull(recipient, "Recipient user cannot be null for recipient-specific statistics calculation");
        ResponseRecipient recipientUser = new ResponseRecipient(recipient);
        Team team = recipient instanceof Student student ? student.getTeam() : null;
        ResponseRecipient recipientTeam = team != null ? new ResponseRecipient(team) : null;

        List<FeedbackResponse> recipientResponses = responses.stream()
                .filter(r -> r.getRecipient().equals(recipientUser)
                        || recipientTeam != null && recipientTeam.equals(r.getRecipient()))
                .toList();
        return calculate(recipientResponses, FeedbackQuestionResultsStatisticsView.RECIPIENT, recipientUser);
    }

    private FeedbackConstsumRecipientsStatistics calculate(
            List<FeedbackResponse> responses, FeedbackQuestionResultsStatisticsView view,
            @Nullable ResponseRecipient selfRecipient) {
        Map<String, Accumulator> accumulators = new LinkedHashMap<>();

        for (FeedbackResponse response : responses) {
            if (response.getRecipient().isNoSpecificRecipient()) {
                continue;
            }
            String key = response.getRecipient().getKey();
            accumulators.computeIfAbsent(key, k -> new Accumulator(response.getRecipient()));
            Accumulator acc = accumulators.get(key);

            FeedbackConstantSumRecipientsResponseDetails rd =
                    (FeedbackConstantSumRecipientsResponseDetails) response.getFeedbackResponseDetailsCopy();
            int answer = rd.getAnswers().get(0);
            boolean isSelf = response.getGiver().getKey().equals(response.getRecipient().getKey());

            acc.points.add(answer);
            acc.total += answer;
            if (!isSelf) {
                acc.pointsExcludingSelf.add(answer);
                acc.totalExcludingSelf += answer;
            }
        }

        String selfKey = selfRecipient != null ? selfRecipient.getKey() : null;
        List<ConstsumRecipientRow> rows = accumulators.values().stream()
                .sorted(Comparator.comparing((Accumulator a) -> a.recipient.getTeamName())
                        .thenComparing(a -> a.recipient.getDisplayName()))
                .map(acc -> buildRow(acc, selfKey != null && selfKey.equals(acc.recipient.getKey())))
                .toList();

        FeedbackConstsumRecipientsStatistics statistics = new FeedbackConstsumRecipientsStatistics(view);
        statistics.setRows(rows);
        return statistics;
    }

    private static ConstsumRecipientRow buildRow(Accumulator acc, boolean isCurrentRecipient) {
        List<Integer> sortedPoints = new ArrayList<>(acc.points);
        sortedPoints.sort(Integer::compareTo);

        double average = acc.points.isEmpty() ? 0.0
                : roundToTwoDecimals((double) acc.total / acc.points.size());

        Double averageExcludingSelf = null;
        if (!acc.pointsExcludingSelf.isEmpty()) {
            averageExcludingSelf = roundToTwoDecimals(
                    (double) acc.totalExcludingSelf / acc.pointsExcludingSelf.size());
        }

        ConstsumRecipientRow row = new ConstsumRecipientRow();
        row.setRecipientName(acc.recipient.getDisplayName());
        row.setRecipientEmail(acc.recipient.isRecipientUser()
                ? acc.recipient.getRecipientUser().getEmail() : null);
        row.setRecipientTeam(acc.recipient.getTeamName());
        row.setIsCurrentRecipient(isCurrentRecipient);
        row.setPointsReceived(sortedPoints);
        row.setTotal(acc.total);
        row.setAverage(average);
        row.setAverageExcludingSelf(averageExcludingSelf);
        return row;
    }

    private static double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private static class Accumulator {
        final ResponseRecipient recipient;
        final List<Integer> points = new ArrayList<>();
        int total;
        final List<Integer> pointsExcludingSelf = new ArrayList<>();
        int totalExcludingSelf;

        Accumulator(ResponseRecipient recipient) {
            this.recipient = recipient;
        }
    }
}
