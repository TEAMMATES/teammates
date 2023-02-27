package teammates.storage.sqlapi;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;

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
 * SUT: {@code AccountsDb}.
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
    public void testCreateAccount_accountDoesNotExist_success()
            throws InvalidParametersException, EntityAlreadyExistsException {
        Account account = new Account("google-id", "name", "email@teammates.com");

        accountsDb.createAccount(account);

        mockHibernateUtil.verify(() -> HibernateUtil.persist(account));
    }

    @Test
    public void testCreateAccount_accountAlreadyExists_throwsEntityAlreadyExistsException() {
        Account existingAccount = getAccountWithId();
        mockHibernateUtil.when(() -> HibernateUtil.getBySimpleNaturalId(Account.class, "google-id"))
                .thenReturn(existingAccount);
        Account account = new Account("google-id", "different name", "email@teammates.com");

        EntityAlreadyExistsException ex = assertThrows(EntityAlreadyExistsException.class,
                () -> accountsDb.createAccount(account));

        assertEquals("Trying to create an entity that exists: " + account.toString(), ex.getMessage());
        mockHibernateUtil.verify(() -> HibernateUtil.persist(account), never());
    }

    @Test
    public void testCreateAccount_invalidEmail_throwsInvalidParametersException() {
        Account account = new Account("google-id", "name", "invalid");

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
    public void testUpdateAccount_accountAlreadyExists_success()
            throws InvalidParametersException, EntityDoesNotExistException {
        Account account = getAccountWithId();
        mockHibernateUtil.when(() -> HibernateUtil.get(Account.class, account.getId()))
                .thenReturn(account);
        account.setName("new name");

        accountsDb.updateAccount(account);

        mockHibernateUtil.verify(() -> HibernateUtil.merge(account));
    }

    @Test
    public void testUpdateAccount_accountDoesNotExist_throwsEntityDoesNotExistException()
            throws InvalidParametersException, EntityAlreadyExistsException {
        Account account = getAccountWithId();

        EntityDoesNotExistException ex = assertThrows(EntityDoesNotExistException.class,
                () -> accountsDb.updateAccount(account));

        assertEquals("Trying to update non-existent Entity: " + account.toString(), ex.getMessage());
        mockHibernateUtil.verify(() -> HibernateUtil.persist(account), never());
    }

    @Test
    public void testUpdateAccount_invalidEmail_throwsInvalidParametersException() {
        Account account = getAccountWithId();
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
        mockHibernateUtil.verify(() -> HibernateUtil.persist(account), never());
    }

    @Test
    public void testDeleteAccount_success() {
        Account account = new Account("google-id", "name", "email@teammates.com");

        accountsDb.deleteAccount(account);

        mockHibernateUtil.verify(() -> HibernateUtil.remove(account));
    }

    private Account getAccountWithId() {
        Account account = new Account("google-id", "name", "email@teammates.com");
        account.setId(1);
        return account;
    }
}
