package teammates.e2e.pageobjects;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Page Object Model for the admin timezone page.
 */
public class AdminTimezonePage extends AppPage {

    @FindBy(id = "tz-java")
    private WebElement javaTimezoneOffsets;

    @FindBy(id = "tz-moment")
    private WebElement momentTimezoneOffsets;

    @FindBy(id = "tzversion-java")
    private WebElement javaTimezoneVersion;

    @FindBy(id = "tzversion-moment")
    private WebElement momentTimezoneVersion;

    public AdminTimezonePage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("Java timezones") && getPageSource().contains("Moment timezones");
    }

    public String getJavaTimezoneOffsets() {
        return javaTimezoneOffsets.getText();
    }

    public String getMomentTimezoneOffsets() {
        return momentTimezoneOffsets.getText();
    }

    public String getJavaTimezoneVersion() {
        return javaTimezoneVersion.getText();
    }

    public String getMomentTimezoneVersion() {
        return momentTimezoneVersion.getText();
    }

}
