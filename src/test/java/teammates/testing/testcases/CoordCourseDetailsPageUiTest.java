package teammates.testing.testcases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.api.Common;
import teammates.datatransfer.DataBundle;
import teammates.exception.NoAlertAppearException;
import teammates.jsp.Helper;
import teammates.testing.config.Config;
import teammates.testing.lib.BackDoor;
import teammates.testing.lib.BrowserInstance;
import teammates.testing.lib.BrowserInstancePool;
import teammates.testing.lib.SharedLib;

/**
 * Tests Coordinator Course Details UI
 */
public class CoordCourseDetailsPageUiTest extends BaseTestCase {
	private static BrowserInstance bi;
	private static DataBundle scn;
	
	private static String appUrl = Config.inst().TEAMMATES_URL;

	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader();
		
		startRecordingTimeForDataImport();
		String jsonString = Common.readFile(Common.TEST_DATA_FOLDER+"/CoordCourseDetailsUiTest.json");
		scn = Common.getTeammatesGson().fromJson(jsonString, DataBundle.class);
		BackDoor.deleteCoordinators(jsonString);
		String backDoorOperationStatus = BackDoor.persistNewDataBundle(jsonString);
		print(backDoorOperationStatus);
		reportTimeForDataImport();
		
		bi = BrowserInstancePool.getBrowserInstance();

		bi.loginAdmin(Config.inst().TEAMMATES_ADMIN_ACCOUNT, Config.inst().TEAMMATES_ADMIN_PASSWORD);
		String link = appUrl+Common.PAGE_COORD_COURSE_DETAILS;
		link = Helper.addParam(link,Common.PARAM_COURSE_ID,scn.courses.get("CCDetailsUiT.CS2104").id);
		link = Helper.addParam(link,Common.PARAM_USER_ID,scn.coords.get("teammates.test").id);
		bi.goToUrl(link);
	}
	
	@AfterClass
	public static void classTearDown() throws Exception {
		BrowserInstancePool.release(bi);
		printTestClassFooter();
	}
	
	@Test 
	public void runTestsInOrder() throws Exception{
		testCoordCourseDetailsPageHTML();
		testCoordCourseDetailsRemindStudent();
		//we put all tests here because we want this test to run last
		testCoordCourseDetailsDeleteStudent();
	}
	
	public void testCoordCourseDetailsPageHTML() throws Exception{
		printTestCaseHeader();
	
		______TS("default view");
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/coordCourseDetailsPage.html");
		
		______TS("sort by team name");
		bi.click(bi.coordCourseDetailSortByTeamName);
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/coordCourseDetailsByTeam.html");
		
		______TS("sort by status");
		bi.click(bi.coordCourseDetailSortByStatus);
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/coordCourseDetailsByStatus.html");
		
		______TS("sort by student name");
		bi.click(bi.coordCourseDetailSortByStudentName);
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/coordCourseDetailsPage.html");
	}
	
	public void testCoordCourseDetailsRemindStudent(){
		printTestCaseHeader();
		
		String studentName = scn.students.get("benny.tmms@CCDetailsUiT.CS2104").name;
		String studentEmail = scn.students.get("benny.tmms@CCDetailsUiT.CS2104").email;
		String otherStudentEmail = scn.students.get("charlie.tmms@CCDetailsUiT.CS2104").email;
		String registeredStudentEmail = scn.students.get("alice.tmms@CCDetailsUiT.CS2104").email;
		
		______TS("sending reminder to a single student to join course");
		
		bi.clickCoordCourseDetailRemind(studentName);
		
		if(!Config.inst().isLocalHost()){
			String key = BackDoor.getKeyForStudent(scn.courses.get("CCDetailsUiT.CS2104").id, studentEmail);
			bi.waitForEmail();
			assertEquals(key,SharedLib.getRegistrationKeyFromGmail(studentEmail, Config.inst().TEAMMATES_APP_PASSWORD, scn.courses.get("CCDetailsUiT.CS2104").id));
		}
		
		______TS("sending reminder to all unregistered students to join course");
		
		bi.clickAndConfirm(bi.coordCourseDetailRemindButton);
		if(!Config.inst().isLocalHost()){
			bi.waitForEmail();
			
			//verify an unregistered student received reminder
			String key = BackDoor.getKeyForStudent(scn.courses.get("CCDetailsUiT.CS2104").id, otherStudentEmail);
			assertEquals(key,SharedLib.getRegistrationKeyFromGmail(otherStudentEmail, Config.inst().TEAMMATES_APP_PASSWORD, scn.courses.get("CCDetailsUiT.CS2104").id));
			
			//verify a registered student did not receive a reminder
			assertEquals(null,SharedLib.getRegistrationKeyFromGmail(registeredStudentEmail, Config.inst().TEAMMATES_APP_PASSWORD, scn.courses.get("CCDetailsUiT.CS2104").id));
		}
	}

	public void testCoordCourseDetailsDeleteStudent() throws Exception{
		printTestCaseHeader();
		
		String studentName = scn.students.get("benny.tmms@CCDetailsUiT.CS2104").name;
		String studentEmail = scn.students.get("benny.tmms@CCDetailsUiT.CS2104").email;
		
		int studentRowId = bi.getStudentRowId(studentName);
		assertTrue(studentRowId!=-1);
		
		______TS("click and cancel");
		
		try{
			bi.clickCoordCourseDetailStudentDeleteAndCancel(studentRowId);
			String student = BackDoor.getStudentAsJason(scn.courses.get("CCDetailsUiT.CS2104").id, studentEmail);
			if(isNullJSON(student)) {
				fail("Student was deleted when it's not supposed to be");
			}
		} catch (NoAlertAppearException e){
			fail("No alert box when clicking delete button at course details page.");
		}

		______TS("click and confirm");
		
		try{
			bi.clickCoordCourseDetailStudentDeleteAndConfirm(studentRowId);
			bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/coordCourseDetailsStudentDeleteSuccessful.html");
		} catch (NoAlertAppearException e){
			fail("No alert box when clicking delete button at course details page.");
		}
	}
}