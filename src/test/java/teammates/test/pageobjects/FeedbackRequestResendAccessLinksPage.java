package teammates.test.pageobjects;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class FeedbackRequestResendAccessLinksPage extends GenericAppPage {

    @FindBy(id = "email")
    private WebElement email;

    @FindBy(id = "submitButton")
    private WebElement submitButton;

    public FeedbackRequestResendAccessLinksPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("Request for Resending of Access Links");
    }

    public void fillEmailAddress(String emailAddress) {
        fillTextBox(email, emailAddress);
    }

    public void clickSubmitButton() {
        click(submitButton);
        waitForPageToLoad();
    }
}
