package teammates.storage.api;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        AccountAttributes a = createNewAccount("valid.googleId");

        ______TS("typical success case");
        AccountAttributes retrieved = accountsDb.getAccount(a.getGoogleId());
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
        AccountAttributes firstAccount = createNewAccount("first.googleId");

        accounts = accountsDb.getAccountsForEmail("valid@email.com");

        assertEquals(List.of(firstAccount), accounts);

        ______TS("typical success case: multiple accounts with email");
        AccountAttributes secondAccount = createNewAccount("second.googleId");
        AccountAttributes thirdAccount = createNewAccount("third.googleId");

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
                .withEmail("fresh-account@email.com")
                .build();

        accountsDb.createEntity(a);

        ______TS("duplicate account, creation fail");

        AccountAttributes duplicatedAccount = AccountAttributes.builder("test.account")
                .withName("name2")
                .withEmail("test2@email.com")
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
        AccountAttributes a = createNewAccount("valid.googleId");

        AccountAttributes updatedAccount =
                accountsDb.updateAccount(
                        AccountAttributes.updateOptionsBuilder(a.getGoogleId())
                                .build());

        // please verify the log message manually to ensure that saving request is not issued
        assertEquals(JsonUtils.toJson(a), JsonUtils.toJson(updatedAccount));
    }

    @Test
    public void testUpdateAccount() throws Exception {
        AccountAttributes a = createNewAccount("valid.googleId");

        ______TS("typical edit success case");

        Map<String, Instant> readNotifications = new HashMap<>();
        readNotifications.put("1", Instant.now());

        ______TS("typical edit success case");
        assertEquals(new HashMap<>(), a.getReadNotifications());
        AccountAttributes updatedAccount = accountsDb.updateAccount(
                AccountAttributes.updateOptionsBuilder(a.getGoogleId())
                        .withReadNotifications(readNotifications)
                        .build()
        );

        AccountAttributes actualAccount = accountsDb.getAccount(a.getGoogleId());

        assertEquals(readNotifications, actualAccount.getReadNotifications());
        assertEquals(readNotifications, updatedAccount.getReadNotifications());

        ______TS("non-existent account");

        EntityDoesNotExistException ednee = assertThrows(EntityDoesNotExistException.class,
                () -> accountsDb.updateAccount(
                        AccountAttributes.updateOptionsBuilder("non.existent")
                                .build()
                ));
        AssertHelper.assertContains(AccountsDb.ERROR_UPDATE_NON_EXISTENT, ednee.getMessage());

        ______TS("failure: null parameter");

        assertThrows(AssertionError.class,
                () -> accountsDb.updateAccount(null));

        accountsDb.deleteAccount(a.getGoogleId());
    }

    @Test
    public void testDeleteAccount() throws Exception {
        AccountAttributes a = createNewAccount("valid.googleId");

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

    private AccountAttributes createNewAccount(String googleId) throws Exception {
        AccountAttributes a = AccountAttributes.builder(googleId)
                .withName("Valid Fresh Account")
                .withEmail("valid@email.com")
                .build();
        return accountsDb.putEntity(a);
    }

}
