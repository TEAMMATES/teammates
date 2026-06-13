package teammates.ui.webapi;

import teammates.common.util.Const;
import teammates.ui.exception.UnauthorizedAccessException;

/**
 * Action: deletes all students in a course.
 */
public class DeleteStudentsAction extends LoggedInAction {
    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        gateKeeper.verifyInstructorHasPrivilege(requestContext, courseId, Const.InstructorPermissions.CAN_MODIFY_STUDENT);
    }

    @Override
    public JsonResult execute() {
        var courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        logic.deleteStudentsInCourse(courseId);

        return new JsonResult("Successful");
    }
}
