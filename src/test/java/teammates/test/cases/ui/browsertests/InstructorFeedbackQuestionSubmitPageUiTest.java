package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNull;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.util.Const;
import teammates.common.util.Url;
import teammates.test.driver.BackDoor;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.FeedbackQuestionSubmitPage;

public class InstructorFeedbackQuestionSubmitPageUiTest extends BaseUiTestCase {
    private static DataBundle testData;
    private static Browser browser;
    private FeedbackQuestionSubmitPage submitPage;
    private FeedbackQuestionAttributes fq;
    
    @BeforeClass
    public static void classSetup() throws Exception {
        printTestClassHeader();
        testData = loadDataBundle("/InstructorFeedbackQuestionSubmitPageUiTest.json");
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
        ______TS("Awaiting session");
        
        fq = BackDoor.getFeedbackQuestion("IFQSubmitUiT.CS2104", "Awaiting Session", 1);
        submitPage = loginToInstructorFeedbackQuestionSubmitPage("IFQSubmitUiT.instr", "Awaiting Session", fq.getId());

        // This is the full HTML verification for Instructor Feedback Question Submit Page, the rest can all be verifyMainHtml
        submitPage.verifyHtml("/instructorFeedbackQuestionSubmitPageAwaiting.html");
        
        ______TS("Open session");
        
        fq = BackDoor.getFeedbackQuestion("IFQSubmitUiT.CS2104", "Open Session", 1);
        submitPage = loginToInstructorFeedbackQuestionSubmitPage("IFQSubmitUiT.instr", "Open Session", fq.getId());
        submitPage.verifyHtmlMainContent("/instructorFeedbackQuestionSubmitPageOpen.html");
        
        ______TS("Grace period session");
        
        fq = BackDoor.getFeedbackQuestion("IFQSubmitUiT.CS2104", "Grace Period Session", 1);
        submitPage = loginToInstructorFeedbackQuestionSubmitPage("IFQSubmitUiT.instr", "Grace Period Session", fq.getId());
        submitPage.verifyHtmlMainContent("/instructorFeedbackQuestionSubmitPageGracePeriod.html");
        
        ______TS("Closed) session");
        
        fq = BackDoor.getFeedbackQuestion("IFQSubmitUiT.CS2104", "Closed Session", 1);
        submitPage = loginToInstructorFeedbackQuestionSubmitPage("IFQSubmitUiT.instr", "Closed Session", fq.getId());
        submitPage.verifyHtmlMainContent("/instructorFeedbackQuestionSubmitPageClosed.html");
        
        ______TS("Private session");
        
        fq = BackDoor.getFeedbackQuestion("IFQSubmitUiT.CS2104", "Private Session", 1);
        submitPage = loginToInstructorFeedbackQuestionSubmitPage("IFQSubmitUiT.instr", "Private Session", fq.getId());
        submitPage.verifyHtmlMainContent("/instructorFeedbackQuestionSubmitPagePrivate.html");
    }
    
    private void testSubmitAction() {
        ______TS("create new responses");

        fq = BackDoor.getFeedbackQuestion("IFQSubmitUiT.CS2104", "Open Session", 1);
        submitPage = loginToInstructorFeedbackQuestionSubmitPage("IFQSubmitUiT.instr", "Open Session", fq.getId());
        
        submitPage.fillResponseTextBox(1, 0, "Test Self Feedback");
        
        assertNull(BackDoor.getFeedbackResponse(fq.getId(),
                "IFQSubmitUiT.instr@gmail.tmt",
                "IFQSubmitUiT.instr@gmail.tmt"));
        
        submitPage.clickSubmitButton();

        assertEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED,
                submitPage.getStatus());
        
        assertNotNull(BackDoor.getFeedbackResponse(fq.getId(),
                "IFQSubmitUiT.instr@gmail.tmt",
                "IFQSubmitUiT.instr@gmail.tmt"));
        assertEquals("Test Self Feedback",
                BackDoor.getFeedbackResponse(fq.getId(),
                    "IFQSubmitUiT.instr@gmail.tmt",
                    "IFQSubmitUiT.instr@gmail.tmt").getResponseDetails().getAnswerString());
        
        ______TS("edit existing response");        
        
        String editedResponse = "Edited self feedback.";
        submitPage.fillResponseTextBox(1, 0, editedResponse);
        
        submitPage.clickSubmitButton();

        assertEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED,
                submitPage.getStatus());
        assertNotNull(BackDoor.getFeedbackResponse(fq.getId(),
                "IFQSubmitUiT.instr@gmail.tmt",
                "IFQSubmitUiT.instr@gmail.tmt"));
        assertEquals(editedResponse,
                BackDoor.getFeedbackResponse(fq.getId(),
                    "IFQSubmitUiT.instr@gmail.tmt",
                    "IFQSubmitUiT.instr@gmail.tmt").getResponseDetails().getAnswerString());
    }
    
    private FeedbackQuestionSubmitPage loginToInstructorFeedbackQuestionSubmitPage(
            String instructorName, String fsName, String questionId) {
        Url editUrl = createUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_QUESTION_SUBMISSION_EDIT_PAGE).
                withUserId(testData.instructors.get(instructorName).googleId).
                withCourseId(testData.feedbackSessions.get(fsName).courseId).
                withSessionName(testData.feedbackSessions.get(fsName).feedbackSessionName).
                withParam(Const.ParamsNames.FEEDBACK_QUESTION_ID, questionId);
        return loginAdminToPage(browser, editUrl, FeedbackQuestionSubmitPage.class);
    }
    
    @AfterClass
    public static void classTearDown() throws Exception {
        BrowserPool.release(browser);
    }
}
