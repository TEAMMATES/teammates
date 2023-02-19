package teammates.storage.sqlapi;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.Course;
import teammates.test.BaseTestCase;

/**
 * SUT: {@code CoursesDb}.
 */
public class CoursesDbTest extends BaseTestCase {

    private CoursesDb coursesDb = CoursesDb.inst();

    private Session session;

    @BeforeMethod
    public void setUp() {
        session = mock(Session.class);
        SessionFactory sessionFactory = mock(SessionFactory.class);
        HibernateUtil.setSessionFactory(sessionFactory);
        when(sessionFactory.getCurrentSession()).thenReturn(session);
    }

    @Test
    public void testCreateCourse_courseDoesNotExist_success()
            throws InvalidParametersException, EntityAlreadyExistsException {
        Course c = new Course("course-id", "course-name", null, "institute");

        when(session.get(Course.class, "course-id")).thenReturn(null);

        coursesDb.createCourse(c);

        verify(session, times(1)).persist(c);
    }

    @Test
    public void testCreateCourse_courseAlreadyExists_throwsEntityAlreadyExistsException() {
        Course c = new Course("course-id", "course-name", null, "institute");

        when(session.get(Course.class, "course-id")).thenReturn(c);

        EntityAlreadyExistsException ex = assertThrows(EntityAlreadyExistsException.class,
                () -> coursesDb.createCourse(c));
        assertEquals(ex.getMessage(), "Trying to create an entity that exists: " + c.toString());
        verify(session, never()).persist(c);
    }

    @Test
    public void testGetCourse_courseAlreadyExists_success() {
        Course c = new Course("course-id", "course-name", null, "institute");

        when(session.get(Course.class, "course-id")).thenReturn(c);
        Course courseFetched = coursesDb.getCourse("course-id");

        assertEquals(c, courseFetched);
    }

    @Test
    public void testGetCourse_courseDoesNotExist_returnsNull() {
        when(session.get(Course.class, "course-id-not-in-db")).thenReturn(null);
        Course courseFetched = coursesDb.getCourse("course-id-not-in-db");

        assertEquals(courseFetched, null);
    }

    @Test
    public void testUpdateCourse_courseDoesNotExist_throwsEntityDoesNotExistException() {
        Course c = new Course("course-id-not-in-db", "course-name", null, "institute");

        when(session.get(Course.class, "course-id-not-in-db")).thenReturn(null);
        EntityDoesNotExistException ex = assertThrows(EntityDoesNotExistException.class,
                () -> coursesDb.updateCourse(c));

        assertEquals(ex.getMessage(), "Trying to update non-existent Entity: ");
        verify(session, never()).merge(c);
    }

    @Test
    public void testUpdateCourse_courseExists_success()
            throws InvalidParametersException, EntityDoesNotExistException {
        Course c = new Course("course-id", "new-course-name", null, "institute");

        when(session.get(Course.class, "course-id")).thenReturn(c);

        coursesDb.updateCourse(c);

        verify(session, times(1)).merge(c);
    }

    @Test
    public void testSoftDeleteCourse_courseDoesNotExist_throwsEntityDoesNotExistException() {
        Course c = new Course("course-id-not-in-db", "course-name", null, "institute");
        Instant deletedAt = c.getDeletedAt();

        when(session.get(Course.class, "course-id-not-in-db")).thenReturn(null);
        EntityDoesNotExistException ex = assertThrows(EntityDoesNotExistException.class,
                () -> coursesDb.softDeleteCourse("course-id-not-in-db"));

        assertEquals(ex.getMessage(), "Trying to update non-existent Entity: ");
        assertEquals(deletedAt, c.getDeletedAt());
    }

    @Test
    public void testSoftDeleteCourse_courseExists_success() throws EntityDoesNotExistException {
        Course c = new Course("course-id", "new-course-name", null, "institute");
        Instant deletedAt = c.getDeletedAt();

        when(session.get(Course.class, "course-id")).thenReturn(c);

        Instant deletedTime = coursesDb.softDeleteCourse("course-id");

        assertNotEquals(deletedAt, deletedTime); // assert that deletedAt changes after softDeleteCourse() is called.
    }

    @Test
    public void testRestoreDeletedCourse_courseDoesNotExist_throwsEntityDoesNotExistException() {
        Instant initialDeletedAt = Instant.parse("2011-01-01T00:00:00Z");
        Course c = new Course("course-id-not-in-db", "course-name", null, "institute");
        c.setDeletedAt(initialDeletedAt);

        assertEquals(c.getDeletedAt(), initialDeletedAt);

        when(session.get(Course.class, "course-id-not-in-db")).thenReturn(null);
        EntityDoesNotExistException ex = assertThrows(EntityDoesNotExistException.class,
                () -> coursesDb.restoreDeletedCourse("course-id-not-in-db"));

        assertEquals(ex.getMessage(), "Trying to update non-existent Entity: ");
        assertEquals(c.getDeletedAt(), initialDeletedAt);
    }

    @Test
    public void testRestoreDeletedCourse_courseExists_success() throws EntityDoesNotExistException {
        Course c = new Course("course-id", "new-course-name", null, "institute");
        c.setDeletedAt(Instant.parse("2011-01-01T00:00:00Z"));

        assertNotEquals(c.getDeletedAt(), null);

        when(session.get(Course.class, "course-id")).thenReturn(c);

        coursesDb.restoreDeletedCourse("course-id");

        assertEquals(c.getDeletedAt(), null);
    }

    @Test
    public void testDeleteCourse_courseDoesNotExist_failsSilently() {
        Course c = new Course("course-id-not-in-db", "course-name", null, "institute");

        when(session.get(Course.class, "course-id-not-in-db")).thenReturn(null);
        coursesDb.deleteCourse("course-id-not-in-db");

        verify(session, never()).remove(c);
    }

    @Test
    public void testDeleteCourse_courseExists_success() {
        Course c = new Course("course-id", "new-course-name", null, "institute");

        when(session.get(Course.class, "course-id")).thenReturn(c);

        coursesDb.deleteCourse("course-id");

        verify(session, times(1)).remove(c);
    }
}
