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

    public GoogleLoginPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("Sign in with your Google Account");
    }

    @Override
    public InstructorHomePage loginAsInstructor(String username, String password) {
        completeGoogleLoginSteps(username, password);
        browser.isAdminLoggedIn = false;
        return changePageType(InstructorHomePage.class);
    }

    @Override
    public AppPage loginAsInstructorUnsuccessfully(String userName, String password) {
        completeGoogleLoginSteps(userName, password);
        browser.isAdminLoggedIn = false;
        return this;
    }

    @Override
    public void loginAdminAsInstructor(
            String adminUsername, String adminPassword, String instructorUsername) {
        completeGoogleLoginSteps(adminUsername, adminPassword);
        browser.isAdminLoggedIn = true;
    }

    @Override
    public StudentHomePage loginAsStudent(String username, String password) {
        return loginAsStudent(username, password, StudentHomePage.class);
    }

    @Override
    public <T extends AppPage> T loginAsStudent(String username, String password, Class<T> typeOfPage) {
        completeGoogleLoginSteps(username, password);
        browser.isAdminLoggedIn = false;
        return changePageType(typeOfPage);
    }

    private void completeGoogleLoginSteps(String username, String password) {
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

    private void submitCredentials(String username, String password) {
        fillTextBox(usernameTextBox, username);
        click(By.id("next"));
        waitForElementVisibility(passwordTextBox);
        fillTextBox(passwordTextBox, password);

        if (staySignedCheckbox.isSelected()) {
            click(staySignedCheckbox);
        }

        click(loginButton);
        waitForPageToLoad();
    }

    @Override
    public StudentCourseJoinConfirmationPage loginAsJoiningStudent(
            String username, String password) {
        completeGoogleLoginSteps(username, password);
        browser.isAdminLoggedIn = false;
        return changePageType(StudentCourseJoinConfirmationPage.class);
    }

    @Override
    public InstructorCourseJoinConfirmationPage loginAsJoiningInstructor(
            String username, String password) {
        completeGoogleLoginSteps(username, password);
        browser.isAdminLoggedIn = false;
        return changePageType(InstructorCourseJoinConfirmationPage.class);
    }

    @Override
    public InstructorHomePage loginAsJoiningInstructorByPassConfirmation(
            String username, String password) {
        completeGoogleLoginSteps(username, password);
        browser.isAdminLoggedIn = false;
        return changePageType(InstructorHomePage.class);
    }
}
