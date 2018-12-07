package teammates.ui.controller;

import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;

/**
 * Action: Restore a deleted course from Recycle Bin for an instructor.
 */
public class InstructorCourseRestoreSoftDeletedCourseAction extends Action {

    @Override
    public ActionResult execute() {

        String idOfCourseToRestore = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertPostParamNotNull(Const.ParamsNames.COURSE_ID, idOfCourseToRestore);

        gateKeeper.verifyAccessible(logic.getInstructorForGoogleId(idOfCourseToRestore, account.googleId),
                logic.getCourse(idOfCourseToRestore),
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE);

        try {
            /* Restore the deleted course and setup status to be shown to user and admin */
            logic.restoreCourseFromRecycleBin(idOfCourseToRestore);
            String statusMessage = String.format(Const.StatusMessages.COURSE_RESTORED, idOfCourseToRestore);
            statusToUser.add(new StatusMessage(statusMessage, StatusMessageColor.SUCCESS));
            statusToAdmin = "Course restored: " + idOfCourseToRestore;
        } catch (Exception e) {
            setStatusForException(e);
        }

        return createRedirectResult(Const.ActionURIs.INSTRUCTOR_COURSES_PAGE);
    }
}
