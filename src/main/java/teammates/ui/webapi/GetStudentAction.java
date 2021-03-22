package teammates.ui.webapi;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.ui.output.StudentData;

/**
 * Get the information of a student inside a course.
 */
class GetStudentAction extends Action {

    /** Message indicating that a student not found. */
    static final String STUDENT_NOT_FOUND = "No student found";

    /** String indicating ACCESS is not given. */
    private static final String UNAUTHORIZED_ACCESS = "You are not allowed to view this resource!";

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.PUBLIC;
    }

    @Override
    void checkSpecificAccessControl() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        CourseAttributes course = logic.getCourse(courseId);

        StudentAttributes student;

        String studentEmail = getRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);
        String regKey = getRequestParamValue(Const.ParamsNames.REGKEY);

        if (studentEmail != null) {
            student = logic.getStudentForEmail(courseId, studentEmail);
            if (student == null || userInfo == null || !userInfo.isInstructor) {
                throw new UnauthorizedAccessException(UNAUTHORIZED_ACCESS);
            }

            InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.id);
            gateKeeper.verifyAccessible(instructor, logic.getCourse(courseId), student.section,
                    Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS);
        } else if (regKey != null) {
            getUnregisteredStudent().orElseThrow(() -> new UnauthorizedAccessException(UNAUTHORIZED_ACCESS));
        } else {
            if (userInfo == null || !userInfo.isStudent) {
                throw new UnauthorizedAccessException(UNAUTHORIZED_ACCESS);
            }

            student = logic.getStudentForGoogleId(courseId, userInfo.id);
            gateKeeper.verifyAccessible(student, course);
        }
    }

    @Override
    JsonResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        StudentAttributes student;

        String studentEmail = getRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);

        if (studentEmail == null) {
            student = getUnregisteredStudent().orElseGet(() -> {
                if (userInfo == null) {
                    return null;
                }

                return logic.getStudentForGoogleId(courseId, userInfo.id);
            });
        } else {
            student = logic.getStudentForEmail(courseId, studentEmail);
        }

        if (student == null) {
            return new JsonResult(STUDENT_NOT_FOUND, HttpStatus.SC_NOT_FOUND);
        }

        StudentData studentData = new StudentData(student);
        if (userInfo != null && userInfo.isAdmin) {
            studentData.setKey(StringHelper.encrypt(student.getKey()));
            studentData.setGoogleId(student.googleId);
        }

        if (studentEmail == null) {
            // hide information if not an instructor
            studentData.hideInformationForStudent();
            // add student institute
            studentData.setInstitute(logic.getCourseInstitute(courseId));
        }

        return new JsonResult(studentData);
    }
}
