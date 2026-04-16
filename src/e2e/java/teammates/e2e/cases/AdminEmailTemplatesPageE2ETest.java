package teammates.e2e.cases;

import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.AdminEmailTemplatesPage;
import teammates.ui.output.EmailTemplateData;
import teammates.ui.webapi.ConfigurableEmailTemplate;

/**
 * SUT: {@link Const.WebPageURIs#ADMIN_EMAIL_TEMPLATES_PAGE}.
 */
public class AdminEmailTemplatesPageE2ETest extends BaseE2ETestCase {

    // Template chosen because it has a well-known required placeholder (${joinUrl}).
    private static final String TEST_TEMPLATE_KEY = "NEW_INSTRUCTOR_ACCOUNT_WELCOME";

    // A valid custom body that satisfies the ${joinUrl} requirement.
    private static final String CUSTOM_SUBJECT = "E2E Test: Welcome, custom subject";
    // @checkstyle.ignore next 3 lines : template literal syntax in test string
    private static final String CUSTOM_BODY =
            "<p>Please click <a href=\"${joinUrl}\">here</a> to activate your account.</p>";

    // A body that deliberately omits ${joinUrl}, used to verify placeholder validation.
    private static final String BODY_MISSING_PLACEHOLDER =
            "<p>Welcome to TEAMMATES! Your account has been created.</p>";

    @Override
    protected void prepareTestData() {
        // Email templates are global entities — no per-test data bundle is required.
        // Reset the test template to its built-in default to guarantee a clean starting state,
        // regardless of what any previous test run may have left behind.
        BACKDOOR.resetEmailTemplate(TEST_TEMPLATE_KEY);
    }

    @Test
    @Override
    public void testAll() {
        AppUrl url = createFrontendUrl(Const.WebPageURIs.ADMIN_EMAIL_TEMPLATES_PAGE);
        AdminEmailTemplatesPage emailTemplatesPage = loginAdminToPage(url, AdminEmailTemplatesPage.class);

        ______TS("default template loads with no custom badge");
        emailTemplatesPage.selectTemplate(TEST_TEMPLATE_KEY);
        assertFalse(emailTemplatesPage.isCustomBadgeVisible());
        assertTrue(emailTemplatesPage.isDefaultBadgeVisible());
        assertEquals(
                ConfigurableEmailTemplate.NEW_INSTRUCTOR_ACCOUNT_WELCOME.getDefaultSubject(),
                emailTemplatesPage.getSubject());

        ______TS("save valid custom template");
        emailTemplatesPage.setSubject(CUSTOM_SUBJECT);
        emailTemplatesPage.setBody(CUSTOM_BODY);
        emailTemplatesPage.saveTemplate();
        emailTemplatesPage.verifyStatusMessage("Email template saved successfully.");
        assertTrue(emailTemplatesPage.isCustomBadgeVisible());
        assertFalse(emailTemplatesPage.isDefaultBadgeVisible());

        // Verify the custom record was actually persisted in the database.
        EmailTemplateData persisted = BACKDOOR.getEmailTemplateData(TEST_TEMPLATE_KEY);
        assertNotNull(persisted);
        assertTrue(persisted.getIsCustomized());
        assertEquals(TEST_TEMPLATE_KEY, persisted.getTemplateKey());

        ______TS("save rejected when required placeholder is missing from body");
        emailTemplatesPage.setBody(BODY_MISSING_PLACEHOLDER);
        emailTemplatesPage.saveTemplate();
        emailTemplatesPage.verifyStatusMessage(
                "Email body is missing required placeholder(s): [${joinUrl}]");
        // The failed save must not change the persisted custom state.
        assertTrue(emailTemplatesPage.isCustomBadgeVisible());

        ______TS("revert to default clears custom record and restores built-in content");
        emailTemplatesPage.revertToDefault();
        emailTemplatesPage.verifyStatusMessage("Email template reverted to default.");
        assertFalse(emailTemplatesPage.isCustomBadgeVisible());
        assertTrue(emailTemplatesPage.isDefaultBadgeVisible());
        assertEquals(
                ConfigurableEmailTemplate.NEW_INSTRUCTOR_ACCOUNT_WELCOME.getDefaultSubject(),
                emailTemplatesPage.getSubject());

        // Verify the database no longer holds a custom record for this key.
        EmailTemplateData reverted = BACKDOOR.getEmailTemplateData(TEST_TEMPLATE_KEY);
        assertNotNull(reverted);
        assertFalse(reverted.getIsCustomized());
    }

    @AfterClass
    public void classTeardown() {
        // Safety net: reset the template even if the test failed before the revert step.
        BACKDOOR.resetEmailTemplate(TEST_TEMPLATE_KEY);
    }
}
