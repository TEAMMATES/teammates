package teammates.test.pageobjects;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class StudentCourseJoinConfirmationPage extends AppPage {
    @FindBy(id = "button_confirm")
    private WebElement confirmButton;

    @FindBy(id = "button_cancel")
    private WebElement cancelButton;

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
        click(confirmButton);
        waitForPageToLoad();
        return changePageType(typeOfPage);
    }

    public LoginPage clickCancelButton() {
        click(cancelButton);
        waitForPageToLoad();
        return createCorrectLoginPageType(browser);
    }
}
