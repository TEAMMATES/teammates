package teammates.storage.sqlapi;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;

import java.util.ArrayList;

import org.mockito.MockedStatic;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPermissionRole;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.test.BaseTestCase;

/**
 * SUT: {@code UsersDb}.
 */
public class UsersDbTest extends BaseTestCase {

    private UsersDb usersDb;

    private MockedStatic<HibernateUtil> mockHibernateUtil;

    @BeforeMethod
    public void setUp() {
        mockHibernateUtil = mockStatic(HibernateUtil.class);
        usersDb = spy(UsersDb.class);
    }

    @AfterMethod
    public void teardown() {
        mockHibernateUtil.close();
    }

    @Test
    public void testCreateInstructor_validInstructorThatDoesNotExist_success()
            throws InvalidParametersException, EntityAlreadyExistsException {
        Course course = new Course("course-id", "course-name", Const.DEFAULT_TIME_ZONE, "institute");
        InstructorPrivileges instructorPrivileges = mock(InstructorPrivileges.class);
        InstructorPermissionRole role = InstructorPermissionRole
                .getEnum(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        Instructor newInstructor = spy(new Instructor(course, "instructor-name", "valid@email.tmt",
                false, Const.DEFAULT_DISPLAY_NAME_FOR_INSTRUCTOR, role, instructorPrivileges));

        doReturn(new ArrayList<>()).when(newInstructor).getInvalidityInfo();
        doReturn(false).when(usersDb).hasExistingInstructor(anyString(), anyString());

        usersDb.createInstructor(newInstructor);

        mockHibernateUtil.verify(() -> HibernateUtil.persist(newInstructor));
    }

    @Test
    public void testCreateInstructor_invalidInstructorWithEmptyCourseId_throwsInvalidParametersException() {
        Course course = new Course("", "course-name", Const.DEFAULT_TIME_ZONE, "institute");
        InstructorPrivileges instructorPrivileges = mock(InstructorPrivileges.class);
        InstructorPermissionRole role = InstructorPermissionRole
                .getEnum(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        Instructor newInstructor = new Instructor(course, "instructor-name", "valid@email.tmt",
                false, Const.DEFAULT_DISPLAY_NAME_FOR_INSTRUCTOR, role, instructorPrivileges);

        assertThrows(InvalidParametersException.class, () -> usersDb.createInstructor(newInstructor));

        mockHibernateUtil.verify(() -> HibernateUtil.persist(newInstructor), never());
    }

    @Test
    public void testCreateInstructor_validInstructorThatExists_throwsEntityAlreadyExistsException() {
        Course course = new Course("course-id", "course-name", Const.DEFAULT_TIME_ZONE, "institute");
        InstructorPrivileges instructorPrivileges = mock(InstructorPrivileges.class);
        InstructorPermissionRole role = InstructorPermissionRole
                .getEnum(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        Instructor newInstructor = spy(new Instructor(course, "instructor-name", "valid@email.tmt",
                false, Const.DEFAULT_DISPLAY_NAME_FOR_INSTRUCTOR, role, instructorPrivileges));

        doReturn(new ArrayList<>()).when(newInstructor).getInvalidityInfo();
        doReturn(true).when(usersDb).hasExistingInstructor(anyString(), anyString());

        assertThrows(EntityAlreadyExistsException.class,
                () -> usersDb.createInstructor(newInstructor));

        mockHibernateUtil.verify(() -> HibernateUtil.persist(newInstructor), never());
    }

    @Test
    public void testCreateStudent_validStudentThatDoesNotExist_success()
            throws InvalidParametersException, EntityAlreadyExistsException {
        Course course = new Course("course-id", "course-name", Const.DEFAULT_TIME_ZONE, "institute");
        Student newStudent = spy(new Student(course, "student-name", "valid@email.tmt", "comments"));

        doReturn(new ArrayList<>()).when(newStudent).getInvalidityInfo();
        doReturn(false).when(usersDb).hasExistingStudent(anyString(), anyString());

        usersDb.createStudent(newStudent);

        mockHibernateUtil.verify(() -> HibernateUtil.persist(newStudent));
    }

    @Test
    public void testCreateStudent_invalidStudentWithInvalidEmail_throwsInvalidParametersException() {
        Course course = new Course("", "course-name", Const.DEFAULT_TIME_ZONE, "institute");
        Student newStudent = new Student(course, "student-name", "invalid-email", "comments");

        assertThrows(InvalidParametersException.class, () -> usersDb.createStudent(newStudent));

        mockHibernateUtil.verify(() -> HibernateUtil.persist(newStudent), never());
    }

    @Test
    public void testCreateStudent_validStudentThatExists_throwsEntityAlreadyExistsException() {
        Course course = new Course("course-id", "course-name", Const.DEFAULT_TIME_ZONE, "institute");
        Student newStudent = spy(new Student(course, "student-name", "valid@email.tmt", "comments"));

        doReturn(new ArrayList<>()).when(newStudent).getInvalidityInfo();
        doReturn(true).when(usersDb).hasExistingStudent(anyString(), anyString());

        assertThrows(EntityAlreadyExistsException.class,
                () -> usersDb.createStudent(newStudent));

        mockHibernateUtil.verify(() -> HibernateUtil.persist(newStudent), never());
    }

    @Test
    public void testGetInstructor_instructorIdPresent_success() {
        Course course = mock(Course.class);
        InstructorPrivileges instructorPrivileges = mock(InstructorPrivileges.class);
        Instructor instructor = new Instructor(course, "instructor-name", "instructor-email",
                false, "instructor-display-name",
                InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER, instructorPrivileges);

        mockHibernateUtil
                .when(() -> HibernateUtil.get(Instructor.class, instructor.getId()))
                .thenReturn(instructor);

        Instructor actualInstructor = usersDb.getInstructor(instructor.getId());

        assertEquals(instructor, actualInstructor);
    }

    @Test
    public void testGetStudent_studentIdPresent_success() {
        Course course = mock(Course.class);
        Student student = new Student(course, "student-name", "student-email", "comments");

        mockHibernateUtil
                .when(() -> HibernateUtil.get(Student.class, student.getId()))
                .thenReturn(student);

        Student actualStudent = usersDb.getStudent(student.getId());

        assertEquals(student, actualStudent);
    }

    @Test
    public void testUpdateUser_userAsValidInstructor_success()
            throws InvalidParametersException, EntityDoesNotExistException {
        Course course = new Course("course-id", "course-name", Const.DEFAULT_TIME_ZONE, "institute");
        InstructorPrivileges instructorPrivileges = mock(InstructorPrivileges.class);
        InstructorPermissionRole role = InstructorPermissionRole
                .getEnum(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        Instructor newInstructor = spy(new Instructor(course, "instructor-name", "valid@email.tmt",
                false, Const.DEFAULT_DISPLAY_NAME_FOR_INSTRUCTOR, role, instructorPrivileges));

        doReturn(new ArrayList<>()).when(newInstructor).getInvalidityInfo();
        doReturn(false).when(usersDb).hasExistingInstructor(anyString(), anyString());

        usersDb.updateUser(newInstructor);

        mockHibernateUtil.verify(() -> HibernateUtil.merge(newInstructor));
    }

    @Test
    public void testUpdateUser_userAsValidStudent_success()
            throws InvalidParametersException, EntityDoesNotExistException {
        Course course = new Course("course-id", "course-name", Const.DEFAULT_TIME_ZONE, "institute");
        Student newStudent = spy(new Student(course, "student-name", "valid@email.tmt", "comments"));

        doReturn(new ArrayList<>()).when(newStudent).getInvalidityInfo();
        doReturn(false).when(usersDb).hasExistingStudent(anyString(), anyString());

        usersDb.updateUser(newStudent);

        mockHibernateUtil.verify(() -> HibernateUtil.merge(newStudent));
    }

    @Test
    public void testDeleteUser_userNotNull_success() {
        Student student = mock(Student.class);

        usersDb.deleteUser(student);

        mockHibernateUtil.verify(() -> HibernateUtil.remove(student));
    }

    @Test
    public void testDeleteUser_userNull_shouldFailSilently() {
        usersDb.deleteUser(null);

        mockHibernateUtil.verify(() -> HibernateUtil.remove(any()), never());
    }
}
