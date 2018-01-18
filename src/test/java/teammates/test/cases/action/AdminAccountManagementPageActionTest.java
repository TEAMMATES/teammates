package teammates.test.cases.action;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.ui.controller.AdminAccountManagementPageAction;
import teammates.ui.controller.ShowPageResult;

/**
 * SUT: {@link AdminAccountManagementPageAction}.
 */
public class AdminAccountManagementPageActionTest extends BaseActionTest {

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.ADMIN_ACCOUNT_MANAGEMENT_PAGE;
    }

    @Override
    @Test
    public void testExecuteAndPostProcess() {

        ______TS("case: view admin acount management page");

        String[] submissionParams = new String[] {};

        final String adminUserId = "admin.user";
        gaeSimulation.loginAsAdmin(adminUserId);

        AdminAccountManagementPageAction action = getAction(submissionParams);
        ShowPageResult result = getShowPageResult(action);

        assertEquals("", result.getStatusMessage());
        assertEquals(
                getPageResultDestination(Const.ViewURIs.ADMIN_ACCOUNT_MANAGEMENT, false, adminUserId),
                result.getDestinationWithParams());
        assertFalse(result.isError);

    }

    @Override
    protected AdminAccountManagementPageAction getAction(String... params) {
        return (AdminAccountManagementPageAction) gaeSimulation.getActionObject(getActionUri(), params);
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        String[] submissionParams = new String[] {};
        verifyOnlyAdminsCanAccess(submissionParams);
    }

}
