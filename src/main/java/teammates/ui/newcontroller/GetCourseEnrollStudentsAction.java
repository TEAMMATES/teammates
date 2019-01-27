package teammates.ui.newcontroller;

import java.util.List;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;


/**
 * Action: Fetches existing students of a course.
 */
public class GetCourseEnrollStudentsAction extends Action {
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
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.id);
        gateKeeper.verifyAccessible(instructor, logic.getCourse(courseId));
    }

    @Override
    public ActionResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        List<StudentAttributes> enrolledStudents = logic.getStudentsForCourse(courseId);
        StudentList output = new StudentList(enrolledStudents);
        return new JsonResult(output);
    }

    /**
     * Output format for {@link GetCourseEnrollStudentsAction}.
     */
    public static class StudentList extends ActionResult.ActionOutput {
        List<StudentAttributes> enrolledStudents;

        public StudentList(List<StudentAttributes> enrolledStudents) {
            this.enrolledStudents = enrolledStudents;
        }

        public List<StudentAttributes> getEnrolledStudents() {
            return enrolledStudents;
        }
    }
}
