package teammates.it.storage.sqlapi;

import java.util.List;

import org.testng.annotations.Test;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.HibernateUtil;
import teammates.it.test.BaseTestCaseWithSqlDatabaseAccess;
import teammates.storage.sqlapi.AccountsDb;
import teammates.storage.sqlentity.Account;

/**
 * SUT: {@link AccountsDb}.
 */
public class AccountsDbIT extends BaseTestCaseWithSqlDatabaseAccess {

    private final AccountsDb accountsDb = AccountsDb.inst();

    @Test
    public void testGetAccountsByEmail() throws InvalidParametersException, EntityAlreadyExistsException {
        ______TS("Get accounts by email, none exists, succeeds");

        List<Account> accounts = accountsDb.getAccountsByEmail("email@teammates.com");

        assertEquals(0, accounts.size());

        ______TS("Get accounts by email, multiple exists, succeeds");

        Account firstAccount = getTypicalAccount();

        Account secondAccount = getTypicalAccount();
        secondAccount.setGoogleId(firstAccount.getGoogleId() + "-2");

        Account thirdAccount = getTypicalAccount();
        thirdAccount.setGoogleId(firstAccount.getGoogleId() + "-3");

        String email = firstAccount.getEmail();

        accountsDb.createAccount(firstAccount);
        accountsDb.createAccount(secondAccount);
        accountsDb.createAccount(thirdAccount);

        accounts = accountsDb.getAccountsByEmail(email);

        assertEquals(3, accounts.size());
        assertTrue(List.of(firstAccount, secondAccount, thirdAccount).containsAll(accounts));
    }

    @Test
    public void testCreateAccount() throws Exception {
        ______TS("Create account, does not exists, succeeds");

        Account account = new Account("google-id", "name", "email@teammates.com");

        accountsDb.createAccount(account);
        HibernateUtil.flushSession();

        Account actualAccount = accountsDb.getAccount(account.getId());
        verifyEquals(account, actualAccount);
    }

    @Test
    public void testUpdateAccount() throws Exception {
        Account account = new Account("google-id", "name", "email@teammates.com");
        accountsDb.createAccount(account);
        HibernateUtil.flushSession();

        ______TS("Update existing account, success");

        account.setName("new account name");
        accountsDb.updateAccount(account);

        Account actual = accountsDb.getAccount(account.getId());
        verifyEquals(account, actual);
    }

    @Test
    public void testDeleteAccount() throws InvalidParametersException, EntityAlreadyExistsException {
        Account account = new Account("google-id", "name", "email@teammates.com");
        accountsDb.createAccount(account);
        HibernateUtil.flushSession();

        ______TS("Delete existing account, success");

        accountsDb.deleteAccount(account);

        Account actual = accountsDb.getAccount(account.getId());
        assertNull(actual);
    }

    private Account getTypicalAccount() {
        return new Account("google-id", "name", "email@teammates.com");
    }
}
