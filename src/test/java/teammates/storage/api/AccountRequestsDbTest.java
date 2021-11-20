package teammates.storage.api;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.AccountRequestAttributes;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.FieldValidator;
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
                .builder("valid@test.com", "TEAMMATES Test Institute 1")
                .withName("Test account Name")
                .build();

        accountRequest = accountRequestsDb.createOrUpdateAccountRequest(accountRequest);
        verifyPresentInDatabase(accountRequest);

        ______TS("duplicate account request, account request updated");

        AccountRequestAttributes duplicateAccount = AccountRequestAttributes
                .builder("valid@test.com", "TEAMMATES Test Institute 1")
                .withName("Test account Name 2")
                .build();

        duplicateAccount = accountRequestsDb.createOrUpdateAccountRequest(duplicateAccount);
        verifyPresentInDatabase(duplicateAccount);

        ______TS("failure case: invalid parameter");

        AccountRequestAttributes invalidAccountRequest = AccountRequestAttributes
                .builder("invalid email", "TEAMMATES Test Institute 1")
                .withName("Test account Name")
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
        AccountRequestAttributes a = AccountRequestAttributes
                .builder("valid2@test.com", "TEAMMATES Test Institute 1")
                .withName("Test account Name")
                .withRegistrationKey("2-123456")
                .build();

        accountRequestsDb.saveEntity(a.toEntity());
        a = accountRequestsDb.getAccountRequest("valid2@test.com", "TEAMMATES Test Institute 1");

        ______TS("silent deletion of non-existent account request");

        accountRequestsDb.deleteAccountRequest("not_exist", "not_exist");

        ______TS("typical success case");

        verifyPresentInDatabase(a);
        accountRequestsDb.deleteAccountRequest(a.getEmail(), a.getInstitute());
        verifyAbsentInDatabase(a);

        ______TS("silent deletion of same account request");

        accountRequestsDb.deleteAccountRequest(a.getEmail(), a.getInstitute());

        ______TS("failure null parameter");

        assertThrows(AssertionError.class,
                () -> accountRequestsDb.deleteAccountRequest(null, null));
    }

    @Test
    public void testGetAccountRequestForRegistrationKey() throws Exception {
        AccountRequestAttributes a = AccountRequestAttributes.builder("valid3@test.com", "TEAMMATES Test Institute 1")
                .withName("Test account Name")
                .withRegistrationKey("3-123456")
                .build();

        accountRequestsDb.saveEntity(a.toEntity());

        ______TS("typical success case");

        AccountRequestAttributes accountRequestAttributes =
                accountRequestsDb.getAccountRequestForRegistrationKey(a.getRegistrationKey());
        assertEquals(a, accountRequestAttributes);

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
        AccountRequestAttributes a = AccountRequestAttributes.builder("valid4@test.com", "TEAMMATES Test Institute 1")
                .withName("Test account Name")
                .withRegistrationKey("4-123456")
                .build();

        accountRequestsDb.saveEntity(a.toEntity());

        ______TS("typical success case");

        AccountRequestAttributes accountRequestAttributes =
                accountRequestsDb.getAccountRequest(a.getEmail(), a.getInstitute());
        assertEquals(a, accountRequestAttributes);

        ______TS("account request not found");

        AccountRequestAttributes notFoundRequestAttributes =
                accountRequestsDb.getAccountRequest("not-found@test.com", "not found");
        assertNull(notFoundRequestAttributes);

        ______TS("failure null parameter");

        assertThrows(AssertionError.class,
                () -> accountRequestsDb.getAccountRequest(null, null));
    }

}
