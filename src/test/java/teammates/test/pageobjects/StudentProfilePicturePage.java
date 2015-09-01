package teammates.test.pageobjects;

import static org.testng.AssertJUnit.assertEquals;

import org.openqa.selenium.By;

import teammates.common.util.Sanitizer;
import teammates.test.driver.TestProperties;

public class StudentProfilePicturePage extends AppPage {

    public StudentProfilePicturePage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        String pageSource = getPageSource();

        // First test is for the actual pages, the tests for after the first || is meant for tests that
        // results in error pages or entity not found pages, note that some of them do not have tags,
        // have no closing tags and those are intentional
        return pageSource.contains("<title>studentProfilePic")
               || pageSource.contains("<body></body>")
               || pageSource.contains("The page you are looking for is not there.")
               || pageSource.contains("You are not authorized to view this page.")
               || pageSource.contains("TEAMMATES could not locate what you were trying to access.");
    }

    public void verifyHasPicture() {
        assertEquals(Sanitizer.sanitizeForHtml(browser.driver.findElement(By.tagName("img")).getAttribute("src")),
                     Sanitizer.sanitizeForHtml(browser.driver.getCurrentUrl()));
    }

    public void verifyIsErrorPage(String expectedFilename) {
        if (TestProperties.inst().isDevServer()) {
            verifyHtmlPart(By.id("mainContent"), expectedFilename);
        } else {
            assertEquals("", browser.driver.findElement(By.tagName("body")).getText());
        }
    }

    public void verifyIsUnauthorisedErrorPage(String expectedFilename) {
        verifyHtmlPart(By.id("mainContent"), expectedFilename);
    }

    public void verifyIsEntityNotFoundErrorPage(String expectedFilename) {
        verifyHtmlPart(By.id("mainContent"), expectedFilename);
    }

}
