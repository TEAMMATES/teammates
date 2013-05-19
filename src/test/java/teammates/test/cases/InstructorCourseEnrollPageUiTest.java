package teammates.test.cases;

import static org.testng.AssertJUnit.assertEquals;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.openqa.selenium.By;


import teammates.common.Common;
import teammates.common.datatransfer.DataBundle;
import teammates.test.driver.BackDoor;
import teammates.test.driver.BrowserInstance;
import teammates.test.driver.BrowserInstancePool;
import teammates.test.driver.TestProperties;

 

/**
 * Tests Instructor Course Enroll UI
 */
public class InstructorCourseEnrollPageUiTest extends BaseTestCase {
	private static BrowserInstance bi;
	private static DataBundle scn;
	
	private static String enrollString = "";
	
	private static String appUrl = TestProperties.inst().TEAMMATES_URL;
	private static String jsonString;

	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader();
		
		startRecordingTimeForDataImport();
		jsonString = Common.readFile(Common.TEST_DATA_FOLDER+"/InstructorCourseEnrollUiTest.json");
		scn = Common.getTeammatesGson().fromJson(jsonString, DataBundle.class);
		BackDoor.deleteCourses(jsonString);
		BackDoor.deleteInstructors(jsonString);
		String backDoorOperationStatus = BackDoor.persistNewDataBundle(jsonString);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, backDoorOperationStatus);
		reportTimeForDataImport();
		
		// NEW
		enrollString += "Team 3 | Emily France | emily.f.tmms@gmail.com | This student has just been added\n";
		// Student with no comment
		enrollString += "Team 3 | Frank Galoe | frank.g.tmms@gmail.com\n";
		// Student with no team
		enrollString += " | Gary Harbine | gary.h.tmms@gmail.com | This student has no team\n";
		// MODIFIED
		enrollString += "Team 1 | Alice Betsy | alice.b.tmms@gmail.com | This comment has been changed\n";
		// UNMODIFIED
		enrollString += "Team 1 | Benny Charles | benny.c.tmms@gmail.com | This student's name is Benny Charles";
		
		
		bi = BrowserInstancePool.getBrowserInstance();
		
		bi.loginAdmin(TestProperties.inst().TEST_ADMIN_ACCOUNT, TestProperties.inst().TEST_ADMIN_PASSWORD);
		String link = appUrl+Common.PAGE_INSTRUCTOR_COURSE_ENROLL;
		link = Common.addParamToUrl(link,Common.PARAM_COURSE_ID,scn.courses.get("CCEnrollUiT.CS2104").id);
		link = Common.addParamToUrl(link,Common.PARAM_USER_ID,scn.instructors.get("CCEnrollUiT.teammates.test").googleId);
		bi.goToUrl(link);
	}
	
	@AfterClass
	public static void classTearDown() throws Exception {
		BrowserInstancePool.release(bi);
		printTestClassFooter();
		
		// Always cleanup
		BackDoor.deleteCourses(jsonString);
	}
	
	@Test
	public void testInstructorCourseEnrollPage() throws Exception{
		
		______TS("Check sample spreadsheet link");
		
		String spreadSheetLink = bi.getElementRelativeHref(By.id("spreadsheet_download"));
		bi.downloadAndVerifyFile(spreadSheetLink,"B2F8A93F24ACAC5713BCBC42DAF1FDA59F7AE04B");
		
		______TS("failure case - no students data");
		
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/instructorCourseEnrollPage.html");
		
		String errorString = "  \t  \n  \r";
		bi.click(By.id("button_enroll"));
		bi.waitForStatusMessage("Please input at least one student detail.");
		
		______TS("failure case - errors in enroll data");
		
		errorString = "a|b|c|d";
		bi.fillString(By.id("enrollstudents"), errorString); //invalid email address
		bi.click(By.id("button_enroll"));
		assertContains(bi.getElementText(By.id("enrollstudents")), errorString);
		
		bi.fillString(By.id("enrollstudents"), "");
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/instructorCourseEnrollError.html");

		______TS("success case");
		
		bi.fillString(By.id("enrollstudents"), enrollString);
		bi.click(By.id("button_enroll"));
		
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/instructorCourseEnrollPageResult.html");
		
		bi.click(By.id("edit_enroll"));	
		assertContainsRegex("{*}Enroll Students for CCEnrollUiT.CS2104{*}",bi.getCurrentPageSource());
		
		String link = appUrl+Common.PAGE_INSTRUCTOR_COURSE;
		link = Common.addParamToUrl(link,Common.PARAM_COURSE_ID,scn.courses.get("CCEnrollUiT.CS2104").id);
		link = Common.addParamToUrl(link,Common.PARAM_USER_ID,scn.instructors.get("CCEnrollUiT.teammates.test").googleId);
		bi.goToUrl(link);
		assertEquals("CCEnrollUiT.CS2104 Programming Language Concepts 4 7 3 Enroll View Edit Delete",
						bi.getElementText(By.className("courses_row")));
	}
}