package teammates.ui.webapi;

import teammates.common.util.Const;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.MessageOutput;

/**
 * Delete a course.
 */
public class DeleteCourseAction extends LoggedInAction {
    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        String idOfCourseToDelete = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        gateKeeper.verifyInstructorHasPrivilege(requestContext, idOfCourseToDelete,
                Const.InstructorPermissions.CAN_MODIFY_COURSE);
    }

    @Override
    public JsonResult execute() {
        String idOfCourseToDelete = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        logic.deleteCourse(idOfCourseToDelete);

        return new JsonResult(new MessageOutput("OK"));
    }
}
