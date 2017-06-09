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

    public void editFeedbackResponseComment(String newCommentText) {
        waitForAjaxLoaderGifToDisappear();
        executeScript("scroll(0, 300)"); // if the element is on bottom.
        browser.driver.findElement(By.id("commentedit-1-1-1-1-GRQ")).click();
        WebElement commentEditForm = browser.driver.findElement(By.id("responseCommentEditForm-1-1-1-1-GRQ"));
        fillRichTextEditor("responsecommenttext-1-1-1-1-GRQ", newCommentText);
        commentEditForm.findElement(By.className("col-sm-offset-5")).findElement(By.tagName("a")).click();
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

    public void verifyCommentRowContent(String commentText, String giverName) {
        waitForAjaxLoaderGifToDisappear();
        WebElement commentRowSelector = browser.driver.findElement(By.id("responseCommentRow-1-1-1-1-GRQ"));
        try {
            WebElement commentTextElement = commentRowSelector.findElement(By.id("plainCommentText-1-1-1-1-GRQ"));
            assertTrue(commentTextElement.findElement(By.tagName("p")).getText().equals(commentText));
        } catch (NoSuchElementException e) {
            waitForTextContainedInElementPresence(By.id("plainCommentText-1-1-1-1-GRQ"), commentText);
        }
        assertTrue(commentRowSelector.findElement(By.className("text-muted")).getText().contains(giverName));
    }

    public void verifyCommentFormErrorMessage(String errorMessage) {
        WebElement errorMessageSpan = waitForElementPresence(By.cssSelector("#errorMessage"));
        assertEquals(errorMessage, errorMessageSpan.getText());
    }

    public void deleteFeedbackResponseComment(String commentIdSuffix) {
        WebElement commentRow = browser.driver.findElement(By.id("responseCommentRow-" + commentIdSuffix));
        click(commentRow.findElement(By.tagName("form")).findElement(By.tagName("a")));
        waitForConfirmationModalAndClickOk();
        ThreadHelper.waitFor(1500);
    }

    public void verifyRowMissing(String rowIdSuffix) {
        try {
            waitForAjaxLoaderGifToDisappear();
            browser.driver.findElement(By.id("responseCommentRow-" + rowIdSuffix));
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
