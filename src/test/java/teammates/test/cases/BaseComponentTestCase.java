package teammates.test.cases;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

import teammates.test.driver.GaeSimulation;

/** Base class for Component tests.
 * Automatically sets up the GAE Simulation @BeforeTest and tears it down @AfterTest
 */
public class BaseComponentTestCase extends BaseTestCase {

    protected static GaeSimulation gaeSimulation = GaeSimulation.inst();
    
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
