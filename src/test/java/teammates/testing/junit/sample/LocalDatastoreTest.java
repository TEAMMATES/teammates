package teammates.testing.junit.sample;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import static com.google.appengine.api.datastore.FetchOptions.Builder.withLimit;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import teammates.jdo.Course;
import static org.junit.Assert.*;

public class LocalDatastoreTest {

    private final LocalServiceTestHelper helper =
        new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    @Before
    public void setUp() {
        helper.setUp();
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    // run this test twice to prove we're not leaking any state across tests
    private void doTest() {
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        assertEquals(0, ds.prepare(new Query(Course.class.getName())).countEntities(withLimit(10)));
        Entity course = new Entity(Course.class.getName());
		course.setProperty("ID", "CS1010");
		course.setProperty("name", "Programming Methodology");
		course.setProperty("coordinatorID", "teammates.coord");
		course.setProperty("archived", false);
		ds.put(course);
		
		assertEquals(1, ds.prepare(new Query(Course.class.getName())).countEntities(withLimit(10)));
    }

    @Test
    public void testInsert1() {
        doTest();
    }

    @Test
    public void testInsert2() {
        doTest();
    }
}