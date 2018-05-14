package teammates.test.pageobjects;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import teammates.common.util.Const;
import teammates.common.util.SanitizationHelper;

public class FeedbackSubmitPage extends AppPage {

    public FeedbackSubmitPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("<h1>Submit Feedback</h1>");
    }

    public String getCourseId() {
        return browser.driver.findElement(By.name("courseid")).getAttribute("value");
    }

    public String getFeedbackSessionName() {
        return browser.driver.findElement(By.name("fsname")).getAttribute("value");
    }

    public boolean isCorrectPage(String courseId, String feedbackSessionName) {
        boolean isCorrectCourseId = this.getCourseId().equals(courseId);
        boolean isCorrectFeedbackSessionName = this.getFeedbackSessionName().equals(feedbackSessionName);
        return isCorrectCourseId && isCorrectFeedbackSessionName && containsExpectedPageContents();
    }

    public void selectRecipient(int qnNumber, int responseNumber, String recipientName) {
        Select selectElement = new Select(browser.driver.findElement(
                By.name(Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-" + qnNumber + "-" + responseNumber)));
        selectElement.selectByVisibleText(recipientName);
    }

    public void fillResponseRichTextEditor(int qnNumber, int responseNumber, String text) {
        String id = Const.ParamsNames.FEEDBACK_RESPONSE_TEXT
                + "-" + qnNumber + "-" + responseNumber;
        fillRichTextEditor(id, text);
    }

    public void fillResponseTextBox(int qnNumber, int responseNumber, String text) {
        WebElement element = browser.driver.findElement(
                By.name(Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-" + qnNumber + "-" + responseNumber));
        fillTextBox(element, text);
    }

    public void fillResponseTextBox(int qnNumber, int responseNumber, int responseSubNumber, String text) {
        WebElement element = browser.driver.findElement(
                By.id(Const.ParamsNames.FEEDBACK_RESPONSE_TEXT
                      + "-" + qnNumber + "-" + responseNumber + "-" + responseSubNumber));
        fillTextBox(element, text);
    }

    public String getResponseTextBoxValue(int qnNumber, int responseNumber) {
        WebElement element = browser.driver.findElement(
                By.name(Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-" + qnNumber + "-" + responseNumber));
        return element.getAttribute("value");
    }

    public String getResponseTextBoxValue(int qnNumber, int responseNumber, int responseSubNumber) {
        WebElement element = browser.driver.findElement(
                By.id(Const.ParamsNames.FEEDBACK_RESPONSE_TEXT
                    + "-" + qnNumber + "-" + responseNumber + "-" + responseSubNumber));
        return element.getAttribute("value");
    }

    public void clearResponseTextBoxValue(int qnNumber, int responseNumber, int responseSubNumber) {
        WebElement element = browser.driver.findElement(
                By.id(Const.ParamsNames.FEEDBACK_RESPONSE_TEXT
                    + "-" + qnNumber + "-" + responseNumber + "-" + responseSubNumber));
        element.clear();
    }

    public boolean isTextBoxValueEmpty(int qnNumber, int responseNumber, int responseSubNumber) {
        WebElement element = browser.driver.findElement(
                By.id(Const.ParamsNames.FEEDBACK_RESPONSE_TEXT
                    + "-" + qnNumber + "-" + responseNumber + "-" + responseSubNumber));
        return checkEmptyTextBoxValue(element);
    }

    public int getResponseTextBoxLengthLabelValue(int qnNumber, int responseNumber) {
        WebElement element = browser.driver.findElement(
                By.id("responseLength" + "-" + qnNumber + "-" + responseNumber));
        return Integer.parseInt(element.getText());
    }

    public void selectResponseTextDropdown(int qnNumber, int responseNumber, int responseSubNumber, String text) {
        WebElement element = browser.driver.findElement(
                By.id(Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-"
                      + qnNumber + "-" + responseNumber
                      + "-" + responseSubNumber));
        Select dropdown = new Select(element);
        dropdown.selectByVisibleText(text);
    }

    public String getConstSumMessage(int qnNumber, int responseNumber) {
        WebElement element = browser.driver.findElement(
                By.id("constSumMessage-" + qnNumber + "-" + responseNumber));
        return element.getText();
    }

    public void chooseMcqOption(int qnNumber, int responseNumber, String choiceName) {
        String name = Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-" + qnNumber + "-" + responseNumber;
        name = SanitizationHelper.sanitizeStringForXPath(name);
        String sanitizedChoiceName = SanitizationHelper.sanitizeStringForXPath(choiceName);
        WebElement element = browser.driver.findElement(
                By.xpath("//input[@name=" + name + " and @value=" + sanitizedChoiceName + "]"));
        click(element);
    }

    public boolean checkIfMcqOrMsqChoiceExists(int qnNumber, int responseNumber, String choiceName) {
        String name = Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-" + qnNumber + "-" + responseNumber;
        name = SanitizationHelper.sanitizeStringForXPath(name);
        String sanitizedChoiceName = SanitizationHelper.sanitizeStringForXPath(choiceName);
        try {
            browser.driver.findElement(
                    By.xpath("//input[@name=" + name + " and @value=" + sanitizedChoiceName + "]"));
        } catch (NoSuchElementException e) {
            return false;
        }
        return true;
    }

    public void fillMcqOtherOptionTextBox(int qnNumber, int responseNumber, String otherOptionText) {
        String elementId = "otherOptionText-" + qnNumber + "-" + responseNumber;
        WebElement otherOptionTextBox = browser.driver.findElement(By.id(elementId));
        fillTextBox(otherOptionTextBox, otherOptionText);
    }

    public void toggleMsqOption(int qnNumber, int responseNumber, String choiceName) {
        String name = Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-" + qnNumber + "-" + responseNumber;
        name = SanitizationHelper.sanitizeStringForXPath(name);
        String sanitizedChoiceName = SanitizationHelper.sanitizeStringForXPath(choiceName);
        WebElement element = browser.driver.findElement(
                By.xpath("//input[@name=" + name + " and @value=" + sanitizedChoiceName + "]"));
        click(element);
    }

    public void fillMsqOtherOptionTextBox(int qnNumber, int responseNumber, String otherOptionText) {
        String elementId = "msqOtherOptionText-" + qnNumber + "-" + responseNumber;
        WebElement otherOptionTextBox = browser.driver.findElement(By.id(elementId));
        fillTextBox(otherOptionTextBox, otherOptionText);
    }

    public void chooseContribOption(int qnNumber, int responseNumber, String choiceName) {
        String name = Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-" + qnNumber + "-" + responseNumber;
        name = SanitizationHelper.sanitizeStringForXPath(name);
        final WebElement selectElement = browser.driver.findElement(By.xpath("//select[@name=" + name + "]"));
        selectDropdownByVisibleValue(selectElement, choiceName);
    }

    public void clickRubricRadio(int qnIndex, int respIndex, int row, int col) {
        WebElement radio = browser.driver.findElement(
                By.id(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_CHOICE
                      + "-" + qnIndex + "-" + respIndex + "-" + row + "-" + col));
        click(radio);
    }

    public void clickRubricRadioMobile(int qnIndex, int respIndex, int row, int col) {
        WebElement radio = browser.driver.findElement(
                By.id("mobile-" + Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_CHOICE
                      + "-" + qnIndex + "-" + respIndex + "-" + row + "-" + col));
        click(radio);
    }

    public boolean isRubricRadioMobileChecked(int qnIndex, int respIndex, int row, int col) {
        WebElement radio = browser.driver.findElement(
                By.id("mobile-" + Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_CHOICE
                      + "-" + qnIndex + "-" + respIndex + "-" + row + "-" + col));
        String isChecked = radio.getAttribute("checked");
        return "true".equals(isChecked);
    }

    public boolean isRubricRadioChecked(int qnIndex, int respIndex, int row, int col) {
        WebElement radio = browser.driver.findElement(
                By.id(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_CHOICE
                      + "-" + qnIndex + "-" + respIndex + "-" + row + "-" + col));
        String isChecked = radio.getAttribute("checked");
        return "true".equals(isChecked);
    }

    public String getRankMessage(int qnNumber, int responseNumber) {
        WebElement element = browser.driver.findElement(
                By.id("rankMessage-" + qnNumber + "-" + responseNumber));
        return element.getText();
    }

    public void submitWithoutConfirmationEmail() {
        WebElement sendEmailCheckbox = browser.driver.findElement(By.name(Const.ParamsNames.SEND_SUBMISSION_EMAIL));
        if (sendEmailCheckbox.isSelected()) {
            click(sendEmailCheckbox);
        }
        clickSubmitButton();
    }

    public boolean isConfirmationEmailBoxTicked() {
        WebElement sendEmailCheckbox = browser.driver.findElement(By.name(Const.ParamsNames.SEND_SUBMISSION_EMAIL));
        return sendEmailCheckbox.isSelected();
    }

    public void clickSubmitButton() {
        WebElement submitButton = browser.driver.findElement(By.id("response_submit_button"));
        click(submitButton);
        waitForPageToLoad();
    }

    public void verifyOtherOptionTextUnclickable(int qnNumber, int responseNumber) {
        WebElement element = browser.driver.findElement(
                By.cssSelector("input[id$='OptionText-" + qnNumber + "-" + responseNumber + "']"));
        verifyUnclickable(element);
    }

    public void waitForCellHoverToDisappear() {
        waitForElementToDisappear(By.className("cell-hover"));
    }

    public void waitForOtherOptionTextToBeClickable(int qnNumber, int responseNumber) {
        WebElement element = browser.driver.findElement(
                By.cssSelector("input[id$='OptionText-" + qnNumber + "-" + responseNumber + "']"));
        waitForElementToBeClickable(element);
    }

    public void verifyVisibilityAndCloseMoreInfoAboutEqualShareModal() {
        WebElement moreInfoAboutEqualShareModalLink = browser.driver.findElement(By.id("more-info-equal-share-modal-link"));
        click(moreInfoAboutEqualShareModalLink);
        WebElement moreInfoAboutEqualShareModal = browser.driver.findElement(By.id("more-info-equal-share-modal"));
        waitForElementVisibility(moreInfoAboutEqualShareModal);
        closeMoreInfoAboutEqualShareModal();
    }

    public void verifyAndCloseSuccessfulSubmissionModal() {
        // Waiting for modal visibility
        WebElement closeButton = browser.driver.findElement(By.className("bootbox-close-button"));
        waitForElementToBeClickable(closeButton);

        // Verify modal header has success class
        WebElement modalHeader = closeButton.findElement(By.xpath(".."));
        assertTrue(modalHeader.getAttribute("class").contains("success"));

        // Verify title content
        WebElement modalTitle = browser.driver.findElement(By.xpath("//h4[@class='modal-title icon-success']"));
        assertEquals(modalTitle.getText(), Const.StatusMessages.FEEDBACK_RESPONSES_SAVED);

        // Verify message content
        final String expectedModalMessage = "All your responses have been successfully recorded! "
                + "You may now leave this page.\n"
                + "Note that you can change your responses and submit them again any time before the session closes.";
        WebElement modalMessage = browser.driver.findElement(By.xpath("//div[@class='bootbox-body']"));
        assertEquals(modalMessage.getText(), expectedModalMessage);

        clickDismissModalButtonAndWaitForModalHidden(closeButton);
    }

    private void closeMoreInfoAboutEqualShareModal() {
        WebElement closeButton = browser.driver
                .findElement(By.xpath("//div[@id='more-info-equal-share-modal']//button[@class='close']"));
        waitForElementToBeClickable(closeButton);
        click(closeButton);
        waitForElementToDisappear(By.id("more-info-equal-share-modal"));
    }

}
