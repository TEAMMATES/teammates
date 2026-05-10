package teammates.ui.webapi;

import static teammates.common.util.FieldValidator.REGEX_EMAIL;

import teammates.common.util.Const;
import teammates.common.util.EmailSendingStatus;
import teammates.common.util.EmailWrapper;
import teammates.common.util.StringHelper;
import teammates.ui.exception.InvalidHttpParameterException;
import teammates.ui.output.SessionLinksRecoveryResponseData;

/**
 * Action specifically created for confirming email and sending session recovery links.
     */
public class SessionLinksRecoveryAction extends PublicAction {

    @Override
        public JsonResult execute() {
                    String recoveryEmailAddress = getRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);

            if (recoveryEmailAddress == null || !recoveryEmailAddress.matches(REGEX_EMAIL)) {
                            throw new InvalidHttpParameterException("Invalid email address: " + recoveryEmailAddress);
            }

            String recoveryEmailAddressParam = getRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);
                    if (recoveryEmailAddressParam != null) {
                                    recoveryEmailAddress = recoveryEmailAddressParam;
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
