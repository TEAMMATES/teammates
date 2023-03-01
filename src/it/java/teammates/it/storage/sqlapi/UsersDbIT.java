package teammates.it.storage.sqlapi;

import static org.mockito.Mockito.mock;

import org.testng.annotations.AfterTest;
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

    private static final int SHARED_ID = 1;

    private final UsersDb usersDb = UsersDb.inst();
    private final CoursesDb coursesDb = CoursesDb.inst();
    private Course course;
    private Section section;
    private Team team;

    @BeforeTest
    public void setUp() throws EntityAlreadyExistsException, InvalidParametersException {
        HibernateUtil.beginTransaction();

        course = new Course("course-id", "course-name", null, "institute");
        coursesDb.createCourse(course);

        section = new Section(course, "section-name");
        section.setId(1);
        HibernateUtil.getCurrentSession().persist(section);

        team = new Team(section, "team-name");
        team.setId(1);
        HibernateUtil.getCurrentSession().persist(team);

        HibernateUtil.commitTransaction();
    }

    @AfterTest
    public void tearDown() {
        coursesDb.deleteCourse(course);

        HibernateUtil.getCurrentSession().remove(section);
        HibernateUtil.getCurrentSession().remove(team);
    }

    @Test
    public void testGetInstructor() throws EntityAlreadyExistsException, InvalidParametersException {
        ______TS("success: gets an instructor that already exists");

        InstructorPrivileges instructorPrivileges =
                new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        InstructorPermissionRole role = InstructorPermissionRole
                .getEnum(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        Instructor newInstructor = new Instructor(coursesDb.getCourse("course-id"), team, "valid.name", "valid@email.tmt",
                false, Const.DEFAULT_DISPLAY_NAME_FOR_INSTRUCTOR, role, instructorPrivileges);

        newInstructor.setId(SHARED_ID);

//        HibernateUtil.getCurrentSession().merge(newInstructor);
//        HibernateUtil.getCurrentSession().getTransaction().commit();

        System.out.println("course " + coursesDb.getCourse("course-id"));
        System.out.println("team " + HibernateUtil.getCurrentSession().get(Team.class, 1));
        System.out.println("section " + HibernateUtil.getCurrentSession().get(Section.class, 1));

        usersDb.createInstructor(newInstructor);

//        HibernateUtil.commitTransaction();

        Integer instructorId = newInstructor.getId();
        Instructor actualInstructor = usersDb.getInstructor(instructorId);
        verifyEquals(newInstructor, actualInstructor);

        ______TS("success: gets an instructor that does not exist");
        Integer nonExistentId = Integer.MIN_VALUE;
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
