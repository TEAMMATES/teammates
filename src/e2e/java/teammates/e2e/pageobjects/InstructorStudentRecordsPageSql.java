package teammates.e2e.pageobjects;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import teammates.storage.sqlentity.Student;

/**
 * Page Object Model for instructor student records page.
 */
public class InstructorStudentRecordsPageSql extends AppPage {

    @FindBy(id = "records-header")
    private WebElement headerText;

    public InstructorStudentRecordsPageSql(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("'s Records");
    }

    public void verifyIsCorrectPage(String courseId, String studentName) {
        String expected = String.format("%s's Records - %s", studentName, courseId);
        assertEquals(expected, headerText.getText());
    }

    public void verifyStudentDetails(Student student) {
        verifyIsCorrectPage(student.getCourse().getId(), student.getName());
    }

}
