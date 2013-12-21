package teammates.test.cases;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

import teammates.common.util.ThreadHelper;
import teammates.test.driver.GaeSimulation;

/** Base class for tests pertaining to Automated tasks.
 * Automatically sets up the GAE Simulation @BeforeTest and tears it down @AfterTest
 */

public class BaseComponentUsingEmailQueueTestCase extends BaseTestCase {
	
	protected static GaeSimulation gaeSimulation;
	
	@BeforeTest
	public void testSetUp() throws Exception {
		gaeSimulation = GaeSimulation.inst();
	}

	
	@AfterTest
	public void testTearDown() throws Exception {
		gaeSimulation.tearDown();
	}
	

	public void waitForTaskQueueExecution(int expectedNumberOfTasks) {
		/*
		 *  Current rate of task execution is 1/s
		 *  Wait for 1 more second to see if erroneous or unwanted tasks
		 *  are added too
		 */
		ThreadHelper.waitFor((expectedNumberOfTasks + 1) * 1000);
	}

}
