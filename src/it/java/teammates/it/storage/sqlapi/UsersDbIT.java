package teammates.it.storage.sqlapi;

import static org.junit.Assert.assertSame;

import org.testng.annotations.BeforeTest;
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
import teammates.storage.sqlentity.Team;

/**
 * SUT: {@link UsersDb}.
 */
public class UsersDbIT extends BaseTestCaseWithSqlDatabaseAccess {

    private final UsersDb usersDb = UsersDb.inst();
    private final CoursesDb coursesDb = CoursesDb.inst();
    private Course course;
    private Instructor instructor;

    @BeforeTest
    public void setUp() throws EntityAlreadyExistsException, InvalidParametersException {
        HibernateUtil.beginTransaction();

        course = new Course("course-id", "course-name", Const.DEFAULT_TIME_ZONE, "institute");
        coursesDb.createCourse(course);

        Section section = new Section(course, "section-name");
        HibernateUtil.persist(section);

        Team team = new Team(section, "team-name");
        HibernateUtil.persist(team);

        InstructorPrivileges instructorPrivileges =
                new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        InstructorPermissionRole role = InstructorPermissionRole
                .getEnum(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        instructor = new Instructor(course, team, "valid.name", "valid@email.tmt",
                false, Const.DEFAULT_DISPLAY_NAME_FOR_INSTRUCTOR, role, instructorPrivileges);
        usersDb.createInstructor(instructor);

        HibernateUtil.flushSession();
    }

    @Test
    public void testGetInstructor() throws EntityAlreadyExistsException, InvalidParametersException {
        ______TS("success: gets an instructor that already exists");
        Instructor actualInstructor = usersDb.getInstructor(instructor.getId());
        assertSame(instructor, actualInstructor);

        ______TS("success: gets an instructor that does not exist");
        Integer nonExistentId = instructor.getId() + 1000;
        Instructor nonExistentInstructor = usersDb.getInstructor(nonExistentId);
        assertNull(nonExistentInstructor);
    }

    /*
    @Test
    public void testGetStudent() throws InvalidParametersException, EntityAlreadyExistsException {
        ______TS("success: gets a student that already exists");

        Course course = mock(Course.class);
        Team team = mock(Team.class);
        Student newStudent = new Student(course, team, "student-name", "student-email", "comments");

        newStudent.setId(SHARED_ID);
        usersDb.createStudent(newStudent);

        Integer studentId = newStudent.getId();
        Student actualstudent = usersDb.getStudent(studentId);
        verifyEquals(newStudent, actualstudent);

        ______TS("success: gets a student that does not exist");
        Integer nonExistentId = Integer.MIN_VALUE;
        Student nonExistentstudent = usersDb.getStudent(nonExistentId);
        assertNull(nonExistentstudent);
    }
     */
}
