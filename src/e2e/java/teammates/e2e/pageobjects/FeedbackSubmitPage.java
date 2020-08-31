package teammates.e2e.pageobjects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
import teammates.common.datatransfer.questions.FeedbackContributionQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackContributionResponseDetails;
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

    public void verifyContributionQuestion(int qnNumber, FeedbackContributionQuestionDetails questionDetails) {
        try {
            selectDropdownOptionByText(getContributionDropdowns(qnNumber).get(0), "Not Sure");
            assertTrue(questionDetails.isNotSureAllowed());
        } catch (NoSuchElementException e) {
            assertFalse(questionDetails.isNotSureAllowed());
        }
    }

    public void submitContributionResponse(int qnNumber, List<FeedbackResponseAttributes> responses) {
        List<WebElement> dropdowns = getContributionDropdowns(qnNumber);
        for (int i = 0; i < responses.size(); i++) {
            FeedbackContributionResponseDetails response =
                    (FeedbackContributionResponseDetails) responses.get(i).getResponseDetails();
            selectDropdownOptionByText(dropdowns.get(i), getContributionString(response.getAnswer()));
        }
        clickSubmitButton();
    }

    public void verifyContributionResponse(int qnNumber, List<FeedbackResponseAttributes> responses) {
        List<WebElement> dropdowns = getContributionDropdowns(qnNumber);
        for (int i = 0; i < responses.size(); i++) {
            FeedbackContributionResponseDetails response =
                    (FeedbackContributionResponseDetails) responses.get(i).getResponseDetails();
            assertEquals(getSelectedDropdownOptionText(dropdowns.get(i)), getContributionString(response.getAnswer()));
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

    private List<WebElement> getContributionDropdowns(int questionNum) {
        return getQuestionForm(questionNum).findElements(By.tagName("select"));
    }

    private String getContributionString(int answer) {
        if (answer == Const.POINTS_NOT_SURE) {
            return "Not Sure";
        } else if (answer == Const.POINTS_EQUAL_SHARE) {
            return "Equal share";
        } else {
            return "Equal share" + (answer > 100 ? " + " : " - ") + Math.abs(answer - 100) + "%";
        }
    }
}
