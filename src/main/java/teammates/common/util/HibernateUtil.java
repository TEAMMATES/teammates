package teammates.common.util;

import org.hibernate.SessionFactory;
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.hibernate.cfg.Configuration;

import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackSession;

/**
 * Class containing utils for getting the Hibernate session factory.
 */
public final class HibernateUtil {

    private static SessionFactory sessionFactory;

    private HibernateUtil() {
        // Utility class
        // Intentional private constructor to prevent instantiation.
    }

    private static Configuration getConfigForSessionFactory(String username, String password, String url) {
        Configuration config = new Configuration()
                .setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect")
                .setProperty("hibernate.connection.driver_class", "org.postgresql.Driver")
                .setProperty("hibernate.connection.username", username)
                .setProperty("hibernate.connection.password", password)
                .setProperty("hibernate.connection.url", url)
                .setProperty("hibernate.hbm2ddl.auto", "create-drop")
                .setProperty("show_sql", "true")
                .setProperty("hibernate.current_session_context_class", "thread")
                .addPackage("teammates.storage.sqlentity")
                .addAnnotatedClass(Course.class)
                .addAnnotatedClass(FeedbackSession.class);
        config.setPhysicalNamingStrategy(new CamelCaseToUnderscoresNamingStrategy());

        return config;
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            Configuration cfg = getConfigForSessionFactory(
                    Config.getDbUsername(),
                    Config.getDbPassword(),
                    Config.getDbConnectionUrl());
            sessionFactory = cfg.buildSessionFactory();
        }

        return sessionFactory;
    }

    /**
     * Closes connection to the database.
     */
    public static void shutdown() {
        getSessionFactory().close();
    }

    public static void setSessionFactoryForTesting(String username, String password, String url) {
        Configuration cfg = getConfigForSessionFactory(username, password, url);
        sessionFactory = cfg.buildSessionFactory();
    }
}
