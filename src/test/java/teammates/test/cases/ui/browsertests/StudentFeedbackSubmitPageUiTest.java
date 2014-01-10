package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertTrue;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackMsqResponseDetails;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EnrollException;
import teammates.common.util.Const;
import teammates.common.util.Url;
import teammates.test.driver.BackDoor;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.FeedbackSubmitPage;

/**
 * Tests 'Submit Feedback' view of students.
 * SUT: {@link StudentFeedbackSubmitPage}.
 */
public class StudentFeedbackSubmitPageUiTest extends BaseUiTestCase {
	private static DataBundle testData;
	private static Browser browser;
	private FeedbackSubmitPage submitPage;
		
	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader();
		testData = loadDataBundle("/StudentFeedbackSubmitPageUiTest.json");
		restoreTestDataOnServer(testData);

		browser = BrowserPool.getBrowser();		
	}
	
	@Test
	public void testAll() throws Exception {
		testContent();
		testSubmitAction();
		testModifyData();
		// No links to test
	}
	
	private void testContent() {
		
		______TS("Awaiting session");
		
		//TODO implement this
		//Session and questions should be visible but cannot be answered
		
		______TS("Open session");
		
		submitPage = loginToStudentFeedbackSubmitPage("Alice", "Open Session");
		submitPage.verifyHtml("/studentFeedbackSubmitPageOpen.html");
		
		______TS("Grace period session");
		
		//TODO implement this
		//Session should look like closed session
		
		______TS("Closed) session");
		
		submitPage = loginToStudentFeedbackSubmitPage("Alice", "Closed Session");
		submitPage.verifyHtml("/studentFeedbackSubmitPageClosed.html");
		
		______TS("Empty session");
		
		submitPage = loginToStudentFeedbackSubmitPage("Alice", "Empty Session");
		submitPage.verifyHtml("/studentFeedbackSubmitPageEmpty.html");
	}
	
	private void testSubmitAction(){
		
		______TS("create new responses");

		submitPage = loginToStudentFeedbackSubmitPage("Alice", "Open Session");
		
		submitPage.fillQuestionTextBox(1, 0, "Test Self Feedback");
		submitPage.fillQuestionTextBox(2, 0, "Response to Benny.");
		submitPage.selectRecipient(2, 1, "Drop out");
		submitPage.fillQuestionTextBox(2, 1, "Response to student who is going to drop out.");
		submitPage.selectRecipient(2, 2, "Extra guy");
		submitPage.fillQuestionTextBox(2, 2, "Response to extra guy.");
		
		// Test partial response for question		
		submitPage.fillQuestionTextBox(4, 1, "Feedback to team 3");
		submitPage.chooseMcqOption(7, 0, "Algo");
		submitPage.toggleMsqOption(9, 0, "UI");
		submitPage.toggleMsqOption(9, 0, "Design");
		
		// Just check that some of the responses persisted.
		FeedbackQuestionAttributes fq =
				BackDoor.getFeedbackQuestion("SFSubmitUiT.CS2104",
						"First Session", 2);
		FeedbackQuestionAttributes fqPartial =
				BackDoor.getFeedbackQuestion("SFSubmitUiT.CS2104",
						"First Session", 4);
		FeedbackQuestionAttributes fqMcq =
				BackDoor.getFeedbackQuestion("SFSubmitUiT.CS2104",
						"First Session", 8);
		FeedbackQuestionAttributes fqMsq =
				BackDoor.getFeedbackQuestion("SFSubmitUiT.CS2104",
						"First Session", 10);
		
		assertNull(BackDoor.getFeedbackResponse(fq.getId(),
				"SFSubmitUiT.alice.b@gmail.com",
				"SFSubmitUiT.benny.c@gmail.com"));
		assertNull(BackDoor.getFeedbackResponse(fqPartial.getId(),
				"SFSubmitUiT.alice.b@gmail.com",
				"Team 3"));
		assertNull(BackDoor.getFeedbackResponse(fqMcq.getId(),
				"SFSubmitUiT.alice.b@gmail.com",
				"Team 2"));
		assertNull(BackDoor.getFeedbackResponse(fqMsq.getId(),
				"SFSubmitUiT.alice.b@gmail.com",
				"Team 2"));

		submitPage.clickSubmitButton();

		assertEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED,
				submitPage.getStatus());
		
		assertNotNull(BackDoor.getFeedbackResponse(fq.getId(),
				"SFSubmitUiT.alice.b@gmail.com",
				"SFSubmitUiT.benny.c@gmail.com"));
		assertNotNull(BackDoor.getFeedbackResponse(fqPartial.getId(),
				"SFSubmitUiT.alice.b@gmail.com",
				"Team 3"));
		assertNotNull(BackDoor.getFeedbackResponse(fqMcq.getId(),
				"SFSubmitUiT.alice.b@gmail.com",
				"Team 2"));
		assertNotNull(BackDoor.getFeedbackResponse(fqMsq.getId(),
				"SFSubmitUiT.alice.b@gmail.com",
				"Team 2"));
		
		submitPage = loginToStudentFeedbackSubmitPage("Alice", "Open Session");
		submitPage.verifyHtml("/studentFeedbackSubmitPagePartiallyFilled.html");
		
		______TS("edit existing response");		
		
		// Test editing an existing response 
		// + fill up rest of responses at the same time
		String editedResponse = "Edited response to Benny.";
		submitPage.fillQuestionTextBox(2, 0, editedResponse);
		submitPage.fillQuestionTextBox(3, 0, "Feedback to instructors");
		submitPage.fillQuestionTextBox(4, 1, "Feedback to team 2.");
		submitPage.fillQuestionTextBox(5, 0, "Feedback to teammate.");
		
		submitPage.chooseMcqOption(6, 0, "UI");
		submitPage.chooseMcqOption(7, 0, "UI"); // Changed from "Algo" to "UI"
		submitPage.chooseMcqOption(7, 1, "UI");
		
		submitPage.toggleMsqOption(8, 0, "UI");
		submitPage.toggleMsqOption(8, 0, "Algo");
		submitPage.toggleMsqOption(8, 0, "Design");
		submitPage.toggleMsqOption(9, 0, "UI");
		submitPage.toggleMsqOption(9, 0, "Algo");
		submitPage.toggleMsqOption(9, 0, "Design");
		submitPage.toggleMsqOption(9, 1, "Design");
		
		// Just check the edited responses, and one new response.
		assertNull(BackDoor.getFeedbackResponse(fq.getId(),
				"SFSubmitUiT.alice.b@gmail.com",
				"Team 2"));
		
		submitPage.clickSubmitButton();

		assertEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED,
				submitPage.getStatus());
		assertEquals(editedResponse,
				BackDoor.getFeedbackResponse(fq.getId(),
					"SFSubmitUiT.alice.b@gmail.com",
					"SFSubmitUiT.benny.c@gmail.com").responseMetaData.getValue());
		fq = BackDoor.getFeedbackQuestion(
				"SFSubmitUiT.CS2104",
				"First Session", 4);
		assertNotNull(BackDoor.getFeedbackResponse(fq.getId(),
				"SFSubmitUiT.alice.b@gmail.com",
				"Team 2"));
		
		assertEquals("UI",
				BackDoor.getFeedbackResponse(fqMcq.getId(),
					"SFSubmitUiT.alice.b@gmail.com",
					"Team 2").getResponseDetails().getAnswerString());
		
		FeedbackMsqResponseDetails frMsq = 
				(FeedbackMsqResponseDetails) BackDoor.getFeedbackResponse(fqMsq.getId(),
						"SFSubmitUiT.alice.b@gmail.com",
						"Team 2").getResponseDetails();
		assertFalse(frMsq.contains("UI"));
		assertTrue(frMsq.contains("Algo"));
		assertFalse(frMsq.contains("Design"));
		
		submitPage = loginToStudentFeedbackSubmitPage("Alice", "Open Session");
		submitPage.verifyHtml("/studentFeedbackSubmitPageFullyFilled.html");
	}
	
	private void testModifyData() throws EnrollException{
		
		//TODO: This should be tested at Logic level instead?
		
		______TS("modify data");
		
		// Next, we edit some student data to cover editing of students
		// after creating the responses.

		// move one student out of Team 2 into a new team
		// This should cause the page to render an extra response box for
		// the team question.
		StudentAttributes extraGuy = testData.students.get("ExtraGuy");
		moveToTeam(extraGuy, "New Team");

		// delete one student
		// This should remove (hide on page render; not deleted) the response made to him,
		// and change the number of options in the recipient dropdown list.
		StudentAttributes dropOutGuy = testData.students.get("DropOut");
		String backDoorOperationStatus = BackDoor.deleteStudent(dropOutGuy.course,
				dropOutGuy.email);
		assertEquals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS, backDoorOperationStatus);

		// move Benny out of team 1 into team 2 and change his email
		// This should cause the team mates question to disappear completely as 
		// no one else is in Team 1, but other responses to Benny should remain.
		StudentAttributes Benny = testData.students.get("Benny");
		moveToTeam(Benny, "Team 2");
		
		submitPage = loginToStudentFeedbackSubmitPage("Alice", "Open Session");
		submitPage.verifyHtml("/studentFeedbackSubmitPageModified.html");
		
	}

	private FeedbackSubmitPage loginToStudentFeedbackSubmitPage(
			String studentName, String fsName) {
		Url editUrl = createUrl(Const.ActionURIs.STUDENT_FEEDBACK_SUBMISSION_EDIT_PAGE)
				.withUserId(testData.students.get(studentName).googleId)
				.withCourseId(testData.feedbackSessions.get(fsName).courseId)
				.withSessionName(testData.feedbackSessions.get(fsName).feedbackSessionName);
		return loginAdminToPage(browser, editUrl, FeedbackSubmitPage.class);
	}

	private void moveToTeam(StudentAttributes student, String newTeam) {
		String backDoorOperationStatus;
		student.team = newTeam;
		backDoorOperationStatus = BackDoor.editStudent(student.email, student);
		assertEquals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS, backDoorOperationStatus);
	}

	@AfterClass
	public static void classTearDown() throws Exception {
		BrowserPool.release(browser);
	}

}