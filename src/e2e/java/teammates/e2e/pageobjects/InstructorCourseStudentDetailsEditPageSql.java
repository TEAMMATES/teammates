package teammates.e2e.pageobjects;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import teammates.storage.sqlentity.Student;

/**
 * Represents the instructor course student details edit page of the website.
 */
public class InstructorCourseStudentDetailsEditPageSql extends AppPage {

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

    public InstructorCourseStudentDetailsEditPageSql(Browser browser) {
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

    public void verifyStudentDetails(Student student) {
        assertEquals(student.getCourseId(), courseId.getText());
        assertEquals(student.getName(), studentNameTextbox.getAttribute("value"));
        if (student.getSection() == null) {
            assertEquals("None", sectionNameTextbox.getAttribute("value"));
        } else {
            assertEquals(student.getSectionName(), sectionNameTextbox.getAttribute("value"));
        }
        assertEquals(student.getTeamName(), teamNameTextbox.getAttribute("value"));
        assertEquals(student.getEmail(), studentEmailTextbox.getAttribute("value"));
        if (student.getComments() != null) {
            assertEquals(student.getComments(), commentsTextbox.getAttribute("value"));
        }
    }

    public void editStudentDetails(Student newStudent) {
        fillTextBox(studentNameTextbox, newStudent.getName());
        fillTextBox(sectionNameTextbox, newStudent.getSectionName());
        fillTextBox(teamNameTextbox, newStudent.getTeamName());
        if (newStudent.getComments() != null) {
            fillTextBox(commentsTextbox, newStudent.getComments());
        }
        clickAndConfirm(submitButton);
    }

    public void editStudentEmailAndResendLinks(String newEmail) {
        fillTextBox(studentEmailTextbox, newEmail);
        click(submitButton);
        click(waitForElementPresence(By.id("btn-resend-links")));
    }
}
