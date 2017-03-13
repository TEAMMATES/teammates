package teammates.test.pageobjects;

import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class InstructorStudentRecordsPage extends AppPage {

    @FindBy(id = "button_add_comment")
    private WebElement addCommentLink;

    @FindBy(id = "button_save_comment")
    private WebElement saveCommentLink;

    @FindBy(id = "commenttext")
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
        click(addCommentLink);
        waitForRichTextEditorToLoad("commenttext");
        click(commentTextBox);
        fillRichTextEditor(commentTextBox.getAttribute("id"), commentText);
        click(saveCommentLink);
        waitForPageToLoad();
        return this;
    }

    public void addCommentWithVisibility(String commentText, int id) {
        click(addCommentLink);
        waitForRichTextEditorToLoad("commenttext");
        click(commentTextBox);
        fillRichTextEditor(commentTextBox.getAttribute("id"), commentText);
        click(getVisibilityToggleLink(id));
        clickAllCheckboxes(id);
        click(saveCommentLink);
        waitForPageToLoad();
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
        click(getCommentEditLink(id));
        WebElement commentTextBox = getCommentTextBox(id);
        waitForRichTextEditorToLoad("commentText" + id);
        click(commentTextBox);
        fillRichTextEditor(commentTextBox.getAttribute("id"), comment);
        click(getCommentSaveLink(id));
        waitForPageToLoad();
        return this;
    }

    public void clickAllCheckboxes(int id) {
        List<WebElement> answerCheckboxes = browser.driver
                                            .findElement(By.id("visibility-options" + id))
                                            .findElements(By.className("answerCheckbox"));
        for (WebElement checkbox : answerCheckboxes) {
            click(checkbox);
        }
    }

    public boolean verifyAddCommentButtonClick() {
        click(addCommentLink);
        return commentTextBox.isDisplayed()
                && saveCommentLink.isDisplayed()
                && !addCommentLink.isDisplayed();
    }

    public boolean verifyEditCommentButtonClick(int id) {
        click(getCommentEditLink(id));
        return getCommentTextBox(id).isEnabled()
                && getCommentSaveLink(id).isDisplayed()
                && !getCommentEditLink(id).isDisplayed();
    }

    public void clickEditCommentAndCancel(int id) {
        click(getCommentEditLink(id));
        click(getCommentEditCancelLink(id));
    }

    public void verifyCommentEditBoxNotVisible(int id) {
        assertFalse(isElementVisible(By.id("commentTextEdit" + id)));
    }

    private WebElement getCommentEditLink(int id) {
        return browser.driver.findElement(By.id("commentedit-" + id));
    }

    private WebElement getCommentEditCancelLink(int id) {
        return browser.driver.findElement(By.id("commentsave-" + id))
                             .findElement(By.xpath("./following-sibling::input"));
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

    private WebElement getVisibilityToggleLink(int id) {
        return browser.driver.findElement(By.id("visibility-options-trigger" + id));
    }

    // Visibility options

    public void clickVisibilityOptionsButton(int id) {
        click(getVisibilityOptions(id));
    }

    public void clickAnswerCheckboxForCourse(int id) {
        click(getAnswerCheckboxForCourse(id));
    }

    public void clickGiverCheckboxForCourse(int id) {
        click(getGiverCheckboxForCourse(id));
    }

    public void clickRecipientCheckboxForCourse(int id) {
        click(getRecipientCheckboxForCourse(id));
    }

    public boolean isAnswerCheckboxForCourseSelected(int id) {
        return getAnswerCheckboxForCourse(id).isSelected();
    }

    public boolean isGiverCheckboxForCourseSelected(int id) {
        return getGiverCheckboxForCourse(id).isSelected();
    }

    public boolean isRecipientCheckboxForCourseSelected(int id) {
        return getRecipientCheckboxForCourse(id).isSelected();
    }

    private WebElement getVisibilityOptions(int id) {
        return browser.driver.findElement(By.id("visibility-options-trigger" + id));
    }

    private WebElement getCourseVisibilityRow(int id) {
        return browser.driver.findElement(By.id("recipient-course" + id));
    }

    private WebElement getAnswerCheckboxForCourse(int id) {
        return getCourseVisibilityRow(id).findElement(By.className("answerCheckbox"));
    }

    private WebElement getGiverCheckboxForCourse(int id) {
        return getCourseVisibilityRow(id).findElement(By.className("giverCheckbox"));
    }

    private WebElement getRecipientCheckboxForCourse(int id) {
        return getCourseVisibilityRow(id).findElement(By.className("recipientCheckbox"));
    }

    /**
     * Clicks all the headings of the record panels to either expand/collapse the panel body.
     */
    public void clickAllRecordPanelHeadings() {
        for (WebElement e : browser.driver.findElements(By.cssSelector("div[id^='studentFeedback-']"))) {
            click(e.findElement(By.cssSelector(".panel-heading")));
        }
    }

    /**
     * Checks if the body of all the record panels are visible.
     * @return true if all record panel bodies are visible
     */
    public boolean areRecordsVisible() {
        return areAllRecordPanelBodiesVisibilityEquals(true);
    }

    /**
     * Checks if the body of all the record panels are hidden.
     * @return true if all record panel bodies are hidden
     */
    public boolean areRecordsHidden() {
        return areAllRecordPanelBodiesVisibilityEquals(false);
    }

    /**
     * Checks if the bodies of all the record panels are collapsed or expanded.
     * @param isVisible true to check for expanded, false to check for collapsed.
     * @return true if all record panel bodies are equals to the visibility being checked.
     */
    private boolean areAllRecordPanelBodiesVisibilityEquals(boolean isVisible) {
        for (WebElement e : getStudentFeedbackPanels()) {
            if (e.isDisplayed() != isVisible) {
                return false;
            }
        }

        return true;
    }

    public List<WebElement> getStudentFeedbackPanels() {
        List<WebElement> webElements = new ArrayList<WebElement>();
        for (WebElement e : browser.driver.findElements(By.cssSelector("div[id^='studentFeedback-']"))) {
            WebElement panel = e.findElement(By.cssSelector(".panel-collapse"));
            if (panel != null) {
                webElements.add(panel);
            }
        }

        return webElements;
    }

    /**
     * Waits for all the panels to collapse.
     */
    public void waitForPanelsToCollapse() {
        waitForElementsToDisappear(getStudentFeedbackPanels());
    }

    /**
     * Waits for all the panels to expand.
     */
    public void waitForPanelsToExpand() {
        waitForElementsVisibility(getStudentFeedbackPanels());
    }

}
