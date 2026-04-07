package teammates.ui.request;

import org.testng.annotations.Test;

import teammates.test.BaseTestCase;

/**
 * SUT: {@link EmailTemplateUpdateRequest}.
 */
public class EmailTemplateUpdateRequestTest extends BaseTestCase {

    private static final String TYPICAL_TEMPLATE_KEY = "NEW_INSTRUCTOR_ACCOUNT_WELCOME";
    private static final String TYPICAL_SUBJECT = "Welcome to TEAMMATES";
    private static final String TYPICAL_BODY = "<p>Welcome! Please click the link to join.</p>";

    @Test
    public void testValidate_withValidFields_shouldPass() throws Exception {
        EmailTemplateUpdateRequest request = new EmailTemplateUpdateRequest(
                TYPICAL_TEMPLATE_KEY, TYPICAL_SUBJECT, TYPICAL_BODY, false);
        request.validate();
    }

    @Test
    public void testValidate_withResetToDefault_shouldPass() throws Exception {
        EmailTemplateUpdateRequest request = new EmailTemplateUpdateRequest(
                TYPICAL_TEMPLATE_KEY, null, null, true);
        request.validate();
    }

    @Test
    public void testValidate_withNullTemplateKey_shouldFail() {
        EmailTemplateUpdateRequest request = new EmailTemplateUpdateRequest(
                null, TYPICAL_SUBJECT, TYPICAL_BODY, false);
        assertThrows(InvalidHttpRequestBodyException.class, request::validate);
    }

    @Test
    public void testValidate_withBlankTemplateKey_shouldFail() {
        EmailTemplateUpdateRequest request = new EmailTemplateUpdateRequest(
                "   ", TYPICAL_SUBJECT, TYPICAL_BODY, false);
        assertThrows(InvalidHttpRequestBodyException.class, request::validate);
    }

    @Test
    public void testValidate_withNullSubject_shouldFail() {
        EmailTemplateUpdateRequest request = new EmailTemplateUpdateRequest(
                TYPICAL_TEMPLATE_KEY, null, TYPICAL_BODY, false);
        assertThrows(InvalidHttpRequestBodyException.class, request::validate);
    }

    @Test
    public void testValidate_withBlankSubject_shouldFail() {
        EmailTemplateUpdateRequest request = new EmailTemplateUpdateRequest(
                TYPICAL_TEMPLATE_KEY, "   ", TYPICAL_BODY, false);
        assertThrows(InvalidHttpRequestBodyException.class, request::validate);
    }

    @Test
    public void testValidate_withNullBody_shouldFail() {
        EmailTemplateUpdateRequest request = new EmailTemplateUpdateRequest(
                TYPICAL_TEMPLATE_KEY, TYPICAL_SUBJECT, null, false);
        assertThrows(InvalidHttpRequestBodyException.class, request::validate);
    }

    @Test
    public void testValidate_withBlankBody_shouldFail() {
        EmailTemplateUpdateRequest request = new EmailTemplateUpdateRequest(
                TYPICAL_TEMPLATE_KEY, TYPICAL_SUBJECT, "   ", false);
        assertThrows(InvalidHttpRequestBodyException.class, request::validate);
    }

    @Test
    public void testValidate_withNullTemplateKeyAndResetToDefaultTrue_shouldFail() {
        EmailTemplateUpdateRequest request = new EmailTemplateUpdateRequest(
                null, null, null, true);
        assertThrows(InvalidHttpRequestBodyException.class, request::validate);
    }
}
