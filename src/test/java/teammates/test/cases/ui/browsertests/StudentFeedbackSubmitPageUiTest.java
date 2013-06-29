package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertNotNull;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.Common;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EnrollException;
import teammates.test.driver.BackDoor;
import teammates.test.driver.Url;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.StudentFeedbackSubmitPage;

/**
 * Tests 'Submit Feedback' view of students.
 * SUT: {@link StudentFeedbackSubmitPage}.
 */
public class StudentFeedbackSubmitPageUiTest extends BaseUiTestCase {

	private static DataBundle testData;
	private static Browser browser;
	private StudentFeedbackSubmitPage submitPage;
	
	
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
		
		______TS("open session");
		
		submitPage = loginToStudentFeedbackSubmitPage("Alice", "Open Session");
		submitPage.verifyHtml("/studentFeedbackSubmitPageOpen.html");
		
		______TS("awaiting (closed) session");
		
		submitPage = loginToStudentFeedbackSubmitPage("Alice", "Closed Session");
		submitPage.verifyHtml("/studentFeedbackSubmitPageClosed.html");
		
		______TS("empty session");
		
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
		submitPage.fillQuestionTextBox(3, 1, "Feedback to team 3");
		
		// Just check that one of the responses persisted.
		FeedbackQuestionAttributes fq =
				BackDoor.getFeedbackQuestion("SFSubmitUiT.CS2104",
						"First Session", 2);
		assertNull(BackDoor.getFeedbackResponse(fq.getId(),
				"SFSubmitUiT.alice.b@gmail.com",
				"SFSubmitUiT.benny.c@gmail.com"));

		submitPage.clickSubmitButton();

		assertEquals(Common.MESSAGE_FEEDBACK_RESPONSES_SAVED,
				submitPage.getStatus());
		assertNotNull(BackDoor.getFeedbackResponse(fq.getId(),
				"SFSubmitUiT.alice.b@gmail.com",
				"SFSubmitUiT.benny.c@gmail.com"));
		
		______TS("edit existing response");		
		
		submitPage = loginToStudentFeedbackSubmitPage("Alice", "Open Session");

		// Test editing an existing response 
		// + fill up rest of responses at the same time
		String editedResponse = "Edited response to Benny.";
		submitPage.fillQuestionTextBox(2, 0, editedResponse);
		submitPage.fillQuestionTextBox(3, 1, "Feedback to team 2.");
		submitPage.fillQuestionTextBox(4, 0, "Feedback to teammate.");
		
		// Just check the edited response, and one new response.
		assertNull(BackDoor.getFeedbackResponse(fq.getId(),
				"SFSubmitUiT.alice.b@gmail.com",
				"Team 2"));
		
		submitPage.clickSubmitButton();

		assertEquals(Common.MESSAGE_FEEDBACK_RESPONSES_SAVED,
				submitPage.getStatus());
		assertEquals(editedResponse,
				BackDoor.getFeedbackResponse(fq.getId(),
					"SFSubmitUiT.alice.b@gmail.com",
					"SFSubmitUiT.benny.c@gmail.com").answer.getValue());
		fq = BackDoor.getFeedbackQuestion(
				"SFSubmitUiT.CS2104",
				"First Session", 4);
		assertNotNull(BackDoor.getFeedbackResponse(fq.getId(),
				"SFSubmitUiT.alice.b@gmail.com",
				"Team 2"));

	}
	
	private void testModifyData() throws EnrollException{
		
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
		assertEquals(Common.BACKEND_STATUS_SUCCESS, backDoorOperationStatus);

		// move Benny out of team 1 into team 2 and change his email
		// This should cause the team mates question to disappear completely as 
		// noone else is in Team 1, but other responses to Benny should remain.
		StudentAttributes Benny = testData.students.get("Benny");
		moveToTeam(Benny, "Team 2");
		
		submitPage = loginToStudentFeedbackSubmitPage("Alice", "Open Session");
		submitPage.verifyHtml("/studentFeedbackSubmitPageModified.html");
		
	}

	@AfterClass
	public static void classTearDown() throws Exception {
		BrowserPool.release(browser);
	}
	
	private StudentFeedbackSubmitPage loginToStudentFeedbackSubmitPage(
			String studentName, String fsName) {
		Url editUrl = new Url(Common.PAGE_STUDENT_FEEDBACK_SUBMIT)
				.withUserId(testData.students.get(studentName).googleId)
				.withCourseId(testData.feedbackSessions.get(fsName).courseId)
				.withSessionName(testData.feedbackSessions.get(fsName).feedbackSessionName);
		return loginAdminToPage(browser, editUrl,
				StudentFeedbackSubmitPage.class);
	}

	private void moveToTeam(StudentAttributes student, String newTeam) {
		String backDoorOperationStatus;
		student.team = newTeam;
		backDoorOperationStatus = BackDoor.editStudent(student.email, student);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, backDoorOperationStatus);
	}

}