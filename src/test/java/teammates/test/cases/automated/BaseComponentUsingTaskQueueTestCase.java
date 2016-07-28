package teammates.test.cases.automated;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

import teammates.test.cases.BaseComponentTestCase;
import teammates.test.driver.GaeSimulation;

/** Base class for tests pertaining to Automated tasks.
 * Automatically sets up the GAE Simulation @BeforeTest and tears it down @AfterTest
 */

public class BaseComponentUsingTaskQueueTestCase extends BaseComponentTestCase {
    
    protected static GaeSimulation gaeSimulation = GaeSimulation.inst();
    
    @Override
    @BeforeTest
    public void testSetUp() {
        gaeSimulation = GaeSimulation.inst();
        gaeSimulation.setup();
    }

    @Override
    @AfterTest
    public void testTearDown() {
        gaeSimulation.tearDown();
    }
}
