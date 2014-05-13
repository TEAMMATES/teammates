package teammates.ui.controller;

import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class InstructorFeedbackResultsPageAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        
        statusToAdmin = "Show instructor feedback result page<br>" +
                "Session Name: " + feedbackSessionName + "<br>" + 
                "Course ID: " + courseId;
        if(courseId == null || feedbackSessionName == null) {
            return createRedirectResult(Const.ActionURIs.INSTRUCTOR_HOME_PAGE);
        }
        
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        FeedbackSessionAttributes session = logic.getFeedbackSession(feedbackSessionName, courseId);
        boolean isCreatorOnly = true;
        new GateKeeper().verifyAccessible(
                instructor, 
                session,
                !isCreatorOnly);
        
        InstructorFeedbackResultsPageData data = new InstructorFeedbackResultsPageData(account);
        data.instructor = instructor;
        data.bundle = logic.getFeedbackSessionResultsForInstructor(feedbackSessionName, courseId, data.instructor.email);
        if(data.bundle == null) {
            throw new EntityDoesNotExistException(
                    "Feedback session " + feedbackSessionName + " does not exist in " + courseId + ".");
        }
        
        data.sortType = getRequestParamValue(Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE);
        if (data.sortType != null && data.sortType.equals("table")){
            return createShowPageResult(Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_TABLE, data);
        } else if (data.sortType != null && data.sortType.equals("giver")) {
            return createShowPageResult(Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_GIVER, data);
        } else {
            // data.sortType == null OR data.sortType.equals("recipient"): sort by recipients
            return createShowPageResult(Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_RECIPIENT, data);
        }
    }

}
