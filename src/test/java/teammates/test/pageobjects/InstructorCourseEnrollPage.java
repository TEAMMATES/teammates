package teammates.test.pageobjects;

import static org.testng.AssertJUnit.assertTrue;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class InstructorCourseEnrollPage extends AppPage {

    @FindBy(id = "enrollstudents")
    private WebElement enrollTextBox;

    @FindBy(id = "button_enroll")
    private WebElement enrollButton;

    public InstructorCourseEnrollPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        // Intentional check for opening h1 and not closing h1 because the following content is not static
        return getPageSource().contains("<h1>Enroll Students for");
    }

    public InstructorCourseEnrollPage verifyIsCorrectPage(String courseId) {
        assertTrue(getPageSource().contains("Enroll Students for " + courseId));
        return this;
    }

    public String getEnrollText() {
        return getTextBoxValue(enrollTextBox);
    }

    public InstructorCourseEnrollResultPage enroll(String enrollString) {
        fillSpreadsheet(enrollString);
        click(enrollButton);
        waitForPageToLoad();
        return changePageType(InstructorCourseEnrollResultPage.class);
    }

    public InstructorCourseEnrollPage enrollUnsuccessfully(String enrollString) {
        fillSpreadsheet(enrollString);
        click(enrollButton);
        waitForPageToLoad();
        return this;
    }

}
