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
    private int numOfConstSumOptions;
    private List<String> constSumOptions;
    private boolean distributeToRecipients;
    private boolean pointsPerOption;
    private boolean forceUnevenDistribution;
    private int points;
    
    public FeedbackConstantSumQuestionDetails() {
        super(FeedbackQuestionType.CONSTSUM);
        
        this.setNumOfConstSumOptions(0);
        this.setConstSumOptions(new ArrayList<String>());
        this.setDistributeToRecipients(false);
        this.setPointsPerOption(false);
        this.setPoints(100);
        this.setForceUnevenDistribution(false);
    }

    public FeedbackConstantSumQuestionDetails(String questionText,
            List<String> constSumOptions,
            boolean pointsPerOption, int points, boolean unevenDistribution) {
        super(FeedbackQuestionType.CONSTSUM, questionText);
        
        this.setNumOfConstSumOptions(constSumOptions.size());
        this.setConstSumOptions(constSumOptions);
        this.setDistributeToRecipients(false);
        this.setPointsPerOption(pointsPerOption);
        this.setPoints(points);
        this.setForceUnevenDistribution(unevenDistribution);
        
    }

    public FeedbackConstantSumQuestionDetails(String questionText,
            boolean pointsPerOption, int points, boolean unevenDistribution) {
        super(FeedbackQuestionType.CONSTSUM, questionText);
        
        this.setNumOfConstSumOptions(0);
        this.setConstSumOptions(new ArrayList<String>());
        this.setDistributeToRecipients(true);
        this.setPointsPerOption(pointsPerOption);
        this.setPoints(points);
        this.setForceUnevenDistribution(unevenDistribution);
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
                    setNumOfConstSumOptions(getNumOfConstSumOptions() + 1);
                }
            }
            this.setConstantSumQuestionDetails(constSumOptions, pointsPerOption, points, forceUnevenDistribution);
        }
        return true;
    }

    private void setConstantSumQuestionDetails(
            List<String> constSumOptions, boolean pointsPerOption,
            int points, boolean unevenDistribution) {
        
        this.setNumOfConstSumOptions(constSumOptions.size());
        this.setConstSumOptions(constSumOptions);
        this.setDistributeToRecipients(false);
        this.setPointsPerOption(pointsPerOption);
        this.setPoints(points);
        this.setForceUnevenDistribution(unevenDistribution);
        
    }

    private void setConstantSumQuestionDetails(boolean pointsPerOption,
            int points, boolean unevenDistribution) {
        
        this.setNumOfConstSumOptions(0);
        this.setConstSumOptions(new ArrayList<String>());
        this.setDistributeToRecipients(true);
        this.setPointsPerOption(pointsPerOption);
        this.setPoints(points);
        this.setForceUnevenDistribution(unevenDistribution);
    }

    @Override
    public String getQuestionTypeDisplayName() {
        if (isDistributeToRecipients()) {
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
        
        if (isDistributeToRecipients()) {
            String optionFragment = 
                    FeedbackQuestionFormTemplates.populateTemplate(optionFragmentTemplate,
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
            for (int i = 0; i < getConstSumOptions().size(); i++) {
                String optionFragment = 
                        FeedbackQuestionFormTemplates.populateTemplate(optionFragmentTemplate,
                                "${qnIdx}", Integer.toString(qnIdx),
                                "${responseIdx}", Integer.toString(responseIdx),
                                "${optionIdx}", Integer.toString(i),
                                "${disabled}", sessionIsOpen ? "" : "disabled",
                                "${constSumOptionVisibility}", "",
                                "${constSumOptionPoint}", Integer.toString(existingConstSumResponse.getAnswerList().get(i)),
                                "${Const.ParamsNames.FEEDBACK_RESPONSE_TEXT}", Const.ParamsNames.FEEDBACK_RESPONSE_TEXT,
                                "${constSumOptionValue}",  Sanitizer.sanitizeForHtml(getConstSumOptions().get(i)));
                optionListHtml.append(optionFragment).append(Const.EOL);
            }
        }

        String html = FeedbackQuestionFormTemplates.populateTemplate(
                FeedbackQuestionFormTemplates.CONSTSUM_SUBMISSION_FORM,
                "${constSumSubmissionFormOptionFragments}", optionListHtml.toString(),
                "${qnIdx}", Integer.toString(qnIdx),
                "${responseIdx}", Integer.toString(responseIdx),
                "${constSumOptionVisibility}", isDistributeToRecipients() ? "style=\"display:none\"" : "",
                "${constSumToRecipientsValue}", Boolean.toString(isDistributeToRecipients()),
                "${constSumPointsPerOptionValue}", Boolean.toString(isPointsPerOption()),
                "${constSumNumOptionValue}", Integer.toString(getConstSumOptions().size()),
                "${constSumPointsValue}", Integer.toString(getPoints()),
                "${constSumUnevenDistributionValue}", Boolean.toString(isForceUnevenDistribution()),
                "${Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMTORECIPIENTS}", Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMTORECIPIENTS,
                "${Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSPEROPTION}", Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSPEROPTION,
                "${Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMNUMOPTION}", Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMNUMOPTION,
                "${Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTS}", Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTS,
                "${Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMDISTRIBUTEUNEVENLY}", Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMDISTRIBUTEUNEVENLY
                );
        
        return html;
    }

    @Override
    public String getQuestionWithoutExistingResponseSubmissionFormHtml(
            boolean sessionIsOpen, int qnIdx, int responseIdx, String courseId, int totalNumRecipients) {
        
        StringBuilder optionListHtml = new StringBuilder();
        String optionFragmentTemplate = FeedbackQuestionFormTemplates.CONSTSUM_SUBMISSION_FORM_OPTIONFRAGMENT;
        
        if (isDistributeToRecipients()) {
            String optionFragment = 
                    FeedbackQuestionFormTemplates.populateTemplate(optionFragmentTemplate,
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
            for (int i = 0; i < getConstSumOptions().size(); i++) {
                String optionFragment = 
                        FeedbackQuestionFormTemplates.populateTemplate(optionFragmentTemplate,
                                "${qnIdx}", Integer.toString(qnIdx),
                                "${responseIdx}", Integer.toString(responseIdx),
                                "${optionIdx}", Integer.toString(i),
                                "${disabled}", sessionIsOpen ? "" : "disabled",
                                "${constSumOptionVisibility}", "",
                                "${constSumOptionPoint}", "",
                                "${Const.ParamsNames.FEEDBACK_RESPONSE_TEXT}", Const.ParamsNames.FEEDBACK_RESPONSE_TEXT,
                                "${constSumOptionValue}",  Sanitizer.sanitizeForHtml(getConstSumOptions().get(i)));
                optionListHtml.append(optionFragment).append(Const.EOL);
            }
        }

        String html = FeedbackQuestionFormTemplates.populateTemplate(
                FeedbackQuestionFormTemplates.CONSTSUM_SUBMISSION_FORM,
                "${constSumSubmissionFormOptionFragments}", optionListHtml.toString(),
                "${qnIdx}", Integer.toString(qnIdx),
                "${responseIdx}", Integer.toString(responseIdx),
                "${constSumOptionVisibility}", isDistributeToRecipients() ? "style=\"display:none\"" : "",
                "${constSumToRecipientsValue}", Boolean.toString(isDistributeToRecipients()),
                "${constSumPointsPerOptionValue}", Boolean.toString(isPointsPerOption()),
                "${constSumNumOptionValue}", Integer.toString(getConstSumOptions().size()),
                "${constSumPointsValue}", Integer.toString(getPoints()),
                "${constSumUnevenDistributionValue}", Boolean.toString(isForceUnevenDistribution()),
                "${Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMTORECIPIENTS}", Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMTORECIPIENTS,
                "${Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSPEROPTION}", Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSPEROPTION,
                "${Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMNUMOPTION}", Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMNUMOPTION,
                "${Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTS}", Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTS,
                "${Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMDISTRIBUTEUNEVENLY}", Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMDISTRIBUTEUNEVENLY
                );
        
        return html;
    }

    @Override
    public String getQuestionSpecificEditFormHtml(int questionNumber) {
        StringBuilder optionListHtml = new StringBuilder();
        String optionFragmentTemplate = FeedbackQuestionFormTemplates.CONSTSUM_EDIT_FORM_OPTIONFRAGMENT;
        for (int i = 0; i < getNumOfConstSumOptions(); i++) {
            String optionFragment = 
                    FeedbackQuestionFormTemplates.populateTemplate(optionFragmentTemplate,
                            "${i}", Integer.toString(i),
                            "${constSumOptionValue}",  Sanitizer.sanitizeForHtml(getConstSumOptions().get(i)),
                            "${Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMOPTION}", Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMOPTION);

            optionListHtml.append(optionFragment).append(Const.EOL);
        }
        
        String html = FeedbackQuestionFormTemplates.populateTemplate(
                FeedbackQuestionFormTemplates.CONSTSUM_EDIT_FORM,
                "${constSumEditFormOptionFragments}", optionListHtml.toString(),
                "${questionNumber}", Integer.toString(questionNumber),
                "${Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED}", Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED,
                "${numOfConstSumOptions}", Integer.toString(getNumOfConstSumOptions()),
                "${constSumToRecipientsValue}", Boolean.toString(isDistributeToRecipients()),
                "${selectedConstSumPointsPerOption}", isPointsPerOption() ? "selected" : "",
                "${constSumOptionTableVisibility}", isDistributeToRecipients() ? "style=\"display:none\"" : "",
                "${constSumPoints}", getPoints() == 0 ? "100" : Integer.toString(getPoints()),
                "${optionRecipientDisplayName}", isDistributeToRecipients() ? "recipient" : "option",
                "${distributeUnevenly}", isForceUnevenDistribution() ? "checked" : "",
                "${Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMTORECIPIENTS}", Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMTORECIPIENTS,
                "${Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSPEROPTION}", Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSPEROPTION,
                "${Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTS}", Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTS,
                "${Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMDISTRIBUTEUNEVENLY}", Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMDISTRIBUTEUNEVENLY);
        
        return html;
    }

    @Override
    public String getNewQuestionSpecificEditFormHtml() {
        // Add two empty options by default
        this.setNumOfConstSumOptions(2);
        this.getConstSumOptions().add("");
        this.getConstSumOptions().add("");

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

        if (this.isDistributeToRecipients()) {
            additionalInfo.append(this.getQuestionTypeDisplayName()).append("<br>");
        } else if (getNumOfConstSumOptions() > 0) {
            optionListHtml.append("<ul style=\"list-style-type: disc;margin-left: 20px;\" >");
            for (int i = 0; i < getNumOfConstSumOptions(); i++) {
                String optionFragment = 
                        FeedbackQuestionFormTemplates.populateTemplate(optionFragmentTemplate,
                                "${msqChoiceValue}", getConstSumOptions().get(i));
                
                optionListHtml.append(optionFragment);
            }
            optionListHtml.append("</ul>");
            additionalInfo.append(FeedbackQuestionFormTemplates.populateTemplate(
                FeedbackQuestionFormTemplates.MSQ_ADDITIONAL_INFO,
                "${questionTypeName}", this.getQuestionTypeDisplayName(),
                "${msqAdditionalInfoFragments}", optionListHtml.toString()));
        
        }
        //Point information
        additionalInfo.append(isPointsPerOption() 
                              ? "Points per " + (isDistributeToRecipients() ? "recipient" : "option") + ": " + getPoints() 
                              : "Total points: " + getPoints());
        
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
        List<String> options = getConstSumOptions();
        
        Map<String, List<Integer>> optionPoints = generateOptionPointsMapping(responses);

        DecimalFormat df = new DecimalFormat("#.##");
        
        for (Entry<String, List<Integer>> entry : optionPoints.entrySet()) {
            
            List<Integer> points = entry.getValue();
            double average = computeAverage(points);
            String pointsReceived = getListOfPointsAsString(points);
            
            if (isDistributeToRecipients()) {
                String participantIdentifier = entry.getKey();
                String name = bundle.getNameForEmail(participantIdentifier);
                String teamName = bundle.getTeamNameForEmail(participantIdentifier);
                
                fragments.append(FeedbackQuestionFormTemplates.populateTemplate(FeedbackQuestionFormTemplates.CONSTSUM_RESULT_STATS_RECIPIENTFRAGMENT,
                        "${constSumOptionValue}",  Sanitizer.sanitizeForHtml(name),
                        "${team}", Sanitizer.sanitizeForHtml(teamName),
                        "${pointsReceived}", pointsReceived,
                        "${averagePoints}", df.format(average)));
            
            } else {
                String option = options.get(Integer.parseInt(entry.getKey()));
                
                fragments.append(FeedbackQuestionFormTemplates.populateTemplate(FeedbackQuestionFormTemplates.CONSTSUM_RESULT_STATS_OPTIONFRAGMENT,
                                    "${constSumOptionValue}",  Sanitizer.sanitizeForHtml(option),
                                    "${pointsReceived}", pointsReceived,
                                    "${averagePoints}", df.format(average)));
            }
        }
        
        if (isDistributeToRecipients()) {
            return FeedbackQuestionFormTemplates.populateTemplate(FeedbackQuestionFormTemplates.CONSTSUM_RESULT_RECIPIENT_STATS,
                    "${optionRecipientDisplayName}", "Recipient",
                    "${fragments}", fragments.toString());
        }
        return FeedbackQuestionFormTemplates.populateTemplate(FeedbackQuestionFormTemplates.CONSTSUM_RESULT_OPTION_STATS,
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
        List<String> options = getConstSumOptions();
        Map<String, List<Integer>> optionPoints = generateOptionPointsMapping(responses);

        DecimalFormat df = new DecimalFormat("#.##");
        
        for (Entry<String, List<Integer>> entry : optionPoints.entrySet()) {
            String option;
            if (isDistributeToRecipients()) {
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
        
        return (isDistributeToRecipients() ? "Team, Recipient" : "Option")
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
                        isDistributeToRecipients() ? response.recipientEmail : String.valueOf(i);
                
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

        if (this.getNumOfConstSumOptions() != newConstSumDetails.getNumOfConstSumOptions()
            || !this.getConstSumOptions().containsAll(newConstSumDetails.getConstSumOptions())
            || !newConstSumDetails.getConstSumOptions().containsAll(this.getConstSumOptions())) {
            return true;
        }
        
        if (this.isDistributeToRecipients() != newConstSumDetails.isDistributeToRecipients()) {
            return true;
        }
        
        if (this.getPoints() != newConstSumDetails.getPoints()) {
            return true;
        }
        
        if (this.isPointsPerOption() != newConstSumDetails.isPointsPerOption()) {
            return true;
        }
        
        return this.isForceUnevenDistribution() != newConstSumDetails.isForceUnevenDistribution();
    }

    @Override
    public String getCsvHeader() {
        if (isDistributeToRecipients()) {
            return "Feedback";
        }
        List<String> sanitizedOptions = Sanitizer.sanitizeListForCsv(getConstSumOptions());
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
        if (!isDistributeToRecipients() && getNumOfConstSumOptions() < Const.FeedbackQuestion.CONST_SUM_MIN_NUM_OF_OPTIONS) {
            errors.add(Const.FeedbackQuestion.CONST_SUM_ERROR_NOT_ENOUGH_OPTIONS + Const.FeedbackQuestion.CONST_SUM_MIN_NUM_OF_OPTIONS + ".");
        }
        
        if (getPoints() < Const.FeedbackQuestion.CONST_SUM_MIN_NUM_OF_POINTS) {
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
        
        int numOptions = isDistributeToRecipients() ? numOfResponseSpecific : getConstSumOptions().size();
        int totalPoints = isPointsPerOption() ? getPoints() * numOptions : getPoints();
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
            if (isDistributeToRecipients()) {
                sum += frd.getAnswerList().get(0);
            } else {
                sum = 0;
                for (Integer i : frd.getAnswerList()) {
                    sum += i;
                }
                if (sum != totalPoints || frd.getAnswerList().size() != getConstSumOptions().size()) {
                    errors.add(Const.FeedbackQuestion.CONST_SUM_ERROR_MISMATCH);
                    return errors;
                }
            }
            
            Set<Integer> answerSet = new HashSet<Integer>();
            if (this.isForceUnevenDistribution()) {
                for (int i : frd.getAnswerList()) {
                    if (answerSet.contains(i)) {
                        errors.add(Const.FeedbackQuestion.CONST_SUM_ERROR_UNIQUE);
                        return errors;
                    }
                    answerSet.add(i);
                }
            }
        }
        if (isDistributeToRecipients() && sum != totalPoints) {
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

    public void setNumOfConstSumOptions(int numOfConstSumOptions) {
        this.numOfConstSumOptions = numOfConstSumOptions;
    }

    public List<String> getConstSumOptions() {
        return constSumOptions;
    }

    public void setConstSumOptions(List<String> constSumOptions) {
        this.constSumOptions = constSumOptions;
    }

    public boolean isDistributeToRecipients() {
        return distributeToRecipients;
    }

    public void setDistributeToRecipients(boolean distributeToRecipients) {
        this.distributeToRecipients = distributeToRecipients;
    }

    public boolean isPointsPerOption() {
        return pointsPerOption;
    }

    public void setPointsPerOption(boolean pointsPerOption) {
        this.pointsPerOption = pointsPerOption;
    }

    public boolean isForceUnevenDistribution() {
        return forceUnevenDistribution;
    }

    public void setForceUnevenDistribution(boolean forceUnevenDistribution) {
        this.forceUnevenDistribution = forceUnevenDistribution;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

}
