package teammates.e2e.pageobjects;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.openqa.selenium.By;

/**
 * Page Object Model for the error reporting modal.
 */
public class ErrorReportingModal extends AppPage {

    public ErrorReportingModal(Browser browser) {
        super(browser);
    }

    @Override
    public boolean containsExpectedPageContents() {
        return waitForElementPresence(By.tagName("h2")).getText().contains("Uh oh! Something went wrong.");
    }

    public void verifyErrorMessage(String message) {
        assertEquals(browser.driver.findElement(By.id("error-message")).getText(),
                "The server returns the following error message: \"" + message + "\".");
    }
}
