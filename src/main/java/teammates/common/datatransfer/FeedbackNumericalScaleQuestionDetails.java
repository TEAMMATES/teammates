package teammates.common.datatransfer;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import teammates.common.util.Const;
import teammates.common.util.FeedbackQuestionFormTemplates;
import teammates.common.util.Sanitizer;
import teammates.common.util.StringHelper;

public class FeedbackNumericalScaleQuestionDetails extends
        FeedbackAbstractQuestionDetails {
    public int minScale;
    public int maxScale;
    public double step;
    
    public FeedbackNumericalScaleQuestionDetails() {
        super(FeedbackQuestionType.NUMSCALE);
        this.minScale = 1;
        this.maxScale = 5;
        this.step = 0.5;
    }
    
    public FeedbackNumericalScaleQuestionDetails(String questionText, int minScale, int maxScale, double step) {
        super(FeedbackQuestionType.NUMSCALE, questionText);
        this.minScale = minScale;
        this.maxScale = maxScale;
        this.step = step;
    }
    
    @Override
    public String getQuestionTypeDisplayName() {
        return Const.FeedbackQuestionTypeNames.NUMSCALE;
    }

    @Override
    public String getQuestionWithExistingResponseSubmissionFormHtml(
            boolean sessionIsOpen, int qnIdx, int responseIdx, String courseId,
            FeedbackAbstractResponseDetails existingResponseDetails) {
        FeedbackNumericalScaleResponseDetails numscaleResponseDetails = 
                (FeedbackNumericalScaleResponseDetails) existingResponseDetails;
        
        return FeedbackQuestionFormTemplates.populateTemplate(
                FeedbackQuestionFormTemplates.NUMSCALE_SUBMISSION_FORM,
                "${qnIdx}", Integer.toString(qnIdx),
                "${disabled}", sessionIsOpen ? "" : "disabled=\"disabled\"",
                "${responseIdx}", Integer.toString(responseIdx),
                "${minScale}", Integer.toString(minScale),
                "${maxScale}", Integer.toString(maxScale),
                "${step}", StringHelper.toDecimalFormatString(step),
                "${existingAnswer}", numscaleResponseDetails.getAnswerString(),
                "${possibleValuesString}", getPossibleValuesStringSubmit(),
                "${Const.ParamsNames.FEEDBACK_RESPONSE_TEXT}", Const.ParamsNames.FEEDBACK_RESPONSE_TEXT,
                "${Const.ParamsNames.FEEDBACK_QUESTION_NUMSCALE_MIN}", Const.ParamsNames.FEEDBACK_QUESTION_NUMSCALE_MIN,
                "${Const.ParamsNames.FEEDBACK_QUESTION_NUMSCALE_MAX}", Const.ParamsNames.FEEDBACK_QUESTION_NUMSCALE_MAX,
                "${Const.ParamsNames.FEEDBACK_QUESTION_NUMSCALE_STEP}", Const.ParamsNames.FEEDBACK_QUESTION_NUMSCALE_STEP);
    }

    @Override
    public String getQuestionWithoutExistingResponseSubmissionFormHtml(
            boolean sessionIsOpen, int qnIdx, int responseIdx, String courseId) {
        return FeedbackQuestionFormTemplates.populateTemplate(
                FeedbackQuestionFormTemplates.NUMSCALE_SUBMISSION_FORM,
                "${qnIdx}", Integer.toString(qnIdx),
                "${disabled}", sessionIsOpen ? "" : "disabled=\"disabled\"",
                "${responseIdx}", Integer.toString(responseIdx),
                "${minScale}", Integer.toString(minScale),
                "${maxScale}", Integer.toString(maxScale),
                "${step}", StringHelper.toDecimalFormatString(step),
                "${existingAnswer}", "",
                "${possibleValuesString}", getPossibleValuesStringSubmit(),
                "${Const.ParamsNames.FEEDBACK_RESPONSE_TEXT}", Const.ParamsNames.FEEDBACK_RESPONSE_TEXT,
                "${Const.ParamsNames.FEEDBACK_QUESTION_NUMSCALE_MIN}", Const.ParamsNames.FEEDBACK_QUESTION_NUMSCALE_MIN,
                "${Const.ParamsNames.FEEDBACK_QUESTION_NUMSCALE_MAX}", Const.ParamsNames.FEEDBACK_QUESTION_NUMSCALE_MAX,
                "${Const.ParamsNames.FEEDBACK_QUESTION_NUMSCALE_STEP}", Const.ParamsNames.FEEDBACK_QUESTION_NUMSCALE_STEP);
    }

    @Override
    public String getQuestionSpecificEditFormHtml(int questionNumber) {
        return FeedbackQuestionFormTemplates.populateTemplate(
                FeedbackQuestionFormTemplates.NUMSCALE_EDIT_FORM,
                "${questionNumber}", Integer.toString(questionNumber),
                "${minScale}", Integer.toString(minScale),
                "${maxScale}", Integer.toString(maxScale),
                "${step}", StringHelper.toDecimalFormatString(step),
                "${possibleValues}", getPossibleValuesStringEdit(),
                "${Const.ParamsNames.FEEDBACK_QUESTION_NUMSCALE_MIN}", Const.ParamsNames.FEEDBACK_QUESTION_NUMSCALE_MIN,
                "${Const.ParamsNames.FEEDBACK_QUESTION_NUMSCALE_MAX}", Const.ParamsNames.FEEDBACK_QUESTION_NUMSCALE_MAX,
                "${Const.ParamsNames.FEEDBACK_QUESTION_NUMSCALE_STEP}", Const.ParamsNames.FEEDBACK_QUESTION_NUMSCALE_STEP,
                "${Const.ToolTips.FEEDBACK_QUESTION_NUMSCALE_MIN}", Const.Tooltips.FEEDBACK_QUESTION_NUMSCALE_MIN,
                "${Const.ToolTips.FEEDBACK_QUESTION_NUMSCALE_MAX}", Const.Tooltips.FEEDBACK_QUESTION_NUMSCALE_MAX,
                "${Const.ToolTips.FEEDBACK_QUESTION_NUMSCALE_STEP}", Const.Tooltips.FEEDBACK_QUESTION_NUMSCALE_STEP);
    }

    @Override
    public String getQuestionAdditionalInfoHtml(int questionNumber,
            String additionalInfoId) {
        String additionalInfo = getQuestionTypeDisplayName() + ":<br/>";
        additionalInfo += "Minimum value: " + minScale 
                                + ". Increment: " + step + ". Maximum value: " + maxScale + ".";
        
        return FeedbackQuestionFormTemplates.populateTemplate(
                FeedbackQuestionFormTemplates.FEEDBACK_QUESTION_ADDITIONAL_INFO,
                "${more}", "[more]",
                "${less}", "[less]",
                "${questionNumber}", Integer.toString(questionNumber),
                "${additionalInfoId}", additionalInfoId,
                "${questionAdditionalInfo}", additionalInfo);
    }

    @Override
    public String getQuestionResultStatisticsHtml(List<FeedbackResponseAttributes> responses,
            FeedbackQuestionAttributes question,
            AccountAttributes currentUser,
            FeedbackSessionResultsBundle bundle,
            String view) {
        
        if (view.equals("student")) {
            return getStudentQuestionResultsStatisticsHtml(responses, currentUser, question, bundle);
        } else {
            return getInstructorQuestionResultsStatisticsHtml(responses, question, bundle);
        }
    }

    private String getInstructorQuestionResultsStatisticsHtml(
            List<FeedbackResponseAttributes> responses, 
            FeedbackQuestionAttributes question, FeedbackSessionResultsBundle bundle) {
        String html = "";
        
        HashMap<String, Double> min = new HashMap<String, Double>();
        HashMap<String, Double> max = new HashMap<String, Double>();
        HashMap<String, Integer> numResponses = new HashMap<String, Integer>();
        HashMap<String, Double> total = new HashMap<String, Double>();
        HashMap<String, Double> totalExcludingSelfResponse = new HashMap<String, Double>();
        HashMap<String, Boolean> userGaveResponseToSelf = new HashMap<String, Boolean>();

        boolean isDirectedAtTeams = (question.recipientType == FeedbackParticipantType.TEAMS) || 
                                    (question.recipientType == FeedbackParticipantType.OWN_TEAM);
        
        for(FeedbackResponseAttributes response : responses){
            double answer = ((FeedbackNumericalScaleResponseDetails)response.getResponseDetails()).getAnswer();
            
            int numOfResponses = numResponses.containsKey(response.recipientEmail)? numResponses.get(response.recipientEmail) + 1 : 1;
            numResponses.put(response.recipientEmail, numOfResponses);
                
            double minScoreReceived = min.containsKey(response.recipientEmail)?  Math.min(answer, min.get(response.recipientEmail)): answer;
            min.put(response.recipientEmail, minScoreReceived);
                
            double maxScoreReceived = max.containsKey(response.recipientEmail)? Math.max(answer, max.get(response.recipientEmail)) : answer;
            max.put(response.recipientEmail, maxScoreReceived);
                
            double totalScore = total.containsKey(response.recipientEmail)? total.get(response.recipientEmail) + answer: answer;
            total.put(response.recipientEmail, totalScore);
            
            if (!response.recipientEmail.equals(response.giverEmail)) {
                totalScore = totalExcludingSelfResponse.containsKey(response.recipientEmail)? 
                             totalExcludingSelfResponse.get(response.recipientEmail) + answer: answer;
                totalExcludingSelfResponse.put(response.recipientEmail, totalScore);
                
            } else {
                userGaveResponseToSelf.put(response.recipientEmail, true);
            }
        }
        
        String statsTitle = "Response Summary";
        
        String fragmentTemplateToUse = (userGaveResponseToSelf.isEmpty() || isDirectedAtTeams) ? 
                FeedbackQuestionFormTemplates.NUMSCALE_RESULTS_STATS_FRAGMENT: 
                FeedbackQuestionFormTemplates.NUMSCALE_RESULTS_STATS_FRAGMENT_WITH_SELF_RESPONSE;
        String templateToUse = (userGaveResponseToSelf.isEmpty() || isDirectedAtTeams) ? 
                               FeedbackQuestionFormTemplates.NUMSCALE_RESULT_STATS : 
                               FeedbackQuestionFormTemplates.NUMSCALE_RESULT_STATS_WITH_SELF_RESPONSE;

        
        DecimalFormat df = new DecimalFormat();
        df.setMinimumFractionDigits(0);
        df.setMaximumFractionDigits(5);
        df.setRoundingMode(RoundingMode.DOWN);
  
        StringBuilder userFragmentHtml = new StringBuilder();
        String recipientName;
        
        for (String recipient : numResponses.keySet()) {
            String userAverageWithoutSelfResponse;
            double userAverage = total.get(recipient) / numResponses.get(recipient);
            
            if (userGaveResponseToSelf.containsKey(recipient) && totalExcludingSelfResponse.containsKey(recipient)) {
                double userAverageExcludingSelfResponse = totalExcludingSelfResponse.get(recipient) / 
                                                   (numResponses.get(recipient) - 1);
                userAverageWithoutSelfResponse = df.format(userAverageExcludingSelfResponse);
            } else {
                userAverageWithoutSelfResponse = "-";
            }
            
            
            recipientName = recipient.equals("%GENERAL%") ? "General" : bundle.getNameForEmail(recipient);
            
            userFragmentHtml.append(FeedbackQuestionFormTemplates.populateTemplate(
                    fragmentTemplateToUse,
                    "${recipientName}", recipientName,
                    "${Average}", df.format(userAverage),
                    "${Max}", df.format(max.get(recipient)),
                    "${Min}", df.format(min.get(recipient)),
                    "${AverageExcludingSelfResponse}", userAverageWithoutSelfResponse));
        }
        
        html = FeedbackQuestionFormTemplates.populateTemplate(
                templateToUse,
                "${summaryTitle}", statsTitle,
                "${statsFragments}", userFragmentHtml.toString());
        
        return html;
    }

    private String getStudentQuestionResultsStatisticsHtml(
            List<FeedbackResponseAttributes> responses, AccountAttributes currentUser,
            FeedbackQuestionAttributes question, FeedbackSessionResultsBundle bundle) {
        String html = "";
       
        HashMap<String, Double> min = new HashMap<String, Double>();
        HashMap<String, Double> max = new HashMap<String, Double>();
        HashMap<String, Integer> numResponses = new HashMap<String, Integer>();
        HashMap<String, Double> total = new HashMap<String, Double>();
        HashMap<String, Double> totalExcludingSelfResponse = new HashMap<String, Double>();
        HashMap<String, Boolean> userGaveResponseToSelf = new HashMap<String, Boolean>();
        
        //Check visibility of recipient
        List<String> hiddenRecipients = new ArrayList<String>(); // List of recipients to hide
        FeedbackParticipantType type = question.recipientType;
        for(FeedbackResponseAttributes response : responses){
            if (bundle.visibilityTable.get(response.getId())[1] == false &&
                    type != FeedbackParticipantType.SELF &&
                    type != FeedbackParticipantType.NONE) {
                hiddenRecipients.add(response.recipientEmail);
            }
        }
        
        String currentUserTeam = bundle.emailTeamNameTable.get(currentUser.email);
        
        boolean isAbleToSeeOthersResponses = false;
        
        for(FeedbackResponseAttributes response : responses){
            if (hiddenRecipients.contains(response.recipientEmail)) {
                continue;
            }
            
            if ((!response.recipientEmail.equals(currentUser.email) && !response.giverEmail.equals(currentUser.email)) &&
                (!response.recipientEmail.equals(currentUserTeam) && !response.giverEmail.equals(currentUserTeam))) {
                isAbleToSeeOthersResponses = true;
            }
            
            double answer = ((FeedbackNumericalScaleResponseDetails)response.getResponseDetails()).getAnswer();
            
            
            int numOfResponses = numResponses.containsKey(response.recipientEmail)? 
                                 numResponses.get(response.recipientEmail) + 1 : 1;
            numResponses.put(response.recipientEmail, numOfResponses);
            
            double minScoreReceived = min.containsKey(response.recipientEmail)? 
                                      Math.min(answer, min.get(response.recipientEmail)): answer;
            min.put(response.recipientEmail, minScoreReceived);
            
            double maxScoreReceived = max.containsKey(response.recipientEmail)? 
                                      Math.max(answer, max.get(response.recipientEmail)) : answer;
            max.put(response.recipientEmail, maxScoreReceived);
            
            double totalScore = total.containsKey(response.recipientEmail)? 
                                total.get(response.recipientEmail) + answer: answer;
            total.put(response.recipientEmail, totalScore);
            
            
            if (!response.recipientEmail.equals(response.giverEmail)) {
                totalScore = totalExcludingSelfResponse.containsKey(response.recipientEmail)? 
                        totalExcludingSelfResponse.get(response.recipientEmail) + answer: answer;
                totalExcludingSelfResponse.put(response.recipientEmail, totalScore);
                
            } else {
                userGaveResponseToSelf.put(response.recipientEmail, true);
            }
        }
        
        boolean isDirectedAtGeneral = question.recipientType == FeedbackParticipantType.NONE;
        boolean isDirectedAtTeams = (question.recipientType == FeedbackParticipantType.TEAMS) || 
                                    (question.recipientType == FeedbackParticipantType.OWN_TEAM);
        boolean isDirectedAtStudents = !isDirectedAtGeneral && !isDirectedAtTeams;
        
        
        String fragmentTemplateToUse = (userGaveResponseToSelf.isEmpty() || isDirectedAtTeams) ? 
                                       FeedbackQuestionFormTemplates.NUMSCALE_RESULTS_STATS_FRAGMENT: 
                                       FeedbackQuestionFormTemplates.NUMSCALE_RESULTS_STATS_FRAGMENT_WITH_SELF_RESPONSE;
        String templateToUse = (userGaveResponseToSelf.isEmpty() || isDirectedAtTeams) ? FeedbackQuestionFormTemplates.NUMSCALE_RESULT_STATS: FeedbackQuestionFormTemplates.NUMSCALE_RESULT_STATS_WITH_SELF_RESPONSE;
              
        String statsTitle;
        if (isDirectedAtGeneral || isAbleToSeeOthersResponses) {
            statsTitle = "Response Summary";
        } else if (isDirectedAtTeams) {
            statsTitle = "Summary of responses received by your team";
        } else {
            statsTitle = "Summary of responses received by you";
        }
  
        
        DecimalFormat df = new DecimalFormat();
        df.setMinimumFractionDigits(0);
        df.setMaximumFractionDigits(5);
        df.setRoundingMode(RoundingMode.DOWN);
        
        
        StringBuilder userFragmentHtml = new StringBuilder();
        
        // display the current user's statistics in the first row of the table 
        if (isDirectedAtStudents && numResponses.containsKey(currentUser.email)) {
            
            double userAverage = total.get(currentUser.email) / numResponses.get(currentUser.email);
            String userAverageWithoutSelfResponse;
            
            if (userGaveResponseToSelf.containsKey(currentUser.email) && totalExcludingSelfResponse.containsKey(currentUser.email)) {
                double userAverageExcludingSelfResponse = totalExcludingSelfResponse.get(currentUser.email) / 
                                                          (numResponses.get(currentUser.email) - 1);
                userAverageWithoutSelfResponse = df.format(userAverageExcludingSelfResponse);
            } else {
                userAverageWithoutSelfResponse = "-";
            }
            
            if (numResponses.get(currentUser.email) >= 2) {
                userFragmentHtml.append(FeedbackQuestionFormTemplates.populateTemplate(
                        fragmentTemplateToUse,
                        "${recipientName}", "You",
                        "${Average}", df.format(userAverage),
                        "${Max}", df.format(max.get(currentUser.email)),
                        "${Min}", df.format(min.get(currentUser.email)),
                        "${AverageExcludingSelfResponse}", userAverageWithoutSelfResponse));
            }
            
            numResponses.remove(currentUser.email); 
            
        } else if (isDirectedAtTeams && 
                   numResponses.containsKey(currentUserTeam)) {
            double userAverage;
            userAverage = total.get(currentUserTeam) / numResponses.get(currentUserTeam);
        
            if (numResponses.get(currentUserTeam) >= 2) {
                userFragmentHtml.append(FeedbackQuestionFormTemplates.populateTemplate(
                        fragmentTemplateToUse,
                        "${recipientName}", "Your Team (" + currentUserTeam + ")",
                        "${Average}", df.format(userAverage),
                        "${Max}", df.format(max.get(currentUserTeam)),
                        "${Min}", df.format(min.get(currentUserTeam))));
            }
            
            numResponses.remove(currentUserTeam); 
        }
        
        
        StringBuilder otherUserFragmentHtml = new StringBuilder();
        
        for (String recipient : numResponses.keySet()) {
            
            if (numResponses.get(recipient) <= 1) {
                continue;
            }
            
            double userAverage = total.get(recipient) / numResponses.get(recipient);
            String userAverageWithoutSelfResponse;
            
            if (userGaveResponseToSelf.containsKey(recipient) && totalExcludingSelfResponse.containsKey(recipient)) {
                double userAverageExcludingSelfResponse = totalExcludingSelfResponse.get(recipient) / 
                                                   (numResponses.get(recipient) - 1);
                userAverageWithoutSelfResponse = df.format(userAverageExcludingSelfResponse);
            } else {
                userAverageWithoutSelfResponse = "-";
            }
            
            String recipientName = recipient.equals("%GENERAL%")? "General" : bundle.getNameForEmail(recipient);
            
            otherUserFragmentHtml.append(FeedbackQuestionFormTemplates.populateTemplate(
                    fragmentTemplateToUse,
                    "${recipientName}", recipientName,
                    "${Average}", df.format(userAverage),
                    "${Max}", df.format(max.get(recipient)),
                    "${Min}", df.format(min.get(recipient)),
                    "${AverageExcludingSelfResponse}", userAverageWithoutSelfResponse));
        }
        
        if (userFragmentHtml.length() == 0 && otherUserFragmentHtml.length() == 0) {
            return "";
        }
        
        html = FeedbackQuestionFormTemplates.populateTemplate(
                        templateToUse,
                        "${summaryTitle}", statsTitle,
                        "${statsFragments}", userFragmentHtml.toString() + otherUserFragmentHtml.toString());
        
        return html;
    }
    
    @Override
    public String getQuestionResultStatisticsCsv(
            List<FeedbackResponseAttributes> responses,
            FeedbackQuestionAttributes question,
            FeedbackSessionResultsBundle bundle) {
        if(responses.size() == 0){
            return "";
        }
        
        String csv = "";
        
        HashMap<String, Double> min = new HashMap<String, Double>();
        HashMap<String, Double> max = new HashMap<String, Double>();
        HashMap<String, Integer> numResponses = new HashMap<String, Integer>();
        HashMap<String, Double> total = new HashMap<String, Double>();
        HashMap<String, Double> totalExcludingSelfResponse = new HashMap<String, Double>();
        HashMap<String, Boolean> userGaveResponseToSelf = new HashMap<String, Boolean>();
        
        
        for(FeedbackResponseAttributes response : responses){
            double answer = ((FeedbackNumericalScaleResponseDetails)response.getResponseDetails()).getAnswer();
                
                
            int numOfResponses = numResponses.containsKey(response.recipientEmail)? numResponses.get(response.recipientEmail) + 1 : 1;
            numResponses.put(response.recipientEmail, numOfResponses);
                
            double minScoreReceived = min.containsKey(response.recipientEmail)?  Math.min(answer, min.get(response.recipientEmail)): answer;
            min.put(response.recipientEmail, minScoreReceived);
                
            double maxScoreReceived = max.containsKey(response.recipientEmail)? Math.max(answer, max.get(response.recipientEmail)) : answer;
            max.put(response.recipientEmail, maxScoreReceived);
                
            double totalScore = total.containsKey(response.recipientEmail)? total.get(response.recipientEmail) + answer: answer;
            total.put(response.recipientEmail, totalScore);
            
            if (!response.recipientEmail.equals(response.giverEmail)) {
                totalScore = totalExcludingSelfResponse.containsKey(response.recipientEmail)? 
                             totalExcludingSelfResponse.get(response.recipientEmail) + answer: answer;
                totalExcludingSelfResponse.put(response.recipientEmail, totalScore);
                
            } else {
                userGaveResponseToSelf.put(response.recipientEmail, true);
            }            
        }
        
        
        DecimalFormat df = new DecimalFormat();
        df.setMinimumFractionDigits(0);
        df.setMaximumFractionDigits(5);
        df.setRoundingMode(RoundingMode.DOWN);
  
        
        csv += "Recipient, Average, Minimum, Maximum" ;
        boolean showAverageWithoutSelfResponse = !(userGaveResponseToSelf.keySet().isEmpty());
        if (showAverageWithoutSelfResponse) {
            csv += ", Average excluding student's own response";
        }
        csv += Const.EOL;
        
        for (String recipient : numResponses.keySet()) {
            double userAverage = total.get(recipient) / numResponses.get(recipient);
            
            String recipientName;
            if (recipient.equals("%GENERAL%")) {
                recipientName = "General";
            } else {
                recipientName = bundle.getNameForEmail(recipient);
            }
            
            csv += Sanitizer.sanitizeForCsv(recipientName) + ",";
            csv += String.valueOf(userAverage) + "," + min.get(recipient).toString() + "," + max.get(recipient).toString();
            
            if (showAverageWithoutSelfResponse) {
                if (userGaveResponseToSelf.containsKey(recipient) && totalExcludingSelfResponse.containsKey(recipient)) {
                    double userAverageExcludingSelfResponse = totalExcludingSelfResponse.get(recipient) / 
                                                              (numResponses.get(recipient) - 1);
                    csv += "," + df.format(userAverageExcludingSelfResponse); 
                } else {
                    csv += ",-";
                }
            }
            csv += Const.EOL;
        }
        
        
        return csv;
    }
    
    @Override
    public boolean isChangesRequiresResponseDeletion(
            FeedbackAbstractQuestionDetails newDetails) {
        FeedbackNumericalScaleQuestionDetails newNumScaleDetails = 
                (FeedbackNumericalScaleQuestionDetails) newDetails;
        
        if(this.minScale != newNumScaleDetails.minScale 
                || this.maxScale != newNumScaleDetails.maxScale
                || this.step != newNumScaleDetails.step) {
            return true;
        }
        return false;
    }

    @Override
    public String getCsvHeader() {
        return "Feedback";
    }

    private String getPossibleValuesStringEdit() {
        return "[Based on the above settings, acceptable responses are: " + getPossibleValuesString();
    }
    
    private String getPossibleValuesStringSubmit() {
        return "[Possible values: " + getPossibleValuesString();
    }
    
    private String getPossibleValuesString() {
        double cur = minScale + step;
        int possibleValuesCount = 1;
        while ((maxScale - cur) >= -1e-9) {
            cur += step;
            possibleValuesCount++;
        }
        
        String possibleValuesString = new String();
        if (possibleValuesCount > 6) {
            possibleValuesString += StringHelper.toDecimalFormatString(minScale) + ", "
                    + StringHelper.toDecimalFormatString(minScale + step) + ", "
                    + StringHelper.toDecimalFormatString(minScale + 2*step) + ", ..., "
                    + StringHelper.toDecimalFormatString(maxScale - 2*step) + ", "
                    + StringHelper.toDecimalFormatString(maxScale - step) + ", "
                    + StringHelper.toDecimalFormatString(maxScale);
        } else {
            possibleValuesString += minScale;
            cur = minScale + step;
            while ((maxScale - cur) >= -1e-9) {
                possibleValuesString += ", " + StringHelper.toDecimalFormatString(cur);
                cur += step;
            }
        }
        possibleValuesString += "]";
        
        return possibleValuesString;
    }
    
    final String ERROR_MIN_MAX = "Minimum value must be < maximum value for "+Const.FeedbackQuestionTypeNames.NUMSCALE+".";
    final String ERROR_STEP = "Step value must be > 0 for "+Const.FeedbackQuestionTypeNames.NUMSCALE+".";
    
    @Override
    public List<String> validateQuestionDetails() {
        List<String> errors = new ArrayList<String>();
        if(minScale >= maxScale){
            errors.add(ERROR_MIN_MAX);
        }
        if(step <= 0){
            errors.add(ERROR_STEP);
        }
        return errors;
    }
    
    final String ERROR_OUT_OF_RANGE = " is out of the range for " + Const.FeedbackQuestionTypeNames.NUMSCALE + ".";
    
    @Override
    public List<String> validateResponseAttributes(
            List<FeedbackResponseAttributes> responses,
            int numRecipients) {
        List<String> errors = new ArrayList<String>();
        for(FeedbackResponseAttributes response : responses){
            FeedbackNumericalScaleResponseDetails frd = (FeedbackNumericalScaleResponseDetails) response.getResponseDetails();
            if(frd.getAnswer() < minScale || frd.getAnswer() > maxScale){
                errors.add(frd.getAnswerString() + ERROR_OUT_OF_RANGE + "(min="+minScale+", max="+maxScale+")");
            }
            //TODO: strengthen check for step
        }
        return errors;
    }
}
