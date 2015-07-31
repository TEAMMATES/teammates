package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertTrue;

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
    public void classSetup() throws Exception {
        printTestClassHeader();
        testData = loadDataBundle("/FeedbackMcqQuestionUiTest.json");
        removeAndRestoreTestDataOnServer(testData);
        browser = BrowserPool.getBrowser();
        
        instructorId = testData.accounts.get("instructor1").googleId;
        courseId = testData.courses.get("course").id;
        feedbackSessionName = testData.feedbackSessions.get("openSession").feedbackSessionName;
        feedbackEditPage = getFeedbackEditPage(instructorId, courseId, feedbackSessionName, browser);

    }
    
    @Test
    public void allTests() throws Exception{
        testEditPage();
        
        //TODO: move/create other MCQ question related UI tests here.
        //i.e. results page, submit page.
    }
    
    private void testEditPage(){
        testNewQuestionFrame();
        testInputValidation();
        testCustomizeOptions();
        testAddQuestionAction();
        testEditQuestionAction();
        testDeleteQuestionAction();
    }

    public void testNewQuestionFrame() {

        ______TS("MCQ: new question (frame) link");

        feedbackEditPage.selectNewQuestionType("Multiple-choice (single answer) question");
        feedbackEditPage.clickNewQuestionButton();
        assertTrue(feedbackEditPage.verifyNewMcqQuestionFormIsDisplayed());
    }
    
    public void testInputValidation() {

        ______TS("empty question text");

        feedbackEditPage.clickAddQuestionButton();
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_TEXTINVALID, feedbackEditPage.getStatus());
        
        ______TS("empty options");

        feedbackEditPage.fillQuestionBox("Test question text");
        feedbackEditPage.clickAddQuestionButton();
        assertEquals("Too little choices for Multiple-choice (single answer) question. Minimum number of options is: 2.", feedbackEditPage.getStatus());

        ______TS("remove when 1 left");

        feedbackEditPage.selectNewQuestionType("Multiple-choice (single answer) question");
        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.fillQuestionBox("Test question text");
        assertTrue(feedbackEditPage.verifyNewMcqQuestionFormIsDisplayed());

        feedbackEditPage.clickRemoveMcqOptionLink(1, -1);
        assertEquals(false, feedbackEditPage.isElementPresent("mcqOptionRow-1--1"));

        // TODO: Check that after deleting, the value is cleared
        assertEquals(true, feedbackEditPage.isElementPresent("mcqOptionRow-0--1"));
        feedbackEditPage.clickRemoveMcqOptionLink(0, -1);
        assertEquals(true, feedbackEditPage.isElementPresent("mcqOptionRow-0--1"));
        feedbackEditPage.clickAddQuestionButton();
        assertEquals("Too little choices for Multiple-choice (single answer) question. Minimum number of options is: 2.", feedbackEditPage.getStatus());
        
        ______TS("remove when 1 left and select Add Other Option");

        feedbackEditPage.selectNewQuestionType("Multiple-choice (single answer) question");
        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.fillQuestionBox("Test question text");
        assertTrue(feedbackEditPage.verifyNewMcqQuestionFormIsDisplayed());

        feedbackEditPage.clickRemoveMcqOptionLink(1, -1);
        assertEquals(false, feedbackEditPage.isElementPresent("mcqOptionRow-1--1"));
        assertEquals(true, feedbackEditPage.isElementPresent("mcqOptionRow-0--1"));
        
        feedbackEditPage.clickAddMcqOtherOptionCheckboxForNewQuestion();
        feedbackEditPage.clickAddQuestionButton();
        assertEquals("Too little choices for Multiple-choice (single answer) question. Minimum number of options is: 2.", feedbackEditPage.getStatus());
    }

    public void testCustomizeOptions() {

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

    public void testAddQuestionAction() {

        ______TS("MCQ: add question action success");

        feedbackEditPage.fillQuestionBox("mcq qn");
        feedbackEditPage.selectRecipientsToBeStudents();
        feedbackEditPage.clickAddQuestionButton();
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED, feedbackEditPage.getStatus());
        assertNotNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));
        
        //NOTE: Tests feedback giver/recipient and visibility options are copied from previous question.
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackMcqQuestionAddSuccess.html");
    }

    public void testEditQuestionAction() {

        ______TS("MCQ: edit question success");

        assertEquals(true, feedbackEditPage.clickEditQuestionButton(1));
        feedbackEditPage.fillEditQuestionBox("edited mcq qn text", 1);
        assertEquals(true, feedbackEditPage.isElementPresent("mcqOptionRow-0-1"));
        feedbackEditPage.clickRemoveMcqOptionLink(0, 1);
        assertEquals(false, feedbackEditPage.isElementPresent("mcqOptionRow-0-1"));
        feedbackEditPage.clickSaveExistingQuestionButton(1);
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_EDITED, feedbackEditPage.getStatus());

        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackMcqQuestionEditSuccess.html");

        ______TS("MCQ: edit to generated options");

        assertEquals(true, feedbackEditPage.clickEditQuestionButton(1));
        feedbackEditPage.fillEditQuestionBox("generated mcq qn text", 1);
        assertEquals(true, feedbackEditPage.isElementVisible("mcqAddOptionLink"));
        feedbackEditPage.verifyFieldValue(
                Const.ParamsNames.FEEDBACK_QUESTION_GENERATEDOPTIONS,
                FeedbackParticipantType.NONE.toString());
        assertEquals(false, feedbackEditPage.isElementEnabled("mcqGenerateForSelect-1"));
        feedbackEditPage.clickGenerateOptionsCheckbox(1);
        assertEquals(true, feedbackEditPage.isElementEnabled("mcqGenerateForSelect-1"));
        feedbackEditPage.verifyFieldValue(
                Const.ParamsNames.FEEDBACK_QUESTION_GENERATEDOPTIONS + "-1",
                FeedbackParticipantType.STUDENTS.toString());
        assertEquals(false, feedbackEditPage.isElementVisible("mcqAddOptionLink"));

        feedbackEditPage.clickSaveExistingQuestionButton(1);
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_EDITED, feedbackEditPage.getStatus());
        assertEquals(false, feedbackEditPage.isElementPresent("mcqOptionRow-0-1"));
        assertEquals(false, feedbackEditPage.isElementEnabled("generateOptionsCheckbox-1"));
        assertEquals(true, feedbackEditPage.isElementSelected("generateOptionsCheckbox-1"));
        assertEquals(false, feedbackEditPage.isElementEnabled("mcqGenerateForSelect-1"));
        feedbackEditPage.verifyFieldValue(
                "mcqGenerateForSelect-1",
                FeedbackParticipantType.STUDENTS.toString());
        feedbackEditPage.verifyFieldValue(
                Const.ParamsNames.FEEDBACK_QUESTION_GENERATEDOPTIONS + "-1",
                FeedbackParticipantType.STUDENTS.toString());

        ______TS("MCQ: change generated type");

        assertEquals(true, feedbackEditPage.clickEditQuestionButton(1));
        assertEquals(true, feedbackEditPage.isElementEnabled("generateOptionsCheckbox-1"));
        assertEquals(true, feedbackEditPage.isElementSelected("generateOptionsCheckbox-1"));
        assertEquals(true, feedbackEditPage.isElementEnabled("mcqGenerateForSelect-1"));
        feedbackEditPage.selectMcqGenerateOptionsFor("teams", 1);
        feedbackEditPage.verifyFieldValue(
                "mcqGenerateForSelect-1",
                FeedbackParticipantType.TEAMS.toString());
        feedbackEditPage.verifyFieldValue(
                Const.ParamsNames.FEEDBACK_QUESTION_GENERATEDOPTIONS + "-1",
                FeedbackParticipantType.TEAMS.toString());

    }
    
    public void testDeleteQuestionAction(){

        ______TS("MCQ: qn delete then cancel");

        feedbackEditPage.clickAndCancel(feedbackEditPage.getDeleteQuestionLink(1));
        assertNotNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));

        ______TS("MCQ: qn delete then accept");

        feedbackEditPage.clickAndConfirm(feedbackEditPage.getDeleteQuestionLink(1));
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_DELETED, feedbackEditPage.getStatus());
        assertNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));
    }
    
    @AfterClass
    public static void classTearDown() throws Exception {
        BrowserPool.release(browser);
    }
}
