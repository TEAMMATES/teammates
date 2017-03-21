package teammates.test.pageobjects;

import java.io.IOException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * InstructorEditStudentFeedbackPage is a page object to represent the page seen by a instructor
 * when moderating feedback.
 *
 * <p>It inherits the methods and attributes from {@link FeedbackSubmitPage} as the page is
 * built upon the feedback submission page seen by students/instructors.
 *
 * @see FeedbackSubmitPage
 */
public class InstructorEditStudentFeedbackPage extends FeedbackSubmitPage {

    @FindBy(id = "moderationHintButton")
    private WebElement moderationHintButton;

    @FindBy(id = "moderationHint")
    private WebElement moderationHint;

    public InstructorEditStudentFeedbackPage(Browser browser) {
        super(browser);
    }

    public void clickModerationHintButton() {
        click(moderationHintButton);
    }

    public boolean isModerationHintVisible() {
        return moderationHint.isDisplayed();
    }

    public void verifyModerationHeaderHtml(String filePathParam) throws IOException {
        verifyHtmlPart(By.className("navbar"), filePathParam);
    }

    public String getModerationHintButtonText() {
        return moderationHintButton.getText();
    }

}
