package teammates.logic.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.StringHelper;
import teammates.storage.api.AccountsDb;
import teammates.storage.entity.Account;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.test.BaseTestCaseWithDatabaseAccess;
import teammates.test.GroupNames;

/**
 * SUT: {@link AccountsLogic}.
 */
public class AccountsLogicIT extends BaseTestCaseWithDatabaseAccess {

    private AccountsLogic accountsLogic = AccountsLogic.inst();
    private UsersLogic usersLogic = UsersLogic.inst();

    private AccountsDb accountsDb = AccountsDb.inst();

    private DataBundle typicalDataBundle;

    @BeforeMethod(alwaysRun = true)
    protected void setUp() {
        typicalDataBundle = persistDataBundle(getTypicalDataBundle());
    }

    @Test(groups = GroupNames.INTEGRATION)
    public void testJoinCourseForStudent() {
        Student student2YetToJoinCourse = typicalDataBundle.students.get("student2YetToJoinCourse4");
        Student student3YetToJoinCourse = typicalDataBundle.students.get("student3YetToJoinCourse4");
        Student studentInCourse = typicalDataBundle.students.get("student1InCourse1");

        Account loggedInAccount = getTypicalAccountForEmail("acct.student@teammates.tmt");
        inTransaction(() -> accountsDb.persistAccount(loggedInAccount));

        ______TS("failure: wrong key");

        String wrongKey = StringHelper.encrypt("wrongkey");
        EntityDoesNotExistException ednee = assertThrowsInTransaction(EntityDoesNotExistException.class,
                () -> accountsLogic.joinCourse(wrongKey, loggedInAccount));
        assertEquals("No user with given registration key: " + wrongKey, ednee.getMessage());

        ______TS("failure: account belongs to an existing student in the course");

        Account studentInCourseAccount = inTransaction(() -> accountsLogic.getAccount(studentInCourse.getAccountId()));
        EntityAlreadyExistsException eaee = assertThrowsInTransaction(EntityAlreadyExistsException.class,
                () -> accountsLogic.joinCourse(student2YetToJoinCourse.getRegKey(),
                studentInCourseAccount));
        assertEquals("This account is already associated with another student", eaee.getMessage());

        ______TS("success: student joins course");

        inTransaction(() -> accountsLogic.joinCourse(student2YetToJoinCourse.getRegKey(), loggedInAccount));

        assertEquals(loggedInAccount.getId(), inTransaction(() -> usersLogic.getStudentForEmail(
                student2YetToJoinCourse.getCourseId(), student2YetToJoinCourse.getEmail()).getAccountId()));

        ______TS("success: student joined but account already exists");

        Account existingAccount = getTypicalAccountForEmail(student3YetToJoinCourse.getEmail());
        inTransaction(() -> accountsDb.persistAccount(existingAccount));

        inTransaction(() -> accountsLogic.joinCourse(student3YetToJoinCourse.getRegKey(), existingAccount));

        assertEquals(existingAccount.getId(), inTransaction(() -> usersLogic.getStudentForEmail(
                student3YetToJoinCourse.getCourseId(), student3YetToJoinCourse.getEmail()).getAccountId()));

        ______TS("failure: already joined");

        eaee = assertThrowsInTransaction(EntityAlreadyExistsException.class,
                () -> accountsLogic.joinCourse(student2YetToJoinCourse.getRegKey(), loggedInAccount));
        assertEquals("User has already joined course", eaee.getMessage());
    }

    @Test(groups = GroupNames.INTEGRATION)
    public void testJoinCourseForInstructor() {
        Instructor instructor2YetToJoinCourse = typicalDataBundle.instructors.get("instructor2YetToJoinCourse4");
        Instructor instructor3YetToJoinCourse = typicalDataBundle.instructors.get("instructor3YetToJoinCourse4");

        Account loggedInAccount = getTypicalAccountForEmail("acct.instr@teammates.tmt");
        inTransaction(() -> accountsDb.persistAccount(loggedInAccount));

        String[] key = new String[] {
                getRegKeyForInstructor(instructor2YetToJoinCourse.getCourseId(), instructor2YetToJoinCourse.getEmail()),
                getRegKeyForInstructor(instructor2YetToJoinCourse.getCourseId(), instructor3YetToJoinCourse.getEmail()),
        };

        ______TS("failure: account belongs to an existing instructor in the course");

        Account instructor1Account = inTransaction(() -> accountsLogic.getAccount(
                typicalDataBundle.accounts.get("instructor1").getId()));
        EntityAlreadyExistsException eaee = assertThrowsInTransaction(EntityAlreadyExistsException.class,
                () -> accountsLogic.joinCourse(
                        key[0], instructor1Account));
        assertEquals("This account is already associated with another instructor", eaee.getMessage());

        ______TS("success: instructor joins course");

        inTransaction(() -> accountsLogic.joinCourse(key[0], loggedInAccount));

        Instructor joinedInstructor = inTransaction(() -> usersLogic.getInstructorForEmail(
                        instructor2YetToJoinCourse.getCourseId(), instructor2YetToJoinCourse.getEmail()));
        assertEquals(loggedInAccount.getId(), joinedInstructor.getAccountId());

        ______TS("success: instructor joined but account already exists");

        Account existingAccount = getTypicalAccountForEmail(instructor3YetToJoinCourse.getEmail());
        inTransaction(() -> accountsDb.persistAccount(existingAccount));

        inTransaction(() -> accountsLogic.joinCourse(key[1], existingAccount));

        joinedInstructor = inTransaction(() -> usersLogic.getInstructorForEmail(
                        instructor3YetToJoinCourse.getCourseId(), existingAccount.getEmail()));
        assertEquals(existingAccount.getId(), joinedInstructor.getAccountId());

        ______TS("failure: instructor already joined");

        eaee = assertThrowsInTransaction(EntityAlreadyExistsException.class,
                () -> accountsLogic.joinCourse(key[0], loggedInAccount));
        assertEquals("User has already joined course", eaee.getMessage());

        ______TS("failure: key belongs to a different user");

        Account otherAccount = getTypicalAccountForEmail("other@teammates.tmt");
        inTransaction(() -> accountsDb.persistAccount(otherAccount));

        eaee = assertThrowsInTransaction(EntityAlreadyExistsException.class,
                () -> accountsLogic.joinCourse(key[0], otherAccount));
        assertEquals("User has already joined course", eaee.getMessage());

        ______TS("failure: invalid key");

        String invalidKey = StringHelper.encrypt("invalidKey");

        EntityDoesNotExistException ednee = assertThrowsInTransaction(EntityDoesNotExistException.class,
                () -> accountsLogic.joinCourse(invalidKey, loggedInAccount));
        assertEquals("No user with given registration key: " + invalidKey,
                ednee.getMessage());
    }

    private String getRegKeyForInstructor(String courseId, String email) {
        return inTransaction(() -> usersLogic.getInstructorForEmail(courseId, email).getRegKey());
    }

    private Account getTypicalAccountForEmail(String email) {
        Account account = getTypicalAccount();
        account.setEmail(email);
        account.setGoogleId(email);
        return account;
    }
}
