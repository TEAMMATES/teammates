package teammates.test.pageobjects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class InstructorCourseStudentDetailsViewPage extends AppPage {

    @FindBy (id = "studentemail")
    private WebElement studentEmail;

    public InstructorCourseStudentDetailsViewPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("Enrollment Details");
    }

    public void verifyIsCorrectPage(String email) {
        assertTrue(containsExpectedPageContents());
        assertEquals(email, studentEmail.getText());
    }

}
