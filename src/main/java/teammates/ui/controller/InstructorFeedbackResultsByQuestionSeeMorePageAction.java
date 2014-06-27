package teammates.ui.controller;

import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class InstructorFeedbackResultsByQuestionSeeMorePageAction extends Action {

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
        
        InstructorFeedbackResultsByQuestionSeeMorePageData data = 
                new InstructorFeedbackResultsByQuestionSeeMorePageData(account);
        
        String questionNumStr = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_NUMBER);
        Assumption.assertNotNull("null question number", questionNumStr);
        
        int questionNum = Integer.parseInt(questionNumStr);
        FeedbackSessionResultsBundle bundle = logic.getFeedbackSessionResultsForInstructorFromQuestion(feedbackSessionName, courseId, instructor.email, questionNum);
        data.responses = bundle.responses;
        for(FeedbackQuestionAttributes question : bundle.questions.values()){
            data.questionStats = question.getQuestionDetails().getQuestionResultStatisticsHtml(bundle.responses, question, bundle, "question");
            for(FeedbackResponseAttributes response : data.responses){
                data.answerTable.put(response.getId(), response.getResponseDetails().getAnswerHtml(question.getQuestionDetails()));
            }
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

        data.emailTeamTable = bundle.emailTeamNameTable;

        return createAjaxResult(Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_QUESTION, data);
    }

}
