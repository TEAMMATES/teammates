package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.FeedbackQuestionFormTemplates;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.Sanitizer;
import teammates.common.util.Utils;
import teammates.logic.core.TeamEvalResult;

public class FeedbackContributionQuestionDetails extends FeedbackQuestionDetails {
    
    public boolean isNotSureAllowed;
    
    public FeedbackContributionQuestionDetails() {
        super(FeedbackQuestionType.CONTRIB);
        this.isNotSureAllowed = true;
    }

    public FeedbackContributionQuestionDetails(String questionText) {
        super(FeedbackQuestionType.CONTRIB, questionText);
        this.isNotSureAllowed = true;
    }
    
    private void setContributionQuestionDetails(boolean isNotSureAllowed) {
        this.isNotSureAllowed = isNotSureAllowed;
    }
       
    @Override
    public boolean extractQuestionDetails(
            Map<String, String[]> requestParameters,
            FeedbackQuestionType questionType) {
        String isNotSureAllowedString = HttpRequestHelper.getValueFromParamMap(
                requestParameters,
                Const.ParamsNames.FEEDBACK_QUESTION_CONTRIBISNOTSUREALLOWED);
        Boolean isNotSureAllowed;
        if (isNotSureAllowedString == null) {
            isNotSureAllowed = false;
        } else {
            isNotSureAllowed = isNotSureAllowedString.equals("on");
        }
        this.setContributionQuestionDetails(isNotSureAllowed);
        return true;
    }

    @Override
    public String getQuestionTypeDisplayName() {
        return Const.FeedbackQuestionTypeNames.CONTRIB;
    }
    
    @Override
    public boolean isChangesRequiresResponseDeletion(FeedbackQuestionDetails newDetails) {
        FeedbackContributionQuestionDetails newContribDetails = (FeedbackContributionQuestionDetails) newDetails;
        return newContribDetails.isNotSureAllowed != this.isNotSureAllowed;
    }
    
    @Override
    public boolean isIndividualResponsesShownToStudents() {
        return false;
    }

    @Override
    public String getQuestionWithExistingResponseSubmissionFormHtml(boolean sessionIsOpen, int qnIdx,
            int responseIdx, String courseId, FeedbackResponseDetails existingResponseDetails) {

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
        return FeedbackQuestionFormTemplates.populateTemplate(
                FeedbackQuestionFormTemplates.CONTRIB_EDIT_FORM,
                "${questionNumber}", Integer.toString(questionNumber),
                "${isNotSureAllowedChecked}", (isNotSureAllowed) ? "checked=\"checked\"" : "",
                "${Const.ParamsNames.FEEDBACK_QUESTION_CONTRIBISNOTSUREALLOWED}",
                Const.ParamsNames.FEEDBACK_QUESTION_CONTRIBISNOTSUREALLOWED);
    }

    @Override
    public String getNewQuestionSpecificEditFormHtml() {
        this.isNotSureAllowed = true;
        
        return "<div id=\"contribForm\">" + 
                    this.getQuestionSpecificEditFormHtml(-1) +
               "</div>";
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
            String studentEmail,
            FeedbackSessionResultsBundle bundle,
            String view) {
        if(view.equals("question")){//for instructor, only question view has stats.
            return getQuestionResultsStatisticsHtmlQuestionView(responses, question, bundle);
        } else if(view.equals("student")){//Student view of stats.
            return getQuestionResultStatisticsHtmlStudentView(responses, question, studentEmail, bundle);
        } else {
            return "";
        }
    }
    
    private String getQuestionResultStatisticsHtmlStudentView(List<FeedbackResponseAttributes> responses,
            FeedbackQuestionAttributes question,
            String studentEmail,
            FeedbackSessionResultsBundle bundle) {
    
        if(responses.size() == 0 ){
            return "";
        }
    
        String currentUserEmail = studentEmail;
        String currentUserTeam = bundle.emailTeamNameTable.get(studentEmail);
        
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
        
        //Each team's contribution question results.
        Map<String, TeamEvalResult> teamResults = getTeamResults(teamNames, teamSubmissionArray, teamMembersEmail);
        
        String html = "";
        
        TeamEvalResult currentUserTeamResults = teamResults.get(currentUserTeam);
        if(currentUserTeamResults == null){
            return "";
        }

        int currentUserIndex = teamMembersEmail.get(currentUserTeam).indexOf(currentUserEmail);
        int selfClaim = currentUserTeamResults.claimed[currentUserIndex][currentUserIndex];
        int teamClaim = currentUserTeamResults.denormalizedAveragePerceived[currentUserIndex][currentUserIndex];
        
        String contribAdditionalInfo = FeedbackQuestionFormTemplates.populateTemplate(
                FeedbackQuestionFormTemplates.CONTRIB_ADDITIONAL_INFO,
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
        
        //List of all teams
        List<String> teamNames = getTeamNames(bundle);
        
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
        Map<String, TeamEvalResult> teamResults = getTeamResults(teamNames, teamSubmissionArray, teamMembersEmail);
        
        //Each person's results summary
        Map<String, StudentResultSummary> studentResults = getStudentResults(
                teamMembersEmail, teamResults);
        
        
        //Check visibility of recipient
        boolean hideRecipient = false;
        FeedbackParticipantType type = question.recipientType;
        for(FeedbackResponseAttributes response : responses){
            if (bundle.visibilityTable.get(response.getId())[1] == false &&
                    type != FeedbackParticipantType.SELF &&
                    type != FeedbackParticipantType.NONE) {
                hideRecipient = true;
            }
        }
        

        String html = "";
        String contribFragments = "";
        
        for(Map.Entry<String, StudentResultSummary> entry : studentResults.entrySet()){
            StudentResultSummary summary = entry.getValue();
            String email = entry.getKey();
            String name = bundle.roster.getStudentForEmail(email).name;
            String team = bundle.roster.getStudentForEmail(email).team;
            
            List<String> teamEmails = teamMembersEmail.get(team);
            TeamEvalResult teamResult = teamResults.get(team);
            int studentIndx = teamEmails.indexOf(email);
            
            String displayName = name;
            String displayTeam = team;
            if (hideRecipient == true) {
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
                    "${studentTeam}", Sanitizer.sanitizeForHtml(displayTeam),
                    "${studentName}", Sanitizer.sanitizeForHtml(displayName),                    
                    "${CC}", getPointsAsColorizedHtml(summary.claimedToInstructor),
                    "${PC}", getPointsAsColorizedHtml(summary.perceivedToInstructor),
                    "${Diff}", getPointsDiffAsHtml(summary),
                    "${RR}", getNormalizedPointsListColorizedDescending(incomingPoints, studentIndx),
                    
                    "${Const.ParamsNames.STUDENT_NAME}", Const.ParamsNames.STUDENT_NAME);
        }
        
        html += FeedbackQuestionFormTemplates.populateTemplate(
                FeedbackQuestionFormTemplates.CONTRIB_RESULT_STATS,
                "${contribFragments}", contribFragments,
                "${Const.Tooltips.CLAIMED}", Sanitizer.sanitizeForHtml(Const.Tooltips.CLAIMED),
                "${Const.Tooltips.PERCEIVED}", Const.Tooltips.PERCEIVED,
                "${Const.Tooltips.FEEDBACK_CONTRIBUTION_POINTS_RECEIVED}", Const.Tooltips.FEEDBACK_CONTRIBUTION_POINTS_RECEIVED,
                "${Const.Tooltips.FEEDBACK_CONTRIBUTION_DIFF}", Const.Tooltips.FEEDBACK_CONTRIBUTION_DIFF);

        
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

        //List of all teams
        List<String> teamNames = getTeamNames(bundle);
        
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
        Map<String, TeamEvalResult> teamResults = getTeamResults(teamNames, teamSubmissionArray, teamMembersEmail);
        
        //Each person's results summary
        Map<String, StudentResultSummary> studentResults = getStudentResults(
                teamMembersEmail, teamResults);
        
        //Check visibility of recipient
        boolean hideRecipient = false;
        
        FeedbackParticipantType type = question.recipientType;
        for(FeedbackResponseAttributes response : responses){
            if (bundle.visibilityTable.get(response.getId())[1] == false &&
                    type != FeedbackParticipantType.SELF &&
                    type != FeedbackParticipantType.NONE) {
                hideRecipient = true;
            }
        }
        
        
        String contribFragments = "";
        Map<String, String> sortedMap = new TreeMap<String, String>();
        
        for(Map.Entry<String, StudentResultSummary> entry : studentResults.entrySet()){
            StudentResultSummary summary = entry.getValue();
            String email = entry.getKey();
            String name = bundle.roster.getStudentForEmail(email).name;
            String team = bundle.roster.getStudentForEmail(email).team;
            
            List<String> teamEmails = teamMembersEmail.get(team);
            TeamEvalResult teamResult = teamResults.get(team);
            int studentIndx = teamEmails.indexOf(email);
            
            String displayName = name;
            String displayTeam = team;
            String displayEmail = email;
            if (hideRecipient == true) {
                String hash = Integer.toString(Math.abs(name.hashCode()));
                displayName = type.toSingularFormString();
                displayName = "Anonymous " + displayName + " " + hash;
                displayTeam = displayName + Const.TEAM_OF_EMAIL_OWNER;
                displayEmail = Const.USER_NOBODY_TEXT;
            }
            
            int[] incomingPoints = new int[teamResult.normalizedPeerContributionRatio.length];
            for(int i=0 ; i<incomingPoints.length ; i++){
                incomingPoints[i] = teamResult.normalizedPeerContributionRatio[i][studentIndx];
            }
                     
            String contribFragmentString = Sanitizer.sanitizeForCsv(displayTeam) + ","
                             + Sanitizer.sanitizeForCsv(displayName) + ","
                             + Sanitizer.sanitizeForCsv(displayEmail) + ","
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
        csv += "In the points given below, an equal share is equal to 100 points. ";
        csv += "e.g. 80 means \"Equal share - 20%\" and 110 means \"Equal share + 10%\"." + Const.EOL;
        csv += "Claimed Contribution (CC) = the contribution claimed by the student." + Const.EOL;
        csv += "Perceived Contribution (PC) = the average value of student's contribution as perceived by the team members." + Const.EOL;
        csv += "Team, Name, Email, CC, PC, Ratings Recieved" + Const.EOL;
        //Data
        csv += contribFragments + Const.EOL;

        return csv;
    }
    
    private List<String> getTeamNames(FeedbackSessionResultsBundle bundle) {
        List<String> teamNames = new ArrayList<String>();
        for (Set<String> teamNamesForSection : bundle.sectionTeamNameTable.values()) {
            teamNames.addAll(teamNamesForSection);
        }
        return teamNames;
    }

    /**
     * @return A Map with student email as key and StudentResultSummary as value for the specified question.
     */
    public Map<String, StudentResultSummary> getStudentResults(FeedbackSessionResultsBundle bundle,
            FeedbackQuestionAttributes question){
        
        List<FeedbackResponseAttributes> responses = getActualResponses(question, bundle);

        List<String> teamNames = getTeamsWithAtLeastOneResponse(responses, bundle);
        
        Map<String, List<String>> teamMembersEmail = getTeamMembersEmail(bundle, teamNames);
        
        Map<String, List<FeedbackResponseAttributes>> teamResponses = getTeamResponses(
                responses, bundle, teamNames);
        
        Map<String, int[][]> teamSubmissionArray = getTeamSubmissionArray(
                teamNames, teamMembersEmail, teamResponses);
        
        Map<String, TeamEvalResult> teamResults = getTeamResults(teamNames, teamSubmissionArray, teamMembersEmail);
        
        return getStudentResults(teamMembersEmail, teamResults);
    }
    
    /**
     * @return A Map with student email as key and TeamEvalResult as value for the specified question.
     */
    public Map<String, TeamEvalResult> getTeamEvalResults(FeedbackSessionResultsBundle bundle,
            FeedbackQuestionAttributes question){
        
        List<FeedbackResponseAttributes> responses = getActualResponses(question, bundle);

        List<String> teamNames = getTeamsWithAtLeastOneResponse(responses, bundle);
        
        Map<String, List<String>> teamMembersEmail = getTeamMembersEmail(bundle, teamNames);
        
        Map<String, List<FeedbackResponseAttributes>> teamResponses = getTeamResponses(
                responses, bundle, teamNames);
        
        Map<String, int[][]> teamSubmissionArray = getTeamSubmissionArray(
                teamNames, teamMembersEmail, teamResponses);
        
        Map<String, TeamEvalResult> teamResults = getTeamResults(teamNames, teamSubmissionArray, teamMembersEmail);
        
        return teamResults;
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
            Map<String, int[][]> teamSubmissionArray, Map<String, List<String>> teamMembersEmail) {
        Map<String, TeamEvalResult> teamResults = new LinkedHashMap<String, TeamEvalResult>();
        for(String team : teamNames){
            TeamEvalResult teamEvalResult = new TeamEvalResult(teamSubmissionArray.get(team));
            teamEvalResult.studentEmails = teamMembersEmail.get(team);
            teamResults.put(team, teamEvalResult);
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
            List<String> memberEmails = new ArrayList<String>(bundle.rosterTeamNameMembersTable.get(teamName));
            teamMembersEmail.put(teamName, memberEmails);
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
        
        if (result.isEmpty()) {
            return getPointsAsColorizedHtml(Const.POINTS_NOT_SUBMITTED);
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
        if (result.isEmpty()) {
            return Integer.toString(Const.INT_UNINITIALIZED);
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
    
    /**
     * Method to color the points by adding <code>span</code> tag with appropriate
     * class (posDiff and negDiff).
     * Positive points will be green, negative will be red, 0 will be black.
     * This will also put N/A or Not Sure for respective points representation.
     * The output will be E+x% for positive points, E-x% for negative points,
     * and just E for equal share.
     * Zero contribution will be printed as 0%
     * @param points
     *         In terms of full percentage, so equal share will be 100, 20% more
     *         from equal share will be 120, etc.
     */
    private static String getPointsAsColorizedHtml(int points) {
        if (points == Const.POINTS_NOT_SUBMITTED || points == Const.INT_UNINITIALIZED) {
            return "<span class=\"color_neutral\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"" + 
                   Const.Tooltips.FEEDBACK_CONTRIBUTION_NOT_AVAILABLE + "\">N/A</span>";
        } else if (points == Const.POINTS_NOT_SURE) {
            return "<span class=\"color-negative\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"" + 
                   Const.Tooltips.FEEDBACK_CONTRIBUTION_NOT_SURE + "\">N/S</span>";
        } else if (points == 0) {
            return "<span class=\"color-negative\">0%</span>";
        } else if (points > 100) {
            return "<span class=\"color-positive\">E +" + (points - 100) + "%</span>";
        } else if (points < 100) {
            return "<span class=\"color-negative\">E -" + (100 - points) + "%</span>";
        } else {
            return "<span class=\"color_neutral\">E</span>";
        }
    }
    
    private static String getPointsDiffAsHtml(StudentResultSummary summary) {
        int claimed = summary.claimedToInstructor;
        int perceived = summary.perceivedToInstructor;
        int diff = perceived - claimed;
        if (perceived == Const.POINTS_NOT_SUBMITTED || perceived == Const.INT_UNINITIALIZED
                || claimed == Const.POINTS_NOT_SUBMITTED || claimed == Const.INT_UNINITIALIZED) {
            return "<span class=\"color_neutral\" data-toggle=\"tooltip\" data-placement=\"top\" "
                   + "data-container=\"body\" title=\"" + Const.Tooltips.FEEDBACK_CONTRIBUTION_NOT_AVAILABLE 
                   + "\">N/A</span>";
        } else if (perceived == Const.POINTS_NOT_SURE || claimed == Const.POINTS_NOT_SURE) {
            return "<span class=\"color-negative\" data-toggle=\"tooltip\" data-placement=\"top\" "
                   + "data-container=\"body\" title=\"" + Const.Tooltips.FEEDBACK_CONTRIBUTION_NOT_SURE + "\">N/S"
                   + "</span>";
        } else if (diff > 0) {
            return "<span class=\"color-positive\">+" + diff + "%</span>";
        } else if (diff < 0) {
            return "<span class=\"color-negative\">" + diff + "%</span>";
        } else {
            return "<span>" + diff + "</span>";
        }
    }
    
    @Override
    public String getCsvHeader() {
        return "Feedback";
    }

    @Override
    public String getQuestionTypeChoiceOption() {
        return "<option value = \"CONTRIB\">"+Const.FeedbackQuestionTypeNames.CONTRIB+"</option>";
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
            
            // Valid answers: 0, 10, 20, .... 190, 200
            boolean isValidRange = frd.getAnswer() >= 0 && frd.getAnswer() <= 200;
            boolean isMultipleOf10 = frd.getAnswer() % 10 == 0;
            if(isValidRange && isMultipleOf10) {
                validAnswer = true;
            }
            if (frd.getAnswer() == Const.POINTS_NOT_SURE || frd.getAnswer() == Const.POINTS_NOT_SUBMITTED) {
                validAnswer = true;
            }
            if(validAnswer == false){
                errors.add(ERROR_INVALID_OPTION);
            }
        }
        return errors;
    }
    
    final static public String ERROR_CONTRIB_QN_INVALID_FEEDBACK_PATH = 
            Const.FeedbackQuestionTypeNames.CONTRIB + " must have "
            + FeedbackParticipantType.STUDENTS.toDisplayGiverName()
            + " and " + FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF.toDisplayRecipientName()
            + " as the feedback giver and recipient respectively."
            + " These values will be used instead.";
    
    @Override
    public String validateGiverRecipientVisibility(FeedbackQuestionAttributes feedbackQuestionAttributes) {
        String errorMsg = "";
        
        // giver type can only be STUDENTS
        if(feedbackQuestionAttributes.giverType != FeedbackParticipantType.STUDENTS) {
            Utils.getLogger().severe("Unexpected giverType for contribution question: " + feedbackQuestionAttributes.giverType + " (forced to :" + FeedbackParticipantType.STUDENTS + ")");
            feedbackQuestionAttributes.giverType = FeedbackParticipantType.STUDENTS;
            errorMsg = ERROR_CONTRIB_QN_INVALID_FEEDBACK_PATH;
        }
        
        // recipient type can only be OWN_TEAM_MEMBERS_INCLUDING_SELF
        if(feedbackQuestionAttributes.recipientType != FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF) {
            Utils.getLogger().severe("Unexpected recipientType for contribution question: " + feedbackQuestionAttributes.recipientType + " (forced to :" + FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF + ")");
            feedbackQuestionAttributes.recipientType = FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF;
            errorMsg = ERROR_CONTRIB_QN_INVALID_FEEDBACK_PATH;
        }
        
        // restrictions on visibility options
        Assumption.assertTrue("Contrib Qn Invalid visibility options",
                (feedbackQuestionAttributes.showResponsesTo.contains(FeedbackParticipantType.RECEIVER)
                == feedbackQuestionAttributes.showResponsesTo.contains(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS) &&
                (feedbackQuestionAttributes.showResponsesTo.contains(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS)
                == feedbackQuestionAttributes.showResponsesTo.contains(FeedbackParticipantType.OWN_TEAM_MEMBERS))));
        
        return errorMsg;
    }
    

    public static String getPerceivedContributionInEqualShareFormatHtml(int i) {
        return "<span>&nbsp;&nbsp;["
                + "Perceived Contribution: "
                + convertToEqualShareFormatHtml(i)
                + "]</span>";
    }
    
    public String getPerceivedContributionHtml(FeedbackQuestionAttributes question,
            String targetEmail, FeedbackSessionResultsBundle bundle) {
        
        if (hasPerceivedContribution(targetEmail, question, bundle)) {
            Map<String, StudentResultSummary> stats = FeedbackContributionResponseDetails.getContribQnStudentResultSummary(question, bundle);
            StudentResultSummary studentResult = stats.get(targetEmail);
            int pc = studentResult.perceivedToInstructor;
            
            String perceivedContributionHtml = FeedbackContributionQuestionDetails.getPerceivedContributionInEqualShareFormatHtml(pc);
            
            return perceivedContributionHtml;
        } else {
            return "";
        }
    }
    
    private boolean hasPerceivedContribution(String email, FeedbackQuestionAttributes question, FeedbackSessionResultsBundle bundle) {
        Map<String, StudentResultSummary> stats = FeedbackContributionResponseDetails.getContribQnStudentResultSummary(question, bundle);
        return stats.containsKey(email);
    }
    
    /**
     * Used to display missing responses between a possible giver and a possible recipient.
     * Returns "No Response" with the Perceived Contribution if the giver is the recipient.
     * Otherwise, returns "No Response".
     */
    @Override
    public String getNoResponseTextInHtml(String giverEmail, String recipientEmail, FeedbackSessionResultsBundle bundle, FeedbackQuestionAttributes question) {
        String noResponseHtml = "<i>" + Const.INSTRUCTOR_FEEDBACK_RESULTS_MISSING_RESPONSE + "</i>";
        
        // in the row for the student's self response,
        // show the perceived contribution if the student has one
        if (giverEmail.equals(recipientEmail) && hasPerceivedContribution(recipientEmail, question, bundle)) {
            noResponseHtml += getPerceivedContributionHtml(question, recipientEmail, bundle);
        } 
        return noResponseHtml;
    }
    
    
    /*
     * The functions below are taken and modified from EvalSubmissionEditPageData.java
     * -------------------------------------------------------------------------------
     */
    
    /**
     * Returns the options for contribution share in a team. 
     */
    private String getContributionOptionsHtml(int points){
        if (points == Const.INT_UNINITIALIZED) {
            points = Const.POINTS_NOT_SUBMITTED;
        }
        String result = "<option class=\""
                + getContributionOptionsColor(Const.POINTS_NOT_SUBMITTED)
                + "\" value=\"" + Const.POINTS_NOT_SUBMITTED + "\""
                + (points == Const.POINTS_NOT_SUBMITTED ? " selected=\"selected\"" : "") + ">"
                + convertToEqualShareFormat(Const.POINTS_NOT_SUBMITTED) + "</option>";
        for(int i=200; i>=0; i-=10){
            result += "<option "+
                        "class=\"" + getContributionOptionsColor(i) + "\" " +
                        "value=\"" + i + "\"" +
                        (i==points ? "selected=\"selected\"" : "") +
                        ">" + convertToEqualShareFormat(i) +
                        "</option>\r\n";
        }
        if (isNotSureAllowed) {
            result += "<option class=\""
                    + getContributionOptionsColor(Const.POINTS_NOT_SURE)
                    + "\" value=\"" + Const.POINTS_NOT_SURE + "\""
                    + (points == Const.POINTS_NOT_SURE ? " selected=\"selected\"" : "") + ">"
                    + "Not Sure</option>";
        }
        return result;
    }
    
    /**
     * Return the CSS color of different point
     */
    private String getContributionOptionsColor(int points){
        if (points == Const.POINTS_NOT_SURE
                || points == Const.POINTS_EQUAL_SHARE
                || points == Const.POINTS_NOT_SUBMITTED) {
            // Not sure, Equal Share, Not Submitted
            return "color_neutral";
        } else if ( points < Const.POINTS_EQUAL_SHARE){
            // Negative share
            return "color-negative";
        } else{
            // Positive share
            return "color-positive";
        }
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
        else if (i == Const.POINTS_NOT_SUBMITTED)
            return "";
        else
            return "";
    }
   
    /**
     * Converts points in integer to String for HTML display.
     * @param i
     * @return points in text form "Equal Share..." with html formatting for colors.
     */
    public static String convertToEqualShareFormatHtml(int i) {
        if(i==Const.INT_UNINITIALIZED)
            return "<span class=\"color_neutral\">N/A</span>";
        else if (i == Const.POINTS_NOT_SUBMITTED)
            return "<span class=\"color_neutral\"></span>";
        else if(i==Const.POINTS_NOT_SURE)
            return "<span class=\"color-negative\">Not Sure</span>";
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

    public boolean isQuestionSkipped(String[] answer) {
        if (answer == null) {
            return true;
        }
        for (String ans : answer) {
            if (!ans.trim().isEmpty() && Integer.parseInt(ans) != Const.POINTS_NOT_SUBMITTED) {
                return false;
            }
        }
        return true;
    }

}
