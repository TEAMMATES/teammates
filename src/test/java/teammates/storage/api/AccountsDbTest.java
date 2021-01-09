package teammates.storage.api;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.FieldValidator;
import teammates.common.util.JsonUtils;
import teammates.test.AssertHelper;
import teammates.test.BaseComponentTestCase;

/**
 * SUT: {@link AccountsDb}.
 */
public class AccountsDbTest extends BaseComponentTestCase {

    private AccountsDb accountsDb = new AccountsDb();

    @Test
    public void testGetAccount() throws Exception {
        AccountAttributes a = createNewAccount();

        ______TS("typical success case without");
        AccountAttributes retrieved = accountsDb.getAccount(a.googleId);
        assertNotNull(retrieved);

        ______TS("typical success with student profile");
        retrieved = accountsDb.getAccount(a.googleId);
        assertNotNull(retrieved);

        ______TS("expect null for non-existent account");
        retrieved = accountsDb.getAccount("non.existent");
        assertNull(retrieved);

        ______TS("failure: null parameter");
        assertThrows(AssertionError.class, () -> accountsDb.getAccount(null));

        // delete created account
        accountsDb.deleteAccount(a.googleId);
    }

    @Test
    public void testCreateAccount() throws Exception {

        ______TS("typical success case (legacy data)");
        AccountAttributes a = AccountAttributes.builder("test.account")
                .withName("Test account Name")
                .withIsInstructor(false)
                .withEmail("fresh-account@email.com")
                .withInstitute("TEAMMATES Test Institute 1")
                .build();

        accountsDb.createEntity(a);

        ______TS("duplicate account, creation fail");

        AccountAttributes duplicatedAccount = AccountAttributes.builder("test.account")
                .withName("name2")
                .withEmail("test2@email.com")
                .withInstitute("de2v")
                .withIsInstructor(false)
                .build();
        assertThrows(EntityAlreadyExistsException.class, () -> {
            accountsDb.createEntity(duplicatedAccount);
        });

        accountsDb.deleteAccount(a.googleId);

        // Should we not allow empty fields?
        ______TS("failure case: invalid parameter");
        a.email = "invalid email";
        InvalidParametersException ipe = assertThrows(InvalidParametersException.class,
                () -> accountsDb.createEntity(a));
        AssertHelper.assertContains(
                getPopulatedErrorMessage(
                        FieldValidator.EMAIL_ERROR_MESSAGE, "invalid email",
                        FieldValidator.EMAIL_FIELD_NAME, FieldValidator.REASON_INCORRECT_FORMAT,
                        FieldValidator.EMAIL_MAX_LENGTH),
                ipe.getMessage());

        ______TS("failure: null parameter");
        assertThrows(AssertionError.class, () -> accountsDb.createEntity(null));
    }

    @Test
    public void testUpdateAccount_noChangeToAccount_shouldNotIssueSaveRequest() throws Exception {
        AccountAttributes a = createNewAccount();

        AccountAttributes updatedAccount =
                accountsDb.updateAccount(
                        AccountAttributes.updateOptionsBuilder(a.getGoogleId())
                                .build());

        // please verify the log message manually to ensure that saving request is not issued
        assertEquals(JsonUtils.toJson(a), JsonUtils.toJson(updatedAccount));

        updatedAccount =
                accountsDb.updateAccount(
                        AccountAttributes.updateOptionsBuilder(a.getGoogleId())
                                .withIsInstructor(a.isInstructor())
                                .build());

        // please verify the log message manually to ensure that saving request is not issued
        assertEquals(JsonUtils.toJson(a), JsonUtils.toJson(updatedAccount));
    }

    @Test
    public void testUpdateAccount() throws Exception {
        AccountAttributes a = createNewAccount();

        ______TS("typical edit success case");
        assertFalse(a.isInstructor);
        AccountAttributes updatedAccount = accountsDb.updateAccount(
                AccountAttributes.updateOptionsBuilder(a.googleId)
                        .withIsInstructor(true)
                        .build()
        );

        AccountAttributes actualAccount = accountsDb.getAccount(a.googleId);

        assertTrue(actualAccount.isInstructor);
        assertTrue(updatedAccount.isInstructor);

        ______TS("non-existent account");

        EntityDoesNotExistException ednee = assertThrows(EntityDoesNotExistException.class,
                () -> accountsDb.updateAccount(
                        AccountAttributes.updateOptionsBuilder("non.existent")
                                .withIsInstructor(true)
                                .build()
                ));
        AssertHelper.assertContains(AccountsDb.ERROR_UPDATE_NON_EXISTENT, ednee.getMessage());

        ______TS("failure: null parameter");

        assertThrows(AssertionError.class,
                () -> accountsDb.updateAccount(null));

        accountsDb.deleteAccount(a.googleId);
    }

    // the test is to ensure that optimized saving policy is implemented without false negative
    @Test
    public void testUpdateAccount_singleFieldUpdate_shouldUpdateCorrectly() throws Exception {
        AccountAttributes typicalAccount = createNewAccount();

        assertFalse(typicalAccount.isInstructor());
        AccountAttributes updatedAccount = accountsDb.updateAccount(
                AccountAttributes.updateOptionsBuilder(typicalAccount.googleId)
                        .withIsInstructor(true)
                        .build());
        AccountAttributes actualAccount = accountsDb.getAccount(typicalAccount.googleId);
        assertTrue(actualAccount.isInstructor());
        assertTrue(updatedAccount.isInstructor());
    }

    @Test
    public void testDeleteAccount() throws Exception {
        AccountAttributes a = createNewAccount();

        ______TS("silent deletion of non-existent account");

        accountsDb.deleteAccount("not_exist");
        assertNotNull(accountsDb.getAccount(a.googleId));

        ______TS("typical success case");
        AccountAttributes newAccount = accountsDb.getAccount(a.googleId);
        assertNotNull(newAccount);

        accountsDb.deleteAccount(a.googleId);

        AccountAttributes newAccountDeleted = accountsDb.getAccount(a.googleId);
        assertNull(newAccountDeleted);

        ______TS("silent deletion of same account");
        accountsDb.deleteAccount(a.googleId);

        ______TS("failure null parameter");

        assertThrows(AssertionError.class,
                () -> accountsDb.deleteAccount(null));
    }

    private AccountAttributes createNewAccount() throws Exception {
        AccountAttributes a = getNewAccountAttributes();
        return accountsDb.putEntity(a);
    }

    private AccountAttributes getNewAccountAttributes() {
        return AccountAttributes.builder("valid.googleId")
                .withName("Valid Fresh Account")
                .withIsInstructor(false)
                .withEmail("valid@email.com")
                .withInstitute("TEAMMATES Test Institute 1")
                .build();
    }
}
