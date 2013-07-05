package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.common.util.Url;
import teammates.test.driver.BackDoor;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.InstructorEvalEditPage;

/**
 * Tests 'Edit evaluation' functionality for instructors.
 * SUT: {@link InstructorEvalEditPage}.
 */
public class InstructorEvalEditPageUiTest extends BaseUiTestCase {
	
	private static DataBundle testData;
	private static Browser browser;
	private static InstructorEvalEditPage editPage;
	
	private static EvaluationAttributes existingEval;
	
	
	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader();
		testData = loadDataBundle("/InstructorEvalEditPageUiTest.json");
		restoreTestDataOnServer(testData);
		browser = BrowserPool.getBrowser();
		
		existingEval = testData.evaluations.get("evaluation");
		
	}
	
	@Test
	public void runTestsInOrder() throws Exception{
		testContent();
		testInputValidation();
		testCancelAction();
		testEditAction();
	}
	
	public void testContent() throws Exception{
		
		String instructorId = testData.instructors.get("instructor").googleId;
		Url editPageUrl = createUrl(Const.ActionURIs.INSTRUCTOR_EVAL_EDIT_PAGE)
		.withUserId(instructorId)
		.withCourseId(existingEval.courseId)
		.withEvalName(existingEval.name);
		
		editPage = loginAdminToPage(browser, editPageUrl, InstructorEvalEditPage.class);
		editPage.verifyHtml("/instructorEvalEdit.html");
	}
	
	public void testEditAction() throws Exception{
		
		//these will be the updated values
		//TODO: make the start time a future time (after Issue 897 is fixed)
		existingEval.startTime = TimeHelper.convertToDate("2012-04-01 11:59 PM UTC"); 
		existingEval.endTime = TimeHelper.convertToDate("2015-04-01 10:00 PM UTC"); 
		existingEval.p2pEnabled = !existingEval.p2pEnabled; 
		existingEval.instructions = existingEval.instructions+"(edited)"; 
		existingEval.gracePeriod = existingEval.gracePeriod + 5;
		
		editPage.submitUpdate(
				existingEval.startTime, 
				existingEval.endTime, 
				existingEval.p2pEnabled, 
				existingEval.instructions, 
				existingEval.gracePeriod).verifyStatus(Const.StatusMessages.EVALUATION_EDITED);
		
		EvaluationAttributes updated = BackDoor.getEvaluation(existingEval.courseId, existingEval.name);
		assertEquals(existingEval.toString(), updated.toString());
	}
	
	public void testCancelAction(){
		//TODO: implement this, or remove the 'Cancel' button (preferred).
	}

	private void testInputValidation() {
		// TODO: implement this
	}

	@AfterClass
	public static void classTearDown() throws Exception {
		BrowserPool.release(browser);
	}

}