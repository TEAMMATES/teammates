package teammates.testing;

import static org.junit.Assert.fail;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;



/**
 * Test Login/Logout of Coordinator
 * 
 * @author Huy
 * 
 */
public class TestCoordLogin extends BaseTest {

	@BeforeClass
	public static void classSetup() {
		setupSelenium();
	}

	@AfterClass
	public static void classTearDown() {
		wrapUp();
	}

	/**
	 * Test coordinator logins
	 * 
	 * @throws Exception
	 */
	@Test
	public void testLoginLogoutSuccessful() {

		coordinatorLogin(Config.TEAMMATES_APP_ACCOUNT, Config.TEAMMATES_APP_PASSWD);
		verifyCoordinatorPage();
		logout();

		wdClick(coordLoginButton);
		waitForPageLoad();

		if (isGoogleLoginPage() || isLocalLoginPage())
			return;
		fail("Click login again fails.");
	}
}