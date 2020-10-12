package teammates.e2e.pageobjects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.attributes.StudentProfileAttributes;

/**
 * Represents the instructor course student details view page of the website.
 */
public class InstructorCourseStudentDetailsViewPage extends AppPage {
    @FindBy (id = "student-name")
    private WebElement studentName;

    @FindBy (id = "short-name")
    private WebElement studentShortName;

    @FindBy (id = "gender")
    private WebElement studentGender;

    @FindBy (id = "personal-email")
    private WebElement studentPersonalEmail;

    @FindBy (id = "institution")
    private WebElement studentInstitution;

    @FindBy (id = "nationality")
    private WebElement studentNationality;

    @FindBy (id = "course-id")
    private WebElement courseId;

    @FindBy (id = "section-name")
    private WebElement studentSectionName;

    @FindBy (id = "team-name")
    private WebElement studentTeamName;

    @FindBy (id = "email")
    private WebElement studentOfficialEmail;

    @FindBy (id = "comments")
    private WebElement studentComments;

    @FindBy (id = "more-info")
    private WebElement moreInformation;

    public InstructorCourseStudentDetailsViewPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("Enrollment Details");
    }

    public void verifyStudentDetails(StudentProfileAttributes studentProfile, StudentAttributes student) {
        verifyDetail(student.getName(), studentName);

        StudentProfileAttributes profileToTest = studentProfile;
        if (studentProfile == null) {
            profileToTest = StudentProfileAttributes.builder(student.getGoogleId()).build();
        }
        verifyDetail(profileToTest.getShortName(), studentShortName);
        verifyStudentGender(profileToTest.getGender());
        verifyDetail(profileToTest.getEmail(), studentPersonalEmail);
        verifyDetail(profileToTest.getInstitute(), studentInstitution);
        verifyDetail(profileToTest.getNationality(), studentNationality);

        verifyDetail(student.getCourse(), courseId);
        verifyDetail(student.getSection(), studentSectionName);
        verifyDetail(student.getTeam(), studentTeamName);
        verifyDetail(student.getEmail(), studentOfficialEmail);
        verifyDetail(student.getComments(), studentComments);

        verifyDetail(profileToTest.getMoreInfo(), moreInformation);
    }

    private void verifyDetail(String expected, WebElement detailField) {
        if (expected.isEmpty()) {
            assertTrue(isElementPresent(By.id("not-specified")));
        } else {
            assertEquals(expected, detailField.getText());
        }
    }

    private void verifyStudentGender(StudentProfileAttributes.Gender expected) {
        if (expected.equals(StudentProfileAttributes.Gender.OTHER)) {
            assertTrue(isElementPresent(By.id("not-specified")));
        } else {
            assertEquals(expected.toString(), studentGender.getText());
        }
    }
}
