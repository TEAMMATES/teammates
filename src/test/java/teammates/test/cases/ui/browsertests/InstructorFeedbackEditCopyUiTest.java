package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;
import org.openqa.selenium.By;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.common.util.Url;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.InstructorFeedbackEditPage;


public class InstructorFeedbackEditCopyUiTest extends BaseUiTestCase {
    private static Browser browser;
    private static InstructorFeedbackEditPage feedbackEditPage;
    private static DataBundle testData;
    private static String instructorId;
    private static String courseId;
    private static String feedbackSessionName;

    @BeforeClass
    public static void classSetup() throws Exception {
        printTestClassHeader();
        testData = loadDataBundle("/InstructorFeedbackEditCopyTest.json");
        removeAndRestoreTestDataOnServer(testData);
        instructorId = testData.accounts.get("instructorWithSessions").googleId;
        courseId = testData.courses.get("course").id;
        feedbackSessionName = testData.feedbackSessions.get("openSession").feedbackSessionName;

        browser = BrowserPool.getBrowser();
    }

    @Test
    public void allTests() throws Exception{
        feedbackEditPage = getFeedbackEditPage();
        
        ______TS("Submit empty course list");
        feedbackEditPage.clickFsCopyButton();
        feedbackEditPage.fsCopyToModal.waitForModalToLoad();

        // Full HTML verification already done in InstructorFeedbackEditPageUiTest
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackEditCopyPage.html");
        
        feedbackEditPage.clickFsCopySubmitButton();
        feedbackEditPage.verifyStatus(Const.StatusMessages.FEEDBACK_SESSION_COPY_NONESELECTED);
        
        
        ______TS("Copying fails due to fs with same name getting copied to the original course");
        feedbackEditPage.clickFsCopyButton();
        feedbackEditPage.fsCopyToModal.waitForModalToLoad();
        feedbackEditPage.fsCopyToModal.fillFormWithAllCoursesSelected(feedbackSessionName);
        
        feedbackEditPage.clickFsCopySubmitButton();
        
        assertTrue(feedbackEditPage.fsCopyToModal.isErrorMessageVisible());
        feedbackEditPage.fsCopyToModal.verifyErrorMessage();
        
        
        feedbackEditPage.fsCopyToModal.resetCoursesCheckbox();
        feedbackEditPage.fsCopyToModal.checkCourse("FeedbackEditCopy.CS2104");
        
        feedbackEditPage.clickFsCopySubmitButton();
        assertTrue(feedbackEditPage.fsCopyToModal.isErrorMessageVisible());
        feedbackEditPage.fsCopyToModal.verifyErrorMessage();
        
        
        ______TS("Copying fails due to fs with invalid name");
        feedbackEditPage = getFeedbackEditPage();
        
        feedbackEditPage.clickFsCopyButton();
        feedbackEditPage.fsCopyToModal.waitForModalToLoad();
        feedbackEditPage.fsCopyToModal.fillFormWithAllCoursesSelected("Invalid name | for feedback session");
        
        feedbackEditPage.clickFsCopySubmitButton();
        
        feedbackEditPage.verifyStatus(
                "\"Invalid name | for feedback session\" is not acceptable to TEAMMATES as "
                + "feedback session name because it contains invalid characters. "
                + "All feedback session name must start with an alphanumeric character, "
                + "and cannot contain any vertical bar (|) or percent sign (%).");
        
        
        ______TS("Successful case");
        feedbackEditPage.clickFsCopyButton();
        feedbackEditPage.fsCopyToModal.waitForModalToLoad();
        feedbackEditPage.fsCopyToModal.fillFormWithAllCoursesSelected("New name!");
        
        feedbackEditPage.clickFsCopySubmitButton();
        
        feedbackEditPage.verifyStatus(Const.StatusMessages.FEEDBACK_SESSION_COPIED);
        feedbackEditPage.waitForElementPresence(By.id("table-sessions"));

        // Full HTML verification already done in InstructorFeedbackEditPageUiTest
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackEditCopySuccess.html");
        
        
        ______TS("Copying fails due to fs with same name in course selected");
        feedbackEditPage = getFeedbackEditPage();
        feedbackEditPage.clickFsCopyButton();
        feedbackEditPage.fsCopyToModal.waitForModalToLoad();
        
        feedbackEditPage.fsCopyToModal.fillFsName("New name!");
        feedbackEditPage.fsCopyToModal.checkCourse("FeedbackEditCopy.CS2105");
        
        feedbackEditPage.clickFsCopySubmitButton();

        // Full HTML verification already done in InstructorFeedbackEditPageUiTest
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackEditCopyFail.html");
    }


    @AfterClass
    public static void classTearDown() throws Exception {
        BrowserPool.release(browser);
    }

    private InstructorFeedbackEditPage getFeedbackEditPage() {
        Url feedbackPageLink = createUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE)
                                             .withUserId(instructorId)
                                             .withCourseId(courseId)
                                             .withSessionName(feedbackSessionName);
        return loginAdminToPage(browser, feedbackPageLink, InstructorFeedbackEditPage.class);
    }


}