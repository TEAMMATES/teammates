package teammates.e2e.pageobjects;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import teammates.storage.sqlentity.Student;

/**
 * Represents the instructor course student details view page of the website.
 */
public class InstructorCourseStudentDetailsViewPageSql extends AppPage {
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

    public InstructorCourseStudentDetailsViewPageSql(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("Enrollment Details");
    }

    public void verifyIsCorrectPage(String expectedCourseId, String expectedStudentEmail) {
        verifyDetail(expectedCourseId, courseId.getText());
        verifyDetail(expectedStudentEmail, studentOfficialEmail.getText());
    }

    public void verifyStudentDetails(Student student) {
        verifyDetail(student.getName(), studentName.getText());

        verifyDetail(student.getCourseId(), courseId.getText());
        verifyDetail(student.getSection().getName(), studentSectionName.getText());
        verifyDetail(student.getTeam().getName(), studentTeamName.getText());
        verifyDetail(student.getEmail(), studentOfficialEmail.getText());
        verifyDetail(student.getComments(), studentComments.getText());
    }

    private void verifyDetail(String expected, String detailField) {
        if (expected.isEmpty()) {
            assertEquals(NOT_SPECIFIED_LABEL, detailField);
        } else {
            assertEquals(expected, detailField);
        }
    }

}
