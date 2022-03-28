package teammates.storage.api;

import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.FieldValidator;
import teammates.common.util.JsonUtils;
import teammates.test.AssertHelper;
import teammates.test.BaseTestCaseWithLocalDatabaseAccess;

/**
 * SUT: {@link AccountsDb}.
 */
public class AccountsDbTest extends BaseTestCaseWithLocalDatabaseAccess {

    private final AccountsDb accountsDb = AccountsDb.inst();

    @Test
    public void testGetAccount() throws Exception {
        AccountAttributes a = createNewAccount("valid.googleId", false);

        ______TS("typical success case without");
        AccountAttributes retrieved = accountsDb.getAccount(a.getGoogleId());
        assertNotNull(retrieved);

        ______TS("typical success with student profile");
        retrieved = accountsDb.getAccount(a.getGoogleId());
        assertNotNull(retrieved);

        ______TS("expect null for non-existent account");
        retrieved = accountsDb.getAccount("non.existent");
        assertNull(retrieved);

        ______TS("failure: null parameter");
        assertThrows(AssertionError.class, () -> accountsDb.getAccount(null));

        // delete created account
        accountsDb.deleteAccount(a.getGoogleId());
    }

    @Test
    public void testGetAccountsForEmail() throws Exception {
        ______TS("typical success case: no accounts with email");
        List<AccountAttributes> accounts = accountsDb.getAccountsForEmail("valid@email.com");

        assertTrue(accounts.isEmpty());

        ______TS("typical success case: one account with email");
        AccountAttributes firstAccount = createNewAccount("first.googleId", true);

        accounts = accountsDb.getAccountsForEmail("valid@email.com");

        assertEquals(List.of(firstAccount), accounts);

        ______TS("typical success case: multiple accounts with email");
        AccountAttributes secondAccount = createNewAccount("second.googleId", true);
        AccountAttributes thirdAccount = createNewAccount("third.googleId", false);

        accounts = accountsDb.getAccountsForEmail("valid@email.com");

        assertEquals(3, accounts.size());
        assertTrue(List.of(firstAccount, secondAccount, thirdAccount).containsAll(accounts));

        // delete created accounts
        accountsDb.deleteAccount(firstAccount.getGoogleId());
        accountsDb.deleteAccount(secondAccount.getGoogleId());
        accountsDb.deleteAccount(thirdAccount.getGoogleId());
    }

    @Test
    public void testCreateAccount() throws Exception {

        ______TS("typical success case");
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

        accountsDb.deleteAccount(a.getGoogleId());

        // Should we not allow empty fields?
        ______TS("failure case: invalid parameter");
        a.setEmail("invalid email");
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
        AccountAttributes a = createNewAccount("valid.googleId", true);

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
        AccountAttributes a = createNewAccount("valid.googleId", false);

        ______TS("typical edit success case");
        assertFalse(a.isInstructor());
        AccountAttributes updatedAccount = accountsDb.updateAccount(
                AccountAttributes.updateOptionsBuilder(a.getGoogleId())
                        .withIsInstructor(true)
                        .build()
        );

        AccountAttributes actualAccount = accountsDb.getAccount(a.getGoogleId());

        assertTrue(actualAccount.isInstructor());
        assertTrue(updatedAccount.isInstructor());

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

        accountsDb.deleteAccount(a.getGoogleId());
    }

    // the test is to ensure that optimized saving policy is implemented without false negative
    @Test
    public void testUpdateAccount_singleFieldUpdate_shouldUpdateCorrectly() throws Exception {
        AccountAttributes typicalAccount = createNewAccount("valid.googleId", false);

        assertFalse(typicalAccount.isInstructor());
        AccountAttributes updatedAccount = accountsDb.updateAccount(
                AccountAttributes.updateOptionsBuilder(typicalAccount.getGoogleId())
                        .withIsInstructor(true)
                        .build());
        AccountAttributes actualAccount = accountsDb.getAccount(typicalAccount.getGoogleId());
        assertTrue(actualAccount.isInstructor());
        assertTrue(updatedAccount.isInstructor());
    }

    @Test
    public void testDeleteAccount() throws Exception {
        AccountAttributes a = createNewAccount("valid.googleId", true);

        ______TS("silent deletion of non-existent account");

        accountsDb.deleteAccount("not_exist");
        assertNotNull(accountsDb.getAccount(a.getGoogleId()));

        ______TS("typical success case");
        AccountAttributes newAccount = accountsDb.getAccount(a.getGoogleId());
        assertNotNull(newAccount);

        accountsDb.deleteAccount(a.getGoogleId());

        AccountAttributes newAccountDeleted = accountsDb.getAccount(a.getGoogleId());
        assertNull(newAccountDeleted);

        ______TS("silent deletion of same account");
        accountsDb.deleteAccount(a.getGoogleId());

        ______TS("failure null parameter");

        assertThrows(AssertionError.class,
                () -> accountsDb.deleteAccount(null));
    }

    private AccountAttributes createNewAccount(String googleId, boolean isInstructor) throws Exception {
        AccountAttributes a = getNewAccountAttributes(googleId, isInstructor);
        return accountsDb.putEntity(a);
    }

    private AccountAttributes getNewAccountAttributes(String googleId, boolean isInstructor) {
        return AccountAttributes.builder(googleId)
                .withName("Valid Fresh Account")
                .withIsInstructor(isInstructor)
                .withEmail("valid@email.com")
                .withInstitute("TEAMMATES Test Institute 1")
                .build();
    }
}
