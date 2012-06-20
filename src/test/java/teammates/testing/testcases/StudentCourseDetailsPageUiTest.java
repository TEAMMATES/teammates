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
 * Tests Student Course Details page
 * @author Aldrian Obaja
 */
public class StudentCourseDetailsPageUiTest extends BaseTestCase {
	private static BrowserInstance bi;
	private static DataBundle scn;
	
	private static String appURL = Config.inst().TEAMMATES_URL.replaceAll("/(?=$)","");

	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader("StudentEvalEditUITest");
		String jsonString = Common.readFile(Common.TEST_DATA_FOLDER+"/StudentCourseDetailsUiTest.json");
		scn = Common.getTeammatesGson().fromJson(jsonString, DataBundle.class);
		
		BackDoor.deleteCoordinators(jsonString);
		System.out.println("Importing test data...");
		long start = System.currentTimeMillis();
		System.out.println(BackDoor.persistNewDataBundle(jsonString));
		System.out.println("The test data was imported in "+(System.currentTimeMillis()-start)+" ms");
		
		bi = BrowserInstancePool.getBrowserInstance();
		
		bi.loginStudent(scn.students.get("alice.tmms@SCDetailsUiT.CS2104").id, Config.inst().TEAMMATES_APP_PASSWORD);
	}
	
	@AfterClass
	public static void classTearDown() throws Exception {
		BrowserInstancePool.release(bi);
		printTestClassFooter("StudentCourseDetailsUITest");
	}

	@Test	
	public void testStudentCourseDetailsPageHTML() throws Exception{
		String link = Common.PAGE_STUDENT_COURSE_DETAILS;
		link = Helper.addParam(link, Common.PARAM_COURSE_ID, scn.courses.get("SCDetailsUiT.CS2104").id);
		bi.goToUrl(appURL+link);
//		bi.printCurrentPage(Common.TEST_PAGES_FOLDER+"/StudentCourseDetailsHTML.html");
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/StudentCourseDetailsHTML.html");
	}
}