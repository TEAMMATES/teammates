package teammates.ui.webapi;

import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.ui.output.MessageOutput;

/**
 * Delete a course.
 */
public class DeleteCourseAction extends Action {

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        if (!userInfo.isInstructor) {
            throw new UnauthorizedAccessException("Instructor privilege is required to access this resource.");
        }
        String idOfCourseToDelete = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        gateKeeper.verifyAccessible(logic.getInstructorForGoogleId(idOfCourseToDelete, userInfo.id),
                logic.getCourse(idOfCourseToDelete),
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE);
    }

    @Override
    public ActionResult execute() {
        String idOfCourseToDelete = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        logic.deleteCourseCascade(idOfCourseToDelete);

        return new JsonResult(new MessageOutput("OK"));
    }
}
