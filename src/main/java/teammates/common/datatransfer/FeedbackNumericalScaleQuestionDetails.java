package teammates.common.datatransfer;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import teammates.common.util.Const;
import teammates.common.util.FeedbackQuestionFormTemplates;
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
        HashMap<String, Double> userAverage = new HashMap<String, Double>();
        HashMap<String, Double> min = new HashMap<String, Double>();
        HashMap<String, Double> max = new HashMap<String, Double>();
        HashMap<String, Integer> numResponses = new HashMap<String, Integer>();
        HashMap<String, Double> total = new HashMap<String, Double>();
        
        
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
        }
        
        String statsTitle = "Response Summary";
        
        DecimalFormat df = new DecimalFormat();
        df.setMinimumFractionDigits(0);
        df.setMaximumFractionDigits(5);
        df.setRoundingMode(RoundingMode.DOWN);
  
        
        StringBuilder userFragmentHtml = new StringBuilder();
        
        for (String recipient : numResponses.keySet()) {    
            userAverage.put(recipient, total.get(recipient) / numResponses.get(recipient));
            
            String recipientName;
            if (recipient.equals("%GENERAL%")) {
                recipientName = "General";
            } else {
                recipientName = bundle.getNameForEmail(recipient);
            }
            
                    
            userFragmentHtml.append(FeedbackQuestionFormTemplates.populateTemplate(
                    FeedbackQuestionFormTemplates.NUMSCALE_RESULTS_STATS_FRAGMENT,
                    "${recipientName}", recipientName,
                    "${Average}", df.format(userAverage.get(recipient)),
                    "${Max}", df.format(max.get(recipient)),
                    "${Min}", df.format(min.get(recipient))));
        }
        
        html = FeedbackQuestionFormTemplates.populateTemplate(
                FeedbackQuestionFormTemplates.NUMSCALE_RESULT_STATS,
                "${summaryTitle}", statsTitle,
                "${statsFragments}", userFragmentHtml.toString());
        
        return html;
    }

    private String getStudentQuestionResultsStatisticsHtml(
            List<FeedbackResponseAttributes> responses, AccountAttributes currentUser,
            FeedbackQuestionAttributes question, FeedbackSessionResultsBundle bundle) {
        String html = "";
        String recipientOfResponseToSelf = "You (excluding your own response to yourself)";
        
        HashMap<String, Double> userAverage = new HashMap<String, Double>();
        HashMap<String, Double> min = new HashMap<String, Double>();
        HashMap<String, Double> max = new HashMap<String, Double>();
        HashMap<String, Integer> numResponses = new HashMap<String, Integer>();
        HashMap<String, Double> total = new HashMap<String, Double>();
        
        String currentUserTeam = bundle.emailTeamNameTable.get(currentUser.email);
        
        boolean isDirectedAtGeneral = question.recipientType == FeedbackParticipantType.NONE;
        boolean isDirectedAtTeams = (question.recipientType == FeedbackParticipantType.TEAMS) || 
                                    (question.recipientType == FeedbackParticipantType.OWN_TEAM);
        boolean isDirectedAtStudents = !isDirectedAtGeneral && !isDirectedAtTeams;
        
        boolean isVisibleToAll = (question.showResponsesTo.contains(FeedbackParticipantType.TEAMS) &&
                                  question.showRecipientNameTo.contains(FeedbackParticipantType.TEAMS)) || 
                                 (question.showResponsesTo.contains(FeedbackParticipantType.STUDENTS) &&
                                  question.showRecipientNameTo.contains(FeedbackParticipantType.STUDENTS));
        boolean userGaveResponseToSelf = false;
        
        for(FeedbackResponseAttributes response : responses){
            boolean isDirectedAtUser = isDirectedAtStudents && response.recipientEmail.equals(currentUser.email); 
            boolean isDirectedAtUserTeam = isDirectedAtTeams && 
                                           response.recipientEmail.equals(StringHelper.recoverFromSanitizedText(currentUserTeam));
            
            if (isVisibleToAll || isDirectedAtUser || isDirectedAtUserTeam || isDirectedAtGeneral) {
                double answer = ((FeedbackNumericalScaleResponseDetails)response.getResponseDetails()).getAnswer();
                
                
                int numOfResponses = numResponses.containsKey(response.recipientEmail)? numResponses.get(response.recipientEmail) + 1 : 1;
                numResponses.put(response.recipientEmail, numOfResponses);
                
                double minScoreReceived = min.containsKey(response.recipientEmail)?  Math.min(answer, min.get(response.recipientEmail)): answer;
                min.put(response.recipientEmail, minScoreReceived);
                
                double maxScoreReceived = max.containsKey(response.recipientEmail)? Math.max(answer, max.get(response.recipientEmail)) : answer;
                max.put(response.recipientEmail, maxScoreReceived);
                
                double totalScore = total.containsKey(response.recipientEmail)? total.get(response.recipientEmail) + answer: answer;
                total.put(response.recipientEmail, totalScore);
                
                if (response.recipientEmail.equals(currentUser.email) && !response.giverEmail.equals(currentUser.email)) {
                    String rowName = recipientOfResponseToSelf;
                    
                    numResponses.put(rowName, numOfResponses);
                    min.put(rowName, minScoreReceived);
                    max.put(rowName, maxScoreReceived);
                    total.put(rowName, totalScore);
                } else if (response.recipientEmail.equals(currentUser.email) && response.giverEmail.equals(currentUser.email)) {
                    userGaveResponseToSelf = true;
                }
                
            }
        }
        
        // Do not give summary statistics if less than 2 responses
        if (numResponses.containsKey(currentUser.email) && numResponses.get(currentUser.email) < 2) {
            return "";
        }
        
        
        String statsTitle = "Summary of responses received by you";
        if (isDirectedAtGeneral || isVisibleToAll) {
            statsTitle = "Response Summary";
        } else if (isDirectedAtTeams) {
            statsTitle = "Summary of responses received by your team";
        }
  
        
        DecimalFormat df = new DecimalFormat();
        df.setMinimumFractionDigits(0);
        df.setMaximumFractionDigits(5);
        df.setRoundingMode(RoundingMode.DOWN);
  
        
        StringBuilder userFragmentHtml = new StringBuilder();
        // display the current user's statistics in the first row of the table 
        if (question.recipientType == FeedbackParticipantType.STUDENTS && 
            numResponses.containsKey(currentUser.email)) {
            userAverage.put(currentUser.email, total.get(currentUser.email) / numResponses.get(currentUser.email));
            userFragmentHtml.append(FeedbackQuestionFormTemplates.populateTemplate(
                    FeedbackQuestionFormTemplates.NUMSCALE_RESULTS_STATS_FRAGMENT,
                    "${recipientName}", "You",
                    "${Average}", df.format(userAverage.get(currentUser.email)),
                    "${Max}", df.format(max.get(currentUser.email)),
                    "${Min}", df.format(min.get(currentUser.email))));
            numResponses.remove(currentUser.email); 
            
        } else if (question.recipientType == FeedbackParticipantType.TEAMS && 
                   numResponses.containsKey(currentUserTeam)) {
            userAverage.put(currentUserTeam, total.get(currentUserTeam) / numResponses.get(currentUserTeam));
            userFragmentHtml.append(FeedbackQuestionFormTemplates.populateTemplate(
                    FeedbackQuestionFormTemplates.NUMSCALE_RESULTS_STATS_FRAGMENT,
                    "${recipientName}", "Your Team",
                    "${Average}", df.format(userAverage.get(currentUserTeam)),
                    "${Max}", df.format(max.get(currentUserTeam)),
                    "${Min}", df.format(min.get(currentUserTeam))));
            numResponses.remove(currentUserTeam); 
            
        }
        // display a row consisting of the statistics excluding the user's own response to himself/herself
        if (userGaveResponseToSelf) {
            userAverage.put(recipientOfResponseToSelf, total.get(recipientOfResponseToSelf) / numResponses.get(recipientOfResponseToSelf));
            userFragmentHtml.append(FeedbackQuestionFormTemplates.populateTemplate(
                    FeedbackQuestionFormTemplates.NUMSCALE_RESULTS_STATS_FRAGMENT,
                    "${recipientName}", recipientOfResponseToSelf,
                    "${Average}", df.format(userAverage.get(recipientOfResponseToSelf)),
                    "${Max}", df.format(max.get(recipientOfResponseToSelf)),
                    "${Min}", df.format(min.get(recipientOfResponseToSelf))));
        }
        numResponses.remove(recipientOfResponseToSelf);
        
        for (String recipient : numResponses.keySet()) {    
            userAverage.put(recipient, total.get(recipient) / numResponses.get(recipient));
            
            String recipientName;
            if (recipient.equals("%GENERAL%")) {
                recipientName = "General";
            } else if (recipient.equals(recipientOfResponseToSelf)) {
                recipientName = recipient;
            } else if (recipient.equals(currentUser.email)) {
                recipientName = "You";
            } else {
                recipientName = bundle.getNameForEmail(recipient);
            }
            
                    
            userFragmentHtml.append(FeedbackQuestionFormTemplates.populateTemplate(
                    FeedbackQuestionFormTemplates.NUMSCALE_RESULTS_STATS_FRAGMENT,
                    "${recipientName}", recipientName,
                    "${Average}", df.format(userAverage.get(recipient)),
                    "${Max}", df.format(max.get(recipient)),
                    "${Min}", df.format(min.get(recipient))));
        }
        
        if (userFragmentHtml.length() == 0) {
            return "";
        }
        
        html = FeedbackQuestionFormTemplates.populateTemplate(
                        FeedbackQuestionFormTemplates.NUMSCALE_RESULT_STATS,
                        "${summaryTitle}", statsTitle,
                        "${statsFragments}", userFragmentHtml.toString());
        
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
        double average = 0;
        double min = Integer.MAX_VALUE;
        double max = Integer.MIN_VALUE;
        int numResponses = 0;
        double total = 0;
        
        for(FeedbackResponseAttributes response : responses){
            numResponses++;
            double answer = ((FeedbackNumericalScaleResponseDetails)response.getResponseDetails()).getAnswer();
            min = Math.min(answer, min);
            max = Math.max(answer, max);
            total += answer;
        }
        
        average = total/numResponses;
        
        DecimalFormat df = new DecimalFormat();
        df.setMinimumFractionDigits(0);
        df.setMaximumFractionDigits(5);
        df.setRoundingMode(RoundingMode.DOWN);
        
        csv += "Average, Minimum, Maximum" + Const.EOL;
        
        csv += df.format(average) + ","
            + ((min == Integer.MAX_VALUE)? "-" : df.format(min)) + ","
            + ((max == Integer.MIN_VALUE)? "-" : df.format(max)) + Const.EOL;
        
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
