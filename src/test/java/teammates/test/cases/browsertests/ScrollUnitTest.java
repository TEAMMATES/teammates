package teammates.test.cases.browsertests;

import org.openqa.selenium.JavascriptExecutor;
import org.testng.annotations.Test;
import teammates.common.util.ThreadHelper;

public class ScrollUnitTest extends BaseUiTestCase {

    public final String SCROLL_TO_POSITION_SCRIPT = "if (arguments[1] === undefined || arguments[1] === null) {\n" +
            "        $(window).scrollTop(arguments[0]);\n" +
            "    } else {\n" +
            "        $('html, body').animate({ scrollTop: arguments[0] }, arguments[1]);\n" +
            "    }";
    @Override
    protected void prepareTestData() {

    }

    @Test
    public void scrollToPosition_studentFeedbackPage_scrolledCorrectly() {
        int scrollPos = 1500;
        String duration = "400"; // 400ms which gives enough time to scroll


        browser.driver.navigate().to("localhost:8080/about.jsp");
        JavascriptExecutor javascriptExecutor = (JavascriptExecutor) browser.driver;
        javascriptExecutor.executeScript(SCROLL_TO_POSITION_SCRIPT, scrollPos, duration);
        ThreadHelper.waitFor(400);

        assertEquals((long) scrollPos, javascriptExecutor.executeScript("return window.scrollY"));
    }
}
