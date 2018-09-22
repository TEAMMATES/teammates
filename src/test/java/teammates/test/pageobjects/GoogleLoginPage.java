package teammates.test.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.google.common.base.Preconditions;

import teammates.test.driver.TestProperties;

public class GoogleLoginPage extends LoginPage {

    private static final String EXPECTED_SNIPPET_SIGN_IN = "Sign in - Google Accounts";
    private static final String EXPECTED_SNIPPET_APPROVAL = "requesting permission to access your Google Account";

    @FindBy(css = "div[role='presentation']")
    private WebElement loginPanel;

    @FindBy(id = "identifierId")
    private WebElement identifierTextBox;

    @FindBy(id = "identifierNext")
    private WebElement identifierNextButton;

    @FindBy(css = "#password input[type=password]")
    private WebElement passwordTextBox;

    @FindBy(id = "passwordNext")
    private WebElement passwordNextButton;

    public GoogleLoginPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains(EXPECTED_SNIPPET_SIGN_IN);
    }

    @Override
    public InstructorHomePage loginAsInstructor(String username, String password) {
        return loginAsInstructor(username, password, InstructorHomePage.class);
    }

    @Override
    public <T extends AppPage> T loginAsInstructor(String username, String password, Class<T> typeOfPage) {
        completeGoogleLoginSteps(username, password);
        browser.isAdminLoggedIn = false;
        return changePageType(typeOfPage);
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
        waitForPageToLoad();
        waitForRedirectIfAny();
        boolean isPageRequestingAccessApproval = getPageSource().contains(EXPECTED_SNIPPET_APPROVAL);
        if (isPageRequestingAccessApproval) {
            markCheckBoxAsChecked(browser.driver.findElement(By.id("persist_checkbox")));
            click(By.id("approve_button"));
            waitForPageToLoad();
        }
    }

    private void waitForRedirectIfAny() {
        String loginRedirectUrl = TestProperties.TEAMMATES_URL + "/_ah/conflogin";
        waitFor(d -> {
            String url = Preconditions.checkNotNull(d).getCurrentUrl();
            boolean isTeammatesPage = url.startsWith(TestProperties.TEAMMATES_URL) && !url.startsWith(loginRedirectUrl);
            boolean isApprovalPage = d.getPageSource().contains(EXPECTED_SNIPPET_APPROVAL);
            return isTeammatesPage || isApprovalPage;
        });
    }

    private void waitForLoginPanelAnimationToComplete() {
        // the login panel will have attribute `aria-busy="true"` while in animation
        waitFor(ExpectedConditions.attributeToBe(loginPanel, "aria-busy", ""));
    }

    private void submitCredentials(String username, String password) {
        completeFillIdentifierSteps(username);
        click(identifierNextButton);

        waitForLoginPanelAnimationToComplete();
        fillTextBox(passwordTextBox, password);

        click(passwordNextButton);
        waitForPageToLoad();
    }

    private void completeFillIdentifierSteps(String identifier) {
        By switchAccountButtonBy = By.id("profileIdentifier");
        By useAnotherAccountButtonBy = By.id("identifierLink");

        if (isElementPresent(switchAccountButtonBy)) {
            click(switchAccountButtonBy);
            waitForLoginPanelAnimationToComplete();
        }

        if (isElementPresent(useAnotherAccountButtonBy)) {
            click(useAnotherAccountButtonBy);
            waitForLoginPanelAnimationToComplete();
        }

        fillTextBox(identifierTextBox, identifier);
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
