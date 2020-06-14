package teammates.common.datatransfer.questions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StringHelper;

public class FeedbackConstantSumQuestionDetails extends FeedbackQuestionDetails {
    private int numOfConstSumOptions;
    private List<String> constSumOptions;
    private boolean distributeToRecipients;
    private boolean pointsPerOption;
    private boolean forceUnevenDistribution;
    private String distributePointsFor;
    private int points;

    public FeedbackConstantSumQuestionDetails() {
        super(FeedbackQuestionType.CONSTSUM);

        this.numOfConstSumOptions = 0;
        this.constSumOptions = new ArrayList<>();
        this.distributeToRecipients = false;
        this.pointsPerOption = false;
        this.points = 100;
        this.forceUnevenDistribution = false;
        this.distributePointsFor = FeedbackConstantSumDistributePointsType.NONE.getDisplayedOption();
    }

    public FeedbackConstantSumQuestionDetails(String questionText,
            List<String> constSumOptions,
            boolean pointsPerOption, int points, boolean unevenDistribution, String distributePointsFor) {
        super(FeedbackQuestionType.CONSTSUM, questionText);

        this.numOfConstSumOptions = constSumOptions.size();
        this.constSumOptions = constSumOptions;
        this.distributeToRecipients = false;
        this.pointsPerOption = pointsPerOption;
        this.points = points;
        this.forceUnevenDistribution = unevenDistribution;
        this.distributePointsFor = distributePointsFor;
    }

    public void setNumOfConstSumOptions(int numOfConstSumOptions) {
        this.numOfConstSumOptions = numOfConstSumOptions;
    }

    public void setConstSumOptions(List<String> constSumOptions) {
        this.constSumOptions = constSumOptions;
    }

    public void setDistributeToRecipients(boolean distributeToRecipients) {
        this.distributeToRecipients = distributeToRecipients;
    }

    public void setPointsPerOption(boolean pointsPerOption) {
        this.pointsPerOption = pointsPerOption;
    }

    public void setForceUnevenDistribution(boolean forceUnevenDistribution) {
        this.forceUnevenDistribution = forceUnevenDistribution;
    }

    public void setDistributePointsFor(String distributePointsFor) {
        this.distributePointsFor = distributePointsFor;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    @Override
    public String getQuestionTypeDisplayName() {
        if (distributeToRecipients) {
            return Const.FeedbackQuestionTypeNames.CONSTSUM_RECIPIENT;
        }
        return Const.FeedbackQuestionTypeNames.CONSTSUM_OPTION;
    }

    @Override
    public List<String> getInstructions() {
        return null;
    }

    @Override
    public String getQuestionResultStatisticsCsv(
            List<FeedbackResponseAttributes> responses,
            FeedbackQuestionAttributes question,
            FeedbackSessionResultsBundle bundle) {
        if (responses.isEmpty()) {
            return "";
        }

        StringBuilder fragments = new StringBuilder();
        List<String> options = constSumOptions;
        Map<String, List<Integer>> optionPoints = generateOptionPointsMapping(responses);

        DecimalFormat df = new DecimalFormat("#.##");

        Map<String, List<Integer>> sortedOptionPoints = new TreeMap<>();

        Map<String, String> identifierMap = new HashMap<>();

        if (distributeToRecipients) {
            putRecipientsInSortedMap(optionPoints, identifierMap, sortedOptionPoints, bundle);
        } else {
            putOptionsInSortedMap(optionPoints, options, sortedOptionPoints);
        }

        sortedOptionPoints.forEach((key, points) -> {
            String option;
            if (distributeToRecipients) {
                String participantIdentifier = identifierMap.get(key);
                String teamName = bundle.getTeamNameForEmail(participantIdentifier);
                String recipientName = bundle.getNameForEmail(participantIdentifier);

                option = SanitizationHelper.sanitizeForCsv(teamName)
                         + "," + SanitizationHelper.sanitizeForCsv(recipientName);
            } else {
                option = SanitizationHelper.sanitizeForCsv(key);
            }

            double average = computeAverage(points);
            double total = computeTotal(points);

            fragments.append(option)
                    .append(',').append(df.format(average))
                    .append(',').append(df.format(total))
                    .append(',').append(StringHelper.join(",", points))
                    .append(System.lineSeparator());

        });

        return (distributeToRecipients ? "Team, Recipient" : "Option")
               + ", Average Points, Total Points, Received Points" + System.lineSeparator()
               + fragments + System.lineSeparator();
    }

    /**
     * Puts recipients from an unsorted map to a sorted map.
     *
     * @param recipientMapping      Original map containing recipients
     * @param identifierMap         Helper map to retrieve email from name concatenated with email string
     * @param sortedOptionPoints    Sorted map to contain recipient info, recipient concatenated with email used as key
     */
    private void putRecipientsInSortedMap(
            Map<String, List<Integer>> recipientMapping, Map<String, String> identifierMap,
            Map<String, List<Integer>> sortedOptionPoints, FeedbackSessionResultsBundle bundle) {

        recipientMapping.forEach((participantIdentifier, value) -> {
            String name = bundle.getNameForEmail(participantIdentifier);
            String nameEmail = name + participantIdentifier;

            identifierMap.put(nameEmail, participantIdentifier);
            sortedOptionPoints.put(nameEmail, value);
        });
    }

    /**
     * Puts options from an unsorted map to a sorted map.
     *
     * @param optionPoints          Original mapping of option points
     * @param optionList            List of options in question
     * @param sortedOptionPoints    Sorted map of option points
     */
    private void putOptionsInSortedMap(
            Map<String, List<Integer>> optionPoints, List<String> optionList,
            Map<String, List<Integer>> sortedOptionPoints) {

        optionPoints.forEach((key, value) -> {
            String option = optionList.get(Integer.parseInt(key));

            sortedOptionPoints.put(option, value);
        });
    }

    /**
     * From the feedback responses, generate a mapping of the option to a list of points received for that option.
     * The key of the map returned is the option name / recipient's participant identifier.
     * The values of the map are list of points received by the key.
     * @param responses  a list of responses
     */
    private Map<String, List<Integer>> generateOptionPointsMapping(
            List<FeedbackResponseAttributes> responses) {

        Map<String, List<Integer>> optionPoints = new HashMap<>();
        for (FeedbackResponseAttributes response : responses) {
            FeedbackConstantSumResponseDetails frd = (FeedbackConstantSumResponseDetails) response.getResponseDetails();

            for (int i = 0; i < frd.getAnswerList().size(); i++) {
                String optionReceivingPoints =
                        distributeToRecipients ? response.recipient : String.valueOf(i);

                int pointsReceived = frd.getAnswerList().get(i);
                updateOptionPointsMapping(optionPoints, optionReceivingPoints, pointsReceived);
            }
        }
        return optionPoints;
    }

    /**
     * Used to update the OptionPointsMapping for the option optionReceivingPoints.
     */
    private void updateOptionPointsMapping(
            Map<String, List<Integer>> optionPoints,
            String optionReceivingPoints, int pointsReceived) {
        optionPoints.computeIfAbsent(optionReceivingPoints, key -> new ArrayList<>()).add(pointsReceived);
    }

    private int computeTotal(List<Integer> points) {
        int total = 0;
        for (Integer point : points) {
            total += point;
        }
        return total;
    }

    private double computeAverage(List<Integer> points) {
        return (double) computeTotal(points) / points.size();
    }

    @Override
    public boolean shouldChangesRequireResponseDeletion(
            FeedbackQuestionDetails newDetails) {
        FeedbackConstantSumQuestionDetails newConstSumDetails = (FeedbackConstantSumQuestionDetails) newDetails;

        if (this.numOfConstSumOptions != newConstSumDetails.numOfConstSumOptions
                || !this.constSumOptions.containsAll(newConstSumDetails.constSumOptions)
                || !newConstSumDetails.constSumOptions.containsAll(this.constSumOptions)) {
            return true;
        }

        if (this.distributeToRecipients != newConstSumDetails.distributeToRecipients) {
            return true;
        }

        if (this.points != newConstSumDetails.points) {
            return true;
        }

        if (this.pointsPerOption != newConstSumDetails.pointsPerOption) {
            return true;
        }

        if (this.forceUnevenDistribution != newConstSumDetails.forceUnevenDistribution) {
            return true;
        }

        return !this.distributePointsFor.equals(newConstSumDetails.distributePointsFor);
    }

    @Override
    public String getCsvHeader() {
        if (distributeToRecipients) {
            return "Feedback";
        }
        List<String> sanitizedOptions = SanitizationHelper.sanitizeListForCsv(constSumOptions);
        return "Feedbacks:," + StringHelper.toString(sanitizedOptions, ",");
    }

    @Override
    public List<String> validateQuestionDetails() {
        List<String> errors = new ArrayList<>();
        if (!distributeToRecipients && numOfConstSumOptions < Const.FeedbackQuestion.CONST_SUM_MIN_NUM_OF_OPTIONS) {
            errors.add(Const.FeedbackQuestion.CONST_SUM_ERROR_NOT_ENOUGH_OPTIONS
                       + Const.FeedbackQuestion.CONST_SUM_MIN_NUM_OF_OPTIONS + ".");
        }

        if (points < Const.FeedbackQuestion.CONST_SUM_MIN_NUM_OF_POINTS) {
            errors.add(Const.FeedbackQuestion.CONST_SUM_ERROR_NOT_ENOUGH_POINTS
                       + Const.FeedbackQuestion.CONST_SUM_MIN_NUM_OF_POINTS + ".");
        }

        if (!FieldValidator.areElementsUnique(constSumOptions)) {
            errors.add(Const.FeedbackQuestion.CONST_SUM_ERROR_DUPLICATE_OPTIONS);
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

    public int getNumOfConstSumOptions() {
        return numOfConstSumOptions;
    }

    public List<String> getConstSumOptions() {
        return constSumOptions;
    }

    public String getDistributePointsFor() {
        return distributePointsFor;
    }

    public boolean isForceUnevenDistribution() {
        return forceUnevenDistribution;
    }

    public boolean isDistributeToRecipients() {
        return distributeToRecipients;
    }

    public boolean isPointsPerOption() {
        return pointsPerOption;
    }

    public int getPoints() {
        return points;
    }
}
