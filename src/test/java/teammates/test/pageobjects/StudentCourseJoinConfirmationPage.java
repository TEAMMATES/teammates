package teammates.test.pageobjects;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

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
        confirmButton.click();
        waitForPageToLoad();
        return changePageType(StudentHomePage.class);
    }
    
    public String clickCancelButtonAndGetSourceOfDestination() {
        cancelButton.click();
        waitForPageToLoad();
        return browser.driver.getPageSource();
    }
}
