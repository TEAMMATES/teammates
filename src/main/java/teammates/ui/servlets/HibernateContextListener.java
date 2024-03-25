package teammates.ui.servlets;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import teammates.common.util.Config;
import teammates.common.util.HibernateUtil;

/**
 * Setup in web.xml to set up Hibernate Session Factory at application startup.
 */
public class HibernateContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        // Invoked by Jetty at application startup.
        HibernateUtil.buildSessionFactory(Config.getDbConnectionUrl(), Config.POSTGRES_USERNAME, Config.POSTGRES_PASSWORD);
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        // Nothing to do
    }
}
