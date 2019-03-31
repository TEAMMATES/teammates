package teammates.e2e.pageobjects;

import org.openqa.selenium.By;

/**
 * Page Object Model for student course details page.
 */
public class StudentCourseDetailsPage extends AppPage {

    public StudentCourseDetailsPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return browser.driver.findElement(By.tagName("h4")).getText().equals("Course");
    }

}
