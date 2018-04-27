package teammates.test.cases.action;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.ui.controller.AjaxResult;
import teammates.ui.controller.FeedbackResendLinksAction;

/**
 * SUT: {@link FeedbackResendLinksAction}.
 */
public class FeedbackResendLinksActionTest extends BaseActionTest {

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.STUDENT_FEEDBACK_RESEND_LINKS;
    }

    @Override
    protected FeedbackResendLinksAction getAction(String... params) {
        return (FeedbackResendLinksAction) gaeSimulation.getActionObject(getActionUri(), params);
    }

    @Override
    @Test
    protected void testExecuteAndPostProcess() throws Exception {
        StudentAttributes student = typicalBundle.students.get("student1InCourse1");
        FieldValidator validator = new FieldValidator();

        ______TS("Typical Success Case");

        String userEmail = student.getEmail();

        String[] params = new String[] {
                Const.ParamsNames.STUDENT_EMAIL, userEmail,
        };

        FeedbackResendLinksAction action = getAction(params);
        AjaxResult result = getAjaxResult(action);
        assertEquals(Const.StatusMessages.FEEDBACK_SESSION_ACCESS_LINKS_RESENT, result.getStatusMessage());

        ______TS("Invalid Email Case");

        String invalidEmail = "dummyInvalidEmail";

        params = new String[] {
                Const.ParamsNames.STUDENT_EMAIL, invalidEmail,
        };

        action = getAction(params);
        result = getAjaxResult(action);
        assertEquals(validator.getInvalidityInfoForEmail(invalidEmail), result.getStatusMessage());
    }

    @Override
    protected void testAccessControl() throws Exception {
        // This is not required
    }

}
