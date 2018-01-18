package teammates.test.pageobjects;

import static org.testng.AssertJUnit.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class AdminAccountManagementPage extends AppPage {

    public AdminAccountManagementPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource()
                .contains(
                        "<h1>Instructor Account Management</h1>");
    }

    public AdminAccountManagementPage clickDeleteInstructorStatus(String instructorId) {
        WebElement button = browser.driver.findElement(By.id(instructorId + "_delete"));
        click(button);
        waitForPageToLoad();
        return this;
    }

    public AdminAccountDetailsPage clickViewInstructorDetails(String instructorId) {
        WebElement button = browser.driver.findElement(By.id(instructorId + "_details"));
        click(button);
        waitForPageToLoad();
        switchToNewWindow();
        return changePageType(AdminAccountDetailsPage.class);
    }

    public AdminActivityLogPage clickViewRecentActions(String instructorId) {
        WebElement button = browser.driver.findElement(By.id(instructorId + "_recentActions"));
        click(button);
        waitForPageToLoad();
        switchToNewWindow();
        return changePageType(AdminActivityLogPage.class);
    }

    public AdminAccountManagementPage clickAndCancelDeleteAccountLink(String googleId) {
        WebElement deleteAccountLink = getDeleteAccountLink(googleId);
        clickAndCancel(deleteAccountLink);
        return this;
    }

    public AdminAccountManagementPage clickAndConfirmDeleteAccountLink(String googleId) {
        WebElement deleteAccountLink = getDeleteAccountLink(googleId);
        clickAndConfirm(deleteAccountLink);
        return this;
    }

    public void verifyIsCorrectPage() {
        assertTrue(containsExpectedPageContents());
    }

    private WebElement getDeleteAccountLink(String googleId) {
        return browser.driver.findElement(By.id(googleId + "_deleteAccount"));
    }

    private WebElement getAccountTable() {
        List<WebElement> tables = browser.driver.findElements(By.cssSelector("table"));
        if (!tables.isEmpty()) {
            return tables.get(0); // only get the first table
        }
        return null;
    }

    public boolean isTableVisible() {
        WebElement accountTable = getAccountTable();
        if (accountTable != null) {
            return accountTable.isDisplayed();
        }
        return false;
    }

    public List<String> getTableHeaders() {
        List<String> result = new ArrayList<>();
        List<WebElement> tableHeaders = browser.driver.findElements(By.cssSelector("table > thead > tr > th"));
        for (int i = 0; i < tableHeaders.size(); i++) {
            WebElement header = tableHeaders.get(i);
            result.add(header.getText());
        }
        return result;
    }

    public void waitForAdminAccountsManagementPageToFinishLoading() {
        By currentPageEntryCountSpan = By.id("currentPageEntryCount");
        waitForElementPresence(currentPageEntryCountSpan);
    }
}
