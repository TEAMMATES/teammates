package teammates.e2e.pageobjects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import teammates.common.datatransfer.attributes.AccountRequestAttributes;

/**
 * Represents the admin requests page of the website.
 */
public class AdminRequestsPage extends AppPage {

    private static final String DISPLAYED_DATE_FORMAT = "EEE, dd MMM yyyy, hh:mm a";

    @FindBy(id = "from-datepicker")
    private WebElement fromDateBox;

    @FindBy(id = "to-datepicker")
    private WebElement toDateBox;

    @FindBy(id = "show-account-requests-button")
    private WebElement showAccountRequestsButton;

    public AdminRequestsPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("Account Requests</h1>");
    }

    public void fillFromDate(Instant instant) {
        fillDatePicker(fromDateBox, instant, "UTC");
    }

    public void fillToDate(Instant instant) {
        fillDatePicker(toDateBox, instant, "UTC");
    }

    public void fillNewName(String panelIdentifier, String newName) {
        WebElement nameInputBox = browser.driver.findElement(By.id("name-input-" + panelIdentifier));
        fillTextBox(nameInputBox, newName);
    }

    public void fillNewInstitute(String panelIdentifier, String newInstitute) {
        WebElement nameInputBox = browser.driver.findElement(By.id("institute-input-" + panelIdentifier));
        fillTextBox(nameInputBox, newInstitute);
    }

    public void fillNewEmail(String panelIdentifier, String newEmail) {
        WebElement nameInputBox = browser.driver.findElement(By.id("email-input-" + panelIdentifier));
        fillTextBox(nameInputBox, newEmail);
    }

    public void clickShowAccountRequestsButton() {
        click(showAccountRequestsButton);
        waitForPageToLoad();
    }

    public void clickPanelHeader(String panelIdentifier) {
        click(By.id("panel-header-" + panelIdentifier));
        waitUntilAnimationFinish();
    }

    public void clickEditButton(String panelIdentifier) {
        WebElement editButton = getButton("edit-button", panelIdentifier);
        click(editButton);
        waitForPageToLoad();
    }

    public void clickSaveButton(String panelIdentifier) {
        WebElement saveButton = getButton("save-button", panelIdentifier);
        click(saveButton);
        waitForPageToLoad(true);
    }

    public void clickCancelButton(String panelIdentifier) {
        WebElement cancelButton = getButton("cancel-button", panelIdentifier);
        click(cancelButton);
        waitForPageToLoad(true);
    }

    public void clickApproveButton(String panelIdentifier) {
        WebElement approveButton = getButton("approve-button", panelIdentifier);
        click(approveButton);
        waitForPageToLoad(true);
    }

    public void clickRejectButton(String panelIdentifier) {
        WebElement rejectButton = getButton("reject-button", panelIdentifier);
        click(rejectButton);
        waitForPageToLoad(true);
    }

    public void clickResetButton(String panelIdentifier, boolean hasConfirmationModal) {
        WebElement resetButton = getButton("reset-button", panelIdentifier);
        if (hasConfirmationModal) {
            clickAndConfirm(resetButton);
        } else {
            click(resetButton);
            waitForPageToLoad(true);
        }
    }

    public void clickDeleteButton(String panelIdentifier) {
        WebElement deleteButton = getButton("delete-button", panelIdentifier);
        clickAndConfirm(deleteButton);
        waitForPageToLoad(true);
    }

    private WebElement getButton(String button, String panelIdentifier) {
        try {
            return browser.driver.findElement(By.cssSelector("[id*='" + button + "-" + panelIdentifier + "']"));
        } catch (NoSuchElementException e) {
            expandTab(panelIdentifier);
            return browser.driver.findElement(By.cssSelector("[id*='" + button + "-" + panelIdentifier + "']"));
        }
    }

    public Integer getAccountRequestPanelPendingProcessingIndex(AccountRequestAttributes accountRequest) {
        WebElement panel = getAccountRequestPanelPendingProcessing(accountRequest);
        String id = panel.getAttribute("id");
        return Integer.parseInt(id.substring(id.lastIndexOf('-') + 1));
    }

    public Integer getAccountRequestPanelWithinPeriodIndex(AccountRequestAttributes accountRequest) {
        WebElement panel = getAccountRequestPanelWithinPeriod(accountRequest);
        String id = panel.getAttribute("id");
        return Integer.parseInt(id.substring(id.lastIndexOf('-') + 1));
    }

    public List<WebElement> getAccountRequestPanelsPendingProcessing() {
        return browser.driver.findElements(By.cssSelector("[id^='ar-pending-processing-']"));
    }

    public List<WebElement> getAccountRequestPanelsWithinPeriod() {
        return browser.driver.findElements(By.cssSelector("[id^='ar-within-period-']"));
    }

    public WebElement getAccountRequestPanelPendingProcessing(AccountRequestAttributes accountRequest) {
        return getAccountRequestPanelInList(getAccountRequestPanelsPendingProcessing(), accountRequest);
    }

    public WebElement getAccountRequestPanelWithinPeriod(AccountRequestAttributes accountRequest) {
        return getAccountRequestPanelInList(getAccountRequestPanelsWithinPeriod(), accountRequest);
    }

    private WebElement getAccountRequestPanelInList(List<WebElement> panels, AccountRequestAttributes accountRequest) {
        String email = accountRequest.getEmail();
        String institute = accountRequest.getInstitute();
        for (WebElement panel : panels) {
            expandTab(panel);

            if (panel.findElement(By.cssSelector("[id^='email']")).getText().contains(email)
                    && panel.findElement(By.cssSelector("[id^='institute']")).getText().contains(institute)) {
                return panel;
            }
        }
        return null;
    }

    /**
     * Verifies the specified account request is displayed in any panel (pending processing).
     */
    public void verifyAccountRequestPanelPendingProcessingContent(AccountRequestAttributes accountRequest) {
        WebElement accountRequestPanel = getAccountRequestPanelPendingProcessing(accountRequest);
        verifyAccountRequestPanelContent(accountRequestPanel, accountRequest, false);
    }

    /**
     * Verifies the specified account request is displayed in the specified panel (pending processing).
     */
    public void verifyAccountRequestPanelPendingProcessingContent(Integer index, AccountRequestAttributes accountRequest) {
        WebElement accountRequestPanel = browser.driver.findElement(By.id("ar-pending-processing-" + index));
        verifyAccountRequestPanelContent(accountRequestPanel, accountRequest, false);
    }

    /**
     * Verifies the specified account request is displayed in any panel (within period).
     */
    public void verifyAccountRequestPanelWithinPeriodContent(AccountRequestAttributes accountRequest) {
        WebElement accountRequestPanel = getAccountRequestPanelWithinPeriod(accountRequest);
        verifyAccountRequestPanelContent(accountRequestPanel, accountRequest, true);
    }

    /**
     * Verifies the specified account request is displayed in the specified panel (within period).
     */
    public void verifyAccountRequestPanelWithinPeriodContent(Integer index, AccountRequestAttributes accountRequest) {
        WebElement accountRequestPanel = browser.driver.findElement(By.id("ar-within-period-" + index));
        verifyAccountRequestPanelContent(accountRequestPanel, accountRequest, true);
    }

    private void verifyAccountRequestPanelContent(WebElement accountRequestPanel, AccountRequestAttributes accountRequest,
                                                  boolean shouldRegisteredAtBePresent) {
        expandTab(accountRequestPanel);

        String actualNameInPanelHeader = accountRequestPanel.findElement(By.cssSelector("[id^='panel-header']")).getText();
        String actualName = accountRequestPanel.findElement(By.cssSelector("[id^='name']")).getText();
        String actualInstitute = accountRequestPanel.findElement(By.cssSelector("[id^='institute']")).getText();
        String actualEmail = accountRequestPanel.findElement(By.cssSelector("[id^='email']")).getText();
        String actualHomePageUrl = accountRequestPanel.findElement(By.cssSelector("[id^='home-page-url']")).getText();
        String actualComments = accountRequestPanel.findElement(By.cssSelector("[id^='comments']")).getText();
        String actualStatus = accountRequestPanel.findElement(By.cssSelector("[id^='status']")).getText();
        String actualCreatedAt = accountRequestPanel.findElement(By.cssSelector("[id^='submitted-at']")).getText();
        String actualLastProcessedAt =
                accountRequestPanel.findElement(By.cssSelector("[id^='last-processed-at']")).getText();

        assertEquals(accountRequest.getName(), actualNameInPanelHeader);
        assertEquals(accountRequest.getName(), actualName);
        assertEquals(accountRequest.getInstitute(), actualInstitute);
        assertEquals(accountRequest.getEmail(), actualEmail);
        assertEquals(accountRequest.getHomePageUrl(), actualHomePageUrl);
        assertEquals(accountRequest.getComments(), actualComments);
        assertEquals(accountRequest.getStatus().toString(), actualStatus);
        assertTrue(actualCreatedAt.contains(
                getDisplayedDateTime(accountRequest.getCreatedAt(), getTimezone(), DISPLAYED_DATE_FORMAT)));
        if (accountRequest.getLastProcessedAt() == null) {
            assertEquals("", actualLastProcessedAt);
        } else {
            assertTrue(actualLastProcessedAt.contains(
                    getDisplayedDateTime(accountRequest.getLastProcessedAt(), getTimezone(), DISPLAYED_DATE_FORMAT)));
        }

        if (shouldRegisteredAtBePresent) {
            String actualRegisteredAt = accountRequestPanel.findElement(By.cssSelector("[id^='registered-at']")).getText();
            if (accountRequest.getRegisteredAt() == null) {
                assertEquals("", actualRegisteredAt);
            } else {
                // comparison of the timezone part of the displayed time is ignored
                assertTrue(actualRegisteredAt.contains(
                        getDisplayedDateTime(accountRequest.getRegisteredAt(), getTimezone(), DISPLAYED_DATE_FORMAT)));
            }
        } else {
            assertTrue(accountRequestPanel.findElements(By.cssSelector("[id^='registered-at']")).isEmpty());
        }
    }

    public void verifyDisplayedStatusForNewlyDeletedAccountRequest(String panelIdentifier) {
        assertTrue(isElementPresent(By.id("status-undefined-" + panelIdentifier)));
    }

    public void verifyTabExpanded(String panelIdentifier) {
        assertTrue(isElementPresent(By.id("name-" + panelIdentifier)));
    }

    public void verifyTabCollapsed(String panelIdentifier) {
        assertFalse(isElementPresent(By.id("name-" + panelIdentifier)));
    }

    public void verifyErrorMessagePresent(String panelIdentifier, String messageContent) {
        String actualMessage = browser.driver.findElement(By.id("error-message-" + panelIdentifier)).getText();
        assertTrue(actualMessage.contains(messageContent));
    }

    public void verifyErrorMessageNotPresent(String panelIdentifier) {
        assertFalse(isElementPresent(By.id("error-message-" + panelIdentifier)));
    }

    private String getTimezone() {
        String timezone = browser.driver.findElement(By.tagName("h5")).getText();
        return timezone.substring(timezone.indexOf('(') + 1, timezone.indexOf(')'));
    }

    private void expandTab(WebElement accountRequestPanel) {
        if (accountRequestPanel.findElements(By.cssSelector("[id^='name']")).isEmpty()) {
            click(accountRequestPanel.findElement(By.cssSelector("[id^='panel-header']")));
            waitUntilAnimationFinish();
        }
    }

    private void expandTab(String panelIdentifier) {
        if (browser.driver.findElements(By.id("name-" + panelIdentifier)).isEmpty()) {
            click(browser.driver.findElement(By.id("panel-header-" + panelIdentifier)));
            waitUntilAnimationFinish();
        }
    }

}
