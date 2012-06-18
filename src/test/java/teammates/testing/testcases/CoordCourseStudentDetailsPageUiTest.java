package teammates.testing.testcases;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.api.Common;
import teammates.datatransfer.DataBundle;
import teammates.jsp.Helper;
import teammates.testing.config.Config;
import teammates.testing.lib.BackDoor;
import teammates.testing.lib.BrowserInstance;
import teammates.testing.lib.BrowserInstancePool;

/**
 * Tests Coordinator Course Student Details and Edit UI
 * @author Aldrian Obaja
 */
public class CoordCourseStudentDetailsPageUiTest extends BaseTestCase {
	private static BrowserInstance bi;
	private static DataBundle scn;
	
	private static String appURL = Config.inst().TEAMMATES_URL.replaceAll("/(?=$)","");

	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader("CoordCourseDetailsTest");
		String jsonString = Common.readFile(Common.TEST_DATA_FOLDER+"/CoordCourseStudentDetailsUiTest.json");
		scn = Common.getTeammatesGson().fromJson(jsonString, DataBundle.class);

		System.out.println("Importing test data...");
		long start = System.currentTimeMillis();
		BackDoor.deleteCoordinators(jsonString);
		System.out.println(BackDoor.persistNewDataBundle(jsonString));
		System.out.println("The test data was imported in "+(System.currentTimeMillis()-start)+" ms");
		
		bi = BrowserInstancePool.getBrowserInstance();
		
		bi.loginCoord(scn.coords.get("teammates.test").id, Config.inst().TEAMMATES_APP_PASSWD);
	}
	
	@AfterClass
	public static void classTearDown() throws Exception {
		BrowserInstancePool.release(bi);
		printTestClassFooter("CoordCourseStudentDetailsUITest");
	}
	
	@Test
	public void testCoordCourseStudentDetailsPage() throws Exception{
		String link = Helper.addParam(appURL+Common.PAGE_COORD_COURSE_STUDENT_DETAILS,Common.PARAM_COURSE_ID,scn.courses.get("CCSDetailsUiT.CS2104").id);
		link = Helper.addParam(link,Common.PARAM_STUDENT_EMAIL,scn.students.get("alice.tmms@CCSDetailsUiT.CS2104").email);
		bi.goToUrl(link);
		
//		bi.printCurrentPage(Common.TEST_PAGES_FOLDER+"/CoordCourseStudentDetailsPage.html");
		bi.verifyCurrentPageHTMLRegex(Common.TEST_PAGES_FOLDER+"/CoordCourseStudentDetailsPage.html");
	}
	
	@Test
	public void testCoordCourseStudentEditPage() throws Exception{
		String link = Helper.addParam(appURL+Common.PAGE_COORD_COURSE_STUDENT_EDIT,Common.PARAM_COURSE_ID,scn.courses.get("CCSDetailsUiT.CS2104").id);
		link = Helper.addParam(link,Common.PARAM_STUDENT_EMAIL,scn.students.get("alice.tmms@CCSDetailsUiT.CS2104").email);
		bi.goToUrl(link);
		
//		bi.printCurrentPage(Common.TEST_PAGES_FOLDER+"/CoordCourseStudentEditPage.html");
		bi.verifyCurrentPageHTMLRegex(Common.TEST_PAGES_FOLDER+"/CoordCourseStudentEditPage.html");
	}
}