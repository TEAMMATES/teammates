package teammates.test.pageobjects;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class InstructorCourseEnrollResultPage extends AppPage {

    @FindBy(id = "edit_enroll")
    private WebElement editLink;

    public InstructorCourseEnrollResultPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        // Intentional check for opening h1 and not closing h1 because the following content is not static
        return getPageSource().contains("<h1>Enrollment Results for");
    }

    public InstructorCourseEnrollPage clickEditLink() {
        click(editLink);
        waitForPageToLoad();
        return changePageType(InstructorCourseEnrollPage.class);
    }

}
