package teammates.ui.webapi;

import static teammates.common.util.FieldValidator.REGEX_EMAIL;

import teammates.common.util.Const;
import teammates.common.util.EmailSendingStatus;
import teammates.common.util.EmailWrapper;
import teammates.common.util.StringHelper;
import teammates.ui.exception.InvalidHttpParameterException;
import teammates.ui.exception.InvalidOperationException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.SendLoginEmailResponseData;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * Sends a login email.
     */
public class SendLoginEmailAction extends PublicAction {

    @Override
        public JsonResult execute()
                throws InvalidHttpRequestBodyException, InvalidOperationException {
                            String userEmail = getNonNullRequestParamValue(Const.ParamsNames.USER_EMAIL);

            if (!userEmail.matches(REGEX_EMAIL)) {
                            throw new InvalidHttpParameterException("Invalid email address: " + userEmail);
            }

            if (StringHelper.isEmpty(userEmail)) {
                            return new JsonResult(new SendLoginEmailResponseData(false, "An error occurred. "
                                                                                                     + "The email could not be generated."));
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
