package teammates.common.datatransfer.questions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.HttpRequestHelper;

public abstract class FeedbackRankQuestionDetails extends FeedbackQuestionDetails {

    static final transient int NO_VALUE = Integer.MIN_VALUE;
    protected int minOptionsToBeRanked;
    protected int maxOptionsToBeRanked;
    private boolean areDuplicatesAllowed;

    FeedbackRankQuestionDetails(FeedbackQuestionType questionType) {
        super(questionType);
        minOptionsToBeRanked = NO_VALUE;
        maxOptionsToBeRanked = NO_VALUE;
    }

    public FeedbackRankQuestionDetails(FeedbackQuestionType questionType, String questionText) {
        super(questionType, questionText);
        minOptionsToBeRanked = NO_VALUE;
        maxOptionsToBeRanked = NO_VALUE;
    }

    @Override
    public boolean extractQuestionDetails(Map<String, String[]> requestParameters,
                                          FeedbackQuestionType questionType) {

        String areDuplicatesAllowedString =
                HttpRequestHelper.getValueFromParamMap(
                        requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_RANKISDUPLICATESALLOWED);
        boolean areDuplicatesAllowed = "on".equals(areDuplicatesAllowedString);

        this.areDuplicatesAllowed = areDuplicatesAllowed;

        return true;
    }

    @Override
    public abstract String getQuestionTypeDisplayName();

    @Override
    public abstract String getQuestionWithExistingResponseSubmissionFormHtml(
                        boolean sessionIsOpen, int qnIdx, int responseIdx, String courseId,
                        int totalNumRecipients,
                        FeedbackResponseDetails existingResponseDetails, StudentAttributes student);

    @Override
    public abstract String getQuestionWithoutExistingResponseSubmissionFormHtml(
            boolean sessionIsOpen, int qnIdx, int responseIdx, String courseId, int totalNumRecipients,
            StudentAttributes student);

    @Override
    public abstract String getQuestionSpecificEditFormHtml(int questionNumber);

    /**
     * Updates the mapping of ranks for the option optionReceivingPoints.
     */
    protected void updateOptionRanksMapping(
            Map<String, List<Integer>> optionRanks,
            String optionReceivingRanks, int rankReceived) {
        optionRanks.computeIfAbsent(optionReceivingRanks, key -> new ArrayList<>())
                   .add(rankReceived);
    }

    /**
     * Returns the list of points as as string to display.
     */
    protected String getListOfRanksReceivedAsString(List<Integer> ranksReceived) {
        ranksReceived.sort(null);
        StringBuilder pointsReceived = new StringBuilder();

        if (ranksReceived.size() > 10) {
            for (int i = 0; i < 5; i++) {
                pointsReceived.append(ranksReceived.get(i)).append(" , ");
            }

            pointsReceived.append("...");

            for (int i = ranksReceived.size() - 5; i < ranksReceived.size(); i++) {
                pointsReceived.append(" , ").append(ranksReceived.get(i));
            }
        } else {
            for (int i = 0; i < ranksReceived.size(); i++) {
                pointsReceived.append(ranksReceived.get(i));

                if (i != ranksReceived.size() - 1) {
                    pointsReceived.append(" , ");
                }
            }
        }

        return pointsReceived.toString();
    }

    protected double computeAverage(List<Integer> values) {
        double total = 0;
        for (double value : values) {
            total = total + value;
        }
        return total / values.size();
    }

    /**
     * For a single set of ranking (options / feedback responses),
     * fix ties by assigning the MIN value of the ordering to all the tied options
     * e.g. the normalised ranks of the set of ranks (1,4,1,4) is (1,3,1,3)
     * @param rankOfOption  a map containing the original unfiltered answer for each options
     * @param options  a list of options
     * @return a map of the option to the normalised rank of the response
     */
    protected <K> Map<K, Integer> obtainMappingToNormalisedRanksForRanking(
                                                        Map<K, Integer> rankOfOption,
                                                        List<K> options) {
        Map<K, Integer> normalisedRankForSingleSetOfRankings = new HashMap<>();

        // group the options/feedback response by its rank
        TreeMap<Integer, List<K>> rankToAnswersMap = new TreeMap<>();
        for (K answer : options) {
            int rankGiven = rankOfOption.get(answer);
            if (rankGiven == Const.POINTS_NOT_SUBMITTED) {
                normalisedRankForSingleSetOfRankings.put(answer, Const.POINTS_NOT_SUBMITTED);
                continue;
            }

            rankToAnswersMap.computeIfAbsent(rankGiven, key -> new ArrayList<>())
                            .add(answer);
        }

        // every answer in the same group is given the same rank
        int currentRank = 1;
        for (List<K> answersWithSameRank : rankToAnswersMap.values()) {
            for (K answer : answersWithSameRank) {
                normalisedRankForSingleSetOfRankings.put(answer, currentRank);
            }

            currentRank += answersWithSameRank.size();
        }

        return normalisedRankForSingleSetOfRankings;
    }

    /**
     * Generates the normalized overall ranking from the ranks of the recipients or options
     * by comparing the average ranks of the recipients or options
     * E.g. A and B received (1,2) and C received (1,2,3),
     * so A and B have the average rank of 1.5 and C's average rank is 2
     * After normalization, the overall rank of A, B and C will be 1, 1 and 3
     * @param recipientRanks is a map
     *                       with key being the recipient identifier and the value the list of ranks of the recipient
     * @return a map of recipients/options with their corresponding overall rank after normalization
     */
    protected Map<String, Integer> generateNormalizedOverallRankMapping(Map<String, List<Integer>> recipientRanks) {
        TreeMap<Double, List<String>> recipientAverageRank = new TreeMap<>();
        recipientRanks.forEach((recipientIdentifier, ranks) -> {
            double average = computeAverage(ranks);
            recipientAverageRank.computeIfAbsent(average, key -> new ArrayList<>())
                    .add(recipientIdentifier);
        });

        Map<String, Integer> normalizedOverallRanking = new HashMap<>();
        int currentRank = 1;
        for (List<String> recipientsWithSameRank : recipientAverageRank.values()) {
            for (String recipient : recipientsWithSameRank) {
                normalizedOverallRanking.put(recipient, currentRank);
            }
            currentRank += recipientsWithSameRank.size();
        }

        return normalizedOverallRanking;
    }

    public boolean isAreDuplicatesAllowed() {
        return areDuplicatesAllowed;
    }

}
