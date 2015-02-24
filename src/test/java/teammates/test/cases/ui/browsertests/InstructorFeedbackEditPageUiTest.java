package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertTrue;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackParticipantType;
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
        
        // No testDeleteQuestionAction() method as this is tested separately
        // for each of the question-types later
        
        testGetQuestionLink();

        testTwoNewEssayQuestionsWithRecipientBeingNobodySpecific();

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
        testEditNumScaleQuestionNumberAction();
        testDeleteNumScaleQuestionAction();        
        
        testNewConstSumOptionQuestionFrame();
        testInputValidationForConstSumOptionQuestion();
        testCustomizeConstSumOptionOptions();
        testAddConstSumOptionQuestionAction();
        testEditConstSumOptionQuestionAction();
        testDeleteConstSumOptionQuestionAction();
    
        testNewConstSumRecipientQuestionFrame();
        testInputValidationForConstSumRecipientQuestion();
        testCustomizeConstSumRecipientOptions();
        testAddConstSumRecipientQuestionAction();
        testEditConstSumRecipientQuestionAction();
        testDeleteConstSumRecipientQuestionAction();
        
        //Contribution questions tests moved to own test class.
        
        testCopyQuestion();
      
        testPreviewSessionAction();

        testDoneEditingLink();
        
        testChangeFeedbackRecipient();
        
        testDeleteSessionAction();
    }

    private void testContent() throws Exception{

        ______TS("no questions");

        feedbackEditPage = getFeedbackEditPage();
        //Verify Html instead of main content to verify copy panel and preview panel
        feedbackEditPage.verifyHtml("/instructorFeedbackEditEmpty.html");
    }

    private void testEditSessionLink(){
        ______TS("edit session link");
        feedbackEditPage.clickEditSessionButton();
        assertTrue(feedbackEditPage.verifyEditSessionBoxIsEnabled());
        
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
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackEditSuccess.html");


        ______TS("test edit page after manual publish");

        // Do a backdoor 'manual' publish.
        editedSession.resultsVisibleFromTime = Const.TIME_REPRESENTS_NOW;
        String status = BackDoor.editFeedbackSession(editedSession);
        assertEquals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS, status);

        feedbackEditPage = getFeedbackEditPage();
        feedbackEditPage.isElementSelected(Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON + "_atvisible");
        //feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackEditPublished.html");
        // Restore defaults
        feedbackEditPage.clickEditSessionButton();
        

        feedbackEditPage.clickEditUncommonSettingsButton();
        feedbackEditPage.clickDefaultPublishTimeButton();
        feedbackEditPage.clickSaveSessionButton();
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
        WebElement visibilityMessage = browser.driver.findElement(By.className("visibilityMessageButton"));
        feedbackEditPage.waitForElementVisible(visibilityMessage);

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
        String questionId = BackDoor.getFeedbackQuestion(courseId,
                feedbackSessionName, 1).getId();
        
        String expectedUrl = TestProperties.inst().TEAMMATES_URL
                + Const.ActionURIs.INSTRUCTOR_FEEDBACK_QUESTION_SUBMISSION_EDIT_PAGE
                + "?courseid=" + courseId
                + "&fsname=First+Session"
                + "&questionid=" + questionId;

        assertTrue(feedbackEditPage.isElementVisible("statusMessage"));
        assertEquals(
                "Link for question 1: " + expectedUrl,
                feedbackEditPage.getStatus());
        
        Url url = new Url(expectedUrl).withUserId(instructorId);
        FeedbackQuestionSubmitPage questionPage = loginAdminToPage(browser, url, FeedbackQuestionSubmitPage.class);
        
        assertTrue(questionPage.isCorrectPage(courseId, feedbackSessionName));
        
        feedbackEditPage = getFeedbackEditPage();
    }

    private void testTwoNewEssayQuestionsWithRecipientBeingNobodySpecific() {

        ______TS("essay questions with recipient being nobody specific");

        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.fillQuestionBox("filled qn");
        feedbackEditPage.selectRecipientsToBeNobodySpecific();
        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.clickNewQuestionButton();
        
        feedbackEditPage.fillQuestionBox("filled qn");
        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.clickAndConfirm(feedbackEditPage.getDeleteQuestionLink(2));
        feedbackEditPage.clickAndConfirm(feedbackEditPage.getDeleteQuestionLink(2));
    }
    
    @SuppressWarnings("unused")
    private void ____MCQ_methods___________________________________() {
    }

    private void testNewMcqQuestionFrame() {

        ______TS("MCQ: new question (frame) link");

        feedbackEditPage.selectNewQuestionType("Multiple-choice (single answer) question");
        feedbackEditPage.clickNewQuestionButton();
        assertTrue(feedbackEditPage.verifyNewMcqQuestionFormIsDisplayed());
    }

    private void testInputValidationForMcqQuestion() {

        ______TS("empty question text");

        feedbackEditPage.clickAddQuestionButton();
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_TEXTINVALID, feedbackEditPage.getStatus());
        
        ______TS("empty options");

        feedbackEditPage.fillQuestionBox("Test question text");
        feedbackEditPage.clickAddQuestionButton();
        assertEquals("Too little choices for Multiple-choice (single answer) question. Minimum number of options is: 2.", feedbackEditPage.getStatus());
        
    }

    private void testCustomizeMcqOptions() {

        feedbackEditPage.selectNewQuestionType("Multiple-choice (single answer) question");
        feedbackEditPage.clickNewQuestionButton();
        
        feedbackEditPage.fillMcqOption(0, "Choice 1");
        feedbackEditPage.fillMcqOption(1, "Choice 2");

        ______TS("MCQ: add mcq option");
        
        assertEquals(false, feedbackEditPage.isElementPresent("mcqOptionRow-2--1"));
        feedbackEditPage.clickAddMoreMcqOptionLink();
        assertEquals(true, feedbackEditPage.isElementPresent("mcqOptionRow-2--1"));

        ______TS("MCQ: remove mcq option");

        feedbackEditPage.fillMcqOption(2, "Choice 3");
        assertEquals(true, feedbackEditPage.isElementPresent("mcqOptionRow-1--1"));
        feedbackEditPage.clickRemoveMcqOptionLink(1, -1);
        assertEquals(false, feedbackEditPage.isElementPresent("mcqOptionRow-1--1"));

        ______TS("MCQ: add mcq option after remove");

        feedbackEditPage.clickAddMoreMcqOptionLink();
        assertEquals(true, feedbackEditPage.isElementPresent("mcqOptionRow-3--1"));
        feedbackEditPage.clickAddMoreMcqOptionLink();
        feedbackEditPage.fillMcqOption(4, "Choice 5");
        assertEquals(true, feedbackEditPage.isElementPresent("mcqOptionRow-4--1"));
    }

    private void testAddMcqQuestionAction(){

        ______TS("MCQ: add question action success");

        feedbackEditPage.fillQuestionBox("mcq qn");
        feedbackEditPage.clickAddQuestionButton();
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED, feedbackEditPage.getStatus());
        assertNotNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 2));
        
        //NOTE: Tests feedback giver/recipient and visibility options are copied from previous question.
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackMcqQuestionAddSuccess.html");
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

        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackMcqQuestionEditSuccess.html");

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
    
    @SuppressWarnings("unused")
    private void ____MSQ_methods___________________________________() {
    }

    private void testNewMsqQuestionFrame() {

        ______TS("MSQ: new question (frame) link");

        feedbackEditPage.selectNewQuestionType("Multiple-choice (multiple answers) question");
        feedbackEditPage.clickNewQuestionButton();
        assertTrue(feedbackEditPage.verifyNewMsqQuestionFormIsDisplayed());
    }

    private void testInputValidationForMsqQuestion() {

        ______TS("empty question text");

        feedbackEditPage.clickAddQuestionButton();
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_TEXTINVALID, feedbackEditPage.getStatus());
        
        ______TS("empty options");

        feedbackEditPage.fillQuestionBox("Test question text");
        feedbackEditPage.clickAddQuestionButton();
        assertEquals("Too little choices for Multiple-choice (multiple answers) question. Minimum number of options is: 2.", feedbackEditPage.getStatus());
        
    }

    private void testCustomizeMsqOptions() {

        feedbackEditPage.selectNewQuestionType("Multiple-choice (multiple answers) question");
        feedbackEditPage.clickNewQuestionButton();
        
        feedbackEditPage.fillMsqOption(0, "Choice 1");
        feedbackEditPage.fillMsqOption(1, "Choice 2");
        
        ______TS("MSQ: add msq option");

        assertEquals(false, feedbackEditPage.isElementPresent("msqOptionRow-2--1"));
        feedbackEditPage.clickAddMoreMsqOptionLink();
        assertEquals(true, feedbackEditPage.isElementPresent("msqOptionRow-2--1"));

        ______TS("MSQ: remove msq option");

        feedbackEditPage.fillMsqOption(2, "Choice 3");
        assertEquals(true, feedbackEditPage.isElementPresent("msqOptionRow-1--1"));
        feedbackEditPage.clickRemoveMsqOptionLink(1, -1);
        assertEquals(false, feedbackEditPage.isElementPresent("msqOptionRow-1--1"));

        ______TS("MSQ: add msq option after remove");

        feedbackEditPage.clickAddMoreMsqOptionLink();
        assertEquals(true, feedbackEditPage.isElementPresent("msqOptionRow-3--1"));
        feedbackEditPage.clickAddMoreMsqOptionLink();
        feedbackEditPage.fillMsqOption(4, "Choice 5");
        assertEquals(true, feedbackEditPage.isElementPresent("msqOptionRow-4--1"));
    }

    private void testAddMsqQuestionAction(){

        ______TS("MSQ: add question action success");

        feedbackEditPage.fillQuestionBox("msq qn");
        assertNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 2));
        feedbackEditPage.clickAddQuestionButton();
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED, feedbackEditPage.getStatus());
        assertNotNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 2));
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackMsqQuestionAddSuccess.html");
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

        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackMsqQuestionEditSuccess.html");

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
    
    @SuppressWarnings("unused")
    private void ____NUMSCALE_methods___________________________________() {
    }

    private void testNewNumScaleQuestionFrame() {
        ______TS("NUMSCALE: new question (frame) link");

        feedbackEditPage.selectNewQuestionType("Numerical-scale question");
        feedbackEditPage.clickNewQuestionButton();
        assertTrue(feedbackEditPage.verifyNewNumScaleQuestionFormIsDisplayed());
    }

    private void testInputValidationForNumScaleQuestion() {
        
        ______TS("empty options");
        
        feedbackEditPage.fillQuestionBox("NumScale qn");
        feedbackEditPage.fillMinNumScaleBox("", -1);
        feedbackEditPage.fillStepNumScaleBox("", -1);
        feedbackEditPage.fillMaxNumScaleBox("", -1);
        
        assertEquals("[Please enter valid numbers for all the options.]",
                feedbackEditPage.getNumScalePossibleValuesString(-1));
        
        feedbackEditPage.clickAddQuestionButton();
        
        assertEquals("Please enter valid options. The min/max/step cannot be empty.", feedbackEditPage.getStatus());
        
        
        ______TS("invalid options");
        
        feedbackEditPage.fillQuestionBox("NumScale qn");
        feedbackEditPage.fillMinNumScaleBox("1", -1);
        feedbackEditPage.fillStepNumScaleBox("0.3", -1);
        feedbackEditPage.fillMaxNumScaleBox("", -1);
        feedbackEditPage.fillMaxNumScaleBox("5", -1);
        
        assertEquals("[The interval 1 - 5 is not divisible by the specified increment.]",
                feedbackEditPage.getNumScalePossibleValuesString(-1));
        
        feedbackEditPage.clickAddQuestionButton();
        
        assertEquals("Please enter valid options. The interval is not divisible by the specified increment.", feedbackEditPage.getStatus());
        
        
        ______TS("NUMSCALE: min >= max test");
        //Tests javascript that automatically makes max = min+1 when max is <= min.
        feedbackEditPage.fillMinNumScaleBox(1, -1);
        feedbackEditPage.fillStepNumScaleBox(1, -1);
        feedbackEditPage.fillMaxNumScaleBox(5, -1);
        assertEquals("[Based on the above settings, acceptable responses are: 1, 2, 3, 4, 5]",
                feedbackEditPage.getNumScalePossibleValuesString(-1));
        
        fillNumScaleBoxWithRecheck(true, 6, -1, "7");
        fillNumScaleBoxWithRecheck(false, 6, -1, "7");
            
        //Reset values
        feedbackEditPage.fillMinNumScaleBox(1, -1);
        feedbackEditPage.fillMaxNumScaleBox(5, -1);
    }
    
    private void fillNumScaleBoxWithRecheck(boolean isMinScaleBox, int scale, int qnNumber, String expected){
        int counter = 0;
        while(counter != 100) {
            if(isMinScaleBox){
                feedbackEditPage.fillMinNumScaleBox(scale, qnNumber);
            } else {
                feedbackEditPage.fillMaxNumScaleBox(scale, qnNumber);
            }
            if(expected.equals(feedbackEditPage.getMaxNumScaleBox(qnNumber))){
                return;
            }
            counter++;
            browser.driver.switchTo().window("");
        }
        assertEquals(expected, feedbackEditPage.getMaxNumScaleBox(qnNumber));
    }
    
    private void testCustomizeNumScaleOptions() {
        feedbackEditPage.fillQuestionBox("NumScale qn");
        assertEquals("[Based on the above settings, acceptable responses are: 1, 2, 3, 4, 5]",
                feedbackEditPage.getNumScalePossibleValuesString(-1));
        feedbackEditPage.fillStepNumScaleBox(0.3, -1);
        assertEquals("[The interval 1 - 5 is not divisible by the specified increment.]",
                feedbackEditPage.getNumScalePossibleValuesString(-1));
        feedbackEditPage.fillMinNumScaleBox(5, -1);
        feedbackEditPage.fillMaxNumScaleBox(6, -1);
        feedbackEditPage.fillStepNumScaleBox(0.001, -1);
        assertEquals("[Based on the above settings, acceptable responses are: 5, 5.001, 5.002, ..., 5.998, 5.999, 6]",
                feedbackEditPage.getNumScalePossibleValuesString(-1));
        feedbackEditPage.fillMinNumScaleBox(0, -1);
        feedbackEditPage.fillMaxNumScaleBox(1, -1);
        feedbackEditPage.fillStepNumScaleBox(0.1, -1);
        assertEquals("[Based on the above settings, acceptable responses are: 0, 0.1, 0.2, ..., 0.8, 0.9, 1]",
                feedbackEditPage.getNumScalePossibleValuesString(-1));
    }

    private void testAddNumScaleQuestionAction() {
        ______TS("NUMSCALE: add question action success");

        assertNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 2));
        feedbackEditPage.clickAddQuestionButton();
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED, feedbackEditPage.getStatus());
        assertNotNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 2));
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackNumScaleQuestionAddSuccess.html");
    }

    private void testEditNumScaleQuestionAction() {
        ______TS("NUMSCALE: edit question success");

        assertEquals(true, feedbackEditPage.clickEditQuestionButton(2));
        feedbackEditPage.fillEditQuestionBox("edited numscale qn text", 2);
        feedbackEditPage.fillMinNumScaleBox(3, 2);
        feedbackEditPage.fillMaxNumScaleBox(4, 2);
        feedbackEditPage.fillStepNumScaleBox(0.2, 2);
        assertEquals("[Based on the above settings, acceptable responses are: 3, 3.2, 3.4, 3.6, 3.8, 4]",
                feedbackEditPage.getNumScalePossibleValuesString(2));
        feedbackEditPage.clickSaveExistingQuestionButton(2);
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_EDITED, feedbackEditPage.getStatus());

        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackNumScaleQuestionEditSuccess.html");
    }
    
    private void testEditNumScaleQuestionNumberAction() {
        ______TS("NUMSCALE: edit question number success");

        assertEquals(true, feedbackEditPage.clickEditQuestionButton(2));
        feedbackEditPage.selectQuestionNumber(2, 1);        
        feedbackEditPage.clickSaveExistingQuestionButton(2);
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_EDITED, feedbackEditPage.getStatus());


        ______TS("NUMSCALE: edit question number success - change back to question 2");
        
        assertEquals(true, feedbackEditPage.clickEditQuestionButton(1));
        feedbackEditPage.selectQuestionNumber(1, 2);
        feedbackEditPage.clickSaveExistingQuestionButton(1);
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_EDITED, feedbackEditPage.getStatus());

        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackNumScaleQuestionEditSuccess.html");
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
    
    @SuppressWarnings("unused")
    private void ____CONSTSUM_OPTION_methods___________________________________() {
    }
    
    private void testNewConstSumOptionQuestionFrame() {
        ______TS("CONSTSUM-option: new question (frame) link");

        feedbackEditPage.selectNewQuestionType("Distribute points (among options) question");
        feedbackEditPage.clickNewQuestionButton();
        assertTrue(feedbackEditPage.verifyNewConstSumQuestionFormIsDisplayed());
    }
    
    private void testInputValidationForConstSumOptionQuestion() {
        
        ______TS("empty options");
        
        feedbackEditPage.fillQuestionBox("ConstSum-option qn");
        feedbackEditPage.fillConstSumPointsBox("", -1);
        
        assertEquals("1", feedbackEditPage.getConstSumPointsBox(-1));
        
        feedbackEditPage.clickAddQuestionButton();
        
        assertEquals("Too little options for Distribute points (among options) question. Minimum number of options is: 2.", feedbackEditPage.getStatus());
 
    }
    
    private void testCustomizeConstSumOptionOptions() {
        feedbackEditPage.selectNewQuestionType("Distribute points (among options) question");
        feedbackEditPage.clickNewQuestionButton();
        
        feedbackEditPage.fillConstSumOption(0, "Option 1");
        feedbackEditPage.fillConstSumOption(1, "Option 2");
        
        ______TS("CONST SUM: add option");

        assertEquals(false, feedbackEditPage.isElementPresent("constSumOptionRow-2--1"));
        feedbackEditPage.clickAddMoreConstSumOptionLink();
        assertEquals(true, feedbackEditPage.isElementPresent("constSumOptionRow-2--1"));

        ______TS("CONST SUM: remove option");

        feedbackEditPage.fillConstSumOption(2, "Option 3");
        assertEquals(true, feedbackEditPage.isElementPresent("constSumOptionRow-1--1"));
        feedbackEditPage.clickRemoveConstSumOptionLink(1, -1);
        assertEquals(false, feedbackEditPage.isElementPresent("constSumOptionRow-1--1"));

        ______TS("CONST SUM: add option after remove");

        feedbackEditPage.clickAddMoreConstSumOptionLink();
        assertEquals(true, feedbackEditPage.isElementPresent("constSumOptionRow-3--1"));
        feedbackEditPage.clickAddMoreConstSumOptionLink();
        feedbackEditPage.fillConstSumOption(4, "Option 5");
        assertEquals(true, feedbackEditPage.isElementPresent("constSumOptionRow-4--1"));
    }

    private void testAddConstSumOptionQuestionAction() {
        ______TS("CONST SUM: add question action success");
        
        feedbackEditPage.fillQuestionBox("const sum qn");
        assertNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 2));
        feedbackEditPage.clickAddQuestionButton();
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED, feedbackEditPage.getStatus());
        assertNotNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 2));
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackConstSumOptionQuestionAddSuccess.html");
    }

    private void testEditConstSumOptionQuestionAction() {
        ______TS("CONST SUM: edit question success");

        assertEquals(true, feedbackEditPage.clickEditQuestionButton(2));
        feedbackEditPage.fillEditQuestionBox("edited const sum qn text", 2);
        feedbackEditPage.fillConstSumPointsBox("200", 2);
        feedbackEditPage.selectConstSumPointsOptions("per recipient:", 2);
        
        feedbackEditPage.clickSaveExistingQuestionButton(2);
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_EDITED, feedbackEditPage.getStatus());

        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackConstSumOptionQuestionEditSuccess.html");
    }
    
    private void testDeleteConstSumOptionQuestionAction(){
        ______TS("CONSTSUM: qn delete then cancel");

        feedbackEditPage.clickAndCancel(feedbackEditPage.getDeleteQuestionLink(2));
        assertNotNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 2));

        ______TS("CONSTSUM: qn delete then accept");

        feedbackEditPage.clickAndConfirm(feedbackEditPage.getDeleteQuestionLink(2));
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_DELETED, feedbackEditPage.getStatus());
        assertNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 2));    
    }
    
    @SuppressWarnings("unused")
    private void ____CONST_SUM_RECIPIENTS_methods___________________________________() {
    }
    
    private void testNewConstSumRecipientQuestionFrame() {
        ______TS("CONSTSUM-recipient: new question (frame) link");

        feedbackEditPage.selectNewQuestionType("Distribute points (among recipients) question");
        feedbackEditPage.clickNewQuestionButton();
        assertTrue(feedbackEditPage.verifyNewConstSumQuestionFormIsDisplayed());
    }
    
    private void testInputValidationForConstSumRecipientQuestion() {
        
        ______TS("CONST SUM:input validation");
        
        feedbackEditPage.fillQuestionBox("ConstSum-recipient qn");
        feedbackEditPage.fillConstSumPointsBox("", -1);
        
        assertEquals("1", feedbackEditPage.getConstSumPointsBox(-1));
        assertEquals(false, feedbackEditPage.isElementVisible("constSumOptionTable--1"));
        
        feedbackEditPage.getDeleteQuestionLink(-1).click();
        assertEquals("", feedbackEditPage.getStatus());
        assertNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 2));
        
    }
    

    private void testCustomizeConstSumRecipientOptions() {
        feedbackEditPage.selectNewQuestionType("Distribute points (among recipients) question");
        feedbackEditPage.clickNewQuestionButton();
        
        ______TS("CONST SUM: set points options");

        feedbackEditPage.selectConstSumPointsOptions("per recipient:", -1);
        feedbackEditPage.fillConstSumPointsBox("100", -1);
        
    }

    private void testAddConstSumRecipientQuestionAction() {
        ______TS("CONST SUM: add question action success");
        
        feedbackEditPage.fillQuestionBox("const sum qn");
        assertNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 2));
        feedbackEditPage.clickAddQuestionButton();
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED, feedbackEditPage.getStatus());
        assertNotNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 2));
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackConstSumRecipientQuestionAddSuccess.html");
    }

    private void testEditConstSumRecipientQuestionAction() {
        ______TS("CONST SUM: edit question success");

        assertEquals(true, feedbackEditPage.clickEditQuestionButton(2));
        feedbackEditPage.fillEditQuestionBox("edited const sum qn text", 2);
        feedbackEditPage.fillConstSumPointsBox("200", 2);
        feedbackEditPage.selectConstSumPointsOptions("in total:", 2);
        
        feedbackEditPage.clickSaveExistingQuestionButton(2);
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_EDITED, feedbackEditPage.getStatus());

        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackConstSumRecipientQuestionEditSuccess.html");
    }
    
    private void testDeleteConstSumRecipientQuestionAction(){
        ______TS("CONSTSUM: qn delete then cancel");

        feedbackEditPage.clickAndCancel(feedbackEditPage.getDeleteQuestionLink(2));
        assertNotNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 2));

        ______TS("CONSTSUM: qn delete then accept");

        feedbackEditPage.clickAndConfirm(feedbackEditPage.getDeleteQuestionLink(2));
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_DELETED, feedbackEditPage.getStatus());
        assertNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 2));    
    }
    
    @SuppressWarnings("unused")
    private void ____other_methods___________________________________() {
    }
    
    private void testPreviewSessionAction() {

        // add questions for previewing
        feedbackEditPage.clickNewQuestionButton();
        assertTrue(feedbackEditPage.verifyNewEssayQuestionFormIsDisplayed());
        feedbackEditPage.fillQuestionBox("question for me");
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
        
        previewPage.waitForElementPresence(By.id("button_preview_instructor"), 15);
        previewPage = feedbackEditPage.clickPreviewAsInstructorButton();
        previewPage.verifyHtmlMainContent("/instructorFeedbackSubmitPagePreview.html");
        previewPage.closeCurrentWindowAndSwitchToParentWindow();
    }

    private void testDoneEditingLink() {
        InstructorFeedbacksPage feedbackPage = feedbackEditPage.clickDoneEditingLink();
        
        ______TS("Compare URLs");
        
        String expectedRedirectUrl = TestProperties.inst().TEAMMATES_URL + 
                Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE + 
                "?" + Const.ParamsNames.USER_ID +
                "=" + instructorId + "&" + 
                Const.ParamsNames.COURSE_ID + 
                "=" + courseId + "&" +
                Const.ParamsNames.FEEDBACK_SESSION_NAME +
                "=" + feedbackSessionName.replaceAll(" ", "%20");
        assertEquals(expectedRedirectUrl, feedbackPage.getPageUrl());
        
        ______TS("Check for highlight on last modified row");
        
        feedbackPage.verifyHtmlMainContent("/instructorFeedbackDoneEditing.html");
        
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
        AssertHelper.assertContains(Const.StatusMessages.FEEDBACK_SESSION_DELETED, feedbackPage.getStatus());
        assertNull(BackDoor.getFeedbackSession(courseId, feedbackSessionName));
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
    
    private void testCopyQuestion() {
        
        ______TS("Success case: copy questions successfully");
        
        ThreadHelper.waitFor(1000);
        feedbackEditPage.clickCopyButton();
        ThreadHelper.waitFor(1000);
        feedbackEditPage.clickCopyTableAtRow(0);
        feedbackEditPage.clickCopySubmitButton();
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackCopyQuestionSuccess.html");
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