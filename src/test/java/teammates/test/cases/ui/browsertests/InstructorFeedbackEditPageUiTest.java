package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertTrue;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.util.Const;
import teammates.common.util.ThreadHelper;
import teammates.common.util.TimeHelper;
import teammates.common.util.Url;
import teammates.test.driver.AssertHelper;
import teammates.test.driver.BackDoor;
import teammates.test.driver.TestProperties;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.FeedbackQuestionSubmitPage;
import teammates.test.pageobjects.FeedbackSubmitPage;
import teammates.test.pageobjects.InstructorFeedbackEditPage;
import teammates.test.pageobjects.InstructorFeedbacksPage;
import teammates.test.util.Priority;

import com.google.appengine.api.datastore.Text;

/**
 * Covers the 'Edit Feedback Session' page for instructors.
 * SUT is {@link InstructorFeedbackEditPage}.
 */
@Priority(-1)
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
        removeAndRestoreTestDataOnServer(testData);

        editedSession = testData.feedbackSessions.get("openSession");
        editedSession.gracePeriod = 30;
        editedSession.sessionVisibleFromTime = Const.TIME_REPRESENTS_FOLLOW_OPENING;
        editedSession.resultsVisibleFromTime = Const.TIME_REPRESENTS_LATER;
        editedSession.instructions = new Text("Please fill in the edited feedback session.");
        editedSession.endTime = TimeHelper.convertToDate("2016-05-01 10:00 PM UTC");

        instructorId = testData.accounts.get("instructorWithSessions").googleId;
        courseId = testData.courses.get("course").id;
        feedbackSessionName = testData.feedbackSessions.get("openSession").feedbackSessionName;

        browser = BrowserPool.getBrowser();
        feedbackEditPage = getFeedbackEditPage();
    }

    @AfterClass
    public static void classTearDown() throws Exception {
        BrowserPool.release(browser);
    }

    @Test
    private void allTests() throws Exception {
        testContent();

        testEditSessionLink();
        testEditSessionAction();
        
        testGeneralQuestionOperations();

        testPreviewSessionAction();

        testDoneEditingLink();
        
        testDeleteSessionAction();
    }
    
    private void testGeneralQuestionOperations() {
        testCancelNewOrEditQuestion();
        
        testNewQuestionLink();
        testInputValidationForQuestion();
        testAddQuestionAction();

        testEditQuestionLink();
        testEditQuestionAction();

        testGetQuestionLink();
        testCopyQuestion();

        testChangeFeedbackRecipient();

        testEditQuestionNumberAction();

        testDeleteQuestionAction(2);
        testDeleteQuestionAction(1);
    }

    private void testContent() {

        ______TS("fresh new page");

        // This is the full HTML verification for Instructor Feedback Edit Page, the rest can all be verifyMainHtml
        feedbackEditPage.verifyHtml("/instructorFeedbackEditEmpty.html");

    }

    private void testEditSessionLink() {
        ______TS("edit session link");
        feedbackEditPage.clickEditSessionButton();
        assertTrue(feedbackEditPage.verifyEditSessionBoxIsEnabled());
        
    }

    private void testEditSessionAction() throws Exception {

        ______TS("typical success case");

        feedbackEditPage.clickEndDateBox();
        assertTrue(feedbackEditPage.verifyEndDatesBeforeTodayAreDisabled());
        feedbackEditPage.clickManualPublishTimeButton();
        feedbackEditPage.clickDefaultVisibleTimeButton();
        
        feedbackEditPage.editFeedbackSession(editedSession.startTime, editedSession.endTime,
                editedSession.instructions, editedSession.gracePeriod);
        
        feedbackEditPage.verifyStatus(Const.StatusMessages.FEEDBACK_SESSION_EDITED);
        FeedbackSessionAttributes savedSession = BackDoor.getFeedbackSession(
                editedSession.courseId, editedSession.feedbackSessionName);
        assertEquals(editedSession.toString(), savedSession.toString());
        feedbackEditPage.reloadPage();
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackEditSuccess.html");


        ______TS("test edit page not changed after manual publish");

        // Do a backdoor 'manual' publish.
        editedSession.resultsVisibleFromTime = Const.TIME_REPRESENTS_NOW;
        String status = BackDoor.editFeedbackSession(editedSession);
        assertEquals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS, status);

        feedbackEditPage = getFeedbackEditPage();

        // Ensuring that the settings did not default back to original values after manual publishing
        feedbackEditPage.verifyHtml("/instructorFeedbackEditManuallyPublished.html");

        // Restore defaults
        feedbackEditPage.clickEditSessionButton();

        feedbackEditPage.clickEditUncommonSettingsButton();
        feedbackEditPage.clickDefaultPublishTimeButton();
        feedbackEditPage.clickSaveSessionButton();
        
        ______TS("test end time earlier than start time");
        feedbackEditPage.clickEditSessionButton();
        editedSession.instructions = new Text("Made some changes");
        feedbackEditPage.editFeedbackSession(editedSession.endTime, editedSession.startTime,
                                        editedSession.instructions, editedSession.gracePeriod);
        
        String expectedString = "The end time for this feedback session cannot be earlier than the start time.";
        feedbackEditPage.verifyFieldValue("instructions", "Made some changes");
        feedbackEditPage.verifyStatus(expectedString);
    }

    private void testNewQuestionLink() {

        ______TS("new question (frame) link");
        feedbackEditPage.clickNewQuestionButton();
        assertTrue(feedbackEditPage.verifyNewEssayQuestionFormIsDisplayed());
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
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackQuestionAddSuccess.html");
    }

    private void testEditQuestionLink() {

        ______TS("edit question link");

        assertEquals(true, feedbackEditPage.clickEditQuestionButton(1));
    }

    private void testEditQuestionAction() {

        ______TS("edit question 1 to Team-to-Team");

        feedbackEditPage.clickVisibilityOptionsForQuestion1();
        feedbackEditPage.selectGiverTypeForQuestion1("Teams in this course");        
        feedbackEditPage.selectRecipientTypeForQuestion1("Other teams in the course");
        
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackQuestionEditToTeamToTeam.html");

        
        ______TS("test visibility options of question 1");
        feedbackEditPage.clickquestionSaveForQuestion1();
        feedbackEditPage.clickVisibilityOptionsForQuestion1();
        
        //TODO: use simple element checks instead of html checks after adding names to the checkboxes 
        //      in the edit page (follow todo in instructorsFeedbackEdit.js)
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackQuestionVisibilityOptions.html");
        
        
        ______TS("test visibility preview of question 1");
        feedbackEditPage.clickVisibilityPreviewForQuestion1();
        WebElement visibilityMessage = browser.driver.findElement(By.id("visibilityMessage-1"));
        feedbackEditPage.waitForElementVisibility(visibilityMessage);

        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackQuestionVisibilityPreview.html");
        
        //change back
        feedbackEditPage.clickVisibilityOptionsForQuestion1();
        feedbackEditPage.selectGiverTypeForQuestion1("Me (Session creator)");
        feedbackEditPage.selectRecipientTypeForQuestion1("Other students in the course");
        feedbackEditPage.clickquestionSaveForQuestion1();
    }
    
    private void testGetQuestionLink() {

        ______TS("get individual question link");

        feedbackEditPage.clickGetLinkButton();
        String questionId = BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1).getId();
        
        String expectedUrl = TestProperties.inst().TEAMMATES_URL
                             + Const.ActionURIs.INSTRUCTOR_FEEDBACK_QUESTION_SUBMISSION_EDIT_PAGE
                             + "?courseid=" + courseId
                             + "&fsname=First%20Session"
                             + "&questionid=" + questionId;

        assertTrue(feedbackEditPage.isElementVisible("statusMessage"));
        assertEquals("Link for question 1: " + expectedUrl, feedbackEditPage.getStatus());
        
        Url url = new Url(expectedUrl).withUserId(instructorId);
        FeedbackQuestionSubmitPage questionPage =
                loginAdminToPage(browser, url, FeedbackQuestionSubmitPage.class);
        
        assertTrue(questionPage.isCorrectPage(courseId, feedbackSessionName));
        
        feedbackEditPage = getFeedbackEditPage();
    }

    private void testCancelNewOrEditQuestion() {
        ______TS("Testing cancelling adding or editing questions");
        
        feedbackEditPage.selectNewQuestionType("Multiple-choice (single answer) question");
        feedbackEditPage.clickNewQuestionButton();
        
        
        ______TS("MCQ: click and cancel 'cancel new question'");
        
        feedbackEditPage.clickAndCancel(feedbackEditPage.getCancelQuestionLink(-1));
        assertTrue(feedbackEditPage.verifyNewMcqQuestionFormIsDisplayed());
        
        
        ______TS("MCQ: click and confirm 'cancel new question'");
        feedbackEditPage.clickAndConfirm(feedbackEditPage.getCancelQuestionLink(-1));
        assertFalse(feedbackEditPage.verifyNewMcqQuestionFormIsDisplayed());
        
        
        ______TS("MCQ: click and cancel 'editing question'");
        
        // Add question 2 first
        feedbackEditPage.selectNewQuestionType("Multiple-choice (single answer) question");
        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.fillQuestionBox("mcq qn");
        feedbackEditPage.fillMcqOption(0, "Choice 1");
        feedbackEditPage.fillMcqOption(1, "Choice 2");
        feedbackEditPage.clickAddQuestionButton();
        
        // Enable edit mode before testing canceling
        assertTrue(feedbackEditPage.clickEditQuestionButton(1));
        
        feedbackEditPage.clickAndCancel(feedbackEditPage.getCancelQuestionLink(1));
        assertTrue(feedbackEditPage.checkCancelEditQuestionButtonVisibility(1));
        
        
        ______TS("MCQ: click and confirm 'editing question'");
        feedbackEditPage.clickAndConfirm(feedbackEditPage.getCancelQuestionLink(1));
        assertFalse(feedbackEditPage.checkCancelEditQuestionButtonVisibility(1));
        
        // Delete it to reset the status for the following tests
        feedbackEditPage.clickAndConfirm(feedbackEditPage.getDeleteQuestionLink(1));
    }
    
    private void testEditQuestionNumberAction() {
        ______TS("edit question number success");

        assertEquals(true, feedbackEditPage.clickEditQuestionButton(2));
        feedbackEditPage.selectQuestionNumber(2, 1);        
        feedbackEditPage.clickSaveExistingQuestionButton(2);
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_EDITED, feedbackEditPage.getStatus());
    }

    private void testCopyQuestion() {
        
        ______TS("Success case: copy questions successfully");
        
        ThreadHelper.waitFor(1000);
        feedbackEditPage.clickCopyButton();
        ThreadHelper.waitFor(1000);
        feedbackEditPage.clickCopyTableAtRow(0);
        feedbackEditPage.clickCopySubmitButton();
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackCopyQuestionSuccess.html");
    }
    
    private void testChangeFeedbackRecipient() {
        ______TS("click on Edit visibility then change recipient");

        feedbackEditPage = getFeedbackEditPage();

        assertTrue(feedbackEditPage.verifyPreviewLabelIsActive(1));
        assertFalse(feedbackEditPage.verifyEditLabelIsActive(1));
        assertTrue(feedbackEditPage.verifyVisibilityMessageIsDisplayed(1));
        assertFalse(feedbackEditPage.verifyVisibilityOptionsIsDisplayed(1));

        feedbackEditPage.clickQuestionEditForQuestion1();
        feedbackEditPage.clickEditLabel(1);
        feedbackEditPage.selectRecipientTypeForQuestion1("Other teams in the course");

        assertFalse(feedbackEditPage.verifyPreviewLabelIsActive(1));
        assertTrue(feedbackEditPage.verifyEditLabelIsActive(1));
        assertFalse(feedbackEditPage.verifyVisibilityMessageIsDisplayed(1));
        assertTrue(feedbackEditPage.verifyVisibilityOptionsIsDisplayed(1));
    }

    private void testDeleteQuestionAction(int qnNumber) {
        
        ______TS("qn " + qnNumber + " delete then cancel");

        feedbackEditPage.clickAndCancel(feedbackEditPage.getDeleteQuestionLink(qnNumber));
        assertNotNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, qnNumber));

        
        ______TS("qn " + qnNumber + " delete then accept");

        feedbackEditPage.clickAndConfirm(feedbackEditPage.getDeleteQuestionLink(qnNumber));
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_DELETED, feedbackEditPage.getStatus());
        assertNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, qnNumber));
        
    }
    
    private void testPreviewSessionAction() {

        // add questions for previewing
        feedbackEditPage.clickNewQuestionButton();
        assertTrue(feedbackEditPage.verifyNewEssayQuestionFormIsDisplayed());
        feedbackEditPage.fillQuestionBox("question for me");
        feedbackEditPage.selectRecipientsToBeStudents();
        feedbackEditPage.clickAddQuestionButton();

        feedbackEditPage.clickNewQuestionButton();
        assertTrue(feedbackEditPage.verifyNewEssayQuestionFormIsDisplayed());
        feedbackEditPage.fillQuestionBox("question for students");
        feedbackEditPage.selectGiverToBeStudents();
        feedbackEditPage.clickAddQuestionButton();

        feedbackEditPage.clickNewQuestionButton();
        assertTrue(feedbackEditPage.verifyNewEssayQuestionFormIsDisplayed());
        feedbackEditPage.fillQuestionBox("question for instructors");
        feedbackEditPage.selectGiverToBeInstructors();
        feedbackEditPage.clickAddQuestionButton();

        
        ______TS("preview as student");

        FeedbackSubmitPage previewPage;
        previewPage = feedbackEditPage.clickPreviewAsStudentButton();
        previewPage.verifyHtmlMainContent("/studentFeedbackSubmitPagePreview.html");
        previewPage.closeCurrentWindowAndSwitchToParentWindow();

        
        ______TS("preview as instructor");

        previewPage.waitForElementPresence(By.id("button_preview_instructor"));
        previewPage = feedbackEditPage.clickPreviewAsInstructorButton();
        previewPage.verifyHtmlMainContent("/instructorFeedbackSubmitPagePreview.html");
        previewPage.closeCurrentWindowAndSwitchToParentWindow();
    }

    private void testDoneEditingLink() {
        InstructorFeedbacksPage feedbackPage = feedbackEditPage.clickDoneEditingLink();
        
        ______TS("Compare URLs");
        
        String expectedRedirectUrl = TestProperties.inst().TEAMMATES_URL
                                     + Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE + "?"
                                     + Const.ParamsNames.USER_ID + "=" + instructorId + "&"
                                     + Const.ParamsNames.COURSE_ID + "=" + courseId + "&"
                                     + Const.ParamsNames.FEEDBACK_SESSION_NAME + "="
                                     + feedbackSessionName.replaceAll(" ", "+");
        assertEquals(expectedRedirectUrl, feedbackPage.getPageUrl());
        
        
        ______TS("Check for highlight on last modified row");
        
        String idOfModifiedSession = "session0";
        String idOfModifiedSession2 = "session1";
        assertTrue(feedbackPage.isContainingCssClass(By.id(idOfModifiedSession), "warning"));
        assertFalse(feedbackPage.isContainingCssClass(By.id(idOfModifiedSession2), "warning"));
        
        // restore feedbackeditpage
        feedbackEditPage = getFeedbackEditPage();
    }

    private void testDeleteSessionAction() {

        ______TS("session delete then cancel");

        feedbackEditPage.clickAndCancel(feedbackEditPage.getDeleteSessionLink());
        assertNotNull(BackDoor.getFeedbackSession(courseId, feedbackSessionName));


        ______TS("session delete then accept");

        // check redirect to main feedback page
        InstructorFeedbacksPage feedbackPage = feedbackEditPage.deleteSession();
        AssertHelper.assertContains(Const.StatusMessages.FEEDBACK_SESSION_DELETED,
                                    feedbackPage.getStatus());
        assertNull(BackDoor.getFeedbackSession(courseId, feedbackSessionName));
    }

    private static InstructorFeedbackEditPage getFeedbackEditPage() {
        Url feedbackPageLink = createUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE)
                                    .withUserId(instructorId)
                                    .withCourseId(courseId)
                                    .withSessionName(feedbackSessionName);
        return loginAdminToPage(browser, feedbackPageLink, InstructorFeedbackEditPage.class);
    }

}
