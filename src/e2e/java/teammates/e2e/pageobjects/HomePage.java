package teammates.e2e.pageobjects;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Page Object Model for home page in the development server.
 */
public class HomePage extends AppPage {

    @FindBy(id = "btnStudentLogin")
    private WebElement studentLoginLink;

    public HomePage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getTitle().contains("TEAMMATES");
    }

    public LoginPage clickStudentLogin() {
        click(studentLoginLink);
        return createCorrectLoginPageType(browser);
    }

}
