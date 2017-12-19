package teammates.test.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;

public class AdminHomePage extends AppPage {
    @FindBy (id = "addInstructorDetailsSingleLine")
    private WebElement detailsSingleLineTextBox;

    @FindBy (id = "instructorShortName")
    private WebElement shortNameTextBox;

    @FindBy (id = "instructorName")
    private WebElement nameTextBox;

    @FindBy (id = "instructorEmail")
    private WebElement emailTextBox;

    @FindBy (id = "instructorInstitution")
    private WebElement institutionTextBox;

    @FindBy (id = "btnAddInstructor")
    private WebElement submitButton;

    @FindBy (id = "btnAddInstructorDetailsSingleLineForm")
    private WebElement submitButtonDetailsSingleLineForm;

    public AdminHomePage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("<h1>Add New Instructor</h1>");
    }

    /**
     * Fills the form with values from the parameters and clicks the submit button.
     * If an attribute value is null, the existing value in the form is used.
     */
    public AdminHomePage createInstructor(String shortName, InstructorAttributes attributesForNewAccount, String institute) {
        if (shortName != null) {
            fillTextBox(shortNameTextBox, shortName);
        }
        if (attributesForNewAccount.name != null) {
            fillTextBox(nameTextBox, attributesForNewAccount.name);
        }
        if (attributesForNewAccount.email != null) {
            fillTextBox(emailTextBox, attributesForNewAccount.email);
        }
        if (institute != null) {
            fillTextBox(institutionTextBox, institute);
        }

        click(submitButton);
        waitForElementToBeClickable(submitButton);
        return this;
    }

    public void createInstructorByInstructorDetailsSingleLineForm(String instructorDetails) {
        if (instructorDetails != null) {
            fillTextBox(detailsSingleLineTextBox, instructorDetails);
        }
        click(submitButtonDetailsSingleLineForm);
        waitForElementToBeClickable(submitButtonDetailsSingleLineForm);
    }

    public void clearInstructorDetailsSingleLineForm() {
        fillTextBox(detailsSingleLineTextBox, "");
    }

    public String getShortNameFromResultTable(int index) {
        return getCellValueFromDataTable(index, 0);
    }

    public String getNameFromResultTable(int index) {
        return getCellValueFromDataTable(index, 1);
    }

    public String getEmailFromResultTable(int index) {
        return getCellValueFromDataTable(index, 2);
    }

    public String getInstitutionFromResultTable(int index) {
        return getCellValueFromDataTable(index, 3);
    }

    public String getStatusFromResultTable(int index) {
        return getCellValueFromDataTable(index, 4);
    }

    public String getMessageFromResultTable(int index) {
        return getCellValueFromDataTable(index, 5);
    }

    public String getJoinLink(String messageText) {
        WebElement link = browser.driver.findElement(By.linkText(Const.JOIN_LINK));
        return link.getAttribute("href");
    }

}
