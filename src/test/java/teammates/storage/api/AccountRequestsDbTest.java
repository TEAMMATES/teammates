package teammates.storage.api;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.AccountRequestAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
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
    public void testCreateAccountRequest() throws Exception {
        ______TS("typical success case");

        AccountRequestAttributes accountRequest = AccountRequestAttributes
                .builder("valid@test.com", "TEAMMATES Test Institute 1", "Test account Name")
                .build();

        accountRequest = accountRequestsDb.createEntity(accountRequest);
        verifyPresentInDatabase(accountRequest);

        ______TS("failure: duplicate account request");

        AccountRequestAttributes duplicateAccountRequest = AccountRequestAttributes
                .builder("valid@test.com", "TEAMMATES Test Institute 1", "Test account Name")
                .build();

        assertThrows(EntityAlreadyExistsException.class, () -> {
            accountRequestsDb.createEntity(duplicateAccountRequest);
        });

        accountRequestsDb.deleteAccountRequest("valid@test.com", "TEAMMATES Test Institute 1");

        ______TS("failure case: invalid parameter");

        AccountRequestAttributes invalidAccountRequest = AccountRequestAttributes
                .builder("invalid email", "TEAMMATES Test Institute 1", "Test account Name")
                .build();

        InvalidParametersException ipe = assertThrows(InvalidParametersException.class,
                () -> accountRequestsDb.createEntity(invalidAccountRequest));
        AssertHelper.assertContains(
                getPopulatedErrorMessage(
                        FieldValidator.EMAIL_ERROR_MESSAGE, "invalid email",
                        FieldValidator.EMAIL_FIELD_NAME, FieldValidator.REASON_INCORRECT_FORMAT,
                        FieldValidator.EMAIL_MAX_LENGTH),
                ipe.getMessage());

        ______TS("failure: null parameter");
        assertThrows(AssertionError.class, () -> accountRequestsDb.createEntity(null));
    }

    @Test
    public void testUpdateAccountRequest() throws Exception {
        accountRequestsDb.createEntity(AccountRequestAttributes
                .builder("valid@test.com", "TEAMMATES Test Institute 1", "Test account Name")
                .build());

        ______TS("typical success case");
        AccountRequestAttributes.UpdateOptions updateOptions = AccountRequestAttributes
                .updateOptionsBuilder("valid@test.com", "TEAMMATES Test Institute 1")
                .withRegisteredAt(Const.TIME_REPRESENTS_NOW)
                .build();
        accountRequestsDb.updateAccountRequest(updateOptions);

        AccountRequestAttributes accountRequest = accountRequestsDb
                .getAccountRequest("valid@test.com", "TEAMMATES Test Institute 1");

        assertEquals(Const.TIME_REPRESENTS_NOW, accountRequest.getRegisteredAt());

        ______TS("failure: account request not found");
        AccountRequestAttributes.UpdateOptions updateOptionsNotFound = AccountRequestAttributes
                .updateOptionsBuilder("not_found@test.com", "Unknown Test Institute 1")
                .withRegisteredAt(Const.TIME_REPRESENTS_NOW)
                .build();

        assertThrows(EntityDoesNotExistException.class,
                () -> accountRequestsDb.updateAccountRequest(updateOptionsNotFound));
    }

    @Test
    public void testDeleteAccountRequest() {
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
    public void testGetAccountRequestForRegistrationKey() {
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
    public void testGetAccountRequest() {
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
