package teammates.e2e.pageobjects;

import static org.junit.Assert.assertEquals;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.questions.FeedbackConstantSumQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackConstantSumResponseDetails;

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

    public void verifyConstSumQuestion(int qnNumber, String recipient,
                                             FeedbackConstantSumQuestionDetails questionDetails) {
        if (!questionDetails.isDistributeToRecipients()) {
            List<String> constSumOptions = questionDetails.getConstSumOptions();
            List<WebElement> optionTexts = getConstSumOptions(qnNumber, recipient);
            for (int i = 0; i < constSumOptions.size(); i++) {
                assertEquals(constSumOptions.get(i), optionTexts.get(i).getText());
            }
        }

        int totalPoints = questionDetails.getPoints();
        if (questionDetails.isPointsPerOption()) {
            totalPoints *= questionDetails.getNumOfConstSumOptions();
        }
        assertEquals(getQuestionForm(qnNumber).findElement(By.id("total-points-message")).getText(),
                "Total points distributed should add up to " + totalPoints + ".");

        if (questionDetails.isForceUnevenDistribution()) {
            String entityType = questionDetails.isDistributeToRecipients() ? "recipient" : "option";
            if (questionDetails.getDistributePointsFor().equals("All options")) {
                assertEquals(getQuestionForm(qnNumber).findElement(By.id("all-uneven-message")).getText(),
                        "Every " + entityType + " should be allocated different number of points.");
            } else {
                assertEquals(getQuestionForm(qnNumber).findElement(By.id("one-uneven-message")).getText(),
                        "At least one " + entityType + " should be allocated different number of points.");
            }
        }
    }

    public void submitConstSumOptionResponse(int qnNumber, String recipient, FeedbackResponseAttributes response) {
        FeedbackConstantSumResponseDetails responseDetails =
                (FeedbackConstantSumResponseDetails) response.getResponseDetails();
        List<Integer> answers = responseDetails.getAnswers();
        List<WebElement> constSumInputs = getConstSumInputs(qnNumber, recipient);
        for (int i = 0; i < answers.size(); i++) {
            fillTextBox(constSumInputs.get(i), Integer.toString(answers.get(i)));
        }
        clickSubmitButton();
    }

    public void verifyConstSumOptionResponse(int qnNumber, String recipient, FeedbackResponseAttributes response) {
        FeedbackConstantSumResponseDetails responseDetails =
                (FeedbackConstantSumResponseDetails) response.getResponseDetails();
        List<Integer> answers = responseDetails.getAnswers();
        List<WebElement> constSumInputs = getConstSumInputs(qnNumber, recipient);
        for (int i = 0; i < answers.size(); i++) {
            assertEquals(constSumInputs.get(i).getAttribute("value"), Integer.toString(answers.get(i)));
        }
    }

    public void submitConstSumRecipientResponse(int qnNumber, List<FeedbackResponseAttributes> responses) {
        List<WebElement> recipientInputs = getConstSumRecipientInputs(qnNumber);
        for (int i = 0; i < responses.size(); i++) {
            FeedbackConstantSumResponseDetails response =
                    (FeedbackConstantSumResponseDetails) responses.get(i).getResponseDetails();
            fillTextBox(recipientInputs.get(i), Integer.toString(response.getAnswers().get(0)));
        }
        clickSubmitButton();
    }

    public void verifyConstSumRecipientResponse(int qnNumber, List<FeedbackResponseAttributes> responses) {
        List<WebElement> recipientInputs = getConstSumRecipientInputs(qnNumber);
        for (int i = 0; i < responses.size(); i++) {
            FeedbackConstantSumResponseDetails response =
                    (FeedbackConstantSumResponseDetails) responses.get(i).getResponseDetails();
            assertEquals(recipientInputs.get(i).getAttribute("value"),
                    Integer.toString(response.getAnswers().get(0)));
        }
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
        // For questions with recipient none or self.
        if (recipient.isEmpty()) {
            return 0;
        }
        WebElement questionForm = getQuestionForm(qnNumber);
        // For questions with flexible recipient.
        try {
            List<WebElement> recipientDropdowns = questionForm.findElements(By.id("recipient-dropdown"));
            for (int i = 0; i < recipientDropdowns.size(); i++) {
                String dropdownText = getSelectedDropdownOptionText(recipientDropdowns.get(i));
                if (dropdownText.isEmpty()) {
                    selectDropdownOptionByText(recipientDropdowns.get(i), recipient);
                    return i;
                } else if (dropdownText.equals(recipient)) {
                    return i;
                }
            }
        } catch (NoSuchElementException e) {
            // continue
        }
        int i = 0;
        while (true) {
            if (questionForm.findElement(By.id("recipient-name-" + i)).getText().contains(recipient)) {
                return i;
            }
            i++;
        }
    }

    private WebElement getConstSumOptionsSection(int qnNumber, String recipient) {
        int recipientIndex = getRecipientIndex(qnNumber, recipient);
        WebElement questionForm = getQuestionForm(qnNumber);
        return questionForm.findElements(By.tagName("tm-constsum-options-question-edit-answer-form")).get(recipientIndex);
    }

    private List<WebElement> getConstSumOptions(int qnNumber, String recipient) {
        WebElement constSumOptionSection = getConstSumOptionsSection(qnNumber, recipient);
        return constSumOptionSection.findElements(By.tagName("strong"));
    }

    private List<WebElement> getConstSumInputs(int qnNumber, String recipient) {
        WebElement constSumOptionSection = getConstSumOptionsSection(qnNumber, recipient);
        return constSumOptionSection.findElements(By.cssSelector("input[type=number]"));
    }

    private List<WebElement> getConstSumRecipientInputs(int qnNumber) {
        return getQuestionForm(qnNumber).findElements(By.cssSelector("input[type=number]"));
    }
}
