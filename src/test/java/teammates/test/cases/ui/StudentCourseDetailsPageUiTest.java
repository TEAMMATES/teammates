package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import teammates.common.Common;
import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.test.cases.BaseTestCase;
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
		
		String studentId = scn.students.get("SCDetailsUiT.alice").id;
		
		//create the student account if it doesn't exist 
		AccountAttributes testStudentAccount = new AccountAttributes(studentId, "Alice Tmms", false, "SCDetailsUiT.alice@gmail.com", "National University of Singapore");
		String backDoorOperationStatus = BackDoor.createAccount(testStudentAccount);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, backDoorOperationStatus);
		
		String link = appUrl + Common.PAGE_STUDENT_COURSE_DETAILS;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, scn.courses.get("SCDetailsUiT.CS2104").id);
		link = Common.addParamToUrl(link, Common.PARAM_USER_ID , studentId);
		bi.goToUrl(link);
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/studentCourseDetailsWithTeammatesHTML.html");

		______TS("without teammates"); //TODO: to be removed if team is compulsory
		
		studentId = scn.students.get("SCDetailsUiT.charlie").id;
		
		//recreate student account if it doesn't exist
		testStudentAccount = new AccountAttributes(studentId, "Charlie Tmms", false, "SCDetailsUiT.charlie@gmail.com", "National University of Singapore");
		backDoorOperationStatus = BackDoor.createAccount(testStudentAccount);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, backDoorOperationStatus);
		
		link = appUrl + Common.PAGE_STUDENT_COURSE_DETAILS;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, scn.courses.get("SCDetailsUiT.CS2104").id);
		link = Common.addParamToUrl(link, Common.PARAM_USER_ID , studentId);
		bi.goToUrl(link);
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/studentCourseDetailsWithoutTeammatesHTML.html");
		
		______TS("no team"); //TODO: to be removed if team is compulsory
		
		studentId = scn.students.get("SCDetailsUiT.danny").id;
		
		//recreate student account if it doesn't exist
		testStudentAccount = new AccountAttributes(studentId, "Danny Tmms", false, "SCDetailsUiT.danny@gmail.com", "National University of Singapore");
		backDoorOperationStatus = BackDoor.createAccount(testStudentAccount);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, backDoorOperationStatus);
		
		link = appUrl + Common.PAGE_STUDENT_COURSE_DETAILS;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, scn.courses.get("SCDetailsUiT.CS2104").id);
		link = Common.addParamToUrl(link, Common.PARAM_USER_ID , studentId);
		bi.goToUrl(link);
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/studentCourseDetailsNoTeamHTML.html");
	}
	
}