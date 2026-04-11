package teammates.storage.sqlapi;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;

import java.util.UUID;

import org.mockito.MockedStatic;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.Account;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link AccountsDb}.
 */
public class AccountsDbTest extends BaseTestCase {

    private final AccountsDb accountsDb = AccountsDb.inst();

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
    public void testCreateAccount_accountDoesNotExist_success()
            throws InvalidParametersException, EntityAlreadyExistsException {
        Account account = new Account(getTypicalLoginIssuer(), "oidc-subject", "login-id",
                "name", "email@teammates.com");

        accountsDb.createAccount(account);

        mockHibernateUtil.verify(() -> HibernateUtil.persist(account));
    }

    @Test
    public void testCreateAccount_accountAlreadyExists_throwsEntityAlreadyExistsException() {
        Account account = getTypicalAccount();
        mockHibernateUtil.when(() -> HibernateUtil.get(Account.class, account.getId())).thenReturn(account);

        EntityAlreadyExistsException ex = assertThrows(EntityAlreadyExistsException.class,
                () -> accountsDb.createAccount(account));

        assertEquals("Trying to create an entity that exists: " + account.toString(), ex.getMessage());
        mockHibernateUtil.verify(() -> HibernateUtil.persist(account), never());
    }

    @Test
    public void testCreateAccount_invalidEmail_throwsInvalidParametersException() {
        Account account = new Account(getTypicalLoginIssuer(), "oidc-subject", "login-id", "name", "invalid");

        InvalidParametersException ex = assertThrows(InvalidParametersException.class,
                () -> accountsDb.createAccount(account));

        assertEquals(
                "\"invalid\" is not acceptable to TEAMMATES as a/an email because it is not in the correct format. "
                        + "An email address contains some text followed by one '@' sign followed by some more text, "
                        + "and should end with a top level domain address like .com. "
                        + "It cannot be longer than 254 characters, "
                        + "cannot be empty and cannot contain spaces.",
                ex.getMessage());
        mockHibernateUtil.verify(() -> HibernateUtil.persist(account), never());
    }

    @Test
    public void testUpdateAccount_accountExists_success()
            throws InvalidParametersException, EntityDoesNotExistException {
        Account account = getTypicalAccount();
        mockHibernateUtil.when(() -> HibernateUtil.get(Account.class, account.getId())).thenReturn(account);
        mockHibernateUtil.when(() -> HibernateUtil.merge(account)).thenReturn(account);
        account.setName("new name");

        accountsDb.updateAccount(account);

        mockHibernateUtil.verify(() -> HibernateUtil.merge(account));
    }

    @Test
    public void testUpdateAccount_accountDoesNotExist_throwsEntityDoesNotExistException() {
        Account account = getTypicalAccount();

        EntityDoesNotExistException ex = assertThrows(EntityDoesNotExistException.class,
                () -> accountsDb.updateAccount(account));

        assertEquals("Trying to update non-existent Entity: " + account.toString(), ex.getMessage());
        mockHibernateUtil.verify(() -> HibernateUtil.merge(account), never());
    }

    @Test
    public void testUpdateAccount_invalidEmail_throwsInvalidParametersException() {
        Account account = getTypicalAccount();
        account.setEmail("invalid");

        InvalidParametersException ex = assertThrows(InvalidParametersException.class,
                () -> accountsDb.updateAccount(account));

        assertEquals(
                "\"invalid\" is not acceptable to TEAMMATES as a/an email because it is not in the correct format. "
                        + "An email address contains some text followed by one '@' sign followed by some more text, "
                        + "and should end with a top level domain address like .com. "
                        + "It cannot be longer than 254 characters, "
                        + "cannot be empty and cannot contain spaces.",
                ex.getMessage());
        mockHibernateUtil.verify(() -> HibernateUtil.merge(account), never());
    }

    @Test
    public void testDeleteAccount_success() {
        Account account = getTypicalAccount();

        accountsDb.deleteAccount(account);

        mockHibernateUtil.verify(() -> HibernateUtil.remove(account));
    }

    @Test
    public void testDeleteAccount_null_success() {
        accountsDb.deleteAccount(null);

        mockHibernateUtil.verifyNoInteractions();
    }
}
