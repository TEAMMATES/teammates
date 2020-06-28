package teammates.common.datatransfer.questions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.SanitizationHelper;

public class FeedbackMcqQuestionDetails extends FeedbackQuestionDetails {

    private boolean hasAssignedWeights;
    private List<Double> mcqWeights;
    private double mcqOtherWeight;
    private int numOfMcqChoices;
    private List<String> mcqChoices;
    private boolean otherEnabled;
    private FeedbackParticipantType generateOptionsFor;

    public FeedbackMcqQuestionDetails() {
        super(FeedbackQuestionType.MCQ);

        this.hasAssignedWeights = false;
        this.mcqWeights = new ArrayList<>();
        this.numOfMcqChoices = 0;
        this.mcqChoices = new ArrayList<>();
        this.otherEnabled = false;
        this.mcqOtherWeight = 0;
        this.generateOptionsFor = FeedbackParticipantType.NONE;
    }

    @Override
    public List<String> getInstructions() {
        return null;
    }

    public int getNumOfMcqChoices() {
        return numOfMcqChoices;
    }

    public void setNumOfMcqChoices(int numOfMcqChoices) {
        this.numOfMcqChoices = numOfMcqChoices;
    }

    public List<String> getMcqChoices() {
        return mcqChoices;
    }

    public boolean hasAssignedWeights() {
        return hasAssignedWeights;
    }

    public void setHasAssignedWeights(boolean hasAssignedWeights) {
        this.hasAssignedWeights = hasAssignedWeights;
    }

    public List<Double> getMcqWeights() {
        return new ArrayList<>(mcqWeights);
    }

    public void setMcqWeights(List<Double> mcqWeights) {
        this.mcqWeights = mcqWeights;
    }

    public double getMcqOtherWeight() {
        return mcqOtherWeight;
    }

    public void setMcqOtherWeight(double mcqOtherWeight) {
        this.mcqOtherWeight = mcqOtherWeight;
    }

    public FeedbackParticipantType getGenerateOptionsFor() {
        return generateOptionsFor;
    }

    public void setGenerateOptionsFor(FeedbackParticipantType generateOptionsFor) {
        this.generateOptionsFor = generateOptionsFor;
    }

    public void setMcqChoices(List<String> mcqChoices) {
        this.mcqChoices = mcqChoices;
    }

    @Override
    public String getQuestionTypeDisplayName() {
        return Const.FeedbackQuestionTypeNames.MCQ;
    }

    public boolean getOtherEnabled() {
        return otherEnabled;
    }

    public void setOtherEnabled(boolean otherEnabled) {
        this.otherEnabled = otherEnabled;
    }

    @Override
    public boolean shouldChangesRequireResponseDeletion(FeedbackQuestionDetails newDetails) {
        FeedbackMcqQuestionDetails newMcqDetails = (FeedbackMcqQuestionDetails) newDetails;

        if (this.numOfMcqChoices != newMcqDetails.numOfMcqChoices
                || !this.mcqChoices.containsAll(newMcqDetails.mcqChoices)
                || !newMcqDetails.mcqChoices.containsAll(this.mcqChoices)) {
            return true;
        }

        if (this.generateOptionsFor != newMcqDetails.generateOptionsFor) {
            return true;
        }

        return this.otherEnabled != newMcqDetails.otherEnabled;
    }

    @Override
    public String getQuestionResultStatisticsCsv(
            List<FeedbackResponseAttributes> responses,
            FeedbackQuestionAttributes question,
            FeedbackSessionResultsBundle bundle) {
        if (responses.isEmpty()) {
            return "";
        }
        StringBuilder csv = new StringBuilder();
        MCQStatistics mcqStats = new MCQStatistics(this);
        Map<String, Integer> answerFrequency = mcqStats.collateAnswerFrequency(responses);
        // Add the Response Summary Statistics to the CSV String.
        csv.append(mcqStats.getResponseSummaryStatsCsv(answerFrequency, responses.size()));

        // If weights are assigned, add the 'Per Recipient Statistics' to the CSV string.
        if (hasAssignedWeights) {
            csv.append(System.lineSeparator())
                    .append("Per Recipient Statistics").append(System.lineSeparator())
                    .append(mcqStats.getPerRecipientResponseStatsCsv(responses, bundle));
        }
        return csv.toString();
    }

    @Override
    public String getCsvHeader() {
        return "Feedback";
    }

    @Override
    public List<String> validateQuestionDetails() {
        List<String> errors = new ArrayList<>();
        if (generateOptionsFor == FeedbackParticipantType.NONE) {

            if (numOfMcqChoices < Const.FeedbackQuestion.MCQ_MIN_NUM_OF_CHOICES) {
                errors.add(Const.FeedbackQuestion.MCQ_ERROR_NOT_ENOUGH_CHOICES
                        + Const.FeedbackQuestion.MCQ_MIN_NUM_OF_CHOICES + ".");
            }

            // If there are Empty Mcq options entered trigger this error
            boolean isEmptyMcqOptionEntered = mcqChoices.stream().anyMatch(mcqText -> mcqText.trim().equals(""));
            if (isEmptyMcqOptionEntered) {
                errors.add(Const.FeedbackQuestion.MCQ_ERROR_EMPTY_MCQ_OPTION);
            }

            // If weights are enabled, number of choices and weights should be same.
            // If user enters an invalid weight for a valid choice,
            // the mcqChoices.size() will be greater than mcqWeights.size(),
            // in that case, trigger this error.
            if (hasAssignedWeights && mcqChoices.size() != mcqWeights.size()) {
                errors.add(Const.FeedbackQuestion.MCQ_ERROR_INVALID_WEIGHT);
            }

            // If weights are not enabled, but weight list is not empty or otherWeight is not 0
            // In that case, trigger this error.
            if (!hasAssignedWeights && (!mcqWeights.isEmpty() || mcqOtherWeight != 0)) {
                errors.add(Const.FeedbackQuestion.MCQ_ERROR_INVALID_WEIGHT);
            }

            // If weights are enabled, but other option is disabled, and mcqOtherWeight is not 0
            // In that case, trigger this error.
            if (hasAssignedWeights && !otherEnabled && mcqOtherWeight != 0) {
                errors.add(Const.FeedbackQuestion.MCQ_ERROR_INVALID_WEIGHT);
            }

            // If weights are enabled, and any of the weights have negative value,
            // trigger this error.
            if (hasAssignedWeights && !mcqWeights.isEmpty()) {
                mcqWeights.stream()
                        .filter(weight -> weight < 0)
                        .forEach(weight -> errors.add(Const.FeedbackQuestion.MCQ_ERROR_INVALID_WEIGHT));
            }

            // If 'Other' option is enabled, and other weight has negative value,
            // trigger this error.
            if (hasAssignedWeights && otherEnabled && mcqOtherWeight < 0) {
                errors.add(Const.FeedbackQuestion.MCQ_ERROR_INVALID_WEIGHT);
            }

            //If there are duplicate mcq options trigger this error
            boolean isDuplicateOptionsEntered = mcqChoices.stream().map(String::trim).distinct().count()
                                                != mcqChoices.size();
            if (isDuplicateOptionsEntered) {
                errors.add(Const.FeedbackQuestion.MCQ_ERROR_DUPLICATE_MCQ_OPTION);
            }
        }

        return errors;
    }

    @Override
    public boolean isFeedbackParticipantCommentsOnResponsesAllowed() {
        return true;
    }

    @Override
    public String validateGiverRecipientVisibility(FeedbackQuestionAttributes feedbackQuestionAttributes) {
        return "";
    }

    /**
     * Class that contains methods which are used to calculate both MCQ and MSQ response statistics.
     */
    public abstract static class MultipleOptionStatistics {
        protected boolean hasAssignedWeights;
        protected List<String> choices;
        protected List<Double> weights;
        protected double otherWeight;
        protected boolean otherEnabled;
        protected int numOfChoices;

        /**
         * Calculates the answer frequency for each option based on the received responses for a question.
         */
        protected abstract Map<String, Integer> collateAnswerFrequency(List<FeedbackResponseAttributes> responses);

        /**
         * Calculates the weighted percentage for each option.
         * weighted percentage = (response count per option) * (weight of that option) / totalWeightedResponseCount<br>
         * where as, totalWeightedResponseCount is defined as:<br>
         * totalWeightedResponseCount += [response count of option i * weight of option i] for all options.
         * @param answerFrequency Response count of each option.
         */
        public Map<String, Double> calculateWeightedPercentagePerOption(Map<String, Integer> answerFrequency) {
            Map<String, Double> weightedPercentagePerOption = new LinkedHashMap<>();

            Assumption.assertTrue("Weights should be enabled when calling the function", hasAssignedWeights);
            double totalWeightedResponseCount = calculateTotalWeightedResponseCount(answerFrequency);

            for (int i = 0; i < choices.size(); i++) {
                String option = choices.get(i);
                double weight = weights.get(i);
                weightedPercentagePerOption.put(option, weight);
            }

            if (otherEnabled) {
                weightedPercentagePerOption.put("Other", otherWeight);
            }

            weightedPercentagePerOption.forEach((key, weight) -> {
                int frequency = answerFrequency.get(key);
                double weightedPercentage = totalWeightedResponseCount == 0 ? 0
                        : 100 * ((frequency * weight) / totalWeightedResponseCount);

                // Replace the value by the actual weighted percentage.
                weightedPercentagePerOption.put(key, weightedPercentage);
            });
            return weightedPercentagePerOption;
        }

        /**
         * Calculates the sum of the product of response count and weight of that option, for all options.
         * totalWeightedResponseCount += [(responseCount of option i) * (weight of option i)] for all options.
         */
        public double calculateTotalWeightedResponseCount(Map<String, Integer> answerFrequency) {
            return answerFrequency.entrySet().stream()
                    .map(entry -> {
                        String choice = entry.getKey();
                        double weight = "Other".equals(choice) ? otherWeight : weights.get(choices.indexOf(choice));
                        return entry.getValue() * weight;
                    })
                    .mapToDouble(Double::doubleValue)
                    .sum();
        }

        /**
         * Returns a list of {@link FeedbackResponseAttributes} sorted by comparing recipient Team name and
         * recipient name for each recipient email.
         * @param unsortedResponses The list of unsorted responses that needs to be sorted.
         * @param bundle Result bundle that is used to retrieve recipientTeamName and recipientName for each recipient.
         */
        public List<FeedbackResponseAttributes> getResponseAttributesSorted(
                List<FeedbackResponseAttributes> unsortedResponses, FeedbackSessionResultsBundle bundle) {
            List<FeedbackResponseAttributes> responses = new LinkedList<>(unsortedResponses);

            responses.sort(Comparator
                    .comparing((FeedbackResponseAttributes obj) -> bundle.getTeamNameForEmail(obj.recipient))
                    .thenComparing(obj -> bundle.getNameForEmail(obj.recipient)));

            return responses;
        }

        /**
         * Generates statistics for each recipient for 'Per recipient statistics' to be used for
         * both the results page and csv files.
         * The specific stats that are generated are -<br>
         * Team, Name, Response count for each option, Total, Average.
         * @param recipientEmail Email of the recipient whose statistics should be calculated
         * @param recipientResponses Map containing the response count of each choice for the recipient
         * @param bundle Feedback session results bundle to get the team name and name of the recipient
         * @return List of strings containing the 'Per recipient stats' of the recipient
         */
        public List<String> generateStatisticsForEachRecipient(String recipientEmail,
                Map<String, Integer> recipientResponses, FeedbackSessionResultsBundle bundle) {

            Assumption.assertTrue("Weights should be enabled when calling the function", hasAssignedWeights);
            List<String> recipientStats = new ArrayList<>();
            DecimalFormat df = new DecimalFormat("0.00");

            String recipientName = bundle.getNameForEmail(recipientEmail);
            String recipientTeam = bundle.getTeamNameForEmail(recipientEmail);
            double total = 0;
            double average = 0;
            int numOfResponsesForThisRecipient = 0;

            recipientStats.add(recipientTeam);
            recipientStats.add(recipientName);

            for (Map.Entry<String, Integer> countPerChoice : recipientResponses.entrySet()) {
                String choice = countPerChoice.getKey();
                int responseCount = countPerChoice.getValue();

                double weight = 0;
                recipientStats.add(Integer.toString(responseCount));

                // Get the weight of the choice.
                if ("Other".equals(choice)) {
                    weight = otherWeight;
                } else {
                    weight = weights.get(choices.indexOf(choice));
                }
                // Add the total weight of all responses of this choice to total.
                total += responseCount * weight;
                numOfResponsesForThisRecipient += responseCount;
            }

            recipientStats.add(df.format(total));
            average = numOfResponsesForThisRecipient == 0 ? 0 : total / numOfResponsesForThisRecipient;
            recipientStats.add(df.format(average));

            return recipientStats;
        }

        /**
         * Returns a String containing the Response Summary statistics for CSV files.
         */
        public String getResponseSummaryStatsCsv(Map<String, Integer> answerFrequency, int totalResponseCount) {

            String header = "";

            StringBuilder fragments = new StringBuilder();
            DecimalFormat df = new DecimalFormat("#.##");

            // If weights are assigned, CSV file should include 'Weight' and 'Weighted Percentage' column as well.
            if (hasAssignedWeights) {
                header = "Choice, Weight, Response Count, Percentage (%), Weighted Percentage (%)";
                Map<String, Double> weightedPercentagePerOption = calculateWeightedPercentagePerOption(answerFrequency);

                answerFrequency.forEach((key, responseCount) -> {
                    String weightString = "";
                    if ("Other".equals(key)) {
                        weightString = df.format(otherWeight);
                    } else {
                        weightString = df.format(weights.get(choices.indexOf(key)));
                    }

                    fragments.append(SanitizationHelper.sanitizeForCsv(key)).append(',')
                             .append(SanitizationHelper.sanitizeForCsv(weightString)).append(',')
                             .append(Integer.toString(responseCount)).append(',')
                             .append(df.format(100 * (double) responseCount / totalResponseCount)).append(',')
                             .append(df.format(weightedPercentagePerOption.get(key))).append(System.lineSeparator());
                });
            } else {
                header = "Choice, Response Count, Percentage (%)";

                answerFrequency.forEach((key, value) -> fragments.append(SanitizationHelper.sanitizeForCsv(key)).append(',')
                        .append(value.toString()).append(',')
                        .append(df.format(100 * (double) value / totalResponseCount)).append(System.lineSeparator()));
            }

            return header + System.lineSeparator() + fragments.toString();
        }

        public String getPerRecipientResponseStatsHeaderCsv() {
            StringBuilder header = new StringBuilder(100);
            DecimalFormat df = new DecimalFormat("#.##");

            header.append("Team, Recipient Name,");

            for (int i = 0; i < numOfChoices; i++) {
                String choiceString = choices.get(i) + " [" + df.format(weights.get(i)) + "]";
                header.append(SanitizationHelper.sanitizeForCsv(choiceString)).append(',');
            }
            if (otherEnabled) {
                String otherOptionString = "Other [" + df.format(otherWeight) + "]";
                header.append(SanitizationHelper.sanitizeForCsv(otherOptionString)).append(',');
            }
            header.append("Total, Average").append(System.lineSeparator());
            return header.toString();
        }

        /**
         * Returns the 'Per Recipient' stats body part for CSV files.<br>
         * @param responses The response attribute list should be sorted first before passing as an argument.
         * @param bundle Feedback session results bundle
         */
        protected String getPerRecipientResponseStatsBodyCsv(List<FeedbackResponseAttributes> responses,
                FeedbackSessionResultsBundle bundle) {
            StringBuilder bodyBuilder = new StringBuilder(100);
            Map<String, Map<String, Integer>> perRecipientResponses = calculatePerRecipientResponseCount(responses);

            for (Map.Entry<String, Map<String, Integer>> entry : perRecipientResponses.entrySet()) {
                String recipient = entry.getKey();
                Map<String, Integer> responsesForRecipient = entry.getValue();
                String perRecipientStats = getPerRecipientResponseStatsBodyFragmentCsv(
                        recipient, responsesForRecipient, bundle);
                bodyBuilder.append(perRecipientStats);
            }

            return bodyBuilder.toString();

        }

        /**
         * Returns a string containing a per recipient response stats for a single recipient.
         */
        private String getPerRecipientResponseStatsBodyFragmentCsv(String recipientEmail,
                Map<String, Integer> recipientResponses, FeedbackSessionResultsBundle bundle) {
            StringBuilder fragments = new StringBuilder(100);
            List<String> statsForEachRecipient = generateStatisticsForEachRecipient(
                    recipientEmail, recipientResponses, bundle);

            // Add each column data in fragments
            fragments.append(String.join(", ", statsForEachRecipient) + System.lineSeparator());
            return fragments.toString();
        }

        /**
         * Returns a Map containing response counts for each option for every recipient.
         */
        protected abstract Map<String, Map<String, Integer>> calculatePerRecipientResponseCount(
                List<FeedbackResponseAttributes> responses);

    }

    /**
     * Class to calculate result statistics of responses for MCQ questions.
     */
    private static class MCQStatistics extends MultipleOptionStatistics {

        MCQStatistics(FeedbackMcqQuestionDetails mcqDetails) {
            this.choices = mcqDetails.getMcqChoices();
            this.numOfChoices = choices.size();
            this.weights = mcqDetails.getMcqWeights();
            this.otherEnabled = mcqDetails.getOtherEnabled();
            this.hasAssignedWeights = mcqDetails.hasAssignedWeights();
            this.otherWeight = mcqDetails.getMcqOtherWeight();
        }

        /**
         * Calculates the answer frequency for each option based on the received responses.
         */
        @Override
        protected Map<String, Integer> collateAnswerFrequency(List<FeedbackResponseAttributes> responses) {
            Map<String, Integer> answerFrequency = new LinkedHashMap<>();

            for (String option : choices) {
                answerFrequency.put(option, 0);
            }

            if (otherEnabled) {
                answerFrequency.put("Other", 0);
            }

            for (FeedbackResponseAttributes response : responses) {
                FeedbackResponseDetails responseDetails = response.getResponseDetails();
                boolean isOtherOptionAnswer =
                        ((FeedbackMcqResponseDetails) responseDetails).isOtherOptionAnswer();
                String key = isOtherOptionAnswer ? "Other" : responseDetails.getAnswerString();

                answerFrequency.put(key, answerFrequency.getOrDefault(key, 0) + 1);
            }

            return answerFrequency;
        }

        /**
         * Returns a Map containing response counts for each option for every recipient.
         */
        @Override
        protected Map<String, Map<String, Integer>> calculatePerRecipientResponseCount(
                List<FeedbackResponseAttributes> responses) {
            Map<String, Map<String, Integer>> perRecipientResponse = new LinkedHashMap<>();

            responses.forEach(response -> {
                perRecipientResponse.computeIfAbsent(response.recipient, key -> {
                    // construct default value for responseCount
                    Map<String, Integer> responseCountPerOption = new LinkedHashMap<>();
                    for (String choice : choices) {
                        responseCountPerOption.put(choice, 0);
                    }
                    if (otherEnabled) {
                        responseCountPerOption.put("Other", 0);
                    }
                    return responseCountPerOption;
                });
                perRecipientResponse.computeIfPresent(response.recipient, (key, responseCountPerOption) -> {
                    // update responseCount here
                    FeedbackMcqResponseDetails frd = (FeedbackMcqResponseDetails) response.getResponseDetails();
                    boolean isOtherAnswer = frd.isOtherOptionAnswer();
                    String answer = isOtherAnswer ? "Other" : frd.getAnswerString();

                    responseCountPerOption.computeIfPresent(answer, (choice, count) -> count + 1);
                    return responseCountPerOption;
                });
            });
            return perRecipientResponse;
        }

        // Generate Recipient Response statistics for csv files.

        /**
         * Returns a String containing the 'Per Recipient Statistics' stats for CSV files for all recipients.
         */
        public String getPerRecipientResponseStatsCsv(List<FeedbackResponseAttributes> responses,
                FeedbackSessionResultsBundle bundle) {
            String header = getPerRecipientResponseStatsHeaderCsv();
            // Get the response attributes sorted based on Recipient Team name and recipient name.
            List<FeedbackResponseAttributes> sortedResponses = getResponseAttributesSorted(responses, bundle);
            String body = getPerRecipientResponseStatsBodyCsv(sortedResponses, bundle);

            return header + body;
        }

        // Generate Recipient Response statistics for result page.

    }

}
