package teammates.test.cases.ui.browsertests;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackQuestionType;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.test.driver.AssertHelper;
import teammates.test.driver.BackDoor;
import teammates.test.pageobjects.AppPage;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
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
    public static void classSetup() {
        printTestClassHeader();
        testData = loadDataBundle("/InstructorFeedbackEditPageUiTest.json");
        removeAndRestoreTestDataOnServer(testData);

        editedSession = testData.feedbackSessions.get("openSession");
        editedSession.setGracePeriod(30);
        editedSession.setSessionVisibleFromTime(Const.TIME_REPRESENTS_FOLLOW_OPENING);
        editedSession.setResultsVisibleFromTime(Const.TIME_REPRESENTS_LATER);
        editedSession.setInstructions(new Text("Please fill in the edited feedback session."));
        editedSession.setEndTime(TimeHelper.convertToDate("2026-05-01 10:00 PM UTC"));

        instructorId = testData.accounts.get("instructorWithSessions").googleId;
        courseId = testData.courses.get("course").getId();
        feedbackSessionName = testData.feedbackSessions.get("openSession").getFeedbackSessionName();

        browser = BrowserPool.getBrowser();
        feedbackEditPage = getFeedbackEditPage();
    }

    @AfterClass
    public static void classTearDown() {
        BrowserPool.release(browser);
    }

    @Test
    public void allTests() throws Exception {
        testContent();

        testEditSessionLink();
        testEditSessionAction();
        
        testGeneralQuestionOperations();

        testPreviewSessionAction();

        testDoneEditingLink();
        
        testDeleteSessionAction();
    }
    
    private void testGeneralQuestionOperations() throws Exception {
        testCancelAddingNewQuestion();
        testCancelEditQuestion();
        
        testNewQuestionLink();
        testInputValidationForQuestion();
        testAddQuestionAction();

        testEditQuestionLink();
        testEditQuestionAction();

        testCopyQuestion();

        testChangeFeedbackRecipient();

        testEditQuestionNumberAction();
        
        testAjaxOnVisibilityMessageButton();

        testDeleteQuestionAction(2);
        testDeleteQuestionAction(1);

        testEditNonExistentQuestion();
        
        testResponseRate();
    }

    private void testContent() throws Exception {

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

        assertTrue(feedbackEditPage.areDatesOfPreviousCurrentAndNextMonthEnabled());

        feedbackEditPage.clickManualPublishTimeButton();
        feedbackEditPage.clickDefaultVisibleTimeButton();
        
        feedbackEditPage.editFeedbackSession(editedSession.getStartTime(), editedSession.getEndTime(),
                editedSession.getInstructions(), editedSession.getGracePeriod());
        
        feedbackEditPage.verifyStatus(Const.StatusMessages.FEEDBACK_SESSION_EDITED);
        FeedbackSessionAttributes savedSession = BackDoor.getFeedbackSession(
                editedSession.getCourseId(), editedSession.getFeedbackSessionName());
        assertEquals(editedSession.toString(), savedSession.toString());
        assertEquals("overflow-auto alert alert-success statusMessage",
                feedbackEditPage.getStatusMessage().findElement(By.className("statusMessage")).getAttribute("class"));
        assertEquals(Const.StatusMessages.FEEDBACK_SESSION_EDITED, feedbackEditPage.getStatus());
        feedbackEditPage.reloadPage();
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackEditSuccess.html");


        ______TS("test edit page not changed after manual publish");

        // Do a backdoor 'manual' publish.
        editedSession.setResultsVisibleFromTime(Const.TIME_REPRESENTS_NOW);
        String status = BackDoor.editFeedbackSession(editedSession);
        assertEquals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS, status);

        feedbackEditPage = getFeedbackEditPage();

        // Ensuring that the settings did not default back to original values after manual publishing
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackEditManuallyPublished.html");

        // Restore defaults
        feedbackEditPage.clickEditSessionButton();

        feedbackEditPage.clickEditUncommonSettingsButton();
        feedbackEditPage.clickDefaultPublishTimeButton();
        feedbackEditPage.clickSaveSessionButton();
        
        ______TS("test end time earlier than start time");
        feedbackEditPage.clickEditSessionButton();
        editedSession.setInstructions(new Text("Made some changes"));
        feedbackEditPage.editFeedbackSession(editedSession.getEndTime(), editedSession.getStartTime(),
                                        editedSession.getInstructions(), editedSession.getGracePeriod());
        
        String expectedString = "The end time for this feedback session cannot be earlier than the start time.";
        feedbackEditPage.verifyFieldValue("instructions", "Made some changes");
        feedbackEditPage.verifyStatus(expectedString);
    }

    private void testNewQuestionLink() {

        ______TS("new question (frame) link");
        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionType("TEXT");
        assertTrue(feedbackEditPage.verifyNewEssayQuestionFormIsDisplayed());
    }

    private void testInputValidationForQuestion() {

        ______TS("empty question text");

        feedbackEditPage.clickAddQuestionButton();
        
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_TEXTINVALID, feedbackEditPage.getStatus());

        
        ______TS("empty number of max respondants field");

        feedbackEditPage.fillNewQuestionBox("filled qn");
        feedbackEditPage.selectRecipientsToBeStudents();
        feedbackEditPage.fillNumOfEntitiesToGiveFeedbackToBox("");
        feedbackEditPage.clickCustomNumberOfRecipientsButton();
        feedbackEditPage.clickAddQuestionButton();
        
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_NUMBEROFENTITIESINVALID, feedbackEditPage.getStatus());

    }

    private void testAddQuestionAction() throws Exception {

        ______TS("add question action success");

        feedbackEditPage.clickMaxNumberOfRecipientsButton();
        feedbackEditPage.clickAddQuestionButton();

        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED, feedbackEditPage.getStatus());
        assertNotNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackQuestionAddSuccess.html");
    }

    private void testEditQuestionLink() {

        ______TS("edit question link");

        feedbackEditPage.clickEditQuestionButton(1);
        assertTrue(feedbackEditPage.isQuestionEnabled(1));
    }

    private void testEditQuestionAction() throws Exception {

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
        
        
        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionType("TEXT");
        feedbackEditPage.waitForElementVisibility(browser.driver.findElement(By.id("questionTableNew")));
        feedbackEditPage.clickVisibilityOptionsForNewQuestion();
        feedbackEditPage.clickResponseVisiblityCheckBoxForNewQuestion("RECEIVER_TEAM_MEMBERS");
        feedbackEditPage.clickVisibilityPreviewForNewQuestion();
        
        feedbackEditPage.waitForTextContainedInElementPresence(
                By.id("visibilityMessage"),
                "The recipient's team members can see your response, but not the name of the recipient, or your name.");
        feedbackEditPage.selectRecipientTypeForNewQuestion("Instructors in the course");
        
        feedbackEditPage.waitForTextContainedInElementAbsence(
                By.id("visibilityMessage"),
                "The recipient's team members can see your response, but not the name of the recipient, or your name.");
        
        feedbackEditPage.getDiscardChangesLink(-1).click();
        feedbackEditPage.waitForConfirmationModalAndClickCancel();
        
        ______TS("add question 2 and edit it to giver's team members and giver");
        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.fillNewQuestionBox("test visibility when choosing giver's team members and giver");
        feedbackEditPage.selectGiverToBeStudents();
        feedbackEditPage.selectRecipientsToBeGiverTeamMembersAndGiver();
        feedbackEditPage.clickMaxNumberOfRecipientsButton();
        feedbackEditPage.clickAddQuestionButton();
        
        ______TS("test Recipient's Team Members row is hidden");
        feedbackEditPage.clickVisibilityOptionsForQuestion(2);
        
        // use getAttribute("textContent") instead of getText
        // because of the row of Recipient's Team Members is not displayed
        assertEquals("Recipient's Team Members",
                     feedbackEditPage.getVisibilityOptionTableRow(2, 4).getAttribute("textContent").trim());
        assertFalse(feedbackEditPage.getVisibilityOptionTableRow(2, 4).isDisplayed());
                
        ______TS("test visibility preview of question 2");
        feedbackEditPage.clickVisibilityPreviewForQuestion(2);
        WebElement visibilityMessage2 = browser.driver.findElement(By.id("visibilityMessage-2"));
        feedbackEditPage.waitForElementVisibility(visibilityMessage2);

        assertTrue("Expected the receiving student to be able to see response, but was "
                   + visibilityMessage2.getText(), visibilityMessage2.getText()
                   .contains("The receiving student can see your response, and your name."));
        
        assertTrue("Expected instructors to be able to see response, but was "
                   + visibilityMessage2.getText(), visibilityMessage2.getText()
                   .contains("Instructors in this course can see your response, the name of the recipient, and your name."));
        
        feedbackEditPage.getDeleteQuestionLink(2).click();
        feedbackEditPage.waitForConfirmationModalAndClickOk();
    }

    private void testCancelAddingNewQuestion() {
        ______TS("Cancelling the adding of a new question");
        
        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionType("MCQ");
        
        ______TS("Click cancel but click no to confirmation prompt");
        feedbackEditPage.getDiscardChangesLink(-1).click();
        feedbackEditPage.waitForConfirmationModalAndClickCancel();
        assertTrue(feedbackEditPage.verifyNewMcqQuestionFormIsDisplayed());
        
        
        ______TS("Click cancel and click yes to confirmation prompt");
        feedbackEditPage.getDiscardChangesLink(-1).click();
        feedbackEditPage.waitForConfirmationModalAndClickOk();
        assertFalse(feedbackEditPage.verifyNewMcqQuestionFormIsDisplayed());

        ______TS("Make sure controls disabled by Team Contribution questions are re-enabled after cancelling");
        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionType("CONTRIB");
        feedbackEditPage.getDiscardChangesLink(-1).click();
        feedbackEditPage.waitForConfirmationModalAndClickOk();

        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionType("NUMSCALE");
        assertTrue(feedbackEditPage.isAllFeedbackPathOptionsEnabled());

        feedbackEditPage.getDiscardChangesLink(-1).click();
        feedbackEditPage.waitForConfirmationModalAndClickOk();

    }

    private void testCancelEditQuestion() {
        ______TS("Canceling the edit of an existing question");
        int qnIndex = 1;
        String qnTextOriginal = "mcq qn";

        // Add question 2 first
        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionType("MCQ");
        feedbackEditPage.fillNewQuestionBox(qnTextOriginal);
        feedbackEditPage.fillMcqOption(0, "Choice 1");
        feedbackEditPage.fillMcqOption(1, "Choice 2");
        feedbackEditPage.clickAddQuestionButton();
        
        // Enable edit mode before testing canceling
        feedbackEditPage.clickEditQuestionButton(qnIndex);
        

        ______TS("Click cancel but click no to confirmation prompt");
        feedbackEditPage.getDiscardChangesLink(qnIndex).click();
        feedbackEditPage.waitForConfirmationModalAndClickCancel();
        assertTrue(feedbackEditPage.isDiscardChangesButtonVisible(qnIndex));
        
        
        ______TS("Click cancel and click yes to confirmation prompt");
        feedbackEditPage.fillEditQuestionBox("new edits to question text", qnIndex);
        String qnTextAfterEdit = feedbackEditPage.getQuestionBoxText(qnIndex);
        assertFalse(qnTextOriginal.equals(qnTextAfterEdit));

        feedbackEditPage.getDiscardChangesLink(qnIndex).click();
        feedbackEditPage.waitForConfirmationModalAndClickOk();
        assertFalse(feedbackEditPage.isDiscardChangesButtonVisible(qnIndex));
        String qnTextAfterCancelEdit = feedbackEditPage.getQuestionBoxText(qnIndex);
        assertEquals(qnTextOriginal, qnTextAfterCancelEdit);
        
        ______TS("Try re-editing a question after cancelling, making sure that form controls still work");
        feedbackEditPage.clickEditQuestionButton(qnIndex);
        feedbackEditPage.selectRecipientsToBeStudents(qnIndex);
        assertTrue(feedbackEditPage.isOptionForSelectingNumberOfEntitiesVisible(qnIndex));

        // Delete it to reset the status for the following tests
        feedbackEditPage.getDeleteQuestionLink(qnIndex).click();
        feedbackEditPage.waitForConfirmationModalAndClickOk();
    }
    
    private void testEditQuestionNumberAction() {
        ______TS("edit question number success");

        feedbackEditPage.clickEditQuestionButton(2);
        feedbackEditPage.selectQuestionNumber(2, 1);
        feedbackEditPage.clickSaveExistingQuestionButton(2);
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_EDITED, feedbackEditPage.getStatus());
        
        ______TS("questions still editable even if questions numbers became inconsistent");
        
        FeedbackQuestionAttributes firstQuestion =
                                        BackDoor.getFeedbackQuestion(courseId,
                                                                     feedbackSessionName,
                                                                     1);
        assertEquals(1, firstQuestion.questionNumber);

        FeedbackQuestionAttributes secondQuestion =
                                        BackDoor.getFeedbackQuestion(courseId,
                                                                     feedbackSessionName,
                                                                     2);
        assertEquals(2, secondQuestion.questionNumber);
        int originalSecondQuestionNumber = secondQuestion.questionNumber;
        
        // edit so that both questions have the same question number
        secondQuestion.questionNumber = firstQuestion.questionNumber;
        BackDoor.editFeedbackQuestion(secondQuestion);
        
        // verify both can be edited
        feedbackEditPage = getFeedbackEditPage();
        feedbackEditPage.clickEditQuestionButton(1);
        assertTrue(feedbackEditPage.isQuestionEnabled(1));
        feedbackEditPage.clickEditQuestionButton(2);
        assertTrue(feedbackEditPage.isQuestionEnabled(2));
        
        // fix inconsistent state
        secondQuestion.questionNumber = originalSecondQuestionNumber;
        BackDoor.editFeedbackQuestion(secondQuestion);
        feedbackEditPage = getFeedbackEditPage();
    }

    private void testCopyQuestion() throws Exception {
        
        ______TS("Success case: copy questions successfully");
        
        feedbackEditPage.clickCopyButton();
        
        feedbackEditPage.waitForCopyTableToLoad();
        
        assertFalse("Unable to submit when there are no questions selected",
                    feedbackEditPage.isCopySubmitButtonEnabled());
        feedbackEditPage.verifyHtmlPart(By.id("copyModal"), "/instructorFeedbackCopyQuestionModal.html");
        feedbackEditPage.clickCopyTableAtRow(0);
        
        assertTrue("Can click after selecting", feedbackEditPage.isCopySubmitButtonEnabled());
        
        feedbackEditPage.clickCopySubmitButton();
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackCopyQuestionSuccess.html");
        
        ______TS("Success case: copy multiple questions successfully");
        
        int numQuestionEditForms = feedbackEditPage.getNumberOfQuestionEditForms();
        feedbackEditPage.clickCopyButton();
        
        feedbackEditPage.waitForCopyTableToLoad();
        feedbackEditPage.clickCopyTableAtRow(0);
        feedbackEditPage.clickCopyTableAtRow(1);
        
        feedbackEditPage.clickCopySubmitButton();
        
        assertEquals(numQuestionEditForms + 2, feedbackEditPage.getNumberOfQuestionEditForms());
        
        ______TS("No copiable questions");
        
        feedbackEditPage = getFeedbackEditPageOfCourseWithoutQuestions();
        feedbackEditPage.clickCopyButton();
        
        feedbackEditPage.waitForCopyErrorMessageToLoad();
        assertEquals("There are no questions to be copied.",
                     feedbackEditPage.getCopyErrorMessageText());
        
        assertFalse("Should not be able to submit if there are no questions",
                    feedbackEditPage.isCopySubmitButtonEnabled());
                
        ______TS("Fails gracefully with an error message");
        feedbackEditPage = getFeedbackEditPage();
        
        feedbackEditPage.changeActionLinkOnCopyButton("INVALID URL");
        feedbackEditPage.clickCopyButton();

        feedbackEditPage.waitForCopyErrorMessageToLoad();
        assertEquals("Error retrieving questions. Please close the dialog window and try again.",
                     feedbackEditPage.getCopyErrorMessageText());
        
        assertFalse("Should not be able to submit if loading failed",
                    feedbackEditPage.isCopySubmitButtonEnabled());
        
        // revert back to state expected by tests after this by deleting new copied questions
        String questionId = BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 4).getId();
        BackDoor.deleteFeedbackQuestion(questionId);
        questionId = BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 3).getId();
        BackDoor.deleteFeedbackQuestion(questionId);
        
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
    
    private void testAjaxOnVisibilityMessageButton() {
        ______TS("Failure case: ajax on clicking visibility message button");
        
        feedbackEditPage.clickVisibilityOptionsForQuestion1();
        feedbackEditPage.changeQuestionTypeInForm(1, "InvalidQuestionType");
        feedbackEditPage.clickVisibilityPreviewForQuestion1();
        feedbackEditPage.waitForAjaxErrorOnVisibilityMessageButton(1);
        assertFalse(feedbackEditPage.verifyVisibilityMessageIsDisplayed(1));
    }

    private void testDeleteQuestionAction(int qnNumber) {
        
        ______TS("qn " + qnNumber + " delete then cancel");

        feedbackEditPage.getDeleteQuestionLink(qnNumber).click();
        feedbackEditPage.waitForConfirmationModalAndClickCancel();
        assertNotNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, qnNumber));

        
        ______TS("qn " + qnNumber + " delete then accept");

        feedbackEditPage.getDeleteQuestionLink(qnNumber).click();
        feedbackEditPage.waitForConfirmationModalAndClickOk();
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_DELETED, feedbackEditPage.getStatus());
        assertNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, qnNumber));
        
    }

    private void testEditNonExistentQuestion() {

        ______TS("test editing a non-existent question");

        // Create a new question and save
        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionType("TEXT");
        feedbackEditPage.fillNewQuestionBox("new question");
        feedbackEditPage.clickAddQuestionButton();

        // Delete the new question through the backdoor so that it still appears in the browser
        String questionId = BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1).getId();
        String status = BackDoor.deleteFeedbackQuestion(questionId);
        assertEquals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS, status);

        // Edit the deleted question and save
        feedbackEditPage.clickEditQuestionButton(1);
        feedbackEditPage.fillEditQuestionBox("non-existent question", 1);
        feedbackEditPage.clickSaveExistingQuestionButton(1);

        AppUrl expectedRedirectUrl = createUrl("/entityNotFoundPage.jsp");

        assertEquals(expectedRedirectUrl.toAbsoluteString(), browser.driver.getCurrentUrl());

        // Restore feedbackEditPage
        feedbackEditPage = getFeedbackEditPage();
    }
    
    private void testResponseRate() {
        
        // Create a new question and save
        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionType("TEXT");
        feedbackEditPage.fillNewQuestionBox("new question");
        feedbackEditPage.clickAddQuestionButton();

        // Create response for the new question
        FeedbackResponseAttributes feedbackResponse =
                new FeedbackResponseAttributes(
                        feedbackSessionName,
                        courseId,
                        "1",
                        FeedbackQuestionType.TEXT,
                        "tmms.test@gmail.tmt",
                        Const.DEFAULT_SECTION,
                        "alice.b.tmms@gmail.tmt",
                        Const.DEFAULT_SECTION,
                        new Text("Response from instructor to Alice"));
        BackDoor.createFeedbackResponse(feedbackResponse);

        ______TS("check response rate before editing question");

        InstructorFeedbacksPage feedbacksPage = navigateToInstructorFeedbacksPage();
        feedbacksPage.waitForAjaxLoaderGifToDisappear();

        assertEquals("1 / 1", feedbacksPage.getResponseValue(courseId, feedbackSessionName));

        // Change the feedback path of the question and save
        feedbackEditPage = getFeedbackEditPage();
        feedbackEditPage.clickEditQuestionButton(1);
        feedbackEditPage.selectRecipientTypeForQuestion1("Other teams in the course");
        feedbackEditPage.clickquestionSaveForQuestion1();
        feedbackEditPage.waitForConfirmationModalAndClickOk();
        
        ______TS("check response rate after editing question");

        feedbacksPage = navigateToInstructorFeedbacksPage();
        feedbacksPage.waitForAjaxLoaderGifToDisappear();

        assertEquals("0 / 1", feedbacksPage.getResponseValue(courseId, feedbackSessionName));
        
        // Delete the question
        feedbackEditPage = getFeedbackEditPage();
        feedbackEditPage.getDeleteQuestionLink(1).click();
        feedbackEditPage.waitForConfirmationModalAndClickOk();
    }
    
    private InstructorFeedbacksPage navigateToInstructorFeedbacksPage() {
        
        AppUrl feedbacksPageUrl = createUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE).withUserId(instructorId);
        InstructorFeedbacksPage feedbacksPage =
                AppPage.getNewPageInstance(browser, feedbacksPageUrl, InstructorFeedbacksPage.class);
        feedbacksPage.waitForPageToLoad();
        return feedbacksPage;
    }

    private void testPreviewSessionAction() throws Exception {

        // add questions for previewing
        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionType("TEXT");
        assertTrue(feedbackEditPage.verifyNewEssayQuestionFormIsDisplayed());
        feedbackEditPage.fillNewQuestionBox("question for me");
        feedbackEditPage.selectRecipientsToBeStudents();
        feedbackEditPage.clickAddQuestionButton();
        

        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionType("TEXT");
        assertTrue(feedbackEditPage.verifyNewEssayQuestionFormIsDisplayed());
        feedbackEditPage.fillNewQuestionBox("question for students");
        feedbackEditPage.selectGiverToBeStudents();
        feedbackEditPage.clickAddQuestionButton();

        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionType("TEXT");
        assertTrue(feedbackEditPage.verifyNewEssayQuestionFormIsDisplayed());
        feedbackEditPage.fillNewQuestionBox("question for instructors");
        feedbackEditPage.selectGiverToBeInstructors();
        feedbackEditPage.clickAddQuestionButton();

        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionType("TEXT");
        assertTrue(feedbackEditPage.verifyNewEssayQuestionFormIsDisplayed());
        feedbackEditPage.fillNewQuestionBox("question for students to instructors");
        feedbackEditPage.selectGiverToBeStudents();
        feedbackEditPage.selectRecipientsToBeInstructors();
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
        
        String expectedRedirectUrl = createUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE)
                                        .withUserId(instructorId)
                                        .withCourseId(courseId)
                                        .withSessionName(feedbackSessionName)
                                        .toAbsoluteString();
        assertEquals(expectedRedirectUrl, feedbackPage.getPageUrl());
        
        
        ______TS("Check for highlight on last modified row");
        
        feedbackPage.waitForAjaxLoaderGifToDisappear();
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
        AppUrl feedbackPageLink = createUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE)
                                    .withUserId(instructorId)
                                    .withCourseId(courseId)
                                    .withSessionName(feedbackSessionName);
        return loginAdminToPage(browser, feedbackPageLink, InstructorFeedbackEditPage.class);
    }
    
    private static InstructorFeedbackEditPage getFeedbackEditPageOfCourseWithoutQuestions() {
        String instructor = testData.instructors.get("teammates.test.instructor3").googleId;
        String courseWithoutQuestion = testData.courses.get("course2").getId();
        String sessionWithoutQuestions = testData.feedbackSessions.get("openSession3")
                                                                  .getFeedbackSessionName();
        AppUrl feedbackPageLink = createUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE)
                                    .withUserId(instructor)
                                    .withCourseId(courseWithoutQuestion)
                                    .withSessionName(sessionWithoutQuestions);
        return loginAdminToPage(browser, feedbackPageLink, InstructorFeedbackEditPage.class);
    }

}
