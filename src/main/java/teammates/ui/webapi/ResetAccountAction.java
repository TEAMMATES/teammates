package teammates.ui.webapi;

import java.util.ArrayList;
import java.util.List;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.common.util.EmailWrapper;
import teammates.storage.sqlentity.Course;
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
        Course course = logic.getCourse(courseId);
        if (course == null) {
            throw new EntityNotFoundException("Course does not exist");
        }

        if (studentEmail != null) {
            Student existingStudent = logic.getStudentForEmail(courseId, studentEmail);

            if (existingStudent == null) {
                throw new EntityNotFoundException("Student does not exist.");
            }

            try {
                if (existingStudent.getGoogleId() != null) {
                    logic.resetStudentGoogleId(studentEmail, courseId, existingStudent.getGoogleId());
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
        } else if (instructorEmail != null) {
            Instructor existingInstructor = logic.getInstructorForEmail(courseId, instructorEmail);

            if (existingInstructor == null) {
                throw new EntityNotFoundException("Instructor does not exist.");
            }

            try {
                if (existingInstructor.getGoogleId() != null) {
                    logic.resetInstructorGoogleId(instructorEmail, courseId, existingInstructor.getGoogleId());
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
