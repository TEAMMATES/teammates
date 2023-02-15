package teammates.storage.sqlapi;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.exception.EntityAlreadyExistsException;
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
    public void createCourseDoesNotExist() throws InvalidParametersException, EntityAlreadyExistsException {
        Course c = new Course("course-id", "course-name", null, "institute");

        when(session.get(Course.class, "course-id")).thenReturn(null);

        coursesDb.createCourse(c);

        verify(session, times(1)).persist(c);
    }

    @Test
    public void createCourseAlreadyExists() {
        Course c = new Course("course-id", "course-name", null, "institute");

        when(session.get(Course.class, "course-id")).thenReturn(c);

        EntityAlreadyExistsException ex = assertThrows(EntityAlreadyExistsException.class, () -> coursesDb.createCourse(c));
        assertEquals(ex.getMessage(), "Trying to create an entity that exists: " + c.toString());
        verify(session, never()).persist(c);
    }
}
