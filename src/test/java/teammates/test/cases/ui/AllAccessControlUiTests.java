package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import java.io.FileNotFoundException;

import org.openqa.selenium.By;
import org.openqa.selenium.internal.selenesedriver.GetPageSource;

import teammates.common.Common;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.EvaluationAttributes.EvalStatus;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.test.cases.BaseTestCase;
import teammates.test.driver.BackDoor;
import teammates.test.driver.BrowserInstance;
import teammates.test.driver.BrowserInstancePool;
import teammates.test.driver.TestProperties;

public class AllAccessControlUiTests extends BaseTestCase {
	
	private static String appUrl = TestProperties.inst().TEAMMATES_URL;

	private static String unregUsername = TestProperties.inst().TEST_UNREG_ACCOUNT;
	private static String unregPassword = TestProperties.inst().TEST_UNREG_PASSWORD;

	private static String studentUsername = TestProperties.inst().TEST_STUDENT_ACCOUNT;
	private static String studentPassword = TestProperties.inst().TEST_STUDENT_PASSWORD;
	
	private static String instructorUsername = TestProperties.inst().TEST_INSTRUCTOR_ACCOUNT;
	private static String instructorPassword = TestProperties.inst().TEST_INSTRUCTOR_PASSWORD;

	static String adminUsername = TestProperties.inst().TEST_ADMIN_ACCOUNT;

	private static BrowserInstance bi;
	private static DataBundle dataBundle;
	private static String backDoorOperationStatus;
	private static String link;

	private static InstructorAttributes otherInstructor;

	// both TEST_INSTRUCTOR and TEST_STUDENT are from this course
	private static CourseAttributes ownCourse;

	private static CourseAttributes otherCourse;

	private static StudentAttributes ownStudent;
	private static StudentAttributes otherStudent;

	private static EvaluationAttributes ownEvaluation;
	private static EvaluationAttributes otherEvaluation;
	
	@BeforeClass
	public static void classSetup() {

		printTestClassHeader();

		dataBundle = getTypicalDataBundle();
		
		otherInstructor = dataBundle.instructors.get("instructor1OfCourse2");

		ownCourse = dataBundle.courses.get("typicalCourse1");
		otherCourse = dataBundle.courses.get("typicalCourse2");

		ownStudent = dataBundle.students.get("student1InCourse1");
		otherStudent = dataBundle.students.get("student1InCourse2");

		ownEvaluation = dataBundle.evaluations.get("evaluation1InCourse1");
		otherEvaluation = dataBundle.evaluations.get("evaluation1InCourse2");

		bi = BrowserInstancePool.getBrowserInstance();
		bi.logout(); // in case already logged in
	}
	
	@BeforeMethod
	public void methodSetup(){
		//TODO: see if we can reduce the time cost by making this @BeforeGroup
		restoreTestData();
	}

	@AfterClass
	public static void classTearDown() throws Exception {
		BrowserInstancePool.release(bi);
		printTestClassFooter();
	}

	@Test
	public void testUserNotLoggedIn() throws Exception {

		bi.logout();
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER + "/login.html");

		______TS("student pages");

		verifyRedirectToLogin(Common.PAGE_STUDENT_HOME);
		verifyRedirectToLogin(Common.PAGE_STUDENT_JOIN_COURSE);
		verifyRedirectToLogin(Common.PAGE_STUDENT_COURSE_DETAILS);
		verifyRedirectToLogin(Common.PAGE_STUDENT_EVAL_SUBMISSION_EDIT);
		verifyRedirectToLogin(Common.PAGE_STUDENT_EVAL_RESULTS);

		______TS("instructor pages");

		verifyRedirectToLogin(Common.PAGE_INSTRUCTOR_HOME);
		verifyRedirectToLogin(Common.PAGE_INSTRUCTOR_COURSE);
		verifyRedirectToLogin(Common.PAGE_INSTRUCTOR_COURSE_ADD);
		verifyRedirectToLogin(Common.PAGE_INSTRUCTOR_COURSE_DELETE);
		verifyRedirectToLogin(Common.PAGE_INSTRUCTOR_COURSE_DETAILS);
		verifyRedirectToLogin(Common.PAGE_INSTRUCTOR_COURSE_EDIT);
		verifyRedirectToLogin(Common.PAGE_INSTRUCTOR_COURSE_EDIT_SAVE);
		verifyRedirectToLogin(Common.PAGE_INSTRUCTOR_COURSE_ENROLL);
		verifyRedirectToLogin(Common.PAGE_INSTRUCTOR_COURSE_ENROLL_SAVE);
		verifyRedirectToLogin(Common.PAGE_INSTRUCTOR_COURSE_REMIND);
		verifyRedirectToLogin(Common.PAGE_INSTRUCTOR_COURSE_STUDENT_DELETE);
		verifyRedirectToLogin(Common.PAGE_INSTRUCTOR_COURSE_STUDENT_DETAILS);
		verifyRedirectToLogin(Common.PAGE_INSTRUCTOR_COURSE_STUDENT_EDIT);
		verifyRedirectToLogin(Common.PAGE_INSTRUCTOR_EVAL);
		verifyRedirectToLogin(Common.PAGE_INSTRUCTOR_EVAL_EDIT);
		verifyRedirectToLogin(Common.PAGE_INSTRUCTOR_EVAL_DELETE);
		verifyRedirectToLogin(Common.PAGE_INSTRUCTOR_EVAL_REMIND);
		verifyRedirectToLogin(Common.PAGE_INSTRUCTOR_EVAL_RESULTS);
		verifyRedirectToLogin(Common.PAGE_INSTRUCTOR_EVAL_PUBLISH);
		verifyRedirectToLogin(Common.PAGE_INSTRUCTOR_EVAL_UNPUBLISH);
		verifyRedirectToLogin(Common.PAGE_INSTRUCTOR_EVAL_SUBMISSION_VIEW);
		verifyRedirectToLogin(Common.PAGE_INSTRUCTOR_EVAL_SUBMISSION_EDIT);

		______TS("admin pages");

		link = Common.PAGE_ADMIN_HOME;
		verifyRedirectToLogin(link);
		link = Common.addParamToUrl(link, Common.PARAM_USER_ID, adminUsername);
		verifyRedirectToLogin(link);
	}

	@Test
	public void testUserNotRegistered() throws Exception {

		______TS("student pages");

		bi.logout();
		bi.loginStudent(unregUsername, unregPassword);

		verifyRedirectToWelcomeStrangerPage(Common.PAGE_STUDENT_HOME,
				unregUsername);
		verifyRedirectToWelcomeStrangerPage(Common.PAGE_STUDENT_JOIN_COURSE,
				unregUsername);

		verifyRedirectToWelcomeStrangerPage(Common.PAGE_STUDENT_COURSE_DETAILS,
				unregUsername);

		verifyRedirectToWelcomeStrangerPage(
				Common.PAGE_STUDENT_EVAL_SUBMISSION_EDIT, unregUsername);
		verifyRedirectToWelcomeStrangerPage(Common.PAGE_STUDENT_EVAL_RESULTS,
				unregUsername);

		______TS("instructor pages");

		bi.logout();
		bi.loginInstructor(unregUsername, unregPassword);

		link = Common.PAGE_INSTRUCTOR_HOME;
		verifyRedirectToNotAuthorized(link);
		verifyCannotMasquerade(link, otherInstructor.googleId);

		link = Common.PAGE_INSTRUCTOR_COURSE;
		verifyRedirectToNotAuthorized(link);
		verifyCannotMasquerade(link, otherInstructor.googleId);
		
		link = Common.PAGE_INSTRUCTOR_COURSE_ADD;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, "id");
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_NAME, "name");
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_INSTRUCTOR_LIST, "g1|n1|e@b.com");
		verifyRedirectToNotAuthorized(link);
		verifyCannotMasquerade(link, otherInstructor.googleId);

		link = Common.PAGE_INSTRUCTOR_COURSE_DELETE;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, otherCourse.id);
		verifyRedirectToNotAuthorized(link);
		verifyCannotMasquerade(link, otherInstructor.googleId);

		link = Common.PAGE_INSTRUCTOR_COURSE_DETAILS;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, otherCourse.id);
		verifyRedirectToNotAuthorized(link);
		verifyCannotMasquerade(link, otherInstructor.googleId);
		
		link = Common.PAGE_INSTRUCTOR_COURSE_EDIT;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, otherCourse.id);
		verifyRedirectToNotAuthorized(link);
		verifyCannotMasquerade(link, otherInstructor.googleId);
		
		link = Common.PAGE_INSTRUCTOR_COURSE_EDIT_SAVE;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, otherCourse.id);
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_INSTRUCTOR_LIST, "g1|n1|e@b.com");
		verifyRedirectToNotAuthorized(link);
		verifyCannotMasquerade(link, otherInstructor.googleId);

		link = Common.PAGE_INSTRUCTOR_COURSE_ENROLL;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, otherCourse.id);
		bi.goToUrl(link);
		verifyRedirectToNotAuthorized();
		link = Common.addParamToUrl(link, Common.PARAM_USER_ID, otherInstructor.googleId);
		bi.goToUrl(link);
		verifyRedirectToNotAuthorized();
		
		link = Common.PAGE_INSTRUCTOR_COURSE_ENROLL_SAVE;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, otherCourse.id);
		link = Common.addParamToUrl(link, Common.PARAM_STUDENTS_ENROLLMENT_INFO, "t1|n1|e@b.com");
		bi.goToUrl(link);
		verifyRedirectToNotAuthorized();
		
		// remind whole course
		link = Common.PAGE_INSTRUCTOR_COURSE_REMIND;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, otherCourse.id);
		verifyRedirectToNotAuthorized(link);
		verifyCannotMasquerade(link, otherInstructor.googleId);

		// remind one student
		link = Common.PAGE_INSTRUCTOR_COURSE_REMIND;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, otherCourse.id);
		link = Common.addParamToUrl(link, Common.PARAM_STUDENT_EMAIL,
				otherStudent.email);
		verifyRedirectToNotAuthorized(link);
		verifyCannotMasquerade(link, otherInstructor.googleId);

		link = Common.PAGE_INSTRUCTOR_COURSE_STUDENT_DELETE;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, otherCourse.id);
		link = Common.addParamToUrl(link, Common.PARAM_STUDENT_EMAIL,
				otherStudent.email);
		verifyRedirectToNotAuthorized(link);
		verifyCannotMasquerade(link, otherInstructor.googleId);

		link = Common.PAGE_INSTRUCTOR_COURSE_STUDENT_DETAILS;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, otherCourse.id);
		link = Common.addParamToUrl(link, Common.PARAM_STUDENT_EMAIL,
				otherStudent.email);
		verifyRedirectToNotAuthorized(link);
		verifyCannotMasquerade(link, otherInstructor.googleId);

		link = Common.PAGE_INSTRUCTOR_COURSE_STUDENT_EDIT;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, otherCourse.id);
		link = Common.addParamToUrl(link, Common.PARAM_STUDENT_EMAIL,
				otherStudent.email);
		verifyRedirectToNotAuthorized(link);
		verifyCannotMasquerade(link, otherInstructor.googleId);

		link = Common.PAGE_INSTRUCTOR_EVAL;
		verifyRedirectToNotAuthorized(link);
		verifyCannotMasquerade(link, otherInstructor.googleId);

		link = Common.PAGE_INSTRUCTOR_EVAL_EDIT;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, otherCourse.id);
		link = Common.addParamToUrl(link, Common.PARAM_EVALUATION_NAME,
				otherEvaluation.name);
		verifyRedirectToNotAuthorized(link);
		verifyCannotMasquerade(link, otherInstructor.googleId);

		link = Common.PAGE_INSTRUCTOR_EVAL_DELETE;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, otherCourse.id);
		link = Common.addParamToUrl(link, Common.PARAM_EVALUATION_NAME,
				otherEvaluation.name);
		verifyRedirectToNotAuthorized(link);
		verifyCannotMasquerade(link, otherInstructor.googleId);

		link = Common.PAGE_INSTRUCTOR_EVAL_REMIND;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, otherCourse.id);
		link = Common.addParamToUrl(link, Common.PARAM_EVALUATION_NAME,
				otherEvaluation.name);
		verifyRedirectToNotAuthorized(link);
		verifyCannotMasquerade(link, otherInstructor.googleId);

		link = Common.PAGE_INSTRUCTOR_EVAL_RESULTS;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, otherCourse.id);
		link = Common.addParamToUrl(link, Common.PARAM_EVALUATION_NAME,
				otherEvaluation.name);
		verifyRedirectToNotAuthorized(link);
		verifyCannotMasquerade(link, otherInstructor.googleId);

		link = Common.PAGE_INSTRUCTOR_EVAL_PUBLISH;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, otherCourse.id);
		link = Common.addParamToUrl(link, Common.PARAM_EVALUATION_NAME,
				otherEvaluation.name);
		verifyRedirectToNotAuthorized(link);
		verifyCannotMasquerade(link, otherInstructor.googleId);

		link = Common.PAGE_INSTRUCTOR_EVAL_UNPUBLISH;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, otherCourse.id);
		link = Common.addParamToUrl(link, Common.PARAM_EVALUATION_NAME,
				otherEvaluation.name);
		verifyRedirectToNotAuthorized(link);
		verifyCannotMasquerade(link, otherInstructor.googleId);

		link = Common.PAGE_INSTRUCTOR_EVAL_SUBMISSION_VIEW;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, otherCourse.id);
		link = Common.addParamToUrl(link, Common.PARAM_EVALUATION_NAME,
				otherEvaluation.name);
		link = Common.addParamToUrl(link, Common.PARAM_STUDENT_EMAIL,
				otherStudent.email);
		verifyRedirectToNotAuthorized(link);
		verifyCannotMasquerade(link, otherInstructor.googleId);

		link = Common.PAGE_INSTRUCTOR_EVAL_SUBMISSION_EDIT;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, otherCourse.id);
		link = Common.addParamToUrl(link, Common.PARAM_EVALUATION_NAME,
				otherEvaluation.name);
		link = Common.addParamToUrl(link, Common.PARAM_STUDENT_EMAIL,
				otherStudent.email);
		verifyRedirectToNotAuthorized(link);
		verifyCannotMasquerade(link, otherInstructor.googleId);

		______TS("admin pages");
		
		//cannot access admin while logged in as student
		verifyCannotAccessAdminPages();
		
		//cannot access admin while logged in as student
		bi.logout();
		bi.loginStudent(unregUsername, unregPassword);
		verifyCannotAccessAdminPages();
	}

	@Test
	public void testStudentAccessToAdminPages() throws Exception {
		bi.loginStudent(studentUsername, studentPassword);
		verifyCannotAccessAdminPages();
	}

	@Test
	public void testStudentHome() {
		bi.loginStudent(studentUsername, studentPassword);
		verifyPageContains(Common.PAGE_STUDENT_HOME, studentUsername
				+ "{*}Student Home{*}View Team");
	}

	@Test
	public void testStudentJoinCourse() {
		bi.loginStudent(studentUsername, studentPassword);
		verifyPageContains(Common.PAGE_STUDENT_JOIN_COURSE, studentUsername
				+ "{*}Student Home{*}View Team");
	}

	@Test
	public void testStudentCourseDetails() {
		
		bi.loginStudent(studentUsername, studentPassword);
		
		______TS("student can view details of a student's own course");
		
		link = Common.PAGE_STUDENT_COURSE_DETAILS;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, ownCourse.id);
		verifyPageContains(link, studentUsername + "{*}Team Details for "
				+ ownCourse.id);
	
		______TS("student cannot view details of a course she is not registered for");
		
		link = Common.PAGE_STUDENT_COURSE_DETAILS;
		CourseAttributes otherCourse = dataBundle.courses.get("typicalCourse2");
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, otherCourse.id);
		verifyRedirectToNotAuthorized(link);
	
		______TS("student cannot view course details while masquerading as a student in that course");
	
		StudentAttributes otherStudent = dataBundle.students.get("student1InCourse2");
		// ensure other student belong to other course
		assertEquals(otherStudent.course, otherCourse.id);
		link = Common.addParamToUrl(link, Common.PARAM_USER_ID, otherStudent.googleId);
		verifyRedirectToNotAuthorized(link);
	}

	@Test
	public void testStudentEvalSubmission() {
		
		bi.loginStudent(studentUsername, studentPassword);
		
		link = Common.PAGE_STUDENT_EVAL_SUBMISSION_EDIT;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, ownCourse.id);
		EvaluationAttributes ownEvaluation = dataBundle.evaluations.get("evaluation1InCourse1");
		link = Common.addParamToUrl(link, Common.PARAM_EVALUATION_NAME,	ownEvaluation.name);
		
		______TS("student cannot submit evaluation in AWAITING state");
	
		ownEvaluation.startTime = Common.getDateOffsetToCurrentTime(1);
		ownEvaluation.endTime = Common.getDateOffsetToCurrentTime(2);
		ownEvaluation.activated = false;
		assertEquals(EvalStatus.AWAITING, ownEvaluation.getStatus());
		backDoorOperationStatus = BackDoor.editEvaluation(ownEvaluation);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, backDoorOperationStatus);
		bi.goToUrl(link);
		bi.waitForStatusMessage(Common.MESSAGE_EVALUATION_NOT_OPEN);
		assertEquals("true", bi.getElementAttribute(By.id(Common.PARAM_POINTS + "0"), "disabled"));
		assertEquals("true", bi.getElementAttribute(By.id(Common.PARAM_JUSTIFICATION + "0"), "disabled"));
		assertEquals("true", bi.getElementAttribute(By.id(Common.PARAM_COMMENTS + "0"), "disabled"));
		assertEquals("true", bi.getElementAttribute(bi.studentSubmitEvaluationButton, "disabled"));
		
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
		bi.goToUrl(link);
		bi.waitForStatusMessage(Common.MESSAGE_EVALUATION_NOT_OPEN);
		assertEquals("true", bi.getElementAttribute(By.id(Common.PARAM_POINTS + "0"), "disabled"));
		assertEquals("true", bi.getElementAttribute(By.id(Common.PARAM_JUSTIFICATION + "0"), "disabled"));
		assertEquals("true", bi.getElementAttribute(By.id(Common.PARAM_COMMENTS + "0"), "disabled"));
		assertEquals("true", bi.getElementAttribute(bi.studentSubmitEvaluationButton, "disabled"));
		
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
		bi.goToUrl(link);
		bi.waitForStatusMessage(Common.MESSAGE_EVALUATION_NOT_OPEN);
		assertEquals("true", bi.getElementAttribute(By.id(Common.PARAM_POINTS + "0"), "disabled"));
		assertEquals("true", bi.getElementAttribute(By.id(Common.PARAM_JUSTIFICATION + "0"), "disabled"));
		assertEquals("true", bi.getElementAttribute(By.id(Common.PARAM_COMMENTS + "0"), "disabled"));
		assertEquals("true", bi.getElementAttribute(bi.studentSubmitEvaluationButton, "disabled"));
		
		______TS("student cannot submit evaluation in PUBLISHED state");
	
		ownEvaluation.endTime = Common.getDateOffsetToCurrentTime(-1);
		ownEvaluation.timeZone = 0.0;
		ownEvaluation.published = true;
		assertEquals(EvalStatus.PUBLISHED, ownEvaluation.getStatus());
		backDoorOperationStatus = BackDoor.editEvaluation(ownEvaluation);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, backDoorOperationStatus);
		bi.goToUrl(link);
		bi.waitForStatusMessage(Common.MESSAGE_EVALUATION_NOT_OPEN);
		assertEquals("true", bi.getElementAttribute(By.id(Common.PARAM_POINTS + "0"), "disabled"));
		assertEquals("true", bi.getElementAttribute(By.id(Common.PARAM_JUSTIFICATION + "0"), "disabled"));
		assertEquals("true", bi.getElementAttribute(By.id(Common.PARAM_COMMENTS + "0"), "disabled"));
		assertEquals("true", bi.getElementAttribute(bi.studentSubmitEvaluationButton, "disabled"));
		
	}

	@Test
	public void testStudentEvalResult() {
		bi.loginStudent(studentUsername, studentPassword);
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
	public void runInstructorAccessControlTestsInOrder() throws Exception{
		testInstructorAccessToAdminPages();
		testInstructorHome();
		testInstructorCourseAdd();
		testInstructorCourseDetails();
		testMultipleInstructorForSameCourse();
		testInstructorEnroll();
		testInstructorStudentEdit();
		testInstructorCourseRemind();
		testInstructorCourseEdit();
		testInstructorCourseStudentDetails();
		testInstructorEval();
		testInstructorEvalEdit();
		testInstructorEvalRemind();
		testInstructorEvalResults();
		testInstructorEvalPublish();
		testInstructorEvalUnpublish();
		testInstructorEvalSubmissionView();
		testInstructorEvalSubmissionEdit();
		testInstructorEvalDelete();
		testInstructorCourseStudentDelete();
		testInstructorCourseDelete();
	}

	public void testInstructorAccessToAdminPages() throws Exception {
		
		bi.loginInstructor(instructorUsername, instructorPassword);
		
		verifyCannotAccessAdminPages();
	}

	public void testInstructorHome() {
	
		bi.loginInstructor(instructorUsername, instructorPassword);
	
		______TS("can view own homepage");
	
		link = Common.PAGE_INSTRUCTOR_HOME;
		verifyPageContains(link, instructorUsername + "{*}Instructor Home{*}"
				+ ownCourse.id);
	
		______TS("cannot view other homepage");
	
		link = Common.PAGE_INSTRUCTOR_HOME;
		verifyCannotMasquerade(link, otherInstructor.googleId);
	
	}

	public void testInstructorCourseAdd() {
	
		bi.loginInstructor(instructorUsername, instructorPassword);
	
		______TS("can view own course page");
	
		link = Common.PAGE_INSTRUCTOR_COURSE;
		verifyPageContains(link, instructorUsername + "{*}Add New Course{*}"
				+ ownCourse.id);
	
		______TS("can add course");
	
		link = Common.PAGE_INSTRUCTOR_COURSE;
		bi.goToUrl(link);
		bi.addCourse("new-course-tCCA", "New Course");
		assertContainsRegex(instructorUsername + "{*}Add New Course{*}"
				+ Common.MESSAGE_COURSE_ADDED, bi.getCurrentPageSource());
	
		______TS("cannot add course while masquerading");
	
		verifyCannotMasquerade(link, otherInstructor.googleId);
	}

	public void testInstructorCourseDetails() {
	
		bi.loginInstructor(instructorUsername, instructorPassword);
	
		______TS("can view own course details");
	
		link = Common.PAGE_INSTRUCTOR_COURSE_DETAILS;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, ownCourse.id);
	
		verifyPageContains(link, instructorUsername + "{*}Course Details{*}"
				+ ownCourse.id);
	
		______TS("cannot view others course details");
	
		link = Common.PAGE_INSTRUCTOR_COURSE_DETAILS;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, otherCourse.id);
		verifyRedirectToNotAuthorized(link);
		
		______TS("cannot view others course details by masquerading");
	
		verifyCannotMasquerade(link, otherInstructor.googleId);
	
	}

	public void testMultipleInstructorForSameCourse() {
		// TODO Auto-generated method stub
		// login as instructor1OfCourse1
		// go to course home page for Course 1
		
		// login as instructor2OfCourse1
		// go to course home page for Course 1
		
		// login as instructor1OfCourse2
		// go to course home page for Course 1 - fails (not instructor)
		
	}

	public void testInstructorEnroll() {
	
		bi.loginInstructor(instructorUsername, instructorPassword);
	
		______TS("can view own course enroll page");
	
		link = Common.PAGE_INSTRUCTOR_COURSE_ENROLL;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, ownCourse.id);
	
		verifyPageContains(link, instructorUsername + "{*}Enroll Students for{*}"
				+ ownCourse.id);
		bi.fillString(By.id("enrollstudents"), "t1|name1|email1@gmail.com");
		bi.click(By.id("button_enroll"));
		assertContains("Enrollment Results for " + ownCourse.id, bi.getCurrentPageSource());
	
		______TS("cannot view others course enroll page");
	
		link = Common.PAGE_INSTRUCTOR_COURSE_ENROLL;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, otherCourse.id);
		verifyRedirectToNotAuthorized(link);
		
		link = Common.PAGE_INSTRUCTOR_COURSE_ENROLL_SAVE;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, otherCourse.id);
		link = Common.addParamToUrl(link, Common.PARAM_STUDENTS_ENROLLMENT_INFO, "t1|name2|email2@gmail.com");
		verifyRedirectToNotAuthorized(link);
	
		______TS("cannot view others course enroll page by masquerading");
		
		link = Common.PAGE_INSTRUCTOR_COURSE_ENROLL;
		link = Common.addParamToUrl(link, Common.PARAM_USER_ID, otherInstructor.googleId);
		verifyRedirectToNotAuthorized(link);
		
		link = Common.PAGE_INSTRUCTOR_COURSE_ENROLL_SAVE;
		link = Common.addParamToUrl(link, Common.PARAM_USER_ID, otherInstructor.googleId);
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, otherCourse.id);
		link = Common.addParamToUrl(link, Common.PARAM_STUDENTS_ENROLLMENT_INFO, "t1|name2|email2@gmail.com");
		verifyRedirectToNotAuthorized(link);
	}

	public void testInstructorStudentEdit() {
	
		bi.loginInstructor(instructorUsername, instructorPassword);
	
		______TS("can edit details of own student");
	
		link = Common.PAGE_INSTRUCTOR_COURSE_STUDENT_EDIT;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, ownCourse.id);
		link = Common.addParamToUrl(link, Common.PARAM_STUDENT_EMAIL,
				ownStudent.email);
		verifyPageContains(link, instructorUsername + "{*}Edit Student Details{*}"
				+ ownStudent.email);
		bi.click(bi.instructorCourseDetailsStudentEditSaveButton);
		assertContainsRegex(instructorUsername + "{*}Course Details{*}"
				+ Common.MESSAGE_STUDENT_EDITED, bi.getCurrentPageSource());
		
		______TS("cannot edit details of not-own student");
	
		link = Common.PAGE_INSTRUCTOR_COURSE_STUDENT_EDIT;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, otherCourse.id);
		link = Common.addParamToUrl(link, Common.PARAM_STUDENT_EMAIL,
				otherStudent.email);
		verifyRedirectToNotAuthorized(link);
	
		______TS("cannot edit details of not-own student by masquerading");
	
		verifyCannotMasquerade(link, otherInstructor.googleId);
	}

	public void testInstructorCourseRemind() {
	
		bi.loginInstructor(instructorUsername, instructorPassword);
	
		______TS("can send reminders to own student");
	
		link = Common.PAGE_INSTRUCTOR_COURSE_REMIND;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, ownCourse.id);
		link = Common.addParamToUrl(link, Common.PARAM_STUDENT_EMAIL,
				ownStudent.email);
		verifyPageContains(link, instructorUsername + "{*}Course Details{*}"
				+ ownCourse.id);
	
		______TS("cannot send reminders to not-own student");
	
		link = Common.PAGE_INSTRUCTOR_COURSE_REMIND;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, otherCourse.id);
		link = Common.addParamToUrl(link, Common.PARAM_STUDENT_EMAIL,
				otherStudent.email);
		verifyRedirectToNotAuthorized(link);
	
		______TS("cannot send reminders to not-own student by masquerading");
	
		verifyCannotMasquerade(link, otherInstructor.googleId);
	
		______TS("can send reminders to own course");
	
		link = Common.PAGE_INSTRUCTOR_COURSE_REMIND;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, ownCourse.id);
		verifyPageContains(link, instructorUsername + "{*}Course Details{*}"
				+ ownCourse.id);
	
		______TS("cannot send reminders to not-own course");
	
		link = Common.PAGE_INSTRUCTOR_COURSE_REMIND;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, otherCourse.id);
		verifyRedirectToNotAuthorized(link);
	
		______TS("cannot send reminders to not-own course by masquerading");
	
		verifyCannotMasquerade(link, otherInstructor.googleId);
	}

	public void testInstructorCourseEdit() {
		bi.loginInstructor(instructorUsername, instructorPassword);
		
		______TS("can edit own course details");
	
		link = Common.PAGE_INSTRUCTOR_COURSE_EDIT;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, ownCourse.id);
		
		bi.goToUrl(link);
		bi.clickAndConfirm(By.id("button_submit"));
		
		assertContains(
				"The course has been edited",
				bi.getCurrentPageSource());
	
		______TS("cannot edit other course details");
	
		link = Common.PAGE_INSTRUCTOR_COURSE_EDIT;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, otherCourse.id);
		
		bi.goToUrl(link);
		
		verifyRedirectToNotAuthorized();
	}

	public void testInstructorCourseStudentDetails() {
	
		bi.loginInstructor(instructorUsername, instructorPassword);
	
		______TS("can view details of own student");
	
		link = Common.PAGE_INSTRUCTOR_COURSE_STUDENT_DETAILS;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, ownCourse.id);
		link = Common.addParamToUrl(link, Common.PARAM_STUDENT_EMAIL,
				ownStudent.email);
		verifyPageContains(link, instructorUsername + "{*}Student Details{*}"
				+ ownStudent.email);
	
		______TS("cannot view details of not-own student");
	
		link = Common.PAGE_INSTRUCTOR_COURSE_STUDENT_DETAILS;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, otherCourse.id);
		link = Common.addParamToUrl(link, Common.PARAM_STUDENT_EMAIL,
				otherStudent.email);
		verifyRedirectToNotAuthorized(link);
	
		______TS("cannot view details of not-own student by masquerading");
	
		verifyCannotMasquerade(link, otherInstructor.googleId);
	
	}

	public void testInstructorEval() {
	
		bi.loginInstructor(instructorUsername, instructorPassword);
	
		______TS("can view own evals page");
	
		link = Common.PAGE_INSTRUCTOR_EVAL;
		verifyPageContains(link, instructorUsername + "{*}Add New Evaluation{*}"
				+ ownCourse.id);
		
		______TS("can add eval");
	
		link = Common.PAGE_INSTRUCTOR_EVAL;
		bi.goToUrl(link);
		bi.addEvaluation(ownCourse.id, "new eval",
				Common.getDateOffsetToCurrentTime(1),
				Common.getDateOffsetToCurrentTime(2), true, "ins", 0);
		//This test might fail on Firefox because Selenium doesn't select the right
		//  value from the the 'course id' combo box. In that case, run the test in chrome.
		assertContainsRegex(instructorUsername + "{*}Add New Evaluation{*}"
				+ Common.MESSAGE_EVALUATION_ADDED, bi.getCurrentPageSource());
	
		______TS("cannot view others eval page by masquerading");
	
		verifyCannotMasquerade(link, otherInstructor.googleId);
	}

	public void testInstructorEvalEdit() {
	
		bi.loginInstructor(instructorUsername, instructorPassword);
	
		______TS("can edit own evaluation");
	
		link = Common.PAGE_INSTRUCTOR_EVAL_EDIT;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, ownCourse.id);
		link = Common.addParamToUrl(link, Common.PARAM_EVALUATION_NAME,
				ownEvaluation.name);
		verifyPageContains(link, instructorUsername + "{*}Edit Evaluation{*}"
				+ ownEvaluation.name);
	
		______TS("cannot edit other evaluation");
	
		// note: we allow loading of other's evaluation for editing because
		// it is too expensive to prevent. However, user cannot submit edits.
	
		link = Common.PAGE_INSTRUCTOR_EVAL_EDIT;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, otherCourse.id);
		link = Common.addParamToUrl(link, Common.PARAM_EVALUATION_NAME,
				otherEvaluation.name);
		bi.goToUrl(link);
		bi.click(By.id("button_submit"));
		verifyRedirectToNotAuthorized();
	
		______TS("cannot edit other evaluation by masquerading");
	
		// note: see note in previous section.
	
		link = Common.addParamToUrl(link, Common.PARAM_USER_ID, otherInstructor.googleId);
		bi.goToUrl(link);
		bi.click(By.id("button_submit"));
		verifyRedirectToNotAuthorized();
	}

	public void testInstructorEvalRemind() {
	
		bi.loginInstructor(instructorUsername, instructorPassword);
	
		______TS("can send reminders to own evaluation");
	
		link = Common.PAGE_INSTRUCTOR_EVAL_REMIND;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, ownCourse.id);
		link = Common.addParamToUrl(link, Common.PARAM_EVALUATION_NAME,
				ownEvaluation.name);
		verifyPageContains(link, instructorUsername + "{*}Add New Evaluation{*}"
				+ Common.MESSAGE_EVALUATION_REMINDERSSENT);
	
		______TS("cannot send reminders to not-own evaluation");
	
		link = Common.PAGE_INSTRUCTOR_EVAL_REMIND;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, otherCourse.id);
		link = Common.addParamToUrl(link, Common.PARAM_EVALUATION_NAME,
				otherEvaluation.name);
		verifyRedirectToNotAuthorized(link);
	
		______TS("cannot send reminders to not-own course by masquerading");
	
		verifyCannotMasquerade(link, otherInstructor.googleId);
	}

	public void testInstructorEvalResults() {
	
		bi.loginInstructor(instructorUsername, instructorPassword);
	
		______TS("can view result of own evaluation");
	
		link = Common.PAGE_INSTRUCTOR_EVAL_RESULTS;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, ownCourse.id);
		link = Common.addParamToUrl(link, Common.PARAM_EVALUATION_NAME,
				ownEvaluation.name);
		verifyPageContains(link, instructorUsername + "{*}Evaluation Result{*}"
				+ ownEvaluation.name);
	
	
		______TS("cannot view result of not-own evaluation");
	
		link = Common.PAGE_INSTRUCTOR_EVAL_RESULTS;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, otherCourse.id);
		link = Common.addParamToUrl(link, Common.PARAM_EVALUATION_NAME,
				otherEvaluation.name);
		verifyRedirectToNotAuthorized(link);
	
		______TS("cannot view result of not-own course by masquerading");
	
		verifyCannotMasquerade(link, otherInstructor.googleId);
	}

	public void testInstructorEvalPublish() {
	
		bi.loginInstructor(instructorUsername, instructorPassword);
		
		______TS("can publish result of own evaluation");
		
		ownEvaluation.endTime = Common.getDateOffsetToCurrentTime(-1);
		ownEvaluation.published = false;
		assertEquals(EvalStatus.CLOSED, ownEvaluation.getStatus());
		backDoorOperationStatus = BackDoor.editEvaluation(ownEvaluation);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, backDoorOperationStatus);
	
		link = Common.PAGE_INSTRUCTOR_EVAL_PUBLISH;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, ownCourse.id);
		link = Common.addParamToUrl(link, Common.PARAM_EVALUATION_NAME,
				ownEvaluation.name);
		link = Common.addParamToUrl(link, Common.PARAM_NEXT_URL,
				Common.PAGE_INSTRUCTOR_HOME);
		verifyPageContains(link, instructorUsername + "{*}Instructor Home{*}"
				+ Common.MESSAGE_EVALUATION_PUBLISHED);
	
		______TS("cannot publish result of not-own evaluation");
		
		//TODO: can this pass for the wrong reasons? e.g., the eval is not publishable
	
		link = Common.PAGE_INSTRUCTOR_EVAL_PUBLISH;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, otherCourse.id);
		link = Common.addParamToUrl(link, Common.PARAM_EVALUATION_NAME,
				otherEvaluation.name);
		link = Common.addParamToUrl(link, Common.PARAM_NEXT_URL,
				Common.PAGE_INSTRUCTOR_HOME);
		verifyRedirectToNotAuthorized(link);
	
		______TS("cannot publish result of not-own course by masquerading");
	
		verifyCannotMasquerade(link, otherInstructor.googleId);
	}

	public void testInstructorEvalUnpublish() {
	
		bi.loginInstructor(instructorUsername, instructorPassword);
	
		______TS("can unpublish result of own evaluation");
		
		ownEvaluation.endTime = Common.getDateOffsetToCurrentTime(-1);
		ownEvaluation.published = true;
		assertEquals(EvalStatus.PUBLISHED, ownEvaluation.getStatus());
		backDoorOperationStatus = BackDoor.editEvaluation(ownEvaluation);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, backDoorOperationStatus);
		
		link = Common.PAGE_INSTRUCTOR_EVAL_UNPUBLISH;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, ownCourse.id);
		link = Common.addParamToUrl(link, Common.PARAM_EVALUATION_NAME,
				ownEvaluation.name);
		link = Common.addParamToUrl(link, Common.PARAM_NEXT_URL,
				Common.PAGE_INSTRUCTOR_HOME);
		verifyPageContains(link, instructorUsername + "{*}Instructor Home{*}"
				+ Common.MESSAGE_EVALUATION_UNPUBLISHED);
	
		______TS("cannot unpublish result of not-own evaluation");
	
		//TODO: can this case pass for wrong reasons? (e.g., the eval is not unpublishable)
		link = Common.PAGE_INSTRUCTOR_EVAL_UNPUBLISH;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, otherCourse.id);
		link = Common.addParamToUrl(link, Common.PARAM_EVALUATION_NAME,
				otherEvaluation.name);
		link = Common.addParamToUrl(link, Common.PARAM_NEXT_URL,
				Common.PAGE_INSTRUCTOR_HOME);
		verifyRedirectToNotAuthorized(link);
	
		______TS("cannot unpublish result of not-own course by masquerading");
	
		verifyCannotMasquerade(link, otherInstructor.googleId);
	}

	public void testInstructorEvalSubmissionView() {
	
		bi.loginInstructor(instructorUsername, instructorPassword);
	
		______TS("can view submission of own student");
	
		link = Common.PAGE_INSTRUCTOR_EVAL_SUBMISSION_VIEW;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, ownCourse.id);
		link = Common.addParamToUrl(link, Common.PARAM_EVALUATION_NAME,
				ownEvaluation.name);
		link = Common.addParamToUrl(link, Common.PARAM_STUDENT_EMAIL,
				ownStudent.email);
		verifyPageContains(link, instructorUsername
				+ "{*}View Student's Evaluation{*}" + ownStudent.name);
	
		______TS("cannot view submission of  not-own evaluation");
	
		link = Common.PAGE_INSTRUCTOR_EVAL_SUBMISSION_VIEW;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, otherCourse.id);
		link = Common.addParamToUrl(link, Common.PARAM_EVALUATION_NAME,
				otherEvaluation.name);
		link = Common.addParamToUrl(link, Common.PARAM_STUDENT_EMAIL,
				otherStudent.email);
		verifyRedirectToNotAuthorized(link);
	
		______TS("cannot view submission of not-own course by masquerading");
	
		verifyCannotMasquerade(link, otherInstructor.googleId);
	}

	public void testInstructorEvalSubmissionEdit() {
	
		bi.loginInstructor(instructorUsername, instructorPassword);
	
		______TS("can edit submission of own student");
	
		link = Common.PAGE_INSTRUCTOR_EVAL_SUBMISSION_EDIT;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, ownCourse.id);
		link = Common.addParamToUrl(link, Common.PARAM_EVALUATION_NAME,
				ownEvaluation.name);
		link = Common.addParamToUrl(link, Common.PARAM_STUDENT_EMAIL,
				ownStudent.email);
		verifyPageContains(link, instructorUsername
				+ "{*}Edit Student's Submission{*}" + ownStudent.name);
		bi.click(By.id("button_submit"));
		// We check for this message because there is no parent window for
		// the browser to switch back as done in normal user operation.
		assertContains(
				"This browser window is expected to close automatically",
				bi.getCurrentPageSource());
	
		______TS("cannot edit submission of  not-own evaluation");
	
		link = Common.PAGE_INSTRUCTOR_EVAL_SUBMISSION_EDIT;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, otherCourse.id);
		link = Common.addParamToUrl(link, Common.PARAM_EVALUATION_NAME,
				otherEvaluation.name);
		link = Common.addParamToUrl(link, Common.PARAM_STUDENT_EMAIL,
				otherStudent.email);
		verifyRedirectToNotAuthorized(link);
	
		______TS("cannot edit submission of not-own course by masquerading");
	
		verifyCannotMasquerade(link, otherInstructor.googleId);
	}

	public void testInstructorEvalDelete() {
	
		bi.loginInstructor(instructorUsername, instructorPassword);
	
		______TS("can delete own evaluation");
	
		link = Common.PAGE_INSTRUCTOR_EVAL_DELETE;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, ownCourse.id);
		link = Common.addParamToUrl(link, Common.PARAM_EVALUATION_NAME,
				ownEvaluation.name);
		verifyPageContains(link, instructorUsername + "{*}Add New Evaluation{*}"
				+ Common.MESSAGE_EVALUATION_DELETED);
	
		______TS("cannot delete not-own evaluation");
	
		link = Common.PAGE_INSTRUCTOR_EVAL_DELETE;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, otherCourse.id);
		link = Common.addParamToUrl(link, Common.PARAM_EVALUATION_NAME,
				otherEvaluation.name);
		verifyRedirectToNotAuthorized(link);
	
		______TS("cannot delete not-own evaluation by masquerading");
	
		verifyCannotMasquerade(link, otherInstructor.googleId);
	}

	public void testInstructorCourseStudentDelete() {
	
		bi.loginInstructor(instructorUsername, instructorPassword);
	
		______TS("can delete own student");
	
		link = Common.PAGE_INSTRUCTOR_COURSE_STUDENT_DELETE;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, ownCourse.id);
		link = Common.addParamToUrl(link, Common.PARAM_STUDENT_EMAIL,
				ownStudent.email);
		verifyPageContains(link, instructorUsername + "{*}Course Details{*}"
				+ Common.MESSAGE_STUDENT_DELETED);
	
		______TS("cannot delete of not-own student");
	
		link = Common.PAGE_INSTRUCTOR_COURSE_STUDENT_DELETE;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, otherCourse.id);
		link = Common.addParamToUrl(link, Common.PARAM_STUDENT_EMAIL,
				otherStudent.email);
		verifyRedirectToNotAuthorized(link);
	
		______TS("cannot delete not-own student by masquerading");
	
		verifyCannotMasquerade(link, otherInstructor.googleId);
	}

	public void testInstructorCourseDelete() {
	
		bi.loginInstructor(instructorUsername, instructorPassword);
	
		______TS("can delete own course");
	
		link = Common.PAGE_INSTRUCTOR_COURSE_DELETE;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, ownCourse.id);
		verifyPageContains(link, instructorUsername + "{*}Add New Course{*}"
				+ Common.MESSAGE_COURSE_DELETED);
	
		______TS("cannot delete not-own course");
	
		link = Common.PAGE_INSTRUCTOR_COURSE_DELETE;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, otherCourse.id);
		verifyRedirectToNotAuthorized(link);
	
		______TS("cannot delete not-own course by masquerading");
	
		verifyCannotMasquerade(link, otherInstructor.googleId);
	
	}

	private static void restoreTestData() {
		startRecordingTimeForDataImport();
		dataBundle = getTypicalDataBundle();
		
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		reportTimeForDataImport();
	}

	private void verifyCannotAccessAdminPages() {
		//cannot access directly
		link = Common.PAGE_ADMIN_HOME;
		verifyRedirectToNotAuthorized(link);
		//cannot access by masquerading either
		link = Common.addParamToUrl(link, Common.PARAM_USER_ID, adminUsername);
		verifyRedirectToNotAuthorized(link);
	}

	private void verifyCannotMasquerade(String link, String otherInstructorId) {
		link = Common.addParamToUrl(link, Common.PARAM_USER_ID, otherInstructorId);
		verifyRedirectToNotAuthorized(link);
	}

	private void verifyRedirectToWelcomeStrangerPage(String path,
			String unregUsername) {
		printUrl(appUrl + path);
		bi.goToUrl(appUrl + path);
		// A simple regex check is enough because we do full HTML tests
		// elsewhere
		assertContainsRegex("{*}" + unregUsername + "{*}Welcome stranger{*}",
				bi.getCurrentPageSource());
	}

	private void verifyRedirectToNotAuthorized() {
		String pageSource = bi.getCurrentPageSource();
		assertTrue(pageSource.contains("You are not authorized to view this page.")||
				pageSource.contains("Your client does not have permission"));
	}

	private void verifyRedirectToNotAuthorized(String path) {
		printUrl(appUrl + path);
		bi.goToUrl(appUrl + path);
		verifyRedirectToNotAuthorized();
	}

	private void verifyPageContains(String path, String targetText) {
		printUrl(appUrl + path);
		bi.goToUrl(appUrl + path);
		assertContainsRegex(targetText, bi.getCurrentPageSource());
	}

	private void verifyRedirectToLogin(String path) {
		printUrl(appUrl + path);
		bi.goToUrl(appUrl + path);
		assertTrue(bi.isLocalLoginPage() || bi.isGoogleLoginPage());
	}

	private void printUrl(String url) {
		print("   " + url);
	}

}
