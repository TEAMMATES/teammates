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
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        Instructor instructor = getInstructorFromRequest(courseId);
        gateKeeper.verifyInstructorInCourse(instructor, logic.getCourse(courseId));
        gateKeeper.verifyAccessible(instructor, Const.InstructorPermissions.CAN_MODIFY_STUDENT);
    }

    @Override
    public JsonResult execute() {
        var courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        logic.deleteStudentsInCourse(courseId);

        return new JsonResult("Successful");
    }
}
