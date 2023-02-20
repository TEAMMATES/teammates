package teammates.storage.sqlapi;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;

import org.mockito.MockedStatic;
import org.testng.annotations.AfterMethod;
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

    private MockedStatic<HibernateUtil> mockHibernateUtil;

    @BeforeMethod
    public void setUpMethod() {
        mockHibernateUtil = mockStatic(HibernateUtil.class);
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

        assertEquals(ex.getMessage(), "Trying to create an entity that exists: " + c.toString());
        mockHibernateUtil.verify(() -> HibernateUtil.persist(c), never());
    }
}
