package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.mortbay.log.Log;

import teammates.common.util.Const;
import teammates.common.util.FeedbackQuestionFormTemplates;
import teammates.logic.core.TeamEvalResult;

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
                "${questionNumber}", Integer.toString(questionNumber),
                "${additionalInfoId}", additionalInfoId,
                "${questionAdditionalInfo}", additionalInfo);
        return html;
    }
    
    /**
     * Uses classes from evaluations to calculate statistics.
     */
    @Override
    public String getQuestionResultStatisticsHtml(List<FeedbackResponseAttributes> responses,
            FeedbackSessionResultsBundle bundle) {
        if(responses.size() == 0){
            return "";
        }

        String html = "";
        
        
        //List of teams with at least one response
        List<String> teamNames = new ArrayList<String>();
        for(FeedbackResponseAttributes response : responses){
            if(!teamNames.contains(bundle.getTeamNameForEmail(response.giverEmail))){
                teamNames.add(bundle.getTeamNameForEmail(response.giverEmail));
            }
        }
        
        //Each team's member(email) list
        Map<String, List<String>> teamMembersEmail = new LinkedHashMap<String, List<String>>();
        for(String teamName : teamNames){
            teamMembersEmail.put(teamName, new ArrayList<String>());
        }
        for(Map.Entry<String, String> entry : bundle.emailTeamNameTable.entrySet()){
            if(teamMembersEmail.containsKey(entry.getValue())){
                teamMembersEmail.get(entry.getValue()).add(entry.getKey());
            }
        }
        
        //Each team's responses
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
        
        //Get each team's submission array. -> int[teamSize][teamSize]
        //Where int[0][1] refers points from student 0 to student 1
        //Where student 0 is the 0th student in the list in teamMembersEmail
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
                int points = ((FeedbackContributionResponseDetails) response.getResponseDetails()).getAnswer();
                teamSubmissionArray.get(team)[giverIndx][recipientIndx] = points;
            }
        }
        
        
        //For testing
        for(Map.Entry<String, List<String>> entry : teamMembersEmail.entrySet()){
            html += entry.getKey() + " size: " +  teamMembersEmail.get(entry.getKey()).size() +"<br>";
            html += entry.getValue().toString() + "<br>";
            
            html += "Submission Array:<br>";
            for(int i=0 ; i<teamSubmissionArray.get(entry.getKey()).length ; i++)
                html += Arrays.toString(teamSubmissionArray.get(entry.getKey())[i]) + "<br>";
            
        }
        
        
        //Each team's eval results.
        Map<String, TeamEvalResult> teamResults = new LinkedHashMap<String, TeamEvalResult>();
        
        
        
        
        
        return html;
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

}
