package teammates.e2e.pageobjects;

import static org.junit.Assert.assertEquals;

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

    private List<WebElement> getOngoingSessionsRows() {
        return ongoingSessionsTable.findElement(By.tagName("tbody")).findElements(By.tagName("tr"));
    }

    public void verifySessionRows(String[][] sessionsCells, boolean[] expectedSessionShownStatus) {
        assertEquals(sessionsCells.length, expectedSessionShownStatus.length);

        boolean[] actualSessionShownStatus = new boolean[expectedSessionShownStatus.length];

        List<WebElement> ongoingSessionRows = getOngoingSessionsRows();
        for (WebElement sessionRow : ongoingSessionRows) {
            List<WebElement> cells = sessionRow.findElements(By.tagName("td"));

            // Only validate for the preset ongoing sessions
            // This is because the page will display all ongoing sessions in the database, which is not predictable

            for (int i = 0; i < sessionsCells.length; i++) {
                String[] sessionCells = sessionsCells[i];
                if (sessionCells[1].equals(cells.get(1).getText())) {
                    verifyTableRowValues(sessionRow, sessionCells);
                    actualSessionShownStatus[i] = true;
                }
            }
        }

        for (int i = 0; i < expectedSessionShownStatus.length; i++) {
            assertEquals(expectedSessionShownStatus[i], actualSessionShownStatus[i]);
        }
    }

    public void toggleSessionFilter() {
        click(toggleFilterButton);
    }

    public void waitForSessionFilterVisibility() {
        By by = By.id("filter-section");
        waitForElementVisibility(by);
    }

    public void setFilterStartDate(Instant instant) {
        WebElement timezoneElement = browser.driver.findElement(By.id("filter-timezone"));
        String timezone = getSelectedDropdownOptionText(timezoneElement);

        WebElement startDate = browser.driver.findElement(By.id("start-date"));
        fillTextBox(startDate, formatDateTimeForFilter(instant, ZoneId.of(timezone)));
    }

    public void setFilterEndDate(Instant instant) {
        WebElement timezoneElement = browser.driver.findElement(By.id("filter-timezone"));
        String timezone = getSelectedDropdownOptionText(timezoneElement);

        WebElement endDate = browser.driver.findElement(By.id("end-date"));
        fillTextBox(endDate, formatDateTimeForFilter(instant, ZoneId.of(timezone)));
    }

    public void filterSessions() {
        By by = By.id("btn-get-sessions");
        waitForElementPresence(by);
        click(by);
        waitForPageToLoad();
        waitUntilAnimationFinish();
    }

    private String formatDateTimeForFilter(Instant instant, ZoneId timeZone) {
        return DateTimeFormatter
                .ofPattern("yyyy-MM-dd")
                .format(instant.atZone(timeZone));
    }

    public String getSessionsTableTimezone() {
        WebElement timezoneElement = browser.driver.findElement(By.id("table-timezone"));
        return getSelectedDropdownOptionText(timezoneElement);
    }

}
