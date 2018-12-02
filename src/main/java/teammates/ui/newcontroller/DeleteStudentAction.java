package teammates.ui.newcontroller;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;

/**
 * Action: deletes a student from a course.
 */
public class DeleteStudentAction extends Action {

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
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String studentId = getNonNullRequestParamValue(Const.ParamsNames.STUDENT_ID);

        StudentAttributes student = logic.getStudentForGoogleId(courseId, studentId);
        logic.deleteStudent(courseId, student.email);

        return new JsonResult("Student is successfully deleted.", HttpStatus.SC_OK);
    }

}
