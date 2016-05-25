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
import teammates.common.util.FeedbackQuestionFormTemplates;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.Sanitizer;
import teammates.common.util.StringHelper;
import teammates.logic.core.FeedbackQuestionsLogic;
import teammates.ui.template.InstructorFeedbackResultsResponseRow;

public class FeedbackConstantSumQuestionDetails extends FeedbackQuestionDetails {
    public int numOfConstSumOptions;
    public List<String> constSumOptions;
    public boolean distributeToRecipients;
    public boolean pointsPerOption;
    public boolean forceUnevenDistribution;
    public int points;
    
    private String QUESTION_INDEX = "${qnIdx}"; 
    private String RESPONSE_INDEX = "${responseIdx}";
    private String OPTION_INDEX = "${optionIdx}";
    private String DISABLED = "${disabled}";
    private String CONST_SUM_OPTION_VISIBILITY = "${constSumOptionVisibility}";
    private String CONST_SUM_OPTION_POINT = "${constSumOptionPoint}";
    private String CONST_PARAMSNAMES_FEEDBACK_RESPONSE_TEXT = "${Const.ParamsNames.FEEDBACK_RESPONSE_TEXT}";
    private String CONST_SUM_OPTION_VALUE = "${constSumOptionValue}";
    private String OPTION_RECIEPIENT_DISPLAY_NAME = "${optionRecipientDisplayName}";
    private String CONST_PARAMSNAMES_FEEDBACK_QUESTION_CONSTSUMPOINTS = "${Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTS}";
    private String CONST_SUM_TO_RECIPIENTS_VALUE = "${constSumToRecipientsValue}";
    private String CONST_PARAMSNAMES_FEEDBACK_QUESTION_CONSTSUMTORECIPIENTS = "${Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMTORECIPIENTS}";
    private String CONST_PARAMSNAMES_FEEDBACK_QUESTION_CONSTSUMDISTRIBUTEUNEVENLY = "${Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMDISTRIBUTEUNEVENLY}";
    private String CONST_PARAMSNAME_FEEDBACK_QUESTION_CONSTSUMPOINTSPEROPTION = "${Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSPEROPTION}";
    
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
        
        List<String> tempConstSumOptions = new LinkedList<String>();
        String distributeToRecipientsString = null;
        String pointsPerOptionString = null;
        String pointsString = null;
        String forceUnevenDistributionString = null;
        boolean distributeToRecipients = false;
        boolean pointsPerOption = false;
        boolean forceUnevenDistribution = false;
        int points = 0;
        
        distributeToRecipientsString = HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMTORECIPIENTS);
        pointsPerOptionString = HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSPEROPTION);
        pointsString = HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTS);
        Assumption.assertNotNull("Null points", pointsString);
        forceUnevenDistributionString = HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMDISTRIBUTEUNEVENLY);
        
        distributeToRecipients = "true".equals(distributeToRecipientsString);
        pointsPerOption = "true".equals(pointsPerOptionString);
        points = Integer.parseInt(pointsString);
        forceUnevenDistribution = "on".equals(forceUnevenDistributionString); 
        
        if (distributeToRecipients) {
            this.setConstantSumQuestionDetails(pointsPerOption, points, forceUnevenDistribution);
        } else {
            String numConstSumOptionsCreatedString = HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED);
            Assumption.assertNotNull("Null number of choice for ConstSum", numConstSumOptionsCreatedString);
            int numConstSumOptionsCreated = Integer.parseInt(numConstSumOptionsCreatedString);
            
            for (int i = 0; i < numConstSumOptionsCreated; i++) {
                String constSumOption = HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMOPTION + "-" + i);
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
        
        FeedbackConstantSumResponseDetails existingConstSumResponse = (FeedbackConstantSumResponseDetails) existingResponseDetails;
        StringBuilder optionListHtml = new StringBuilder();
        String optionFragmentTemplate = FeedbackQuestionFormTemplates.CONSTSUM_SUBMISSION_FORM_OPTIONFRAGMENT;
        
        if (distributeToRecipients) {
            String optionFragment = 
                    FeedbackQuestionFormTemplates.populateTemplate(optionFragmentTemplate,
                            QUESTION_INDEX, Integer.toString(qnIdx),
                            RESPONSE_INDEX, Integer.toString(responseIdx),
                            OPTION_INDEX, "0",
                            DISABLED, sessionIsOpen ? "" : "disabled",
                            CONST_SUM_OPTION_VISIBILITY, "style=\"display:none\"",
                            CONST_SUM_OPTION_POINT, existingConstSumResponse.getAnswerString(),
                            CONST_PARAMSNAMES_FEEDBACK_RESPONSE_TEXT, Const.ParamsNames.FEEDBACK_RESPONSE_TEXT,
                            CONST_SUM_OPTION_VALUE, "");
            optionListHtml.append(optionFragment).append(Const.EOL);
        } else {
            for (int i = 0; i < constSumOptions.size(); i++) {
                String optionFragment = 
                        FeedbackQuestionFormTemplates.populateTemplate(optionFragmentTemplate,
                                QUESTION_INDEX, Integer.toString(qnIdx),
                                RESPONSE_INDEX, Integer.toString(responseIdx),
                                OPTION_INDEX, Integer.toString(i),
                                DISABLED, sessionIsOpen ? "" : "disabled",
                                CONST_SUM_OPTION_VISIBILITY, "",
                                CONST_SUM_OPTION_POINT, Integer.toString(existingConstSumResponse.getAnswerList().get(i)),
                                CONST_PARAMSNAMES_FEEDBACK_RESPONSE_TEXT, Const.ParamsNames.FEEDBACK_RESPONSE_TEXT,
                                CONST_SUM_OPTION_VALUE,  Sanitizer.sanitizeForHtml(constSumOptions.get(i)));
                optionListHtml.append(optionFragment).append(Const.EOL);
            }
        }

        return FeedbackQuestionFormTemplates.populateTemplate(
                FeedbackQuestionFormTemplates.CONSTSUM_SUBMISSION_FORM,
                "${constSumSubmissionFormOptionFragments}", optionListHtml.toString(),
                QUESTION_INDEX, Integer.toString(qnIdx),
                RESPONSE_INDEX, Integer.toString(responseIdx),
                CONST_SUM_OPTION_VISIBILITY, distributeToRecipients ? "style=\"display:none\"" : "",
                CONST_SUM_TO_RECIPIENTS_VALUE, Boolean.toString(distributeToRecipients),
                "${constSumPointsPerOptionValue}", Boolean.toString(pointsPerOption),
                "${constSumNumOptionValue}", Integer.toString(constSumOptions.size()),
                "${constSumPointsValue}", Integer.toString(points),
                "${constSumUnevenDistributionValue}", Boolean.toString(forceUnevenDistribution),
                CONST_PARAMSNAMES_FEEDBACK_QUESTION_CONSTSUMTORECIPIENTS, Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMTORECIPIENTS,
                CONST_PARAMSNAME_FEEDBACK_QUESTION_CONSTSUMPOINTSPEROPTION, Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSPEROPTION,
                "${Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMNUMOPTION}", Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMNUMOPTION,
                CONST_PARAMSNAMES_FEEDBACK_QUESTION_CONSTSUMPOINTS, Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTS,
                CONST_PARAMSNAMES_FEEDBACK_QUESTION_CONSTSUMDISTRIBUTEUNEVENLY, Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMDISTRIBUTEUNEVENLY
                );
    }

    @Override
    public String getQuestionWithoutExistingResponseSubmissionFormHtml(
            boolean sessionIsOpen, int qnIdx, int responseIdx, String courseId, int totalNumRecipients) {
        
        StringBuilder optionListHtml = new StringBuilder();
        String optionFragmentTemplate = FeedbackQuestionFormTemplates.CONSTSUM_SUBMISSION_FORM_OPTIONFRAGMENT;
        
        if (distributeToRecipients) {
            String optionFragment = 
                    FeedbackQuestionFormTemplates.populateTemplate(optionFragmentTemplate,
                            QUESTION_INDEX, Integer.toString(qnIdx),
                            RESPONSE_INDEX, Integer.toString(responseIdx),
                            OPTION_INDEX, "0",
                            DISABLED, sessionIsOpen ? "" : "disabled",
                            CONST_SUM_OPTION_VISIBILITY, "style=\"display:none\"",
                            CONST_SUM_OPTION_POINT, "",
                            CONST_PARAMSNAMES_FEEDBACK_RESPONSE_TEXT, Const.ParamsNames.FEEDBACK_RESPONSE_TEXT,
                            CONST_SUM_OPTION_VALUE, "");
            optionListHtml.append(optionFragment).append(Const.EOL);
        } else {
            for (int i = 0; i < constSumOptions.size(); i++) {
                String optionFragment = 
                        FeedbackQuestionFormTemplates.populateTemplate(optionFragmentTemplate,
                                QUESTION_INDEX, Integer.toString(qnIdx),
                                RESPONSE_INDEX, Integer.toString(responseIdx),
                                OPTION_INDEX, Integer.toString(i),
                                DISABLED, sessionIsOpen ? "" : "disabled",
                                CONST_SUM_OPTION_VISIBILITY, "",
                                CONST_SUM_OPTION_POINT, "",
                                CONST_PARAMSNAMES_FEEDBACK_RESPONSE_TEXT, Const.ParamsNames.FEEDBACK_RESPONSE_TEXT,
                                CONST_SUM_OPTION_VALUE,  Sanitizer.sanitizeForHtml(constSumOptions.get(i)));
                optionListHtml.append(optionFragment).append(Const.EOL);
            }
        }

        return FeedbackQuestionFormTemplates.populateTemplate(
                FeedbackQuestionFormTemplates.CONSTSUM_SUBMISSION_FORM,
                "${constSumSubmissionFormOptionFragments}", optionListHtml.toString(),
                QUESTION_INDEX, Integer.toString(qnIdx),
                RESPONSE_INDEX, Integer.toString(responseIdx),
                CONST_SUM_OPTION_VISIBILITY, distributeToRecipients ? "style=\"display:none\"" : "",
                CONST_SUM_TO_RECIPIENTS_VALUE, Boolean.toString(distributeToRecipients),
                "${constSumPointsPerOptionValue}", Boolean.toString(pointsPerOption),
                "${constSumNumOptionValue}", Integer.toString(constSumOptions.size()),
                "${constSumPointsValue}", Integer.toString(points),
                "${constSumUnevenDistributionValue}", Boolean.toString(forceUnevenDistribution),
                CONST_PARAMSNAMES_FEEDBACK_QUESTION_CONSTSUMTORECIPIENTS, Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMTORECIPIENTS,
                CONST_PARAMSNAME_FEEDBACK_QUESTION_CONSTSUMPOINTSPEROPTION, Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSPEROPTION,
                "${Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMNUMOPTION}", Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMNUMOPTION,
                CONST_PARAMSNAMES_FEEDBACK_QUESTION_CONSTSUMPOINTS, Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTS,
                CONST_PARAMSNAMES_FEEDBACK_QUESTION_CONSTSUMDISTRIBUTEUNEVENLY, Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMDISTRIBUTEUNEVENLY
                );
    }

    @Override
    public String getQuestionSpecificEditFormHtml(int questionNumber) {
        StringBuilder optionListHtml = new StringBuilder();
        String optionFragmentTemplate = FeedbackQuestionFormTemplates.CONSTSUM_EDIT_FORM_OPTIONFRAGMENT;
        for (int i = 0; i < numOfConstSumOptions; i++) {
            String optionFragment = 
                    FeedbackQuestionFormTemplates.populateTemplate(optionFragmentTemplate,
                            "${i}", Integer.toString(i),
                            CONST_SUM_OPTION_VALUE,  Sanitizer.sanitizeForHtml(constSumOptions.get(i)),
                            "${Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMOPTION}", Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMOPTION);

            optionListHtml.append(optionFragment).append(Const.EOL);
        }
        
        return FeedbackQuestionFormTemplates.populateTemplate(
                FeedbackQuestionFormTemplates.CONSTSUM_EDIT_FORM,
                "${constSumEditFormOptionFragments}", optionListHtml.toString(),
                "${questionNumber}", Integer.toString(questionNumber),
                "${Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED}", Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED,
                "${numOfConstSumOptions}", Integer.toString(numOfConstSumOptions),
                CONST_SUM_TO_RECIPIENTS_VALUE, Boolean.toString(distributeToRecipients),
                "${selectedConstSumPointsPerOption}", pointsPerOption ? "selected" : "",
                "${constSumOptionTableVisibility}", distributeToRecipients ? "style=\"display:none\"" : "",
                "${constSumPoints}", points == 0 ? "100" : Integer.toString(points),
                OPTION_RECIEPIENT_DISPLAY_NAME, distributeToRecipients ? "recipient" : "option",
                "${distributeUnevenly}", forceUnevenDistribution ? "checked" : "",
                CONST_PARAMSNAMES_FEEDBACK_QUESTION_CONSTSUMTORECIPIENTS, Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMTORECIPIENTS,
                CONST_PARAMSNAME_FEEDBACK_QUESTION_CONSTSUMPOINTSPEROPTION, Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSPEROPTION,
                CONST_PARAMSNAMES_FEEDBACK_QUESTION_CONSTSUMPOINTS, Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTS,
                CONST_PARAMSNAMES_FEEDBACK_QUESTION_CONSTSUMDISTRIBUTEUNEVENLY, Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMDISTRIBUTEUNEVENLY);

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

        if (this.distributeToRecipients) {
            additionalInfo.append(this.getQuestionTypeDisplayName()).append("<br>");
        } else if (numOfConstSumOptions > 0) {
            optionListHtml.append("<ul style=\"list-style-type: disc;margin-left: 20px;\" >");
            for (int i = 0; i < numOfConstSumOptions; i++) {
                String optionFragment = 
                        FeedbackQuestionFormTemplates.populateTemplate(optionFragmentTemplate,
                                "${msqChoiceValue}", constSumOptions.get(i));
                
                optionListHtml.append(optionFragment);
            }
            optionListHtml.append("</ul>");
            additionalInfo.append(FeedbackQuestionFormTemplates.populateTemplate(
                FeedbackQuestionFormTemplates.MSQ_ADDITIONAL_INFO,
                "${questionTypeName}", this.getQuestionTypeDisplayName(),
                "${msqAdditionalInfoFragments}", optionListHtml.toString()));
        
        }
        //Point information
        additionalInfo.append(pointsPerOption 
                              ? "Points per " + (distributeToRecipients ? "recipient" : "option") + ": " + points 
                              : "Total points: " + points);
        
        return FeedbackQuestionFormTemplates.populateTemplate(
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
                
                fragments.append(FeedbackQuestionFormTemplates.populateTemplate(FeedbackQuestionFormTemplates.CONSTSUM_RESULT_STATS_RECIPIENTFRAGMENT,
                        CONST_SUM_OPTION_VALUE,  Sanitizer.sanitizeForHtml(name),
                        "${team}", Sanitizer.sanitizeForHtml(teamName),
                        "${pointsReceived}", pointsReceived,
                        "${averagePoints}", df.format(average)));
            
            } else {
                String option = options.get(Integer.parseInt(entry.getKey()));
                
                fragments.append(FeedbackQuestionFormTemplates.populateTemplate(FeedbackQuestionFormTemplates.CONSTSUM_RESULT_STATS_OPTIONFRAGMENT,
                                    CONST_SUM_OPTION_VALUE,  Sanitizer.sanitizeForHtml(option),
                                    "${pointsReceived}", pointsReceived,
                                    "${averagePoints}", df.format(average)));
            }
        }
        
        if (distributeToRecipients) {
            return FeedbackQuestionFormTemplates.populateTemplate(FeedbackQuestionFormTemplates.CONSTSUM_RESULT_RECIPIENT_STATS,
                    OPTION_RECIEPIENT_DISPLAY_NAME, "Recipient",
                    "${fragments}", fragments.toString());
        }
        return FeedbackQuestionFormTemplates.populateTemplate(FeedbackQuestionFormTemplates.CONSTSUM_RESULT_OPTION_STATS,
                OPTION_RECIEPIENT_DISPLAY_NAME, "Option",
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
                        distributeToRecipients ? response.recipientEmail : String.valueOf(i);
                
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
        return "<option value=\"CONSTSUM_OPTION\">" + Const.FeedbackQuestionTypeNames.CONSTSUM_OPTION + "</option>" 
               + "<option value=\"CONSTSUM_RECIPIENT\">" + Const.FeedbackQuestionTypeNames.CONSTSUM_RECIPIENT + "</option>";
    }

    @Override
    public List<String> validateQuestionDetails() {
        List<String> errors = new ArrayList<String>();
        if (!distributeToRecipients && numOfConstSumOptions < Const.FeedbackQuestion.CONST_SUM_MIN_NUM_OF_OPTIONS) {
            errors.add(Const.FeedbackQuestion.CONST_SUM_ERROR_NOT_ENOUGH_OPTIONS + Const.FeedbackQuestion.CONST_SUM_MIN_NUM_OF_OPTIONS + ".");
        }
        
        if (points < Const.FeedbackQuestion.CONST_SUM_MIN_NUM_OF_POINTS) {
            errors.add(Const.FeedbackQuestion.CONST_SUM_ERROR_NOT_ENOUGH_POINTS + Const.FeedbackQuestion.CONST_SUM_MIN_NUM_OF_POINTS + ".");
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

}
