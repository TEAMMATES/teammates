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

    public DevServerLoginPage(Browser browser){
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return containsExpectedPageContents(getPageSource());
    }
    
    public static boolean containsExpectedPageContents(String pageSource) {
        return pageSource.contains("<h3>Not logged in</h3>");
    }

    @Override
    public InstructorHomePage loginAsInstructor(String username, String password) {
        fillTextBox(emailTextBox, username);
        loginButton.click();
        waitForPageToLoad();
        browser.isAdminLoggedIn = false;
        return changePageType(InstructorHomePage.class);
    }

    @Override
    public AppPage loginAsInstructorUnsuccessfully(String userName, String password) {
            fillTextBox(emailTextBox, userName);
            loginButton.click();
            waitForPageToLoad();
            browser.isAdminLoggedIn = false;
            return this;
    }

    @Override
    public void loginAdminAsInstructor(
            String adminUsername, String adminPassword, String instructorUsername) {
        fillTextBox(emailTextBox, instructorUsername);
        isAdminCheckBox.click();
        loginButton.click();
        waitForPageToLoad();
        browser.isAdminLoggedIn = true;
    }

    @Override
    public StudentHomePage loginAsStudent(String username, String password) {
        fillTextBox(emailTextBox, username);
        loginButton.click();
        waitForPageToLoad();
        browser.isAdminLoggedIn = false;
        return changePageType(StudentHomePage.class);
    }

    @Override
    public StudentCourseJoinConfirmationPage loginAsJoiningStudent(String username, String password) {
        fillTextBox(emailTextBox, username);
        loginButton.click();
        waitForPageToLoad();
        browser.isAdminLoggedIn = false;
        return changePageType(StudentCourseJoinConfirmationPage.class);
    }
    
    @Override
    public InstructorCourseJoinConfirmationPage loginAsJoiningInstructor(String username, String password) {
        fillTextBox(emailTextBox, username);
        loginButton.click();
        waitForPageToLoad();
        browser.isAdminLoggedIn = false;
        return changePageType(InstructorCourseJoinConfirmationPage.class);
    }
    
    @Override
    public InstructorHomePage loginAsJoiningInstructorByPassConfirmation(String username, String password) {
        fillTextBox(emailTextBox, username);
        loginButton.click();
        waitForPageToLoad();
        browser.isAdminLoggedIn = false;
        return changePageType(InstructorHomePage.class);
    }
    
    
}
