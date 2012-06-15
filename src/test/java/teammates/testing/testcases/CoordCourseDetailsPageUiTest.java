package teammates.testing.testcases;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.api.Common;
import teammates.datatransfer.DataBundle;
import teammates.jsp.Helper;
import teammates.testing.config.Config;
import teammates.testing.lib.BrowserInstance;
import teammates.testing.lib.BrowserInstancePool;
import teammates.testing.lib.TMAPI;

/**
 * Tests Coordinator Course Details UI
 * @author Aldrian Obaja
 */
public class CoordCourseDetailsPageUiTest extends BaseTestCase {
	private static BrowserInstance bi;
	private static DataBundle scn;
	
	private static String appURL = Config.inst().TEAMMATES_URL.replaceAll("/(?=$)","");

	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader("CoordCourseDetailsTest");
		String jsonString = Common.readFile(Common.TEST_DATA_FOLDER+"/CoordCourseDetailsUiTest.json");
		scn = Common.getTeammatesGson().fromJson(jsonString, DataBundle.class);

		System.out.println("Importing test data...");
		long start = System.currentTimeMillis();
		TMAPI.deleteCoordinators(jsonString);
		System.out.println(TMAPI.persistNewDataBundle(jsonString));
		System.out.println("The test data was imported in "+(System.currentTimeMillis()-start)+" ms");
		
		bi = BrowserInstancePool.getBrowserInstance();
		
		bi.loginCoord(scn.coords.get("teammates.test").id, Config.inst().TEAMMATES_APP_PASSWD);
		bi.goToUrl(Helper.addParam(appURL+Common.PAGE_COORD_COURSE_DETAILS,Common.PARAM_COURSE_ID,scn.courses.get("CCDetailsUiT.CS2104").id));
	}
	
	@AfterClass
	public static void classTearDown() throws Exception {
		BrowserInstancePool.release(bi);
		printTestClassFooter("CoordCourseDetailsUITest");
	}
	
	@Test
	public void testCoordCourseDetailsPage() throws Exception{
//		bi.printCurrentPage(Common.TEST_PAGES_FOLDER+"/CoordCourseDetailsPage.html");
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/CoordCourseDetailsPage.html");
	}
}