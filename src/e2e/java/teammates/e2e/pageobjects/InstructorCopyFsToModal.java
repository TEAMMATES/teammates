package teammates.e2e.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import teammates.common.util.Const;

import java.util.List;

import static org.junit.Assert.assertEquals;

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
        return !browser.driver.findElements(By.className("modal-body")).isEmpty();
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
     * Populates the fields of the form by using the provided name, and selecting the last course shown.
     * @param newFsName feedback session name of the new session
     */
    public void fillFormWithLastCourseSelected(String newFsName) {
        WebElement fscopyModalBody = browser.driver.findElement(By.className("modal-body"));
        List<WebElement> coursesCheckBoxes = fscopyModalBody.findElements(By.name("copySessionChooseCourse"));
        for (WebElement e : coursesCheckBoxes) {
            markCheckBoxAsChecked(e);
        }

        WebElement fsNameInput = fscopyModalBody.findElement(By.id("copied-fsname"));
        fillTextBox(fsNameInput, newFsName);
    }

    /**
     * Returns true if the status message modal is visible.
     */
    // TODO: can be removed
    public boolean isFormSubmissionStatusMessageVisible() {
        return copyModalStatusMessage.isDisplayed();
    }

    // TODO: can be removed
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
        return browser.driver.findElement(By.className("alert alert-danger")).getText();
    }

    public WebElement getSubmitButton() {
        return browser.driver.findElement(By.id("fscopy-submit"));
    }

    public void clickSubmitButton() {
        WebElement fsCopySubmitButton = getSubmitButton();
        click(fsCopySubmitButton);
    }

    public void clickCancelButton() {
        // need to monitor if there is any problem (#fsCopyModal)
        WebElement cancelButton = browser.driver.findElement(By.id("fscopy-cancel"));
        clickDismissModalButtonAndWaitForModalHidden(cancelButton);
    }

    /**
     * Waits for the error message indicating that the loading of the form modal has failed.
     * this may not be applicable as it requires changes in the home page
     */
    public void waitForModalLoadingError() {
        waitForElementPresence(By.className(".alert.alert-danger"));
    }
}
