package teammates.test.cases;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

import teammates.storage.datastore.Datastore;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalMailServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.meterware.servletunit.ServletRunner;
import com.meterware.servletunit.ServletUnitClient;

public class BaseComponentTest extends BaseTestCase {

	protected static String URI;
	protected static ServletRunner sr;
	protected static ServletUnitClient sc;
	
	
	@BeforeTest
	public void suiteSetUp() throws Exception {
		setupGaeSimulation();
	}

	protected static synchronized void setupGaeSimulation() {
		System.out.println("Setting up GAE simulation");
		LocalTaskQueueTestConfig localTasks = new LocalTaskQueueTestConfig();
		LocalUserServiceTestConfig localUserServices = new LocalUserServiceTestConfig();
		LocalDatastoreServiceTestConfig localDatastore = new LocalDatastoreServiceTestConfig();
		LocalMailServiceTestConfig localMail = new LocalMailServiceTestConfig();
		helper = new LocalServiceTestHelper(localDatastore, localMail,	localUserServices, localTasks);
		setHelperTimeZone(helper);
		helper.setUp();
		Datastore.initialize();
		sr = new ServletRunner();
		sc = sr.newClient();
	}

	@AfterTest
	public void suiteTearDown() throws Exception {
		helper.tearDown();
	}
	
	public static void resetDatastore(){
		helper.tearDown();
		helper.setUp();
	}
}
