package teammates.common.datatransfer.questions;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.google.common.primitives.Ints;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StringHelper;
import teammates.common.util.Templates;
import teammates.common.util.Templates.FeedbackQuestion.FormTemplates;
import teammates.common.util.Templates.FeedbackQuestion.Slots;
import teammates.ui.pagedata.PageData;
import teammates.ui.template.ElementTag;
import teammates.ui.template.InstructorFeedbackResultsResponseRow;

public class FeedbackRankRecipientsQuestionDetails extends FeedbackRankQuestionDetails {

    public FeedbackRankRecipientsQuestionDetails() {
        super(FeedbackQuestionType.RANK_RECIPIENTS);
    }

    @Override
    public String getQuestionTypeDisplayName() {
        return Const.FeedbackQuestionTypeNames.RANK_RECIPIENT;
    }

    @Override
    public boolean extractQuestionDetails(Map<String, String[]> requestParameters,
            FeedbackQuestionType questionType) {
        super.extractQuestionDetails(requestParameters, questionType);

        String minRecipientsToBeRanked = Strings.nullToEmpty(HttpRequestHelper.getValueFromParamMap(
                requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_RANK_MIN_RECIPIENTS_TO_BE_RANKED));
        String maxRecipientsToBeRanked = Strings.nullToEmpty(HttpRequestHelper.getValueFromParamMap(
                requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_RANK_MAX_RECIPIENTS_TO_BE_RANKED));

        minOptionsToBeRanked = MoreObjects.firstNonNull(Ints.tryParse(minRecipientsToBeRanked), NO_VALUE);
        maxOptionsToBeRanked = MoreObjects.firstNonNull(Ints.tryParse(maxRecipientsToBeRanked), NO_VALUE);

        return true;
    }

    @Override
    public String getQuestionWithExistingResponseSubmissionFormHtml(
                        boolean sessionIsOpen, int qnIdx, int responseIdx, String courseId,
                        int totalNumRecipients,
                        FeedbackResponseDetails existingResponseDetails, StudentAttributes student) {

        FeedbackRankRecipientsResponseDetails existingResponse =
                (FeedbackRankRecipientsResponseDetails) existingResponseDetails;
        StringBuilder optionListHtml = new StringBuilder();
        String optionFragmentTemplate = FormTemplates.RANK_SUBMISSION_FORM_OPTIONFRAGMENT;

        String optionFragment =
                Templates.populateTemplate(optionFragmentTemplate,
                        Slots.QUESTION_INDEX, Integer.toString(qnIdx),
                        Slots.RESPONSE_INDEX, Integer.toString(responseIdx),
                        Slots.OPTION_INDEX, "0",
                        Slots.DISABLED, sessionIsOpen ? "" : "disabled",
                        Slots.RANK_OPTION_VISIBILITY, "style=\"display:none\"",
                        Slots.OPTIONS, getSubmissionOptionsHtmlForRankingRecipients(
                                           totalNumRecipients, existingResponse.answer),
                        Slots.FEEDBACK_RESPONSE_TEXT, Const.ParamsNames.FEEDBACK_RESPONSE_TEXT,
                        Slots.RANK_OPTION_VALUE, "");
        optionListHtml.append(optionFragment).append(System.lineSeparator());

        boolean isMinOptionsToBeRankedEnabled = minOptionsToBeRanked != NO_VALUE;
        boolean isMaxOptionsToBeRankedEnabled = maxOptionsToBeRanked != NO_VALUE;

        return Templates.populateTemplate(
                FormTemplates.RANK_SUBMISSION_FORM,
                Slots.RANK_SUBMISSION_FORM_OPTION_FRAGMENTS, optionListHtml.toString(),
                Slots.QUESTION_INDEX, Integer.toString(qnIdx),
                Slots.RESPONSE_INDEX, Integer.toString(responseIdx),
                Slots.RANK_OPTION_VISIBILITY, "style=\"display:none\"",
                Slots.RANK_PARAM_TO_RECIPIENT, Const.ParamsNames.FEEDBACK_QUESTION_RANKTORECIPIENTS,
                Slots.RANK_TO_RECIPIENTS_VALUE, "true",
                Slots.RANK_PARAM_NUM_OPTION, Const.ParamsNames.FEEDBACK_QUESTION_RANKNUMOPTIONS,
                Slots.RANK_NUM_OPTION_VALUE, Integer.toString(0),
                Slots.RANK_PARAM_IS_DUPLICATES_ALLOWED, Const.ParamsNames.FEEDBACK_QUESTION_RANKISDUPLICATESALLOWED,
                Slots.RANK_ARE_DUPLICATES_ALLOWED_VALUE, Boolean.toString(isAreDuplicatesAllowed()),
                Slots.RANK_IS_MAX_OPTIONS_TO_BE_RANKED_ENABLED, isMaxOptionsToBeRankedEnabled ? "" : "disabled",
                Slots.RANK_PARAM_MAX_OPTIONS_TO_BE_RANKED, Const.ParamsNames.FEEDBACK_QUESTION_RANK_MAX_OPTIONS_TO_BE_RANKED,
                Slots.RANK_MAX_OPTIONS_TO_BE_RANKED,
                isMaxOptionsToBeRankedEnabled ? Integer.toString(Math.min(maxOptionsToBeRanked, totalNumRecipients)) : "",
                Slots.RANK_IS_MIN_OPTIONS_TO_BE_RANKED_ENABLED, isMinOptionsToBeRankedEnabled ? "" : "disabled",
                Slots.RANK_PARAM_MIN_OPTIONS_TO_BE_RANKED, Const.ParamsNames.FEEDBACK_QUESTION_RANK_MIN_OPTIONS_TO_BE_RANKED,
                Slots.RANK_MIN_OPTIONS_TO_BE_RANKED,
                isMinOptionsToBeRankedEnabled ? Integer.toString(Math.min(minOptionsToBeRanked, totalNumRecipients)) : ""
                );
    }

    @Override
    public String getQuestionWithoutExistingResponseSubmissionFormHtml(
            boolean sessionIsOpen, int qnIdx, int responseIdx, String courseId, int totalNumRecipients,
            StudentAttributes student) {

        StringBuilder optionListHtml = new StringBuilder();
        String optionFragmentTemplate = FormTemplates.RANK_SUBMISSION_FORM_OPTIONFRAGMENT;

        String optionFragment =
                Templates.populateTemplate(optionFragmentTemplate,
                        Slots.QUESTION_INDEX, Integer.toString(qnIdx),
                        Slots.RESPONSE_INDEX, Integer.toString(responseIdx),
                        Slots.OPTION_INDEX, "0",
                        Slots.DISABLED, sessionIsOpen ? "" : "disabled",
                        Slots.RANK_OPTION_VISIBILITY, "style=\"display:none\"",
                        Slots.OPTIONS, getSubmissionOptionsHtmlForRankingRecipients(
                                           totalNumRecipients, Const.INT_UNINITIALIZED),
                        Slots.FEEDBACK_RESPONSE_TEXT, Const.ParamsNames.FEEDBACK_RESPONSE_TEXT,
                        Slots.RANK_OPTION_VALUE, "");
        optionListHtml.append(optionFragment).append(System.lineSeparator());

        boolean isMinOptionsToBeRankedEnabled = minOptionsToBeRanked != NO_VALUE;
        boolean isMaxOptionsToBeRankedEnabled = maxOptionsToBeRanked != NO_VALUE;

        return Templates.populateTemplate(
                FormTemplates.RANK_SUBMISSION_FORM,
                Slots.RANK_SUBMISSION_FORM_OPTION_FRAGMENTS, optionListHtml.toString(),
                Slots.QUESTION_INDEX, Integer.toString(qnIdx),
                Slots.RESPONSE_INDEX, Integer.toString(responseIdx),
                Slots.RANK_OPTION_VISIBILITY, "style=\"display:none\"",
                Slots.RANK_TO_RECIPIENTS_VALUE, "true",
                Slots.RANK_PARAM_TO_RECIPIENT, Const.ParamsNames.FEEDBACK_QUESTION_RANKTORECIPIENTS,
                Slots.RANK_PARAM_NUM_OPTION, Const.ParamsNames.FEEDBACK_QUESTION_RANKNUMOPTIONS,
                Slots.RANK_NUM_OPTION_VALUE, Integer.toString(0),
                Slots.RANK_PARAM_IS_DUPLICATES_ALLOWED, Const.ParamsNames.FEEDBACK_QUESTION_RANKISDUPLICATESALLOWED,
                Slots.RANK_ARE_DUPLICATES_ALLOWED_VALUE, Boolean.toString(isAreDuplicatesAllowed()),
                Slots.RANK_IS_MAX_OPTIONS_TO_BE_RANKED_ENABLED, isMaxOptionsToBeRankedEnabled ? "" : "disabled",
                Slots.RANK_PARAM_MAX_OPTIONS_TO_BE_RANKED, Const.ParamsNames.FEEDBACK_QUESTION_RANK_MAX_OPTIONS_TO_BE_RANKED,
                Slots.RANK_MAX_OPTIONS_TO_BE_RANKED,
                isMaxOptionsToBeRankedEnabled ? Integer.toString(Math.min(maxOptionsToBeRanked, totalNumRecipients)) : "",
                Slots.RANK_IS_MIN_OPTIONS_TO_BE_RANKED_ENABLED, isMinOptionsToBeRankedEnabled ? "" : "disabled",
                Slots.RANK_PARAM_MIN_OPTIONS_TO_BE_RANKED, Const.ParamsNames.FEEDBACK_QUESTION_RANK_MIN_OPTIONS_TO_BE_RANKED,
                Slots.RANK_MIN_OPTIONS_TO_BE_RANKED,
                isMinOptionsToBeRankedEnabled ? Integer.toString(Math.min(minOptionsToBeRanked, totalNumRecipients)) : ""
                );
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

    private String getSubmissionOptionsHtmlForRankingRecipients(int totalNumRecipients, int rankGiven) {

        StringBuilder result = new StringBuilder(100);

        ElementTag option = PageData.createOption("", "", rankGiven == Const.INT_UNINITIALIZED);
        result.append("<option"
                     + option.getAttributesToString() + ">"
                     + option.getContent()
                     + "</option>");
        for (int i = 1; i <= totalNumRecipients; i++) {
            option = PageData.createOption(String.valueOf(i), String.valueOf(i), rankGiven == i);
            result.append("<option"
                        + option.getAttributesToString() + ">"
                        + option.getContent()
                        + "</option>");
        }

        return result.toString();
    }

    @Override
    public String getQuestionSpecificEditFormHtml(int questionNumber) {

        boolean isMinOptionsToBeRankedEnabled = minOptionsToBeRanked != NO_VALUE;
        boolean isMaxOptionsToBeRankedEnabled = maxOptionsToBeRanked != NO_VALUE;

        return Templates.populateTemplate(
                FormTemplates.RANK_EDIT_RECIPIENTS_FORM,
                Slots.QUESTION_NUMBER, Integer.toString(questionNumber),
                Slots.OPTION_RECIPIENT_DISPLAY_NAME, "recipient",
                Slots.RANK_PARAM_IS_DUPLICATES_ALLOWED,
                        Const.ParamsNames.FEEDBACK_QUESTION_RANKISDUPLICATESALLOWED,
                Slots.RANK_ARE_DUPLICATES_ALLOWED_CHECKED, isAreDuplicatesAllowed() ? "checked" : "",
                Slots.RANK_PARAM_MIN_RECIPIENTS_CHECKBOX,
                Const.ParamsNames.FEEDBACK_QUESTION_RANK_IS_MIN_RECIPIENTS_TO_BE_RANKED_ENABLED,
                Slots.RANK_PARAM_MIN_RECIPIENTS_TO_BE_RANKED,
                Const.ParamsNames.FEEDBACK_QUESTION_RANK_MIN_RECIPIENTS_TO_BE_RANKED,
                Slots.RANK_MIN_RECIPIENTS_TO_BE_RANKED,
                isMinOptionsToBeRankedEnabled ? Integer.toString(minOptionsToBeRanked) : "",
                Slots.RANK_IS_MIN_RECIPIENTS_TO_BE_RANKED_ENABLED, isMinOptionsToBeRankedEnabled ? "checked" : "",
                Slots.RANK_PARAM_MAX_RECIPIENTS_CHECKBOX,
                        Const.ParamsNames.FEEDBACK_QUESTION_RANK_IS_MAX_RECIPIENTS_TO_BE_RANKED_ENABLED,
                Slots.RANK_PARAM_MAX_RECIPIENTS_TO_BE_RANKED,
                        Const.ParamsNames.FEEDBACK_QUESTION_RANK_MAX_RECIPIENTS_TO_BE_RANKED,
                Slots.RANK_MAX_RECIPIENTS_TO_BE_RANKED,
                        isMaxOptionsToBeRankedEnabled ? Integer.toString(maxOptionsToBeRanked) : "",
                Slots.RANK_IS_MAX_RECIPIENTS_TO_BE_RANKED_ENABLED, isMaxOptionsToBeRankedEnabled ? "checked" : "");

    }

    @Override
    public String getNewQuestionSpecificEditFormHtml() {

        return "<div id=\"rankRecipientsForm\">"
                + this.getQuestionSpecificEditFormHtml(-1)
                + "</div>";
    }

    @Override
    public String getQuestionAdditionalInfoHtml(int questionNumber,
            String additionalInfoId) {
        String additionalInfo = this.getQuestionTypeDisplayName() + "<br>";

        return Templates.populateTemplate(
                        FormTemplates.FEEDBACK_QUESTION_ADDITIONAL_INFO,
                        Slots.MORE, "[more]",
                        Slots.LESS, "[less]",
                        Slots.QUESTION_NUMBER, Integer.toString(questionNumber),
                        Slots.ADDITIONAL_INFO_ID, additionalInfoId,
                        Slots.QUESTION_ADDITIONAL_INFO, additionalInfo);
    }

    /**
     * Constructs results statistics for each student.
     * Statistics to student is only shown when visibility setting is permissive enough
     * i.e., if the ranks for all team members are in the results bundle.
     */
    private String getStudentQuestionResultsStatisticsHtml(String studentEmail, FeedbackQuestionAttributes question,
            FeedbackSessionResultsBundle bundle) {
        // at least should be able to be viewed by other students for team recipient
        if (question.recipientType.isTeam()
                    && (!question.showResponsesTo.contains(FeedbackParticipantType.RECEIVER)
                    || !question.showResponsesTo.contains(FeedbackParticipantType.STUDENTS))) {
            return "";
        }
        // at least should be able to be viewed by own team members for non-team recipient
        if (!question.recipientType.isTeam()
                    && (!question.showResponsesTo.contains(FeedbackParticipantType.RECEIVER)
                    || !question.showResponsesTo.contains(FeedbackParticipantType.OWN_TEAM_MEMBERS))) {
            return "";
        }

        List<FeedbackResponseAttributes> allResponses = bundle.getActualUnsortedResponses(question);
        Map<String, List<Integer>> recipientRanks = generateOptionRanksMapping(allResponses);

        boolean isRecipientTypeTeam = question.recipientType == FeedbackParticipantType.TEAMS
                || question.recipientType == FeedbackParticipantType.OWN_TEAM;

        String currentUserTeam = bundle.roster.getStudentForEmail(studentEmail).getTeam();
        String currentUserIdentifier = isRecipientTypeTeam ? currentUserTeam : studentEmail;

        List<Integer> ranksReceived = recipientRanks.get(currentUserIdentifier);
        // If response recipient is instructor, responses for current student/team will not exist.
        if (ranksReceived == null) {
            return "";
        }

        Map<String, Integer> recipientOverallRank = generateNormalizedOverallRankMapping(recipientRanks);

        Map<String, List<Integer>> recipientRanksExcludingSelf = getRecipientRanksExcludingSelf(allResponses);

        Map<String, Integer> recipientOverallRankExceptSelf =
                generateNormalizedOverallRankMapping(recipientRanksExcludingSelf);

        Map<String, Integer> recipientSelfRanks = generateSelfRankForEachRecipient(allResponses);

        String ranksReceivedAsString = getListOfRanksReceivedAsString(ranksReceived);
        String overallRank = Integer.toString(recipientOverallRank.get(currentUserIdentifier));
        String name = bundle.getNameForEmail(currentUserIdentifier);

        String overallRankExceptSelf = recipientOverallRankExceptSelf.containsKey(currentUserIdentifier)
                ? Integer.toString(recipientOverallRankExceptSelf.get(currentUserIdentifier)) : "-";
        String selfRank = recipientSelfRanks.containsKey(currentUserIdentifier)
                ? Integer.toString(recipientSelfRanks.get(currentUserIdentifier)) : "-";

        String fragments = Templates.populateTemplate(FormTemplates.RANK_RESULT_STATS_RECIPIENTFRAGMENT,
                Slots.RANK_OPTION_VALUE, SanitizationHelper.sanitizeForHtml(name),
                Slots.TEAM, SanitizationHelper.sanitizeForHtml(currentUserTeam),
                Slots.RANK_RECIEVED, ranksReceivedAsString,
                Slots.RANK_SELF, selfRank,
                Slots.RANK_OVERALL, overallRank,
                Slots.RANK_EXCLUDING_SELF_OVERALL, overallRankExceptSelf);

        String statsTitle = isRecipientTypeTeam ? "Summary of responses received by your team"
                : "Summary of responses received by you";
        return Templates.populateTemplate(FormTemplates.RANK_RESULT_RECIPIENT_STATS,
                Slots.SUMMARY_TITLE, statsTitle,
                Slots.RANK_OPTION_RECIPIENT_DISPLAY_NAME, "Recipient",
                Slots.FRAGMENTS, fragments);
    }

    @Override
    public String getQuestionResultStatisticsHtml(
                        List<FeedbackResponseAttributes> responses,
                        FeedbackQuestionAttributes question,
                        String studentEmail,
                        FeedbackSessionResultsBundle bundle,
                        String view) {

        if (responses.isEmpty()) {
            return "";
        }
        if ("student".equals(view)) {
            return getStudentQuestionResultsStatisticsHtml(studentEmail, question, bundle);
        }
        return getInstructorQuestionResultsStatisticsHtml(responses, bundle);
    }

    private String getInstructorQuestionResultsStatisticsHtml(
            List<FeedbackResponseAttributes> responses,
            FeedbackSessionResultsBundle bundle) {
        StringBuilder fragments = new StringBuilder();

        Map<String, List<Integer>> recipientRanks = generateOptionRanksMapping(responses);

        Map<String, Integer> recipientOverallRank = generateNormalizedOverallRankMapping(recipientRanks);

        Map<String, List<Integer>> recipientRanksExcludingSelf = getRecipientRanksExcludingSelf(responses);

        Map<String, Integer> recipientOverallRankExceptSelf =
                generateNormalizedOverallRankMapping(recipientRanksExcludingSelf);

        Map<String, Integer> recipientSelfRanks = generateSelfRankForEachRecipient(responses);

        String fragmentTemplateToUse = FormTemplates.RANK_RESULT_STATS_RECIPIENTFRAGMENT;
        String templateToUse = FormTemplates.RANK_RESULT_RECIPIENT_STATS;

        recipientRanks.forEach((participantIdentifier, ranks) -> {

            String ranksReceived = getListOfRanksReceivedAsString(ranks);
            String overallRank = Integer.toString(recipientOverallRank.get(participantIdentifier));
            String name = bundle.getNameForEmail(participantIdentifier);
            String teamName = bundle.getTeamNameForEmail(participantIdentifier);
            String overallRankExceptSelf = recipientOverallRankExceptSelf.containsKey(participantIdentifier)
                    ? Integer.toString(recipientOverallRankExceptSelf.get(participantIdentifier)) : "-";
            String selfRank = recipientSelfRanks.containsKey(participantIdentifier)
                    ? Integer.toString(recipientSelfRanks.get(participantIdentifier)) : "-";

            fragments.append(Templates.populateTemplate(fragmentTemplateToUse,
                    Slots.RANK_OPTION_VALUE, SanitizationHelper.sanitizeForHtml(name),
                    Slots.TEAM, SanitizationHelper.sanitizeForHtml(teamName),
                    Slots.RANK_RECIEVED, ranksReceived,
                    Slots.RANK_SELF, selfRank,
                    Slots.RANK_OVERALL, overallRank,
                    Slots.RANK_EXCLUDING_SELF_OVERALL, overallRankExceptSelf));

        });

        return Templates.populateTemplate(templateToUse,
                Slots.SUMMARY_TITLE, "Response Summary",
                Slots.RANK_OPTION_RECIPIENT_DISPLAY_NAME, "Recipient",
                Slots.FRAGMENTS, fragments.toString());

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
    public String getQuestionTypeChoiceOption() {
        return "<li data-questiontype = \"" + FeedbackQuestionType.RANK_RECIPIENTS.name() + "\"><a href=\"javascript:;\">"
              + Const.FeedbackQuestionTypeNames.RANK_RECIPIENT + "</a></li>";
    }

    @Override
    public List<String> validateQuestionDetails(String courseId) {
        return new ArrayList<>();
    }

    @Override
    public List<String> validateResponseAttributes(
            List<FeedbackResponseAttributes> responses,
            int numRecipients) {
        if (responses.isEmpty()) {
            return new ArrayList<>();
        }

        if (isAreDuplicatesAllowed()) {
            return new ArrayList<>();
        }
        List<String> errors = new ArrayList<>();

        Set<Integer> responseRank = new HashSet<>();
        for (FeedbackResponseAttributes response : responses) {
            FeedbackRankRecipientsResponseDetails frd =
                    (FeedbackRankRecipientsResponseDetails) response.getResponseDetails();

            if (responseRank.contains(frd.answer)) {
                errors.add("Duplicate rank " + frd.answer + " in question");
            } else if (frd.answer > numRecipients) {
                errors.add("Invalid rank " + frd.answer + " in question");
            }
            responseRank.add(frd.answer);
        }

        return errors;
    }

    @Override
    public Comparator<InstructorFeedbackResultsResponseRow> getResponseRowsSortOrder() {
        return Comparator.comparing(InstructorFeedbackResultsResponseRow::getGiverTeam)
                .thenComparing(InstructorFeedbackResultsResponseRow::getGiverDisplayableIdentifier)
                .thenComparing(InstructorFeedbackResultsResponseRow::getDisplayableResponse)
                .thenComparing(InstructorFeedbackResultsResponseRow::getRecipientTeam)
                .thenComparing(InstructorFeedbackResultsResponseRow::getRecipientDisplayableIdentifier);
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
