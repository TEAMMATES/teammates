package teammates.ui.request;

import org.testng.annotations.Test;

import teammates.test.BaseTestCase;

/**
 * SUT: {@link AccountRequestRejectionRequest}.
 */
public class AccountRequestRejectionRequestTest extends BaseTestCase {

    private static final String TYPICAL_TITLE = "We are Unable to Create an Account for you";
    private static final String TYPICAL_BODY = new StringBuilder()
            .append("<p>Hi, Example</p>\n")
            .append("<p>Thanks for your interest in using TEAMMATES. ")
            .append("We are unable to create a TEAMMATES instructor account for you.</p>\n\n")
            .append("<p>\n")
            .append("  <strong>Reason:</strong> The email address you provided ")
            .append("is not an 'official' email address provided by your institution.<br />\n")
            .append("  <strong>Remedy:</strong> ")
            .append("Please re-submit an account request with your 'official' institution email address.\n")
            .append("</p>\n\n")
            .append("<p>If you need further clarification or would like to appeal this decision, ")
            .append("please feel free to contact us at teammates@comp.nus.edu.sg.</p>\n")
            .append("<p>Regards,<br />TEAMMATES Team.</p>\n")
            .toString();

    @Test
    public void testValidate_withNonNullBodyAndNonNullTitle_shouldPass() throws Exception {
        AccountRequestRejectionRequest request = new AccountRequestRejectionRequest(TYPICAL_TITLE, TYPICAL_BODY);
        request.validate();
    }

    @Test
    public void testValidate_withNullBodyAndNullTitle_shouldPass() throws Exception {
        AccountRequestRejectionRequest request = new AccountRequestRejectionRequest(null, null);
        request.validate();
    }

    @Test
    public void testValidate_withNonNullBodyAndNullTitle_shouldFail() {
        AccountRequestRejectionRequest request = new AccountRequestRejectionRequest(null, TYPICAL_BODY);
        assertThrows(InvalidHttpRequestBodyException.class, request::validate);
    }

    @Test
    public void testValidate_withNullBodyAndNonNullTitle_shouldFail() {
        AccountRequestRejectionRequest request = new AccountRequestRejectionRequest(TYPICAL_TITLE, null);
        assertThrows(InvalidHttpRequestBodyException.class, request::validate);
    }
}
