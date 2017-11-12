package teammates.test.cases.action;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.AdminEmailAttributes;
import teammates.common.util.Const;
import teammates.logic.core.AdminEmailsLogic;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.AdminEmailComposePageAction;
import teammates.ui.controller.ShowPageResult;
import teammates.ui.pagedata.AdminEmailComposePageData;

/**
 * SUT: {@link AdminEmailComposePageAction}.
 */
public class AdminEmailComposePageActionTest extends BaseActionTest {

    private AdminEmailsLogic adminEmailsLogic = AdminEmailsLogic.inst();

    @Override
    protected void prepareTestData() {
        super.prepareTestData();
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

        ______TS("compose new email : typical values given : success");
        AdminEmailComposePageAction action = getAction();
        ShowPageResult pageResult = getShowPageResult(action);
        assertEquals(
                getPageResultDestination(Const.ViewURIs.ADMIN_EMAIL, false, adminUserId),
                pageResult.getDestinationWithParams());

        String normalLogSegment = "adminEmailComposePage Page Load";
        AssertHelper.assertContains(normalLogSegment, action.getLogMessage());

        assertEquals("", pageResult.getStatusMessage());

        AdminEmailComposePageData data = (AdminEmailComposePageData) pageResult.data;
        assertNull(data.emailToEdit);

        ______TS("edit existing email : typical values given : success");
        // retrieve email id from logic
        AdminEmailAttributes emailData = dataBundle.adminEmails.get("adminEmail1");
        AdminEmailAttributes email = adminEmailsLogic.getAdminEmailBySubject(emailData.subject);
        action = getAction(Const.ParamsNames.ADMIN_EMAIL_ID, email.emailId);
        pageResult = getShowPageResult(action);
        assertEquals(
                getPageResultDestination(Const.ViewURIs.ADMIN_EMAIL, false, "admin.user"),
                pageResult.getDestinationWithParams());

        String expectedLogSegment = normalLogSegment + " : Edit Email [Admin Email 1 &lt;b&gt;bold tags&lt;&#x2f;b&gt;]";
        AssertHelper.assertContains(expectedLogSegment, action.getLogMessage());

        assertEquals("", pageResult.getStatusMessage());

        data = (AdminEmailComposePageData) pageResult.data;
        assertNotNull(data.emailToEdit);

        ______TS("edit existing email : email does not exist : failure");
        String emailId = "nonexistingEmailId";
        action = getAction(Const.ParamsNames.ADMIN_EMAIL_ID, emailId);
        pageResult = getShowPageResult(action);
        assertEquals(
                getPageResultDestination(Const.ViewURIs.ADMIN_EMAIL, true, "admin.user"),
                pageResult.getDestinationWithParams());

        expectedLogSegment = normalLogSegment + " : " + Const.StatusMessages.EMAIL_NOT_FOUND;
        AssertHelper.assertContains(expectedLogSegment, action.getLogMessage());

        assertEquals(Const.StatusMessages.EMAIL_NOT_FOUND, pageResult.getStatusMessage());

        data = (AdminEmailComposePageData) pageResult.data;
        assertNull(data.emailToEdit);
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        String[] submissionParams = new String[] {};
        verifyOnlyAdminsCanAccess(submissionParams);
    }
}
