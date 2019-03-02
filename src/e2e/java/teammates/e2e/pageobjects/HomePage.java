package teammates.e2e.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Represents the home page of the website.
 */
public class HomePage extends AppPageNew {

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
        waitForElementVisibility(By.tagName("h3"));
        return createCorrectLoginPageType(browser);
    }
}
