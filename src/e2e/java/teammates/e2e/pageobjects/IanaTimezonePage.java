package teammates.e2e.pageobjects;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Page Object Model for the official IANA page for timezone database.
 */
public class IanaTimezonePage extends AppPage {

    public static final String IANA_TIMEZONE_DATABASE_URL = "https://www.iana.org/time-zones";

    @FindBy(id = "version")
    private WebElement timezoneVersion;

    @FindBy(id = "date")
    private WebElement timezoneReleaseDate;

    public IanaTimezonePage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return true;
    }

    public String getVersion() {
        return timezoneVersion.getText();
    }

    public String getReleaseDate() {
        return timezoneReleaseDate.getText();
    }

}
