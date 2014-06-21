package teammates.test.pageobjects;

import static org.testng.AssertJUnit.assertTrue;
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
    
    public void verifyIsErrorPage() {
        verifyHtmlPart(By.id("frameBodyWrapper"), "/studentProfilePictureNotFound.html");
    }

}
