package teammates.ui.webapi;

import static teammates.common.util.FieldValidator.REGEX_EMAIL;

import teammates.common.util.Const;
import teammates.common.util.EmailSendingStatus;
import teammates.common.util.EmailWrapper;
import teammates.common.util.LoginLinkOptions;
import teammates.common.util.StringHelper;
import teammates.logic.external.FirebaseAuthService;
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
        String userEmail = getNonNullRequestParamValue(Const.ParamsNames.USER_EMAIL);
        if (!StringHelper.isMatching(userEmail, REGEX_EMAIL)) {
            throw new InvalidHttpParameterException("Invalid email address: " + userEmail);
        }

        String userCaptchaResponse = getRequestParamValue(Const.ParamsNames.USER_CAPTCHA_RESPONSE);
        if (!recaptchaVerifier.isVerificationSuccessful(userCaptchaResponse)) {
            return new JsonResult(new SendLoginEmailResponseData(false, "ReCAPTCHA verification "
                    + "failed. Please try again."));
        }

        String continueUrl = getRequestParamValue(Const.ParamsNames.CONTINUE_URL);
        LoginLinkOptions.Builder loginLinkOptionsBuilder = LoginLinkOptions.builder();
        if (authProxy.getService() instanceof FirebaseAuthService) {
            loginLinkOptionsBuilder = loginLinkOptionsBuilder.withUserEmail(userEmail).withContinueUrl(continueUrl);
        }
        LoginLinkOptions loginLinkOptions = loginLinkOptionsBuilder.build();
        String loginLink = authProxy.generateLoginLink(loginLinkOptions);
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
