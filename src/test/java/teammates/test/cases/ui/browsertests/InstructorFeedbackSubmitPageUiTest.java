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
import teammates.common.datatransfer.FeedbackNumericalScaleResponseDetails;
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
 * Tests 'Submit Feedback' view of instructors.
 * SUT: {@link FeedbackSubmitPage}.
 */
public class InstructorFeedbackSubmitPageUiTest extends BaseUiTestCase {

	private static DataBundle testData;
	private static Browser browser;
	private FeedbackSubmitPage submitPage;
	
	
	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader();
		testData = loadDataBundle("/InstructorFeedbackSubmitPageUiTest.json");
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
		
		submitPage = loginToInstructorFeedbackSubmitPage("IFSubmitUiT.instr", "Awaiting Session");
		submitPage.verifyHtml("/instructorFeedbackSubmitPageAwaiting.html");
		
		______TS("Open session");
		
		submitPage = loginToInstructorFeedbackSubmitPage("IFSubmitUiT.instr", "Open Session");
		submitPage.verifyHtml("/instructorFeedbackSubmitPageOpen.html");
		
		______TS("Grace period session");
		
		//TODO implement this
		//Session should look like closed session
		
		______TS("Closed) session");
		
		submitPage = loginToInstructorFeedbackSubmitPage("IFSubmitUiT.instr", "Closed Session");
		submitPage.verifyHtml("/instructorFeedbackSubmitPageClosed.html");
		
		______TS("Empty session");
		
		submitPage = loginToInstructorFeedbackSubmitPage("IFSubmitUiT.instr", "Empty Session");				
		submitPage.verifyHtml("/instructorFeedbackSubmitPageEmpty.html");
		
		______TS("Private session");
		
		submitPage = loginToInstructorFeedbackSubmitPage("IFSubmitUiT.instr", "Private Session");
		submitPage.verifyHtml("/instructorFeedbackSubmitPagePrivate.html");
	
	}
	
	private void testSubmitAction(){
		
		______TS("create new responses");

		submitPage = loginToInstructorFeedbackSubmitPage("IFSubmitUiT.instr", "Open Session");
		
		submitPage.fillResponseTextBox(1, 0, "Test Self Feedback");
		submitPage.fillResponseTextBox(2, 0, "Response to Alice.");
		submitPage.selectRecipient(2, 1, "Drop out");
		submitPage.fillResponseTextBox(2, 1, "Response to student who is going to drop out.");
		submitPage.selectRecipient(2, 2, "Extra guy");
		submitPage.fillResponseTextBox(2, 2, "Response to extra guy.");
		submitPage.fillResponseTextBox(13, 0, "1");
		
		// Test partial response for question		
		submitPage.fillResponseTextBox(4, 1, "Feedback to Instructor 3");
		submitPage.chooseMcqOption(6, 0, "Algo");
		submitPage.toggleMsqOption(8, 0, "UI");
		submitPage.toggleMsqOption(8, 0, "Design");
		
		// Just check that some of the responses persisted.
		FeedbackQuestionAttributes fq =
				BackDoor.getFeedbackQuestion("IFSubmitUiT.CS2104",
						"First Session", 2);
		FeedbackQuestionAttributes fqPartial =
				BackDoor.getFeedbackQuestion("IFSubmitUiT.CS2104",
						"First Session", 6);
		FeedbackQuestionAttributes fqMcq =
				BackDoor.getFeedbackQuestion("IFSubmitUiT.CS2104",
						"First Session", 8);
		FeedbackQuestionAttributes fqMsq =
				BackDoor.getFeedbackQuestion("IFSubmitUiT.CS2104",
						"First Session", 10);
		FeedbackQuestionAttributes fqNumscale =
				BackDoor.getFeedbackQuestion("IFSubmitUiT.CS2104",
						"First Session", 15);
		
		assertNull(BackDoor.getFeedbackResponse(fq.getId(),
				"IFSubmitUiT.instr@gmail.com",
				"IFSubmitUiT.alice.b@gmail.com"));
		assertNull(BackDoor.getFeedbackResponse(fqPartial.getId(),
				"IFSubmitUiT.instr@gmail.com",
				"IFSubmitUiT.instr2@gmail.com"));
		assertNull(BackDoor.getFeedbackResponse(fqMcq.getId(),
				"IFSubmitUiT.instr@gmail.com",
				"IFSubmitUiT.instr2@gmail.com"));
		assertNull(BackDoor.getFeedbackResponse(fqMsq.getId(),
				"IFSubmitUiT.instr@gmail.com",
				"IFSubmitUiT.instr2@gmail.com"));
		assertNull(BackDoor.getFeedbackResponse(fqNumscale.getId(),
				"IFSubmitUiT.instr@gmail.com",
				"IFSubmitUiT.instr@gmail.com"));

		submitPage.clickSubmitButton();

		assertEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED,
				submitPage.getStatus());
		
		assertNotNull(BackDoor.getFeedbackResponse(fq.getId(),
				"IFSubmitUiT.instr@gmail.com",
				"IFSubmitUiT.alice.b@gmail.com"));
		assertNotNull(BackDoor.getFeedbackResponse(fqPartial.getId(),
				"IFSubmitUiT.instr@gmail.com",
				"IFSubmitUiT.instr3@gmail.com"));
		assertNotNull(BackDoor.getFeedbackResponse(fqMcq.getId(),
				"IFSubmitUiT.instr@gmail.com",
				"IFSubmitUiT.instr2@gmail.com"));
		assertNotNull(BackDoor.getFeedbackResponse(fqMsq.getId(),
				"IFSubmitUiT.instr@gmail.com",
				"IFSubmitUiT.instr2@gmail.com"));
		assertNotNull(BackDoor.getFeedbackResponse(fqNumscale.getId(),
				"IFSubmitUiT.instr@gmail.com",
				"IFSubmitUiT.instr@gmail.com"));
		
		submitPage = loginToInstructorFeedbackSubmitPage("IFSubmitUiT.instr", "Open Session");
		submitPage.verifyHtml("/instructorFeedbackSubmitPagePartiallyFilled.html");
				
		______TS("edit existing response");		
		
		// Test editing an existing response 
		// + fill up rest of responses at the same time
		String editedResponse = "Edited response to Alice.";
		submitPage.fillResponseTextBox(2, 0, editedResponse);
		submitPage.fillResponseTextBox(3, 0, "Feedback to instructors");
		submitPage.fillResponseTextBox(4, 1, "Feedback to instructor 2.");
		submitPage.fillResponseTextBox(4, 2, "Feedback to instructor 4.");
		submitPage.fillResponseTextBox(4, 3, "Feedback to instructor 5.");
		
		submitPage.chooseMcqOption(5, 0, "UI");
		submitPage.chooseMcqOption(6, 0, "UI"); // Changed from "Algo" to "UI"
		submitPage.chooseMcqOption(6, 1, "UI");
		submitPage.chooseMcqOption(6, 2, "UI");
		
		submitPage.toggleMsqOption(7, 0, "UI");
		submitPage.toggleMsqOption(7, 0, "Algo");
		submitPage.toggleMsqOption(7, 0, "Design");
		submitPage.toggleMsqOption(8, 0, "UI");
		submitPage.toggleMsqOption(8, 0, "Algo");
		submitPage.toggleMsqOption(8, 0, "Design");
		submitPage.toggleMsqOption(8, 1, "Design");
		submitPage.toggleMsqOption(8, 2, "UI");
		
		submitPage.chooseMcqOption(9, 0, "Drop out (Team 2)");
		submitPage.toggleMsqOption(10, 0, "Alice Betsy (Team 1)");
		submitPage.toggleMsqOption(10, 0, "Benny Charles (Team 1)");
		submitPage.toggleMsqOption(10, 0, "Charlie Davis (Team 2)");
		submitPage.toggleMsqOption(10, 0, "Extra guy (Team 2)");
		
		submitPage.chooseMcqOption(11, 0, "Team 2");
		submitPage.toggleMsqOption(12, 0, "Team 1");
		submitPage.toggleMsqOption(12, 0, "Team 3");
		
		submitPage.fillResponseTextBox(13, 0, "5"); 
		submitPage.fillResponseTextBox(14, 0, "1.5"); 
		submitPage.fillResponseTextBox(14, 1, "2"); 
		submitPage.fillResponseTextBox(14, 2, "3.5"); 
		
		// Just check the edited responses, and one new response.
		assertNull(BackDoor.getFeedbackResponse(fq.getId(),
				"IFSubmitUiT.instr@gmail.com",
				"Team 2"));
		
		submitPage.clickSubmitButton();

		assertEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED,
				submitPage.getStatus());
		assertEquals(editedResponse,
				BackDoor.getFeedbackResponse(fq.getId(),
					"IFSubmitUiT.instr@gmail.com",
					"IFSubmitUiT.alice.b@gmail.com").getResponseDetails().getAnswerString());
		
		fq = BackDoor.getFeedbackQuestion(
				"IFSubmitUiT.CS2104",
				"First Session", 7);
		assertNotNull(BackDoor.getFeedbackResponse(fq.getId(),
				"IFSubmitUiT.instr@gmail.com",
				"IFSubmitUiT.instr@gmail.com"));
		assertEquals("UI",
				BackDoor.getFeedbackResponse(fqMcq.getId(),
					"IFSubmitUiT.instr@gmail.com",
					"IFSubmitUiT.instr2@gmail.com").getResponseDetails().getAnswerString());
		
		FeedbackMsqResponseDetails frMsq = 
				(FeedbackMsqResponseDetails) BackDoor.getFeedbackResponse(fqMsq.getId(),
						"IFSubmitUiT.instr@gmail.com",
						"IFSubmitUiT.instr2@gmail.com").getResponseDetails();
		assertFalse(frMsq.contains("UI"));
		assertTrue(frMsq.contains("Algo"));
		assertFalse(frMsq.contains("Design"));
		
		FeedbackNumericalScaleResponseDetails frNumscale = 
				(FeedbackNumericalScaleResponseDetails) BackDoor.getFeedbackResponse(fqNumscale.getId(),
						"IFSubmitUiT.instr@gmail.com",
						"IFSubmitUiT.instr@gmail.com").getResponseDetails();
		assertEquals("5", frNumscale.getAnswerString());
		
		submitPage = loginToInstructorFeedbackSubmitPage("IFSubmitUiT.instr", "Open Session");
		submitPage.verifyHtml("/instructorFeedbackSubmitPageFullyFilled.html");
	}
	
	private void testModifyData() throws EnrollException{
		
		//TODO: This should be tested at Logic level instead?
		
		______TS("modify data");
		
		// Next, we edit some student data to cover editing of students
		// after creating the responses.

		// move one student out of Team 2 into a new team
		// This should not cause the existing response to disappear
		StudentAttributes extraGuy = testData.students.get("ExtraGuy");
		moveToTeam(extraGuy, "New Team");

		// delete one student
		// This should remove (hide on page render; not deleted) the response made to him,
		// and change the number of options in the recipient dropdown list.
		StudentAttributes dropOutGuy = testData.students.get("DropOut");
		String backDoorOperationStatus = BackDoor.deleteStudent(dropOutGuy.course,
				dropOutGuy.email);
		assertEquals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS, backDoorOperationStatus);

		submitPage = loginToInstructorFeedbackSubmitPage("IFSubmitUiT.instr", "Open Session");
		submitPage.verifyHtml("/instructorFeedbackSubmitPageModified.html");
	}

	private FeedbackSubmitPage loginToInstructorFeedbackSubmitPage(
			String instructorName, String fsName) {
		Url editUrl = createUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_SUBMISSION_EDIT_PAGE)
				.withUserId(testData.instructors.get(instructorName).googleId)
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