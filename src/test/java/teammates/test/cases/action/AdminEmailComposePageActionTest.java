package teammates.test.cases.action;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.AdminEmailAttributes;
import teammates.common.util.Const;
import teammates.common.util.SanitizationHelper;
import teammates.logic.core.AdminEmailsLogic;
import teammates.storage.api.AdminEmailsDb;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.AdminEmailComposePageAction;
import teammates.ui.controller.ShowPageResult;
import teammates.ui.pagedata.AdminEmailComposePageData;

public class AdminEmailComposePageActionTest extends BaseActionTest{

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.ADMIN_EMAIL_COMPOSE_PAGE;
    }

    @Override
    protected AdminEmailComposePageAction getAction(String... params) {
        return (AdminEmailComposePageAction) gaeSimulation.getActionObject(getActionUri(), params);
    }

    @Override
    @Test
    public void testExecuteAndPostProcess() throws Exception {

        final String adminUserId = "admin.user";
        gaeSimulation.loginAsAdmin(adminUserId);

        ______TS("case: compose new email");
        AdminEmailComposePageAction action = getAction();
        ShowPageResult pageResult = getShowPageResult(action);
        assertEquals(Const.ViewURIs.ADMIN_EMAIL + "?error=false&user=admin.user",
                     pageResult.getDestinationWithParams());

        String normalLogSegment = "adminEmailComposePage Page Load";
        AssertHelper.assertContains(normalLogSegment, action.getLogMessage());

        assertEquals("", pageResult.getStatusMessage());

        AdminEmailComposePageData data = (AdminEmailComposePageData) pageResult.data;
        assertNull(data.emailToEdit);

    }
}
