package teammates.logic.statistics;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import teammates.common.datatransfer.SessionResultsBundle;
import teammates.common.datatransfer.questions.FeedbackNumericalScaleResponseDetails;
import teammates.common.datatransfer.statistics.FeedbackNumScaleStatistics;
import teammates.common.datatransfer.statistics.FeedbackNumScaleStatistics.NumScaleRecipientRow;
import teammates.common.datatransfer.statistics.FeedbackQuestionResultsStatisticsView;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.ResponseRecipient;
import teammates.storage.entity.Student;
import teammates.storage.entity.Team;
import teammates.storage.entity.User;

/**
 * Calculates numerical scale question statistics for results pages.
 */
public class FeedbackNumScaleQuestionStatisticsCalculator implements
        FeedbackQuestionStatisticsCalculator<FeedbackNumScaleStatistics, FeedbackNumScaleStatistics> {

    @Override
    public FeedbackNumScaleStatistics calculateCourseWide(
            FeedbackQuestion question, List<FeedbackResponse> responses, SessionResultsBundle bundle) {
        return calculate(responses, FeedbackQuestionResultsStatisticsView.COURSE_WIDE);
    }

    @Override
    public FeedbackNumScaleStatistics calculateForRecipient(
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
        return calculate(recipientResponses, FeedbackQuestionResultsStatisticsView.RECIPIENT);
    }

    private FeedbackNumScaleStatistics calculate(
            List<FeedbackResponse> responses, FeedbackQuestionResultsStatisticsView view) {
        Map<String, Accumulator> accumulators = new LinkedHashMap<>();

        for (FeedbackResponse response : responses) {
            if (response.getRecipient().isNoSpecificRecipient()) {
                continue;
            }
            String key = response.getRecipient().getKey();
            accumulators.computeIfAbsent(key, k -> new Accumulator(response.getRecipient()));
            Accumulator acc = accumulators.get(key);

            FeedbackNumericalScaleResponseDetails rd =
                    (FeedbackNumericalScaleResponseDetails) response.getFeedbackResponseDetailsCopy();
            double answer = rd.getAnswer();
            boolean isSelf = response.getGiver().getKey().equals(response.getRecipient().getKey());

            acc.sum += answer;
            acc.count++;
            if (acc.min == null || answer < acc.min) {
                acc.min = answer;
            }
            if (acc.max == null || answer > acc.max) {
                acc.max = answer;
            }
            if (!isSelf) {
                acc.sumExcludingSelf += answer;
                acc.countExcludingSelf++;
            }
        }

        List<NumScaleRecipientRow> rows = accumulators.values().stream()
                .sorted(Comparator.comparing((Accumulator a) -> a.recipient.getTeamName())
                        .thenComparing(a -> a.recipient.getDisplayName()))
                .map(FeedbackNumScaleQuestionStatisticsCalculator::buildRow)
                .collect(Collectors.toList());

        FeedbackNumScaleStatistics statistics = new FeedbackNumScaleStatistics(view);
        statistics.setRows(rows);
        return statistics;
    }

    private static NumScaleRecipientRow buildRow(Accumulator acc) {
        NumScaleRecipientRow row = new NumScaleRecipientRow();
        row.setRecipientName(acc.recipient.getDisplayName());
        row.setRecipientEmail(acc.recipient.isRecipientUser()
                ? acc.recipient.getRecipientUser().getEmail() : null);
        row.setRecipientTeam(acc.recipient.getTeamName());
        if (acc.count > 0) {
            row.setAverage(roundToTwoDecimals(acc.sum / acc.count));
            row.setMin(acc.min);
            row.setMax(acc.max);
        }
        if (acc.countExcludingSelf > 0) {
            row.setAverageExcludingSelf(roundToTwoDecimals(acc.sumExcludingSelf / acc.countExcludingSelf));
        }
        return row;
    }

    private static double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private static class Accumulator {
        final ResponseRecipient recipient;
        double sum;
        int count;
        Double min;
        Double max;
        double sumExcludingSelf;
        int countExcludingSelf;

        Accumulator(ResponseRecipient recipient) {
            this.recipient = recipient;
        }
    }
}
