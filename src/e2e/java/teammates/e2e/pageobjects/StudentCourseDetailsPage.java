package teammates.e2e.pageobjects;

import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import teammates.e2e.util.TestProperties;
import teammates.storage.entity.Course;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;

/**
 * Page Object Model for student course details page.
 */
public class StudentCourseDetailsPage extends AppPage {

    @FindBy(id = "course-name")
    private WebElement courseNameField;

    @FindBy(id = "course-id")
    private WebElement courseIdField;

    @FindBy(id = "course-institute")
    private WebElement courseInstituteField;

    @FindBy(id = "instructors")
    private WebElement instructorsList;

    @FindBy(id = "student-name")
    private WebElement studentNameField;

    @FindBy(id = "student-section")
    private WebElement studentSectionField;

    @FindBy(id = "student-team")
    private WebElement studentTeamField;

    @FindBy(id = "student-email")
    private WebElement studentEmailField;

    public StudentCourseDetailsPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return waitForElementPresence(By.tagName("h1")).getText().matches("Team Details for .+");
    }

    public void verifyCourseDetails(Course courseDetails) {
        Assertions.assertEquals(courseDetails.getName(), courseNameField.getText());
        Assertions.assertEquals(courseDetails.getId(), courseIdField.getText());
        Assertions.assertEquals(courseDetails.getInstitute(), courseInstituteField.getText());
    }

    public void verifyInstructorsDetails(Instructor[] instructorDetails) {
        String[] actualInstructors = instructorsList.getText().split(TestProperties.LINE_SEPARATOR);
        for (int i = 0; i < instructorDetails.length; i++) {
            Instructor expected = instructorDetails[i];
            Assertions.assertEquals(expected.getDisplayName() + ": " + expected.getName() + " (" + expected.getEmail() + ")",
                    actualInstructors[i]);
        }
    }

    public void verifyStudentDetails(Student studentDetails) {
        Assertions.assertEquals(studentDetails.getName(), studentNameField.getText());
        Assertions.assertEquals(studentDetails.getSectionName(), studentSectionField.getText());
        Assertions.assertEquals(studentDetails.getTeamName(), studentTeamField.getText());
        Assertions.assertEquals(studentDetails.getEmail(), studentEmailField.getText());
    }

    public void verifyTeammatesDetails(Student[] teammates) {
        int numTables = teammates.length;

        for (int i = 0; i < numTables; i++) {
            List<String> profileItems = new ArrayList<>();
            profileItems.add("Name: " + teammates[i].getName());
            profileItems.add("Email: " + teammates[i].getEmail());

            WebElement actualProfile = browser.driver.findElement(By.id("teammates-details-" + i));
            Assertions.assertEquals(profileItems.stream().collect(Collectors.joining(TestProperties.LINE_SEPARATOR)),
                    actualProfile.getText());
        }
    }

    public void sortTeammatesByName() {
        click(browser.driver.findElement(By.id("sort-name")));
    }

}
