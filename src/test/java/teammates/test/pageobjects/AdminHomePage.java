package teammates.test.pageobjects;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import teammates.common.datatransfer.InstructorAttributes;

public class AdminHomePage extends AppPage {
    @FindBy (id = "addInstructorDetailsSingleLine")
    WebElement detailsSingleLineTextBox;
    
    @FindBy (id = "instructorShortName")
    WebElement shortNameTextBox;

    @FindBy (id = "instructorName")
    WebElement nameTextBox;
    
    @FindBy (id = "instructorEmail")
    WebElement emailTextBox;
    
    @FindBy (id = "instructorInstitution")
    WebElement institutionTextBox;
    
    @FindBy (id = "btnAddInstructor")
    WebElement submitButton;
    
    @FindBy (id = "btnAddInstructorDetailsSingleLineForm")
    WebElement submitButtonDetailsSingleLineForm;
    
    public AdminHomePage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("<h1>Add New Instructor</h1>");
    }

    /** Fills the form with values from the parameters and clicks the submit button.
     * If an attribute value is null, the existing value in the form is used.
     * 
     * @param attributesForNewAccount
     * @param isCreateCourse True if a sample course should be created for this account.
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
    
    public String getMessageFromResultTable(int index) {
        return getCellValueFromDataTable(index, 5);
    }

}
