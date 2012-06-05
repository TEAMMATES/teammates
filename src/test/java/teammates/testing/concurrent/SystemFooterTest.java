package teammates.testing.concurrent;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.api.Common;
import teammates.testing.config.Config;
import teammates.testing.lib.BrowserInstance;
import teammates.testing.lib.BrowserInstancePool;
import teammates.testing.object.Scenario;

/**
 * Currently doesn't work as the getElementText(bi.footer) returns only the part "Contact us"
 * Don't know how to fix ~Aldrian~
 *
 */
public class SystemFooterTest extends TestCase {
	static BrowserInstance bi;
	static Scenario scn = setupScenarioInstance("scenario");

	public static int MAX_VERSION_LENGTH = 10;
	private int FIRST_STUDENT = 0;

	@BeforeClass
	public static void classSetup() throws Exception {
		bi = BrowserInstancePool.getBrowserInstance();
	}

	@AfterClass
	public static void classTearDown() throws Exception {
		if(bi.isElementPresent(bi.logoutTab)) {
			bi.logout();
		}
		BrowserInstancePool.release(bi);
	}

	@Test
	public void testOnMainSite() throws Exception {
		System.out.println("Test: Footer on Main Page.");
		testFooter();
	}

	@Test
	public void testOnCoordSite() throws Exception{
		System.out.println("Test: Footer on Coordinator.jsp.");
		if(bi.isElementPresent(bi.logoutTab)) {
			bi.logout();
		}
		bi.loginCoord(scn.coordinator.username, scn.coordinator.password);
		testFooter();
		bi.logout();
	}

	@Test
	public void testOnStudentSite() throws Exception{
		System.out.println("Test: Footer on Student.jsp.");
		if(bi.isElementPresent(bi.logoutTab)) {
			bi.logout();
		}
		bi.studentLogin(scn.students.get(FIRST_STUDENT).email, Config.inst().TEAMMATES_APP_PASSWD);
		testFooter();
		bi.logout();
	}
	
	private void testFooter() throws Exception{
		bi.verifyCurrentPageHTMLRegex(Common.TEST_PAGES_FOLDER+"footerRegex.html");
	}
}
