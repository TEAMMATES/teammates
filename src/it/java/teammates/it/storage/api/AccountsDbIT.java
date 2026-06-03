package teammates.it.storage.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.testng.annotations.Test;

import teammates.it.test.BaseTestCaseWithDatabaseAccess;
import teammates.storage.api.AccountsDb;
import teammates.storage.entity.Account;

/**
 * SUT: {@link AccountsDb}.
 */
public class AccountsDbIT extends BaseTestCaseWithDatabaseAccess {

    private final AccountsDb accountsDb = AccountsDb.inst();

    @Test
    public void testGetAccountsByEmail() {
        ______TS("Get accounts by email, none exists, succeeds");

        List<Account> accounts = inTransaction(() -> accountsDb.getAccountsByEmail("email@teammates.com"));

        assertEquals(0, accounts.size());

        ______TS("Get accounts by email, multiple exists, succeeds");

        Account firstAccount = getTypicalAccount();

        Account secondAccount = getTypicalAccount();
        secondAccount.setGoogleId(firstAccount.getGoogleId() + "-2");

        Account thirdAccount = getTypicalAccount();
        thirdAccount.setGoogleId(firstAccount.getGoogleId() + "-3");

        String email = firstAccount.getEmail();

        inTransaction(() -> {
            accountsDb.persistAccount(firstAccount);
            accountsDb.persistAccount(secondAccount);
            accountsDb.persistAccount(thirdAccount);
        });

        accounts = inTransaction(() -> accountsDb.getAccountsByEmail(email));

        assertEquals(3, accounts.size());
        assertTrue(List.of(firstAccount, secondAccount, thirdAccount).containsAll(accounts));
    }

    @Test
    public void testCreateAccount() {
        ______TS("Create account, does not exists, succeeds");

        Account account = getTypicalAccount();

        inTransaction(() -> accountsDb.persistAccount(account));

        Account actualAccount = inTransaction(() -> accountsDb.getAccount(account.getId()));
        assertEquals(account, actualAccount);
    }

    @Test
    public void testDeleteAccount() {
        Account account = getTypicalAccount();
        inTransaction(() -> accountsDb.persistAccount(account));

        ______TS("Delete existing account, success");

        inTransaction(() -> accountsDb.deleteAccount(account));

        Account actual = inTransaction(() -> accountsDb.getAccount(account.getId()));
        assertNull(actual);
    }
}
