package teammates.ui.webapi;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.storage.sqlentity.Course;
import teammates.ui.output.CourseData;

/**
 * Move a course to the recycle bin.
 */
public class BinCourseAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        if (!userInfo.isInstructor) {
            throw new UnauthorizedAccessException("Instructor privilege is required to access this resource.");
        }

        String idOfCourseToBin = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        Course course = sqlLogic.getCourse(idOfCourseToBin);
        gateKeeper.verifyAccessible(sqlLogic.getInstructorByGoogleId(idOfCourseToBin, userInfo.id),
                course, Const.InstructorPermissions.CAN_MODIFY_COURSE);
    }

    @Override
    public JsonResult execute() {
        String idOfCourseToBin = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        try {
            Course binnedCourse = sqlLogic.moveCourseToRecycleBin(idOfCourseToBin);
            return new JsonResult(new CourseData(binnedCourse));
        } catch (EntityDoesNotExistException e) {
            throw new EntityNotFoundException(e);
        }
    }

}
