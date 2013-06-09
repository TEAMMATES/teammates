package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.Common;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.StudentAttributes;
import teammates.test.driver.BackDoor;
import teammates.test.driver.TestProperties;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.HomePage;
import teammates.test.pageobjects.StudentHelpPage;
import teammates.test.pageobjects.StudentHomePage;

/**
 * Covers Homepage and Login page for students. Some part of it is using a 
 * real Google account alice.tmms. 
 */
public class StudentHomePageUiTest extends BaseUiTestCase {
	private static Browser b;
	private static DataBundle testData;
	private StudentHomePage studentHome;
	
	private static String jsonString;
	
	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader();
		
		jsonString = Common.readFile(Common.TEST_DATA_FOLDER+"/StudentHomeUiTest.json");
		testData = Common.getTeammatesGson().fromJson(jsonString, DataBundle.class);
		BackDoor.deleteCourses(jsonString);
		BackDoor.deleteInstructors(jsonString);
		BackDoor.deleteCourse("idOfTypicalCourse1");
		String backDoorOperationStatus = BackDoor.persistNewDataBundle(jsonString);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, backDoorOperationStatus);
		
		b = BrowserPool.getBrowser();
	}


	@Test	
	public void allTests() throws Exception{
		testContent();
		testJoinAction();
		testLinks();
	}


	private void testContent() throws Exception {
		
		//TODO: login as admin to see 'welcome stranger' page. After that,
		//  login as student.
		
		studentHome = HomePage.getNewInstance(b)
	    		.clickStudentLogin()
	    		.loginAsStudent(
	    				testData.students.get("alice.tmms@SHomeUiT.CS2104").googleId, 
	    				TestProperties.inst().TEAMMATES_COMMON_PASSWORD_FOR_STUDENT_ACCOUNTS);
			
		______TS("typical case");
		
		studentHome.verifyHtml("/studentHomeHTML.html");
		
		BackDoor.deleteCourse(testData.courses.get("SHomeUiT.CS2104").id);
		BackDoor.deleteCourse(testData.courses.get("SHomeUiT.CS1101").id);
		
		______TS("no courses, 'welcome stranger' message");
	
		studentHome.clickHomeTab();
		studentHome.verifyHtml("/studentHomeHTMLEmpty.html");
	}


	private void testJoinAction() throws Exception {
		
		______TS("fail: invalid key");
		
		BackDoor.createCourse(testData.courses.get("SHomeUiT.CS2104"));
		BackDoor.createInstructor(testData.instructors.get("SHomeUiT.instr.CS2104"));
		BackDoor.createAccount(testData.accounts.get("SHomeUiT.instr"));
		StudentAttributes alice = testData.students.get("alice.tmms@SHomeUiT.CS2104");
		alice.googleId = null;
		BackDoor.createStudent(alice);
		studentHome.fillKey("ThisIsAnInvalidKey");
		studentHome.clickJoinButton();

		studentHome.verifyHtml("/studentHomeInvalidKey.html");
		
		______TS("joining the first course");

		BackDoor.createCourse(testData.courses.get("SHomeUiT.CS2104"));
		String courseId = testData.courses.get("SHomeUiT.CS2104").id;
		String studentEmail = testData.students.get("alice.tmms@SHomeUiT.CS2104").email;
		
		studentHome.fillKey(BackDoor.getKeyForStudent(courseId, studentEmail));
		studentHome.clickJoinButton();

		studentHome.verifyHtml("/studentHomeJoined.html");
	}


	private void testLinks() {
		
		______TS("help page");
		
		StudentHelpPage helpPage = studentHome.clickHelpLink();
		helpPage.close();
	}
	

	@AfterClass
	public static void classTearDown() throws Exception {
		BackDoor.deleteCourses(jsonString);
		BrowserPool.release(b);
	}
}