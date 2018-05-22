package teammates.test.cases.browsertests;

import org.openqa.selenium.JavascriptExecutor;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import teammates.common.util.ThreadHelper;
import teammates.test.driver.TestProperties;

public class ScrollUnitTest extends BaseUiTestCase {

    public final String SCROLL_TO_POSITION_SCRIPT = "if (arguments[1] === undefined || arguments[1] === null) {\n" +
            "        $(window).scrollTop(arguments[0]);\n" +
            "    } else {\n" +
            "        $('html, body').animate({ scrollTop: arguments[0] }, arguments[1]);\n" +
            "    }";

    public final String VERTICAL_SCROLL_VALUE = "return window.scrollY";

    @Override
    protected void prepareTestData() {
        // No data needed
    }

    @BeforeClass
    public void classSetup() {
        browser.driver.navigate().to(TestProperties.TEAMMATES_URL + "/about.jsp");
    }

    @Test
    public void allTests() {
        scrollToPosition_aboutUsPage_scrolledCorrectly();
        scrollToTop_aboutUsPage_scrolledCorrectly();
    }

    public void scrollToPosition_aboutUsPage_scrolledCorrectly() {
        int scrollPos = 1500;
        String duration = "400"; // 400ms which gives enough time to scroll

        JavascriptExecutor javascriptExecutor = (JavascriptExecutor) browser.driver;
        javascriptExecutor.executeScript(SCROLL_TO_POSITION_SCRIPT, scrollPos, duration);
        ThreadHelper.waitFor(400);

        assertEquals((long) scrollPos, javascriptExecutor.executeScript(VERTICAL_SCROLL_VALUE));
    }

    public void scrollToTop_aboutUsPage_scrolledCorrectly() {
        String duration = "400"; // 400ms which gives enough time to scroll

        JavascriptExecutor javascriptExecutor = (JavascriptExecutor) browser.driver;
        javascriptExecutor.executeScript(SCROLL_TO_POSITION_SCRIPT, 0, duration);
        ThreadHelper.waitFor(400);

        assertEquals((long) 0, javascriptExecutor.executeScript(VERTICAL_SCROLL_VALUE));
    }
}
