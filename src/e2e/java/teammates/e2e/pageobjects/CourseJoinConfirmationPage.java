package teammates.e2e.pageobjects;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Page Object Model for the course join confirmation page.
 */
public class CourseJoinConfirmationPage extends AppPage {
    @FindBy(id = "btn-confirm")
    private WebElement confirmButton;

    public CourseJoinConfirmationPage(Browser browser) {
        super(browser);
    }

    @Override
    public boolean containsExpectedPageContents() {
        // This page has no unique indicator as the content depends on whether it follows the happy path or not
        return true;
    }

    public void verifyJoiningUser(String googleId) {
        assertEquals(browser.driver.findElement(By.id("user-id")).getText(), googleId);
    }

    public <T extends AppPage> T confirmJoinCourse(Class<T> typeOfPage) {
        click(confirmButton);
        waitForPageToLoad();
        return changePageType(typeOfPage);
    }

    public void verifyDisplayedMessage(String message) {
        assertEquals(browser.driver.findElement(By.className("card-body")).getText(), message);
    }
}
