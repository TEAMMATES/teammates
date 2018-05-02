package teammates.test.pageobjects;

import static org.testng.AssertJUnit.assertEquals;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import teammates.common.util.Const;

/**
 * Page Object class for handling the modal for copying a feedback session to multiple courses.
 */
public class InstructorCopyFsToModal extends AppPage {

    private static final String FEEDBACK_COPY_MODAL_STATUS = "feedback-copy-modal-status";
    public WebElement copyModalStatusMessage;

    public InstructorCopyFsToModal(Browser browser) {
        super(browser);
    }

    /**
     * Returns true if the modal for copying feedback sessions to multiple courses,
     *         identified by its html id, is present, otherwise false.
     */
    public static boolean isPresentOnPage(Browser browser) {
        return !browser.driver.findElements(By.id("fsCopyModal")).isEmpty();
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("Copy this feedback session to other courses");
    }

    public void waitForModalToLoad() {
        By byCopiedFsNameField = By.id(Const.ParamsNames.COPIED_FEEDBACK_SESSION_NAME);
        waitForElementPresence(byCopiedFsNameField);
        waitForElementVisibility(browser.driver.findElement(byCopiedFsNameField));

        copyModalStatusMessage = browser.driver.findElement(By.id(FEEDBACK_COPY_MODAL_STATUS));
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
     * Unchecks every course in the course list.
     */
    public void resetCoursesCheckboxes() {
        WebElement fsCopyModal = browser.driver.findElement(By.id("fsCopyModal"));
        List<WebElement> coursesCheckBoxes = fsCopyModal.findElements(By.name(Const.ParamsNames.COPIED_COURSES_ID));
        for (WebElement e : coursesCheckBoxes) {
            markCheckBoxAsUnchecked(e);
        }
    }

    /**
     * Returns true if the status message modal is visible.
     */
    public boolean isFormSubmissionStatusMessageVisible() {
        return copyModalStatusMessage.isDisplayed();
    }

    public void waitForFormSubmissionErrorMessagePresence() {
        waitForElementPresence(By.cssSelector("#" + FEEDBACK_COPY_MODAL_STATUS + ".alert-danger"));
    }

    /**
     * Verifies that the status message on the copy modal is the {@code expectedStatusMessage}.
     */
    public void verifyStatusMessage(String expectedStatusMessage) {
        assertEquals(expectedStatusMessage, getFsCopyStatus());
    }

    private String getFsCopyStatus() {
        return copyModalStatusMessage.getText();
    }

    public void clickSubmitButton() {
        WebElement fsCopySubmitButton = browser.driver.findElement(By.id("fscopy_submit"));
        click(fsCopySubmitButton);
    }

    public void clickCloseButton() {
        WebElement closeButton = browser.driver.findElement(By.cssSelector("#fsCopyModal .close"));
        clickDismissModalButtonAndWaitForModalHidden(closeButton);
    }

    /**
     * Waits for the error message indicating that the loading of the form modal has failed.
     */
    public void waitForModalLoadingError() {
        waitForElementPresence(By.id("fs-copy-modal-error"));
    }
}
