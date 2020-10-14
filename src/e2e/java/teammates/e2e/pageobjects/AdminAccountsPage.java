package teammates.e2e.pageobjects;

import static org.junit.Assert.assertEquals;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import teammates.common.datatransfer.attributes.AccountAttributes;

/**
 * Page Object Model for the admin accounts page.
 */
public class AdminAccountsPage extends AppPage {

    @FindBy(id = "account-google-id")
    private WebElement accountId;

    @FindBy(id = "account-name")
    private WebElement accountName;

    @FindBy(id = "account-email")
    private WebElement accountEmail;

    @FindBy(id = "account-institute")
    private WebElement accountInstitute;

    @FindBy(id = "account-is-instructor")
    private WebElement accountIsInstructor;

    public AdminAccountsPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("Account Details");
    }

    public void verifyAccountDetails(AccountAttributes account) {
        assertEquals(account.getGoogleId(), accountId.getText());
        assertEquals(account.getName(), accountName.getText());
        assertEquals(account.getEmail(), accountEmail.getText());
        assertEquals(account.getInstitute(), accountInstitute.getText());
        assertEquals(account.isInstructor(), Boolean.parseBoolean(accountIsInstructor.getText()));
    }

}
