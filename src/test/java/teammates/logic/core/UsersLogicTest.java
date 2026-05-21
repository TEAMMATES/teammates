package teammates.logic.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static teammates.common.util.Const.ERROR_UPDATE_NON_EXISTENT;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.common.util.Const.InstructorPermissions;
import teammates.storage.api.UsersDb;
import teammates.storage.entity.Account;
import teammates.storage.entity.Course;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.storage.entity.User;
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
        CoursesLogic coursesLogic = mock(CoursesLogic.class);
        usersLogic.initLogicDependencies(usersDb, accountsLogic, coursesLogic, feedbackResponsesLogic);

        course = new Course("course-id", "course-name", Const.DEFAULT_TIME_ZONE, "institute");
        instructor = getTypicalInstructor();
        student = getTypicalStudent();
        account = getTypicalAccount();

        instructor.setAccount(account);
        student.setAccount(account);
    }

    @Test
    public void testResetAccount_instructorExists_success()
            throws EntityDoesNotExistException {
        String googleId = account.getGoogleId();

        when(usersDb.getUser(instructor.getId())).thenReturn(instructor);

        User resetUser = usersLogic.resetAccount(instructor.getId());

        assertEquals(instructor, resetUser);
        assertNull(instructor.getAccount());
        verify(accountsLogic, times(0)).deleteAccount(googleId);
    }

    @Test
    public void testResetAccount_userDoesNotExist_throwsEntityDoesNotExistException() {
        UUID userId = UUID.randomUUID();

        when(usersDb.getUser(userId)).thenReturn(null);

        EntityDoesNotExistException exception = assertThrows(EntityDoesNotExistException.class,
                () -> usersLogic.resetAccount(userId));

        assertEquals(ERROR_UPDATE_NON_EXISTENT + "User [id=" + userId + "]", exception.getMessage());
    }

    @Test
    public void testResetAccount_studentExists_success()
            throws EntityDoesNotExistException {
        String googleId = account.getGoogleId();

        when(usersDb.getUser(student.getId())).thenReturn(student);

        User resetUser = usersLogic.resetAccount(student.getId());

        assertEquals(student, resetUser);
        assertNull(student.getAccount());
        verify(accountsLogic, times(0)).deleteAccount(googleId);
    }

    @Test
    public void testGetUnregisteredStudentsForCourse_success() {
        Account registeredAccount = new Account("valid-google-id", "student-name", "valid1-student@email.tmt");
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
        assertEquals(unregisteredStudentNullAccount, unregisteredStudents.get(0));
    }

    @Test
    public void testDeleteStudentsInCourseCascade_success() {
        usersLogic.deleteStudentsInCourse(course.getId());

        verify(usersDb, times(1)).deleteStudentsInCourse(course.getId());
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
