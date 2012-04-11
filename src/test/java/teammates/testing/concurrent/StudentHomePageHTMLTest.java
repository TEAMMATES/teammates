package teammates.testing.concurrent;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.testing.config.Config;
import teammates.testing.lib.BrowserInstance;
import teammates.testing.lib.BrowserInstancePool;
import teammates.testing.object.Scenario;

/**
 * Test to verify the source HTML of the student landing page
 * 
 * @author Shakthi
 *
 */

public class StudentHomePageHTMLTest extends TestCase {
	static Scenario scn = Scenario.scenarioForPageVerification("target/test-classes/data/page_verification.json");
	static BrowserInstance bi;
	
	private static String TEST_STUDENT = scn.students.get(2).email;

	@BeforeClass
	public static void classSetup() throws Exception {
		bi = BrowserInstancePool.request();
		bi.studentLogin(TEST_STUDENT, Config.inst().TEAMMATES_APP_PASSWD);
	}

	@AfterClass
	public static void classTearDown() throws Exception {
		if (bi.isElementPresent(bi.logoutTab))
			bi.logout();

		BrowserInstancePool.release(bi);
	}

	@Test
	public void verifyStudentLandingPageSuccessful() throws Exception {
		bi.goToStudentHome();
		bi.verifyCurrentPageHTML("target/test-classes/pages/studentHome.html");
	}
}