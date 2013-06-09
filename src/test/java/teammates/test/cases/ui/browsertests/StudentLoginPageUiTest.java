package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.assertTrue;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountAttributes;
import teammates.test.cases.BaseTestCase;
import teammates.test.driver.BackDoor;
import teammates.test.driver.BrowserInstance;
import teammates.test.driver.BrowserInstancePool;
import teammates.test.driver.TestProperties;

public class StudentLoginPageUiTest extends BaseTestCase {
	
	private static BrowserInstance bi;
	
	private static String appUrl = TestProperties.inst().TEAMMATES_URL;
	
	//TODO: we can remove this test becase it is covered by HomePage test
	
	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader();
		bi = BrowserInstancePool.getBrowserInstance();
		bi.logout();
	}
	
	@AfterClass
	public static void classTearDown() throws Exception {
		//no need to delete test data as they can be reused next time
		BrowserInstancePool.release(bi);
		printTestClassFooter();
	}
	
	
	@Test
	public void testStudentLogin(){
		
		//create the student account if it doesn't exist
		String studentUsername = TestProperties.inst().TEST_STUDENT_ACCOUNT;
		AccountAttributes testStudentAccount = new AccountAttributes(studentUsername, "Emily Tmms", false, "emily.tmms@gmail.com", "National University of Singapore");
		BackDoor.createAccount(testStudentAccount);
		
		//try to login
		bi.goToUrl(appUrl);
		bi.click(bi.studentLoginButton);
		assertTrue(bi.isLocalLoginPage()||bi.isGoogleLoginPage());
		String studentPassword = TestProperties.inst().TEST_STUDENT_PASSWORD;
		boolean isAdmin = false;
		bi.login(studentUsername, studentPassword, isAdmin);
		assertContainsRegex(studentUsername+"{*}Student Home{*}", bi.getCurrentPageSource());
	}

}
