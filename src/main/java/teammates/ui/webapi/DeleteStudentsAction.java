package teammates.ui.webapi;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;

/**
 * Action: deletes all students in a course.
 */
class DeleteStudentsAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() {
        if (!userInfo.isInstructor) {
            throw new UnauthorizedAccessException("Instructor privilege is required to delete students from course.");
        }
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.id);
        gateKeeper.verifyAccessible(
                instructor, logic.getCourse(courseId), Const.InstructorPermissions.CAN_MODIFY_STUDENT);
    }

    @Override
    JsonResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        logic.deleteStudentsInCourseCascade(courseId);

        return new JsonResult("All the students have been removed from the course", HttpStatus.SC_OK);
    }
}
