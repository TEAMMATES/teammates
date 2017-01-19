package teammates.test.cases.ui.browsertests;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.util.Const;
import teammates.test.driver.BackDoor;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.InstructorFeedbackEditPage;

public class FeedbackMcqQuestionUiTest extends FeedbackQuestionUiTest {
    private static Browser browser;
    private static InstructorFeedbackEditPage feedbackEditPage;
    private static DataBundle testData;

    private static String courseId;
    private static String feedbackSessionName;
    private static String instructorId;
    
    @BeforeClass
    public void classSetup() {
        printTestClassHeader();
        testData = loadDataBundle("/FeedbackMcqQuestionUiTest.json");
        removeAndRestoreDataBundle(testData);
        browser = BrowserPool.getBrowser();
        
        instructorId = testData.accounts.get("instructor1").googleId;
        courseId = testData.courses.get("course").getId();
        feedbackSessionName = testData.feedbackSessions.get("openSession").getFeedbackSessionName();
        feedbackEditPage = getFeedbackEditPage(instructorId, courseId, feedbackSessionName, browser);

    }
    
    @Test
    public void allTests() throws Exception {
        testEditPage();
        
        //TODO: move/create other MCQ question related UI tests here.
        //i.e. results page, submit page.
    }
    
    private void testEditPage() throws Exception {
        testNewQuestionFrame();
        testInputValidation();
        testCustomizeOptions();
        testAddQuestionAction();
        testEditQuestionAction();
        testDeleteQuestionAction();
    }

    @Override
    public void testNewQuestionFrame() {

        ______TS("MCQ: new question (frame) link");

        
        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionType("MCQ");
        assertTrue(feedbackEditPage.verifyNewMcqQuestionFormIsDisplayed());
    }
    
    @Override
    public void testInputValidation() {

        ______TS("empty question text");

        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.verifyStatus(Const.StatusMessages.FEEDBACK_QUESTION_TEXTINVALID);
        
        ______TS("empty options");

        feedbackEditPage.fillNewQuestionBox("Test question text");
        feedbackEditPage.fillNewQuestionDescription("more details");
        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.verifyStatus("Too little choices for Multiple-choice (single answer) question. "
                                      + "Minimum number of options is: 2.");

        ______TS("remove when 1 left");

        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionType("MCQ");
        feedbackEditPage.fillNewQuestionBox("Test question text");
        feedbackEditPage.fillNewQuestionDescription("more details");
        assertTrue(feedbackEditPage.verifyNewMcqQuestionFormIsDisplayed());

        feedbackEditPage.clickRemoveMcqOptionLink(1, -1);
        assertFalse(feedbackEditPage.isElementPresent("mcqOptionRow-1--1"));

        // TODO: Check that after deleting, the value is cleared
        assertTrue(feedbackEditPage.isElementPresent("mcqOptionRow-0--1"));
        feedbackEditPage.clickRemoveMcqOptionLink(0, -1);
        assertTrue(feedbackEditPage.isElementPresent("mcqOptionRow-0--1"));
        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.verifyStatus("Too little choices for Multiple-choice (single answer) question. "
                                      + "Minimum number of options is: 2.");
        
        ______TS("remove when 1 left and select Add Other Option");

        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionType("MCQ");
        feedbackEditPage.fillNewQuestionBox("Test question text");
        feedbackEditPage.fillNewQuestionDescription("more details");
        assertTrue(feedbackEditPage.verifyNewMcqQuestionFormIsDisplayed());

        feedbackEditPage.clickRemoveMcqOptionLink(1, -1);
        assertFalse(feedbackEditPage.isElementPresent("mcqOptionRow-1--1"));
        assertTrue(feedbackEditPage.isElementPresent("mcqOptionRow-0--1"));
        
        feedbackEditPage.clickAddMcqOtherOptionCheckboxForNewQuestion();
        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.verifyStatus("Too little choices for Multiple-choice (single answer) question. "
                                      + "Minimum number of options is: 2.");
    }

    @Override
    public void testCustomizeOptions() {

        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionType("MCQ");
        
        feedbackEditPage.fillMcqOption(0, "Choice 1");
        feedbackEditPage.fillMcqOption(1, "Choice 2");

        ______TS("MCQ: add mcq option");
        
        assertFalse(feedbackEditPage.isElementPresent("mcqOptionRow-2--1"));
        feedbackEditPage.clickAddMoreMcqOptionLinkForNewQuestion();
        assertTrue(feedbackEditPage.isElementPresent("mcqOptionRow-2--1"));

        ______TS("MCQ: remove mcq option");

        feedbackEditPage.fillMcqOption(2, "Choice 3");
        assertTrue(feedbackEditPage.isElementPresent("mcqOptionRow-1--1"));
        feedbackEditPage.clickRemoveMcqOptionLink(1, -1);
        assertFalse(feedbackEditPage.isElementPresent("mcqOptionRow-1--1"));

        ______TS("MCQ: add mcq option after remove");

        feedbackEditPage.clickAddMoreMcqOptionLinkForNewQuestion();
        assertTrue(feedbackEditPage.isElementPresent("mcqOptionRow-3--1"));
        feedbackEditPage.clickAddMoreMcqOptionLinkForNewQuestion();
        feedbackEditPage.fillMcqOption(4, "Choice 5");
        assertTrue(feedbackEditPage.isElementPresent("mcqOptionRow-4--1"));
    }

    @Override
    public void testAddQuestionAction() throws Exception {

        ______TS("MCQ: add question action success");

        feedbackEditPage.fillNewQuestionBox("mcq qn");
        feedbackEditPage.fillNewQuestionDescription("more details");
        feedbackEditPage.enableOtherFeedbackPathOptionsForNewQuestion();
        feedbackEditPage.selectRecipientsToBeStudents();
        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.verifyStatus(Const.StatusMessages.FEEDBACK_QUESTION_ADDED);
        assertNotNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));
        
        //NOTE: Tests feedback giver/recipient and visibility options are copied from previous question.
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackMcqQuestionAddSuccess.html");
    }

    @Override
    public void testEditQuestionAction() throws Exception {

        ______TS("MCQ: edit question success");

        feedbackEditPage.clickEditQuestionButton(1);
        feedbackEditPage.fillEditQuestionBox("edited mcq qn text", 1);
        feedbackEditPage.fillEditQuestionDescription("more details", 1);
        assertTrue(feedbackEditPage.isElementPresent("mcqOptionRow-0-1"));
        feedbackEditPage.clickRemoveMcqOptionLink(0, 1);
        assertFalse(feedbackEditPage.isElementPresent("mcqOptionRow-0-1"));
        feedbackEditPage.clickSaveExistingQuestionButton(1);
        feedbackEditPage.verifyStatus(Const.StatusMessages.FEEDBACK_QUESTION_EDITED);

        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackMcqQuestionEditSuccess.html");

        ______TS("MCQ: edit to generated options");

        feedbackEditPage.clickEditQuestionButton(1);
        feedbackEditPage.fillEditQuestionBox("generated mcq qn text", 1);
        feedbackEditPage.fillEditQuestionDescription("more details", 1);
        assertTrue(feedbackEditPage.isElementVisible("mcqAddOptionLink-1"));
        feedbackEditPage.verifyFieldValue(
                Const.ParamsNames.FEEDBACK_QUESTION_GENERATEDOPTIONS,
                FeedbackParticipantType.NONE.toString());
        assertFalse(feedbackEditPage.isElementEnabled("mcqGenerateForSelect-1"));
        feedbackEditPage.clickGenerateOptionsCheckbox(1);
        assertTrue(feedbackEditPage.isElementEnabled("mcqGenerateForSelect-1"));
        feedbackEditPage.verifyFieldValue(
                Const.ParamsNames.FEEDBACK_QUESTION_GENERATEDOPTIONS + "-1",
                FeedbackParticipantType.STUDENTS.toString());
        assertFalse(feedbackEditPage.isElementVisible("mcqAddOptionLink-1"));

        feedbackEditPage.clickSaveExistingQuestionButton(1);
        feedbackEditPage.verifyStatus(Const.StatusMessages.FEEDBACK_QUESTION_EDITED);
        assertFalse(feedbackEditPage.isElementPresent("mcqOptionRow-0-1"));
        assertFalse(feedbackEditPage.isElementEnabled("generateOptionsCheckbox-1"));
        assertTrue(feedbackEditPage.isElementSelected("generateOptionsCheckbox-1"));
        assertFalse(feedbackEditPage.isElementEnabled("mcqGenerateForSelect-1"));
        feedbackEditPage.verifyFieldValue(
                "mcqGenerateForSelect-1",
                FeedbackParticipantType.STUDENTS.toString());
        feedbackEditPage.verifyFieldValue(
                Const.ParamsNames.FEEDBACK_QUESTION_GENERATEDOPTIONS + "-1",
                FeedbackParticipantType.STUDENTS.toString());

        ______TS("MCQ: change generated type");

        feedbackEditPage.clickEditQuestionButton(1);
        assertTrue(feedbackEditPage.isElementEnabled("generateOptionsCheckbox-1"));
        assertTrue(feedbackEditPage.isElementSelected("generateOptionsCheckbox-1"));
        assertTrue(feedbackEditPage.isElementEnabled("mcqGenerateForSelect-1"));
        feedbackEditPage.selectMcqGenerateOptionsFor("teams", 1);
        feedbackEditPage.verifyFieldValue(
                "mcqGenerateForSelect-1",
                FeedbackParticipantType.TEAMS.toString());
        feedbackEditPage.verifyFieldValue(
                Const.ParamsNames.FEEDBACK_QUESTION_GENERATEDOPTIONS + "-1",
                FeedbackParticipantType.TEAMS.toString());

    }
    
    @Override
    public void testDeleteQuestionAction() {

        ______TS("MCQ: qn delete then cancel");

        feedbackEditPage.clickDeleteQuestionLink(1);
        feedbackEditPage.waitForConfirmationModalAndClickCancel();
        assertNotNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));

        ______TS("MCQ: qn delete then accept");

        feedbackEditPage.clickDeleteQuestionLink(1);
        feedbackEditPage.waitForConfirmationModalAndClickOk();
        feedbackEditPage.verifyStatus(Const.StatusMessages.FEEDBACK_QUESTION_DELETED);
        assertNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));
    }
    
    @AfterClass
    public static void classTearDown() {
        BrowserPool.release(browser);
    }
}
