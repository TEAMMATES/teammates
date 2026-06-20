package teammates.logic.statistics;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import teammates.common.datatransfer.SessionResultsBundle;
import teammates.storage.entity.ResponseRecipient;
import teammates.common.datatransfer.questions.FeedbackConstantSumOptionsQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackConstantSumOptionsResponseDetails;
import teammates.common.datatransfer.statistics.FeedbackConstsumOptionsStatistics;
import teammates.common.datatransfer.statistics.FeedbackConstsumOptionsStatistics.ConstsumOptionRow;
import teammates.common.datatransfer.statistics.FeedbackQuestionResultsStatisticsView;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.Student;
import teammates.storage.entity.Team;
import teammates.storage.entity.User;

/**
 * Calculates constant sum options question statistics for results pages.
 */
public class FeedbackConstsumOptionsQuestionStatisticsCalculator implements
        FeedbackQuestionStatisticsCalculator<FeedbackConstsumOptionsStatistics, FeedbackConstsumOptionsStatistics> {

    @Override
    public FeedbackConstsumOptionsStatistics calculateCourseWide(
            FeedbackQuestion question, List<FeedbackResponse> responses, SessionResultsBundle bundle) {
        return calculate(question, responses, FeedbackQuestionResultsStatisticsView.COURSE_WIDE);
    }

    @Override
    public FeedbackConstsumOptionsStatistics calculateForRecipient(
            FeedbackQuestion question, List<FeedbackResponse> responses, SessionResultsBundle bundle, User recipient) {
        Objects.requireNonNull(recipient, "Recipient user cannot be null for recipient-specific statistics calculation");
        ResponseRecipient recipientUser = new ResponseRecipient(recipient);
        Team team = recipient instanceof Student student ? student.getTeam() : null;
        ResponseRecipient recipientTeam = team != null ? new ResponseRecipient(team) : null;

        List<FeedbackResponse> recipientResponses = responses.stream()
                .filter(r -> r.getRecipient().equals(recipientUser)
                        || recipientTeam != null && recipientTeam.equals(r.getRecipient()))
                .toList();
        return calculate(question, recipientResponses, FeedbackQuestionResultsStatisticsView.RECIPIENT);
    }

    private FeedbackConstsumOptionsStatistics calculate(
            FeedbackQuestion question, List<FeedbackResponse> responses, FeedbackQuestionResultsStatisticsView view) {
        FeedbackConstantSumOptionsQuestionDetails details =
                (FeedbackConstantSumOptionsQuestionDetails) question.getQuestionDetailsCopy();
        List<String> optionNames = details.getConstSumOptions();

        Map<String, List<Integer>> pointsPerOption = new LinkedHashMap<>();
        for (String option : optionNames) {
            pointsPerOption.put(option, new ArrayList<>());
        }

        for (FeedbackResponse response : responses) {
            FeedbackConstantSumOptionsResponseDetails rd =
                    (FeedbackConstantSumOptionsResponseDetails) response.getFeedbackResponseDetailsCopy();
            List<Integer> answers = rd.getAnswers();
            for (int i = 0; i < optionNames.size() && i < answers.size(); i++) {
                pointsPerOption.get(optionNames.get(i)).add(answers.get(i));
            }
        }

        List<ConstsumOptionRow> optionRows = new ArrayList<>();
        for (String option : optionNames) {
            List<Integer> points = pointsPerOption.get(option);
            points.sort(Integer::compareTo);

            int total = points.stream().mapToInt(Integer::intValue).sum();
            double average = points.isEmpty() ? 0.0 : roundToTwoDecimals((double) total / points.size());

            ConstsumOptionRow row = new ConstsumOptionRow();
            row.setOption(option);
            row.setPointsReceived(new ArrayList<>(points));
            row.setTotal(total);
            row.setAverage(average);
            optionRows.add(row);
        }

        FeedbackConstsumOptionsStatistics statistics = new FeedbackConstsumOptionsStatistics(view);
        statistics.setOptions(optionRows);
        return statistics;
    }

    private static double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
