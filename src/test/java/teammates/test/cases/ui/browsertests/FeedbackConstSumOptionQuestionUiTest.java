package teammates.test.cases.ui.browsertests;

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
    public void classSetup() {
        printTestClassHeader();
        testData = loadDataBundle("/FeedbackConstSumOptionQuestionUiTest.json");
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
        
        //TODO: move/create other ConstSumOption question related UI tests here.
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
        ______TS("CONSTSUM-option: new question (frame) link");

        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionType("CONSTSUM_OPTION");
        assertTrue(feedbackEditPage.verifyNewConstSumQuestionFormIsDisplayed());
    }
    
    @Override
    public void testInputValidation() {
        
        ______TS("empty options");
        
        feedbackEditPage.fillNewQuestionBox("ConstSum-option qn");
        feedbackEditPage.fillNewQuestionDescription("more details");
        
        feedbackEditPage.fillConstSumPointsBox("", -1);
        assertEquals("1", feedbackEditPage.getConstSumPointsBox(-1));
        
        feedbackEditPage.fillConstSumPointsForEachOptionBox("", -1);
        assertEquals("1", feedbackEditPage.getConstSumPointsForEachOptionBox(-1));
        
        feedbackEditPage.clickAddQuestionButton();
        
        feedbackEditPage.verifyStatus("Too little options for Distribute points (among options) question. "
                                      + "Minimum number of options is: 2.");
        
        ______TS("remove when 1 left");

        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionType("CONSTSUM_OPTION");
        feedbackEditPage.fillNewQuestionBox("Test const sum question");
        feedbackEditPage.fillNewQuestionDescription("more details");
        assertTrue(feedbackEditPage.verifyNewConstSumQuestionFormIsDisplayed());

        feedbackEditPage.clickRemoveConstSumOptionLink(1, -1);
        assertFalse(feedbackEditPage.isElementPresent("constSumOptionRow-1--1"));

        // TODO: Check that after deleting, the value is cleared
        assertTrue(feedbackEditPage.isElementPresent("constSumOptionRow-0--1"));
        feedbackEditPage.clickRemoveConstSumOptionLink(0, -1);
        assertTrue(feedbackEditPage.isElementPresent("constSumOptionRow-0--1"));
        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.verifyStatus("Too little options for Distribute points (among options) question. "
                                      + "Minimum number of options is: 2.");
    }

    @Override
    public void testCustomizeOptions() {
        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionType("CONSTSUM_OPTION");
        
        feedbackEditPage.fillConstSumOption(0, "Option 1");
        feedbackEditPage.fillConstSumOption(1, "Option 2");
        
        ______TS("CONST SUM: add option");

        assertFalse(feedbackEditPage.isElementPresent("constSumOptionRow-2--1"));
        feedbackEditPage.clickAddMoreConstSumOptionLink(-1);
        assertTrue(feedbackEditPage.isElementPresent("constSumOptionRow-2--1"));

        ______TS("CONST SUM: remove option");

        feedbackEditPage.fillConstSumOption(2, "Option 3");
        assertTrue(feedbackEditPage.isElementPresent("constSumOptionRow-1--1"));
        feedbackEditPage.clickRemoveConstSumOptionLink(1, -1);
        assertFalse(feedbackEditPage.isElementPresent("constSumOptionRow-1--1"));

        ______TS("CONST SUM: add option after remove");

        feedbackEditPage.clickAddMoreConstSumOptionLink(-1);
        assertTrue(feedbackEditPage.isElementPresent("constSumOptionRow-3--1"));
        feedbackEditPage.clickAddMoreConstSumOptionLink(-1);
        feedbackEditPage.fillConstSumOption(4, "Option 5");
        assertTrue(feedbackEditPage.isElementPresent("constSumOptionRow-4--1"));
    }

    @Override
    public void testAddQuestionAction() throws Exception {
        ______TS("CONST SUM: add question action success");
        
        feedbackEditPage.fillNewQuestionBox("const sum qn");
        feedbackEditPage.fillNewQuestionDescription("more details");
        feedbackEditPage.enableOtherFeedbackPathOptionsForNewQuestion();
        feedbackEditPage.selectRecipientsToBeStudents();
        feedbackEditPage.fillConstSumPointsBox("30", -1);
        assertNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));
        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.verifyStatus(Const.StatusMessages.FEEDBACK_QUESTION_ADDED);
        assertNotNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));
        
        assertEquals("30", feedbackEditPage.getConstSumPointsBox(1));
        assertEquals("30", feedbackEditPage.getConstSumPointsForEachOptionBox(1));
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackConstSumOptionQuestionAddSuccess.html");
    }

    @Override
    public void testEditQuestionAction() throws Exception {
        ______TS("CONST SUM: edit question success");

        feedbackEditPage.clickEditQuestionButton(1);
        feedbackEditPage.fillEditQuestionBox("edited const sum qn text", 1);
        feedbackEditPage.fillEditQuestionDescription("more details", 1);
        feedbackEditPage.selectConstSumPointsOptions("PerOption", 1);
        feedbackEditPage.fillConstSumPointsForEachOptionBox("200", 1);
        
        feedbackEditPage.clickSaveExistingQuestionButton(1);
        feedbackEditPage.verifyStatus(Const.StatusMessages.FEEDBACK_QUESTION_EDITED);
        
        assertEquals("200", feedbackEditPage.getConstSumPointsBox(1));
        assertEquals("200", feedbackEditPage.getConstSumPointsForEachOptionBox(1));

        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackConstSumOptionQuestionEditSuccess.html");
    }
    
    @Override
    public void testDeleteQuestionAction() {
        ______TS("CONSTSUM: qn delete then cancel");

        feedbackEditPage.clickDeleteQuestionLink(1);
        feedbackEditPage.waitForConfirmationModalAndClickCancel();
        assertNotNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));

        ______TS("CONSTSUM: qn delete then accept");

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
