package teammates.testing;

import static org.junit.Assert.fail;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.testing.config.Config;

/**
 * Tests the login/logout functions
 * 
 */
public class TestStudentLogin extends BaseTest {

	@BeforeClass
	public static void classSetup() {
		setupSelenium();
		setupScenario();
	}

	@AfterClass
	public static void classTearDown() {
		wrapUp();
	}

	@Test
	public void testLoginLogoutSuccessful() throws Exception {
		studentLogin(Config.inst().INDIVIDUAL_ACCOUNT, Config.inst().TEAMMATES_APP_PASSWD);
		verifyStudentPage();
		logout();

		// Click Student link again, check if it's redirected to Google page
		wdClick(studentLoginButton);
		waitForPageLoad();

		if (isGoogleLoginPage() || isLocalLoginPage())
			return;
		fail("Click login again fails.");
	}
}