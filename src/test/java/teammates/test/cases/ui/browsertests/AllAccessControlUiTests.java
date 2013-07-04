package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;


import org.openqa.selenium.By;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.EvaluationAttributes.EvalStatus;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.common.util.Url;
import teammates.test.driver.BackDoor;
import teammates.test.driver.TestProperties;
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

	private static String studentUsername = TestProperties.inst().TEST_STUDENT1_ACCOUNT;
	private static String studentPassword = TestProperties.inst().TEST_STUDENT1_PASSWORD;
	
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
	
	@Test
	public void testUserNotLoggedIn() throws Exception {
		
		restoreTypicalTestData();
		
		currentPage.logout().verifyHtml("/login.html");

		______TS("student pages");

		verifyRedirectToLogin(Const.ActionURIs.STUDENT_HOME);
		

		______TS("instructor pages");

		verifyRedirectToLogin(Const.ActionURIs.INSTRUCTOR_HOME);
		

		______TS("admin pages");

		verifyRedirectToLogin(Const.ActionURIs.ADMIN_HOME);
		
		
	}

	@Test
	public void testUserNotRegistered() throws Exception {
		
		restoreTypicalTestData();

		______TS("student pages");

		loginStudent(unregUsername, unregPassword);

		verifyRedirectToWelcomeStrangerPage(Const.ActionURIs.STUDENT_HOME, unregUsername);


		______TS("instructor pages");

		loginInstructorUnsuccessfully(unregUsername, unregPassword);

		Url url = new Url(Const.ActionURIs.INSTRUCTOR_HOME);
		verifyRedirectToNotAuthorized(url);
		verifyCannotMasquerade(url, otherInstructor.googleId);


		______TS("admin pages");
		
		//cannot access admin while logged in as student
		verifyCannotAccessAdminPages();
		

	}

	@Test
	public void testStudentAccessToAdminPages() throws Exception {
		restoreTypicalTestData();
		loginStudent(studentUsername, studentPassword);
		verifyCannotAccessAdminPages();
	}

	@Test
	public void testStudentHome() {
		
		restoreTypicalTestData();
		loginStudent(studentUsername, studentPassword);
		
		______TS("cannot view other homepage");
		
		link = Const.ActionURIs.STUDENT_HOME;
		verifyCannotMasquerade(link, otherInstructor.googleId);
	}



	@Test
	public void testStudentEvalSubmission() {
		
		restoreSpecialTestData();
		
		loginStudent(studentUsername, studentPassword);
		
		link = Const.ActionURIs.STUDENT_EVAL_SUBMISSION_EDIT;
		link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, ownCourse.id);
		EvaluationAttributes ownEvaluation = testData.evaluations.get("evaluation1InCourse1");
		link = Url.addParamToUrl(link, Const.ParamsNames.EVALUATION_NAME,	ownEvaluation.name);
		
		______TS("student cannot submit evaluation in AWAITING state");
	
		ownEvaluation.startTime = TimeHelper.getDateOffsetToCurrentTime(1);
		ownEvaluation.endTime = TimeHelper.getDateOffsetToCurrentTime(2);
		ownEvaluation.activated = false;
		assertEquals(EvalStatus.AWAITING, ownEvaluation.getStatus());
		backDoorOperationStatus = BackDoor.editEvaluation(ownEvaluation);
		assertEquals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS, backDoorOperationStatus);
		currentPage.navigateTo(new Url(link))
			.verifyStatus(Const.StatusMessages.EVALUATION_NOT_OPEN);
		assertEquals("true", currentPage.getElementAttribute(By.id(Const.ParamsNames.POINTS + "0"), "disabled"));
		assertEquals("true", currentPage.getElementAttribute(By.id(Const.ParamsNames.JUSTIFICATION + "0"), "disabled"));
		assertEquals("true", currentPage.getElementAttribute(By.id(Const.ParamsNames.COMMENTS + "0"), "disabled"));
		assertEquals("true", currentPage.getElementAttribute(By.id("button_submit"), "disabled"));
		
		______TS("student can view own evaluation submission page");
	
		link = Const.ActionURIs.STUDENT_EVAL_SUBMISSION_EDIT;
		link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, ownCourse.id);
		link = Url.addParamToUrl(link, Const.ParamsNames.EVALUATION_NAME,
				ownEvaluation.name);
		verifyPageContains(link, studentUsername
				+ "{*}Evaluation Submission{*}" + ownCourse.id + "{*}"
				+ ownEvaluation.name);
	
		______TS("student cannot submit evaluation in CLOSED state");
		
		ownEvaluation.startTime = TimeHelper.getDateOffsetToCurrentTime(-2);
		ownEvaluation.endTime = TimeHelper.getDateOffsetToCurrentTime(-1);
		backDoorOperationStatus = BackDoor.editEvaluation(ownEvaluation);
		assertEquals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS, backDoorOperationStatus);
		currentPage.navigateTo(new Url(link))
			.verifyStatus(Const.StatusMessages.EVALUATION_NOT_OPEN);
		assertEquals("true", currentPage.getElementAttribute(By.id(Const.ParamsNames.POINTS + "0"), "disabled"));
		assertEquals("true", currentPage.getElementAttribute(By.id(Const.ParamsNames.JUSTIFICATION + "0"), "disabled"));
		assertEquals("true", currentPage.getElementAttribute(By.id(Const.ParamsNames.COMMENTS + "0"), "disabled"));
		assertEquals("true", currentPage.getElementAttribute(By.id("button_submit"), "disabled"));
		
		______TS("student cannot submit evaluation in CLOSED state (evaluation with different timezone)");
		//Set the end time to the next hour, but push the timezone ahead 2 hours, so the evaluation has expired by 1 hour
		//Then we verify that the evaluation is disabled
		ownEvaluation.endTime = TimeHelper.getNextHour();
		ownEvaluation.timeZone = 10.0;   //put user's timezone ahead by 10hrs
		//TODO: this test case needs tweaking. It fails on some computers when
		//  the above is set to +2. Furthermore, we need a test case to ensure
		//  editing is enabled when the user timezone is behind. This test 
		//  case only checks if editing is disabled when timezone is ahead.
		backDoorOperationStatus = BackDoor.editEvaluation(ownEvaluation);
		assertEquals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS, backDoorOperationStatus);
		currentPage.navigateTo(new Url(link))
			.verifyStatus(Const.StatusMessages.EVALUATION_NOT_OPEN);
		assertEquals("true", currentPage.getElementAttribute(By.id(Const.ParamsNames.POINTS + "0"), "disabled"));
		assertEquals("true", currentPage.getElementAttribute(By.id(Const.ParamsNames.JUSTIFICATION + "0"), "disabled"));
		assertEquals("true", currentPage.getElementAttribute(By.id(Const.ParamsNames.COMMENTS + "0"), "disabled"));
		assertEquals("true", currentPage.getElementAttribute(By.id("button_submit"), "disabled"));
		
		______TS("student cannot submit evaluation in PUBLISHED state");
	
		ownEvaluation.endTime = TimeHelper.getDateOffsetToCurrentTime(-1);
		ownEvaluation.timeZone = 0.0;
		ownEvaluation.published = true;
		assertEquals(EvalStatus.PUBLISHED, ownEvaluation.getStatus());
		backDoorOperationStatus = BackDoor.editEvaluation(ownEvaluation);
		assertEquals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS, backDoorOperationStatus);
		currentPage.navigateTo(new Url(link))
			.verifyStatus(Const.StatusMessages.EVALUATION_NOT_OPEN);
		assertEquals("true", currentPage.getElementAttribute(By.id(Const.ParamsNames.POINTS + "0"), "disabled"));
		assertEquals("true", currentPage.getElementAttribute(By.id(Const.ParamsNames.JUSTIFICATION + "0"), "disabled"));
		assertEquals("true", currentPage.getElementAttribute(By.id(Const.ParamsNames.COMMENTS + "0"), "disabled"));
		assertEquals("true", currentPage.getElementAttribute(By.id("button_submit"), "disabled"));
		
		deleteSpecialTestData();
		
	}

	@Test
	public void testStudentEvalResult() {
		restoreSpecialTestData();
		loginStudent(studentUsername, studentPassword);
		______TS("student cannot view own evaluation result before publishing");
		
		ownEvaluation.published = false;
		assertTrue(EvalStatus.PUBLISHED != ownEvaluation.getStatus());
		backDoorOperationStatus = BackDoor.editEvaluation(ownEvaluation);
		assertEquals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS, backDoorOperationStatus);
	
		link = Const.ActionURIs.STUDENT_EVAL_RESULTS;
		link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, ownCourse.id);
		link = Url.addParamToUrl(link, Const.ParamsNames.EVALUATION_NAME,
				ownEvaluation.name);
		verifyRedirectedToServerErrorPage(link); //TODO: this error should be handled better.
	
		______TS("student can view own evaluation result after publishing");
	
		ownEvaluation.startTime = TimeHelper.getDateOffsetToCurrentTime(-2);
		ownEvaluation.endTime = TimeHelper.getDateOffsetToCurrentTime(-1);
		ownEvaluation.timeZone = 0.0;
		ownEvaluation.published = true;
		assertEquals(EvalStatus.PUBLISHED, ownEvaluation.getStatus());
		backDoorOperationStatus = BackDoor.editEvaluation(ownEvaluation);
		assertEquals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS, backDoorOperationStatus);
		verifyPageContains(link, studentUsername + "{*}Evaluation Results{*}"
				+ ownEvaluation.name + "{*}" + ownCourse.id);
		deleteSpecialTestData();
	}

	@Test
	public void testInstructorHome() {
	
		restoreSpecialTestData();
		
		loginInstructor(instructorUsername, instructorPassword);
	
		______TS("cannot view other homepage");
	
		link = Const.ActionURIs.INSTRUCTOR_HOME;
		verifyCannotMasquerade(link, otherInstructor.googleId);
		
		deleteSpecialTestData();
	
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

	private static void restoreTypicalTestData() {
		
		testData = getTypicalDataBundle();
		
		// This test suite requires some real accounts; Here, we inject them to the test data.
		testData.students.get("student1InCourse1").googleId = TestProperties.inst().TEST_STUDENT1_ACCOUNT;
		testData.instructors.get("instructor1OfCourse1").googleId = TestProperties.inst().TEST_INSTRUCTOR_ACCOUNT;
		
        String backDoorOperationStatus = BackDoor.restoreDataBundle(testData); 
        assertEquals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS, backDoorOperationStatus);
	}
	
	private static void restoreSpecialTestData() {
		
		testData = getTypicalDataBundle();
		
		// This test suite requires some real accounts; Here, we inject them to the test data.
		testData.students.get("student1InCourse1").googleId = TestProperties.inst().TEST_STUDENT1_ACCOUNT;
		testData.instructors.get("instructor1OfCourse1").googleId = TestProperties.inst().TEST_INSTRUCTOR_ACCOUNT;
		
        String backDoorOperationStatus = BackDoor.restoreDataBundle(testData); 
        assertEquals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS, backDoorOperationStatus);
	}

	private void verifyCannotAccessAdminPages() {
		//cannot access directly
		Url url = new Url(Const.ActionURIs.ADMIN_HOME);
		verifyRedirectToNotAuthorized(url);
		//cannot access by masquerading either
		url = url.withUserId(adminUsername);
		verifyRedirectToNotAuthorized(url);
	}

	private void verifyCannotMasquerade(String link, String otherInstructorId) {
		link = Url.addParamToUrl(link, Const.ParamsNames.USER_ID, otherInstructorId);
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

	private void verifyRedirectedToServerErrorPage(String url) {
		verifyPageContains(url, "There was an error in our server.");
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
		
		//delete any data related to real accounts used in testing (to prevent state leakage to other tests)
		deleteSpecialTestData();
		BrowserPool.release(browser);
	}

	private static void deleteSpecialTestData() {
		StudentAttributes student = testData.students.get("student1InCourse1");
		BackDoor.deleteStudent(student.course, student.email);
		BackDoor.deleteInstructor(TestProperties.inst().TEST_INSTRUCTOR_ACCOUNT);
		BackDoor.deleteCourse(student.course);
	}

}
