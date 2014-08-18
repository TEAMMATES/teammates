package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import teammates.common.util.Const;
import teammates.common.util.FeedbackQuestionFormTemplates;
import teammates.common.util.Sanitizer;
import teammates.logic.core.TeamEvalResult;
import teammates.ui.controller.InstructorEvalResultsPageData;
import teammates.ui.controller.PageData;

public class FeedbackContributionQuestionDetails extends FeedbackAbstractQuestionDetails {
    
    public FeedbackContributionQuestionDetails() {
        super(FeedbackQuestionType.CONTRIB);
    }

    public FeedbackContributionQuestionDetails(String questionText) {
        super(FeedbackQuestionType.CONTRIB, questionText);
        
    }

    @Override
    public String getQuestionTypeDisplayName() {
        return Const.FeedbackQuestionTypeNames.CONTRIB;
    }
    
    @Override
    public boolean isChangesRequiresResponseDeletion(FeedbackAbstractQuestionDetails newDetails) {
        return false;
    }

    @Override
    public String getQuestionWithExistingResponseSubmissionFormHtml(boolean sessionIsOpen, int qnIdx,
            int responseIdx, String courseId, FeedbackAbstractResponseDetails existingResponseDetails) {

        FeedbackContributionResponseDetails frd = (FeedbackContributionResponseDetails) existingResponseDetails;
        int points = frd.getAnswer();
        String optionSelectFragmentsHtml = getContributionOptionsHtml(points);
        
        String html = FeedbackQuestionFormTemplates.populateTemplate(
                FeedbackQuestionFormTemplates.CONTRIB_SUBMISSION_FORM,
                "${qnIdx}", Integer.toString(qnIdx),
                "${responseIdx}", Integer.toString(responseIdx),
                "${Const.ParamsNames.FEEDBACK_RESPONSE_TEXT}", Const.ParamsNames.FEEDBACK_RESPONSE_TEXT,
                "${disabled}", sessionIsOpen ? "" : "disabled=\"disabled\"",
                "${contribSelectFragmentsHtml}", optionSelectFragmentsHtml);
        
        return html;
    }

    @Override
    public String getQuestionWithoutExistingResponseSubmissionFormHtml(
            boolean sessionIsOpen, int qnIdx, int responseIdx, String courseId) {

        String optionSelectHtml = getContributionOptionsHtml(Const.INT_UNINITIALIZED);
        
        String html = FeedbackQuestionFormTemplates.populateTemplate(
                FeedbackQuestionFormTemplates.CONTRIB_SUBMISSION_FORM,
                "${qnIdx}", Integer.toString(qnIdx),
                "${responseIdx}", Integer.toString(responseIdx),
                "${Const.ParamsNames.FEEDBACK_RESPONSE_TEXT}", Const.ParamsNames.FEEDBACK_RESPONSE_TEXT,
                "${disabled}", sessionIsOpen ? "" : "disabled=\"disabled\"",
                "${contribSelectFragmentsHtml}", optionSelectHtml);
        
        return html;
    }

    @Override
    public String getQuestionSpecificEditFormHtml(int questionNumber) {
        return "";
    }

    @Override
    public String getQuestionAdditionalInfoHtml(int questionNumber, String additionalInfoId) {
        String additionalInfo = this.getQuestionTypeDisplayName();
        
        String html = FeedbackQuestionFormTemplates.populateTemplate(
                FeedbackQuestionFormTemplates.FEEDBACK_QUESTION_ADDITIONAL_INFO,
                "${more}", "[more]",
                "${less}", "[less]",
                "${questionNumber}", Integer.toString(questionNumber),
                "${additionalInfoId}", additionalInfoId,
                "${questionAdditionalInfo}", additionalInfo);
        return html;
    }
    
    /**
     * Uses classes from evaluations to calculate statistics.
     * Uses actualResponses from FeedbackSessionResultsBundle - need to hide data that should be hidden.
     *      Hide name and teamName if recipient should not be visible.
     */
    @Override
    public String getQuestionResultStatisticsHtml(List<FeedbackResponseAttributes> responses,
            FeedbackQuestionAttributes question,
            AccountAttributes currentUser,
            FeedbackSessionResultsBundle bundle,
            String view) {
        if(view.equals("question")){//for instructor, only question view has stats.
            return getQuestionResultsStatisticsHtmlQuestionView(responses, question, bundle);
        } else if(view.equals("student")){//Student view of stats.
            return getQuestionResultStatisticsHtmlStudentView(responses, question, currentUser, bundle);
        } else {
            return "";
        }
    }
    
    private String getQuestionResultStatisticsHtmlStudentView(List<FeedbackResponseAttributes> responses,
            FeedbackQuestionAttributes question,
            AccountAttributes currentUser,
            FeedbackSessionResultsBundle bundle) {
    
        if(responses.size() == 0 ){
            return "";
        }
    
        String currentUserEmail = currentUser.email;
        String currentUserTeam = bundle.emailTeamNameTable.get(currentUser.email);
        
        responses = getActualResponses(question, bundle);

        //List of teams with at least one response
        List<String> teamNames = getTeamsWithAtLeastOneResponse(responses, bundle);
        
        //Each team's member(email) list
        Map<String, List<String>> teamMembersEmail = getTeamMembersEmail(bundle, teamNames);
        
        //Each team's responses
        Map<String, List<FeedbackResponseAttributes>> teamResponses = getTeamResponses(
                responses, bundle, teamNames);
        
        //Get each team's submission array. -> int[teamSize][teamSize]
        //Where int[0][1] refers points from student 0 to student 1
        //Where student 0 is the 0th student in the list in teamMembersEmail
        Map<String, int[][]> teamSubmissionArray = getTeamSubmissionArray(
                teamNames, teamMembersEmail, teamResponses);
        
        //Each team's eval results.
        Map<String, TeamEvalResult> teamResults = getTeamResults(teamNames, teamSubmissionArray);
        
        String html = "";
        
        TeamEvalResult currentUserTeamResults = teamResults.get(currentUserTeam);
        if(currentUserTeamResults == null){
            return "";
        }

        int currentUserIndex = teamMembersEmail.get(currentUserTeam).indexOf(currentUserEmail);
        int selfClaim = currentUserTeamResults.claimed[currentUserIndex][currentUserIndex];
        int teamClaim = currentUserTeamResults.denormalizedAveragePerceived[currentUserIndex][currentUserIndex];
        
        String contribAdditionalInfo = FeedbackQuestionFormTemplates.populateTemplate(
                FeedbackQuestionFormTemplates.FEEDBACK_QUESTION_ADDITIONAL_INFO,
                "${more}", "[how to interpret, etc..]",
                "${less}", "[less]",
                "${questionNumber}", Integer.toString(question.questionNumber),
                "${additionalInfoId}", "contributionInfo",
                "${questionAdditionalInfo}", FeedbackQuestionFormTemplates.CONTRIB_RESULT_STATS_STUDENT_INFO);
        
        html += FeedbackQuestionFormTemplates.populateTemplate(
                FeedbackQuestionFormTemplates.CONTRIB_RESULT_STATS_STUDENT,
                "${contribAdditionalInfo}", contribAdditionalInfo,
                "${myViewOfMe}", getPointsAsColorizedHtml(selfClaim),
                "${myViewOfOthers}", getNormalizedPointsListColorizedDescending(currentUserTeamResults.claimed[currentUserIndex], currentUserIndex),
                "${teamViewOfMe}",getPointsAsColorizedHtml(teamClaim),
                "${teamViewOfOthers}",getNormalizedPointsListColorizedDescending(currentUserTeamResults.denormalizedAveragePerceived[currentUserIndex], currentUserIndex));

        return html;
    }
    
    private String getQuestionResultsStatisticsHtmlQuestionView(List<FeedbackResponseAttributes> responses,
            FeedbackQuestionAttributes question,
            FeedbackSessionResultsBundle bundle) {
    
        if(responses.size() == 0 ){
            return "";
        }
    
        responses = getActualResponses(question, bundle);

        //List of teams with at least one response
        List<String> teamNames = getTeamsWithAtLeastOneResponse(responses, bundle);
        
        //Each team's member(email) list
        Map<String, List<String>> teamMembersEmail = getTeamMembersEmail(bundle, teamNames);
        
        //Each team's responses
        Map<String, List<FeedbackResponseAttributes>> teamResponses = getTeamResponses(
                responses, bundle, teamNames);
        
        //Get each team's submission array. -> int[teamSize][teamSize]
        //Where int[0][1] refers points from student 0 to student 1
        //Where student 0 is the 0th student in the list in teamMembersEmail
        Map<String, int[][]> teamSubmissionArray = getTeamSubmissionArray(
                teamNames, teamMembersEmail, teamResponses);
        
        //Each team's eval results.
        Map<String, TeamEvalResult> teamResults = getTeamResults(teamNames, teamSubmissionArray);
        
        //Each person's results summary
        Map<String, StudentResultSummary> studentResults = getStudentResults(
                teamMembersEmail, teamResults);
        
        
        //Check visibility of recipient
        boolean hideRecipient = false;
        List<String> hiddenRecipients = new ArrayList<String>();//List of recipients to hide
        FeedbackParticipantType type = question.recipientType;
        for(FeedbackResponseAttributes response : responses){
            if (bundle.visibilityTable.get(response.getId())[1] == false &&
                    type != FeedbackParticipantType.SELF &&
                    type != FeedbackParticipantType.NONE) {
                hiddenRecipients.add(response.recipientEmail);
                hideRecipient = true;
            }
        }
        

        String html = "";
        String contribFragments = "";
        
        for(Map.Entry<String, StudentResultSummary> entry : studentResults.entrySet()){
            StudentResultSummary summary = entry.getValue();
            String email = entry.getKey();
            String name = bundle.emailNameTable.get(email);
            String team = bundle.emailTeamNameTable.get(email);
            
            List<String> teamEmails = teamMembersEmail.get(team);
            TeamEvalResult teamResult = teamResults.get(team);
            int studentIndx = teamEmails.indexOf(email);
            
            String displayName = name;
            String displayTeam = team;
            if(hideRecipient == true && hiddenRecipients.contains(email)){
                String hash = Integer.toString(Math.abs(name.hashCode()));
                displayName = type.toSingularFormString();
                displayName = "Anonymous " + displayName + " " + hash;
                displayTeam = displayName + Const.TEAM_OF_EMAIL_OWNER;
            }
            
            int[] incomingPoints = new int[teamResult.normalizedPeerContributionRatio.length];
            for(int i=0 ; i<incomingPoints.length ; i++){
                incomingPoints[i] = teamResult.normalizedPeerContributionRatio[i][studentIndx];
            }
            
            contribFragments += FeedbackQuestionFormTemplates.populateTemplate(
                    FeedbackQuestionFormTemplates.CONTRIB_RESULT_STATS_FRAGMENT,
                    "${studentTeam}", PageData.sanitizeForHtml(displayTeam),
                    "${studentName}", PageData.sanitizeForHtml(displayName),
                    
                    "${CC}", InstructorEvalResultsPageData.getPointsAsColorizedHtml(summary.claimedToInstructor),
                    "${PC}", InstructorEvalResultsPageData.getPointsAsColorizedHtml(summary.perceivedToInstructor),
                    "${Diff}", InstructorEvalResultsPageData.getPointsDiffAsHtml(summary),
                    "${RR}", getNormalizedPointsListColorizedDescending(incomingPoints, studentIndx),
                    
                    "${Const.ParamsNames.STUDENT_NAME}", Const.ParamsNames.STUDENT_NAME);
        }
        
        html += FeedbackQuestionFormTemplates.populateTemplate(
                FeedbackQuestionFormTemplates.CONTRIB_RESULT_STATS,
                "${contribFragments}", contribFragments,
                "${Const.Tooltips.CLAIMED}", Const.Tooltips.CLAIMED,
                "${Const.Tooltips.PERCEIVED}", Const.Tooltips.PERCEIVED,
                "${Const.Tooltips.EVALUATION_POINTS_RECEIVED}", Const.Tooltips.EVALUATION_POINTS_RECEIVED,
                "${Const.Tooltips.EVALUATION_DIFF}", Const.Tooltips.EVALUATION_DIFF);

        
        return html;
    }
    
    @Override
    public String getQuestionResultStatisticsCsv(
            List<FeedbackResponseAttributes> responses,
            FeedbackQuestionAttributes question,
            FeedbackSessionResultsBundle bundle) {
        
        
        if(responses.size() == 0 ){
            return "";
        }
    
        responses = getActualResponses(question, bundle);

        //List of teams with at least one response
        List<String> teamNames = getTeamsWithAtLeastOneResponse(responses, bundle);
        
        //Each team's member(email) list
        Map<String, List<String>> teamMembersEmail = getTeamMembersEmail(bundle, teamNames);
        
        //Each team's responses
        Map<String, List<FeedbackResponseAttributes>> teamResponses = getTeamResponses(
                responses, bundle, teamNames);
        
        //Get each team's submission array. -> int[teamSize][teamSize]
        //Where int[0][1] refers points from student 0 to student 1
        //Where student 0 is the 0th student in the list in teamMembersEmail
        Map<String, int[][]> teamSubmissionArray = getTeamSubmissionArray(
                teamNames, teamMembersEmail, teamResponses);
        
        //Each team's eval results.
        Map<String, TeamEvalResult> teamResults = getTeamResults(teamNames, teamSubmissionArray);
        
        //Each person's results summary
        Map<String, StudentResultSummary> studentResults = getStudentResults(
                teamMembersEmail, teamResults);
        
        //Check visibility of recipient
        boolean hideRecipient = false;
        List<String> hiddenRecipients = new ArrayList<String>();//List of recipients to hide
        FeedbackParticipantType type = question.recipientType;
        for(FeedbackResponseAttributes response : responses){
            if (bundle.visibilityTable.get(response.getId())[1] == false &&
                    type != FeedbackParticipantType.SELF &&
                    type != FeedbackParticipantType.NONE) {
                hiddenRecipients.add(response.recipientEmail);
                hideRecipient = true;
            }
        }
        
        
        String contribFragments = "";
        Map<String, String> sortedMap = new TreeMap<String, String>();
        
        for(Map.Entry<String, StudentResultSummary> entry : studentResults.entrySet()){
            StudentResultSummary summary = entry.getValue();
            String email = entry.getKey();
            String name = bundle.emailNameTable.get(email);
            String team = bundle.emailTeamNameTable.get(email);
            
            List<String> teamEmails = teamMembersEmail.get(team);
            TeamEvalResult teamResult = teamResults.get(team);
            int studentIndx = teamEmails.indexOf(email);
            
            String displayName = name;
            String displayTeam = team;
            if(hideRecipient == true && hiddenRecipients.contains(email)){
                String hash = Integer.toString(Math.abs(name.hashCode()));
                displayName = type.toSingularFormString();
                displayName = "Anonymous " + displayName + " " + hash;
                displayTeam = displayName + Const.TEAM_OF_EMAIL_OWNER;
            }
            
            int[] incomingPoints = new int[teamResult.normalizedPeerContributionRatio.length];
            for(int i=0 ; i<incomingPoints.length ; i++){
                incomingPoints[i] = teamResult.normalizedPeerContributionRatio[i][studentIndx];
            }
            
            
            
            String contribFragmentString = Sanitizer.sanitizeForCsv(displayTeam) + ","
                             + Sanitizer.sanitizeForCsv(displayName) + ","
                             + Sanitizer.sanitizeForCsv(Integer.toString(summary.claimedToInstructor)) + ","
                             + Sanitizer.sanitizeForCsv(Integer.toString(summary.perceivedToInstructor)) + ","
                             + Sanitizer.sanitizeForCsv(getNormalizedPointsListDescending(incomingPoints, studentIndx)) + Const.EOL;
        
            // Replace all Unset values
            contribFragmentString = contribFragmentString.replaceAll(Integer.toString(Const.INT_UNINITIALIZED), "N/A");
            contribFragmentString = contribFragmentString.replaceAll(Integer.toString(Const.POINTS_NOT_SURE), "Not Sure");
            contribFragmentString = contribFragmentString.replaceAll(Integer.toString(Const.POINTS_NOT_SUBMITTED), "Not Submitted");
            
            //For sorting purposes
            sortedMap.put(displayTeam +"-%-"+ displayName, contribFragmentString);
            
        }

        for( Map.Entry<String, String> entry : sortedMap.entrySet()){
            contribFragments += entry.getValue();
        }
        
        String csv = "";
        
        //Header
        csv += "Team, Name, CC, PC, Ratings Recieved" + Const.EOL;
        //Data
        csv += contribFragments + Const.EOL;

        return csv;
    }
    
    /**
     * @return A Map with student email as key and StudentResultSummary as value for the specified question.
     */
    public Map<String, StudentResultSummary> getStudentResults(FeedbackSessionResultsBundle bundle, FeedbackQuestionAttributes question){
        List<FeedbackResponseAttributes> responses = getActualResponses(question, bundle);

        List<String> teamNames = getTeamsWithAtLeastOneResponse(responses, bundle);
        
        Map<String, List<String>> teamMembersEmail = getTeamMembersEmail(bundle, teamNames);
        
        Map<String, List<FeedbackResponseAttributes>> teamResponses = getTeamResponses(
                responses, bundle, teamNames);
        
        Map<String, int[][]> teamSubmissionArray = getTeamSubmissionArray(
                teamNames, teamMembersEmail, teamResponses);
        
        Map<String, TeamEvalResult> teamResults = getTeamResults(teamNames, teamSubmissionArray);
        
        return getStudentResults(teamMembersEmail, teamResults);
    }

    private Map<String, StudentResultSummary> getStudentResults(
            Map<String, List<String>> teamMembersEmail,
            Map<String, TeamEvalResult> teamResults) {
        Map<String, StudentResultSummary> studentResults = new LinkedHashMap<String, StudentResultSummary>();
        for(Map.Entry<String, TeamEvalResult> entry : teamResults.entrySet()){
            TeamEvalResult teamResult = entry.getValue();
            List<String> teamEmails = teamMembersEmail.get(entry.getKey());
            int i = 0;
            for(String studentEmail : teamEmails){
                StudentResultSummary summary = new StudentResultSummary();
                summary.claimedFromStudent = teamResult.claimed[i][i];
                summary.claimedToInstructor = teamResult.normalizedClaimed[i][i];
                summary.perceivedToStudent = teamResult.denormalizedAveragePerceived[i][i];
                summary.perceivedToInstructor = teamResult.normalizedAveragePerceived[i];
                
                studentResults.put(studentEmail, summary);
                
                i++;
            }
        }
        return studentResults;
    }

    private Map<String, TeamEvalResult> getTeamResults(List<String> teamNames,
            Map<String, int[][]> teamSubmissionArray) {
        Map<String, TeamEvalResult> teamResults = new LinkedHashMap<String, TeamEvalResult>();
        for(String team : teamNames){
            teamResults.put(team, new TeamEvalResult(teamSubmissionArray.get(team)));
        }
        return teamResults;
    }

    private Map<String, int[][]> getTeamSubmissionArray(List<String> teamNames,
            Map<String, List<String>> teamMembersEmail,
            Map<String, List<FeedbackResponseAttributes>> teamResponses) {
        Map<String, int[][]> teamSubmissionArray = new LinkedHashMap<String,int[][]>();
        for(String team : teamNames){
            int teamSize = teamMembersEmail.get(team).size();
            teamSubmissionArray.put(team, new int[teamSize][teamSize]);
            //Initialize all as not submitted.
            for(int i=0 ; i<teamSize ; i++){
                for(int j=0 ; j<teamSize ; j++){
                    teamSubmissionArray.get(team)[i][j] = Const.POINTS_NOT_SUBMITTED;
                }
            }
            //Fill in submitted points
            List<FeedbackResponseAttributes> teamResponseList = teamResponses.get(team);
            List<String> memberEmailList = teamMembersEmail.get(team);
            for(FeedbackResponseAttributes response : teamResponseList){
                int giverIndx = memberEmailList.indexOf(response.giverEmail);
                int recipientIndx = memberEmailList.indexOf(response.recipientEmail);
                if(giverIndx == -1 || recipientIndx == -1){
                    continue;
                }
                int points = ((FeedbackContributionResponseDetails) response.getResponseDetails()).getAnswer();
                teamSubmissionArray.get(team)[giverIndx][recipientIndx] = points;
            }
        }
        return teamSubmissionArray;
    }

    private Map<String, List<FeedbackResponseAttributes>> getTeamResponses(
            List<FeedbackResponseAttributes> responses,
            FeedbackSessionResultsBundle bundle, List<String> teamNames) {
        Map<String, List<FeedbackResponseAttributes>> teamResponses = 
                new LinkedHashMap<String,List<FeedbackResponseAttributes>>();
        for(String teamName : teamNames){
            teamResponses.put(teamName, new ArrayList<FeedbackResponseAttributes>());
        }
        for(FeedbackResponseAttributes response : responses){
            String team = bundle.emailTeamNameTable.get(response.giverEmail);
            if(teamResponses.containsKey(team)){
                teamResponses.get(team).add(response);
            }
        }
        return teamResponses;
    }

    private Map<String, List<String>> getTeamMembersEmail(
            FeedbackSessionResultsBundle bundle, List<String> teamNames) {
        Map<String, List<String>> teamMembersEmail = new LinkedHashMap<String, List<String>>();
        for(String teamName : teamNames){
            teamMembersEmail.put(teamName, new ArrayList<String>());
        }
        for(Map.Entry<String, String> entry : bundle.emailTeamNameTable.entrySet()){
            if(teamMembersEmail.containsKey(entry.getValue())){
                teamMembersEmail.get(entry.getValue()).add(entry.getKey());
            }
        }
        return teamMembersEmail;
    }

    private List<String> getTeamsWithAtLeastOneResponse(
            List<FeedbackResponseAttributes> responses,
            FeedbackSessionResultsBundle bundle) {
        List<String> teamNames = new ArrayList<String>();
        for(FeedbackResponseAttributes response : responses){
            if(!teamNames.contains(bundle.getTeamNameForEmail(response.giverEmail))){
                teamNames.add(bundle.getTeamNameForEmail(response.giverEmail));
            }
        }
        return teamNames;
    }

    private List<FeedbackResponseAttributes> getActualResponses(
            FeedbackQuestionAttributes question,
            FeedbackSessionResultsBundle bundle) {
        List<FeedbackResponseAttributes> responses;
        String questionId = question.getId();
        //Get all actual responses for this question.
        responses = new ArrayList<FeedbackResponseAttributes>();
        for(FeedbackResponseAttributes response : bundle.actualResponses){
            if(response.feedbackQuestionId.equals(questionId)){
                responses.add(response);
            }
        }
        return responses;
    }
    
    private static String getNormalizedPointsListColorizedDescending(int[] subs, int index){
        List<String> result = new ArrayList<String>();
        for(int i=0 ; i<subs.length ; i++){
            if(i==index){
                continue;
            }
            result.add(getPointsAsColorizedHtml(subs[i]));
        }
        Collections.sort(result);
        Collections.reverse(result);
        String resultString = "";
        for(String s : result){
            if(!resultString.isEmpty()){
                resultString+=", ";
            }
            resultString += s;
        }
        return resultString;
    }
    
    private static String getNormalizedPointsListDescending(int[] subs, int index){
        List<String> result = new ArrayList<String>();
        for(int i=0 ; i<subs.length ; i++){
            if(i==index){
                continue;
            }
            result.add(Integer.toString(subs[i]));
        }
        Collections.sort(result);
        Collections.reverse(result);
        String resultString = "";
        for(String s : result){
            if(!resultString.isEmpty()){
                resultString+=", ";
            }
            resultString += s;
        }
        return resultString;
    }
    
    private static String getPointsAsColorizedHtml(int points){
        return PageData.getPointsAsColorizedHtml(points);
    }
    
    @Override
    public String getCsvHeader() {
        return "Feedback";
    }

    @Override
    public List<String> validateQuestionDetails() {
        List<String> errors = new ArrayList<String>();
        return errors;
    }

    final String ERROR_INVALID_OPTION = "Invalid option for the " + Const.FeedbackQuestionTypeNames.CONTRIB + ".";
    
    @Override
    public List<String> validateResponseAttributes(
            List<FeedbackResponseAttributes> responses,
            int numRecipients) {
        List<String> errors = new ArrayList<String>();
        for(FeedbackResponseAttributes response : responses){
            boolean validAnswer = false;
            FeedbackContributionResponseDetails frd = (FeedbackContributionResponseDetails) response.getResponseDetails();
            for(int i=200; i>=0; i-=10){
                if(frd.getAnswer() == i){
                    validAnswer = true;
                    break;
                }
            }
            if(frd.getAnswer() == Const.POINTS_NOT_SURE){
                validAnswer = true;
            }
            
            if(validAnswer == false){
                errors.add(ERROR_INVALID_OPTION);
            }
        }
        return errors;
    }
    
    /*
     * The functions below are taken and modified from EvalSubmissionEditPageData.java
     * -------------------------------------------------------------------------------
     */
    
    /**
     * Returns the options for contribution share in a team. 
     */
    private String getContributionOptionsHtml(int points){
        String result = "";
        if(points==Const.POINTS_NOT_SUBMITTED || points==Const.INT_UNINITIALIZED ){
            points=Const.POINTS_NOT_SURE;
        }
        for(int i=200; i>=0; i-=10){
            result += "<option value=\"" + i + "\"" +
                        (i==points
                        ? "selected=\"selected\""
                        : "") +
                        ">" + convertToEqualShareFormat(i) +
                        "</option>\r\n";
        }
        result+="<option value=\"" + Const.POINTS_NOT_SURE + "\""
                + (points==Const.POINTS_NOT_SURE ? " selected=\"selected\"" : "") + ">" +
                "Not Sure</option>";
        return result;
    }
    
    /**
     * Converts points in integer to String.
     * @param i
     * @return points in text form "Equal Share..."
     */
    public static String convertToEqualShareFormat(int i) {
        if (i > 100)
            return "Equal share + " + (i - 100) + "%"; // Do more
        else if (i == 100)
            return "Equal share"; // Do same
        else if (i > 0)
            return "Equal share - " + (100 - i) + "%"; // Do less
        else if(i == 0)
            return "0%"; // Do none
        else if(i == Const.POINTS_NOT_SURE)
            return "Not Sure";
        else
            return "";
    }
   
    /**
     * Converts points in integer to String for HTML display.
     * @param i
     * @return points in text form "Equal Share..." with html formatting for colors.
     */
    public static String convertToEqualShareFormatHtml(int i) {
        if(i==Const.POINTS_NOT_SUBMITTED || i==Const.INT_UNINITIALIZED)
            return "<span class=\"color-negative\"\">N/A</span>";
        else if(i==Const.POINTS_NOT_SURE)
            return "<span class=\"color-negative\"\">Not Sure</span>";
        else if(i==0)
            return "<span class=\"color-negative\">0%</span>";
        else if(i>100)
            return "<span class=\"color-positive\">Equal Share +"+(i-100)+"%</span>";
        else if(i<100)
            return "<span class=\"color-negative\">Equal Share -"+(100-i)+"%</span>";
        else if(i==100)
            return "<span class=\"color_neutral\">Equal Share</span>";
        else
            return "";
    }

}
