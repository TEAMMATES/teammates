package teammates.ui.controller;

import java.util.HashMap;
import java.util.Iterator;
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

public class InstructorFeedbackResultsByGRQSeeMorePageAction extends Action {

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
        
        InstructorFeedbackResultsByGRQSeeMorePageData data = 
                new InstructorFeedbackResultsByGRQSeeMorePageData(account);
        
        String section = getRequestParamValue(Const.ParamsNames.SECTION_NAME);
        Assumption.assertNotNull("null question number", section);
        String groupByTeam = getRequestParamValue(Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYTEAM);
        boolean groupByTeamEnabled = (groupByTeam != null);

        FeedbackSessionResultsBundle bundle = logic.getFeedbackSessionResultsForInstructorFromSection(feedbackSessionName, courseId, instructor.email, section);
        Iterator<FeedbackResponseAttributes> iterResponse = bundle.responses.iterator();
        while (iterResponse.hasNext()) {
            FeedbackResponseAttributes response = iterResponse.next();
            FeedbackQuestionAttributes question = bundle.questions.get(response.feedbackQuestionId);
            data.answer.put(response.getId(), response.getResponseDetails().getAnswerHtml(question.getQuestionDetails()));
            if ((!instructor.isAllowedForPrivilege(response.giverSection,
                    response.feedbackSessionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS))
                    || !(instructor.isAllowedForPrivilege(response.recipientSection,
                            response.feedbackSessionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS))) {
                bundle.responseComments.remove(response.getId());
                iterResponse.remove();
            }
        }
        data.responses = bundle.getResponsesSortedByGiver(groupByTeamEnabled);
        data.comments = bundle.responseComments;
        
        for(FeedbackQuestionAttributes question : bundle.questions.values()){
            String questionId = question.getId();
            FeedbackAbstractQuestionDetails questionDetails = question.getQuestionDetails();
            Map<String, String> params = new HashMap<String, String>();
            params.put("questionNum", String.valueOf(question.questionNumber));
            params.put("questionText", Sanitizer.sanitizeForHtml(questionDetails.questionText));
            params.put("questionAdditionalInfo", questionDetails.getQuestionAdditionalInfoHtml(question.questionNumber, "additionalInfoId"));
            data.questionsInfo.put(questionId, params);
        }
        
        List<String> sections = logic.getSectionNamesForCourse(courseId);
        for(String s : sections){
            Map<String, String> params = new HashMap<String, String>();
            params.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS,
                    String.valueOf(instructor.isAllowedForPrivilege(s,feedbackSessionName,
                            Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS)));
            params.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS,
                    String.valueOf(instructor.isAllowedForPrivilege(s,feedbackSessionName,
                            Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS)));
            data.privilegesInfo.put(s, params);
        }
        
        for(String emailKey : bundle.emailTeamNameTable.keySet()){
            String teamName = bundle.getTeamNameForEmail(emailKey);
            if(!teamName.equals(bundle.emailTeamNameTable.get(emailKey))){
                bundle.emailTeamNameTable.put(emailKey, teamName);
            }
        }

        data.emailTeamNameTable = bundle.emailTeamNameTable;
        
        return createAjaxResult(Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_QUESTION, data);
    }

}
