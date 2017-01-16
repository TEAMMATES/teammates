package teammates.test.cases.ui.browsertests;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackParticipantType;
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
    public void classSetup() {
        printTestClassHeader();
        testData = loadDataBundle("/InstructorFeedbackEditPageUiTest.json");
        removeAndRestoreDataBundle(testData);

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

        testChangeFeedbackGiver();
        testChangeFeedbackRecipient();
        testVisibilityOptionsCorrespondToFeedbackPath();
        testVisibilityOptionsUncheckedWhenHidden();

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
        assertTrue(feedbackEditPage.isElementInViewport(Const.ParamsNames.STATUS_MESSAGES_LIST));

        FeedbackSessionAttributes savedSession = BackDoor.getFeedbackSession(
                editedSession.getCourseId(), editedSession.getFeedbackSessionName());
        editedSession.setInstructions(new Text("<p>" + editedSession.getInstructionsString() + "</p>"));
        assertEquals(editedSession.toString(), savedSession.toString());
        assertEquals("overflow-auto alert alert-success statusMessage",
                feedbackEditPage.getStatusMessage().findElement(By.className("statusMessage")).getAttribute("class"));
        feedbackEditPage.verifyStatus(Const.StatusMessages.FEEDBACK_SESSION_EDITED);
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
        feedbackEditPage.verifyStatus(expectedString);
    }

    private void testNewQuestionLink() {

        ______TS("new question (frame) link");
        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionType("TEXT");
        assertTrue(feedbackEditPage.verifyNewEssayQuestionFormIsDisplayed());
        feedbackEditPage.waitForTextContainedInElementPresence(
                By.id("visibilityMessage--1"),
                "You can see your own feedback in the results page later on.");
        feedbackEditPage.waitForTextContainedInElementPresence(
                By.id("visibilityMessage--1"),
                "Instructors in this course can see your response, the name of the recipient, and your name.");
        assertTrue("Visibility preview for new question should be displayed",
                   feedbackEditPage.verifyVisibilityMessageIsDisplayed(-1));
    }

    private void testInputValidationForQuestion() {

        ______TS("empty question text");

        feedbackEditPage.clickAddQuestionButton();
        
        feedbackEditPage.verifyStatus(Const.StatusMessages.FEEDBACK_QUESTION_TEXTINVALID);
        
        ______TS("empty number of max respondents field");

        feedbackEditPage.fillNewQuestionBox("filled qn");
        feedbackEditPage.fillNewQuestionDescription("more details");
        feedbackEditPage.enableOtherFeedbackPathOptionsForNewQuestion();
        feedbackEditPage.selectRecipientsToBeStudents();
        feedbackEditPage.fillNumOfEntitiesToGiveFeedbackToBoxForNewQuestion("");
        feedbackEditPage.clickCustomNumberOfRecipientsButton();
        feedbackEditPage.clickAddQuestionButton();
        
        feedbackEditPage.verifyStatus(Const.StatusMessages.FEEDBACK_QUESTION_NUMBEROFENTITIESINVALID);

    }

    private void testAddQuestionAction() throws Exception {

        ______TS("add question action success");

        feedbackEditPage.clickMaxNumberOfRecipientsButton();
        feedbackEditPage.clickAddQuestionButton();

        feedbackEditPage.verifyStatus(Const.StatusMessages.FEEDBACK_QUESTION_ADDED);
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

        feedbackEditPage.enableOtherVisibilityOptions(1);
        feedbackEditPage.enableOtherFeedbackPathOptions(1);
        feedbackEditPage.selectGiverToBe(FeedbackParticipantType.TEAMS, 1);
        feedbackEditPage.selectRecipientToBe(FeedbackParticipantType.TEAMS, 1);
        
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackQuestionEditToTeamToTeam.html");

        
        ______TS("test visibility options of question 1");
        feedbackEditPage.clickquestionSaveForQuestion1();
        feedbackEditPage.clickEditQuestionButton(1);
        
        //TODO: use simple element checks instead of html checks after adding names to the checkboxes
        //      in the edit page (follow todo in instructorsFeedbackEdit.js)
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackQuestionVisibilityOptions.html");
        
        
        ______TS("test visibility preview of question 1");
        feedbackEditPage.enableOtherVisibilityOptions(1);
        WebElement visibilityMessage = browser.driver.findElement(By.id("visibilityMessage-1"));
        feedbackEditPage.waitForElementVisibility(visibilityMessage);

        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackQuestionVisibilityPreview.html");
        
        //change back
        feedbackEditPage.enableOtherVisibilityOptions(1);
        feedbackEditPage.enableOtherFeedbackPathOptions(1);
        feedbackEditPage.selectGiverToBe(FeedbackParticipantType.SELF, 1);
        feedbackEditPage.selectRecipientToBe(FeedbackParticipantType.STUDENTS, 1);
        feedbackEditPage.clickquestionSaveForQuestion1();
        
        
        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionType("TEXT");
        feedbackEditPage.waitForElementVisibility(browser.driver.findElement(By.id("questionTable--1")));
        feedbackEditPage.enableOtherVisibilityOptions(-1);
        feedbackEditPage.clickResponseVisibilityCheckBox("RECEIVER_TEAM_MEMBERS", -1);
        
        feedbackEditPage.waitForTextContainedInElementPresence(
                By.id("visibilityMessage--1"),
                "The recipient's team members can see your response, but not the name of the recipient, or your name.");
        feedbackEditPage.enableOtherFeedbackPathOptionsForNewQuestion();
        feedbackEditPage.selectRecipientTypeForNewQuestion("Instructors in the course");
        
        feedbackEditPage.waitForTextContainedInElementAbsence(
                By.id("visibilityMessage--1"),
                "The recipient's team members can see your response, but not the name of the recipient, or your name.");
        
        feedbackEditPage.clickDiscardChangesLink(-1);
        feedbackEditPage.waitForConfirmationModalAndClickCancel();
        
        ______TS("add question 2 and edit it to giver's team members and giver");
        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.fillNewQuestionBox("test visibility when choosing giver's team members and giver");
        feedbackEditPage.fillNewQuestionDescription(
                "<h3 style=\"text-align: center;\"><strong>Description</strong></h3><hr /><p>&nbsp;</p>");
        feedbackEditPage.enableOtherFeedbackPathOptionsForNewQuestion();
        feedbackEditPage.selectGiverToBeStudents();
        feedbackEditPage.selectRecipientsToBeGiverTeamMembersAndGiver();
        feedbackEditPage.clickMaxNumberOfRecipientsButton();
        feedbackEditPage.clickAddQuestionButton();
        
        ______TS("test Recipient's Team Members row is hidden");
        feedbackEditPage.clickEditQuestionButton(2);
        feedbackEditPage.enableOtherVisibilityOptions(2);
        
        // use getAttribute("textContent") instead of getText
        // because of the row of Recipient's Team Members is not displayed
        assertEquals("Recipient's Team Members",
                     feedbackEditPage.getVisibilityOptionTableRow(2, 4).getAttribute("textContent").trim());
        assertFalse(feedbackEditPage.getVisibilityOptionTableRow(2, 4).isDisplayed());
                
        ______TS("test visibility preview of question 2");
        WebElement visibilityMessage2 = browser.driver.findElement(By.id("visibilityMessage-2"));
        feedbackEditPage.waitForElementVisibility(visibilityMessage2);

        assertTrue("Expected the receiving student to be able to see response, but was "
                   + visibilityMessage2.getText(), visibilityMessage2.getText()
                   .contains("The receiving student can see your response, and your name."));
        
        assertTrue("Expected instructors to be able to see response, but was "
                   + visibilityMessage2.getText(), visibilityMessage2.getText()
                   .contains("Instructors in this course can see your response, the name of the recipient, and your name."));
        
        feedbackEditPage.clickDeleteQuestionLink(2);
        feedbackEditPage.waitForConfirmationModalAndClickOk();
    }

    private void testCancelAddingNewQuestion() {
        ______TS("Cancelling the adding of a new question");
        
        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionType("MCQ");
        
        ______TS("Click cancel but click no to confirmation prompt");
        feedbackEditPage.clickDiscardChangesLink(-1);
        feedbackEditPage.waitForConfirmationModalAndClickCancel();
        assertTrue(feedbackEditPage.verifyNewMcqQuestionFormIsDisplayed());
        
        
        ______TS("Click cancel and click yes to confirmation prompt");
        feedbackEditPage.clickDiscardChangesLink(-1);
        feedbackEditPage.waitForConfirmationModalAndClickOk();
        assertFalse(feedbackEditPage.verifyNewMcqQuestionFormIsDisplayed());

        ______TS("Make sure controls disabled by Team Contribution questions are re-enabled after cancelling");
        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionType("CONTRIB");
        feedbackEditPage.clickDiscardChangesLink(-1);
        feedbackEditPage.waitForConfirmationModalAndClickOk();

        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionType("NUMSCALE");
        assertTrue(feedbackEditPage.isAllFeedbackPathOptionsEnabledForNewQuestion());

        feedbackEditPage.clickDiscardChangesLink(-1);
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
        feedbackEditPage.fillNewQuestionDescription("more details");
        feedbackEditPage.fillMcqOption(0, "Choice 1");
        feedbackEditPage.fillMcqOption(1, "Choice 2");
        feedbackEditPage.clickAddQuestionButton();
        
        // Enable edit mode before testing canceling
        feedbackEditPage.clickEditQuestionButton(qnIndex);
        

        ______TS("Click cancel but click no to confirmation prompt");
        feedbackEditPage.clickDiscardChangesLink(qnIndex);
        feedbackEditPage.waitForConfirmationModalAndClickCancel();
        assertTrue(feedbackEditPage.isDiscardChangesButtonVisible(qnIndex));
        
        
        ______TS("Click cancel and click yes to confirmation prompt");
        feedbackEditPage.fillEditQuestionBox("new edits to question text", qnIndex);
        feedbackEditPage.fillEditQuestionDescription("more details", qnIndex);
        String qnTextAfterEdit = feedbackEditPage.getQuestionBoxText(qnIndex);
        assertFalse(qnTextOriginal.equals(qnTextAfterEdit));

        feedbackEditPage.clickDiscardChangesLink(qnIndex);
        feedbackEditPage.waitForConfirmationModalAndClickOk();
        assertFalse(feedbackEditPage.isDiscardChangesButtonVisible(qnIndex));
        String qnTextAfterCancelEdit = feedbackEditPage.getQuestionBoxText(qnIndex);
        assertEquals(qnTextOriginal, qnTextAfterCancelEdit);
        
        ______TS("Try re-editing a question after cancelling, making sure that form controls still work");
        feedbackEditPage.clickEditQuestionButton(qnIndex);
        feedbackEditPage.enableOtherFeedbackPathOptions(qnIndex);
        feedbackEditPage.selectRecipientsToBeStudents(qnIndex);
        assertTrue(feedbackEditPage.isOptionForSelectingNumberOfEntitiesVisible(qnIndex));

        // Delete it to reset the status for the following tests
        feedbackEditPage.clickDeleteQuestionLink(qnIndex);
        feedbackEditPage.waitForConfirmationModalAndClickOk();
    }
    
    private void testEditQuestionNumberAction() {
        ______TS("edit question number success");

        feedbackEditPage.clickEditQuestionButton(2);
        feedbackEditPage.selectQuestionNumber(2, 1);
        feedbackEditPage.clickSaveExistingQuestionButton(2);
        feedbackEditPage.verifyStatus(Const.StatusMessages.FEEDBACK_QUESTION_EDITED);
        
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
    
    private void testChangeFeedbackGiver() {
        feedbackEditPage = getFeedbackEditPage();
        feedbackEditPage.clickQuestionEditForQuestion1();
        
        ______TS("change giver to \"Students in this course\" from \"Me (Session creator)\"");
        feedbackEditPage.selectGiverToBe(FeedbackParticipantType.STUDENTS, 1);
        assertTrue(feedbackEditPage.isAllRecipientOptionsDisplayed(1));
        
        ______TS("change giver to \"Instructors in this course\" from \"Students in this course\"");
        feedbackEditPage.selectGiverToBe(FeedbackParticipantType.INSTRUCTORS, 1);
        assertTrue(feedbackEditPage.isRecipientOptionDisplayed(FeedbackParticipantType.SELF, 1));
        assertTrue(feedbackEditPage.isRecipientOptionDisplayed(FeedbackParticipantType.STUDENTS, 1));
        assertTrue(feedbackEditPage.isRecipientOptionDisplayed(FeedbackParticipantType.INSTRUCTORS, 1));
        assertTrue(feedbackEditPage.isRecipientOptionDisplayed(FeedbackParticipantType.TEAMS, 1));
        assertTrue(feedbackEditPage.isRecipientOptionDisplayed(FeedbackParticipantType.OWN_TEAM, 1));
        assertFalse(feedbackEditPage.isRecipientOptionDisplayed(FeedbackParticipantType.OWN_TEAM_MEMBERS, 1));
        assertFalse(feedbackEditPage.isRecipientOptionDisplayed(
                            FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF, 1));
        assertTrue(feedbackEditPage.isRecipientOptionDisplayed(FeedbackParticipantType.NONE, 1));
        
        ______TS("change giver to \"Teams in this course\" from \"Instructors in this course\"");
        feedbackEditPage.selectGiverToBe(FeedbackParticipantType.TEAMS, 1);
        assertTrue(feedbackEditPage.isRecipientOptionDisplayed(FeedbackParticipantType.SELF, 1));
        assertTrue(feedbackEditPage.isRecipientOptionDisplayed(FeedbackParticipantType.STUDENTS, 1));
        assertTrue(feedbackEditPage.isRecipientOptionDisplayed(FeedbackParticipantType.INSTRUCTORS, 1));
        assertTrue(feedbackEditPage.isRecipientOptionDisplayed(FeedbackParticipantType.TEAMS, 1));
        assertFalse(feedbackEditPage.isRecipientOptionDisplayed(FeedbackParticipantType.OWN_TEAM, 1));
        assertFalse(feedbackEditPage.isRecipientOptionDisplayed(FeedbackParticipantType.OWN_TEAM_MEMBERS, 1));
        assertTrue(feedbackEditPage.isRecipientOptionDisplayed(
                        FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF, 1));
        assertTrue(feedbackEditPage.isRecipientOptionDisplayed(FeedbackParticipantType.NONE, 1));
        
        ______TS("change giver to \"Me (Session creator)\" from \"Teams in this course\"");
        feedbackEditPage.selectGiverToBe(FeedbackParticipantType.SELF, 1);
        assertTrue(feedbackEditPage.isRecipientOptionDisplayed(FeedbackParticipantType.SELF, 1));
        assertTrue(feedbackEditPage.isRecipientOptionDisplayed(FeedbackParticipantType.STUDENTS, 1));
        assertTrue(feedbackEditPage.isRecipientOptionDisplayed(FeedbackParticipantType.INSTRUCTORS, 1));
        assertTrue(feedbackEditPage.isRecipientOptionDisplayed(FeedbackParticipantType.TEAMS, 1));
        assertTrue(feedbackEditPage.isRecipientOptionDisplayed(FeedbackParticipantType.OWN_TEAM, 1));
        assertFalse(feedbackEditPage.isRecipientOptionDisplayed(FeedbackParticipantType.OWN_TEAM_MEMBERS, 1));
        assertFalse(feedbackEditPage.isRecipientOptionDisplayed(
                        FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF, 1));
        assertTrue(feedbackEditPage.isRecipientOptionDisplayed(FeedbackParticipantType.NONE, 1));
        
        ______TS("change giver such that first visible recipient is selected");
        feedbackEditPage.selectGiverToBe(FeedbackParticipantType.STUDENTS, 1);
        feedbackEditPage.selectRecipientToBe(FeedbackParticipantType.OWN_TEAM_MEMBERS, 1);
        feedbackEditPage.selectGiverToBe(FeedbackParticipantType.SELF, 1);
        assertEquals("SELF", feedbackEditPage.getRecipientTypeForQuestion1());
        
        feedbackEditPage.selectGiverToBe(FeedbackParticipantType.STUDENTS, 1);
        feedbackEditPage.selectRecipientToBe(FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF, 1);
        feedbackEditPage.selectGiverToBe(FeedbackParticipantType.SELF, 1);
        assertEquals("SELF", feedbackEditPage.getRecipientTypeForQuestion1());
        
        feedbackEditPage.selectGiverToBe(FeedbackParticipantType.STUDENTS, 1);
        feedbackEditPage.selectRecipientToBe(FeedbackParticipantType.OWN_TEAM_MEMBERS, 1);
        feedbackEditPage.selectGiverToBe(FeedbackParticipantType.INSTRUCTORS, 1);
        assertEquals("SELF", feedbackEditPage.getRecipientTypeForQuestion1());
        
        feedbackEditPage.selectGiverToBe(FeedbackParticipantType.STUDENTS, 1);
        feedbackEditPage.selectRecipientToBe(FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF, 1);
        feedbackEditPage.selectGiverToBe(FeedbackParticipantType.INSTRUCTORS, 1);
        assertEquals("SELF", feedbackEditPage.getRecipientTypeForQuestion1());
        
        feedbackEditPage.selectGiverToBe(FeedbackParticipantType.STUDENTS, 1);
        feedbackEditPage.selectRecipientToBe(FeedbackParticipantType.OWN_TEAM, 1);
        feedbackEditPage.selectGiverToBe(FeedbackParticipantType.TEAMS, 1);
        assertEquals("SELF", feedbackEditPage.getRecipientTypeForQuestion1());
        
        feedbackEditPage.selectGiverToBe(FeedbackParticipantType.STUDENTS, 1);
        feedbackEditPage.selectRecipientToBe(FeedbackParticipantType.OWN_TEAM_MEMBERS, 1);
        feedbackEditPage.selectGiverToBe(FeedbackParticipantType.TEAMS, 1);
        assertEquals("SELF", feedbackEditPage.getRecipientTypeForQuestion1());
        
        ______TS("add new question");
        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionType("TEXT");
        
        assertEquals("TEAMS", feedbackEditPage.getGiverTypeForQuestion1());
        assertEquals("SELF", feedbackEditPage.getRecipientTypeForQuestion1());
        
        assertTrue(feedbackEditPage.isRecipientOptionDisplayed(FeedbackParticipantType.SELF, 1));
        assertTrue(feedbackEditPage.isRecipientOptionDisplayed(FeedbackParticipantType.STUDENTS, 1));
        assertTrue(feedbackEditPage.isRecipientOptionDisplayed(FeedbackParticipantType.INSTRUCTORS, 1));
        assertTrue(feedbackEditPage.isRecipientOptionDisplayed(FeedbackParticipantType.TEAMS, 1));
        assertFalse(feedbackEditPage.isRecipientOptionDisplayed(FeedbackParticipantType.OWN_TEAM, 1));
        assertFalse(feedbackEditPage.isRecipientOptionDisplayed(FeedbackParticipantType.OWN_TEAM_MEMBERS, 1));
        assertTrue(feedbackEditPage.isRecipientOptionDisplayed(
                        FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF, 1));
        assertTrue(feedbackEditPage.isRecipientOptionDisplayed(FeedbackParticipantType.NONE, 1));
    }
    
    private void testChangeFeedbackRecipient() {
        ______TS("click on Edit visibility then change recipient");

        feedbackEditPage = getFeedbackEditPage();

        assertTrue(feedbackEditPage.verifyVisibilityMessageIsDisplayed(1));
        assertFalse(feedbackEditPage.verifyVisibilityOptionsIsDisplayed(1));

        feedbackEditPage.clickQuestionEditForQuestion1();
        feedbackEditPage.enableOtherVisibilityOptions(1);
        feedbackEditPage.enableOtherFeedbackPathOptions(1);
        feedbackEditPage.selectRecipientToBe(FeedbackParticipantType.TEAMS, 1);

        assertTrue(feedbackEditPage.verifyVisibilityMessageIsDisplayed(1));
        assertTrue(feedbackEditPage.verifyVisibilityOptionsIsDisplayed(1));
    }
    
    private void testVisibilityOptionsCorrespondToFeedbackPath() {
        ______TS("Changing feedback path will enable/disable the corresponding visibility options");

        feedbackEditPage = getFeedbackEditPage();

        feedbackEditPage.clickQuestionEditForQuestion1();
        feedbackEditPage.enableOtherVisibilityOptions(1);

        ______TS("Default case: all options enabled");
        feedbackEditPage.selectGiverToBe(FeedbackParticipantType.STUDENTS, 1);
        feedbackEditPage.selectRecipientToBe(FeedbackParticipantType.STUDENTS, 1);

        assertEnabledVisibilityOptionsIncludesOnly(
                Arrays.asList(FeedbackParticipantType.RECEIVER, FeedbackParticipantType.OWN_TEAM_MEMBERS,
                              FeedbackParticipantType.RECEIVER_TEAM_MEMBERS, FeedbackParticipantType.STUDENTS,
                              FeedbackParticipantType.INSTRUCTORS),
                1);

        // testing recipientTypes
        ______TS("Selecting SELF as recipient disables options for RECEVIER and RECEIVER_TEAM_MEMBERS");
        feedbackEditPage.selectGiverToBe(FeedbackParticipantType.STUDENTS, 1);
        feedbackEditPage.selectRecipientToBe(FeedbackParticipantType.SELF, 1);

        assertEnabledVisibilityOptionsIncludesOnly(
                Arrays.asList(FeedbackParticipantType.OWN_TEAM_MEMBERS, FeedbackParticipantType.STUDENTS,
                              FeedbackParticipantType.INSTRUCTORS),
                1);

        ______TS("Selecting INSTRUCTORS as recipient disables option for RECEIVER_TEAM_MEMBERS");
        feedbackEditPage.selectGiverToBe(FeedbackParticipantType.STUDENTS, 1);
        feedbackEditPage.selectRecipientToBe(FeedbackParticipantType.INSTRUCTORS, 1);

        assertEnabledVisibilityOptionsIncludesOnly(
                Arrays.asList(FeedbackParticipantType.RECEIVER, FeedbackParticipantType.OWN_TEAM_MEMBERS,
                              FeedbackParticipantType.STUDENTS, FeedbackParticipantType.INSTRUCTORS),
                1);

        ______TS("Selecting OWN_TEAM as recipient disables options for RECEIVER and RECEIVER_TEAM_MEMBERS");
        feedbackEditPage.selectGiverToBe(FeedbackParticipantType.STUDENTS, 1);
        feedbackEditPage.selectRecipientToBe(FeedbackParticipantType.OWN_TEAM, 1);

        assertEnabledVisibilityOptionsIncludesOnly(
                Arrays.asList(FeedbackParticipantType.OWN_TEAM_MEMBERS, FeedbackParticipantType.STUDENTS,
                              FeedbackParticipantType.INSTRUCTORS),
                1);

        ______TS("Selecting NONE as recipient disables options for RECEIVER and RECEIVER_TEAM_MEMBERS");
        feedbackEditPage.selectGiverToBe(FeedbackParticipantType.STUDENTS, 1);
        feedbackEditPage.selectRecipientToBe(FeedbackParticipantType.NONE, 1);

        assertEnabledVisibilityOptionsIncludesOnly(
                Arrays.asList(FeedbackParticipantType.OWN_TEAM_MEMBERS, FeedbackParticipantType.STUDENTS,
                              FeedbackParticipantType.INSTRUCTORS),
                1);

        // testing giverTypes
        ______TS("Selecting SELF as giver disables option for OWN_TEAM_MEMBERS");
        feedbackEditPage.selectGiverToBe(FeedbackParticipantType.SELF, 1);
        feedbackEditPage.selectRecipientToBe(FeedbackParticipantType.STUDENTS, 1);

        assertEnabledVisibilityOptionsIncludesOnly(
                Arrays.asList(FeedbackParticipantType.RECEIVER, FeedbackParticipantType.RECEIVER_TEAM_MEMBERS,
                              FeedbackParticipantType.STUDENTS, FeedbackParticipantType.INSTRUCTORS),
                1);

        // testing specific giverType-recipientType combinations
        ______TS("Selecting SELF as giver and recipient further disables option for RECIPIENT_TEAM_MEMBERS");
        feedbackEditPage.selectGiverToBe(FeedbackParticipantType.SELF, 1);
        feedbackEditPage.selectRecipientToBe(FeedbackParticipantType.SELF, 1);

        assertEnabledVisibilityOptionsIncludesOnly(
                Arrays.asList(FeedbackParticipantType.STUDENTS, FeedbackParticipantType.INSTRUCTORS),
                1);

        ______TS("Selecting TEAMS as giver and OWN_TEAM_MEMBERS_INCLUDING_SELF as recipient further disables "
                 + "option for RECIPIENT");
        feedbackEditPage.selectGiverToBe(FeedbackParticipantType.TEAMS, 1);
        feedbackEditPage.selectRecipientToBe(FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF, 1);

        assertEnabledVisibilityOptionsIncludesOnly(
                Arrays.asList(FeedbackParticipantType.STUDENTS, FeedbackParticipantType.INSTRUCTORS),
                1);

    }

    private void assertEnabledVisibilityOptionsIncludesOnly(List<FeedbackParticipantType> expectedTypes,
                                                            int questionNumber) {
        Set<String> expectedEnabledOptions = new HashSet<String>();
        for (FeedbackParticipantType expectedType : expectedTypes) {
            expectedEnabledOptions.add(expectedType.toString());
        }

        Set<String> actualEnableOptions = new HashSet<String>();
        WebElement optionsTable = browser.driver.findElement(By.id("visibilityOptions-" + questionNumber));
        List<WebElement> enabledRows =
                optionsTable.findElements(By.cssSelector("tr:not([style='display: none;'])"));
        // remove the header row
        enabledRows.remove(0);
        for (WebElement enabledRow : enabledRows) {
            WebElement checkbox = enabledRow.findElement(By.cssSelector("input"));
            actualEnableOptions.add(checkbox.getAttribute("value"));
        }

        assertEquals(expectedEnabledOptions, actualEnableOptions);
    }

    private void testVisibilityOptionsUncheckedWhenHidden() {
        ______TS("Test visibility checkbox gets unchecked when hidden according to feedback path");
        feedbackEditPage = getFeedbackEditPage();

        feedbackEditPage.clickQuestionEditForQuestion1();
        feedbackEditPage.enableOtherVisibilityOptions(1);

        feedbackEditPage.selectGiverToBe(FeedbackParticipantType.STUDENTS, 1);
        feedbackEditPage.selectRecipientToBe(FeedbackParticipantType.STUDENTS, 1);

        feedbackEditPage.clickResponseVisibilityCheckBox("RECEIVER_TEAM_MEMBERS", 1);

        assertTrue("Expected checkbox to be checked",
                feedbackEditPage.isCheckboxChecked("answerCheckbox", "RECEIVER_TEAM_MEMBERS", 1));
        feedbackEditPage.selectRecipientToBe(FeedbackParticipantType.OWN_TEAM, 1);

        assertFalse("Expected checkbox to not be checked",
                feedbackEditPage.isCheckboxChecked("answerCheckbox", "RECEIVER_TEAM_MEMBERS", 1));
    }

    private void testAjaxOnVisibilityMessageButton() {
        ______TS("Test visibility message corresponds to visibility options");
        feedbackEditPage.clickEditQuestionButton(1);
        feedbackEditPage.enableOtherFeedbackPathOptions(1);
        feedbackEditPage.selectGiverToBe(FeedbackParticipantType.STUDENTS, 1);
        feedbackEditPage.selectRecipientToBe(FeedbackParticipantType.STUDENTS, 1);
        feedbackEditPage.enableOtherVisibilityOptions(1);
        feedbackEditPage.clickGiverNameVisibilityCheckBox("STUDENTS", 1);
        WebElement visibilityMessage1 = browser.driver.findElement(By.id("visibilityMessage-1"));
        feedbackEditPage.waitForElementVisibility(visibilityMessage1);
        assertTrue("Visibility message does not correspond to visibility options",
                   feedbackEditPage.getVisibilityMessage(1).contains("Other students in the course can see your response, "
                                                                     + "and your name, but not the name of the recipient"));

        ______TS("Test visibility message corresponds to visibility options: going from Others to a predefined option");
        feedbackEditPage.enableOtherFeedbackPathOptions(1);
        feedbackEditPage.selectGiverToBe(FeedbackParticipantType.STUDENTS, 1);
        feedbackEditPage.selectRecipientToBe(FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF, 1);
        feedbackEditPage.clickGiverNameVisibilityCheckBox("RECEIVER", 1);
        feedbackEditPage.clickGiverNameVisibilityCheckBox("OWN_TEAM_MEMBERS", 1);
        feedbackEditPage.clickGiverNameVisibilityCheckBox("STUDENTS", 1);
        feedbackEditPage.clickVisibilityDropdown("VISIBLE_TO_INSTRUCTORS_ONLY", 1);
        WebElement visibilityMessage2 = browser.driver.findElement(By.id("visibilityMessage-1"));
        feedbackEditPage.waitForElementVisibility(visibilityMessage2);
        assertFalse("Visibility message does not correspond to visibility options",
                   feedbackEditPage.getVisibilityMessage(1).contains("The receiving student"));

        ______TS("Failure case: ajax on clicking visibility message button");
        
        feedbackEditPage.changeQuestionTypeInForm(1, "InvalidQuestionType");
        feedbackEditPage.clickEditQuestionButton(1);
        feedbackEditPage.enableOtherVisibilityOptions(1);
        feedbackEditPage.waitForAjaxErrorOnVisibilityMessageButton(1);
    }

    private void testDeleteQuestionAction(int qnNumber) {
        
        ______TS("qn " + qnNumber + " delete then cancel");

        feedbackEditPage.clickDeleteQuestionLink(qnNumber);
        feedbackEditPage.waitForConfirmationModalAndClickCancel();
        assertNotNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, qnNumber));

        
        ______TS("qn " + qnNumber + " delete then accept");

        feedbackEditPage.clickDeleteQuestionLink(qnNumber);
        feedbackEditPage.waitForConfirmationModalAndClickOk();
        feedbackEditPage.verifyStatus(Const.StatusMessages.FEEDBACK_QUESTION_DELETED);
        assertNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, qnNumber));
        
    }

    private void testEditNonExistentQuestion() {

        ______TS("test editing a non-existent question");

        // Create a new question and save
        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionType("TEXT");
        feedbackEditPage.fillNewQuestionBox("new question");
        feedbackEditPage.fillNewQuestionDescription("more details");
        feedbackEditPage.clickAddQuestionButton();

        // Delete the new question through the backdoor so that it still appears in the browser
        String questionId = BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1).getId();
        String status = BackDoor.deleteFeedbackQuestion(questionId);
        assertEquals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS, status);

        // Edit the deleted question and save
        feedbackEditPage.clickEditQuestionButton(1);
        feedbackEditPage.fillEditQuestionBox("non-existent question", 1);
        feedbackEditPage.fillEditQuestionDescription("more details", 1);
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
        feedbackEditPage.fillNewQuestionDescription("more details");
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
        feedbackEditPage.enableOtherFeedbackPathOptions(1);
        feedbackEditPage.selectRecipientToBe(FeedbackParticipantType.TEAMS, 1);
        feedbackEditPage.clickquestionSaveForQuestion1();
        feedbackEditPage.waitForConfirmationModalAndClickOk();
        
        ______TS("check response rate after editing question");

        feedbacksPage = navigateToInstructorFeedbacksPage();
        feedbacksPage.waitForAjaxLoaderGifToDisappear();

        assertEquals("0 / 1", feedbacksPage.getResponseValue(courseId, feedbackSessionName));
        
        // Delete the question
        feedbackEditPage = getFeedbackEditPage();
        feedbackEditPage.clickDeleteQuestionLink(1);
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
        feedbackEditPage.fillNewQuestionDescription("more details");
        feedbackEditPage.enableOtherFeedbackPathOptionsForNewQuestion();
        feedbackEditPage.selectRecipientsToBeStudents();
        feedbackEditPage.clickAddQuestionButton();
        

        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionType("TEXT");
        assertTrue(feedbackEditPage.verifyNewEssayQuestionFormIsDisplayed());
        feedbackEditPage.fillNewQuestionBox("question for students");
        feedbackEditPage.fillNewQuestionDescription("more details");
        feedbackEditPage.enableOtherFeedbackPathOptionsForNewQuestion();
        feedbackEditPage.selectGiverToBeStudents();
        feedbackEditPage.clickAddQuestionButton();

        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionType("TEXT");
        assertTrue(feedbackEditPage.verifyNewEssayQuestionFormIsDisplayed());
        feedbackEditPage.fillNewQuestionBox("question for instructors");
        feedbackEditPage.fillNewQuestionDescription("more details");
        feedbackEditPage.enableOtherFeedbackPathOptionsForNewQuestion();
        feedbackEditPage.selectGiverToBeInstructors();
        feedbackEditPage.clickAddQuestionButton();

        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionType("TEXT");
        assertTrue(feedbackEditPage.verifyNewEssayQuestionFormIsDisplayed());
        feedbackEditPage.fillNewQuestionBox("question for students to instructors");
        feedbackEditPage.fillNewQuestionDescription("more details");
        feedbackEditPage.enableOtherFeedbackPathOptionsForNewQuestion();
        feedbackEditPage.selectGiverToBeStudents();
        feedbackEditPage.selectRecipientsToBeInstructors();
        feedbackEditPage.clickAddQuestionButton();

        ______TS("preview as instructor");

        feedbackEditPage.waitForElementPresence(By.id("button_preview_instructor"));
        FeedbackSubmitPage previewPage = feedbackEditPage.clickPreviewAsInstructorButton();
        previewPage.verifyHtmlMainContent("/instructorFeedbackSubmitPagePreview.html");
        previewPage.closeCurrentWindowAndSwitchToParentWindow();

        ______TS("preview as student");

        feedbackEditPage.waitForElementPresence(By.id("button_preview_student"));
        previewPage = feedbackEditPage.clickPreviewAsStudentButton();
        previewPage.verifyHtmlMainContent("/studentFeedbackSubmitPagePreview.html");
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
