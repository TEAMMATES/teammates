package teammates.common.datatransfer.questions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StringHelper;
import teammates.common.util.Templates;
import teammates.common.util.Templates.FeedbackQuestion.FormTemplates;
import teammates.common.util.Templates.FeedbackQuestion.Slots;
import teammates.logic.core.FeedbackQuestionsLogic;
import teammates.ui.template.InstructorFeedbackResultsResponseRow;

public class FeedbackConstantSumQuestionDetails extends FeedbackQuestionDetails {
    private int numOfConstSumOptions;
    private List<String> constSumOptions;
    private boolean distributeToRecipients;
    private boolean pointsPerOption;
    private boolean forceUnevenDistribution;
    private int points;

    public FeedbackConstantSumQuestionDetails() {
        super(FeedbackQuestionType.CONSTSUM);

        this.numOfConstSumOptions = 0;
        this.constSumOptions = new ArrayList<>();
        this.distributeToRecipients = false;
        this.pointsPerOption = false;
        this.points = 100;
        this.forceUnevenDistribution = false;
    }

    public FeedbackConstantSumQuestionDetails(String questionText,
            List<String> constSumOptions,
            boolean pointsPerOption, int points, boolean unevenDistribution) {
        super(FeedbackQuestionType.CONSTSUM, questionText);

        this.numOfConstSumOptions = constSumOptions.size();
        this.constSumOptions = constSumOptions;
        this.distributeToRecipients = false;
        this.pointsPerOption = pointsPerOption;
        this.points = points;
        this.forceUnevenDistribution = unevenDistribution;

    }

    @Override
    public boolean extractQuestionDetails(
            Map<String, String[]> requestParameters,
            FeedbackQuestionType questionType) {

        String distributeToRecipientsString =
                HttpRequestHelper.getValueFromParamMap(requestParameters,
                                                       Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMTORECIPIENTS);
        String pointsPerOptionString =
                HttpRequestHelper.getValueFromParamMap(requestParameters,
                                                       Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSPEROPTION);
        String pointsString =
                HttpRequestHelper.getValueFromParamMap(requestParameters,
                                                       Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTS);
        String pointsForEachOptionString =
                HttpRequestHelper.getValueFromParamMap(requestParameters,
                                                       Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSFOREACHOPTION);
        String pointsForEachRecipientString =
                HttpRequestHelper.getValueFromParamMap(requestParameters,
                                                       Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSFOREACHRECIPIENT);

        Assumption.assertNotNull("Null points in total", pointsString);
        Assumption.assertNotNull("Null points for each option", pointsForEachOptionString);
        Assumption.assertNotNull("Null points for each recipient", pointsForEachRecipientString);
        String forceUnevenDistributionString =
                HttpRequestHelper.getValueFromParamMap(requestParameters,
                                                       Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMDISTRIBUTEUNEVENLY);

        boolean distributeToRecipients = "true".equals(distributeToRecipientsString);
        boolean pointsPerOption = "true".equals(pointsPerOptionString);

        int points = 0;
        if (pointsPerOption) {
            points = distributeToRecipients ? Integer.parseInt(pointsForEachRecipientString)
                                            : Integer.parseInt(pointsForEachOptionString);
        } else {
            points = Integer.parseInt(pointsString);
        }
        boolean forceUnevenDistribution = "on".equals(forceUnevenDistributionString);

        if (distributeToRecipients) {
            this.setConstantSumQuestionDetails(pointsPerOption, points, forceUnevenDistribution);
        } else {
            String numConstSumOptionsCreatedString =
                    HttpRequestHelper.getValueFromParamMap(requestParameters,
                                                           Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED);
            Assumption.assertNotNull("Null number of choice for ConstSum", numConstSumOptionsCreatedString);
            int numConstSumOptionsCreated = Integer.parseInt(numConstSumOptionsCreatedString);

            for (int i = 0; i < numConstSumOptionsCreated; i++) {
                String constSumOption =
                        HttpRequestHelper.getValueFromParamMap(
                                requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMOPTION + "-" + i);
                if (constSumOption != null && !constSumOption.trim().isEmpty()) {
                    constSumOptions.add(constSumOption);
                    numOfConstSumOptions++;
                }
            }
            this.setConstantSumQuestionDetails(constSumOptions, pointsPerOption, points, forceUnevenDistribution);
        }
        return true;
    }

    private void setConstantSumQuestionDetails(
            List<String> constSumOptions, boolean pointsPerOption,
            int points, boolean unevenDistribution) {

        this.numOfConstSumOptions = constSumOptions.size();
        this.constSumOptions = constSumOptions;
        this.distributeToRecipients = false;
        this.pointsPerOption = pointsPerOption;
        this.points = points;
        this.forceUnevenDistribution = unevenDistribution;

    }

    private void setConstantSumQuestionDetails(boolean pointsPerOption,
            int points, boolean unevenDistribution) {

        this.numOfConstSumOptions = 0;
        this.constSumOptions = new ArrayList<>();
        this.distributeToRecipients = true;
        this.pointsPerOption = pointsPerOption;
        this.points = points;
        this.forceUnevenDistribution = unevenDistribution;
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
    public String getQuestionWithExistingResponseSubmissionFormHtml(
            boolean sessionIsOpen, int qnIdx, int responseIdx, String courseId,
            int totalNumRecipients,
            FeedbackResponseDetails existingResponseDetails) {

        FeedbackConstantSumResponseDetails existingConstSumResponse =
                (FeedbackConstantSumResponseDetails) existingResponseDetails;
        StringBuilder optionListHtml = new StringBuilder();
        String optionFragmentTemplate = FormTemplates.CONSTSUM_SUBMISSION_FORM_OPTIONFRAGMENT;

        if (distributeToRecipients) {
            String optionFragment =
                    Templates.populateTemplate(optionFragmentTemplate,
                            Slots.QUESTION_INDEX, Integer.toString(qnIdx),
                            Slots.RESPONSE_INDEX, Integer.toString(responseIdx),
                            Slots.OPTION_INDEX, "0",
                            Slots.DISABLED, sessionIsOpen ? "" : "disabled",
                            Slots.CONSTSUM_OPTION_VISIBILITY, "style=\"display:none\"",
                            Slots.MARGIN_LEFT, "",
                            Slots.CONSTSUM_OPTION_POINT, existingConstSumResponse.getAnswerString(),
                            Slots.FEEDBACK_RESPONSE_TEXT, Const.ParamsNames.FEEDBACK_RESPONSE_TEXT,
                            Slots.CONSTSUM_OPTION_VALUE, "");
            optionListHtml.append(optionFragment).append(Const.EOL);
        } else {
            for (int i = 0; i < constSumOptions.size(); i++) {
                String optionFragment =
                        Templates.populateTemplate(optionFragmentTemplate,
                                Slots.QUESTION_INDEX, Integer.toString(qnIdx),
                                Slots.RESPONSE_INDEX, Integer.toString(responseIdx),
                                Slots.OPTION_INDEX, Integer.toString(i),
                                Slots.DISABLED, sessionIsOpen ? "" : "disabled",
                                Slots.MARGIN_LEFT, "margin-left-auto",
                                Slots.CONSTSUM_OPTION_VISIBILITY, "",
                                Slots.CONSTSUM_OPTION_POINT,
                                        Integer.toString(existingConstSumResponse.getAnswerList().get(i)),
                                Slots.FEEDBACK_RESPONSE_TEXT, Const.ParamsNames.FEEDBACK_RESPONSE_TEXT,
                                Slots.CONSTSUM_OPTION_VALUE, SanitizationHelper.sanitizeForHtml(constSumOptions.get(i)));
                optionListHtml.append(optionFragment).append(Const.EOL);
            }
        }

        return Templates.populateTemplate(
                FormTemplates.CONSTSUM_SUBMISSION_FORM,
                Slots.CONSTSUM_SUBMISSION_FORM_OPTION_FRAGMENT, optionListHtml.toString(),
                Slots.QUESTION_INDEX, Integer.toString(qnIdx),
                Slots.RESPONSE_INDEX, Integer.toString(responseIdx),
                Slots.CONSTSUM_OPTION_VISIBILITY, distributeToRecipients ? "style=\"display:none\"" : "",
                Slots.CONSTSUM_TO_RECIPIENTS_VALUE, Boolean.toString(distributeToRecipients),
                Slots.CONSTSUM_POINTS_PER_OPTION_VALUE, Boolean.toString(pointsPerOption),
                Slots.CONSTSUM_NUM_OPTION_VALUE, Integer.toString(constSumOptions.size()),
                Slots.CONSTSUM_POINTS_VALUE, Integer.toString(points),
                Slots.CONSTSUM_UNEVEN_DISTRIBUTION_VALUE, Boolean.toString(forceUnevenDistribution),
                Slots.CONSTSUM_TO_RECIPIENTS, Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMTORECIPIENTS,
                Slots.CONSTSUM_POINTS_PER_OPTION, Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSPEROPTION,
                Slots.CONSTSUM_NUM_OPTION, Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMNUMOPTION,
                Slots.CONSTSUM_PARAM_POINTS, Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTS,
                Slots.CONSTSUM_PARAM_POINTSFOREACHOPTION, Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSFOREACHOPTION,
                Slots.CONSTSUM_PARAM_POINTSFOREACHRECIPIENT,
                        Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSFOREACHRECIPIENT,
                Slots.CONSTSUM_PARAM_DISTRIBUTE_UNEVENLY, Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMDISTRIBUTEUNEVENLY
                );
    }

    @Override
    public String getQuestionWithoutExistingResponseSubmissionFormHtml(
            boolean sessionIsOpen, int qnIdx, int responseIdx, String courseId, int totalNumRecipients) {

        StringBuilder optionListHtml = new StringBuilder();
        String optionFragmentTemplate = FormTemplates.CONSTSUM_SUBMISSION_FORM_OPTIONFRAGMENT;

        if (distributeToRecipients) {
            String optionFragment =
                    Templates.populateTemplate(optionFragmentTemplate,
                            Slots.QUESTION_INDEX, Integer.toString(qnIdx),
                            Slots.RESPONSE_INDEX, Integer.toString(responseIdx),
                            Slots.OPTION_INDEX, "0",
                            Slots.DISABLED, sessionIsOpen ? "" : "disabled",
                            Slots.MARGIN_LEFT, "",
                            Slots.CONSTSUM_OPTION_VISIBILITY, "style=\"display:none\"",
                            Slots.CONSTSUM_OPTION_POINT, "",
                            Slots.FEEDBACK_RESPONSE_TEXT, Const.ParamsNames.FEEDBACK_RESPONSE_TEXT,
                            Slots.CONSTSUM_OPTION_VALUE, "");
            optionListHtml.append(optionFragment).append(Const.EOL);
        } else {
            for (int i = 0; i < constSumOptions.size(); i++) {
                String optionFragment =
                        Templates.populateTemplate(optionFragmentTemplate,
                                Slots.QUESTION_INDEX, Integer.toString(qnIdx),
                                Slots.RESPONSE_INDEX, Integer.toString(responseIdx),
                                Slots.OPTION_INDEX, Integer.toString(i),
                                Slots.DISABLED, sessionIsOpen ? "" : "disabled",
                                Slots.MARGIN_LEFT, "margin-left-auto",
                                Slots.CONSTSUM_OPTION_VISIBILITY, "",
                                Slots.CONSTSUM_OPTION_POINT, "",
                                Slots.FEEDBACK_RESPONSE_TEXT, Const.ParamsNames.FEEDBACK_RESPONSE_TEXT,
                                Slots.CONSTSUM_OPTION_VALUE, SanitizationHelper.sanitizeForHtml(constSumOptions.get(i)));
                optionListHtml.append(optionFragment).append(Const.EOL);
            }
        }

        return Templates.populateTemplate(
                FormTemplates.CONSTSUM_SUBMISSION_FORM,
                Slots.CONSTSUM_SUBMISSION_FORM_OPTION_FRAGMENT, optionListHtml.toString(),
                Slots.QUESTION_INDEX, Integer.toString(qnIdx),
                Slots.RESPONSE_INDEX, Integer.toString(responseIdx),
                Slots.CONSTSUM_OPTION_VISIBILITY, distributeToRecipients ? "style=\"display:none\"" : "",
                Slots.CONSTSUM_TO_RECIPIENTS_VALUE, Boolean.toString(distributeToRecipients),
                Slots.CONSTSUM_POINTS_PER_OPTION_VALUE, Boolean.toString(pointsPerOption),
                Slots.CONSTSUM_NUM_OPTION_VALUE, Integer.toString(constSumOptions.size()),
                Slots.CONSTSUM_POINTS_VALUE, Integer.toString(points),
                Slots.CONSTSUM_UNEVEN_DISTRIBUTION_VALUE, Boolean.toString(forceUnevenDistribution),
                Slots.CONSTSUM_TO_RECIPIENTS, Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMTORECIPIENTS,
                Slots.CONSTSUM_POINTS_PER_OPTION, Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSPEROPTION,
                Slots.CONSTSUM_NUM_OPTION, Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMNUMOPTION,
                Slots.CONSTSUM_PARAM_POINTS, Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTS,
                Slots.CONSTSUM_PARAM_POINTSFOREACHOPTION, Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSFOREACHOPTION,
                Slots.CONSTSUM_PARAM_POINTSFOREACHRECIPIENT,
                        Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSFOREACHRECIPIENT,
                Slots.CONSTSUM_PARAM_DISTRIBUTE_UNEVENLY, Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMDISTRIBUTEUNEVENLY
                );
    }

    @Override
    public String getQuestionSpecificEditFormHtml(int questionNumber) {
        StringBuilder optionListHtml = new StringBuilder();
        String optionFragmentTemplate = FormTemplates.CONSTSUM_EDIT_FORM_OPTIONFRAGMENT;
        for (int i = 0; i < numOfConstSumOptions; i++) {
            String optionFragment =
                    Templates.populateTemplate(optionFragmentTemplate,
                            Slots.ITERATOR, Integer.toString(i),
                            Slots.CONSTSUM_OPTION_VALUE, SanitizationHelper.sanitizeForHtml(constSumOptions.get(i)),
                            Slots.CONSTSUM_PARAM_OPTION, Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMOPTION);

            optionListHtml.append(optionFragment).append(Const.EOL);
        }

        return Templates.populateTemplate(
                FormTemplates.CONSTSUM_EDIT_FORM,
                Slots.CONSTSUM_EDIT_FORM_OPTION_FRAGMENT, optionListHtml.toString(),
                Slots.QUESTION_NUMBER, Integer.toString(questionNumber),
                Slots.NUMBER_OF_CHOICE_CREATED, Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED,
                Slots.CONSTSUM_NUMBER_OF_OPTIONS, Integer.toString(numOfConstSumOptions),
                Slots.CONSTSUM_TO_RECIPIENTS_VALUE, Boolean.toString(distributeToRecipients),
                Slots.CONSTSUM_SELECTED_POINTS_PER_OPTION, pointsPerOption ? "selected" : "",
                Slots.CONSTSUM_OPTION_TABLE_VISIBILITY, distributeToRecipients ? "style=\"display:none\"" : "",
                Slots.CONSTSUM_POINTS, points == 0 ? "100" : Integer.toString(points),
                Slots.OPTION_DISPLAY, distributeToRecipients ? "style=\"display:none\"" : "",
                Slots.RECIPIENT_DISPLAY, distributeToRecipients ? "" : "style=\"display:none\"",
                Slots.PER_OPTION_CHECKED, !distributeToRecipients && pointsPerOption ? "checked" : "",
                Slots.PER_RECIPIENT_CHECKED, distributeToRecipients && pointsPerOption ? "checked" : "",
                Slots.OPTION_RECIPIENT_DISPLAY_NAME, distributeToRecipients ? "recipient" : "option",
                Slots.CONSTSUM_TOOLTIP_POINTS,
                        distributeToRecipients ? Const.Tooltips.FEEDBACK_QUESTION_CONSTSUMPOINTS_RECIPIENT
                                               : Const.Tooltips.FEEDBACK_QUESTION_CONSTSUMPOINTS_OPTION,
                Slots.CONSTSUM_TOOLTIP_POINTS_PER_OPTION, Const.Tooltips.FEEDBACK_QUESTION_CONSTSUMPOINTSFOREACHOPTION,
                Slots.CONSTSUM_TOOLTIP_POINTS_PER_RECIPIENT,
                        Const.Tooltips.FEEDBACK_QUESTION_CONSTSUMPOINTSFOREACHRECIPIENT,
                Slots.CONSTSUM_DISTRIBUTE_UNEVENLY, forceUnevenDistribution ? "checked" : "",
                Slots.CONSTSUM_TO_RECIPIENTS, Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMTORECIPIENTS,
                Slots.CONSTSUM_POINTS_PER_OPTION, Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSPEROPTION,
                Slots.CONSTSUM_PARAM_POINTS, Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTS,
                Slots.CONSTSUM_PARAM_POINTSFOREACHOPTION, Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSFOREACHOPTION,
                Slots.CONSTSUM_PARAM_POINTSFOREACHRECIPIENT,
                        Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSFOREACHRECIPIENT,
                Slots.CONSTSUM_PARAM_DISTRIBUTE_UNEVENLY, Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMDISTRIBUTEUNEVENLY);

    }

    @Override
    public String getNewQuestionSpecificEditFormHtml() {
        // Add two empty options by default
        this.numOfConstSumOptions = 2;
        this.constSumOptions.add("");
        this.constSumOptions.add("");

        return "<div id=\"constSumForm\">"
                  + this.getQuestionSpecificEditFormHtml(-1)
             + "</div>";
    }

    @Override
    public String getQuestionAdditionalInfoHtml(int questionNumber,
            String additionalInfoId) {
        StringBuilder optionListHtml = new StringBuilder();
        String optionFragmentTemplate = FormTemplates.MSQ_ADDITIONAL_INFO_FRAGMENT;
        StringBuilder additionalInfo = new StringBuilder();

        if (distributeToRecipients) {
            additionalInfo.append(getQuestionTypeDisplayName()).append("<br>");
        } else if (numOfConstSumOptions > 0) {
            optionListHtml.append("<ul style=\"list-style-type: disc;margin-left: 20px;\" >");
            for (int i = 0; i < numOfConstSumOptions; i++) {
                String optionFragment =
                        Templates.populateTemplate(optionFragmentTemplate,
                                Slots.MSQ_CHOICE_VALUE, constSumOptions.get(i));

                optionListHtml.append(optionFragment);
            }
            optionListHtml.append("</ul>");
            additionalInfo.append(Templates.populateTemplate(
                    FormTemplates.MSQ_ADDITIONAL_INFO,
                    Slots.QUESTION_TYPE_NAME, this.getQuestionTypeDisplayName(),
                    Slots.MSQ_ADDITIONAL_INFO_FRAGMENTS, optionListHtml.toString()));

        }
        //Point information
        additionalInfo.append(pointsPerOption
                              ? "Points per " + (distributeToRecipients ? "recipient" : "option") + ": " + points
                              : "Total points: " + points);

        return Templates.populateTemplate(
                FormTemplates.FEEDBACK_QUESTION_ADDITIONAL_INFO,
                Slots.MORE, "[more]",
                Slots.LESS, "[less]",
                Slots.QUESTION_NUMBER, Integer.toString(questionNumber),
                Slots.ADDITIONAL_INFO_ID, additionalInfoId,
                Slots.QUESTION_ADDITIONAL_INFO, additionalInfo.toString());
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

        sortedOptionPoints.forEach((option, points) -> {

            double average = computeAverage(points);
            int total = computeTotal(points);
            String pointsReceived = getListOfPointsAsString(points);

            if (distributeToRecipients) {
                String participantIdentifier = identifierMap.get(option);
                String name = bundle.getNameForEmail(participantIdentifier);
                String teamName = bundle.getTeamNameForEmail(participantIdentifier);

                fragments.append(Templates.populateTemplate(FormTemplates.CONSTSUM_RESULT_STATS_RECIPIENTFRAGMENT,
                        Slots.CONSTSUM_OPTION_VALUE, SanitizationHelper.sanitizeForHtml(name),
                        Slots.TEAM, SanitizationHelper.sanitizeForHtml(teamName),
                        Slots.CONSTSUM_POINTS_RECEIVED, pointsReceived,
                        Slots.CONSTSUM_TOTAL_POINTS, Integer.toString(total),
                        Slots.CONSTSUM_AVERAGE_POINTS, df.format(average)));
            } else {

                fragments.append(Templates.populateTemplate(FormTemplates.CONSTSUM_RESULT_STATS_OPTIONFRAGMENT,
                        Slots.CONSTSUM_OPTION_VALUE, SanitizationHelper.sanitizeForHtml(option),
                        Slots.CONSTSUM_POINTS_RECEIVED, pointsReceived,
                        Slots.CONSTSUM_TOTAL_POINTS, Integer.toString(total),
                        Slots.CONSTSUM_AVERAGE_POINTS, df.format(average)));
            }
        });

        if (distributeToRecipients) {
            return Templates.populateTemplate(FormTemplates.CONSTSUM_RESULT_RECIPIENT_STATS,
                    Slots.OPTION_RECIPIENT_DISPLAY_NAME, "Recipient",
                    Slots.FRAGMENTS, fragments.toString());
        }
        return Templates.populateTemplate(FormTemplates.CONSTSUM_RESULT_OPTION_STATS,
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
                    .append(Const.EOL);

        });

        return (distributeToRecipients ? "Team, Recipient" : "Option")
               + ", Average Points, Total Points, Received Points" + Const.EOL
               + fragments + Const.EOL;
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
        List<Integer> points = optionPoints.get(optionReceivingPoints);
        if (points == null) {
            points = new ArrayList<>();
            optionPoints.put(optionReceivingPoints, points);
        }

        points.add(pointsReceived);
    }

    /**
     * Returns the list of points as as string to display.
     */
    private String getListOfPointsAsString(List<Integer> points) {
        points.sort(null);
        StringBuilder pointsReceived = new StringBuilder();
        if (points.size() > 10) {
            for (int i = 0; i < 5; i++) {
                pointsReceived.append(points.get(i)).append(" , ");
            }
            pointsReceived.append("...");
            for (int i = points.size() - 5; i < points.size(); i++) {
                pointsReceived.append(" , ").append(points.get(i));
            }
        } else {
            for (int i = 0; i < points.size(); i++) {
                pointsReceived.append(points.get(i));
                if (i != points.size() - 1) {
                    pointsReceived.append(" , ");
                }
            }
        }
        return pointsReceived.toString();
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
    public boolean isChangesRequiresResponseDeletion(
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

        return this.forceUnevenDistribution != newConstSumDetails.forceUnevenDistribution;
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
    public String getQuestionTypeChoiceOption() {
        // Constant sum has two options for user to select.
        return "<li data-questiontype = \"CONSTSUM_OPTION\">"
                 + "<a href=\"javascript:;\">" + Const.FeedbackQuestionTypeNames.CONSTSUM_OPTION + "</a>"
             + "</li>"
             + "<li data-questiontype = \"CONSTSUM_RECIPIENT\">"
                 + "<a href=\"javascript:;\">" + Const.FeedbackQuestionTypeNames.CONSTSUM_RECIPIENT + "</a>"
             + "</li>";
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
    public List<String> validateResponseAttributes(
            List<FeedbackResponseAttributes> responses,
            int numRecipients) {
        List<String> errors = new ArrayList<>();

        if (responses.isEmpty()) {
            //No responses, no errors.
            return errors;
        }

        String fqId = responses.get(0).feedbackQuestionId;
        FeedbackQuestionsLogic fqLogic = FeedbackQuestionsLogic.inst();
        FeedbackQuestionAttributes fqa = fqLogic.getFeedbackQuestion(fqId);

        int numOfResponseSpecific = fqa.numberOfEntitiesToGiveFeedbackTo;
        int maxResponsesPossible = numRecipients;
        if (numOfResponseSpecific == Const.MAX_POSSIBLE_RECIPIENTS
                || numOfResponseSpecific > maxResponsesPossible) {
            numOfResponseSpecific = maxResponsesPossible;
        }

        int numOptions = distributeToRecipients ? numOfResponseSpecific : constSumOptions.size();
        int totalPoints = pointsPerOption ? points * numOptions : points;
        int sum = 0;
        for (FeedbackResponseAttributes response : responses) {
            FeedbackConstantSumResponseDetails frd = (FeedbackConstantSumResponseDetails) response.getResponseDetails();

            //Check that all response points are >= 0
            for (Integer i : frd.getAnswerList()) {
                if (i < 0) {
                    errors.add(Const.FeedbackQuestion.CONST_SUM_ERROR_NEGATIVE);
                    return errors;
                }
            }

            //Check that points sum up properly
            if (distributeToRecipients) {
                sum += frd.getAnswerList().get(0);
            } else {
                sum = 0;
                for (Integer i : frd.getAnswerList()) {
                    sum += i;
                }
                if (sum != totalPoints || frd.getAnswerList().size() != constSumOptions.size()) {
                    errors.add(Const.FeedbackQuestion.CONST_SUM_ERROR_MISMATCH);
                    return errors;
                }
            }

            Set<Integer> answerSet = new HashSet<>();
            if (this.forceUnevenDistribution) {
                for (int i : frd.getAnswerList()) {
                    if (answerSet.contains(i)) {
                        errors.add(Const.FeedbackQuestion.CONST_SUM_ERROR_UNIQUE);
                        return errors;
                    }
                    answerSet.add(i);
                }
            }
        }
        if (distributeToRecipients && sum != totalPoints) {
            errors.add(Const.FeedbackQuestion.CONST_SUM_ERROR_MISMATCH + sum + "/" + totalPoints);
            return errors;
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

    public int getNumOfConstSumOptions() {
        return numOfConstSumOptions;
    }

    public List<String> getConstSumOptions() {
        return constSumOptions;
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
