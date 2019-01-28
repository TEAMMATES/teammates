package teammates.ui.webapi.action;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.ui.webapi.output.ApiOutput;

/**
 * Action: fetches student edit details.
 */
public class GetStudentEditDetailsAction extends Action {
    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        if (!userInfo.isInstructor) {
            throw new UnauthorizedAccessException("Instructor privilege is required to access this resource.");
        }
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String studentEmail = getNonNullRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);

        StudentAttributes student = logic.getStudentForEmail(courseId, studentEmail);
        if (student == null) {
            throw new EntityNotFoundException(
                    new EntityDoesNotExistException("No student with given email in given course."));
        }
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.id);
        gateKeeper.verifyAccessible(instructor, logic.getCourse(courseId), student.section,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS);
    }

    @Override
    public ActionResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String studentEmail = getNonNullRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);

        StudentAttributes student = logic.getStudentForEmail(courseId, studentEmail);
        if (student == null) {
            return new JsonResult("No student with given email in given course.", HttpStatus.SC_NOT_FOUND);
        }

        boolean isOpenOrPublishedEmailSentForTheCourse =
                logic.isOpenOrPublishedEmailSentForTheCourse(courseId);

        StudentEditDetails dataFormat =
                new StudentEditDetails(student, isOpenOrPublishedEmailSentForTheCourse);

        return new JsonResult(dataFormat);
    }

    /**
     * Data format for {@link GetStudentEditDetailsAction}.
     */
    public static class StudentEditDetails extends ApiOutput {

        private final StudentAttributes student;
        private final boolean isOpenOrPublishedEmailSentForTheCourse;

        public StudentEditDetails(StudentAttributes student,
                                  boolean isOpenOrPublishedEmailSentForTheCourse) {
            this.student = student;
            this.isOpenOrPublishedEmailSentForTheCourse = isOpenOrPublishedEmailSentForTheCourse;
        }

        public boolean isOpenOrPublishedEmailSentForTheCourse() {
            return isOpenOrPublishedEmailSentForTheCourse;
        }

        public StudentAttributes getStudent() {
            return student;
        }
    }
}
