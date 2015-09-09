package teammates.common.datatransfer;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.FeedbackQuestionFormTemplates;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.Sanitizer;
import teammates.common.util.StringHelper;

public class FeedbackNumericalScaleQuestionDetails extends
        FeedbackQuestionDetails {
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
            FeedbackResponseDetails existingResponseDetails) {
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
    public String getNewQuestionSpecificEditFormHtml() {
        // Set default values
        this.minScale = 1;
        this.maxScale = 5;
        this.step = 1;
        
        return "<div id=\"numScaleForm\">" + 
                    this.getQuestionSpecificEditFormHtml(-1) +
               "</div>";
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
            String studentEmail,
            FeedbackSessionResultsBundle bundle,
            String view) {
        
        if (view.equals("student")) {
            return getStudentQuestionResultsStatisticsHtml(responses, studentEmail, question, bundle);
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
        Map<String, Double> average = new HashMap<String, Double>();
        Map<String, Double> averageExcludingSelf = new HashMap<String, Double>();
        Map<String, Double> total = new HashMap<String, Double>();
        Map<String, Double> totalExcludingSelf = new HashMap<String, Double>();
        Map<String, Integer> numResponses = new HashMap<String, Integer>();
        Map<String, Integer> numResponsesExcludingSelf = new HashMap<String, Integer>();
        
        // need to know which recipients are hidden since anonymised recipients will not appear in the summary table
        List<String> hiddenRecipients = getHiddenRecipients(responses, question, bundle);

        
        populateSummaryStatisticsFromResponses(responses, min, max, average, averageExcludingSelf, total, 
                                               totalExcludingSelf, numResponses, numResponsesExcludingSelf);
        
        boolean showAvgExcludingSelf = showAverageExcludingSelf(question, averageExcludingSelf);
        
        String statsTitle = "Response Summary";
        
        String fragmentTemplateToUse = showAvgExcludingSelf ? 
                                       FeedbackQuestionFormTemplates.NUMSCALE_RESULTS_STATS_FRAGMENT_WITH_SELF_RESPONSE:
                                       FeedbackQuestionFormTemplates.NUMSCALE_RESULTS_STATS_FRAGMENT;
        
        String templateToUse = showAvgExcludingSelf ? 
                               FeedbackQuestionFormTemplates.NUMSCALE_RESULT_STATS_WITH_SELF_RESPONSE:
                               FeedbackQuestionFormTemplates.NUMSCALE_RESULT_STATS;

        
        DecimalFormat df = new DecimalFormat();
        df.setMinimumFractionDigits(0);
        df.setMaximumFractionDigits(5);
        df.setRoundingMode(RoundingMode.DOWN);
  
        StringBuilder fragmentHtml = new StringBuilder();        
        
        for (String recipient : numResponses.keySet()) {
            // hidden recipients do not appear in the summary table, so ignore responses with hidden recipients
            if(hiddenRecipients.contains(recipient)){
                continue;
            }
            
            Double userAverageExcludingSelf = averageExcludingSelf.get(recipient);
            String userAverageExcludingSelfText = getAverageExcludingSelfText(showAvgExcludingSelf, df, userAverageExcludingSelf);
            
            String recipientName = recipient.equals(Const.GENERAL_QUESTION) ? "General" : bundle.getNameForEmail(recipient);
            String recipientTeam  = bundle.getTeamNameForEmail(recipient);

            fragmentHtml.append(FeedbackQuestionFormTemplates.populateTemplate(
                                    fragmentTemplateToUse,
                                    "${recipientTeam}", recipientTeam,
                                    "${recipientName}", recipientName,
                                    "${Average}", df.format(average.get(recipient)),
                                    "${Max}", df.format(max.get(recipient)),
                                    "${Min}", df.format(min.get(recipient)),
                                    "${AverageExcludingSelfResponse}", userAverageExcludingSelfText));
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
            List<FeedbackResponseAttributes> responses, String studentEmail,
            FeedbackQuestionAttributes question, FeedbackSessionResultsBundle bundle) {
        String html = "";
       
        Map<String, Double> min = new HashMap<String, Double>();
        Map<String, Double> max = new HashMap<String, Double>();
        Map<String, Double> average = new HashMap<String, Double>();
        Map<String, Double> averageExcludingSelf = new HashMap<String, Double>();
        Map<String, Double> total = new HashMap<String, Double>();
        Map<String, Double> totalExcludingSelf = new HashMap<String, Double>();
        Map<String, Integer> numResponses = new HashMap<String, Integer>();
        Map<String, Integer> numResponsesExcludingSelf = new HashMap<String, Integer>();
        
        // need to know which recipients are hidden since anonymised recipients will not appear in the summary table
        List<String> hiddenRecipients = getHiddenRecipients(responses, question, bundle);

        
        populateSummaryStatisticsFromResponses(responses, min, max, average, averageExcludingSelf, total, 
                                               totalExcludingSelf, numResponses, numResponsesExcludingSelf);
        boolean showAvgExcludingSelf = showAverageExcludingSelf(question, averageExcludingSelf);

        String fragmentTemplateToUse = showAvgExcludingSelf ? 
                                       FeedbackQuestionFormTemplates.NUMSCALE_RESULTS_STATS_FRAGMENT_WITH_SELF_RESPONSE: 
                                       FeedbackQuestionFormTemplates.NUMSCALE_RESULTS_STATS_FRAGMENT;
        String templateToUse = showAvgExcludingSelf ? 
                               FeedbackQuestionFormTemplates.NUMSCALE_RESULT_STATS_WITH_SELF_RESPONSE: 
                               FeedbackQuestionFormTemplates.NUMSCALE_RESULT_STATS;
  
        
        DecimalFormat df = new DecimalFormat();
        df.setMinimumFractionDigits(0);
        df.setMaximumFractionDigits(5);
        df.setRoundingMode(RoundingMode.DOWN);

        
        boolean isRecipientTypeGeneral = question.recipientType == FeedbackParticipantType.NONE;
        boolean isRecipientTypeTeam = (question.recipientType == FeedbackParticipantType.TEAMS) || 
                                    (question.recipientType == FeedbackParticipantType.OWN_TEAM);
        boolean isRecipientTypeStudent = !isRecipientTypeGeneral && !isRecipientTypeTeam;
        
        String currentUserTeam = bundle.getTeamNameForEmail(studentEmail);
        String currentUserIdentifier = getCurrentUserIdentifier(numResponses,
                                                                isRecipientTypeStudent, studentEmail, 
                                                                isRecipientTypeTeam, currentUserTeam);  
        
        Set<String> recipientSet = numResponses.keySet();
        ArrayList<String> recipientList = new ArrayList<String>();
        
        boolean hasCurrentUserReceivedAnyResponse = recipientSet.contains(currentUserIdentifier);
        
        // Move current user to the head of the recipient list
        if(hasCurrentUserReceivedAnyResponse){
            recipientList.add(currentUserIdentifier);   
        }        
        for(String otherRecipient : recipientSet){
            // Skip current user as it is added to the head of the list
            if(otherRecipient.equalsIgnoreCase(currentUserIdentifier)){
                continue;
            }            
            recipientList.add(otherRecipient);
        }
        
        StringBuilder fragmentHtml = new StringBuilder();
        for (String recipient : recipientList) {
            
            // hidden recipients do not appear in the summary table, so ignore responses with hidden recipients
            boolean isHiddenRecipient = false;
            if(hiddenRecipients.contains(recipient)){
                isHiddenRecipient = true;
            }
            
            String recipientName = null;
            String recipientTeam = null;
            
            boolean isRecipientCurrentUser = recipient.equalsIgnoreCase(currentUserIdentifier);
            boolean isRecipientGeneral = recipient.equalsIgnoreCase(Const.GENERAL_QUESTION);
            
            recipientName = getDisplayableRecipientName(isHiddenRecipient,
                            isRecipientCurrentUser, hasAtLeastTwoResponses(numResponses, currentUserIdentifier),
                            isRecipientTypeStudent, hasAtLeastTwoResponsesOtherThanCurrentUser(numResponses,currentUserIdentifier),
                            isRecipientGeneral, bundle.getNameForEmail(recipient), currentUserTeam);
            
            recipientTeam = getDisplayableRecipientTeam(isHiddenRecipient,
                            isRecipientCurrentUser, hasAtLeastTwoResponses(numResponses, currentUserIdentifier),
                            isRecipientTypeStudent, hasAtLeastTwoResponsesOtherThanCurrentUser(numResponses,currentUserIdentifier),
                            bundle.getTeamNameForEmail(recipient), currentUserTeam);

            Double minScore = null;
            Double maxScore = null;
            Double averageScore = null;
            Double averageScoreExcludingSelf = null;
            
            boolean isRecipientDetailsAvailable = recipientName != null && recipientTeam != null;
            
            if(!isRecipientDetailsAvailable){
                continue;
            }
            
            minScore = min.get(recipient);
            maxScore = max.get(recipient);
            averageScore = average.get(recipient);
            averageScoreExcludingSelf = averageExcludingSelf.get(recipient);
            
            String averageScoreExcludingSelfText = getAverageExcludingSelfText(showAvgExcludingSelf, df, averageScoreExcludingSelf);
            
            String recipientFragmentHtml = FeedbackQuestionFormTemplates.populateTemplate(
                    fragmentTemplateToUse,
                    "${recipientTeam}", recipientTeam,
                    "${recipientName}", recipientName,
                    "${Average}", df.format(averageScore),
                    "${Max}", df.format(maxScore),
                    "${Min}", df.format(minScore),
                    "${AverageExcludingSelfResponse}", averageScoreExcludingSelfText);
            
            fragmentHtml.append(recipientFragmentHtml);
        }
        
        if (fragmentHtml.length() == 0) {
            return "";
        }
        
        String statsTitle = getStatsTitle(isRecipientTypeGeneral, isRecipientTypeTeam, 
                            hasAtLeastTwoResponsesOtherThanCurrentUser(numResponses, currentUserIdentifier));
        
        html = FeedbackQuestionFormTemplates.populateTemplate(
                        templateToUse,
                        "${summaryTitle}", statsTitle,
                        "${statsFragments}", fragmentHtml.toString());
        
        return html;
    }

    private String getDisplayableRecipientName(boolean isHiddenRecipient,
            boolean isRecipientCurrentUser, boolean hasAtLeastTwoResponses,
            boolean isRecipientTypeStudent, boolean hasAtLeastTwoResponsesOtherThanCurrentUser,
            boolean isRecipientGeneral, String recipientName, String currentUserTeam) {
        
        // Replace current user name with "You"
        if (!isHiddenRecipient && isRecipientCurrentUser && hasAtLeastTwoResponses){
            return isRecipientTypeStudent? "You" : "Your Team (" + currentUserTeam + ")";
        }
        
        // Replace general identifier with "General"
        if (!isHiddenRecipient && !isRecipientCurrentUser && 
            hasAtLeastTwoResponsesOtherThanCurrentUser){
            return isRecipientGeneral ? "General" : recipientName;
        }
        return null;
    }
    
    private String getDisplayableRecipientTeam(boolean isHiddenRecipient,
            boolean isRecipientCurrentUser, boolean hasAtLeastTwoResponses,
            boolean isRecipientTypeStudent, boolean hasAtLeastTwoResponsesOtherThanCurrentUser,
            String recipientTeamName, String currentUserTeam) {
        
        // Replace current user team with "" when recipient type is not student
        if (!isHiddenRecipient && isRecipientCurrentUser && hasAtLeastTwoResponses){
            return isRecipientTypeStudent? currentUserTeam : "";
        }
        
        // Display other recipients' team name
        if (!isHiddenRecipient && !isRecipientCurrentUser && 
            hasAtLeastTwoResponsesOtherThanCurrentUser){
            return recipientTeamName;
        }
        return null;
    }
    
    private String getCurrentUserIdentifier(Map<String, Integer> numResponses, 
            boolean isRecipientStudent, String currentUserEmail,
            boolean isRecipientTeam, String currentUserTeam) {
        
        if (isRecipientStudent && numResponses.containsKey(currentUserEmail) &&
                numResponses.get(currentUserEmail) >= 1) {
            return currentUserEmail;
        } else if (isRecipientTeam && numResponses.containsKey(currentUserTeam) && 
                numResponses.get(currentUserTeam) >= 1) {
            return currentUserTeam;
        } else {
            return "";
        }
    }

    private boolean hasAtLeastTwoResponses(Map<String, Integer> numResponses,
            String recipient) {
        if(numResponses == null){
            return false;
        }
        Integer numOfResponses = numResponses.get(recipient);
        return numOfResponses == null ? false : numOfResponses >= 2;
    }

    private String getAverageExcludingSelfText(boolean showAvgExcludingSelf, DecimalFormat df, Double averageExcludingSelf) {
        // Display a dash if the user has only self response
        String averageExcludingSelfText = averageExcludingSelf == null ? "-" : df.format(averageExcludingSelf);
        
        averageExcludingSelfText = showAvgExcludingSelf ? averageExcludingSelfText : "";
        return averageExcludingSelfText;
    }
    
    @Override
    public String getQuestionResultStatisticsCsv(
            List<FeedbackResponseAttributes> responses,
            FeedbackQuestionAttributes question,
            FeedbackSessionResultsBundle bundle) {
        if(responses.size() == 0){
            return "";
        }
        
        Map<String, Double> min = new HashMap<String, Double>();
        Map<String, Double> max = new HashMap<String, Double>();
        Map<String, Double> average = new HashMap<String, Double>();
        Map<String, Double> averageExcludingSelf = new HashMap<String, Double>();
        Map<String, Double> total = new HashMap<String, Double>();
        Map<String, Double> totalExcludingSelf = new HashMap<String, Double>();
        Map<String, Integer> numResponses = new HashMap<String, Integer>();
        Map<String, Integer> numResponsesExcludingSelf = new HashMap<String, Integer>();
        
        // need to know which recipients are hidden since anonymised recipients will not appear in the summary table
        List<String> hiddenRecipients = getHiddenRecipients(responses, question, bundle);
        
        populateSummaryStatisticsFromResponses(responses, min, max, average, averageExcludingSelf, total, 
                                               totalExcludingSelf, numResponses, numResponsesExcludingSelf);
        
        boolean showAvgExcludingSelf = showAverageExcludingSelf(question, averageExcludingSelf);
        
        DecimalFormat df = new DecimalFormat();
        df.setMinimumFractionDigits(0);
        df.setMaximumFractionDigits(5);
        df.setRoundingMode(RoundingMode.DOWN);
  
        String csvHeader = "";
        csvHeader += "Team, Recipient, Average, Minimum, Maximum" ;
        csvHeader += showAvgExcludingSelf ? ", Average excluding self response" : "";
        csvHeader += Const.EOL;
        
        String csvBody = "";
        for (String recipient : numResponses.keySet()) {
            // hidden recipients do not appear in the summary table, so ignore responses with hidden recipients
            if(hiddenRecipients.contains(recipient)){
                continue;
            }
            
            String recipientTeam = bundle.getTeamNameForEmail(recipient);
            boolean isRecipientGeneral = recipient.equals(Const.GENERAL_QUESTION);
            
            Double averageScoreExcludingSelf = averageExcludingSelf.get(recipient);
            String averageScoreExcludingSelfText = getAverageExcludingSelfText(showAvgExcludingSelf, df, averageScoreExcludingSelf);
            
            csvBody += Sanitizer.sanitizeForCsv(recipientTeam);
            csvBody += "," + Sanitizer.sanitizeForCsv(isRecipientGeneral ? "General" : bundle.getNameForEmail(recipient));
            csvBody += "," + df.format(average.get(recipient));
            csvBody += "," + df.format(min.get(recipient));
            csvBody += "," + df.format(max.get(recipient));
            csvBody += showAvgExcludingSelf ? "," + averageScoreExcludingSelfText : "" ;
            csvBody += Const.EOL;
        }
        
        String csv = csvHeader + csvBody;
        return csv;
    }
    
    private boolean showAverageExcludingSelf(
            FeedbackQuestionAttributes question, Map<String, Double> averageExcludingSelf) {
        
        if(question.recipientType == FeedbackParticipantType.NONE){
            // General recipient type would not give self response
            // Therefore average exclude self response will always be hidden
            return false;
        }        
        
        for(Double average : averageExcludingSelf.values()){
            // There exists at least one average score exclude self
            if(average != null){
                return true;
            }
        }
        return false;
    }

    private void populateSummaryStatisticsFromResponses(
            List<FeedbackResponseAttributes> responses,
            Map<String, Double> min, Map<String, Double> max,
            Map<String, Double> average, Map<String, Double> averageExcludingSelf,
            Map<String, Double> total, Map<String, Double> totalExcludingSelf,
            Map<String, Integer> numResponses,
            Map<String, Integer> numResponsesExcludingSelf) {
        
        for(FeedbackResponseAttributes response : responses){
            FeedbackNumericalScaleResponseDetails responseDetails = (FeedbackNumericalScaleResponseDetails)response.getResponseDetails();
            double answer = responseDetails.getAnswer();
            String giverEmail = response.giverEmail;
            String recipientEmail = response.recipientEmail;

            // Compute number of responses including user's self response
            if(!numResponses.containsKey(recipientEmail)){
                numResponses.put(recipientEmail, 0);
            }            
            int numOfResponses = numResponses.get(recipientEmail) + 1;
            numResponses.put(recipientEmail, numOfResponses);
            
            
            // Compute number of responses excluding user's self response
            if(!numResponsesExcludingSelf.containsKey(recipientEmail)){
                numResponsesExcludingSelf.put(recipientEmail, 0);
            }            
            boolean isSelfResponse = giverEmail.equalsIgnoreCase(recipientEmail);
            if(!isSelfResponse){
                int numOfResponsesExcludingSelf = numResponsesExcludingSelf.get(recipientEmail) + 1;
                numResponsesExcludingSelf.put(recipientEmail, numOfResponsesExcludingSelf);
            }
            
            
            // Compute minimum score received
            if(!min.containsKey(recipientEmail)){
                min.put(recipientEmail, answer);
            }            
            double minScoreReceived = Math.min(answer, min.get(recipientEmail));
            min.put(recipientEmail, minScoreReceived);
            
            
            // Compute maximum score received
            if(!max.containsKey(recipientEmail)){
                max.put(recipientEmail, answer);
            }
            double maxScoreReceived = Math.max(answer, max.get(recipientEmail));
            max.put(recipientEmail, maxScoreReceived);
            
            
            // Compute total score received
            if(!total.containsKey(recipientEmail)){
                total.put(recipientEmail, 0.0);
            }
            double totalScore = total.get(recipientEmail) + answer;
            total.put(recipientEmail, totalScore);
            
            
            // Compute total score received excluding self
            if(!totalExcludingSelf.containsKey(recipientEmail)){
                totalExcludingSelf.put(recipientEmail, null);
            }            
            if(!isSelfResponse) {
                Double totalScoreExcludingSelf = totalExcludingSelf.get(recipientEmail);
                
                // totalScoreExcludingSelf == null when the user has only self response
                totalExcludingSelf.put(recipientEmail, totalScoreExcludingSelf == null ? answer : totalScoreExcludingSelf + answer);                
            }
            
            
            // Compute average score received
            if(!average.containsKey(recipientEmail)){
                average.put(recipientEmail, 0.0);
            }
            double averageReceived = total.get(recipientEmail) / numResponses.get(recipientEmail);
            average.put(recipientEmail, averageReceived);
            
            
            // Compute average score received excluding self
            if(!averageExcludingSelf.containsKey(recipientEmail)){
                averageExcludingSelf.put(recipientEmail, null);
            }
            if(!isSelfResponse && totalExcludingSelf.get(recipientEmail) != null) {
                double averageReceivedExcludingSelf = totalExcludingSelf.get(recipientEmail) / numResponsesExcludingSelf.get(recipientEmail);
                averageExcludingSelf.put(recipientEmail, averageReceivedExcludingSelf);
            }
        }        
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

    /**
     * Return true when the number of responses for any recipient other than the current user has at least 2.
     * This is used for displaying the statistic for other users as it doesn't make sense when all other users
     * have only 1 response each
     * Return false otherwise.
     */
    private boolean hasAtLeastTwoResponsesOtherThanCurrentUser(
            Map<String, Integer> numResponses, String currentUserIdentifier) {
        boolean isAtLeastTwoResponsesOtherThanCurrentUser = false;
        
        // At least 2 responses are given to any recipient other than current user
        for (String recipient: numResponses.keySet()) {

            if (hasAtLeastTwoResponses(numResponses, recipient) && 
                !recipient.equals(currentUserIdentifier)) {
                
                isAtLeastTwoResponsesOtherThanCurrentUser = true;
                break;
            }
        }
        return isAtLeastTwoResponsesOtherThanCurrentUser;
    }
    
    
    @Override
    public boolean isChangesRequiresResponseDeletion(
            FeedbackQuestionDetails newDetails) {
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

    @Override
    public String getQuestionTypeChoiceOption() {
        return "<option value = \"NUMSCALE\">"+Const.FeedbackQuestionTypeNames.NUMSCALE+"</option>";
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
