package teammates.e2e.pageobjects;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Page Object Model for the admin sessions page.
 */
public class AdminSessionsPage extends AppPage {

    @FindBy(id = "btn-toggle-filter")
    private WebElement toggleFilterButton;

    @FindBy(id = "ongoing-sessions-table")
    private WebElement ongoingSessionsTable;

    public AdminSessionsPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("Ongoing Sessions");
    }

    public List<WebElement> getOngoingSessionsRows() {
        return ongoingSessionsTable.findElement(By.tagName("tbody")).findElements(By.tagName("tr"));
    }

    public void verifySessionRow(WebElement sessionRow, String[] expectedRowValues) {
        verifyTableRowValues(sessionRow, expectedRowValues);
    }

    public void toggleSessionFilter() {
        click(toggleFilterButton);
    }

    public void waitForSessionFilterVisibility() {
        By by = By.id("filter-section");
        waitForElementVisibility(by);
    }

    public void setFilterStartDate(Instant instant) {
        WebElement timezoneElement = browser.driver.findElement(By.id("timezone"));
        String timezone = getSelectedDropdownOptionText(timezoneElement);

        WebElement startDate = browser.driver.findElement(By.id("start-date"));
        fillTextBox(startDate, formatDateTimeForFilter(instant, ZoneId.of(timezone)));
    }

    public void setFilterEndDate(Instant instant) {
        WebElement timezoneElement = browser.driver.findElement(By.id("timezone"));
        String timezone = getSelectedDropdownOptionText(timezoneElement);

        WebElement endDate = browser.driver.findElement(By.id("end-date"));
        fillTextBox(endDate, formatDateTimeForFilter(instant, ZoneId.of(timezone)));
    }

    public void filterSessions() {
        By by = By.id("btn-get-sessions");
        waitForElementPresence(by);
        click(by);
        waitForPageToLoad();
    }

    private String formatDateTimeForFilter(Instant instant, ZoneId timeZone) {
        return DateTimeFormatter
                .ofPattern("YYYY-MM-dd")
                .format(instant.atZone(timeZone));
    }

}
