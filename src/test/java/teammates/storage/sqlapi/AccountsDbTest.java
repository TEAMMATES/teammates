package teammates.storage.sqlapi;

import static org.mockito.Mockito.mockStatic;

import java.util.UUID;

import org.mockito.MockedStatic;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.Account;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link AccountsDb}.
 */
public class AccountsDbTest extends BaseTestCase {

    private AccountsDb accountsDb = AccountsDb.inst();

    private MockedStatic<HibernateUtil> mockHibernateUtil;

    @BeforeMethod
    public void setUpMethod() {
        mockHibernateUtil = mockStatic(HibernateUtil.class);
    }

    @AfterMethod
    public void teardownMethod() {
        mockHibernateUtil.close();
    }

    @Test
    public void testGetAccount_accountExists_success() {
        Account account = getTypicalAccount();
        UUID id = account.getId();

        mockHibernateUtil.when(() -> HibernateUtil.get(Account.class, id)).thenReturn(account);

        Account actualAccount = accountsDb.getAccount(id);

        mockHibernateUtil.verify(() -> HibernateUtil.get(Account.class, id));

        assertEquals(account, actualAccount);
    }

    @Test
    public void testGetAccountByGoogleId_accountExists_success() {
        Account account = getTypicalAccount();
        String googleId = account.getGoogleId();

        mockHibernateUtil
                .when(() -> HibernateUtil.getBySimpleNaturalId(Account.class, googleId))
                .thenReturn(account);

        Account actualAccount = accountsDb.getAccountByGoogleId(googleId);

        mockHibernateUtil.verify(() ->
                HibernateUtil.getBySimpleNaturalId(Account.class, googleId));

        assertEquals(account, actualAccount);
    }

    @Test
    public void testCreateAccount_accountDoesNotExist_success() {
        Account account = new Account("google-id", "name", "email@teammates.com");

        accountsDb.createAccount(account);

        mockHibernateUtil.verify(() -> HibernateUtil.persist(account));
    }

    @Test
    public void testDeleteAccount_success() {
        Account account = new Account("google-id", "name", "email@teammates.com");

        accountsDb.deleteAccount(account);

        mockHibernateUtil.verify(() -> HibernateUtil.remove(account));
    }
}
