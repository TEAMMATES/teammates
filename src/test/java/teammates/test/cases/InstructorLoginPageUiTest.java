package teammates.test.cases;

import org.testng.AssertJUnit;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountAttributes;
import teammates.test.driver.BackDoor;
import teammates.test.driver.BrowserInstance;
import teammates.test.driver.BrowserInstancePool;
import teammates.test.driver.TestProperties;

public class InstructorLoginPageUiTest extends BaseTestCase {
	
	private static BrowserInstance bi;
	
	private static String appUrl = TestProperties.inst().TEAMMATES_URL;
	
	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader();
		bi = BrowserInstancePool.getBrowserInstance();
	}
	
	@AfterClass
	public static void classTearDown() throws Exception {
		
		//no need to delete test data as they can be reused next time
		BrowserInstancePool.release(bi);
		printTestClassFooter();
	}
	
	@Test
	public void testInstructorLogin() {
		// Create an account for the instructor, if it doesn't exist
		String instructorUsername = TestProperties.inst().TEST_INSTRUCTOR_ACCOUNT;
		AccountAttributes testInstructor = new AccountAttributes(instructorUsername, "Test Course Creator", true, "instructor@testCourse.com", "National University of Singapore");
		BackDoor.createAccount(testInstructor);
		
		//try to login
		bi.goToUrl(appUrl);
		bi.click(bi.instructorLoginButton);
		AssertJUnit.assertTrue(bi.isLocalLoginPage()||bi.isGoogleLoginPage());
		String instructorPassword = TestProperties.inst().TEST_INSTRUCTOR_PASSWORD;
		boolean isAdmin = false;
		bi.login(testInstructor.googleId, instructorPassword, isAdmin);
		assertContainsRegex(instructorUsername+"{*}Instructor Home{*}", bi.getCurrentPageSource());
	}
	
}
