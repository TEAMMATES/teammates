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
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.questions.FeedbackContributionQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackContributionResponseDetails;
import teammates.common.datatransfer.questions.FeedbackMcqResponseDetails;
import teammates.common.util.Const;

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
            waitForConfirmationModalAndClickOk();
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
        waitForConfirmationModalAndClickOk();
    }

    public void verifyCannotSubmit() {
        assertFalse(waitForElementPresence(By.id("btn-submit")).isEnabled());
    }

    public void markWithConfirmationEmail() {
        markOptionAsSelected(confirmationEmailCheckbox);
    }

    public void addComment(int qnNumber, String recipient, String newComment) {
        WebElement commentSection = getCommentSection(qnNumber, recipient);
        click(commentSection.findElement(By.id("btn-add-comment")));
        writeToCommentEditor(commentSection, newComment);
        clickSubmitButton();
    }

    public void editComment(int qnNumber, String recipient, String editedComment) {
        WebElement commentSection = getCommentSection(qnNumber, recipient);
        click(commentSection.findElement(By.id("btn-edit-comment")));
        writeToCommentEditor(commentSection, editedComment);
        clickSubmitButton();
    }

    public void deleteComment(int qnNumber, String recipient) {
        clickAndConfirm(getCommentSection(qnNumber, recipient).findElement(By.id("btn-delete-comment")));
    }

    public void verifyComment(int qnNumber, String recipient, String expectedComment) {
        WebElement commentSection = getCommentSection(qnNumber, recipient);
        String actualComment = commentSection.findElement(By.id("comment-text")).getAttribute("innerHTML");
        assertEquals(expectedComment, actualComment);
    }

    public void verifyNoCommentPresent(int qnNumber, String recipient) {
        int numComments = getCommentSection(qnNumber, recipient).findElements(By.id("comment-text")).size();
        assertEquals(numComments, 0);
    }

    public void submitMcqResponse(int qnNumber, String recipient, FeedbackResponseAttributes response) {
        FeedbackMcqResponseDetails responseDetails = (FeedbackMcqResponseDetails) response.getResponseDetails();
        if (responseDetails.isOther()) {
            markOptionAsSelected(getMcqOtherOptionRadioBtn(qnNumber, recipient));
            fillTextBox(getMcqOtherOptionTextbox(qnNumber, recipient), responseDetails.getOtherFieldContent());
        } else {
            List<WebElement> optionTexts = getMcqOptions(qnNumber, recipient);
            for (int i = 0; i < optionTexts.size(); i++) {
                if (optionTexts.get(i).getText().equals(responseDetails.getAnswer())) {
                    markOptionAsSelected(getMcqRadioBtns(qnNumber, recipient).get(i));
                    break;
                }
            }
        }
        clickSubmitButton();
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

    private void clickSubmitButton() {
        clickAndConfirm(browser.driver.findElement(By.id("btn-submit")));
    }

    private WebElement getCommentSection(int qnNumber, String recipient) {
        int recipientIndex = getRecipientIndex(qnNumber, recipient);
        return getQuestionForm(qnNumber).findElements(By.id("comment-section")).get(recipientIndex);
    }

    private void writeToCommentEditor(WebElement commentSection, String comment) {
        scrollElementToCenter(commentSection);
        waitForElementPresence(By.tagName("editor"));
        writeToRichTextEditor(commentSection.findElement(By.tagName("editor")), comment);
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

    private WebElement getMcqSection(int qnNumber, String recipient) {
        int recipientIndex = getRecipientIndex(qnNumber, recipient);
        WebElement questionForm = getQuestionForm(qnNumber);
        return questionForm.findElements(By.tagName("tm-mcq-question-edit-answer-form")).get(recipientIndex);
    }

    private WebElement getMcqOtherOptionRadioBtn(int qnNumber, String recipient) {
        WebElement mcqSection = getMcqSection(qnNumber, recipient);
        return mcqSection.findElement(By.cssSelector("#other-option input[type=radio]"));
    }

    private WebElement getMcqOtherOptionTextbox(int qnNumber, String recipient) {
        WebElement mcqSection = getMcqSection(qnNumber, recipient);
        return mcqSection.findElement(By.cssSelector("#other-option input[type=text]"));
    }

    private List<WebElement> getMcqOptions(int qnNumber, String recipient) {
        WebElement mcqSection = getMcqSection(qnNumber, recipient);
        return mcqSection.findElements(By.className("option-text"));
    }

    private List<WebElement> getMcqRadioBtns(int qnNumber, String recipient) {
        WebElement mcqSection = getMcqSection(qnNumber, recipient);
        return mcqSection.findElements(By.cssSelector("input[type=radio]"));
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
