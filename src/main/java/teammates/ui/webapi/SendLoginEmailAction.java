package teammates.ui.webapi;

import static teammates.common.util.FieldValidator.REGEX_EMAIL;

import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;

import teammates.common.util.Const;
import teammates.common.util.EmailSendingStatus;
import teammates.common.util.EmailWrapper;
import teammates.common.util.StringHelper;
import teammates.ui.output.SendLoginEmailResponseData;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * Creates a new account request.
 */
class SendLoginEmailAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.PUBLIC;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        // Login email can be sent to anyone
    }

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException, InvalidOperationException {
        String userEmail = getNonNullRequestParamValue(Const.ParamsNames.USER_EMAIL);
        if (!StringHelper.isMatching(userEmail, REGEX_EMAIL)) {
            throw new InvalidHttpParameterException("Invalid email address: " + userEmail);
        }

        String continueUrl = getNonNullRequestParamValue(Const.ParamsNames.CONTINUE_URL);

        String userCaptchaResponse = getRequestParamValue(Const.ParamsNames.USER_CAPTCHA_RESPONSE);
        if (!recaptchaVerifier.isVerificationSuccessful(userCaptchaResponse)) {
            return new JsonResult(new SendLoginEmailResponseData(false, "Something went wrong with "
                    + "the reCAPTCHA verification. Please try again."));
        }

        ActionCodeSettings actionCodeSettings = ActionCodeSettings.builder()
                .setUrl(continueUrl)
                .setHandleCodeInApp(true)
                .build();
        String loginLink;
        try {
            loginLink = FirebaseAuth.getInstance().generateSignInWithEmailLink(userEmail, actionCodeSettings);
        } catch (IllegalArgumentException | FirebaseAuthException e) {
            throw new InvalidOperationException("Error generating login link: " + e.getMessage());
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
