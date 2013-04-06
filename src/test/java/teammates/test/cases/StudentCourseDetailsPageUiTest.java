package teammates.test.cases;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.common.Common;
import teammates.common.datatransfer.AccountData;
import teammates.common.datatransfer.DataBundle;
import teammates.test.driver.BackDoor;
import teammates.test.driver.BrowserInstance;
import teammates.test.driver.BrowserInstancePool;
import teammates.test.driver.TestProperties;

/**
 * Tests Student Course Details page
 */
public class StudentCourseDetailsPageUiTest extends BaseTestCase {
	private static BrowserInstance bi;
	private static DataBundle scn;
	
	private static String appUrl = TestProperties.inst().TEAMMATES_URL;
	private static String jsonString;

	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader();
		
		startRecordingTimeForDataImport();
		jsonString = Common.readFile(Common.TEST_DATA_FOLDER+"/StudentCourseDetailsUiTest.json");
		scn = Common.getTeammatesGson().fromJson(jsonString, DataBundle.class);
		BackDoor.deleteCourses(jsonString);
		BackDoor.deleteInstructors(jsonString);
		String backDoorOperationStatus = BackDoor.persistNewDataBundle(jsonString);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, backDoorOperationStatus);
		reportTimeForDataImport();
		
		bi = BrowserInstancePool.getBrowserInstance();

		bi.loginAdmin(TestProperties.inst().TEST_ADMIN_ACCOUNT, TestProperties.inst().TEST_ADMIN_PASSWORD);
	}
	
	@AfterClass
	public static void classTearDown() throws Exception {
		BrowserInstancePool.release(bi);
		printTestClassFooter();

		// Always cleanup
		BackDoor.deleteCourses(jsonString);
	}

	@Test	
	public void testStudentCourseDetailsPageHTML() throws Exception{
		
		______TS("with teammates");
		
		String studentId = scn.students.get("alice.tmms").id;
		
		//create the student account if it doesn't exist 
		AccountData testStudentAccount = new AccountData(studentId, "Alice Tmms", false, "alice.tmms@gmail.com", "National University of Singapore");
		String backDoorOperationStatus = BackDoor.createAccount(testStudentAccount);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, backDoorOperationStatus);
		
		String link = appUrl + Common.PAGE_STUDENT_COURSE_DETAILS;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, scn.courses.get("SCDetailsUiT.CS2104").id);
		link = Common.addParamToUrl(link, Common.PARAM_USER_ID , studentId);
		bi.goToUrl(link);
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/studentCourseDetailsWithTeammatesHTML.html");

		______TS("without teammates"); //TODO: to be removed if team is compulsory
		
		studentId = scn.students.get("charlie.tmms").id;
		
		//recreate student account if it doesn't exist
		testStudentAccount = new AccountData(studentId, "Charlie Tmms", false, "charlie.tmms@gmail.com", "National University of Singapore");
		backDoorOperationStatus = BackDoor.createAccount(testStudentAccount);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, backDoorOperationStatus);
		
		link = appUrl + Common.PAGE_STUDENT_COURSE_DETAILS;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, scn.courses.get("SCDetailsUiT.CS2104").id);
		link = Common.addParamToUrl(link, Common.PARAM_USER_ID , studentId);
		bi.goToUrl(link);
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/studentCourseDetailsWithoutTeammatesHTML.html");
		
		______TS("no team"); //TODO: to be removed if team is compulsory
		
		studentId = scn.students.get("danny.tmms").id;
		
		//recreate student account if it doesn't exist
		testStudentAccount = new AccountData(studentId, "Danny Tmms", false, "danny.tmms@gmail.com", "National University of Singapore");
		backDoorOperationStatus = BackDoor.createAccount(testStudentAccount);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, backDoorOperationStatus);
		
		link = appUrl + Common.PAGE_STUDENT_COURSE_DETAILS;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, scn.courses.get("SCDetailsUiT.CS2104").id);
		link = Common.addParamToUrl(link, Common.PARAM_USER_ID , studentId);
		bi.goToUrl(link);
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/studentCourseDetailsNoTeamHTML.html");
	}
	
}