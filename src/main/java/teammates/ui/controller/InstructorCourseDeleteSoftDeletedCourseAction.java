package teammates.ui.controller;

import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;

/**
 * Action: Permanently delete a course from Recycle Bin for an instructor.
 */
public class InstructorCourseDeleteSoftDeletedCourseAction extends Action {

    @Override
    public ActionResult execute() {

        String idOfCourseToDelete = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertPostParamNotNull(Const.ParamsNames.COURSE_ID, idOfCourseToDelete);

        gateKeeper.verifyAccessible(logic.getInstructorForGoogleId(idOfCourseToDelete, account.googleId),
                logic.getCourse(idOfCourseToDelete),
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE);

        try {
            /* Permanently delete the course and setup status to be shown to user and admin */
            logic.deleteCourse(idOfCourseToDelete);
            String statusMessage = String.format(Const.StatusMessages.COURSE_DELETED, idOfCourseToDelete);
            statusToUser.add(new StatusMessage(statusMessage, StatusMessageColor.SUCCESS));
            statusToAdmin = "Course deleted: " + idOfCourseToDelete;
        } catch (Exception e) {
            setStatusForException(e);
        }

        return createRedirectResult(Const.ActionURIs.INSTRUCTOR_COURSES_PAGE);
    }
}
