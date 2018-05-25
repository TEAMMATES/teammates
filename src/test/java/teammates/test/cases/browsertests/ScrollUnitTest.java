package teammates.test.cases.browsertests;

import org.openqa.selenium.JavascriptExecutor;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.common.util.ThreadHelper;
import teammates.test.pageobjects.InstructorFeedbackEditPage;

/**
 * Unit Test for functions in scrollTo.js.
 * All tests are done on InstructorFeedbackEditPage page to get direct access to JS functions
 */
public class ScrollUnitTest extends BaseUiTestCase {

    private static final String SCROLL_TO_POSITION_SCRIPT = "if (arguments[1] === undefined || arguments[1] === null) {\n"
            + "        $(window).scrollTop(arguments[0]);\n"
            + "    } else {\n"
            + "        $('html, body').animate({ scrollTop: arguments[0] }, arguments[1]);\n"
            + "    }";

    private static final String VERTICAL_SCROLL_VALUE = "return window.scrollY";

    private InstructorFeedbackEditPage feedbackEditPage;

    private String instructorId;
    private String courseId;
    private String feedbackSessionName;

    @Override
    protected void prepareTestData() {
        // No need  of data. Dummy data only for scrolling purposes
        testData = loadDataBundle("/InstructorFeedbackEditPageUiTest.json");
        removeAndRestoreDataBundle(testData);

        instructorId = "CFeedbackEditUiT.instructor";
        courseId = "CFeedbackEditUiT.CS2104";
        feedbackSessionName = "First Session";
    }

    @BeforeClass
    public void classSetup() {
        feedbackEditPage = getFeedbackEditPage();
    }

    @Test
    public void allTests() {
        scrollToPosition_feedbackEditPage_scrolledCorrectly();
        scrollToTop_feedbackEditPage_scrolledCorrectly();
        scrollToElement_feedbackEditPage_scrolledCorrectly();
    }

    private void scrollToPosition_feedbackEditPage_scrolledCorrectly() {
        int scrollPos = 400;
        String duration = "400"; // 400ms which gives enough time to scroll

        JavascriptExecutor javascriptExecutor = (JavascriptExecutor) browser.driver;
        javascriptExecutor.executeScript(SCROLL_TO_POSITION_SCRIPT, scrollPos, duration);
        ThreadHelper.waitFor(400);

        assertEquals((long) scrollPos, javascriptExecutor.executeScript(VERTICAL_SCROLL_VALUE));
    }

    private void scrollToTop_feedbackEditPage_scrolledCorrectly() {
        String duration = "400"; // 400ms which gives enough time to scroll

        JavascriptExecutor javascriptExecutor = (JavascriptExecutor) browser.driver;
        javascriptExecutor.executeScript(SCROLL_TO_POSITION_SCRIPT, 0, duration);
        ThreadHelper.waitFor(400);

        assertEquals((long) 0, javascriptExecutor.executeScript(VERTICAL_SCROLL_VALUE));
    }

    private void scrollToElement_feedbackEditPage_scrolledCorrectly() {
        JavascriptExecutor javascriptExecutor = (JavascriptExecutor) browser.driver;

        assertFalse(feedbackEditPage.isElementInViewport("empty_message"));

        javascriptExecutor.executeScript("scrollToElement(document.getElementById('empty_message'), {duration: 800});");
        ThreadHelper.waitFor(800);

        assertTrue(feedbackEditPage.isElementInViewport("empty_message"));
    }

    private InstructorFeedbackEditPage getFeedbackEditPage() {
        AppUrl feedbackPageLink = createUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE)
                .withUserId(instructorId)
                .withCourseId(courseId)
                .withSessionName(feedbackSessionName)
                .withEnableSessionEditDetails(true);
        return loginAdminToPage(feedbackPageLink, InstructorFeedbackEditPage.class);
    }
}
