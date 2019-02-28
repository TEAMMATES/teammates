package teammates.ui.webapi.action;

import java.util.Optional;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.ui.webapi.output.StudentData;

/**
 * Get the information of a student inside a course.
 */
public class GetStudentAction extends Action {

    private static final String UNAUTHORIZED_ACCESS = "You are not allowed to view this resource!";

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.PUBLIC;
    }

    @Override
    public void checkSpecificAccessControl() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        CourseAttributes course = logic.getCourse(courseId);

        StudentAttributes student = null;

        String studentEmail = getRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);
        String regKey = getRequestParamValue(Const.ParamsNames.REGKEY);

        if (studentEmail != null) {
            student = logic.getStudentForEmail(courseId, studentEmail);
            if (student == null || !userInfo.isInstructor) {
                throw new UnauthorizedAccessException(UNAUTHORIZED_ACCESS);
            }

            InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.id);
            gateKeeper.verifyAccessible(instructor, logic.getCourse(courseId), student.section,
                    Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS);
        } else if (regKey != null) {
            getUnregisteredStudent().orElseThrow(() -> new UnauthorizedAccessException(UNAUTHORIZED_ACCESS));
        } else {
            if (!userInfo.isStudent) {
                throw new UnauthorizedAccessException(UNAUTHORIZED_ACCESS);
            }

            student = logic.getStudentForGoogleId(courseId, userInfo.id);
            gateKeeper.verifyAccessible(student, course);
        }
    }

    @Override
    public ActionResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        StudentAttributes student = null;

        String studentEmail = getRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);
        String regKey = getRequestParamValue(Const.ParamsNames.REGKEY);

        if (studentEmail != null) {
            student = logic.getStudentForEmail(courseId, studentEmail);
        } else if (regKey != null) {
            Optional<StudentAttributes> studentCheck = getUnregisteredStudent();
            if (studentCheck.isPresent()) {
                student = studentCheck.get();
            }
        } else {
            student = logic.getStudentForGoogleId(courseId, userInfo.id);
        }

        if (student == null) {
            return new JsonResult("No student found", HttpStatus.SC_NOT_FOUND);
        }

        StudentData studentData = new StudentData(student);

        if (studentEmail == null) {
            studentData.setComments(null);
            studentData.setJoinState(null);
        }

        return new JsonResult(studentData);
    }
}
