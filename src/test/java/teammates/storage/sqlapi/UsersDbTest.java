package teammates.storage.sqlapi;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;

import org.mockito.MockedStatic;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPermissionRole;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.exception.EntityAlreadyExistsException;
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

    private UsersDb usersDb = UsersDb.inst();

    private MockedStatic<HibernateUtil> mockHibernateUtil;

    @BeforeMethod
    public void setUp() {
        mockHibernateUtil = mockStatic(HibernateUtil.class);
    }

    @AfterMethod
    public void teardown() {
        mockHibernateUtil.close();
    }

    private Instructor getTypicalInstructor() {
        Course course = new Course("course-id", "course-name", Const.DEFAULT_TIME_ZONE, "institute");
        InstructorPrivileges instructorPrivileges =
                new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        InstructorPermissionRole role = InstructorPermissionRole
                .getEnum(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);

        return new Instructor(course, "instructor-name", "valid@teammates.tmt",
                false, Const.DEFAULT_DISPLAY_NAME_FOR_INSTRUCTOR, role, instructorPrivileges);
    }

    private Student getTypicalStudent() {
        Course course = new Course("course-id", "course-name", Const.DEFAULT_TIME_ZONE, "institute");
        return new Student(course, "student-name", "valid@teammates.tmt", "comments");
    }

    @Test
    public void testCreateInstructor_validInstructorDoesNotExist_success()
            throws InvalidParametersException, EntityAlreadyExistsException {
        Instructor newInstructor = getTypicalInstructor();

        usersDb.createInstructor(newInstructor);

        mockHibernateUtil.verify(() -> HibernateUtil.persist(newInstructor));
    }

    @Test
    public void testCreateStudent_studentDoesNotExist_success()
            throws InvalidParametersException, EntityAlreadyExistsException {
        Student newStudent = getTypicalStudent();

        usersDb.createStudent(newStudent);

        mockHibernateUtil.verify(() -> HibernateUtil.persist(newStudent));
    }

    @Test
    public void testCreateStudent_studentWithInvalidEmail_throwsInvalidParametersException() {
        Student newStudent = getTypicalStudent();
        newStudent.setEmail("invalid-email");

        assertThrows(InvalidParametersException.class, () -> usersDb.createStudent(newStudent));

        mockHibernateUtil.verify(() -> HibernateUtil.persist(newStudent), never());
    }

    @Test
    public void testGetInstructor_instructorIdPresent_success() {
        Instructor instructor = getTypicalInstructor();

        mockHibernateUtil
                .when(() -> HibernateUtil.get(Instructor.class, instructor.getId()))
                .thenReturn(instructor);

        Instructor actualInstructor = usersDb.getInstructor(instructor.getId());

        assertEquals(instructor, actualInstructor);
    }

    @Test
    public void testGetStudent_studentIdPresent_success() {
        Student student = getTypicalStudent();

        mockHibernateUtil
                .when(() -> HibernateUtil.get(Student.class, student.getId()))
                .thenReturn(student);

        Student actualStudent = usersDb.getStudent(student.getId());

        assertEquals(student, actualStudent);
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
