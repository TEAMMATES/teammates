package teammates.test.cases.action;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.ui.controller.AdminAccountDetailsPageAction;
import teammates.ui.controller.ShowPageResult;
import teammates.ui.pagedata.AdminAccountDetailsPageData;

/**
 * SUT: {@link AdminAccountDetailsPageAction}.
 */
public class AdminAccountDetailsPageActionTest extends BaseActionTest {

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.ADMIN_ACCOUNT_DETAILS_PAGE;
    }

    @Override
    @Test
    public void testExecuteAndPostProcess() {

        ______TS("case: view instructor account details");

        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_ID, instructor1OfCourse1.googleId
        };

        final String adminUserId = "admin.user";
        gaeSimulation.loginAsAdmin(adminUserId);

        AdminAccountDetailsPageAction action = getAction(submissionParams);
        ShowPageResult result = getShowPageResult(action);

        assertEquals("", result.getStatusMessage());
        assertEquals(
                getPageResultDestination(Const.ViewURIs.ADMIN_ACCOUNT_DETAILS, false, adminUserId),
                result.getDestinationWithParams());
        assertFalse(result.isError);

        AdminAccountDetailsPageData data = (AdminAccountDetailsPageData) result.data;
        assertEquals(instructor1OfCourse1.googleId, data.getAccountInformation().googleId);

    }

    @Override
    protected AdminAccountDetailsPageAction getAction(String... params) {
        return (AdminAccountDetailsPageAction) gaeSimulation.getActionObject(getActionUri(), params);
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        String[] submissionParams = new String[] {};
        verifyOnlyAdminsCanAccess(submissionParams);
    }

}
