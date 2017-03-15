package teammates.test.cases.action;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.AdminEmailAttributes;
import teammates.common.util.Const;
import teammates.common.util.SanitizationHelper;
import teammates.logic.core.AdminEmailsLogic;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.AdminEmailComposePageAction;
import teammates.ui.controller.ShowPageResult;
import teammates.ui.pagedata.AdminEmailComposePageData;

public class AdminEmailComposePageActionTest extends BaseActionTest {

    protected AdminEmailsLogic adminEmailsLogic = AdminEmailsLogic.inst();

    @Override
    protected void prepareTestData() {
        dataBundle = loadDataBundle("/AdminEmailComposePageTest.json");
        removeAndRestoreDataBundle(dataBundle);
    }

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
        assertEquals(
                Const.ViewURIs.ADMIN_EMAIL + "?error=false&user=admin.user", pageResult.getDestinationWithParams());

        String normalLogSegment = "adminEmailComposePage Page Load";
        AssertHelper.assertContains(normalLogSegment, action.getLogMessage());

        assertEquals("", pageResult.getStatusMessage());

        AdminEmailComposePageData data = (AdminEmailComposePageData) pageResult.data;
        assertNull(data.emailToEdit);

        ______TS("case: edit existing email");
        // retrieve email id from logic
        AdminEmailAttributes email = adminEmailsLogic.getAllAdminEmails().get(0);
        action = getAction(Const.ParamsNames.ADMIN_EMAIL_ID, email.emailId);
        pageResult = getShowPageResult(action);
        assertEquals(
                Const.ViewURIs.ADMIN_EMAIL + "?error=false&user=admin.user", pageResult.getDestinationWithParams());

        String expectedLogSegment = normalLogSegment + " : Edit Email ["
                                    + SanitizationHelper.sanitizeForHtml(email.getSubject()) + "]";
        AssertHelper.assertContains(expectedLogSegment, action.getLogMessage());

        assertEquals("", pageResult.getStatusMessage());

        data = (AdminEmailComposePageData) pageResult.data;
        assertNotNull(data.emailToEdit);

        ______TS("case: edit non-existing email");
        String emailId = "nonexistingEmailId";
        action = getAction(Const.ParamsNames.ADMIN_EMAIL_ID, emailId);
        pageResult = getShowPageResult(action);
        assertEquals(
                Const.ViewURIs.ADMIN_EMAIL + "?error=true&user=admin.user", pageResult.getDestinationWithParams());

        expectedLogSegment = normalLogSegment + " : " + Const.StatusMessages.EMAIL_NOT_FOUND;
        AssertHelper.assertContains(expectedLogSegment, action.getLogMessage());

        assertEquals(Const.StatusMessages.EMAIL_NOT_FOUND, pageResult.getStatusMessage());

        data = (AdminEmailComposePageData) pageResult.data;
        assertNull(data.emailToEdit);
    }
}
