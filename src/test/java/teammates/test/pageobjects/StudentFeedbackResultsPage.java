package teammates.test.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class StudentFeedbackResultsPage extends AppPage {

    public StudentFeedbackResultsPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("<h1>Feedback Results - Student</h1>");
    }
    
    public boolean clickQuestionAdditionalInfoButton(int qnNumber, String additionalInfoId){
        WebElement qnAdditionalInfoButton = browser.driver.findElement(By.id("questionAdditionalInfoButton-" + qnNumber + "-" + additionalInfoId));    
        qnAdditionalInfoButton.click();
        // Check if links toggle properly.
        WebElement qnAdditionalInfo = browser.driver.findElement(By.id("questionAdditionalInfo-" + qnNumber + "-" + additionalInfoId));    
        return qnAdditionalInfo.isDisplayed();
    }
    
    public String getQuestionAdditionalInfoButtonText(int qnNumber, String additionalInfoId){
        WebElement qnAdditionalInfoButton = browser.driver.findElement(By.id("questionAdditionalInfoButton-" + qnNumber + "-" + additionalInfoId));    
        return qnAdditionalInfoButton.getText();
    }
}
