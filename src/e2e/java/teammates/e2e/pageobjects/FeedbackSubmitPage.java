package teammates.e2e.pageobjects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;

/**
 * Represents the feedback submission page of the website.
 */
public class FeedbackSubmitPage extends AppPage {

    @FindBy(id = "confirmation-email-checkbox")
    private WebElement confirmationEmailCheckbox;

    public FeedbackSubmitPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        if (isElementPresent(By.className("modal-content"))) {
            click(waitForElementPresence(By.className("modal-btn-ok")));
        }
        return getPageTitle().contains("Submit Feedback");
    }

    public void verifyFeedbackSessionDetails(FeedbackSessionAttributes feedbackSession) {
        assertEquals(getCourseId(), feedbackSession.getCourseId());
        assertEquals(getFeedbackSessionName(), feedbackSession.getFeedbackSessionName());
        assertDateEquals(getOpeningTime(), feedbackSession.getStartTime(), feedbackSession.getTimeZone());
        assertDateEquals(getClosingTime(), feedbackSession.getEndTime(), feedbackSession.getTimeZone());
        assertEquals(getInstructions(), feedbackSession.getInstructions());
    }

    public void verifyNumQuestions(int expected) {
        assertEquals(browser.driver.findElements(By.tagName("tm-question-submission-form")).size(), expected);
    }

    public void verifyQuestionDetails(int qnNumber, FeedbackQuestionAttributes questionAttributes) {
        assertEquals(getQuestionBrief(qnNumber), questionAttributes.getQuestionDetails().getQuestionText());
        verifyVisibilityList(qnNumber, questionAttributes);
        if (questionAttributes.getQuestionDescription() != null) {
            assertEquals(getQuestionDescription(qnNumber), questionAttributes.getQuestionDescription());
        }
    }

    public void verifyLimitedRecipients(int qnNumber, int numRecipients, List<String> recipientNames) {
        List<WebElement> recipientDropdowns = getQuestionForm(qnNumber).findElements(By.id("recipient-dropdown"));
        assertEquals(numRecipients, recipientDropdowns.size());
        List<WebElement> recipients = recipientDropdowns.get(0).findElements(By.tagName("option"));
        assertEquals(recipientNames.size(), recipients.size() - 1);
        Collections.sort(recipientNames);
        for (int i = 0; i < recipientNames.size(); i++) {
            assertEquals(recipientNames.get(i), recipients.get(i + 1).getText());
        }
    }

    public void verifyRecipients(int qnNumber, List<String> recipientNames, String role) {
        WebElement questionForm = getQuestionForm(qnNumber);
        Collections.sort(recipientNames);
        for (int i = 0; i < recipientNames.size(); i++) {
            assertEquals(recipientNames.get(i) + " (" + role + ")",
                    questionForm.findElement(By.id("recipient-name-" + i)).getText());
        }
    }

    public void verifyWarningMessageForPartialResponse(int[] unansweredQuestions) {
        click(waitForElementPresence(By.id("btn-submit")));
        StringBuilder expectedSb = new StringBuilder();
        for (int unansweredQuestion : unansweredQuestions) {
            expectedSb.append(unansweredQuestion).append(", ");
        }
        String expectedString = expectedSb.toString().substring(0, expectedSb.length() - 2) + ".";
        String warningString = waitForElementPresence(By.id("not-answered-questions")).getText();
        assertEquals(warningString.split(": ")[1], expectedString);
        click(browser.driver.findElement(By.id("btn-close")));
    }

    public void verifyCannotSubmit() {
        assertFalse(waitForElementPresence(By.id("btn-submit")).isEnabled());
    }

    public void markWithConfirmationEmail() {
        markCheckBoxAsChecked(confirmationEmailCheckbox);
    }

    public void addComment(int qnNumber, int recipientNumber, String newComment) {
        WebElement commentSection = getCommentSection(qnNumber, recipientNumber);
        click(commentSection.findElement(By.id("btn-add-comment")));
        writeToCommentEditor(commentSection, newComment);
    }

    public void editComment(int qnNumber, int recipientNumber, String editedComment) {
        WebElement commentSection = getCommentSection(qnNumber, recipientNumber);
        click(commentSection.findElement(By.id("btn-edit-comment")));
        writeToCommentEditor(commentSection, editedComment);
    }

    public void deleteComment(int qnNumber, int recipientNumber) {
        clickAndConfirm(getCommentSection(qnNumber, recipientNumber).findElement(By.id("btn-delete-comment")));
    }

    public void verifyComment(int qnNumber, int recipientNumber, String expectedComment) {
        WebElement commentSection = getCommentSection(qnNumber, recipientNumber);
        String actualComment = commentSection.findElement(By.id("comment-text")).getAttribute("innerHTML");
        assertEquals(expectedComment, actualComment);
    }

    public void verifyNoCommentPresent(int qnNumber, int recipientNumber) {
        try {
            getCommentSection(qnNumber, recipientNumber).findElement(By.id("btn-add-comment"));
        } catch (NoSuchElementException e) {
            fail("Comment is present.");
        }
    }

    public void selectMultipleChoiceOption(int qnNumber, int recipientNumber, int mcqOption) {
        List<WebElement> mcqOptions = getMcqOptions(qnNumber, recipientNumber);
        click(mcqOptions.get(mcqOption - 1));
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

    private void assertDateEquals(String actual, Instant instant, ZoneId timeZone) {
        String dateStrWithAbbr = getDateStringWithAbbr(instant, timeZone);
        String dateStrWithOffset = getDateStringWithOffset(instant, timeZone);

        boolean isExpected = actual.equals(dateStrWithAbbr) || actual.equals(dateStrWithOffset);
        assertTrue(isExpected);
    }

    private String getDateStringWithAbbr(Instant instant, ZoneId timeZone) {
        return DateTimeFormatter
                .ofPattern("EE, dd MMM, yyyy, hh:mm a z")
                .format(instant.atZone(timeZone));
    }

    private String getDateStringWithOffset(Instant instant, ZoneId timeZone) {
        return DateTimeFormatter
                .ofPattern("EE, dd MMM, yyyy, hh:mm a X")
                .format(instant.atZone(timeZone));
    }

    private WebElement getQuestionForm(int qnNumber) {
        return browser.driver.findElements(By.tagName("tm-question-submission-form")).get(qnNumber - 1);
    }

    private String getQuestionBrief(int qnNumber) {
        String questionDetails = getQuestionForm(qnNumber).findElement(By.id("question-details")).getText();
        return questionDetails.split(": ")[1];
    }

    private void verifyVisibilityList(int qnNumber, FeedbackQuestionAttributes questionAttributes) {
        if (questionAttributes.showResponsesTo.isEmpty()) {
            verifyVisibilityStringPresent(qnNumber, "No-one can see your responses");
        }
        if (questionAttributes.recipientType.equals(FeedbackParticipantType.SELF)) {
            verifyVisibilityStringPresent(qnNumber, "You can see your own feedback in the results page later on.");
        }
        for (FeedbackParticipantType viewerType : questionAttributes.showResponsesTo) {
            verifyVisibilityStringPresent(qnNumber, getVisibilityString(questionAttributes, viewerType));
        }
    }

    private void verifyVisibilityStringPresent(int qnNumber, String expectedString) {
        List<WebElement> visibilityStrings = getQuestionForm(qnNumber).findElement(By.id("visibility-list"))
                .findElements(By.tagName("li"));
        for (WebElement visibilityString : visibilityStrings) {
            if (visibilityString.getText().equals(expectedString)) {
                return;
            }
        }
        fail("Expected visibility string not found: " + qnNumber + ": " + expectedString);
    }

    private String getVisibilityString(FeedbackQuestionAttributes questionAttributes,
                                       FeedbackParticipantType viewerType) {
        if (!questionAttributes.showResponsesTo.contains(viewerType)) {
            return "";
        }

        StringBuilder message = new StringBuilder(getViewerString(viewerType, questionAttributes.recipientType));
        message.append(" can see your response");
        if (questionAttributes.showRecipientNameTo.contains(viewerType)) {
            message.append(", the name of the recipient");
            if (questionAttributes.showGiverNameTo.contains(viewerType)) {
                message.append(", and your name");
            } else {
                message.append(", but not your name");
            }
        } else {
            if (questionAttributes.showGiverNameTo.contains(viewerType)) {
                message.append(", and your name, but not the name of the recipient");
            } else {
                message.append(", but not the name of the recipient, or your name");
            }
        }
        return message.toString();
    }

    private String getViewerString(FeedbackParticipantType viewerType, FeedbackParticipantType recipientType) {
        switch(viewerType) {
        case RECEIVER:
            return "The receiving " + getRecipientString(recipientType);
        case OWN_TEAM_MEMBERS:
            return "Your team members";
        case STUDENTS:
            return "Other students in the course";
        case INSTRUCTORS:
            return "Instructors in this course";
        default:
            throw new RuntimeException("Unknown viewer type");
        }
    }

    private String getRecipientString(FeedbackParticipantType recipientType) {
        switch(recipientType) {
        case TEAMS:
            return "teams";
        case OWN_TEAM_MEMBERS:
            return "student";
        case STUDENTS:
            return "students";
        case INSTRUCTORS:
            return "instructors";
        default:
            throw new RuntimeException("Unknown recipientType");
        }
    }

    private String getQuestionDescription(int qnNumber) {
        return getQuestionForm(qnNumber).findElement(By.id("question-description")).getAttribute("innerHTML");
    }

    private WebElement getCommentSection(int qnNumber, int recipientNumber) {
        return getQuestionForm(qnNumber).findElements(By.id("comment-section")).get(recipientNumber - 1);
    }

    private void writeToCommentEditor(WebElement commentSection, String comment) {
        scrollElementToCenter(commentSection);
        writeToRichTextEditor(commentSection.findElement(By.cssSelector("textarea")), comment);
    }

    private List<WebElement> getMcqOptions(int qnNumber, int recipientNumber) {
        return getQuestionForm(qnNumber)
                .findElements(By.tagName("tm-mcq-question-edit-answer-form"))
                .get(recipientNumber - 1)
                .findElements(By.tagName("input"));
    }
}
