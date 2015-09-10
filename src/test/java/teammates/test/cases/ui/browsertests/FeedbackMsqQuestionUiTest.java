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

public class FeedbackMsqQuestionUiTest extends FeedbackQuestionUiTest {
    private static Browser browser;
    private static InstructorFeedbackEditPage feedbackEditPage;
    private static DataBundle testData;

    private static String courseId;
    private static String feedbackSessionName;
    private static String instructorId;
    
    @BeforeClass
    public void classSetup() throws Exception {
        printTestClassHeader();
        testData = loadDataBundle("/FeedbackMsqQuestionUiTest.json");
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
        
        //TODO: move/create other MSQ question related UI tests here.
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

        ______TS("MSQ: new question (frame) link");

        feedbackEditPage.selectNewQuestionType("Multiple-choice (multiple answers) question");
        feedbackEditPage.clickNewQuestionButton();
        assertTrue(feedbackEditPage.verifyNewMsqQuestionFormIsDisplayed());
    }
    
    public void testInputValidation() {

        ______TS("empty question text");

        feedbackEditPage.clickAddQuestionButton();
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_TEXTINVALID, feedbackEditPage.getStatus());
        
        ______TS("empty options");

        feedbackEditPage.fillQuestionBox("Test question text");
        feedbackEditPage.clickAddQuestionButton();
        assertEquals("Too little choices for Multiple-choice (multiple answers) question. Minimum number of options is: 2.", feedbackEditPage.getStatus());
        
        ______TS("remove when 1 left");

        feedbackEditPage.selectNewQuestionType("Multiple-choice (multiple answers) question");
        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.fillQuestionBox("Test question text");

        feedbackEditPage.clickRemoveMsqOptionLink(1, -1);
        assertEquals(false, feedbackEditPage.isElementPresent("msqOptionRow-1--1"));
        
        // TODO: Check that after deleting, the value is cleared
        assertEquals(true, feedbackEditPage.isElementPresent("msqOptionRow-0--1"));
        feedbackEditPage.clickRemoveMsqOptionLink(0, -1);
        assertEquals(true, feedbackEditPage.isElementPresent("msqOptionRow-0--1"));
        feedbackEditPage.clickAddQuestionButton();
        assertEquals("Too little choices for Multiple-choice (multiple answers) question. Minimum number of options is: 2.", feedbackEditPage.getStatus());
        
        ______TS("select Add Other Option");

        feedbackEditPage.selectNewQuestionType("Multiple-choice (multiple answers) question");
        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.fillQuestionBox("Msq with other option");
        assertTrue(feedbackEditPage.verifyNewMsqQuestionFormIsDisplayed());
        
        assertEquals(true, feedbackEditPage.isElementPresent("msqOtherOptionFlag--1"));
        feedbackEditPage.clickAddMsqOtherOptionCheckboxForNewQuestion();
        assertEquals(true, feedbackEditPage.isElementSelected("msqOtherOptionFlag--1"));
        feedbackEditPage.clickAddQuestionButton();
    }

    public void testCustomizeOptions() {

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

    public void testAddQuestionAction() {

        ______TS("MSQ: add question action success");

        feedbackEditPage.fillQuestionBox("msq qn");
        feedbackEditPage.selectRecipientsToBeStudents();
        assertNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));
        feedbackEditPage.clickAddQuestionButton();
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED, feedbackEditPage.getStatus());
        assertNotNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackMsqQuestionAddSuccess.html");
    }

    public void testEditQuestionAction() {

        ______TS("MSQ: edit question success");

        assertEquals(true, feedbackEditPage.clickEditQuestionButton(1));
        feedbackEditPage.fillEditQuestionBox("edited msq qn text", 1);
        assertEquals(true, feedbackEditPage.isElementPresent("msqOptionRow-0-1"));
        feedbackEditPage.clickRemoveMsqOptionLink(0, 1);
        assertEquals(false, feedbackEditPage.isElementPresent("msqOptionRow-0-1"));
        feedbackEditPage.clickSaveExistingQuestionButton(1);
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_EDITED, feedbackEditPage.getStatus());

        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackMsqQuestionEditSuccess.html");

        ______TS("MSQ: edit to generated options");

        assertEquals(true, feedbackEditPage.clickEditQuestionButton(1));
        feedbackEditPage.fillEditQuestionBox("generated msq qn text", 1);
        assertEquals(true, feedbackEditPage.isElementVisible("msqAddOptionLink"));
        feedbackEditPage.verifyFieldValue(
                Const.ParamsNames.FEEDBACK_QUESTION_GENERATEDOPTIONS,
                FeedbackParticipantType.NONE.toString());
        assertEquals(false, feedbackEditPage.isElementEnabled("msqGenerateForSelect-1"));
        feedbackEditPage.clickGenerateOptionsCheckbox(1);
        assertEquals(true, feedbackEditPage.isElementEnabled("msqGenerateForSelect-1"));
        feedbackEditPage.verifyFieldValue(
                Const.ParamsNames.FEEDBACK_QUESTION_GENERATEDOPTIONS + "-1",
                FeedbackParticipantType.STUDENTS.toString());
        assertEquals(false, feedbackEditPage.isElementVisible("msqAddOptionLink"));

        feedbackEditPage.clickSaveExistingQuestionButton(1);
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_EDITED, feedbackEditPage.getStatus());
        assertEquals(false, feedbackEditPage.isElementPresent("msqOptionRow-0-1"));
        assertEquals(false, feedbackEditPage.isElementEnabled("generateOptionsCheckbox-1"));
        assertEquals(true, feedbackEditPage.isElementSelected("generateOptionsCheckbox-1"));
        assertEquals(false, feedbackEditPage.isElementEnabled("msqGenerateForSelect-1"));
        feedbackEditPage.verifyFieldValue(
                "msqGenerateForSelect-1",
                FeedbackParticipantType.STUDENTS.toString());
        feedbackEditPage.verifyFieldValue(
                Const.ParamsNames.FEEDBACK_QUESTION_GENERATEDOPTIONS + "-1",
                FeedbackParticipantType.STUDENTS.toString());

        ______TS("MSQ: change generated type");

        assertEquals(true, feedbackEditPage.clickEditQuestionButton(1));
        assertEquals(true, feedbackEditPage.isElementEnabled("generateOptionsCheckbox-1"));
        assertEquals(true, feedbackEditPage.isElementSelected("generateOptionsCheckbox-1"));
        assertEquals(true, feedbackEditPage.isElementEnabled("msqGenerateForSelect-1"));
        feedbackEditPage.selectMsqGenerateOptionsFor("teams", 1);
        feedbackEditPage.verifyFieldValue(
                "msqGenerateForSelect-1",
                FeedbackParticipantType.TEAMS.toString());
        feedbackEditPage.verifyFieldValue(
                Const.ParamsNames.FEEDBACK_QUESTION_GENERATEDOPTIONS + "-1",
                FeedbackParticipantType.TEAMS.toString());

    }
    
    public void testDeleteQuestionAction(){

        ______TS("MSQ: qn delete then cancel");

        feedbackEditPage.clickAndCancel(feedbackEditPage.getDeleteQuestionLink(1));
        assertNotNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));

        ______TS("MSQ: qn delete then accept");

        feedbackEditPage.clickAndConfirm(feedbackEditPage.getDeleteQuestionLink(1));
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_DELETED, feedbackEditPage.getStatus());
        assertNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));
    }
    
    @AfterClass
    public static void classTearDown() throws Exception {
        BrowserPool.release(browser);
    }
}
