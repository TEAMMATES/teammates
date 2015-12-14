package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertTrue;

import org.openqa.selenium.By;
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

public class FeedbackRankQuestionUiTest extends FeedbackQuestionUiTest {
    private static Browser browser;
    private static InstructorFeedbackEditPage feedbackEditPage;
    private static FeedbackSubmitPage submitPage;
    private static InstructorFeedbackResultsPage instructorResultsPage;
    private static StudentFeedbackResultsPage studentResultsPage;
    private static DataBundle testData;

    private static String courseId;
    private static String instructorEditFSName;
    private static String instructorSubmitFSName;
    private static String studentSubmitFSName;
    
    private static String instructorId;
    
    @BeforeClass
    public void classSetup() throws Exception {
        printTestClassHeader();
        testData = loadDataBundle("/FeedbackRankQuestionUiTest.json");
        removeAndRestoreTestDataOnServer(testData);
        browser = BrowserPool.getBrowser();
        
        instructorId = testData.accounts.get("instructor1").googleId;
        courseId = testData.courses.get("course").id;
        instructorEditFSName = testData.feedbackSessions.get("edit").feedbackSessionName;
        feedbackEditPage = getFeedbackEditPage();

    }
    
    
    //@Test
    public void testStudentSubmitAndResultsPages() {
        ______TS("Rank submission: input disabled for closed session");
        
        submitPage = loginToStudentFeedbackSubmitPage("alice.tmms@FRankQnUiT.CS2104", "closedSession");
        
        
        ______TS("Rank single question submission");
        FeedbackQuestionAttributes fq = BackDoor.getFeedbackQuestion("FRankQnUiT.CS2104", "Third Session", 1);
        
        FeedbackQuestionSubmitPage questionSubmitPage = loginToStudentFeedbackQuestionSubmitPage("alice.tmms@FRankQnUiT.CS2104", "openSession3", fq.getId());

        
        questionSubmitPage.clickSubmitButton();
        assertEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED, questionSubmitPage.getStatus());
        
        assertNotNull(BackDoor.getFeedbackResponse(fq.getId(),
                "alice.b.tmms@gmail.tmt",
                "alice.b.tmms@gmail.tmt"));
        
        
        ______TS("Rank submission: partial ranking");
        
        submitPage = loginToStudentFeedbackSubmitPage("alice.tmms@FRankQnUiT.CS2104", "openSession2");
        
        
        // Submit
        submitPage.clickSubmitButton();
        assertEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED, submitPage.getStatus());
        
        // Go back to submission page and verify html
        submitPage = loginToStudentFeedbackSubmitPage("alice.tmms@FRankQnUiT.CS2104", "openSession2");
        submitPage.waitForCellHoverToDisappear();
        submitPage.verifyHtmlMainContent("/studentFeedbackSubmitPageRankSuccess.html");
        
        ______TS("Rank submission: full ranking");
        
        
        ______TS("Rank submission: duplicates not allowed");
        
        
        ______TS("Rank submission: duplicates allowed");
        
        ______TS("Rank student results");

        studentResultsPage = loginToStudentFeedbackResultsPage("alice.tmms@FRankQnUiT.CS2104", "openSession2");
        studentResultsPage.verifyHtmlMainContent("/studentFeedbackResultsPageRank.html");
    }
    
    //@Test
    public void testInstructorSubmitAndResultsPage() {
        
        ______TS("Rank submission: input disabled for closed session");
        
        submitPage = loginToInstructorFeedbackSubmitPage("teammates.test.instructor", "closedSession");
        int qnNumber = 1;
        int responseNumber = 0;
        int rowNumber = 0;
        assertFalse(submitPage.isNamedElementEnabled(Const.ParamsNames.FEEDBACK_QUESTION_RANKOPTION + "-" 
                                                     + qnNumber + "-" + responseNumber + "-" + rowNumber));
        

        ______TS("Rank submission: test dropdown has less options if students are not visible");

        ______TS("Rank standard submission");
        
        
        ______TS("Rank instructor results : question");

        instructorResultsPage = loginToInstructorFeedbackResultsPageWithViewType("teammates.test.instructor", "openSession2", false, "question");
        instructorResultsPage.waitForPanelsToCollapse();
        
        instructorResultsPage.verifyHtmlMainContent("/instructorFeedbackResultsPageRankQuestionView.html");
        
        
        ______TS("Rank instructor results : Giver > Recipient > Question");
        instructorResultsPage = loginToInstructorFeedbackResultsPageWithViewType("teammates.test.instructor", "openSession2", false, "giver-recipient-question");
        instructorResultsPage.waitForPanelsToCollapse();
        instructorResultsPage.verifyHtmlMainContent("/instructorFeedbackResultsPageRankGRQView.html");
        
        ______TS("Rank instructor results : Giver > Question > Recipient");
        instructorResultsPage = loginToInstructorFeedbackResultsPageWithViewType("teammates.test.instructor", "openSession2", false, "giver-question-recipient");
        instructorResultsPage.waitForPanelsToCollapse();
        instructorResultsPage.verifyHtmlMainContent("/instructorFeedbackResultsPageRankGQRView.html");
        
        ______TS("Rank instructor results : Recipient > Giver > Question ");
        instructorResultsPage = loginToInstructorFeedbackResultsPageWithViewType("teammates.test.instructor", "openSession2", false, "recipient-question-giver");
        instructorResultsPage.waitForPanelsToCollapse();
        instructorResultsPage.verifyHtmlMainContent("/instructorFeedbackResultsPageRankRQGView.html");
        
        ______TS("Rank instructor results : Recipient > Question > Giver");
        instructorResultsPage = loginToInstructorFeedbackResultsPageWithViewType("teammates.test.instructor", "openSession2", false, "recipient-giver-question");
        instructorResultsPage.waitForPanelsToCollapse();
        instructorResultsPage.verifyHtmlMainContent("/instructorFeedbackResultsPageRankRGQView.html");
        
    }
    


    @Test
    public void testEditPage(){
        testNewQuestionFrame();
        testInputValidation();
        testCustomizeOptions();
        testAddQuestionAction();
        testEditQuestionAction();
        testDeleteQuestionAction();
    }
    
    
    public void testNewQuestionFrame() {
        testNewRankRecipientsQuestionFrame();
        feedbackEditPage.reloadPage();
        testNewRankOptionsQuestionFrame();
    }

    private void testNewRankRecipientsQuestionFrame() {
        ______TS("Rank recipients: new question (frame)");

        feedbackEditPage.selectNewQuestionType("Rank (recipients) question");
        feedbackEditPage.clickNewQuestionButton();
        assertTrue(feedbackEditPage.verifyNewRankRecipientsQuestionFormIsDisplayed());
        
    }
 
    private void testNewRankOptionsQuestionFrame() {
        ______TS("Rank options: new question (frame)");
        feedbackEditPage.selectNewQuestionType("Rank (options) question");
        feedbackEditPage.clickNewQuestionButton();
        assertTrue(feedbackEditPage.verifyNewRankOptionsQuestionFormIsDisplayed());
    }
    
    public void testInputValidation() {
        
        ______TS("Rank edit: empty question text");

        feedbackEditPage.clickAddQuestionButton();
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_TEXTINVALID, feedbackEditPage.getStatus());
        
    }
    
    @Override
    public void testCustomizeOptions() {
        // todo
    }
    

    public void testAddQuestionAction() {
        ______TS("Rank edit: add rank option question action success");
        
        assertNull(BackDoor.getFeedbackQuestion(courseId, instructorEditFSName, 1));
        
        feedbackEditPage.fillQuestionBox("Rank qn");
        feedbackEditPage.fillRankOptionForNewQuestion(0, "Option 1 <>");
        feedbackEditPage.fillRankOptionForNewQuestion(1, "  Option 2  ");
        
        // blank option, we check that it is removed later on
        feedbackEditPage.clickAddMoreRankOptionLinkForNewQn(); 
        
        
        feedbackEditPage.tickDuplicatesAllowedCheckboxForNewQuestion();
        
        feedbackEditPage.clickAddQuestionButton();
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED, feedbackEditPage.getStatus());
        assertNotNull(BackDoor.getFeedbackQuestion(courseId, instructorEditFSName, 1));
        
        
        ______TS("Rank edit: add rank recipient question action success");
        feedbackEditPage.selectNewQuestionType("Rank (recipients) question");
        feedbackEditPage.clickNewQuestionButton();
        
        
        assertNull(BackDoor.getFeedbackQuestion(courseId, instructorEditFSName, 2));
        
        feedbackEditPage.verifyRankOptionIsHiddenForNewQuestion(0);
        feedbackEditPage.verifyRankOptionIsHiddenForNewQuestion(1);
        feedbackEditPage.fillQuestionBox("Rank recipients qn");
        
        feedbackEditPage.clickAddQuestionButton();
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED, feedbackEditPage.getStatus());
        assertNotNull(BackDoor.getFeedbackQuestion(courseId, instructorEditFSName, 2));
        
       // feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackRankQuestionAddSuccess.html");
    }

    public void testEditQuestionAction() {
        ______TS("rank edit: edit rank options question success");
        assertTrue(feedbackEditPage.clickEditQuestionButton(1));
        
        // Verify that fields are editable
        //feedbackEditPage.verifyHtmlPart(By.id("questionTable1"),
        //                                "/instructorFeedbackRankQuestionEdit.html");
        
        feedbackEditPage.fillEditQuestionBox("edited Rank qn text", 1);

        feedbackEditPage.clickRemoveRankOptionLink(1, 0);
        assertEquals("Should still remain with 2 options,"
                         + "less than 2 options should not be permitted",
                     2, feedbackEditPage.getNumOfOptionsInRankOptionsQuestion(1));
        
        feedbackEditPage.fillRankOptionForQuestion(1, 1, " (Edited) Option 2 ");
        
        // Should end up with 4 choices, including (1) and (2)
        feedbackEditPage.clickAddMoreRankOptionLink(1);
        feedbackEditPage.clickAddMoreRankOptionLink(1);
        feedbackEditPage.fillRankOptionForQuestion(1, 2, "  <New> Option 3 ");
        feedbackEditPage.fillRankOptionForQuestion(1, 3, "Option 4 (slightly longer text for this one)");
        
        feedbackEditPage.untickDuplicatesAllowedCheckboxForQuestion(1);
        
        feedbackEditPage.clickSaveExistingQuestionButton(1);
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_EDITED, feedbackEditPage.getStatus());
        //feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackRankQuestionEditSuccess.html");

        ______TS("rank edit: edit rank recipients question success");
        assertTrue(feedbackEditPage.clickEditQuestionButton(2));
        
        feedbackEditPage.tickDuplicatesAllowedCheckboxForQuestion(2);
        feedbackEditPage.clickSaveExistingQuestionButton(2);
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_EDITED, feedbackEditPage.getStatus());
        assertTrue(feedbackEditPage.isRankDuplicatesAllowedChecked(2));

    }
    
    public void testDeleteQuestionAction() {
        ______TS("rank: qn delete");

        feedbackEditPage.clickAndConfirm(feedbackEditPage.getDeleteQuestionLink(2));
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_DELETED, feedbackEditPage.getStatus());
        assertNull(BackDoor.getFeedbackQuestion(courseId, instructorEditFSName, 2));
        
        feedbackEditPage.clickAndConfirm(feedbackEditPage.getDeleteQuestionLink(1));
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_DELETED, feedbackEditPage.getStatus());
        assertNull(BackDoor.getFeedbackQuestion(courseId, instructorEditFSName, 1));
    }
        
    private InstructorFeedbackEditPage getFeedbackEditPage() {
        Url feedbackPageLink = createUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE).
                withUserId(instructorId).withCourseId(courseId).withSessionName(instructorEditFSName);
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
        Url editUrl 
            = createUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_PAGE)
                    .withUserId(testData.instructors.get(instructorName).googleId)
                    .withCourseId(testData.feedbackSessions.get(fsName).courseId)
                    .withSessionName(testData.feedbackSessions.get(fsName).feedbackSessionName);
        
        if (needAjax){
            editUrl = editUrl.withParam(Const.ParamsNames.FEEDBACK_RESULTS_NEED_AJAX, String.valueOf(needAjax));
        }
        
        if (viewType != null){
            editUrl = editUrl.withParam(Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, viewType);
        }
        
        return loginAdminToPage(browser, editUrl, InstructorFeedbackResultsPage.class);
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
