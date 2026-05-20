package teammates.ui.webapi;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.common.util.EmailWrapper;
import teammates.storage.entity.Course;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.storage.entity.User;
import teammates.ui.exception.EntityNotFoundException;

/**
 * Action: resets an account ID.
 */
public class ResetAccountAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() {
        UUID userId = getUuidRequestParamValue(Const.ParamsNames.USER_ID);
        User existingUser = logic.getUser(userId);

        if (existingUser == null) {
            throw new EntityNotFoundException("User does not exist.");
        }

        Course course = existingUser.getCourse();

        if (existingUser instanceof Student existingStudent) {
            try {
                if (existingStudent.getGoogleId() != null) {
                    logic.resetStudentGoogleId(existingStudent.getEmail(), existingStudent.getCourseId(),
                            existingStudent.getGoogleId());
                }
                // Generate and queue rejoin email to priority queue
                EmailWrapper email = emailGenerator
                        .generateStudentCourseRejoinEmailAfterGoogleIdReset(course, existingStudent);
                List<EmailWrapper> emails = new ArrayList<>();
                emails.add(email);
                taskQueuer.scheduleEmailsForPrioritySending(emails);
            } catch (EntityDoesNotExistException e) {
                throw new EntityNotFoundException(e);
            }
        } else if (existingUser instanceof Instructor existingInstructor) {
            try {
                if (existingInstructor.getGoogleId() != null) {
                    logic.resetInstructorGoogleId(existingInstructor.getEmail(), existingInstructor.getCourseId(),
                            existingInstructor.getGoogleId());
                }
                // Generate and queue rejoin email to priority queue
                EmailWrapper email = emailGenerator
                        .generateInstructorCourseRejoinEmailAfterGoogleIdReset(existingInstructor, course);
                List<EmailWrapper> emails = new ArrayList<>();
                emails.add(email);
                taskQueuer.scheduleEmailsForPrioritySending(emails);
            } catch (EntityDoesNotExistException e) {
                throw new EntityNotFoundException(e);
            }
        }

        return new JsonResult("Account is successfully reset.");
    }

}
