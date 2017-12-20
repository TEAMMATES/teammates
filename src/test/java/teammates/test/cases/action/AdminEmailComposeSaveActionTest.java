package teammates.test.cases.action;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.AdminEmailAttributes;
import teammates.common.util.Const;
import teammates.common.util.SanitizationHelper;
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
        super.prepareTestData();
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
        String subject = "New Email Subject";
        String content = "<p>Email Content</p>";
        String receiver = "test@example.tmt";
        AdminEmailComposeSaveAction action =
                getAction(
                        Const.ParamsNames.ADMIN_EMAIL_CONTENT, content,
                        Const.ParamsNames.ADMIN_EMAIL_SUBJECT, subject,
                        Const.ParamsNames.ADMIN_EMAIL_ADDRESS_RECEIVERS, receiver);
        ShowPageResult pageResult = getShowPageResult(action);
        assertEquals(
                getPageResultDestination(Const.ViewURIs.ADMIN_EMAIL, false, adminUserId),
                pageResult.getDestinationWithParams());

        String expectedLogSegment = Const.StatusMessages.EMAIL_DRAFT_SAVED + ": <br>Subject: New Email Subject";
        AssertHelper.assertContains(expectedLogSegment, action.getLogMessage());

        assertEquals(Const.StatusMessages.EMAIL_DRAFT_SAVED, pageResult.getStatusMessage());

        AdminEmailComposePageData data = (AdminEmailComposePageData) pageResult.data;
        assertNull(data.emailToEdit);

        AdminEmailAttributes savedEmail = adminEmailsLogic.getAdminEmailBySubject(subject);
        assertNotNull("Email should be saved and should exists.", savedEmail);
        assertEquals(SanitizationHelper.sanitizeForRichText(content), savedEmail.getContentValue());
        assertEquals(receiver, String.join(", ", savedEmail.getAddressReceiver().toArray(new String[0])));

        ______TS("save new email : invalid subject : failure");
        content = "<p>Email Content</p>";
        subject = "!Not starting with alphanumeric";
        receiver = "test@example.tmt";
        action = getAction(
                Const.ParamsNames.ADMIN_EMAIL_CONTENT, content,
                Const.ParamsNames.ADMIN_EMAIL_SUBJECT, subject,
                Const.ParamsNames.ADMIN_EMAIL_ADDRESS_RECEIVERS, receiver);
        pageResult = getShowPageResult(action);
        assertEquals(
                getPageResultDestination(Const.ViewURIs.ADMIN_EMAIL, true, adminUserId),
                pageResult.getDestinationWithParams());

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
                getPageResultDestination(Const.ViewURIs.ADMIN_EMAIL, true, adminUserId),
                pageResult.getDestinationWithParams());

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
        subject = "valid existing email subject";
        receiver = "test@example.tmt, test2@example.tmt";
        action = getAction(
                Const.ParamsNames.ADMIN_EMAIL_CONTENT, content,
                Const.ParamsNames.ADMIN_EMAIL_SUBJECT, subject,
                Const.ParamsNames.ADMIN_EMAIL_ADDRESS_RECEIVERS, receiver,
                Const.ParamsNames.ADMIN_EMAIL_ID, emailId);
        pageResult = getShowPageResult(action);
        assertEquals(
                getPageResultDestination(Const.ViewURIs.ADMIN_EMAIL, false, adminUserId),
                pageResult.getDestinationWithParams());

        expectedLogSegment = Const.StatusMessages.EMAIL_DRAFT_SAVED + ": <br>"
                + "Subject: valid existing email subject";
        AssertHelper.assertContains(expectedLogSegment, action.getLogMessage());

        assertEquals(Const.StatusMessages.EMAIL_DRAFT_SAVED, pageResult.getStatusMessage());

        data = (AdminEmailComposePageData) pageResult.data;
        assertNull(data.emailToEdit);

        savedEmail = adminEmailsLogic.getAdminEmailBySubject(subject);
        assertNotNull("Email should be saved and should exists.", savedEmail);
        assertEquals(SanitizationHelper.sanitizeForRichText(content), savedEmail.getContentValue());
        assertEquals(receiver, String.join(", ", savedEmail.getAddressReceiver().toArray(new String[0])));

        ______TS("save existing email : values require sanitization : success");
        emailId = email.emailId;
        content = "<p onclick=\"alert('hello');\">contents</p> </div> unclosed tags <script>alert(\"hello\");</script>";
        subject = "valid existing email subject <b>To check sanitization</b>";
        receiver = "test@example.tmt, test2@example.tmt, test3@example.tmt";
        action = getAction(
                Const.ParamsNames.ADMIN_EMAIL_CONTENT, content,
                Const.ParamsNames.ADMIN_EMAIL_SUBJECT, subject,
                Const.ParamsNames.ADMIN_EMAIL_ADDRESS_RECEIVERS, receiver,
                Const.ParamsNames.ADMIN_EMAIL_ID, emailId);
        pageResult = getShowPageResult(action);
        assertEquals(
                getPageResultDestination(Const.ViewURIs.ADMIN_EMAIL, false, adminUserId),
                pageResult.getDestinationWithParams());

        expectedLogSegment = Const.StatusMessages.EMAIL_DRAFT_SAVED + ": <br>"
                + "Subject: valid existing email subject &lt;b&gt;To check sanitization&lt;&#x2f;b&gt;";
        AssertHelper.assertContains(expectedLogSegment, action.getLogMessage());

        assertEquals(Const.StatusMessages.EMAIL_DRAFT_SAVED, pageResult.getStatusMessage());

        data = (AdminEmailComposePageData) pageResult.data;
        assertNull(data.emailToEdit);

        savedEmail = adminEmailsLogic.getAdminEmailBySubject(subject);
        assertNotNull("Email should be saved and should exists.", savedEmail);
        assertEquals(SanitizationHelper.sanitizeForRichText(content), savedEmail.getContentValue());
        assertEquals(receiver, String.join(", ", savedEmail.getAddressReceiver().toArray(new String[0])));

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
                getPageResultDestination(Const.ViewURIs.ADMIN_EMAIL, true, adminUserId),
                pageResult.getDestinationWithParams());

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
                getPageResultDestination(Const.ViewURIs.ADMIN_EMAIL, true, adminUserId),
                pageResult.getDestinationWithParams());

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
        subject = "valid non-existing email subject <b>To check sanitization</b>";
        receiver = "test@example.tmt";
        action = getAction(
                Const.ParamsNames.ADMIN_EMAIL_CONTENT, content,
                Const.ParamsNames.ADMIN_EMAIL_SUBJECT, subject,
                Const.ParamsNames.ADMIN_EMAIL_ADDRESS_RECEIVERS, receiver,
                Const.ParamsNames.ADMIN_EMAIL_ID, emailId);
        pageResult = getShowPageResult(action);
        assertEquals(
                getPageResultDestination(Const.ViewURIs.ADMIN_EMAIL, false, adminUserId),
                pageResult.getDestinationWithParams());

        expectedLogSegment = Const.StatusMessages.EMAIL_DRAFT_SAVED + ": <br>"
                + "Subject: valid non-existing email subject &lt;b&gt;To check sanitization&lt;&#x2f;b&gt;";
        AssertHelper.assertContains(expectedLogSegment, action.getLogMessage());

        assertEquals(Const.StatusMessages.EMAIL_DRAFT_SAVED, pageResult.getStatusMessage());

        data = (AdminEmailComposePageData) pageResult.data;
        assertNull(data.emailToEdit);

        savedEmail = adminEmailsLogic.getAdminEmailBySubject(subject);
        assertNotNull("Email should be saved and should exists.", savedEmail);
        assertEquals(SanitizationHelper.sanitizeForRichText(content), savedEmail.getContentValue());
        assertEquals(receiver, String.join(", ", savedEmail.getAddressReceiver().toArray(new String[0])));

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
        assertEquals(getPageResultDestination(Const.ViewURIs.ADMIN_EMAIL, true, "admin.user"),
                pageResult.getDestinationWithParams());

        expectedLogSegment = Const.ACTION_RESULT_FAILURE;
        AssertHelper.assertContains(expectedLogSegment, action.getLogMessage());

        expectedStatus = "The field 'email subject' is empty.";
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
                getPageResultDestination(Const.ViewURIs.ADMIN_EMAIL, true, "admin.user"),
                pageResult.getDestinationWithParams());

        expectedLogSegment = Const.ACTION_RESULT_FAILURE;
        AssertHelper.assertContains(expectedLogSegment, action.getLogMessage());

        expectedStatus = "email content should not be empty.";
        AssertHelper.assertContains(expectedStatus, pageResult.getStatusMessage());

        data = (AdminEmailComposePageData) pageResult.data;
        assertEquals(subject, data.emailToEdit.subject);
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        String[] submissionParams = new String[] {};
        verifyOnlyAdminsCanAccess(submissionParams);
    }
}
