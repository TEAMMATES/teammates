package teammates.test.pageobjects;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class InstructorCourseJoinConfirmationPage extends AppPage {
    @FindBy(id = "button_confirm")
    protected WebElement confirmButton;
    
    @FindBy(id = "button_cancel")
    protected WebElement cancelButton;
    
    public InstructorCourseJoinConfirmationPage(Browser browser) {
        super(browser);
    }
    
    @Override
    public boolean containsExpectedPageContents() {
        return getPageSource().contains("<h3>Confirm your Google account</h3>");
    }

    public InstructorHomePage clickConfirmButton() {
        confirmButton.click();
        waitForPageToLoad();
        return changePageType(InstructorHomePage.class);
    }
    
    public HomePage clickCancelButton() {
        cancelButton.click();
        waitForPageToLoad();
        return changePageType(HomePage.class);
    }
}
