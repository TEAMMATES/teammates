package teammates.test.pageobjects;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import teammates.test.driver.TestProperties;

public class StudentCourseJoinConfirmationPage extends AppPage {
    @FindBy(id = "button_confirm")
    protected WebElement confirmButton;
    
    @FindBy(id = "button_cancel")
    protected WebElement cancelButton;
    
    public StudentCourseJoinConfirmationPage(Browser browser) {
        super(browser);
    }
    
    @Override
    public boolean containsExpectedPageContents() {
        return getPageSource().contains("<h4>Confirm your Google account</h4>");
    }

    public StudentHomePage clickConfirmButton() {
        return clickConfirmButton(StudentHomePage.class);
    }
    
    public <T extends AppPage> T clickConfirmButton(Class<T> typeOfPage) {
        confirmButton.click();
        waitForPageToLoad();
        return changePageType(typeOfPage);
    }
    
    public String clickCancelButtonAndGetSourceOfDestination() {
        cancelButton.click();
        waitForPageToLoad();
        return browser.driver.getPageSource();
    }
    
    public LoginPage clickCancelButton() {
        cancelButton.click();
        waitForPageToLoad();
        if (TestProperties.isDevServer()) {
            return changePageType(DevServerLoginPage.class);
        }
        return changePageType(GoogleLoginPage.class);
    }
}
