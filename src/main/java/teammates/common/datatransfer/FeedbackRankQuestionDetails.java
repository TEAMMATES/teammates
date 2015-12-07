package teammates.common.datatransfer;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
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

public class FeedbackRankQuestionDetails extends FeedbackQuestionDetails {
    
    public List<String> options;
    public boolean distributeToRecipients;
    
    public FeedbackRankQuestionDetails() {
        super(FeedbackQuestionType.RANK);
        
        this.options = new ArrayList<String>();
        this.distributeToRecipients = false;
    }

    public FeedbackRankQuestionDetails(String questionText,
                                       List<String> rankOptions) {
        super(FeedbackQuestionType.RANK, questionText);
        
        this.options = rankOptions;
        this.distributeToRecipients = false;     
    }

    public FeedbackRankQuestionDetails(String questionText) {
        super(FeedbackQuestionType.RANK, questionText);
        this.options = new ArrayList<String>();
        this.distributeToRecipients = true;
    }
    
    @Override
    public boolean extractQuestionDetails(Map<String, String[]> requestParameters,
                                          FeedbackQuestionType questionType) {
        
        List<String> options = new LinkedList<String>();
        String distributeToRecipientsString = null;

        boolean distributeToRecipients = false;
        
        distributeToRecipientsString = HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_RANKTORECIPIENTS);
        distributeToRecipients = distributeToRecipientsString != null && Boolean.parseBoolean(distributeToRecipientsString);
        
        if (!distributeToRecipients) {
            String numOptionsCreatedString = HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_RANKNUMOPTION);
            Assumption.assertNotNull("Null number of choice for Rank", numOptionsCreatedString);
            int numOptionsCreated = Integer.parseInt(numOptionsCreatedString);
            
            for (int i = 0; i < numOptionsCreated; i++) {
                String rankOption = HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_RANKOPTION + "-" + i);
                if (rankOption != null && !rankOption.trim().isEmpty()) {
                    options.add(rankOption);
                }
            }
        }
        this.setRankQuestionDetails(options);
        
        return true;
    }

    private void setRankQuestionDetails(List<String> options) {
        this.options = options;
        this.distributeToRecipients = options.isEmpty();
    }

    @Override
    public String getQuestionTypeDisplayName() {
        return distributeToRecipients ? Const.FeedbackQuestionTypeNames.RANK_RECIPIENT
                                      : Const.FeedbackQuestionTypeNames.RANK_OPTION; 
    }

    @Override
    public String getQuestionWithExistingResponseSubmissionFormHtml(
                        boolean sessionIsOpen, int qnIdx, int responseIdx, String courseId,
                        FeedbackResponseDetails existingResponseDetails) {
        
        FeedbackRankResponseDetails existingResponse = (FeedbackRankResponseDetails) existingResponseDetails;
        StringBuilder optionListHtml = new StringBuilder();
        String optionFragmentTemplate = FeedbackQuestionFormTemplates.RANK_SUBMISSION_FORM_OPTIONFRAGMENT;
        
        if (distributeToRecipients) {
            String optionFragment = 
                    FeedbackQuestionFormTemplates.populateTemplate(optionFragmentTemplate,
                            "${qnIdx}", Integer.toString(qnIdx),
                            "${responseIdx}", Integer.toString(responseIdx),
                            "${optionIdx}", "0",
                            "${disabled}", sessionIsOpen ? "" : "disabled=\"disabled\"",
                            "${rankOptionVisibility}", "style=\"display:none\"",
                            "${rankOptionPoint}", existingResponse.getAnswerString(),
                            "${Const.ParamsNames.FEEDBACK_RESPONSE_TEXT}", Const.ParamsNames.FEEDBACK_RESPONSE_TEXT,
                            "${rankOptionValue}", "");
            optionListHtml.append(optionFragment + Const.EOL);
        } else {
            for(int i = 0; i < options.size(); i++) {
                String optionFragment = 
                        FeedbackQuestionFormTemplates.populateTemplate(optionFragmentTemplate,
                                "${qnIdx}", Integer.toString(qnIdx),
                                "${responseIdx}", Integer.toString(responseIdx),
                                "${optionIdx}", Integer.toString(i),
                                "${disabled}", sessionIsOpen ? "" : "disabled=\"disabled\"",
                                "${rankOptionVisibility}", "",
                                "${rankOptionPoint}", Integer.toString(existingResponse.getAnswerList().get(i)),
                                "${Const.ParamsNames.FEEDBACK_RESPONSE_TEXT}", Const.ParamsNames.FEEDBACK_RESPONSE_TEXT,
                                "${rankOptionValue}",  Sanitizer.sanitizeForHtml(options.get(i)));
                optionListHtml.append(optionFragment + Const.EOL);
            }
        }
        
        
        String html = FeedbackQuestionFormTemplates.populateTemplate(
                FeedbackQuestionFormTemplates.RANK_SUBMISSION_FORM,
                "${rankSubmissionFormOptionFragments}", optionListHtml.toString(),
                "${qnIdx}", Integer.toString(qnIdx),
                "${responseIdx}", Integer.toString(responseIdx),
                "${rankOptionVisibility}", distributeToRecipients? "style=\"display:none\"" : "",
                "${rankToRecipientsValue}", (distributeToRecipients)? "true" : "false",
                "${Const.ParamsNames.FEEDBACK_QUESTION_RANKTORECIPIENTS}", Const.ParamsNames.FEEDBACK_QUESTION_RANKTORECIPIENTS,
                "${Const.ParamsNames.FEEDBACK_QUESTION_RANKNUMOPTION}", Const.ParamsNames.FEEDBACK_QUESTION_RANKNUMOPTION
                );
        
        return html;
    }

    @Override
    public String getQuestionWithoutExistingResponseSubmissionFormHtml(
            boolean sessionIsOpen, int qnIdx, int responseIdx, String courseId) {
        
        StringBuilder optionListHtml = new StringBuilder();
        String optionFragmentTemplate = FeedbackQuestionFormTemplates.RANK_SUBMISSION_FORM_OPTIONFRAGMENT;
        
        if(distributeToRecipients){
            String optionFragment = 
                    FeedbackQuestionFormTemplates.populateTemplate(optionFragmentTemplate,
                            "${qnIdx}", Integer.toString(qnIdx),
                            "${responseIdx}", Integer.toString(responseIdx),
                            "${optionIdx}", "0",
                            "${disabled}", sessionIsOpen ? "" : "disabled=\"disabled\"",
                            "${rankOptionVisibility}", "style=\"display:none\"",
                            "${rankOptionPoint}", "",
                            "${Const.ParamsNames.FEEDBACK_RESPONSE_TEXT}", Const.ParamsNames.FEEDBACK_RESPONSE_TEXT,
                            "${rankOptionValue}", "");
            optionListHtml.append(optionFragment + Const.EOL);
        } else {
            for(int i = 0; i <options.size(); i++) {
                String optionFragment = 
                        FeedbackQuestionFormTemplates.populateTemplate(optionFragmentTemplate,
                                "${qnIdx}", Integer.toString(qnIdx),
                                "${responseIdx}", Integer.toString(responseIdx),
                                "${optionIdx}", Integer.toString(i),
                                "${disabled}", sessionIsOpen ? "" : "disabled=\"disabled\"",
                                "${rankOptionVisibility}", "",
                                "${rankOptionPoint}", "",
                                "${Const.ParamsNames.FEEDBACK_RESPONSE_TEXT}", Const.ParamsNames.FEEDBACK_RESPONSE_TEXT,
                                "${rankOptionValue}",  Sanitizer.sanitizeForHtml(options.get(i)));
                optionListHtml.append(optionFragment + Const.EOL);
            }
        }
        
        
        String html = FeedbackQuestionFormTemplates.populateTemplate(
                FeedbackQuestionFormTemplates.RANK_SUBMISSION_FORM,
                "${rankSubmissionFormOptionFragments}", optionListHtml.toString(),
                "${qnIdx}", Integer.toString(qnIdx),
                "${responseIdx}", Integer.toString(responseIdx),
                "${rankOptionVisibility}", distributeToRecipients ? "style=\"display:none\"" : "",
                "${rankToRecipientsValue}", (distributeToRecipients) ? "true" : "false",
                "${Const.ParamsNames.FEEDBACK_QUESTION_RANKTORECIPIENTS}", Const.ParamsNames.FEEDBACK_QUESTION_RANKTORECIPIENTS,
                "${Const.ParamsNames.FEEDBACK_QUESTION_RANKNUMOPTION}", Const.ParamsNames.FEEDBACK_QUESTION_RANKNUMOPTION
                );
        
        return html;
    }

    @Override
    public String getQuestionSpecificEditFormHtml(int questionNumber) {
        StringBuilder optionListHtml = new StringBuilder();
        String optionFragmentTemplate = FeedbackQuestionFormTemplates.RANK_EDIT_FORM_OPTIONFRAGMENT;
        for(int i = 0; i < options.size(); i++) {
            String optionFragment = 
                    FeedbackQuestionFormTemplates.populateTemplate(optionFragmentTemplate,
                            "${i}", Integer.toString(i),
                            "${rankOptionValue}",  Sanitizer.sanitizeForHtml(options.get(i)),
                            "${Const.ParamsNames.FEEDBACK_QUESTION_RANKOPTION}", Const.ParamsNames.FEEDBACK_QUESTION_RANKOPTION);

            optionListHtml.append(optionFragment + Const.EOL);
        }
        
        String html = FeedbackQuestionFormTemplates.populateTemplate(
                FeedbackQuestionFormTemplates.RANK_EDIT_FORM,
                "${rankEditFormOptionFragments}", optionListHtml.toString(),
                "${questionNumber}", Integer.toString(questionNumber),
                "${Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED}", Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED,
                "${rankToRecipientsValue}", (distributeToRecipients == true) ? "true" : "false",
                "${rankOptionTableVisibility}", (distributeToRecipients == true) ? "style=\"display:none\"" : "",
                "${optionRecipientDisplayName}", (distributeToRecipients) ? "recipient": "option",
                "${Const.ParamsNames.FEEDBACK_QUESTION_RANKTORECIPIENTS}", Const.ParamsNames.FEEDBACK_QUESTION_RANKTORECIPIENTS);
        
        return html;
    }

    @Override
    public String getNewQuestionSpecificEditFormHtml() {
        // Add two empty options by default
        
        this.options.add("");
        this.options.add("");

        return "<div id=\"rankForm\">" +
                    this.getQuestionSpecificEditFormHtml(-1) + 
               "</div>";
    }

    @Override
    public String getQuestionAdditionalInfoHtml(int questionNumber,
            String additionalInfoId) {
        StringBuilder optionListHtml = new StringBuilder();
        String optionFragmentTemplate = FeedbackQuestionFormTemplates.MSQ_ADDITIONAL_INFO_FRAGMENT;
        String additionalInfo = "";
        
        if (this.distributeToRecipients) {
            additionalInfo = this.getQuestionTypeDisplayName() + "<br>";
        } else if (!options.isEmpty()) {
            optionListHtml.append("<ul style=\"list-style-type: disc;margin-left: 20px;\" >");
            for(int i = 0; i < options.size(); i++) {
                String optionFragment = 
                        FeedbackQuestionFormTemplates.populateTemplate(optionFragmentTemplate,
                                "${msqChoiceValue}", options.get(i));
                
                optionListHtml.append(optionFragment);
            }
            optionListHtml.append("</ul>");
            additionalInfo = FeedbackQuestionFormTemplates.populateTemplate(
                FeedbackQuestionFormTemplates.MSQ_ADDITIONAL_INFO,
                "${questionTypeName}", this.getQuestionTypeDisplayName(),
                "${msqAdditionalInfoFragments}", optionListHtml.toString());
        
        }

        
        String html = FeedbackQuestionFormTemplates.populateTemplate(
                FeedbackQuestionFormTemplates.FEEDBACK_QUESTION_ADDITIONAL_INFO,
                "${more}", "[more]",
                "${less}", "[less]",
                "${questionNumber}", Integer.toString(questionNumber),
                "${additionalInfoId}", additionalInfoId,
                "${questionAdditionalInfo}", additionalInfo);
        
        
        return html;
    }

    @Override
    public String getQuestionResultStatisticsHtml(
            List<FeedbackResponseAttributes> responses,
            FeedbackQuestionAttributes question,
            String studentEmail,
            FeedbackSessionResultsBundle bundle,
            String view) {
        
        if (view.equals("student") || responses.isEmpty()){
            return "";
        }
        
        String html = "";
        String fragments = "";
        
        Map<String, List<Integer>> optionPoints = generateOptionRanksMapping(responses);

        DecimalFormat df = new DecimalFormat("#.##");
        
        for(Entry<String, List<Integer>> entry : optionPoints.entrySet()) {
            
            List<Integer> points = entry.getValue();
            double average = computeAverage(points);
            String pointsReceived = getListOfRanksReceivedAsString(points);
            
            if (distributeToRecipients) {
                String participantIdentifier = entry.getKey();
                String name = bundle.getNameForEmail(participantIdentifier);
                String teamName = bundle.getTeamNameForEmail(participantIdentifier);
                
                fragments += FeedbackQuestionFormTemplates.populateTemplate(FeedbackQuestionFormTemplates.RANK_RESULT_STATS_RECIPIENTFRAGMENT,
                        "${rankOptionValue}",  Sanitizer.sanitizeForHtml(name),
                        "${team}", teamName,
                        "${pointsReceived}", pointsReceived,
                        "${averagePoints}", df.format(average));
            
            } else {
                String option = options.get(Integer.parseInt(entry.getKey()));
                
                fragments += FeedbackQuestionFormTemplates.populateTemplate(FeedbackQuestionFormTemplates.RANK_RESULT_STATS_OPTIONFRAGMENT,
                                    "${rankOptionValue}",  Sanitizer.sanitizeForHtml(option),
                                    "${pointsReceived}", pointsReceived,
                                    "${averagePoints}", df.format(average));
            }
        }
        
        if (distributeToRecipients) {
            html = FeedbackQuestionFormTemplates.populateTemplate(FeedbackQuestionFormTemplates.RANK_RESULT_RECIPIENT_STATS,
                    "${optionRecipientDisplayName}", "Recipient",
                    "${fragments}", fragments);
        } else {
            html = FeedbackQuestionFormTemplates.populateTemplate(FeedbackQuestionFormTemplates.RANK_RESULT_OPTION_STATS,
                    "${optionRecipientDisplayName}", "Option",
                    "${fragments}", fragments);
        }
        
        
        return html;
    }
    
    
    @Override
    public String getQuestionResultStatisticsCsv(
            List<FeedbackResponseAttributes> responses,
            FeedbackQuestionAttributes question,
            FeedbackSessionResultsBundle bundle) {
        if (responses.isEmpty()){
            return "";
        }
        
        String csv = "";
        String fragments = "";
        Map<String, List<Integer>> optionPoints = generateOptionRanksMapping(responses);

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
            fragments += option + "," + 
                         df.format(average) + Const.EOL;
            
        }
        
        csv += (distributeToRecipients? "Team, Recipient":"Option") + ", Average Points" + Const.EOL; 
        csv += fragments + Const.EOL;
        
        return csv;
    }

    /**
     * From the feedback responses, generate a mapping of the option to a list of 
     * ranks received for that option.
     * The key of the map returned is the option name / recipient's participant identifier.
     * The values of the map are list of points received by the key.   
     * @param responses  a list of responses 
     */
    private Map<String, List<Integer>> generateOptionRanksMapping(
            List<FeedbackResponseAttributes> responses) {
        
        Map<String, List<Integer>> optionPoints = new HashMap<String, List<Integer>>();
        for(FeedbackResponseAttributes response : responses) {
            FeedbackConstantSumResponseDetails frd = (FeedbackConstantSumResponseDetails)response.getResponseDetails();
            
            for (int i = 0; i < frd.getAnswerList().size(); i++) {
                String optionReceivingPoints = distributeToRecipients ? 
                                               response.recipientEmail : 
                                               String.valueOf(i);
                
                int ranksReceived = frd.getAnswerList().get(i);
                updateOptionRanksMapping(optionPoints, optionReceivingPoints, ranksReceived);
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
    private void updateOptionRanksMapping(
            Map<String, List<Integer>> optionPoints,
            String optionReceivingPoints, int pointsReceived) {
        List<Integer> points = optionPoints.get(optionReceivingPoints);
        if(points == null){
            points = new ArrayList<Integer>();
            optionPoints.put(optionReceivingPoints, points);
        }
        
        points.add(pointsReceived);
    }

    /**
     * Returns the list of points as as string to display
     * @param points
     */
    private String getListOfRanksReceivedAsString(List<Integer> points) {
        Collections.sort(points);
        String pointsReceived = "";
        if(points.size() > 10){
            for(int i = 0; i < 5; i++){
                pointsReceived += points.get(i) + " , ";
            }
            pointsReceived += "...";
            for(int i = points.size() - 5; i < points.size(); i++){
                pointsReceived += " , " + points.get(i);
            }
        } else {
            for(int i = 0; i < points.size(); i++){
                pointsReceived += points.get(i);
                if(i != points.size() - 1){
                    pointsReceived += " , ";
                }
            }
        }
        return pointsReceived;
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
    public boolean isChangesRequiresResponseDeletion(FeedbackQuestionDetails newDetails) {
        FeedbackRankQuestionDetails newRankQuestionDetails = (FeedbackRankQuestionDetails) newDetails;

        if (this.options.size() != newRankQuestionDetails.options.size() 
            || !this.options.containsAll(newRankQuestionDetails.options) 
            || !newRankQuestionDetails.options.containsAll(this.options)) {
            return true;
        }
        
        if (this.distributeToRecipients != newRankQuestionDetails.distributeToRecipients) {
            return true;
        }
        
        return false;
    }
    

    @Override
    public String getCsvHeader() {
        if (distributeToRecipients) {
            return "Feedback";
        } else {
            List<String> sanitizedOptions = Sanitizer.sanitizeListForCsv(options);
            return "Feedbacks:," + StringHelper.toString(sanitizedOptions, ",");
        }
    }

    @Override
    public String getQuestionTypeChoiceOption() {
        // Rank question type has two subtypes for user to select.
        return "<option value=\"RANK_OPTION\">" + Const.FeedbackQuestionTypeNames.RANK_OPTION + "</option>" 
             + "<option value=\"RANK_RECIPIENT\">" + Const.FeedbackQuestionTypeNames.RANK_RECIPIENT + "</option>";
    }

    @Override
    public List<String> validateQuestionDetails() {
        List<String> errors = new ArrayList<String>();
        if (!distributeToRecipients && options.size() < Const.FeedbackQuestion.CONST_SUM_MIN_NUM_OF_OPTIONS){
            errors.add(Const.FeedbackQuestion.CONST_SUM_ERROR_NOT_ENOUGH_OPTIONS + Const.FeedbackQuestion.CONST_SUM_MIN_NUM_OF_OPTIONS+".");
        }
        return errors;
    }

    @Override
    public List<String> validateResponseAttributes(
            List<FeedbackResponseAttributes> responses,
            int numRecipients) {
        List<String> errors = new ArrayList<String>();
        
        if (responses.isEmpty()) {
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
        numRecipients = numOfResponseSpecific;
        
        Set<Integer> responseRank = new HashSet<>();
        for (FeedbackResponseAttributes response : responses) {
            FeedbackRankResponseDetails frd = (FeedbackRankResponseDetails) response.getResponseDetails();
            
            if (!distributeToRecipients) {
                for (int i : frd.getAnswerList()) {
                    if (!responseRank.add(i)) {
                        errors.add("Duplicate rank");
                    }
                }
            } else {
                if (!responseRank.add(frd.getAnswerList().get(0))) {
                    errors.add("Duplicate rank");
                }
            }
        }
        
        return errors;
    }

}
