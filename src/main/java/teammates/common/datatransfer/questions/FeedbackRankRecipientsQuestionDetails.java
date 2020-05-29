package teammates.common.datatransfer.questions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.util.Const;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StringHelper;

public class FeedbackRankRecipientsQuestionDetails extends FeedbackRankQuestionDetails {

    public FeedbackRankRecipientsQuestionDetails() {
        super(FeedbackQuestionType.RANK_RECIPIENTS);
    }

    @Override
    public String getQuestionTypeDisplayName() {
        return Const.FeedbackQuestionTypeNames.RANK_RECIPIENT;
    }

    @Override
    public List<String> getInstructions() {
        List<String> instructions = new ArrayList<>();

        if (minOptionsToBeRanked != NO_VALUE) {
            instructions.add("You need to rank at least " + minOptionsToBeRanked + " recipients.");
        }

        if (maxOptionsToBeRanked != NO_VALUE) {
            instructions.add("Rank no more than " + maxOptionsToBeRanked + " recipients.");
        }

        return instructions;
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

        Map<String, List<Integer>> recipientRanks = generateOptionRanksMapping(responses);

        Map<String, Integer> recipientOverallRank = generateNormalizedOverallRankMapping(recipientRanks);

        Map<String, List<Integer>> recipientRanksExcludingSelf = getRecipientRanksExcludingSelf(responses);

        Map<String, Integer> recipientOverallRankExceptSelf =
                generateNormalizedOverallRankMapping(recipientRanksExcludingSelf);

        Map<String, Integer> recipientSelfRanks = generateSelfRankForEachRecipient(responses);

        recipientRanks.forEach((participantIdentifier, ranks) -> {

            String teamName = bundle.getTeamNameForEmail(participantIdentifier);
            String recipientName = bundle.getNameForEmail(participantIdentifier);
            String option = SanitizationHelper.sanitizeForCsv(teamName)
                            + ","
                            + SanitizationHelper.sanitizeForCsv(recipientName);

            String overallRankExceptSelf = recipientOverallRankExceptSelf.containsKey(participantIdentifier)
                    ? Integer.toString(recipientOverallRankExceptSelf.get(participantIdentifier)) : "-";
            String overallRank = Integer.toString(recipientOverallRank.get(participantIdentifier));
            String selfRank = recipientSelfRanks.containsKey(participantIdentifier)
                    ? Integer.toString(recipientSelfRanks.get(participantIdentifier)) : "-";

            fragments.append(option);
            fragments.append(',').append(selfRank);
            fragments.append(',').append(overallRank);
            fragments.append(',').append(overallRankExceptSelf);
            fragments.append(',');
            fragments.append(StringHelper.join(",", ranks));
            fragments.append(System.lineSeparator());
        });

        return "Team, Recipient, Self Rank, Overall Rank, Overall Rank Excluding Self, Ranks Received"
                + System.lineSeparator() + fragments + System.lineSeparator();
    }

    /**
     * From the feedback responses, generate a mapping of the option to a list of
     * ranks received for that option.
     * The key of the map returned is the recipient's participant identifier.
     * The values of the map are list of ranks received by the recipient.
     * @param responses  a list of responses
     */
    private Map<String, List<Integer>> generateOptionRanksMapping(List<FeedbackResponseAttributes> responses) {

        Map<FeedbackResponseAttributes, Integer> normalisedRankOfResponse = getNormalisedRankForEachResponse(responses);

        Map<String, List<Integer>> optionRanks = new HashMap<>();
        for (FeedbackResponseAttributes response : responses) {
            updateOptionRanksMapping(optionRanks, response.recipient, normalisedRankOfResponse.get(response));
        }

        return optionRanks;
    }

    /**
     * Generates a key, value mapping. Each key corresponds to a recipient and its value is the normalised self rank.
     * @param responses  a list of responses
     */
    private Map<String, Integer> generateSelfRankForEachRecipient(List<FeedbackResponseAttributes> responses) {
        Map<FeedbackResponseAttributes, Integer> normalisedRankOfResponse = getNormalisedRankForEachResponse(responses);
        Map<String, Integer> recipientToSelfRank = new HashMap<>();

        for (FeedbackResponseAttributes response : responses) {
            if (response.recipient.equalsIgnoreCase(response.giver)) {
                recipientToSelfRank.put(response.recipient, normalisedRankOfResponse.get(response));
            }
        }

        return recipientToSelfRank;
    }

    /**
     * Returns a map of response to the normalised rank by resolving ties for each giver's set of responses.
     * @see FeedbackRankQuestionDetails#obtainMappingToNormalisedRanksForRanking(Map, List) for how ties are resolved
     */
    private Map<FeedbackResponseAttributes, Integer> getNormalisedRankForEachResponse(
                                                            List<FeedbackResponseAttributes> responses) {

        // collect each giver's responses
        Map<String, List<FeedbackResponseAttributes>> responsesGivenByPerson = new HashMap<>();
        for (FeedbackResponseAttributes response : responses) {
            responsesGivenByPerson.computeIfAbsent(response.giver, key -> new ArrayList<>())
                                  .add(response);
        }

        // generate response-responseDetails pair
        Map<FeedbackResponseAttributes, Integer> rankOfResponse = new HashMap<>();
        for (FeedbackResponseAttributes res : responses) {
            FeedbackRankRecipientsResponseDetails frd = (FeedbackRankRecipientsResponseDetails) res.getResponseDetails();
            rankOfResponse.put(res, frd.answer);
        }

        // resolve ties for each giver's responses
        Map<FeedbackResponseAttributes, Integer> normalisedRankOfResponse = new HashMap<>();
        responsesGivenByPerson.forEach((key, feedbackResponseAttributesList) -> {
            normalisedRankOfResponse.putAll(obtainMappingToNormalisedRanksForRanking(rankOfResponse,
                    feedbackResponseAttributesList));
        });

        return normalisedRankOfResponse;
    }

    /**
     * Returns list of responses excluding responses given to self.
     *
     * @param responses a list of responses
     * @return list of responses excluding self given responses
     */
    private List<FeedbackResponseAttributes> getResponsesExcludingSelf(List<FeedbackResponseAttributes> responses) {
        List<FeedbackResponseAttributes> responsesExcludingSelf = new ArrayList<>();
        for (FeedbackResponseAttributes response : responses) {
            if (!response.giver.equalsIgnoreCase(response.recipient)) {
                responsesExcludingSelf.add(response);
            }
        }
        return responsesExcludingSelf;
    }

    /**
     * Returns map of recipient ranks excluding self.
     *
     * @param responses list of all the responses for a question
     * @return map of recipient ranks excluding self responses
     */
    private Map<String, List<Integer>> getRecipientRanksExcludingSelf(List<FeedbackResponseAttributes> responses) {
        List<FeedbackResponseAttributes> responsesExcludingSelf = getResponsesExcludingSelf(responses);
        return generateOptionRanksMapping(responsesExcludingSelf);
    }

    @Override
    public boolean shouldChangesRequireResponseDeletion(FeedbackQuestionDetails newDetails) {
        return false;
    }

    @Override
    public String getCsvHeader() {
        return "Feedback";
    }

    @Override
    public List<String> validateQuestionDetails() {
        return new ArrayList<>();
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
