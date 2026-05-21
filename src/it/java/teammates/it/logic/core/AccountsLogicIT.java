package teammates.it.logic.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.HibernateUtil;
import teammates.common.util.StringHelper;
import teammates.it.test.BaseTestCaseWithDatabaseAccess;
import teammates.logic.core.AccountsLogic;
import teammates.logic.core.UsersLogic;
import teammates.storage.api.AccountsDb;
import teammates.storage.entity.Account;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;

/**
 * SUT: {@link AccountsLogic}.
 */
public class AccountsLogicIT extends BaseTestCaseWithDatabaseAccess {

    private AccountsLogic accountsLogic = AccountsLogic.inst();
    private UsersLogic usersLogic = UsersLogic.inst();

    private AccountsDb accountsDb = AccountsDb.inst();

    private DataBundle typicalDataBundle;

    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        typicalDataBundle = persistDataBundle(getTypicalDataBundle());
        HibernateUtil.flushSession();
        HibernateUtil.clearSession();
    }

    @Test
    public void testJoinCourseForStudent()
            throws EntityAlreadyExistsException, EntityDoesNotExistException {

        Student student2YetToJoinCourse = typicalDataBundle.students.get("student2YetToJoinCourse4");
        Student student3YetToJoinCourse = typicalDataBundle.students.get("student3YetToJoinCourse4");
        Student studentInCourse = typicalDataBundle.students.get("student1InCourse1");

        String loggedInGoogleId = "AccLogicT.student.id";
        Account loggedInAccount = getTypicalAccount();
        loggedInAccount.setGoogleId(loggedInGoogleId);
        loggedInAccount.setEmail("acct.student@teammates.tmt");
        accountsDb.createAccount(loggedInAccount);

        ______TS("failure: wrong key");

        String wrongKey = StringHelper.encrypt("wrongkey");
        EntityDoesNotExistException ednee = assertThrows(EntityDoesNotExistException.class,
                () -> accountsLogic.joinCourse(wrongKey, loggedInAccount));
        assertEquals("No user with given registration key: " + wrongKey, ednee.getMessage());

        ______TS("failure: googleID belongs to an existing student in the course");

        Account studentInCourseAccount = accountsLogic.getAccountForGoogleId(studentInCourse.getGoogleId());
        EntityAlreadyExistsException eaee = assertThrows(EntityAlreadyExistsException.class,
                () -> accountsLogic.joinCourse(student2YetToJoinCourse.getRegKey(),
                studentInCourseAccount));
        assertEquals("This account is already associated with another student", eaee.getMessage());

        ______TS("success: student joins course");

        accountsLogic.joinCourse(student2YetToJoinCourse.getRegKey(), loggedInAccount);

        assertEquals(loggedInGoogleId, usersLogic.getStudentForEmail(
                student2YetToJoinCourse.getCourseId(), student2YetToJoinCourse.getEmail()).getGoogleId());

        ______TS("success: student joined but account already exists");

        Account existingAccount = getTypicalAccount();
        existingAccount.setGoogleId("existingAccountId");
        existingAccount.setEmail(student3YetToJoinCourse.getEmail());
        accountsDb.createAccount(existingAccount);

        accountsLogic.joinCourse(student3YetToJoinCourse.getRegKey(), existingAccount);

        assertEquals("existingAccountId", usersLogic.getStudentForEmail(
                student3YetToJoinCourse.getCourseId(), student3YetToJoinCourse.getEmail()).getGoogleId());

        ______TS("failure: already joined");

        eaee = assertThrows(EntityAlreadyExistsException.class,
                () -> accountsLogic.joinCourse(student2YetToJoinCourse.getRegKey(), loggedInAccount));
        assertEquals("User has already joined course", eaee.getMessage());
    }

    @Test
    public void testJoinCourseForInstructor() throws Exception {
        String instructorIdAlreadyJoinedCourse = "instructor1";
        Instructor instructor2YetToJoinCourse = typicalDataBundle.instructors.get("instructor2YetToJoinCourse4");
        Instructor instructor3YetToJoinCourse = typicalDataBundle.instructors.get("instructor3YetToJoinCourse4");

        String loggedInGoogleId = "AccLogicT.instr.id";
        Account loggedInAccount = getTypicalAccount();
        loggedInAccount.setGoogleId(loggedInGoogleId);
        loggedInAccount.setEmail("acct.instr@teammates.tmt");
        accountsDb.createAccount(loggedInAccount);

        String[] key = new String[] {
                getRegKeyForInstructor(instructor2YetToJoinCourse.getCourseId(), instructor2YetToJoinCourse.getEmail()),
                getRegKeyForInstructor(instructor2YetToJoinCourse.getCourseId(), instructor3YetToJoinCourse.getEmail()),
        };

        ______TS("failure: googleID belongs to an existing instructor in the course");

        Account instructor1Account = accountsLogic.getAccountForGoogleId(instructorIdAlreadyJoinedCourse);
        EntityAlreadyExistsException eaee = assertThrows(EntityAlreadyExistsException.class,
                () -> accountsLogic.joinCourse(
                        key[0], instructor1Account));
        assertEquals("This account is already associated with another instructor", eaee.getMessage());

        ______TS("success: instructor joins course");

        accountsLogic.joinCourse(key[0], loggedInAccount);

        Instructor joinedInstructor = usersLogic.getInstructorForEmail(
                        instructor2YetToJoinCourse.getCourseId(), instructor2YetToJoinCourse.getEmail());
        assertEquals(loggedInGoogleId, joinedInstructor.getGoogleId());

        ______TS("success: instructor joined but account already exists");

        String existingAccountId = "existingAccountId";
        Account existingAccount = getTypicalAccount();
        existingAccount.setGoogleId(existingAccountId);
        existingAccount.setEmail(instructor3YetToJoinCourse.getEmail());
        accountsDb.createAccount(existingAccount);

        accountsLogic.joinCourse(key[1], existingAccount);

        joinedInstructor = usersLogic.getInstructorForEmail(
                        instructor3YetToJoinCourse.getCourseId(), existingAccount.getEmail());
        assertEquals("existingAccountId", joinedInstructor.getGoogleId());

        ______TS("failure: instructor already joined");

        eaee = assertThrows(EntityAlreadyExistsException.class,
                () -> accountsLogic.joinCourse(key[0], loggedInAccount));
        assertEquals("User has already joined course", eaee.getMessage());

        ______TS("failure: key belongs to a different user");

        Account otherAccount = getTypicalAccount();
        otherAccount.setGoogleId("otherUserId");
        otherAccount.setEmail("other@teammates.tmt");
        accountsDb.createAccount(otherAccount);

        eaee = assertThrows(EntityAlreadyExistsException.class,
                () -> accountsLogic.joinCourse(key[0], otherAccount));
        assertEquals("User has already joined course", eaee.getMessage());

        ______TS("failure: invalid key");

        String invalidKey = StringHelper.encrypt("invalidKey");

        EntityDoesNotExistException ednee = assertThrows(EntityDoesNotExistException.class,
                () -> accountsLogic.joinCourse(invalidKey, loggedInAccount));
        assertEquals("No user with given registration key: " + invalidKey,
                ednee.getMessage());
    }

    private String getRegKeyForInstructor(String courseId, String email) {
        return usersLogic.getInstructorForEmail(courseId, email).getRegKey();
    }
}
