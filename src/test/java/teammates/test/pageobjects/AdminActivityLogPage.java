package teammates.test.pageobjects;

import static org.testng.AssertJUnit.assertTrue;

import java.time.Instant;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import teammates.common.util.Const;
import teammates.common.util.TimeHelper;

public class AdminActivityLogPage extends AppPage {

    public AdminActivityLogPage(Browser browser) {
        super(browser);

    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("<h1>Admin Activity Log</h1>");
    }

    public void verifyIsCorrectPage() {
        assertTrue(containsExpectedPageContents());
    }

    public String getPersonInfoOfFirstEntry() {

        WebElement table = browser.driver.findElement(By.id("activity-logs-table"));
        WebElement tableRow = table.findElements(By.tagName("tr")).get(1);
        WebElement element = tableRow.findElement(By.tagName("small"));
        WebElement hiddenInput = element.findElement(By.name("filterQuery"));
        return hiddenInput.getAttribute("value");
    }

    public void clickViewActionsButtonOfFirstEntry() {

        WebElement table = browser.driver.findElement(By.id("activity-logs-table"));
        WebElement tableRow = table.findElements(By.tagName("tr")).get(1);
        WebElement element = tableRow.findElement(By.tagName("button"));
        click(element);
    }

    public String getFilterBoxString() {

        WebElement element = browser.driver.findElement(By.id("filterQuery"));
        return element.getAttribute("value");
    }

    public void fillQueryBoxWithText(String query) {

        WebElement element = browser.driver.findElement(By.id("filterQuery"));
        fillTextBox(element, query);
    }

    public void clickSearchSubmitButton() {

        WebElement button = browser.driver.findElement(By.name("search_submit"));
        click(button);
    }

    public boolean isLogsTableVisible() {
        WebElement table = getLogsTable();
        if (table == null) {
            return false;
        }
        return table.isDisplayed();
    }

    public WebElement getLogsTable() {
        List<WebElement> list = browser.driver.findElements(By.id("activity-logs-table"));
        if (list.isEmpty()) {
            return null;
        }

        return browser.driver.findElement(By.id("activity-logs-table"));
    }

    public int getNumberOfTableHeaders() {
        if (isLogsTableVisible()) {
            List<WebElement> headerList = browser.driver
                    .findElements(By.cssSelector("#activity-logs-table > thead > tr > th"));
            return headerList.size();
        }
        return 0;
    }

    public String getQueryMessage() {

        WebElement alert = browser.driver.findElement(By.id("queryMessage"));
        return alert.getText();
    }

    public void clickReferenceButton() {
        WebElement button = browser.driver.findElement(By.id("referenceText"));
        click(button);
    }

    public void clickUserTimezoneAtFirstRow() {
        WebElement button = browser.driver
                .findElement(By.cssSelector("#activity-logs-table td > a"));
        click(button);
    }

    public boolean isFilterReferenceVisible() {
        WebElement reference = browser.driver.findElement(By.id("filterReference"));
        return reference.isDisplayed();
    }

    public By getFirstActivityLogRow() {
        return By.id("#activity-logs-table tr");
    }

    public boolean isUserTimezoneAtFirstRowClicked() {
        List<WebElement> elements = browser.driver
                .findElements(By.cssSelector("#activity-logs-table td > .localTime > mark"));
        return !elements.isEmpty();
    }

    public Instant getDateOfEarliestLog() {
        String dateTimeString = getLogsTable().findElement(By.cssSelector("tr:last-child > td > a")).getText();

        return TimeHelper.parseLocalDateTime(dateTimeString, "dd/MM/yyyy HH:mm:ss.SSS")
                .atZone(Const.SystemParams.ADMIN_TIME_ZONE).toInstant();

    }
}
