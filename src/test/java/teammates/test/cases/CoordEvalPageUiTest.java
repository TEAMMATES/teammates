package teammates.test.cases;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;

import teammates.common.Common;
import teammates.common.datatransfer.CoordData;
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
 * Tests coordEval.jsp from UI functionality and HTML test
 */
public class CoordEvalPageUiTest extends BaseTestCase {
	private static BrowserInstance bi;
	private static DataBundle scn;
	
	private static String appUrl = TestProperties.inst().TEAMMATES_URL;
	
	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader();
		
		startRecordingTimeForDataImport();
		String jsonString = Common.readFile(Common.TEST_DATA_FOLDER+"/CoordEvalUiTest.json");
		scn = Common.getTeammatesGson().fromJson(jsonString, DataBundle.class);
		BackDoor.deleteCoordinators(jsonString);
		String backDoorOperationStatus = BackDoor.createCoord(scn.coords.get("teammates.test"));
		assertEquals(Common.BACKEND_STATUS_SUCCESS, backDoorOperationStatus);
		reportTimeForDataImport();

		bi = BrowserInstancePool.getBrowserInstance();

		bi.loginAdmin(TestProperties.inst().TEST_ADMIN_ACCOUNT, TestProperties.inst().TEST_ADMIN_PASSWORD);
		String link = appUrl+Common.PAGE_COORD_EVAL;
		link = Common.addParamToUrl(link,Common.PARAM_USER_ID,scn.coords.get("teammates.test").id);
		bi.goToUrl(link);
	}
	
	@AfterClass
	public static void classTearDown() throws Exception {
		BrowserInstancePool.release(bi);
		printTestClassFooter();
	}

	@Test
	public void testCoordEvalPage() throws Exception{
		testCoordEvalHTML();
		testCoordEvalUiPaths();
		testCoordEvalLinks();
	}

	public void testCoordEvalHTML() throws Exception{
		
		
		______TS("no courses");
		
		bi.verifyCurrentPageHTMLRegex(Common.TEST_PAGES_FOLDER+"/coordEvalEmptyAll.html");
		
		______TS("no evaluations");
		BackDoor.createCourse(scn.courses.get("course"));
		BackDoor.createCourse(scn.courses.get("anotherCourse"));
		BackDoor.createStudent(scn.students.get("alice.tmms@CEvalUiT.CS2104"));
		BackDoor.createStudent(scn.students.get("benny.tmms@CEvalUiT.CS2104"));
		BackDoor.createStudent(scn.students.get("charlie.tmms@CEvalUiT.CS1101"));
		BackDoor.createStudent(scn.students.get("danny.tmms@CEvalUiT.CS1101"));
		bi.clickEvaluationTab();
		
		bi.verifyCurrentPageHTMLRegex(Common.TEST_PAGES_FOLDER+"/coordEvalEmptyEval.html");
		
		BackDoor.createEvaluation(scn.evaluations.get("openEval"));
		BackDoor.createEvaluation(scn.evaluations.get("publishedEval"));
		BackDoor.createEvaluation(scn.evaluations.get("closedEval"));
		bi.clickEvaluationTab();

		______TS("typical view, sort by deadline");
		
		bi.verifyCurrentPageHTMLRegex(Common.TEST_PAGES_FOLDER+"/coordEvalByDeadline.html");

		______TS("sort by name");
		
		bi.click(By.id("button_sortname"));
		bi.verifyCurrentPageHTMLRegex(Common.TEST_PAGES_FOLDER+"/coordEvalByName.html");
		
		______TS("sort by course id");
		
		bi.click(By.id("button_sortcourseid"));
		bi.verifyCurrentPageHTMLRegex(Common.TEST_PAGES_FOLDER+"/coordEvalById.html");
	}

	public void testCoordEvalUiPaths() throws Exception{
		
		
		______TS("typical success case");
		EvaluationData eval = scn.evaluations.get("awaitingEval");
		bi.addEvaluation(eval.course, eval.name, eval.startTime, eval.endTime, eval.p2pEnabled, eval.instructions, eval.gracePeriod);
		
		bi.waitForStatusMessage(Common.MESSAGE_EVALUATION_ADDED);
		String link = appUrl+Common.PAGE_COORD_EVAL;
		link = Common.addParamToUrl(link,Common.PARAM_USER_ID,scn.coords.get("teammates.test").id);
		bi.verifyCurrentPageHTMLRegexWithRetry(Common.TEST_PAGES_FOLDER+"/coordEvalAddSuccess.html",link);

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

	public void testCoordEvalLinks() throws Exception{
		testCoordEvalPublishLink();
		testCoordEvalRemindLink();
		testCoordEvalDeleteLink();
	}
	
	public void testCoordEvalPublishLink(){
		

		______TS("CLOSED: publish link clickable");

		String courseID = scn.evaluations.get("closedEval").course;
		String evalName = scn.evaluations.get("closedEval").name;
		int evalRowID = bi.getEvaluationRowID(courseID, evalName);
		By publishLinkLocator = bi.getCoordEvaluationPublishLinkLocator(evalRowID);
		try{
			bi.clickAndCancel(publishLinkLocator);
		} catch (NoAlertException e){
			fail("Publish link not clickable on closed evaluation");
		}
		try{
			bi.clickAndConfirm(publishLinkLocator);
			bi.waitForStatusMessage(Common.MESSAGE_EVALUATION_PUBLISHED);
			//TODO: check for email?
		} catch (NoAlertException e){
			fail("Publish link not clickable on closed evaluation");
		}
		
		______TS("PUBLISHED: unpublish link clickable");
		
		courseID = scn.evaluations.get("publishedEval").course;
		evalName = scn.evaluations.get("publishedEval").name;
		evalRowID = bi.getEvaluationRowID(courseID, evalName);
		publishLinkLocator = bi.getCoordEvaluationUnpublishLinkLocator(evalRowID);
		try{
			bi.clickAndCancel(publishLinkLocator);
		} catch (NoAlertException e){
			fail("Unpublish link not clickable on published evaluation");
		}
		try{
			bi.clickAndConfirm(publishLinkLocator);
			bi.waitForStatusMessage(Common.MESSAGE_EVALUATION_UNPUBLISHED);
		} catch (NoAlertException e){
			fail("Unpublish link not clickable on published evaluation");
		}
	}
	
	public void testCoordEvalRemindLink() throws Exception{
		

		______TS("PUBLISHED: remind link unclickable");
		
		String courseID = scn.evaluations.get("publishedEval").course;
		String evalName = scn.evaluations.get("publishedEval").name;
		int evalRowID = bi.getEvaluationRowID(courseID, evalName);
		By remindLinkLocator = bi.getCoordEvaluationRemindLinkLocator(evalRowID);
		try{
			bi.clickAndCancel(remindLinkLocator);
			fail("Remind link clickable on published evaluation");
		} catch (NoAlertException e){}

		______TS("CLOSED: remind link unclickable");
		
		courseID = scn.evaluations.get("closedEval").course;
		evalName = scn.evaluations.get("closedEval").name;
		evalRowID = bi.getEvaluationRowID(courseID, evalName);
		remindLinkLocator = bi.getCoordEvaluationRemindLinkLocator(evalRowID);
		try{
			bi.clickAndCancel(remindLinkLocator);
			fail("Remind link clickable on closed evaluation");
		} catch (NoAlertException e){}

		______TS("AWAITING: remind link unclickable");
		
		courseID = scn.evaluations.get("awaitingEval").course;
		evalName = scn.evaluations.get("awaitingEval").name;
		evalRowID = bi.getEvaluationRowID(courseID, evalName);
		remindLinkLocator = bi.getCoordEvaluationRemindLinkLocator(evalRowID);
		try{
			bi.clickAndCancel(remindLinkLocator);
			fail("Remind link clickable on awaiting evaluation");
		} catch (NoAlertException e){}

		______TS("OPEN: remind link clickable, click and cancel");

		courseID = scn.evaluations.get("openEval").course;
		evalName = scn.evaluations.get("openEval").name;
		evalRowID = bi.getEvaluationRowID(courseID, evalName);
		remindLinkLocator = bi.getCoordEvaluationRemindLinkLocator(evalRowID);
		try{
			bi.clickAndCancel(remindLinkLocator);
		} catch (NoAlertException e){
			fail("Remind link not clickable on OPEN evaluation, or it is clickable but no confirmation box");
		}
		
		______TS("OPEN: click and confirm");
		
		try{
			bi.clickAndConfirm(remindLinkLocator);
		} catch (NoAlertException e){
			fail("Remind link not clickable on OPEN evaluation, or it is clickable but no confirmation box");
		}

		// Check email
		if(TestProperties.inst().isLocalHost()) return;
		bi.waitForEmail();
		assertEquals(courseID,EmailAccount.getEvaluationReminderFromGmail(scn.students.get("alice.tmms@CEvalUiT.CS2104").email, TestProperties.inst().TEAMMATES_COMMON_PASSWORD_FOR_STUDENT_ACCOUNTS, courseID, evalName));
	}
	
	public void testCoordEvalDeleteLink() throws Exception{
		
		
		______TS("click and cancel");
		
		String courseID = scn.evaluations.get("awaitingEval").course;
		String evalName = scn.evaluations.get("awaitingEval").name;
		int evalRowID = bi.getEvaluationRowID(courseID, evalName);
		By deleteLinkLocator = bi.getCoordEvaluationDeleteLinkLocator(evalRowID);
		try{
			bi.clickAndCancel(deleteLinkLocator);
			String evaluation = BackDoor.getEvaluationAsJson(courseID, evalName);
			if(isNullJSON(evaluation)) fail("Evaluation was deleted when it's not supposed to be");
		} catch (NoAlertException e){
			fail("Delete link not clickable or it is clickable but no confirmation box");
		}

		______TS("click and confirm");
		try{
			bi.clickAndConfirm(deleteLinkLocator);
			// Regex test due to the date in the evaluation form
			bi.verifyCurrentPageHTMLRegex(Common.TEST_PAGES_FOLDER+"/coordEvalDeleteSuccessful.html");
		} catch (NoAlertException e){
			fail("Delete link not clickable or it is clickable but no confirmation box");
		}
	}

	@SuppressWarnings("unused")
	private class TestScenario{
		public CoordData coordinator;
		public CourseData course;
		public EvaluationData evaluation;
		public EvaluationData evaluationInCourseWithNoTeams;
	}
}