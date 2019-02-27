package teammates.e2e.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class DevServerLoginPageNew extends LoginPageNew {

    @FindBy(id = "email")
    private WebElement emailTextBox;

    @FindBy(id = "isAdmin")
    private WebElement isAdminCheckBox;

    @FindBy(xpath = "/html/body/form/div/p[3]/input[1]")
    private WebElement loginButton;

    public DevServerLoginPageNew(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getLoginPageTitle().contains("Not logged in");
    }

    @Override
    public void loginAdminAsInstructor(
            String adminUsername, String adminPassword, String instructorUsername) {
        fillTextBox(emailTextBox, instructorUsername);
        click(isAdminCheckBox);
        click(loginButton);
        waitForPageToLoad();
        browser.isAdminLoggedIn = true;
    }

    @Override
    public StudentHomePageNew loginAsStudent(String username, String password) {
        return loginAsStudent(username, password, StudentHomePageNew.class);
    }

    @Override
    public <T extends AppPageNew> T loginAsStudent(String username, String password, Class<T> typeOfPage) {
        fillTextBox(emailTextBox, username);
        click(loginButton);
        waitForPageToLoad();
        browser.isAdminLoggedIn = false;
        return changePageType(typeOfPage);
    }
}
