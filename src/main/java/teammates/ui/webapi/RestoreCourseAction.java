package teammates.ui.webapi;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.storage.sqlentity.Course;

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
        if (!userInfo.isInstructor) {
            throw new UnauthorizedAccessException("Instructor privilege is required to access this resource.");
        }
        String idOfCourseToRestore = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        if (!isCourseMigrated(idOfCourseToRestore)) {
            CourseAttributes courseAttributes = logic.getCourse(idOfCourseToRestore);
            gateKeeper.verifyAccessible(logic.getInstructorForGoogleId(idOfCourseToRestore, userInfo.id),
                    courseAttributes,
                    Const.InstructorPermissions.CAN_MODIFY_COURSE);
            return;
        }

        Course course = sqlLogic.getCourse(idOfCourseToRestore);
        gateKeeper.verifyAccessible(sqlLogic.getInstructorByGoogleId(idOfCourseToRestore, userInfo.id),
                course, Const.InstructorPermissions.CAN_MODIFY_COURSE);
    }

    @Override
    public JsonResult execute() {

        String idOfCourseToRestore = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String statusMessage;

        try {
            if (isCourseMigrated(idOfCourseToRestore)) {
                sqlLogic.restoreCourseFromRecycleBin(idOfCourseToRestore);
            } else {
                logic.restoreCourseFromRecycleBin(idOfCourseToRestore);
            }

            statusMessage = "The course " + idOfCourseToRestore + " has been restored.";
        } catch (EntityDoesNotExistException e) {
            throw new EntityNotFoundException(e);
        }

        return new JsonResult(statusMessage);
    }
}
