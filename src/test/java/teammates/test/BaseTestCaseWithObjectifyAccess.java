package teammates.test;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.Closeable;

import teammates.storage.api.OfyHelper;
import teammates.storage.search.SearchManager;
import teammates.storage.search.SearchManagerFactory;

/**
 * Base class for all test cases which require access to the Objectify service. Requires a minimal GAE API environment
 * registered for creation of Datastore Key objects used in defining parent-child relationships in entities.
 */
public abstract class BaseTestCaseWithObjectifyAccess extends BaseTestCaseWithMinimalGaeEnvironment {
    private Closeable closeable;

    @BeforeClass
    public void setupSearch() {
        // Using actual SearchManager means that the implementation is identical to production.
        // It works now because we are still using GAE's Search API, but it will no longer work
        // when we move to third-party service with local setup.

        SearchManagerFactory.registerSearchManager(new SearchManager());
    }

    @BeforeClass
    public void setupObjectify() {
        OfyHelper.registerEntityClasses();
        closeable = ObjectifyService.begin();
    }

    @AfterClass
    public void tearDownObjectify() {
        closeable.close();
    }

}
