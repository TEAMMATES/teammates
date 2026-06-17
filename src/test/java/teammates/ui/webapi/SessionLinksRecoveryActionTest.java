package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.logic.api.MockRecaptchaVerifier;
import teammates.test.GroupNames;
import teammates.ui.exception.InvalidHttpParameterException;
import teammates.ui.output.SessionLinksRecoveryResponseData;

/**
 * Tests for {@link SessionLinksRecoveryAction}.
 */
public class SessionLinksRecoveryActionTest
        extends BaseActionTest<SessionLinksRecoveryAction, SessionLinksRecoveryResponseData> {

    @Override
    @BeforeMethod
    public void setUpMethod() {
        super.setUpMethod();
        mockRecaptchaVerifier = new MockRecaptchaVerifier();
    }

    @Test(groups = GroupNames.ACTION)
    public void sessionLinksRecoveryAction_matchingStudentWithRecoverableSession_queuesPriorityEmail() {
        var course = given.course("course");
        given.student("student", s -> s.course(course.alias()).email("student@test.tmt"));
        given.feedbackSession("session", fs -> fs.course(course.alias()).published());
        persistGivenData(given);

        SessionLinksRecoveryResponseData result = execute(getRequest("student@test.tmt", "captcha-token"));

        assertEquals("The recovery links for your feedback sessions have been sent to the specified email address: "
                + "student@test.tmt", result.getMessage());
    }

    @Test(groups = GroupNames.ACTION)
    public void sessionLinksRecoveryAction_failedRecaptcha_throwsInvalidHttpParameterException() {
        mockRecaptchaVerifier = new MockRecaptchaVerifier() {
            @Override
            public boolean isVerificationSuccessful(String captchaResponse) {
                return false;
            }
        };

        RequestContext request = getRequest("student@test.tmt", "invalid-captcha-token");

        InvalidHttpParameterException exception = assertActionThrows(InvalidHttpParameterException.class, request);

        assertEquals("Something went wrong with the reCAPTCHA verification. Please try again.",
                exception.getMessage());
    }

    private RequestContext getRequest(String studentEmail, String captchaResponse) {
        return new RequestContext()
                .withParam(Const.ParamsNames.STUDENT_EMAIL, studentEmail)
                .withParam(Const.ParamsNames.USER_CAPTCHA_RESPONSE, captchaResponse);
    }
}
