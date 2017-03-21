package teammates.test.pageobjects;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

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
        return getPageSource().contains("<h1>Student Details</h1>");
    }

    public void verifyIsCorrectPage(String email) {
        assertTrue(containsExpectedPageContents());
        assertEquals(email, studentEmail.getText());
    }

}
