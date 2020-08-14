package teammates.e2e.pageobjects;

import static org.junit.Assert.assertEquals;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.questions.FeedbackTextQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackTextResponseDetails;

/**
 * Represents the feedback submission page of the website.
 */
public class FeedbackSubmitPage extends AppPage {

    public FeedbackSubmitPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        waitForPageToLoad();
        return getPageTitle().contains("Submit Feedback");
    }

    public void verifyFeedBackSessionDetails(FeedbackSessionAttributes feedbackSession) {
        assertEquals(getCourseId(), feedbackSession.getCourseId());
        assertEquals(getFeedbackSessionName(), feedbackSession.getFeedbackSessionName());
        assertEquals(getOpeningTime(), getDateString(feedbackSession.getStartTime(), feedbackSession.getTimeZone()));
        assertEquals(getClosingTime(), getDateString(feedbackSession.getEndTime(), feedbackSession.getTimeZone()));
        assertEquals(getInstructions(), feedbackSession.getInstructions());
    }

    public void verifyNumQuestions(int expected) {
        assertEquals(browser.driver.findElements(By.tagName("tm-question-submission-form")).size(), expected);
    }

    public void verifyTextQuestion(int qnNumber, FeedbackTextQuestionDetails questionDetails) {
        String recommendedLengthText = getQuestionForm(qnNumber).findElement(By.id("recommended-length")).getText();
        assertEquals(recommendedLengthText, "Recommended length for the answer: "
                + questionDetails.getRecommendedLength() + " words");
    }

    public void submitTextResponse(int qnNumber, String recipient, FeedbackResponseAttributes response) {
        FeedbackTextResponseDetails responseDetails = (FeedbackTextResponseDetails) response.getResponseDetails();
        writeToRichTextEditor(getTextResponseEditor(qnNumber, recipient), responseDetails.getAnswer());
        clickSubmitButton();
    }

    public void verifyTextResponse(int qnNumber, String recipient, FeedbackResponseAttributes response) {
        FeedbackTextResponseDetails responseDetails = (FeedbackTextResponseDetails) response.getResponseDetails();
        int responseLength = responseDetails.getAnswer().split(" ").length;
        assertEquals(getEditorRichText(getTextResponseEditor(qnNumber, recipient)), responseDetails.getAnswer());
        assertEquals(getResponseLengthText(qnNumber, recipient), "Response length: " + responseLength
                                + " words");
    }

    private String getCourseId() {
        return browser.driver.findElement(By.id("course-id")).getText();
    }

    private String getFeedbackSessionName() {
        return browser.driver.findElement(By.id("fs-name")).getText();
    }

    private String getOpeningTime() {
        return browser.driver.findElement(By.id("opening-time")).getText();
    }

    private String getClosingTime() {
        return browser.driver.findElement(By.id("closing-time")).getText();
    }

    private String getInstructions() {
        return browser.driver.findElement(By.id("instructions")).getAttribute("innerHTML");
    }

    private String getDateString(Instant instant, ZoneId timeZone) {
        return DateTimeFormatter
                .ofPattern("EE, dd MMM, yyyy, hh:mm a z")
                .format(instant.atZone(timeZone));
    }

    private WebElement getQuestionForm(int qnNumber) {
        return browser.driver.findElements(By.tagName("tm-question-submission-form")).get(qnNumber - 1);
    }

    private void clickSubmitButton() {
        clickAndConfirm(browser.driver.findElement(By.id("btn-submit")));
    }

    private int getRecipientIndex(int qnNumber, String recipient) {
        WebElement questionForm = getQuestionForm(qnNumber);
        try {
            int i = 0;
            while (true) {
                if (questionForm.findElement(By.id("recipient-name-" + i)).getText().contains(recipient)) {
                    return i;
                }
                i++;
            }
        } catch (NoSuchElementException e) {
            return -1;
        }
    }

    private WebElement getTextResponseEditor(int qnNumber, String recipient) {
        int recipientIndex = getRecipientIndex(qnNumber, recipient);
        WebElement questionForm = getQuestionForm(qnNumber);
        WebElement editor = questionForm.findElements(By.tagName("tm-rich-text-editor")).get(recipientIndex);
        scrollElementToCenter(editor);
        return editor;
    }

    private String getResponseLengthText(int qnNumber, String recipient) {
        int recipientIndex = getRecipientIndex(qnNumber, recipient);
        return getQuestionForm(qnNumber).findElements(By.id("response-length")).get(recipientIndex).getText();
    }
}
