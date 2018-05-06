package teammates.test.cases.browsertests;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.common.util.retry.MaximumRetriesExceededException;
import teammates.test.driver.BackDoor;
import teammates.test.driver.Priority;
import teammates.test.pageobjects.AppPage;
import teammates.test.pageobjects.FeedbackSubmitPage;
import teammates.test.pageobjects.InstructorFeedbackEditPage;
import teammates.test.pageobjects.InstructorFeedbackSessionsPage;

/**
 * SUT: {@link Const.ActionURIs#INSTRUCTOR_FEEDBACK_EDIT_PAGE}.
 */
@Priority(-1)
public class InstructorFeedbackEditPageUiTest extends BaseUiTestCase {
    private InstructorFeedbackEditPage feedbackEditPage;
    private String instructorId;
    private String courseId;
    private String feedbackSessionName;
    /** This contains data for the feedback session to be edited during testing. */
    private FeedbackSessionAttributes editedSession;

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/InstructorFeedbackEditPageUiTest.json");
        removeAndRestoreDataBundle(testData);

        editedSession = testData.feedbackSessions.get("openSession");
        editedSession.setGracePeriodMinutes(30);
        editedSession.setSessionVisibleFromTime(Const.TIME_REPRESENTS_FOLLOW_OPENING);
        editedSession.setResultsVisibleFromTime(Const.TIME_REPRESENTS_LATER);
        editedSession.setInstructions(new Text("Please fill in the edited feedback session."));
        editedSession.setEndTime(TimeHelper.parseInstant("2026-05-01 08:00 PM +0000"));

        instructorId = testData.accounts.get("instructorWithSessions").googleId;
        courseId = testData.courses.get("course").getId();
        feedbackSessionName = testData.feedbackSessions.get("openSession").getFeedbackSessionName();
    }

    @BeforeClass
    public void classSetup() {
        feedbackEditPage = getFeedbackEditPage();
    }

    @Test
    public void allTests() throws Exception {
        testContent();

        testEditSessionAction();

        testGeneralQuestionOperations();

        testPreviewSessionAction();

        testDoneEditingLink();

        testDeleteSessionAction();

        testSanitization();
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
        testEditWithEmptyQuestionTextThenDeleteQuestionAction(1);
        testEditWithInvalidNumericalTextThenDeleteQuestionAction();

        testEditNonExistentQuestion();

        testResponseRate();
    }

    private void testContent() throws Exception {

        ______TS("fresh new page");

        // This is the full HTML verification for Instructor Feedback Edit Page, the rest can all be verifyMainHtml
        feedbackEditPage.verifyHtml("/instructorFeedbackEditEmpty.html");
        assertTrue(feedbackEditPage.verifyEditSessionBoxIsEnabled());

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

        feedbackEditPage.editFeedbackSession(editedSession.getStartTimeLocal(), editedSession.getEndTimeLocal(),
                editedSession.getInstructions(), editedSession.getGracePeriodMinutes());

        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_SESSION_EDITED);
        assertTrue(feedbackEditPage.isElementInViewport(Const.ParamsNames.STATUS_MESSAGES_LIST));

        FeedbackSessionAttributes savedSession = getFeedbackSessionWithRetry(
                editedSession.getCourseId(), editedSession.getFeedbackSessionName());
        editedSession.setInstructions(new Text("<p>" + editedSession.getInstructionsString() + "</p>"));
        assertEquals(editedSession.toString(), savedSession.toString());
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_SESSION_EDITED);
        feedbackEditPage.reloadPage();
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackEditSuccess.html");

        ______TS("test two 'change' links' functionality to expand uncommon settings panels");

        By uncommonSettingsSection = By.id("uncommonSettingsSection");

        // test uncommon settings for 'send emails'
        feedbackEditPage.clickEditUncommonSettingsSendEmailsButton();
        feedbackEditPage.verifyEditSessionBoxIsEnabled();
        feedbackEditPage.toggleClosingSessionEmailReminderCheckbox();
        feedbackEditPage.clickSaveSessionButton();

        // The statement below is a 'dummy' but valid statement to wait for the data in back-end to be persistent
        // TODO: to implement a more sophisticated method to wait for the persistence of data
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_SESSION_EDITED);

        feedbackEditPage.reloadPage();
        // uncommon settings panel not in default will be automatically expanded
        feedbackEditPage.verifyHtmlPart(uncommonSettingsSection,
                                        "/instructorFeedbackEditUncommonSettingsSendEmails.html");

        // test uncommon settings for 'session responses visibility'
        feedbackEditPage.clickEditUncommonSettingsSessionResponsesVisibleButton();
        feedbackEditPage.verifyEditSessionBoxIsEnabled();
        feedbackEditPage.clickDefaultPublishTimeButton();
        feedbackEditPage.verifyHtmlPart(uncommonSettingsSection,
                                        "/instructorFeedbackEditUncommonSettingsSessionVisibility.html");
        feedbackEditPage.clickSaveSessionButton();

        // The statement below is a 'dummy' but valid statement to wait for the data in back-end to be persistent
        // TODO: to implement a more sophisticated method to wait for the persistence of data
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_SESSION_EDITED);
        testEditSessionLink();

        // test expanded uncommon settings section
        feedbackEditPage.reloadPage();
        // uncommon settings panel not in default will be automatically expanded
        feedbackEditPage.isElementVisible("sessionResponsesVisiblePanel");
        feedbackEditPage.isElementVisible("sendEmailsForPanel");

        // Restore defaults
        feedbackEditPage.clickManualPublishTimeButton();
        feedbackEditPage.toggleClosingSessionEmailReminderCheckbox();
        feedbackEditPage.clickSaveSessionButton();
        feedbackEditPage.reloadPage();

        ______TS("test edit page not changed after manual publish");

        // Do a backdoor 'manual' publish.
        editedSession.setResultsVisibleFromTime(Const.TIME_REPRESENTS_NOW);
        String status = BackDoor.editFeedbackSession(editedSession);
        assertEquals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS, status);

        feedbackEditPage = getFeedbackEditPage();

        // Ensuring that the settings did not default back to original values after manual publishing
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackEditManuallyPublished.html");

        // Restore defaults

        feedbackEditPage.clickEditUncommonSettingsSessionResponsesVisibleButton();
        feedbackEditPage.clickDefaultPublishTimeButton();
        feedbackEditPage.clickSaveSessionButton();

        ______TS("test ambiguous date times");
        feedbackEditPage = getFeedbackEditPageOfSessionIndDstCourse();
        FeedbackSessionAttributes dstSession = testData.feedbackSessions.get("dstSession");

        LocalDateTime overlapStartTime = TimeHelper.parseDateTimeFromSessionsForm("Sun, 05 Apr, 2015", "2", "0");
        LocalDateTime gapEndTime = TimeHelper.parseDateTimeFromSessionsForm("Sun, 01 Oct, 2017", "2", "0");

        feedbackEditPage.editFeedbackSession(overlapStartTime, gapEndTime,
                dstSession.getInstructions(), dstSession.getGracePeriodMinutes());

        String overlapStartWarning = String.format(Const.StatusMessages.AMBIGUOUS_LOCAL_DATE_TIME_OVERLAP,
                "start time", "Sun, 05 Apr 2015, 02:00 AM", "Sun, 05 Apr 2015, 02:00 AM AEDT (UTC+1100)",
                "Sun, 05 Apr 2015, 02:00 AM AEST (UTC+1000)", "Sun, 05 Apr 2015, 02:00 AM AEDT (UTC+1100)");
        String gapEndWarning = String.format(Const.StatusMessages.AMBIGUOUS_LOCAL_DATE_TIME_GAP,
                "end time", "Sun, 01 Oct 2017, 02:00 AM", "Sun, 01 Oct 2017, 03:00 AM AEDT (UTC+1100)");
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(
                overlapStartWarning, gapEndWarning, Const.StatusMessages.FEEDBACK_SESSION_EDITED);

        assertEquals("End time on form should be updated to 3am",
                "3", feedbackEditPage.getFeedbackSessionEndTimeValue());

        savedSession = getFeedbackSessionWithRetry(dstSession.getCourseId(), dstSession.getFeedbackSessionName());
        assertEquals("Saved end time should be 3am", 3, savedSession.getEndTimeLocal().getHour());

        ______TS("test end time earlier than start time");
        feedbackEditPage = getFeedbackEditPage();
        editedSession.setInstructions(new Text("Made some changes"));
        feedbackEditPage.editFeedbackSession(editedSession.getEndTimeLocal(), editedSession.getStartTimeLocal(),
                                        editedSession.getInstructions(), editedSession.getGracePeriodMinutes());

        String expectedString = "The end time for this feedback session cannot be earlier than the start time.";
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(expectedString);
    }

    private void testNewQuestionLink() {

        ______TS("new question (frame) link");
        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionTypeAndWaitForNewQuestionPanelReady("TEXT");
        assertTrue(feedbackEditPage.verifyNewEssayQuestionFormIsDisplayed());
        feedbackEditPage.verifyVisibilityMessageContainsForNewQuestion(
                "You can see your own feedback in the results page later on.");
        feedbackEditPage.verifyVisibilityMessageContainsForNewQuestion(
                "Instructors in this course can see your response, the name of the recipient, and your name.");
        assertTrue("Visibility preview for new question should be displayed",
                   feedbackEditPage.verifyVisibilityMessageIsDisplayedForNewQuestion());
    }

    private void testInputValidationForQuestion() {

        ______TS("empty question text");

        feedbackEditPage.clickAddQuestionButton();

        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_QUESTION_TEXTINVALID);

        ______TS("empty number of max respondents field");

        feedbackEditPage.fillQuestionTextBoxForNewQuestion("filled qn");
        feedbackEditPage.fillQuestionDescriptionForNewQuestion("more details");
        feedbackEditPage.enableOtherFeedbackPathOptionsForNewQuestion();
        feedbackEditPage.selectRecipientsToBeStudentsAndWaitForVisibilityMessageToLoad();
        feedbackEditPage.fillNumOfEntitiesToGiveFeedbackToBoxForNewQuestion("");
        feedbackEditPage.clickCustomNumberOfRecipientsButton();
        feedbackEditPage.clickAddQuestionButton();

        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(
                Const.StatusMessages.FEEDBACK_QUESTION_NUMBEROFENTITIESINVALID);

    }

    private void testAddQuestionAction() throws Exception {

        ______TS("add question action success");

        feedbackEditPage.clickMaxNumberOfRecipientsButton();
        feedbackEditPage.clickAddQuestionButton();

        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED);
        assertNotNull(getFeedbackQuestionWithRetry(courseId, feedbackSessionName, 1));
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
        feedbackEditPage.clickCustomNumberOfRecipientsButton();

        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackQuestionEditToTeamToTeam.html");

        ______TS("test visibility options of question 1");
        feedbackEditPage.clickSaveExistingQuestionButton(1);
        feedbackEditPage.clickEditQuestionButton(1);

        //TODO: use simple element checks instead of html checks after adding names to the checkboxes
        //      in the edit page (follow todo in instructorsFeedbackEdit.js)
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackQuestionVisibilityOptions.html");

        ______TS("test visibility preview of question 1");
        feedbackEditPage.enableOtherVisibilityOptions(1);
        WebElement visibilityMessage = browser.driver.findElement(By.id("visibilityMessage-1"));
        feedbackEditPage.waitForElementVisibility(visibilityMessage);

        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackQuestionVisibilityPreview.html");

        ______TS("test new question (frame) link copies custom number of recipients option");
        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionTypeAndWaitForNewQuestionPanelReady("TEXT");
        assertTrue(feedbackEditPage.isCustomNumOfRecipientsChecked());
        feedbackEditPage.clickDiscardChangesLinkForNewQuestion();
        feedbackEditPage.waitForConfirmationModalAndClickOk();

        //change back
        feedbackEditPage.enableOtherVisibilityOptions(1);
        feedbackEditPage.enableOtherFeedbackPathOptions(1);
        feedbackEditPage.selectGiverToBe(FeedbackParticipantType.SELF, 1);
        feedbackEditPage.selectRecipientToBe(FeedbackParticipantType.STUDENTS, 1);
        feedbackEditPage.clickMaxNumberOfRecipientsButton();
        feedbackEditPage.clickSaveExistingQuestionButton(1);

        ______TS("test new question (frame) link copies max number of recipients option");
        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionTypeAndWaitForNewQuestionPanelReady("TEXT");
        assertTrue(feedbackEditPage.isMaxNumOfRecipientsChecked());
        feedbackEditPage.clickDiscardChangesLinkForNewQuestion();
        feedbackEditPage.waitForConfirmationModalAndClickOk();

        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionTypeAndWaitForNewQuestionPanelReady("TEXT");
        feedbackEditPage.waitForElementVisibility(browser.driver.findElement(By.id("questionTable--1")));
        feedbackEditPage.enableOtherVisibilityOptionsForNewQuestion();
        feedbackEditPage.clickResponseVisibilityCheckBoxForNewQuestion("RECEIVER_TEAM_MEMBERS");

        feedbackEditPage.verifyVisibilityMessageContainsForNewQuestion(
                "The recipient's team members can see your response, but not the name of the recipient, or your name.");
        feedbackEditPage.enableOtherFeedbackPathOptionsForNewQuestion();
        feedbackEditPage.selectRecipientTypeForNewQuestionAndWaitForVisibilityMessageToLoad("Instructors in the course");

        feedbackEditPage.verifyVisibilityMessageDoesNotContainForNewQuestion(
                "The recipient's team members can see your response, but not the name of the recipient, or your name.");

        feedbackEditPage.clickDiscardChangesLinkForNewQuestion();
        feedbackEditPage.waitForConfirmationModalAndClickCancel();

        ______TS("add question 2 and edit it to giver's team members and giver");
        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.fillQuestionTextBoxForNewQuestion("test visibility when choosing giver's team members and giver");
        feedbackEditPage.fillQuestionDescriptionForNewQuestion(
                "<h3 style=\"text-align: center;\"><strong>Description</strong></h3><hr /><p>&nbsp;</p>");
        feedbackEditPage.enableOtherFeedbackPathOptionsForNewQuestion();
        feedbackEditPage.selectGiverToBeStudentsAndWaitForVisibilityMessageToLoad();
        feedbackEditPage.selectRecipientsToBeGiverTeamMembersAndGiverAndWaitForVisibilityMessageToLoad();
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
        feedbackEditPage.verifyVisibilityMessageContains(2, "The receiving student can see your response, and your name.");

        feedbackEditPage.verifyVisibilityMessageContains(2,
                "Instructors in this course can see your response, the name of the recipient, and your name.");

        feedbackEditPage.clickDeleteQuestionLink(2);
        feedbackEditPage.waitForConfirmationModalAndClickOk();
    }

    private void testCancelAddingNewQuestion() {
        ______TS("Cancelling the adding of a new question");

        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionTypeAndWaitForNewQuestionPanelReady("MCQ");

        ______TS("Click cancel but click no to confirmation prompt");
        feedbackEditPage.clickDiscardChangesLinkForNewQuestion();
        feedbackEditPage.waitForConfirmationModalAndClickCancel();
        assertTrue(feedbackEditPage.verifyNewMcqQuestionFormIsDisplayed());

        ______TS("Click cancel and click yes to confirmation prompt");
        feedbackEditPage.clickDiscardChangesLinkForNewQuestion();
        feedbackEditPage.waitForConfirmationModalAndClickOk();
        assertFalse(feedbackEditPage.verifyNewMcqQuestionFormIsDisplayed());

        ______TS("Make sure controls disabled by Team Contribution questions are re-enabled after cancelling");
        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionTypeAndWaitForNewQuestionPanelReady("CONTRIB");
        feedbackEditPage.clickDiscardChangesLinkForNewQuestion();
        feedbackEditPage.waitForConfirmationModalAndClickOk();

        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionTypeAndWaitForNewQuestionPanelReady("NUMSCALE");
        assertTrue(feedbackEditPage.isAllFeedbackPathOptionsEnabledForNewQuestion());
        assertTrue(feedbackEditPage.isAllVisibilityOptionsEnabledForNewQuestion());

        feedbackEditPage.clickDiscardChangesLinkForNewQuestion();
        feedbackEditPage.waitForConfirmationModalAndClickOk();

    }

    private void testCancelEditQuestion() {
        ______TS("Canceling the edit of an existing question");
        int qnIndex = 1;
        String qnTextOriginal = "mcq qn";

        // Add 2 questions
        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionTypeAndWaitForNewQuestionPanelReady("MCQ");
        feedbackEditPage.fillQuestionTextBoxForNewQuestion(qnTextOriginal);
        feedbackEditPage.fillQuestionDescriptionForNewQuestion("more details");
        feedbackEditPage.fillMcqOptionForNewQuestion(0, "Choice 1");
        feedbackEditPage.fillMcqOptionForNewQuestion(1, "Choice 2");
        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionTypeAndWaitForNewQuestionPanelReady("TEXT");
        feedbackEditPage.fillQuestionTextBoxForNewQuestion("Question text for question number 2");
        feedbackEditPage.fillQuestionDescriptionForNewQuestion("more details");
        feedbackEditPage.clickAddQuestionButton();

        // Enable edit mode before testing canceling
        feedbackEditPage.clickEditQuestionButton(qnIndex);

        ______TS("Click cancel but click no to confirmation prompt");
        feedbackEditPage.clickDiscardChangesLink(qnIndex);
        feedbackEditPage.waitForConfirmationModalAndClickCancel();
        assertTrue(feedbackEditPage.isDiscardChangesButtonVisible(qnIndex));
        assertTrue(feedbackEditPage.isQuestionEnabled(qnIndex));

        ______TS("Click cancel and click yes to confirmation prompt");
        feedbackEditPage.fillQuestionTextBox("new edits to question text", qnIndex);
        feedbackEditPage.fillQuestionDescription("more details", qnIndex);
        String qnTextAfterEdit = feedbackEditPage.getQuestionBoxText(qnIndex);
        assertFalse(qnTextOriginal.equals(qnTextAfterEdit));

        feedbackEditPage.clickDiscardChangesLink(qnIndex);
        feedbackEditPage.waitForConfirmationModalAndClickOk();
        assertFalse(feedbackEditPage.isDiscardChangesButtonVisible(qnIndex));
        assertFalse(feedbackEditPage.isQuestionEnabled(qnIndex));
        assertFalse(feedbackEditPage.isSelectQuestionNumberEnabled(qnIndex));
        String qnTextAfterCancelEdit = feedbackEditPage.getQuestionBoxText(qnIndex);
        assertEquals(qnTextOriginal, qnTextAfterCancelEdit);

        ______TS("Try cancelling changes to question number");
        qnIndex++;
        feedbackEditPage.clickEditQuestionButton(qnIndex);
        feedbackEditPage.selectQuestionNumber(qnIndex, 1);
        feedbackEditPage.clickDiscardChangesLink(qnIndex);
        feedbackEditPage.waitForConfirmationModalAndClickOk();
        assertEquals(qnIndex, feedbackEditPage.getSelectedQuestionNumber(qnIndex));
        qnIndex--;

        ______TS("Try re-editing a question after cancelling, making sure that form controls still work");
        feedbackEditPage.clickEditQuestionButton(qnIndex);
        assertTrue(feedbackEditPage.isSelectQuestionNumberEnabled(qnIndex));
        feedbackEditPage.enableOtherFeedbackPathOptions(qnIndex);
        feedbackEditPage.selectRecipientsToBeStudentsAndWaitForVisibilityMessageToLoad(qnIndex);
        assertTrue(feedbackEditPage.isOptionForSelectingNumberOfEntitiesVisible(qnIndex));

        // Delete both questions to reset the status for the following tests
        feedbackEditPage.clickDeleteQuestionLink(qnIndex);
        feedbackEditPage.waitForConfirmationModalAndClickOk();
        feedbackEditPage.clickDeleteQuestionLink(qnIndex);
        feedbackEditPage.waitForConfirmationModalAndClickOk();
    }

    private void testEditQuestionNumberAction() throws MaximumRetriesExceededException {
        ______TS("edit question number success");

        feedbackEditPage.clickEditQuestionButton(2);
        feedbackEditPage.selectQuestionNumber(2, 1);
        feedbackEditPage.clickSaveExistingQuestionButton(2);
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_QUESTION_EDITED);

        ______TS("questions still editable even if questions numbers became inconsistent");

        FeedbackQuestionAttributes firstQuestion = getFeedbackQuestionWithRetry(courseId, feedbackSessionName, 1);
        assertEquals(1, firstQuestion.questionNumber);

        FeedbackQuestionAttributes secondQuestion = getFeedbackQuestionWithRetry(courseId, feedbackSessionName, 2);
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
        String questionId = getFeedbackQuestionWithRetry(courseId, feedbackSessionName, 4).getId();
        BackDoor.deleteFeedbackQuestion(questionId);
        questionId = getFeedbackQuestionWithRetry(courseId, feedbackSessionName, 3).getId();
        BackDoor.deleteFeedbackQuestion(questionId);

    }

    private void testChangeFeedbackGiver() {
        feedbackEditPage = getFeedbackEditPage();
        feedbackEditPage.clickQuestionEditForQuestion(1);

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
        assertEquals("SELF", feedbackEditPage.getRecipientTypeForQuestion(1));

        feedbackEditPage.selectGiverToBe(FeedbackParticipantType.STUDENTS, 1);
        feedbackEditPage.selectRecipientToBe(FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF, 1);
        feedbackEditPage.selectGiverToBe(FeedbackParticipantType.SELF, 1);
        assertEquals("SELF", feedbackEditPage.getRecipientTypeForQuestion(1));

        feedbackEditPage.selectGiverToBe(FeedbackParticipantType.STUDENTS, 1);
        feedbackEditPage.selectRecipientToBe(FeedbackParticipantType.OWN_TEAM_MEMBERS, 1);
        feedbackEditPage.selectGiverToBe(FeedbackParticipantType.INSTRUCTORS, 1);
        assertEquals("SELF", feedbackEditPage.getRecipientTypeForQuestion(1));

        feedbackEditPage.selectGiverToBe(FeedbackParticipantType.STUDENTS, 1);
        feedbackEditPage.selectRecipientToBe(FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF, 1);
        feedbackEditPage.selectGiverToBe(FeedbackParticipantType.INSTRUCTORS, 1);
        assertEquals("SELF", feedbackEditPage.getRecipientTypeForQuestion(1));

        feedbackEditPage.selectGiverToBe(FeedbackParticipantType.STUDENTS, 1);
        feedbackEditPage.selectRecipientToBe(FeedbackParticipantType.OWN_TEAM, 1);
        feedbackEditPage.selectGiverToBe(FeedbackParticipantType.TEAMS, 1);
        assertEquals("SELF", feedbackEditPage.getRecipientTypeForQuestion(1));

        feedbackEditPage.selectGiverToBe(FeedbackParticipantType.STUDENTS, 1);
        feedbackEditPage.selectRecipientToBe(FeedbackParticipantType.OWN_TEAM_MEMBERS, 1);
        feedbackEditPage.selectGiverToBe(FeedbackParticipantType.TEAMS, 1);
        assertEquals("SELF", feedbackEditPage.getRecipientTypeForQuestion(1));

        ______TS("add new question");
        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionTypeAndWaitForNewQuestionPanelReady("TEXT");

        assertEquals("TEAMS", feedbackEditPage.getGiverTypeForQuestion(1));
        assertEquals("SELF", feedbackEditPage.getRecipientTypeForQuestion(1));

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

        feedbackEditPage.clickQuestionEditForQuestion(1);
        feedbackEditPage.enableOtherVisibilityOptions(1);
        feedbackEditPage.enableOtherFeedbackPathOptions(1);
        feedbackEditPage.selectRecipientToBe(FeedbackParticipantType.TEAMS, 1);

        assertTrue(feedbackEditPage.verifyVisibilityMessageIsDisplayed(1));
        assertTrue(feedbackEditPage.verifyVisibilityOptionsIsDisplayed(1));
    }

    private void testVisibilityOptionsCorrespondToFeedbackPath() {
        ______TS("Changing feedback path will enable/disable the corresponding visibility options");

        feedbackEditPage = getFeedbackEditPage();

        feedbackEditPage.clickQuestionEditForQuestion(1);
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

    private void testSanitization() throws IOException {
        ______TS("Test sanitization for edit page");

        instructorId = testData.accounts.get("instructor1OfTestingSanitizationCourse").googleId;
        FeedbackSessionAttributes session = testData.feedbackSessions.get("session1InTestingSanitizationCourse");
        courseId = session.getCourseId();
        feedbackSessionName = session.getFeedbackSessionName();

        feedbackEditPage = getFeedbackEditPage();
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackEditPageTestingSanitization.html");

        ______TS("Test sanitization for copy question modal");

        feedbackEditPage.clickCopyButton();
        feedbackEditPage.waitForCopyTableToLoad();
        feedbackEditPage.verifyHtmlPart(By.id("copyModal"), "/instructorFeedbackCopyQuestionModalTestingSanitization.html");
    }

    private void assertEnabledVisibilityOptionsIncludesOnly(List<FeedbackParticipantType> expectedTypes,
                                                            int questionNumber) {
        Set<String> expectedEnabledOptions = new HashSet<>();
        for (FeedbackParticipantType expectedType : expectedTypes) {
            expectedEnabledOptions.add(expectedType.toString());
        }

        Set<String> actualEnableOptions = new HashSet<>();
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

        feedbackEditPage.clickQuestionEditForQuestion(1);
        feedbackEditPage.enableOtherVisibilityOptions(1);

        feedbackEditPage.selectGiverToBe(FeedbackParticipantType.STUDENTS, 1);
        feedbackEditPage.selectRecipientToBe(FeedbackParticipantType.STUDENTS, 1);

        feedbackEditPage.clickResponseVisibilityCheckBox("RECEIVER_TEAM_MEMBERS", 1);

        assertTrue("Expected checkbox to be checked",
                feedbackEditPage.isCheckboxChecked("answerCheckbox", "RECEIVER_TEAM_MEMBERS", 1));
        feedbackEditPage.selectRecipientToBe(FeedbackParticipantType.OWN_TEAM, 1);

        assertFalse("Expected checkbox to not be checked",
                feedbackEditPage.isCheckboxChecked("answerCheckbox", "RECEIVER_TEAM_MEMBERS", 1));

        ______TS("Test other checkboxes retain their state (only visibility checkboxes affected)");
        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionTypeAndWaitForNewQuestionPanelReady("RANK_OPTIONS");
        feedbackEditPage.tickDuplicatesAllowedCheckboxForNewQuestion();
        feedbackEditPage.clickVisibilityDropdownForNewQuestionAndWaitForVisibilityMessageToLoad(
                "ANONYMOUS_TO_RECIPIENT_AND_INSTRUCTORS");
        assertTrue("Expected checkbox to remain checked",
                feedbackEditPage.isRankDuplicatesAllowedCheckedForNewQuestion());
    }

    private void testAjaxOnVisibilityMessageButton() {
        ______TS("Test visibility message corresponds to visibility options");
        feedbackEditPage.clickEditQuestionButton(1);
        feedbackEditPage.enableOtherFeedbackPathOptions(1);
        feedbackEditPage.selectGiverToBe(FeedbackParticipantType.STUDENTS, 1);
        feedbackEditPage.selectRecipientToBe(FeedbackParticipantType.STUDENTS, 1);
        feedbackEditPage.enableOtherVisibilityOptions(1);
        feedbackEditPage.clickGiverNameVisibilityCheckBox("STUDENTS", 1);
        feedbackEditPage.verifyVisibilityMessageContains(1,
                "Other students in the course can see your response, and your name, but not the name of the recipient");

        ______TS("Test visibility message corresponds to visibility options: going from Others to a predefined option");
        feedbackEditPage.enableOtherFeedbackPathOptions(1);
        feedbackEditPage.selectGiverToBe(FeedbackParticipantType.STUDENTS, 1);
        feedbackEditPage.selectRecipientToBe(FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF, 1);
        feedbackEditPage.clickGiverNameVisibilityCheckBox("RECEIVER", 1);
        feedbackEditPage.clickGiverNameVisibilityCheckBox("OWN_TEAM_MEMBERS", 1);
        feedbackEditPage.clickGiverNameVisibilityCheckBox("STUDENTS", 1);
        feedbackEditPage.clickVisibilityDropdownAndWaitForVisibilityMessageToLoad("VISIBLE_TO_INSTRUCTORS_ONLY", 1);
        feedbackEditPage.verifyVisibilityMessageDoesNotContain(1, "The receiving student");

        ______TS("Failure case: ajax on clicking visibility message button");

        feedbackEditPage.changeQuestionTypeInForm(1, "InvalidQuestionType");
        feedbackEditPage.clickEditQuestionButton(1);
        feedbackEditPage.enableOtherVisibilityOptions(1);
        feedbackEditPage.waitForAjaxErrorOnVisibilityMessageButton(1);
    }

    private void testDeleteQuestionAction(int qnNumber) throws MaximumRetriesExceededException {

        ______TS("qn " + qnNumber + " delete then cancel");

        feedbackEditPage.clickDeleteQuestionLink(qnNumber);
        feedbackEditPage.waitForConfirmationModalAndClickCancel();
        assertNotNull(getFeedbackQuestionWithRetry(courseId, feedbackSessionName, qnNumber));

        ______TS("qn " + qnNumber + " delete then accept");

        feedbackEditPage.clickDeleteQuestionLink(qnNumber);
        feedbackEditPage.waitForConfirmationModalAndClickOk();
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_QUESTION_DELETED);
        assertNull(getFeedbackQuestion(courseId, feedbackSessionName, qnNumber));
    }

    private void testEditWithEmptyQuestionTextThenDeleteQuestionAction(int qnNumber)
            throws MaximumRetriesExceededException {
        ______TS("qn " + qnNumber + " edit the question with invalid input then delete question");

        feedbackEditPage.clickEditQuestionButton(qnNumber);
        feedbackEditPage.fillQuestionTextBox("", qnNumber);
        feedbackEditPage.clickSaveExistingQuestionButton(qnNumber);
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_QUESTION_TEXTINVALID);

        feedbackEditPage.clickDeleteQuestionLink(qnNumber);
        feedbackEditPage.waitForConfirmationModalAndClickOk();
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_QUESTION_DELETED);
        assertNull(getFeedbackQuestion(courseId, feedbackSessionName, qnNumber));
    }

    private void testEditWithInvalidNumericalTextThenDeleteQuestionAction()
            throws MaximumRetriesExceededException {
        ______TS("qn 1 edit the question with invalid input without saving then delete question");

        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionTypeAndWaitForNewQuestionPanelReady("NUMSCALE");
        feedbackEditPage.fillQuestionTextBoxForNewQuestion("filled qn");
        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED);

        feedbackEditPage.clickEditQuestionButton(1);
        feedbackEditPage.fillMinNumScaleBox("", 1);
        feedbackEditPage.fillMaxNumScaleBox("", 1);
        feedbackEditPage.fillStepNumScaleBox("", 1);
        feedbackEditPage.clickDeleteQuestionLink(1);
        feedbackEditPage.waitForConfirmationModalAndClickOk();
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_QUESTION_DELETED);
        assertNull(getFeedbackQuestion(courseId, feedbackSessionName, 1));
    }

    private void testEditNonExistentQuestion() throws MaximumRetriesExceededException {

        ______TS("test editing a non-existent question");

        // Create a new question and save
        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionTypeAndWaitForNewQuestionPanelReady("TEXT");
        feedbackEditPage.fillQuestionTextBoxForNewQuestion("new question");
        feedbackEditPage.fillQuestionDescriptionForNewQuestion("more details");
        feedbackEditPage.clickAddQuestionButton();

        // Delete the new question through the backdoor so that it still appears in the browser
        String questionId = getFeedbackQuestionWithRetry(courseId, feedbackSessionName, 1).getId();
        String status = BackDoor.deleteFeedbackQuestion(questionId);
        assertEquals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS, status);

        // Edit the deleted question and save
        feedbackEditPage.clickEditQuestionButton(1);
        feedbackEditPage.fillQuestionTextBox("non-existent question", 1);
        feedbackEditPage.fillQuestionDescription("more details", 1);
        feedbackEditPage.clickSaveExistingQuestionButton(1);

        AppUrl expectedRedirectUrl = createUrl("/entityNotFoundPage.jsp");
        expectedRedirectUrl.withParam(Const.ParamsNames.ERROR_FEEDBACK_URL_REQUESTED,
                Const.ActionURIs.INSTRUCTOR_FEEDBACK_QUESTION_EDIT);

        assertTrue(browser.driver.getCurrentUrl().contains(expectedRedirectUrl.toAbsoluteString()));

        // Restore feedbackEditPage
        feedbackEditPage = getFeedbackEditPage();
    }

    private void testResponseRate() {

        // Create a new question and save
        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionTypeAndWaitForNewQuestionPanelReady("TEXT");
        feedbackEditPage.fillQuestionTextBoxForNewQuestion("new question");
        feedbackEditPage.fillQuestionDescriptionForNewQuestion("more details");
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

        InstructorFeedbackSessionsPage feedbacksPage = navigateToInstructorFeedbackSessionsPage();
        feedbacksPage.waitForAjaxLoaderGifToDisappear();

        feedbacksPage.clickViewResponseLink(courseId, feedbackSessionName);
        feedbacksPage.verifyResponseValue("1 / 1", courseId, feedbackSessionName);

        ______TS("check warning is displayed while editing visibility options of question with existing response");

        // Change the feedback path of the question and save
        feedbackEditPage = getFeedbackEditPage();
        assertTrue(feedbackEditPage.isAlertClassEnabledForVisibilityOptions(1));
        feedbackEditPage.clickEditQuestionButton(1);
        feedbackEditPage.enableOtherFeedbackPathOptions(1);
        feedbackEditPage.selectRecipientToBe(FeedbackParticipantType.TEAMS, 1);
        feedbackEditPage.clickSaveExistingQuestionButton(1);
        feedbackEditPage.waitForConfirmationModalAndClickOk();

        ______TS("check no warning displayed while editing visibility options of question without responses");

        assertFalse(feedbackEditPage.isAlertClassEnabledForVisibilityOptions(1));

        ______TS("check response rate after editing question");

        feedbacksPage = navigateToInstructorFeedbackSessionsPage();
        feedbacksPage.waitForAjaxLoaderGifToDisappear();

        feedbacksPage.clickViewResponseLink(courseId, feedbackSessionName);
        feedbacksPage.verifyResponseValue("0 / 1", courseId, feedbackSessionName);

        // Delete the question
        feedbackEditPage = getFeedbackEditPage();
        feedbackEditPage.clickDeleteQuestionLink(1);
        feedbackEditPage.waitForConfirmationModalAndClickOk();
    }

    private InstructorFeedbackSessionsPage navigateToInstructorFeedbackSessionsPage() {

        AppUrl feedbacksPageUrl = createUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_SESSIONS_PAGE).withUserId(instructorId);
        InstructorFeedbackSessionsPage feedbacksPage =
                AppPage.getNewPageInstance(browser, feedbacksPageUrl, InstructorFeedbackSessionsPage.class);
        feedbacksPage.waitForPageToLoad();
        return feedbacksPage;
    }

    private void testPreviewSessionAction() throws Exception {

        // add questions for previewing
        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionTypeAndWaitForNewQuestionPanelReady("TEXT");
        assertTrue(feedbackEditPage.verifyNewEssayQuestionFormIsDisplayed());
        feedbackEditPage.fillQuestionTextBoxForNewQuestion("question for me");
        feedbackEditPage.fillQuestionDescriptionForNewQuestion("more details");
        feedbackEditPage.enableOtherFeedbackPathOptionsForNewQuestion();
        feedbackEditPage.selectRecipientsToBeStudentsAndWaitForVisibilityMessageToLoad();
        feedbackEditPage.clickAddQuestionButton();

        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionTypeAndWaitForNewQuestionPanelReady("TEXT");
        assertTrue(feedbackEditPage.verifyNewEssayQuestionFormIsDisplayed());
        feedbackEditPage.fillQuestionTextBoxForNewQuestion("question for students");
        feedbackEditPage.fillQuestionDescriptionForNewQuestion("more details");
        feedbackEditPage.enableOtherFeedbackPathOptionsForNewQuestion();
        feedbackEditPage.selectGiverToBeStudentsAndWaitForVisibilityMessageToLoad();
        feedbackEditPage.clickAddQuestionButton();

        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionTypeAndWaitForNewQuestionPanelReady("TEXT");
        assertTrue(feedbackEditPage.verifyNewEssayQuestionFormIsDisplayed());
        feedbackEditPage.fillQuestionTextBoxForNewQuestion("question for instructors");
        feedbackEditPage.fillQuestionDescriptionForNewQuestion("more details");
        feedbackEditPage.enableOtherFeedbackPathOptionsForNewQuestion();
        feedbackEditPage.selectGiverToBeInstructorsAndWaitForVisibilityMessageToLoad();
        feedbackEditPage.clickAddQuestionButton();

        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionTypeAndWaitForNewQuestionPanelReady("TEXT");
        assertTrue(feedbackEditPage.verifyNewEssayQuestionFormIsDisplayed());
        feedbackEditPage.fillQuestionTextBoxForNewQuestion("question for students to instructors");
        feedbackEditPage.fillQuestionDescriptionForNewQuestion("more details");
        feedbackEditPage.enableOtherFeedbackPathOptionsForNewQuestion();
        feedbackEditPage.selectGiverToBeStudentsAndWaitForVisibilityMessageToLoad();
        feedbackEditPage.selectRecipientsToBeInstructorsAndWaitForVisibilityMessageToLoad();
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
        InstructorFeedbackSessionsPage feedbackPage = feedbackEditPage.clickDoneEditingLink();

        ______TS("Compare URLs");

        String expectedRedirectUrl = createUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_SESSIONS_PAGE)
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

    private void testDeleteSessionAction() throws MaximumRetriesExceededException {

        ______TS("session delete then cancel");

        feedbackEditPage.clickAndCancel(feedbackEditPage.getDeleteSessionLink());
        assertNotNull(getFeedbackSessionWithRetry(courseId, feedbackSessionName));

        ______TS("session delete then accept");

        // check redirect to main feedback page
        InstructorFeedbackSessionsPage feedbackPage = feedbackEditPage.deleteSession();
        assertTrue(feedbackPage.getTextsForAllStatusMessagesToUser()
                .contains(Const.StatusMessages.FEEDBACK_SESSION_DELETED));
        assertNull(getFeedbackSession(courseId, feedbackSessionName));
    }

    private InstructorFeedbackEditPage getFeedbackEditPage() {
        AppUrl feedbackPageLink = createUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE)
                                    .withUserId(instructorId)
                                    .withCourseId(courseId)
                                    .withSessionName(feedbackSessionName)
                                    .withEnableSessionEditDetails(true);
        return loginAdminToPage(feedbackPageLink, InstructorFeedbackEditPage.class);
    }

    private InstructorFeedbackEditPage getFeedbackEditPageOfCourseWithoutQuestions() {
        String instructor = testData.instructors.get("teammates.test.instructor3").googleId;
        String courseWithoutQuestion = testData.courses.get("course2").getId();
        String sessionWithoutQuestions = testData.feedbackSessions.get("openSession3")
                                                                  .getFeedbackSessionName();
        AppUrl feedbackPageLink = createUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE)
                                    .withUserId(instructor)
                                    .withCourseId(courseWithoutQuestion)
                                    .withSessionName(sessionWithoutQuestions)
                                    .withEnableSessionEditDetails(true);
        return loginAdminToPage(feedbackPageLink, InstructorFeedbackEditPage.class);
    }

    private InstructorFeedbackEditPage getFeedbackEditPageOfSessionIndDstCourse() {
        AppUrl feedbackPageLink = createUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE)
                .withUserId(testData.instructors.get("instructorOfDstCourse").googleId)
                .withCourseId(testData.courses.get("courseWithDstTimeZone").getId())
                .withSessionName(testData.feedbackSessions.get("dstSession").getFeedbackSessionName())
                .withEnableSessionEditDetails(true);
        return loginAdminToPage(feedbackPageLink, InstructorFeedbackEditPage.class);
    }

}
