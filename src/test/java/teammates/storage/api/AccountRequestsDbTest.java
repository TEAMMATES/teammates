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

        AccountRequestAttributes a = AccountRequestAttributes.builder("valid@test.com")
                .withName("Test account Name")
                .withRegistrationKey("ValidRegistrationKey")
                .withInstitute("TEAMMATES Test Institute 1")
                .build();

        accountRequestsDb.createOrUpdateAccountRequest(a);
        verifyPresentInDatabase(a);

        ______TS("duplicate account, account request updated");

        AccountRequestAttributes duplicateAccount = AccountRequestAttributes.builder("valid@test.com")
                .withName("Test account Name 2")
                .withRegistrationKey("ValidRegistrationKey2")
                .withInstitute("TEAMMATES Test Institute 2")
                .build();

        accountRequestsDb.createOrUpdateAccountRequest(duplicateAccount);

        ______TS("failure case: invalid parameter");

        a.setEmail("invalid email");
        InvalidParametersException ipe = assertThrows(InvalidParametersException.class,
                () -> accountRequestsDb.createOrUpdateAccountRequest(a));
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
        AccountRequestAttributes a = AccountRequestAttributes.builder("valid2@test.com")
                .withName("Test account Name")
                .withRegistrationKey("ValidRegistrationKey2")
                .withInstitute("TEAMMATES Test Institute 1")
                .build();

        accountRequestsDb.createEntity(a);

        ______TS("silent deletion of non-existent account");

        accountRequestsDb.deleteAccountRequest("not_exist");

        ______TS("typical success case");

        verifyPresentInDatabase(a);

        accountRequestsDb.deleteAccountRequest(a.getEmail());

        verifyAbsentInDatabase(a);

        ______TS("silent deletion of same account");

        accountRequestsDb.deleteAccountRequest(a.getEmail());

        ______TS("failure null parameter");

        assertThrows(AssertionError.class,
                () -> accountRequestsDb.deleteAccountRequest(null));
    }

    @Test
    public void testGetAccountRequestForRegistrationKey() throws Exception {
        AccountRequestAttributes a = AccountRequestAttributes.builder("valid3@test.com")
                .withName("Test account Name")
                .withRegistrationKey("ValidRegistrationKey3")
                .withInstitute("TEAMMATES Test Institute 1")
                .build();

        accountRequestsDb.createEntity(a);

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
        AccountRequestAttributes a = AccountRequestAttributes.builder("valid4@test.com")
                .withName("Test account Name")
                .withRegistrationKey("ValidRegistrationKey4")
                .withInstitute("TEAMMATES Test Institute 1")
                .build();

        accountRequestsDb.createEntity(a);

        ______TS("typical success case");

        AccountRequestAttributes accountRequestAttributes =
                accountRequestsDb.getAccountRequest(a.getEmail());
        assertEquals(a, accountRequestAttributes);

        ______TS("account request not found");

        AccountRequestAttributes notFoundRequestAttributes =
                accountRequestsDb.getAccountRequest("not-found@test.com");
        assertNull(notFoundRequestAttributes);

        ______TS("failure null parameter");

        assertThrows(AssertionError.class,
                () -> accountRequestsDb.getAccountRequest(null));
    }

}
