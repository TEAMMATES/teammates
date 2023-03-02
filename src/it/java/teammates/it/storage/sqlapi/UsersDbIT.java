package teammates.it.storage.sqlapi;

import static org.junit.Assert.assertSame;

import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPermissionRole;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.it.test.BaseTestCaseWithSqlDatabaseAccess;
import teammates.storage.sqlapi.CoursesDb;
import teammates.storage.sqlapi.UsersDb;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Section;
import teammates.storage.sqlentity.Student;
import teammates.storage.sqlentity.Team;

/**
 * SUT: {@link UsersDb}.
 */
public class UsersDbIT extends BaseTestCaseWithSqlDatabaseAccess {

    private final UsersDb usersDb = UsersDb.inst();
    private final CoursesDb coursesDb = CoursesDb.inst();

    private Course course;
    private Instructor instructor;
    private Student student;

    @BeforeMethod
    public void setUp() throws EntityAlreadyExistsException, InvalidParametersException {
        HibernateUtil.beginTransaction();

        course = new Course("course-id", "course-name", Const.DEFAULT_TIME_ZONE, "institute");
        coursesDb.createCourse(course);

        Section section = new Section(course, "section-name");
        HibernateUtil.persist(section);

        Team team = new Team(section, "team-name");
        HibernateUtil.persist(team);

        generateInstructor();
        usersDb.createInstructor(instructor);

        student = new Student(course, "student-name", "valid@email.tmt", "comments");
        usersDb.createStudent(student);

        HibernateUtil.flushSession();
    }

    @Test
    public void testGetInstructor() {
        ______TS("success: gets an instructor that already exists");
        Instructor actualInstructor = usersDb.getInstructor(instructor.getId());
        assertSame(instructor, actualInstructor);

        ______TS("success: gets an instructor that does not exist");
        UUID nonExistentId = UUID.fromString("00000000-0000-1000-0000-000000000000");
        Instructor nonExistentInstructor = usersDb.getInstructor(nonExistentId);
        assertNull(nonExistentInstructor);
    }

    @Test
    public void testGetStudent() {
        ______TS("success: gets a student that already exists");
        Student actualstudent = usersDb.getStudent(student.getId());
        assertSame(student, actualstudent);

        ______TS("success: gets a student that does not exist");
        UUID nonExistentId = UUID.fromString("00000000-0000-1000-0000-000000000000");
        Student nonExistentstudent = usersDb.getStudent(nonExistentId);
        assertNull(nonExistentstudent);
    }

    private void generateInstructor() {
        InstructorPrivileges instructorPrivileges =
                new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        InstructorPermissionRole role = InstructorPermissionRole
                .getEnum(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        instructor = new Instructor(course, "instructor-name", "valid@email.tmt",
                false, Const.DEFAULT_DISPLAY_NAME_FOR_INSTRUCTOR, role, instructorPrivileges);
    }
}
