package teammates.e2e.pageobjects;

import org.openqa.selenium.By;

/**
 * Page Object Model for the official IANA timezone database version page.
 */
public class IanaTimezonePage extends AppPage {

    public IanaTimezonePage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getVersion().matches("\\d{4}[a-z]");
    }

    public String getVersion() {
        return browser.driver.findElement(By.tagName("body")).getText().trim();
    }

}
