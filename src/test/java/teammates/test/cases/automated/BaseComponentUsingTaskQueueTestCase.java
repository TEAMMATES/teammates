package teammates.test.cases.automated;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

import teammates.test.cases.BaseTestCase;
import teammates.test.driver.GaeSimulation;

/** Base class for tests pertaining to Automated tasks.
 * Automatically sets up the GAE Simulation @BeforeTest and tears it down @AfterTest
 */

public class BaseComponentUsingTaskQueueTestCase extends BaseTestCase {
    
    protected static GaeSimulation gaeSimulation;
    
    @BeforeTest
    public void testSetUp() throws Exception {
        gaeSimulation = GaeSimulation.inst();
        gaeSimulation.setup();
    }

    
    @AfterTest
    public void testTearDown() throws Exception {
        gaeSimulation.tearDown();
    }
}
