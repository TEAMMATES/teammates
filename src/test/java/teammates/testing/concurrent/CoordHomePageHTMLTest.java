package teammates.testing.concurrent;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.testing.lib.BrowserInstance;
import teammates.testing.lib.BrowserInstancePool;
import teammates.testing.object.Scenario;

/**
 * Test to verify the source HTML of the coordinator landing page
 * 
 * @author Shakthi
 *
 */

public class CoordHomePageHTMLTest extends TestCase {
	static Scenario scn = Scenario.scenarioForPageVerification("target/test-classes/data/page_verification.json");
	static BrowserInstance bi;

	@BeforeClass
	public static void classSetup() throws Exception {
		bi = BrowserInstancePool.request();
		bi.coordinatorLogin(scn.coordinator.username, scn.coordinator.password);
	}

	@AfterClass
	public static void classTearDown() throws Exception {
		if (bi.isElementPresent(bi.logoutTab))
			bi.logout();

		BrowserInstancePool.release(bi);
	}

	@Test
	public void verifyCoordLandingPageSuccessful() throws Exception {
		bi.goToCoordHome();
		bi.verifyCurrentPageHTML("target/test-classes/pages/coordHome.html");
	}
}
