package teammates.storage.sqlapi;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;

import org.mockito.MockedStatic;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Section;
import teammates.storage.sqlentity.Team;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link CoursesDb}.
 */
public class CoursesDbTest extends BaseTestCase {

    private CoursesDb coursesDb;

    private MockedStatic<HibernateUtil> mockHibernateUtil;

    @BeforeMethod
    public void setUpMethod() {
        mockHibernateUtil = mockStatic(HibernateUtil.class);
        coursesDb = spy(CoursesDb.class);
    }

    @AfterMethod
    public void teardownMethod() {
        mockHibernateUtil.close();
    }

    @Test
    public void testCreateCourse_courseDoesNotExist_success() {
        Course c = new Course("course-id", "course-name", null, "institute");

        coursesDb.createCourse(c);

        mockHibernateUtil.verify(() -> HibernateUtil.persist(c));
    }

    @Test
    public void testGetCourse_courseDoesNotExist_returnsNull() {
        mockHibernateUtil.when(() -> HibernateUtil.get(Course.class, "course-id-not-in-db")).thenReturn(null);
        Course courseFetched = coursesDb.getCourse("course-id-not-in-db");

        mockHibernateUtil.verify(() -> HibernateUtil.get(Course.class, "course-id-not-in-db"));
        assertNull(courseFetched);
    }

    @Test
    public void testDeleteCourse_courseExists_success() {
        Course c = new Course("course-id", "new-course-name", null, "institute");

        coursesDb.deleteCourse(c);

        mockHibernateUtil.verify(() -> HibernateUtil.remove(c));
    }

    @Test
    public void testCreateSection_success() {
        Course c = new Course("course-id", "new-course-name", null, "institute");
        Section s = new Section(c, "new-section");

        coursesDb.createSection(s);

        mockHibernateUtil.verify(() -> HibernateUtil.persist(s));
    }

    @Test
    public void testCreateTeam_success() {
        Course c = new Course("course-id", "new-course-name", null, "institute");
        Section s = new Section(c, "new-section");
        Team t = new Team(s, "new-team");

        coursesDb.createTeam(t);

        mockHibernateUtil.verify(() -> HibernateUtil.persist(t));
    }
}
