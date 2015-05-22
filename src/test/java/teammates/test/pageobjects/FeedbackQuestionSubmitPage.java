package teammates.test.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import teammates.common.util.Const;

public class FeedbackQuestionSubmitPage extends AppPage {

    public FeedbackQuestionSubmitPage(Browser browser) {
        super(browser);
    }
    
    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("<h1>Submit Feedback Question</h1>");
    }
    
    public String getCourseId() {
        return browser.driver.findElement(By.name("courseid")).getAttribute("value");
    }
    
    public String getFeedbackSessionName() {
        return browser.driver.findElement(By.name("fsname")).getAttribute("value");
    }
    
    public boolean isCorrectPage (String courseId, String feedbackSessionName) {
        boolean isCorrectCourseId = this.getCourseId().equals(courseId);
        boolean isCorrectFeedbackSessionName = this.getFeedbackSessionName().equals(feedbackSessionName);
        return isCorrectCourseId && isCorrectFeedbackSessionName && containsExpectedPageContents();
    }

    public void fillResponseTextBox(int questionNumber, int responseNumber, String text) {
        WebElement element = browser.driver.findElement(
                By.name(Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-" + questionNumber + "-" + responseNumber));
        fillTextBox(element, text);
    }
    
    public void clickSubmitButton() {
        getSubmitButton().click();
    }
    
    public WebElement getSubmitButton() {
        WebElement button = browser.driver.findElement(By.id("response_submit_button"));
        return button;
    }
    
    public WebElement getTextArea(int questionNum, int responseNum) {
        String textAreaName = "responsetext-" + questionNum + "-" + responseNum; 
        WebElement textArea = browser.driver.findElement(By.name(textAreaName));     
        return textArea;
    }   
    
    public void clickRubricCell(int respIndex, int row, int col) {
        int qnIndex = 1;
        WebElement radio = browser.driver.findElement(By.id(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_CHOICE + "-" + qnIndex + "-" + respIndex + "-" + row + "-" + col));
        // Gets the parent element.
        WebElement cell = radio.findElement(By.xpath(".."));
        cell.click();
    }
}
