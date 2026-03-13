package teammates.storage.search;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

/**
 * Setup in web.xml to register search manager at application startup.
 */
public class SearchManagerStarter implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        // Search indexing is removed. No startup registration needed.
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        // Nothing to do
    }

}
