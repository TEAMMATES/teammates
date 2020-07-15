package teammates.e2e.pageobjects;

import static org.junit.Assert.assertEquals;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.attributes.StudentProfileAttributes;

/**
 * Page Object Model for student course details page.
 */
public class StudentCourseDetailsPage extends AppPage {

    @FindBy(id = "course-name")
    private WebElement courseNameField;

    @FindBy(id = "course-id")
    private WebElement courseIdField;

    @FindBy(id = "time-zone")
    private WebElement timeZoneField;

    @FindBy(id = "created-date")
    private WebElement createdDateField;

    @FindBy(id = "instructor-table")
    private WebElement instructorTable;

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
        return waitForElementPresence(By.tagName("h4")).getText().equals("Course");
    }

    public void verifyCourseDetails(CourseAttributes courseDetails) {
        String expectedCreatedDate = courseDetails.getCreatedAtFullDateTimeString() + " " + courseDetails.getTimeZone();

        assertEquals(courseDetails.getName(), courseNameField.getText());
        assertEquals(courseDetails.getId(), courseIdField.getText());
        assertEquals(courseDetails.getTimeZone().toString(), timeZoneField.getText());
        assertEquals(expectedCreatedDate, createdDateField.getText());
    }

    public void verifyInstructorsDetails(InstructorAttributes[] instructorDetails) {
        String[][] tableDetails = new String[instructorDetails.length][2];
        for (int i = 0; i < instructorDetails.length; i++) {
            tableDetails[i][0] = instructorDetails[i].getName();
            tableDetails[i][1] = instructorDetails[i].getEmail();
        }
        verifyTableBodyValues(instructorTable, tableDetails);
    }

    public void verifyStudentDetails(StudentAttributes studentDetails) {
        assertEquals(studentDetails.getName(), studentNameField.getText());
        assertEquals(studentDetails.getSection(), studentSectionField.getText());
        assertEquals(studentDetails.getTeam(), studentTeamField.getText());
        assertEquals(studentDetails.getEmail(), studentEmailField.getText());
    }

    public void verifyTeammatesDetails(StudentAttributes[] teammates, StudentProfileAttributes[] teammateProfiles) {
        int numTables = teammateProfiles.length;

        for (int i = 0; i < numTables; i++) {
            String[][] tableDetails = new String[5][2];
            tableDetails[0][0] = "Name";
            tableDetails[0][1] = teammates[i].getName();
            tableDetails[1][0] = "Email";
            tableDetails[1][1] = teammateProfiles[i].getEmail();
            tableDetails[2][0] = "Gender";
            tableDetails[2][1] = teammateProfiles[i].getGender().toString();
            tableDetails[3][0] = "Institution";
            tableDetails[3][1] = teammateProfiles[i].getInstitute();
            tableDetails[4][0] = "Nationality";
            tableDetails[4][1] = teammateProfiles[i].getNationality();
            verifyTableBodyValues(browser.driver.findElement(By.id("teammates-table-" + i)), tableDetails);
        }
    }

    public void sortTeammatesByName() {
        click(browser.driver.findElement(By.id("sort-name")));
    }

}
