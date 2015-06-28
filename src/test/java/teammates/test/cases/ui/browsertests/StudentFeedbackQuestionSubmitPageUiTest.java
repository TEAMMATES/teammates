package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertTrue;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.Url;
import teammates.test.driver.BackDoor;
import teammates.test.pageobjects.AppPage;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.FeedbackQuestionSubmitPage;

public class StudentFeedbackQuestionSubmitPageUiTest extends BaseUiTestCase {
    private static DataBundle testData;
    private static Browser browser;
    private FeedbackQuestionSubmitPage submitPage;
    private FeedbackQuestionAttributes fqOpen;
    private FeedbackQuestionAttributes fqClosed;
    private FeedbackQuestionAttributes fq;

    @BeforeClass
    public static void classSetup() throws Exception {
        printTestClassHeader();
        testData = loadDataBundle("/StudentFeedbackQuestionSubmitPageUiTest.json");
        removeAndRestoreTestDataOnServer(testData);
        
        browser = BrowserPool.getBrowser();
    }

    @Test
    public void testAll() throws Exception {
        testContent();
        testSubmitAction();
        // no links to test
    }

    private void testContent() throws Exception {

        AppPage.logout(browser);
        
        ______TS("unreg student");
        
        fqOpen = BackDoor.getFeedbackQuestion("SFQSubmitUiT.CS2104", "Open Session", 1);
        fqClosed = BackDoor.getFeedbackQuestion("SFQSubmitUiT.CS2104", "Closed Session", 1);
        
        // Open session
        StudentAttributes unregStudent = testData.students.get("Unregistered");
        submitPage = goToStudentFeedbackQuestionSubmitPage(unregStudent, "Open Session", fqOpen.getId());

        // This is the full HTML verification for Unregistered Student Feedback Question Submit Page, the rest can all be verifyMainHtml
        submitPage.verifyHtml("/unregisteredStudentFeedbackQuestionSubmitPageOpen.html");
        
        // closed session
        submitPage = goToStudentFeedbackQuestionSubmitPage(unregStudent, "Closed Session", fqClosed.getId());
        submitPage.verifyHtmlMainContent("/unregisteredStudentFeedbackQuestionSubmitPageClosed.html");
        
        ______TS("Awaiting session");

        fq = BackDoor.getFeedbackQuestion("SFQSubmitUiT.CS2104", "Awaiting Session", 1);
        submitPage = loginToStudentFeedbackQuestionSubmitPage("Alice", "Awaiting Session", fq.getId());

        // This is the full HTML verification for Registered Student Feedback Question Submit Page, the rest can all be verifyMainHtml
        submitPage.verifyHtml("/studentFeedbackQuestionSubmitPageAwaiting.html");
        
        ______TS("Open session");

        submitPage = loginToStudentFeedbackQuestionSubmitPage("Alice", "Open Session", fqOpen.getId());
        submitPage.verifyHtmlMainContent("/studentFeedbackQuestionSubmitPageOpen.html");

        ______TS("Grace period session");

        FeedbackSessionAttributes fs = BackDoor.getFeedbackSession("SFQSubmitUiT.CS2104", "Open Session");
        submitPage = loginToStudentFeedbackQuestionSubmitPage("Alice", "Open Session", fqOpen.getId());

        Calendar endDate = GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"));
        fs.timeZone = 0;
        endDate.add(Calendar.MINUTE, -1);
        fs.endTime = endDate.getTime();
        fs.gracePeriod = 10;
        BackDoor.editFeedbackSession(fs);

        submitPage = loginToStudentFeedbackQuestionSubmitPage("Alice", "Open Session", fqOpen.getId());

        assertEquals(false, submitPage.getSubmitButton().isEnabled());

        ______TS("Closed session");

        submitPage = loginToStudentFeedbackQuestionSubmitPage("Alice","Closed Session", fqClosed.getId());
        submitPage.verifyHtmlMainContent("/studentFeedbackQuestionSubmitPageClosed.html");
    }

    private void testSubmitAction() {
        removeAndRestoreTestDataOnServer(testData);
        
        ______TS("create new responses");

        fq = BackDoor.getFeedbackQuestion("SFQSubmitUiT.CS2104", "Open Session", 1);
        submitPage = loginToStudentFeedbackQuestionSubmitPage("Alice", "Open Session", fq.getId());

        submitPage.fillResponseTextBox(1, 0, "Test Self Feedback");
        assertNull(BackDoor.getFeedbackResponse(fq.getId(),
                                                "SFQSubmitUiT.alice.b@gmail.tmt",
                                                "SFQSubmitUiT.alice.b@gmail.tmt"));

        submitPage.clickSubmitButton();

        assertEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED, submitPage.getStatus());

        assertNotNull(BackDoor.getFeedbackResponse(fq.getId(),
                                                   "SFQSubmitUiT.alice.b@gmail.tmt",
                                                   "SFQSubmitUiT.alice.b@gmail.tmt"));
        
        assertEquals("Test Self Feedback", 
                     BackDoor.getFeedbackResponse(fq.getId(), 
                                                  "SFQSubmitUiT.alice.b@gmail.tmt",
                                                  "SFQSubmitUiT.alice.b@gmail.tmt").getResponseDetails().getAnswerString());

        ______TS("edit existing response");

        String editedResponse = "Edited self feedback.";
        submitPage.fillResponseTextBox(1, 0, editedResponse);
        submitPage.clickSubmitButton();

        assertEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED, submitPage.getStatus());
        assertNotNull(BackDoor.getFeedbackResponse(fq.getId(), 
                                                   "SFQSubmitUiT.alice.b@gmail.tmt",
                                                   "SFQSubmitUiT.alice.b@gmail.tmt"));
        
        assertEquals(editedResponse,
                     BackDoor.getFeedbackResponse(fq.getId(),
                                                  "SFQSubmitUiT.alice.b@gmail.tmt", 
                                                  "SFQSubmitUiT.alice.b@gmail.tmt").getResponseDetails().getAnswerString());
        
        submitPage.verifyHtmlMainContent("/studentFeedbackQuestionSubmitPageFilled.html");

        ______TS("Grace period session,successful submission within grace period");

        FeedbackSessionAttributes fs = BackDoor.getFeedbackSession("SFQSubmitUiT.CS2104", "Open Session");

        submitPage = loginToStudentFeedbackQuestionSubmitPage("Alice", "Open Session", fq.getId());

        Calendar endDate = GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"));

        fs.timeZone = 0;
        endDate.add(Calendar.MINUTE, -1);
        fs.endTime = endDate.getTime();
        fs.gracePeriod = 10;
        BackDoor.editFeedbackSession(fs);

        submitPage.fillResponseTextBox(1, 0, "this is a response edited during grace period");
        submitPage.clickSubmitButton();

        assertTrue(submitPage.getStatus().contains("All responses submitted succesfully!"));

        // test if the button is disabled after the response has been submitted
        submitPage = loginToStudentFeedbackQuestionSubmitPage("Alice", "Open Session", fq.getId());
        assertEquals(false, submitPage.getSubmitButton().isEnabled());

        // test the response submitted during the grace period
        fs = BackDoor.getFeedbackSession("SFQSubmitUiT.CS2104", "Open Session");
        fq = BackDoor.getFeedbackQuestion("SFQSubmitUiT.CS2104", "Open Session", 1);
        
        assertEquals("this is a response edited during grace period", 
                     BackDoor.getFeedbackResponse(fq.getId(), 
                                                  "SFQSubmitUiT.alice.b@gmail.tmt", 
                                                  "SFQSubmitUiT.alice.b@gmail.tmt").getResponseDetails().getAnswerString());
        
        assertEquals("this is a response edited during grace period", submitPage.getTextArea(1, 0).getText());

        submitPage.logout();
        testData = loadDataBundle("/StudentFeedbackQuestionSubmitPageUiTest.json");
        removeAndRestoreTestDataOnServer(testData);

        ______TS("Grace period session,submission failure after grace period");

        fq = BackDoor.getFeedbackQuestion("SFQSubmitUiT.CS2104", "Open Session", 1);
        fs = BackDoor.getFeedbackSession("SFQSubmitUiT.CS2104", "Open Session");
        
        endDate = GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"));

        submitPage = loginToStudentFeedbackQuestionSubmitPage("Alice", "Open Session", fq.getId());

        fs.timeZone = 0;
        endDate.add(Calendar.MINUTE, -20);
        fs.endTime = endDate.getTime();
        fs.gracePeriod = 10;
        BackDoor.editFeedbackSession(fs);

        submitPage.fillResponseTextBox(1, 0,"this is a response edited during grace period,but submitted after grace period");
        submitPage.clickSubmitButton();
        submitPage.verifyHtmlMainContent("/studentFeedbackQuestionSubmitPageDeadLineExceeded.html");
        
    }

    private FeedbackQuestionSubmitPage loginToStudentFeedbackQuestionSubmitPage(
            String studentName, String fsName, String questionId) {
        Url editUrl = createUrl(
                Const.ActionURIs.STUDENT_FEEDBACK_QUESTION_SUBMISSION_EDIT_PAGE)
                .withUserId(testData.students.get(studentName).googleId)
                .withCourseId(testData.feedbackSessions.get(fsName).courseId)
                .withSessionName(testData.feedbackSessions.get(fsName).feedbackSessionName)
                .withParam(Const.ParamsNames.FEEDBACK_QUESTION_ID, questionId);
        
        return loginAdminToPage(browser, editUrl,FeedbackQuestionSubmitPage.class);
    }

    private FeedbackQuestionSubmitPage goToStudentFeedbackQuestionSubmitPage(
            StudentAttributes s, String fsName, String questionId) {
        String editUrl = createUrl(
                Const.ActionURIs.STUDENT_FEEDBACK_QUESTION_SUBMISSION_EDIT_PAGE)
                .withRegistrationKey(BackDoor.getKeyForStudent(s.course, s.email))
                .withStudentEmail(s.email)
                .withCourseId(s.course)
                .withSessionName(testData.feedbackSessions.get(fsName).feedbackSessionName)
                .withParam(Const.ParamsNames.FEEDBACK_QUESTION_ID, questionId)
                .toString();
        
        browser.driver.get(editUrl);
        return AppPage.getNewPageInstance(browser, FeedbackQuestionSubmitPage.class);
    }
    
    @AfterClass
    public static void classTearDown() throws Exception {
        BrowserPool.release(browser);
    }
}
