package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.Const.TaskQueue;
import teammates.common.util.EmailWrapper;
import teammates.storage.entity.Student;
import teammates.ui.exception.InvalidHttpParameterException;
import teammates.ui.output.SessionLinksRecoveryResponseData;

/**
 * SUT: {@link SessionLinksRecoveryAction}.
 */
public class SessionLinksRecoveryActionTest extends BaseActionTest<SessionLinksRecoveryAction> {
    private Student stubStudent;
    private EmailWrapper stubEmailWrapper;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.SESSION_LINKS_RECOVERY;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @BeforeMethod
    void setUp() {
        stubStudent = getTypicalStudent();
        stubEmailWrapper = new EmailWrapper();
        stubEmailWrapper.setRecipient(stubStudent.getEmail());
        stubEmailWrapper.setSubject("TEAMMATES: Recovery Email");
        reset(mockLogic, mockEmailGenerator);
    }

    @Test
    void testExecute_invalidEmailParam_throwsInvalidHttpParameterException() {
        String[] params1 = {};
        verifyHttpParameterFailure(params1);

        String[] params2 = {
                Const.ParamsNames.STUDENT_EMAIL, null,
        };
        verifyHttpParameterFailure(params2);

        String[] params3 = {
                Const.ParamsNames.STUDENT_EMAIL, "invalid-email-address",
        };
        InvalidHttpParameterException ihpe = verifyHttpParameterFailure(params3);
        assertEquals("Invalid email address: invalid-email-address", ihpe.getMessage());
    }

    @Test
    void testExecute_nonExistentEmail_success() {
        String[] params = {
                Const.ParamsNames.STUDENT_EMAIL, "non-existent@email.com",
                Const.ParamsNames.USER_CAPTCHA_RESPONSE, "correct-captcha-response",
        };

        stubEmailWrapper.setRecipient("non-existent@email.com");
        when(mockRecaptchaVerifier.isVerificationSuccessful(params[3])).thenReturn(true);
        when(mockLogic.getAllStudentsForEmail("non-existent@email.com")).thenReturn(List.of());
        when(mockEmailGenerator.generateSessionLinksRecoveryEmailForNonExistentStudent("non-existent@email.com"))
                .thenReturn(stubEmailWrapper);

        SessionLinksRecoveryAction action = getAction(params);
        JsonResult result = getJsonResult(action);

        SessionLinksRecoveryResponseData output = (SessionLinksRecoveryResponseData) result.getOutput();
        assertEquals("The recovery links for your feedback sessions have been sent to the "
                + "specified email address: non-existent@email.com", output.getMessage());
        verifySpecifiedTasksAdded(TaskQueue.PRIORITY_EMAIL_QUEUE_NAME, 1);
    }

    @Test
    void testExecute_existingStudent_success() {
        String[] params = {
                Const.ParamsNames.STUDENT_EMAIL, stubStudent.getEmail(),
                Const.ParamsNames.USER_CAPTCHA_RESPONSE, "correct-captcha-response",
        };

        when(mockRecaptchaVerifier.isVerificationSuccessful(params[3])).thenReturn(true);
        when(mockLogic.getAllStudentsForEmail(stubStudent.getEmail())).thenReturn(List.of(stubStudent));
        when(mockEmailGenerator.generateSessionLinksRecoveryEmailForExistingStudent(
                stubStudent.getEmail(), List.of(stubStudent)))
                .thenReturn(stubEmailWrapper);

        SessionLinksRecoveryAction action = getAction(params);
        JsonResult result = getJsonResult(action);

        SessionLinksRecoveryResponseData output = (SessionLinksRecoveryResponseData) result.getOutput();
        assertEquals("The recovery links for your feedback sessions have been sent to the "
                + "specified email address: " + stubStudent.getEmail(), output.getMessage());

        verifySpecifiedTasksAdded(TaskQueue.PRIORITY_EMAIL_QUEUE_NAME, 1);
    }

    @Test
    void testExecute_captchaVerificationFailed_throwsInvalidHttpParameterException() {
        String[] params = {
                Const.ParamsNames.STUDENT_EMAIL, stubStudent.getEmail(),
                Const.ParamsNames.USER_CAPTCHA_RESPONSE, "incorrect-captcha-response",
        };

        when(mockRecaptchaVerifier.isVerificationSuccessful(params[3])).thenReturn(false);

        InvalidHttpParameterException ihpe = verifyHttpParameterFailure(params);
        assertEquals("Something went wrong with the reCAPTCHA verification. Please try again.",
                ihpe.getMessage());

        verifyNoTasksAdded();
    }

    @Test
    protected void testAccessControl() {
        verifyCanAccess();
    }
}
