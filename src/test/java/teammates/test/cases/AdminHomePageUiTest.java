package teammates.test.cases;

import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;

import teammates.common.Common;
import teammates.common.datatransfer.DataBundle;
import teammates.test.driver.BackDoor;
import teammates.test.driver.BrowserInstance;
import teammates.test.driver.BrowserInstancePool;
import teammates.test.driver.TestProperties;

public class AdminHomePageUiTest extends BaseTestCase{

	private static BrowserInstance bi;
	private static String appUrl = TestProperties.inst().TEAMMATES_URL;
	
	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader();
		
		bi = BrowserInstancePool.getBrowserInstance();
		
		bi.loginAdmin(TestProperties.inst().TEST_ADMIN_ACCOUNT, TestProperties.inst().TEST_ADMIN_PASSWORD);
		String link = appUrl+Common.PAGE_ADMIN_HOME;
		bi.goToUrl(link);
	}
	
	@AfterClass
	public static void classTearDown() throws Exception {
		BrowserInstancePool.release(bi);
		printTestClassFooter();
		
		BackDoor.deleteCourse("newInstructor-demo");
		BackDoor.deleteInstructor("newInstructor");
	}
	
	@Test
	public void testAdminCreateAccount(){
		______TS("test create account");
		bi.fillString(By.name(Common.PARAM_INSTRUCTOR_ID), "newInstructor");
		bi.fillString(By.name(Common.PARAM_INSTRUCTOR_NAME), "New Instructor");
		bi.fillString(By.name(Common.PARAM_INSTRUCTOR_EMAIL), "newInstructor@gmail.com");
		bi.clickCheckbox(By.name(Common.PARAM_INSTRUCTOR_IMPORT_SAMPLE));
		bi.click(By.id("btnAddInstructor"));
		
		bi.waitForStatusMessage("Instructor New Instructor has been successfully created");
		
		______TS("verify data created");
		String verification = BackDoor.getInstructorAsJson("newInstructor", "newInstructor-demo");
		assertTrue(verification.contains("\"courseId\": \"newInstructor-demo\""));
		assertTrue(verification.contains("\"googleId\": \"newInstructor\""));
		
	}

}
