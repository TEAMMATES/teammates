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
import teammates.storage.sqlentity.Account;
import teammates.storage.sqlentity.AccountRequest;
import teammates.storage.sqlentity.BaseEntity;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.DeadlineExtension;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Notification;
import teammates.storage.sqlentity.ReadNotification;
import teammates.storage.sqlentity.Section;
import teammates.storage.sqlentity.Student;
import teammates.storage.sqlentity.Team;
import teammates.storage.sqlentity.UsageStatistics;
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
    protected static void setUpClass() throws Exception {
        PGSQL.start();
        // Temporarily disable migration utility
        // DbMigrationUtil.resetDb(PGSQL.getJdbcUrl(), PGSQL.getUsername(),
        // PGSQL.getPassword());
        HibernateUtil.buildSessionFactory(PGSQL.getJdbcUrl(), PGSQL.getUsername(), PGSQL.getPassword());

        LogicStarter.initializeDependencies();
    }

    @AfterSuite
    protected static void tearDownClass() throws Exception {
        PGSQL.close();
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        HibernateUtil.beginTransaction();
    }

    @AfterMethod
    protected void tearDown() {
        HibernateUtil.rollbackTransaction();
    }

    @Override
    protected String getTestDataFolder() {
        return TestProperties.TEST_DATA_FOLDER;
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
        } else if (expected instanceof DeadlineExtension) {
            DeadlineExtension expectedDeadlineExtension = (DeadlineExtension) expected;
            DeadlineExtension actualDeadlineExtension = (DeadlineExtension) actual;
            equalizeIrrelevantData(expectedDeadlineExtension, actualDeadlineExtension);
            assertEquals(JsonUtils.toJson(expectedDeadlineExtension), JsonUtils.toJson(actualDeadlineExtension));
        } else if (expected instanceof FeedbackSession) {
            FeedbackSession expectedSession = (FeedbackSession) expected;
            FeedbackSession actualSession = (FeedbackSession) actual;
            equalizeIrrelevantData(expectedSession, actualSession);
            assertEquals(JsonUtils.toJson(expectedSession), JsonUtils.toJson(actualSession));
        } else if (expected instanceof Notification) {
            Notification expectedNotification = (Notification) expected;
            Notification actualNotification = (Notification) actual;
            equalizeIrrelevantData(expectedNotification, actualNotification);
            assertEquals(JsonUtils.toJson(expectedNotification), JsonUtils.toJson(actualNotification));
        } else if (expected instanceof Account) {
            Account expectedAccount = (Account) expected;
            Account actualAccount = (Account) actual;
            equalizeIrrelevantData(expectedAccount, actualAccount);
            assertEquals(JsonUtils.toJson(expectedAccount), JsonUtils.toJson(actualAccount));
        } else if (expected instanceof AccountRequest) {
            AccountRequest expectedAccountRequest = (AccountRequest) expected;
            AccountRequest actualAccountRequest = (AccountRequest) actual;
            equalizeIrrelevantData(expectedAccountRequest, actualAccountRequest);
            assertEquals(JsonUtils.toJson(expectedAccountRequest), JsonUtils.toJson(actualAccountRequest));
        } else if (expected instanceof UsageStatistics) {
            UsageStatistics expectedUsageStatistics = (UsageStatistics) expected;
            UsageStatistics actualUsageStatistics = (UsageStatistics) actual;
            equalizeIrrelevantData(expectedUsageStatistics, actualUsageStatistics);
            assertEquals(JsonUtils.toJson(expectedUsageStatistics), JsonUtils.toJson(actualUsageStatistics));
        } else if (expected instanceof Instructor) {
            Instructor expectedInstructor = (Instructor) expected;
            Instructor actualInstructor = (Instructor) actual;
            equalizeIrrelevantData(expectedInstructor, actualInstructor);
            assertEquals(JsonUtils.toJson(expectedInstructor), JsonUtils.toJson(actualInstructor));
        } else if (expected instanceof Student) {
            Student expectedStudent = (Student) expected;
            Student actualStudent = (Student) actual;
            equalizeIrrelevantData(expectedStudent, actualStudent);
            assertEquals(JsonUtils.toJson(expectedStudent), JsonUtils.toJson(actualStudent));
        } else if (expected instanceof Section) {
            Section expectedSection = (Section) expected;
            Section actualSection = (Section) actual;
            equalizeIrrelevantData(expectedSection, actualSection);
            assertEquals(JsonUtils.toJson(expectedSection), JsonUtils.toJson(actualSection));
        } else if (expected instanceof Team) {
            Team expectedTeam = (Team) expected;
            Team actualTeam = (Team) actual;
            equalizeIrrelevantData(expectedTeam, actualTeam);
            assertEquals(JsonUtils.toJson(expectedTeam), JsonUtils.toJson(actualTeam));
        } else if (expected instanceof ReadNotification) {
            ReadNotification expectedReadNotification = (ReadNotification) expected;
            ReadNotification actualReadNotification = (ReadNotification) actual;
            equalizeIrrelevantData(expectedReadNotification, actualReadNotification);
        } else {
            fail("Unknown entity");
        }
    }

    /**
     * Verifies that the given entity is present in the database.
     */
    protected void verifyPresentInDatabase(BaseEntity expected) {
        assertNotNull(expected);
        BaseEntity actual = getEntity(expected);
        verifyEquals(expected, actual);
    }

    private BaseEntity getEntity(BaseEntity entity) {
        if (entity instanceof Course) {
            return logic.getCourse(((Course) entity).getId());
        } else if (entity instanceof FeedbackSession) {
            return logic.getFeedbackSession(((FeedbackSession) entity).getId());
        } else if (entity instanceof Account) {
            return logic.getAccount(((Account) entity).getId());
        } else if (entity instanceof Notification) {
            return logic.getNotification(((Notification) entity).getId());
        } else {
            throw new RuntimeException("Unknown entity type");
        }
    }

    private void equalizeIrrelevantData(Course expected, Course actual) {
        // Ignore time field as it is stamped at the time of creation in testing
        expected.setCreatedAt(actual.getCreatedAt());
        expected.setUpdatedAt(actual.getUpdatedAt());
    }

    private void equalizeIrrelevantData(DeadlineExtension expected, DeadlineExtension actual) {
        // Ignore time field as it is stamped at the time of creation in testing
        expected.setCreatedAt(actual.getCreatedAt());
        expected.setUpdatedAt(actual.getUpdatedAt());
    }

    private void equalizeIrrelevantData(FeedbackSession expected, FeedbackSession actual) {
        // Ignore time field as it is stamped at the time of creation in testing
        expected.setCreatedAt(actual.getCreatedAt());
        expected.setUpdatedAt(actual.getUpdatedAt());
    }

    private void equalizeIrrelevantData(Notification expected, Notification actual) {
        // Ignore time field as it is stamped at the time of creation in testing
        expected.setCreatedAt(actual.getCreatedAt());
        expected.setUpdatedAt(actual.getUpdatedAt());
    }

    private void equalizeIrrelevantData(Account expected, Account actual) {
        // Ignore time field as it is stamped at the time of creation in testing
        expected.setCreatedAt(actual.getCreatedAt());
        expected.setUpdatedAt(actual.getUpdatedAt());
    }

    private void equalizeIrrelevantData(AccountRequest expected, AccountRequest actual) {
        // Ignore time field as it is stamped at the time of creation in testing
        expected.setCreatedAt(actual.getCreatedAt());
        expected.setUpdatedAt(actual.getUpdatedAt());
    }

    private void equalizeIrrelevantData(UsageStatistics expected, UsageStatistics actual) {
        // Ignore time field as it is stamped at the time of creation in testing
        expected.setCreatedAt(actual.getCreatedAt());
    }

    private void equalizeIrrelevantData(Instructor expected, Instructor actual) {
        // Ignore time field as it is stamped at the time of creation in testing
        expected.setCreatedAt(actual.getCreatedAt());
        expected.setUpdatedAt(actual.getUpdatedAt());
    }

    private void equalizeIrrelevantData(Student expected, Student actual) {
        // Ignore time field as it is stamped at the time of creation in testing
        expected.setCreatedAt(actual.getCreatedAt());
        expected.setUpdatedAt(actual.getUpdatedAt());
    }

    private void equalizeIrrelevantData(Section expected, Section actual) {
        // Ignore time field as it is stamped at the time of creation in testing
        expected.setCreatedAt(actual.getCreatedAt());
        expected.setUpdatedAt(actual.getUpdatedAt());
    }

    private void equalizeIrrelevantData(Team expected, Team actual) {
        // Ignore time field as it is stamped at the time of creation in testing
        expected.setCreatedAt(actual.getCreatedAt());
        expected.setUpdatedAt(actual.getUpdatedAt());
    }

    private void equalizeIrrelevantData(ReadNotification expected, ReadNotification actual) {
        // Ignore time field as it is stamped at the time of creation in testing
        expected.setCreatedAt(actual.getCreatedAt());
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
