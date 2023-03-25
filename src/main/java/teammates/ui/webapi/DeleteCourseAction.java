package teammates.ui.webapi;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.util.Const;
import teammates.storage.sqlentity.Course;
import teammates.ui.output.MessageOutput;

/**
 * Delete a course.
 */
public class DeleteCourseAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        if (!userInfo.isInstructor) {
            throw new UnauthorizedAccessException("Instructor privilege is required to access this resource.");
        }
        String idOfCourseToDelete = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        if (!isCourseMigrated(idOfCourseToDelete)) {
            CourseAttributes courseAttributes = logic.getCourse(idOfCourseToDelete);
            gateKeeper.verifyAccessible(logic.getInstructorForGoogleId(idOfCourseToDelete, userInfo.id),
                    courseAttributes,
                    Const.InstructorPermissions.CAN_MODIFY_COURSE);
            return;
        }

        Course course = sqlLogic.getCourse(idOfCourseToDelete);
        gateKeeper.verifyAccessible(sqlLogic.getInstructorByGoogleId(idOfCourseToDelete, userInfo.id),
                course, Const.InstructorPermissions.CAN_MODIFY_COURSE);
    }

    @Override
    public JsonResult execute() {
        String idOfCourseToDelete = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        if (!isCourseMigrated(idOfCourseToDelete)) {
            logic.deleteCourseCascade(idOfCourseToDelete);
            return new JsonResult(new MessageOutput("OK"));
        }

        sqlLogic.deleteCourseCascade(idOfCourseToDelete);
        return new JsonResult(new MessageOutput("OK"));
    }
}
