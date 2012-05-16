package teammates.testing.concurrent;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.testing.config.Config;
import teammates.testing.lib.BrowserInstance;
import teammates.testing.lib.BrowserInstancePool;
import teammates.testing.object.Scenario;

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
	public void testOnCoordSite() {
		System.out.println("Test: Footer on Coordinator.jsp.");

		bi.loginCoord(scn.coordinator.username, scn.coordinator.password);

		testFooter();

		bi.logout();
	}

	@Test
	public void testOnStudentSite() {
		System.out.println("Test: Footer on Student.jsp.");
		
		bi.studentLogin(scn.students.get(FIRST_STUDENT).email, Config.inst().TEAMMATES_APP_PASSWD);
		
		testFooter();

		bi.logout();
	}
	
	private void testFooter() {
		assertNotNull(bi.getElementText(bi.footer));
		System.out.println(bi.getElementText(bi.footer));
		assertTrue((bi.getElementText(bi.footer)).contains("[TEAMMATES Version"));
		assertTrue((bi.getElementText(bi.footer)).contains(bi.FOOTER));
		// difference of index of 'n' in Version and the ']' in the format
		// [TEAMMATES Version 1]
		int pos = ((bi.getElementText(bi.footer)).indexOf('n')) - ((bi.getElementText(bi.footer)).indexOf(']'));
		assertTrue(pos < MAX_VERSION_LENGTH);
	}
}
