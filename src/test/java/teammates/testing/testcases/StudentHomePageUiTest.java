package teammates.testing.testcases;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.api.Common;
import teammates.datatransfer.DataBundle;
import teammates.datatransfer.StudentData;
import teammates.testing.config.Config;
import teammates.testing.lib.BackDoor;
import teammates.testing.lib.BrowserInstance;
import teammates.testing.lib.BrowserInstancePool;

/**
 * Tests Student Homepage UI
 * @author Aldrian Obaja
 */
public class StudentHomePageUiTest extends BaseTestCase {
	private static BrowserInstance bi;
	private static DataBundle scn;
	
	private static String appURL = Config.inst().TEAMMATES_URL;

	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader("StudentHomeUITest");
		String jsonString = Common.readFile(Common.TEST_DATA_FOLDER+"/StudentHomeUiTest.json");
		scn = Common.getTeammatesGson().fromJson(jsonString, DataBundle.class);
		
		System.out.println("Importing test data...");
		BackDoor.deleteCoordinators(jsonString);
		long start = System.currentTimeMillis();
		System.out.println(BackDoor.persistNewDataBundle(jsonString));
		System.out.println("The test data was imported in "+(System.currentTimeMillis()-start)+" ms");
		
		bi = BrowserInstancePool.getBrowserInstance();
		
		bi.loginStudent(scn.students.get("alice.tmms@SHomeUiT.CS2104").id, Config.inst().TEAMMATES_APP_PASSWORD);
		bi.goToUrl(appURL+Common.PAGE_STUDENT_HOME);
	}
	
	@AfterClass
	public static void classTearDown() throws Exception {
		BrowserInstancePool.release(bi);
		printTestClassFooter("StudentHomeUITest");
	}

	@Test	
	public void testStudentHomeCoursePageHTML() throws Exception{
		bi.printCurrentPage(Common.TEST_PAGES_FOLDER+"/StudentHomeHTML.html");
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/StudentHomeHTML.html");
		
		BackDoor.deleteCourse(scn.courses.get("SHomeUiT.CS2104").id);
		BackDoor.deleteCourse(scn.courses.get("SHomeUiT.CS1101").id);

		// Should be unauthorized since the student is not in any course, and hence not a student
		bi.goToUrl(appURL+Common.PAGE_STUDENT_HOME);
		bi.printCurrentPage(Common.TEST_PAGES_FOLDER+"/unauthorized.html");
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/unauthorized.html");
		
		BackDoor.createCourse(scn.courses.get("SHomeUiT.CS2104"));
		StudentData alice = scn.students.get("alice.tmms@SHomeUiT.CS2104");
		alice.id = null;
		BackDoor.createStudent(alice);
		
		bi.goToUrl(appURL+Common.PAGE_STUDENT_HOME);
		bi.fillString(bi.studentInputRegKey, "");
		bi.click(bi.studentJoinCourseButton);

		bi.printCurrentPage(Common.TEST_PAGES_FOLDER+"/StudentHomeJoined.html");
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/StudentHomeJoined.html");
	}
}