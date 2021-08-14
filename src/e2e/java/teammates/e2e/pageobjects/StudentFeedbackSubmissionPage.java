package teammates.e2e.pageobjects;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import teammates.test.ThreadHelper;

/**
 * Represents the student feedback submission page of the website.
 */
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
        for (WebElement question : questions) {
            WebElement textQuestion = question.findElement(By.tagName("tm-text-question-instruction"));
            textQuestion
                    .findElements(By.tagName("textarea"))
                    .forEach(textBox -> fillTextBox(textBox, "Response"));
        }
    }

    public void submit() {
        WebElement submitButton = topLevelComponent.findElement(By.id("btn-submit"));
        click(submitButton);
        ThreadHelper.waitFor(3000);
    }

}
