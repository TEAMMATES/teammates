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

public class FeedbackContributionQuestionUiTest extends FeedbackQuestionUiTest {
    private static Browser browser;
    private static InstructorFeedbackEditPage feedbackEditPage;
    private static DataBundle testData;

    private static String courseId;
    private static String feedbackSessionName;
    private static String instructorId;
    
    @BeforeClass
    public void classSetup() throws Exception {
        printTestClassHeader();
        testData = loadDataBundle("/FeedbackContributionQuestionUiTest.json");
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
        
        //TODO: move/create other Contribution question related UI tests here.
        //i.e. results page, submit page.
    }
    
    private void testEditPage(){
        testNewQuestionFrame();
        testInputValidation();
        testCustomizeOptions();
        testAddQuestionAction();
        testEditQuestionAction();
        testDeleteQuestionAction();
        testAddContributionQuestionAsSecondQuestion();
    }
    
    

    public void testNewQuestionFrame() {
        ______TS("CONTRIB: new question (frame) link");

        feedbackEditPage.selectNewQuestionType("Team contribution question");
        feedbackEditPage.clickNewQuestionButton();
        assertTrue(feedbackEditPage.verifyNewContributionQuestionFormIsDisplayed());
    }
    
    public void testInputValidation() {
        
        ______TS("empty question text");

        feedbackEditPage.clickAddQuestionButton();
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_TEXTINVALID, feedbackEditPage.getStatus());
        
    }
    

    public void testCustomizeOptions() {

        //no question specific options to test
        
        ______TS("CONTRIB: set visibility options");
        
        feedbackEditPage.clickVisibilityOptionsForQuestion1();
        //TODO: click and ensure can see answer for recipients,
        //giver team members, recipient team members
        //are always the same. (under visibility options)
        
    }

    public void testAddQuestionAction() {
        ______TS("CONTRIB: add question action success");
        
        feedbackEditPage.fillQuestionBox("contrib qn");
        assertNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));
        feedbackEditPage.clickAddQuestionButton();
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED, feedbackEditPage.getStatus());
        assertNotNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackContribQuestionAddSuccess.html");
    }

    public void testEditQuestionAction() {
        ______TS("CONTRIB: edit question success");

        assertEquals(true, feedbackEditPage.clickEditQuestionButton(1));
        
        //Check invalid feedback paths are disabled.
        //Javascript should hide giver/recipient options that are not STUDENTS to OWN_TEAM_MEMBERS_INCLUDING_SELF
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackContribQuestionEdit.html");
        
        feedbackEditPage.fillEditQuestionBox("edited contrib qn text", 1);
        feedbackEditPage.toggleNotSureCheck(1);
        feedbackEditPage.clickSaveExistingQuestionButton(1);
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_EDITED, feedbackEditPage.getStatus());

        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackContribQuestionEditSuccess.html");
    }
    
    public void testDeleteQuestionAction(){
        ______TS("CONTRIB: qn delete then cancel");

        feedbackEditPage.clickAndCancel(feedbackEditPage.getDeleteQuestionLink(1));
        assertNotNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));

        ______TS("CONTRIB: qn delete then accept");

        feedbackEditPage.clickAndConfirm(feedbackEditPage.getDeleteQuestionLink(1));
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_DELETED, feedbackEditPage.getStatus());
        assertNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));    
    }
    
    /**
     * Tests the case when contribution question is added after another question that
     * has options invalid for contribution questions. This is to prevent invalid options
     * from being copied over to the contribution question.
     */
    private void testAddContributionQuestionAsSecondQuestion(){
        ______TS("CONTRIB: add as second question");

        feedbackEditPage.selectNewQuestionType("Essay question");
        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.fillQuestionBox("q1, essay qn");
        feedbackEditPage.clickAddQuestionButton();
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED, feedbackEditPage.getStatus());
           
        feedbackEditPage.selectNewQuestionType("Team contribution question");
        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.fillQuestionBox("q2, contribution qn");
        feedbackEditPage.clickAddQuestionButton();
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED, feedbackEditPage.getStatus());
        
    }

    @AfterClass
    public static void classTearDown() throws Exception {
        BrowserPool.release(browser);
    }
}
