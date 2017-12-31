package teammates.common.datatransfer.questions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.util.Assumption;
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

public class FeedbackRankOptionsQuestionDetails extends FeedbackRankQuestionDetails {
    public static final transient int MIN_NUM_OF_OPTIONS = 2;
    public static final transient String ERROR_NOT_ENOUGH_OPTIONS =
            "Too little options for " + Const.FeedbackQuestionTypeNames.RANK_OPTION
            + ". Minimum number of options is: ";

    List<String> options;

    public FeedbackRankOptionsQuestionDetails() {
        super(FeedbackQuestionType.RANK_OPTIONS);

        this.options = new ArrayList<>();
    }

    @Override
    public List<String> getInstructions() {
        List<String> instructions = new ArrayList<>();

        if (minOptionsToBeRanked != Integer.MIN_VALUE) {
            instructions.add("You need to rank at least " + minOptionsToBeRanked + " options.");
        }

        if (maxOptionsToBeRanked != Integer.MIN_VALUE) {
            instructions.add("Rank no more than " + maxOptionsToBeRanked + " options.");
        }

        return instructions;
    }

    @Override
    public boolean extractQuestionDetails(Map<String, String[]> requestParameters,
                                          FeedbackQuestionType questionType) {
        super.extractQuestionDetails(requestParameters, questionType);
        List<String> options = new ArrayList<>();

        String numOptionsCreatedString =
                HttpRequestHelper.getValueFromParamMap(
                        requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED);
        Assumption.assertNotNull("Null number of choice for Rank", numOptionsCreatedString);
        int numOptionsCreated = Integer.parseInt(numOptionsCreatedString);

        for (int i = 0; i < numOptionsCreated; i++) {
            String rankOption = HttpRequestHelper.getValueFromParamMap(
                    requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_RANKOPTION + "-" + i);
            if (rankOption != null && !rankOption.trim().isEmpty()) {
                options.add(rankOption);
            }
        }

        this.initialiseQuestionDetails(options);

        String minOptionsToBeRanked = HttpRequestHelper.getValueFromParamMap(
                requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_RANKMINOPTIONSTOBERANKED);
        String maxOptionsToBeRanked = HttpRequestHelper.getValueFromParamMap(
                requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_RANKMAXOPTIONSTOBERANKED);

        if (minOptionsToBeRanked != null) {
            this.minOptionsToBeRanked = Integer.parseInt(minOptionsToBeRanked);
        }

        if (maxOptionsToBeRanked != null) {
            this.maxOptionsToBeRanked = Integer.parseInt(maxOptionsToBeRanked);
        }

        return true;
    }

    private void initialiseQuestionDetails(List<String> options) {
        this.options = options;
    }

    @Override
    public String getQuestionTypeDisplayName() {
        return Const.FeedbackQuestionTypeNames.RANK_OPTION;
    }

    @Override
    public String getQuestionWithExistingResponseSubmissionFormHtml(
                        boolean sessionIsOpen, int qnIdx, int responseIdx, String courseId,
                        int totalNumRecipients,
                        FeedbackResponseDetails existingResponseDetails) {

        FeedbackRankOptionsResponseDetails existingResponse = (FeedbackRankOptionsResponseDetails) existingResponseDetails;
        StringBuilder optionListHtml = new StringBuilder();
        String optionFragmentTemplate = FormTemplates.RANK_SUBMISSION_FORM_OPTIONFRAGMENT;

        for (int i = 0; i < options.size(); i++) {
            String optionFragment =
                    Templates.populateTemplate(optionFragmentTemplate,
                            Slots.QUESTION_INDEX, Integer.toString(qnIdx),
                            Slots.RESPONSE_INDEX, Integer.toString(responseIdx),
                            Slots.OPTION_INDEX, Integer.toString(i),
                            Slots.DISABLED, sessionIsOpen ? "" : "disabled",
                            Slots.RANK_OPTION_VISIBILITY, "",
                            Slots.OPTIONS,
                                    getSubmissionOptionsHtmlForRankingOptions(existingResponse.getAnswerList().get(i)),
                            Slots.FEEDBACK_RESPONSE_TEXT, Const.ParamsNames.FEEDBACK_RESPONSE_TEXT,
                            Slots.RANK_OPTION_VALUE, SanitizationHelper.sanitizeForHtml(options.get(i)));
            optionListHtml.append(optionFragment).append(Const.EOL);

        }

        boolean isMinOptionsToBeRankedEnabled = minOptionsToBeRanked != Integer.MIN_VALUE;
        boolean isMaxOptionsToBeRankedEnabled = maxOptionsToBeRanked != Integer.MIN_VALUE;

        return Templates.populateTemplate(
                FormTemplates.RANK_SUBMISSION_FORM,
                Slots.RANK_SUBMISSION_FORM_OPTION_FRAGMENTS, optionListHtml.toString(),
                Slots.QUESTION_INDEX, Integer.toString(qnIdx),
                Slots.RESPONSE_INDEX, Integer.toString(responseIdx),
                Slots.RANK_OPTION_VISIBILITY, "",
                Slots.RANK_PARAM_TO_RECIPIENT, Const.ParamsNames.FEEDBACK_QUESTION_RANKTORECIPIENTS,
                Slots.RANK_TO_RECIPIENTS_VALUE, "false",
                Slots.RANK_PARAM_NUM_OPTION, Const.ParamsNames.FEEDBACK_QUESTION_RANKNUMOPTIONS,
                Slots.RANK_NUM_OPTION_VALUE, Integer.toString(options.size()),
                Slots.RANK_PARAM_IS_DUPLICATES_ALLOWED, Const.ParamsNames.FEEDBACK_QUESTION_RANKISDUPLICATESALLOWED,
                Slots.RANK_ARE_DUPLICATES_ALLOWED_VALUE, Boolean.toString(isAreDuplicatesAllowed()),
                Slots.RANK_IS_MAX_OPTIONS_TO_BE_RANKED_ENABLED, isMaxOptionsToBeRankedEnabled ? "" : "disabled",
                Slots.RANK_PARAM_MAX_OPTIONS_TO_BE_RANKED, Const.ParamsNames.FEEDBACK_QUESTION_RANKMAXOPTIONSTOBERANKED,
                Slots.RANK_MAX_OPTIONS_TO_BE_RANKED, isMaxOptionsToBeRankedEnabled
                        ? Integer.toString(maxOptionsToBeRanked) : "",
                Slots.RANK_IS_MIN_OPTIONS_TO_BE_RANKED_ENABLED, isMinOptionsToBeRankedEnabled ? "" : "disabled",
                Slots.RANK_PARAM_MIN_OPTIONS_TO_BE_RANKED, Const.ParamsNames.FEEDBACK_QUESTION_RANKMINOPTIONSTOBERANKED,
                Slots.RANK_MIN_OPTIONS_TO_BE_RANKED, isMinOptionsToBeRankedEnabled
                        ? Integer.toString(minOptionsToBeRanked) : ""
                );
    }

    @Override
    public String getQuestionWithoutExistingResponseSubmissionFormHtml(
            boolean sessionIsOpen, int qnIdx, int responseIdx, String courseId, int totalNumRecipients) {

        StringBuilder optionListHtml = new StringBuilder();
        String optionFragmentTemplate = FormTemplates.RANK_SUBMISSION_FORM_OPTIONFRAGMENT;

        for (int i = 0; i < options.size(); i++) {
            String optionFragment =
                    Templates.populateTemplate(optionFragmentTemplate,
                            Slots.QUESTION_INDEX, Integer.toString(qnIdx),
                            Slots.RESPONSE_INDEX, Integer.toString(responseIdx),
                            Slots.OPTION_INDEX, Integer.toString(i),
                            Slots.DISABLED, sessionIsOpen ? "" : "disabled",
                            Slots.RANK_OPTION_VISIBILITY, "",
                            Slots.OPTIONS, getSubmissionOptionsHtmlForRankingOptions(Const.INT_UNINITIALIZED),
                            Slots.FEEDBACK_RESPONSE_TEXT, Const.ParamsNames.FEEDBACK_RESPONSE_TEXT,
                            Slots.RANK_OPTION_VALUE, SanitizationHelper.sanitizeForHtml(options.get(i)));
            optionListHtml.append(optionFragment).append(Const.EOL);
        }

        boolean isMinOptionsToBeRankedEnabled = minOptionsToBeRanked != Integer.MIN_VALUE;
        boolean isMaxOptionsToBeRankedEnabled = maxOptionsToBeRanked != Integer.MIN_VALUE;

        return Templates.populateTemplate(
                FormTemplates.RANK_SUBMISSION_FORM,
                Slots.RANK_SUBMISSION_FORM_OPTION_FRAGMENTS, optionListHtml.toString(),
                Slots.QUESTION_INDEX, Integer.toString(qnIdx),
                Slots.RESPONSE_INDEX, Integer.toString(responseIdx),
                Slots.RANK_OPTION_VISIBILITY, "",
                Slots.RANK_TO_RECIPIENTS_VALUE, "false",
                Slots.RANK_PARAM_TO_RECIPIENT, Const.ParamsNames.FEEDBACK_QUESTION_RANKTORECIPIENTS,
                Slots.RANK_PARAM_NUM_OPTION, Const.ParamsNames.FEEDBACK_QUESTION_RANKNUMOPTIONS,
                Slots.RANK_NUM_OPTION_VALUE, Integer.toString(options.size()),
                Slots.RANK_PARAM_IS_DUPLICATES_ALLOWED, Const.ParamsNames.FEEDBACK_QUESTION_RANKISDUPLICATESALLOWED,
                Slots.RANK_ARE_DUPLICATES_ALLOWED_VALUE, Boolean.toString(isAreDuplicatesAllowed()),
                Slots.RANK_ARE_DUPLICATES_ALLOWED_VALUE, Boolean.toString(isAreDuplicatesAllowed()),
                Slots.RANK_IS_MAX_OPTIONS_TO_BE_RANKED_ENABLED, isMaxOptionsToBeRankedEnabled ? "" : "disabled",
                Slots.RANK_PARAM_MAX_OPTIONS_TO_BE_RANKED, Const.ParamsNames.FEEDBACK_QUESTION_RANKMAXOPTIONSTOBERANKED,
                Slots.RANK_MAX_OPTIONS_TO_BE_RANKED, isMaxOptionsToBeRankedEnabled
                        ? Integer.toString(maxOptionsToBeRanked) : "",
                Slots.RANK_IS_MIN_OPTIONS_TO_BE_RANKED_ENABLED, isMinOptionsToBeRankedEnabled ? "" : "disabled",
                Slots.RANK_PARAM_MIN_OPTIONS_TO_BE_RANKED, Const.ParamsNames.FEEDBACK_QUESTION_RANKMINOPTIONSTOBERANKED,
                Slots.RANK_MIN_OPTIONS_TO_BE_RANKED, isMinOptionsToBeRankedEnabled
                        ? Integer.toString(minOptionsToBeRanked) : ""
                );
    }

    private String getSubmissionOptionsHtmlForRankingOptions(int rankGiven) {
        StringBuilder result = new StringBuilder(100);

        ElementTag option = PageData.createOption("", "", rankGiven == Const.INT_UNINITIALIZED);
        result.append("<option"
                     + option.getAttributesToString() + ">"
                     + option.getContent()
                     + "</option>");
        for (int i = 1; i <= options.size(); i++) {
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
        StringBuilder optionListHtml = new StringBuilder();
        String optionFragmentTemplate = FormTemplates.RANK_EDIT_FORM_OPTIONFRAGMENT;

        for (int i = 0; i < options.size(); i++) {
            String optionFragment =
                    Templates.populateTemplate(optionFragmentTemplate,
                            Slots.ITERATOR, Integer.toString(i),
                            Slots.RANK_OPTION_VALUE, SanitizationHelper.sanitizeForHtml(options.get(i)),
                            Slots.RANK_PARAM_OPTION, Const.ParamsNames.FEEDBACK_QUESTION_RANKOPTION);

            optionListHtml.append(optionFragment).append(Const.EOL);
        }

        boolean isMinOptionsToBeRankedEnabled = minOptionsToBeRanked != Integer.MIN_VALUE;
        boolean isMaxOptionsToBeRankedEnabled = maxOptionsToBeRanked != Integer.MIN_VALUE;

        return Templates.populateTemplate(
                FormTemplates.RANK_EDIT_OPTIONS_FORM,
                Slots.RANK_EDIT_FORM_OPTION_FRAGMENTS, optionListHtml.toString(),
                Slots.QUESTION_NUMBER, Integer.toString(questionNumber),
                Slots.RANK_PARAM_NUMBER_OF_CHOICE_CREATED, Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED,
                Slots.RANK_NUM_OPTIONS, String.valueOf(options.size()),
                Slots.RANK_OPTION_RECIPIENT_DISPLAY_NAME, "option",
                Slots.RANK_PARAM_IS_DUPLICATES_ALLOWED, Const.ParamsNames.FEEDBACK_QUESTION_RANKISDUPLICATESALLOWED,
                Slots.RANK_IS_MIN_OPTIONS_TO_BE_RANKED_ENABLED, isMinOptionsToBeRankedEnabled ? "checked" : "",
                Slots.RANK_PARAM_MIN_OPTIONS_CHECKBOX, Const.ParamsNames.FEEDBACK_QUESTION_RANKISMINOPTIONSTOBERANKEDENABLED,
                Slots.RANK_PARAM_MIN_OPTIONS_TO_BE_RANKED, Const.ParamsNames.FEEDBACK_QUESTION_RANKMINOPTIONSTOBERANKED,
                Slots.RANK_MIN_OPTIONS_TO_BE_RANKED, isMinOptionsToBeRankedEnabled
                        ? Integer.toString(minOptionsToBeRanked) : "1",
                Slots.RANK_IS_MAX_OPTIONS_TO_BE_RANKED_ENABLED, isMaxOptionsToBeRankedEnabled ? "checked" : "",
                Slots.RANK_PARAM_MAX_OPTIONS_CHECKBOX, Const.ParamsNames.FEEDBACK_QUESTION_RANKISMAXOPTIONSTOBERANKEDENABLED,
                Slots.RANK_PARAM_MAX_OPTIONS_TO_BE_RANKED, Const.ParamsNames.FEEDBACK_QUESTION_RANKMAXOPTIONSTOBERANKED,
                Slots.RANK_MAX_OPTIONS_TO_BE_RANKED, isMaxOptionsToBeRankedEnabled
                        ? Integer.toString(maxOptionsToBeRanked) : "1",
                Slots.RANK_ARE_DUPLICATES_ALLOWED_CHECKED, isAreDuplicatesAllowed() ? "checked" : "");

    }

    @Override
    public String getNewQuestionSpecificEditFormHtml() {
        // Add two empty options by default
        this.options.add("");
        this.options.add("");

        return "<div id=\"rankOptionsForm\">"
              + this.getQuestionSpecificEditFormHtml(-1)
              + "</div>";
    }

    @Override
    public String getQuestionAdditionalInfoHtml(int questionNumber,
            String additionalInfoId) {
        StringBuilder optionListHtml = new StringBuilder(100);
        String optionFragmentTemplate = FormTemplates.MSQ_ADDITIONAL_INFO_FRAGMENT;
        String additionalInfo = "";

        optionListHtml.append("<ul style=\"list-style-type: disc;margin-left: 20px;\" >");
        for (String option : options) {
            String optionFragment =
                    Templates.populateTemplate(optionFragmentTemplate,
                            Slots.MSQ_CHOICE_VALUE, option);

            optionListHtml.append(optionFragment);
        }

        optionListHtml.append("</ul>");
        additionalInfo = Templates.populateTemplate(
            FormTemplates.MSQ_ADDITIONAL_INFO,
            Slots.QUESTION_TYPE_NAME, this.getQuestionTypeDisplayName(),
            Slots.MSQ_ADDITIONAL_INFO_FRAGMENTS, optionListHtml.toString());

        return Templates.populateTemplate(
                FormTemplates.FEEDBACK_QUESTION_ADDITIONAL_INFO,
                Slots.MORE, "[more]",
                Slots.LESS, "[less]",
                Slots.QUESTION_NUMBER, Integer.toString(questionNumber),
                Slots.ADDITIONAL_INFO_ID, additionalInfoId,
                Slots.QUESTION_ADDITIONAL_INFO, additionalInfo);
    }

    @Override
    public String getQuestionResultStatisticsHtml(
                        List<FeedbackResponseAttributes> responses,
                        FeedbackQuestionAttributes question,
                        String studentEmail,
                        FeedbackSessionResultsBundle bundle,
                        String view) {

        if ("student".equals(view) || responses.isEmpty()) {
            return "";
        }

        StringBuilder fragments = new StringBuilder(100);

        Map<String, List<Integer>> optionRanks = generateOptionRanksMapping(responses);

        DecimalFormat df = new DecimalFormat("#.##");

        optionRanks.forEach((option, ranks) -> {

            double average = computeAverage(ranks);
            String ranksReceived = getListOfRanksReceivedAsString(ranks);

            fragments.append(Templates.populateTemplate(FormTemplates.RANK_RESULT_STATS_OPTIONFRAGMENT,
                    Slots.RANK_OPTION_VALUE, SanitizationHelper.sanitizeForHtml(option),
                    Slots.RANK_RECIEVED, ranksReceived,
                    Slots.RANK_AVERAGE, df.format(average)));

        });

        return Templates.populateTemplate(FormTemplates.RANK_RESULT_OPTION_STATS,
                Slots.OPTION_RECIPIENT_DISPLAY_NAME, "Option",
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
        Map<String, List<Integer>> optionRanks = generateOptionRanksMapping(responses);

        DecimalFormat df = new DecimalFormat("#.##");

        optionRanks.forEach((key, ranksAssigned) -> {
            String option = SanitizationHelper.sanitizeForCsv(key);

            double average = computeAverage(ranksAssigned);
            String fragment = option + "," + df.format(average) + ","
                    + StringHelper.join(",", ranksAssigned) + Const.EOL;
            fragments.append(fragment);
        });

        return "Option, Average Rank, Ranks Received" + Const.EOL + fragments.toString() + Const.EOL;
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

            for (int i = 0; i < options.size(); i++) {
                String optionReceivingRanks = options.get(i);
                int rankReceived = normalisedRankForOption.get(optionReceivingRanks);

                if (rankReceived != Const.POINTS_NOT_SUBMITTED) {
                    updateOptionRanksMapping(optionRanks, optionReceivingRanks, rankReceived);
                }
            }
        }
        return optionRanks;
    }

    @Override
    public boolean isChangesRequiresResponseDeletion(FeedbackQuestionDetails newDetails) {
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
    public String getQuestionTypeChoiceOption() {
        return "<li data-questiontype = \"" + FeedbackQuestionType.RANK_OPTIONS.name() + "\">"
                 + "<a href=\"javascript:;\">" + Const.FeedbackQuestionTypeNames.RANK_OPTION + "</a>"
             + "</li>";
    }

    @Override
    public List<String> validateQuestionDetails() {
        List<String> errors = new ArrayList<>();
        if (options.size() < MIN_NUM_OF_OPTIONS) {
            errors.add(ERROR_NOT_ENOUGH_OPTIONS + MIN_NUM_OF_OPTIONS + ".");
        }
        return errors;
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

        for (FeedbackResponseAttributes response : responses) {
            FeedbackRankOptionsResponseDetails frd = (FeedbackRankOptionsResponseDetails) response.getResponseDetails();
            Set<Integer> responseRank = new HashSet<>();

            for (int answer : frd.getFilteredSortedAnswerList()) {
                if (responseRank.contains(answer)) {
                    errors.add("Duplicate rank " + answer);
                }
                responseRank.add(answer);
            }
        }

        return errors;
    }

    @Override
    public Comparator<InstructorFeedbackResultsResponseRow> getResponseRowsSortOrder() {
        return null;
    }

    @Override
    public String validateGiverRecipientVisibility(FeedbackQuestionAttributes feedbackQuestionAttributes) {
        return "";
    }

}
