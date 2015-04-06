package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertTrue;

import org.openqa.selenium.By;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.common.util.Url;
import teammates.test.driver.AssertHelper;
import teammates.test.driver.BackDoor;
import teammates.test.driver.TestProperties;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.FeedbackSubmitPage;
import teammates.test.pageobjects.InstructorFeedbackEditPage;
import teammates.test.pageobjects.InstructorFeedbacksPage;
import teammates.test.util.Priority;

import com.google.appengine.api.datastore.Text;

/**
 * The first test that covers the 'Edit Feedback Session' page for instructors.
 * This part of the test covers general sessions operations (e.g edit, preview, delete).
 * SUT is {@link InstructorFeedbackEditPage}.
 */
@Priority(-1)
public class InstructorFeedbackEditSessionsUiTest extends BaseUiTestCase {
    private static Browser browser;
    private static InstructorFeedbackEditPage feedbackEditPage;
    private static DataBundle testData;
    private static String instructorId;
    private static String courseId;
    private static String feedbackSessionName;
    /** This contains data for the feedback session to be edited during testing */
    private static FeedbackSessionAttributes editedSession;

    @BeforeClass
    public static void classSetup() throws Exception {
        printTestClassHeader();
        testData = loadDataBundle("/InstructorFeedbackEditSessionsUiTest.json");
        removeAndRestoreTestDataOnServer(testData);

        editedSession = testData.feedbackSessions.get("openSession");
        editedSession.gracePeriod = 30;
        editedSession.sessionVisibleFromTime = Const.TIME_REPRESENTS_FOLLOW_OPENING;
        editedSession.resultsVisibleFromTime = Const.TIME_REPRESENTS_LATER;
        editedSession.instructions = new Text("Please fill in the edited feedback session.");
        editedSession.endTime = TimeHelper.convertToDate("2016-05-01 10:00 PM UTC");

        instructorId = testData.accounts.get("instructorWithSessions").googleId;
        courseId = testData.courses.get("course").id;
        feedbackSessionName = testData.feedbackSessions.get("openSession").feedbackSessionName;

        browser = BrowserPool.getBrowser();
        feedbackEditPage = getFeedbackEditPage();
    }

    @AfterClass
    public static void classTearDown() throws Exception {
        BrowserPool.release(browser);
    }

    @Test
    private void allTests() throws Exception {
        testContent();

        testEditSessionLink();
        testEditSessionAction();

        testPreviewSessionAction();

        testDoneEditingLink();
        
        testDeleteSessionAction();
    }

    private void testContent() {

        ______TS("fresh new page");

        // Verify Html instead of main content to verify copy panel and preview
        // panel
        feedbackEditPage.verifyHtml("/instructorFeedbackEditEmpty.html");

    }

    private void testEditSessionLink(){
        ______TS("edit session link");
        feedbackEditPage.clickEditSessionButton();
        assertTrue(feedbackEditPage.verifyEditSessionBoxIsEnabled());
        
    }

    private void testEditSessionAction() throws Exception{

        ______TS("typical success case");

        feedbackEditPage.clickManualPublishTimeButton();
        feedbackEditPage.clickDefaultVisibleTimeButton();
        feedbackEditPage.editFeedbackSession(editedSession.startTime, editedSession.endTime,
                editedSession.instructions,
                editedSession.gracePeriod);
        feedbackEditPage.verifyStatus(Const.StatusMessages.FEEDBACK_SESSION_EDITED);
        FeedbackSessionAttributes savedSession =
                BackDoor.getFeedbackSession(editedSession.courseId, editedSession.feedbackSessionName);
        assertEquals(editedSession.toString(), savedSession.toString());
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackEditSuccess.html");


        ______TS("test edit page after manual publish");

        // Do a backdoor 'manual' publish.
        editedSession.resultsVisibleFromTime = Const.TIME_REPRESENTS_NOW;
        String status = BackDoor.editFeedbackSession(editedSession);
        assertEquals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS, status);

        feedbackEditPage = getFeedbackEditPage();
        feedbackEditPage.isElementSelected(Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON + "_atvisible");
        //feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackEditPublished.html");
        // Restore defaults
        feedbackEditPage.clickEditSessionButton();
        

        feedbackEditPage.clickEditUncommonSettingsButton();
        feedbackEditPage.clickDefaultPublishTimeButton();
        feedbackEditPage.clickSaveSessionButton();
    }

    private void testPreviewSessionAction() {

        // add questions for previewing
        feedbackEditPage.clickNewQuestionButton();
        assertTrue(feedbackEditPage.verifyNewEssayQuestionFormIsDisplayed());
        feedbackEditPage.fillQuestionBox("question for me");
        feedbackEditPage.selectRecipientsToBeStudents();
        feedbackEditPage.clickAddQuestionButton();

        feedbackEditPage.clickNewQuestionButton();
        assertTrue(feedbackEditPage.verifyNewEssayQuestionFormIsDisplayed());
        feedbackEditPage.fillQuestionBox("question for students");
        feedbackEditPage.selectGiverToBeStudents();
        feedbackEditPage.clickAddQuestionButton();

        feedbackEditPage.clickNewQuestionButton();
        assertTrue(feedbackEditPage.verifyNewEssayQuestionFormIsDisplayed());
        feedbackEditPage.fillQuestionBox("question for instructors");
        feedbackEditPage.selectGiverToBeInstructors();
        feedbackEditPage.clickAddQuestionButton();

        ______TS("preview as student");

        FeedbackSubmitPage previewPage;
        previewPage = feedbackEditPage.clickPreviewAsStudentButton();
        previewPage.verifyHtmlMainContent("/studentFeedbackSubmitPagePreview.html");
        previewPage.closeCurrentWindowAndSwitchToParentWindow();

        ______TS("preview as instructor");

        previewPage.waitForElementPresence(By.id("button_preview_instructor"), 15);
        previewPage = feedbackEditPage.clickPreviewAsInstructorButton();
        previewPage.verifyHtmlMainContent("/instructorFeedbackSubmitPagePreview.html");
        previewPage.closeCurrentWindowAndSwitchToParentWindow();
    }

    private void testDoneEditingLink() {
        InstructorFeedbacksPage feedbackPage = feedbackEditPage.clickDoneEditingLink();
        
        ______TS("Compare URLs");
        
        String expectedRedirectUrl = TestProperties.inst().TEAMMATES_URL + 
                Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE + 
                "?" + Const.ParamsNames.USER_ID +
                "=" + instructorId + "&" + 
                Const.ParamsNames.COURSE_ID + 
                "=" + courseId + "&" +
                Const.ParamsNames.FEEDBACK_SESSION_NAME +
                "=" + feedbackSessionName.replaceAll(" ", "%20");
        assertEquals(expectedRedirectUrl, feedbackPage.getPageUrl());
        
        ______TS("Check for highlight on last modified row");
        
        feedbackPage.verifyHtmlMainContent("/instructorFeedbackDoneEditing.html");
        
        // restore feedbackeditpage
        feedbackEditPage = getFeedbackEditPage();
    }

    private void testDeleteSessionAction() {

        ______TS("session delete then cancel");

        feedbackEditPage.clickAndCancel(feedbackEditPage.getDeleteSessionLink());
        assertNotNull(BackDoor.getFeedbackSession(courseId, feedbackSessionName));

        ______TS("session delete then accept");

        // check redirect to main feedback page
        InstructorFeedbacksPage feedbackPage = feedbackEditPage.deleteSession();
        AssertHelper.assertContains(Const.StatusMessages.FEEDBACK_SESSION_DELETED,
                feedbackPage.getStatus());
        assertNull(BackDoor.getFeedbackSession(courseId, feedbackSessionName));
    }

    public static void printTestClassHeader() {
        print("[============================="
                + Thread.currentThread().getStackTrace()[2].getClassName()
                + "=============================]");
    }

    protected static void print(String message) {
        System.out.println(message);
    }

    private static InstructorFeedbackEditPage getFeedbackEditPage() {
        Url feedbackPageLink = createUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE).
                withUserId(instructorId).withCourseId(courseId).withSessionName(feedbackSessionName);
        return loginAdminToPage(browser, feedbackPageLink, InstructorFeedbackEditPage.class);
    }

}
