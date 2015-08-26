package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertTrue;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.Url;
import teammates.test.driver.BackDoor;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.FeedbackQuestionSubmitPage;
import teammates.test.pageobjects.FeedbackSubmitPage;
import teammates.test.pageobjects.InstructorFeedbackEditPage;
import teammates.test.pageobjects.InstructorFeedbackResultsPage;
import teammates.test.pageobjects.StudentFeedbackResultsPage;

public class FeedbackRubricQuestionUiTest extends FeedbackQuestionUiTest {
    private static Browser browser;
    private static InstructorFeedbackEditPage feedbackEditPage;
    private static FeedbackSubmitPage submitPage;
    private static InstructorFeedbackResultsPage instructorResultsPage;
    private StudentFeedbackResultsPage studentResultsPage;
    private static DataBundle testData;

    private static String courseId;
    private static String feedbackSessionName;
    private static String instructorId;
    
    @BeforeClass
    public void classSetup() throws Exception {
        printTestClassHeader();
        testData = loadDataBundle("/FeedbackRubricQuestionUiTest.json");
        removeAndRestoreTestDataOnServer(testData);
        browser = BrowserPool.getBrowser();
        
        instructorId = testData.accounts.get("instructor1").googleId;
        courseId = testData.courses.get("course").id;
        feedbackSessionName = testData.feedbackSessions.get("openSession").feedbackSessionName;
        feedbackEditPage = getFeedbackEditPage();

    }
    
    
    @Test
    public void allTests() throws Exception{
        testEditPage();
        testInstructorSubmitPage();
        testStudentSubmitPage();
        testStudentResultsPage();
        testInstructorResultsPage();
        testStudentQuestionSubmitPage();
    }

    private void testStudentResultsPage() {
        ______TS("test rubric question student results page");

        studentResultsPage = loginToStudentFeedbackResultsPage("alice.tmms@FRubricQnUiT.CS2104", "openSession2");
        studentResultsPage.verifyHtmlMainContent("/studentFeedbackResultsPageRubric.html");
    }
    
    private void testInstructorResultsPage() {
        ______TS("test rubric question instructor results page");

        // Question view
        instructorResultsPage = loginToInstructorFeedbackResultsPageWithViewType("teammates.test.instructor", "openSession2", false, "question");
        instructorResultsPage.waitForPanelsToCollapse();
        
        instructorResultsPage.verifyHtmlMainContent("/instructorFeedbackResultsPageRubricQuestionView.html");
        
        
        // Giver Recipient Question View
        instructorResultsPage = loginToInstructorFeedbackResultsPageWithViewType("teammates.test.instructor", "openSession2", false, "giver-recipient-question");
        instructorResultsPage.waitForPanelsToCollapse();
        instructorResultsPage.verifyHtmlMainContent("/instructorFeedbackResultsPageRubricGRQView.html");
        
        // Giver Question Recipient View
        instructorResultsPage = loginToInstructorFeedbackResultsPageWithViewType("teammates.test.instructor", "openSession2", false, "giver-question-recipient");
        instructorResultsPage.waitForPanelsToCollapse();
        instructorResultsPage.verifyHtmlMainContent("/instructorFeedbackResultsPageRubricGQRView.html");
        
        // Recipient Giver Question View
        instructorResultsPage = loginToInstructorFeedbackResultsPageWithViewType("teammates.test.instructor", "openSession2", false, "recipient-question-giver");
        instructorResultsPage.waitForPanelsToCollapse();
        instructorResultsPage.verifyHtmlMainContent("/instructorFeedbackResultsPageRubricRQGView.html");
        
        // Recipient Question Giver View
        instructorResultsPage = loginToInstructorFeedbackResultsPageWithViewType("teammates.test.instructor", "openSession2", false, "recipient-giver-question");
        instructorResultsPage.waitForPanelsToCollapse();
        instructorResultsPage.verifyHtmlMainContent("/instructorFeedbackResultsPageRubricRGQView.html");
        
    }
    
    private void testInstructorSubmitPage() {
        
        ______TS("test rubric question input disabled for closed session");
        
        submitPage = loginToInstructorFeedbackSubmitPage("teammates.test.instructor", "closedSession");
        int qnNumber = 1;
        int responseNumber = 0;
        int rowNumber = 0;
        assertFalse(submitPage.isNamedElementEnabled(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_CHOICE+"-"+qnNumber+"-"+responseNumber+"-"+rowNumber));
        

        ______TS("test rubric question submission");
        // Done in testStudentSubmitPage
        
    }

    private void testStudentSubmitPage() {
        
        ______TS("test rubric question input disabled for closed session");
        
        submitPage = loginToStudentFeedbackSubmitPage("alice.tmms@FRubricQnUiT.CS2104", "closedSession");
        int qnNumber = 1;
        int responseNumber = 0;
        int rowNumber = 0;
        assertFalse(submitPage.isNamedElementEnabled(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_CHOICE+"-"+qnNumber+"-"+responseNumber+"-"+rowNumber));

        ______TS("test rubric question submission");
        
        submitPage = loginToStudentFeedbackSubmitPage("alice.tmms@FRubricQnUiT.CS2104", "openSession2");
        assertTrue(submitPage.isNamedElementEnabled(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_CHOICE+"-"+qnNumber+"-"+responseNumber+"-"+rowNumber));
        
        // Select radio input
        submitPage.clickRubricRadio(1, 0, 0, 0);
        submitPage.clickRubricRadio(1, 0, 1, 1);
        submitPage.clickRubricRadio(1, 0, 0, 1);
        
        // Select table cell
        submitPage.clickRubricCell(1, 1, 0, 1);
        submitPage.clickRubricCell(1, 1, 1, 0);
        submitPage.clickRubricCell(1, 1, 0, 0);

        // Submit
        submitPage.clickSubmitButton();
        assertEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED, submitPage.getStatus());
        
        // Go back to submission page and verify html
        submitPage = loginToStudentFeedbackSubmitPage("alice.tmms@FRubricQnUiT.CS2104", "openSession2");
        submitPage.verifyHtmlMainContent("/studentFeedbackSubmitPageRubricSuccess.html");
        
    }
    
    private void testStudentQuestionSubmitPage() {
        
        ______TS("test rubric question input for FeedbackQuestionSubmissionEdit");
        FeedbackQuestionAttributes fq = BackDoor.getFeedbackQuestion("FRubricQnUiT.CS2104", "Third Session", 1);
        
        FeedbackQuestionSubmitPage questionSubmitPage = loginToStudentFeedbackQuestionSubmitPage("alice.tmms@FRubricQnUiT.CS2104", "openSession3", fq.getId());

        // Select table cell
        questionSubmitPage.clickRubricCell(0, 0, 1);
        questionSubmitPage.clickRubricCell(0, 1, 0);
        questionSubmitPage.clickRubricCell(0, 0, 0);

        // Submit
        questionSubmitPage.clickSubmitButton();
        assertEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED, questionSubmitPage.getStatus());
        
        assertNotNull(BackDoor.getFeedbackResponse(fq.getId(),
                "alice.b.tmms@gmail.tmt",
                "alice.b.tmms@gmail.tmt"));
        
    }

    private void testEditPage(){
        testNewQuestionFrame();
        testInputValidation();
        testCustomizeOptions();
        testAddQuestionAction();
        testEditQuestionAction();
        testDeleteQuestionAction();
        testInputJsValidationForRubricQuestion();
    }
    
    

    public void testNewQuestionFrame() {
        ______TS("RUBRIC: new question (frame) link");

        feedbackEditPage.selectNewQuestionType("Rubric question");
        feedbackEditPage.clickNewQuestionButton();
        assertTrue(feedbackEditPage.verifyNewRubricQuestionFormIsDisplayed());
    }
    
    public void testInputValidation() {
        
        ______TS("empty question text");

        feedbackEditPage.clickAddQuestionButton();
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_TEXTINVALID, feedbackEditPage.getStatus());
    }
    
    public void testCustomizeOptions() {
        
        // TODO somebody do this?
        
    }

    public void testAddQuestionAction() {
        ______TS("RUBRIC: add question action success");
        
        feedbackEditPage.fillQuestionBox("RUBRIC qn");
        assertNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));
        feedbackEditPage.clickAddQuestionButton();
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED, feedbackEditPage.getStatus());
        assertNotNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackRubricQuestionAddSuccess.html");
    }

    public void testEditQuestionAction() {
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
        
        // Remove existing sub-questions
        feedbackEditPage.clickRemoveRubricRowLinkAndConfirm(1, 0);
        feedbackEditPage.clickRemoveRubricRowLinkAndConfirm(1, 1);
 
        // Add new sub-question
        feedbackEditPage.clickAddRubricRowLink(1);
        feedbackEditPage.fillRubricSubQuestionBox("New(2) sub-question text", 1, 3);
        
        // Remove new sub-question
        feedbackEditPage.clickRemoveRubricRowLinkAndConfirm(1, 2);
        
        // Should end up with 1 question
        
        feedbackEditPage.clickSaveExistingQuestionButton(1);
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackRubricQuestionEditSubQuestionSuccess.html");

        ______TS("RUBRIC: edit choices success");
        assertEquals(true, feedbackEditPage.clickEditQuestionButton(1));
        
        // Edit choice for col 1
        feedbackEditPage.fillRubricChoiceBox("New(0) choice", 1, 0);
        
        // Add new choice
        feedbackEditPage.clickAddRubricColLink(1);
        feedbackEditPage.fillRubricChoiceBox("New(1) choice", 1, 4);
        
        // Remove existing choice
        feedbackEditPage.clickRemoveRubricColLinkAndConfirm(1, 0);
 
        // Add new choice
        feedbackEditPage.clickAddRubricColLink(1);
        feedbackEditPage.fillRubricChoiceBox("New(2) choice", 1, 5);
        
        // Remove new choice
        feedbackEditPage.clickRemoveRubricColLinkAndConfirm(1, 4);
        
        // Should end up with 4 choices, including (1) and (2)
        
        feedbackEditPage.clickSaveExistingQuestionButton(1);
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackRubricQuestionEditChoiceSuccess.html");

        ______TS("RUBRIC: edit descriptions success");
        assertEquals(true, feedbackEditPage.clickEditQuestionButton(1));
        
        // Edit description for 0-0
        feedbackEditPage.fillRubricDescriptionBox("New(0) description", 1, 0, 0);
        
        // Edit description for a new row, to test if the js generated html works.
        feedbackEditPage.clickAddRubricRowLink(1);
        feedbackEditPage.fillRubricSubQuestionBox("New sub-question text", 1, 1);
       
        feedbackEditPage.fillRubricDescriptionBox("New(1) description", 1, 1, 0);
        
        // Should end up with 2 rubric descriptions, (0) and (1)
        
        feedbackEditPage.clickSaveExistingQuestionButton(1);
        
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackRubricQuestionEditDescriptionSuccess.html");
    }
    
    public void testDeleteQuestionAction() {
        ______TS("RUBRIC: qn delete then cancel");

        feedbackEditPage.clickAndCancel(feedbackEditPage.getDeleteQuestionLink(1));
        assertNotNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));

        ______TS("RUBRIC: qn delete then accept");

        feedbackEditPage.clickAndConfirm(feedbackEditPage.getDeleteQuestionLink(1));
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_DELETED, feedbackEditPage.getStatus());
        assertNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));    
    }
    
    private void testInputJsValidationForRubricQuestion() {
        // this tests whether the JS validation disallows empty rubric options
        
        ______TS("JS validation test");

        // add a new question
        feedbackEditPage.selectNewQuestionType("Rubric question");
        feedbackEditPage.clickNewQuestionButton();
        
        // start editing it
        feedbackEditPage.fillQuestionBox("RUBRIC qn JS validation test");
        feedbackEditPage.clickAddQuestionButton();
        
        assertEquals(true, feedbackEditPage.clickEditQuestionButton(1));
        
        // try to remove everything
        feedbackEditPage.clickRemoveRubricRowLinkAndConfirm(1, 1);
        feedbackEditPage.clickRemoveRubricRowLinkAndConfirm(1, 0);
        feedbackEditPage.clickRemoveRubricColLinkAndConfirm(1, 3);
        feedbackEditPage.clickRemoveRubricColLinkAndConfirm(1, 2);
        feedbackEditPage.clickRemoveRubricColLinkAndConfirm(1, 1);
        feedbackEditPage.clickRemoveRubricColLinkAndConfirm(1, 0);
        
        // TODO check if the rubric column and link is indeed empty
        
        // add something so that we know that the elements are still there
        // and so that we don't get empty sub question error
        feedbackEditPage.fillRubricSubQuestionBox("New sub-question text", 1, 0);
        feedbackEditPage.fillRubricDescriptionBox("New(0) description", 1, 0, 0);
        
        feedbackEditPage.clickSaveExistingQuestionButton(1);
        
        assertEquals("Too little choices for Rubric question. Minimum number of options is: 2", feedbackEditPage.getStatus());
    }
    
    private InstructorFeedbackEditPage getFeedbackEditPage() {
        Url feedbackPageLink = createUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE).
                withUserId(instructorId).withCourseId(courseId).withSessionName(feedbackSessionName);
        return loginAdminToPage(browser, feedbackPageLink, InstructorFeedbackEditPage.class);
    }
    
    private FeedbackSubmitPage loginToInstructorFeedbackSubmitPage(
            String instructorName, String fsName) {
        Url editUrl = createUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_SUBMISSION_EDIT_PAGE)
                .withUserId(testData.instructors.get(instructorName).googleId)
                .withCourseId(testData.feedbackSessions.get(fsName).courseId)
                .withSessionName(testData.feedbackSessions.get(fsName).feedbackSessionName);
        return loginAdminToPage(browser, editUrl, FeedbackSubmitPage.class);
    }
    
    private FeedbackSubmitPage loginToStudentFeedbackSubmitPage(
            String studentName, String fsName) {
        Url editUrl = createUrl(Const.ActionURIs.STUDENT_FEEDBACK_SUBMISSION_EDIT_PAGE)
                .withUserId(testData.students.get(studentName).googleId)
                .withCourseId(testData.feedbackSessions.get(fsName).courseId)
                .withSessionName(testData.feedbackSessions.get(fsName).feedbackSessionName);
        return loginAdminToPage(browser, editUrl, FeedbackSubmitPage.class);
    }
    
    private StudentFeedbackResultsPage loginToStudentFeedbackResultsPage(
            String studentName, String fsName) {
        Url editUrl = createUrl(Const.ActionURIs.STUDENT_FEEDBACK_RESULTS_PAGE)
                .withUserId(testData.students.get(studentName).googleId)
                .withCourseId(testData.feedbackSessions.get(fsName).courseId)
                .withSessionName(testData.feedbackSessions.get(fsName).feedbackSessionName);
        return loginAdminToPage(browser, editUrl,
                StudentFeedbackResultsPage.class);
    }
    
    private InstructorFeedbackResultsPage loginToInstructorFeedbackResultsPageWithViewType(
            String instructorName, String fsName, boolean needAjax, String viewType) {
        Url editUrl = createUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_PAGE)
                    .withUserId(testData.instructors.get(instructorName).googleId)
                    .withCourseId(testData.feedbackSessions.get(fsName).courseId)
                    .withSessionName(testData.feedbackSessions.get(fsName).feedbackSessionName);
        
        if(needAjax){
            editUrl = editUrl.withParam(Const.ParamsNames.FEEDBACK_RESULTS_NEED_AJAX, String.valueOf(needAjax));
        }
        
        if(viewType != null){
            editUrl = editUrl.withParam(Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, viewType);
        }
        
        return loginAdminToPage(browser, editUrl,
                InstructorFeedbackResultsPage.class);
    }
    
    
    private FeedbackQuestionSubmitPage loginToStudentFeedbackQuestionSubmitPage(
            String studentName, String fsName, String questionId) {
        StudentAttributes s = testData.students.get(studentName);
        Url editUrl = createUrl(
                Const.ActionURIs.STUDENT_FEEDBACK_QUESTION_SUBMISSION_EDIT_PAGE)
                .withUserId(s.googleId)
                .withCourseId(testData.feedbackSessions.get(fsName).courseId)
                .withSessionName(testData.feedbackSessions.get(fsName).feedbackSessionName)
                .withParam(Const.ParamsNames.FEEDBACK_QUESTION_ID, questionId);
        
        return loginAdminToPage(browser, editUrl,FeedbackQuestionSubmitPage.class);
    }

    @AfterClass
    public static void classTearDown() throws Exception {
        BrowserPool.release(browser);
    }
}
