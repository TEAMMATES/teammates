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

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.PUBLIC;
    }

    @Override
    public void checkSpecificAccessControl() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        CourseAttributes course = logic.getCourse(courseId);
        StudentAttributes student = null;

        if (userInfo.isInstructor) {
            String studentEmail = getNonNullRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);
            student = logic.getStudentForEmail(courseId, studentEmail);
            if (student == null) {
                throw new UnauthorizedAccessException(Const.ErrorMessages.UNAUTHORIZED_ACCESS);
            }

            InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.id);
            gateKeeper.verifyAccessible(instructor, logic.getCourse(courseId), student.section,
                    Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS);
        } else if (userInfo.isStudent) {
            student = logic.getStudentForGoogleId(courseId, userInfo.id);
            if (student == null) {
                throw new UnauthorizedAccessException(Const.ErrorMessages.UNAUTHORIZED_ACCESS);
            }

            gateKeeper.verifyAccessible(student, course);
        } else {
            Optional<StudentAttributes> studentCheck = getUnregisteredStudent();
            if (studentCheck.isPresent()) {
                student = studentCheck.get();
                gateKeeper.verifyAccessible(student, course);
            } else {
                throw new UnauthorizedAccessException(Const.ErrorMessages.UNAUTHORIZED_ACCESS);
            }
        }
    }

    @Override
    public ActionResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        StudentAttributes student = null;

        if (userInfo == null) {
            Optional<StudentAttributes> studentCheck = getUnregisteredStudent();
            if (studentCheck.isPresent()) {
                student = studentCheck.get();
            }
        } else if (userInfo.isInstructor) {
            String studentEmail = getNonNullRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);
            student = logic.getStudentForEmail(courseId, studentEmail);
        } else if (userInfo.isStudent) {
            student = logic.getStudentForGoogleId(courseId, userInfo.id);
        }

        if (student == null) {
            return new JsonResult("No student found", HttpStatus.SC_NOT_FOUND);
        }

        StudentData studentData = new StudentData(student);

        if (userInfo == null || userInfo.isStudent) {
            studentData.setComments(null);
            studentData.setJoinState(null);
        }

        return new JsonResult(studentData);
    }
}
