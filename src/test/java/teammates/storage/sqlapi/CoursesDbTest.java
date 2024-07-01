package teammates.storage.sqlapi;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;

import org.mockito.MockedStatic;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Section;
import teammates.storage.sqlentity.Team;
import teammates.test.BaseTestCase;

/**
 * SUT: {@code CoursesDb}.
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
    public void testCreateCourse_courseDoesNotExist_success()
            throws InvalidParametersException, EntityAlreadyExistsException {
        Course c = new Course("course-id", "course-name", null, "institute");

        coursesDb.createCourse(c);

        mockHibernateUtil.verify(() -> HibernateUtil.persist(c));
    }

    @Test
    public void testCreateCourse_courseAlreadyExists_throwsEntityAlreadyExistsException() {
        Course c = new Course("course-id", "course-name", null, "institute");

        mockHibernateUtil.when(() -> HibernateUtil.get(Course.class, "course-id")).thenReturn(c);

        EntityAlreadyExistsException ex = assertThrows(EntityAlreadyExistsException.class,
                () -> coursesDb.createCourse(c));

        assertEquals("Trying to create an entity that exists: " + c.toString(), ex.getMessage());
        mockHibernateUtil.verify(() -> HibernateUtil.persist(c), never());
    }

    @Test
    public void testGetCourse_courseAlreadyExists_success() {
        Course c = new Course("course-id", "course-name", null, "institute");

        mockHibernateUtil.when(() -> HibernateUtil.get(Course.class, "course-id")).thenReturn(c);
        Course courseFetched = coursesDb.getCourse("course-id");

        mockHibernateUtil.verify(() -> HibernateUtil.get(Course.class, "course-id"), times(1));
        assertEquals(c, courseFetched);
    }

    @Test
    public void testGetCourse_courseDoesNotExist_returnsNull() {
        mockHibernateUtil.when(() -> HibernateUtil.get(Course.class, "course-id-not-in-db")).thenReturn(null);
        Course courseFetched = coursesDb.getCourse("course-id-not-in-db");

        mockHibernateUtil.verify(() -> HibernateUtil.get(Course.class, "course-id-not-in-db"), times(1));
        assertNull(courseFetched);
    }

    @Test
    public void testDeleteCourse_courseExists_success() {
        Course c = new Course("course-id", "new-course-name", null, "institute");

        coursesDb.deleteCourse(c);

        mockHibernateUtil.verify(() -> HibernateUtil.remove(c));
    }

    @Test
    public void testUpdateCourse_courseInvalid_throwsInvalidParametersException() {
        Course c = new Course("", "new-course-name", null, "institute");

        assertThrows(InvalidParametersException.class, () -> coursesDb.updateCourse(c));

        mockHibernateUtil.verify(() -> HibernateUtil.merge(c), never());
    }

    @Test
    public void testUpdateCourse_courseDoesNotExist_throwsEntityDoesNotExistException() {
        Course c = new Course("course-id", "new-course-name", null, "institute");
        doReturn(null).when(coursesDb).getCourse(anyString());

        assertThrows(EntityDoesNotExistException.class, () -> coursesDb.updateCourse(c));

        mockHibernateUtil.verify(() -> HibernateUtil.merge(c), never());
    }

    @Test
    public void testUpdateCourse_success() throws InvalidParametersException, EntityDoesNotExistException {
        Course c = new Course("course-id", "new-course-name", null, "institute");
        doReturn(c).when(coursesDb).getCourse(anyString());
        mockHibernateUtil.when(() -> HibernateUtil.merge(c)).thenReturn(c);

        coursesDb.updateCourse(c);

        mockHibernateUtil.verify(() -> HibernateUtil.merge(c));
    }

    @Test
    public void testCreateSection_sectionInvalid_throwsInvalidParametersException() {
        Course c = new Course("course-id", "new-course-name", null, "institute");
        Section s = new Section(c, null);

        assertThrows(InvalidParametersException.class, () -> coursesDb.createSection(s));

        mockHibernateUtil.verify(() -> HibernateUtil.persist(s), never());
    }

    @Test
    public void testCreateSection_sectionAlreadyExists_throwsEntityAlreadyExistsException() {
        Course c = new Course("course-id", "new-course-name", null, "institute");
        Section s = new Section(c, "new-section");

        doReturn(s).when(coursesDb).getSectionByName(anyString(), anyString());

        assertThrows(EntityAlreadyExistsException.class, () -> coursesDb.createSection(s));

        mockHibernateUtil.verify(() -> HibernateUtil.persist(s), never());
    }

    @Test
    public void testCreateSection_success() throws InvalidParametersException, EntityAlreadyExistsException {
        Course c = new Course("course-id", "new-course-name", null, "institute");
        Section s = new Section(c, "new-section");

        doReturn(null).when(coursesDb).getSectionByName(anyString(), anyString());

        coursesDb.createSection(s);

        mockHibernateUtil.verify(() -> HibernateUtil.persist(s));
    }

    @Test
    public void testCreateTeam_teamInvalid_throwsInvalidParametersException() {
        Course c = new Course("course-id", "new-course-name", null, "institute");
        Section s = new Section(c, "new-section");
        Team t = new Team(s, null);

        assertThrows(InvalidParametersException.class, () -> coursesDb.createTeam(t));

        mockHibernateUtil.verify(() -> HibernateUtil.persist(t), never());
    }

    @Test
    public void testCreateTeam_teamAlreadyExists_throwsEntityAlreadyExistsException() {
        Course c = new Course("course-id", "new-course-name", null, "institute");
        Section s = new Section(c, "new-section");
        Team t = new Team(s, "new-team");

        doReturn(t).when(coursesDb).getTeamByName(any(), anyString());

        assertThrows(EntityAlreadyExistsException.class, () -> coursesDb.createTeam(t));

        mockHibernateUtil.verify(() -> HibernateUtil.persist(t), never());
    }

    @Test
    public void testCreateTeam_success() throws InvalidParametersException, EntityAlreadyExistsException {
        Course c = new Course("course-id", "new-course-name", null, "institute");
        Section s = new Section(c, "new-section");
        Team t = new Team(s, "new-team");

        doReturn(null).when(coursesDb).getTeamByName(any(), anyString());

        coursesDb.createTeam(t);

        mockHibernateUtil.verify(() -> HibernateUtil.persist(t));
    }
}
