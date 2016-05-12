package teammates.test.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class GoogleLoginPage extends LoginPage {
    
    @FindBy(id = "Email")
    private WebElement usernameTextBox;
    
    @FindBy(id = "Passwd")
    private WebElement passwordTextBox;
    
    @FindBy(id = "signIn")
    private WebElement loginButton;
    
    
    @FindBy(id = "PersistentCookie")
    private WebElement staySignedCheckbox;


    public GoogleLoginPage(final Browser browser){
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return containsExpectedPageContents(getPageSource());
    }
    
    public static boolean containsExpectedPageContents(final String pageSource){
        return pageSource.contains("Sign in with your Google Account");
    }

    @Override
    public InstructorHomePage loginAsInstructor(final String username, final String password) {
        completeGoogleLoginSteps(username, password);
        InstructorHomePage homePage = changePageType(InstructorHomePage.class);
        browser.isAdminLoggedIn = false;
        return homePage;
    }

    
    @Override
    public AppPage loginAsInstructorUnsuccessfully(final String userName, final String password) {
        completeGoogleLoginSteps(userName, password);
        browser.isAdminLoggedIn = false;
        return this;
    }

    @Override
    public void loginAdminAsInstructor(
            final String adminUsername, final String adminPassword, final String instructorUsername) {
        completeGoogleLoginSteps(adminUsername, adminPassword);
        browser.isAdminLoggedIn = true;
    }

    @Override
    public StudentHomePage loginAsStudent(final String username, final String password) {
        return loginAsStudent(username, password, StudentHomePage.class);
    }

    @Override
    public <T extends AppPage> T loginAsStudent(final String username, final String password, final Class<T> typeOfPage) {
        completeGoogleLoginSteps(username, password);
        T page = changePageType(typeOfPage);
        browser.isAdminLoggedIn = false;
        return page;
    }

    private void completeGoogleLoginSteps(final String username, final String password) {
        submitCredentials(username, password);
        dealWithSignIntoChromePage();
        handleApprovalPageIfAny();
    }

    private void dealWithSignIntoChromePage() {
        try {
            click(By.id("no-button"));
            waitForPageToLoad();
        } catch (NoSuchElementException e) {
            System.out.println("No 'sign into chrome' option");
        }
    }

    private void handleApprovalPageIfAny() {
        boolean isPageRequestingAccessApproval = isElementPresent(By.id("approve_button"));
        if (isPageRequestingAccessApproval) {
            click(By.id("persist_checkbox"));
            click(By.id("approve_button"));
            waitForPageToLoad();
        }
    }

    private void submitCredentials(final String username, final String password) {
        fillTextBox(usernameTextBox, username);
        click(By.id("next"));
        waitForElementVisibility(passwordTextBox);
        fillTextBox(passwordTextBox, password);
        
        if (staySignedCheckbox.isSelected()) {
            staySignedCheckbox.click();
        }
        
        loginButton.click();
        waitForPageToLoad();
    }

    @Override
    public StudentCourseJoinConfirmationPage loginAsJoiningStudent(
            final String username, final String password) {
        completeGoogleLoginSteps(username, password);
        browser.isAdminLoggedIn = false;
        return changePageType(StudentCourseJoinConfirmationPage.class);
    }

    @Override
    public InstructorCourseJoinConfirmationPage loginAsJoiningInstructor(
            final String username, final String password) {
        completeGoogleLoginSteps(username, password);
        browser.isAdminLoggedIn = false;
        return changePageType(InstructorCourseJoinConfirmationPage.class);
    }
    
    @Override
    public InstructorHomePage loginAsJoiningInstructorByPassConfirmation(
            final String username, final String password) {
        completeGoogleLoginSteps(username, password);
        browser.isAdminLoggedIn = false;
        return changePageType(InstructorHomePage.class);
    }
}
