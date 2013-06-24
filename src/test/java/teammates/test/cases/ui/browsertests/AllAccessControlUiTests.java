package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import java.io.FileNotFoundException;

import org.openqa.selenium.By;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.Common;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.EvaluationAttributes.EvalStatus;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.test.driver.BackDoor;
import teammates.test.driver.TestProperties;
import teammates.test.driver.Url;
import teammates.test.pageobjects.AppPage;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.DevServerLoginPage;
import teammates.test.pageobjects.GoogleLoginPage;
import teammates.test.pageobjects.HomePage;
import teammates.test.pageobjects.LoginPage;

/**
 * We do not test all access control at UI level. This class contains a few
 * representative tests only. Access control is tested fully at 'Action' level.
 */
public class AllAccessControlUiTests extends BaseUiTestCase {
	
	private static String unregUsername = TestProperties.inst().TEST_UNREG_ACCOUNT;
	private static String unregPassword = TestProperties.inst().TEST_UNREG_PASSWORD;

	private static String studentUsername = TestProperties.inst().TEST_STUDENT_ACCOUNT;
	private static String studentPassword = TestProperties.inst().TEST_STUDENT_PASSWORD;
	
	private static String instructorUsername = TestProperties.inst().TEST_INSTRUCTOR_ACCOUNT;
	private static String instructorPassword = TestProperties.inst().TEST_INSTRUCTOR_PASSWORD;

	static String adminUsername = TestProperties.inst().TEST_ADMIN_ACCOUNT;

	private static Browser browser;
	private static DataBundle testData;
	private static String backDoorOperationStatus;
	private static AppPage currentPage;
	private static String link;

	private static InstructorAttributes otherInstructor;

	// both TEST_INSTRUCTOR and TEST_STUDENT are from this course
	private static CourseAttributes ownCourse;

	private static EvaluationAttributes ownEvaluation;
	
	@BeforeClass
	public static void classSetup() {

		printTestClassHeader();

		testData = getTypicalDataBundle();
		
		otherInstructor = testData.instructors.get("instructor1OfCourse2");
		ownCourse = testData.courses.get("typicalCourse1");
		ownEvaluation = testData.evaluations.get("evaluation1InCourse1");

		browser = BrowserPool.getBrowser();
		
		currentPage = HomePage.getNewInstance(browser);
	}
	
	@BeforeMethod
	public void methodSetup(){
		restoreTestData();
	}

	@Test
	public void testUserNotLoggedIn() throws Exception {
		
		currentPage.logout().verifyHtml("/login.html");

		______TS("student pages");

		verifyRedirectToLogin(Common.PAGE_STUDENT_HOME);
		

		______TS("instructor pages");

		verifyRedirectToLogin(Common.PAGE_INSTRUCTOR_HOME);
		

		______TS("admin pages");

		verifyRedirectToLogin(Common.PAGE_ADMIN_HOME);
		
		
	}

	@Test
	public void testUserNotRegistered() throws Exception {

		______TS("student pages");

		loginStudent(unregUsername, unregPassword);

		verifyRedirectToWelcomeStrangerPage(Common.PAGE_STUDENT_HOME, unregUsername);


		______TS("instructor pages");

		loginInstructorUnsuccessfully(unregUsername, unregPassword);

		Url url = new Url(Common.PAGE_INSTRUCTOR_HOME);
		verifyRedirectToNotAuthorized(url);
		verifyCannotMasquerade(url, otherInstructor.googleId);


		______TS("admin pages");
		
		//cannot access admin while logged in as student
		verifyCannotAccessAdminPages();
		

	}

	@Test
	public void testStudentAccessToAdminPages() throws Exception {
		loginStudent(studentUsername, studentPassword);
		verifyCannotAccessAdminPages();
	}

	@Test
	public void testStudentHome() {
		loginStudent(studentUsername, studentPassword);
		verifyPageContains(Common.PAGE_STUDENT_HOME, studentUsername
				+ "{*}Student Home{*}View Team");
	}



	@Test
	public void testStudentEvalSubmission() {
		
		loginStudent(studentUsername, studentPassword);
		
		link = Common.PAGE_STUDENT_EVAL_SUBMISSION_EDIT;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, ownCourse.id);
		EvaluationAttributes ownEvaluation = testData.evaluations.get("evaluation1InCourse1");
		link = Common.addParamToUrl(link, Common.PARAM_EVALUATION_NAME,	ownEvaluation.name);
		
		______TS("student cannot submit evaluation in AWAITING state");
	
		ownEvaluation.startTime = Common.getDateOffsetToCurrentTime(1);
		ownEvaluation.endTime = Common.getDateOffsetToCurrentTime(2);
		ownEvaluation.activated = false;
		assertEquals(EvalStatus.AWAITING, ownEvaluation.getStatus());
		backDoorOperationStatus = BackDoor.editEvaluation(ownEvaluation);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, backDoorOperationStatus);
		currentPage.navigateTo(new Url(link))
			.verifyStatus(Common.MESSAGE_EVALUATION_NOT_OPEN);
		assertEquals("true", currentPage.getElementAttribute(By.id(Common.PARAM_POINTS + "0"), "disabled"));
		assertEquals("true", currentPage.getElementAttribute(By.id(Common.PARAM_JUSTIFICATION + "0"), "disabled"));
		assertEquals("true", currentPage.getElementAttribute(By.id(Common.PARAM_COMMENTS + "0"), "disabled"));
		assertEquals("true", currentPage.getElementAttribute(By.id("button_submit"), "disabled"));
		
		______TS("student can view own evaluation submission page");
	
		link = Common.PAGE_STUDENT_EVAL_SUBMISSION_EDIT;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, ownCourse.id);
		link = Common.addParamToUrl(link, Common.PARAM_EVALUATION_NAME,
				ownEvaluation.name);
		verifyPageContains(link, studentUsername
				+ "{*}Evaluation Submission{*}" + ownCourse.id + "{*}"
				+ ownEvaluation.name);
	
		______TS("student cannot submit evaluation in CLOSED state");
		
		ownEvaluation.startTime = Common.getDateOffsetToCurrentTime(-2);
		ownEvaluation.endTime = Common.getDateOffsetToCurrentTime(-1);
		backDoorOperationStatus = BackDoor.editEvaluation(ownEvaluation);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, backDoorOperationStatus);
		currentPage.navigateTo(new Url(link))
			.verifyStatus(Common.MESSAGE_EVALUATION_NOT_OPEN);
		assertEquals("true", currentPage.getElementAttribute(By.id(Common.PARAM_POINTS + "0"), "disabled"));
		assertEquals("true", currentPage.getElementAttribute(By.id(Common.PARAM_JUSTIFICATION + "0"), "disabled"));
		assertEquals("true", currentPage.getElementAttribute(By.id(Common.PARAM_COMMENTS + "0"), "disabled"));
		assertEquals("true", currentPage.getElementAttribute(By.id("button_submit"), "disabled"));
		
		______TS("student cannot submit evaluation in CLOSED state (evaluation with different timezone)");
		//Set the end time to the next hour, but push the timezone ahead 2 hours, so the evaluation has expired by 1 hour
		//Then we verify that the evaluation is disabled
		ownEvaluation.endTime = Common.getNextHour();
		ownEvaluation.timeZone = 10.0;   //put user's timezone ahead by 10hrs
		//TODO: this test case needs tweaking. It fails on some computers when
		//  the above is set to +2. Furthermore, we need a test case to ensure
		//  editing is enabled when the user timezone is behind. This test 
		//  case only checks if editing is disabled when timezone is ahead.
		backDoorOperationStatus = BackDoor.editEvaluation(ownEvaluation);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, backDoorOperationStatus);
		currentPage.navigateTo(new Url(link))
			.verifyStatus(Common.MESSAGE_EVALUATION_NOT_OPEN);
		assertEquals("true", currentPage.getElementAttribute(By.id(Common.PARAM_POINTS + "0"), "disabled"));
		assertEquals("true", currentPage.getElementAttribute(By.id(Common.PARAM_JUSTIFICATION + "0"), "disabled"));
		assertEquals("true", currentPage.getElementAttribute(By.id(Common.PARAM_COMMENTS + "0"), "disabled"));
		assertEquals("true", currentPage.getElementAttribute(By.id("button_submit"), "disabled"));
		
		______TS("student cannot submit evaluation in PUBLISHED state");
	
		ownEvaluation.endTime = Common.getDateOffsetToCurrentTime(-1);
		ownEvaluation.timeZone = 0.0;
		ownEvaluation.published = true;
		assertEquals(EvalStatus.PUBLISHED, ownEvaluation.getStatus());
		backDoorOperationStatus = BackDoor.editEvaluation(ownEvaluation);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, backDoorOperationStatus);
		currentPage.navigateTo(new Url(link))
			.verifyStatus(Common.MESSAGE_EVALUATION_NOT_OPEN);
		assertEquals("true", currentPage.getElementAttribute(By.id(Common.PARAM_POINTS + "0"), "disabled"));
		assertEquals("true", currentPage.getElementAttribute(By.id(Common.PARAM_JUSTIFICATION + "0"), "disabled"));
		assertEquals("true", currentPage.getElementAttribute(By.id(Common.PARAM_COMMENTS + "0"), "disabled"));
		assertEquals("true", currentPage.getElementAttribute(By.id("button_submit"), "disabled"));
		
	}

	@Test
	public void testStudentEvalResult() {
		loginStudent(studentUsername, studentPassword);
		______TS("student cannot view own evaluation result before publishing");
		
		ownEvaluation.published = false;
		assertTrue(EvalStatus.PUBLISHED != ownEvaluation.getStatus());
		backDoorOperationStatus = BackDoor.editEvaluation(ownEvaluation);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, backDoorOperationStatus);
	
		link = Common.PAGE_STUDENT_EVAL_RESULTS;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, ownCourse.id);
		link = Common.addParamToUrl(link, Common.PARAM_EVALUATION_NAME,
				ownEvaluation.name);
		verifyRedirectToNotAuthorized(link);
	
		______TS("student can view own evaluation result after publishing");
	
		ownEvaluation.startTime = Common.getDateOffsetToCurrentTime(-2);
		ownEvaluation.endTime = Common.getDateOffsetToCurrentTime(-1);
		ownEvaluation.timeZone = 0.0;
		ownEvaluation.published = true;
		assertEquals(EvalStatus.PUBLISHED, ownEvaluation.getStatus());
		backDoorOperationStatus = BackDoor.editEvaluation(ownEvaluation);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, backDoorOperationStatus);
		verifyPageContains(link, studentUsername + "{*}Evaluation Results{*}"
				+ ownEvaluation.name + "{*}" + ownCourse.id);
	}
	
	@Test
	public void testInstructorHome() {
	
		loginInstructor(instructorUsername, instructorPassword);
	
		______TS("can view own homepage");
	
		link = Common.PAGE_INSTRUCTOR_HOME;
		verifyPageContains(link, instructorUsername + "{*}Instructor Home{*}"
				+ ownCourse.id);
	
		______TS("cannot view other homepage");
	
		link = Common.PAGE_INSTRUCTOR_HOME;
		verifyCannotMasquerade(link, otherInstructor.googleId);
	
	}

	

	private void loginStudent(String userName, String password) {
		currentPage.logout();
		LoginPage loginPage = HomePage.getNewInstance(browser).clickStudentLogin();
		currentPage = loginPage.loginAsStudent(userName, password);
	}
	
	private void loginInstructorUnsuccessfully(String userName, String password) {
		currentPage.logout();
		LoginPage loginPage = HomePage.getNewInstance(browser).clickInstructorLogin();
		currentPage = loginPage.loginAsInstructorUnsuccessfully(userName, password);
	}
	
	private void loginInstructor(String userName, String password) {
		currentPage.logout();
		LoginPage loginPage = HomePage.getNewInstance(browser).clickInstructorLogin();
		currentPage = loginPage.loginAsInstructor(userName, password);
	}

	private static void restoreTestData() {
		testData = getTypicalDataBundle();
		try {
			String jsonString = Common.readFile(Common.TEST_DATA_FOLDER
					+ "/typicalDataBundle.json");
			BackDoor.deleteFeedbackSessions(jsonString);
			BackDoor.deleteCourses(jsonString);
			BackDoor.deleteCourse("new-course-tCCA"); // Another course added during testing, not in DataBundle
			BackDoor.deleteInstructors(jsonString);
			
			// According to original code, this test suite requires some modifications to be made to the Databundle
			// The student tests use TEST_STUDENT_ACCOUNT
			//	- so we will replace the entry "student1InCourse1" to have the id TEST_STUDENT_ACCOUNT
			// The instructor tests use TEST_INSTRUCTOR_ACCOUNT
			//	- so we will replace the entry "instructor1OfCourse1" to have the id TEST_INSTRUCTOR_ACCOUNT
			
			DataBundle db = Common.getTeammatesGson().fromJson(jsonString, DataBundle.class); // Get the objects to replace
			db.students.get("student1InCourse1").googleId = TestProperties.inst().TEST_STUDENT_ACCOUNT;
			db.instructors.get("instructor1OfCourse1").googleId = TestProperties.inst().TEST_INSTRUCTOR_ACCOUNT;
			jsonString = Common.getTeammatesGson().toJson(db); // Reconvert back to string.
			
            String backDoorOperationStatus = BackDoor.persistNewDataBundle(jsonString); // Persist as usual
            assertEquals(Common.BACKEND_STATUS_SUCCESS, backDoorOperationStatus);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void verifyCannotAccessAdminPages() {
		//cannot access directly
		Url url = new Url(Common.PAGE_ADMIN_HOME);
		verifyRedirectToNotAuthorized(url);
		//cannot access by masquerading either
		url = url.withUserId(adminUsername);
		verifyRedirectToNotAuthorized(url);
	}

	private void verifyCannotMasquerade(String link, String otherInstructorId) {
		link = Common.addParamToUrl(link, Common.PARAM_USER_ID, otherInstructorId);
		verifyRedirectToNotAuthorized(link);
	}
	
	private void verifyCannotMasquerade(Url url, String otherInstructorId) {
		verifyRedirectToNotAuthorized(url.withUserId(otherInstructorId));
	}

	private void verifyRedirectToWelcomeStrangerPage(String path, String unregUsername) {
		printUrl(appUrl + path);
		currentPage.navigateTo(new Url(path));
		// A simple regex check is enough because we do full HTML tests
		// elsewhere
		assertContainsRegex("{*}" + unregUsername + "{*}Welcome stranger{*}",
				currentPage.getPageSource());
	}

	private void verifyRedirectToNotAuthorized() {
		String pageSource = currentPage.getPageSource();
		assertTrue(pageSource.contains("You are not authorized to view this page.")||
				pageSource.contains("Your client does not have permission"));
	}

	private void verifyRedirectToNotAuthorized(String path) {
		printUrl(appUrl + path);
		currentPage.navigateTo(new Url(path));
		verifyRedirectToNotAuthorized();
	}
	
	private void verifyRedirectToNotAuthorized(Url url) {
		printUrl(url.toString());
		currentPage.navigateTo(url);
		verifyRedirectToNotAuthorized();
	}

	private void verifyPageContains(String path, String targetText) {
		printUrl(appUrl + path);
		currentPage.navigateTo(new Url(path));
		assertContainsRegex(targetText, currentPage.getPageSource());
	}

	private void verifyRedirectToLogin(String path) {
		printUrl(appUrl + path);
		currentPage.navigateTo(new Url(path));
		assertTrue(isLoginPage(currentPage));
	}

	private boolean isLoginPage(AppPage currentPage) {
		return GoogleLoginPage.containsExpectedPageContents(currentPage.getPageSource())
				|| DevServerLoginPage.containsExpectedPageContents(currentPage.getPageSource());
	}

	private void printUrl(String url) {
		print("   " + url);
	}

	@AfterClass
	public static void classTearDown() throws Exception {
		BrowserPool.release(browser);
	}

}
