package teammates.ui.webapi;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;

/**
 * Action: deletes a student from a course.
 */
public class DeleteStudentAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        if (userInfo.isAdmin) {
            return;
        }

        if (!userInfo.isInstructor) {
            throw new UnauthorizedAccessException("Instructor privilege is required to access this resource.");
        }

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        if (!isCourseMigrated(courseId)) {
            InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.id);
            gateKeeper.verifyAccessible(
                    instructor, logic.getCourse(courseId), Const.InstructorPermissions.CAN_MODIFY_STUDENT);

            return;
        }

        Instructor instructor = sqlLogic.getInstructorByGoogleId(courseId, userInfo.id);
        gateKeeper.verifyAccessible(
                instructor, sqlLogic.getCourse(courseId), Const.InstructorPermissions.CAN_MODIFY_STUDENT);
    }

    @Override
    public JsonResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String studentId = getRequestParamValue(Const.ParamsNames.STUDENT_ID);

        String studentEmail = null;

        if (!isCourseMigrated(courseId)) {
            if (studentId == null) {
                studentEmail = getNonNullRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);
            } else {
                StudentAttributes student = logic.getStudentForGoogleId(courseId, studentId);
                if (student != null) {
                    studentEmail = student.getEmail();
                }
            }

            // if student is not found, fail silently
            if (studentEmail != null) {
                logic.deleteStudentCascade(courseId, studentEmail);
            }

            return new JsonResult("Student is successfully deleted.");
        }

        if (studentId == null) {
            studentEmail = getNonNullRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);
        } else {
            Student student = sqlLogic.getStudentByGoogleId(courseId, studentId);
            if (student != null) {
                studentEmail = student.getEmail();
            }
        }

        // if student is not found, fail silently
        if (studentEmail != null) {
            sqlLogic.deleteStudentCascade(courseId, studentEmail);
        }

        return new JsonResult("Student is successfully deleted.");
    }

}
