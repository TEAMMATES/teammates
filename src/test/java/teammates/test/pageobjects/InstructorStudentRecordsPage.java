package teammates.test.pageobjects;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.fail;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import teammates.common.util.ThreadHelper;

public class InstructorStudentRecordsPage extends AppPage {

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

    public void closeEditFeedbackResponseCommentForm(String commentIdSuffix) {
        WebElement editResponseForm = browser.driver.findElement(By.id("responseCommentEditForm" + commentIdSuffix));
        WebElement cancelButton = editResponseForm.findElement(By.cssSelector("input[type='button']"));
        click(cancelButton);
    }

    public void editFeedbackResponseComment(String commentIdSuffix, String newCommentText) {
        WebElement commentRow = waitForElementPresence(By.id("responseCommentRow" + commentIdSuffix));
        click(commentRow.findElements(By.tagName("a")).get(1));
        WebElement commentEditForm = browser.driver.findElement(By.id("responseCommentEditForm" + commentIdSuffix));
        fillRichTextEditor("responsecommenttext" + commentIdSuffix, newCommentText);
        click(commentEditForm.findElement(By.className("col-sm-offset-5")).findElement(By.tagName("a")));
        ThreadHelper.waitFor(1000);
    }

    public List<WebElement> getStudentFeedbackPanels() {
        List<WebElement> webElements = new ArrayList<>();
        for (WebElement e : browser.driver.findElements(By.cssSelector("div[id^='studentFeedback-']"))) {
            WebElement panel = e.findElement(By.cssSelector(".panel-collapse"));
            if (panel != null) {
                webElements.add(panel);
            }
        }

        return webElements;
    }

    public void verifyCommentRowContent(String commentRowIdSuffix, String commentText, String giverName) {
        By commentRowSelector = By.id("responseCommentRow" + commentRowIdSuffix);
        WebElement commentRow = waitForElementPresence(commentRowSelector);
        waitForTextContainedInElementPresence(By.id("plainCommentText" + commentRowIdSuffix), commentText);
        assertTrue(commentRow.findElement(By.className("text-muted")).getText().contains(giverName)
                || commentRow.findElement(By.className("text-muted")).getText().contains("you"));
    }

    public void verifyCommentFormErrorMessage(String errorMessage) {
        WebElement errorMessageSpan = waitForElementPresence(By.cssSelector("#errorMessage"));
        assertEquals(errorMessage, errorMessageSpan.getText());
    }

    public void deleteFeedbackResponseComment(String commentIdSuffix) {
        WebElement commentRow = browser.driver.findElement(By.id("responseCommentRow" + commentIdSuffix));
        click(commentRow.findElement(By.tagName("form")).findElement(By.tagName("a")));
        waitForConfirmationModalAndClickOk();
        ThreadHelper.waitFor(1500);
    }

    public void verifyRowMissing(String rowIdSuffix) {
        try {
            waitForAjaxLoaderGifToDisappear();
            browser.driver.findElement(By.id("responseCommentRow" + rowIdSuffix));
            fail("Row expected to be missing found.");
        } catch (NoSuchElementException expected) {
            // row expected to be missing
            return;
        }
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
