package teammates.ui.webapi.action;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.ui.webapi.output.ApiOutput;

/**
 * Get the instructor privilege.
 */
public class GetInstructorPrivilegeAction extends Action {

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.getId());
        if (instructor == null) {
            throw new UnauthorizedAccessException("Not instructor of the course");
        }
    }

    @Override
    public ActionResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.getId());

        InstructorPrivilegeResponse response = new InstructorPrivilegeResponse();
        response.setCanModifyCourse(
                instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE));
        response.setCanModifySession(
                instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION));
        response.setCanModifyStudent(
                instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT));
        response.setCanSubmitSessionInSections(
                instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS)
                        || instructor.isAllowedForPrivilegeAnySection(
                                feedbackSessionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS));

        return new JsonResult(response);
    }

    /**
     * The output format of {@link GetInstructorPrivilegeAction}.
     */
    public static class InstructorPrivilegeResponse extends ApiOutput {
        private boolean canModifyCourse;
        private boolean canModifySession;
        private boolean canModifyStudent;
        private boolean canSubmitSessionInSections;

        public void setCanModifyCourse(boolean canModifyCourse) {
            this.canModifyCourse = canModifyCourse;
        }

        public void setCanModifySession(boolean canModifySession) {
            this.canModifySession = canModifySession;
        }

        public void setCanModifyStudent(boolean canModifyStudent) {
            this.canModifyStudent = canModifyStudent;
        }

        public void setCanSubmitSessionInSections(boolean canSubmitSessionInSections) {
            this.canSubmitSessionInSections = canSubmitSessionInSections;
        }
    }

}
