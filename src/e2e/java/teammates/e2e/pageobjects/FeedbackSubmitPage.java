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
import teammates.common.datatransfer.questions.FeedbackMcqQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackMcqResponseDetails;
import teammates.common.datatransfer.questions.FeedbackMsqQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackMsqResponseDetails;
import teammates.common.datatransfer.questions.FeedbackNumericalScaleQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackNumericalScaleResponseDetails;
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

    public void verifyMcqQuestion(int qnNumber, String recipient, FeedbackMcqQuestionDetails questionDetails) {
        List<String> mcqChoices = questionDetails.getMcqChoices();
        List<WebElement> optionTexts = getMcqOptions(qnNumber, recipient);

        for (int i = 0; i < mcqChoices.size(); i++) {
            assertEquals(mcqChoices.get(i), optionTexts.get(i).getText());
        }

        if (questionDetails.isOtherEnabled()) {
            assertEquals("Other", getMcqSection(qnNumber, recipient).findElement(By.id("other-option")).getText());
        }
    }

    public void verifyGeneratedMcqQuestion(int qnNumber, String recipient, List<String> options) {
        List<WebElement> optionTexts = getMcqOptions(qnNumber, recipient);
        for (int i = 0; i < options.size(); i++) {
            assertEquals(options.get(i), optionTexts.get(i).getText());
        }
    }

    public void submitMcqResponse(int qnNumber, String recipient, FeedbackResponseAttributes response) {
        FeedbackMcqResponseDetails responseDetails = (FeedbackMcqResponseDetails) response.getResponseDetails();
        if (responseDetails.isOther()) {
            click(getMcqOtherOptionRadioBtn(qnNumber, recipient));
            fillTextBox(getMcqOtherOptionTextbox(qnNumber, recipient), responseDetails.getOtherFieldContent());
        } else {
            List<WebElement> optionTexts = getMcqOptions(qnNumber, recipient);
            for (int i = 0; i < optionTexts.size(); i++) {
                if (optionTexts.get(i).getText().equals(responseDetails.getAnswer())) {
                    click(getMcqRadioBtns(qnNumber, recipient).get(i));
                    break;
                }
            }
        }
        clickSubmitButton();
    }

    public void verifyMcqResponse(int qnNumber, String recipient, FeedbackResponseAttributes response) {
        FeedbackMcqResponseDetails responseDetails = (FeedbackMcqResponseDetails) response.getResponseDetails();
        if (responseDetails.isOther()) {
            assertTrue(getMcqOtherOptionRadioBtn(qnNumber, recipient).isSelected());
            assertEquals(getMcqOtherOptionTextbox(qnNumber, recipient).getAttribute("value"),
                    responseDetails.getOtherFieldContent());
        } else {
            List<WebElement> optionTexts = getMcqOptions(qnNumber, recipient);
            List<WebElement> radioBtns = getMcqRadioBtns(qnNumber, recipient);
            for (int i = 0; i < optionTexts.size(); i++) {
                if (optionTexts.get(i).getText().equals(responseDetails.getAnswer())) {
                    assertTrue(radioBtns.get(i).isSelected());
                    break;
                }
                assertFalse(radioBtns.get(i).isSelected());
            }
        }
    }

    public void verifyMsqQuestion(int qnNumber, String recipient, FeedbackMsqQuestionDetails questionDetails) {
        List<String> msqChoices = questionDetails.getMsqChoices();
        if (questionDetails.isOtherEnabled()) {
            msqChoices.add("Other");
        }
        if (questionDetails.getMinSelectableChoices() == Integer.MIN_VALUE) {
            msqChoices.add("None of the above");
        }
        List<WebElement> optionTexts = getMsqOptions(qnNumber, recipient);
        for (int i = 0; i < msqChoices.size(); i++) {
            assertEquals(msqChoices.get(i), optionTexts.get(i).getText());
        }
        verifyMsqSelectableOptions(qnNumber, questionDetails);
    }

    private void verifyMsqSelectableOptions(int qnNumber, FeedbackMsqQuestionDetails questionDetails) {
        if (questionDetails.getMinSelectableChoices() > Integer.MIN_VALUE) {
            assertEquals(getQuestionForm(qnNumber).findElement(By.id("min-selectable-message")).getText(),
                    "Choose at least " + questionDetails.getMinSelectableChoices() + " options.");
        }
        if (questionDetails.getMaxSelectableChoices() > Integer.MIN_VALUE) {
            assertEquals(getQuestionForm(qnNumber).findElement(By.id("max-selectable-message")).getText(),
                    "Choose no more than " + questionDetails.getMaxSelectableChoices() + " options.");
        }
    }

    public void verifyGeneratedMsqQuestion(int qnNumber, String recipient, FeedbackMsqQuestionDetails questionDetails,
                                           List<String> options) {
        List<WebElement> optionTexts = getMsqOptions(qnNumber, recipient);
        for (int i = 0; i < options.size(); i++) {
            assertEquals(options.get(i), optionTexts.get(i).getText());
        }
        verifyMsqSelectableOptions(qnNumber, questionDetails);
    }

    public void submitMsqResponse(int qnNumber, String recipient, FeedbackResponseAttributes response) {
        FeedbackMsqResponseDetails responseDetails = (FeedbackMsqResponseDetails) response.getResponseDetails();
        List<String> answers = responseDetails.getAnswers();
        List<WebElement> optionTexts = getMsqOptions(qnNumber, recipient);
        List<WebElement> checkboxes = getMsqCheckboxes(qnNumber, recipient);
        for (int i = 0; i < optionTexts.size(); i++) {
            if (answers.contains(optionTexts.get(i).getText())) {
                markCheckBoxAsChecked(checkboxes.get(i));
            } else {
                markCheckBoxAsUnchecked(checkboxes.get(i));
            }
        }
        if (responseDetails.isOther()) {
            click(getMsqOtherOptionCheckbox(qnNumber, recipient));
            fillTextBox(getMsqOtherOptionTextbox(qnNumber, recipient), responseDetails.getOtherFieldContent());
        }
        clickSubmitButton();
    }

    public void verifyMsqResponse(int qnNumber, String recipient, FeedbackResponseAttributes response) {
        FeedbackMsqResponseDetails responseDetails = (FeedbackMsqResponseDetails) response.getResponseDetails();
        List<String> answers = responseDetails.getAnswers();
        List<WebElement> optionTexts = getMsqOptions(qnNumber, recipient);
        List<WebElement> checkboxes = getMsqCheckboxes(qnNumber, recipient);
        for (int i = 0; i < optionTexts.size(); i++) {
            if (answers.contains(optionTexts.get(i).getText())) {
                assertTrue(checkboxes.get(i).isSelected());
            } else if (optionTexts.get(i).getText().equals("Others")) {
                assertEquals(checkboxes.get(i).isSelected(), responseDetails.isOther());
            } else {
                assertFalse(checkboxes.get(i).isSelected());
            }
        }
        if (responseDetails.isOther()) {
            assertEquals(getMsqOtherOptionTextbox(qnNumber, recipient).getAttribute("value"),
                    responseDetails.getOtherFieldContent());
        }
    }

    public void verifyNumScaleQuestion(int qnNumber, String recipient,
                                       FeedbackNumericalScaleQuestionDetails questionDetails) {
        double step = questionDetails.getStep();
        double twoSteps = 2 * step;
        double min = questionDetails.getMinScale();
        double max = questionDetails.getMaxScale();
        String possibleValues = String.format("Possible values: [%s, %s, %s, ..., %s, %s, %s]",
                getDoubleString(min), getDoubleString(min + step), getDoubleString(min + twoSteps),
                getDoubleString(max - twoSteps), getDoubleString(max - step), getDoubleString(max));
        String actualValues = getNumScaleSection(qnNumber, recipient).findElement(By.id("possible-values")).getText();
        assertEquals(actualValues, possibleValues);
    }

    public void submitNumScaleResponse(int qnNumber, String recipient, FeedbackResponseAttributes response) {
        FeedbackNumericalScaleResponseDetails responseDetails =
                (FeedbackNumericalScaleResponseDetails) response.getResponseDetails();
        fillTextBox(getNumScaleInput(qnNumber, recipient), Double.toString(responseDetails.getAnswer()));
        clickSubmitButton();
    }

    public void verifyNumScaleResponse(int qnNumber, String recipient, FeedbackResponseAttributes response) {
        FeedbackNumericalScaleResponseDetails responseDetails =
                (FeedbackNumericalScaleResponseDetails) response.getResponseDetails();
        assertEquals(getNumScaleInput(qnNumber, recipient).getAttribute("value"),
                getDoubleString(responseDetails.getAnswer()));
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

    private String getDoubleString(Double value) {
        return value % 1 == 0 ? Integer.toString(value.intValue()) : Double.toString(value);
    }

    private WebElement getMsqSection(int qnNumber, String recipient) {
        int recipientIndex = getRecipientIndex(qnNumber, recipient);
        WebElement questionForm = getQuestionForm(qnNumber);
        return questionForm.findElements(By.tagName("tm-msq-question-edit-answer-form")).get(recipientIndex);
    }

    private WebElement getMsqOtherOptionCheckbox(int qnNumber, String recipient) {
        WebElement msqSection = getMsqSection(qnNumber, recipient);
        return msqSection.findElement(By.cssSelector("#other-option input[type=checkbox]"));
    }

    private WebElement getMsqOtherOptionTextbox(int qnNumber, String recipient) {
        WebElement msqSection = getMsqSection(qnNumber, recipient);
        return msqSection.findElement(By.cssSelector("#other-option input[type=text]"));
    }

    private List<WebElement> getMsqOptions(int qnNumber, String recipient) {
        WebElement msqSection = getMsqSection(qnNumber, recipient);
        return msqSection.findElements(By.tagName("strong"));
    }

    private List<WebElement> getMsqCheckboxes(int qnNumber, String recipient) {
        WebElement msqSection = getMsqSection(qnNumber, recipient);
        return msqSection.findElements(By.cssSelector("input[type=checkbox]"));
    }

    private WebElement getNumScaleSection(int qnNumber, String recipient) {
        int recipientIndex = getRecipientIndex(qnNumber, recipient);
        WebElement questionForm = getQuestionForm(qnNumber);
        return questionForm.findElements(By.tagName("tm-num-scale-question-edit-answer-form")).get(recipientIndex);
    }

    private WebElement getNumScaleInput(int qnNumber, String recipient) {
        WebElement numScaleSection = getNumScaleSection(qnNumber, recipient);
        return numScaleSection.findElement(By.tagName("input"));
    }
}
