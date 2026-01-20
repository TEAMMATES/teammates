package teammates.storage.sqlapi;

import static org.mockito.Mockito.mockStatic;

import org.mockito.MockedStatic;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.Account;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.DeadlineExtension;
import teammates.test.BaseTestCase;

/**
 * SUT: {@code EntitiesDb}.
 */
public class EntitiesDbTest extends BaseTestCase {

    private EntitiesDb entitiesDb;
    private MockedStatic<HibernateUtil> mockHibernateUtil;

    @BeforeMethod
    public void setUpMethod() {
        mockHibernateUtil = mockStatic(HibernateUtil.class);
        entitiesDb = new EntitiesDb();
    }

    @AfterMethod
    public void teardownMethod() {
        mockHibernateUtil.close();
    }

    @Test
    public void testMerge_success() {
        Account account = getTypicalAccount();
        mockHibernateUtil.when(() -> HibernateUtil.merge(account)).thenReturn(account);

        Account result = entitiesDb.merge(account);

        mockHibernateUtil.verify(() -> HibernateUtil.merge(account));
        assertEquals(account, result);
    }

    @Test
    public void testMerge_withCourse_success() {
        Course course = getTypicalCourse();
        mockHibernateUtil.when(() -> HibernateUtil.merge(course)).thenReturn(course);

        Course result = entitiesDb.merge(course);

        mockHibernateUtil.verify(() -> HibernateUtil.merge(course));
        assertEquals(course, result);
    }

    @Test
    public void testMerge_nullEntity_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> entitiesDb.merge(null));
    }

    @Test
    public void testPersist_success() {
        Account account = getTypicalAccount();

        entitiesDb.persist(account);

        mockHibernateUtil.verify(() -> HibernateUtil.persist(account));
    }

    @Test
    public void testPersist_withDeadlineExtension_success() {
        DeadlineExtension de = getTypicalDeadlineExtensionStudent();

        entitiesDb.persist(de);

        mockHibernateUtil.verify(() -> HibernateUtil.persist(de));
    }

    @Test
    public void testPersist_nullEntity_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> entitiesDb.persist(null));
    }

    @Test
    public void testDelete_success() {
        Account account = getTypicalAccount();

        entitiesDb.delete(account);

        mockHibernateUtil.verify(() -> HibernateUtil.remove(account));
    }

    @Test
    public void testDelete_withCourse_success() {
        Course course = getTypicalCourse();

        entitiesDb.delete(course);

        mockHibernateUtil.verify(() -> HibernateUtil.remove(course));
    }

    @Test
    public void testDelete_nullEntity_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> entitiesDb.delete(null));
    }
}
