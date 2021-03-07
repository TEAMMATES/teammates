package teammates.storage.search;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Setup in web.xml to register search manager at application startup.
 */
public class SearchManagerStarter implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        // Invoked by GAE at application startup.
        SearchManagerFactory.registerSearchManager(new SearchManager());
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        // GAE does not currently invoke this method.
    }

}
