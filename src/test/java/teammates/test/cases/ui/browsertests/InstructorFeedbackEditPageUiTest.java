package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNull;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.common.util.Url;
import teammates.test.driver.AssertHelper;
import teammates.test.driver.BackDoor;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.FeedbackSubmitPage;
import teammates.test.pageobjects.InstructorFeedbackEditPage;
import teammates.test.pageobjects.InstructorFeedbacksPage;

import com.google.appengine.api.datastore.Text;

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
		
		testNewMcqQuestionFrame();
		testInputValidationForMcqQuestion();
		testCustomizeMcqOptions();
		testAddMcqQuestionAction();
		testEditMcqQuestionAction();
		testDeleteMcqQuestionAction();
		
		testNewMsqQuestionFrame();
		testInputValidationForMsqQuestion();
		testCustomizeMsqOptions();
		testAddMsqQuestionAction();
		testEditMsqQuestionAction();
		testDeleteMsqQuestionAction();
		
		testNewNumScaleQuestionFrame();
		testInputValidationForNumScaleQuestion();
		testCustomizeNumScaleOptions();
		testAddNumScaleQuestionAction();
		testEditNumScaleQuestionAction();
		testDeleteNumScaleQuestionAction();
		
		testEditQuestionLink();
		testEditQuestionAction();
		testDeleteQuestionAction();
		
		testPreviewSessionAction();
		
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
	
	private void testNewMcqQuestionFrame() {
		
		______TS("MCQ: new question (frame) link");
		
		feedbackEditPage.selectNewQuestionType("Multiple-choice question");
		assertEquals(true, feedbackEditPage.clickNewQuestionButton());		
	}
	
	private void testInputValidationForMcqQuestion() {
		
		//TODO implement this
		
	}
	
	private void testCustomizeMcqOptions() {
		
		______TS("MCQ: add mcq option");
		
		feedbackEditPage.fillMcqOption(0, "Choice 1");
		feedbackEditPage.fillMcqOption(1, "Choice 2");
		
		assertEquals(false, feedbackEditPage.isElementPresent("mcqOptionRow-2"));
		feedbackEditPage.clickAddMoreMcqOptionLink();
		assertEquals(true, feedbackEditPage.isElementPresent("mcqOptionRow-2"));
		
		______TS("MCQ: remove mcq option");
		
		feedbackEditPage.fillMcqOption(2, "Choice 3");
		assertEquals(true, feedbackEditPage.isElementPresent("mcqOptionRow-1"));
		feedbackEditPage.clickRemoveMcqOptionLink(1, -1);
		assertEquals(false, feedbackEditPage.isElementPresent("mcqOptionRow-1"));
		
		______TS("MCQ: add mcq option after remove");
		
		feedbackEditPage.clickAddMoreMcqOptionLink();
		assertEquals(true, feedbackEditPage.isElementPresent("mcqOptionRow-3"));
		feedbackEditPage.clickAddMoreMcqOptionLink();
		feedbackEditPage.fillMcqOption(4, "Choice 5");
		assertEquals(true, feedbackEditPage.isElementPresent("mcqOptionRow-4"));
	}
	
	private void testAddMcqQuestionAction(){
				
		______TS("MCQ: add question action success");

		feedbackEditPage.fillQuestionBox("mcq qn");
		feedbackEditPage.clickAddQuestionButton();
		assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED, feedbackEditPage.getStatus());
		assertNotNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 2));
		feedbackEditPage.verifyHtml("/instructorFeedbackMcqQuestionAddSuccess.html");
	}
	
	private void testEditMcqQuestionAction() {
		
		______TS("MCQ: edit question success");

		assertEquals(true, feedbackEditPage.clickEditQuestionButton(2));	
		feedbackEditPage.fillEditQuestionBox("edited mcq qn text", 2);
		assertEquals(true, feedbackEditPage.isElementPresent("mcqOptionRow-0-2"));
		feedbackEditPage.clickRemoveMcqOptionLink(0, 2);
		assertEquals(false, feedbackEditPage.isElementPresent("mcqOptionRow-0-2"));
		feedbackEditPage.clickSaveExistingQuestionButton(2);
		assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_EDITED, feedbackEditPage.getStatus());
		
		feedbackEditPage.verifyHtml("/instructorFeedbackMcqQuestionEditSuccess.html");
		
		______TS("MCQ: edit to generated options");

		assertEquals(true, feedbackEditPage.clickEditQuestionButton(2));	
		feedbackEditPage.fillEditQuestionBox("generated mcq qn text", 2);
		assertEquals(true, feedbackEditPage.isElementVisible("mcqAddOptionLink"));
		feedbackEditPage.verifyFieldValue(
				Const.ParamsNames.FEEDBACK_QUESTION_GENERATEDOPTIONS, 
				FeedbackParticipantType.NONE.toString());
		assertEquals(false, feedbackEditPage.isElementEnabled("mcqGenerateForSelect-2"));
		feedbackEditPage.clickGenerateOptionsCheckbox(2);
		assertEquals(true, feedbackEditPage.isElementEnabled("mcqGenerateForSelect-2"));
		feedbackEditPage.verifyFieldValue(
				Const.ParamsNames.FEEDBACK_QUESTION_GENERATEDOPTIONS + "-2", 
				FeedbackParticipantType.STUDENTS.toString());
		assertEquals(false, feedbackEditPage.isElementVisible("mcqAddOptionLink"));
		
		feedbackEditPage.clickSaveExistingQuestionButton(2);
		assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_EDITED, feedbackEditPage.getStatus());
		assertEquals(false, feedbackEditPage.isElementPresent("mcqOptionRow-0-2"));
		assertEquals(false, feedbackEditPage.isElementEnabled("generateOptionsCheckbox-2"));
		assertEquals(true, feedbackEditPage.isElementSelected("generateOptionsCheckbox-2"));
		assertEquals(false, feedbackEditPage.isElementEnabled("mcqGenerateForSelect-2"));
		feedbackEditPage.verifyFieldValue(
				"mcqGenerateForSelect-2", 
				FeedbackParticipantType.STUDENTS.toString());
		feedbackEditPage.verifyFieldValue(
				Const.ParamsNames.FEEDBACK_QUESTION_GENERATEDOPTIONS + "-2", 
				FeedbackParticipantType.STUDENTS.toString());
		
		______TS("MCQ: change generated type");
		
		assertEquals(true, feedbackEditPage.clickEditQuestionButton(2));
		assertEquals(true, feedbackEditPage.isElementEnabled("generateOptionsCheckbox-2"));
		assertEquals(true, feedbackEditPage.isElementSelected("generateOptionsCheckbox-2"));
		assertEquals(true, feedbackEditPage.isElementEnabled("mcqGenerateForSelect-2"));
		feedbackEditPage.selectMcqGenerateOptionsFor("teams", 2);
		feedbackEditPage.verifyFieldValue(
				"mcqGenerateForSelect-2", 
				FeedbackParticipantType.TEAMS.toString());
		feedbackEditPage.verifyFieldValue(
				Const.ParamsNames.FEEDBACK_QUESTION_GENERATEDOPTIONS + "-2", 
				FeedbackParticipantType.TEAMS.toString());
		
	}
	
	private void testDeleteMcqQuestionAction() {
		
		______TS("MCQ: qn delete then cancel");
		
		feedbackEditPage.clickAndCancel(feedbackEditPage.getDeleteQuestionLink(2));		
		assertNotNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 2));
		
		______TS("MCQ: qn delete then accept");
		
		feedbackEditPage.clickAndConfirm(feedbackEditPage.getDeleteQuestionLink(2));
		assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_DELETED, feedbackEditPage.getStatus());
		assertNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 2));
	}
	
	private void testNewMsqQuestionFrame() {
		
		______TS("MSQ: new question (frame) link");
		
		feedbackEditPage.selectNewQuestionType("Multiple-select question");
		assertEquals(true, feedbackEditPage.clickNewQuestionButton());
	}
	
	private void testInputValidationForMsqQuestion() {
		
		//TODO implement this
		
	}
	
	private void testCustomizeMsqOptions() {
		
		______TS("MSQ: add msq option");
		
		feedbackEditPage.fillMsqOption(0, "Choice 1");
		feedbackEditPage.fillMsqOption(1, "Choice 2");
		
		assertEquals(false, feedbackEditPage.isElementPresent("msqOptionRow-2"));
		feedbackEditPage.clickAddMoreMsqOptionLink();
		assertEquals(true, feedbackEditPage.isElementPresent("msqOptionRow-2"));
		
		______TS("MSQ: remove msq option");
		
		feedbackEditPage.fillMsqOption(2, "Choice 3");
		assertEquals(true, feedbackEditPage.isElementPresent("msqOptionRow-1"));
		feedbackEditPage.clickRemoveMsqOptionLink(1, -1);
		assertEquals(false, feedbackEditPage.isElementPresent("msqOptionRow-1"));
		
		______TS("MSQ: add msq option after remove");
		
		feedbackEditPage.clickAddMoreMsqOptionLink();
		assertEquals(true, feedbackEditPage.isElementPresent("msqOptionRow-3"));
		feedbackEditPage.clickAddMoreMsqOptionLink();
		feedbackEditPage.fillMsqOption(4, "Choice 5");
		assertEquals(true, feedbackEditPage.isElementPresent("msqOptionRow-4"));
	}
	
	private void testAddMsqQuestionAction(){
				
		______TS("MSQ: add question action success");

		feedbackEditPage.fillQuestionBox("msq qn");
		assertNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 2));
		feedbackEditPage.clickAddQuestionButton();
		assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED, feedbackEditPage.getStatus());
		assertNotNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 2));
		feedbackEditPage.verifyHtml("/instructorFeedbackMsqQuestionAddSuccess.html");
	}
	
	private void testEditMsqQuestionAction() {
		
		______TS("MSQ: edit question success");

		assertEquals(true, feedbackEditPage.clickEditQuestionButton(2));	
		feedbackEditPage.fillEditQuestionBox("edited msq qn text", 2);
		assertEquals(true, feedbackEditPage.isElementPresent("msqOptionRow-0-2"));
		feedbackEditPage.clickRemoveMsqOptionLink(0, 2);
		assertEquals(false, feedbackEditPage.isElementPresent("msqOptionRow-0-2"));
		feedbackEditPage.clickSaveExistingQuestionButton(2);
		assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_EDITED, feedbackEditPage.getStatus());
		
		feedbackEditPage.verifyHtml("/instructorFeedbackMsqQuestionEditSuccess.html");
		
		______TS("MSQ: edit to generated options");

		assertEquals(true, feedbackEditPage.clickEditQuestionButton(2));	
		feedbackEditPage.fillEditQuestionBox("generated msq qn text", 2);
		assertEquals(true, feedbackEditPage.isElementVisible("msqAddOptionLink"));
		feedbackEditPage.verifyFieldValue(
				Const.ParamsNames.FEEDBACK_QUESTION_GENERATEDOPTIONS, 
				FeedbackParticipantType.NONE.toString());
		assertEquals(false, feedbackEditPage.isElementEnabled("msqGenerateForSelect-2"));
		feedbackEditPage.clickGenerateOptionsCheckbox(2);
		assertEquals(true, feedbackEditPage.isElementEnabled("msqGenerateForSelect-2"));
		feedbackEditPage.verifyFieldValue(
				Const.ParamsNames.FEEDBACK_QUESTION_GENERATEDOPTIONS + "-2", 
				FeedbackParticipantType.STUDENTS.toString());
		assertEquals(false, feedbackEditPage.isElementVisible("msqAddOptionLink"));
		
		feedbackEditPage.clickSaveExistingQuestionButton(2);
		assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_EDITED, feedbackEditPage.getStatus());
		assertEquals(false, feedbackEditPage.isElementPresent("msqOptionRow-0-2"));
		assertEquals(false, feedbackEditPage.isElementEnabled("generateOptionsCheckbox-2"));
		assertEquals(true, feedbackEditPage.isElementSelected("generateOptionsCheckbox-2"));
		assertEquals(false, feedbackEditPage.isElementEnabled("msqGenerateForSelect-2"));
		feedbackEditPage.verifyFieldValue(
				"msqGenerateForSelect-2", 
				FeedbackParticipantType.STUDENTS.toString());
		feedbackEditPage.verifyFieldValue(
				Const.ParamsNames.FEEDBACK_QUESTION_GENERATEDOPTIONS + "-2", 
				FeedbackParticipantType.STUDENTS.toString());
		
		______TS("MSQ: change generated type");
		
		assertEquals(true, feedbackEditPage.clickEditQuestionButton(2));
		assertEquals(true, feedbackEditPage.isElementEnabled("generateOptionsCheckbox-2"));
		assertEquals(true, feedbackEditPage.isElementSelected("generateOptionsCheckbox-2"));
		assertEquals(true, feedbackEditPage.isElementEnabled("msqGenerateForSelect-2"));
		feedbackEditPage.selectMsqGenerateOptionsFor("teams", 2);
		feedbackEditPage.verifyFieldValue(
				"msqGenerateForSelect-2", 
				FeedbackParticipantType.TEAMS.toString());
		feedbackEditPage.verifyFieldValue(
				Const.ParamsNames.FEEDBACK_QUESTION_GENERATEDOPTIONS + "-2", 
				FeedbackParticipantType.TEAMS.toString());
		
	}
	
	private void testDeleteMsqQuestionAction() {
		
		______TS("MSQ: qn delete then cancel");
		
		feedbackEditPage.clickAndCancel(feedbackEditPage.getDeleteQuestionLink(2));		
		assertNotNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 2));
		
		______TS("MSQ: qn delete then accept");
		
		feedbackEditPage.clickAndConfirm(feedbackEditPage.getDeleteQuestionLink(2));
		assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_DELETED, feedbackEditPage.getStatus());
		assertNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 2));
	}
	
	private void testNewNumScaleQuestionFrame() {
		______TS("NUMSCALE: new question (frame) link");
		
		feedbackEditPage.selectNewQuestionType("Numerical-scale question");
		assertEquals(true, feedbackEditPage.clickNewQuestionButton());
	}
	
	private void testInputValidationForNumScaleQuestion() {
		//TODO implement this
	}
	
	private void testCustomizeNumScaleOptions() {
		feedbackEditPage.fillQuestionBox("NumScale qn");
		assertEquals("[Possible values: 1, 1.5, 2, ..., 4, 4.5, 5]", 
				feedbackEditPage.getNumScalePossibleValuesString(-1));
		feedbackEditPage.fillStepNumScaleBox(0.3, -1);
		assertEquals("[The interval 1 - 5 is not divisible by the specified increment.]", 
				feedbackEditPage.getNumScalePossibleValuesString(-1));
		feedbackEditPage.fillMinNumScaleBox(1, -1);
		feedbackEditPage.fillMaxNumScaleBox(6, -1);
		feedbackEditPage.fillStepNumScaleBox(1, -1);
		assertEquals("[Possible values: 1, 2, 3, 4, 5, 6]", 
				feedbackEditPage.getNumScalePossibleValuesString(-1));
		feedbackEditPage.fillMinNumScaleBox(0, -1);
		feedbackEditPage.fillMaxNumScaleBox(1, -1);
		feedbackEditPage.fillStepNumScaleBox(0.1, -1);
		assertEquals("[Possible values: 0, 0.1, 0.2, ..., 0.8, 0.9, 1]", 
				feedbackEditPage.getNumScalePossibleValuesString(-1));
	}
	
	private void testAddNumScaleQuestionAction() {
		______TS("NUMSCALE: add question action success");

		assertNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 2));
		feedbackEditPage.clickAddQuestionButton();
		assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED, feedbackEditPage.getStatus());
		assertNotNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 2));
		feedbackEditPage.verifyHtml("/instructorFeedbackNumScaleQuestionAddSuccess.html");
	}
	
	private void testEditNumScaleQuestionAction() {
		______TS("NUMSCALE: edit question success");

		assertEquals(true, feedbackEditPage.clickEditQuestionButton(2));	
		feedbackEditPage.fillEditQuestionBox("edited numscale qn text", 2);
		feedbackEditPage.fillMinNumScaleBox(1, 2);
		feedbackEditPage.fillMaxNumScaleBox(5, 2);
		feedbackEditPage.fillStepNumScaleBox(1, 2);
		assertEquals("[Possible values: 1, 2, 3, 4, 5]", 
				feedbackEditPage.getNumScalePossibleValuesString(2));
		feedbackEditPage.clickSaveExistingQuestionButton(2);
		assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_EDITED, feedbackEditPage.getStatus());
		
		feedbackEditPage.verifyHtml("/instructorFeedbackNumScaleQuestionEditSuccess.html");	
	}
	
	private void testDeleteNumScaleQuestionAction() {
		______TS("NUMSCALE: qn delete then cancel");
		
		feedbackEditPage.clickAndCancel(feedbackEditPage.getDeleteQuestionLink(2));		
		assertNotNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 2));
		
		______TS("NUMSCALE: qn delete then accept");
		
		feedbackEditPage.clickAndConfirm(feedbackEditPage.getDeleteQuestionLink(2));
		assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_DELETED, feedbackEditPage.getStatus());
		assertNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 2));
	}

	private void testEditQuestionLink() {
		
		______TS("edit question link");
		
		assertEquals(true, feedbackEditPage.clickEditQuestionButton(1));	
	}
	
	private void testEditQuestionAction() {
		
		______TS("edit question success");
		
		feedbackEditPage.fillEditQuestionBox("edited qn text", 1);
		feedbackEditPage.clickSaveExistingQuestionButton(1);
		assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_EDITED, feedbackEditPage.getStatus());
		feedbackEditPage.verifyHtml("/instructorFeedbackQuestionEditSuccess.html");
	}

	private void testDeleteQuestionAction() {
		
		______TS("qn delete then cancel");
		
		feedbackEditPage.clickAndCancel(feedbackEditPage.getDeleteQuestionLink(1));		
		assertNotNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));
		
		______TS("qn delete then accept");
		
		feedbackEditPage.clickAndConfirm(feedbackEditPage.getDeleteQuestionLink(1));
		assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_DELETED, feedbackEditPage.getStatus());
		assertNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));
	}

	private void testPreviewSessionAction() {
		
		// add questions for previewing
		assertEquals(true, feedbackEditPage.clickNewQuestionButton());
		feedbackEditPage.fillQuestionBox("question for me");
		feedbackEditPage.clickAddQuestionButton();
		
		assertEquals(true, feedbackEditPage.clickNewQuestionButton());
		feedbackEditPage.fillQuestionBox("question for students");
		feedbackEditPage.selectGiverToBeStudents();
		feedbackEditPage.clickAddQuestionButton();
		
		assertEquals(true, feedbackEditPage.clickNewQuestionButton());
		feedbackEditPage.fillQuestionBox("question for instructors");
		feedbackEditPage.selectGiverToBeInstructors();
		feedbackEditPage.clickAddQuestionButton();
		
		______TS("preview as student");
		
		FeedbackSubmitPage previewPage;
		previewPage = feedbackEditPage.clickPreviewAsStudentButton();
		previewPage.verifyHtml("/studentFeedbackSubmitPagePreview.html");
		previewPage.closeCurrentWindowAndSwitchToParentWindow();
		
		______TS("preview as instructor");
		
		previewPage = feedbackEditPage.clickPreviewAsInstructorButton();
		previewPage.verifyHtml("/instructorFeedbackSubmitPagePreview.html");
		previewPage.closeCurrentWindowAndSwitchToParentWindow();
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