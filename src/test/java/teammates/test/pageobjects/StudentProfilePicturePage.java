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
        return true;
    }
    
    public void verifyHasPicture() {
        assertEquals(Sanitizer.sanitizeForHtml(browser.driver.findElement(By.tagName("img")).getAttribute("src")),
                Sanitizer.sanitizeForHtml(browser.driver.getCurrentUrl()));
    }
    
    public void verifyIsErrorPage(String expectedFilename) {
        if(TestProperties.inst().isDevServer()) {
            verifyHtmlPart(By.id("frameBodyWrapper"), expectedFilename);
        } else {
            assertEquals("", browser.driver.findElement(By.tagName("body")).getText());
        }
    }

    public void verifyIsUnauthorisedErrorPage(String expectedFilename) {
        verifyHtmlPart(By.id("frameBodyWrapper"), expectedFilename);
    }

    public void verifyIsEntityNotFoundErrorPage(String expectedFilename) {
        verifyHtmlPart(By.id("frameBodyWrapper"), expectedFilename);
        
    }

}
