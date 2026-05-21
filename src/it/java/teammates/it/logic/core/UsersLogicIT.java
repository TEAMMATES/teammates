package teammates.it.logic.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;

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
import teammates.storage.entity.Account;
import teammates.storage.entity.Course;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Section;
import teammates.storage.entity.Student;
import teammates.storage.entity.Team;
import teammates.storage.entity.User;

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
    public void testResetAccount_instructor()
            throws InvalidParametersException, EntityAlreadyExistsException, EntityDoesNotExistException {
        Instructor instructor = getTypicalInstructor();
        instructor.setCourse(course);
        instructor.setAccount(account);

        String googleId = instructor.getGoogleId();

        ______TS("failure: reset instructor that does not exist");
        assertThrows(EntityDoesNotExistException.class,
                () -> usersLogic.resetAccount(instructor.getId()));

        ______TS("success: reset instructor that exists");
        usersLogic.createInstructor(instructor);
        User resetUser = usersLogic.resetAccount(instructor.getId());

        assertEquals(instructor, resetUser);
        assertNull(instructor.getAccount());
        assertEquals(account, accountsLogic.getAccountForGoogleId(googleId));

    }

    @Test
    public void testResetAccount_student()
            throws InvalidParametersException, EntityAlreadyExistsException, EntityDoesNotExistException {
        String email = "email@gmail.tmt";
        String googleId = account.getGoogleId();

        ______TS("failure: reset student that does not exist");
        UUID missingStudentId = UUID.randomUUID();
        assertThrows(EntityDoesNotExistException.class,
                () -> usersLogic.resetAccount(missingStudentId));

        ______TS("success: reset student that exists");
        Student student = usersLogic.createStudent(course, team, "name", email, "comments");
        student.setAccount(account);

        User resetUser = usersLogic.resetAccount(student.getId());

        assertEquals(student, resetUser);
        assertNull(student.getAccount());
        assertEquals(account, accountsLogic.getAccountForGoogleId(googleId));

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
