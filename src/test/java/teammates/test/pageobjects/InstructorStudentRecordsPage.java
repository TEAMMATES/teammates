package teammates.test.pageobjects;

import static org.testng.AssertJUnit.assertTrue;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class InstructorStudentRecordsPage extends AppPage {

    @FindBy(id = "button_add_comment")
    private WebElement addCommentLink;

    @FindBy(id = "button_save_comment")
    private WebElement saveCommentLink;

    @FindBy(id = "commentText")
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

    public InstructorStudentRecordsPage addComment(String commentText) {
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

    public InstructorStudentRecordsPage editComment(int id, String comment) {
        getCommentEditLink(id).click();
        getCommentTextBox(id).clear();
        getCommentTextBox(id).sendKeys(comment);
        getCommentSaveLink(id).click();
        waitForPageToLoad();
        return this;
    }

    public boolean verifyAddCommentButtonClick() {
        addCommentLink.click();
        boolean display = commentTextBox.isDisplayed()
                       && saveCommentLink.isDisplayed()
                       && !addCommentLink.isDisplayed();
        return display;
    }

    public boolean verifyEditCommentButtonClick(int id) {
        getCommentEditLink(id).click();
        boolean display = getCommentTextBox(id).isEnabled()
                       && getCommentSaveLink(id).isDisplayed()
                       && !getCommentEditLink(id).isDisplayed();
        return display;
    }

    private WebElement getCommentEditLink(int id) {
        return browser.driver.findElement(By.id("commentedit-" + id));
    }

    private WebElement getCommentDeleteLink(int id) {
        return browser.driver.findElement(By.id("commentdelete-" + id));
    }

    private WebElement getCommentTextBox(int id) {
        return browser.driver.findElement(By.id("commentText" + id));
    }

    private WebElement getCommentSaveLink(int id) {
        return browser.driver.findElement(By.id("commentsave-" + id));
    }

    public void clickFeedbackSession(int id) throws Exception {
        browser.driver.findElement(By.id("studentFeedback-" + id)).click();
        waitForPageToLoad();
        try {
            waitForElementToAppear(By.xpath("//*[@id=\"target-feedback-" + id + "\"]/div[1]/div[1]"));
        } catch (StaleElementReferenceException e) {
            ;// do nothing
        }
    }

}
