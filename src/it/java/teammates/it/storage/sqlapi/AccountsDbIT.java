package teammates.it.storage.sqlapi;

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

}
