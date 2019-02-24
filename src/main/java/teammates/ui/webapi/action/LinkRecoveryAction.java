package teammates.ui.webapi.action;

import teammates.common.util.Const;
import teammates.common.util.EmailWrapper;
import teammates.logic.api.EmailGenerator;
import teammates.ui.webapi.output.LinkRecoveryResponseData;


/**
 * Action specifically created for confirming email and sending session recovery links.
 */
public class LinkRecoveryAction extends Action {

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.PUBLIC;
    }

    @Override
    public void checkSpecificAccessControl() {
        // no specific access control needed.
    }

    @Override
    public ActionResult execute() {
        String recoveryEmail = getNonNullRequestParamValue(Const.ParamsNames.RECOVERY_EMAIL);
        boolean hasStudentsWithRecoveryEmail = !logic.getAllStudentForEmail(recoveryEmail).isEmpty();

        if (hasStudentsWithRecoveryEmail) {
            EmailWrapper email = new EmailGenerator().generateLinkRecoveryEmail(recoveryEmail);
            emailSender.sendEmail(email);
            return new JsonResult(new LinkRecoveryResponseData(true,
                    "The recovery links for your feedback sessions have been sent to the specified email: "
                            + recoveryEmail));
        } else {
            return new JsonResult(new LinkRecoveryResponseData(false,
                    "No student is registered under email: " + recoveryEmail));
        }
    }
}
