package teammates.logic.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPermissionRole;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.storage.entity.Account;
import teammates.storage.entity.Course;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Section;
import teammates.storage.entity.Student;
import teammates.storage.entity.Team;
import teammates.storage.entity.User;
import teammates.test.BaseTestCaseWithDatabaseAccess;
import teammates.test.GroupNames;

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

    @BeforeMethod(alwaysRun = true)
    protected void setUp() {
        inTransaction(() -> {
            Course typicalCourse = getTypicalCourse();
            HibernateUtil.persist(typicalCourse.getInstitute());
            course = coursesLogic.createCourse(
                    typicalCourse.getId(), typicalCourse.getName(), typicalCourse.getTimeZone(),
                    typicalCourse.getInstitute());

            Section section = coursesLogic.createSection(course, "section-name");
            team = coursesLogic.createTeam(section, "team-name");

            account = getTypicalAccount();
            account = accountsLogic.createAccount(
                    account.getProvider(), account.getSubject(), account.getTenantId(), account.getEmail());
        });
    }

    @Test(groups = GroupNames.INTEGRATION)
    public void testUnlinkAccount_instructor() {
        ______TS("failure: unlink instructor that does not exist");
        assertThrowsInTransaction(EntityDoesNotExistException.class,
                () -> usersLogic.unlinkAccount(UUID.randomUUID()));

        ______TS("success: unlink instructor that exists");
        Instructor instructor = inTransaction(() -> usersLogic.createInstructor(
                course, "instructor-name", "valid@teammates.tmt",
                false, Const.DEFAULT_DISPLAY_NAME_FOR_INSTRUCTOR,
                InstructorPermissionRole.COOWNER, account));
        User resetUser = inTransaction(() -> usersLogic.unlinkAccount(instructor.getId()));
        instructor.setAccount(null);

        assertEquals(instructor, resetUser);
        assertNull(instructor.getAccount());
        assertEquals(account, inTransaction(() -> accountsLogic.getAccount(account.getId())));
    }

    @Test(groups = GroupNames.INTEGRATION)
    public void testUnlinkAccount_student() {
        String email = "email@gmail.tmt";

        ______TS("failure: unlink student that does not exist");
        UUID missingStudentId = UUID.randomUUID();
        assertThrowsInTransaction(EntityDoesNotExistException.class,
                () -> usersLogic.unlinkAccount(missingStudentId));

        ______TS("success: unlink student that exists");
        Student student = inTransaction(() -> {
            Student createdStudent = usersLogic.createStudent(course, team, "name", email, "comments");
            createdStudent.setAccount(account);
            return createdStudent;
        });

        User unlinkedtUser = inTransaction(() -> usersLogic.unlinkAccount(student.getId()));
        student.setAccount(null);

        assertEquals(student, unlinkedtUser);
        assertNull(student.getAccount());
        assertEquals(account, inTransaction(() -> accountsLogic.getAccount(account.getId())));
    }

    @Test(groups = GroupNames.INTEGRATION)
    public void testUpdateToEnsureValidityOfInstructorsForTheCourse() {
        Instructor instructor = inTransaction(() -> usersLogic.createInstructor(
                course, "instructor-name", "valid@teammates.tmt",
                false, Const.DEFAULT_DISPLAY_NAME_FOR_INSTRUCTOR,
                InstructorPermissionRole.CUSTOM, account));

        ______TS("does not grant modify instructor privilege when the instructor does not already have it");
        InstructorPrivileges privileges = new InstructorPrivileges(instructor.getId());
        privileges.updatePrivilege(Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR, false);
        inTransaction(() -> InstructorPermissionsLogic.inst().saveInstructorPrivileges(instructor, privileges));

        inTransaction(() -> usersLogic.updateToEnsureValidityOfInstructorsForTheCourse(instructor));

        InstructorPrivileges result = inTransaction(() ->
                InstructorPermissionsLogic.inst().getInstructorPrivileges(instructor));
        assertFalse(result.isAllowedForPrivilege(Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR));
    }
}
