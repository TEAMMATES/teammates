package teammates.test.pageobjects;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class InstructorCourseStudentDetailsEditPage extends AppPage {

    @FindBy (id = "studentname")
    private WebElement studentNameTextbox;

    @FindBy (id = "teamname")
    private WebElement teamNameTextbox;

    @FindBy (id = "newstudentemail")
    private WebElement studentEmailTextbox;

    @FindBy (id = "studentemail")
    private WebElement studentEmailTextboxOriginal;

    @FindBy (id = "comments")
    private WebElement commentsTextbox;

    @FindBy (id = "button_submit")
    private WebElement submitButton;

    public InstructorCourseStudentDetailsEditPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("<h1>Edit Student Details</h1>");
    }

    /**
     * If the parameter value is not null, the value will be filled into the
     * relevent input filed.
     */
    public InstructorCourseDetailsPage submitSuccessfully(String studentName, String teamName,
                                                          String studentEmail, String comments) {
        fillStudentDetailsForm(studentName, teamName, studentEmail, comments);
        return changePageType(InstructorCourseDetailsPage.class);
    }

    /**
     * If the parameter value is not null, the value will be filled into the
     * relevent input field.
     */
    public InstructorCourseStudentDetailsEditPage submitUnsuccessfully(
            String studentName, String teamName, String studentEmail, String comments) {
        fillStudentDetailsForm(studentName, teamName, studentEmail, comments);
        return this;
    }

    /**
     * If the parameter value is not null, the value will be filled into the
     * relevant input field.
     */
    private void fillStudentDetailsForm(String studentName, String teamName, String studentEmail, String comments) {
        if (studentName != null) {
            fillTextBox(studentNameTextbox, studentName);
        }
        if (teamName != null) {
            fillTextBox(teamNameTextbox, teamName);
        }
        if (studentEmail != null) {
            fillTextBox(studentEmailTextbox, studentEmail);
        }
        if (comments != null) {
            fillTextBox(commentsTextbox, comments);
        }
        if (teamName == null) {
            if (studentEmail == null) {
                click(submitButton);
            } else {
                clickAndConfirm(submitButton);
            }

        } else {
            // if team name is edited, the confirmation dialog will pop up
            clickAndConfirmWithoutWaitingForModalDisappearance(submitButton);
            if (studentEmail != null) {
                clickNoOnModal();
            }
        }
    }

    public void verifyIsCorrectPage(String email) {
        assertTrue(containsExpectedPageContents());
        assertEquals(email, studentEmailTextboxOriginal.getAttribute("value"));
    }

    public InstructorCourseDetailsPage submitButtonClicked() {
        click(submitButton);
        return changePageType(InstructorCourseDetailsPage.class);
    }
}
