package teammates.testing.testcases;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;

import teammates.api.Common;
import teammates.datatransfer.DataBundle;
import teammates.jsp.Helper;
import teammates.testing.config.Config;
import teammates.testing.lib.BackDoor;
import teammates.testing.lib.BrowserInstance;
import teammates.testing.lib.BrowserInstancePool;

/**
 * Tests Coordinator Course Enroll UI
 */
public class CoordCourseEnrollPageUiTest extends BaseTestCase {
	private static BrowserInstance bi;
	private static DataBundle scn;
	
	private static String enrollString = "";
	
	private static String appUrl = Config.inst().TEAMMATES_URL;

	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader();
		
		startRecordingTimeForDataImport();
		String jsonString = Common.readFile(Common.TEST_DATA_FOLDER+"/CoordCourseEnrollUiTest.json");
		scn = Common.getTeammatesGson().fromJson(jsonString, DataBundle.class);
		BackDoor.deleteCoordinators(jsonString);
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
		
		bi.loginAdmin(Config.inst().TEST_ADMIN_ACCOUNT, Config.inst().TEST_ADMIN_PASSWORD);
		String link = appUrl+Common.PAGE_COORD_COURSE_ENROLL;
		link = Helper.addParam(link,Common.PARAM_COURSE_ID,scn.courses.get("CCEnrollUiT.CS2104").id);
		link = Helper.addParam(link,Common.PARAM_USER_ID,scn.coords.get("teammates.test").id);
		bi.goToUrl(link);
	}
	
	@AfterClass
	public static void classTearDown() throws Exception {
		BrowserInstancePool.release(bi);
		printTestClassFooter();
	}
	
	@Test
	public void testCoordCourseEnrollPage() throws Exception{
		
		
		______TS("failure case");
		
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/coordCourseEnrollPage.html");
		
		bi.fillString(By.id("enrollstudents"), "a|b|c|d"); //invalid email address
		bi.click(By.id("button_enroll"));

		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/coordCourseEnrollError.html");
		
		______TS("success case");
		
		bi.fillString(By.id("enrollstudents"), enrollString);
		bi.click(By.id("button_enroll"));
		
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/coordCourseEnrollPageResult.html");
		
	}
}