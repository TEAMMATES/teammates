package teammates.testing.testcases;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;

import teammates.api.Common;
import teammates.datatransfer.CoordData;
import teammates.datatransfer.CourseData;
import teammates.datatransfer.DataBundle;
import teammates.datatransfer.EvaluationData;
import teammates.datatransfer.StudentData;
import teammates.jsp.Helper;
import teammates.testing.config.Config;
import teammates.testing.lib.BackDoor;
import teammates.testing.lib.BrowserInstance;
import teammates.testing.lib.BrowserInstancePool;
import teammates.testing.testcases.BaseTestCase;

public class AccessControlUiTest extends BaseTestCase {

	private static BrowserInstance bi;
	private static String appUrl = Config.inst().TEAMMATES_URL;
	private static DataBundle dataBundle;
	private static String backDoorOperationStatus;
	private static String link;
	
	private static String coordUsername;
	private static String coordPassword;

	private static CoordData otherCoord;

	private static CourseData ownCourse;
	private static CourseData otherCourse;

	private static StudentData ownStudent;
	private static StudentData otherStudent;

	private static EvaluationData ownEvaluation;
	private static EvaluationData otherEvaluation;
	
	static String  unregUsername = Config.inst().TEST_UNREG_ACCOUNT;
	static String  unregPassword = Config.inst().TEST_UNREG_PASSWORD;

	static String  adminUsername = Config.inst().TEST_ADMIN_ACCOUNT;
	
	@BeforeClass
	public static void classSetup() {
		printTestClassHeader();
		bi = BrowserInstancePool.getBrowserInstance();

		startRecordingTimeForDataImport();
		dataBundle = getTypicalDataBundle();

		// assign test user IDs to existing student and coord
		StudentData student = dataBundle.students.get("student1InCourse1");
		student.id = Config.inst().TEST_STUDENT_ACCOUNT;

		CoordData coord = dataBundle.coords.get("typicalCoord1");

		// remove existing data under the coord before changing his id
		BackDoor.deleteCoord(coord.id);

		// reassign courses to new coord id
		String idOfTestCoord = Config.inst().TEST_COORD_ACCOUNT;

		for (CourseData cd : dataBundle.courses.values()) {
			if (cd.coord.equals(coord.id)) {
				cd.coord = idOfTestCoord;
			}
		}

		coord.id = idOfTestCoord;

		backDoorOperationStatus = BackDoor.restoreNewDataBundle(Common
				.getTeammatesGson().toJson(dataBundle));
		assertEquals(Common.BACKEND_STATUS_SUCCESS, backDoorOperationStatus);
		reportTimeForDataImport();
		
		 coordUsername = Config.inst().TEST_COORD_ACCOUNT;
		 coordPassword = Config.inst().TEST_COORD_PASSWORD;

		 otherCoord = dataBundle.coords.get("typicalCoord2");

		 ownCourse = dataBundle.courses.get("course1OfCoord1");
		 otherCourse = dataBundle.courses.get("course1OfCoord2");

		 ownStudent = dataBundle.students.get("student1InCourse1");
		 otherStudent = dataBundle.students.get("student1InCourse2");

		 ownEvaluation = dataBundle.evaluations
				.get("evaluation1InCourse1OfCoord1");
		 otherEvaluation = dataBundle.evaluations
				.get("evaluation1InCourse1OfCoord2");

		bi.logout();
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

		______TS("coord pages");

		verifyRedirectToLogin(Common.PAGE_COORD_HOME);
		verifyRedirectToLogin(Common.PAGE_COORD_COURSE);
		verifyRedirectToLogin(Common.PAGE_COORD_COURSE_DELETE);
		verifyRedirectToLogin(Common.PAGE_COORD_COURSE_DETAILS);
		verifyRedirectToLogin(Common.PAGE_COORD_COURSE_ENROLL);
		verifyRedirectToLogin(Common.PAGE_COORD_COURSE_REMIND);
		verifyRedirectToLogin(Common.PAGE_COORD_COURSE_STUDENT_DELETE);
		verifyRedirectToLogin(Common.PAGE_COORD_COURSE_STUDENT_DETAILS);
		verifyRedirectToLogin(Common.PAGE_COORD_COURSE_STUDENT_EDIT);
		verifyRedirectToLogin(Common.PAGE_COORD_EVAL);
		verifyRedirectToLogin(Common.PAGE_COORD_EVAL_EDIT);
		verifyRedirectToLogin(Common.PAGE_COORD_EVAL_DELETE);
		verifyRedirectToLogin(Common.PAGE_COORD_EVAL_REMIND);
		verifyRedirectToLogin(Common.PAGE_COORD_EVAL_RESULTS);
		verifyRedirectToLogin(Common.PAGE_COORD_EVAL_PUBLISH);
		verifyRedirectToLogin(Common.PAGE_COORD_EVAL_UNPUBLISH);
		verifyRedirectToLogin(Common.PAGE_COORD_EVAL_SUBMISSION_VIEW);
		verifyRedirectToLogin(Common.PAGE_COORD_EVAL_SUBMISSION_EDIT);
		
		______TS("admin pages");
		
		link = Common.PAGE_ADMINISTRATOR_HOME;
		verifyRedirectToLogin(link);
		link = Helper.addParam(link, Common.PARAM_USER_ID, adminUsername);
		verifyRedirectToLogin(link);
	}

	@Test
	public void testUserNotRegistered() throws Exception {

		______TS("student");

		
		bi.logout();
		bi.loginStudent(unregUsername, unregPassword);

		verifyRedirectToWelcomeStrangerPage(Common.PAGE_STUDENT_HOME,
				unregUsername);
		verifyRedirectToWelcomeStrangerPage(Common.PAGE_STUDENT_JOIN_COURSE,
				unregUsername);

		verifyRedirectToWelcomeStrangerPage(Common.PAGE_STUDENT_COURSE_DETAILS, unregUsername);
		
		verifyRedirectToWelcomeStrangerPage(Common.PAGE_STUDENT_EVAL_SUBMISSION_EDIT, unregUsername);
		verifyRedirectToWelcomeStrangerPage(Common.PAGE_STUDENT_EVAL_RESULTS, unregUsername);

		______TS("coord");

		bi.logout();
		bi.loginCoord(unregUsername, unregPassword);
		
		CoordData otherCoord = dataBundle.coords.get("typicalCoord2");
		CourseData otherCourse = dataBundle.courses.get("course1OfCoord2");
		StudentData otherStudent = dataBundle.students.get("student1InCourse2");
		EvaluationData otherEvaluation = dataBundle.evaluations
				.get("evaluation1InCourse1OfCoord2");

		String link = Common.PAGE_COORD_HOME;
		verifyRedirectToNotFound(link);
		verifyCannotMasquerade(link, otherCoord.id);
		
		link = Common.PAGE_COORD_COURSE;
		verifyRedirectToNotFound(link);
		verifyCannotMasquerade(link, otherCoord.id);
		
		link = Common.PAGE_COORD_COURSE_DELETE;
		link = Helper.addParam(link, Common.PARAM_COURSE_ID, otherCourse.id);
		verifyRedirectToNotAuthorized(link);
		verifyCannotMasquerade(link, otherCoord.id);
		
		link = Common.PAGE_COORD_COURSE_DETAILS;
		link = Helper.addParam(link, Common.PARAM_COURSE_ID, otherCourse.id);
		verifyRedirectToNotAuthorized(link);
		verifyCannotMasquerade(link, otherCoord.id);
		
		link = Common.PAGE_COORD_COURSE_ENROLL;
		link = Helper.addParam(link, Common.PARAM_COURSE_ID, otherCourse.id);
		bi.goToUrl(link);
		bi.fillString(By.id("enrollstudents"), "t|n|e@g.com|c");
		bi.click(By.id("button_enroll"));
		verifyRedirectToNotAuthorized();
		link = Helper.addParam(link, Common.PARAM_USER_ID, otherCoord.id);
		bi.goToUrl(link);
		bi.fillString(By.id("enrollstudents"), "t|n|e@g.com|c");
		bi.click(By.id("button_enroll"));
		verifyRedirectToNotAuthorized();
		
		//remind whole course
		link = Common.PAGE_COORD_COURSE_REMIND;
		link = Helper.addParam(link, Common.PARAM_COURSE_ID, otherCourse.id);
		verifyRedirectToNotAuthorized(link);
		verifyCannotMasquerade(link, otherCoord.id);
		
		//remind one student
		link = Common.PAGE_COORD_COURSE_REMIND;
		link = Helper.addParam(link, Common.PARAM_COURSE_ID, otherCourse.id);
		link = Helper.addParam(link, Common.PARAM_STUDENT_EMAIL, otherStudent.email);
		verifyRedirectToNotAuthorized(link);
		verifyCannotMasquerade(link, otherCoord.id);
		
		link = Common.PAGE_COORD_COURSE_STUDENT_DELETE;
		link = Helper.addParam(link, Common.PARAM_COURSE_ID, otherCourse.id);
		link = Helper.addParam(link, Common.PARAM_STUDENT_EMAIL, otherStudent.email);
		verifyRedirectToNotAuthorized(link);
		verifyCannotMasquerade(link, otherCoord.id);
		
		link = Common.PAGE_COORD_COURSE_STUDENT_DETAILS;
		link = Helper.addParam(link, Common.PARAM_COURSE_ID, otherCourse.id);
		link = Helper.addParam(link, Common.PARAM_STUDENT_EMAIL, otherStudent.email);
		verifyRedirectToNotAuthorized(link);
		verifyCannotMasquerade(link, otherCoord.id);
		
		link = Common.PAGE_COORD_COURSE_STUDENT_EDIT;
		link = Helper.addParam(link, Common.PARAM_COURSE_ID, otherCourse.id);
		link = Helper.addParam(link, Common.PARAM_STUDENT_EMAIL, otherStudent.email);
		verifyRedirectToNotAuthorized(link);
		verifyCannotMasquerade(link, otherCoord.id);
		
		link = Common.PAGE_COORD_EVAL;
		verifyRedirectToNotFound(link);
		verifyCannotMasquerade(link, otherCoord.id);
		
		link = Common.PAGE_COORD_EVAL_EDIT;
		link = Helper.addParam(link, Common.PARAM_COURSE_ID, otherCourse.id);
		link = Helper.addParam(link, Common.PARAM_EVALUATION_NAME,
				otherEvaluation.name);
		verifyRedirectToNotAuthorized();
		verifyCannotMasquerade(link, otherCoord.id);
		
		link = Common.PAGE_COORD_EVAL_DELETE;
		link = Helper.addParam(link, Common.PARAM_COURSE_ID, otherCourse.id);
		link = Helper.addParam(link, Common.PARAM_EVALUATION_NAME, otherEvaluation.name);
		verifyRedirectToNotAuthorized(link);
		verifyCannotMasquerade(link, otherCoord.id);
		
		link = Common.PAGE_COORD_EVAL_REMIND;
		link = Helper.addParam(link, Common.PARAM_COURSE_ID, otherCourse.id);
		link = Helper.addParam(link, Common.PARAM_EVALUATION_NAME, otherEvaluation.name);
		verifyRedirectToNotAuthorized(link);
		verifyCannotMasquerade(link, otherCoord.id);
		
		link = Common.PAGE_COORD_EVAL_RESULTS;
		link = Helper.addParam(link, Common.PARAM_COURSE_ID, otherCourse.id);
		link = Helper.addParam(link, Common.PARAM_EVALUATION_NAME, otherEvaluation.name);
		verifyRedirectToNotAuthorized(link);
		verifyCannotMasquerade(link, otherCoord.id);
		
		link = Common.PAGE_COORD_EVAL_PUBLISH;
		link = Helper.addParam(link, Common.PARAM_COURSE_ID, otherCourse.id);
		link = Helper.addParam(link, Common.PARAM_EVALUATION_NAME, otherEvaluation.name);
		verifyRedirectToNotAuthorized(link);
		verifyCannotMasquerade(link, otherCoord.id);
		
		link = Common.PAGE_COORD_EVAL_UNPUBLISH;
		link = Helper.addParam(link, Common.PARAM_COURSE_ID, otherCourse.id);
		link = Helper.addParam(link, Common.PARAM_EVALUATION_NAME, otherEvaluation.name);
		verifyRedirectToNotAuthorized(link);
		verifyCannotMasquerade(link, otherCoord.id);
		
		link = Common.PAGE_COORD_EVAL_SUBMISSION_VIEW;
		link = Helper.addParam(link, Common.PARAM_COURSE_ID, otherCourse.id);
		link = Helper.addParam(link, Common.PARAM_EVALUATION_NAME, otherEvaluation.name);
		link = Helper.addParam(link, Common.PARAM_STUDENT_EMAIL, otherStudent.email);
		verifyRedirectToNotAuthorized(link);
		verifyCannotMasquerade(link, otherCoord.id);
		
		link = Common.PAGE_COORD_EVAL_SUBMISSION_EDIT;
		link = Helper.addParam(link, Common.PARAM_COURSE_ID, otherCourse.id);
		link = Helper.addParam(link, Common.PARAM_EVALUATION_NAME, otherEvaluation.name);
		link = Helper.addParam(link, Common.PARAM_STUDENT_EMAIL, otherStudent.email);
		verifyRedirectToNotAuthorized(link);
		verifyCannotMasquerade(link, otherCoord.id);
		
		verify_admin_pages_redirect_to_unauthorized_access_page();
	}

	private void verify_admin_pages_redirect_to_unauthorized_access_page() {
		link = Common.PAGE_ADMINISTRATOR_HOME;
		verifyRedirectToNotAuthorized(link);
		link = Helper.addParam(link, Common.PARAM_USER_ID, adminUsername);
		verifyRedirectToNotAuthorized(link);
	}

	@Test
	public void testStudentAccessControl() throws Exception {

		String studentUsername = Config.inst().TEST_STUDENT_ACCOUNT;
		String studentPassword = Config.inst().TEST_STUDENT_PASSWORD;

		bi.loginStudent(studentUsername, studentPassword);

		verifyPageContains(Common.PAGE_STUDENT_HOME, studentUsername
				+ "{*}Student Home{*}View Team");
		verifyPageContains(Common.PAGE_STUDENT_JOIN_COURSE, studentUsername
				+ "{*}Student Home{*}View Team");
		
		// =================== view course ==============================

		______TS("student can view details of a student's own course");

		String link = Common.PAGE_STUDENT_COURSE_DETAILS;
		String idOfOwnCourse = "idOfCourse1OfCoord1";
		link = Helper.addParam(link, Common.PARAM_COURSE_ID, idOfOwnCourse);
		verifyPageContains(link, studentUsername + "{*}Team Details for "
				+ idOfOwnCourse);

		______TS("student cannot view details of a course she is not registered for");

		link = Common.PAGE_STUDENT_COURSE_DETAILS;
		CourseData otherCourse = dataBundle.courses.get("course1OfCoord2");
		link = Helper.addParam(link, Common.PARAM_COURSE_ID, otherCourse.id);
		verifyRedirectToNotAuthorized(link);

		______TS("student cannot view course details while masquerading as a student in that course");

		StudentData otherStudent = dataBundle.students.get("student1InCourse2");
		// ensure other student belong to other course
		assertEquals(otherStudent.course, otherCourse.id);
		link = Helper.addParam(link, Common.PARAM_USER_ID, otherStudent.id);
		verifyRedirectToNotAuthorized(link);
		
		// =================== evaluation submission =========================

		______TS("student can view own evaluation submission page");

		link = Common.PAGE_STUDENT_EVAL_SUBMISSION_EDIT;
		link = Helper.addParam(link, Common.PARAM_COURSE_ID, idOfOwnCourse);
		EvaluationData ownEvaluation = dataBundle.evaluations
				.get("evaluation1InCourse1OfCoord1");
		link = Helper.addParam(link, Common.PARAM_EVALUATION_NAME,
				ownEvaluation.name);
		verifyPageContains(link, studentUsername
				+ "{*}Evaluation Submission{*}" + idOfOwnCourse + "{*}"
				+ ownEvaluation.name);

		______TS("student cannot submit evaluation after closing");

		ownEvaluation.endTime = Common.getDateOffsetToCurrentTime(-1);
		String backDoorOperationStatus = BackDoor.editEvaluation(ownEvaluation);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, backDoorOperationStatus);
		bi.goToUrl(link);
		bi.click(By.id("button_submit"));
		verifyRedirectToNotAuthorized();
		
		// =================== view evaluation result==========================

		______TS("student cannot view own evaluation result before publishing");

		link = Common.PAGE_STUDENT_EVAL_RESULTS;
		link = Helper.addParam(link, Common.PARAM_COURSE_ID, idOfOwnCourse);
		link = Helper.addParam(link, Common.PARAM_EVALUATION_NAME,
				ownEvaluation.name);
		verifyRedirectToNotAuthorized(link);

		______TS("student can view own evaluation result after publishing");

		ownEvaluation.published = true;
		backDoorOperationStatus = BackDoor.editEvaluation(ownEvaluation);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, backDoorOperationStatus);
		verifyPageContains(link, studentUsername + "{*}Evaluation Results{*}"
				+ ownEvaluation.name + "{*}" + idOfOwnCourse);

	}
	

	@Test
	public void testCoordAccessControl() throws Exception {


		bi.loginCoord(coordUsername, coordPassword);
		
		// =================== view home page ==============================

		______TS("can view own homepage");

		String link = Common.PAGE_COORD_HOME;
		verifyPageContains(link, coordUsername + "{*}Coordinator Home{*}"
				+ ownCourse.id);

		______TS("cannot view other homepage");

		verifyCannotMasquerade(link, otherCoord.id);

		______TS("can view own course page");

		link = Common.PAGE_COORD_COURSE;
		verifyPageContains(link, coordUsername + "{*}Add New Course{*}"
				+ ownCourse.id);
		
		// =================== add course ==============================

		______TS("can add course");

		bi.addCourse("new-course", "New Course");
		assertContainsRegex(coordUsername + "{*}Add New Course{*}"
				+ Common.MESSAGE_COURSE_ADDED, bi.getCurrentPageSource());

		______TS("cannot add course while masquerading");

		verifyCannotMasquerade(link, otherCoord.id);
		
		// =================== view course ==============================

		______TS("can view own course details");

		link = Common.PAGE_COORD_COURSE_DETAILS;
		link = Helper.addParam(link, Common.PARAM_COURSE_ID, ownCourse.id);

		verifyPageContains(link, coordUsername + "{*}Course Details{*}"
				+ ownCourse.id);

		______TS("cannot view others course details");

		link = Common.PAGE_COORD_COURSE_DETAILS;
		link = Helper.addParam(link, Common.PARAM_COURSE_ID, otherCourse.id);
		verifyRedirectToNotAuthorized(link);

		______TS("cannot view others course details by masquerading");

		verifyCannotMasquerade(link, otherCoord.id);
		
		testCoordEnroll();
		
		// =================== remind to join ==============================

		______TS("can send reminders to own student");

		link = Common.PAGE_COORD_COURSE_REMIND;
		link = Helper.addParam(link, Common.PARAM_COURSE_ID, ownCourse.id);
		link = Helper.addParam(link, Common.PARAM_STUDENT_EMAIL,
				ownStudent.email);
		verifyPageContains(link, coordUsername + "{*}Course Details{*}"
				+ ownCourse.id);

		______TS("cannot send reminders to not-own student");

		link = Common.PAGE_COORD_COURSE_REMIND;
		link = Helper.addParam(link, Common.PARAM_COURSE_ID, otherCourse.id);
		link = Helper.addParam(link, Common.PARAM_STUDENT_EMAIL,
				otherStudent.email);
		verifyRedirectToNotAuthorized(link);

		______TS("cannot send reminders to not-own student by masquerading");

		verifyCannotMasquerade(link, otherCoord.id);

		______TS("can send reminders to own course");

		link = Common.PAGE_COORD_COURSE_REMIND;
		link = Helper.addParam(link, Common.PARAM_COURSE_ID, ownCourse.id);
		verifyPageContains(link, coordUsername + "{*}Course Details{*}"
				+ ownCourse.id);

		______TS("cannot send reminders to not-own course");

		link = Common.PAGE_COORD_COURSE_REMIND;
		link = Helper.addParam(link, Common.PARAM_COURSE_ID, otherCourse.id);
		verifyRedirectToNotAuthorized(link);

		______TS("cannot send reminders to not-own course by masquerading");

		verifyCannotMasquerade(link, otherCoord.id);

		// ============== view student details ==============================

		______TS("can view details of own student");

		link = Common.PAGE_COORD_COURSE_STUDENT_DETAILS;
		link = Helper.addParam(link, Common.PARAM_COURSE_ID, ownCourse.id);
		link = Helper.addParam(link, Common.PARAM_STUDENT_EMAIL,
				ownStudent.email);
		verifyPageContains(link, coordUsername + "{*}Student Details{*}"
				+ ownStudent.email);

		______TS("cannot view details of not-own student");

		link = Common.PAGE_COORD_COURSE_STUDENT_DETAILS;
		link = Helper.addParam(link, Common.PARAM_COURSE_ID, otherCourse.id);
		link = Helper.addParam(link, Common.PARAM_STUDENT_EMAIL,
				otherStudent.email);
		verifyRedirectToNotAuthorized(link);

		______TS("cannot view details of not-own student by masquerading");

		verifyCannotMasquerade(link, otherCoord.id);

		testCoordStudentEdit();

		// =================== view evals ==============================

		testCoordEval();

		testCoordEvalEdit();

		// =================== evaluation reminders ===========================

		______TS("can send reminders to own evaluation");

		link = Common.PAGE_COORD_EVAL_REMIND;
		link = Helper.addParam(link, Common.PARAM_COURSE_ID, ownCourse.id);
		link = Helper.addParam(link, Common.PARAM_EVALUATION_NAME,
				ownEvaluation.name);
		verifyPageContains(link, coordUsername + "{*}Add New Evaluation{*}"
				+ Common.MESSAGE_EVALUATION_REMINDERSSENT);

		______TS("cannot send reminders to not-own evaluation");

		link = Common.PAGE_COORD_EVAL_REMIND;
		link = Helper.addParam(link, Common.PARAM_COURSE_ID, otherCourse.id);
		link = Helper.addParam(link, Common.PARAM_EVALUATION_NAME,
				otherEvaluation.name);
		verifyRedirectToNotAuthorized(link);

		______TS("cannot send reminders to not-own course by masquerading");

		verifyCannotMasquerade(link, otherCoord.id);

		// =================== view evaluation result =======================

		______TS("can view result of own evaluation");

		link = Common.PAGE_COORD_EVAL_RESULTS;
		link = Helper.addParam(link, Common.PARAM_COURSE_ID, ownCourse.id);
		link = Helper.addParam(link, Common.PARAM_EVALUATION_NAME,
				ownEvaluation.name);
		verifyPageContains(link, coordUsername + "{*}Evaluation Result{*}"
				+ ownEvaluation.name);

		______TS("cannot view result of not-own evaluation");

		link = Common.PAGE_COORD_EVAL_RESULTS;
		link = Helper.addParam(link, Common.PARAM_COURSE_ID, otherCourse.id);
		link = Helper.addParam(link, Common.PARAM_EVALUATION_NAME,
				otherEvaluation.name);
		verifyRedirectToNotAuthorized(link);

		______TS("cannot view result of not-own course by masquerading");

		verifyCannotMasquerade(link, otherCoord.id);

		// =================== publish evaluation result =====================

		______TS("can publish result of own evaluation");

		link = Common.PAGE_COORD_EVAL_PUBLISH;
		link = Helper.addParam(link, Common.PARAM_COURSE_ID, ownCourse.id);
		link = Helper.addParam(link, Common.PARAM_EVALUATION_NAME,
				ownEvaluation.name);
		link = Helper.addParam(link, Common.PARAM_NEXT_URL,
				Common.PAGE_COORD_HOME);
		verifyPageContains(link, coordUsername + "{*}Coordinator Home{*}"
				+ Common.MESSAGE_EVALUATION_PUBLISHED);

		______TS("cannot publish result of not-own evaluation");

		link = Common.PAGE_COORD_EVAL_PUBLISH;
		link = Helper.addParam(link, Common.PARAM_COURSE_ID, otherCourse.id);
		link = Helper.addParam(link, Common.PARAM_EVALUATION_NAME,
				otherEvaluation.name);
		link = Helper.addParam(link, Common.PARAM_NEXT_URL,
				Common.PAGE_COORD_HOME);
		verifyRedirectToNotAuthorized(link);

		______TS("cannot publish result of not-own course by masquerading");

		verifyCannotMasquerade(link, otherCoord.id);

		// =================== publish evaluation result =====================

		______TS("can unpublish result of own evaluation");

		link = Common.PAGE_COORD_EVAL_UNPUBLISH;
		link = Helper.addParam(link, Common.PARAM_COURSE_ID, ownCourse.id);
		link = Helper.addParam(link, Common.PARAM_EVALUATION_NAME,
				ownEvaluation.name);
		link = Helper.addParam(link, Common.PARAM_NEXT_URL,
				Common.PAGE_COORD_HOME);
		verifyPageContains(link, coordUsername + "{*}Coordinator Home{*}"
				+ Common.MESSAGE_EVALUATION_UNPUBLISHED);

		______TS("cannot unpublish result of not-own evaluation");

		link = Common.PAGE_COORD_EVAL_UNPUBLISH;
		link = Helper.addParam(link, Common.PARAM_COURSE_ID, otherCourse.id);
		link = Helper.addParam(link, Common.PARAM_EVALUATION_NAME,
				otherEvaluation.name);
		link = Helper.addParam(link, Common.PARAM_NEXT_URL,
				Common.PAGE_COORD_HOME);
		verifyRedirectToNotAuthorized(link);

		______TS("cannot unpublish result of not-own course by masquerading");

		verifyCannotMasquerade(link, otherCoord.id);

		// =================== view submission =============================

		______TS("can view submission of own student");

		link = Common.PAGE_COORD_EVAL_SUBMISSION_VIEW;
		link = Helper.addParam(link, Common.PARAM_COURSE_ID, ownCourse.id);
		link = Helper.addParam(link, Common.PARAM_EVALUATION_NAME,
				ownEvaluation.name);
		link = Helper.addParam(link, Common.PARAM_STUDENT_EMAIL,
				ownStudent.email);
		verifyPageContains(link, coordUsername
				+ "{*}View Student's Evaluation{*}" + ownStudent.name);

		______TS("cannot view submission of  not-own evaluation");

		link = Common.PAGE_COORD_EVAL_SUBMISSION_VIEW;
		link = Helper.addParam(link, Common.PARAM_COURSE_ID, otherCourse.id);
		link = Helper.addParam(link, Common.PARAM_EVALUATION_NAME,
				otherEvaluation.name);
		link = Helper.addParam(link, Common.PARAM_STUDENT_EMAIL,
				otherStudent.email);
		verifyRedirectToNotAuthorized(link);

		______TS("cannot view submission of not-own course by masquerading");

		verifyCannotMasquerade(link, otherCoord.id);

		testCoordEvalSubmissionEdit();

		// =================== delete evaluation ==============================

		______TS("can delete own evaluation");

		link = Common.PAGE_COORD_EVAL_DELETE;
		link = Helper.addParam(link, Common.PARAM_COURSE_ID, ownCourse.id);
		link = Helper.addParam(link, Common.PARAM_EVALUATION_NAME,
				ownEvaluation.name);
		verifyPageContains(link, coordUsername + "{*}Add New Evaluation{*}"
				+ Common.MESSAGE_EVALUATION_DELETED);

		______TS("cannot delete not-own evaluation");

		
		link = Common.PAGE_COORD_EVAL_DELETE;
		link = Helper.addParam(link, Common.PARAM_COURSE_ID, otherCourse.id);
		link = Helper.addParam(link, Common.PARAM_EVALUATION_NAME,
				otherEvaluation.name);
		verifyRedirectToNotAuthorized(link);

		______TS("cannot delete not-own evaluation by masquerading");


		link = Helper.addParam(link, Common.PARAM_USER_ID, otherCoord.id);
		verifyRedirectToNotAuthorized(link);

		// =================== delete student ==============================

		______TS("can delete own student");

		link = Common.PAGE_COORD_COURSE_STUDENT_DELETE;
		link = Helper.addParam(link, Common.PARAM_COURSE_ID, ownCourse.id);
		link = Helper.addParam(link, Common.PARAM_STUDENT_EMAIL,
				ownStudent.email);
		verifyPageContains(link, coordUsername + "{*}Course Details{*}"
				+ Common.MESSAGE_STUDENT_DELETED);

		______TS("cannot delete of not-own student");

		link = Common.PAGE_COORD_COURSE_STUDENT_DELETE;
		link = Helper.addParam(link, Common.PARAM_COURSE_ID, otherCourse.id);
		link = Helper.addParam(link, Common.PARAM_STUDENT_EMAIL,
				otherStudent.email);
		verifyRedirectToNotAuthorized(link);

		______TS("cannot delete not-own student by masquerading");

		verifyCannotMasquerade(link, otherCoord.id);

		// =================== delete course ==============================

		______TS("can delete own course");

		link = Common.PAGE_COORD_COURSE_DELETE;
		link = Helper.addParam(link, Common.PARAM_COURSE_ID, ownCourse.id);
		verifyPageContains(link, coordUsername + "{*}Add New Course{*}"
				+ Common.MESSAGE_COURSE_DELETED);

		______TS("cannot delete not-own course");

		link = Common.PAGE_COORD_COURSE_DELETE;
		link = Helper.addParam(link, Common.PARAM_COURSE_ID, otherCourse.id);
		verifyRedirectToNotAuthorized(link);

		______TS("cannot delete not-own course by masquerading");

		verifyCannotMasquerade(link, otherCoord.id);
		
		______TS("admin pages");
		
		verify_admin_pages_redirect_to_unauthorized_access_page();
		
	}

	public void testCoordEval() {

		bi.loginCoord(coordUsername, coordPassword);
		
		______TS("can view own evals page");

		link = Common.PAGE_COORD_EVAL;
		verifyPageContains(link, coordUsername + "{*}Add New Evaluation{*}"
				+ ownCourse.id);

		______TS("can add eval");

		bi.addEvaluation(ownCourse.id, "new eval",
				Common.getDateOffsetToCurrentTime(1),
				Common.getDateOffsetToCurrentTime(2), true, "ins", 0);
		assertContainsRegex(coordUsername + "{*}Add New Evaluation{*}"
				+ Common.MESSAGE_EVALUATION_ADDED, bi.getCurrentPageSource());

		______TS("cannot view others eval page by masquerading");

		verifyCannotMasquerade(link, otherCoord.id);
	}

	private void testCoordEvalSubmissionEdit() {

		______TS("can edit submission of own student");

		link = Common.PAGE_COORD_EVAL_SUBMISSION_EDIT;
		link = Helper.addParam(link, Common.PARAM_COURSE_ID, ownCourse.id);
		link = Helper.addParam(link, Common.PARAM_EVALUATION_NAME,
				ownEvaluation.name);
		link = Helper.addParam(link, Common.PARAM_STUDENT_EMAIL,
				ownStudent.email);
		verifyPageContains(link, coordUsername + "{*}Edit Student's Submission{*}" + ownStudent.name);
		bi.click(By.id("button_submit"));
		//We check for this message because there is no parent window for
		//  the browser to switch back as done in normal user operation.
		assertContains("This browser window is expected to close automatically", bi.getCurrentPageSource());

		______TS("cannot edit submission of  not-own evaluation");

		link = Common.PAGE_COORD_EVAL_SUBMISSION_EDIT;
		link = Helper.addParam(link, Common.PARAM_COURSE_ID, otherCourse.id);
		link = Helper.addParam(link, Common.PARAM_EVALUATION_NAME,
				otherEvaluation.name);
		link = Helper.addParam(link, Common.PARAM_STUDENT_EMAIL,
				otherStudent.email);
		verifyRedirectToNotAuthorized(link);

		______TS("cannot edit submission of not-own course by masquerading");

		verifyCannotMasquerade(link, otherCoord.id);
	}

	private void testCoordEvalEdit() {
		String link;
		// =================== edit evaluation ==============================

		______TS("can edit own evaluation");

		link = Common.PAGE_COORD_EVAL_EDIT;
		link = Helper.addParam(link, Common.PARAM_COURSE_ID, ownCourse.id);
		link = Helper.addParam(link, Common.PARAM_EVALUATION_NAME,
				ownEvaluation.name);
		verifyPageContains(link, coordUsername + "{*}Edit Evaluation{*}"
				+ ownEvaluation.name);

		______TS("cannot edit other evaluation");

		// note: we allow loading of other's evaluation for editing because
		// it is too expensive to prevent. However, user cannot submit edits.

		link = Common.PAGE_COORD_EVAL_EDIT;
		link = Helper.addParam(link, Common.PARAM_COURSE_ID, otherCourse.id);
		link = Helper.addParam(link, Common.PARAM_EVALUATION_NAME,
				otherEvaluation.name);
		bi.goToUrl(link);
		bi.click(By.id("button_submit"));
		verifyRedirectToNotAuthorized();

		______TS("cannot edit other evaluation by masquerading");

		// note: see note in previous section.

		link = Helper.addParam(link, Common.PARAM_USER_ID, otherCoord.id);
		bi.goToUrl(link);
		bi.click(By.id("button_submit"));
		verifyRedirectToNotAuthorized();
	}

	private void testCoordStudentEdit() {
		String link;
		// =============== edit student details ==============================

		______TS("can edit details of own student");

		link = Common.PAGE_COORD_COURSE_STUDENT_EDIT;
		link = Helper.addParam(link, Common.PARAM_COURSE_ID, ownCourse.id);
		link = Helper.addParam(link, Common.PARAM_STUDENT_EMAIL,
				ownStudent.email);
		verifyPageContains(link, coordUsername + "{*}Edit Student Details{*}"
				+ ownStudent.email);
		bi.click(bi.coordCourseDetailsStudentEditSaveButton);
		assertContainsRegex(coordUsername + "{*}Course Details{*}"
				+ Common.MESSAGE_STUDENT_EDITED, bi.getCurrentPageSource());

		______TS("cannot edit details of not-own student");

		link = Common.PAGE_COORD_COURSE_STUDENT_EDIT;
		link = Helper.addParam(link, Common.PARAM_COURSE_ID, otherCourse.id);
		link = Helper.addParam(link, Common.PARAM_STUDENT_EMAIL,
				otherStudent.email);
		verifyRedirectToNotAuthorized(link);

		______TS("cannot edit details of not-own student by masquerading");

		verifyCannotMasquerade(link, otherCoord.id);
	}

	private void testCoordEnroll() {
		
		______TS("can view own ourse enroll page");

		link = Common.PAGE_COORD_COURSE_ENROLL;
		link = Helper.addParam(link, Common.PARAM_COURSE_ID, ownCourse.id);

		verifyPageContains(link, coordUsername + "{*}Enroll Students for{*}"
				+ ownCourse.id);

		______TS("cannot view others course enroll page");

		link = Common.PAGE_COORD_COURSE_ENROLL;
		link = Helper.addParam(link, Common.PARAM_COURSE_ID, otherCourse.id);
		bi.goToUrl(link);
		bi.fillString(By.id("enrollstudents"), "t|n|e@g.com|c");
		bi.click(By.id("button_enroll"));
		verifyRedirectToNotAuthorized();

		______TS("cannot view others course enroll page by masquerading");
		// note: we allow loading of other's course for enrolling because
		// it is too expensive to prevent. However, user cannot submit edits.

		link = Helper.addParam(link, Common.PARAM_USER_ID, otherCoord.id);
		bi.goToUrl(link);
		bi.fillString(By.id("enrollstudents"), "t|n|e@g.com|c");
		bi.click(By.id("button_enroll"));
		verifyRedirectToNotAuthorized();
	}

	private void verifyCannotMasquerade(String link, String otherCoordId) {
		link = Helper.addParam(link, Common.PARAM_USER_ID, otherCoordId);
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
		assertContains("You are not authorized to view this page.",
				bi.getCurrentPageSource());
	}

	private void verifyRedirectToNotAuthorized(String path) {
		printUrl(appUrl + path);
		bi.goToUrl(appUrl + path);
		verifyRedirectToNotAuthorized();
	}
	
	private void verifyRedirectToNotFound(String path) {
		printUrl(appUrl + path);
		bi.goToUrl(appUrl + path);
		assertContainsRegex("We could not locate what you were trying to access.", bi.getCurrentPageSource());
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
