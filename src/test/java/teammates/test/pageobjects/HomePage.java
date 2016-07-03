package teammates.test.pageobjects;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Represents the home page of the website (i.e., index.jsp)
 */
public class HomePage extends AppPage {
    
    @FindBy(id = "btnInstructorLogin")
    private WebElement instructorLoginLink;
    
    @FindBy(id = "btnStudentLogin")
    private WebElement studentLoginLink;
    
    public HomePage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("Designed for Simplicity, Flexibility, and Power:");
    }

    public LoginPage clickInstructorLogin() {
        
        this.instructorLoginLink.click();
        waitForPageToLoad();
        String pageSource = getPageSource();
        if (InstructorHomePage.containsExpectedPageContents(pageSource)) {
            //already logged in. We need to logout because the return type of
            //  this method is a LoginPage
            logout();
            instructorLoginLink.click();
            waitForPageToLoad();
        }
        return createCorrectLoginPageType(browser);
        
    }

    public LoginPage clickStudentLogin() {
        studentLoginLink.click();
        waitForPageToLoad();
        String pageSource = getPageSource();
        if (StudentHomePage.containsExpectedPageContents(pageSource)) {
            //already logged in. We need to logout because the return type of
            //  this method is a LoginPage
            logout();
            studentLoginLink.click();
            waitForPageToLoad();
        }
        return createCorrectLoginPageType(browser);
    }

}
