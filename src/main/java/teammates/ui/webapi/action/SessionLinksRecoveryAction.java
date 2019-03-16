package teammates.ui.webapi.action;

import static teammates.common.util.FieldValidator.REGEX_EMAIL;

import com.google.api.client.http.HttpStatusCodes;

import teammates.common.util.Const;
import teammates.common.util.EmailSendingStatus;
import teammates.common.util.EmailWrapper;
import teammates.common.util.StringHelper;
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

        if (!StringHelper.isMatching(recoveryEmailAddress, REGEX_EMAIL)) {
            return new JsonResult("invalid email address", HttpStatusCodes.STATUS_CODE_BAD_REQUEST);
        }

        EmailWrapper email = new EmailGenerator().generateSessionLinksRecoveryEmailStudent(recoveryEmailAddress);
        EmailSendingStatus status = emailSender.sendEmail(email);

        // Keep this status flag here for recaptcha
        return new JsonResult(new SessionLinksRecoveryResponseData(status.isSuccess(),
                "The recovery links for your feedback sessions have been sent to the "
                        + "specified email address: " + recoveryEmailAddress));
    }
}
