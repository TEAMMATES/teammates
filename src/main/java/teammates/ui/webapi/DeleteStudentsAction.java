package teammates.ui.webapi;

import teammates.common.datatransfer.attributes.InstructorAttributes;
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
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        if (!userInfo.isInstructor) {
            throw new UnauthorizedAccessException("Instructor privilege is required to delete students from course.");
        }
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.id);
        gateKeeper.verifyAccessible(
                instructor, logic.getCourse(courseId), Const.InstructorPermissions.CAN_MODIFY_STUDENT);
    }

    @Override
    public JsonResult execute() {
        var courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        var limit = getNonNullRequestParamValue(Const.ParamsNames.LIMIT);

        logic.deleteStudentsInCourseCascade(courseId, Integer.parseInt(limit));

        return new JsonResult("Successful");
    }
}
