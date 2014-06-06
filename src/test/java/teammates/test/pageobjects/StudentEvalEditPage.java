package teammates.test.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import teammates.common.datatransfer.SubmissionAttributes;
import teammates.common.util.Const;

public class StudentEvalEditPage extends AppPage {
    
    @FindBy(id = "button_submit")
    private WebElement submitButton;

    public StudentEvalEditPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("<h1>Evaluation Submission</h1>");
    }
    
    public void fillSubmissionValues(int receiverId, SubmissionAttributes s){
        fillSubmissionValues(receiverId, s.points, s.justification.getValue(), s.p2pFeedback.getValue());
    }
    
    public void clearSubmittedData(int receiverId){
        setPoints(receiverId, -101);
        setJustification(receiverId, "");
        setComments(receiverId, ""); 
    }
    
    public StudentHomePage submit() {
        submitButton.click();
        waitForPageToLoad();
        return changePageType(StudentHomePage.class);
    }
    
    public StudentEvalEditPage submitUnsuccessfully() {
        submitButton.click();
        waitForPageToLoad();
        return this;
    }

    private void fillSubmissionValues(int receiverIdx, int points, String justification, String p2pComments){
        setPoints(receiverIdx, points);
        setJustification(receiverIdx, justification);
        setComments(receiverIdx, p2pComments);
    }
    
    private void setPoints(int rowId, int points) {
        browser.selenium.select("id=" + Const.ParamsNames.POINTS + rowId, "value="+points);
    }
    
    
    private void setJustification(int rowId, String justification) {
        WebElement element = browser.driver.findElement(By.id(Const.ParamsNames.JUSTIFICATION + rowId));
        fillTextBox(element, justification);
    }
    
    private void setComments(int rowId, String comments) {
        WebElement element = browser.driver.findElement(By.id(Const.ParamsNames.COMMENTS + rowId));
        fillTextBox(element, comments);
    }
    

}
