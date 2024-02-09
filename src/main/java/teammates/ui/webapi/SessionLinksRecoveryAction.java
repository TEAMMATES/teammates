package teammates.ui.webapi;

import static teammates.common.util.FieldValidator.REGEX_EMAIL;

import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.EmailSendingStatus;
import teammates.common.util.EmailWrapper;
import teammates.common.util.StringHelper;
import teammates.ui.output.SessionLinksRecoveryResponseData;

/**
 * Action specifically created for confirming email and sending session recovery links.
 */
public class SessionLinksRecoveryAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.PUBLIC;
    }

    @Override
    void checkSpecificAccessControl() {
        // no specific access control needed.
    }

    @Override
    public JsonResult execute() {
        String recoveryEmailAddress = getNonNullRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);

        if (!StringHelper.isMatching(recoveryEmailAddress, REGEX_EMAIL)) {
            throw new InvalidHttpParameterException("Invalid email address: " + recoveryEmailAddress);
        }

        String userCaptchaResponse = getRequestParamValue(Const.ParamsNames.USER_CAPTCHA_RESPONSE);
        if (!recaptchaVerifier.isVerificationSuccessful(userCaptchaResponse)) {
            return new JsonResult(new SessionLinksRecoveryResponseData(false, "Something went wrong with "
                    + "the reCAPTCHA verification. Please try again."));
        }

        int firstStudentIdx = 0;
        String noStudentName = "";
        List<StudentAttributes> studentFromDataStore = logic.getAllStudentsForEmail(recoveryEmailAddress);

        Map<CourseAttributes, StringBuilder> dataStoreLinkFragmentMap =
                emailGenerator.generateLinkFragmentsMap(studentFromDataStore);

        String studentNameFromDatastore = (studentFromDataStore.isEmpty())
                ? noStudentName
                : studentFromDataStore.get(firstStudentIdx).getName();

        EmailWrapper email = sqlEmailGenerator
                .generateSessionLinksRecoveryEmailForStudent(recoveryEmailAddress,
                studentNameFromDatastore, dataStoreLinkFragmentMap);

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
