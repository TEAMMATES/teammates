package teammates.ui.webapi.action;

import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;

/**
 * Action: Permanently deletes a course from Recycle Bin for an instructor.
 */
public class DeleteInstructorSoftDeletedCourseAction extends Action {

    private String idOfCourseToDelete;

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        if (!userInfo.isInstructor) {
            throw new UnauthorizedAccessException("Instructor privilege is required to access this resource.");
        }
        idOfCourseToDelete = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        gateKeeper.verifyAccessible(logic.getInstructorForGoogleId(idOfCourseToDelete, userInfo.id),
                logic.getCourse(idOfCourseToDelete),
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE);
    }

    @Override
    public ActionResult execute() {

        idOfCourseToDelete = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        logic.deleteCourse(idOfCourseToDelete);

        String statusMessage = "The course " + idOfCourseToDelete + " has been permanently deleted.";
        return new JsonResult(statusMessage);
    }
}
