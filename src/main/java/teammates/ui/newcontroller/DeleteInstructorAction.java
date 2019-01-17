package teammates.ui.newcontroller;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.storage.entity.Instructor;

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

        if (!userInfo.isInstructor) {
            throw new UnauthorizedAccessException("Instructor privilege is required to access this resource.");
        }

        String instructorId = getNonNullRequestParamValue(Const.ParamsNames.INSTRUCTOR_ID);
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, instructorId);
        gateKeeper.verifyAccessible(
                instructor, logic.getCourse(courseId), Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR);
    }

    @Override
    public ActionResult execute() {
        String instructorId = getNonNullRequestParamValue(Const.ParamsNames.INSTRUCTOR_ID);
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String instructorToDeleteEmail = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_EMAIL);

        if (instructorToDeleteEmail == null) {
            InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, instructorId);
            logic.deleteInstructor(courseId, instructor.email);
        } else {
            logic.deleteInstructor(courseId, instructorToDeleteEmail);
        }

        return new JsonResult("Instructor is successfully deleted.", HttpStatus.SC_OK);
    }

}
