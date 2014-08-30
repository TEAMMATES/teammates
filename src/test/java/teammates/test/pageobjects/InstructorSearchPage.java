package teammates.test.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class InstructorSearchPage extends AppPage {
    
    public InstructorSearchPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("<h1>Search</h1>");
    }
    
    public void inputSearchContent(String content) {
        WebElement inputBox = this.getSearchBox();
        inputBox.sendKeys(content);
    }
    
    public void clearSearchBox() {
        WebElement inputBox = this.getSearchBox();
        inputBox.clear();
    }
    
    public void clickSearchButton() {
        this.getSearchButton().click();
        this.waitForPageToLoad();
    }
    
    public void clickStudentCommentCheckBox() {
        this.getStudentCommentCheckBox().click();
    }
    
    public void clickFeedbackResponseCommentCheckBox() {
        this.getFeedbackResponseCommentCheckBox().click();
    }
    
    public void clickStudentCheckBox() {
        this.getStudentCheckBox().click();
    }
    
    private WebElement getSearchBox() {
        return browser.driver.findElement(By.id("searchBox"));
    }
    
    private WebElement getSearchButton() {
        return browser.driver.findElement(By.id("buttonSearch"));
    }
    
    private WebElement getStudentCommentCheckBox() {
        return browser.driver.findElement(By.id("comments-for-student-check"));
    }
    
    private WebElement getFeedbackResponseCommentCheckBox() {
        return browser.driver.findElement(By.id("comments-for-responses-check"));
    }
    
    private WebElement getStudentCheckBox() {
        return browser.driver.findElement(By.id("students-check"));
    }

    public void clickAndHoverPicture(String cellId) {
        browser.driver.findElement(By.id(cellId)).findElement(By.tagName("a")).click();
    }

}
