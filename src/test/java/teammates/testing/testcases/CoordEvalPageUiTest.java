package teammates.testing.testcases;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;

import teammates.api.Common;
import teammates.datatransfer.CoordData;
import teammates.datatransfer.CourseData;
import teammates.datatransfer.DataBundle;
import teammates.datatransfer.EvaluationData;
import teammates.exception.NoAlertAppearException;
import teammates.jsp.Helper;
import teammates.testing.config.Config;
import teammates.testing.lib.BackDoor;
import teammates.testing.lib.BrowserInstance;
import teammates.testing.lib.BrowserInstancePool;
import teammates.testing.lib.SharedLib;

/**
 * Tests coordEval.jsp from UI functionality and HTML test
 * @author Aldrian Obaja
 *
 */
public class CoordEvalPageUiTest extends BaseTestCase {
	private static BrowserInstance bi;
	private static DataBundle scn;
	
	private static String appUrl = Config.inst().TEAMMATES_URL;
	
	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader("CoordEvalAddUITest");
		String jsonString = Common.readFile(Common.TEST_DATA_FOLDER+"/CoordEvalUiTest.json");
		scn = Common.getTeammatesGson().fromJson(jsonString, DataBundle.class);
		
		System.out.println("Importing test data...");
		long start = System.currentTimeMillis();
		BackDoor.deleteCoordinators(jsonString);
		BackDoor.createCoord(scn.coords.get("teammates.test"));
		System.out.println("The test data was imported in "+(System.currentTimeMillis()-start)+" ms");

		bi = BrowserInstancePool.getBrowserInstance();

		bi.loginAdmin(Config.inst().TEAMMATES_ADMIN_ACCOUNT, Config.inst().TEAMMATES_ADMIN_PASSWORD);
		String link = appUrl+Common.PAGE_COORD_EVAL;
		link = Helper.addParam(link,Common.PARAM_USER_ID,scn.coords.get("teammates.test").id);
		bi.goToUrl(link);
	}
	
	@AfterClass
	public static void classTearDown() throws Exception {
		BrowserInstancePool.release(bi);
		printTestClassFooter("CoordEvalAddUITest");
	}

	@Test
	public void testCoordEvalPage() throws Exception{
		testCoordEvalHTML();
		testCoordEvalUiPaths();
		testCoordEvalLinks();
	}

	public void testCoordEvalHTML() throws Exception{
		printTestCaseHeader("CoordEvalHTMLTest");
//		bi.printCurrentPage(Common.TEST_PAGES_FOLDER+"/coordEvalEmptyAll.html");
		bi.verifyCurrentPageHTMLRegex(Common.TEST_PAGES_FOLDER+"/coordEvalEmptyAll.html");
		
		BackDoor.createCourse(scn.courses.get("course"));
		BackDoor.createCourse(scn.courses.get("anotherCourse"));
		BackDoor.createStudent(scn.students.get("alice.tmms@CEvalUiT.CS2104"));
		BackDoor.createStudent(scn.students.get("benny.tmms@CEvalUiT.CS2104"));
		BackDoor.createStudent(scn.students.get("charlie.tmms@CEvalUiT.CS1101"));
		BackDoor.createStudent(scn.students.get("danny.tmms@CEvalUiT.CS1101"));
		bi.goToEvaluation();
		
//		bi.printCurrentPage(Common.TEST_PAGES_FOLDER+"/coordEvalEmptyEval.html");
		bi.verifyCurrentPageHTMLRegex(Common.TEST_PAGES_FOLDER+"/coordEvalEmptyEval.html");
		
		BackDoor.createEvaluation(scn.evaluations.get("openEval"));
		BackDoor.createEvaluation(scn.evaluations.get("publishedEval"));
		BackDoor.createEvaluation(scn.evaluations.get("closedEval"));
		bi.goToEvaluation();

//		bi.printCurrentPage(Common.TEST_PAGES_FOLDER+"/coordEvalByDeadline.html");
		bi.verifyCurrentPageHTMLRegex(Common.TEST_PAGES_FOLDER+"/coordEvalByDeadline.html");

		bi.click(By.id("button_sortname"));
//		bi.printCurrentPage(Common.TEST_PAGES_FOLDER+"/coordEvalByName.html");
		bi.verifyCurrentPageHTMLRegex(Common.TEST_PAGES_FOLDER+"/coordEvalByName.html");
		
		bi.click(By.id("button_sortcourseid"));
//		bi.printCurrentPage(Common.TEST_PAGES_FOLDER+"/coordEvalById.html");
		bi.verifyCurrentPageHTMLRegex(Common.TEST_PAGES_FOLDER+"/coordEvalById.html");
	}

	public void testCoordEvalUiPaths() throws Exception{
		printTestCaseHeader("CoordEvalUiPathsTest");
		
		EvaluationData eval = scn.evaluations.get("awaitingEval");
		bi.addEvaluation(eval.course, eval.name, eval.startTime, eval.endTime, eval.p2pEnabled, eval.instructions, eval.gracePeriod);
		
//		bi.printCurrentPage(Common.TEST_PAGES_FOLDER+"/coordEvalAddSuccess.html");
		bi.verifyCurrentPageHTMLRegex(Common.TEST_PAGES_FOLDER+"/coordEvalAddSuccess.html");

		printTestCaseHeader("CoordEvalInputValidationTest");
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

		printTestCaseHeader("CoordEvalExistsTest");
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
		printTestCaseHeader("testCoordEvalPublishLink");

		// Publish link should be clickable in closed eval
		String courseID = scn.evaluations.get("closedEval").course;
		String evalName = scn.evaluations.get("closedEval").name;
		int evalRowID = bi.getEvaluationRowID(courseID, evalName);
		By publishLinkLocator = bi.getCoordEvaluationPublishLinkLocator(evalRowID);
		try{
			bi.clickAndCancel(publishLinkLocator);
		} catch (NoAlertAppearException e){
			fail("Publish link not clickable on closed evaluation");
		}
		try{
			bi.clickAndConfirm(publishLinkLocator);
			bi.waitForStatusMessage(Common.MESSAGE_EVALUATION_PUBLISHED);
		} catch (NoAlertAppearException e){
			fail("Publish link not clickable on closed evaluation");
		}
		
		// Unpublish link should be clickable in published eval
		courseID = scn.evaluations.get("publishedEval").course;
		evalName = scn.evaluations.get("publishedEval").name;
		evalRowID = bi.getEvaluationRowID(courseID, evalName);
		publishLinkLocator = bi.getCoordEvaluationUnpublishLinkLocator(evalRowID);
		try{
			bi.clickAndCancel(publishLinkLocator);
		} catch (NoAlertAppearException e){
			fail("Unpublish link not clickable on published evaluation");
		}
		try{
			bi.clickAndConfirm(publishLinkLocator);
			bi.waitForStatusMessage(Common.MESSAGE_EVALUATION_UNPUBLISHED);
		} catch (NoAlertAppearException e){
			fail("Unpublish link not clickable on published evaluation");
		}
	}
	
	public void testCoordEvalRemindLink() throws Exception{
		printTestCaseHeader("testCoordEvalRemindLink");

		// Remind link should be not clickable in published eval
		String courseID = scn.evaluations.get("publishedEval").course;
		String evalName = scn.evaluations.get("publishedEval").name;
		int evalRowID = bi.getEvaluationRowID(courseID, evalName);
		By remindLinkLocator = bi.getCoordEvaluationRemindLinkLocator(evalRowID);
		try{
			bi.clickAndCancel(remindLinkLocator);
			fail("Remind link clickable on published evaluation");
		} catch (NoAlertAppearException e){}

		// Remind link should be not clickable in closed eval
		courseID = scn.evaluations.get("closedEval").course;
		evalName = scn.evaluations.get("closedEval").name;
		evalRowID = bi.getEvaluationRowID(courseID, evalName);
		remindLinkLocator = bi.getCoordEvaluationRemindLinkLocator(evalRowID);
		try{
			bi.clickAndCancel(remindLinkLocator);
			fail("Remind link clickable on closed evaluation");
		} catch (NoAlertAppearException e){}

		// Remind link should be not clickable in awaiting eval
		courseID = scn.evaluations.get("awaitingEval").course;
		evalName = scn.evaluations.get("awaitingEval").name;
		evalRowID = bi.getEvaluationRowID(courseID, evalName);
		remindLinkLocator = bi.getCoordEvaluationRemindLinkLocator(evalRowID);
		try{
			bi.clickAndCancel(remindLinkLocator);
			fail("Remind link clickable on awaiting evaluation");
		} catch (NoAlertAppearException e){}

		// Remind link should be clickable in open eval
		courseID = scn.evaluations.get("openEval").course;
		evalName = scn.evaluations.get("openEval").name;
		evalRowID = bi.getEvaluationRowID(courseID, evalName);
		remindLinkLocator = bi.getCoordEvaluationRemindLinkLocator(evalRowID);
		try{
			bi.clickAndCancel(remindLinkLocator);
		} catch (NoAlertAppearException e){
			fail("Remind link not clickable on OPEN evaluation, or it is clickable but no confirmation box");
		}
		
		// Actually click the link
		try{
			bi.clickAndConfirm(remindLinkLocator);
		} catch (NoAlertAppearException e){
			fail("Remind link not clickable on OPEN evaluation, or it is clickable but no confirmation box");
		}

		// Check email
		if(Config.inst().isLocalHost()) return;
		bi.waitForEmail();
		assertEquals(courseID,SharedLib.getEvaluationReminderFromGmail(scn.students.get("alice.tmms@CEvalUiT.CS2104").email, Config.inst().TEAMMATES_APP_PASSWORD, courseID, evalName));
	}
	
	public void testCoordEvalDeleteLink() throws Exception{
		printTestCaseHeader("testCoordEvalDeleteLink");
		
//		bi.printCurrentPage(Common.TEST_PAGES_FOLDER+"/coordEvalDeleteInit.html");
		bi.verifyCurrentPageHTMLRegex(Common.TEST_PAGES_FOLDER+"/coordEvalDeleteInit.html");
		
		// Delete link should be clickable
		String courseID = scn.evaluations.get("awaitingEval").course;
		String evalName = scn.evaluations.get("awaitingEval").name;
		int evalRowID = bi.getEvaluationRowID(courseID, evalName);
		By deleteLinkLocator = bi.getCoordEvaluationDeleteLinkLocator(evalRowID);
		try{
			bi.clickAndCancel(deleteLinkLocator);
			bi.verifyCurrentPageHTMLRegex(Common.TEST_PAGES_FOLDER+"/coordEvalDeleteInit.html");
		} catch (NoAlertAppearException e){
			fail("Delete link not clickable or it is clickable but no confirmation box");
		}

		try{
			bi.clickAndConfirm(deleteLinkLocator);
//			bi.printCurrentPage(Common.TEST_PAGES_FOLDER+"/coordEvalDeleteSuccessful.html");
			bi.verifyCurrentPageHTMLRegex(Common.TEST_PAGES_FOLDER+"/coordEvalDeleteSuccessful.html");
		} catch (NoAlertAppearException e){
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