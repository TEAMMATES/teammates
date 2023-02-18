package teammates.ui.webapi;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.storage.sqlentity.Course;
import teammates.ui.output.CourseData;

/**
 * Move a course to the recycle bin.
 */
class BinCourseAction extends Action {

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

        CourseAttributes courseAttributes = logic.getCourse(idOfCourseToBin);
        if (courseAttributes != null && !courseAttributes.isMigrated()) {
            gateKeeper.verifyAccessible(logic.getInstructorForGoogleId(idOfCourseToBin, userInfo.id),
                courseAttributes, Const.InstructorPermissions.CAN_MODIFY_COURSE);
            return;
        }

        Course course = sqlLogic.getCourse(idOfCourseToBin);
        // TODO: Migrate once instructor entity is ready.
        // gateKeeper.verifyAccessible(logic.getInstructorForGoogleId(idOfCourseToBin, userInfo.id),
        //         course, Const.InstructorPermissions.CAN_MODIFY_COURSE);
    }

    @Override
    public JsonResult execute() {
        String idOfCourseToBin = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        try {
            CourseAttributes courseAttributes = logic.getCourse(idOfCourseToBin);

            if (courseAttributes != null && !courseAttributes.isMigrated()) {
                courseAttributes.setDeletedAt(logic.moveCourseToRecycleBin(idOfCourseToBin));
                return new JsonResult(new CourseData(courseAttributes));
            }

            Course course = sqlLogic.getCourse(idOfCourseToBin);
            course.setDeletedAt(sqlLogic.moveCourseToRecycleBin(idOfCourseToBin));
            return new JsonResult(new CourseData(courseAttributes));
        } catch (EntityDoesNotExistException e) {
            throw new EntityNotFoundException(e);
        }
    }

}
