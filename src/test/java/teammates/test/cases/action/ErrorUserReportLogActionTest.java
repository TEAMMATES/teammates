package teammates.test.cases.action;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.logic.core.AccountsLogic;
import teammates.ui.controller.AjaxResult;
import teammates.ui.controller.ErrorUserReportLogAction;

/**
 * SUT: {@link ErrorUserReportLogAction}.
 */
public class ErrorUserReportLogActionTest extends BaseActionTest {

    private static final AccountsLogic accountsLogic = AccountsLogic.inst();

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.ERROR_FEEDBACK_SUBMIT;
    }

    @Override
    protected ErrorUserReportLogAction getAction(String... params) {
        return (ErrorUserReportLogAction) gaeSimulation.getActionObject(getActionUri(), params);
    }

    @Override
    @Test
    protected void testExecuteAndPostProcess() throws Exception {
        InstructorAttributes instructor1ofCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        gaeSimulation.loginAsInstructor(instructor1ofCourse1.googleId);

        ______TS("Typical Success Case");

        final String testErrorReportSubject = "Test Error Subject";
        final String testErrorReportContent = "This is a test user-submitted error report.";
        final String testErrorReportRequestedUrl = "/page/testurl";

        String[] params = new String[] {
                Const.ParamsNames.ERROR_FEEDBACK_URL_REQUESTED, testErrorReportRequestedUrl,
                Const.ParamsNames.ERROR_FEEDBACK_EMAIL_SUBJECT, testErrorReportSubject,
                Const.ParamsNames.ERROR_FEEDBACK_EMAIL_CONTENT, testErrorReportContent,
        };

        ErrorUserReportLogAction action = getAction(params);
        AjaxResult result = getAjaxResult(action);

        assertEquals(Const.StatusMessages.ERROR_FEEDBACK_SUBMIT_SUCCESS, result.getStatusMessage());

        // getting basic AccountAttributes because ErrorUserReportLogAction only logs this.
        AccountAttributes instructor1ofCourse1AccountAttributes = accountsLogic
                .getAccount(instructor1ofCourse1.googleId, false);

        final String expectedLogMessage = "====== USER FEEDBACK ABOUT ERROR ====== \n"
                + "REQUESTED URL: " + testErrorReportRequestedUrl + "\n"
                + "ACCOUNT DETAILS: " + instructor1ofCourse1AccountAttributes.toString() + "\n"
                + "SUBJECT: " + testErrorReportSubject + "\n"
                + "FEEDBACK: " + testErrorReportContent;

        assertEquals(expectedLogMessage,
                action.getUserErrorReportLogMessage());
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        String[] submissionParams = new String[] {
                Const.ParamsNames.ERROR_FEEDBACK_EMAIL_SUBJECT, "test subject",
                Const.ParamsNames.ERROR_FEEDBACK_EMAIL_CONTENT, "test content",
        };

        verifyOnlyLoggedInUsersCanAccess(submissionParams);

    }

}
