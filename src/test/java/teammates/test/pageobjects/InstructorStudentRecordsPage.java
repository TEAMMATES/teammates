package teammates.test.pageobjects;

import static org.testng.AssertJUnit.assertTrue;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;


public class InstructorStudentRecordsPage extends AppPage {
    
    @FindBy (id = "button_add_comment")
    private WebElement addCommentLink;
    
    @FindBy (id = "button_save_comment")
    private WebElement saveCommentLink;
    
    @FindBy (id = "commentText")
    private WebElement commentTextBox;
    
    public InstructorStudentRecordsPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        String source = getPageSource();
        return source.contains("'s Records") && source.contains("<small class=\"muted\"> - ");
    }
    
    public void verifyBelongsToStudent(String name) {
        assertTrue(getPageSource().contains(name));
    }
    
    public void verifyIsCorrectPage(String studentName) {
        assertTrue(containsExpectedPageContents());
        verifyBelongsToStudent(studentName);
    }
    
    public InstructorEvalSubmissionEditPage clickEvalEditLink(String evalName){
        int rowId = getEvalRowId(evalName);
        getEvalEditLink(rowId).click();
        waitForPageToLoad();
        return changePageType(InstructorEvalSubmissionEditPage.class);
    }
    
    public InstructorStudentRecordsPage addComment(String commentText){
        addCommentLink.click();
        commentTextBox.sendKeys(commentText);
        saveCommentLink.click();
        waitForPageToLoad();
        return this;
    }
    
    public InstructorStudentRecordsPage clickDeleteCommentAndCancel(int id) {
        clickAndCancel(getCommentDeleteLink(id));
        waitForPageToLoad();
        return this;
    }
    
    public InstructorStudentRecordsPage clickDeleteCommentAndConfirm(int id) {
        clickAndConfirm(getCommentDeleteLink(id));
        waitForPageToLoad();
        return this;
    }
    
    public InstructorStudentRecordsPage editComment(int id, String comment){
        getCommentEditLink(id).click();
        getCommentTextBox(id).clear();
        getCommentTextBox(id).sendKeys(comment);
        getCommentSaveLink(id).click();
        waitForPageToLoad();
        return this;
    }
    
    public boolean verifyAddCommentButtonClick(){
        addCommentLink.click();
        boolean display = commentTextBox.isDisplayed()
                && saveCommentLink.isDisplayed()
                && !addCommentLink.isDisplayed();
        return display;
    }
    
    public boolean verifyEditCommentButtonClick(int id){
        getCommentEditLink(id).click();
        boolean display = getCommentTextBox(id).isEnabled()
                && getCommentSaveLink(id).isDisplayed()
                && !getCommentEditLink(id).isDisplayed();
        return display;
    }
    
    private int getEvalRowId(String evalName) {
        int evalCount = browser.driver.findElements(By.xpath("//div[contains(@class, 'student_eval')]")).size();
        for (int i = 0; i < evalCount; i++) {
            String evalNameInRow = getEvalNameInRow(i);
            if (evalNameInRow.equals("Evaluation Name: " + evalName)) {
                return i;
            }
        }
        return -1;
    }
    
    private String getEvalNameInRow(int rowId) {
        String xpath = "//div[@id='studentEval-" + rowId + "']"
                + "//div//h2";
        return browser.driver.findElement(By.xpath(xpath)).getText();
    }
    
    private WebElement getEvalEditLink(int rowId) {
        return browser.driver.findElement(By.id("button_edit-" + rowId));
    }
    
    private WebElement getCommentEditLink(int id) {
        return browser.driver.findElement(By.id("commentedit-" + id));
    }
    
    private WebElement getCommentDeleteLink(int id) {
        return browser.driver.findElement(By.id("commentdelete-" + id));
    }
    
    private WebElement getCommentTextBox(int id){
        return browser.driver.findElement(By.id("commentText" + id));
    }
    
    private WebElement getCommentSaveLink(int id){
        return browser.driver.findElement(By.id("commentsave-" + id));
    }

}
