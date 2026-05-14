package teammates.logic.core;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.storage.api.AccountsDb;
import teammates.storage.entity.Account;
import teammates.storage.entity.User;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link AccountsLogic}.
 */
public class AccountsLogicTest extends BaseTestCase {

    private AccountsLogic accountsLogic = AccountsLogic.inst();

    private AccountsDb accountsDb;

    private UsersLogic usersLogic;

    @BeforeMethod
    public void setUpMethod() {
        accountsDb = mock(AccountsDb.class);
        usersLogic = mock(UsersLogic.class);
        CoursesLogic coursesLogic = mock(CoursesLogic.class);
        accountsLogic.initLogicDependencies(accountsDb, usersLogic, coursesLogic);
    }

    @Test
    public void testDeleteAccount_accountExists_success() {
        Account account = getTypicalAccount();
        String googleId = account.getGoogleId();

        when(accountsLogic.getAccountForGoogleId(googleId)).thenReturn(account);

        accountsLogic.deleteAccount(googleId);

        verify(accountsDb, times(1)).deleteAccount(account);
    }

    @Test
    public void testDeleteAccountCascade_googleIdExists_success() {
        Account account = getTypicalAccount();
        String googleId = account.getGoogleId();
        List<User> users = new ArrayList<>();

        for (int i = 0; i < 2; ++i) {
            users.add(getTypicalInstructor());
            users.add(getTypicalStudent());
        }

        when(usersLogic.getAllUsersByGoogleId(googleId)).thenReturn(users);
        when(accountsLogic.getAccountForGoogleId(googleId)).thenReturn(account);

        accountsLogic.deleteAccountCascade(googleId);

        for (User user : users) {
            verify(usersLogic, times(1)).deleteUser(user);
        }
        verify(accountsDb, times(1)).deleteAccount(account);
    }

    @Test
    public void testCreateOrGetAccountForEmail_accountExists_success() {
        Account account = getTypicalAccount();
        String email = account.getEmail();

        when(accountsDb.getAccountByGoogleId(email)).thenReturn(account);

        Account result = accountsLogic.createOrGetAccountForEmail(email);

        assertEquals(result, account);
    }

    @Test
    public void testCreateOrGetAccountForEmail_accountDoesNotExist_success() {
        String email = "nonexistent@example.com";

        when(accountsDb.getAccountByGoogleId(email)).thenReturn(null);
        when(accountsDb.createAccount(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));

        Account result = accountsLogic.createOrGetAccountForEmail(email);

        verify(accountsDb, times(1)).createAccount(result);
        assertNotNull(result);
        assertEquals(result.getEmail(), email);
    }

    @Test
    public void testCreateOrGetAccountForEmail_nullEmail_throwsException() {
        assertThrows(AssertionError.class,
                () -> accountsLogic.createOrGetAccountForEmail(null));
    }
}
