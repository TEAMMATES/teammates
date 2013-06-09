package teammates.test.cases.ui.browsertests;

import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.Assert;
import org.testng.AssertJUnit;
import teammates.common.Common;
import teammates.common.datatransfer.DataBundle;
import teammates.test.cases.BaseTestCase;
import teammates.test.driver.BackDoor;
import teammates.test.driver.BrowserInstance;
import teammates.test.driver.BrowserInstancePool;
import teammates.test.driver.EmailAccount;
import teammates.test.driver.NoAlertException;
import teammates.test.driver.TestProperties;

/**
 * Tests Instructor Course Details UI
 */
public class InstructorCourseDetailsPageUiTest extends BaseTestCase {
	private static BrowserInstance bi;
	private static DataBundle scn;
	
	private static String appUrl = TestProperties.inst().TEAMMATES_URL;
	private static String jsonString;

	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader();
		
		startRecordingTimeForDataImport();
		jsonString = Common.readFile(Common.TEST_DATA_FOLDER+"/InstructorCourseDetailsUiTest.json");
		scn = Common.getTeammatesGson().fromJson(jsonString, DataBundle.class);
		BackDoor.deleteCourses(jsonString);
		BackDoor.deleteInstructors(jsonString);
		String backDoorOperationStatus = BackDoor.persistNewDataBundle(jsonString);
		AssertJUnit.assertEquals(Common.BACKEND_STATUS_SUCCESS, backDoorOperationStatus);
		reportTimeForDataImport();
		
		bi = BrowserInstancePool.getBrowserInstance();

		bi.loginAdmin(TestProperties.inst().TEST_ADMIN_ACCOUNT, TestProperties.inst().TEST_ADMIN_PASSWORD);
		String link = appUrl+Common.PAGE_INSTRUCTOR_COURSE_DETAILS;
		link = Common.addParamToUrl(link,Common.PARAM_COURSE_ID,scn.courses.get("CCDetailsUiT.CS2104").id);
		link = Common.addParamToUrl(link,Common.PARAM_USER_ID,scn.instructors.get("CCDetailsUiT.instr").googleId);
		bi.goToUrl(link);
	}
	
	@AfterClass
	public static void classTearDown() throws Exception {
		BrowserInstancePool.release(bi);
		printTestClassFooter();
		
		// Always cleanup
		BackDoor.deleteCourses(jsonString);
	}
	
	@Test 
	public void runTestsInOrder() throws Exception{
		testInstructorCourseDetailsPageHTML();
		testInstructorCourseDetailsRemindStudent();
		//we put all tests here because we want this test to run last
		testInstructorCourseDetailsDeleteStudent();
	}
	
	public void testInstructorCourseDetailsPageHTML() throws Exception{
		______TS("default view");
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/instructorCourseDetailsPage.html");
		
		______TS("sort by status");
		bi.click(bi.instructorCourseDetailSortByStatus);
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/instructorCourseDetailsByStatus.html");
		
		bi.assertDataTablePattern(2,"{*}Joined{*}Joined{*}Yet to join{*}Yet to join");
		bi.click(bi.instructorCourseDetailSortByStatus);
		bi.assertDataTablePattern(2,"{*}Yet to join{*}Yet to join{*}Joined{*}Joined");
		
		______TS("sort by student name");
		bi.click(bi.instructorCourseDetailSortByStudentName);
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/instructorCourseDetailsPage.html");
		
		bi.assertDataTablePattern(1,"{*}Alice Betsy{*}Benny Charles{*}Charlie Davis{*}Danny Engrid");
		bi.click(bi.instructorCourseDetailSortByStudentName);
		bi.assertDataTablePattern(1,"{*}Danny Engrid{*}Charlie Davis{*}Benny Charles{*}Alice Betsy");
		
		______TS("sort by team name");
		bi.click(bi.instructorCourseDetailSortByTeamName);
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/instructorCourseDetailsByTeam.html");
		
		bi.assertDataTablePattern(0,"{*}Team 1{*}Team 1{*}Team 2{*}Team 2");
		bi.click(bi.instructorCourseDetailSortByTeamName);
		bi.assertDataTablePattern(0,"{*}Team 2{*}Team 2{*}Team 1{*}Team 1");
	}
	
	public void testInstructorCourseDetailsRemindStudent() {

		String studentName = scn.students.get("benny.tmms@CCDetailsUiT.CS2104").name;
		String studentEmail = scn.students
				.get("benny.tmms@CCDetailsUiT.CS2104").email;
		String otherStudentEmail = scn.students
				.get("charlie.tmms@CCDetailsUiT.CS2104").email;
		String registeredStudentEmail = scn.students
				.get("CCDetailsUiT.alice.tmms@CCDetailsUiT.CS2104").email;

		int studentRowId = bi.getStudentRowId(studentName);

		______TS("sending reminder to a single student to join course: click and cancel");

		try {
			bi.clickInstructorCourseDetailStudentRemindAndCancel(studentRowId);
		} catch (NoAlertException e) {
			Assert.fail("No alert box when clicking send invite button at course details page.");
		}

		String courseId = scn.courses.get("CCDetailsUiT.CS2104").id;
		String studentPassword = TestProperties.inst().TEAMMATES_COMMON_PASSWORD_FOR_STUDENT_ACCOUNTS;
		String keyToSend = Common.encrypt(BackDoor.getKeyForStudent(courseId,
				studentEmail));
		String keyReceivedInEmail = null;
		boolean isEmailEnabled = !TestProperties.inst().isDevServer();

		if (isEmailEnabled) {
			bi.waitForEmail();
			keyReceivedInEmail = EmailAccount.getRegistrationKeyFromGmail(
					studentEmail, studentPassword, courseId);
			String errorMessage = "cancel clicked, but key was sent :"
					+ keyReceivedInEmail + " to " + studentEmail;
			AssertJUnit.assertFalse(errorMessage, keyToSend.equals(keyReceivedInEmail));
		}

		______TS("sending reminder to a single student to join course: click and confirm");

		try {
			bi.clickInstructorCourseDetailStudentRemindAndConfirm(studentRowId);
		} catch (NoAlertException e) {
			Assert.fail("No alert box when clicking send button invite at course details page.");
		}

		if (isEmailEnabled) {
			bi.waitForEmail();
			keyReceivedInEmail = EmailAccount.getRegistrationKeyFromGmail(
					studentEmail, studentPassword, courseId);
			AssertJUnit.assertEquals(keyToSend, keyReceivedInEmail);
		}

		______TS("sending reminder to all unregistered students to join course");

		bi.clickAndConfirm(bi.instructorCourseDetailRemindButton);

		if (isEmailEnabled) {
			bi.waitForEmail();

			keyToSend = Common.encrypt(BackDoor.getKeyForStudent(courseId,
					otherStudentEmail));

			// verify an unregistered student received reminder
			keyReceivedInEmail = EmailAccount.getRegistrationKeyFromGmail(
					otherStudentEmail, studentPassword, courseId);
			AssertJUnit.assertEquals(keyToSend, keyReceivedInEmail);

			// verify a registered student did not receive a reminder
			keyReceivedInEmail = EmailAccount.getRegistrationKeyFromGmail(
					registeredStudentEmail, studentPassword, courseId);
			String errorMessage = "Registered student was sent key :"
					+ keyReceivedInEmail + " to " + studentEmail;
			AssertJUnit.assertFalse(errorMessage, keyToSend.equals(keyReceivedInEmail));
		}
	}

	public void testInstructorCourseDetailsDeleteStudent() throws Exception{
		
		
		String studentName = scn.students.get("benny.tmms@CCDetailsUiT.CS2104").name;
		String studentEmail = scn.students.get("benny.tmms@CCDetailsUiT.CS2104").email;
		
		int studentRowId = bi.getStudentRowId(studentName);
		AssertJUnit.assertTrue(studentRowId!=-1);
		
		______TS("click and cancel");
		
		try{
			bi.clickInstructorCourseDetailStudentDeleteAndCancel(studentRowId);
			String student = BackDoor.getStudentAsJson(scn.courses.get("CCDetailsUiT.CS2104").id, studentEmail);
			if(isNullJSON(student)) {
				Assert.fail("Student was deleted when it's not supposed to be");
			}
		} catch (NoAlertException e){
			Assert.fail("No alert box when clicking delete button at course details page.");
		}

		______TS("click and confirm");
		
		try{
			bi.clickInstructorCourseDetailStudentDeleteAndConfirm(studentRowId);
			bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/instructorCourseDetailsStudentDeleteSuccessful.html");
		} catch (NoAlertException e){
			Assert.fail("No alert box when clicking delete button at course details page.");
		}
	}
}