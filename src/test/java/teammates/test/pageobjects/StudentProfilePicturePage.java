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
        // Intentional check for opening title and not closing title because the following content is not static
        return getPageSource().contains("<title>studentProfilePic");
    }

    public void verifyHasPicture() {
        assertEquals(Sanitizer.sanitizeForHtml(browser.driver.findElement(By.tagName("img")).getAttribute("src")),
                     Sanitizer.sanitizeForHtml(browser.driver.getCurrentUrl()));
    }

}
