package teammates.ui.controller;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class InstructorStudentCommentClearPendingAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertNotNull(courseId);
        
        new GateKeeper().verifyAccessible(
                logic.getInstructorForGoogleId(courseId, account.googleId),
                logic.getCourse(courseId));
        
        logic.sendEmailForPendingComments(courseId);
        
        logic.clearPendingComments(courseId);
        logic.clearPendingFeedbackResponseComments(courseId);
        
        statusToUser.add(Const.StatusMessages.COMMENT_CLEARED);
        statusToAdmin = account.googleId + " cleared pending comments for course " + courseId;
        
        return createRedirectResult((new PageData(account).getInstructorCommentsLink()) + "&" + Const.ParamsNames.COURSE_ID + "=" + courseId);
    }
}
