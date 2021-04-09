package teammates.e2e.pageobjects;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import teammates.common.util.ThreadHelper;

public class StudentFeedbackSubmissionPage extends AppPage {

    @FindBy(css = "tm-loading-retry")
    private WebElement topLevelComponent;

    public StudentFeedbackSubmissionPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageTitle().contains("Submit Feedback");
    }

    public void populateResponse() {
        List<WebElement> questions = topLevelComponent.findElements(By.tagName("tm-question-submission-form"));
        for (WebElement question: questions) {
            try {
                WebElement textQuestion = question.findElement(By.tagName("tm-text-question-instruction"));
                fillTextBox(textQuestion.findElement(By.tagName("textarea")), "Test text");
            } catch (Exception e) {

            }
        }
    }

    public void submit() {
        WebElement submitButton = topLevelComponent.findElement(By.id("btn-submit"));
        click(submitButton);
        ThreadHelper.waitFor(3000);
    }

}
