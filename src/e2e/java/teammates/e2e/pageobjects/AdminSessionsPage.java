package teammates.e2e.pageobjects;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import teammates.common.util.TimeHelper;

/**
 * Page Object Model for the admin sessions page.
 */
public class AdminSessionsPage extends AppPage {

    @FindBy(id = "btn-toggle-filter")
    private WebElement toggleFilterButton;

    public AdminSessionsPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("Ongoing Sessions");
    }

    private List<WebElement> getOngoingSessionNames() {
        return browser.driver.findElements(By.cssSelector("[data-testid='session-name']"));
    }

    public void verifySessionRows(String[][] sessionsCells, boolean[] expectedSessionShownStatus) {
        assertEquals(sessionsCells.length, expectedSessionShownStatus.length);

        boolean[] actualSessionShownStatus = new boolean[expectedSessionShownStatus.length];

        List<String> ongoingSessionNames = getOngoingSessionNames()
                .stream()
                .map(WebElement::getText)
                .map(String::trim)
                .toList();

        for (int i = 0; i < sessionsCells.length; i++) {
            String sessionName = sessionsCells[i][1].trim();
            if (ongoingSessionNames.contains(sessionName)) {
                actualSessionShownStatus[i] = true;
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
        fillTextBox(startDate, formatDateTimeForFilter(instant, timezone));
    }

    public void setFilterEndDate(Instant instant) {
        WebElement timezoneElement = browser.driver.findElement(By.id("filter-timezone"));
        String timezone = getSelectedDropdownOptionText(timezoneElement);

        WebElement endDate = browser.driver.findElement(By.id("end-date"));
        fillTextBox(endDate, formatDateTimeForFilter(instant, timezone));
    }

    public void filterSessions() {
        By by = By.id("btn-get-sessions");
        waitForElementPresence(by);
        click(by);
        waitForPageToLoad();
    }

    private String formatDateTimeForFilter(Instant instant, String timeZone) {
        return TimeHelper.formatInstant(instant, timeZone, "yyyy-MM-dd");
    }

    public String getSessionsTableTimezone() {
        WebElement timezoneElement = browser.driver.findElement(By.id("table-timezone"));
        return getSelectedDropdownOptionText(timezoneElement);
    }
}
