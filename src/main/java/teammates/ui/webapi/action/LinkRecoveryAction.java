package teammates.ui.webapi.action;

import teammates.common.util.Const;
import teammates.ui.webapi.output.ApiOutput;

/**
 * Action specifically created for confirming email and sending recovery link.
 */
public class ConfirmRestoreEmailAction extends Action{
    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.PUBLIC;
    }

    @Override
    public void checkSpecificAccessControl() {

    }

    @Override
    public ActionResult execute() {
        String email = getNonNullRequestParamValue(Const.ParamsNames.RESTORE_EMAIL);
        boolean hasStudentsWithRestoreEmail = !logic.getAllStudentForEmail(email).isEmpty();

        if (hasStudentsWithRestoreEmail) {

        } else {
            return new JsonResult()
        }

    }

    /**
     * Output format for {@link GetFeedbackQuestionRecipientsAction}.
     */
    public static class ResponseRestoreStatus extends ApiOutput {
        public ResponseRestoreStatus(String status)
    }
}
