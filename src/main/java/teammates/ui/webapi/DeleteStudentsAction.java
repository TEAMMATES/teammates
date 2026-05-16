package teammates.ui.webapi;

import teammates.common.util.Const;
import teammates.storage.entity.Instructor;
import teammates.ui.exception.UnauthorizedAccessException;

/**
 * Action: deletes all students in a course.
 */
public class DeleteStudentsAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        if (!authContext.isInstructor()) {
            throw new UnauthorizedAccessException("Instructor privilege is required to delete students from course.");
        }

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        Instructor instructor = logic.getInstructorByGoogleId(courseId, authContext.id());
        gateKeeper.verifyAccessible(
                    instructor, logic.getCourse(courseId), Const.InstructorPermissions.CAN_MODIFY_STUDENT);
    }

    @Override
    public JsonResult execute() {
        var courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        logic.deleteStudentsInCourseCascade(courseId);

        return new JsonResult("Successful");
    }
}
