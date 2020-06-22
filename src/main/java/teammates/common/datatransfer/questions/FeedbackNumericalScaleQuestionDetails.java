package teammates.common.datatransfer.questions;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.util.Const;
import teammates.common.util.SanitizationHelper;

public class FeedbackNumericalScaleQuestionDetails extends FeedbackQuestionDetails {
    private int minScale;
    private int maxScale;
    private double step;

    public FeedbackNumericalScaleQuestionDetails() {
        super(FeedbackQuestionType.NUMSCALE);
        this.minScale = 1;
        this.maxScale = 5;
        this.step = 0.5;
    }

    @Override
    public List<String> getInstructions() {
        return null;
    }

    @Override
    public String getQuestionTypeDisplayName() {
        return Const.FeedbackQuestionTypeNames.NUMSCALE;
    }

    private String getAverageExcludingSelfText(boolean showAvgExcludingSelf, DecimalFormat df, Double averageExcludingSelf) {
        if (showAvgExcludingSelf) {
            // Display a dash if the user has only self response
            return averageExcludingSelf == null ? "-" : df.format(averageExcludingSelf);
        }
        return "";
    }

    @Override
    public String getQuestionResultStatisticsCsv(
            List<FeedbackResponseAttributes> responses,
            FeedbackQuestionAttributes question,
            FeedbackSessionResultsBundle bundle) {
        if (responses.isEmpty()) {
            return "";
        }

        Map<String, Double> min = new HashMap<>();
        Map<String, Double> max = new HashMap<>();
        Map<String, Double> average = new HashMap<>();
        Map<String, Double> averageExcludingSelf = new HashMap<>();
        Map<String, Double> total = new HashMap<>();
        Map<String, Double> totalExcludingSelf = new HashMap<>();
        Map<String, Integer> numResponses = new HashMap<>();
        Map<String, Integer> numResponsesExcludingSelf = new HashMap<>();

        // need to know which recipients are hidden since anonymised recipients will not appear in the summary table
        List<String> hiddenRecipients = getHiddenRecipients(responses, question, bundle);

        populateSummaryStatisticsFromResponses(responses, min, max, average, averageExcludingSelf, total,
                                               totalExcludingSelf, numResponses, numResponsesExcludingSelf);

        boolean shouldShowAvgExcludingSelf = shouldShowAverageExcludingSelf(question, averageExcludingSelf);

        DecimalFormat df = getProperDecimalFormat();
        String csvHeader = "Team, Recipient, Average, Minimum, Maximum"
                         + (shouldShowAvgExcludingSelf ? ", Average excluding self response" : "")
                         + System.lineSeparator();

        StringBuilder csvBody = new StringBuilder();
        for (String recipient : numResponses.keySet()) {
            // hidden recipients do not appear in the summary table, so ignore responses with hidden recipients
            if (hiddenRecipients.contains(recipient)) {
                continue;
            }

            String recipientTeam = bundle.getTeamNameForEmail(recipient);
            boolean isRecipientGeneral = recipient.equals(Const.GENERAL_QUESTION);

            Double averageScoreExcludingSelf = averageExcludingSelf.get(recipient);
            String averageScoreExcludingSelfText =
                    getAverageExcludingSelfText(shouldShowAvgExcludingSelf, df, averageScoreExcludingSelf);

            csvBody.append(SanitizationHelper.sanitizeForCsv(recipientTeam) + ','
                           + SanitizationHelper.sanitizeForCsv(isRecipientGeneral
                                                      ? "General"
                                                      : bundle.getNameForEmail(recipient))
                           + ','
                           + df.format(average.get(recipient)) + ','
                           + df.format(min.get(recipient)) + ','
                           + df.format(max.get(recipient))
                           + (shouldShowAvgExcludingSelf ? ',' + averageScoreExcludingSelfText : "")
                           + System.lineSeparator());
        }

        return csvHeader + csvBody.toString();
    }

    private boolean shouldShowAverageExcludingSelf(
            FeedbackQuestionAttributes question, Map<String, Double> averageExcludingSelf) {

        if (question.recipientType == FeedbackParticipantType.NONE) {
            // General recipient type would not give self response
            // Therefore average exclude self response will always be hidden
            return false;
        }

        for (Double average : averageExcludingSelf.values()) {
            // There exists at least one average score exclude self
            if (average != null) {
                return true;
            }
        }
        return false;
    }

    private void populateSummaryStatisticsFromResponses(
            List<FeedbackResponseAttributes> responses,
            Map<String, Double> min, Map<String, Double> max,
            Map<String, Double> average, Map<String, Double> averageExcludingSelf,
            Map<String, Double> total, Map<String, Double> totalExcludingSelf,
            Map<String, Integer> numResponses,
            Map<String, Integer> numResponsesExcludingSelf) {

        for (FeedbackResponseAttributes response : responses) {
            FeedbackNumericalScaleResponseDetails responseDetails =
                    (FeedbackNumericalScaleResponseDetails) response.getResponseDetails();
            double answer = responseDetails.getAnswer();
            String giverEmail = response.giver;
            String recipientEmail = response.recipient;

            // Compute number of responses including user's self response
            int numOfResponses = numResponses.getOrDefault(recipientEmail, 0) + 1;
            numResponses.put(recipientEmail, numOfResponses);

            // Compute number of responses excluding user's self response
            boolean isSelfResponse = giverEmail.equalsIgnoreCase(recipientEmail);
            if (!isSelfResponse) {
                int numOfResponsesExcludingSelf = numResponsesExcludingSelf.getOrDefault(recipientEmail, 0) + 1;
                numResponsesExcludingSelf.put(recipientEmail, numOfResponsesExcludingSelf);
            }

            // Compute minimum score received
            double minScoreReceived = Math.min(answer, min.getOrDefault(recipientEmail, answer));
            min.put(recipientEmail, minScoreReceived);

            // Compute maximum score received
            double maxScoreReceived = Math.max(answer, max.getOrDefault(recipientEmail, answer));
            max.put(recipientEmail, maxScoreReceived);

            // Compute total score received
            double totalScore = total.getOrDefault(recipientEmail, 0.0) + answer;
            total.put(recipientEmail, totalScore);

            // Compute total score received excluding self
            if (!isSelfResponse) {
                Double totalScoreExcludingSelf = totalExcludingSelf.getOrDefault(recipientEmail, null);

                // totalScoreExcludingSelf == null when the user has only self response
                totalExcludingSelf.put(recipientEmail,
                                       totalScoreExcludingSelf == null ? answer : totalScoreExcludingSelf + answer);
            }

            // Compute average score received
            double averageReceived = total.get(recipientEmail) / numResponses.get(recipientEmail);
            average.put(recipientEmail, averageReceived);

            // Compute average score received excluding self
            averageExcludingSelf.putIfAbsent(recipientEmail, null);
            if (!isSelfResponse && totalExcludingSelf.get(recipientEmail) != null) {
                double averageReceivedExcludingSelf =
                        totalExcludingSelf.get(recipientEmail) / numResponsesExcludingSelf.get(recipientEmail);
                averageExcludingSelf.put(recipientEmail, averageReceivedExcludingSelf);
            }
        }
    }

    private List<String> getHiddenRecipients(
            List<FeedbackResponseAttributes> responses,
            FeedbackQuestionAttributes question,
            FeedbackSessionResultsBundle bundle) {
        List<String> hiddenRecipients = new ArrayList<>(); // List of recipients to hide
        FeedbackParticipantType type = question.recipientType;
        for (FeedbackResponseAttributes response : responses) {
            if (!bundle.visibilityTable.get(response.getId())[1]
                    && type != FeedbackParticipantType.SELF
                    && type != FeedbackParticipantType.NONE) {

                hiddenRecipients.add(response.recipient);
            }
        }
        return hiddenRecipients;
    }

    private DecimalFormat getProperDecimalFormat() {
        DecimalFormat df = new DecimalFormat();
        df.setMinimumFractionDigits(0);
        df.setMaximumFractionDigits(5);
        df.setRoundingMode(RoundingMode.DOWN);
        return df;
    }

    @Override
    public boolean shouldChangesRequireResponseDeletion(
            FeedbackQuestionDetails newDetails) {
        FeedbackNumericalScaleQuestionDetails newNumScaleDetails =
                (FeedbackNumericalScaleQuestionDetails) newDetails;

        return this.minScale != newNumScaleDetails.minScale
               || this.maxScale != newNumScaleDetails.maxScale
               || this.step != newNumScaleDetails.step;
    }

    @Override
    public String getCsvHeader() {
        return "Feedback";
    }

    @Override
    public List<String> validateQuestionDetails() {
        List<String> errors = new ArrayList<>();
        if (minScale >= maxScale) {
            errors.add(Const.FeedbackQuestion.NUMSCALE_ERROR_MIN_MAX);
        }
        if (step <= 0) {
            errors.add(Const.FeedbackQuestion.NUMSCALE_ERROR_STEP);
        }
        return errors;
    }

    @Override
    public boolean isFeedbackParticipantCommentsOnResponsesAllowed() {
        return false;
    }

    @Override
    public String validateGiverRecipientVisibility(FeedbackQuestionAttributes feedbackQuestionAttributes) {
        return "";
    }

    public int getMinScale() {
        return minScale;
    }

    public void setMinScale(int minScale) {
        this.minScale = minScale;
    }

    public int getMaxScale() {
        return maxScale;
    }

    public void setMaxScale(int maxScale) {
        this.maxScale = maxScale;
    }

    public double getStep() {
        return step;
    }

}
