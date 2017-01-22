package teammates.test.pageobjects;

import java.io.IOException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class InstructorEditStudentFeedbackPage extends FeedbackSubmitPage {
    
    @FindBy(id = "moderationHintButton")
    protected WebElement moderationHintButton;
    
    @FindBy(id = "moderationHint")
    protected WebElement moderationHint;
       
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
