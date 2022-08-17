package teammates.e2e.pageobjects;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Represents the account request form page of the website.
 */
public class AccountRequestFormPage extends AppPage {

    /** Field name for the "Your full name" field in the form. */
    public static final String NAME_FIELD_NAME = "name";

    /** Field name for the "University/school/institute" field in the form. */
    public static final String INSTITUTE_FIELD_NAME = "institute";

    /** Field name for the "Country" field in the form. */
    public static final String COUNTRY_FIELD_NAME = "country";

    /** Field name for the ""Official" email address" field in the form. */
    public static final String EMAIL_FIELD_NAME = "email";

    /** Field name for the "URL of your home page (if any)" field in the form. */
    public static final String HOME_PAGE_URL_FIELD_NAME = "url";

    /** Field name for the "I'm requesting for" field in the form. */
    public static final String ACCOUNT_TYPE_FIELD_NAME = "account-type";

    /** Field name for the "Any other comments/queries" field in the form. */
    public static final String COMMENTS_FIELD_NAME = "comments";

    /** Account type name for the "I'm requesting for an instructor account" option in the form. */
    public static final String INSTRUCTOR_ACCOUNT_TYPE_NAME = "instructor";

    /** Account type name for the "I'm requesting for a student account" option in the form. */
    public static final String STUDENT_ACCOUNT_TYPE_NAME = "student";

    @FindBy(id = "btn-submit")
    private WebElement formSubmitButton;

    public AccountRequestFormPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("Request for an Account\n</h1>");
    }

    public void fillInField(String fieldName, String content) {
        WebElement inputBox = browser.driver.findElement(By.id(fieldName));
        fillTextBox(inputBox, content);
    }

    public void selectAccountType(String accountTypeName) {
        WebElement accountTypeRadioButton = browser.driver.findElement(By.id(accountTypeName + "-account-type"));
        click(accountTypeRadioButton);
    }

    public void clickSubmitButton() {
        click(formSubmitButton);
        waitForPageToLoad(true);
    }

    public void verifyFieldErrorMessagePresent(String fieldName, String messageContent) {
        String actualMessage =
                browser.driver.findElement(By.cssSelector("#" + fieldName + "-error span:not([hidden])")).getText();
        assertTrue(actualMessage.contains(messageContent));
    }

    public void verifyEmptyFieldErrorMessagePresent(String fieldName) {
        verifyFieldErrorMessagePresent(fieldName, "This field should not be empty");
    }

    public void verifyFieldErrorMessageNotPresent(String fieldName) {
        assertFalse(isElementPresent(By.id(fieldName + "-error")));
    }

    public void verifyNoErrorMessagePresent() {
        verifyFieldErrorMessageNotPresent(NAME_FIELD_NAME);
        verifyFieldErrorMessageNotPresent(INSTITUTE_FIELD_NAME);
        verifyFieldErrorMessageNotPresent(COUNTRY_FIELD_NAME);
        verifyFieldErrorMessageNotPresent(EMAIL_FIELD_NAME);
        verifyFieldErrorMessageNotPresent(HOME_PAGE_URL_FIELD_NAME);
        verifyFieldErrorMessageNotPresent(ACCOUNT_TYPE_FIELD_NAME);
        verifyFieldErrorMessageNotPresent(COMMENTS_FIELD_NAME);
    }

    public void verifyAllEmptyFieldErrorMessagesPresent() {
        // these four are compulsory fields
        verifyEmptyFieldErrorMessagePresent(NAME_FIELD_NAME);
        verifyEmptyFieldErrorMessagePresent(INSTITUTE_FIELD_NAME);
        verifyEmptyFieldErrorMessagePresent(COUNTRY_FIELD_NAME);
        verifyEmptyFieldErrorMessagePresent(EMAIL_FIELD_NAME);
    }

    public void verifyPageBottomErrorMessage(String messageContent) {
        String actualMessage = browser.driver.findElement(By.id("other-error")).getText();
        assertTrue(actualMessage.contains(messageContent));
    }

    public void verifySubmitButtonEnabled() {
        assertTrue(formSubmitButton.isEnabled());
    }

    public void verifySubmitButtonDisabled() {
        assertFalse(formSubmitButton.isEnabled());
    }

}
