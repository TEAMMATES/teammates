package teammates.ui.webapi;

import static teammates.common.util.FieldValidator.REGEX_EMAIL;

import com.google.api.client.http.HttpStatusCodes;

import teammates.common.util.Const;
import teammates.common.util.EmailSendingStatus;
import teammates.common.util.EmailWrapper;
import teammates.common.util.StringHelper;
import teammates.ui.output.SessionLinksRecoveryResponseData;

/**
 * Action specifically created for confirming email and sending session recovery links.
 */
class SessionLinksRecoveryAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.PUBLIC;
    }

    @Override
    void checkSpecificAccessControl() {
        // no specific access control needed.
    }

    @Override
    JsonResult execute() {
        String recoveryEmailAddress = getNonNullRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);

        if (!StringHelper.isMatching(recoveryEmailAddress, REGEX_EMAIL)) {
            return new JsonResult("Invalid email address: " + recoveryEmailAddress,
                                    HttpStatusCodes.STATUS_CODE_BAD_REQUEST);
        }

        String userCaptchaResponse = getRequestParamValue(Const.ParamsNames.USER_CAPTCHA_RESPONSE);
        if (!recaptchaVerifier.isVerificationSuccessful(userCaptchaResponse)) {
            return new JsonResult(new SessionLinksRecoveryResponseData(false, "Something went wrong with "
                    + "the reCAPTCHA verification. Please try again."));
        }

        EmailWrapper email = emailGenerator.generateSessionLinksRecoveryEmailForStudent(recoveryEmailAddress);
        EmailSendingStatus status = emailSender.sendEmail(email);

        if (status.isSuccess()) {
            return new JsonResult(new SessionLinksRecoveryResponseData(true,
                    "The recovery links for your feedback sessions have been sent to the "
                            + "specified email address: " + recoveryEmailAddress));
        } else {
            return new JsonResult(new SessionLinksRecoveryResponseData(false, "An error occurred. "
                    + "The email could not be sent."));
        }
    }
}
