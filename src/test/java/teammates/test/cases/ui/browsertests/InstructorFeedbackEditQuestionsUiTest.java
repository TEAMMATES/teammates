package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertTrue;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.util.Const;
import teammates.common.util.Url;
import teammates.test.driver.BackDoor;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.InstructorFeedbackEditPage;
import teammates.test.util.Priority;

/**
 * The second test that covers the 'Edit Feedback Session' page for instructors.
 * This part of the test covers different capabilities of different question types.
 * SUT is {@link InstructorFeedbackEditPage}.
 */
@Priority(-1)
public class InstructorFeedbackEditQuestionsUiTest extends BaseUiTestCase {
    private static Browser browser;
    private static InstructorFeedbackEditPage feedbackEditPage;
    private static DataBundle testData;
    private static String instructorId;
    private static String courseId;
    private static String feedbackSessionName;
    /** This contains data for the feedback session to be edited during testing */

    @BeforeClass
    public static void classSetup() throws Exception {
        printTestClassHeader();
        testData = loadDataBundle("/InstructorFeedbackEditQuestionsUiTest.json");
        removeAndRestoreTestDataOnServer(testData);

        instructorId = testData.accounts.get("instructorWithSessions").googleId;
        courseId = testData.courses.get("course").id;
        feedbackSessionName = testData.feedbackSessions.get("openSession").feedbackSessionName;

        browser = BrowserPool.getBrowser();
        feedbackEditPage = getFeedbackEditPage();
        
        // need to do this as the test pages have 'submission opening time' ticked for
        // session visible from and 'immediately' ticked for responses visible from
        feedbackEditPage.clickEditSessionButton();
        feedbackEditPage.clickDefaultVisibleTimeButton();
        feedbackEditPage.clickDefaultPublishTimeButton();
        feedbackEditPage.clickSaveSessionButton();
    }
    
    @BeforeMethod
    public void reloadAndDeleteAnyExistingQuestion() {
        feedbackEditPage = getFeedbackEditPage();
        while (BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1) != null) {
            feedbackEditPage.clickAndConfirm(feedbackEditPage.getDeleteQuestionLink(1));
        }
    }

    @Test
    public void testMcqQuestionOperations() throws Exception {
        testNewMcqQuestionFrame();
        testInputValidationForMcqQuestion();
        testCustomizeMcqOptions();
        testAddMcqQuestionAction();
        testEditMcqQuestionAction();
        testDeleteMcqQuestionAction();
    }

    @Test
    public void testMsqQuestionOperations() throws Exception {
        testNewMsqQuestionFrame();
        testInputValidationForMsqQuestion();
        testCustomizeMsqOptions();
        testAddMsqQuestionAction();
        testEditMsqQuestionAction();
        testDeleteMsqQuestionAction();
    }
    
    @Test
    public void testNumScaleQuestionOperations() throws Exception {
        testNewNumScaleQuestionFrame();
        testInputValidationForNumScaleQuestion();
        testCustomizeNumScaleOptions();
        testAddNumScaleQuestionAction();
        testEditNumScaleQuestionAction();
        testDeleteNumScaleQuestionAction();        
    }

    @Test
    public void testConstSumOptionQuestionOperations() throws Exception {
        testNewConstSumOptionQuestionFrame();
        testInputValidationForConstSumOptionQuestion();
        testCustomizeConstSumOptionOptions();
        testAddConstSumOptionQuestionAction();
        testEditConstSumOptionQuestionAction();
        testDeleteConstSumOptionQuestionAction();
    }

    @Test
    public void testConstSumRecipientQuestionOperations() throws Exception {
        testNewConstSumRecipientQuestionFrame();
        testInputValidationForConstSumRecipientQuestion();
        testCustomizeConstSumRecipientOptions();
        testAddConstSumRecipientQuestionAction();
        testEditConstSumRecipientQuestionAction();
        testDeleteConstSumRecipientQuestionAction();
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
        feedbackEditPage.selectRecipientsToBeStudents();
        feedbackEditPage.clickAddQuestionButton();
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED, feedbackEditPage.getStatus());
        assertNotNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));
        
        //NOTE: Tests feedback giver/recipient and visibility options are copied from previous question.
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackMcqQuestionAddSuccess.html");
    }

    private void testEditMcqQuestionAction() {

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

    private void testDeleteMcqQuestionAction() {

        ______TS("MCQ: qn delete then cancel");

        feedbackEditPage.clickAndCancel(feedbackEditPage.getDeleteQuestionLink(1));
        assertNotNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));

        ______TS("MCQ: qn delete then accept");

        feedbackEditPage.clickAndConfirm(feedbackEditPage.getDeleteQuestionLink(1));
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_DELETED, feedbackEditPage.getStatus());
        assertNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));
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
        feedbackEditPage.selectRecipientsToBeStudents();
        assertNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));
        feedbackEditPage.clickAddQuestionButton();
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED, feedbackEditPage.getStatus());
        assertNotNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackMsqQuestionAddSuccess.html");
    }

    private void testEditMsqQuestionAction() {

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

    private void testDeleteMsqQuestionAction() {

        ______TS("MSQ: qn delete then cancel");

        feedbackEditPage.clickAndCancel(feedbackEditPage.getDeleteQuestionLink(1));
        assertNotNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));

        ______TS("MSQ: qn delete then accept");

        feedbackEditPage.clickAndConfirm(feedbackEditPage.getDeleteQuestionLink(1));
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_DELETED, feedbackEditPage.getStatus());
        assertNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));
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
        feedbackEditPage.fillMaxNumScaleBox("5", -1);
        
        assertEquals("[The interval 1 - 5 is not divisible by the specified increment.]",
                feedbackEditPage.getNumScalePossibleValuesString(-1));
        
        feedbackEditPage.clickAddQuestionButton();
        
        assertEquals("Please enter valid options. The interval is not divisible by the specified increment.", feedbackEditPage.getStatus());
        
        ______TS("possible floating point error");
        
        feedbackEditPage.fillQuestionBox("NumScale qn");
        feedbackEditPage.fillMinNumScaleBox("1", -1);
        feedbackEditPage.fillStepNumScaleBox("0.001", -1);
        feedbackEditPage.fillMaxNumScaleBox("5555", -1);
        
        assertEquals("[Based on the above settings, acceptable responses are: 1, 1.001, 1.002, ..., 5554.998, 5554.999, 5555]",
                feedbackEditPage.getNumScalePossibleValuesString(-1));
        
        ______TS("more than three dp step rounding test");

        feedbackEditPage.fillMaxNumScaleBox("1002", -1);
        feedbackEditPage.fillStepNumScaleBox("1.00123456789", -1);

        assertEquals("[Based on the above settings, acceptable responses are: 1, 2.001, 3.002, ..., 999.998, 1000.999, 1002]",
                feedbackEditPage.getNumScalePossibleValuesString(-1));
        
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

        assertNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));
        feedbackEditPage.selectRecipientsToBeStudents();
        feedbackEditPage.clickAddQuestionButton();
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED, feedbackEditPage.getStatus());
        assertNotNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackNumScaleQuestionAddSuccess.html");
    }

    private void testEditNumScaleQuestionAction() {
        ______TS("NUMSCALE: edit question success");

        assertEquals(true, feedbackEditPage.clickEditQuestionButton(1));
        feedbackEditPage.fillEditQuestionBox("edited numscale qn text", 1);
        feedbackEditPage.fillMinNumScaleBox(3, 1);
        feedbackEditPage.fillMaxNumScaleBox(4, 1);
        feedbackEditPage.fillStepNumScaleBox(0.002, 1);
        assertEquals("[Based on the above settings, acceptable responses are: 3, 3.002, 3.004, ..., 3.996, 3.998, 4]",
                feedbackEditPage.getNumScalePossibleValuesString(1));
        feedbackEditPage.clickSaveExistingQuestionButton(1);
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_EDITED, feedbackEditPage.getStatus());

        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackNumScaleQuestionEditSuccess.html");
    }

    private void testDeleteNumScaleQuestionAction() {
        ______TS("NUMSCALE: qn delete then cancel");

        feedbackEditPage.clickAndCancel(feedbackEditPage.getDeleteQuestionLink(1));
        assertNotNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));

        ______TS("NUMSCALE: qn delete then accept");

        feedbackEditPage.clickAndConfirm(feedbackEditPage.getDeleteQuestionLink(1));
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_DELETED, feedbackEditPage.getStatus());
        assertNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));
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
        
        ______TS("remove when 1 left");

        feedbackEditPage.selectNewQuestionType("Distribute points (among options) question");
        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.fillQuestionBox("Test const sum question");
        assertTrue(feedbackEditPage.verifyNewConstSumQuestionFormIsDisplayed());

        feedbackEditPage.clickRemoveConstSumOptionLink(1, -1);
        assertEquals(false, feedbackEditPage.isElementPresent("constSumOptionRow-1--1"));

        // TODO: Check that after deleting, the value is cleared
        assertEquals(true, feedbackEditPage.isElementPresent("constSumOptionRow-0--1"));
        feedbackEditPage.clickRemoveConstSumOptionLink(0, -1);
        assertEquals(true, feedbackEditPage.isElementPresent("constSumOptionRow-0--1"));
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
        feedbackEditPage.selectRecipientsToBeStudents();
        assertNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));
        feedbackEditPage.clickAddQuestionButton();
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED, feedbackEditPage.getStatus());
        assertNotNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackConstSumOptionQuestionAddSuccess.html");
    }

    private void testEditConstSumOptionQuestionAction() {
        ______TS("CONST SUM: edit question success");

        assertEquals(true, feedbackEditPage.clickEditQuestionButton(1));
        feedbackEditPage.fillEditQuestionBox("edited const sum qn text", 1);
        feedbackEditPage.fillConstSumPointsBox("200", 1);
        feedbackEditPage.selectConstSumPointsOptions("per recipient:", 1);
        
        feedbackEditPage.clickSaveExistingQuestionButton(1);
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_EDITED, feedbackEditPage.getStatus());

        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackConstSumOptionQuestionEditSuccess.html");
    }
    
    private void testDeleteConstSumOptionQuestionAction(){
        ______TS("CONSTSUM: qn delete then cancel");

        feedbackEditPage.clickAndCancel(feedbackEditPage.getDeleteQuestionLink(1));
        assertNotNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));

        ______TS("CONSTSUM: qn delete then accept");

        feedbackEditPage.clickAndConfirm(feedbackEditPage.getDeleteQuestionLink(1));
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_DELETED, feedbackEditPage.getStatus());
        assertNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));    
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
        feedbackEditPage.selectRecipientsToBeStudents();
        assertNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));
        feedbackEditPage.clickAddQuestionButton();
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED, feedbackEditPage.getStatus());
        assertNotNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackConstSumRecipientQuestionAddSuccess.html");
    }

    private void testEditConstSumRecipientQuestionAction() {
        ______TS("CONST SUM: edit question success");

        assertEquals(true, feedbackEditPage.clickEditQuestionButton(1));
        feedbackEditPage.fillEditQuestionBox("edited const sum qn text", 1);
        feedbackEditPage.fillConstSumPointsBox("200", 1);
        feedbackEditPage.selectConstSumPointsOptions("in total:", 1);
        
        feedbackEditPage.clickSaveExistingQuestionButton(1);
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_EDITED, feedbackEditPage.getStatus());

        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackConstSumRecipientQuestionEditSuccess.html");
    }
    
    private void testDeleteConstSumRecipientQuestionAction(){
        ______TS("CONSTSUM: qn delete then cancel");

        feedbackEditPage.clickAndCancel(feedbackEditPage.getDeleteQuestionLink(1));
        assertNotNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));

        ______TS("CONSTSUM: qn delete then accept");

        feedbackEditPage.clickAndConfirm(feedbackEditPage.getDeleteQuestionLink(1));
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_DELETED, feedbackEditPage.getStatus());
        assertNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));    
    }
    
    @SuppressWarnings("unused")
    private void ____other_methods___________________________________() {
    }
    
    @AfterClass
    public static void classTearDown() throws Exception {
        BrowserPool.release(browser);
    }

    private static InstructorFeedbackEditPage getFeedbackEditPage() {
        Url feedbackPageLink = createUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE).
                withUserId(instructorId).withCourseId(courseId).withSessionName(feedbackSessionName);
        return loginAdminToPage(browser, feedbackPageLink, InstructorFeedbackEditPage.class);
    }

}