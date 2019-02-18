package teammates.ui.webapi.action;

import org.apache.http.HttpStatus;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;

/**
 * Action: Restores a soft-deleted course from Recycle Bin.
 */
public class RestoreInstructorSoftDeletedCourseAction extends Action {

    private String idOfCourseToRestore;

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        if (!userInfo.isInstructor) {
            throw new UnauthorizedAccessException("Instructor privilege is required to access this resource.");
        }
        idOfCourseToRestore = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        gateKeeper.verifyAccessible(logic.getInstructorForGoogleId(idOfCourseToRestore, userInfo.id),
                logic.getCourse(idOfCourseToRestore),
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE);
    }

    @Override
    public ActionResult execute() {

        idOfCourseToRestore = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String statusMessage;

        try {
            logic.restoreCourseFromRecycleBin(idOfCourseToRestore);

            statusMessage = "The course " + idOfCourseToRestore + " has been restored.";
        } catch (EntityDoesNotExistException e) {
            return new JsonResult(e.getMessage(), HttpStatus.SC_BAD_REQUEST);
        }

        return new JsonResult(statusMessage);
    }
}
