package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.common.util.Url;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.InstructorFeedbackEditPage;
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
		
		assertEquals("[more]", resultsPage.getQuestionAdditionalInfoButtonText(9,"giver-1-recipient-1"));
		assertEquals(true, resultsPage.clickQuestionAdditionalInfoButton(9,"giver-1-recipient-1"));
		assertEquals("[less]", resultsPage.getQuestionAdditionalInfoButtonText(9,"giver-1-recipient-1"));
		assertEquals(false, resultsPage.clickQuestionAdditionalInfoButton(9,"giver-1-recipient-1"));
		assertEquals("[more]", resultsPage.getQuestionAdditionalInfoButtonText(9,"giver-1-recipient-1"));

		assertEquals(true, resultsPage.clickQuestionAdditionalInfoButton(10,"giver-1-recipient-1"));
		assertEquals(false, resultsPage.clickQuestionAdditionalInfoButton(10,"giver-1-recipient-1"));

		assertEquals(true, resultsPage.clickQuestionAdditionalInfoButton(11,"giver-1-recipient-1"));
		assertEquals(false, resultsPage.clickQuestionAdditionalInfoButton(11,"giver-1-recipient-1"));

		assertEquals(true, resultsPage.clickQuestionAdditionalInfoButton(12,"giver-1-recipient-1"));
		assertEquals(false, resultsPage.clickQuestionAdditionalInfoButton(12,"giver-1-recipient-1"));

		resultsPage.verifyHtml("/instructorFeedbackResultsSortGiver.html");
		
		assertEquals("[more]", resultsPage.getQuestionAdditionalInfoButtonText(9,"giver-1-recipient-1"));
		assertEquals(true, resultsPage.clickQuestionAdditionalInfoButton(9,"giver-1-recipient-1"));
		assertEquals("[less]", resultsPage.getQuestionAdditionalInfoButtonText(9,"giver-1-recipient-1"));
		assertEquals(false, resultsPage.clickQuestionAdditionalInfoButton(9,"giver-1-recipient-1"));
		assertEquals("[more]", resultsPage.getQuestionAdditionalInfoButtonText(9,"giver-1-recipient-1"));

		assertEquals(true, resultsPage.clickQuestionAdditionalInfoButton(10,"giver-1-recipient-1"));
		assertEquals(false, resultsPage.clickQuestionAdditionalInfoButton(10,"giver-1-recipient-1"));

		assertEquals(true, resultsPage.clickQuestionAdditionalInfoButton(11,"giver-1-recipient-1"));
		assertEquals(false, resultsPage.clickQuestionAdditionalInfoButton(11,"giver-1-recipient-1"));

		assertEquals(true, resultsPage.clickQuestionAdditionalInfoButton(12,"giver-1-recipient-1"));
		assertEquals(false, resultsPage.clickQuestionAdditionalInfoButton(12,"giver-1-recipient-1"));

		
		resultsPage.displayByRecipient();
		resultsPage.verifyHtml("/instructorFeedbackResultsSortRecipient.html");

		assertEquals("[more]", resultsPage.getQuestionAdditionalInfoButtonText(9,"giver-1-recipient-1"));
		assertEquals(true, resultsPage.clickQuestionAdditionalInfoButton(9,"giver-1-recipient-1"));
		assertEquals("[less]", resultsPage.getQuestionAdditionalInfoButtonText(9,"giver-1-recipient-1"));
		assertEquals(false, resultsPage.clickQuestionAdditionalInfoButton(9,"giver-1-recipient-1"));
		assertEquals("[more]", resultsPage.getQuestionAdditionalInfoButtonText(9,"giver-1-recipient-1"));

		assertEquals(true, resultsPage.clickQuestionAdditionalInfoButton(10,"giver-1-recipient-1"));
		assertEquals(false, resultsPage.clickQuestionAdditionalInfoButton(10,"giver-1-recipient-1"));

		assertEquals(true, resultsPage.clickQuestionAdditionalInfoButton(11,"giver-1-recipient-1"));
		assertEquals(false, resultsPage.clickQuestionAdditionalInfoButton(11,"giver-1-recipient-1"));

		assertEquals(true, resultsPage.clickQuestionAdditionalInfoButton(12,"giver-1-recipient-1"));
		assertEquals(false, resultsPage.clickQuestionAdditionalInfoButton(12,"giver-1-recipient-1"));

		
		resultsPage.displayByTable();
		resultsPage.verifyHtml("/instructorFeedbackResultsSortTable.html");
		
		
		assertEquals("[more]", resultsPage.getQuestionAdditionalInfoButtonText(9,""));
		assertEquals(true, resultsPage.clickQuestionAdditionalInfoButton(9,""));
		assertEquals("[less]", resultsPage.getQuestionAdditionalInfoButtonText(9,""));
		assertEquals(false, resultsPage.clickQuestionAdditionalInfoButton(9,""));
		assertEquals("[more]", resultsPage.getQuestionAdditionalInfoButtonText(9,""));
		
		assertEquals(true, resultsPage.clickQuestionAdditionalInfoButton(10,""));
		assertEquals(false, resultsPage.clickQuestionAdditionalInfoButton(10,""));

		assertEquals(true, resultsPage.clickQuestionAdditionalInfoButton(11,""));
		assertEquals(false, resultsPage.clickQuestionAdditionalInfoButton(11,""));

		assertEquals(true, resultsPage.clickQuestionAdditionalInfoButton(12,""));
		assertEquals(false, resultsPage.clickQuestionAdditionalInfoButton(12,""));

		
		______TS("test in-table sort");
		
		resultsPage.sortTableByAnswer()
				.verifyTablePattern(2,"{*}1 Response to Danny.{*}2 Response to Benny.{*}3 Response to Emily.{*}4 Response to Charlie.");
		resultsPage.sortTableByAnswer()
				.verifyTablePattern(2,"{*}4 Response to Charlie.{*}3 Response to Emily.{*}2 Response to Benny.{*}1 Response to Danny.");
		
		resultsPage.sortTableByGiver()
				.verifyTablePattern(0,"{*}Alice Betsy{*}Benny Charles{*}Benny Charles{*}Charlie Dávis");
		resultsPage.sortTableByGiver()
				.verifyTablePattern(0,"{*}Charlie Dávis{*}Benny Charles{*}Benny Charles{*}Alice Betsy");
		
		resultsPage.sortTableByRecipient()
				.verifyTablePattern(1,"{*}Benny Charles{*}Charlie Dávis{*}Danny Engrid{*}Emily");
		resultsPage.sortTableByRecipient()
				.verifyTablePattern(1,"{*}Emily{*}Danny Engrid{*}Charlie Dávis{*}Benny Charles");

	}
	
	public void testLink() {
		______TS("action: download report");
		
		Url reportUrl = createUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_DOWNLOAD)
			.withUserId("CFResultsUiT.instr")
			.withCourseId("CFResultsUiT.CS2104")
			.withSessionName("First Session");
		
		resultsPage.verifyDownloadLink(reportUrl);
		
		______TS("action: edit");
		InstructorFeedbackEditPage editPage = resultsPage.clickEditLink();
		editPage.verifyContains("Edit Feedback Session");
		editPage.verifyContains("CFResultsUiT.CS2104");
		editPage.verifyContains("First Session");
	}

	@AfterClass
	public static void classTearDown() throws Exception {
		BrowserPool.release(browser);
	}
	
	private InstructorFeedbackResultsPage loginToInstructorFeedbackSubmitPage(
			String instructorName, String fsName) {
		Url editUrl = createUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_PAGE)
				.withUserId(testData.instructors.get(instructorName).googleId)
				.withCourseId(testData.feedbackSessions.get(fsName).courseId)
				.withSessionName(testData.feedbackSessions.get(fsName).feedbackSessionName);
		return loginAdminToPage(browser, editUrl,
				InstructorFeedbackResultsPage.class);
	}

}