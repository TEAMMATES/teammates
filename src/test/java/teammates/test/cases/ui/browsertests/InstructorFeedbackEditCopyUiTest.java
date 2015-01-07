package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertTrue;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.util.Const;
import teammates.common.util.ThreadHelper;
import teammates.common.util.TimeHelper;
import teammates.common.util.Url;
import teammates.test.driver.AssertHelper;
import teammates.test.driver.BackDoor;
import teammates.test.driver.TestProperties;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.FeedbackQuestionSubmitPage;
import teammates.test.pageobjects.FeedbackSubmitPage;
import teammates.test.pageobjects.InstructorFeedbackEditPage;
import teammates.test.pageobjects.InstructorFeedbacksPage;
import teammates.test.util.Priority;

import com.google.appengine.api.datastore.Text;

/**
 * Covers the 'Edit Feedback Session' page for instructors.
 * SUT is {@link InstructorFeedbackEditPage}.
 */
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
        testData = loadDataBundle("/InstructorFeedbackEditCopyUiTest.json");
        removeAndRestoreTestDataOnServer(testData);

        instructorId = testData.accounts.get("instructorWithSessions").googleId;
        courseId = testData.courses.get("course").id;
        feedbackSessionName = testData.feedbackSessions.get("openSession").feedbackSessionName;

        browser = BrowserPool.getBrowser();
    }

    @Test
    public void allTests() throws Exception{
        feedbackEditPage = getFeedbackEditPage();
        
        ______TS("submit empty course list");
        feedbackEditPage.clickFsCopyButton();
        ThreadHelper.waitFor(1000);
        
        feedbackEditPage.verifyHtml("/instructorFeedbackEditCopyPage.html");
        
        feedbackEditPage.clickFsCopySubmitButton();
        feedbackEditPage.verifyStatus(Const.StatusMessages.FEEDBACK_SESSION_COPY_NONESELECTED);
        
        
        ______TS("copy to all");
        feedbackEditPage.clickFsCopyButton();
        ThreadHelper.waitFor(1000);
        feedbackEditPage.fillCopyToOtherCoursesForm("New name!");
        
        feedbackEditPage.clickFsCopySubmitButton();
        
        feedbackEditPage.verifyStatus(Const.StatusMessages.FEEDBACK_SESSION_COPIED);
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