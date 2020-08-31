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
import teammates.common.datatransfer.questions.FeedbackRankOptionsQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackRankOptionsResponseDetails;
import teammates.common.datatransfer.questions.FeedbackRankQuestionDetails;
import teammates.common.util.Const;

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

    public void verifyRankQuestion(int qnNumber, String recipient, FeedbackRankQuestionDetails questionDetails) {
        if (questionDetails.getMaxOptionsToBeRanked() != Integer.MIN_VALUE) {
            assertEquals(getQuestionForm(qnNumber).findElement(By.id("max-options-message")).getText(),
                    "Rank no more than " + questionDetails.getMaxOptionsToBeRanked() + " options.");
        }
        if (questionDetails.getMinOptionsToBeRanked() != Integer.MIN_VALUE) {
            assertEquals(getQuestionForm(qnNumber).findElement(By.id("min-options-message")).getText(),
                    "Rank at least " + questionDetails.getMinOptionsToBeRanked() + " options.");
        }
        if (questionDetails instanceof FeedbackRankOptionsQuestionDetails) {
            FeedbackRankOptionsQuestionDetails optionDetails = (FeedbackRankOptionsQuestionDetails) questionDetails;
            List<String> options = optionDetails.getOptions();
            List<WebElement> optionTexts = getRankOptions(qnNumber, recipient);
            for (int i = 0; i < options.size(); i++) {
                assertEquals(options.get(i), optionTexts.get(i).getText());
            }
        }
    }

    public void submitRankOptionResponse(int qnNumber, String recipient, FeedbackResponseAttributes response) {
        FeedbackRankOptionsResponseDetails responseDetails =
                (FeedbackRankOptionsResponseDetails) response.getResponseDetails();
        List<Integer> answers = responseDetails.getAnswers();
        for (int i = 0; i < answers.size(); i++) {
            if (answers.get(i) == Const.POINTS_NOT_SUBMITTED) {
                selectDropdownOptionByText(getRankOptionsDropdowns(qnNumber, recipient).get(i), "");
            } else {
                selectDropdownOptionByText(getRankOptionsDropdowns(qnNumber, recipient).get(i),
                        Integer.toString(answers.get(i)));
            }
        }
        clickSubmitButton();
    }

    public void verifyRankOptionResponse(int qnNumber, String recipient, FeedbackResponseAttributes response) {
        FeedbackRankOptionsResponseDetails responseDetails =
                (FeedbackRankOptionsResponseDetails) response.getResponseDetails();
        List<Integer> answers = responseDetails.getAnswers();
        for (int i = 0; i < answers.size(); i++) {
            if (answers.get(i) == Const.POINTS_NOT_SUBMITTED) {
                assertEquals(getSelectedDropdownOptionText(getRankOptionsDropdowns(qnNumber, recipient).get(i)),
                        "");
            } else {
                assertEquals(getSelectedDropdownOptionText(getRankOptionsDropdowns(qnNumber, recipient).get(i)),
                        Integer.toString(answers.get(i)));
            }
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

    private WebElement getRankOptionsSection(int qnNumber, String recipient) {
        int recipientIndex = getRecipientIndex(qnNumber, recipient);
        WebElement questionForm = getQuestionForm(qnNumber);
        return questionForm.findElements(By.tagName("tm-rank-options-question-edit-answer-form")).get(recipientIndex);
    }

    private List<WebElement> getRankOptions(int questionNum, String recipient) {
        WebElement rankSection = getRankOptionsSection(questionNum, recipient);
        return rankSection.findElements(By.tagName("strong"));
    }

    private List<WebElement> getRankOptionsDropdowns(int questionNum, String recipient) {
        WebElement rankSection = getRankOptionsSection(questionNum, recipient);
        return rankSection.findElements(By.tagName("select"));
    }
}
