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

    @Test
    public void testSqlInjectionInCreateAccount() throws Exception {
        ______TS("SQL Injection test in createAccount email field");

        // Attempt to use SQL commands in email field
        String email = "test';/**/DROP/**/TABLE/**/accounts;/**/--@gmail.com";
        Account accountEmail = new Account("google-id-email", "name", email);

        // The regex check should fail and throw an exception
        assertThrows(InvalidParametersException.class, () -> accountsDb.createAccount(accountEmail));

        ______TS("SQL Injection test in createAccount name field");

        // Attempt to use SQL commands in email field
        String name = "test';/**/DROP/**/TABLE/**/accounts;/**/--";
        Account accountName = new Account("google-id-name", name, "email@gmail.com");

        // The system should treat the input as a plain text string
        accountsDb.createAccount(accountName);
        Account actualAccountName = accountsDb.getAccountByGoogleId("google-id-name");
        assertEquals(name, actualAccountName.getName());
    }

    @Test
    public void testSqlInjectionInGetAccountByGoogleId() throws Exception {
        ______TS("SQL Injection test in getAccountByGoogleId");

        Account account = new Account("google-id", "name", "email@gmail.com");
        accountsDb.createAccount(account);

        // The system should treat the input as a plain text string
        String googleId = "test' OR 1 = 1; --";
        Account actual = accountsDb.getAccountByGoogleId(googleId);
        assertEquals(null, actual);
    }

    @Test
    public void testSqlInjectionInGetAccountsByEmail() throws Exception {
        ______TS("SQL Injection test in getAccountsByEmail");

        Account account = new Account("google-id", "name", "email@gmail.com");
        accountsDb.createAccount(account);

        // The system should treat the input as a plain text string
        String email = "test' OR 1 = 1; --";
        List<Account> actualAccounts = accountsDb.getAccountsByEmail(email);
        assertEquals(0, actualAccounts.size());
    }

    @Test
    public void testSqlInjectionInUpdateAccount() throws Exception {
        ______TS("SQL Injection test in updateAccount");

        Account account = new Account("google-id", "name", "email@gmail.com");
        accountsDb.createAccount(account);

        // The system should treat the input as a plain text string
        String name = "newName'; DROP TABLE accounts; --";
        account.setName(name);
        accountsDb.updateAccount(account);
        Account actual = accountsDb.getAccountByGoogleId("google-id");
        assertEquals(account.getName(), actual.getName());
    }

    @Test
    public void testSqlInjectionInDeleteAccount() throws Exception {
        ______TS("SQL Injection test in deleteAccount");

        Account account = new Account("google-id", "name", "email@gmail.com");
        accountsDb.createAccount(account);

        String name = "newName'; DELETE FROM accounts; --";
        Account injectionAccount = new Account("google-id-injection", name, "email-injection@gmail.com");
        accountsDb.createAccount(injectionAccount);

        accountsDb.deleteAccount(injectionAccount);
        Account actualInjectionAccount = accountsDb.getAccountByGoogleId("google-id-injection");

        // The account should be deleted
        assertEquals(null, actualInjectionAccount);

        // All other accounts should not be deleted
        Account actualAccount = accountsDb.getAccountByGoogleId("google-id");
        assertEquals(account, actualAccount);
    }
}
