package teammates.test.pageobjects;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import teammates.common.util.Url;

/**
 * Represents the home page of the website (i.e., index.html)
 */
public class HomePage extends AppPage {
    
    @FindBy(id = "btnInstructorLogin")
    private WebElement instructorLoginLink;
    
    @FindBy(id = "btnStudentLogin")
    private WebElement studentLoginLink;
    
    public HomePage(Browser    browser){
        super(browser);
    }

    public static HomePage getNewInstance(){
        return getNewPageInstance(HOMEPAGE, HomePage.class);
    }
    
    public static HomePage getNewInstance(Browser browser){
        return getNewPageInstance(browser, new Url(HOMEPAGE), HomePage.class);
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
        }
        return createCorretLoginPageType(pageSource);
        
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
            pageSource = getPageSource();
        }
        return createCorretLoginPageType(pageSource);
    }

    private LoginPage createCorretLoginPageType(String pageSource) {
        if (DevServerLoginPage.containsExpectedPageContents(pageSource)) {
            return changePageType(DevServerLoginPage.class);
        } else if (GoogleLoginPage.containsExpectedPageContents(pageSource)) {
            return changePageType(GoogleLoginPage.class);
        } else {
            throw new IllegalStateException("Not a valid login page :"    + pageSource);
        }
    }

}
