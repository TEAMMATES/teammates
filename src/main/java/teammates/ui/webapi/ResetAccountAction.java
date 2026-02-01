package teammates.ui.webapi;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;

/**
 * Action: resets an account ID.
 */
public class ResetAccountAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() {
        String studentEmail = getRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);
        String instructorEmail = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_EMAIL);

        if (studentEmail == null && instructorEmail == null) {
            throw new InvalidHttpParameterException("Either student email or instructor email has to be specified.");
        }

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String wrongGoogleId = null;

        if (studentEmail != null) {
            Student existingStudent = sqlLogic.getStudentForEmail(courseId, studentEmail);

            if (existingStudent == null) {
                throw new EntityNotFoundException("Student does not exist.");
            }

            wrongGoogleId = existingStudent.getGoogleId();

            try {
                sqlLogic.resetStudentGoogleId(studentEmail, courseId, wrongGoogleId);
                taskQueuer.scheduleCourseRegistrationInviteToStudent(courseId, studentEmail, true);
            } catch (EntityDoesNotExistException e) {
                throw new EntityNotFoundException(e);
            }
        } else if (instructorEmail != null) {
            Instructor existingInstructor = sqlLogic.getInstructorForEmail(courseId, instructorEmail);

            if (existingInstructor == null) {
                throw new EntityNotFoundException("Instructor does not exist.");
            }

            wrongGoogleId = existingInstructor.getGoogleId();

            try {
                sqlLogic.resetInstructorGoogleId(instructorEmail, courseId, wrongGoogleId);
                taskQueuer.scheduleCourseRegistrationInviteToInstructor(null, instructorEmail, courseId, true);
            } catch (EntityDoesNotExistException e) {
                throw new EntityNotFoundException(e);
            }
        }

        if (wrongGoogleId != null
                && sqlLogic.getStudentsByGoogleId(wrongGoogleId).isEmpty()
                && sqlLogic.getInstructorsForGoogleId(wrongGoogleId).isEmpty()) {
            sqlLogic.deleteAccountCascade(wrongGoogleId);
        }

        return new JsonResult("Account is successfully reset.");
    }

}
