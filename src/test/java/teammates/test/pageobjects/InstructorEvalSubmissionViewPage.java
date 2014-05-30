package teammates.test.pageobjects;

import static org.testng.AssertJUnit.assertTrue;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class InstructorEvalSubmissionViewPage extends AppPage {
    
    @FindBy (id = "button_edit")
    WebElement editButton;

    public InstructorEvalSubmissionViewPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("<h1>View Student's Evaluation</h1>");
    }
    
    public InstructorEvalSubmissionEditPage clickEditButton() {
        editButton.click();
        waitForPageToLoad();
        return changePageType(InstructorEvalSubmissionEditPage.class);
    }

    public void verifyIsCorrectPage(String studentName) {
        assertTrue(containsExpectedPageContents());
        assertTrue(getPageSource().contains("<h1>View Student's Evaluation</h1>"));
    }

}
