package teammates.test.pageobjects;

import static org.testng.AssertJUnit.assertEquals;

import org.openqa.selenium.By;

import teammates.common.util.Sanitizer;

public class StudentProfilePicturePage extends AppPage {

    public StudentProfilePicturePage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return true;
    }
    
    public void verifyHasPicture() {
        assertEquals(Sanitizer.sanitizeForHtml(browser.driver.findElement(By.tagName("img")).getAttribute("src")),
                Sanitizer.sanitizeForHtml(browser.driver.getCurrentUrl()));
    }
    
    public void verifyIsErrorPage(String expectedFilename) {
        try {
            verifyHtmlPart(By.id("frameBodyWrapper"), expectedFilename);
        } catch (AssertionError ae) {
            if (! browser.driver.getCurrentUrl().contains("localhost")) {
                assertEquals("", browser.driver.findElement(By.tagName("body")).getText());
            } else {
                throw ae;
            }
        }
    }

}
