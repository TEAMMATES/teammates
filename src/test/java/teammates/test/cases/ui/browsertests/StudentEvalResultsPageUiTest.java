package teammates.test.cases.ui.browsertests;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.common.util.Url;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.StudenEvalResultsPage;

/**
 * Covers 'Evaluation Results' page for students.
 * SUT: {@link StudenEvalResultsPage}
 */
public class StudentEvalResultsPageUiTest extends BaseUiTestCase {
	private static Browser browser;
	private static DataBundle testData;
	private StudenEvalResultsPage resultsPage;
	

	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader();
		testData = loadDataBundle("/StudentEvalResultsPageUiTest.json");
		restoreTestDataOnServer(testData);
		browser = BrowserPool.getBrowser();
	}
	
	@Test	
	public void testAll() throws Exception{
		
		______TS("content");
		
		verifyResultContent("Third Eval", "SEvalRUiT.charlie.d", "/studentEvalResultsTypicalHTML.html");

		//typical case: two members
		
		verifyResultContent("Third Eval", "SEvalRUiT.alice.b", "/studentEvalResultsTwoMembersTypicalHTML.html");
		
		//TODO: give more details of each extreme case. In what way is it extreme?
		//extreme case: 1
		
		verifyResultContent("Second Eval", "SEvalRUiT.charlie.d", "/studentEvalResultsExtreme1HTML.html");

		//extreme case: 2
		
		verifyResultContent("Second Eval", "SEvalRUiT.danny.e", "/studentEvalResultsExtreme2HTML.html");

		//extreme case: 3
		
		verifyResultContent("Second Eval", "SEvalRUiT.emily.f", "/studentEvalResultsExtreme3HTML.html");

		//student did not submit
		
		verifyResultContent("Second Eval", "SEvalRUiT.alice.b", "/studentEvalResultsNotSubmittedHTML.html");

		//teammates did not submit
		
		verifyResultContent("Second Eval", "SEvalRUiT.benny.c", "/studentEvalResultsTheOtherDidn'tSubmitHTML.html");
		
		//with p2pFeedback disabled
		
		verifyResultContent("P2P Disabled Eval", "SEvalRUiT.benny.c", "/studentEvalResultsP2PDisabled.html");
		
		______TS("links, inputValidation, actions");
		
		//nothing to test here.
		
	}

	private void verifyResultContent(String evalObjectId, String studentObjectId, String filePath) {
		
		Url resultsUrl = createUrl(Const.ActionURIs.STUDENT_EVAL_RESULTS_PAGE)
			.withUserId(testData.students.get(studentObjectId).googleId)
			.withCourseId(testData.evaluations.get(evalObjectId).courseId)
			.withEvalName(testData.evaluations.get(evalObjectId).name);
		
		resultsPage = loginAdminToPage(browser, resultsUrl, StudenEvalResultsPage.class);
		resultsPage.verifyHtml(filePath);
	}

	@AfterClass
	public static void classTearDown() throws Exception {
		BrowserPool.release(browser);
	}
}