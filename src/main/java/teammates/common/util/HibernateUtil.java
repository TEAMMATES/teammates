package teammates.common.util;

import org.hibernate.SessionFactory;
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.hibernate.cfg.Configuration;

import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackSession;

/**
 * Class containing utils for setting up the Hibernate session factory.
 */
public final class HibernateUtil {

    private static SessionFactory sessionFactory;

    static {
        Configuration config = new Configuration()
                .setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect")
                .setProperty("hibernate.connection.driver_class", "org.postgresql.Driver")
                .setProperty("hibernate.connection.username", Config.getDbUsername())
                .setProperty("hibernate.connection.password", Config.getDbPassword())
                .setProperty("hibernate.connection.url", Config.getDbConnectionUrl())
                .setProperty("hibernate.hbm2ddl.auto", "validate")
                .setProperty("show_sql", "true")
                .setProperty("hibernate.current_session_context_class", "thread")
                .addPackage("teammates.storage.sqlentity")
                .addAnnotatedClass(Course.class)
                .addAnnotatedClass(FeedbackSession.class);
        config.setPhysicalNamingStrategy(new CamelCaseToUnderscoresNamingStrategy());

        sessionFactory = config.buildSessionFactory();
    }

    private HibernateUtil() {
        // Utility class
        // Intentional private constructor to prevent instantiation.
    }

    /**
     * Returns the SessionFactory singleton object.
     */
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
