package teammates.test.pageobjects;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.fail;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;

import teammates.common.util.Const;
import teammates.common.util.ThreadHelper;

public class InstructorFeedbackResultsPage extends AppPage {

    @FindBy(id = "button_sortFrom")
    private WebElement sortTableGiverButton;
    
    @FindBy(id = "button_sortTo")
    private WebElement sortTableRecipientButton;
    
    @FindBy(id = "button_sortFeedback")
    private WebElement sortTableAnswerButton;
    
    @FindBy(id = "button_add_comment")
    private WebElement showResponseCommentAddFormButton;
    
    @FindBy(id = "showResponseCommentAddForm-1-1-1")
    private WebElement addResponseCommentForm;
    
    
    public InstructorFeedbackResultsPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("<h1>Feedback Results - Instructor</h1>");
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
    
    public void displayByGiver() {
        Select select = new Select(browser.driver.findElement(By.name(Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE)));
        select.selectByVisibleText("Group by - Giver > Recipient > Question");
        waitForPageToLoad();
    }
    
    public void displayByRecipient() {
        Select select = new Select(browser.driver.findElement(By.name(Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE)));
        select.selectByVisibleText("Group by - Recipient > Giver > Question");
        waitForPageToLoad();
    }
    
    public void displayByQuestion() {
        Select select = new Select(browser.driver.findElement(By.name(Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE)));
        select.selectByVisibleText("Group by - Question");
        waitForPageToLoad();
    }
    
    public AppPage sortTableByGiver() {
        sortTableGiverButton.click();
        return this;
    }
    
    public AppPage sortTableByRecipient() {
        sortTableRecipientButton.click();
        return this;
    }
    
    public AppPage sortTableByAnswer() {
        sortTableAnswerButton.click();
        return this;
    }
    
    public InstructorFeedbackEditPage clickEditLink() {
        WebElement button = browser.driver.findElement(By.linkText("[Edit]"));
        button.click();
        waitForPageToLoad();
        return changePageType(InstructorFeedbackEditPage.class);
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
    
    public void addFeedbackResponseComment(String commentText) {
        showResponseCommentAddFormButton.click();
        fillTextBox(addResponseCommentForm.findElement(By.tagName("textarea")), commentText);
        addResponseCommentForm.findElement(By.tagName("a")).click();
        ThreadHelper.waitFor(1000);
    }
    
    public void editFeedbackResponseComment(String commentIdSuffix, String newCommentText) {
        WebElement commentRow = browser.driver.findElement(By.id("responseCommentRow" + commentIdSuffix));
        commentRow.findElements(By.tagName("a")).get(1).click();
        
        WebElement commentEditForm = browser.driver.findElement(By.id("responseCommentEditForm" + commentIdSuffix));
        fillTextBox(commentEditForm.findElement(By.name("responsecommenttext")), newCommentText);
        commentEditForm.findElement(By.tagName("a")).click();
        ThreadHelper.waitFor(1000);
    }
    
    public void deleteFeedbackResponseComment(String commentIdSuffix) {
        WebElement commentRow = browser.driver.findElement(By.id("responseCommentRow" + commentIdSuffix));
        commentRow.findElement(By.tagName("form"))
            .findElement(By.tagName("a")).click();
        ThreadHelper.waitFor(1000);
    }
    
    public void verifyCommentRowContent(String commentRowIdSuffix, String commentText, String giverName) {
        WebElement commentRow = browser.driver.findElement(By.id("responseCommentRow" + commentRowIdSuffix));
        assertEquals(commentText, commentRow.findElement(By.id("plainCommentText" + commentRowIdSuffix)).getText());
        assertTrue(commentRow.findElement(By.className("text-muted")).getText().contains(giverName));
    }
    
    public void verifyCommentFormErrorMessage(String commentTableIdSuffix, String errorMessage) {
        WebElement commentRow = browser.driver.findElement(By.id("responseCommentTable" + commentTableIdSuffix));
        assertEquals(errorMessage, commentRow.findElement(By.tagName("span")).getText());
    }
    
    public void verifyRowMissing(String rowIdSuffix) {
        try {
            verifyCommentRowContent(rowIdSuffix, "", "");
            fail("Row expected to be missing found.");
        } catch (NoSuchElementException e) {
            // row expected to be missing
        }
    }
}
