package teammates.common.datatransfer;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.FeedbackQuestionFormTemplates;
import teammates.common.util.HttpRequestHelper;
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
    
    @Override
    public boolean extractQuestionDetails(
            Map<String, String[]> requestParameters,
            FeedbackQuestionType questionType) {
        
        String minScaleString = HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_NUMSCALE_MIN);
        Assumption.assertNotNull("Null minimum scale", minScaleString);
        int minScale = Integer.parseInt(minScaleString);
        
        String maxScaleString = HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_NUMSCALE_MAX);
        Assumption.assertNotNull("Null maximum scale", maxScaleString);
        int maxScale = Integer.parseInt(maxScaleString);
        
        String stepString = HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_NUMSCALE_STEP);
        Assumption.assertNotNull("Null step", stepString);
        Double step = Double.parseDouble(stepString);

        this.setNumericalScaleQuestionDetails(minScale, maxScale, step);
        
        return true;
    }

    private void setNumericalScaleQuestionDetails(int minScale, int maxScale, double step) {
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
        
        Map<String, Double> min = new HashMap<String, Double>();
        Map<String, Double> max = new HashMap<String, Double>();
        Map<String, Integer> numResponses = new HashMap<String, Integer>();
        Map<String, Double> total = new HashMap<String, Double>();
        Map<String, Double> totalExcludingSelfResponse = new HashMap<String, Double>();
        Map<String, Boolean> userGaveResponseToSelf = new HashMap<String, Boolean>();

        // need to know which recipients are hidden since anonymised recipients will not appear in the summary table
        List<String> hiddenRecipients = getHiddenRecipients(responses, question, bundle);
        
        boolean showAvgExcludingSelfResponse = populateSummaryStatisticsFromResponsesHidingRecipients(
                                               responses, min, max, numResponses, total, totalExcludingSelfResponse, 
                                               userGaveResponseToSelf, hiddenRecipients);
        
        String statsTitle = "Response Summary";
        
        String fragmentTemplateToUse = showAvgExcludingSelfResponse ? 
                                       FeedbackQuestionFormTemplates.NUMSCALE_RESULTS_STATS_FRAGMENT_WITH_SELF_RESPONSE:
                                       FeedbackQuestionFormTemplates.NUMSCALE_RESULTS_STATS_FRAGMENT;
        
        String templateToUse = showAvgExcludingSelfResponse ? 
                               FeedbackQuestionFormTemplates.NUMSCALE_RESULT_STATS_WITH_SELF_RESPONSE:
                               FeedbackQuestionFormTemplates.NUMSCALE_RESULT_STATS;

        
        DecimalFormat df = new DecimalFormat();
        df.setMinimumFractionDigits(0);
        df.setMaximumFractionDigits(5);
        df.setRoundingMode(RoundingMode.DOWN);
  
        StringBuilder fragmentHtml = new StringBuilder();
        
        
        for (String recipient : numResponses.keySet()) {
            String userAverageWithoutSelfResponse = "";
            double userAverage = total.get(recipient) / numResponses.get(recipient);
            
            if (showAvgExcludingSelfResponse) {
                userAverageWithoutSelfResponse = computeUserAverageWithoutSelfResponse(
                        numResponses, totalExcludingSelfResponse,
                        userGaveResponseToSelf, df, recipient, userAverage);
            }
            
            
            String recipientName = recipient.equals(Const.GENERAL_QUESTION) ? "General" : bundle.getNameForEmail(recipient);
            
            fragmentHtml.append(FeedbackQuestionFormTemplates.populateTemplate(
                                    fragmentTemplateToUse,
                                    "${recipientName}", recipientName,
                                    "${Average}", df.format(userAverage),
                                    "${Max}", df.format(max.get(recipient)),
                                    "${Min}", df.format(min.get(recipient)),
                                    "${AverageExcludingSelfResponse}", userAverageWithoutSelfResponse));
        }
        
        if (fragmentHtml.length() == 0) {
            return "";
        }
        
        html = FeedbackQuestionFormTemplates.populateTemplate(
                templateToUse,
                "${summaryTitle}", statsTitle,
                "${statsFragments}", fragmentHtml.toString());
        
        return html;
    }


    private String getStudentQuestionResultsStatisticsHtml(
            List<FeedbackResponseAttributes> responses, AccountAttributes currentUser,
            FeedbackQuestionAttributes question, FeedbackSessionResultsBundle bundle) {
        String html = "";
       
        Map<String, Double> min = new HashMap<String, Double>();
        Map<String, Double> max = new HashMap<String, Double>();
        Map<String, Integer> numResponses = new HashMap<String, Integer>();
        Map<String, Double> total = new HashMap<String, Double>();
        Map<String, Double> totalExcludingSelfResponse = new HashMap<String, Double>();
        Map<String, Boolean> userGaveResponseToSelf = new HashMap<String, Boolean>();
        
        List<String> hiddenRecipients = getHiddenRecipients(responses,
                question, bundle);
        
        boolean showAvgExcludingSelfResponse = populateSummaryStatisticsFromResponsesHidingRecipients(
                                               responses, min, max, numResponses, total, totalExcludingSelfResponse, 
                                               userGaveResponseToSelf, hiddenRecipients);
        
        boolean isDirectedAtGeneral = question.recipientType == FeedbackParticipantType.NONE;
        boolean isDirectedAtTeams = (question.recipientType == FeedbackParticipantType.TEAMS) || 
                                    (question.recipientType == FeedbackParticipantType.OWN_TEAM);
        boolean isDirectedAtStudents = !isDirectedAtGeneral && !isDirectedAtTeams;
        
        
        String fragmentTemplateToUse = showAvgExcludingSelfResponse ? 
                                       FeedbackQuestionFormTemplates.NUMSCALE_RESULTS_STATS_FRAGMENT_WITH_SELF_RESPONSE: 
                                       FeedbackQuestionFormTemplates.NUMSCALE_RESULTS_STATS_FRAGMENT;
        String templateToUse = showAvgExcludingSelfResponse ? 
                               FeedbackQuestionFormTemplates.NUMSCALE_RESULT_STATS_WITH_SELF_RESPONSE: 
                               FeedbackQuestionFormTemplates.NUMSCALE_RESULT_STATS;
  
        
        DecimalFormat df = new DecimalFormat();
        df.setMinimumFractionDigits(0);
        df.setMaximumFractionDigits(5);
        df.setRoundingMode(RoundingMode.DOWN);
        
        
        String userFragmentHtml = "";
        String currentUserRecipient = "";
        
        String currentUserTeam = bundle.emailTeamNameTable.get(currentUser.email);
        

        if (isDirectedAtStudents && numResponses.containsKey(currentUser.email)) {
            currentUserRecipient = currentUser.email;
        } else if (isDirectedAtTeams && numResponses.containsKey(currentUserTeam)) {
            currentUserRecipient = currentUserTeam;
        }

        // display the current user's statistics in the first row of the table 
        if (!currentUserRecipient.equals("") && numResponses.get(currentUserRecipient) >= 2) {
            userFragmentHtml = populateStatsFragmentForUser(min, max,
                    numResponses, total, totalExcludingSelfResponse,
                    userGaveResponseToSelf, showAvgExcludingSelfResponse,
                    isDirectedAtStudents, fragmentTemplateToUse, df,
                    currentUserRecipient, currentUserTeam);
        }
        
        
        StringBuilder otherUsersFragmentsHtml = new StringBuilder();
        boolean isAbleToSeeAllResponses = checkIfAllResponsesAreVisible(numResponses, currentUserRecipient);
        
        if (isAbleToSeeAllResponses) {
            for (String recipient : numResponses.keySet()) {
                
                String recipientFragmentHtml = populateStatsFragmentForRecipient(
                        bundle, min, max, numResponses, total,
                        totalExcludingSelfResponse, userGaveResponseToSelf,
                        showAvgExcludingSelfResponse, fragmentTemplateToUse,
                        df, recipient);
                
                otherUsersFragmentsHtml.append(recipientFragmentHtml);
                
            }
        }
        
        if (userFragmentHtml.length() == 0 && otherUsersFragmentsHtml.length() == 0) {
            return "";
        }
        
        String statsTitle = getStatsTitle(isDirectedAtGeneral, isDirectedAtTeams, isAbleToSeeAllResponses);
        
        html = FeedbackQuestionFormTemplates.populateTemplate(
                        templateToUse,
                        "${summaryTitle}", statsTitle,
                        "${statsFragments}", userFragmentHtml + otherUsersFragmentsHtml.toString());
        
        return html;
    }

    private String populateStatsFragmentForUser(Map<String, Double> min,
            Map<String, Double> max, Map<String, Integer> numResponses,
            Map<String, Double> total,
            Map<String, Double> totalExcludingSelfResponse,
            Map<String, Boolean> userGaveResponseToSelf,
            boolean showAvgExcludingSelfResponse, boolean isDirectedAtStudents,
            String fragmentTemplateToUse, DecimalFormat df,
            String currentUserRecipient, String currentUserTeam) {
        String userFragmentHtml;
        double userAverage = total.get(currentUserRecipient) / numResponses.get(currentUserRecipient);
        String userAverageWithoutSelfResponse = "";
        
        if (showAvgExcludingSelfResponse) {
            userAverageWithoutSelfResponse = computeUserAverageWithoutSelfResponse(
                                                 numResponses, totalExcludingSelfResponse,
                                                 userGaveResponseToSelf, df, currentUserRecipient,
                                                 userAverage);
        }
        
        userFragmentHtml = FeedbackQuestionFormTemplates.populateTemplate(
                            fragmentTemplateToUse,
                            "${recipientName}", isDirectedAtStudents? "You" : "Your Team (" + currentUserTeam + ")",
                            "${Average}", df.format(userAverage),
                            "${Max}", df.format(max.get(currentUserRecipient)),
                            "${Min}", df.format(min.get(currentUserRecipient)),
                            "${AverageExcludingSelfResponse}", userAverageWithoutSelfResponse);
        
        numResponses.remove(currentUserRecipient);
        return userFragmentHtml;
    }

    private String populateStatsFragmentForRecipient(
            FeedbackSessionResultsBundle bundle, Map<String, Double> min,
            Map<String, Double> max, Map<String, Integer> numResponses,
            Map<String, Double> total,
            Map<String, Double> totalExcludingSelfResponse,
            Map<String, Boolean> userGaveResponseToSelf,
            boolean showAvgExcludingSelfResponse, String fragmentTemplateToUse,
            DecimalFormat df, String recipient) {
        double userAverage = total.get(recipient) / numResponses.get(recipient);
        String userAverageWithoutSelfResponse = "";
        
        if (showAvgExcludingSelfResponse) {
            userAverageWithoutSelfResponse = computeUserAverageWithoutSelfResponse(
                    numResponses, totalExcludingSelfResponse,
                    userGaveResponseToSelf, df, recipient, userAverage);
        }
        
        String recipientName = recipient.equals(Const.GENERAL_QUESTION)? "General" : bundle.getNameForEmail(recipient);
        
        String recipientFragmentHtml = FeedbackQuestionFormTemplates.populateTemplate(
                fragmentTemplateToUse,
                "${recipientName}", recipientName,
                "${Average}", df.format(userAverage),
                "${Max}", df.format(max.get(recipient)),
                "${Min}", df.format(min.get(recipient)),
                "${AverageExcludingSelfResponse}", userAverageWithoutSelfResponse);
        return recipientFragmentHtml;
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
        
        Map<String, Double> min = new HashMap<String, Double>();
        Map<String, Double> max = new HashMap<String, Double>();
        Map<String, Integer> numResponses = new HashMap<String, Integer>();
        Map<String, Double> total = new HashMap<String, Double>();
        Map<String, Double> totalExcludingSelfResponse = new HashMap<String, Double>();
        Map<String, Boolean> userGaveResponseToSelf = new HashMap<String, Boolean>();
        
        
        List<String> hiddenRecipients = getHiddenRecipients(responses,
                question, bundle);
        
        boolean showAvgExcludingSelfResponse = populateSummaryStatisticsFromResponsesHidingRecipients(
                                               responses, min, max, numResponses, total, totalExcludingSelfResponse, 
                                               userGaveResponseToSelf, hiddenRecipients);
        
        DecimalFormat df = new DecimalFormat();
        df.setMinimumFractionDigits(0);
        df.setMaximumFractionDigits(5);
        df.setRoundingMode(RoundingMode.DOWN);
  
        
        csv += "Recipient, Average, Minimum, Maximum" ;
        
        if (showAvgExcludingSelfResponse) {
            csv += ", Average excluding self response";
        }
        csv += Const.EOL;
        
        for (String recipient : numResponses.keySet()) {
            double userAverage = total.get(recipient) / numResponses.get(recipient);
            
            String recipientName;
            if (recipient.equals(Const.GENERAL_QUESTION)) {
                recipientName = "General";
            } else {
                recipientName = bundle.getNameForEmail(recipient);
            }
            
            csv += Sanitizer.sanitizeForCsv(recipientName) + ",";
            csv += String.valueOf(userAverage) + "," + min.get(recipient).toString() + "," + max.get(recipient).toString();
            
            if (showAvgExcludingSelfResponse) {
                csv += "," + computeUserAverageWithoutSelfResponse(
                        numResponses, totalExcludingSelfResponse,
                        userGaveResponseToSelf, df, recipient, userAverage);
            }
            csv += Const.EOL;
        }
        
        return csv;
    }
    
    private String computeUserAverageWithoutSelfResponse(
            Map<String, Integer> numResponses,
            Map<String, Double> totalExcludingSelfResponse,
            Map<String, Boolean> userGaveResponseToSelf, DecimalFormat df,
            String recipient, double userAverage) {
        String userAverageWithoutSelfResponse;
        
        if (userGaveResponseToSelf.containsKey(recipient) && numResponses.get(recipient) == 1) {
            userAverageWithoutSelfResponse = "-";
        } else if (userGaveResponseToSelf.containsKey(recipient) && totalExcludingSelfResponse.containsKey(recipient)) {
            double userAverageExcludingSelfResponse = totalExcludingSelfResponse.get(recipient) / 
                                               (numResponses.get(recipient) - 1);
            userAverageWithoutSelfResponse = df.format(userAverageExcludingSelfResponse);
        } else {
            double userAverageExcludingSelfResponse = userAverage;
            userAverageWithoutSelfResponse = df.format(userAverageExcludingSelfResponse);
        }
        
        return userAverageWithoutSelfResponse;
    }
    
    
    
    private boolean populateSummaryStatisticsFromResponsesHidingRecipients(
            List<FeedbackResponseAttributes> responses,
            Map<String, Double> min, Map<String, Double> max,
            Map<String, Integer> numResponses, Map<String, Double> total,
            Map<String, Double> totalExcludingSelfResponse,
            Map<String, Boolean> userGaveResponseToSelf,
            List<String> hiddenRecipients) {
        
        boolean showAvgExcludingSelfResponse = false;
        for(FeedbackResponseAttributes response : responses){
            // hidden recipients do not appear in the summary table, so ignore responses with hidden recipients
            if (hiddenRecipients != null && hiddenRecipients.contains(response.recipientEmail)) {
                continue;
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
            
            if (userGaveResponseToSelf.containsKey(response.recipientEmail)) {
                showAvgExcludingSelfResponse = true;
            }
        }
        
        return showAvgExcludingSelfResponse; 
    }
    
    private List<String> getHiddenRecipients(
            List<FeedbackResponseAttributes> responses,
            FeedbackQuestionAttributes question,
            FeedbackSessionResultsBundle bundle) {
        List<String> hiddenRecipients = new ArrayList<String>(); // List of recipients to hide
        FeedbackParticipantType type = question.recipientType;
        for(FeedbackResponseAttributes response : responses){
            if (bundle.visibilityTable.get(response.getId())[1] == false &&
                type != FeedbackParticipantType.SELF &&
                type != FeedbackParticipantType.NONE) {
                
                hiddenRecipients.add(response.recipientEmail);
            }
        }
        return hiddenRecipients;
    }

    
    private String getStatsTitle(boolean isDirectedAtGeneral,
            boolean isDirectedAtTeams, boolean isAbleToSeeAllResponses) {
        String statsTitle;
        if (isDirectedAtGeneral || isAbleToSeeAllResponses) {
            statsTitle = "Response Summary";
        } else if (isDirectedAtTeams) {
            statsTitle = "Summary of responses received by your team";
        } else {
            statsTitle = "Summary of responses received by you";
        }
        return statsTitle;
    }

    private boolean checkIfAllResponsesAreVisible(
            Map<String, Integer> numResponses, String currentUserRecipient) {
        boolean isAbleToSeeAllResponses = false;
        
        for (Entry<String, Integer> entry: numResponses.entrySet()) {
            String recipient = entry.getKey();
            int numOfResponse = entry.getValue();
            if (numOfResponse > 1 && !recipient.equals(currentUserRecipient)) {
                isAbleToSeeAllResponses = true;
                break;
            }
        }
        return isAbleToSeeAllResponses;
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
