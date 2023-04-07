package teammates.e2e.pageobjects;

import static org.junit.Assert.assertEquals;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import teammates.common.datatransfer.attributes.StudentAttributes;

/**
 * Represents the instructor course student details view page of the website.
 */
public class InstructorCourseStudentDetailsViewPage extends AppPage {
    private static final String NOT_SPECIFIED_LABEL = "Not Specified";

    @FindBy (id = "student-name")
    private WebElement studentName;

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

    public InstructorCourseStudentDetailsViewPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("Enrollment Details");
    }

    public void verifyIsCorrectPage(String expectedCourseId, String expectedStudentEmail) {
        verifyDetail(expectedCourseId, courseId);
        verifyDetail(expectedStudentEmail, studentOfficialEmail);
    }

    public void verifyStudentDetails(StudentAttributes student) {
        verifyDetail(student.getName(), studentName);

        verifyDetail(student.getCourse(), courseId);
        verifyDetail(student.getSection(), studentSectionName);
        verifyDetail(student.getTeam(), studentTeamName);
        verifyDetail(student.getEmail(), studentOfficialEmail);
        verifyDetail(student.getComments(), studentComments);
    }

    private void verifyDetail(String expected, WebElement detailField) {
        if (expected.isEmpty()) {
            assertEquals(NOT_SPECIFIED_LABEL, detailField.getText());
        } else {
            assertEquals(expected, detailField.getText());
        }
    }

}
