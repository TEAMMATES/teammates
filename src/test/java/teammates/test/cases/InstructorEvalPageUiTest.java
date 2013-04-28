package teammates.test.cases;

import static org.testng.AssertJUnit.assertEquals;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.Assert;
import org.openqa.selenium.By;

import teammates.common.Common;
import teammates.common.datatransfer.InstructorData;
import teammates.common.datatransfer.CourseData;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.EvaluationData;
import teammates.test.driver.BackDoor;
import teammates.test.driver.BrowserInstance;
import teammates.test.driver.BrowserInstancePool;
import teammates.test.driver.EmailAccount;
import teammates.test.driver.NoAlertException;
import teammates.test.driver.TestProperties;

/**
 * Tests instructorEval.jsp from UI functionality and HTML test
 */
public class InstructorEvalPageUiTest extends BaseTestCase {
	private static BrowserInstance bi;
	private static DataBundle scn;
	
	private static String appUrl = TestProperties.inst().TEAMMATES_URL;
	private static String jsonString;
	
	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader();
		
		startRecordingTimeForDataImport();
		jsonString = Common.readFile(Common.TEST_DATA_FOLDER+"/InstructorEvalUiTest.json");
		scn = Common.getTeammatesGson().fromJson(jsonString, DataBundle.class);
		BackDoor.deleteCourses(jsonString);
		BackDoor.deleteInstructors(jsonString);
		
		// Create fresh account relation
		String backDoorOperationStatus = BackDoor.createAccount(scn.accounts.get("teammates.test"));
		assertEquals(Common.BACKEND_STATUS_SUCCESS, backDoorOperationStatus);
		reportTimeForDataImport();

		bi = BrowserInstancePool.getBrowserInstance();

		bi.loginAdmin(TestProperties.inst().TEST_ADMIN_ACCOUNT, TestProperties.inst().TEST_ADMIN_PASSWORD);
		String link = appUrl+Common.PAGE_INSTRUCTOR_EVAL;
		link = Common.addParamToUrl(link,Common.PARAM_USER_ID,scn.accounts.get("teammates.test").googleId);
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
	public void testInstructorEvalPage() throws Exception{
		testInstructorEvalHTML();
		testInstructorEvalUiPaths();
		testInstructorEvalLinks();
	}

	public void testInstructorEvalHTML() throws Exception{
		______TS("no courses");
		
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/instructorEvalEmptyAll.html");
		
		______TS("no evaluations");
		BackDoor.createCourse(scn.courses.get("course"));
		BackDoor.createCourse(scn.courses.get("anotherCourse"));
		BackDoor.createInstructor(scn.instructors.get("teammates.test.course"));
		BackDoor.createInstructor(scn.instructors.get("teammates.test.anotherCourse"));
		BackDoor.createStudent(scn.students.get("alice.tmms@CEvalUiT.CS2104"));
		BackDoor.createStudent(scn.students.get("benny.tmms@CEvalUiT.CS2104"));
		BackDoor.createStudent(scn.students.get("charlie.tmms@CEvalUiT.CS1101"));
		BackDoor.createStudent(scn.students.get("danny.tmms@CEvalUiT.CS1101"));
		bi.clickEvaluationTab();
		
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/instructorEvalEmptyEval.html");
		
		BackDoor.createEvaluation(scn.evaluations.get("openEval"));
		BackDoor.createEvaluation(scn.evaluations.get("publishedEval"));
		BackDoor.createEvaluation(scn.evaluations.get("closedEval"));
		bi.clickEvaluationTab();

		______TS("typical view, sort by deadline");
		
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/instructorEvalByDeadline.html");

		______TS("sort by name");
		
		bi.click(By.id("button_sortname"));
		
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/instructorEvalByName.html");
		
		bi.assertDataTablePattern(1,"{*}First Eval{*}Second Eval{*}Third Eval");
		bi.click(By.id("button_sortname"));
		bi.assertDataTablePattern(1,"{*}Third Eval{*}Second Eval{*}First Eval");
		
		______TS("sort by course id");
		
		bi.click(By.id("button_sortcourseid"));
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/instructorEvalById.html");
		
		bi.assertDataTablePattern(0,"{*}CEvalUiT.CS1101{*}CEvalUiT.CS2104{*}CEvalUiT.CS2104");
		bi.click(By.id("button_sortcourseid"));
		bi.assertDataTablePattern(0,"{*}CEvalUiT.CS2104{*}CEvalUiT.CS2104{*}CEvalUiT.CS1101");
	
		//set back to ascending
		bi.click(By.id("button_sortcourseid"));
	}

	public void testInstructorEvalUiPaths() throws Exception{
		
		
		______TS("typical success case");
		EvaluationData eval = scn.evaluations.get("awaitingEval");
		bi.addEvaluation(eval.course, eval.name, eval.startTime, eval.endTime, eval.p2pEnabled, eval.instructions, eval.gracePeriod);
		
		bi.waitForStatusMessage(Common.MESSAGE_EVALUATION_ADDED);
		String link = appUrl+Common.PAGE_INSTRUCTOR_EVAL;
		link = Common.addParamToUrl(link,Common.PARAM_USER_ID,scn.accounts.get("teammates.test").googleId);
		bi.verifyCurrentPageHTMLWithRetry(Common.TEST_PAGES_FOLDER+"/instructorEvalAddSuccess.html",link);

		______TS("client-side input validation");
		
		// Empty name, closing date
		bi.click(bi.addEvaluationButton);
		bi.waitForStatusMessage(Common.MESSAGE_FIELDS_EMPTY);
		
		// Empty closing date
		bi.fillString(bi.inputEvaluationName, "Some value");
		bi.click(bi.addEvaluationButton);
		bi.waitForStatusMessage(Common.MESSAGE_FIELDS_EMPTY);
		
		// Empty name
		bi.addEvaluation(eval.course, "", eval.startTime, eval.endTime, eval.p2pEnabled, eval.instructions, eval.gracePeriod);
		bi.waitForStatusMessage(Common.MESSAGE_FIELDS_EMPTY);
		
		// Empty instructions
		bi.addEvaluation(eval.course, eval.name, eval.startTime, eval.endTime, eval.p2pEnabled, "", eval.gracePeriod);
		bi.waitForStatusMessage(Common.MESSAGE_FIELDS_EMPTY);

		// Invalid name
		bi.addEvaluation(eval.course, eval.name+"!@#$%^&*()_+", eval.startTime, eval.endTime, eval.p2pEnabled, eval.instructions, eval.gracePeriod);
		bi.waitForStatusMessage(Common.MESSAGE_EVALUATION_NAMEINVALID);
		
		// Invalid schedule
		bi.addEvaluation(eval.course, eval.name, eval.endTime, eval.startTime, eval.p2pEnabled, eval.instructions, eval.gracePeriod);
		bi.waitForStatusMessage(Common.MESSAGE_EVALUATION_SCHEDULEINVALID.replace("<br />", "\n"));

		______TS("duplicate evalution name");
		// Evaluation exists
		bi.addEvaluation(eval.course, eval.name, eval.startTime, eval.endTime, eval.p2pEnabled, eval.instructions, eval.gracePeriod);
		bi.waitForStatusMessage(Common.MESSAGE_EVALUATION_EXISTS);
	}

	public void testInstructorEvalLinks() throws Exception{
		testInstructorEvalPublishLink();
		testInstructorEvalRemindLink();
		testInstructorEvalDeleteLink();
	}
	
	public void testInstructorEvalPublishLink(){
		

		______TS("CLOSED: publish link clickable");

		String courseID = scn.evaluations.get("closedEval").course;
		String evalName = scn.evaluations.get("closedEval").name;
		int evalRowID = bi.getEvaluationRowID(courseID, evalName);
		By publishLinkLocator = bi.getInstructorEvaluationPublishLinkLocator(evalRowID);
		try{
			bi.clickAndCancel(publishLinkLocator);
		} catch (NoAlertException e){
			Assert.fail("Publish link not clickable on closed evaluation");
		}
		try{
			bi.clickAndConfirm(publishLinkLocator);
			bi.waitForStatusMessage(Common.MESSAGE_EVALUATION_PUBLISHED);
			//TODO: check for email?
		} catch (NoAlertException e){
			Assert.fail("Publish link not clickable on closed evaluation");
		}
		
		______TS("PUBLISHED: unpublish link clickable");
		
		courseID = scn.evaluations.get("publishedEval").course;
		evalName = scn.evaluations.get("publishedEval").name;
		evalRowID = bi.getEvaluationRowID(courseID, evalName);
		publishLinkLocator = bi.getInstructorEvaluationUnpublishLinkLocator(evalRowID);
		try{
			bi.clickAndCancel(publishLinkLocator);
		} catch (NoAlertException e){
			Assert.fail("Unpublish link not clickable on published evaluation");
		}
		try{
			bi.clickAndConfirm(publishLinkLocator);
			bi.waitForStatusMessage(Common.MESSAGE_EVALUATION_UNPUBLISHED);
		} catch (NoAlertException e){
			Assert.fail("Unpublish link not clickable on published evaluation");
		}
	}
	
	public void testInstructorEvalRemindLink() throws Exception{
		
		
		______TS("CLOSED: remind link unclickable");
		
		String courseID = scn.evaluations.get("closedEval").course;
		String evalName = scn.evaluations.get("closedEval").name;
		int evalRowID = bi.getEvaluationRowID(courseID, evalName);
		By remindLinkLocator = bi.getInstructorEvaluationRemindLinkLocator(evalRowID);
		try{
			bi.clickAndCancel(remindLinkLocator);
			Assert.fail("Remind link clickable on closed evaluation");
		} catch (NoAlertException e){}

		______TS("PUBLISHED: remind link unclickable");
		
		courseID = scn.evaluations.get("publishedEval").course;
		evalName = scn.evaluations.get("publishedEval").name;
		evalRowID = bi.getEvaluationRowID(courseID, evalName);
		remindLinkLocator = bi.getInstructorEvaluationRemindLinkLocator(evalRowID);
		try{
			bi.clickAndCancel(remindLinkLocator);
			Assert.fail("Remind link clickable on published evaluation");
		} catch (NoAlertException e){}

		______TS("AWAITING: remind link unclickable");
		
		courseID = scn.evaluations.get("awaitingEval").course;
		evalName = scn.evaluations.get("awaitingEval").name;
		evalRowID = bi.getEvaluationRowID(courseID, evalName);
		remindLinkLocator = bi.getInstructorEvaluationRemindLinkLocator(evalRowID);
		try{
			bi.clickAndCancel(remindLinkLocator);
			Assert.fail("Remind link clickable on awaiting evaluation");
		} catch (NoAlertException e){}

		______TS("OPEN: remind link clickable, click and cancel");

		courseID = scn.evaluations.get("openEval").course;
		evalName = scn.evaluations.get("openEval").name;
		evalRowID = bi.getEvaluationRowID(courseID, evalName);
		remindLinkLocator = bi.getInstructorEvaluationRemindLinkLocator(evalRowID);
		try{
			bi.clickAndCancel(remindLinkLocator);
		} catch (NoAlertException e){
			Assert.fail("Remind link not clickable on OPEN evaluation, or it is clickable but no confirmation box");
		}
		
		______TS("OPEN: click and confirm");
		
		try{
			bi.clickAndConfirm(remindLinkLocator);
		} catch (NoAlertException e){
			Assert.fail("Remind link not clickable on OPEN evaluation, or it is clickable but no confirmation box");
		}

		// Check email
		if(TestProperties.inst().isLocalHost()) return;
		bi.waitForEmail();
		assertEquals(courseID,EmailAccount.getEvaluationReminderFromGmail(scn.students.get("alice.tmms@CEvalUiT.CS2104").email, TestProperties.inst().TEAMMATES_COMMON_PASSWORD_FOR_STUDENT_ACCOUNTS, courseID, evalName));
	}
	
	public void testInstructorEvalDeleteLink() throws Exception{
		
		
		______TS("click and cancel");
		
		String courseID = scn.evaluations.get("awaitingEval").course;
		String evalName = scn.evaluations.get("awaitingEval").name;
		int evalRowID = bi.getEvaluationRowID(courseID, evalName);
		By deleteLinkLocator = bi.getInstructorEvaluationDeleteLinkLocator(evalRowID);
		try{
			bi.clickAndCancel(deleteLinkLocator);
			String evaluation = BackDoor.getEvaluationAsJson(courseID, evalName);
			if(isNullJSON(evaluation)) Assert.fail("Evaluation was deleted when it's not supposed to be");
		} catch (NoAlertException e){
			Assert.fail("Delete link not clickable or it is clickable but no confirmation box");
		}

		______TS("click and confirm");
		try{
			bi.clickAndConfirm(deleteLinkLocator);
			// Regex test due to the date in the evaluation form
			bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/instructorEvalDeleteSuccessful.html");
		} catch (NoAlertException e){
			Assert.fail("Delete link not clickable or it is clickable but no confirmation box");
		}
	}

	@SuppressWarnings("unused")
	private class TestScenario{
		public InstructorData instructor;
		public CourseData course;
		public EvaluationData evaluation;
		public EvaluationData evaluationInCourseWithNoTeams;
	}
}