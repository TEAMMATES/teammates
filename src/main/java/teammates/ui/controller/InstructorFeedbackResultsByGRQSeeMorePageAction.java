package teammates.ui.controller;

import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
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
        data.responses = bundle.getResponsesSortedByGiver(groupByTeamEnabled);
        data.comments = bundle.responseComments;
        
        return createAjaxResult(Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_QUESTION, data);
    }

}
