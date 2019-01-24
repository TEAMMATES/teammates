package teammates.ui.newcontroller;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;

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
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.getId());

        InstructorPrivilegeResponse response = new InstructorPrivilegeResponse();
        response.setCanModifySession(
                instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION));
        response.setCanSubmitSessionInSections(
                instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS)
                        || instructor.isAllowedForPrivilegeAnySection(
                                feedbackSessionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS));

        return new JsonResult(response);
    }


    /**
     * The output format of {@link GetInstructorPrivilegeAction}.
     */
    public static class InstructorPrivilegeResponse extends ActionResult.ActionOutput {
        private boolean canModifySession;
        private boolean canSubmitSessionInSections;

        public void setCanModifySession(boolean canModifySession) {
            this.canModifySession = canModifySession;
        }

        public void setCanSubmitSessionInSections(boolean canSubmitSessionInSections) {
            this.canSubmitSessionInSections = canSubmitSessionInSections;
        }
    }

}
