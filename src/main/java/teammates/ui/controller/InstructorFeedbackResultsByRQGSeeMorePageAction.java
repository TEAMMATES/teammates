package teammates.ui.controller;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.FeedbackAbstractQuestionDetails;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Sanitizer;
import teammates.logic.api.GateKeeper;

public class InstructorFeedbackResultsByRQGSeeMorePageAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertNotNull("null course id", courseId);
        String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        Assumption.assertNotNull("null feedback session name", feedbackSessionName);
        
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        FeedbackSessionAttributes session = logic.getFeedbackSession(feedbackSessionName, courseId);
        boolean isCreatorOnly = true;
        
        new GateKeeper().verifyAccessible(
                instructor, 
                session,
                !isCreatorOnly);
        
        InstructorFeedbackResultsByRQGSeeMorePageData data = 
                new InstructorFeedbackResultsByRQGSeeMorePageData(account);
        
        String section = getRequestParamValue(Const.ParamsNames.SECTION_NAME);
        Assumption.assertNotNull("null question number", section);
        String groupByTeam = getRequestParamValue(Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYTEAM);
        boolean groupByTeamEnabled = (groupByTeam != null);

        FeedbackSessionResultsBundle bundle = logic.getFeedbackSessionResultsForInstructorToSection(feedbackSessionName, courseId, instructor.email, section);
        Iterator<FeedbackResponseAttributes> iterResponse = bundle.responses.iterator();
        while (iterResponse.hasNext()) {
            FeedbackResponseAttributes response = iterResponse.next();
            FeedbackQuestionAttributes question = bundle.questions.get(response.feedbackQuestionId);
            data.answer.put(response.getId(), response.getResponseDetails().getAnswerHtml(question.getQuestionDetails()));
        }
        data.responses = bundle.getResponsesSortedByRecipientQuestionGiver(groupByTeamEnabled);
        
        for (Map.Entry<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> responseEntries : data.responses.entrySet()) {
            String participant = responseEntries.getKey();
            Map<String, String> questionStatsTable = new LinkedHashMap<String, String>();
            for(Map.Entry<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> currentResponseEntry : responseEntries.getValue().entrySet()){
                FeedbackQuestionAttributes question = currentResponseEntry.getKey();
                FeedbackAbstractQuestionDetails questionDetails = question.getQuestionDetails();
                String statsHtml = questionDetails.getQuestionResultStatisticsHtml(currentResponseEntry.getValue(), question, account, bundle, "recipient-question-giver");
                questionStatsTable.put(question.getId(), statsHtml);
            }
            data.participantStats.put(participant, questionStatsTable);
        }
        
        if(groupByTeamEnabled){
            Map<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> teamResponses = bundle.getQuestionResponseMapByGiverTeam();
            for (Map.Entry<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> teamResponseEntries : teamResponses.entrySet()) {
                String team = teamResponseEntries.getKey();
                Map<String, String> questionStatsTable = (data.participantStats.get(team) != null) ? data.participantStats.get(team) : new  LinkedHashMap<String, String>();
                for(Map.Entry<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> currentTeamResponseEntry : teamResponseEntries.getValue().entrySet()){
                    FeedbackQuestionAttributes question = currentTeamResponseEntry.getKey();
                    FeedbackAbstractQuestionDetails questionDetails = question.getQuestionDetails();
                    String statsHtml = questionDetails.getQuestionResultStatisticsHtml(currentTeamResponseEntry.getValue(), question, account, bundle, "recipient-question-giver");
                    if(!statsHtml.equals("")){
                        questionStatsTable.put(question.getId(), statsHtml);
                    }
                }
                data.participantStats.put(team, questionStatsTable);
            }
        }
        
        for(FeedbackQuestionAttributes question : bundle.questions.values()){
            String questionId = question.getId();
            FeedbackAbstractQuestionDetails questionDetails = question.getQuestionDetails();
            Map<String, String> params = new HashMap<String, String>();
            params.put("questionNum", String.valueOf(question.questionNumber));
            params.put("questionText", Sanitizer.sanitizeForHtml(questionDetails.questionText));
            params.put("questionAdditionalInfo", questionDetails.getQuestionAdditionalInfoHtml(question.questionNumber, "additionalInfoId"));
            params.put("questionRecipientType", question.recipientType.toDisplayRecipientName());
            data.questionsInfo.put(questionId, params);
        }
        
        for(String emailKey : bundle.emailNameTable.keySet()){
            String name = bundle.getNameForEmail(emailKey);
            if(!name.equals(bundle.emailNameTable.get(emailKey))){
                bundle.emailNameTable.put(emailKey, name);
            }
        }
        data.emailNameTable = bundle.emailNameTable;
        
            
        for(String emailKey : bundle.emailTeamNameTable.keySet()){
            String teamName = bundle.getTeamNameForEmail(emailKey).equals("")? bundle.getNameForEmail(emailKey): bundle.getTeamNameForEmail(emailKey);
            if(!teamName.equals(bundle.emailTeamNameTable.get(emailKey))){
                bundle.emailTeamNameTable.put(emailKey, teamName);
            }
        }

        data.emailTeamNameTable = bundle.emailTeamNameTable;
        
        log.info("TEST 4");
        
        return createAjaxResult(Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_QUESTION, data);
    }

}
