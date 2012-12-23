package teammates.test.cases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;

import teammates.common.Common;
import teammates.common.datatransfer.DataBundle;
import teammates.test.driver.BackDoor;
import teammates.test.driver.BrowserInstance;
import teammates.test.driver.BrowserInstancePool;
import teammates.test.driver.NoAlertException;
import teammates.test.driver.TestProperties;

/**
 * Tests instructorEvalResults.jsp from UI functionality and HTML test
 */
public class InstructorEvalResultsPageUiTest extends BaseTestCase {
	private static BrowserInstance bi;
	private static DataBundle scn;
	
	private static String appUrl = TestProperties.inst().TEAMMATES_URL;
	
	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader();

		startRecordingTimeForDataImport();
		String jsonString = Common.readFile(Common.TEST_DATA_FOLDER+"/InstructorEvalResultsUiTest.json");
		scn = Common.getTeammatesGson().fromJson(jsonString, DataBundle.class);
		BackDoor.deleteInstructors(jsonString);
		String backDoorOperationStatus = BackDoor.persistNewDataBundle(jsonString);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, backDoorOperationStatus);
		reportTimeForDataImport();

		bi = BrowserInstancePool.getBrowserInstance();

		bi.loginAdmin(TestProperties.inst().TEST_ADMIN_ACCOUNT, TestProperties.inst().TEST_ADMIN_PASSWORD);
	}
	
	@AfterClass
	public static void classTearDown() throws Exception {
		BrowserInstancePool.release(bi);
		printTestClassFooter();
	}

	@Test
	public void testInstructorEvalResultsOpenEval() throws Exception{
		

		______TS("summary view");
		
		String link = appUrl+Common.PAGE_INSTRUCTOR_EVAL_RESULTS;
		link = Common.addParamToUrl(link,Common.PARAM_COURSE_ID,scn.courses.get("CEvalRUiT.CS1101").id);
		link = Common.addParamToUrl(link,Common.PARAM_EVALUATION_NAME,scn.evaluations.get("First Eval").name);
		link = Common.addParamToUrl(link,Common.PARAM_USER_ID,scn.instructors.get("teammates.demo.instructor").googleId);
		bi.goToUrl(link);
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/instructorEvalResultsOpenEval.html");
		
		//TODO: check for sorting?

		______TS("details by reviewer");
		
		bi.getSelenium().check("id=radio_reviewer");
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/instructorEvalResultsOpenEvalByReviewer.html");
		
		______TS("details by reviewee");
		
		bi.getSelenium().check("id=radio_reviewee");
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/instructorEvalResultsOpenEvalByReviewee.html");
	}
	
	@Test
	public void testInstructorEvalResultsPublishedEval() throws Exception{
		
		
		______TS("summary view");
		
		String link = appUrl + Common.PAGE_INSTRUCTOR_EVAL_RESULTS;
		link = Common.addParamToUrl(link,Common.PARAM_COURSE_ID,scn.courses.get("CEvalRUiT.CS1101").id);
		link = Common.addParamToUrl(link,Common.PARAM_EVALUATION_NAME,scn.evaluations.get("Second Eval").name);
		link = Common.addParamToUrl(link,Common.PARAM_USER_ID,scn.instructors.get("teammates.demo.instructor").googleId);
		bi.goToUrl(link);
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/instructorEvalResultsPublishedEval.html");

		______TS("unpublishing: click and cancel");
		
		By unpublishButton = By.id("button_unpublish");
		try{
			bi.clickAndCancel(unpublishButton);
			bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/instructorEvalResultsPublishedEval.html");
		} catch (NoAlertException e){
			fail("No confirmation box when clicking unpublish button");
		}
		
		______TS("unpublishing: click and confirm");
		
		try{
			bi.clickAndConfirm(unpublishButton);
			bi.waitForStatusMessage(Common.MESSAGE_EVALUATION_UNPUBLISHED);
		} catch (NoAlertException e){
			fail("No confirmation box when clicking unpublish button");
		}
		//TODO: check for the full html?
	}
	
	@Test
	public void testInstructorEvalResultsClosedEval() throws Exception{
		
		
		______TS("summary view");
		
		String link = appUrl + Common.PAGE_INSTRUCTOR_EVAL_RESULTS;
		link = Common.addParamToUrl(link,Common.PARAM_COURSE_ID,scn.courses.get("CEvalRUiT.CS1101").id);
		link = Common.addParamToUrl(link,Common.PARAM_EVALUATION_NAME,scn.evaluations.get("Third Eval").name);
		link = Common.addParamToUrl(link,Common.PARAM_USER_ID,scn.instructors.get("teammates.demo.instructor").googleId);
		bi.goToUrl(link);
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/instructorEvalResultsClosedEval.html");

		______TS("publishing: click and cancel");
		
		By publishButton = By.id("button_publish");
		try{
			bi.clickAndCancel(publishButton);
			bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/instructorEvalResultsClosedEval.html");
		} catch (NoAlertException e){
			fail("No confirmation box when clicking publish button");
		}
		
		______TS("publishing: click and confirm");
		
		try{
			bi.clickAndConfirm(publishButton);
			bi.waitForStatusMessage(Common.MESSAGE_EVALUATION_PUBLISHED);
			//TODO: verify emails were sent to students
		} catch (NoAlertException e){
			fail("No confirmation box when clicking publish button");
		}
	}
}