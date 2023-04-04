package teammates.e2e.pageobjects;

import static org.junit.Assert.assertEquals;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import teammates.common.datatransfer.attributes.StudentAttributes;

/**
 * Represents the instructor course student details edit page of the website.
 */
public class InstructorCourseStudentDetailsEditPage extends AppPage {

    @FindBy (id = "course-id")
    private WebElement courseId;

    @FindBy (id = "student-name")
    private WebElement studentNameTextbox;

    @FindBy (id = "section-name")
    private WebElement sectionNameTextbox;

    @FindBy (id = "team-name")
    private WebElement teamNameTextbox;

    @FindBy (id = "new-student-email")
    private WebElement studentEmailTextbox;

    @FindBy (id = "comments")
    private WebElement commentsTextbox;

    @FindBy (id = "btn-submit")
    private WebElement submitButton;

    public InstructorCourseStudentDetailsEditPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageTitle().contains("Edit Student Details");
    }

    public void verifyIsCorrectPage(String expectedCourseId, String expectedStudentEmail) {
        assertEquals(expectedCourseId, courseId.getText());
        assertEquals(expectedStudentEmail, studentEmailTextbox.getAttribute("value"));
    }

    public void verifyStudentDetails(StudentAttributes studentDetails) {
        assertEquals(studentDetails.getCourse(), courseId.getText());
        assertEquals(studentDetails.getName(), studentNameTextbox.getAttribute("value"));
        if (studentDetails.getSection() == null) {
            assertEquals("None", sectionNameTextbox.getAttribute("value"));
        } else {
            assertEquals(studentDetails.getSection(), sectionNameTextbox.getAttribute("value"));
        }
        assertEquals(studentDetails.getTeam(), teamNameTextbox.getAttribute("value"));
        assertEquals(studentDetails.getEmail(), studentEmailTextbox.getAttribute("value"));
        if (studentDetails.getComments() != null) {
            assertEquals(studentDetails.getComments(), commentsTextbox.getAttribute("value"));
        }
    }

    public void editStudentDetails(StudentAttributes newStudentDetails) {
        fillTextBox(studentNameTextbox, newStudentDetails.getName());
        fillTextBox(sectionNameTextbox, newStudentDetails.getSection());
        fillTextBox(teamNameTextbox, newStudentDetails.getTeam());
        if (newStudentDetails.getComments() != null) {
            fillTextBox(commentsTextbox, newStudentDetails.getComments());
        }
        clickAndConfirm(submitButton);
    }

    public void editStudentEmailAndResendLinks(String newEmail) {
        fillTextBox(studentEmailTextbox, newEmail);
        click(submitButton);
        click(waitForElementPresence(By.id("btn-resend-links")));
    }
}
