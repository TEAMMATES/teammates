package teammates.ui.webapi;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.storage.entity.Course;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.UnauthorizedAccessException;

/**
 * Action: Restores a course from Recycle Bin.
 */
public class RestoreCourseAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        if (!authContext.isInstructor()) {
            throw new UnauthorizedAccessException("Instructor privilege is required to access this resource.");
        }
        String idOfCourseToRestore = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        Course course = logic.getCourse(idOfCourseToRestore);

        gateKeeper.verifyAccessible(logic.getInstructorByGoogleId(idOfCourseToRestore, authContext.id()),
                course, Const.InstructorPermissions.CAN_MODIFY_COURSE);
    }

    @Override
    public JsonResult execute() {

        String idOfCourseToRestore = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String statusMessage;

        try {
            logic.restoreCourseFromRecycleBin(idOfCourseToRestore);
            statusMessage = "The course " + idOfCourseToRestore + " has been restored.";
        } catch (EntityDoesNotExistException e) {
            throw new EntityNotFoundException(e);
        }

        return new JsonResult(statusMessage);
    }
}
