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
	private static String jsonString;
	
	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader();

		startRecordingTimeForDataImport();
		jsonString = Common.readFile(Common.TEST_DATA_FOLDER+"/InstructorEvalResultsUiTest.json");
		scn = Common.getTeammatesGson().fromJson(jsonString, DataBundle.class);
		BackDoor.deleteCourses(jsonString);
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

		// Always cleanup
		BackDoor.deleteCourses(jsonString);
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
		
		
		
		______TS("sort by name");
		bi.click(By.id("button_sortname"));
		assertContainsRegex("{*}Alice Betsy{*}Benny Charles{*}Charlie Davis{*}Danny Engrid{*}Emily{*}",bi.getCurrentPageSource());
		bi.click(By.id("button_sortname"));
		assertContainsRegex("{*}Emily{*}Danny Engrid{*}Charlie Davis{*}Benny Charles{*}Alice Betsy{*}",bi.getCurrentPageSource());
		
		______TS("sort by claimed");
		bi.click(By.id("button_sortclaimed"));
		assertContainsRegex("{*}E -5%{*}E +3%{*}E +5%{*}E +10%{*}E +10%{*}",bi.getCurrentPageSource());
		bi.click(By.id("button_sortclaimed"));
		assertContainsRegex("{*}E +10%{*}E +10%{*}E +5%{*}E +3%{*}E -5%{*}",bi.getCurrentPageSource());
		
		______TS("sort by perceived");
		//removed the "E" only for testing else will cause infinite loop
		bi.click(By.id("button_sortperceived"));
		assertContainsRegex("{*}E -3%{*}E -1%{*}E +4%{*}",bi.getCurrentPageSource());
		bi.click(By.id("button_sortperceived"));
		assertContainsRegex("{*}E +4%{*}E -1%{*}E -3%{*}",bi.getCurrentPageSource());
		
		______TS("sort by diff");
		bi.click(By.id("button_sortdiff"));
		assertContainsRegex("{*}-11%{*}-6%{*}-6%{*}-5%{*}+5%{*}",bi.getCurrentPageSource());
		bi.click(By.id("button_sortdiff"));
		assertContainsRegex("{*}+5%{*}-5%{*}-6%{*}-6%{*}-11%{*}",bi.getCurrentPageSource());
		
		
		______TS("sort by team name");
		bi.click(By.id("button_sortteamname"));
		assertContainsRegex("{*}Team 1{*}Team 1{*}Team 1{*}Team 2{*}Team 2{*}",bi.getCurrentPageSource());
		bi.click(By.id("button_sortteamname"));
		assertContainsRegex("{*}Team 2{*}Team 2{*}Team 1{*}Team 1{*}Team 1{*}",bi.getCurrentPageSource());
		
		//set back to ascending
		bi.click(By.id("button_sortteamname"));

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
		
		 ______TS("Check download evaluation report link");
         
         //Current attempt
         String evaluationReportLink = appUrl + Common.PAGE_INSTRUCTOR_EVAL_EXPORT;
         evaluationReportLink = Common.addParamToUrl(evaluationReportLink,Common.PARAM_COURSE_ID,scn.courses.get("CEvalRUiT.CS1101").id);
         evaluationReportLink = Common.addParamToUrl(evaluationReportLink,Common.PARAM_EVALUATION_NAME,scn.evaluations.get("First Eval").name); //First Evaluation is the published evaluation in the sample data for instructor
         bi.goToUrl(evaluationReportLink);
         		
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
	
	@Test
	public void testInstructorEvalResultsP2PDisabled() throws Exception{
		______TS("summary view");
		
		String link = appUrl+Common.PAGE_INSTRUCTOR_EVAL_RESULTS;
		link = Common.addParamToUrl(link,Common.PARAM_COURSE_ID,scn.courses.get("CEvalRUiT.CS1101").id);
		link = Common.addParamToUrl(link,Common.PARAM_EVALUATION_NAME,scn.evaluations.get("Fifth Eval").name);
		link = Common.addParamToUrl(link,Common.PARAM_USER_ID,scn.instructors.get("teammates.demo.instructor").googleId);
		bi.goToUrl(link);
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/instructorEvalResultsP2PDisabled.html");
		
		//TODO: check for sorting?

		______TS("details by reviewer");
		
		bi.getSelenium().check("id=radio_reviewer");
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/instructorEvalResultsP2PDisabledByReviewer.html");
		
		______TS("details by reviewee");
		
		bi.getSelenium().check("id=radio_reviewee");
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/instructorEvalResultsP2PDisabledByReviewee.html");
	}
}