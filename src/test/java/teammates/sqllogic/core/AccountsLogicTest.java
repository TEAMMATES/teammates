package teammates.sqllogic.core;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.logic.entity.Account;
import teammates.logic.entity.User;
import teammates.storage.sqlapi.AccountsDb;
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
}
