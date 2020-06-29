package teammates.common.datatransfer.questions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StringHelper;

public class FeedbackRubricQuestionDetails extends FeedbackQuestionDetails {

    private static final String STATISTICS_NO_VALUE_STRING = "-";

    private boolean hasAssignedWeights;
    private List<List<Double>> rubricWeightsForEachCell;
    private int numOfRubricChoices;
    private List<String> rubricChoices;
    private int numOfRubricSubQuestions;
    private List<String> rubricSubQuestions;
    private List<List<String>> rubricDescriptions;

    public FeedbackRubricQuestionDetails() {
        super(FeedbackQuestionType.RUBRIC);

        this.hasAssignedWeights = false;
        this.numOfRubricChoices = 0;
        this.rubricChoices = new ArrayList<>();
        this.numOfRubricSubQuestions = 0;
        this.rubricSubQuestions = new ArrayList<>();
        this.initializeRubricDescriptions();
        this.rubricWeightsForEachCell = new ArrayList<>();
    }

    public FeedbackRubricQuestionDetails(String questionText) {
        super(FeedbackQuestionType.RUBRIC, questionText);

        this.hasAssignedWeights = false;
        this.numOfRubricChoices = 0;
        this.rubricChoices = new ArrayList<>();
        this.numOfRubricSubQuestions = 0;
        this.rubricSubQuestions = new ArrayList<>();
        this.initializeRubricDescriptions();
        this.rubricWeightsForEachCell = new ArrayList<>();
    }

    @Override
    public List<String> getInstructions() {
        return null;
    }

    public List<String> getRubricChoices() {
        return rubricChoices;
    }

    /**
     * Checks if the dimensions of rubricDescription is valid according
     * to numOfRubricSubQuestions and numOfRubricChoices.
     */
    private boolean isValidDescriptionSize() {
        if (rubricDescriptions.size() != numOfRubricSubQuestions) {
            return false;
        }
        for (List<String> rubricDescription : rubricDescriptions) {
            if (rubricDescription.size() != numOfRubricChoices) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the dimensions of rubricWeightsForEachCell is valid according
     * to numOfRubricSubQuestions and numOfRubricChoices.
     */
    private boolean isValidWeightSize() {
        if (rubricWeightsForEachCell.size() != numOfRubricSubQuestions) {
            return false;
        }
        return rubricWeightsForEachCell.stream().allMatch(x -> x.size() == numOfRubricChoices);
    }

    @Override
    public String getQuestionTypeDisplayName() {
        return Const.FeedbackQuestionTypeNames.RUBRIC;
    }

    @Override
    public boolean shouldChangesRequireResponseDeletion(FeedbackQuestionDetails newDetails) {
        FeedbackRubricQuestionDetails newRubricDetails = (FeedbackRubricQuestionDetails) newDetails;
        // TODO: need to check for exact match.

        // Responses require deletion if choices change
        if (!this.rubricChoices.equals(newRubricDetails.rubricChoices)) {
            return true;
        }

        // Responses require deletion if sub-questions change
        return this.numOfRubricSubQuestions != newRubricDetails.numOfRubricSubQuestions
            || !this.rubricSubQuestions.containsAll(newRubricDetails.rubricSubQuestions)
            || !newRubricDetails.rubricSubQuestions.containsAll(this.rubricSubQuestions);
    }

    private void initializeRubricDescriptions() {
        rubricDescriptions = new ArrayList<>();
        for (int subQns = 0; subQns < numOfRubricSubQuestions; subQns++) {
            List<String> descList = new ArrayList<>();
            for (int ch = 0; ch < numOfRubricChoices; ch++) {
                descList.add("");
            }
            rubricDescriptions.add(descList);
        }
    }

    /**
     * Returns a string containing percentage Frequency, response frequency,
     * and if weights are assigned, then weights.
     */
    private String getPercentageFrequencyString(boolean isSubQuestionRespondedTo, float[][] rubricStats,
            int[][] responseFrequency, int subQnIndex, int choiceIndex) {
        DecimalFormat df = new DecimalFormat("#");
        DecimalFormat dfWeight = new DecimalFormat("#.##");
        List<List<Double>> weights = getRubricWeights();

        return (isSubQuestionRespondedTo
                ? df.format(rubricStats[subQnIndex][choiceIndex] * 100) + "%" : STATISTICS_NO_VALUE_STRING)
                + " (" + responseFrequency[subQnIndex][choiceIndex] + ")"
                + (hasAssignedWeights ? " [" + dfWeight.format(weights.get(subQnIndex).get(choiceIndex)) + "]" : "");
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

        // table header
        for (String header : rubricChoices) {
            csv.append(',').append(SanitizationHelper.sanitizeForCsv(header));
        }

        if (hasAssignedWeights) {
            csv.append(",Average");
        }

        csv.append(System.lineSeparator());

        // table body
        DecimalFormat dfAverage = new DecimalFormat("0.00");

        int[][] responseFrequency = calculateResponseFrequency(responses, this);
        float[][] rubricStats = calculatePercentageFrequencyAndAverage(this,
                responseFrequency);

        for (int i = 0; i < rubricSubQuestions.size(); i++) {
            String alphabeticalIndex = StringHelper.integerToLowerCaseAlphabeticalIndex(i + 1);
            csv.append(SanitizationHelper.sanitizeForCsv(alphabeticalIndex + ") " + rubricSubQuestions.get(i)));
            boolean isSubQuestionRespondedTo = responseFrequency[i][numOfRubricChoices] > 0;
            for (int j = 0; j < rubricChoices.size(); j++) {
                String percentageFrequencyString =
                        getPercentageFrequencyString(isSubQuestionRespondedTo, rubricStats, responseFrequency, i, j);

                csv.append(',').append(percentageFrequencyString);
            }

            if (hasAssignedWeights) {
                String averageString = isSubQuestionRespondedTo
                                     ? dfAverage.format(rubricStats[i][numOfRubricChoices])
                                     : STATISTICS_NO_VALUE_STRING;
                csv.append(',').append(averageString);
            }

            csv.append(System.lineSeparator());
        }

        if (hasAssignedWeights) {
            csv.append(System.lineSeparator())
                    .append("Per Recipient Statistics").append(System.lineSeparator())
                    .append(getPerRecipientStatisticsHeader())
                    .append(getPerRecipientStatisticsCsv(responses, bundle));
        }

        return csv.toString();
    }

    private List<Map.Entry<String, RubricRecipientStatistics>> getPerRecipientStatisticsSorted(
            List<FeedbackResponseAttributes> responses,
            FeedbackSessionResultsBundle bundle) {
        Map<String, RubricRecipientStatistics> recipientToRecipientStats = new HashMap<>();

        for (FeedbackResponseAttributes response : responses) {
            recipientToRecipientStats.computeIfAbsent(response.recipient, recipient -> {
                String recipientTeam = bundle.getTeamNameForEmail(recipient);
                String recipientName = bundle.getNameForEmail(recipient);
                return new RubricRecipientStatistics(recipient, recipientName, recipientTeam);
            })
                    .addResponseToRecipientStats(response);
        }

        List<Map.Entry<String, RubricRecipientStatistics>> recipientStatsList =
                new LinkedList<>(recipientToRecipientStats.entrySet());
        recipientStatsList.sort(Comparator.comparing((Map.Entry<String, RubricRecipientStatistics> obj) ->
                obj.getValue().recipientTeam.toLowerCase())
                .thenComparing(obj -> obj.getValue().recipientName));
        return recipientStatsList;
    }

    private String getPerRecipientStatisticsCsv(List<FeedbackResponseAttributes> responses,
            FeedbackSessionResultsBundle bundle) {
        StringBuilder csv = new StringBuilder(100);
        List<Map.Entry<String, RubricRecipientStatistics>> recipientStatsList =
                getPerRecipientStatisticsSorted(responses, bundle);

        for (Map.Entry<String, RubricRecipientStatistics> entry : recipientStatsList) {
            csv.append(entry.getValue().getCsvForAllSubQuestions());
        }

        return csv.toString();
    }

    @Override
    public String getNoResponseTextInCsv(String giverEmail, String recipientEmail,
            FeedbackSessionResultsBundle bundle,
            FeedbackQuestionAttributes question) {
        return SanitizationHelper.sanitizeForCsv("All Sub-Questions") + ","
             + SanitizationHelper.sanitizeForCsv(getNoResponseText(giverEmail, recipientEmail, bundle, question));
    }

    @Override
    public String getCsvHeader() {
        return "Choice Value";
    }

    private String getPerRecipientStatisticsHeader() {
        StringBuilder header = new StringBuilder(100);
        String headerFragment = "Team,Recipient Name,Recipient's Email,Sub Question,";

        header.append(headerFragment);

        for (int i = 0; i < numOfRubricChoices; i++) {
            header.append(SanitizationHelper.sanitizeForCsv(rubricChoices.get(i))).append(',');
        }

        header.append("Total,Average").append(System.lineSeparator());

        return header.toString();
    }

    @Override
    public String getCsvDetailedResponsesHeader(int noOfComments) {
        return "Team" + "," + "Giver's Full Name" + ","
                + "Giver's Last Name" + "," + "Giver's Email" + ","
                + "Recipient's Team" + "," + "Recipient's Full Name" + ","
                + "Recipient's Last Name" + "," + "Recipient's Email" + ","
                + "Sub Question" + "," + getCsvHeader() + "," + "Choice Number"
                + getCsvDetailedInstructorsCommentsHeader(noOfComments)
                + System.lineSeparator();
    }

    @Override
    public String getCsvDetailedResponsesRow(FeedbackSessionResultsBundle fsrBundle,
            FeedbackResponseAttributes feedbackResponseAttributes,
            FeedbackQuestionAttributes question) {

        // Retrieve giver details
        String giverLastName = fsrBundle.getLastNameForEmail(feedbackResponseAttributes.giver);
        String giverFullName = fsrBundle.getNameForEmail(feedbackResponseAttributes.giver);
        String giverTeamName = fsrBundle.getTeamNameForEmail(feedbackResponseAttributes.giver);
        String giverEmail = fsrBundle.getDisplayableEmailGiver(feedbackResponseAttributes);

        // Retrieve recipient details
        String recipientLastName = fsrBundle.getLastNameForEmail(feedbackResponseAttributes.recipient);
        String recipientFullName = fsrBundle.getNameForEmail(feedbackResponseAttributes.recipient);
        String recipientTeamName = fsrBundle.getTeamNameForEmail(feedbackResponseAttributes.recipient);
        String recipientEmail = fsrBundle.getDisplayableEmailRecipient(feedbackResponseAttributes);
        FeedbackRubricResponseDetails frd = (FeedbackRubricResponseDetails) feedbackResponseAttributes.getResponseDetails();
        StringBuilder detailedResponsesRow = new StringBuilder(100);
        for (int i = 0; i < frd.answer.size(); i++) {
            int chosenIndex = frd.answer.get(i);
            String chosenChoiceNumber = "";
            String chosenChoiceValue = "";
            String chosenIndexString = StringHelper.integerToLowerCaseAlphabeticalIndex(i + 1);

            if (chosenIndex == -1) {
                chosenChoiceValue = Const.INSTRUCTOR_FEEDBACK_RESULTS_MISSING_RESPONSE;
            } else {
                chosenChoiceNumber = Integer.toString(chosenIndex + 1);
                chosenChoiceValue = rubricChoices.get(frd.answer.get(i));
            }

            detailedResponsesRow.append(
                    SanitizationHelper.sanitizeForCsv(StringHelper.removeExtraSpace(giverTeamName)) + ','
                    + SanitizationHelper.sanitizeForCsv(StringHelper.removeExtraSpace(giverFullName)) + ','
                    + SanitizationHelper.sanitizeForCsv(StringHelper.removeExtraSpace(giverLastName)) + ','
                    + SanitizationHelper.sanitizeForCsv(StringHelper.removeExtraSpace(giverEmail)) + ','
                    + SanitizationHelper.sanitizeForCsv(StringHelper.removeExtraSpace(recipientTeamName)) + ','
                    + SanitizationHelper.sanitizeForCsv(StringHelper.removeExtraSpace(recipientFullName)) + ','
                    + SanitizationHelper.sanitizeForCsv(StringHelper.removeExtraSpace(recipientLastName)) + ','
                    + SanitizationHelper.sanitizeForCsv(StringHelper.removeExtraSpace(recipientEmail)) + ','
                    + SanitizationHelper.sanitizeForCsv(chosenIndexString) + ','
                    + SanitizationHelper.sanitizeForCsv(chosenChoiceValue) + ','
                    + SanitizationHelper.sanitizeForCsv(chosenChoiceNumber));

            // To show feedback participant comment only once for each response.
            if (isFeedbackParticipantCommentsOnResponsesAllowed() && i == 0) {
                String feedbackParticipantComment =
                        fsrBundle.getCsvDetailedFeedbackParticipantCommentOnResponse(feedbackResponseAttributes);
                detailedResponsesRow.append(',').append(feedbackParticipantComment);
            }
            // To show instructor comment only once for each response.
            if (i == 0) {
                String instructorComment =
                        fsrBundle.getCsvDetailedInstructorFeedbackResponseComments(feedbackResponseAttributes);
                detailedResponsesRow.append(instructorComment);
            }
            detailedResponsesRow.append(System.lineSeparator());
        }

        return detailedResponsesRow.toString();
    }

    @Override
    public List<String> validateQuestionDetails() {
        // For rubric questions,
        // 1) Description size should be valid
        // 2) At least 2 choices
        // 3) At least 1 sub-question
        // 4) Choices and sub-questions should not be empty
        // 5) Weights must be assigned to all cells if weights are assigned, which means
        //    weight size should be equal to (numOfRubricChoices * numOfRubricSubQuestions).

        List<String> errors = new ArrayList<>();

        if (!isValidDescriptionSize()) {
            // This should not happen.
            // Set descriptions to empty if the sizes are invalid when extracting question details.
            errors.add(Const.FeedbackQuestion.RUBRIC_ERROR_DESC_INVALID_SIZE);
        }

        if (numOfRubricChoices < Const.FeedbackQuestion.RUBRIC_MIN_NUM_OF_CHOICES) {
            errors.add(Const.FeedbackQuestion.RUBRIC_ERROR_NOT_ENOUGH_CHOICES
                       + Const.FeedbackQuestion.RUBRIC_MIN_NUM_OF_CHOICES);
        }

        if (this.numOfRubricSubQuestions < Const.FeedbackQuestion.RUBRIC_MIN_NUM_OF_SUB_QUESTIONS) {
            errors.add(Const.FeedbackQuestion.RUBRIC_ERROR_NOT_ENOUGH_SUB_QUESTIONS
                       + Const.FeedbackQuestion.RUBRIC_MIN_NUM_OF_SUB_QUESTIONS);
        }

        //Rubric choices are now allowed to be empty.
        /*
        for (String choice : this.rubricChoices) {
            if (choice.trim().isEmpty()) {
                errors.add(ERROR_RUBRIC_EMPTY_CHOICE);
                break;
            }
        }
        */

        for (String subQn : rubricSubQuestions) {
            if (subQn.trim().isEmpty()) {
                errors.add(Const.FeedbackQuestion.RUBRIC_ERROR_EMPTY_SUB_QUESTION);
                break;
            }
        }

        if (hasAssignedWeights && !isValidWeightSize()) {
            errors.add(Const.FeedbackQuestion.RUBRIC_ERROR_INVALID_WEIGHT);
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

    public boolean hasAssignedWeights() {
        return hasAssignedWeights;
    }

    /**
     * Returns a list of rubric weights if the weights are assigned,
     * otherwise returns an empty list.
     */
    public List<List<Double>> getRubricWeights() {
        if (hasAssignedWeights) {
            return rubricWeightsForEachCell;
        }

        return new ArrayList<>();
    }

    public int getNumOfRubricChoices() {
        return numOfRubricChoices;
    }

    public void setNumOfRubricChoices(int numOfRubricChoices) {
        this.numOfRubricChoices = numOfRubricChoices;
    }

    public int getNumOfRubricSubQuestions() {
        return numOfRubricSubQuestions;
    }

    public void setNumOfRubricSubQuestions(int numOfRubricSubQuestions) {
        this.numOfRubricSubQuestions = numOfRubricSubQuestions;
    }

    public void setHasAssignedWeights(boolean hasAssignedWeights) {
        this.hasAssignedWeights = hasAssignedWeights;
    }

    public void setRubricWeightsForEachCell(List<List<Double>> rubricWeightsForEachCell) {
        this.rubricWeightsForEachCell = rubricWeightsForEachCell;
    }

    public void setRubricChoices(List<String> rubricChoices) {
        this.rubricChoices = rubricChoices;
    }

    public void setRubricSubQuestions(List<String> rubricSubQuestions) {
        this.rubricSubQuestions = rubricSubQuestions;
    }

    public void setRubricDescriptions(List<List<String>> rubricDescriptions) {
        this.rubricDescriptions = rubricDescriptions;
    }

    public List<String> getRubricSubQuestions() {
        return rubricSubQuestions;
    }

    /**
     * Returns the frequency of being selected for each choice of each sub-question
     * and the total number of responses for each sub-question.
     *
     * <p>Last element in each row stores the total number of responses for the sub-question.
     *
     * <p>e.g.<br>
     * responseFrequency[subQuestionIndex][choiceIndex]
     * -> is the number of times choiceIndex is chosen for subQuestionIndex.<br>
     * responseFrequency[subQuestionIndex][numOfRubricChoices]
     * -> is the total number of the responses for the given sub-question.
     */
    private static int[][] calculateResponseFrequency(List<FeedbackResponseAttributes> responses,
            FeedbackRubricQuestionDetails questionDetails) {
        int numOfRubricSubQuestions = questionDetails.getNumOfRubricSubQuestions();
        int numOfRubricChoices = questionDetails.getNumOfRubricChoices();

        int[][] responseFrequency = new int[numOfRubricSubQuestions][numOfRubricChoices + 1];
        // count frequencies
        for (FeedbackResponseAttributes response : responses) {
            FeedbackRubricResponseDetails frd = (FeedbackRubricResponseDetails) response.getResponseDetails();
            for (int i = 0; i < numOfRubricSubQuestions; i++) {
                int chosenChoice = frd.getAnswer(i);
                if (chosenChoice != -1) {
                    responseFrequency[i][chosenChoice] += 1;
                    responseFrequency[i][numOfRubricChoices] += 1;
                }
            }
        }
        return responseFrequency;
    }

    /**
     * Returns the calculated percentage frequencies for each choice and average value for each sub-question
     * The percentage value between [0,1] of each choice being selected for the sub-question.
     *
     * <p>Values are set to 0 if there are no responses to that sub-question.
     * Average value is set to 0 if there are no assigned weights.
     *
     * <p>e.g.<br>
     * percentageFrequencyAndAverageValue[subQuestionIndex][choiceIndex]
     * -> is the percentage choiceIndex is chosen for subQuestionIndex.<br>
     * percentageFrequencyAndAverageValue[subQuestionIndex][numOfRubricChoices]
     * -> is the average weight of the responses for the given sub-question.
     *
     * @param responseFrequency decides whether the value returned is for excluding-self or including-self.
     */
    private static float[][] calculatePercentageFrequencyAndAverage(
            FeedbackRubricQuestionDetails questionDetails, int [][] responseFrequency) {
        Assumption.assertNotNull("Response Frequency should be initialised and calculated first.",
                (Object[]) responseFrequency);

        int numOfRubricSubQuestions = questionDetails.getNumOfRubricSubQuestions();
        int numOfRubricChoices = questionDetails.getNumOfRubricChoices();

        float[][] percentageFrequencyAndAverage = new float[numOfRubricSubQuestions][numOfRubricChoices + 1];
        // calculate percentage frequencies and average value
        for (int i = 0; i < percentageFrequencyAndAverage.length; i++) {
            int totalForSubQuestion = responseFrequency[i][numOfRubricChoices];
            //continue to next row if no response for this sub-question
            if (totalForSubQuestion == 0) {
                continue;
            }
            // divide responsesFrequency by totalForSubQuestion to get percentage
            for (int j = 0; j < numOfRubricChoices; j++) {
                percentageFrequencyAndAverage[i][j] = (float) responseFrequency[i][j] / totalForSubQuestion;
            }
            List<List<Double>> weights = questionDetails.getRubricWeights();
            // calculate the average for each sub-question
            if (questionDetails.hasAssignedWeights()) {
                for (int j = 0; j < numOfRubricChoices; j++) {
                    float choiceWeight =
                            (float) (weights.get(i).get(j)
                                    * percentageFrequencyAndAverage[i][j]);
                    percentageFrequencyAndAverage[i][numOfRubricChoices] += choiceWeight;
                }
            }
        }
        return percentageFrequencyAndAverage;
    }

    /**
     * Class to store any stats related to a recipient.
     */
    private class RubricRecipientStatistics {
        String recipientEmail;
        String recipientName;
        String recipientTeam;
        int[][] numOfResponsesPerSubQuestionPerChoice;
        double[] totalPerSubQuestion;
        int[] respondentsPerSubQuestion;
        List<List<Double>> weights;

        RubricRecipientStatistics(String recipientEmail, String recipientName, String recipientTeam) {
            Assumption.assertTrue("Per Recipient Stats is only available when weights are enabled", hasAssignedWeights);
            this.recipientEmail = recipientEmail;
            this.recipientName = recipientName;
            this.recipientTeam = recipientTeam;
            numOfResponsesPerSubQuestionPerChoice = new int[getNumOfRubricSubQuestions()][getNumOfRubricChoices()];
            totalPerSubQuestion = new double[getNumOfRubricSubQuestions()];
            respondentsPerSubQuestion = new int[getNumOfRubricSubQuestions()];
            weights = getRubricWeights();
        }

        public void addResponseToRecipientStats(FeedbackResponseAttributes response) {
            if (!response.recipient.equalsIgnoreCase(recipientEmail)) {
                return;
            }

            FeedbackRubricResponseDetails rubricResponse = (FeedbackRubricResponseDetails) response.getResponseDetails();

            for (int i = 0; i < getNumOfRubricSubQuestions(); i++) {
                int choice = rubricResponse.getAnswer(i);

                if (choice >= 0) {
                    ++numOfResponsesPerSubQuestionPerChoice[i][choice];
                    totalPerSubQuestion[i] += weights.get(i).get(choice);
                    respondentsPerSubQuestion[i]++;
                }
            }
        }

        public String getCsvForSubQuestion(int subQuestion) {
            StringBuilder csv = new StringBuilder(100);
            String alphabeticalIndex = StringHelper.integerToLowerCaseAlphabeticalIndex(subQuestion + 1);
            String subQuestionString = SanitizationHelper.sanitizeForCsv(alphabeticalIndex + ") "
                    + getRubricSubQuestions().get(subQuestion));
            DecimalFormat df = new DecimalFormat("0.00");
            DecimalFormat dfWeight = new DecimalFormat("#.##");

            // Append recipient identification details and rubric subQuestion
            csv.append(recipientTeam).append(',')
                    .append(recipientName).append(',')
                    .append(recipientEmail).append(',')
                    .append(subQuestionString);

            // Append number of responses per subQuestion per rubric choice
            for (int i = 0; i < getNumOfRubricChoices(); i++) {
                csv.append(',').append(Integer.toString(numOfResponsesPerSubQuestionPerChoice[subQuestion][i]))
                        .append(" [" + dfWeight.format(weights.get(subQuestion).get(i)) + "]");
            }

            // Append aggregate statistics
            csv.append(',').append(df.format(totalPerSubQuestion[subQuestion])).append(',')
                    .append(respondentsPerSubQuestion[subQuestion] == 0 ? "0.00"
                            : df.format(totalPerSubQuestion[subQuestion] / respondentsPerSubQuestion[subQuestion]))
                    .append(System.lineSeparator());

            return csv.toString();
        }

        public String getCsvForAllSubQuestions() {
            StringBuilder csv = new StringBuilder(100);

            for (int i = 0; i < getNumOfRubricSubQuestions(); i++) {
                csv.append(getCsvForSubQuestion(i));
            }

            return csv.toString();
        }
    }

}
