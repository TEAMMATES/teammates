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
 */
public class StudentHomePageUiTest extends BaseTestCase {
	private static BrowserInstance bi;
	private static DataBundle scn;
	
	private static String appURL = Config.inst().TEAMMATES_URL;

	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader();
		
		startRecordingTimeForDataImport();
		String jsonString = Common.readFile(Common.TEST_DATA_FOLDER+"/StudentHomeUiTest.json");
		scn = Common.getTeammatesGson().fromJson(jsonString, DataBundle.class);
		BackDoor.deleteCoordinators(jsonString);
		String backDoorOperationStatus = BackDoor.persistNewDataBundle(jsonString);
		print(backDoorOperationStatus);
		reportTimeForDataImport();
		
		bi = BrowserInstancePool.getBrowserInstance();
		
		bi.loginStudent(scn.students.get("alice.tmms@SHomeUiT.CS2104").id, Config.inst().TEAMMATES_APP_PASSWORD);
	}
	
	@AfterClass
	public static void classTearDown() throws Exception {
		BrowserInstancePool.release(bi);
		printTestClassFooter();
	}

	@Test	
	public void testStudentHomeCoursePageHTML() throws Exception{
		printTestCaseHeader();
		
		______TS("typical case");
		
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/studentHomeHTML.html");
		
		BackDoor.deleteCourse(scn.courses.get("SHomeUiT.CS2104").id);
		BackDoor.deleteCourse(scn.courses.get("SHomeUiT.CS1101").id);
		
		______TS("empty");

		bi.goToUrl(appURL+Common.PAGE_STUDENT_HOME);
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/studentHomeHTMLEmpty.html");
		
		______TS("just joined first course");

		BackDoor.createCourse(scn.courses.get("SHomeUiT.CS2104"));
		StudentData alice = scn.students.get("alice.tmms@SHomeUiT.CS2104");
		alice.id = null;
		BackDoor.createStudent(alice);
		String courseID = scn.courses.get("SHomeUiT.CS2104").id;
		String studentEmail = scn.students.get("alice.tmms@SHomeUiT.CS2104").email;
		bi.fillString(bi.studentInputRegKey, BackDoor.getKeyForStudent(courseID, studentEmail));
		bi.click(bi.studentJoinCourseButton);

		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/studentHomeJoined.html");
	}
}