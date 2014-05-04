package teammates.test.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class InstructorCoursesDetailsPage extends AppPage {

    public InstructorCoursesDetailsPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("Course Details");
    }
    
    public int getStudentCountForCourse(String courseId) {
        WebElement cellWithStudentCount = browser.driver.findElement(By.id("total_students"));
        return Integer.parseInt(cellWithStudentCount.getText());
    }

}
