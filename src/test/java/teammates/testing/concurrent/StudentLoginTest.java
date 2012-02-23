package teammates.testing.concurrent;

import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.testing.config.Config;
import teammates.testing.lib.BrowserInstance;
import teammates.testing.lib.BrowserInstancePool;

public class StudentLoginTest extends TestCase {
	static BrowserInstance bi;
	
	@BeforeClass
	public static void classSetup() {
		System.out.println("========== TestStudentLogin");
		bi = BrowserInstancePool.request();
	}

	@AfterClass
	public static void classTearDown() {
		BrowserInstancePool.release(bi);
		System.out.println("TestStudentLogin ==========//");
	}

	@Test
	public void testStudentLoginLogoutSuccessful() {
		//testStudentLoginSuccessful
		bi.gotoHome();
		bi.studentLogin(Config.inst().INDIVIDUAL_ACCOUNT, Config.inst().TEAMMATES_APP_PASSWD);
		bi.verifyStudentPage();
		
		
		//testStudentLogoutSuccessful
		bi.logout();
		assertTrue(bi.isElementPresent(bi.STUDENT_LOGIN_BUTTON));

		// Click Student link again, check if it's redirected to Google page
//		bi.wdClick(bi.STUDENT_LOGIN_BUTTON);
//		bi.waitForPageLoad();
//		if (bi.isGoogleLoginPage() || bi.isLocalLoginPage())
//			return;
//		fail("Click login again fails.");
	}
}
