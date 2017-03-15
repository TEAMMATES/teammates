package teammates.test.cases.action;

import org.testng.annotations.Test;
import teammates.common.util.Const;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.AdminEmailComposeSaveAction;
import teammates.ui.controller.ShowPageResult;
import teammates.ui.pagedata.AdminEmailComposePageData;

public class AdminEmailComposeSaveActionTest extends BaseActionTest {

    @Override
    protected void prepareTestData() {
        dataBundle = loadDataBundle("/AdminEmailComposePageTest.json");
        removeAndRestoreDataBundle(dataBundle);
    }

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.ADMIN_EMAIL_COMPOSE_SAVE;
    }

    @Override
    protected AdminEmailComposeSaveAction getAction(String... params) {
        return (AdminEmailComposeSaveAction) gaeSimulation.getActionObject(getActionUri(), params);
    }

    @Override
    @Test
    public void testExecuteAndPostProcess() throws Exception {
        final String adminUserId = "admin.user";
        gaeSimulation.loginAsAdmin(adminUserId);

        ______TS("case: save new email success");
        AdminEmailComposeSaveAction action =
                getAction(Const.ParamsNames.ADMIN_EMAIL_CONTENT, "<p>Email Content</p>",
                          Const.ParamsNames.ADMIN_EMAIL_SUBJECT, "Email Subject",
                          Const.ParamsNames.ADMIN_EMAIL_ADDRESS_RECEIVERS, "test@example.tmt");
        ShowPageResult pageResult = getShowPageResult(action);
        assertEquals(Const.ViewURIs.ADMIN_EMAIL + "?error=false&user=admin.user",
                pageResult.getDestinationWithParams());

        String expectedLogSegment = Const.StatusMessages.EMAIL_DRAFT_SAVED + ": <br>"
                + "Subject: Email Subject";
        AssertHelper.assertContains(expectedLogSegment, action.getLogMessage());

        assertEquals(Const.StatusMessages.EMAIL_DRAFT_SAVED, pageResult.getStatusMessage());

        AdminEmailComposePageData data = (AdminEmailComposePageData) pageResult.data;
        assertNull(data.emailToEdit);

        ______TS("case: save new email failure: invalid subject");
        String content = "<p>Email Content</p>";
        String subject = "!Not starting with alphanumeric";
        String receiver = "test@example.tmt";
        action = getAction(Const.ParamsNames.ADMIN_EMAIL_CONTENT, content,
                           Const.ParamsNames.ADMIN_EMAIL_SUBJECT, subject,
                           Const.ParamsNames.ADMIN_EMAIL_ADDRESS_RECEIVERS, receiver);
        pageResult = getShowPageResult(action);
        assertEquals(Const.ViewURIs.ADMIN_EMAIL + "?error=true&user=admin.user",
                pageResult.getDestinationWithParams());

        expectedLogSegment = Const.ACTION_RESULT_FAILURE;
        AssertHelper.assertContains(expectedLogSegment, action.getLogMessage());

        String expectedStatus =
                "\"!Not starting with alphanumeric\" is not acceptable to TEAMMATES as a/an email subject";
        AssertHelper.assertContains(expectedStatus, pageResult.getStatusMessage());

        data = (AdminEmailComposePageData) pageResult.data;
        assertEquals(subject, data.emailToEdit.subject);

        ______TS("case: edit existing email");


        ______TS("case: edit non-existing email");

    }
}
