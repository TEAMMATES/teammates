package teammates.testing.concurrent;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.testing.config.Config;
import teammates.testing.lib.BrowserInstance;
import teammates.testing.lib.BrowserInstancePool;
import teammates.testing.lib.SharedLib;

/**
 * Test Coordinator Login, Logout
 * */
public class CoordLoginTest extends TestCase {
	static BrowserInstance bi;

	@BeforeClass
	public static void classSetup() {
		SharedLib.tprintln("========== TestCoordLogin");
		bi = BrowserInstancePool.getBrowserInstance();
	}

	@AfterClass
	public static void classTearDown() {
		BrowserInstancePool.release(bi);
		SharedLib.tprintln("TestCoordLogin ==========//");
	}

	/**
	 * Test coordinator login/logout
	 * 
	 * @throws Exception
	 */
	@Test
	public void testLoginLogoutSuccessful() throws Exception {
		//login
		bi.goToMain();
		bi.loginCoord(Config.inst().TEAMMATES_APP_ACCOUNT, Config.inst().TEAMMATES_APP_PASSWORD);
		bi.verifyCoordHomePage();
		//logout
		bi.logout();
		assertTrue(bi.isElementPresent(bi.COORD_LOGIN_BUTTON));
	}

	//TODO: testCoordLoginUnauthorizedFailed

}
