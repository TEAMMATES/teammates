package teammates.test.pageobjects;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class FeedbackRequestResendLinksPage extends GenericAppPage {

    @FindBy(id = "email")
    private WebElement email;

    @FindBy(id = "submitButton")
    private WebElement submitButton;

    public FeedbackRequestResendLinksPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("Request for Resend of Feedback Links");
    }

    public void fillEmailAddress(String emailAddress) {
        fillTextBox(email, emailAddress);
    }

    public void clickSubmitButton() {
        click(submitButton);
        waitForPageToLoad();
    }
}
