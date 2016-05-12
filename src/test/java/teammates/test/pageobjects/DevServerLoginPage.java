package teammates.test.pageobjects;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;


public class DevServerLoginPage extends LoginPage {
    
    @FindBy(id = "email")
    private WebElement emailTextBox;
    
    @FindBy(id = "isAdmin")
    private WebElement isAdminCheckBox;
    
    @FindBy(xpath = "/html/body/form/div/p[3]/input[1]")
    private WebElement loginButton;

    public DevServerLoginPage(final Browser browser){
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return containsExpectedPageContents(getPageSource());
    }
    
    public static boolean containsExpectedPageContents(final String pageSource) {
        return pageSource.contains("<h3>Not logged in</h3>");
    }

    @Override
    public InstructorHomePage loginAsInstructor(final String username, final String password) {
        fillTextBox(emailTextBox, username);
        loginButton.click();
        waitForPageToLoad();
        browser.isAdminLoggedIn = false;
        return changePageType(InstructorHomePage.class);
    }

    @Override
    public AppPage loginAsInstructorUnsuccessfully(final String userName, final String password) {
            fillTextBox(emailTextBox, userName);
            loginButton.click();
            waitForPageToLoad();
            browser.isAdminLoggedIn = false;
            return this;
    }

    @Override
    public void loginAdminAsInstructor(
            final String adminUsername, final String adminPassword, final String instructorUsername) {
        fillTextBox(emailTextBox, instructorUsername);
        isAdminCheckBox.click();
        loginButton.click();
        waitForPageToLoad();
        browser.isAdminLoggedIn = true;
    }

    @Override
    public StudentHomePage loginAsStudent(final String username, final String password) {
        return loginAsStudent(username, password, StudentHomePage.class);
    }

    @Override
    public <T extends AppPage> T loginAsStudent(final String username, final String password, final Class<T> typeOfPage) {
        fillTextBox(emailTextBox, username);
        loginButton.click();
        waitForPageToLoad();
        browser.isAdminLoggedIn = false;
        return changePageType(typeOfPage);
    }

    @Override
    public StudentCourseJoinConfirmationPage loginAsJoiningStudent(final String username, final String password) {
        fillTextBox(emailTextBox, username);
        loginButton.click();
        waitForPageToLoad();
        browser.isAdminLoggedIn = false;
        return changePageType(StudentCourseJoinConfirmationPage.class);
    }
    
    @Override
    public InstructorCourseJoinConfirmationPage loginAsJoiningInstructor(final String username, final String password) {
        fillTextBox(emailTextBox, username);
        loginButton.click();
        waitForPageToLoad();
        browser.isAdminLoggedIn = false;
        return changePageType(InstructorCourseJoinConfirmationPage.class);
    }
    
    @Override
    public InstructorHomePage loginAsJoiningInstructorByPassConfirmation(final String username, final String password) {
        fillTextBox(emailTextBox, username);
        loginButton.click();
        waitForPageToLoad();
        browser.isAdminLoggedIn = false;
        return changePageType(InstructorHomePage.class);
    }
    
    
}
