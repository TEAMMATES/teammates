package teammates.common.datatransfer;

import java.text.DecimalFormat;
import java.util.ArrayList;
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
import teammates.ui.controller.PageData;
import teammates.ui.template.ElementTag;

public class FeedbackRankOptionsQuestionDetails extends FeedbackRankQuestionDetails {
    public transient static final int MIN_NUM_OF_OPTIONS = 2;
    public transient static final String ERROR_NOT_ENOUGH_OPTIONS 
            = "Too little options for " + Const.FeedbackQuestionTypeNames.RANK_OPTION 
            + ". Minimum number of options is: ";
    
    public List<String> options;
    
    public FeedbackRankOptionsQuestionDetails() {
        super(FeedbackQuestionType.RANK_OPTIONS);
        
        this.options = new ArrayList<String>();
    }

    public FeedbackRankOptionsQuestionDetails(String questionText,
                                       List<String> rankOptions,
                                       int maxRank) {
        super(FeedbackQuestionType.RANK_OPTIONS, questionText);
        this.options = rankOptions;
    }

    
    @Override
    public boolean extractQuestionDetails(Map<String, String[]> requestParameters,
                                          FeedbackQuestionType questionType) {
        super.extractQuestionDetails(requestParameters, questionType);
        List<String> options = new ArrayList<>();  
      
        String numOptionsCreatedString = HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED);
        Assumption.assertNotNull("Null number of choice for Rank", numOptionsCreatedString);
        int numOptionsCreated = Integer.parseInt(numOptionsCreatedString);
        
        for (int i = 0; i < numOptionsCreated; i++) {
            String rankOption = HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_RANKOPTION + "-" + i);
            if (rankOption != null && !rankOption.trim().isEmpty()) {
                options.add(rankOption);
            }
        }
        
        this.initialiseQuestionDetails(options);
        
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
        String optionFragmentTemplate = FeedbackQuestionFormTemplates.RANK_SUBMISSION_FORM_OPTIONFRAGMENT;
     
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
        
        String html = FeedbackQuestionFormTemplates.populateTemplate(
                FeedbackQuestionFormTemplates.RANK_SUBMISSION_FORM,
                "${rankSubmissionFormOptionFragments}", optionListHtml.toString(),
                "${qnIdx}", Integer.toString(qnIdx),
                "${responseIdx}", Integer.toString(responseIdx),
                "${rankOptionVisibility}", "",
                "${Const.ParamsNames.FEEDBACK_QUESTION_RANKTORECIPIENTS}", Const.ParamsNames.FEEDBACK_QUESTION_RANKTORECIPIENTS,
                "${rankToRecipientsValue}", "false",
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
        
        for (int i = 0; i < options.size(); i++) {
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
        
        
        String html = FeedbackQuestionFormTemplates.populateTemplate(
                            FeedbackQuestionFormTemplates.RANK_SUBMISSION_FORM,
                            "${rankSubmissionFormOptionFragments}", optionListHtml.toString(),
                            "${qnIdx}", Integer.toString(qnIdx),
                            "${responseIdx}", Integer.toString(responseIdx),
                            "${rankOptionVisibility}", "",
                            "${rankToRecipientsValue}", "false",
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
        StringBuilder optionListHtml = new StringBuilder();
        String optionFragmentTemplate = FeedbackQuestionFormTemplates.MSQ_ADDITIONAL_INFO_FRAGMENT;
        String additionalInfo = "";
        
        optionListHtml.append("<ul style=\"list-style-type: disc;margin-left: 20px;\" >");
        for (String option : options) {
            String optionFragment = 
                    FeedbackQuestionFormTemplates.populateTemplate(optionFragmentTemplate,
                            "${msqChoiceValue}", option);
            
            optionListHtml.append(optionFragment);
        }
        
        optionListHtml.append("</ul>");
        additionalInfo = FeedbackQuestionFormTemplates.populateTemplate(
            FeedbackQuestionFormTemplates.MSQ_ADDITIONAL_INFO,
            "${questionTypeName}", this.getQuestionTypeDisplayName(),
            "${msqAdditionalInfoFragments}", optionListHtml.toString());

        
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

            String option = options.get(Integer.parseInt(entry.getKey()));
            
            fragments += FeedbackQuestionFormTemplates.populateTemplate(FeedbackQuestionFormTemplates.RANK_RESULT_STATS_OPTIONFRAGMENT,
                                "${rankOptionValue}",  Sanitizer.sanitizeForHtml(option),
                                "${pointsReceived}", pointsReceived,
                                "${averagePoints}", df.format(average));
        
        }
 
        html = FeedbackQuestionFormTemplates.populateTemplate(FeedbackQuestionFormTemplates.RANK_RESULT_OPTION_STATS,
                "${optionRecipientDisplayName}", "Option",
                "${fragments}", fragments);
        
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
            String option = Sanitizer.sanitizeForCsv(options.get(Integer.parseInt(entry.getKey())));
          
            List<Integer> points = entry.getValue();
            double average = computeAverage(points);
            fragments += option + "," + 
                         df.format(average) + Const.EOL;
            
        }
        
        csv += "Option" + ", Average Points" + Const.EOL 
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
            FeedbackRankOptionsResponseDetails frd = (FeedbackRankOptionsResponseDetails)response.getResponseDetails();
            
            for (int i = 0; i < frd.getFilteredAnswerList().size(); i++) {
                String optionReceivingPoints =  String.valueOf(i);
                int ranksReceived = frd.getFilteredAnswerList().get(i);
                
                updateOptionRanksMapping(optionPoints, optionReceivingPoints, ranksReceived);
            }
        }
        return optionPoints;
    }


    @Override
    public boolean isChangesRequiresResponseDeletion(FeedbackQuestionDetails newDetails) {
        FeedbackRankOptionsQuestionDetails newRankQuestionDetails = (FeedbackRankOptionsQuestionDetails) newDetails;

        if (this.options.size() != newRankQuestionDetails.options.size() 
            || !this.options.containsAll(newRankQuestionDetails.options) 
            || !newRankQuestionDetails.options.containsAll(this.options)) {
            return true;
        }
        
        return false;
    }
    

    @Override
    public String getCsvHeader() {
        List<String> sanitizedOptions = Sanitizer.sanitizeListForCsv(options);
        return "Feedbacks:," + StringHelper.toString(sanitizedOptions, ",");
    }

    @Override
    public String getQuestionTypeChoiceOption() {
        return "<option value=\"" + FeedbackQuestionType.RANK_OPTIONS.name() + "\">" + Const.FeedbackQuestionTypeNames.RANK_OPTION 
             + "</option>";
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
            return new ArrayList<String>();
        }

        List<String> errors = new ArrayList<>();
        
        for (FeedbackResponseAttributes response : responses) {
            FeedbackRankOptionsResponseDetails frd = (FeedbackRankOptionsResponseDetails ) response.getResponseDetails();
            Set<Integer> responseRank = new HashSet<>();
            
            for (int i : frd.getFilteredAnswerList()) {
                if (i == Const.POINTS_NOT_SUBMITTED) {
                    continue;
                }
                
                if (!areDuplicatesAllowed && responseRank.contains(i)) {
                    errors.add("Duplicate rank " + i);
                }
                responseRank.add(i);
            }
        }
        
        return errors;
    }

}
