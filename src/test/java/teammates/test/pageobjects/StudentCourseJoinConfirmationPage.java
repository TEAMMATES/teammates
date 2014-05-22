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
        return getPageSource().contains("<h2>Confirm your Google account</h2>");
    }

    public StudentHomePage clickConfirmButton() {
        confirmButton.click();
        waitForPageToLoad();
        return changePageType(StudentHomePage.class);
    }
    
    public HomePage clickCancelButton() {
        cancelButton.click();
        waitForPageToLoad();
        return changePageType(HomePage.class);
    }
}
