package teammates.test.cases.action;

import org.testng.annotations.Test;

import com.google.apphosting.api.DeadlineExceededException;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.exception.NullPostParameterException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.ui.controller.ActionResult;
import teammates.ui.controller.AdminExceptionTestAction;

/**
 * SUT: {@link AdminExceptionTestAction}.
 */
public class AdminExceptionTestActionTest extends BaseActionTest {

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.ADMIN_EXCEPTION_TEST;
    }

    /**
     * This method verifies if exceptions from AdminExceptionTestAction are thrown correctly.
     */
    @Override
    @Test
    @SuppressWarnings("PMD.AvoidCatchingNPE") // deliberately done for testing
    public void testExecuteAndPostProcess() {
        String adminUserId = "admin.user";
        gaeSimulation.loginAsAdmin(adminUserId);

        String error;
        AdminExceptionTestAction action;

        ______TS("test for AssertionError");
        error = AssertionError.class.getSimpleName();
        action = getAction(Const.ParamsNames.ERROR, error);
        try {
            action.executeAndPostProcess();
            signalFailureToDetectException("AssertionError");
        } catch (AssertionError ae) {
            assertEquals(ae.getMessage(), "AssertionError Testing");
        }

        ______TS("test for EntityDoesNotExistException");
        error = EntityDoesNotExistException.class.getSimpleName();
        action = getAction(Const.ParamsNames.ERROR, error);
        try {
            action.executeAndPostProcess();
            signalFailureToDetectException("EntityNotFoundException");
        } catch (EntityNotFoundException enfe) {
            assertEquals(enfe.getMessage(), "EntityDoesNotExistException Testing");
        }

        ______TS("test for UnauthorizedAccessException");
        error = UnauthorizedAccessException.class.getSimpleName();
        action = getAction(Const.ParamsNames.ERROR, error);
        try {
            action.executeAndPostProcess();
            signalFailureToDetectException("UnauthorizedAccessException");
        } catch (UnauthorizedAccessException uae) {
            assertEquals(uae.getMessage(), "UnauthorizedAccessException Testing");
        }

        ______TS("test for NullPointerException");
        error = NullPointerException.class.getSimpleName();
        action = getAction(Const.ParamsNames.ERROR, error);
        try {
            action.executeAndPostProcess();
            signalFailureToDetectException("NullPointerException");
        } catch (NullPointerException npe) {
            assertEquals(npe.getMessage(), "NullPointerException Testing");
        }

        ______TS("test for DeadlineExceededException");
        error = DeadlineExceededException.class.getSimpleName();
        action = getAction(Const.ParamsNames.ERROR, error);
        try {
            action.executeAndPostProcess();
            signalFailureToDetectException("DeadlineExceededException");
        } catch (DeadlineExceededException dlee) {
            assertEquals(dlee.getMessage(), "DeadlineExceededException Testing");
        }

        ______TS("test for NullPostParameterException");
        error = NullPostParameterException.class.getSimpleName();
        action = getAction(Const.ParamsNames.ERROR, error);
        try {
            action.executeAndPostProcess();
            signalFailureToDetectException("NullPostParameterException");
        } catch (NullPostParameterException nppe) {
            assertEquals(nppe.getMessage(), "NullPostParameterException Testing");
        }

        ______TS("test for success scenario");
        error = "";
        action = getAction(Const.ParamsNames.ERROR, error);
        ActionResult result = action.executeAndPostProcess();
        assertEquals(Const.ActionURIs.ADMIN_HOME_PAGE, result.destination);
    }

    @Override
    protected AdminExceptionTestAction getAction(String... params) {
        return (AdminExceptionTestAction) gaeSimulation.getActionObject(getActionUri(), params);
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        String[] submissionParams = new String[] {};
        verifyOnlyAdminsCanAccess(submissionParams);
    }
}
