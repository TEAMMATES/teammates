package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.*;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.common.util.Url;
import teammates.test.driver.AssertHelper;
import teammates.test.driver.BackDoor;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.InstructorFeedbackEditPage;
import teammates.test.pageobjects.InstructorFeedbacksPage;

/**
 * Covers the 'Edit Feedback Session' page for instructors. 
 * SUT is {@link InstructorFeedbackEditPage}.
 */
public class InstructorFeedbackEditPageUiTest extends BaseUiTestCase {
	private static Browser browser;
	private static InstructorFeedbackEditPage feedbackEditPage;
	private static DataBundle testData;
	private static String instructorId;
	private static String courseId;
	private static String feedbackSessionName;
	/** This contains data for the feedback session to be edited during testing */
	private static FeedbackSessionAttributes editedSession;
	
	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader();
		testData = loadDataBundle("/InstructorFeedbackEditPageUiTest.json");
		restoreTestDataOnServer(testData);
		
		editedSession = testData.feedbackSessions.get("openSession");
		editedSession.gracePeriod = 30;
		editedSession.sessionVisibleFromTime = Const.TIME_REPRESENTS_FOLLOW_OPENING;
		editedSession.resultsVisibleFromTime = Const.TIME_REPRESENTS_LATER;
		editedSession.instructions = new Text("Please fill in the edited feedback session.");
		editedSession.endTime = TimeHelper.convertToDate("2014-05-01 10:00 PM UTC");
		
		instructorId = testData.accounts.get("instructorWithSessions").googleId;
		courseId = testData.courses.get("course").id;
		feedbackSessionName = testData.feedbackSessions.get("openSession").feedbackSessionName;
		
		browser = BrowserPool.getBrowser();
		
	}
	
	@Test
	public void allTests() throws Exception{
		testContent();
		
		testEditSessionLink();	
		testEditSessionAction();
		
		testNewQuestionLink();
		testInputValidationForQuestion();
		testAddQuestionAction();
		
		testEditQuestionLink();
		testEditQuestionAction();
		
		testDeleteQuestionAction();
		
		testDeleteSessionAction();
	}

	private void testContent() throws Exception{
		
		______TS("no questions");
		
		feedbackEditPage = getFeedbackEditPage();
		feedbackEditPage.verifyHtml("/instructorFeedbackEditEmpty.html");
	}
	
	private void testEditSessionLink(){
		______TS("edit session link");
		assertEquals(true, feedbackEditPage.clickEditSessionButton());		
	}

	private void testEditSessionAction() throws Exception{
		
		______TS("typical success case");
		
		feedbackEditPage.clickManualPublishTimeButton();
		feedbackEditPage.clickDefaultVisibleTimeButton();
		feedbackEditPage.editFeedbackSession(editedSession.startTime, editedSession.endTime,
				editedSession.instructions,
				editedSession.gracePeriod);
		feedbackEditPage.verifyStatus(Const.StatusMessages.FEEDBACK_SESSION_EDITED);
		FeedbackSessionAttributes savedSession = 
				BackDoor.getFeedbackSession(editedSession.courseId, editedSession.feedbackSessionName);
		assertEquals(editedSession.toString(), savedSession.toString());
		feedbackEditPage.verifyHtml("/instructorFeedbackEditSuccess.html");
		
		
		______TS("test edit page after manual publish");
		
		// Do a backdoor 'manual' publish.
		editedSession.resultsVisibleFromTime = Const.TIME_REPRESENTS_NOW;
		String status = BackDoor.editFeedbackSession(editedSession);
		assertEquals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS, status);
		
		feedbackEditPage = getFeedbackEditPage();
		feedbackEditPage.verifyHtml("/instructorFeedbackEditPublished.html");
		// Restore defaults
		feedbackEditPage.clickEditSessionButton();
		feedbackEditPage.clickDefaultPublishTimeButton();
		feedbackEditPage.clickSaveSessionButton();

	}

	
	private void testNewQuestionLink() {
		
		______TS("new question (frame) link");
		
		assertEquals(true, feedbackEditPage.clickNewQuestionButton());		
	}
	
	private void testInputValidationForQuestion() {
		
		______TS("empty question text");
		
		feedbackEditPage.clickAddQuestionButton();
		assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_TEXTINVALID, feedbackEditPage.getStatus());
		
		______TS("empty number of max respondants field");
		
		feedbackEditPage.fillQuestionBox("filled qn");
		feedbackEditPage.selectRecipientsToBeStudents();
		feedbackEditPage.fillNumOfEntitiesToGiveFeedbackToBox("");
		feedbackEditPage.clickCustomNumberOfRecipientsButton();
		feedbackEditPage.clickAddQuestionButton();
		assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_NUMBEROFENTITIESINVALID, feedbackEditPage.getStatus());

	}

	private void testAddQuestionAction() {
		
		______TS("add question action success");

		feedbackEditPage.clickMaxNumberOfRecipientsButton();
		feedbackEditPage.clickAddQuestionButton();
		assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED, feedbackEditPage.getStatus());
		assertNotNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));
		feedbackEditPage.verifyHtml("/instructorFeedbackQuestionAddSuccess.html");
	}
	
	private void testEditQuestionLink() {
		
		______TS("edit question link");
		
		assertEquals(true, feedbackEditPage.clickEditQuestionButton());	
	}
	
	private void testEditQuestionAction() {
		
		______TS("edit question success");
		
		feedbackEditPage.fillEditQuestionBox("edited qn text");
		feedbackEditPage.clickSaveExistingQuestionButton();
		assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_EDITED, feedbackEditPage.getStatus());
		feedbackEditPage.verifyHtml("/instructorFeedbackQuestionEditSuccess.html");
	}

	private void testDeleteQuestionAction() {
		
		______TS("qn delete then cancel");
		
		feedbackEditPage.clickAndCancel(feedbackEditPage.getDeleteQuestionLink());		
		assertNotNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));
		
		______TS("qn delete then accept");
		
		feedbackEditPage.clickAndConfirm(feedbackEditPage.getDeleteQuestionLink());
		assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_DELETED, feedbackEditPage.getStatus());
		assertNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));
	}
	
	private void testDeleteSessionAction() {
		
		______TS("session delete then cancel");
		
		feedbackEditPage.clickAndCancel(feedbackEditPage.getDeleteSessionLink());		
		assertNotNull(BackDoor.getFeedbackSession(courseId, feedbackSessionName));
		
		______TS("session delete then accept");
		
		// check redirect to main feedback page
		InstructorFeedbacksPage feedbackPage = feedbackEditPage.deleteSession();
		AssertHelper.assertContains(Const.StatusMessages.FEEDBACK_SESSION_DELETED, feedbackPage.getStatus());
		assertNull(BackDoor.getFeedbackSession(courseId, feedbackSessionName));
		
	}


	@AfterClass
	public static void classTearDown() throws Exception {
		BrowserPool.release(browser);
	}

	private InstructorFeedbackEditPage getFeedbackEditPage() {		
		Url feedbackPageLink = createUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE).
				withUserId(instructorId).withCourseId(courseId).withSessionName(feedbackSessionName);
		return loginAdminToPage(browser, feedbackPageLink, InstructorFeedbackEditPage.class);
	}

}