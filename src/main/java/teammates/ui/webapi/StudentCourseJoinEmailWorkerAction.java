package teammates.ui.webapi;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Assumption;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.EmailWrapper;

/**
 * Task queue worker action: sends registration email for a student of a course.
 */
class StudentCourseJoinEmailWorkerAction extends AdminOnlyAction {

    @Override
    JsonResult execute() {
        String courseId = getNonNullRequestParamValue(ParamsNames.COURSE_ID);
        String studentEmail = getNonNullRequestParamValue(ParamsNames.STUDENT_EMAIL);
        boolean isRejoin = getBooleanRequestParamValue(ParamsNames.IS_STUDENT_REJOINING);

        CourseAttributes course = logic.getCourse(courseId);
        Assumption.assertNotNull(course);

        StudentAttributes student = logic.getStudentForEmail(courseId, studentEmail);
        Assumption.assertNotNull(student);

        EmailWrapper email = isRejoin
                ? emailGenerator.generateStudentCourseRejoinEmailAfterGoogleIdReset(course, student)
                : emailGenerator.generateStudentCourseJoinEmail(course, student);
        emailSender.sendEmail(email);
        return new JsonResult("Successful");
    }

}
