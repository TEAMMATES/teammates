package teammates.test.pageobjects;

import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import teammates.common.util.Const;

/**
 * Page Object class for handling the modal for copying a feedback session multiple times to other courses.
 */
public class InstructorCopyFsToModal extends AppPage {
    
    public InstructorCopyFsToModal(Browser browser) {
        super(browser);
    }
    
    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("Copy this feedback session to other courses");
    }
    
    public void waitForModalToLoad() {
        waitForElementPresence(By.id(Const.ParamsNames.COPIED_FEEDBACK_SESSION_NAME));
    }
    
    /**
     * Populates the fields of the form by using the provided name, and selecting every course. 
     * @param newFsName feedback session name of the new session
     */
    public void fillFormWithAllCoursesSelected(String newFsName) {
        WebElement fsCopyModal = browser.driver.findElement(By.id("fsCopyModal"));
        List<WebElement> coursesCheckBoxes = fsCopyModal.findElements(By.name(Const.ParamsNames.COPIED_COURSES_ID));
        for (WebElement e : coursesCheckBoxes) {
            markCheckBoxAsChecked(e);
        }
        
        WebElement fsNameInput = fsCopyModal.findElement(By.id(Const.ParamsNames.COPIED_FEEDBACK_SESSION_NAME));
        fillTextBox(fsNameInput, newFsName);
    }
    
    /**
     * Unchecks every course in the course list
     */
    public void resetCoursesCheckboxes() {
        WebElement fsCopyModal = browser.driver.findElement(By.id("fsCopyModal"));
        List<WebElement> coursesCheckBoxes = fsCopyModal.findElements(By.name(Const.ParamsNames.COPIED_COURSES_ID));
        for (WebElement e : coursesCheckBoxes) {
            markCheckBoxAsUnchecked(e);
        }
    }
    
    /**
     * @return true if the status message modal is visible.
     */
    public boolean isFormSubmissionStatusMessageVisible() {
        WebElement copyModalErrorMessage = browser.driver.findElement(By.id("feedback-copy-modal-status"));
        return copyModalErrorMessage.isDisplayed();
    }
    
    public void waitForFormSubmissionStatusMessageVisibility() {
        WebElement statusMessage = browser.driver.findElement(
                                        By.id("feedback-copy-modal-status"));
        waitForElementVisibility(statusMessage);
    }
    
    /**
     * Verifies that the status message on the copy modal is the {@code expectedStatusMessage}
     * @param expectedStatusMessage
     */
    public void verifyStatusMessage(String expectedStatusMessage) {
        assertEquals(expectedStatusMessage, getFsCopyStatus());
    }
    
    private String getFsCopyStatus() {
        WebElement copyModalStatusMessage = browser.driver.findElement(By.id("feedback-copy-modal-status"));
        return copyModalStatusMessage.getText();
    }
    
    /**
     * Verifies that the status message modal contains the html classes for styling error messages
     */
    public void verifyStatusContainsErrorHtmlClasses() {
        String htmlClassesOfModalStatus = getFsCopyModalStatusHtmlClass();
        assertTrue("Expected status message to be an error, but css class was" + htmlClassesOfModalStatus,
                   htmlClassesOfModalStatus.contains("alert-danger"));
    }
    
    private String getFsCopyModalStatusHtmlClass() {
        WebElement copyModalStatusMessage = browser.driver.findElement(By.id("feedback-copy-modal-status"));
        return copyModalStatusMessage.getAttribute("class");
    }

    public void clickSubmitButton() {
        WebElement fsCopySubmitButton = browser.driver.findElement(By.id("fscopy_submit"));
        fsCopySubmitButton.click();
    }

    /**
     * Clicks on the 'copy' button on the table that displays the feedback sessions
     */
    public void clickCopyButtonOnTable(String courseId, String feedbackSessionName) {
        By fsCopyButtonElement = By.id("button_fscopy" + "-" + courseId + "-" + feedbackSessionName);
        
        // give it some time to load as it is loaded via AJAX
        waitForElementPresence(fsCopyButtonElement);
        
        WebElement fsCopyButton = browser.driver.findElement(fsCopyButtonElement);
        
        fsCopyButton.click();
    }

    /**
     * Waits for the error message indicating that the loading of the form modal has failed
     */
    public void waitForModalLoadingError() {
        waitForElementPresence(By.id("fs-copy-modal-error"));
    }
}