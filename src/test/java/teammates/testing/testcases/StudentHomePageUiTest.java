package teammates.testing.testcases;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.api.Common;
import teammates.datatransfer.DataBundle;
import teammates.testing.config.Config;
import teammates.testing.lib.BrowserInstance;
import teammates.testing.lib.BrowserInstancePool;
import teammates.testing.lib.TMAPI;

/**
 * Tests Studentinator Homepage UI
 * @author Aldrian Obaja
 */
public class StudentHomePageUiTest extends BaseTestCase {
	private static BrowserInstance bi;
	private static DataBundle scn;
	
	private static String appURL = Config.inst().TEAMMATES_URL.replaceAll("/(?=$)","");

	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader("StudentHomeUITest");
		String jsonString = Common.readFile(Common.TEST_DATA_FOLDER+"/StudentHomeUiTest.json");
		scn = Common.getTeammatesGson().fromJson(jsonString, DataBundle.class);
		
		TMAPI.deleteCoordinators(jsonString);
		System.out.println("Importing test data...");
		long start = System.currentTimeMillis();
		System.out.println(TMAPI.persistNewDataBundle(jsonString));
		System.out.println("The test data was imported in "+(System.currentTimeMillis()-start)+" ms");
		
		bi = BrowserInstancePool.getBrowserInstance();
		
		bi.loginStudent(scn.students.get("alice.tmms@SHomeUiT.CS2104").id, Config.inst().TEAMMATES_APP_PASSWD);
		bi.goToUrl(appURL+Common.PAGE_STUDENT_HOME);
	}
	
	@AfterClass
	public static void classTearDown() throws Exception {
		BrowserInstancePool.release(bi);
		printTestClassFooter("StudentHomeUITest");
	}

	@Test	
	public void testStudentHomeCoursePageHTML() throws Exception{
//		bi.printCurrentPage(Common.TEST_PAGES_FOLDER+"/StudentHomeHTML.html");
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/StudentHomeHTML.html");
		
		TMAPI.deleteCourse(scn.courses.get("SHomeUiT.CS2104").id);
		TMAPI.deleteCourse(scn.courses.get("SHomeUiT.CS1101").id);

		// Should be unauthorized since the student is not in any course, and hence not a student
		bi.goToUrl(appURL+Common.PAGE_STUDENT_HOME);
//		bi.printCurrentPage(Common.TEST_PAGES_FOLDER+"/unauthorized.html");
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/unauthorized.html");
	}
}