package teammates.test.pageobjects;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import teammates.common.util.Const;
import teammates.common.util.ThreadHelper;
import teammates.test.driver.TestProperties;

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

    public InstructorHomePage clickConfirmButtonWithRetry() {
        clickConfirmButtonAndWaitForPageToLoad();
        for (int delay = 1; !isPageUri(Const.ActionURIs.INSTRUCTOR_HOME_PAGE)
                && delay <= TestProperties.PERSISTENCE_RETRY_PERIOD_IN_S / 2; delay *= 2) {
            System.out.println("Course join failed; waiting " + delay + "s before retry");
            ThreadHelper.waitFor(delay * 1000);
            browser.driver.navigate().back();
            clickConfirmButtonAndWaitForPageToLoad();
        }
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
