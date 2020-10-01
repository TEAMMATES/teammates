package teammates.e2e.pageobjects;

import static org.junit.Assert.assertEquals;

import org.openqa.selenium.By;

/**
 * Represents the instructor search page.
 */
public class InstructorSearchPage extends AppPage {

    public InstructorSearchPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageTitle().contains("Search");
    }

    public void verifyNumCoursesInStudentResults(int expectedNum) {
        assertEquals(expectedNum, getNumCoursesInStudentResults());
    }

    private int getNumCoursesInStudentResults() {
        return browser.driver.findElements(By.id("student-course-table")).size();
    }
}
