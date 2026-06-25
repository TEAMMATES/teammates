package teammates.ui.request;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountVerificationRequestRejectionType;
import teammates.test.BaseTestCase;
import teammates.ui.exception.InvalidHttpRequestBodyException;

/**
 * SUT: {@link AccountVerificationRequestRejectionRequest}.
 */
public class AccountVerificationRequestRejectionRequestTest extends BaseTestCase {

    @Test
    public void testValidate_withRejectionType_shouldPass() throws Exception {
        AccountVerificationRequestRejectionRequest request =
                new AccountVerificationRequestRejectionRequest(AccountVerificationRequestRejectionType.OTHERS, null);
        request.validate();
    }

    @Test
    public void testValidate_withRejectionTypeAndAdditionalComments_shouldPass() throws Exception {
        AccountVerificationRequestRejectionRequest request =
                new AccountVerificationRequestRejectionRequest(
                        AccountVerificationRequestRejectionType.NOT_OFFICIAL_EMAIL, "Please use your institution email.");
        request.validate();
    }

    @Test
    public void testValidate_withNullRejectionType_shouldFail() {
        AccountVerificationRequestRejectionRequest request =
                new AccountVerificationRequestRejectionRequest(null, null);
        assertThrows(InvalidHttpRequestBodyException.class, request::validate);
    }
}
