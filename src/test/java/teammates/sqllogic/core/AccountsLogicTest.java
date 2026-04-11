package teammates.sqllogic.core;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.sqlapi.AccountsDb;
import teammates.storage.sqlentity.Account;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.User;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link AccountsLogic}.
 */
public class AccountsLogicTest extends BaseTestCase {

    private AccountsLogic accountsLogic = AccountsLogic.inst();

    private AccountsDb accountsDb;

    private UsersLogic usersLogic;

    private CoursesLogic coursesLogic;

    @BeforeMethod
    public void setUpMethod() {
        accountsDb = mock(AccountsDb.class);
        usersLogic = mock(UsersLogic.class);
        coursesLogic = mock(CoursesLogic.class);
        accountsLogic.initLogicDependencies(accountsDb, usersLogic, coursesLogic);
    }

    @Test
    public void testDeleteAccount_accountExists_success() {
        Account account = getTypicalAccount();
        UUID accountId = account.getId();

        when(accountsDb.getAccount(accountId)).thenReturn(account);

        accountsLogic.deleteAccount(accountId);

        verify(accountsDb, times(1)).deleteAccount(account);
    }

    @Test
    public void testDeleteAccountCascade_accountIdExists_success() {
        Account account = getTypicalAccount();
        UUID accountId = account.getId();
        List<User> users = new ArrayList<>();

        for (int i = 0; i < 2; ++i) {
            users.add(getTypicalInstructor());
            users.add(getTypicalStudent());
        }

        when(usersLogic.getAllUsersByAccountId(accountId)).thenReturn(users);
        when(accountsDb.getAccount(accountId)).thenReturn(account);

        accountsLogic.deleteAccountCascade(accountId);

        for (User user : users) {
            verify(usersLogic, times(1)).deleteUser(user);
        }
        verify(accountsDb, times(1)).deleteAccount(account);
    }

    @Test
    public void testJoinCourseForInstructor_existingAccount_success()
            throws InvalidParametersException, EntityDoesNotExistException, EntityAlreadyExistsException {
        Account account = getTypicalAccount();
        UUID accountId = account.getId();
        Instructor instructor = getTypicalInstructor();
        Course course = getTypicalCourse();
        String regKey = instructor.getRegKey();

        when(usersLogic.getInstructorByRegistrationKey(regKey)).thenReturn(instructor);
        when(coursesLogic.getCourse(instructor.getCourseId())).thenReturn(course);
        when(usersLogic.getInstructorByAccountId(instructor.getCourseId(), accountId)).thenReturn(null);
        when(usersLogic.attachAccountToInstructor(accountId, instructor)).thenReturn(instructor);

        Instructor actual = accountsLogic.joinCourseForInstructor(regKey, accountId);

        assertEquals(instructor, actual);
        verify(usersLogic, times(1)).attachAccountToInstructor(accountId, instructor);
    }
}
