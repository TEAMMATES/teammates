package teammates.test.pageobjects;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import teammates.common.util.Const;

/**
 * Utility class for handling the modal for copying a feedback session to other courses 
 *
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
    
    public void fillFormWithAllCoursesSelected(String newFsName) {
        WebElement fsCopyModal = browser.driver.findElement(By.id("fsCopyModal"));
        List<WebElement> coursesCheckBoxes = fsCopyModal.findElements(By.name(Const.ParamsNames.COPIED_COURSES_ID));
        for (WebElement e : coursesCheckBoxes) {
            markCheckBoxAsChecked(e);
        }
        
        WebElement fsNameInput = fsCopyModal.findElement(By.id(Const.ParamsNames.COPIED_FEEDBACK_SESSION_NAME));
        fillTextBox(fsNameInput, newFsName);
    }
    
    public void resetCoursesCheckbox() {
        WebElement fsCopyModal = browser.driver.findElement(By.id("fsCopyModal"));
        List<WebElement> coursesCheckBoxes = fsCopyModal.findElements(By.name(Const.ParamsNames.COPIED_COURSES_ID));
        for (WebElement e : coursesCheckBoxes) {
            markCheckBoxAsUnchecked(e);
        }
    }
    
    public void fillFsName(String fsName) {
        WebElement fsCopyModal = browser.driver.findElement(By.id("fsCopyModal"));
        WebElement fsNameInput = fsCopyModal.findElement(By.id(Const.ParamsNames.COPIED_FEEDBACK_SESSION_NAME));
        fillTextBox(fsNameInput, fsName);
    }
    
    public void checkCourse(String courseId) {
        WebElement fsCopyModal = browser.driver.findElement(By.id("fsCopyModal"));
        WebElement courseCheckBox = 
                fsCopyModal.findElement(
                        By.xpath("//input[@name='copiedcoursesid' and @value='" + courseId + "']"));
        
        assertNotNull(courseCheckBox);
        markCheckBoxAsChecked(courseCheckBox);
    }
    
    public boolean isErrorMessageVisible() {
        WebElement copyModalErrorMessage = browser.driver.findElement(By.id("fs_copy_modal_error"));
        return copyModalErrorMessage.isDisplayed();
    }
    
    public void verifyErrorMessage() {
        String copyErrorMessage = getFsCopyModalError();
        String expectedErrorMessage = "Please give the feedback session a different name if the " 
                                    + "destination course and the source course are the same";
        assertEquals(expectedErrorMessage, copyErrorMessage);
    }
    
    private String getFsCopyModalError() {
        WebElement copyModalErrorMessage = browser.driver.findElement(By.id("fs_copy_modal_error"));
        return copyModalErrorMessage.getText();
    }

    public void clickSubmitButton() {
        WebElement fsCopySubmitButton = browser.driver.findElement(By.id("fscopy_submit"));
        
        fsCopySubmitButton.click();
    }

    /**
     * On InstructorHome and InstructorFeedbacks, this clicks on the 'copy' button on the 
     * table that displays the feedback sessions
     * @param courseId
     * @param feedbackSessionName
     */
    public void clickCopyButtonOnTable(String courseId, String feedbackSessionName) {
        By fsCopyButtonElement = By.id("button_fscopy" + "-" + courseId + "-" + feedbackSessionName);
        
        // give it some time to load as it is loaded via AJAX
        waitForElementPresence(fsCopyButtonElement);
        
        WebElement fsCopyButton = browser.driver.findElement(fsCopyButtonElement);
        
        fsCopyButton.click();
    }
}
