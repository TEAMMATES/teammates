package teammates.logic.core;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.AccountRequestAttributes;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.FieldValidator;
import teammates.storage.api.AccountRequestsDb;
import teammates.test.AssertHelper;

/**
 * SUT: {@link AccountRequestsLogic}.
 */
public class AccountRequestsLogicTest extends BaseLogicTest {

    private final AccountRequestsLogic accountRequestsLogic = AccountRequestsLogic.inst();
    private final AccountRequestsDb accountRequestsDb = AccountRequestsDb.inst();

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

        AccountRequestAttributes a = AccountRequestAttributes.builder("valid@test.com")
                .withName("Test account Name")
                .withRegistrationKey("ValidRegistrationKey")
                .withInstitute("TEAMMATES Test Institute 1")
                .build();

        accountRequestsLogic.createOrUpdateAccountRequest(a);
        verifyPresentInDatabase(a);

        ______TS("duplicate account, account request updated");

        AccountRequestAttributes duplicateAccount = AccountRequestAttributes.builder("valid@test.com")
                .withName("Test account Name 2")
                .withRegistrationKey("ValidRegistrationKey2")
                .withInstitute("TEAMMATES Test Institute 2")
                .build();

        accountRequestsLogic.createOrUpdateAccountRequest(duplicateAccount);
        assertEquals(getAccountRequest(duplicateAccount), duplicateAccount);

        ______TS("failure case: invalid parameter");

        a.setEmail("invalid email");
        InvalidParametersException ipe = assertThrows(InvalidParametersException.class,
                () -> accountRequestsLogic.createOrUpdateAccountRequest(a));
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
        AccountRequestAttributes a = AccountRequestAttributes.builder("valid2@test.com")
                .withName("Test account Name")
                .withRegistrationKey("ValidRegistrationKey2")
                .withInstitute("TEAMMATES Test Institute 1")
                .build();

        accountRequestsDb.createEntity(a);

        ______TS("silent deletion of non-existent account");

        accountRequestsLogic.deleteAccountRequest("not_exist");

        ______TS("typical success case");

        verifyPresentInDatabase(a);

        accountRequestsLogic.deleteAccountRequest(a.getEmail());

        verifyAbsentInDatabase(a);

        ______TS("silent deletion of same account");

        accountRequestsLogic.deleteAccountRequest(a.getEmail());

        ______TS("failure null parameter");

        assertThrows(AssertionError.class,
                () -> accountRequestsLogic.deleteAccountRequest(null));
    }

    @Test
    public void testgetAccountRequestForRegistrationKey() throws Exception {
        AccountRequestAttributes a = AccountRequestAttributes.builder("valid3@test.com")
                .withName("Test account Name")
                .withRegistrationKey("ValidRegistrationKey3")
                .withInstitute("TEAMMATES Test Institute 1")
                .build();

        accountRequestsDb.createEntity(a);

        ______TS("typical success case");

        AccountRequestAttributes accountRequestAttributes = 
                accountRequestsLogic.getAccountRequestForRegistrationKey(a.getRegistrationKey());
        assertEquals(a, accountRequestAttributes);

        ______TS("account request not found");

        AccountRequestAttributes notFoundRequestAttributes = 
                accountRequestsLogic.getAccountRequestForRegistrationKey("not-found");
        assertNull(notFoundRequestAttributes);

        ______TS("failure null parameter");

        assertThrows(AssertionError.class, 
                () -> accountRequestsLogic.getAccountRequestForRegistrationKey(null));
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
                accountRequestsLogic.getAccountRequest(a.getEmail());
        assertEquals(a, accountRequestAttributes);

        ______TS("account request not found");

        AccountRequestAttributes notFoundRequestAttributes = 
                accountRequestsLogic.getAccountRequest("not-found@test.com");
        assertNull(notFoundRequestAttributes);

        ______TS("failure null parameter");

        assertThrows(AssertionError.class, 
                () -> accountRequestsLogic.getAccountRequest(null));
    }

}
