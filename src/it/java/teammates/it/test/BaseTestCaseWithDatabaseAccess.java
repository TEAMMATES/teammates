package teammates.it.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;

import org.junit.jupiter.api.function.Executable;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import teammates.common.datatransfer.DataBundle;
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
import teammates.storage.entity.ResponseInstructorComment;
import teammates.storage.entity.Student;
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

    /**
     * Executes the given action within a transaction.
     */
    protected void inTransaction(TransactionAction action) {
        HibernateUtil.beginTransaction();
        try {
            action.run();
            HibernateUtil.commitTransaction();
        } catch (Throwable t) {
            HibernateUtil.rollbackTransaction();
            throw new RuntimeException(t);
        }
    }

    /**
     * Executes the given supplier within a transaction and returns the result.
     */
    protected <T> T inTransaction(TransactionSupplier<T> action) {
        HibernateUtil.beginTransaction();
        try {
            T result = action.get();
            HibernateUtil.commitTransaction();
            return result;
        } catch (Throwable t) {
            HibernateUtil.rollbackTransaction();
            throw new RuntimeException(t);
        }
    }

    /**
     * Asserts that the given executable throws an exception of the expected type when executed within a transaction.
     */
    protected <E extends Throwable> E assertThrowsInTransaction(
            Class<E> expectedType, Executable executable) {
        return assertThrows(expectedType, () -> {
            HibernateUtil.beginTransaction();
            try {
                executable.execute();
                HibernateUtil.commitTransaction();
            } catch (Throwable t) {
                HibernateUtil.rollbackTransaction();
                throw t;
            }
        });
    }

    @AfterSuite
    protected static void tearDownSuite() {
        PGSQL.close();
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
        inTransaction(this::clearDatabase);
    }

    @Override
    protected String getTestDataFolder() {
        return TestProperties.TEST_DATA_FOLDER;
    }

    /**
     * Persist data bundle into the db.
     */
    protected DataBundle persistDataBundle(DataBundle dataBundle) {
        return inTransaction(() -> logic.persistDataBundle(dataBundle));
    }

    private void clearDatabase() {
        HibernateUtil.createNativeMutationQuery("""
            TRUNCATE TABLE
                account_requests,
                accounts,
                courses,
                deadline_extensions,
                feedback_questions,
                feedback_responses,
                feedback_session_logs,
                feedback_sessions,
                instructors,
                notifications,
                read_notifications,
                response_instructor_comments,
                sections,
                students,
                teams,
                usage_statistics,
                users
            RESTART IDENTITY CASCADE
            """)
                .executeUpdate();
    }

    /**
     * Verifies that the given entity is present in the database.
     */
    protected void verifyPresentInDatabase(BaseEntity expected) {
        assertNotNull(expected);
        BaseEntity actual = inTransaction(() -> getEntity(expected));
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
            return logic.getAccountRequest(((AccountRequest) entity).getId());
        } else if (entity instanceof Instructor) {
            return logic.getInstructor(((Instructor) entity).getId());
        } else if (entity instanceof Student) {
            return logic.getStudent(((Student) entity).getId());
        } else {
            throw new RuntimeException("Unknown entity type");
        }
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

    /**
     * Functional interface for executing code within a transaction.
     *
     * @param <T> the type of the result returned by the supplier
     */
    @FunctionalInterface
    protected interface TransactionSupplier<T> {
        /**
         * Executes the supplier within a transaction and returns the result.
         */
        T get() throws Exception;
    }

    /**
     * Functional interface for executing code within a transaction that does not return a result.
     */
    @FunctionalInterface
    protected interface TransactionAction {
        /**
         * Executes the action within a transaction.
         */
        void run() throws Exception;
    }
}
