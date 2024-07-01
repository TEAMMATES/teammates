package teammates.common.util;

import java.util.List;

import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.MutationQuery;
import org.hibernate.resource.transaction.spi.TransactionStatus;

import teammates.storage.sqlentity.Account;
import teammates.storage.sqlentity.AccountRequest;
import teammates.storage.sqlentity.BaseEntity;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.DeadlineExtension;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackResponse;
import teammates.storage.sqlentity.FeedbackResponseComment;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.FeedbackSessionLog;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Notification;
import teammates.storage.sqlentity.ReadNotification;
import teammates.storage.sqlentity.Section;
import teammates.storage.sqlentity.Student;
import teammates.storage.sqlentity.Team;
import teammates.storage.sqlentity.UsageStatistics;
import teammates.storage.sqlentity.User;
import teammates.storage.sqlentity.questions.FeedbackConstantSumQuestion;
import teammates.storage.sqlentity.questions.FeedbackContributionQuestion;
import teammates.storage.sqlentity.questions.FeedbackMcqQuestion;
import teammates.storage.sqlentity.questions.FeedbackMsqQuestion;
import teammates.storage.sqlentity.questions.FeedbackNumericalScaleQuestion;
import teammates.storage.sqlentity.questions.FeedbackRankOptionsQuestion;
import teammates.storage.sqlentity.questions.FeedbackRankRecipientsQuestion;
import teammates.storage.sqlentity.questions.FeedbackRubricQuestion;
import teammates.storage.sqlentity.questions.FeedbackTextQuestion;
import teammates.storage.sqlentity.responses.FeedbackConstantSumResponse;
import teammates.storage.sqlentity.responses.FeedbackContributionResponse;
import teammates.storage.sqlentity.responses.FeedbackMcqResponse;
import teammates.storage.sqlentity.responses.FeedbackMsqResponse;
import teammates.storage.sqlentity.responses.FeedbackNumericalScaleResponse;
import teammates.storage.sqlentity.responses.FeedbackRankOptionsResponse;
import teammates.storage.sqlentity.responses.FeedbackRankRecipientsResponse;
import teammates.storage.sqlentity.responses.FeedbackRubricResponse;
import teammates.storage.sqlentity.responses.FeedbackTextResponse;

/**
 * Utility class for Hibernate related methods.
 */
public final class HibernateUtil {
    private static SessionFactory sessionFactory;

    private static final List<Class<? extends BaseEntity>> ANNOTATED_CLASSES = List.of(
            AccountRequest.class,
            Course.class,
            FeedbackSession.class,
            Account.class,
            Notification.class,
            ReadNotification.class,
            User.class,
            Instructor.class,
            Student.class,
            UsageStatistics.class,
            Section.class,
            Team.class,
            FeedbackQuestion.class,
            FeedbackConstantSumQuestion.class,
            FeedbackContributionQuestion.class,
            FeedbackMcqQuestion.class,
            FeedbackMsqQuestion.class,
            FeedbackNumericalScaleQuestion.class,
            FeedbackRankOptionsQuestion.class,
            FeedbackRankRecipientsQuestion.class,
            FeedbackRubricQuestion.class,
            FeedbackTextQuestion.class,
            DeadlineExtension.class,
            FeedbackResponse.class,
            FeedbackConstantSumResponse.class,
            FeedbackContributionResponse.class,
            FeedbackMcqResponse.class,
            FeedbackMsqResponse.class,
            FeedbackNumericalScaleResponse.class,
            FeedbackRankOptionsResponse.class,
            FeedbackRankRecipientsResponse.class,
            FeedbackRubricResponse.class,
            FeedbackTextResponse.class,
            FeedbackResponseComment.class,
            FeedbackSessionLog.class);

    private HibernateUtil() {
        // Utility class
        // Intentional private constructor to prevent instantiation.
    }

    /**
     * Builds a session factory if it does not already exist.
     */
    public static void buildSessionFactory(String dbUrl, String username, String password) {
        synchronized (HibernateUtil.class) {
            if (sessionFactory != null) {
                return;
            }
        }

        Configuration config = new Configuration()
                .setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect")
                .setProperty("hibernate.connection.driver_class", "org.postgresql.Driver")
                .setProperty("hibernate.connection.provider_class",
                        "org.hibernate.hikaricp.internal.HikariCPConnectionProvider")
                .setProperty("hibernate.connection.username", username)
                .setProperty("hibernate.connection.password", password)
                .setProperty("hibernate.connection.url", dbUrl)
                .setProperty("hibernate.hbm2ddl.auto", "validate")
                .setProperty("show_sql", "true")
                .setProperty("hibernate.current_session_context_class", "thread")
                .setProperty("hibernate.hikari.minimumIdle", "10")
                .setProperty("hibernate.hikari.maximumPoolSize", "30")
                .setProperty("hibernate.hikari.idleTimeout", "300000")
                .setProperty("hibernate.hikari.connectionTimeout", "30000")
                // Uncomment only during migration for optimized batch-insertion, batch-update, and batch-fetch.
                // .setProperty("hibernate.jdbc.batch_size", "50")
                // .setProperty("hibernate.order_updates", "true")
                // .setProperty("hibernate.batch_versioned_data", "true")
                // .setProperty("hibernate.jdbc.fetch_size", "50")
                .addPackage("teammates.storage.sqlentity");

        if (Config.IS_DEV_SERVER) {
            config.setProperty("hibernate.hbm2ddl.auto", "update");
        }

        for (Class<? extends BaseEntity> cls : ANNOTATED_CLASSES) {
            config = config.addAnnotatedClass(cls);
        }
        config.setPhysicalNamingStrategy(new CamelCaseToUnderscoresNamingStrategy());

        setSessionFactory(config.buildSessionFactory());
    }

    /**
     * Returns the SessionFactory.
     */
    private static SessionFactory getSessionFactory() {
        assert sessionFactory != null;

        return sessionFactory;
    }

    /**
     * Returns the current hibernate session.
     * @see SessionFactory#getCurrentSession()
     */
    private static Session getCurrentSession() {
        return getSessionFactory().getCurrentSession();
    }

    /**
     * Returns a CriteriaBuilder object.
     * @see SessionFactory#getCriteriaBuilder()
     */
    public static CriteriaBuilder getCriteriaBuilder() {
        return getCurrentSession().getCriteriaBuilder();
    }

    /**
     * Returns a generic typed TypedQuery object.
     * @see Session#createQuery(CriteriaQuery)
     */
    public static <T> TypedQuery<T> createQuery(CriteriaQuery<T> cr) {
        return getCurrentSession().createQuery(cr);
    }

    /**
     * Returns a MutationQuery object.
     * @see Session#createMutationQuery(CriteriaDelete)
     */
    public static <T> MutationQuery createMutationQuery(CriteriaDelete<T> cd) {
        return getCurrentSession().createMutationQuery(cd);
    }

    public static void setSessionFactory(SessionFactory sessionFactory) {
        HibernateUtil.sessionFactory = sessionFactory;
    }

    /**
     * Start a resource transaction.
     * @see Transaction#begin()
     */
    public static void beginTransaction() {
        Transaction transaction = getCurrentSession().getTransaction();
        transaction.begin();
    }

    /**
     * Roll back the current resource transaction if needed.
     * @see Transaction#rollback()
     */
    public static void rollbackTransaction() {
        Session session = getCurrentSession();
        if (session.getTransaction().getStatus() == TransactionStatus.ACTIVE
                || session.getTransaction().getStatus() == TransactionStatus.MARKED_ROLLBACK) {
            session.getTransaction().rollback();
        }
    }

    /**
     * Commit the current resource transaction, writing any unflushed changes to the database.
     * @see Transaction#commit()
     */
    public static void commitTransaction() {
        Transaction transaction = getCurrentSession().getTransaction();
        transaction.commit();
    }

    /**
     * Force this session to flush. Must be called at the end of a unit of work, before the transaction is committed.
     * @see Session#flush()
     */
    public static void flushSession() {
        getCurrentSession().flush();
    }

    /**
     * Force this session to clear. Usually called together with flush.
     * @see Session#clear()
     */
    public static void clearSession() {
        getCurrentSession().clear();
    }

    /**
     * Return the persistent instance of the given entity class with the given identifier,
     * or null if there is no such persistent instance.
     * @see Session#get(Class, Object)
     */
    public static <T extends BaseEntity> T get(Class<T> entityType, Object id) {
        return getCurrentSession().get(entityType, id);
    }

    /**
     * Return the persistent instance of the given entity class with the given natural id,
     * or null if there is no such persistent instance.
     * @see Session#get(Class, Object)
     */
    public static <T extends BaseEntity> T getBySimpleNaturalId(Class<T> entityType, Object id) {
        return getCurrentSession().bySimpleNaturalId(entityType).load(id);
    }

    /**
     * Copy the state of the given object onto the persistent object with the same identifier.
     * @see Session#merge(E)
     */
    public static <E> E merge(E object) {
        return getCurrentSession().merge(object);
    }

    /**
     * Make a transient instance persistent and mark it for later insertion in the database.
     * @see Session#persist(Object)
     */
    public static void persist(BaseEntity entity) {
        getCurrentSession().persist(entity);
    }

    /**
     * Mark a persistence instance associated with this session for removal from the underlying database.
     * @see Session#remove(Object)
     */
    public static void remove(BaseEntity entity) {
        getCurrentSession().remove(entity);
    }

    /**
     * Create and execute a {@code MutationQuery} for the given delete criteria tree.
     */
    public static <T> void executeDelete(CriteriaDelete<T> cd) {
        getCurrentSession().createMutationQuery(cd).executeUpdate();
    }

    /**
     * Return a reference to the persistent instance with the given class and
     * identifier,making the assumption that the instance is still persistent in the
     * database.
     * @see Session#getReference(Class, Object)
     */
    public static <T> T getReference(Class<T> entityType, Object id) {
        return getCurrentSession().getReference(entityType, id);
    }

    /**
     * Flush the current session and evict the given entity from the session.
     * @see Session#evict(Object)
     */
    public static <T> void flushAndEvict(T entity) {
        flushSession();
        getCurrentSession().evict(entity);
    }

}
