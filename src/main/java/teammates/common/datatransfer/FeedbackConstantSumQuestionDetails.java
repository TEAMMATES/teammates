package teammates.common.datatransfer;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.Sanitizer;
import teammates.common.util.StringHelper;
import teammates.common.util.Templates;
import teammates.common.util.Templates.FeedbackQuestionFormTemplates;
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
        this.constSumOptions = new ArrayList<String>();
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

    public FeedbackConstantSumQuestionDetails(String questionText,
            boolean pointsPerOption, int points, boolean unevenDistribution) {
        super(FeedbackQuestionType.CONSTSUM, questionText);
        
        this.numOfConstSumOptions = 0;
        this.constSumOptions = new ArrayList<String>();
        this.distributeToRecipients = true;
        this.pointsPerOption = pointsPerOption;
        this.points = points;
        this.forceUnevenDistribution = unevenDistribution;
    }
    
    @Override
    public boolean extractQuestionDetails(
            Map<String, String[]> requestParameters,
            FeedbackQuestionType questionType) {
        
        List<String> constSumOptions = new LinkedList<String>();
        String distributeToRecipientsString = null;
        String pointsPerOptionString = null;
        String pointsString = null;
        String forceUnevenDistributionString = null;
        boolean distributeToRecipients = false;
        boolean pointsPerOption = false;
        boolean forceUnevenDistribution = false;
        int points = 0;
        
        distributeToRecipientsString =
                HttpRequestHelper.getValueFromParamMap(requestParameters,
                                                       Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMTORECIPIENTS);
        pointsPerOptionString =
                HttpRequestHelper.getValueFromParamMap(requestParameters,
                                                       Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSPEROPTION);
        pointsString =
                HttpRequestHelper.getValueFromParamMap(requestParameters,
                                                       Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTS);
        Assumption.assertNotNull("Null points", pointsString);
        forceUnevenDistributionString =
                HttpRequestHelper.getValueFromParamMap(requestParameters,
                                                       Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMDISTRIBUTEUNEVENLY);
        
        distributeToRecipients = "true".equals(distributeToRecipientsString);
        pointsPerOption = "true".equals(pointsPerOptionString);
        points = Integer.parseInt(pointsString);
        forceUnevenDistribution = "on".equals(forceUnevenDistributionString);
        
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
        this.constSumOptions = new ArrayList<String>();
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
    public String getQuestionWithExistingResponseSubmissionFormHtml(
            boolean sessionIsOpen, int qnIdx, int responseIdx, String courseId,
            int totalNumRecipients,
            FeedbackResponseDetails existingResponseDetails) {
        
        FeedbackConstantSumResponseDetails existingConstSumResponse =
                (FeedbackConstantSumResponseDetails) existingResponseDetails;
        StringBuilder optionListHtml = new StringBuilder();
        String optionFragmentTemplate = FeedbackQuestionFormTemplates.CONSTSUM_SUBMISSION_FORM_OPTIONFRAGMENT;
        
        if (distributeToRecipients) {
            String optionFragment =
                    Templates.populateTemplate(optionFragmentTemplate,
                            "${qnIdx}", Integer.toString(qnIdx),
                            "${responseIdx}", Integer.toString(responseIdx),
                            "${optionIdx}", "0",
                            "${disabled}", sessionIsOpen ? "" : "disabled",
                            "${constSumOptionVisibility}", "style=\"display:none\"",
                            "${constSumOptionPoint}", existingConstSumResponse.getAnswerString(),
                            "${Const.ParamsNames.FEEDBACK_RESPONSE_TEXT}", Const.ParamsNames.FEEDBACK_RESPONSE_TEXT,
                            "${constSumOptionValue}", "");
            optionListHtml.append(optionFragment).append(Const.EOL);
        } else {
            for (int i = 0; i < constSumOptions.size(); i++) {
                String optionFragment =
                        Templates.populateTemplate(optionFragmentTemplate,
                                "${qnIdx}", Integer.toString(qnIdx),
                                "${responseIdx}", Integer.toString(responseIdx),
                                "${optionIdx}", Integer.toString(i),
                                "${disabled}", sessionIsOpen ? "" : "disabled",
                                "${constSumOptionVisibility}", "",
                                "${constSumOptionPoint}", Integer.toString(existingConstSumResponse.getAnswerList().get(i)),
                                "${Const.ParamsNames.FEEDBACK_RESPONSE_TEXT}", Const.ParamsNames.FEEDBACK_RESPONSE_TEXT,
                                "${constSumOptionValue}", Sanitizer.sanitizeForHtml(constSumOptions.get(i)));
                optionListHtml.append(optionFragment).append(Const.EOL);
            }
        }

        String html = Templates.populateTemplate(
                FeedbackQuestionFormTemplates.CONSTSUM_SUBMISSION_FORM,
                "${constSumSubmissionFormOptionFragments}", optionListHtml.toString(),
                "${qnIdx}", Integer.toString(qnIdx),
                "${responseIdx}", Integer.toString(responseIdx),
                "${constSumOptionVisibility}", distributeToRecipients ? "style=\"display:none\"" : "",
                "${constSumToRecipientsValue}", Boolean.toString(distributeToRecipients),
                "${constSumPointsPerOptionValue}", Boolean.toString(pointsPerOption),
                "${constSumNumOptionValue}", Integer.toString(constSumOptions.size()),
                "${constSumPointsValue}", Integer.toString(points),
                "${constSumUnevenDistributionValue}", Boolean.toString(forceUnevenDistribution),
                "${Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMTORECIPIENTS}",
                        Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMTORECIPIENTS,
                "${Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSPEROPTION}",
                        Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSPEROPTION,
                "${Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMNUMOPTION}", Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMNUMOPTION,
                "${Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTS}", Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTS,
                "${Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMDISTRIBUTEUNEVENLY}",
                        Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMDISTRIBUTEUNEVENLY
                );
        
        return html;
    }

    @Override
    public String getQuestionWithoutExistingResponseSubmissionFormHtml(
            boolean sessionIsOpen, int qnIdx, int responseIdx, String courseId, int totalNumRecipients) {
        
        StringBuilder optionListHtml = new StringBuilder();
        String optionFragmentTemplate = FeedbackQuestionFormTemplates.CONSTSUM_SUBMISSION_FORM_OPTIONFRAGMENT;
        
        if (distributeToRecipients) {
            String optionFragment =
                    Templates.populateTemplate(optionFragmentTemplate,
                            "${qnIdx}", Integer.toString(qnIdx),
                            "${responseIdx}", Integer.toString(responseIdx),
                            "${optionIdx}", "0",
                            "${disabled}", sessionIsOpen ? "" : "disabled",
                            "${constSumOptionVisibility}", "style=\"display:none\"",
                            "${constSumOptionPoint}", "",
                            "${Const.ParamsNames.FEEDBACK_RESPONSE_TEXT}", Const.ParamsNames.FEEDBACK_RESPONSE_TEXT,
                            "${constSumOptionValue}", "");
            optionListHtml.append(optionFragment).append(Const.EOL);
        } else {
            for (int i = 0; i < constSumOptions.size(); i++) {
                String optionFragment =
                        Templates.populateTemplate(optionFragmentTemplate,
                                "${qnIdx}", Integer.toString(qnIdx),
                                "${responseIdx}", Integer.toString(responseIdx),
                                "${optionIdx}", Integer.toString(i),
                                "${disabled}", sessionIsOpen ? "" : "disabled",
                                "${constSumOptionVisibility}", "",
                                "${constSumOptionPoint}", "",
                                "${Const.ParamsNames.FEEDBACK_RESPONSE_TEXT}", Const.ParamsNames.FEEDBACK_RESPONSE_TEXT,
                                "${constSumOptionValue}", Sanitizer.sanitizeForHtml(constSumOptions.get(i)));
                optionListHtml.append(optionFragment).append(Const.EOL);
            }
        }

        String html = Templates.populateTemplate(
                FeedbackQuestionFormTemplates.CONSTSUM_SUBMISSION_FORM,
                "${constSumSubmissionFormOptionFragments}", optionListHtml.toString(),
                "${qnIdx}", Integer.toString(qnIdx),
                "${responseIdx}", Integer.toString(responseIdx),
                "${constSumOptionVisibility}", distributeToRecipients ? "style=\"display:none\"" : "",
                "${constSumToRecipientsValue}", Boolean.toString(distributeToRecipients),
                "${constSumPointsPerOptionValue}", Boolean.toString(pointsPerOption),
                "${constSumNumOptionValue}", Integer.toString(constSumOptions.size()),
                "${constSumPointsValue}", Integer.toString(points),
                "${constSumUnevenDistributionValue}", Boolean.toString(forceUnevenDistribution),
                "${Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMTORECIPIENTS}",
                        Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMTORECIPIENTS,
                "${Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSPEROPTION}",
                        Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSPEROPTION,
                "${Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMNUMOPTION}",
                        Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMNUMOPTION,
                "${Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTS}", Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTS,
                "${Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMDISTRIBUTEUNEVENLY}",
                        Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMDISTRIBUTEUNEVENLY
                );
        
        return html;
    }

    @Override
    public String getQuestionSpecificEditFormHtml(int questionNumber) {
        StringBuilder optionListHtml = new StringBuilder();
        String optionFragmentTemplate = FeedbackQuestionFormTemplates.CONSTSUM_EDIT_FORM_OPTIONFRAGMENT;
        for (int i = 0; i < numOfConstSumOptions; i++) {
            String optionFragment =
                    Templates.populateTemplate(optionFragmentTemplate,
                            "${i}", Integer.toString(i),
                            "${constSumOptionValue}", Sanitizer.sanitizeForHtml(constSumOptions.get(i)),
                            "${Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMOPTION}",
                                    Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMOPTION);

            optionListHtml.append(optionFragment).append(Const.EOL);
        }
        
        String html = Templates.populateTemplate(
                FeedbackQuestionFormTemplates.CONSTSUM_EDIT_FORM,
                "${constSumEditFormOptionFragments}", optionListHtml.toString(),
                "${questionNumber}", Integer.toString(questionNumber),
                "${Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED}",
                        Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED,
                "${numOfConstSumOptions}", Integer.toString(numOfConstSumOptions),
                "${constSumToRecipientsValue}", Boolean.toString(distributeToRecipients),
                "${selectedConstSumPointsPerOption}", pointsPerOption ? "selected" : "",
                "${constSumOptionTableVisibility}", distributeToRecipients ? "style=\"display:none\"" : "",
                "${constSumPoints}", points == 0 ? "100" : Integer.toString(points),
                "${optionRecipientDisplayName}", distributeToRecipients ? "recipient" : "option",
                "${distributeUnevenly}", forceUnevenDistribution ? "checked" : "",
                "${Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMTORECIPIENTS}",
                        Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMTORECIPIENTS,
                "${Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSPEROPTION}",
                        Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSPEROPTION,
                "${Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTS}", Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTS,
                "${Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMDISTRIBUTEUNEVENLY}",
                        Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMDISTRIBUTEUNEVENLY);
        
        return html;
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
        String optionFragmentTemplate = FeedbackQuestionFormTemplates.MSQ_ADDITIONAL_INFO_FRAGMENT;
        StringBuilder additionalInfo = new StringBuilder();

        if (distributeToRecipients) {
            additionalInfo.append(getQuestionTypeDisplayName()).append("<br>");
        } else if (numOfConstSumOptions > 0) {
            optionListHtml.append("<ul style=\"list-style-type: disc;margin-left: 20px;\" >");
            for (int i = 0; i < numOfConstSumOptions; i++) {
                String optionFragment =
                        Templates.populateTemplate(optionFragmentTemplate,
                                "${msqChoiceValue}", constSumOptions.get(i));
                
                optionListHtml.append(optionFragment);
            }
            optionListHtml.append("</ul>");
            additionalInfo.append(Templates.populateTemplate(
                    FeedbackQuestionFormTemplates.MSQ_ADDITIONAL_INFO,
                    "${questionTypeName}", getQuestionTypeDisplayName(),
                    "${msqAdditionalInfoFragments}", optionListHtml.toString()));
        
        }
        //Point information
        additionalInfo.append(pointsPerOption
                              ? "Points per " + (distributeToRecipients ? "recipient" : "option") + ": " + points
                              : "Total points: " + points);
        
        return Templates.populateTemplate(
                FeedbackQuestionFormTemplates.FEEDBACK_QUESTION_ADDITIONAL_INFO,
                "${more}", "[more]",
                "${less}", "[less]",
                "${questionNumber}", Integer.toString(questionNumber),
                "${additionalInfoId}", additionalInfoId,
                "${questionAdditionalInfo}", additionalInfo.toString());
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
        
        for (Entry<String, List<Integer>> entry : optionPoints.entrySet()) {
            
            List<Integer> points = entry.getValue();
            double average = computeAverage(points);
            String pointsReceived = getListOfPointsAsString(points);
            
            if (distributeToRecipients) {
                String participantIdentifier = entry.getKey();
                String name = bundle.getNameForEmail(participantIdentifier);
                String teamName = bundle.getTeamNameForEmail(participantIdentifier);
                
                fragments.append(Templates.populateTemplate(FeedbackQuestionFormTemplates.CONSTSUM_RESULT_STATS_RECIPIENTFRAGMENT,
                        "${constSumOptionValue}", Sanitizer.sanitizeForHtml(name),
                        "${team}", Sanitizer.sanitizeForHtml(teamName),
                        "${pointsReceived}", pointsReceived,
                        "${averagePoints}", df.format(average)));
            
            } else {
                String option = options.get(Integer.parseInt(entry.getKey()));
                
                fragments.append(Templates.populateTemplate(FeedbackQuestionFormTemplates.CONSTSUM_RESULT_STATS_OPTIONFRAGMENT,
                                    "${constSumOptionValue}", Sanitizer.sanitizeForHtml(option),
                                    "${pointsReceived}", pointsReceived,
                                    "${averagePoints}", df.format(average)));
            }
        }
        
        if (distributeToRecipients) {
            return Templates.populateTemplate(FeedbackQuestionFormTemplates.CONSTSUM_RESULT_RECIPIENT_STATS,
                    "${optionRecipientDisplayName}", "Recipient",
                    "${fragments}", fragments.toString());
        }
        return Templates.populateTemplate(FeedbackQuestionFormTemplates.CONSTSUM_RESULT_OPTION_STATS,
                "${optionRecipientDisplayName}", "Option",
                "${fragments}", fragments.toString());
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
        
        for (Entry<String, List<Integer>> entry : optionPoints.entrySet()) {
            String option;
            if (distributeToRecipients) {
                String teamName = bundle.getTeamNameForEmail(entry.getKey());
                String recipientName = bundle.getNameForEmail(entry.getKey());
                option = Sanitizer.sanitizeForCsv(teamName) + "," + Sanitizer.sanitizeForCsv(recipientName);
            } else {
                option = Sanitizer.sanitizeForCsv(options.get(Integer.parseInt(entry.getKey())));
            }
            
            List<Integer> points = entry.getValue();
            double average = computeAverage(points);
            fragments.append(option).append(',').append(df.format(average)).append(Const.EOL);
            
        }
        
        return (distributeToRecipients ? "Team, Recipient" : "Option")
               + ", Average Points" + Const.EOL
               + fragments + Const.EOL;
    }

    /**
     * From the feedback responses, generate a mapping of the option to a list of points received for that option.
     * The key of the map returned is the option name / recipient's participant identifier.
     * The values of the map are list of points received by the key.
     * @param responses  a list of responses
     */
    private Map<String, List<Integer>> generateOptionPointsMapping(
            List<FeedbackResponseAttributes> responses) {
        
        Map<String, List<Integer>> optionPoints = new HashMap<String, List<Integer>>();
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
     * Used to update the OptionPointsMapping for the option optionReceivingPoints
     * 
     * @param optionPoints
     * @param optionReceivingPoints
     * @param pointsReceived
     */
    private void updateOptionPointsMapping(
            Map<String, List<Integer>> optionPoints,
            String optionReceivingPoints, int pointsReceived) {
        List<Integer> points = optionPoints.get(optionReceivingPoints);
        if (points == null) {
            points = new ArrayList<Integer>();
            optionPoints.put(optionReceivingPoints, points);
        }
        
        points.add(pointsReceived);
    }

    /**
     * Returns the list of points as as string to display
     * @param points
     */
    private String getListOfPointsAsString(List<Integer> points) {
        Collections.sort(points);
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

    private double computeAverage(List<Integer> points) {
        double average = 0;
        for (Integer point : points) {
            average += point;
        }
        average = average / points.size();
        return average;
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
        List<String> sanitizedOptions = Sanitizer.sanitizeListForCsv(constSumOptions);
        return "Feedbacks:," + StringHelper.toString(sanitizedOptions, ",");
    }

    @Override
    public String getQuestionTypeChoiceOption() {
        // Constant sum has two options for user to select.
        return "<li data-questiontype = \"CONSTSUM_OPTION\">"
                 + "<a>" + Const.FeedbackQuestionTypeNames.CONSTSUM_OPTION + "</a>"
             + "</li>"
             + "<li data-questiontype = \"CONSTSUM_RECIPIENT\">"
                 + "<a>" + Const.FeedbackQuestionTypeNames.CONSTSUM_RECIPIENT + "</a>"
             + "</li>";
    }
    
    @Override
    public List<String> validateQuestionDetails() {
        List<String> errors = new ArrayList<String>();
        if (!distributeToRecipients && numOfConstSumOptions < Const.FeedbackQuestion.CONST_SUM_MIN_NUM_OF_OPTIONS) {
            errors.add(Const.FeedbackQuestion.CONST_SUM_ERROR_NOT_ENOUGH_OPTIONS
                       + Const.FeedbackQuestion.CONST_SUM_MIN_NUM_OF_OPTIONS + ".");
        }
        
        if (points < Const.FeedbackQuestion.CONST_SUM_MIN_NUM_OF_POINTS) {
            errors.add(Const.FeedbackQuestion.CONST_SUM_ERROR_NOT_ENOUGH_POINTS
                       + Const.FeedbackQuestion.CONST_SUM_MIN_NUM_OF_POINTS + ".");
        }
        
        return errors;
    }

    @Override
    public List<String> validateResponseAttributes(
            List<FeedbackResponseAttributes> responses,
            int numRecipients) {
        List<String> errors = new ArrayList<String>();
        
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
            
            Set<Integer> answerSet = new HashSet<Integer>();
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
