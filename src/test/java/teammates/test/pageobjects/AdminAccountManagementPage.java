package teammates.test.pageobjects;

import static org.testng.AssertJUnit.assertTrue;
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

    public AdminAccountManagementPage clickDeleteInstructorStatus(
            String instructorId) {
        browser.driver.findElement(By.id(instructorId + "_delete")).click();
        waitForPageToLoad();
        return this;
    }

    public AdminAccountDetailsPage clickViewInstructorDetails(
            String instructorId) {
        browser.driver.findElement(By.id(instructorId + "_details")).click();
        waitForPageToLoad();
        switchToNewWindow();
        return changePageType(AdminAccountDetailsPage.class);
    }
    
    public AdminActivityLogPage clickViewRecentActions(
            String instructorId) {
        browser.driver.findElement(By.id(instructorId + "_recentActions")).click();
        waitForPageToLoad();
        switchToNewWindow();
        return changePageType(AdminActivityLogPage.class);
    }

    public AdminAccountManagementPage clickAndCancelDeleteAccountLink(
            String googleId) {
        WebElement deleteAccountLink = getDeleteAccountLink(googleId);
        clickAndCancel(deleteAccountLink);
        return this;
    }

    public AdminAccountManagementPage clickAndConfirmDeleteAccountLink(
            String googleId) {
        WebElement deleteAccountLink = getDeleteAccountLink(googleId);
        clickAndConfirm(deleteAccountLink);
        waitForPageToLoad();
        return this;
    }

    public void verifyIsCorrectPage() {
        assertTrue(containsExpectedPageContents());
    }

    private WebElement getDeleteAccountLink(String googleId) {
        return browser.driver.findElement(By.id(googleId + "_deleteAccount"));
    }
}
