package teammates.storage.api;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.AccountRequestAttributes;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.FieldValidator;
import teammates.storage.entity.AccountRequest;
import teammates.test.AssertHelper;
import teammates.test.BaseTestCaseWithLocalDatabaseAccess;

/**
 * SUT: {@link AccountRequestsDb}.
 */
public class AccountRequestsDbTest extends BaseTestCaseWithLocalDatabaseAccess {

    private final AccountRequestsDb accountRequestsDb = AccountRequestsDb.inst();

    @Test
    public void testCreateOrUpdateAccountRequest() throws Exception {

        ______TS("typical success case");

        AccountRequestAttributes accountRequest = AccountRequestAttributes
                .builder("valid@test.com", "TEAMMATES Test Institute 1", "Test account Name")
                .build();

        accountRequest = accountRequestsDb.createOrUpdateAccountRequest(accountRequest);
        verifyPresentInDatabase(accountRequest);

        ______TS("duplicate account request, account request updated");

        AccountRequestAttributes duplicateAccount = AccountRequestAttributes
                .builder("valid@test.com", "TEAMMATES Test Institute 1", "Test account Name 2")
                .build();

        duplicateAccount = accountRequestsDb.createOrUpdateAccountRequest(duplicateAccount);
        verifyPresentInDatabase(duplicateAccount);

        ______TS("failure case: invalid parameter");

        AccountRequestAttributes invalidAccountRequest = AccountRequestAttributes
                .builder("invalid email", "TEAMMATES Test Institute 1", "Test account Name")
                .build();

        InvalidParametersException ipe = assertThrows(InvalidParametersException.class,
                () -> accountRequestsDb.createOrUpdateAccountRequest(invalidAccountRequest));
        AssertHelper.assertContains(
                getPopulatedErrorMessage(
                        FieldValidator.EMAIL_ERROR_MESSAGE, "invalid email",
                        FieldValidator.EMAIL_FIELD_NAME, FieldValidator.REASON_INCORRECT_FORMAT,
                        FieldValidator.EMAIL_MAX_LENGTH),
                ipe.getMessage());

        ______TS("failure: null parameter");
        assertThrows(AssertionError.class,
                () -> accountRequestsDb.createOrUpdateAccountRequest(null));
    }

    @Test
    public void testDeleteAccountRequest() throws Exception {
        AccountRequest accountRequest = new AccountRequest("valid2@test.com",
                        "Test account Name", "TEAMMATES Test Institute 1");
        accountRequest.setRegistrationKey("2-123456");

        accountRequestsDb.saveEntity(accountRequest);

        ______TS("silent deletion of non-existent account request");

        accountRequestsDb.deleteAccountRequest("not_exist", "not_exist");

        ______TS("typical success case");

        verifyPresentInDatabase(AccountRequestAttributes.valueOf(accountRequest));
        accountRequestsDb.deleteAccountRequest("valid2@test.com", "TEAMMATES Test Institute 1");
        verifyAbsentInDatabase(AccountRequestAttributes.valueOf(accountRequest));

        ______TS("silent deletion of same account request");

        accountRequestsDb.deleteAccountRequest("valid2@test.com", "TEAMMATES Test Institute 1");

        ______TS("failure null parameter");

        assertThrows(AssertionError.class,
                () -> accountRequestsDb.deleteAccountRequest(null, null));
    }

    @Test
    public void testGetAccountRequestForRegistrationKey() throws Exception {
        AccountRequest accountRequest = new AccountRequest("valid3@test.com",
                        "Test account Name", "TEAMMATES Test Institute 1");
        accountRequest.setRegistrationKey("3-123456");

        accountRequestsDb.saveEntity(accountRequest);

        ______TS("typical success case");

        AccountRequestAttributes accountRequestAttributes =
                accountRequestsDb.getAccountRequestForRegistrationKey("3-123456");
        assertEquals(AccountRequestAttributes.valueOf(accountRequest), accountRequestAttributes);

        ______TS("account request not found");

        AccountRequestAttributes notFoundRequestAttributes =
                accountRequestsDb.getAccountRequestForRegistrationKey("not-found");
        assertNull(notFoundRequestAttributes);

        ______TS("failure null parameter");

        assertThrows(AssertionError.class,
                () -> accountRequestsDb.getAccountRequestForRegistrationKey(null));
    }

    @Test
    public void testGetAccountRequest() throws Exception {
        AccountRequest accountRequest = new AccountRequest("valid4@test.com",
                        "Test account Name", "TEAMMATES Test Institute 1");
        accountRequest.setRegistrationKey("4-123456");

        accountRequestsDb.saveEntity(accountRequest);

        ______TS("typical success case");

        AccountRequestAttributes accountRequestAttributes =
                accountRequestsDb.getAccountRequest("valid4@test.com", "TEAMMATES Test Institute 1");
        assertEquals(AccountRequestAttributes.valueOf(accountRequest), accountRequestAttributes);

        ______TS("account request not found");

        AccountRequestAttributes notFoundRequestAttributes =
                accountRequestsDb.getAccountRequest("not-found@test.com", "not found");
        assertNull(notFoundRequestAttributes);

        ______TS("failure null parameter");

        assertThrows(AssertionError.class,
                () -> accountRequestsDb.getAccountRequest(null, null));
    }

}
