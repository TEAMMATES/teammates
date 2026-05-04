package teammates.it.logic.core;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.Const.InstructorPermissions;
import teammates.it.test.BaseTestCaseWithDatabaseAccess;
import teammates.logic.core.AccountsLogic;
import teammates.logic.core.CoursesLogic;
import teammates.logic.core.UsersLogic;
import teammates.logic.entity.Account;
import teammates.logic.entity.Course;
import teammates.logic.entity.Instructor;
import teammates.logic.entity.Section;
import teammates.logic.entity.Student;
import teammates.logic.entity.Team;

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

        Student anotherUser = getTypicalStudent();
        anotherUser.setCourse(course);
        anotherUser.setAccount(anotherAccount);
        anotherUser.setTeam(team);

        usersLogic.createStudent(anotherUser);
        usersLogic.resetInstructorGoogleId(email, courseId, googleId);

        assertNull(instructor.getAccount());
        assertEquals(anotherAccount, accountsLogic.getAccountForGoogleId(googleId));
    }

    @Test
    public void testResetStudentGoogleId()
            throws InvalidParametersException, EntityAlreadyExistsException, EntityDoesNotExistException {
        Student student = getTypicalStudent();
        student.setCourse(course);
        student.setAccount(account);
        student.setTeam(team);

        String email = student.getEmail();
        String courseId = student.getCourseId();
        String googleId = student.getGoogleId();

        ______TS("success: reset student that does not exist");
        assertThrows(EntityDoesNotExistException.class,
                () -> usersLogic.resetStudentGoogleId(email, courseId, googleId));

        ______TS("success: reset student that exists");
        usersLogic.createStudent(student);
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
