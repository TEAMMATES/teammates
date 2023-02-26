package teammates.storage.sqlapi;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import org.mockito.MockedStatic;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPermissionRole;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.storage.sqlentity.Team;
import teammates.test.BaseTestCase;

/**
 * SUT: {@code UsersDb}.
 */
public class UsersDbTest extends BaseTestCase {

    private UsersDb usersDb = UsersDb.inst();

    private MockedStatic<HibernateUtil> mockHibernateUtil;

    private static final int USER_ID = 1;

    @BeforeMethod
    public void setUpMethod() {
        mockHibernateUtil = mockStatic(HibernateUtil.class);
    }

    @AfterMethod
    public void teardownMethod() {
        mockHibernateUtil.close();
    }

    @Test
    public void testGetInstructor_instructorIdPresent_success() {
        Course course = mock(Course.class);
        Team team = mock(Team.class);
        InstructorPrivileges instructorPrivileges = mock(InstructorPrivileges.class);
        Instructor instructor = new Instructor(course, team, "instructor-name", "instructor-email",
                false, "instructor-display-name",
                InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER, instructorPrivileges);

        instructor.setId(USER_ID);

        mockHibernateUtil
                .when(() -> HibernateUtil.get(Instructor.class, instructor.getId()))
                .thenReturn(instructor);

        Instructor actualInstructor = usersDb.getInstructor(instructor.getId());

        assertEquals(instructor, actualInstructor);
    }

    @Test
    public void testGetStudent_studentIdPresent_success() {
        Course course = mock(Course.class);
        Team team = mock(Team.class);
        Student student = new Student(course, team, "student-name", "student-email", "comments");

        student.setId(USER_ID);

        mockHibernateUtil
                .when(() -> HibernateUtil.get(Student.class, student.getId()))
                .thenReturn(student);

        Student actualStudent = usersDb.getStudent(student.getId());

        assertEquals(student, actualStudent);
    }
}
