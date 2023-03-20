package teammates.sqllogic.core;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static teammates.common.util.Const.ERROR_UPDATE_NON_EXISTENT;

import java.util.Collections;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPermissionRole;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.storage.sqlapi.UsersDb;
import teammates.storage.sqlentity.Account;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link UsersLogic}.
 */
public class UsersLogicTest extends BaseTestCase {

    private UsersLogic usersLogic = UsersLogic.inst();

    private AccountsLogic accountsLogic;

    private UsersDb usersDb;

    private Instructor instructor;

    private Student student;

    private Account account;

    private Course course;

    @BeforeMethod
    public void setUpMethod() {
        usersDb = mock(UsersDb.class);
        accountsLogic = mock(AccountsLogic.class);
        usersLogic.initLogicDependencies(usersDb, accountsLogic);

        course = new Course("course-id", "course-name", Const.DEFAULT_TIME_ZONE, "institute");
        instructor = getTypicalInstructor();
        student = getTypicalStudent();
        account = generateTypicalAccount();

        instructor.setAccount(account);
        student.setAccount(account);
    }

    @Test
    public void testResetInstructorGoogleId_instructorExistsWithEmptyUsersListFromGoogleId_success()
            throws EntityDoesNotExistException {
        String courseId = instructor.getCourseId();
        String email = instructor.getEmail();
        String googleId = account.getGoogleId();

        when(usersLogic.getInstructorForEmail(courseId, email)).thenReturn(instructor);
        when(usersDb.getAllUsersByGoogleId(googleId)).thenReturn(Collections.emptyList());
        when(accountsLogic.getAccountForGoogleId(googleId)).thenReturn(account);

        usersLogic.resetInstructorGoogleId(email, courseId, googleId);

        assertEquals(null, instructor.getAccount());
        verify(accountsLogic, times(1)).deleteAccountCascade(googleId);
    }

    @Test
    public void testResetInstructorGoogleId_instructorDoesNotExists_throwsEntityDoesNotExistException()
            throws EntityDoesNotExistException {
        String courseId = instructor.getCourseId();
        String email = instructor.getEmail();
        String googleId = account.getGoogleId();

        when(usersLogic.getInstructorForEmail(courseId, email)).thenReturn(null);

        EntityDoesNotExistException exception = assertThrows(EntityDoesNotExistException.class,
                () -> usersLogic.resetInstructorGoogleId(email, courseId, googleId));

        assertEquals(ERROR_UPDATE_NON_EXISTENT
                + "Instructor [courseId=" + courseId + ", email=" + email + "]", exception.getMessage());
    }

    @Test
    public void testResetStudentGoogleId_studentExistsWithEmptyUsersListFromGoogleId_success()
            throws EntityDoesNotExistException {
        String courseId = student.getCourseId();
        String email = student.getEmail();
        String googleId = account.getGoogleId();

        when(usersLogic.getStudentForEmail(courseId, email)).thenReturn(student);
        when(usersDb.getAllUsersByGoogleId(googleId)).thenReturn(Collections.emptyList());
        when(accountsLogic.getAccountForGoogleId(googleId)).thenReturn(account);

        usersLogic.resetStudentGoogleId(email, courseId, googleId);

        assertNull(student.getAccount());
        verify(accountsLogic, times(1)).deleteAccountCascade(googleId);
    }

    @Test
    public void testResetStudentGoogleId_entityDoesNotExists_throwsEntityDoesNotExistException()
            throws EntityDoesNotExistException {
        String courseId = student.getCourseId();
        String email = student.getEmail();
        String googleId = account.getGoogleId();

        when(usersLogic.getStudentForEmail(courseId, email)).thenReturn(null);

        EntityDoesNotExistException exception = assertThrows(EntityDoesNotExistException.class,
                () -> usersLogic.resetStudentGoogleId(email, courseId, googleId));

        assertEquals(ERROR_UPDATE_NON_EXISTENT
                + "Student [courseId=" + courseId + ", email=" + email + "]", exception.getMessage());
    }

    private Instructor getTypicalInstructor() {
        InstructorPrivileges instructorPrivileges = new InstructorPrivileges(
                Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        InstructorPermissionRole role = InstructorPermissionRole
                .getEnum(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);

        return new Instructor(course, "instructor-name", "valid-instructor@email.tmt",
                true, Const.DEFAULT_DISPLAY_NAME_FOR_INSTRUCTOR, role, instructorPrivileges);
    }

    private Student getTypicalStudent() {
        return new Student(course, "student-name", "valid-student@email.tmt", "comments");
    }

    private Account generateTypicalAccount() {
        return new Account("test-googleId", "test-name", "test@test.com");
    }

}
