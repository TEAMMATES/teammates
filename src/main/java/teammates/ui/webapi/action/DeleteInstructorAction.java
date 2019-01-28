package teammates.ui.webapi.action;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;

/**
 * Action: deletes an instructor from a course.
 */
public class DeleteInstructorAction extends Action {

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        if (userInfo.isAdmin) {
            return;
        }
        // TODO allow access to instructors with modify permission
        throw new UnauthorizedAccessException("Admin privilege is required to access this resource.");
    }

    @Override
    public ActionResult execute() {
        String instructorId = getNonNullRequestParamValue(Const.ParamsNames.INSTRUCTOR_ID);
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, instructorId);
        logic.deleteInstructor(courseId, instructor.email);

        return new JsonResult("Instructor is successfully deleted.", HttpStatus.SC_OK);
    }

}
