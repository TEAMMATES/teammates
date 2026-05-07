package teammates.it.logic.core;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.Const.InstructorPermissions;
import teammates.common.util.HibernateUtil;
import teammates.it.test.BaseTestCaseWithDatabaseAccess;
import teammates.logic.core.AccountsLogic;
import teammates.logic.core.CoursesLogic;
import teammates.logic.core.UsersLogic;
import teammates.storage.entity.Account;
import teammates.storage.entity.Course;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Section;
import teammates.storage.entity.Student;
import teammates.storage.entity.Team;

/**
 * SUT: {@link UsersLogic}.
 */
public class UsersLogicIT extends BaseTestCaseWithDatabaseAccess {

    private final UsersLogic usersLogic = UsersLogic.inst();

    private final AccountsLogic accountsLogic = AccountsLogic.inst();

    private final CoursesLogic coursesLogic = CoursesLogic.inst();

    private Course course;

    private Team team;

    private Account account;

    @BeforeMethod
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        course = getTypicalCourse();
        coursesLogic.createCourse(course);

        Section section = coursesLogic.createSection(course, "section-name");
        team = coursesLogic.createTeam(section, "team-name");

        account = getTypicalAccount();
        accountsLogic.createAccount(account);
    }

    @Test
    public void testResetInstructorGoogleId()
            throws InvalidParametersException, EntityAlreadyExistsException, EntityDoesNotExistException {
        Instructor instructor = getTypicalInstructor();
        instructor.setCourse(course);
        instructor.setAccount(account);

        String email = instructor.getEmail();
        String courseId = instructor.getCourseId();
        String googleId = instructor.getGoogleId();

        ______TS("success: reset instructor that does not exist");
        assertThrows(EntityDoesNotExistException.class,
                () -> usersLogic.resetInstructorGoogleId(email, courseId, googleId));

        ______TS("success: reset instructor that exists");
        usersLogic.createInstructor(instructor);
        usersLogic.resetInstructorGoogleId(email, courseId, googleId);

        assertNull(instructor.getAccount());
        assertEquals(0, accountsLogic.getAccountsForEmail(email).size());

        ______TS("found at least one other user with same googleId, should not delete account");
        Account anotherAccount = getTypicalAccount();
        accountsLogic.createAccount(anotherAccount);

        instructor.setCourse(course);
        instructor.setAccount(anotherAccount);

        Student anotherUser = usersLogic.createStudent(course, team, "name", "student-email@gmail.tmt", "comments");
        anotherUser.setAccount(anotherAccount);
        HibernateUtil.flushSession();

        usersLogic.resetInstructorGoogleId(email, courseId, googleId);

        assertNull(instructor.getAccount());
        assertEquals(anotherAccount, accountsLogic.getAccountForGoogleId(googleId));
    }

    @Test
    public void testResetStudentGoogleId()
            throws InvalidParametersException, EntityAlreadyExistsException, EntityDoesNotExistException {
        String email = "email@gmail.tmt";
        String courseId = course.getId();
        String googleId = account.getGoogleId();

        ______TS("success: reset student that does not exist");
        assertThrows(EntityDoesNotExistException.class,
                () -> usersLogic.resetStudentGoogleId(email, courseId, googleId));

        ______TS("success: reset student that exists");
        Student student = usersLogic.createStudent(course, team, "name", email, "comments");
        student.setAccount(account);

        usersLogic.resetStudentGoogleId(email, courseId, googleId);

        assertNull(student.getAccount());
        assertEquals(0, accountsLogic.getAccountsForEmail(email).size());

        ______TS("found at least one other user with same googleId, should not delete account");
        Account anotherAccount = getTypicalAccount();
        accountsLogic.createAccount(anotherAccount);

        student.setCourse(course);
        student.setAccount(anotherAccount);

        Instructor anotherUser = getTypicalInstructor();
        anotherUser.setCourse(course);
        anotherUser.setAccount(anotherAccount);

        usersLogic.createInstructor(anotherUser);
        usersLogic.resetStudentGoogleId(email, courseId, googleId);

        assertNull(student.getAccount());
        assertEquals(anotherAccount, accountsLogic.getAccountForGoogleId(googleId));
    }

    @Test
    public void testUpdateToEnsureValidityOfInstructorsForTheCourse() {
        Instructor instructor = getTypicalInstructor();
        instructor.setCourse(course);
        instructor.setAccount(account);

        ______TS("success: preserves modify instructor privilege if last instructor in course with privilege");
        InstructorPrivileges privileges = instructor.getPrivileges();
        privileges.updatePrivilege(InstructorPermissions.CAN_MODIFY_INSTRUCTOR, false);
        instructor.setPrivileges(privileges);
        usersLogic.updateToEnsureValidityOfInstructorsForTheCourse(course.getId(), instructor);

        assertFalse(instructor.getPrivileges().isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR));
    }
}
