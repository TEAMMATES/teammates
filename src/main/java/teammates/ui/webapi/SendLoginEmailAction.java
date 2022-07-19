package teammates.ui.webapi;

import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;

import teammates.common.util.Const;
import teammates.common.util.EmailWrapper;
import teammates.common.util.FieldValidator;
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
        String userEmail = getRequestParamValue(Const.ParamsNames.USER_EMAIL);
        String emailError = FieldValidator.getInvalidityInfoForEmail(userEmail);
        if (!emailError.isEmpty()) {
            throw new InvalidHttpRequestBodyException(emailError);
        }
        String continueUrl = getNonNullRequestParamValue(Const.ParamsNames.CONTINUE_URL);

        String loginLink;
        try {
            ActionCodeSettings actionCodeSettings = ActionCodeSettings.builder()
                    .setUrl(continueUrl)
                    .setHandleCodeInApp(true)
                    .build();
            loginLink = FirebaseAuth.getInstance().generateSignInWithEmailLink(userEmail, actionCodeSettings);
        } catch (IllegalArgumentException | FirebaseAuthException e) {
            throw new InvalidOperationException("Error generating login link: " + e.getMessage());
        }

        EmailWrapper loginEmail = emailGenerator.generateLoginEmail(userEmail, loginLink);
        emailSender.sendEmail(loginEmail);

        return new JsonResult("Login email sent.");
    }

}
