package teammates.test.pageobjects;

import java.io.IOException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class InstructorEditStudentFeedbackPage extends FeedbackSubmitPage {

    public InstructorEditStudentFeedbackPage(Browser browser) {
        super(browser);
    }
    
    public void clickModerationHintButton() {
        click(By.id("moderationHintButton"));
    }
    
    public boolean isModerationHintVisible() {
        return isElementVisible("moderationHint");
    }
    
    public void verifyModerationHeaderHtml(String filePathParam) throws IOException {
        verifyHtmlPart(By.className("navbar"), filePathParam);
    }
    
    public String getModerationHintButtonText() {
        WebElement moderationHintButton = browser.driver.findElement(By.id("moderationHintButton"));
        return moderationHintButton.getText();
    }

}
