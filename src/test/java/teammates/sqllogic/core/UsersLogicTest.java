package teammates.sqllogic.core;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static teammates.common.util.Const.ERROR_UPDATE_NON_EXISTENT;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.Const.InstructorPermissions;
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
        FeedbackResponsesLogic feedbackResponsesLogic = mock(FeedbackResponsesLogic.class);
        FeedbackResponseCommentsLogic feedbackResponseCommentsLogic = mock(FeedbackResponseCommentsLogic.class);
        DeadlineExtensionsLogic deadlineExtensionsLogic = mock(DeadlineExtensionsLogic.class);
        usersLogic.initLogicDependencies(usersDb, accountsLogic, feedbackResponsesLogic,
                feedbackResponseCommentsLogic, deadlineExtensionsLogic);

        course = new Course("course-id", "course-name", Const.DEFAULT_TIME_ZONE, "institute");
        instructor = getTypicalInstructor();
        student = getTypicalStudent();
        account = getTypicalAccount();

        instructor.setAccount(account);
        student.setAccount(account);
    }

    @Test
    public void testResetInstructorAccountId_instructorExistsWithEmptyUsersListForAccount_success()
            throws EntityDoesNotExistException {
        String courseId = instructor.getCourseId();
        String email = instructor.getEmail();
        UUID accountId = account.getId();

        when(usersDb.getInstructorForEmail(courseId, email)).thenReturn(instructor);
        when(usersDb.getAllUsersByAccountId(accountId)).thenReturn(Collections.emptyList());

        usersLogic.resetInstructorAccountId(email, courseId, accountId);

        assertNull(instructor.getAccount());
        verify(accountsLogic, times(1)).deleteAccountCascade(accountId);
    }

    @Test
    public void testResetInstructorAccountId_instructorDoesNotExist_throwsEntityDoesNotExistException() {
        String courseId = instructor.getCourseId();
        String email = instructor.getEmail();
        UUID accountId = account.getId();

        when(usersDb.getInstructorForEmail(courseId, email)).thenReturn(null);

        EntityDoesNotExistException exception = assertThrows(EntityDoesNotExistException.class,
                () -> usersLogic.resetInstructorAccountId(email, courseId, accountId));

        assertEquals(ERROR_UPDATE_NON_EXISTENT
                + "Instructor [courseId=" + courseId + ", email=" + email + "]", exception.getMessage());
    }

    @Test
    public void testResetStudentAccountId_studentExistsWithEmptyUsersListForAccount_success()
            throws EntityDoesNotExistException {
        String courseId = student.getCourseId();
        String email = student.getEmail();
        UUID accountId = account.getId();

        when(usersDb.getStudentForEmail(courseId, email)).thenReturn(student);
        when(usersDb.getAllUsersByAccountId(accountId)).thenReturn(Collections.emptyList());

        usersLogic.resetStudentAccountId(email, courseId, accountId);

        assertNull(student.getAccount());
        verify(accountsLogic, times(1)).deleteAccountCascade(accountId);
    }

    @Test
    public void testResetStudentAccountId_entityDoesNotExist_throwsEntityDoesNotExistException() {
        String courseId = student.getCourseId();
        String email = student.getEmail();
        UUID accountId = account.getId();

        when(usersDb.getStudentForEmail(courseId, email)).thenReturn(null);

        EntityDoesNotExistException exception = assertThrows(EntityDoesNotExistException.class,
                () -> usersLogic.resetStudentAccountId(email, courseId, accountId));

        assertEquals(ERROR_UPDATE_NON_EXISTENT
                + "Student [courseId=" + courseId + ", email=" + email + "]", exception.getMessage());
    }

    @Test
    public void testAttachAccountToInstructor_studentWithoutAccount_linkedToInstructorAccount()
            throws InvalidParametersException, EntityDoesNotExistException {
        UUID accountId = account.getId();
        instructor.setAccount(null);
        student.setAccount(null);

        when(accountsLogic.getAccount(accountId)).thenReturn(account);
        when(usersDb.getStudentForEmail(instructor.getCourseId(), instructor.getEmail())).thenReturn(student);

        Instructor actual = usersLogic.attachAccountToInstructor(accountId, instructor);

        assertEquals(instructor, actual);
        assertEquals(account, instructor.getAccount());
        assertEquals(account, student.getAccount());
        verify(usersDb, times(1)).updateUser(instructor);
        verify(usersDb, times(1)).updateUser(student);
    }

    @Test
    public void testAttachAccountToInstructor_studentAlreadyLinkedToSameAccount_noStudentOverwrite()
            throws InvalidParametersException, EntityDoesNotExistException {
        UUID accountId = account.getId();
        instructor.setAccount(null);
        student.setAccount(account);

        when(accountsLogic.getAccount(accountId)).thenReturn(account);
        when(usersDb.getStudentForEmail(instructor.getCourseId(), instructor.getEmail())).thenReturn(student);

        usersLogic.attachAccountToInstructor(accountId, instructor);

        verify(usersDb, times(1)).updateUser(instructor);
        verify(usersDb, never()).updateUser(student);
    }

    @Test
    public void testAttachAccountToInstructor_studentAlreadyLinkedToDifferentAccount_noStudentOverwrite()
            throws InvalidParametersException, EntityDoesNotExistException {
        UUID accountId = account.getId();
        Account otherAccount = getTypicalAccount();
        instructor.setAccount(null);
        student.setAccount(otherAccount);

        when(accountsLogic.getAccount(accountId)).thenReturn(account);
        when(usersDb.getStudentForEmail(instructor.getCourseId(), instructor.getEmail())).thenReturn(student);

        usersLogic.attachAccountToInstructor(accountId, instructor);

        assertEquals(otherAccount, student.getAccount());
        verify(usersDb, times(1)).updateUser(instructor);
        verify(usersDb, never()).updateUser(student);
    }

    @Test
    public void testAttachAccountToInstructor_accountDoesNotExist_throwsEntityDoesNotExistException() {
        UUID accountId = UUID.randomUUID();
        instructor.setAccount(null);

        when(accountsLogic.getAccount(accountId)).thenReturn(null);

        EntityDoesNotExistException exception = assertThrows(EntityDoesNotExistException.class,
                () -> usersLogic.attachAccountToInstructor(accountId, instructor));

        assertEquals("There is no account associated with that account id", exception.getMessage());
        verify(usersDb, never()).updateUser(instructor);
    }

    @Test
    public void testGetUnregisteredStudentsForCourse_success() {
        Account registeredAccount = getTypicalAccount();
        Student registeredStudent = new Student(course, "reg-student-name", "valid1-student@email.tmt", "comments");
        registeredStudent.setAccount(registeredAccount);

        Student unregisteredStudentNullAccount =
                new Student(course, "unreg1-student-name", "valid2-student@email.tmt", "comments");
        unregisteredStudentNullAccount.setAccount(null);

        List<Student> students = Arrays.asList(
                registeredStudent,
                unregisteredStudentNullAccount);

        when(usersDb.getStudentsForCourse(course.getId())).thenReturn(students);

        List<Student> unregisteredStudents = usersLogic.getUnregisteredStudentsForCourse(course.getId());

        assertEquals(1, unregisteredStudents.size());
        assertTrue(unregisteredStudents.get(0).equals(unregisteredStudentNullAccount));
    }

    @Test
    public void testUpdateToEnsureValidityOfInstructorsForTheCourse_lastModifyInstructorPrivilege_shouldPreserve() {
        InstructorPrivileges privileges = instructor.getPrivileges();
        privileges.updatePrivilege(InstructorPermissions.CAN_MODIFY_INSTRUCTOR, false);
        instructor.setPrivileges(privileges);
        usersLogic.updateToEnsureValidityOfInstructorsForTheCourse(course.getId(), instructor);

        assertFalse(instructor.getPrivileges().isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR));
    }
}
