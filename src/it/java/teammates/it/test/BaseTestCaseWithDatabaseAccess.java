package teammates.it.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.UUID;

import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import teammates.common.datatransfer.DataBundle;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.HibernateUtil;
import teammates.logic.api.Logic;
import teammates.logic.core.LogicStarter;
import teammates.storage.entity.Account;
import teammates.storage.entity.AccountRequest;
import teammates.storage.entity.BaseEntity;
import teammates.storage.entity.Course;
import teammates.storage.entity.DeadlineExtension;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Notification;
import teammates.storage.entity.ReadNotification;
import teammates.storage.entity.ResponseInstructorComment;
import teammates.storage.entity.Section;
import teammates.storage.entity.Student;
import teammates.storage.entity.Team;
import teammates.storage.entity.UsageStatistics;
import teammates.test.BaseTestCase;

import liquibase.command.CommandScope;

/**
 * Base test case for tests that access the database.
 */
public abstract class BaseTestCaseWithDatabaseAccess extends BaseTestCase {

    private static final PostgreSQLContainer PGSQL = new PostgreSQLContainer("postgres:15.1-alpine");

    private final Logic logic = Logic.inst();

    @BeforeSuite
    protected static void setUpSuite() throws Exception {
        PGSQL.start();

        runLiquibaseMigrations(PGSQL.getJdbcUrl(), PGSQL.getUsername(), PGSQL.getPassword());

        HibernateUtil.buildSessionFactory(PGSQL.getJdbcUrl(), PGSQL.getUsername(), PGSQL.getPassword());

        LogicStarter.initializeDependencies();
    }

    private static void runLiquibaseMigrations(String jdbcUrl, String username, String password) throws Exception {
        new CommandScope("update")
                .addArgumentValue("changelogFile", "db/changelog/db.changelog-root.xml")
                .addArgumentValue("url", jdbcUrl)
                .addArgumentValue("username", username)
                .addArgumentValue("password", password)
                .execute();
    }

    @AfterSuite
    protected static void tearDownSuite() throws Exception {
        PGSQL.close();
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        HibernateUtil.beginTransaction();
    }

    /**
     * Rolls back the per-test transaction so each method runs against a clean DB state.
     *
     * <p>
     * {@code alwaysRun} ensures this runs even when configuration ({@code BeforeMethod}) or the
     * test method fails, so an open transaction is never left on the thread-bound session.
     */
    @AfterMethod(alwaysRun = true)
    protected void tearDown() {
        HibernateUtil.rollbackTransaction();
    }

    @Override
    protected String getTestDataFolder() {
        return TestProperties.TEST_DATA_FOLDER;
    }

    /**
     * Persist data bundle into the db.
     */
    protected DataBundle persistDataBundle(DataBundle dataBundle)
            throws InvalidParametersException {
        return logic.persistDataBundle(dataBundle);
    }

    /**
     * Verifies that the given entity is present in the database.
     */
    protected void verifyPresentInDatabase(BaseEntity expected) {
        assertNotNull(expected);
        BaseEntity actual = getEntity(expected);
        assertEquals(expected, actual);
    }

    private BaseEntity getEntity(BaseEntity entity) {
        if (entity instanceof Course) {
            return logic.getCourse(((Course) entity).getId());
        } else if (entity instanceof DeadlineExtension) {
            return logic.getDeadlineExtension(((DeadlineExtension) entity).getId());
        } else if (entity instanceof FeedbackSession) {
            return logic.getFeedbackSession(((FeedbackSession) entity).getId());
        } else if (entity instanceof FeedbackQuestion) {
            return logic.getFeedbackQuestion(((FeedbackQuestion) entity).getId());
        } else if (entity instanceof FeedbackResponse) {
            return logic.getFeedbackResponse(((FeedbackResponse) entity).getId());
        } else if (entity instanceof ResponseInstructorComment) {
            return logic.getResponseInstructorComment(((ResponseInstructorComment) entity).getId());
        } else if (entity instanceof Account) {
            return logic.getAccount(((Account) entity).getId());
        } else if (entity instanceof Notification) {
            return logic.getNotification(((Notification) entity).getId());
        } else if (entity instanceof AccountRequest) {
            AccountRequest accountRequest = (AccountRequest) entity;
            return logic.getAccountRequest(accountRequest.getId());
        } else if (entity instanceof Instructor) {
            return logic.getInstructor(((Instructor) entity).getId());
        } else if (entity instanceof Student) {
            return logic.getStudent(((Student) entity).getId());
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

    private void equalizeIrrelevantData(FeedbackQuestion expected, FeedbackQuestion actual) {
        // Ignore time field as it is stamped at the time of creation in testing
        expected.setCreatedAt(actual.getCreatedAt());
        expected.setUpdatedAt(actual.getUpdatedAt());
    }

    private void equalizeIrrelevantData(FeedbackResponse expected, FeedbackResponse actual) {
        // Ignore time field as it is stamped at the time of creation in testing
        expected.setCreatedAt(actual.getCreatedAt());
        expected.setUpdatedAt(actual.getUpdatedAt());
    }

    private void equalizeIrrelevantData(ResponseInstructorComment expected, ResponseInstructorComment actual) {
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
