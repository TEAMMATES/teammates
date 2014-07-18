package teammates.test.pageobjects;

import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.assertEquals;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import teammates.common.datatransfer.SubmissionAttributes;
import teammates.common.util.Const;

public class InstructorEvalSubmissionEditPage extends AppPage {
    
    @FindBy (id = "button_submit")
    WebElement submitButton;
    
    @FindBy (id = "courseid")
    WebElement courseIdField;
    
    @FindBy (id = "evaluationname")
    WebElement evaluationNameField;

    public InstructorEvalSubmissionEditPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("<h1>Edit Student's Submission</h1>");
    }

    public void setValuesForSubmission(int rowId, SubmissionAttributes newSubmissionValues) {
        setPointsForSubmission(rowId, newSubmissionValues.points);
        setJustificationForSubmission(rowId, newSubmissionValues.justification.getValue());
        setP2pCommentForSubmission(rowId, newSubmissionValues.p2pFeedback.getValue());
    }
    
    public InstructorFeedbacksPage submit() {
        submitButton.click();
        waitForPageToLoad();
        return changePageType(InstructorFeedbacksPage.class);
    }

    
    private int getRowIdForReviewee(String studentName) {
        int max = browser.driver.findElements(By.className("reportHeader")).size();
        for (int i = 0; i < max; i++) {
            if (browser.driver.findElement(By.id("sectiontitle" + i)).getText()
                    .toUpperCase().contains(studentName.toUpperCase())) {
                return i;
            }
        }
        return -1;
    }
    
    private void setPointsForSubmission(int rowId, int points) {
        browser.selenium.select("id=" + Const.ParamsNames.POINTS + rowId, "value="+points);
    }


    private void setJustificationForSubmission(int rowId, String justification) {
        WebElement textBox = browser.driver.findElement(By.id(Const.ParamsNames.JUSTIFICATION + rowId));
        fillTextBox(textBox, justification);
    }

    private void setP2pCommentForSubmission(int rowId, String comments) {
        WebElement textBox = browser.driver.findElement(By.id(Const.ParamsNames.COMMENTS + rowId));
        fillTextBox(textBox, comments);
    }

    public void verifyIsCorrectPage(String courseId, String evalName, String studentName) {
        assertEquals(courseId, courseIdField.getAttribute("value"));
        assertEquals(evalName, evaluationNameField.getAttribute("value"));
        assertTrue(getPageSource().contains(studentName+"'s evaluation submission"));
        
    }

}
