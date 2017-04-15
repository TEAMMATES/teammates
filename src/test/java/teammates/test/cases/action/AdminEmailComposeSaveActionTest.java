package teammates.test.cases.action;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.AdminEmailAttributes;
import teammates.common.util.Const;
import teammates.logic.core.AdminEmailsLogic;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.AdminEmailComposeSaveAction;
import teammates.ui.controller.ShowPageResult;
import teammates.ui.pagedata.AdminEmailComposePageData;

/**
 * SUT: {@link AdminEmailComposeSaveAction}.
 */
public class AdminEmailComposeSaveActionTest extends BaseActionTest {

    private AdminEmailsLogic adminEmailsLogic = AdminEmailsLogic.inst();

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

        ______TS("save new email : typical values given : success");
        AdminEmailComposeSaveAction action =
                getAction(
                        Const.ParamsNames.ADMIN_EMAIL_CONTENT, "<p>Email Content</p>",
                        Const.ParamsNames.ADMIN_EMAIL_SUBJECT, "Email Subject",
                        Const.ParamsNames.ADMIN_EMAIL_ADDRESS_RECEIVERS, "test@example.tmt");
        ShowPageResult pageResult = getShowPageResult(action);
        assertEquals(
                Const.ViewURIs.ADMIN_EMAIL + "?error=false&user=admin.user", pageResult.getDestinationWithParams());

        String expectedLogSegment = Const.StatusMessages.EMAIL_DRAFT_SAVED + ": <br>Subject: Email Subject";
        AssertHelper.assertContains(expectedLogSegment, action.getLogMessage());

        assertEquals(Const.StatusMessages.EMAIL_DRAFT_SAVED, pageResult.getStatusMessage());

        AdminEmailComposePageData data = (AdminEmailComposePageData) pageResult.data;
        assertNull(data.emailToEdit);

        ______TS("save new email : invalid subject : failure");
        String content = "<p>Email Content</p>";
        String subject = "!Not starting with alphanumeric";
        String receiver = "test@example.tmt";
        action = getAction(
                Const.ParamsNames.ADMIN_EMAIL_CONTENT, content,
                Const.ParamsNames.ADMIN_EMAIL_SUBJECT, subject,
                Const.ParamsNames.ADMIN_EMAIL_ADDRESS_RECEIVERS, receiver);
        pageResult = getShowPageResult(action);
        assertEquals(
                Const.ViewURIs.ADMIN_EMAIL + "?error=true&user=admin.user", pageResult.getDestinationWithParams());

        expectedLogSegment = Const.ACTION_RESULT_FAILURE;
        AssertHelper.assertContains(expectedLogSegment, action.getLogMessage());

        String expectedStatus =
                "\"!Not starting with alphanumeric\" is not acceptable to TEAMMATES as a/an email subject";
        AssertHelper.assertContains(expectedStatus, pageResult.getStatusMessage());

        data = (AdminEmailComposePageData) pageResult.data;
        assertEquals(subject, data.emailToEdit.subject);

        ______TS("save new email : invalid content : failure");
        content = "";
        subject = "valid subject";
        receiver = "test@example.tmt";
        action = getAction(
                Const.ParamsNames.ADMIN_EMAIL_CONTENT, content,
                Const.ParamsNames.ADMIN_EMAIL_SUBJECT, subject,
                Const.ParamsNames.ADMIN_EMAIL_ADDRESS_RECEIVERS, receiver);
        pageResult = getShowPageResult(action);
        assertEquals(
                Const.ViewURIs.ADMIN_EMAIL + "?error=true&user=admin.user", pageResult.getDestinationWithParams());

        expectedLogSegment = Const.ACTION_RESULT_FAILURE;
        AssertHelper.assertContains(expectedLogSegment, action.getLogMessage());

        expectedStatus = "email content should not be empty.";
        AssertHelper.assertContains(expectedStatus, pageResult.getStatusMessage());

        data = (AdminEmailComposePageData) pageResult.data;
        assertEquals(subject, data.emailToEdit.subject);

        ______TS("save existing email : typical values given : success");
        AdminEmailAttributes emailData = dataBundle.adminEmails.get("adminEmail1");
        AdminEmailAttributes email = adminEmailsLogic.getAdminEmailBySubject(emailData.getSubject());
        String emailId = email.emailId;
        content = "valid content";
        subject = "valid subject <b>To check sanitization</b>";
        receiver = "test@example.tmt";
        action = getAction(
                Const.ParamsNames.ADMIN_EMAIL_CONTENT, content,
                Const.ParamsNames.ADMIN_EMAIL_SUBJECT, subject,
                Const.ParamsNames.ADMIN_EMAIL_ADDRESS_RECEIVERS, receiver,
                Const.ParamsNames.ADMIN_EMAIL_ID, emailId);
        pageResult = getShowPageResult(action);
        assertEquals(
                Const.ViewURIs.ADMIN_EMAIL + "?error=false&user=admin.user", pageResult.getDestinationWithParams());

        expectedLogSegment = Const.StatusMessages.EMAIL_DRAFT_SAVED + ": <br>"
                + "Subject: valid subject &lt;b&gt;To check sanitization&lt;&#x2f;b&gt;";
        AssertHelper.assertContains(expectedLogSegment, action.getLogMessage());

        assertEquals(Const.StatusMessages.EMAIL_DRAFT_SAVED, pageResult.getStatusMessage());

        data = (AdminEmailComposePageData) pageResult.data;
        assertNull(data.emailToEdit);

        ______TS("save existing email : invalid subject : failure");
        content = "valid content";
        subject = " ";
        receiver = "test@example.tmt";
        action = getAction(
                Const.ParamsNames.ADMIN_EMAIL_CONTENT, content,
                Const.ParamsNames.ADMIN_EMAIL_SUBJECT, subject,
                Const.ParamsNames.ADMIN_EMAIL_ADDRESS_RECEIVERS, receiver,
                Const.ParamsNames.ADMIN_EMAIL_ID, emailId);
        pageResult = getShowPageResult(action);
        assertEquals(
                Const.ViewURIs.ADMIN_EMAIL + "?error=true&user=admin.user", pageResult.getDestinationWithParams());

        expectedLogSegment = Const.ACTION_RESULT_FAILURE;
        AssertHelper.assertContains(expectedLogSegment, action.getLogMessage());

        expectedStatus = "The provided email subject is not acceptable to TEAMMATES as it contains only whitespace"
                + " or contains extra spaces at the beginning or at the end of the text.";
        AssertHelper.assertContains(expectedStatus, pageResult.getStatusMessage());

        data = (AdminEmailComposePageData) pageResult.data;
        assertEquals(subject, data.emailToEdit.subject);
        assertEquals(email.emailId, data.emailToEdit.emailId);

        ______TS("save existing email : invalid content : failure");
        content = "";
        subject = "valid subject";
        receiver = "test@example.tmt";
        action = getAction(
                Const.ParamsNames.ADMIN_EMAIL_CONTENT, content,
                Const.ParamsNames.ADMIN_EMAIL_SUBJECT, subject,
                Const.ParamsNames.ADMIN_EMAIL_ADDRESS_RECEIVERS, receiver,
                Const.ParamsNames.ADMIN_EMAIL_ID, emailId);
        pageResult = getShowPageResult(action);
        assertEquals(
                Const.ViewURIs.ADMIN_EMAIL + "?error=true&user=admin.user", pageResult.getDestinationWithParams());

        expectedLogSegment = Const.ACTION_RESULT_FAILURE;
        AssertHelper.assertContains(expectedLogSegment, action.getLogMessage());

        expectedStatus = "email content should not be empty.";
        AssertHelper.assertContains(expectedStatus, pageResult.getStatusMessage());

        data = (AdminEmailComposePageData) pageResult.data;
        assertEquals(subject, data.emailToEdit.subject);
        assertEquals(email.emailId, data.emailToEdit.emailId);

        ______TS("save non-existing email : typical values given : success");
        emailId = "nonExisitingId";
        content = "valid content";
        subject = "valid subject <b>To check sanitization</b>";
        receiver = "test@example.tmt";
        action = getAction(
                Const.ParamsNames.ADMIN_EMAIL_CONTENT, content,
                Const.ParamsNames.ADMIN_EMAIL_SUBJECT, subject,
                Const.ParamsNames.ADMIN_EMAIL_ADDRESS_RECEIVERS, receiver,
                Const.ParamsNames.ADMIN_EMAIL_ID, emailId);
        pageResult = getShowPageResult(action);
        assertEquals(
                Const.ViewURIs.ADMIN_EMAIL + "?error=false&user=admin.user", pageResult.getDestinationWithParams());

        expectedLogSegment = Const.StatusMessages.EMAIL_DRAFT_SAVED + ": <br>"
                + "Subject: valid subject &lt;b&gt;To check sanitization&lt;&#x2f;b&gt;";
        AssertHelper.assertContains(expectedLogSegment, action.getLogMessage());

        assertEquals(Const.StatusMessages.EMAIL_DRAFT_SAVED, pageResult.getStatusMessage());

        data = (AdminEmailComposePageData) pageResult.data;
        assertNull(data.emailToEdit);

        ______TS("save non-existing email : invalid subject : failure");
        emailId = "nonExisitingId";
        content = "valid content";
        subject = "";
        receiver = "test@example.tmt";
        action = getAction(
                Const.ParamsNames.ADMIN_EMAIL_CONTENT, content,
                Const.ParamsNames.ADMIN_EMAIL_SUBJECT, subject,
                Const.ParamsNames.ADMIN_EMAIL_ADDRESS_RECEIVERS, receiver,
                Const.ParamsNames.ADMIN_EMAIL_ID, emailId);
        pageResult = getShowPageResult(action);
        assertEquals(
                Const.ViewURIs.ADMIN_EMAIL + "?error=true&user=admin.user", pageResult.getDestinationWithParams());

        expectedLogSegment = Const.ACTION_RESULT_FAILURE;
        AssertHelper.assertContains(expectedLogSegment, action.getLogMessage());

        expectedStatus = "\"\" is not acceptable to TEAMMATES as a/an email subject";
        AssertHelper.assertContains(expectedStatus, pageResult.getStatusMessage());

        data = (AdminEmailComposePageData) pageResult.data;
        assertEquals(subject, data.emailToEdit.subject);

        ______TS("save non-existing email : invalid content : failure");
        emailId = "nonExisitingId";
        content = "";
        subject = "valid subject";
        receiver = "test@example.tmt";
        action = getAction(
                Const.ParamsNames.ADMIN_EMAIL_CONTENT, content,
                Const.ParamsNames.ADMIN_EMAIL_SUBJECT, subject,
                Const.ParamsNames.ADMIN_EMAIL_ADDRESS_RECEIVERS, receiver,
                Const.ParamsNames.ADMIN_EMAIL_ID, emailId);
        pageResult = getShowPageResult(action);
        assertEquals(
                Const.ViewURIs.ADMIN_EMAIL + "?error=true&user=admin.user", pageResult.getDestinationWithParams());

        expectedLogSegment = Const.ACTION_RESULT_FAILURE;
        AssertHelper.assertContains(expectedLogSegment, action.getLogMessage());

        expectedStatus = "email content should not be empty.";
        AssertHelper.assertContains(expectedStatus, pageResult.getStatusMessage());

        data = (AdminEmailComposePageData) pageResult.data;
        assertEquals(subject, data.emailToEdit.subject);
    }
}
