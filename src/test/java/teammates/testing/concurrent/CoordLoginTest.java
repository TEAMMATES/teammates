package teammates.testing.concurrent;

import static org.junit.Assert.*;
import static teammates.testing.lib.Utils.tprintln;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.testing.BaseTest2;
import teammates.testing.config.Config;
import teammates.testing.lib.BrowserInstance;
import teammates.testing.lib.BrowserInstancePool;

/**
 * Test Coordinator Login, Logout
 * */
public class CoordLoginTest extends BaseTest2 {
	static BrowserInstance bi;

	@BeforeClass
	public static void classSetup() {
		tprintln("========== TestCoordLogin");
		bi = BrowserInstancePool.request();
	}

	@AfterClass
	public static void classTearDown() {
		BrowserInstancePool.release(bi);
		tprintln("TestCoordLogin ==========//");
	}

	/**
	 * Test coordinator login/logout
	 * 
	 * @throws Exception
	 */
	@Test
	public void testLoginLogoutSuccessful() throws Exception {
		//login
		bi.gotoHome();
		bi.coordinatorLogin(Config.inst().TEAMMATES_APP_ACCOUNT, Config.inst().TEAMMATES_APP_PASSWD);
		bi.verifyCoordinatorPage();
		//logout
		bi.logout();
		assertTrue(bi.isElementPresent(bi.COORD_LOGIN_BUTTON));
	}

	//TODO: testCoordLoginUnauthorizedFailed

}
