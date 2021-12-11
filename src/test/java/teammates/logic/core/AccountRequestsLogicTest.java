package teammates.logic.core;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.AccountRequestAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.FieldValidator;
import teammates.test.AssertHelper;

/**
 * SUT: {@link AccountRequestsLogic}.
 */
public class AccountRequestsLogicTest extends BaseLogicTest {

    private final AccountRequestsLogic accountRequestsLogic = AccountRequestsLogic.inst();

    @Override
    protected void prepareTestData() {
        // test data is refreshed before each test case
    }

    @BeforeMethod
    public void refreshTestData() {
        dataBundle = getTypicalDataBundle();
        removeAndRestoreTypicalDataBundle();
    }

    @Test
    public void testCreateOrUpdateAccountRequest() throws Exception {

        ______TS("typical success case");

        AccountRequestAttributes accountRequest = AccountRequestAttributes
                .builder("valid@test.com", "TEAMMATES Test Institute 1")
                .withName("Test account Name")
                .build();

        AccountRequestAttributes createdAccountRequest =
                accountRequestsLogic.createOrUpdateAccountRequest(accountRequest);
        verifyPresentInDatabase(createdAccountRequest);

        assertEquals(accountRequest.getEmail(), createdAccountRequest.getEmail());
        assertEquals(accountRequest.getName(), createdAccountRequest.getName());
        assertEquals(accountRequest.getInstitute(), createdAccountRequest.getInstitute());
        assertNotNull(createdAccountRequest.getRegistrationKey());
        assertNotNull(createdAccountRequest.getCreatedAt());

        ______TS("duplicate account request, account request updated");

        AccountRequestAttributes duplicateAccount = AccountRequestAttributes
                .builder("valid@test.com", "TEAMMATES Test Institute 2")
                .withName("Test account Name 2")
                .build();

        duplicateAccount = accountRequestsLogic.createOrUpdateAccountRequest(duplicateAccount);
        verifyPresentInDatabase(duplicateAccount);

        ______TS("failure case: invalid parameter");

        AccountRequestAttributes invalidAccountRequest = AccountRequestAttributes
                .builder("invalid email", "TEAMMATES Test Institute 1")
                .withName("Test account Name")
                .build();

        InvalidParametersException ipe = assertThrows(InvalidParametersException.class,
                () -> accountRequestsLogic.createOrUpdateAccountRequest(invalidAccountRequest));
        AssertHelper.assertContains(
                getPopulatedErrorMessage(
                        FieldValidator.EMAIL_ERROR_MESSAGE, "invalid email",
                        FieldValidator.EMAIL_FIELD_NAME, FieldValidator.REASON_INCORRECT_FORMAT,
                        FieldValidator.EMAIL_MAX_LENGTH),
                ipe.getMessage());

        ______TS("failure: null parameter");
        assertThrows(AssertionError.class,
                () -> accountRequestsLogic.createOrUpdateAccountRequest(null));
    }

    @Test
    public void testDeleteAccountRequest() throws Exception {
        AccountRequestAttributes a = dataBundle.accountRequests.get("accountRequest1");

        ______TS("silent deletion of non-existent account request");

        accountRequestsLogic.deleteAccountRequest("not_exist", "not_exist");

        ______TS("typical success case");

        verifyPresentInDatabase(a);

        accountRequestsLogic.deleteAccountRequest(a.getEmail(), a.getInstitute());

        verifyAbsentInDatabase(a);

        ______TS("silent deletion of same account request");

        accountRequestsLogic.deleteAccountRequest(a.getEmail(), a.getInstitute());

        ______TS("failure null parameter");

        assertThrows(AssertionError.class,
                () -> accountRequestsLogic.deleteAccountRequest(null, null));
    }

    @Test
    public void testGetAccountRequestForRegistrationKey() throws Exception {
        AccountRequestAttributes a =
                accountRequestsLogic.getAccountRequest("typical@gmail.tmt", "TEAMMATES Test Institute 1");

        ______TS("typical success case");

        AccountRequestAttributes accountRequestAttributes =
                accountRequestsLogic.getAccountRequestForRegistrationKey(a.getRegistrationKey());
        assertEquals(a, accountRequestAttributes);

        ______TS("account request not found");

        assertThrows(EntityDoesNotExistException.class,
                () -> accountRequestsLogic.getAccountRequestForRegistrationKey("not-found"));

        ______TS("failure null parameter");

        assertThrows(AssertionError.class,
                () -> accountRequestsLogic.getAccountRequestForRegistrationKey(null));
    }

    @Test
    public void testGetAccountRequest() throws Exception {
        AccountRequestAttributes a = dataBundle.accountRequests.get("accountRequest1");

        ______TS("typical success case");

        AccountRequestAttributes accountRequestAttributes =
                accountRequestsLogic.getAccountRequest(a.getEmail(), a.getInstitute());
        assertEquals(a, accountRequestAttributes);

        ______TS("account request not found");

        assertThrows(EntityDoesNotExistException.class,
                () -> accountRequestsLogic.getAccountRequest("not-found@test.com", "not-found"));

        ______TS("failure null parameter");

        assertThrows(AssertionError.class,
                () -> accountRequestsLogic.getAccountRequest(null, null));
    }

}
