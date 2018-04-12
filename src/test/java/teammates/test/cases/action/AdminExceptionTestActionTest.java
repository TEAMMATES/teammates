package teammates.test.cases.action;

import org.testng.annotations.Test;

import com.google.apphosting.api.DeadlineExceededException;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.NullPostParameterException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.ui.controller.AdminExceptionTestAction;

/**
 * SUT: {@link AdminExceptionTestAction}.
 */
public class AdminExceptionTestActionTest extends BaseActionTest {

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.ADMIN_EXCEPTION_TEST;
    }

    @Override
    @Test
    @SuppressWarnings("PMD.AvoidCatchingNPE") // deliberately done for testing
    public void testExecuteAndPostProcess() {
        final String adminUserId = "admin.user";
        gaeSimulation.loginAsAdmin(adminUserId);

        String error;
        AdminExceptionTestAction action;

        ______TS("test for AssertionError");
        error = AssertionError.class.getSimpleName();
        action = getAction(Const.ParamsNames.ERROR, error);
        try {
            action.executeAndPostProcess();
            fail("Expected AssertionError to be thrown");
        } catch (AssertionError ae) {
            assertEquals(ae.getMessage(), "AssertionError Testing");
        }

        ______TS("test for EntityDoesNotExistException");
        error = EntityDoesNotExistException.class.getSimpleName();
        action = getAction(Const.ParamsNames.ERROR, error);
        try {
            action.executeAndPostProcess();
            fail("Expected EntityNotFoundException to be thrown");
        } catch (teammates.common.exception.EntityNotFoundException enfe) {
            assertEquals(enfe.getMessage(), "EntityDoesNotExistException Testing");
        }

        ______TS("test for UnauthorizedAccessException");
        error = UnauthorizedAccessException.class.getSimpleName();
        action = getAction(Const.ParamsNames.ERROR, error);
        try {
            action.executeAndPostProcess();
            fail("Expected UnauthorizedAccessException to be thrown");
        } catch (UnauthorizedAccessException uae) {
            // Successful catch
        }

        ______TS("test for NullPointerException");
        error = NullPointerException.class.getSimpleName();
        action = getAction(Const.ParamsNames.ERROR, error);
        try {
            action.executeAndPostProcess();
            fail("Expected NullPointerException to be thrown");
        } catch (NullPointerException npe) {
            // Successful catch
        }

        ______TS("test for DeadlineExceededException");
        error = DeadlineExceededException.class.getSimpleName();
        action = getAction(Const.ParamsNames.ERROR, error);
        try {
            action.executeAndPostProcess();
            fail("Expected DeadlineExceededException to be thrown");
        } catch (DeadlineExceededException dlee) {
            // Successful catch
        }

        ______TS("test for NullPostParameterException");
        error = NullPostParameterException.class.getSimpleName();
        action = getAction(Const.ParamsNames.ERROR, error);
        try {
            action.executeAndPostProcess();
            fail("Expected NullPostParameterException to be thrown");
        } catch (NullPostParameterException nppe) {
            assertEquals(nppe.getMessage(), "test null post param exception");
        }
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
