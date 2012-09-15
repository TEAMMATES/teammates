package teammates.test.cases;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.common.Common;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.StudentData;
import teammates.test.driver.BackDoor;
import teammates.test.driver.BrowserInstance;
import teammates.test.driver.BrowserInstancePool;
import teammates.test.driver.TestProperties;

/**
 * Tests Student Homepage UI
 */
public class StudentHomePageUiTest extends BaseTestCase {
	private static BrowserInstance bi;
	private static DataBundle scn;
	private static Boolean helpWindowClosed;
	
	private static String appURL = TestProperties.inst().TEAMMATES_URL;

	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader();
		
		startRecordingTimeForDataImport();
		String jsonString = Common.readFile(Common.TEST_DATA_FOLDER+"/StudentHomeUiTest.json");
		scn = Common.getTeammatesGson().fromJson(jsonString, DataBundle.class);
		BackDoor.deleteCoordinators(jsonString);
		String backDoorOperationStatus = BackDoor.persistNewDataBundle(jsonString);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, backDoorOperationStatus);
		reportTimeForDataImport();
		helpWindowClosed = true;
		
		bi = BrowserInstancePool.getBrowserInstance();
		
		bi.loginStudent(scn.students.get("alice.tmms@SHomeUiT.CS2104").id, TestProperties.inst().TEAMMATES_COMMON_PASSWORD_FOR_STUDENT_ACCOUNTS);
	}
	
	@AfterClass
	public static void classTearDown() throws Exception {
		if (!helpWindowClosed){
			bi.closeSelectedWindow();
			helpWindowClosed = true;
		}
		BrowserInstancePool.release(bi);
		printTestClassFooter();
	}
	
	@Before
	public void testSetup() {
		if (!helpWindowClosed){
			bi.closeSelectedWindow();
			helpWindowClosed = true;
		}
	}

	@Test	
	public void testStudentHomeCoursePageHTML() throws Exception{
			
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
	
	@Test
	public void testHelpLink() throws Exception{
		helpWindowClosed = false;
		bi.clickAndSwitchToNewWindow(bi.helpTab);
		assertContains("Teammates Onldine Peer Feedback System for Student Team Projects - Student Help", bi.getCurrentPageSource());
	}
}