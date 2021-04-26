package teammates.ui.webapi;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.EmailWrapper;

/**
 * Task queue worker action: sends registration email for a student of a course.
 */
class StudentCourseJoinEmailWorkerAction extends AdminOnlyAction {

    @Override
    JsonResult execute() {
        String courseId = getNonNullRequestParamValue(ParamsNames.COURSE_ID);
        CourseAttributes course = logic.getCourse(courseId);
        if (course == null) {
            throw new EntityNotFoundException(
                    new EntityDoesNotExistException("Course with ID " + courseId + " does not exist!"));
        }

        String studentEmail = getNonNullRequestParamValue(ParamsNames.STUDENT_EMAIL);
        StudentAttributes student = logic.getStudentForEmail(courseId, studentEmail);
        if (student == null) {
            throw new EntityNotFoundException(new EntityDoesNotExistException("Student does not exist."));
        }

        boolean isRejoin = getBooleanRequestParamValue(ParamsNames.IS_STUDENT_REJOINING);
        EmailWrapper email = isRejoin
                ? emailGenerator.generateStudentCourseRejoinEmailAfterGoogleIdReset(course, student)
                : emailGenerator.generateStudentCourseJoinEmail(course, student);
        emailSender.sendEmail(email);
        return new JsonResult("Successful");
    }

}
