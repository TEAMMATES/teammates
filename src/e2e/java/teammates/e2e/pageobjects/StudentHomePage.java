package teammates.e2e.pageobjects;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Page Object Model for student home page.
 */
public class StudentHomePage extends AppPage {

    @FindBy(linkText = "View team")
    private WebElement studentViewTeamBtn;

    public StudentHomePage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageTitle().equals("Student Home");
    }

    /**
     * Equivalent of clicking the 'View team' button on course panel.
     * @return the loaded page
     */
    public StudentCourseDetailsPage loadStudentCourseDetails() {
        click(studentViewTeamBtn);
        waitForPageToLoad();
        return changePageType(StudentCourseDetailsPage.class);
    }

}
