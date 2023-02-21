package teammates.common.util;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.hibernate.cfg.Configuration;

import teammates.storage.sqlentity.Account;
import teammates.storage.sqlentity.AccountRequest;
import teammates.storage.sqlentity.BaseEntity;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Notification;
import teammates.storage.sqlentity.ReadNotification;
import teammates.storage.sqlentity.Section;
import teammates.storage.sqlentity.Student;
import teammates.storage.sqlentity.Team;
import teammates.storage.sqlentity.UsageStatistics;
import teammates.storage.sqlentity.User;

/**
 * Class containing utils for setting up the Hibernate session factory.
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
            Team.class);

    private HibernateUtil() {
        // Utility class
        // Intentional private constructor to prevent instantiation.
    }

    /**
     * Returns the SessionFactory.
     */
    public static SessionFactory getSessionFactory() {
        assert sessionFactory != null;

        return sessionFactory;
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
                .setProperty("hibernate.connection.username", username)
                .setProperty("hibernate.connection.password", password)
                .setProperty("hibernate.connection.url", dbUrl)
                .setProperty("hibernate.hbm2ddl.auto", "update")
                .setProperty("show_sql", "true")
                .setProperty("hibernate.current_session_context_class", "thread")
                .addPackage("teammates.storage.sqlentity");

        for (Class<? extends BaseEntity> cls : ANNOTATED_CLASSES) {
            config = config.addAnnotatedClass(cls);
        }
        config.setPhysicalNamingStrategy(new CamelCaseToUnderscoresNamingStrategy());

        setSessionFactory(config.buildSessionFactory());
    }

    public static void setSessionFactory(SessionFactory sessionFactory) {
        HibernateUtil.sessionFactory = sessionFactory;
    }
}
