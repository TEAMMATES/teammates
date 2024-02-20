package teammates.ui.servlets;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import teammates.common.util.HibernateUtil;

/**
 * Setup in web.xml to set up Hibernate Session Factory at application startup.
 */
public class HibernateContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        // Invoked by Jetty at application startup.
        // HibernateUtil.buildSessionFactory(Config.getDbConnectionUrl(), Config.APP_LOCALPOSTGRES_USERNAME,
        //        Config.APP_LOCALPOSTGRES_PASSWORD);
        
        String connectionUrl = "jdbc:postgresql://35.240.141.62:5432/postgres";
        String username = "postgres";
        String password = "teammatesrocks";
        HibernateUtil.buildSessionFactory(connectionUrl, username, password);
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        // Nothing to do
    }
}
