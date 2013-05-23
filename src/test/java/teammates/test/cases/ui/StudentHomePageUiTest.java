package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeClass;
import teammates.common.Common;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.StudentAttributes;
import teammates.test.cases.BaseTestCase;
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
	private static String jsonString;
	
	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader();
		
		startRecordingTimeForDataImport();
		jsonString = Common.readFile(Common.TEST_DATA_FOLDER+"/StudentHomeUiTest.json");
		scn = Common.getTeammatesGson().fromJson(jsonString, DataBundle.class);
		BackDoor.deleteCourses(jsonString);
		BackDoor.deleteInstructors(jsonString);
		BackDoor.deleteCourse("idOfTypicalCourse1");
		String backDoorOperationStatus = BackDoor.persistNewDataBundle(jsonString);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, backDoorOperationStatus);
		reportTimeForDataImport();
		helpWindowClosed = true;
		
		bi = BrowserInstancePool.getBrowserInstance();
		
		bi.loginStudent(scn.students.get("alice.tmms@SHomeUiT.CS2104").googleId, TestProperties.inst().TEAMMATES_COMMON_PASSWORD_FOR_STUDENT_ACCOUNTS);
	}
	
	@AfterClass
	public static void classTearDown() throws Exception {
		if (!helpWindowClosed){
			bi.closeSelectedWindow();
			helpWindowClosed = true;
		}
		BrowserInstancePool.release(bi);
		printTestClassFooter();

		// Always cleanup
		BackDoor.deleteCourses(jsonString);
	}
	
	@BeforeMethod
	public void testSetup() {
		if (!helpWindowClosed){
			bi.closeSelectedWindow();
			helpWindowClosed = true;
		}
		
		BackDoor.deleteCourses(jsonString);
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
		
		______TS("invalid key");
		
		BackDoor.createCourse(scn.courses.get("SHomeUiT.CS2104"));
		BackDoor.createInstructor(scn.instructors.get("SHomeUiT.instr.CS2104"));
		BackDoor.createAccount(scn.accounts.get("SHomeUiT.instr"));
		StudentAttributes alice = scn.students.get("alice.tmms@SHomeUiT.CS2104");
		alice.googleId = null;
		BackDoor.createStudent(alice);
		bi.fillString(bi.studentInputRegKey, "ThisIsAnInvalidKey");
		bi.click(bi.studentJoinCourseButton);
		
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/studentHomeInvalidKey.html");
		
		______TS("just joined first course");

		BackDoor.createCourse(scn.courses.get("SHomeUiT.CS2104"));
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
		assertContains("TEAMMATES Online Peer Feedback System for Student Team Projects - Student Help", bi.getCurrentPageSource());
	}
}