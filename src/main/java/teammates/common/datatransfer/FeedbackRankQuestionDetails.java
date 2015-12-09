package teammates.common.datatransfer;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
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
import teammates.ui.controller.PageData;
import teammates.ui.template.ElementTag;

public class FeedbackRankQuestionDetails extends FeedbackQuestionDetails {
    
    public transient static final int MIN_NUM_OF_OPTIONS = 2;
    public transient static final String ERROR_NOT_ENOUGH_OPTIONS 
            = "Too little options for " + Const.FeedbackQuestionTypeNames.RANK_OPTION 
            + ". Minimum number of options is: ";
    
    public List<String> options;
    public boolean isRankRecipients;
    
    public boolean areDuplicatesAllowed;
    
    public FeedbackRankQuestionDetails() {
        super(FeedbackQuestionType.RANK);
        
        this.options = new ArrayList<String>();
        this.isRankRecipients = false;
    }

    public FeedbackRankQuestionDetails(String questionText,
                                       List<String> rankOptions,
                                       int maxRank) {
        super(FeedbackQuestionType.RANK, questionText);
        
        this.options = rankOptions;
        this.isRankRecipients = false;
    }

    
    @Override
    public boolean extractQuestionDetails(Map<String, String[]> requestParameters,
                                          FeedbackQuestionType questionType) {
        
        List<String> options = new ArrayList<>();

        String distributeToRecipientsString 
            = HttpRequestHelper.getValueFromParamMap(requestParameters, 
                                                     Const.ParamsNames.FEEDBACK_QUESTION_RANKTORECIPIENTS);
        boolean isDistributeToRecipients = distributeToRecipientsString != null 
                                        && Boolean.parseBoolean(distributeToRecipientsString);
        
        String areDuplicatesAllowedString 
            = HttpRequestHelper.getValueFromParamMap(requestParameters, 
                                                     Const.ParamsNames.FEEDBACK_QUESTION_RANKISDUPLICATESALLOWED);
        boolean areDuplicatesAllowed = areDuplicatesAllowedString != null 
                                    && areDuplicatesAllowedString.equals("on");
        
        if (!isDistributeToRecipients) {
            String numOptionsCreatedString = HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED);
            Assumption.assertNotNull("Null number of choice for Rank", numOptionsCreatedString);
            int numOptionsCreated = Integer.parseInt(numOptionsCreatedString);
            
            for (int i = 0; i < numOptionsCreated; i++) {
                String rankOption = HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_RANKOPTION + "-" + i);
                if (rankOption != null && !rankOption.trim().isEmpty()) {
                    options.add(rankOption);
                }
            }
        }
        this.initialiseQuestionDetails(options, areDuplicatesAllowed);
        
        return true;
    }

    private void initialiseQuestionDetails(List<String> options, boolean areDuplicatesAllowed) {
        this.options = options;
        this.isRankRecipients = options.isEmpty();
        this.areDuplicatesAllowed = areDuplicatesAllowed;
    }

    @Override
    public String getQuestionTypeDisplayName() {
        return isRankRecipients ? Const.FeedbackQuestionTypeNames.RANK_RECIPIENT
                                : Const.FeedbackQuestionTypeNames.RANK_OPTION; 
    }

    @Override
    public String getQuestionWithExistingResponseSubmissionFormHtml(
                        boolean sessionIsOpen, int qnIdx, int responseIdx, String courseId,
                        int totalNumRecipients,
                        FeedbackResponseDetails existingResponseDetails) {
        
        FeedbackRankResponseDetails existingResponse = (FeedbackRankResponseDetails) existingResponseDetails;
        StringBuilder optionListHtml = new StringBuilder();
        String optionFragmentTemplate = FeedbackQuestionFormTemplates.RANK_SUBMISSION_FORM_OPTIONFRAGMENT;
        
        if (isRankRecipients) {
            String optionFragment = 
                    FeedbackQuestionFormTemplates.populateTemplate(optionFragmentTemplate,
                            "${qnIdx}", Integer.toString(qnIdx),
                            "${responseIdx}", Integer.toString(responseIdx),
                            "${optionIdx}", "0",
                            "${disabled}", sessionIsOpen ? "" : "disabled=\"disabled\"",
                            "${rankOptionVisibility}", "style=\"display:none\"",
                            "${options}", getSubmissionOptionsHtmlForRankingRecipients(totalNumRecipients, existingResponse.getAnswerList().get(0)),
                            "${Const.ParamsNames.FEEDBACK_RESPONSE_TEXT}", Const.ParamsNames.FEEDBACK_RESPONSE_TEXT,
                            "${rankOptionValue}", "");
            optionListHtml.append(optionFragment + Const.EOL);
        } else {
            for (int i = 0; i < options.size(); i++) {
                String optionFragment = 
                        FeedbackQuestionFormTemplates.populateTemplate(optionFragmentTemplate,
                                "${qnIdx}", Integer.toString(qnIdx),
                                "${responseIdx}", Integer.toString(responseIdx),
                                "${optionIdx}", Integer.toString(i),
                                "${disabled}", sessionIsOpen ? "" : "disabled=\"disabled\"",
                                "${rankOptionVisibility}", "",
                                "${options}",getSubmissionOptionsHtmlForRankingOptions(existingResponse.getAnswerList().get(i)),
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
                "${rankOptionVisibility}", isRankRecipients? "style=\"display:none\"" : "",
                "${Const.ParamsNames.FEEDBACK_QUESTION_RANKTORECIPIENTS}", Const.ParamsNames.FEEDBACK_QUESTION_RANKTORECIPIENTS,
                "${rankToRecipientsValue}", Boolean.toString(isRankRecipients),
                "${Const.ParamsNames.FEEDBACK_QUESTION_RANKNUMOPTION}", Const.ParamsNames.FEEDBACK_QUESTION_RANKNUMOPTIONS,
                "${rankNumOptionValue}", Integer.toString(options.size()),
                "${Const.ParamsNames.FEEDBACK_QUESTION_RANKISDUPLICATESALLOWED}", Const.ParamsNames.FEEDBACK_QUESTION_RANKISDUPLICATESALLOWED,
                "${areDuplicatesAllowedValue}", Boolean.toString(areDuplicatesAllowed)
                );
        
        return html;
    }

    @Override
    public String getQuestionWithoutExistingResponseSubmissionFormHtml(
            boolean sessionIsOpen, int qnIdx, int responseIdx, String courseId, int totalNumRecipients) {
        
        StringBuilder optionListHtml = new StringBuilder();
        String optionFragmentTemplate = FeedbackQuestionFormTemplates.RANK_SUBMISSION_FORM_OPTIONFRAGMENT;
        
        if (isRankRecipients) {
            String optionFragment = 
                    FeedbackQuestionFormTemplates.populateTemplate(optionFragmentTemplate,
                            "${qnIdx}", Integer.toString(qnIdx),
                            "${responseIdx}", Integer.toString(responseIdx),
                            "${optionIdx}", "0",
                            "${disabled}", sessionIsOpen ? "" : "disabled=\"disabled\"",
                            "${rankOptionVisibility}", "style=\"display:none\"",
                            "${options}", getSubmissionOptionsHtmlForRankingRecipients(totalNumRecipients, Const.INT_UNINITIALIZED),
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
                                "${options}", getSubmissionOptionsHtmlForRankingOptions(Const.INT_UNINITIALIZED),
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
                            "${rankOptionVisibility}", isRankRecipients ? "style=\"display:none\"" : "",
                            "${rankToRecipientsValue}", Boolean.toString(isRankRecipients),
                            "${Const.ParamsNames.FEEDBACK_QUESTION_RANKTORECIPIENTS}", Const.ParamsNames.FEEDBACK_QUESTION_RANKTORECIPIENTS,
                            "${Const.ParamsNames.FEEDBACK_QUESTION_RANKNUMOPTION}", Const.ParamsNames.FEEDBACK_QUESTION_RANKNUMOPTIONS,
                            "${rankNumOptionValue}", Integer.toString(options.size()),
                            "${Const.ParamsNames.FEEDBACK_QUESTION_RANKISDUPLICATESALLOWED}", Const.ParamsNames.FEEDBACK_QUESTION_RANKISDUPLICATESALLOWED,
                            "${areDuplicatesAllowedValue}", Boolean.toString(areDuplicatesAllowed)
                            );
        
        return html;
    }
    
    private String getSubmissionOptionsHtmlForRankingOptions(int rankGiven) {
        StringBuilder result = new StringBuilder();
        if (!isRankRecipients) {
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
        } 
       
        return result.toString();
    }
    
    private String getSubmissionOptionsHtmlForRankingRecipients(int totalNumRecipients, int rankGiven) {       
        
        StringBuilder result = new StringBuilder();
        if (isRankRecipients) {
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
        } 
       
        return result.toString();
    }

    @Override
    public String getQuestionSpecificEditFormHtml(int questionNumber) {
        
        if (this.isRankRecipients) {
            return FeedbackQuestionFormTemplates.populateTemplate(
                    FeedbackQuestionFormTemplates.RANK_EDIT_RECIPIENTS_FORM,
                    "${questionNumber}", Integer.toString(questionNumber),
                    "${optionRecipientDisplayName}", "recipient",
                    "${Const.ParamsNames.FEEDBACK_QUESTION_RANKISDUPLICATESALLOWED}", Const.ParamsNames.FEEDBACK_QUESTION_RANKISDUPLICATESALLOWED,
                    "${areDuplicatesAllowedChecked}", areDuplicatesAllowed ? "checked=\"checked\"" : "");
        } else {
            StringBuilder optionListHtml = new StringBuilder();
            String optionFragmentTemplate = FeedbackQuestionFormTemplates.RANK_EDIT_FORM_OPTIONFRAGMENT;
            
            for (int i = 0; i < options.size(); i++) {
                String optionFragment = 
                        FeedbackQuestionFormTemplates.populateTemplate(optionFragmentTemplate,
                                "${i}", Integer.toString(i),
                                "${rankOptionValue}",  Sanitizer.sanitizeForHtml(options.get(i)),
                                "${Const.ParamsNames.FEEDBACK_QUESTION_RANKOPTION}", Const.ParamsNames.FEEDBACK_QUESTION_RANKOPTION);
    
                optionListHtml.append(optionFragment + Const.EOL);
            }
            
            return FeedbackQuestionFormTemplates.populateTemplate(
                    FeedbackQuestionFormTemplates.RANK_EDIT_OPTIONS_FORM,
                    "${rankEditFormOptionFragments}", optionListHtml.toString(),
                    "${questionNumber}", Integer.toString(questionNumber),
                    "${Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED}", Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED,
                    "${numOfRankOptions}", String.valueOf(options.size()), 
                    "${optionRecipientDisplayName}", "option",
                    "${Const.ParamsNames.FEEDBACK_QUESTION_RANKISDUPLICATESALLOWED}", Const.ParamsNames.FEEDBACK_QUESTION_RANKISDUPLICATESALLOWED,
                    "${areDuplicatesAllowedChecked}", areDuplicatesAllowed ? "checked=\"checked\"" : "");
        }
    }

    @Override
    public String getNewQuestionSpecificEditFormHtml() {
        // Add two empty options by default
        
        this.options.add("");
        this.options.add("");

        return "<div id=\"rankForm\">" 
                + this.getQuestionSpecificEditFormHtml(-1) 
                + "</div>";
    }

    @Override
    public String getQuestionAdditionalInfoHtml(int questionNumber,
            String additionalInfoId) {
        StringBuilder optionListHtml = new StringBuilder();
        String optionFragmentTemplate = FeedbackQuestionFormTemplates.MSQ_ADDITIONAL_INFO_FRAGMENT;
        String additionalInfo = "";
        
        if (this.isRankRecipients) {
            additionalInfo = this.getQuestionTypeDisplayName() + "<br>";
        } else {
            optionListHtml.append("<ul style=\"list-style-type: disc;margin-left: 20px;\" >");
            for (int i = 0; i < options.size(); i++) {
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
        
        for (Entry<String, List<Integer>> entry : optionPoints.entrySet()) {
            
            List<Integer> points = entry.getValue();
            double average = computeAverage(points);
            String pointsReceived = getListOfRanksReceivedAsString(points);
            
            if (isRankRecipients) {
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
        
        if (isRankRecipients) {
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
            if (isRankRecipients) {
                String teamName = bundle.getTeamNameForEmail(entry.getKey());
                String recipientName = bundle.getNameForEmail(entry.getKey());
                option = Sanitizer.sanitizeForCsv(teamName) 
                       + "," 
                       + Sanitizer.sanitizeForCsv(recipientName);
            } else {
                option = Sanitizer.sanitizeForCsv(options.get(Integer.parseInt(entry.getKey())));
            }
            
            List<Integer> points = entry.getValue();
            double average = computeAverage(points);
            fragments += option + "," + 
                         df.format(average) + Const.EOL;
            
        }
        
        csv += (isRankRecipients ? "Team, Recipient" : "Option") + ", Average Points" + Const.EOL 
             + fragments + Const.EOL;
        
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
        
        Map<String, List<Integer>> optionPoints = new HashMap<>();
        for (FeedbackResponseAttributes response : responses) {
            FeedbackRankResponseDetails frd = (FeedbackRankResponseDetails)response.getResponseDetails();
            
            for (int i = 0; i < frd.getAnswerList().size(); i++) {
                String optionReceivingPoints = isRankRecipients ? 
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
        if (points == null) {
            points = new ArrayList<Integer>();
            optionPoints.put(optionReceivingPoints, points);
        }
        
        points.add(pointsReceived);
    }

    /**
     * Returns the list of points as as string to display
     * @param ranksReceived
     */
    private String getListOfRanksReceivedAsString(List<Integer> ranksReceived) {
        Collections.sort(ranksReceived);
        String pointsReceived = "";
        
        if (ranksReceived.size() > 10) {
            for (int i = 0; i < 5; i++) {
                pointsReceived += ranksReceived.get(i) + " , ";
            }
            
            pointsReceived += "...";
            
            for (int i = ranksReceived.size() - 5; i < ranksReceived.size(); i++) {
                pointsReceived += " , " + ranksReceived.get(i);
            }
        } else {
            for (int i = 0; i < ranksReceived.size(); i++) {
                pointsReceived += ranksReceived.get(i);
                
                if (i != ranksReceived.size() - 1) {
                    pointsReceived += " , ";
                }
            }
        }
        
        return pointsReceived;
    }

    private double computeAverage(List<Integer> points) {
        double average = 0;
        for (int point : points) {
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
        
        if (this.isRankRecipients != newRankQuestionDetails.isRankRecipients) {
            return true;
        }
        
        return false;
    }
    

    @Override
    public String getCsvHeader() {
        if (isRankRecipients) {
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
        List<String> errors = new ArrayList<>();
        if (!isRankRecipients && options.size() < MIN_NUM_OF_OPTIONS) {
            errors.add(ERROR_NOT_ENOUGH_OPTIONS + MIN_NUM_OF_OPTIONS + ".");
        }
        return errors;
    }

    @Override
    public List<String> validateResponseAttributes(
            List<FeedbackResponseAttributes> responses,
            int numRecipients) {
        if (responses.isEmpty()) {
            return new ArrayList<String>();
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
        
        List<String> errors = new ArrayList<>();
        Set<Integer> responseRank = new HashSet<>();
        
        for (FeedbackResponseAttributes response : responses) {
            FeedbackRankResponseDetails frd = (FeedbackRankResponseDetails) response.getResponseDetails();
            
            if (!isRankRecipients) {
                for (int i : frd.getAnswerList()) {
                    if (!areDuplicatesAllowed && !responseRank.add(i)) {
                        errors.add("Duplicate rank");
                    }
                }
            } else {
                if (!areDuplicatesAllowed && !responseRank.add(frd.getAnswerList().get(0))) {
                    errors.add("Duplicate rank");
                }
            }
        }
        
        return errors;
    }

}
