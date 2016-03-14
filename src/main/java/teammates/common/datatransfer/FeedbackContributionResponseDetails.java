package teammates.common.datatransfer;

import java.util.HashMap;
import java.util.Map;

import teammates.common.util.Const;
import teammates.common.util.Sanitizer;
import teammates.common.util.Utils;
import teammates.logic.core.TeamEvalResult;

public class FeedbackContributionResponseDetails extends FeedbackResponseDetails {
    /**This is the claimed points from giver to recipient.
    */
    private int answer;
    
    public FeedbackContributionResponseDetails() {
        super(FeedbackQuestionType.CONTRIB);
        answer = Const.POINTS_NOT_SUBMITTED;
    }
    
    public FeedbackContributionResponseDetails(int answer) {
        super(FeedbackQuestionType.CONTRIB);
        this.answer = answer;
    }    
    
    @Override
    public void extractResponseDetails(FeedbackQuestionType questionType,
            FeedbackQuestionDetails questionDetails, String[] answer) {
        try {
            int contribAnswer = Integer.parseInt(answer[0]);
            setAnswer(contribAnswer);
        } catch (NumberFormatException e) {
            Utils.getLogger().severe("Failed to parse contrib answer to integer - " + answer[0]);
            throw e;
        }
    }

    /**
     * Get answer in integer form
     * @return
     */
    public int getAnswer() {
        return answer;
    }
    
    @Override
    public String getAnswerString() {
        return Integer.toString(answer);
    }
    
    // Not used for contribution question, due to calculations required. See corresponding function below.
    @Override
    public String getAnswerHtml(FeedbackQuestionDetails questionDetails) {
        return FeedbackContributionQuestionDetails.convertToEqualShareFormatHtml(getAnswer());
    }

    // Not used for contribution question, due to calculations required. See corresponding function below.
    @Override
    public String getAnswerCsv(FeedbackQuestionDetails questionDetails) {
        return Sanitizer.sanitizeForCsv(FeedbackContributionQuestionDetails.convertToEqualShareFormat(getAnswer()));
    }
    
    @Override
    public String getAnswerHtml(FeedbackResponseAttributes response, FeedbackQuestionAttributes question, FeedbackSessionResultsBundle feedbackSessionResultsBundle) {
        return getContributionQuestionResponseAnswerHtml(response, question, feedbackSessionResultsBundle);
    }

    @Override
    public String getAnswerCsv(FeedbackResponseAttributes response, FeedbackQuestionAttributes question, FeedbackSessionResultsBundle feedbackSessionResultsBundle) {
        return getContributionQuestionResponseAnswerCsv(response, question, feedbackSessionResultsBundle);
    }

    private void setAnswer(int answer) {
        this.answer = answer;
    }
    
    private String getContributionQuestionResponseAnswerHtml(
            FeedbackResponseAttributes response,
            FeedbackQuestionAttributes question,
            FeedbackSessionResultsBundle feedbackSessionResultsBundle) {
        Map<String, TeamEvalResult> teamResults = getContribQnTeamEvalResult(question, feedbackSessionResultsBundle);
        Map<String, StudentResultSummary> stats = getContribQnStudentResultSummary(question, feedbackSessionResultsBundle);
        
        // Need to get actual team name and giver/recipient emails here,
        // only for getting the responseAnswer.
        FeedbackResponseAttributes actualResponse = feedbackSessionResultsBundle.getActualResponse(response);
        String giverTeamName = feedbackSessionResultsBundle.emailTeamNameTable.get(actualResponse.giverEmail);
        TeamEvalResult teamResult = teamResults.get(giverTeamName);
        
        int giverIndex = teamResult.studentEmails.indexOf(actualResponse.giverEmail);
        int recipientIndex = teamResult.studentEmails.indexOf(actualResponse.recipientEmail);
        
        
        String responseAnswerHtml = "";
        
        if (giverIndex == -1 || recipientIndex == -1) {
            if (giverIndex == -1) {
                Utils.getLogger().severe("getContributionQuestionResponseAnswerHtml - giverIndex is -1\n"
                        + "Cannot find giver: " + actualResponse.giverEmail + "\n"
                        + "CourseId: " + feedbackSessionResultsBundle.feedbackSession.courseId + "\n"
                        + "Session Name: " + feedbackSessionResultsBundle.feedbackSession.feedbackSessionName + "\n"
                        + "Response Id: " + actualResponse.getId());
            }
            if (recipientIndex == -1) {
                Utils.getLogger().severe("getContributionQuestionResponseAnswerHtml - recipientIndex is -1\n"
                        + "Cannot find recipient: " + actualResponse.recipientEmail + "\n"
                        + "CourseId: " + feedbackSessionResultsBundle.feedbackSession.courseId + "\n"
                        + "Session Name: " + feedbackSessionResultsBundle.feedbackSession.feedbackSessionName + "\n"
                        + "Response Id: " + actualResponse.getId());
            }
        } else {
            responseAnswerHtml = FeedbackContributionQuestionDetails.convertToEqualShareFormatHtml(
                    teamResult.normalizedPeerContributionRatio[giverIndex][recipientIndex]);
    
            if (response.giverEmail.equals(response.recipientEmail)) {
                StudentResultSummary studentResult = stats.get(response.giverEmail);
                responseAnswerHtml = FeedbackContributionQuestionDetails.convertToEqualShareFormatHtml(
                        studentResult.claimedToInstructor);
                if (studentResult != null) {
                    //For CONTRIB qns, We want to show PC if giver == recipient.
                    int pc = studentResult.perceivedToInstructor;
                    responseAnswerHtml += FeedbackContributionQuestionDetails.getPerceivedContributionInEqualShareFormatHtml(pc);
                }
            }
        }
        return responseAnswerHtml;
    }
    
    private String getContributionQuestionResponseAnswerCsv(
            FeedbackResponseAttributes response,
            FeedbackQuestionAttributes question,
            FeedbackSessionResultsBundle feedbackSessionResultsBundle) {
        Map<String, TeamEvalResult> teamResults = getContribQnTeamEvalResult(question, feedbackSessionResultsBundle);
        Map<String, StudentResultSummary> stats = getContribQnStudentResultSummary(question, feedbackSessionResultsBundle);
        
        // Need to get actual team name and giver/recipient emails here,
        // only for getting the responseAnswer.
        FeedbackResponseAttributes actualResponse = feedbackSessionResultsBundle.getActualResponse(response);
        String giverTeamName = feedbackSessionResultsBundle.emailTeamNameTable.get(actualResponse.giverEmail);
        TeamEvalResult teamResult = teamResults.get(giverTeamName);
        
        int giverIndex = teamResult.studentEmails.indexOf(actualResponse.giverEmail);
        int recipientIndex = teamResult.studentEmails.indexOf(actualResponse.recipientEmail);
        
        String responseAnswerCsv = "";
        
        if (giverIndex == -1 || recipientIndex == -1) {
            if (giverIndex == -1) {
                Utils.getLogger().severe("getContributionQuestionResponseAnswerCsv - giverIndex is -1\n"
                        + "Cannot find giver: " + actualResponse.giverEmail + "\n"
                        + "CourseId: " + feedbackSessionResultsBundle.feedbackSession.courseId + "\n"
                        + "Session Name: " + feedbackSessionResultsBundle.feedbackSession.feedbackSessionName + "\n"
                        + "Response Id: " + actualResponse.getId());
            }
            if (recipientIndex == -1) {
                Utils.getLogger().severe("getContributionQuestionResponseAnswerCsv - recipientIndex is -1\n"
                        + "Cannot find recipient: " + actualResponse.recipientEmail + "\n"
                        + "CourseId: " + feedbackSessionResultsBundle.feedbackSession.courseId + "\n"
                        + "Session Name: " + feedbackSessionResultsBundle.feedbackSession.feedbackSessionName + "\n"
                        + "Response Id: " + actualResponse.getId());
            }
        } else {
            responseAnswerCsv = Sanitizer.sanitizeForCsv(
                    FeedbackContributionQuestionDetails.convertToEqualShareFormat(
                            teamResult.normalizedPeerContributionRatio[giverIndex][recipientIndex]));
            
            if (response.giverEmail.equals(response.recipientEmail)) {
                StudentResultSummary studentResult = stats.get(response.giverEmail);
                responseAnswerCsv = Sanitizer.sanitizeForCsv(
                        FeedbackContributionQuestionDetails.convertToEqualShareFormat(
                                studentResult.claimedToInstructor));
            }
        }
        return responseAnswerCsv;
    }
    
    // TODO: check if this can be made non-static
    public static Map<String, StudentResultSummary> getContribQnStudentResultSummary(FeedbackQuestionAttributes question,
            FeedbackSessionResultsBundle feedbackSessionResultsBundle) {
        Map<String, StudentResultSummary> contribQnStats = feedbackSessionResultsBundle.contributionQuestionStudentResultSummary.get(question.getId());
        if(contribQnStats == null){
            FeedbackContributionQuestionDetails fqcd = (FeedbackContributionQuestionDetails) question.getQuestionDetails();
            contribQnStats = fqcd.getStudentResults(feedbackSessionResultsBundle, question);
            
            //Convert email to anonEmail and add stats.
            Map<String, StudentResultSummary> anonContribQnStats = new HashMap<String, StudentResultSummary>();
            for(Map.Entry<String, StudentResultSummary> entry : contribQnStats.entrySet()){
                anonContribQnStats.put(feedbackSessionResultsBundle.getAnonEmailFromStudentEmail(entry.getKey()), entry.getValue());
            }
            for(Map.Entry<String, StudentResultSummary> entry : anonContribQnStats.entrySet()){
                if(contribQnStats.get(entry.getKey()) == null){
                    contribQnStats.put(entry.getKey(), entry.getValue());
                }
            }
            
            feedbackSessionResultsBundle.contributionQuestionStudentResultSummary.put(question.getId(), contribQnStats);
        }
        
        return contribQnStats;
    }
    
    public Map<String, TeamEvalResult> getContribQnTeamEvalResult(FeedbackQuestionAttributes question,
            FeedbackSessionResultsBundle feedbackSessionResultsBundle) {
        Map<String, TeamEvalResult> contribQnStats = feedbackSessionResultsBundle.contributionQuestionTeamEvalResults.get(question.getId());
        if(contribQnStats == null){
            FeedbackContributionQuestionDetails fqcd = (FeedbackContributionQuestionDetails) question.getQuestionDetails();
            contribQnStats = fqcd.getTeamEvalResults(feedbackSessionResultsBundle, question);
            feedbackSessionResultsBundle.contributionQuestionTeamEvalResults.put(question.getId(), contribQnStats);
        }
        
        return contribQnStats;
    }
}
