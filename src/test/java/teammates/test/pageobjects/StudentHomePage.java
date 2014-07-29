package teammates.test.pageobjects;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class StudentHomePage extends AppPage {

    @FindBy(id = "button_join_course")
    protected WebElement joinButton;

    public StudentHomePage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return containsExpectedPageContents(getPageSource());
    }

    public static boolean containsExpectedPageContents(String pageSource) {
        return pageSource.contains("<h2>Student Home</h2>");
    }

    public StudentHelpPage clickHelpLink() {
        studentHelpTab.click();
        waitForPageToLoad();
        switchToNewWindow();
        return changePageType(StudentHelpPage.class);
    }

    public void clickHomeTab() {
        studentHomeTab.click();
        waitForPageToLoad();

    }

    public void clickViewTeam() {

        List<WebElement> viewTeamLinks = browser.driver.findElements(By.linkText("View Team"));

        viewTeamLinks.get(0).click();
    }

    public WebElement getViewEvalResultsButton(String EvalOrFeedbackName) {
        
        int rowId = getEvalRowId(EvalOrFeedbackName);       
        WebElement button = browser.driver.findElement(By.id("viewEvaluationResults"+rowId));
        return button;
    }
    
    public WebElement getEditEvalButton(String EvalOrFeedbackName) {

        int rowId = getEvalRowId(EvalOrFeedbackName);        
        WebElement button = browser.driver.findElement(By.id("editEvaluationSubmission"+rowId));
        return button;
    }
    
    public WebElement getSubmitEvalButton(String EvalOrFeedbackName) {

        int rowId = getEvalRowId(EvalOrFeedbackName);        
        WebElement button = browser.driver.findElement(By.id("submitEvaluation"+rowId));
        return button;
    }
    
    public WebElement getViewFeedbackButton(String EvalOrFeedbackName) {
        
        int rowId = getEvalRowId(EvalOrFeedbackName);       
        WebElement button = browser.driver.findElement(By.id("viewFeedbackResults"+rowId));
        return button;
    }

    public WebElement getEditFeedbackButton(String EvalOrFeedbackName) {
    
        int rowId = getEvalRowId(EvalOrFeedbackName);       
        WebElement button = browser.driver.findElement(By.id("editFeedbackResponses"+rowId));
        return button;
    }
    
    public WebElement getSubmitFeedbackButton(String EvalOrFeedbackName) {
        
        int rowId = getEvalRowId(EvalOrFeedbackName);       
        WebElement button = browser.driver.findElement(By.id("submitFeedback"+rowId));
        return button;
    }
     
    
    private int getEvalRowId(String name) {
        
        int id = 0;
        while (isElementPresent(By.id("evaluation" + id))) {

            WebElement element = browser.driver.findElement(By.id("evaluation" + id));
            WebElement text = element.findElement(By.tagName("td"));

            if(text.getText().contains(name)){
            return id;
            }
            
            id++;
        }
        return -1;
    }

}
