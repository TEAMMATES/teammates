package teammates.test.cases.action;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.logic.core.AccountsLogic;
import teammates.ui.controller.AdminAccountDeleteAction;
import teammates.ui.controller.RedirectResult;

/**
 * SUT: {@link AdminAccountDeleteAction}.
 */
public class AdminAccountDeletePageActionTest extends BaseActionTest {

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.ADMIN_ACCOUNT_DELETE;
    }

    @Override
    @Test
    public void testExecuteAndPostProcess() {

        ______TS("success: delete entire account");

        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_ID, instructor1OfCourse1.googleId,
                "account", "true"
        };

        final String adminUserId = "admin.user";
        gaeSimulation.loginAsAdmin(adminUserId);

        AdminAccountDeleteAction deleteAction = getAction(submissionParams);
        RedirectResult result = getRedirectResult(deleteAction);

        assertNull(AccountsLogic.inst().getAccount(instructor1OfCourse1.googleId));
        assertEquals(Const.StatusMessages.INSTRUCTOR_ACCOUNT_DELETED, result.getStatusMessage());
        assertEquals(
                getPageResultDestination(Const.ActionURIs.ADMIN_ACCOUNT_MANAGEMENT_PAGE, false, adminUserId),
                result.getDestinationWithParams());

    }

    @Override
    protected AdminAccountDeleteAction getAction(String... params) {
        return (AdminAccountDeleteAction) gaeSimulation.getActionObject(getActionUri(), params);
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        String[] submissionParams = new String[] {};
        verifyOnlyAdminsCanAccess(submissionParams);
    }
}
