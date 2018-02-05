package teammates.ui.automated;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.TeammatesException;
import teammates.common.util.Assumption;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.EmailWrapper;
import teammates.logic.api.EmailGenerator;

/**
 * Task queue worker action: sends registration email for a student of a course.
 */
public class StudentCourseJoinEmailWorkerAction extends AutomatedAction {

    @Override
    protected String getActionDescription() {
        return null;
    }

    @Override
    protected String getActionMessage() {
        return null;
    }

    @Override
    public void execute() {
        String courseId = getRequestParamValue(ParamsNames.COURSE_ID);
        Assumption.assertPostParamNotNull(ParamsNames.COURSE_ID, courseId);
        String studentEmail = getRequestParamValue(ParamsNames.STUDENT_EMAIL);
        Assumption.assertPostParamNotNull(ParamsNames.STUDENT_EMAIL, studentEmail);
        String isRejoinString = getRequestParamValue(ParamsNames.IS_STUDENT_REJOINING);
        Assumption.assertPostParamNotNull(ParamsNames.IS_STUDENT_REJOINING, isRejoinString);
        boolean isRejoin = Boolean.parseBoolean(isRejoinString);

        CourseAttributes course = logic.getCourse(courseId);
        Assumption.assertNotNull(course);
        StudentAttributes student = logic.getStudentForEmail(courseId, studentEmail);
        Assumption.assertNotNull(student);

        EmailWrapper email = isRejoin
                ? new EmailGenerator().generateStudentCourseRejoinEmailAfterGoogleIdReset(course, student)
                : new EmailGenerator().generateStudentCourseJoinEmail(course, student);
        try {
            emailSender.sendEmail(email);
        } catch (Exception e) {
            Assumption.fail("Unexpected error while sending email" + TeammatesException.toStringWithStackTrace(e));
        }
    }

}
