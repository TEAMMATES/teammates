package teammates.ui.controller;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.StatusMessage;
import teammates.common.util.Const.StatusMessageColor;
import teammates.logic.api.GateKeeper;

public class InstructorFeedbackDeleteAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        
        Assumption.assertNotNull(courseId);
        Assumption.assertNotNull(feedbackSessionName);
        
        new GateKeeper().verifyAccessible(
                logic.getInstructorForGoogleId(courseId, account.googleId),
                logic.getFeedbackSession(feedbackSessionName, courseId),
                false,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);
        
        logic.deleteFeedbackSession(feedbackSessionName, courseId);
        statusToUser.add(new StatusMessage(Const.StatusMessages.FEEDBACK_SESSION_DELETED, StatusMessageColor.SUCCESS));
        statusToAdmin = "Feedback Session <span class=\"bold\">[" + feedbackSessionName + "]</span> "
                        + "from Course: <span class=\"bold\">[" + courseId + " deleted.";
        
        return createRedirectResult(Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE);
    }

}
