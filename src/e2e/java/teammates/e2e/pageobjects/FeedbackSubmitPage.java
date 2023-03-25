package teammates.e2e.pageobjects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.questions.FeedbackConstantSumQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackConstantSumResponseDetails;
import teammates.common.datatransfer.questions.FeedbackContributionQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackContributionResponseDetails;
import teammates.common.datatransfer.questions.FeedbackMcqQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackMcqResponseDetails;
import teammates.common.datatransfer.questions.FeedbackMsqQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackMsqResponseDetails;
import teammates.common.datatransfer.questions.FeedbackNumericalScaleQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackNumericalScaleResponseDetails;
import teammates.common.datatransfer.questions.FeedbackRankOptionsQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackRankOptionsResponseDetails;
import teammates.common.datatransfer.questions.FeedbackRankQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackRankRecipientsResponseDetails;
import teammates.common.datatransfer.questions.FeedbackRubricQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackRubricResponseDetails;
import teammates.common.datatransfer.questions.FeedbackTextQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackTextResponseDetails;
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
        if (isElementPresent(By.className("modal-content"))) {
            waitForConfirmationModalAndClickOk();
        }
        return getPageTitle().contains("Submit Feedback");
    }

    public void verifyFeedbackSessionDetails(FeedbackSessionAttributes feedbackSession, CourseAttributes course) {
        assertEquals(getCourseId(), feedbackSession.getCourseId());
        assertEquals(getCourseName(), course.getName());
        assertEquals(getCourseInstitute(), course.getInstitute());
        assertEquals(getFeedbackSessionName(), feedbackSession.getFeedbackSessionName());
        assertDateEquals(getOpeningTime(), feedbackSession.getStartTime(), feedbackSession.getTimeZone());
        assertDateEquals(getClosingTime(), feedbackSession.getEndTime(), feedbackSession.getTimeZone());
        assertEquals(getInstructions(), feedbackSession.getInstructions());
    }

    public void verifyNumQuestions(int expected) {
        assertEquals(browser.driver.findElements(By.cssSelector("[id^='question-submission-form-qn-']")).size(), expected);
    }

    public void verifyQuestionDetails(int qnNumber, FeedbackQuestionAttributes questionAttributes) {
        assertEquals(getQuestionBrief(qnNumber), questionAttributes.getQuestionDetailsCopy().getQuestionText());
        verifyVisibilityList(qnNumber, questionAttributes);
        if (questionAttributes.getQuestionDescription() != null) {
            assertEquals(getQuestionDescription(qnNumber), questionAttributes.getQuestionDescription());
        }
    }

    public void verifyLimitedRecipients(int qnNumber, int numRecipients, List<String> recipientNames) {
        List<WebElement> recipientDropdowns = getQuestionForm(qnNumber)
                .findElements(By.cssSelector("[id^='recipient-dropdown-qn-']"));
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
                    questionForm.findElement(By.id("recipient-name-qn-" + qnNumber + "-idx-" + i)).getText());
        }
    }

    public void verifyWarningMessageForPartialResponse(int[] unansweredQuestions) {
        click(getSubmitAllQuestionsButton());
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
        WebElement submitButton = waitForElementPresence(By.cssSelector("[id^='btn-submit-qn-']"));
        if (submitButton != null) {
            assertFalse(submitButton.isEnabled());
        }
    }

    public void addComment(int qnNumber, String recipient, String newComment) {
        WebElement commentSection = getCommentSection(qnNumber, recipient);
        click(commentSection.findElement(By.className("btn-add-comment")));
        writeToCommentEditor(commentSection, newComment);
    }

    public void editComment(int qnNumber, String recipient, String editedComment) {
        WebElement commentSection = getCommentSection(qnNumber, recipient);
        click(commentSection.findElement(By.className("btn-edit-comment")));
        writeToCommentEditor(commentSection, editedComment);
    }

    public void deleteComment(int qnNumber, String recipient) {
        clickAndConfirm(getCommentSection(qnNumber, recipient).findElement(By.className("btn-delete-comment")));
    }

    public void verifyComment(int qnNumber, String recipient, String expectedComment) {
        WebElement commentSection = getCommentSection(qnNumber, recipient);
        String actualComment = commentSection.findElement(By.className("comment-text")).getAttribute("innerHTML");
        assertEquals(expectedComment, actualComment);
    }

    public void verifyNoCommentPresent(int qnNumber, String recipient) {
        int numComments = getCommentSection(qnNumber, recipient).findElements(By.className("comment-text")).size();
        assertEquals(numComments, 0);
    }

    public void verifyTextQuestion(int qnNumber, FeedbackTextQuestionDetails questionDetails) {
        String recommendedLengthText = getQuestionForm(qnNumber).findElement(By.id("recommended-length")).getText();
        assertEquals(recommendedLengthText, "Recommended length for the answer: "
                + questionDetails.getRecommendedLength() + " words");
    }

    public void fillTextResponse(int qnNumber, String recipient, FeedbackResponseAttributes response) {
        FeedbackTextResponseDetails responseDetails = (FeedbackTextResponseDetails) response.getResponseDetailsCopy();
        writeToRichTextEditor(getTextResponseEditor(qnNumber, recipient), responseDetails.getAnswer());
    }

    public void verifyTextResponse(int qnNumber, String recipient, FeedbackResponseAttributes response) {
        FeedbackTextResponseDetails responseDetails = (FeedbackTextResponseDetails) response.getResponseDetailsCopy();
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

    public void fillMcqResponse(int qnNumber, String recipient, FeedbackResponseAttributes response) {
        FeedbackMcqResponseDetails responseDetails = (FeedbackMcqResponseDetails) response.getResponseDetailsCopy();
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
    }

    public void verifyMcqResponse(int qnNumber, String recipient, FeedbackResponseAttributes response) {
        FeedbackMcqResponseDetails responseDetails = (FeedbackMcqResponseDetails) response.getResponseDetailsCopy();
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
        if (questionDetails.getMinSelectableChoices() == Const.POINTS_NO_VALUE) {
            msqChoices.add("None of the above");
        }
        List<WebElement> optionTexts = getMsqOptions(qnNumber, recipient);
        for (int i = 0; i < msqChoices.size(); i++) {
            assertEquals(msqChoices.get(i), optionTexts.get(i).getText());
        }
        verifyMsqSelectableOptionsMessage(qnNumber, questionDetails);
    }

    private void verifyMsqSelectableOptionsMessage(int qnNumber, FeedbackMsqQuestionDetails questionDetails) {
        if (questionDetails.getMinSelectableChoices() != Const.POINTS_NO_VALUE) {
            assertEquals(getQuestionForm(qnNumber).findElement(By.id("min-options-message")).getText(),
                    "Choose at least " + questionDetails.getMinSelectableChoices() + " options.");
        }
        if (questionDetails.getMaxSelectableChoices() != Const.POINTS_NO_VALUE) {
            assertEquals(getQuestionForm(qnNumber).findElement(By.id("max-options-message")).getText(),
                    "Choose no more than " + questionDetails.getMaxSelectableChoices() + " options.");
        }
    }

    public void verifyGeneratedMsqQuestion(int qnNumber, String recipient, FeedbackMsqQuestionDetails questionDetails,
                                           List<String> options) {
        List<WebElement> optionTexts = getMsqOptions(qnNumber, recipient);
        for (int i = 0; i < options.size(); i++) {
            assertEquals(options.get(i), optionTexts.get(i).getText());
        }
        verifyMsqSelectableOptionsMessage(qnNumber, questionDetails);
    }

    public void fillMsqResponse(int qnNumber, String recipient, FeedbackResponseAttributes response) {
        FeedbackMsqResponseDetails responseDetails = (FeedbackMsqResponseDetails) response.getResponseDetailsCopy();
        List<String> answers = responseDetails.getAnswers();
        if (answers.get(0).isEmpty()) {
            answers.add("None of the above");
        }
        List<WebElement> optionTexts = getMsqOptions(qnNumber, recipient);
        List<WebElement> checkboxes = getMsqCheckboxes(qnNumber, recipient);
        for (int i = 0; i < optionTexts.size(); i++) {
            if (answers.contains(optionTexts.get(i).getText())) {
                markOptionAsSelected(checkboxes.get(i));
            } else {
                markOptionAsUnselected(checkboxes.get(i));
            }
        }
        if (responseDetails.isOther()) {
            markOptionAsSelected(getMsqOtherOptionCheckbox(qnNumber, recipient));
            fillTextBox(getMsqOtherOptionTextbox(qnNumber, recipient), responseDetails.getOtherFieldContent());
        }
    }

    public void verifyMsqResponse(int qnNumber, String recipient, FeedbackResponseAttributes response) {
        FeedbackMsqResponseDetails responseDetails = (FeedbackMsqResponseDetails) response.getResponseDetailsCopy();
        List<String> answers = responseDetails.getAnswers();
        if (answers.get(0).isEmpty()) {
            answers.add("None of the above");
        }
        List<WebElement> optionTexts = getMsqOptions(qnNumber, recipient);
        List<WebElement> checkboxes = getMsqCheckboxes(qnNumber, recipient);
        for (int i = 0; i < optionTexts.size(); i++) {
            if (answers.contains(optionTexts.get(i).getText())) {
                assertTrue(checkboxes.get(i).isSelected());
            } else if ("Other".equals(optionTexts.get(i).getText())) {
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

    public void fillNumScaleResponse(int qnNumber, String recipient, FeedbackResponseAttributes response) {
        FeedbackNumericalScaleResponseDetails responseDetails =
                (FeedbackNumericalScaleResponseDetails) response.getResponseDetailsCopy();
        fillTextBox(getNumScaleInput(qnNumber, recipient), Double.toString(responseDetails.getAnswer()));
    }

    public void verifyNumScaleResponse(int qnNumber, String recipient, FeedbackResponseAttributes response) {
        FeedbackNumericalScaleResponseDetails responseDetails =
                (FeedbackNumericalScaleResponseDetails) response.getResponseDetailsCopy();
        assertEquals(getNumScaleInput(qnNumber, recipient).getAttribute("value"),
                getDoubleString(responseDetails.getAnswer()));
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
            if ("All options".equals(questionDetails.getDistributePointsFor())) {
                assertEquals(getQuestionForm(qnNumber).findElement(By.id("all-uneven-message")).getText(),
                        "Every " + entityType + " should be allocated different number of points.");
            } else {
                assertEquals(getQuestionForm(qnNumber).findElement(By.id("one-uneven-message")).getText(),
                        "At least one " + entityType + " should be allocated different number of points.");
            }
        }
    }

    public void fillConstSumOptionResponse(int qnNumber, String recipient, FeedbackResponseAttributes response) {
        FeedbackConstantSumResponseDetails responseDetails =
                (FeedbackConstantSumResponseDetails) response.getResponseDetailsCopy();
        List<Integer> answers = responseDetails.getAnswers();
        List<WebElement> constSumInputs = getConstSumInputs(qnNumber, recipient);
        for (int i = 0; i < answers.size(); i++) {
            fillTextBox(constSumInputs.get(i), Integer.toString(answers.get(i)));
        }
    }

    public void verifyConstSumOptionResponse(int qnNumber, String recipient, FeedbackResponseAttributes response) {
        FeedbackConstantSumResponseDetails responseDetails =
                (FeedbackConstantSumResponseDetails) response.getResponseDetailsCopy();
        List<Integer> answers = responseDetails.getAnswers();
        List<WebElement> constSumInputs = getConstSumInputs(qnNumber, recipient);
        for (int i = 0; i < answers.size(); i++) {
            assertEquals(constSumInputs.get(i).getAttribute("value"), Integer.toString(answers.get(i)));
        }
    }

    public void fillConstSumRecipientResponse(int qnNumber, List<FeedbackResponseAttributes> responses) {
        List<WebElement> recipientInputs = getConstSumRecipientInputs(qnNumber);
        for (int i = 0; i < responses.size(); i++) {
            FeedbackConstantSumResponseDetails response =
                    (FeedbackConstantSumResponseDetails) responses.get(i).getResponseDetailsCopy();
            fillTextBox(recipientInputs.get(i), Integer.toString(response.getAnswers().get(0)));
        }
    }

    public void verifyConstSumRecipientResponse(int qnNumber, List<FeedbackResponseAttributes> responses) {
        List<WebElement> recipientInputs = getConstSumRecipientInputs(qnNumber);
        for (int i = 0; i < responses.size(); i++) {
            FeedbackConstantSumResponseDetails response =
                    (FeedbackConstantSumResponseDetails) responses.get(i).getResponseDetailsCopy();
            assertEquals(recipientInputs.get(i).getAttribute("value"),
                    Integer.toString(response.getAnswers().get(0)));
        }
    }

    public void verifyContributionQuestion(int qnNumber, FeedbackContributionQuestionDetails questionDetails) {
        try {
            selectDropdownOptionByText(getContributionDropdowns(qnNumber).get(0), "Not Sure");
            assertTrue(questionDetails.isNotSureAllowed());
            assertFalse(questionDetails.isZeroSum());
        } catch (NoSuchElementException e) {
            assertFalse(questionDetails.isNotSureAllowed());
        }
    }

    public void fillContributionResponse(int qnNumber, List<FeedbackResponseAttributes> responses) {
        List<WebElement> dropdowns = getContributionDropdowns(qnNumber);
        for (int i = 0; i < responses.size(); i++) {
            FeedbackContributionResponseDetails response =
                    (FeedbackContributionResponseDetails) responses.get(i).getResponseDetailsCopy();
            selectDropdownOptionByText(dropdowns.get(i), getContributionString(response.getAnswer()));
        }
    }

    public void verifyContributionResponse(int qnNumber, List<FeedbackResponseAttributes> responses) {
        List<WebElement> dropdowns = getContributionDropdowns(qnNumber);
        for (int i = 0; i < responses.size(); i++) {
            FeedbackContributionResponseDetails response =
                    (FeedbackContributionResponseDetails) responses.get(i).getResponseDetailsCopy();
            assertEquals(getSelectedDropdownOptionText(dropdowns.get(i)), getContributionString(response.getAnswer()));
        }
    }

    public void verifyRubricQuestion(int qnNumber, String recipient, FeedbackRubricQuestionDetails questionDetails) {
        List<String> choices = questionDetails.getRubricChoices();
        List<String> subQuestions = questionDetails.getRubricSubQuestions();
        List<List<String>> descriptions = questionDetails.getRubricDescriptions();

        String[][] expectedTableData = new String[subQuestions.size()][choices.size()];
        String[][] expectedTableRowHeader = new String[1][choices.size()];
        String[][] expectedTableColumnHeader = new String[subQuestions.size()][1];

        for (int i = 0; i < choices.size(); i++) {
            expectedTableRowHeader[0][i] = choices.get(i);
        }
        for (int i = 0; i < subQuestions.size(); i++) {
            expectedTableColumnHeader[i][0] = subQuestions.get(i);
        }
        for (int i = 0; i < descriptions.size(); i++) {
            List<String> description = descriptions.get(i);
            for (int j = 0; j < description.size(); j++) {
                expectedTableData[i][j] = description.get(j);
            }
        }
        WebElement rubricTable = getRubricTable(qnNumber, recipient);
        verifyTableBodyValues(rubricTable, expectedTableData);
        verifyTableRowHeaderValues(rubricTable, expectedTableRowHeader);
        verifyTableColumnHeaderValues(rubricTable, expectedTableColumnHeader);
    }

    public void fillRubricResponse(int qnNumber, String recipient, FeedbackResponseAttributes response) {
        FeedbackRubricResponseDetails responseDetails =
                (FeedbackRubricResponseDetails) response.getResponseDetailsCopy();
        List<Integer> answers = responseDetails.getAnswer();
        for (int i = 0; i < answers.size(); i++) {
            click(getRubricInputs(qnNumber, recipient, i + 2).get(answers.get(i)));
        }
    }

    public void verifyRubricResponse(int qnNumber, String recipient, FeedbackResponseAttributes response) {
        FeedbackRubricResponseDetails responseDetails =
                (FeedbackRubricResponseDetails) response.getResponseDetailsCopy();
        List<Integer> answers = responseDetails.getAnswer();
        for (int i = 0; i < answers.size(); i++) {
            assertTrue(getRubricInputs(qnNumber, recipient, i + 2).get(answers.get(i)).isSelected());
        }
    }

    public void verifyRankQuestion(int qnNumber, String recipient, FeedbackRankQuestionDetails questionDetails) {
        if (questionDetails.getMaxOptionsToBeRanked() != Const.POINTS_NO_VALUE) {
            assertEquals(getQuestionForm(qnNumber).findElement(By.id("max-options-message")).getText(),
                    "Rank no more than " + questionDetails.getMaxOptionsToBeRanked() + " options.");
        }
        if (questionDetails.getMinOptionsToBeRanked() != Const.POINTS_NO_VALUE) {
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

    public void fillRankOptionResponse(int qnNumber, String recipient, FeedbackResponseAttributes response) {
        FeedbackRankOptionsResponseDetails responseDetails =
                (FeedbackRankOptionsResponseDetails) response.getResponseDetailsCopy();
        List<Integer> answers = responseDetails.getAnswers();
        for (int i = 0; i < answers.size(); i++) {
            if (answers.get(i) == Const.POINTS_NOT_SUBMITTED) {
                selectDropdownOptionByText(getRankOptionsDropdowns(qnNumber, recipient).get(i), "");
            } else {
                selectDropdownOptionByText(getRankOptionsDropdowns(qnNumber, recipient).get(i),
                        Integer.toString(answers.get(i)));
            }
        }
    }

    public void verifyRankOptionResponse(int qnNumber, String recipient, FeedbackResponseAttributes response) {
        FeedbackRankOptionsResponseDetails responseDetails =
                (FeedbackRankOptionsResponseDetails) response.getResponseDetailsCopy();
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

    public void fillRankRecipientResponse(int qnNumber, List<FeedbackResponseAttributes> responses) {
        List<WebElement> recipientDropdowns = getRankRecipientDropdowns(qnNumber);
        for (int i = 0; i < responses.size(); i++) {
            FeedbackRankRecipientsResponseDetails response =
                    (FeedbackRankRecipientsResponseDetails) responses.get(i).getResponseDetailsCopy();
            if (response.getAnswer() == Const.POINTS_NOT_SUBMITTED) {
                selectDropdownOptionByText(recipientDropdowns.get(i), "");
            } else {
                selectDropdownOptionByText(recipientDropdowns.get(i), Integer.toString(response.getAnswer()));
            }
        }
    }

    public void verifyRankRecipientResponse(int qnNumber, List<FeedbackResponseAttributes> responses) {
        List<WebElement> recipientDropdowns = getRankRecipientDropdowns(qnNumber);
        for (int i = 0; i < responses.size(); i++) {
            FeedbackRankRecipientsResponseDetails response =
                    (FeedbackRankRecipientsResponseDetails) responses.get(i).getResponseDetailsCopy();
            if (response.getAnswer() == Const.POINTS_NOT_SUBMITTED) {
                assertEquals(getSelectedDropdownOptionText(recipientDropdowns.get(i)), "");
            } else {
                assertEquals(getSelectedDropdownOptionText(recipientDropdowns.get(i)),
                        Integer.toString(response.getAnswer()));
            }
        }
    }

    private String getCourseId() {
        return browser.driver.findElement(By.id("course-id")).getText();
    }

    private String getCourseName() {
        return browser.driver.findElement(By.id("course-name")).getText();
    }

    private String getCourseInstitute() {
        return browser.driver.findElement(By.id("course-institute")).getText();
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

    private void assertDateEquals(String actual, Instant instant, String timeZone) {
        String dateStrWithAbbr = getDateStringWithAbbr(instant, timeZone);
        String dateStrWithOffset = getDateStringWithOffset(instant, timeZone);

        boolean isExpected = actual.equals(dateStrWithAbbr) || actual.equals(dateStrWithOffset);
        assertTrue(isExpected);
    }

    private String getDateStringWithAbbr(Instant instant, String timeZone) {
        return getDisplayedDateTime(instant, timeZone, "EE, dd MMM, yyyy, hh:mm a z");
    }

    private String getDateStringWithOffset(Instant instant, String timeZone) {
        return getDisplayedDateTime(instant, timeZone, "EE, dd MMM, yyyy, hh:mm a X");
    }

    private WebElement getQuestionForm(int qnNumber) {
        By questionFormId = By.id("question-submission-form-qn-" + qnNumber);
        waitForElementPresence(questionFormId);
        WebElement questionForm = browser.driver.findElement(questionFormId);
        // Scroll to the question to ensure that the details are fully loaded
        scrollElementToCenter(questionForm);
        waitUntilAnimationFinish();
        return questionForm;
    }

    private String getQuestionBrief(int qnNumber) {
        String questionDetails = getQuestionForm(qnNumber).findElement(By.className("question-details")).getText();
        return questionDetails.split(": ")[1];
    }

    private void verifyVisibilityList(int qnNumber, FeedbackQuestionAttributes questionAttributes) {
        if (questionAttributes.getShowResponsesTo().isEmpty()) {
            verifyVisibilityStringPresent(qnNumber, "No-one can see your responses");
        }
        if (questionAttributes.getRecipientType().equals(FeedbackParticipantType.SELF)) {
            verifyVisibilityStringPresent(qnNumber, "You can see your own feedback in the results page later on.");
        }
        for (FeedbackParticipantType viewerType : questionAttributes.getShowResponsesTo()) {
            verifyVisibilityStringPresent(qnNumber, getVisibilityString(questionAttributes, viewerType));
        }
    }

    private void verifyVisibilityStringPresent(int qnNumber, String expectedString) {
        List<WebElement> visibilityStrings = getQuestionForm(qnNumber).findElement(By.className("visibility-list"))
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
        if (!questionAttributes.getShowResponsesTo().contains(viewerType)) {
            return "";
        }

        StringBuilder message = new StringBuilder(getViewerString(viewerType, questionAttributes.getRecipientType()));
        message.append(" can see your response");
        if (questionAttributes.getShowRecipientNameTo().contains(viewerType)) {
            message.append(", the name of the recipient");
            if (questionAttributes.getShowGiverNameTo().contains(viewerType)) {
                message.append(", and your name");
            } else {
                message.append(", but not your name");
            }
        } else {
            if (questionAttributes.getShowGiverNameTo().contains(viewerType)) {
                message.append(", and your name, but not the name of the recipient");
            } else {
                message.append(", but not the name of the recipient, or your name");
            }
        }
        return message.toString();
    }

    private String getViewerString(FeedbackParticipantType viewerType, FeedbackParticipantType recipientType) {
        switch (viewerType) {
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
        switch (recipientType) {
        case TEAMS:
        case TEAMS_EXCLUDING_SELF:
        case TEAMS_IN_SAME_SECTION:
            return "teams";
        case OWN_TEAM_MEMBERS:
            return "student";
        case STUDENTS:
        case STUDENTS_EXCLUDING_SELF:
        case STUDENTS_IN_SAME_SECTION:
            return "students";
        case INSTRUCTORS:
            return "instructors";
        default:
            throw new RuntimeException("Unknown recipientType");
        }
    }

    public void clickSubmitQuestionButton(int qnNumber) {
        WebElement submitQnButton = waitForElementPresence(By.id("btn-submit-qn-" + qnNumber));
        clickAndConfirm(submitQnButton);
    }

    public void clickSubmitAllQuestionsButton() {
        clickAndConfirm(getSubmitAllQuestionsButton());
    }

    private WebElement getSubmitAllQuestionsButton() {
        return waitForElementPresence(By.id("btn-submit"));
    }

    private String getQuestionDescription(int qnNumber) {
        return getQuestionForm(qnNumber).findElement(By.className("question-description")).getAttribute("innerHTML");
    }

    private WebElement getCommentSection(int qnNumber, String recipient) {
        int recipientIndex = getRecipientIndex(qnNumber, recipient);
        return getQuestionForm(qnNumber).findElement(By.id("comment-section-qn-" + qnNumber + "-idx-" + recipientIndex));
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
            List<WebElement> recipientDropdowns =
                    questionForm.findElements(By.cssSelector("[id^='recipient-dropdown-qn-']"));
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
        int limit = 20; // we are not likely to set test data exceeding this number
        for (int i = 0; i < limit; i++) {
            if (questionForm.findElement(By.id("recipient-name-qn-" + qnNumber + "-idx-" + i))
                    .getText().contains(recipient)) {
                return i;
            }
        }
        return -1;
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

    private String getDoubleString(Double value) {
        return value % 1 == 0 ? Integer.toString(value.intValue()) : Double.toString(value);
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

    private List<WebElement> getRankRecipientDropdowns(int questionNum) {
        return getQuestionForm(questionNum).findElements(By.tagName("select"));
    }
}
