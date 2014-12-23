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
import teammates.common.util.Url;
import teammates.test.driver.BackDoor;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.InstructorFeedbackEditPage;

public class FeedbackRubricQuestionUiTest extends BaseUiTestCase{
    private static Browser browser;
    private static InstructorFeedbackEditPage feedbackEditPage;
    private static DataBundle testData;

    private static String courseId;
    private static String feedbackSessionName;
    private static String instructorId;
    
    @BeforeClass
    public static void classSetup() throws Exception {
        printTestClassHeader();
        testData = loadDataBundle("/FeedbackRubricQuestionUiTest.json");
        removeAndRestoreTestDataOnServer(testData);
        browser = BrowserPool.getBrowser();
        
        instructorId = testData.accounts.get("instructor1").googleId;
        courseId = testData.courses.get("course").id;
        feedbackSessionName = testData.feedbackSessions.get("openSession").feedbackSessionName;

    }
    
    
    @Test
    public void allTests() throws Exception{
        testEditPage();
        
        // Submission
        // Student Results
        // Instructor Results (All views)
        
        
    }
    
    private void testEditPage(){
        
        feedbackEditPage = getFeedbackEditPage();
        
        testNewRubricQuestionFrame();
        testInputValidationForRubricQuestion();
        testAddRubricQuestionAction();
        testEditRubricQuestionAction();
        testDeleteRubricQuestionAction();
    }
    
    

    private void testNewRubricQuestionFrame() {
        ______TS("RUBRIC: new question (frame) link");

        feedbackEditPage.selectNewQuestionType("Rubric question");
        feedbackEditPage.clickNewQuestionButton();
        assertTrue(feedbackEditPage.verifyNewRubricQuestionFormIsDisplayed());
    }
    
    private void testInputValidationForRubricQuestion() {
        
        ______TS("empty question text");

        feedbackEditPage.clickAddQuestionButton();
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_TEXTINVALID, feedbackEditPage.getStatus());
        
    }

    private void testAddRubricQuestionAction() {
        ______TS("RUBRIC: add question action success");
        
        feedbackEditPage.fillQuestionBox("RUBRIC qn");
        assertNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));
        feedbackEditPage.clickAddQuestionButton();
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED, feedbackEditPage.getStatus());
        assertNotNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackRubricQuestionAddSuccess.html");
    }

    private void testEditRubricQuestionAction() {
        ______TS("RUBRIC: edit question success");
        
        // Click edit button
        assertEquals(true, feedbackEditPage.clickEditQuestionButton(1));
        
        // Check that fields are editable
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackRubricQuestionEdit.html");
        
        feedbackEditPage.fillEditQuestionBox("edited RUBRIC qn text", 1);
        feedbackEditPage.clickSaveExistingQuestionButton(1);
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_EDITED, feedbackEditPage.getStatus());
        
        // Check question text is updated
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackRubricQuestionEditSuccess.html");
        
        ______TS("RUBRIC: edit sub-questions success");
        assertEquals(true, feedbackEditPage.clickEditQuestionButton(1));
        
        // Edit sub-question for row 1
        feedbackEditPage.fillRubricSubQuestionBox("New(0) sub-question text", 1, 0);
        
        // Add new sub-question
        feedbackEditPage.clickAddRubricRowLink(1);
        feedbackEditPage.fillRubricSubQuestionBox("New(1) sub-question text", 1, 2);
        
        // Remove existing sub-question
        feedbackEditPage.clickRemoveRubricRowLinkAndConfirm(1, 1);
 
        // Add new sub-question
        feedbackEditPage.clickAddRubricRowLink(1);
        feedbackEditPage.fillRubricSubQuestionBox("New(2) sub-question text", 1, 3);
        
        // Remove new sub-question
        feedbackEditPage.clickRemoveRubricRowLinkAndConfirm(1, 2);
        
        // Should end up with 2 questions, (0) and (2).
        
        feedbackEditPage.clickSaveExistingQuestionButton(1);
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackRubricQuestionEditSubQuestionSuccess.html");

        ______TS("RUBRIC: edit choices success");
        
        
        feedbackEditPage.clickSaveExistingQuestionButton(1);
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackRubricQuestionEditChoiceSuccess.html");
        
        ______TS("RUBRIC: edit descriptions success");
        feedbackEditPage.clickSaveExistingQuestionButton(1);
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackRubricQuestionEditDescriptionSuccess.html");
        
    }
    
    private void testDeleteRubricQuestionAction() {
        ______TS("RUBRIC: qn delete then cancel");

        feedbackEditPage.clickAndCancel(feedbackEditPage.getDeleteQuestionLink(1));
        assertNotNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));

        ______TS("RUBRIC: qn delete then accept");

        feedbackEditPage.clickAndConfirm(feedbackEditPage.getDeleteQuestionLink(1));
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_DELETED, feedbackEditPage.getStatus());
        assertNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));    
    }
    
    private InstructorFeedbackEditPage getFeedbackEditPage() {
        Url feedbackPageLink = createUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE).
                withUserId(instructorId).withCourseId(courseId).withSessionName(feedbackSessionName);
        return loginAdminToPage(browser, feedbackPageLink, InstructorFeedbackEditPage.class);
    }

    @AfterClass
    public static void classTearDown() throws Exception {
        BrowserPool.release(browser);
    }
}
