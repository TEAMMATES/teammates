package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertTrue;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.test.driver.BackDoor;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.InstructorFeedbackEditPage;

public class FeedbackConstSumOptionQuestionUiTest extends FeedbackQuestionUiTest {
    private static Browser browser;
    private static InstructorFeedbackEditPage feedbackEditPage;
    private static DataBundle testData;

    private static String courseId;
    private static String feedbackSessionName;
    private static String instructorId;
    
    @BeforeClass
    public void classSetup() throws Exception {
        printTestClassHeader();
        testData = loadDataBundle("/FeedbackConstSumOptionQuestionUiTest.json");
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
        
        //TODO: move/create other ConstSumOption question related UI tests here.
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
        ______TS("CONSTSUM-option: new question (frame) link");

        feedbackEditPage.selectNewQuestionType("Distribute points (among options) question");
        feedbackEditPage.clickNewQuestionButton();
        assertTrue(feedbackEditPage.verifyNewConstSumQuestionFormIsDisplayed());
    }
    
    public void testInputValidation() {
        
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

    public void testCustomizeOptions() {
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

    public void testAddQuestionAction() {
        ______TS("CONST SUM: add question action success");
        
        feedbackEditPage.fillQuestionBox("const sum qn");
        feedbackEditPage.selectRecipientsToBeStudents();
        assertNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));
        feedbackEditPage.clickAddQuestionButton();
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED, feedbackEditPage.getStatus());
        assertNotNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackConstSumOptionQuestionAddSuccess.html");
    }

    public void testEditQuestionAction() {
        ______TS("CONST SUM: edit question success");

        assertEquals(true, feedbackEditPage.clickEditQuestionButton(1));
        feedbackEditPage.fillEditQuestionBox("edited const sum qn text", 1);
        feedbackEditPage.fillConstSumPointsBox("200", 1);
        feedbackEditPage.selectConstSumPointsOptions("per recipient:", 1);
        
        feedbackEditPage.clickSaveExistingQuestionButton(1);
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_EDITED, feedbackEditPage.getStatus());

        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackConstSumOptionQuestionEditSuccess.html");
    }
    
    public void testDeleteQuestionAction(){
        ______TS("CONSTSUM: qn delete then cancel");

        feedbackEditPage.clickAndCancel(feedbackEditPage.getDeleteQuestionLink(1));
        assertNotNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));

        ______TS("CONSTSUM: qn delete then accept");

        feedbackEditPage.clickAndConfirm(feedbackEditPage.getDeleteQuestionLink(1));
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_DELETED, feedbackEditPage.getStatus());
        assertNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));    
    }
    
    @AfterClass
    public static void classTearDown() throws Exception {
        BrowserPool.release(browser);
    }
}
