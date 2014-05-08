package teammates.ui.controller;

import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class InstructorFeedbackUnpublishAction extends InstructorFeedbacksPageAction {
    
    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        FeedbackSessionAttributes session = logic.getFeedbackSession(feedbackSessionName, courseId);
        boolean isCreatorOnly = true;
        
        new GateKeeper().verifyAccessible(
                instructor,
                session,
                isCreatorOnly);
        
        if(session.isPublished()){
            try {
                logic.unpublishFeedbackSession(feedbackSessionName, courseId);
            } catch (InvalidParametersException e) {
                Assumption.fail("InvalidParametersException not expected at this point");
            }

            statusToUser.add(Const.StatusMessages.FEEDBACK_SESSION_UNPUBLISHED);
            statusToAdmin = "Evaluation <span class=\"bold\">("
                    + feedbackSessionName + ")</span> " +
                    "for Course <span class=\"bold\">[" + courseId
                    + "]</span> unpublished.";
        } else {
            isError = true;
            statusToUser.add(Const.StatusMessages.FEEDBACK_SESSION_UNPUBLISH_FAILED);
            statusToAdmin = "Evaluation <span class=\"bold\">("
                    + feedbackSessionName + ")</span> " +
                    "for Course <span class=\"bold\">[" + courseId
                    + "]</span> unpublished for more than one time.";
        }
        
        return createRedirectResult(Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE);
    }
    
}
