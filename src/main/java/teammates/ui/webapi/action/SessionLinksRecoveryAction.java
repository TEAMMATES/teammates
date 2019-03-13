package teammates.ui.webapi.action;

import teammates.common.util.Const;
import teammates.common.util.EmailWrapper;
import teammates.logic.api.EmailGenerator;
import teammates.ui.webapi.output.SessionLinksRecoveryResponseData;


/**
 * Action specifically created for confirming email and sending session recovery links.
 */
public class SessionLinksRecoveryAction extends Action {

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
        String recoveryEmailAddress = getNonNullRequestParamValue(Const.ParamsNames.SESSION_LINKS_RECOVERY_EMAIL);

        EmailWrapper email = new EmailGenerator().generateSessionLinksRecoveryEmail(recoveryEmailAddress);
        emailSender.sendEmail(email);

        // Keep this status flag here for recaptcha
        return new JsonResult(new SessionLinksRecoveryResponseData(true,
                "The recovery links for your feedback sessions have been sent to the "
                        + "specified email address: " + recoveryEmailAddress));
    }
}
