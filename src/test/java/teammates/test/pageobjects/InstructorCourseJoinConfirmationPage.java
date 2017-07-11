package teammates.test.pageobjects;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import teammates.common.util.Const;
import teammates.common.util.retry.MaximumRetriesExceededException;
import teammates.common.util.retry.RetryableTask;

public class InstructorCourseJoinConfirmationPage extends AppPage {
    @FindBy(id = "button_confirm")
    private WebElement confirmButton;

    @FindBy(id = "button_cancel")
    private WebElement cancelButton;

    public InstructorCourseJoinConfirmationPage(Browser browser) {
        super(browser);
    }

    @Override
    public boolean containsExpectedPageContents() {
        return getPageSource().contains("<h3>Confirm your Google account</h3>");
    }

    public InstructorHomePage clickConfirmButton() {
        clickConfirmButtonAndWaitForPageToLoad();
        return changePageType(InstructorHomePage.class);
    }

    public InstructorHomePage clickConfirmButtonWithRetry() throws MaximumRetriesExceededException {
        persistenceRetryManager.runUntilSuccessful(new RetryableTask("Course join") {
            @Override
            public void run() {
                clickConfirmButtonAndWaitForPageToLoad();
            }

            @Override
            public boolean isSuccessful() {
                return isPageUri(Const.ActionURIs.INSTRUCTOR_HOME_PAGE);
            }

            @Override
            public void beforeRetry() {
                browser.driver.navigate().back();
            }
        });
        return changePageType(InstructorHomePage.class);
    }

    private void clickConfirmButtonAndWaitForPageToLoad() {
        click(confirmButton);
        waitForPageToLoad();
    }

    public HomePage clickCancelButton() {
        click(cancelButton);
        waitForPageToLoad();
        return changePageType(HomePage.class);
    }
}
