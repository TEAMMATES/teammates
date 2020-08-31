package teammates.e2e.pageobjects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.questions.FeedbackRubricQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackRubricResponseDetails;

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

    public void verifyRubricQuestion(int qnNumber, String recipient, FeedbackRubricQuestionDetails questionDetails) {
        List<String> choices = questionDetails.getRubricChoices();
        List<String> subQuestions = questionDetails.getRubricSubQuestions();
        List<List<String>> descriptions = questionDetails.getRubricDescriptions();

        String[][] expectedTable = new String[subQuestions.size() + 1][choices.size() + 1];
        expectedTable[0][0] = "";
        for (int i = 1; i <= choices.size(); i++) {
            expectedTable[0][i] = choices.get(i - 1);
        }
        for (int i = 1; i <= subQuestions.size(); i++) {
            expectedTable[i][0] = subQuestions.get(i - 1);
        }
        for (int i = 1; i <= descriptions.size(); i++) {
            List<String> description = descriptions.get(i - 1);
            for (int j = 1; j <= description.size(); j++) {
                expectedTable[i][j] = description.get(j - 1);
            }
        }
        verifyTableBodyValues(getRubricTable(qnNumber, recipient), expectedTable);
    }

    public void submitRubricResponse(int qnNumber, String recipient, FeedbackResponseAttributes response) {
        FeedbackRubricResponseDetails responseDetails =
                (FeedbackRubricResponseDetails) response.getResponseDetails();
        List<Integer> answers = responseDetails.getAnswer();
        for (int i = 0; i < answers.size(); i++) {
            click(getRubricInputs(qnNumber, recipient, i + 2).get(answers.get(i)));
        }
        clickSubmitButton();
    }

    public void verifyRubricResponse(int qnNumber, String recipient, FeedbackResponseAttributes response) {
        FeedbackRubricResponseDetails responseDetails =
                (FeedbackRubricResponseDetails) response.getResponseDetails();
        List<Integer> answers = responseDetails.getAnswer();
        for (int i = 0; i < answers.size(); i++) {
            assertTrue(getRubricInputs(qnNumber, recipient, i + 2).get(answers.get(i)).isSelected());
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

    private WebElement getRubricSection(int qnNumber, String recipient) {
        int recipientIndex = getRecipientIndex(qnNumber, recipient);
        WebElement questionForm = getQuestionForm(qnNumber);
        return questionForm.findElements(By.tagName("tm-rubric-question-edit-answer-form")).get(recipientIndex);
    }

    private WebElement getRubricTable(int qnNumber, String recipient) {
        return getRubricSection(qnNumber, recipient).findElement(By.tagName("table"));
    }

    private List<WebElement> getRubricInputs(int qnNumber, String recipient, int rowNumber) {
        WebElement rubricRow = getRubricSection(qnNumber, recipient).findElements(By.tagName("tr")).get(rowNumber - 1);
        return rubricRow.findElements(By.tagName("input"));
    }
}
