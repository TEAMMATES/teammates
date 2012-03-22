package teammates.testing.concurrent;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.testing.lib.BrowserInstance;
import teammates.testing.lib.BrowserInstancePool;
import teammates.testing.object.Scenario;

public class SystemVerifyHelpPageTest extends TestCase {
	static Scenario scn = setupScenarioInstance("scenario");
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
	public void clickHelpTabOpenNewWindowSuccessful() throws Exception {
		String helpWindow = "Teammates Online Peer Feedback System for Student Team Projects";
		bi.clickAndOpenNewWindow(bi.helpTab, helpWindow);

		assertEquals(true, bi.isTextPresent("Table of Contents"));
		assertEquals(true, bi.isTextPresent("Getting Started"));
		assertEquals(true, bi.isTextPresent("Frequently Asked Questions"));
		assertEquals(true, bi.isTextPresent("Helpful tips for using the system"));

		bi.closeSelectedWindow();
	}

	@Test
	public void verifyHelpPageSuccessful() throws Exception {
		bi.verifyPageHTML("http://www.comp.nus.edu.sg/~teams/coordinatorhelp.html", 
				"target/test-classes/pages/coordHelp.html");
	}

}
