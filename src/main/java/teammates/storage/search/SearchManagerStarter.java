package teammates.storage.search;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import teammates.common.util.Config;

/**
 * Setup in web.xml to register search manager at application startup.
 */
public class SearchManagerStarter implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        // Invoked by Jetty at application startup.
        SearchManagerFactory.registerInstructorSearchManager(new InstructorSearchManager(Config.SEARCH_SERVICE_HOST, false));
        SearchManagerFactory.registerStudentSearchManager(new StudentSearchManager(Config.SEARCH_SERVICE_HOST, false));
        SearchManagerFactory.registerAccountRequestSearchManager(
                new AccountRequestSearchManager(Config.SEARCH_SERVICE_HOST, false));
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        // Nothing to do
    }

}
