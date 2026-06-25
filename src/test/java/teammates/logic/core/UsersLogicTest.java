package teammates.logic.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static teammates.common.util.Const.ERROR_UPDATE_NON_EXISTENT;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPermissionRole;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.datatransfer.Provider;
import teammates.common.datatransfer.StudentQuery;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.logic.email.CourseJoinEmailsLogic;
import teammates.storage.api.UsersDb;
import teammates.storage.entity.Account;
import teammates.storage.entity.Course;
import teammates.storage.entity.Institute;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.storage.entity.User;
import teammates.test.BaseTestCase;
import teammates.ui.exception.InvalidOperationException;

/**
 * SUT: {@link UsersLogic}.
 */
public class UsersLogicTest extends BaseTestCase {

    private UsersLogic usersLogic = UsersLogic.inst();

    private UsersDb usersDb;

    private InstructorPermissionsLogic instructorPermissionsLogic;

    private Instructor instructor;

    private Student student;

    private Course course;

    @BeforeMethod
    public void setUpMethod() {
        usersDb = mock(UsersDb.class);
        FeedbackResponsesLogic feedbackResponsesLogic = mock(FeedbackResponsesLogic.class);
        FeedbackSessionsLogic feedbackSessionsLogic = mock(FeedbackSessionsLogic.class);
        CoursesLogic coursesLogic = mock(CoursesLogic.class);
        CourseJoinEmailsLogic courseJoinEmailsLogic = mock(CourseJoinEmailsLogic.class);
        instructorPermissionsLogic = mock(InstructorPermissionsLogic.class);
        doAnswer(invocation -> {
            Instructor instr = invocation.getArgument(0);
            String permissionName = invocation.getArgument(1);
            InstructorPermissionRole role = instr.getRole();
            InstructorPrivileges privileges = role == null
                    || role == InstructorPermissionRole.CUSTOM
                            ? new InstructorPrivileges(instr.getId())
                            : new InstructorPrivileges(instr.getId(), role.getRoleName());
            return privileges.isAllowedForPrivilege(permissionName);
        }).when(instructorPermissionsLogic).hasPermissions(any(Instructor.class), any(String.class));
        usersLogic.initLogicDependencies(usersDb, coursesLogic, courseJoinEmailsLogic, feedbackSessionsLogic,
                feedbackResponsesLogic, instructorPermissionsLogic);

        course = new Course("course-id", "course-name", Const.DEFAULT_TIME_ZONE);
        new Institute("institute", "SG").addCourse(course);
        instructor = getTypicalInstructor();
        student = getTypicalStudent();
        Account account = getTypicalAccount();

        instructor.setAccount(account);
        student.setAccount(account);
    }

    @Test
    public void testResetAccount_instructorExists_success()
            throws EntityDoesNotExistException {
        when(usersDb.getUser(instructor.getId())).thenReturn(instructor);

        User resetUser = usersLogic.unlinkAccount(instructor.getId());

        assertEquals(instructor, resetUser);
        assertNull(instructor.getAccount());
    }

    @Test
    public void testResetAccount_userDoesNotExist_throwsEntityDoesNotExistException() {
        UUID userId = UUID.randomUUID();

        when(usersDb.getUser(userId)).thenReturn(null);

        EntityDoesNotExistException exception = assertThrows(EntityDoesNotExistException.class,
                () -> usersLogic.unlinkAccount(userId));

        assertEquals(ERROR_UPDATE_NON_EXISTENT + "User [id=" + userId + "]", exception.getMessage());
    }

    @Test
    public void testResetAccount_studentExists_success()
            throws EntityDoesNotExistException {
        when(usersDb.getUser(student.getId())).thenReturn(student);

        User resetUser = usersLogic.unlinkAccount(student.getId());

        assertEquals(student, resetUser);
        assertNull(student.getAccount());
    }

    @Test
    public void testGetUnregisteredStudentsForCourse_success() {
        Account registeredAccount = new Account(Provider.TEAMMATES_DEV, "valid-google-id", "validTenantId",
                "valid1-student@email.tmt");
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
    public void testGetStudentsVisibleToAccount_instructorInCourse_canViewAllStudents() {
        Account requesterAccount = getTypicalAccount();
        Instructor requester = createInstructor("requester@teammates.tmt", true);
        requester.setAccount(requesterAccount);
        requester.setRole(InstructorPermissionRole.CUSTOM);

        Student student1 = new Student(course, "student-1", "student1@teammates.tmt", "comments");
        Student student2 = new Student(course, "student-2", "student2@teammates.tmt", "comments");

        when(usersDb.getInstructorsByAccountId(requesterAccount.getId())).thenReturn(List.of(requester));
        when(usersDb.getStudents(new StudentQuery(List.of(course.getId()), null, null)))
                .thenReturn(List.of(student1, student2));

        List<Student> actual = usersLogic.getStudentsVisibleToAccount(
                new StudentQuery(List.of(course.getId()), null, null), requesterAccount);

        assertEquals(List.of(student1, student2), actual);
    }

    @Test
    public void testDeleteStudentsInCourse_success() {
        usersLogic.deleteStudentsInCourse(course.getId());

        verify(usersDb, times(1)).deleteStudentsInCourse(course.getId());
    }

    @Test
    public void testDeleteInstructorCascade_hasAlternativeInstructor_success() throws InvalidOperationException {
        Instructor instructorToDelete = createRegisteredInstructor("to-delete@teammates.tmt", true);
        Instructor alternativeInstructor = createRegisteredInstructor("alternative@teammates.tmt", true);

        when(usersDb.getInstructor(instructorToDelete.getId())).thenReturn(instructorToDelete);
        when(usersDb.getInstructorsForCourse(course.getId()))
                .thenReturn(new ArrayList<>(List.of(instructorToDelete, alternativeInstructor)));

        usersLogic.deleteInstructorCascade(instructorToDelete.getId());

        verify(usersDb, times(1)).removeUser(instructorToDelete);
    }

    @Test
    public void testDeleteInstructorCascade_instructorDoesNotExist_failSilently() throws InvalidOperationException {
        UUID userId = UUID.randomUUID();
        when(usersDb.getInstructor(userId)).thenReturn(null);

        usersLogic.deleteInstructorCascade(userId);

        verify(usersDb, times(0)).removeUser(instructor);
    }

    @Test
    public void testDeleteInstructorCascade_noAlternativeModifyInstructor_throwsInvalidOperationException() {
        Instructor instructorToDelete = createRegisteredInstructor("to-delete@teammates.tmt", true);
        Instructor alternativeInstructor = createUnregisteredInstructor("alternative@teammates.tmt", true);

        when(usersDb.getInstructor(instructorToDelete.getId())).thenReturn(instructorToDelete);
        when(usersDb.getInstructorsForCourse(course.getId()))
                .thenReturn(new ArrayList<>(List.of(instructorToDelete, alternativeInstructor)));

        InvalidOperationException ioe = assertThrows(InvalidOperationException.class,
                () -> usersLogic.deleteInstructorCascade(instructorToDelete.getId()));

        assertEquals("The instructor you are trying to delete is the last instructor in the course. "
                + "Deleting the last instructor from the course is not allowed.", ioe.getMessage());
        verify(usersDb, times(0)).removeUser(instructorToDelete);
    }

    @Test
    public void testDeleteInstructorCascade_noAlternativeVisibleInstructor_throwsInvalidOperationException() {
        Instructor instructorToDelete = createRegisteredInstructor("to-delete@teammates.tmt", true);
        Instructor alternativeInstructor = createRegisteredInstructor("alternative@teammates.tmt", false);

        when(usersDb.getInstructor(instructorToDelete.getId())).thenReturn(instructorToDelete);
        when(usersDb.getInstructorsForCourse(course.getId()))
                .thenReturn(new ArrayList<>(List.of(instructorToDelete, alternativeInstructor)));

        InvalidOperationException ioe = assertThrows(InvalidOperationException.class,
                () -> usersLogic.deleteInstructorCascade(instructorToDelete.getId()));

        assertEquals("The instructor you are trying to delete is the last instructor in the course. "
                + "Deleting the last instructor from the course is not allowed.", ioe.getMessage());
        verify(usersDb, times(0)).removeUser(instructorToDelete);
    }

    @Test
    public void testUpdateToEnsureValidityOfInstructorsForTheCourse_lastModifyInstructorPrivilege_shouldPreserve() {
        instructor.setRole(InstructorPermissionRole.CUSTOM);
        InstructorPrivileges privileges = new InstructorPrivileges(instructor.getId());
        privileges.updatePrivilege(Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR, false);
        when(instructorPermissionsLogic.getInstructorPrivileges(instructor)).thenReturn(privileges);

        usersLogic.updateToEnsureValidityOfInstructorsForTheCourse(instructor);

        assertFalse(instructorPermissionsLogic.getInstructorPrivileges(instructor)
                .isAllowedForPrivilege(Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR));
    }

    private Instructor createRegisteredInstructor(String email, boolean isDisplayedToStudents) {
        Instructor instructor = createInstructor(email, isDisplayedToStudents);
        instructor.setAccount(getTypicalAccount());
        return instructor;
    }

    private Instructor createUnregisteredInstructor(String email, boolean isDisplayedToStudents) {
        Instructor instructor = createInstructor(email, isDisplayedToStudents);
        instructor.setAccount(null);
        return instructor;
    }

    private Instructor createInstructor(String email, boolean isDisplayedToStudents) {
        Instructor instructor = getTypicalInstructor();
        instructor.setCourse(course);
        instructor.setEmail(email);
        instructor.setDisplayedToStudents(isDisplayedToStudents);
        return instructor;
    }

}
