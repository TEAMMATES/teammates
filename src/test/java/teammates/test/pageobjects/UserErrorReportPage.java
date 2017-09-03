package teammates.test.pageobjects;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import teammates.common.util.Config;
import teammates.common.util.Const;

public class UserErrorReportPage extends AppPage {

    @FindBy (name = Const.ParamsNames.ERROR_FEEDBACK_EMAIL_RECEIVER_ADDRESS)
    private WebElement errorFeedbackEmailReceiverAddressField;

    @FindBy (name = Const.ParamsNames.ERROR_FEEDBACK_EMAIL_SUBJECT)
    private WebElement errorFeedbackEmailSubjectField;

    @FindBy (name = Const.ParamsNames.ERROR_FEEDBACK_EMAIL_CONTENT)
    private WebElement errorFeedbackEmailContentField;

    @FindBy (xpath = "//button[@type='submit']")
    private WebElement sendFeedbackButton;

    public UserErrorReportPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("<h2>Uh oh! Something went wrong.</h2>");
    }

    public void verifyErrorReportFormContents() {
        assertEquals(errorFeedbackEmailReceiverAddressField.getAttribute("value"), Config.SUPPORT_EMAIL);
        assertTrue(errorFeedbackEmailReceiverAddressField.getAttribute("readonly") != null);
        assertEquals(errorFeedbackEmailSubjectField.getAttribute("value"), Const.ERROR_FEEDBACK_EMAIL_SUBJECT);
        assertTrue(errorFeedbackEmailSubjectField.isEnabled());
    }

    public void fillFormAndClickSubmit(String message) {
        fillTextBox(errorFeedbackEmailContentField, message);
        click(sendFeedbackButton);
    }

}
