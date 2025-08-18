package teammates.e2e.pageobjects;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import teammates.test.ThreadHelper;

/**
 * Represents the admin home page of the website.
 */
public class AdminHomePage extends AppPage {
    private static final int ACCOUNT_REQUEST_COL_NAME = 1;
    private static final int ACCOUNT_REQUEST_COL_EMAIL = 2;
    private static final int ACCOUNT_REQUEST_COL_INSTITUTE = 4;

    @FindBy(id = "instructor-details-single-line")
    private WebElement detailsSingleLineTextBox;

    @FindBy (id = "instructor-name")
    private WebElement nameTextBox;

    @FindBy (id = "instructor-email")
    private WebElement emailTextBox;

    @FindBy (id = "instructor-institution")
    private WebElement institutionTextBox;

    @FindBy (id = "add-instructor")
    private WebElement submitButton;

    @FindBy (id = "add-instructor-single-line")
    private WebElement submitButtonDetailsSingleLineForm;

    public AdminHomePage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("Add New Instructor</h1>");
    }

    public void queueInstructorForAdding(String name, String email, String institute) {
        if (name != null) {
            fillTextBox(nameTextBox, name);
        }
        if (email != null) {
            fillTextBox(emailTextBox, email);
        }
        if (institute != null) {
            fillTextBox(institutionTextBox, institute);
        }

        click(submitButton);
    }

    public void queueInstructorForAdding(String instructorDetails) {
        if (instructorDetails != null) {
            fillTextBox(detailsSingleLineTextBox, instructorDetails);
        }
        click(submitButtonDetailsSingleLineForm);
    }

    public void clickApproveAccountRequestButton(String name, String email, String institute) {
        WebElement accountRequestRow = getAccountRequestRow(name, email, institute);
        waitForElementPresence(By.cssSelector("[id^='approve-account-request-']"));
        WebElement approveButton = accountRequestRow.findElement(By.cssSelector("[id^='approve-account-request-']"));
        waitForElementToBeClickable(approveButton);
        approveButton.click();
        waitForPageToLoad();
    }

    public void clickMoreInfoButtonForRegisteredInstructor(int i) {
        By by = By.id("instructor-" + i + "-registered-info-button");
        waitForElementVisibility(by);
        WebElement element = browser.driver.findElement(by);
        click(element);
        waitForElementVisibility(By.id("reset-account-request-link"));
    }

    public void clickResetAccountRequestLink() {
        By by = By.id("reset-account-request-link");
        WebElement element = browser.driver.findElement(by);
        click(element);
        ThreadHelper.waitFor(1000); // Modals are stacked, wait briefly to ensure confirmation modal is shown
        List<WebElement> okButtons = browser.driver.findElements(By.className("modal-btn-ok"));
        clickDismissModalButtonAndWaitForModalHidden(okButtons.get(1)); // Second modal is confirmation modal
    }

    public String removeSpanFromText(String text) {
        return text.replace("<span class=\"highlighted-text\">", "").replace("</span>", "");
    }

    public WebElement getAccountRequestRow(String name, String email, String institute) {
        List<WebElement> rows = browser.driver.findElements(By.cssSelector("tm-account-request-table tbody tr"));
        for (WebElement row : rows) {
            List<WebElement> columns = row.findElements(By.tagName("td"));
            if (removeSpanFromText(columns.get(ACCOUNT_REQUEST_COL_NAME - 1)
                    .getAttribute("innerHTML")).contains(name)
                    && removeSpanFromText(columns.get(ACCOUNT_REQUEST_COL_EMAIL - 1)
                    .getAttribute("innerHTML")).contains(email)
                    && removeSpanFromText(columns.get(ACCOUNT_REQUEST_COL_INSTITUTE - 1)
                    .getAttribute("innerHTML")).contains(institute)) {
                return row;
            }
        }
        return null;
    }

    public void verifyInstructorInAccountRequestTable(String name, String email, String institute) {
        WebElement row = getAccountRequestRow(name, email, institute);
        assertNotNull(row);
    }
}
