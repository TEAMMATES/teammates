package teammates.ui.webapi;

import static teammates.common.util.FieldValidator.REGEX_EMAIL;

import java.util.List;

import teammates.common.util.Const;
import teammates.common.util.EmailWrapper;
import teammates.common.util.StringHelper;
import teammates.storage.entity.Student;
import teammates.ui.exception.InvalidHttpParameterException;
import teammates.ui.output.SessionLinksRecoveryResponseData;

/**
 * Action specifically created for confirming email and sending session recovery links.
 */
public class SessionLinksRecoveryAction extends PublicAction {

    @Override
    public JsonResult execute() {
        String recoveryEmailAddress = getNonNullRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);

        if (!StringHelper.isMatching(recoveryEmailAddress, REGEX_EMAIL)) {
            throw new InvalidHttpParameterException("Invalid email address: " + recoveryEmailAddress);
        }

        String userCaptchaResponse = getRequestParamValue(Const.ParamsNames.USER_CAPTCHA_RESPONSE);
        if (!recaptchaVerifier.isVerificationSuccessful(userCaptchaResponse)) {
            throw new InvalidHttpParameterException("Something went wrong with the reCAPTCHA verification. "
                    + "Please try again.");
        }

        List<Student> studentsForEmail = logic.getAllStudentsForEmail(recoveryEmailAddress);
        EmailWrapper email = studentsForEmail.isEmpty()
                ? emailGenerator.generateSessionLinksRecoveryEmailForNonExistentStudent(recoveryEmailAddress)
                : emailGenerator.generateSessionLinksRecoveryEmailForExistingStudent(
                        recoveryEmailAddress, studentsForEmail);
        emailQueueService.enqueuePriority(List.of(email));

        return new JsonResult(new SessionLinksRecoveryResponseData(
                    "The recovery links for your feedback sessions have been sent to the "
                            + "specified email address: " + recoveryEmailAddress));
    }
}
