package teammates.test.pageobjects;

import static org.testng.AssertJUnit.assertTrue;
import org.openqa.selenium.By;

public class AdminAccountDetailsPage extends AppPage {

    public AdminAccountDetailsPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("<h1>Instructor Account Details</h1>");
    }

    public AdminAccountDetailsPage clickRemoveInstructorFromCourse(String courseId) {
        this.waitForElementPresence(By.id("instructor_"+courseId));
        browser.driver.findElement(By.id("instructor_"+courseId)).click();
        waitForPageToLoad();
        return this;
    }
    
    public AdminAccountDetailsPage clickRemoveStudentFromCourse(String courseId) {
        browser.driver.findElement(By.id("student_"+courseId)).click();
        waitForPageToLoad();
        return this;
    }

    public void verifyIsCorrectPage(String instructorId) {
        assertTrue(containsExpectedPageContents());
        assertTrue(getPageSource().contains("<p class=\"form-control-static\">"+instructorId+"</p>"));
    }

}
