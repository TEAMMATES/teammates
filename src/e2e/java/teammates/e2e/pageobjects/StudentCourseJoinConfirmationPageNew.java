package teammates.e2e.pageobjects;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class StudentCourseJoinConfirmationPageNew extends AppPageNew {
    @FindBy(id = "button_confirm")
    private WebElement confirmButton;

    @FindBy(id = "button_cancel")
    private WebElement cancelButton;

    public StudentCourseJoinConfirmationPageNew(Browser browser) {
        super(browser);
    }

    @Override
    public boolean containsExpectedPageContents() {
        return getPageSource().contains("<h4>Confirm your Google account</h4>");
    }

    public StudentHomePageNew clickConfirmButton() {
        return clickConfirmButton(StudentHomePageNew.class);
    }

    public <T extends AppPageNew> T clickConfirmButton(Class<T> typeOfPage) {
        click(confirmButton);
        waitForPageToLoad();
        return changePageType(typeOfPage);
    }

    public LoginPageNew clickCancelButton() {
        click(cancelButton);
        waitForPageToLoad();
        return createCorrectLoginPageType(browser);
    }
}
