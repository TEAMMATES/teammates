package teammates.test.cases;

import static org.testng.AssertJUnit.assertTrue;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.openqa.selenium.By;

import teammates.common.Common;
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
		BackDoor.deleteAccount("newInstructor");
		BackDoor.deleteAccount("newInstructor2");
		BackDoor.deleteAccount("newInstructor3");
	}
	
	@Test
	public void testAdminCreateAccount(){
		______TS("test invalid instructor Data");
		bi.click(By.id("btnAddInstructor"));
		bi.waitForStatusMessage(Common.MESSAGE_FIELDS_EMPTY);
		
		bi.fillString(By.name(Common.PARAM_INSTRUCTOR_ID), "newInstructor");
		bi.fillString(By.name(Common.PARAM_INSTRUCTOR_NAME), "!@#$%^&");
		bi.fillString(By.name(Common.PARAM_INSTRUCTOR_EMAIL), "newInstructor");
        bi.fillString(By.name(Common.PARAM_INSTRUCTOR_INSTITUTION), "Institution");
		
		bi.click(By.id("btnAddInstructor"));
		bi.waitForStatusMessage("The e-mail address is invalid.");
		
		bi.fillString(By.name(Common.PARAM_INSTRUCTOR_EMAIL), "newInstructor@gmail.com");
		bi.click(By.id("btnAddInstructor"));
		bi.waitForStatusMessage("Name should only consist of alphanumerics or hyphens, apostrophes, fullstops, commas, slashes, round brackets\nand not more than 40 characters.");
		
		
		______TS("test create account with sample data");
		bi.fillString(By.name(Common.PARAM_INSTRUCTOR_ID), "newInstructor");
		bi.fillString(By.name(Common.PARAM_INSTRUCTOR_NAME), "New Instructor");
		bi.fillString(By.name(Common.PARAM_INSTRUCTOR_EMAIL), "newInstructor@gmail.com");
		bi.clickCheckbox(By.name(Common.PARAM_INSTRUCTOR_IMPORT_SAMPLE));
		bi.click(By.id("btnAddInstructor"));
		
		bi.waitForStatusMessage("Instructor New Instructor has been successfully created");
		
		String newInstructorDetails = BackDoor.getAccountAsJson("newInstructor");
		assertTrue(newInstructorDetails.contains("\"googleId\": \"newInstructor\""));
		String instructorVerification = BackDoor.getInstructorAsJson("newInstructor", "newInstructor-demo");
		assertTrue(instructorVerification.contains("\"courseId\": \"newInstructor-demo\""));
		assertTrue(instructorVerification.contains("\"googleId\": \"newInstructor\""));
		
		______TS("test create account with registered data");
		bi.fillString(By.name(Common.PARAM_INSTRUCTOR_ID), "newInstructor");
		bi.fillString(By.name(Common.PARAM_INSTRUCTOR_NAME), "New Instructor Duplicate");
		bi.fillString(By.name(Common.PARAM_INSTRUCTOR_EMAIL), "newInstructorDuplicate@gmail.com");
		bi.fillString(By.name(Common.PARAM_INSTRUCTOR_INSTITUTION), "Institution");
		bi.click(By.id("btnAddInstructor"));
		
		bi.waitForStatusMessage("The Google ID newInstructor is already registered as an instructor");
		
		______TS("test create account without sample data");
		bi.fillString(By.name(Common.PARAM_INSTRUCTOR_ID), "newInstructor2");
		bi.fillString(By.name(Common.PARAM_INSTRUCTOR_NAME), "New Instructor2");
		bi.fillString(By.name(Common.PARAM_INSTRUCTOR_EMAIL), "newInstructor2@gmail.com");
		bi.fillString(By.name(Common.PARAM_INSTRUCTOR_INSTITUTION), "Institution");
		bi.click(By.id("btnAddInstructor"));
		
		bi.waitForStatusMessage("Instructor New Instructor2 has been successfully created");
		
		newInstructorDetails = BackDoor.getAccountAsJson("newInstructor2");
		assertTrue(newInstructorDetails.contains("\"googleId\": \"newInstructor2\""));
		
		______TS("test create account with spaces around input values");
		bi.fillString(By.name(Common.PARAM_INSTRUCTOR_ID), "  newInstructor3  ");
		bi.fillString(By.name(Common.PARAM_INSTRUCTOR_NAME), "  New Instructor3  ");
		bi.fillString(By.name(Common.PARAM_INSTRUCTOR_EMAIL), "  newInstructor3@gmail.com  ");
		bi.fillString(By.name(Common.PARAM_INSTRUCTOR_INSTITUTION), "  Institution  ");
		bi.click(By.id("btnAddInstructor"));

		bi.waitForStatusMessage("Instructor New Instructor3 has been successfully created");

		newInstructorDetails = BackDoor.getAccountAsJson("newInstructor3");
		assertTrue(newInstructorDetails.contains("\"googleId\": \"newInstructor3\""));
		assertTrue(newInstructorDetails.contains("\"name\": \"New Instructor3\""));
		assertTrue(newInstructorDetails.contains("\"email\": \"newInstructor3@gmail.com\""));
		assertTrue(newInstructorDetails.contains("\"institute\": \"Institution\""));
		
	}

}
