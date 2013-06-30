package teammates.test.cases.ui.browsertests;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.Common;
import teammates.common.Url;
import teammates.common.datatransfer.DataBundle;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.InstructorFeedbackResultsPage;

/**
 * Tests 'Feedback Results' view of instructors.
 * SUT: {@link InstructorFeedbackResultsPage}.
 */
public class InstructorFeedbackResultsPageUiTest extends BaseUiTestCase {

	private static DataBundle testData;
	private static Browser browser;
	private InstructorFeedbackResultsPage resultsPage;
	
	
	
	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader();
		testData = loadDataBundle("/InstructorFeedbackResultsPageUiTest.json");
		restoreTestDataOnServer(testData);

		browser = BrowserPool.getBrowser();		
	}
	
	@Test
	public void testAll() throws Exception {
		testContent();
		testSortAction();
		testLink();
	}
	
	public void testContent(){
		
		______TS("standard session results");
		
		resultsPage = loginToInstructorFeedbackSubmitPage("CFResultsUiT.instr", "Open Session");
		resultsPage.verifyHtml("/instructorFeedbackResultsPageOpen.html");

		______TS("empty session");
		
		resultsPage = loginToInstructorFeedbackSubmitPage("CFResultsUiT.instr", "Empty Session");
		resultsPage.verifyHtml("/instructorFeedbackResultsPageEmpty.html");
	}
	
	public void testSortAction(){
		
		______TS("test sort types");
		
		resultsPage = loginToInstructorFeedbackSubmitPage("CFResultsUiT.instr", "Open Session");
		resultsPage.displayByGiver();
		resultsPage.verifyHtml("/instructorFeedbackResultsSortGiver.html");

		resultsPage.displayByRecipient();
		resultsPage.verifyHtml("/instructorFeedbackResultsSortRecipient.html");

		resultsPage.displayByTable();
		resultsPage.verifyHtml("/instructorFeedbackResultsSortTable.html");
		
		______TS("test in-table sort");
		
		resultsPage.sortTableByAnswer()
				.verifyTablePattern(2,"{*}1 Response to Danny.{*}2 Response to Benny.{*}3 Response to Emily.{*}4 Response to Charlie.");
		resultsPage.sortTableByAnswer()
				.verifyTablePattern(2,"{*}4 Response to Charlie.{*}3 Response to Emily.{*}2 Response to Benny.{*}1 Response to Danny.");
		
		resultsPage.sortTableByGiver()
				.verifyTablePattern(0,"{*}Alice Betsy{*}Benny Charles{*}Benny Charles{*}Charlie Davis");
		resultsPage.sortTableByGiver()
				.verifyTablePattern(0,"{*}Charlie Davis{*}Benny Charles{*}Benny Charles{*}Alice Betsy");
		
		resultsPage.sortTableByRecipient()
				.verifyTablePattern(1,"{*}Benny Charles{*}Charlie Davis{*}Danny Engrid{*}Emily");
		resultsPage.sortTableByRecipient()
				.verifyTablePattern(1,"{*}Emily{*}Danny Engrid{*}Charlie Davis{*}Benny Charles");

	}
	
	public void testLink() {
		resultsPage.clickEditLink();
	}

	@AfterClass
	public static void classTearDown() throws Exception {
		BrowserPool.release(browser);
	}
	
	private InstructorFeedbackResultsPage loginToInstructorFeedbackSubmitPage(
			String instructorName, String fsName) {
		Url editUrl = new Url(Common.PAGE_INSTRUCTOR_FEEDBACK_RESULTS)
				.withUserId(testData.instructors.get(instructorName).googleId)
				.withCourseId(testData.feedbackSessions.get(fsName).courseId)
				.withSessionName(testData.feedbackSessions.get(fsName).feedbackSessionName);
		return loginAdminToPage(browser, editUrl,
				InstructorFeedbackResultsPage.class);
	}

}