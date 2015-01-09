package teammates.test.cases.ui.browsertests;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.common.util.ThreadHelper;
import teammates.common.util.Url;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.InstructorFeedbackEditPage;
import teammates.test.util.Priority;


@Priority(-1)
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
        ThreadHelper.waitFor(1000);
        
        feedbackEditPage.verifyHtml("/instructorFeedbackEditCopyPage.html");
        
        feedbackEditPage.clickFsCopySubmitButton();
        feedbackEditPage.verifyStatus(Const.StatusMessages.FEEDBACK_SESSION_COPY_NONESELECTED);
        
        
        ______TS("Copying fails due to fs with same name in course selected");
        feedbackEditPage.clickFsCopyButton();
        ThreadHelper.waitFor(1000);
        feedbackEditPage.fillCopyToOtherCoursesForm(feedbackSessionName);
        
        feedbackEditPage.clickFsCopySubmitButton();
        
        String error = String.format(Const.StatusMessages.FEEDBACK_SESSION_COPY_ALREADYEXISTS, feedbackSessionName, testData.courses.get("course").id);
        feedbackEditPage.verifyStatus(error);
        ThreadHelper.waitFor(1000);
        
        feedbackEditPage.verifyHtml("/instructorFeedbackEditCopyFail.html");
        
        
        ______TS("Successful case");
        feedbackEditPage.clickFsCopyButton();
        ThreadHelper.waitFor(1000);
        feedbackEditPage.fillCopyToOtherCoursesForm("New name!");
        
        feedbackEditPage.clickFsCopySubmitButton();
        
        feedbackEditPage.verifyStatus(Const.StatusMessages.FEEDBACK_SESSION_COPIED);
        ThreadHelper.waitFor(1000);
        
        feedbackEditPage.verifyHtml("/instructorFeedbackEditCopySuccess.html");
        
    }


    @AfterClass
    public static void classTearDown() throws Exception {
        BrowserPool.release(browser);
    }

    private InstructorFeedbackEditPage getFeedbackEditPage() {
        Url feedbackPageLink = createUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE).
                withUserId(instructorId).withCourseId(courseId).withSessionName(feedbackSessionName);
        return loginAdminToPage(browser, feedbackPageLink, InstructorFeedbackEditPage.class);
    }


}