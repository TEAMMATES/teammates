package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNull;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.util.Const;
import teammates.common.util.Url;
import teammates.test.driver.BackDoor;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.FeedbackQuestionSubmitPage;

public class StudentFeedbackQuestionSubmitPageUiTest extends BaseUiTestCase {
	private static DataBundle testData;
	private static Browser browser;
	private FeedbackQuestionSubmitPage submitPage;
	private FeedbackQuestionAttributes fq;
	
	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader();
		testData = loadDataBundle("/StudentFeedbackQuestionSubmitPageUiTest.json");
		restoreTestDataOnServer(testData);

		browser = BrowserPool.getBrowser();		
	}
	
	@Test
	public void testAll() throws Exception {
		testContent();
		testSubmitAction();
		// no links to test
	}
	
	private void testContent() throws Exception {
		
		______TS("Awaiting session");
		
		fq = BackDoor.getFeedbackQuestion("SFQSubmitUiT.CS2104", "Awaiting Session", 1);
		submitPage = loginToStudentFeedbackQuestionSubmitPage("Alice", "Awaiting Session", fq.getId());
		submitPage.verifyHtml("/studentFeedbackQuestionSubmitPageAwaiting.html");
		
		______TS("Open session");
		
		fq = BackDoor.getFeedbackQuestion("SFQSubmitUiT.CS2104", "Open Session", 1);
		submitPage = loginToStudentFeedbackQuestionSubmitPage("Alice", "Open Session", fq.getId());
		submitPage.verifyHtml("/studentFeedbackQuestionSubmitPageOpen.html");
		
		______TS("Grace period session");
		
		//TODO implement this
		//Session should look like closed session
		
		______TS("Closed) session");
		
		fq = BackDoor.getFeedbackQuestion("SFQSubmitUiT.CS2104", "Closed Session", 1);
		submitPage = loginToStudentFeedbackQuestionSubmitPage("Alice", "Closed Session", fq.getId());
		submitPage.verifyHtml("/studentFeedbackQuestionSubmitPageClosed.html");
	}
	
	private void testSubmitAction() {
		
		______TS("create new responses");

		fq = BackDoor.getFeedbackQuestion("SFQSubmitUiT.CS2104", "Open Session", 1);
		submitPage = loginToStudentFeedbackQuestionSubmitPage("Alice", "Open Session", fq.getId());
		
		submitPage.fillResponseTextBox(0, "Test Self Feedback");
		assertNull(BackDoor.getFeedbackResponse(fq.getId(),
				"SFQSubmitUiT.alice.b@gmail.com",
				"SFQSubmitUiT.alice.b@gmail.com"));
		
		submitPage.clickSubmitButton();

		assertEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED,
				submitPage.getStatus());
		
		assertNotNull(BackDoor.getFeedbackResponse(fq.getId(),
				"SFQSubmitUiT.alice.b@gmail.com",
				"SFQSubmitUiT.alice.b@gmail.com"));
		assertEquals("Test Self Feedback",
				BackDoor.getFeedbackResponse(fq.getId(),
					"SFQSubmitUiT.alice.b@gmail.com",
					"SFQSubmitUiT.alice.b@gmail.com").getResponseDetails().getAnswerString());
		
		______TS("edit existing response");		
		
		String editedResponse = "Edited self feedback.";
		submitPage.fillResponseTextBox(0, editedResponse);
		
		submitPage.clickSubmitButton();

		assertEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED,
				submitPage.getStatus());
		assertNotNull(BackDoor.getFeedbackResponse(fq.getId(),
				"SFQSubmitUiT.alice.b@gmail.com",
				"SFQSubmitUiT.alice.b@gmail.com"));
		assertEquals(editedResponse,
				BackDoor.getFeedbackResponse(fq.getId(),
					"SFQSubmitUiT.alice.b@gmail.com",
					"SFQSubmitUiT.alice.b@gmail.com").getResponseDetails().getAnswerString());
		
		submitPage.verifyHtml("/studentFeedbackQuestionSubmitPageFilled.html");
	}
	
	private FeedbackQuestionSubmitPage loginToStudentFeedbackQuestionSubmitPage(
			String studentName, String fsName, String questionId) {
		Url editUrl = createUrl(Const.ActionURIs.STUDENT_FEEDBACK_QUESTION_SUBMISSION_EDIT_PAGE)
				.withUserId(testData.students.get(studentName).googleId)
				.withCourseId(testData.feedbackSessions.get(fsName).courseId)
				.withSessionName(testData.feedbackSessions.get(fsName).feedbackSessionName)
				.withParam(Const.ParamsNames.FEEDBACK_QUESTION_ID, questionId);
		return loginAdminToPage(browser, editUrl, FeedbackQuestionSubmitPage.class);
	}
}
