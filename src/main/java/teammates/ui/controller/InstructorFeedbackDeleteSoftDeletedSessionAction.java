package teammates.ui.controller;

import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;

/**
 * Action: Permanently delete a specific session from Recycle Bin for an instructor.
 */
public class InstructorFeedbackDeleteSoftDeletedSessionAction extends Action {

    @Override
    public ActionResult execute() {

        String idOfCourseToDelete = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertPostParamNotNull(Const.ParamsNames.COURSE_ID, idOfCourseToDelete);

        String nameOfSessionToDelete = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        Assumption.assertPostParamNotNull(Const.ParamsNames.FEEDBACK_SESSION_NAME, nameOfSessionToDelete);

        gateKeeper.verifyAccessible(logic.getInstructorForGoogleId(idOfCourseToDelete, account.googleId),
                logic.getFeedbackSession(nameOfSessionToDelete, idOfCourseToDelete),
                false,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);

        try {
            logic.deleteFeedbackSession(nameOfSessionToDelete, idOfCourseToDelete);
            statusToUser.add(new StatusMessage(Const.StatusMessages.FEEDBACK_SESSION_DELETED, StatusMessageColor.SUCCESS));
            statusToAdmin = "Feedback Session <span class=\"bold\">[" + nameOfSessionToDelete + "]</span> "
                    + "from Course: <span class=\"bold\">[" + idOfCourseToDelete + " permanently deleted.";
        } catch (Exception e) {
            setStatusForException(e);
        }

        return createRedirectResult(Const.ActionURIs.INSTRUCTOR_FEEDBACK_SESSIONS_PAGE);
    }
}
