package teammates.test.pageobjects;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class StudentHomePage extends AppPage {

    @FindBy(id = "button-join-course")
    protected WebElement joinButton;

    public StudentHomePage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return containsExpectedPageContents(getPageSource());
    }

    public static boolean containsExpectedPageContents(String pageSource) {
        return pageSource.contains("<h1>Student Home</h1>");
    }

    public void clickViewTeam() {

        List<WebElement> viewTeamLinks = browser.driver.findElements(By.linkText("View Team"));

        viewTeamLinks.get(0).click();
    }
    
    public WebElement getViewFeedbackButton(String feedbackName) {
        
        int rowId = getEvalRowId(feedbackName);
        return browser.driver.findElement(By.id("viewFeedbackResults" + rowId));
    }

    public WebElement getEditFeedbackButton(String feedbackName) {
    
        int rowId = getEvalRowId(feedbackName);
        return browser.driver.findElement(By.id("editFeedbackResponses" + rowId));
    }
    
    public WebElement getSubmitFeedbackButton(String feedbackName) {
        
        int rowId = getEvalRowId(feedbackName);
        return browser.driver.findElement(By.id("submitFeedback" + rowId));
    }

    private int getEvalRowId(String name) {
        
        int id = 0;
        while (isElementPresent(By.id("evaluation" + id))) {

            WebElement element = browser.driver.findElement(By.id("evaluation" + id));
            WebElement text = element.findElement(By.tagName("td"));

            if (text.getText().contains(name)) {
                return id;
            }
            
            id++;
        }
        return -1;
    }

}
