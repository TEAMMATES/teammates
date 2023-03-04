package teammates.e2e.pageobjects;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import teammates.test.ThreadHelper;

/**
 * Represents the admin home page of the website.
 */
public class AdminHomePage extends AppPage {

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

    @FindBy (id = "add-all-instructors")
    private WebElement addAllInstructorsButton;

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

    public void addAllInstructors() {
        click(addAllInstructorsButton);
        waitForElementToBeClickable(addAllInstructorsButton);
    }

    public String getMessageForInstructor(int i) {
        By by = By.id("message-instructor-" + i);
        waitForElementVisibility(by);
        WebElement element = browser.driver.findElement(by);
        if (element == null) {
            return null;
        }
        return element.getText();
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
}
