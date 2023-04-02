package teammates.ui.webapi;

import static teammates.common.util.FieldValidator.REGEX_EMAIL;

import teammates.common.util.Const;
import teammates.common.util.EmailSendingStatus;
import teammates.common.util.EmailWrapper;
import teammates.common.util.StringHelper;
import teammates.ui.output.SendLoginEmailResponseData;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * Sends a login email.
 */
class SendLoginEmailAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.PUBLIC;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        // Sending of login emails can be requested by anyone
    }

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException, InvalidOperationException {
        if (!authProxy.isLoginEmailEnabled()) {
            throw new InvalidOperationException("Login using email link is not supported");
        }

        String userEmail = getNonNullRequestParamValue(Const.ParamsNames.USER_EMAIL);
        if (!StringHelper.isMatching(userEmail, REGEX_EMAIL)) {
            throw new InvalidHttpParameterException("Invalid email address: " + userEmail);
        }

        String userCaptchaResponse = getRequestParamValue(Const.ParamsNames.USER_CAPTCHA_RESPONSE);
        if (!recaptchaVerifier.isVerificationSuccessful(userCaptchaResponse)) {
            return new JsonResult(new SendLoginEmailResponseData(false, "ReCAPTCHA verification "
                    + "failed. Please try again."));
        }

        String continueUrl = getNonNullRequestParamValue(Const.ParamsNames.CONTINUE_URL);
        String loginLink = authProxy.generateLoginLink(userEmail, continueUrl);
        if (loginLink == null) {
            return new JsonResult(new SendLoginEmailResponseData(false, "An error occurred. "
                    + "The email could not be generated."));
        }

        EmailWrapper loginEmail = emailGenerator.generateLoginEmail(userEmail, loginLink);
        EmailSendingStatus status = emailSender.sendEmail(loginEmail);

        if (status.isSuccess()) {
            return new JsonResult(new SendLoginEmailResponseData(true,
                    "The login link has been sent to the specified email address: " + userEmail));
        } else {
            return new JsonResult(new SendLoginEmailResponseData(false, "An error occurred. "
                    + "The email could not be sent."));
        }
    }

}
