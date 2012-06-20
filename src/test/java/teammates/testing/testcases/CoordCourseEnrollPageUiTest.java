package teammates.testing.testcases;

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
 * @author Aldrian Obaja
 */
public class CoordCourseEnrollPageUiTest extends BaseTestCase {
	private static BrowserInstance bi;
	private static DataBundle scn;
	
	private static String enrollString = "";
	
	private static String appURL = Config.inst().TEAMMATES_URL.replaceAll("/(?=$)","");

	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader("CoordCourseEnrollTest");
		String jsonString = Common.readFile(Common.TEST_DATA_FOLDER+"/CoordCourseEnrollUiTest.json");
		scn = Common.getTeammatesGson().fromJson(jsonString, DataBundle.class);

		System.out.println("Importing test data...");
		long start = System.currentTimeMillis();
		BackDoor.deleteCoordinators(jsonString);
		System.out.println(BackDoor.persistNewDataBundle(jsonString));
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
		System.out.println("The test data was imported in "+(System.currentTimeMillis()-start)+" ms");
		
		bi = BrowserInstancePool.getBrowserInstance();
		
		bi.loginCoord(scn.coords.get("teammates.test").id, Config.inst().TEAMMATES_APP_PASSWORD);
		bi.goToUrl(Helper.addParam(appURL+Common.PAGE_COORD_COURSE_ENROLL,Common.PARAM_COURSE_ID,scn.courses.get("CCEnrollUiT.CS2104").id));
	}
	
	@AfterClass
	public static void classTearDown() throws Exception {
		BrowserInstancePool.release(bi);
		printTestClassFooter("CoordCourseEnrollUITest");
	}
	
	@Test
	public void testCoordCourseEnrollPage() throws Exception{
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/CoordCourseEnrollPage.html");
		
		bi.fillString(By.id("enrollstudents"), enrollString);
		bi.click(By.id("button_enroll"));

		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/CoordCourseEnrollPageResult.html");
		
	}
}