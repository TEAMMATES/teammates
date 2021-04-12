package teammates.e2e.pageobjects;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.util.StringHelper;

/**
 * Page Object Model for student course details page.
 */
public class StudentCourseDetailsPage extends AppPage {

    @FindBy(id = "course-name")
    private WebElement courseNameField;

    @FindBy(id = "course-id")
    private WebElement courseIdField;

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

    public void verifyCourseDetails(CourseAttributes courseDetails) {
        assertEquals(courseDetails.getName(), courseNameField.getText());
        assertEquals(courseDetails.getId(), courseIdField.getText());
    }

    public void verifyInstructorsDetails(InstructorAttributes[] instructorDetails) {
        String[] actualInstructors = instructorsList.getText().split(System.lineSeparator());
        for (int i = 0; i < instructorDetails.length; i++) {
            InstructorAttributes expected = instructorDetails[i];
            assertEquals(expected.displayedName + ": " + expected.name + " (" + expected.email + ")",
                    actualInstructors[i]);
        }
    }

    public void verifyStudentDetails(StudentAttributes studentDetails) {
        assertEquals(studentDetails.getName(), studentNameField.getText());
        assertEquals(studentDetails.getSection(), studentSectionField.getText());
        assertEquals(studentDetails.getTeam(), studentTeamField.getText());
        assertEquals(studentDetails.getEmail(), studentEmailField.getText());
    }

    private String convertGender(StudentProfileAttributes.Gender gender) {
        switch (gender) {
        case MALE:
            return "Male";
        case FEMALE:
            return "Female";
        default:
            return "Not Specified";
        }
    }

    public void verifyTeammatesDetails(StudentAttributes[] teammates, StudentProfileAttributes[] teammateProfiles) {
        int numTables = teammateProfiles.length;

        for (int i = 0; i < numTables; i++) {
            List<String> profileItems = new ArrayList<>();
            profileItems.add("Name: " + teammates[i].getName());
            profileItems.add("Email: " + teammates[i].getEmail());
            profileItems.add("Gender: " + convertGender(teammateProfiles[i].getGender()));
            if (!StringHelper.isEmpty(teammateProfiles[i].getNationality())) {
                profileItems.add("Nationality: " + teammateProfiles[i].getNationality());
            }
            if (!StringHelper.isEmpty(teammateProfiles[i].getInstitute())) {
                profileItems.add("Institute: " + teammateProfiles[i].getInstitute());
            }

            WebElement actualProfile = browser.driver.findElement(By.id("teammates-details-" + i));
            assertEquals(profileItems.stream().collect(Collectors.joining(System.lineSeparator())), actualProfile.getText());
        }
    }

    public void sortTeammatesByName() {
        click(browser.driver.findElement(By.id("sort-name")));
    }

}
