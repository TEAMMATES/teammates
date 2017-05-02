package teammates.test.pageobjects;

import static org.testng.AssertJUnit.assertTrue;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class AdminEmailLogPage extends AppPage {

    public AdminEmailLogPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("<h1>Admin Email Log</h1>");
    }

    public void verifyIsCorrectPage() {
        assertTrue(containsExpectedPageContents());
    }

    public void clickReferenceButton() {
        WebElement button = browser.driver.findElement(By.id("referenceText"));
        click(button);
    }

    public boolean isFilterReferenceVisible() {
        WebElement reference = browser.driver.findElement(By.id("filterReference"));
        return reference.isDisplayed();
    }
}
