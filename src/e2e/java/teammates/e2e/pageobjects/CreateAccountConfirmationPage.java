package teammates.e2e.pageobjects;

import static org.junit.Assert.assertEquals;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Page Object Model for the create account confirmation page.
 */
public class CreateAccountConfirmationPage extends AppPage {
    @FindBy(id = "btn-confirm")
    private WebElement confirmButton;

    public CreateAccountConfirmationPage(Browser browser) {
        super(browser);
    }

    @Override
    public boolean containsExpectedPageContents() {
        String text = waitForElementPresence(By.tagName("h3")).getText();
        return text.contains("Confirm your Google account") || text.contains("Invalid account creation link");
    }

    public void verifyJoiningUser(String googleId) {
        assertEquals(browser.driver.findElement(By.id("user-id")).getText(), googleId);
    }

    public <T extends AppPage> T confirmJoinCourse(Class<T> typeOfPage) {
        click(confirmButton);
        waitForPageToLoad();
        return changePageType(typeOfPage);
    }

    public boolean isInvalidLinkMessageShowing() {
        return waitForElementPresence(By.tagName("h3")).getText().contains("Invalid account creation link");
    }

}
