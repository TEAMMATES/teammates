package teammates.test.cases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;

import teammates.common.Common;
import teammates.test.driver.BackDoor;
import teammates.test.driver.BrowserInstance;
import teammates.test.driver.BrowserInstancePool;
import teammates.test.driver.TestProperties;

public class AdminAccountManagementUiTest extends BaseTestCase{
	private static BrowserInstance bi;
	private static String appUrl = TestProperties.inst().TEAMMATES_URL;
	private static String jsonString;
	
	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader();
		
		bi = BrowserInstancePool.getBrowserInstance();
		
		bi.loginAdmin(TestProperties.inst().TEST_ADMIN_ACCOUNT, TestProperties.inst().TEST_ADMIN_PASSWORD);
		
		jsonString = Common.readFile(Common.TEST_DATA_FOLDER+"/AdminAccountManagementUiTest.json");
		BackDoor.deleteCourses(jsonString);
		BackDoor.deleteInstructors(jsonString);
		String backDoorOperationStatus = BackDoor.persistNewDataBundle(jsonString);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, backDoorOperationStatus);
		String link = appUrl+Common.PAGE_ADMIN_ACCOUNT_MANAGEMENT;
		bi.goToUrl(link);
	}
	
	@AfterClass
	public static void classTearDown() throws Exception {
		BrowserInstancePool.release(bi);
		printTestClassFooter();
		
		BackDoor.deleteCourses(jsonString);
		BackDoor.deleteInstructors(jsonString);
	}
	
	@Test
	public void testDeleteInstructorStatus(){
		String link = appUrl+Common.PAGE_ADMIN_ACCOUNT_MANAGEMENT;
		bi.goToUrl(link);

		bi.click(By.id("teammates.instr1_delete"));
		bi.waitForStatusMessage(Common.MESSAGE_INSTRUCTOR_STATUS_DELETED);
		String instructor = BackDoor.getAccountAsJson("teammates.instr1");
		assertTrue(instructor.contains("\"isInstructor\": false"));
	}
	
	
	@Test
	public void testRemoveInstructorFromCourse(){
		String link = appUrl+Common.PAGE_ADMIN_ACCOUNT_DETAILS;
		link = Common.addParamToUrl(link, Common.PARAM_INSTRUCTOR_ID, "teammates.instr2");
		bi.goToUrl(link);
		
		bi.click(By.id("instructor_CS2104"));
		bi.waitForStatusMessage(Common.MESSAGE_INSTRUCTOR_REMOVED_FROM_COURSE);
		String instructor = BackDoor.getInstructorAsJson("teammates.instr2", "CS2104");
		assertEquals("Instructor was not properly removed from the course", instructor, "null");
	}
	
	@Test
	public void testRemoveStudentFromCourse(){
		String link = appUrl+Common.PAGE_ADMIN_ACCOUNT_DETAILS;
		link = Common.addParamToUrl(link, Common.PARAM_INSTRUCTOR_ID, "teammates.instr2");
		bi.goToUrl(link);
		
		bi.click(By.id("student_CS1101"));
		bi.waitForStatusMessage(Common.MESSAGE_INSTRUCTOR_REMOVED_FROM_COURSE);
		String instructor = BackDoor.getStudentAsJson("CS1101", "teammates.instr2@gmail.com");
		assertEquals("Instructor was not properly removed from the course", instructor, "null");
	}
	
	@Test
	public void testDeleteInstructorAccount(){
		String link = appUrl+Common.PAGE_ADMIN_ACCOUNT_MANAGEMENT;
		bi.goToUrl(link);

		bi.clickAndConfirm(By.id("teammates.instr3_deleteAccount"));
		bi.waitForStatusMessage(Common.MESSAGE_INSTRUCTOR_ACCOUNT_DELETED);
		String instructor = BackDoor.getAccountAsJson("teammates.instr3");
		assertEquals("Instructor Account was not deleted properly", instructor, "null");
		
	}
	
}
