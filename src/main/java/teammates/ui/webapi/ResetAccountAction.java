package teammates.ui.webapi;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.util.Const;

/**
 * Action: resets an account ID.
 */
class ResetAccountAction extends AdminOnlyAction {

    @Override
    JsonResult execute() {
        String studentEmail = getRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);
        String instructorEmail = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_EMAIL);

        if (studentEmail == null && instructorEmail == null) {
            return new JsonResult("Either student email or instructor email has to be specified.",
                    HttpStatus.SC_BAD_REQUEST);
        }

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String wrongGoogleId = null;
        if (studentEmail != null) {
            StudentAttributes existingStudent = logic.getStudentForEmail(courseId, studentEmail);
            if (existingStudent == null) {
                return new JsonResult("Student does not exist.",
                        HttpStatus.SC_NOT_FOUND);
            }
            wrongGoogleId = existingStudent.googleId;

            try {
                logic.resetStudentGoogleId(studentEmail, courseId);
                taskQueuer.scheduleCourseRegistrationInviteToStudent(courseId, studentEmail, true);
            } catch (EntityDoesNotExistException e) {
                throw new EntityNotFoundException(e);
            }
        } else if (instructorEmail != null) {
            InstructorAttributes existingInstructor = logic.getInstructorForEmail(courseId, instructorEmail);
            if (existingInstructor == null) {
                return new JsonResult("Instructor does not exist.",
                        HttpStatus.SC_NOT_FOUND);
            }
            wrongGoogleId = existingInstructor.googleId;
            AccountAttributes account = logic.getAccount(wrongGoogleId);
            String institute = account.institute;

            try {
                logic.resetInstructorGoogleId(instructorEmail, courseId);
                taskQueuer.scheduleCourseRegistrationInviteToInstructor(null, instructorEmail, courseId, institute, true);
            } catch (EntityDoesNotExistException e) {
                throw new EntityNotFoundException(e);
            }
        }

        if (wrongGoogleId != null
                && logic.getStudentsForGoogleId(wrongGoogleId).isEmpty()
                && logic.getInstructorsForGoogleId(wrongGoogleId).isEmpty()) {
            if (fileStorage.doesFileExist(wrongGoogleId)) {
                fileStorage.delete(wrongGoogleId);
            }
            logic.deleteAccountCascade(wrongGoogleId);
        }

        return new JsonResult("Account is successfully reset.", HttpStatus.SC_OK);
    }

}
