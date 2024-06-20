package teammates.logic.core;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.AccountRequestAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.storage.entity.AccountRequest;
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
    public void testCreateAccountRequest() throws Exception {
        ______TS("typical success case");

        AccountRequestAttributes accountRequest = AccountRequestAttributes
                .builder("valid@test.com", "TEAMMATES Test Institute 1", "Test account Name")
                .build();

        AccountRequestAttributes createdAccountRequest =
                accountRequestsLogic.createAccountRequest(accountRequest);
        verifyPresentInDatabase(createdAccountRequest);

        assertEquals(accountRequest.getEmail(), createdAccountRequest.getEmail());
        assertEquals(accountRequest.getName(), createdAccountRequest.getName());
        assertEquals(accountRequest.getInstitute(), createdAccountRequest.getInstitute());
        assertNotNull(createdAccountRequest.getRegistrationKey());
        assertNotNull(createdAccountRequest.getCreatedAt());

        ______TS("failure: duplicate account request");

        AccountRequestAttributes duplicateAccountRequest = AccountRequestAttributes
                .builder("valid@test.com", "TEAMMATES Test Institute 1", "Test account Name")
                .build();

        assertThrows(EntityAlreadyExistsException.class, () -> {
            accountRequestsLogic.createAccountRequest(duplicateAccountRequest);
        });

        accountRequestsLogic.deleteAccountRequest("valid@test.com", "TEAMMATES Test Institute 1");

        ______TS("failure case: invalid parameter");

        AccountRequestAttributes invalidAccountRequest = AccountRequestAttributes
                .builder("invalid email", "TEAMMATES Test Institute 1", "Test account Name")
                .build();

        InvalidParametersException ipe = assertThrows(InvalidParametersException.class,
                () -> accountRequestsLogic.createAccountRequest(invalidAccountRequest));
        AssertHelper.assertContains(
                getPopulatedErrorMessage(
                        FieldValidator.EMAIL_ERROR_MESSAGE, "invalid email",
                        FieldValidator.EMAIL_FIELD_NAME, FieldValidator.REASON_INCORRECT_FORMAT,
                        FieldValidator.EMAIL_MAX_LENGTH),
                ipe.getMessage());

        ______TS("failure: null parameter");
        assertThrows(AssertionError.class, () -> accountRequestsLogic.createAccountRequest(null));
    }

    @Test
    public void testUpdateAccountRequest() throws Exception {
        accountRequestsLogic.createAccountRequest(AccountRequestAttributes
                .builder("valid@test.com", "TEAMMATES Test Institute 1", "Test account Name")
                .build());

        ______TS("typical success case");
        AccountRequestAttributes.UpdateOptions updateOptions = AccountRequestAttributes
                .updateOptionsBuilder("valid@test.com", "TEAMMATES Test Institute 1")
                .withRegisteredAt(Const.TIME_REPRESENTS_NOW)
                .build();
        accountRequestsLogic.updateAccountRequest(updateOptions);

        AccountRequestAttributes accountRequest = accountRequestsLogic
                .getAccountRequest("valid@test.com", "TEAMMATES Test Institute 1");

        assertEquals(Const.TIME_REPRESENTS_NOW, accountRequest.getRegisteredAt());

        ______TS("failure: account request not found");
        AccountRequestAttributes.UpdateOptions updateOptionsNotFound = AccountRequestAttributes
                .updateOptionsBuilder("not_found@test.com", "Unknown Test Institute 1")
                .withRegisteredAt(Const.TIME_REPRESENTS_NOW)
                .build();

        assertThrows(EntityDoesNotExistException.class,
                () -> accountRequestsLogic.updateAccountRequest(updateOptionsNotFound));
    }

    @Test
    public void testDeleteAccountRequest() throws Exception {
        // This ensures the AccountRequestAttributes has the correct ID.
        AccountRequestAttributes accountRequestAttributes = dataBundle.accountRequests.get("unregisteredInstructor1");
        AccountRequest accountRequest = accountRequestAttributes.toEntity();
        AccountRequestAttributes a = AccountRequestAttributes.valueOf(accountRequest);

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
                accountRequestsLogic.getAccountRequest("unregisteredinstructor1@gmail.tmt", "TEAMMATES Test Institute 1");

        ______TS("typical success case");

        AccountRequestAttributes accountRequestAttributes =
                accountRequestsLogic.getAccountRequestForRegistrationKey(a.getRegistrationKey());
        assertEquals(a, accountRequestAttributes);

        ______TS("account request not found");

        assertNull(accountRequestsLogic.getAccountRequestForRegistrationKey("not-found"));

        ______TS("failure null parameter");

        assertThrows(AssertionError.class,
                () -> accountRequestsLogic.getAccountRequestForRegistrationKey(null));
    }

    @Test
    public void testGetAccountRequest() {
        AccountRequestAttributes a = dataBundle.accountRequests.get("unregisteredInstructor1");

        ______TS("typical success case");

        AccountRequestAttributes accountRequestAttributes =
                accountRequestsLogic.getAccountRequest(a.getEmail(), a.getInstitute());
        assertEquals(a, accountRequestAttributes);

        ______TS("account request not found");

        assertNull(accountRequestsLogic.getAccountRequest("not-found@test.com", "not-found"));

        ______TS("failure null parameter");

        assertThrows(AssertionError.class,
                () -> accountRequestsLogic.getAccountRequest(null, null));
    }

}
