package teammates.test.cases.action;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.ui.controller.AdminHomePageAction;
import teammates.ui.controller.ShowPageResult;
import teammates.ui.pagedata.AdminHomePageData;

/**
 * SUT: {@link AdminHomePageAction}.
 */
public class AdminHomePageActionTest extends BaseActionTest {

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.ADMIN_HOME_PAGE;
    }

    @Override
    @Test
    public void testExecuteAndPostProcess() {

        ______TS("Normal case: starting with a blank adminHome page");
        final String adminUserId = "admin.user";
        gaeSimulation.loginAsAdmin(adminUserId);
        final AdminHomePageAction a = getAction();

        final ShowPageResult result = getShowPageResult(a);
        assertEquals(Const.ViewURIs.ADMIN_HOME, result.destination);
        final AdminHomePageData startingPageData = (AdminHomePageData) result.data;
        assertEquals("", startingPageData.instructorEmail);
        assertEquals("", startingPageData.instructorInstitution);
        assertEquals("", startingPageData.instructorName);
        assertEquals("", result.getStatusMessage());

    }

    @Override
    protected AdminHomePageAction getAction(String... params) {
        return (AdminHomePageAction) gaeSimulation.getActionObject(getActionUri(), params);
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        String[] submissionParams = new String[] {};
        verifyOnlyAdminsCanAccess(submissionParams);
    }

}
