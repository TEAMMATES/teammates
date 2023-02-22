package teammates.it.test;

import java.util.UUID;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import teammates.common.util.HibernateUtil;
import teammates.common.util.JsonUtils;
import teammates.sqllogic.api.Logic;
import teammates.sqllogic.core.LogicStarter;
import teammates.storage.sqlentity.BaseEntity;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Notification;
import teammates.test.BaseTestCase;

/**
 * Base test case for tests that access the database.
 */
@Test(singleThreaded = true)
public class BaseTestCaseWithSqlDatabaseAccess extends BaseTestCase {
    /**
     * Test container.
     */
    protected static final PostgreSQLContainer<?> PGSQL = new PostgreSQLContainer<>("postgres:15.1-alpine");

    private final Logic logic = Logic.inst();

    @BeforeSuite
    public static void setUpClass() throws Exception {
        PGSQL.start();
        // Temporarily disable migration utility
        // DbMigrationUtil.resetDb(PGSQL.getJdbcUrl(), PGSQL.getUsername(), PGSQL.getPassword());
        HibernateUtil.buildSessionFactory(PGSQL.getJdbcUrl(), PGSQL.getUsername(), PGSQL.getPassword());

        LogicStarter.initializeDependencies();
    }

    @AfterSuite
    public static void tearDownClass() throws Exception {
        PGSQL.close();
    }

    @BeforeMethod
    public void setUp() throws Exception {
        HibernateUtil.beginTransaction();
    }

    @AfterMethod
    public void tearDown() {
        HibernateUtil.rollbackTransaction();
    }

    /**
     * Verifies that two entities are equal.
     */
    protected void verifyEquals(BaseEntity expected, BaseEntity actual) {
        if (expected instanceof Course) {
            Course expectedCourse = (Course) expected;
            Course actualCourse = (Course) actual;
            equalizeIrrelevantData(expectedCourse, actualCourse);
            assertEquals(JsonUtils.toJson(expectedCourse), JsonUtils.toJson(actualCourse));
        } else if (expected instanceof Notification) {
            Notification expectedNotification = (Notification) expected;
            Notification actualNotification = (Notification) actual;
            equalizeIrrelevantData(expectedNotification, actualNotification);
            assertEquals(JsonUtils.toJson(expectedNotification), JsonUtils.toJson(actualNotification));
        }
    }

    /**
     * Verifies that the given entity is present in the database.
     */
    protected void verifyPresentInDatabase(BaseEntity expected) {
        BaseEntity actual = getEntity(expected);
        verifyEquals(expected, actual);
    }

    private BaseEntity getEntity(BaseEntity entity) {
        if (entity instanceof Course) {
            return logic.getCourse(((Course) entity).getId());
        } else {
            throw new RuntimeException("Unknown entity type!");
        }
    }

    private void equalizeIrrelevantData(Course expected, Course actual) {
        // Ignore time field as it is stamped at the time of creation in testing
        expected.setCreatedAt(actual.getCreatedAt());
        expected.setUpdatedAt(actual.getUpdatedAt());
    }

    private void equalizeIrrelevantData(Notification expected, Notification actual) {
        // Ignore time field as it is stamped at the time of creation in testing
        expected.setCreatedAt(actual.getCreatedAt());
        expected.setUpdatedAt(actual.getUpdatedAt());
    }

    /**
     * Generates a UUID that is different from the given {@code uuid}.
     */
    protected UUID generateDifferentUuid(UUID uuid) {
        UUID ret = UUID.randomUUID();
        while (ret.equals(uuid)) {
            ret = UUID.randomUUID();
        }
        return ret;
    }
}
