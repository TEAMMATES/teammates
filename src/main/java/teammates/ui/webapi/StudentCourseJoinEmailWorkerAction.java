package teammates.ui.webapi;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.EmailWrapper;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Student;

/**
 * Task queue worker action: sends registration email for a student of a course.
 */
public class StudentCourseJoinEmailWorkerAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() {
        String courseId = getNonNullRequestParamValue(ParamsNames.COURSE_ID);

        if (!isCourseMigrated(courseId)) {
            CourseAttributes course = logic.getCourse(courseId);
            if (course == null) {
                throw new EntityNotFoundException("Course with ID " + courseId + " does not exist!");
            }

            String studentEmail = getNonNullRequestParamValue(ParamsNames.STUDENT_EMAIL);
            StudentAttributes student = logic.getStudentForEmail(courseId, studentEmail);
            if (student == null) {
                throw new EntityNotFoundException("Student does not exist.");
            }

            boolean isRejoin = getBooleanRequestParamValue(ParamsNames.IS_STUDENT_REJOINING);
            EmailWrapper email = isRejoin
                    ? emailGenerator.generateStudentCourseRejoinEmailAfterGoogleIdReset(course, student)
                    : emailGenerator.generateStudentCourseJoinEmail(course, student);
            emailSender.sendEmail(email);
            return new JsonResult("Successful");
        }

        Course course = sqlLogic.getCourse(courseId);
        if (course == null) {
            throw new EntityNotFoundException("Course with ID " + courseId + " does not exist!");
        }

        String studentEmail = getNonNullRequestParamValue(ParamsNames.STUDENT_EMAIL);
        Student student = sqlLogic.getStudentForEmail(courseId, studentEmail);
        if (student == null) {
            throw new EntityNotFoundException("Student does not exist.");
        }

        boolean isRejoin = getBooleanRequestParamValue(ParamsNames.IS_STUDENT_REJOINING);
        EmailWrapper email = isRejoin
                ? sqlEmailGenerator.generateStudentCourseRejoinEmailAfterGoogleIdReset(course, student)
                : sqlEmailGenerator.generateStudentCourseJoinEmail(course, student);
        emailSender.sendEmail(email);
        return new JsonResult("Successful");
    }

}
