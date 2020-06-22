package teammates.common.datatransfer.questions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StringHelper;

public class FeedbackRankOptionsQuestionDetails extends FeedbackRankQuestionDetails {
    public static final transient int MIN_NUM_OF_OPTIONS = 2;
    public static final transient String ERROR_INVALID_MAX_OPTIONS_ENABLED =
            "Max options enabled is invalid";
    public static final transient String ERROR_INVALID_MIN_OPTIONS_ENABLED =
            "Min options enabled is invalid";
    public static final transient String ERROR_MIN_OPTIONS_ENABLED_MORE_THAN_CHOICES =
            "Min options enabled is more than the total choices";
    public static final transient String ERROR_MAX_OPTIONS_ENABLED_MORE_THAN_CHOICES =
            "Max options enabled is more than the total choices";
    public static final transient String ERROR_NOT_ENOUGH_OPTIONS =
            "Too little options for " + Const.FeedbackQuestionTypeNames.RANK_OPTION
            + ". Minimum number of options is: ";
    public static final transient String ERROR_EMPTY_OPTIONS_ENTERED =
            "Empty Rank Options are not allowed";

    List<String> options;

    public FeedbackRankOptionsQuestionDetails() {
        super(FeedbackQuestionType.RANK_OPTIONS);

        this.options = new ArrayList<>();
    }

    @Override
    public List<String> getInstructions() {
        List<String> instructions = new ArrayList<>();

        if (minOptionsToBeRanked != NO_VALUE) {
            instructions.add("You need to rank at least " + minOptionsToBeRanked + " options.");
        }

        if (maxOptionsToBeRanked != NO_VALUE) {
            instructions.add("Rank no more than " + maxOptionsToBeRanked + " options.");
        }

        return instructions;
    }

    @Override
    public String getQuestionTypeDisplayName() {
        return Const.FeedbackQuestionTypeNames.RANK_OPTION;
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
        Map<String, List<Integer>> optionRanks = generateOptionRanksMapping(responses);

        Map<String, Integer> optionOverallRank = generateNormalizedOverallRankMapping(optionRanks);

        optionRanks.forEach((key, ranksAssigned) -> {
            String option = SanitizationHelper.sanitizeForCsv(key);
            String overallRank = Integer.toString(optionOverallRank.get(key));

            String fragment = option + "," + overallRank + ","
                    + StringHelper.join(",", ranksAssigned) + System.lineSeparator();
            fragments.append(fragment);
        });

        return "Option, Overall Rank, Ranks Received" + System.lineSeparator()
                + fragments.toString() + System.lineSeparator();
    }

    /**
     * From the feedback responses, generate a mapping of the option to a list of
     * ranks received for that option.
     * The key of the map returned is the option name.
     * The values of the map are list of ranks received by the key.
     * @param responses  a list of responses
     */
    private Map<String, List<Integer>> generateOptionRanksMapping(
                                            List<FeedbackResponseAttributes> responses) {
        Map<String, List<Integer>> optionRanks = new HashMap<>();
        for (FeedbackResponseAttributes response : responses) {
            FeedbackRankOptionsResponseDetails frd = (FeedbackRankOptionsResponseDetails) response.getResponseDetails();

            List<Integer> answers = frd.getAnswerList();
            Map<String, Integer> mapOfOptionToRank = new HashMap<>();

            Assumption.assertEquals(answers.size(), options.size());

            for (int i = 0; i < options.size(); i++) {
                int rankReceived = answers.get(i);
                mapOfOptionToRank.put(options.get(i), rankReceived);
            }

            Map<String, Integer> normalisedRankForOption =
                    obtainMappingToNormalisedRanksForRanking(mapOfOptionToRank, options);

            for (String optionReceivingRanks : options) {
                int rankReceived = normalisedRankForOption.get(optionReceivingRanks);

                if (rankReceived != Const.POINTS_NOT_SUBMITTED) {
                    updateOptionRanksMapping(optionRanks, optionReceivingRanks, rankReceived);
                }
            }
        }
        return optionRanks;
    }

    @Override
    public boolean shouldChangesRequireResponseDeletion(FeedbackQuestionDetails newDetails) {
        FeedbackRankOptionsQuestionDetails newRankQuestionDetails = (FeedbackRankOptionsQuestionDetails) newDetails;

        return this.options.size() != newRankQuestionDetails.options.size()
            || !this.options.containsAll(newRankQuestionDetails.options)
            || !newRankQuestionDetails.options.containsAll(this.options)
            || this.minOptionsToBeRanked != newRankQuestionDetails.minOptionsToBeRanked
            || this.maxOptionsToBeRanked != newRankQuestionDetails.maxOptionsToBeRanked;
    }

    @Override
    public String getCsvHeader() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < this.options.size(); i++) {
            result.append(String.format("Rank %d,", i + 1));
        }
        result.deleteCharAt(result.length() - 1); // remove the last comma

        return result.toString();
    }

    @Override
    public List<String> validateQuestionDetails() {
        List<String> errors = new ArrayList<>();

        boolean isEmptyRankOptionEntered = options.stream().anyMatch(optionText -> optionText.trim().equals(""));
        if (isEmptyRankOptionEntered) {
            errors.add(ERROR_EMPTY_OPTIONS_ENTERED);
        }

        boolean isMaxOptionsToBeRankedEnabled = maxOptionsToBeRanked != NO_VALUE;
        boolean isMinOptionsToBeRankedEnabled = minOptionsToBeRanked != NO_VALUE;

        if (isMaxOptionsToBeRankedEnabled) {
            if (maxOptionsToBeRanked < 1) {
                errors.add(ERROR_INVALID_MAX_OPTIONS_ENABLED);
            }
            if (maxOptionsToBeRanked > options.size()) {
                errors.add(ERROR_MAX_OPTIONS_ENABLED_MORE_THAN_CHOICES);
            }
        }

        if (isMinOptionsToBeRankedEnabled) {
            if (minOptionsToBeRanked < 1) {
                errors.add(ERROR_INVALID_MIN_OPTIONS_ENABLED);
            }
            if (minOptionsToBeRanked > options.size()) {
                errors.add(ERROR_MIN_OPTIONS_ENABLED_MORE_THAN_CHOICES);
            }
        }

        if (isMaxOptionsToBeRankedEnabled && isMinOptionsToBeRankedEnabled
                && minOptionsToBeRanked > maxOptionsToBeRanked) {
            errors.add(ERROR_INVALID_MIN_OPTIONS_ENABLED);
        }

        if (options.size() < MIN_NUM_OF_OPTIONS) {
            errors.add(ERROR_NOT_ENOUGH_OPTIONS + MIN_NUM_OF_OPTIONS + ".");
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
}
