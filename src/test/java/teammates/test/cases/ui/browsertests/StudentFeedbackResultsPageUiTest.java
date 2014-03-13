package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.common.util.Const;
import teammates.common.util.Url;
import teammates.test.driver.BackDoor;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.StudentFeedbackResultsPage;

/**
 * Tests 'Feedback Results' view of students.
 * SUT: {@link StudentFeedbackResultsPage}.
 */
public class StudentFeedbackResultsPageUiTest extends BaseUiTestCase {

	private static DataBundle testData;
	private static Browser browser;
	private StudentFeedbackResultsPage resultsPage;
	
	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader();
		testData = loadDataBundle("/StudentFeedbackResultsPageUiTest.json");
		restoreTestDataOnServer(testData);
		
		FeedbackQuestionAttributes fq = BackDoor.getFeedbackQuestion(
				"SFResultsUiT.CS2104", "First Session", 1);
		FeedbackResponseAttributes fr = BackDoor.getFeedbackResponse(
				fq.getId(), "SFResultsUiT.alice.b@gmail.com",
				"SFResultsUiT.alice.b@gmail.com");

		// Make feedback response comment point to 
		// actual feedback question/response key
		String frcKey = "comment1FromT1C1ToR1Q1S1C1";
		FeedbackResponseCommentAttributes frc = testData.feedbackResponseComments.get(frcKey);
		frc.feedbackQuestionId = fq.getId();
		frc.feedbackResponseId = fr.getId();
		testData.feedbackResponseComments.put(frcKey, frc);
		BackDoor.updateFeedbackResponseComment(frcKey, frc);

		frcKey = "comment2FromT1C1ToR1Q1S1C1";
		frc = testData.feedbackResponseComments.get(frcKey);
		frc.feedbackQuestionId = fq.getId();
		frc.feedbackResponseId = fr.getId();
		testData.feedbackResponseComments.put(frcKey, frc);
		BackDoor.updateFeedbackResponseComment(frcKey, frc);
		
		browser = BrowserPool.getBrowser();		
	}
	
	@Test
	public void testAll() throws Exception {
		
		______TS("no responses");
		
		resultsPage = loginToStudentFeedbackSubmitPage("Alice", "Empty Session");
		resultsPage.verifyHtml("/studentFeedbackResultsPageEmpty.html");
			
		______TS("standard session results");
		
		resultsPage = loginToStudentFeedbackSubmitPage("Alice", "Open Session");
		resultsPage.verifyHtml("/studentFeedbackResultsPageOpen.html");
		
		______TS("MCQ session results");
		
		resultsPage = loginToStudentFeedbackSubmitPage("Alice", "MCQ Session");
		resultsPage.verifyHtml("/studentFeedbackResultsPageMCQ.html");
		
		assertEquals("[more]", resultsPage.getQuestionAdditionalInfoButtonText(4,""));
		assertEquals(true, resultsPage.clickQuestionAdditionalInfoButton(4,""));
		assertEquals("[less]", resultsPage.getQuestionAdditionalInfoButtonText(4,""));
		assertEquals(false, resultsPage.clickQuestionAdditionalInfoButton(4,""));
		assertEquals("[more]", resultsPage.getQuestionAdditionalInfoButtonText(4,""));

		assertEquals("[more]", resultsPage.getQuestionAdditionalInfoButtonText(5,""));
		assertEquals(true, resultsPage.clickQuestionAdditionalInfoButton(5,""));
		assertEquals("[less]", resultsPage.getQuestionAdditionalInfoButtonText(5,""));
		assertEquals(false, resultsPage.clickQuestionAdditionalInfoButton(5,""));
		assertEquals("[more]", resultsPage.getQuestionAdditionalInfoButtonText(5,""));
		
		______TS("MSQ session results");
		
		resultsPage = loginToStudentFeedbackSubmitPage("Alice", "MSQ Session");
		resultsPage.verifyHtml("/studentFeedbackResultsPageMSQ.html");
		
		assertEquals("[more]", resultsPage.getQuestionAdditionalInfoButtonText(4,""));
		assertEquals(true, resultsPage.clickQuestionAdditionalInfoButton(4,""));
		assertEquals("[less]", resultsPage.getQuestionAdditionalInfoButtonText(4,""));
		assertEquals(false, resultsPage.clickQuestionAdditionalInfoButton(4,""));
		assertEquals("[more]", resultsPage.getQuestionAdditionalInfoButtonText(4,""));

		assertEquals("[more]", resultsPage.getQuestionAdditionalInfoButtonText(5,""));
		assertEquals(true, resultsPage.clickQuestionAdditionalInfoButton(5,""));
		assertEquals("[less]", resultsPage.getQuestionAdditionalInfoButtonText(5,""));
		assertEquals(false, resultsPage.clickQuestionAdditionalInfoButton(5,""));
		assertEquals("[more]", resultsPage.getQuestionAdditionalInfoButtonText(5,""));
		
		______TS("NUMSCALE session results");
		
		resultsPage = loginToStudentFeedbackSubmitPage("Alice", "NUMSCALE Session");
		resultsPage.verifyHtml("/studentFeedbackResultsPageNUMSCALE.html");
	}

	@AfterClass
	public static void classTearDown() throws Exception {
		BrowserPool.release(browser);
	}
	
	private StudentFeedbackResultsPage loginToStudentFeedbackSubmitPage(
			String studentName, String fsName) {
		Url editUrl = createUrl(Const.ActionURIs.STUDENT_FEEDBACK_RESULTS_PAGE)
				.withUserId(testData.students.get(studentName).googleId)
				.withCourseId(testData.feedbackSessions.get(fsName).courseId)
				.withSessionName(testData.feedbackSessions.get(fsName).feedbackSessionName);
		return loginAdminToPage(browser, editUrl,
				StudentFeedbackResultsPage.class);
	}

}