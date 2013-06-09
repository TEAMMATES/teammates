package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.Common;
import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.test.driver.BackDoor;
import teammates.test.driver.EmailAccount;
import teammates.test.driver.TestProperties;
import teammates.test.driver.Url;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.InstructorEvalsPage;

/**
 * Covers the 'Evaluations' page for instructors
 */
public class InstructorEvalsPageUiTest extends BaseUiTestCase {
	private static String jsonString;
	private static Browser b;
	private static InstructorEvalsPage evalsPage;
	private static DataBundle testData;
	
	@BeforeClass
	public static void classSetup() throws Exception {
		
		printTestClassHeader();
		
		jsonString = Common.readFile(Common.TEST_DATA_FOLDER+"/InstructorEvalUiTest.json");
		testData = Common.getTeammatesGson().fromJson(jsonString, DataBundle.class);
		BackDoor.deleteCourses(jsonString);
		BackDoor.deleteInstructors(jsonString);
		
		// Create fresh account relation
		AccountAttributes instructorAccount = testData.accounts.get("CEvalUiT.instructor");
		String backDoorOperationStatus = BackDoor.createAccount(instructorAccount);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, backDoorOperationStatus);

		b = BrowserPool.getBrowser();
		
		Url evalPageLink = new Url(Common.PAGE_INSTRUCTOR_EVAL)
			.withUserId(instructorAccount.googleId);
		evalsPage = loginAdminToPageAsInstructor(b, evalPageLink, InstructorEvalsPage.class);
		
	}
	
	@Test
	public void testInstructorEvalPage() throws Exception{
		testContent();
		testAddAction();
		testPublishLink();
		testRemindLink();
		testDeleteAction(); //TODO: move near to testAddAction();
		//TODO: what about edit links?
	}

	public void testContent() throws Exception{
		
		______TS("no courses");
		
		evalsPage.verifyHtml("/instructorEvalEmptyAll.html");
		
		______TS("no evaluations");
		
		BackDoor.createCourse(testData.courses.get("course"));
		BackDoor.createCourse(testData.courses.get("anotherCourse"));
		BackDoor.createInstructor(testData.instructors.get("teammates.test.instructor"));
		BackDoor.createInstructor(testData.instructors.get("teammates.test.anotherInstructor"));
		BackDoor.createStudent(testData.students.get("alice.tmms@CEvalUiT.CS2104"));
		BackDoor.createStudent(testData.students.get("benny.tmms@CEvalUiT.CS2104"));
		BackDoor.createStudent(testData.students.get("charlie.tmms@CEvalUiT.CS1101"));
		BackDoor.createStudent(testData.students.get("danny.tmms@CEvalUiT.CS1101"));
		evalsPage.loadEvaluationsTab();
		
		evalsPage.verifyHtml("/instructorEvalEmptyEval.html");
		
		______TS("typical view, sort by deadline (default)");
		
		BackDoor.createEvaluation(testData.evaluations.get("openEval"));
		BackDoor.createEvaluation(testData.evaluations.get("publishedEval"));
		BackDoor.createEvaluation(testData.evaluations.get("closedEval"));
		evalsPage.loadEvaluationsTab();
		
		evalsPage.verifyHtml("/instructorEvalByDeadline.html");

		______TS("sort by name");
		
		evalsPage.sortByName()
			.verifyTablePattern(1,"{*}First Eval{*}Second Eval{*}Third Eval");
		evalsPage.verifyHtml("/instructorEvalByName.html");
		
		evalsPage.sortByName()
			.verifyTablePattern( 1,"{*}Third Eval{*}Second Eval{*}First Eval");
		
		______TS("sort by course id");
		
		evalsPage.sortById()
		.verifyTablePattern(0,"{*}CEvalUiT.CS1101{*}CEvalUiT.CS2104{*}CEvalUiT.CS2104");
		evalsPage.verifyHtml("/instructorEvalById.html");
		
		evalsPage.sortById()
			.verifyTablePattern(0,"{*}CEvalUiT.CS2104{*}CEvalUiT.CS2104{*}CEvalUiT.CS1101");
	
	}

	public void testAddAction() throws Exception{
		
		
		______TS("typical success case");
		
		EvaluationAttributes eval = testData.evaluations.get("awaitingEval");
		evalsPage.addEvaluation(eval.courseId, eval.name, eval.startTime, eval.endTime, eval.p2pEnabled, eval.instructions, eval.gracePeriod);
		evalsPage.verifyStatus(Common.MESSAGE_EVALUATION_ADDED);
		evalsPage.verifyHtml("/instructorEvalAddSuccess.html");

		______TS("client-side input validation");
		
		// Empty name, closing date
		evalsPage.clickSubmitButton();
		assertEquals(Common.MESSAGE_FIELDS_EMPTY, evalsPage.getStatus());
		
		//TODO: The client-side validation tests below should be covered in JS tests, not as UI tests
		
		// Empty closing date
		evalsPage.fillEvalName("Some value");
		evalsPage.clickSubmitButton();
		assertEquals(Common.MESSAGE_FIELDS_EMPTY, evalsPage.getStatus());
		
		// Empty name
		evalsPage.addEvaluation(eval.courseId, "", eval.startTime, eval.endTime, eval.p2pEnabled, eval.instructions, eval.gracePeriod);
		assertEquals(Common.MESSAGE_FIELDS_EMPTY, evalsPage.getStatus());
		
		// Empty instructions
		evalsPage.addEvaluation(eval.courseId, eval.name, eval.startTime, eval.endTime, eval.p2pEnabled, "", eval.gracePeriod);
		assertEquals(Common.MESSAGE_FIELDS_EMPTY, evalsPage.getStatus());

		// Invalid name
		evalsPage.addEvaluation(eval.courseId, eval.name+"!@#$%^&*()_+", eval.startTime, eval.endTime, eval.p2pEnabled, eval.instructions, eval.gracePeriod);
		assertEquals(Common.MESSAGE_EVALUATION_NAMEINVALID, evalsPage.getStatus());
		
		// Invalid schedule
		evalsPage.addEvaluation(eval.courseId, eval.name, eval.endTime, eval.startTime, eval.p2pEnabled, eval.instructions, eval.gracePeriod);
		assertEquals(Common.MESSAGE_EVALUATION_SCHEDULEINVALID.replace("<br />", "\n"), evalsPage.getStatus());

		______TS("duplicate evalution name");

		evalsPage.addEvaluation(eval.courseId, eval.name, eval.startTime, eval.endTime, eval.p2pEnabled, eval.instructions, eval.gracePeriod);
		assertEquals(Common.MESSAGE_EVALUATION_EXISTS, evalsPage.getStatus());
	}

	
	public void testDeleteAction() throws Exception{
		
		String courseId = testData.evaluations.get("awaitingEval").courseId;
		String evalName = testData.evaluations.get("awaitingEval").name;
		
		evalsPage.loadEvaluationsTab(); //refresh the page
		evalsPage.clickAndCancel(evalsPage.getDeleteLink(courseId, evalName));
		assertNotNull(null, BackDoor.getEvaluation(courseId, evalName));
	
		evalsPage.clickAndConfirm(evalsPage.getDeleteLink(courseId, evalName));
		evalsPage.verifyHtml("/instructorEvalDeleteSuccessful.html");
		
	}

	public void testPublishLink(){
		
		evalsPage.loadEvaluationsTab(); //refresh the page
		
		______TS("CLOSED: publish link clickable");

		String courseId = testData.evaluations.get("closedEval").courseId;
		String evalName = testData.evaluations.get("closedEval").name;
		
		evalsPage.clickAndCancel(evalsPage.getPublishLink(courseId, evalName));
		assertEquals(false, BackDoor.getEvaluation(courseId, evalName).published);
		
		evalsPage.clickAndConfirm(evalsPage.getPublishLink(courseId, evalName));
		evalsPage.verifyStatus(Common.MESSAGE_EVALUATION_PUBLISHED);
		assertEquals(true, BackDoor.getEvaluation(courseId, evalName).published);
		
		
		______TS("PUBLISHED: unpublish link clickable");
		
		courseId = testData.evaluations.get("publishedEval").courseId;
		evalName = testData.evaluations.get("publishedEval").name;
		
		evalsPage.clickAndCancel(evalsPage.getUnpublishLink(courseId, evalName));
		assertEquals(true, BackDoor.getEvaluation(courseId, evalName).published);
		
		evalsPage.clickAndConfirm(evalsPage.getUnpublishLink(courseId, evalName));
		assertEquals(Common.MESSAGE_EVALUATION_UNPUBLISHED, evalsPage.getStatus());
		assertEquals(false, BackDoor.getEvaluation(courseId, evalName).published);
		
	}
	
	public void testRemindLink() throws Exception{
		
		evalsPage.loadEvaluationsTab();
		
		______TS("CLOSED: remind link unclickable");
		
		String courseId = testData.evaluations.get("closedEval").courseId;
		String evalName = testData.evaluations.get("closedEval").name;
		
		evalsPage.verifyUnclickable(evalsPage.getRemindLink(courseId, evalName));
		

		______TS("PUBLISHED: remind link unclickable");
		
		courseId = testData.evaluations.get("publishedEval").courseId;
		evalName = testData.evaluations.get("publishedEval").name;
		
		evalsPage.verifyUnclickable(evalsPage.getRemindLink(courseId, evalName));
		
		______TS("AWAITING: remind link unclickable");
		
		courseId = testData.evaluations.get("awaitingEval").courseId;
		evalName = testData.evaluations.get("awaitingEval").name;
		
		evalsPage.verifyUnclickable(evalsPage.getRemindLink(courseId, evalName));
		
		______TS("OPEN: remind link clickable");

		courseId = testData.evaluations.get("openEval").courseId;
		evalName = testData.evaluations.get("openEval").name;
		
		evalsPage.loadEvaluationsTab(); //refresh the page
		evalsPage.clickAndCancel(evalsPage.getRemindLink(courseId, evalName));
		
		evalsPage.clickAndConfirm(evalsPage.getRemindLink(courseId, evalName));
		
		if(!TestProperties.inst().isDevServer()) {
			waitFor(5000); //wait for the emails to reach the mail box
			assertEquals(courseId,
					EmailAccount.getEvaluationReminderFromGmail(
							testData.students.get("alice.tmms@CEvalUiT.CS2104").email, 
							TestProperties.inst().TEAMMATES_COMMON_PASSWORD_FOR_STUDENT_ACCOUNTS, 
							courseId, 
							evalName));
		}
	
	}
	
	@AfterClass
	public static void classTearDown() throws Exception {
		BrowserPool.release(b);
		BackDoor.deleteCourses(jsonString);
	}

}