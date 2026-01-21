package teammates.e2e.pageobjects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.util.Const;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackResponse;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.questions.FeedbackConstantSumQuestion;
import teammates.storage.sqlentity.questions.FeedbackContributionQuestion;
import teammates.storage.sqlentity.questions.FeedbackMcqQuestion;
import teammates.storage.sqlentity.questions.FeedbackMsqQuestion;
import teammates.storage.sqlentity.questions.FeedbackNumericalScaleQuestion;
import teammates.storage.sqlentity.questions.FeedbackRankOptionsQuestion;
import teammates.storage.sqlentity.questions.FeedbackRankRecipientsQuestion;
import teammates.storage.sqlentity.questions.FeedbackRubricQuestion;
import teammates.storage.sqlentity.questions.FeedbackTextQuestion;
import teammates.storage.sqlentity.responses.FeedbackConstantSumResponse;
import teammates.storage.sqlentity.responses.FeedbackContributionResponse;
import teammates.storage.sqlentity.responses.FeedbackMcqResponse;
import teammates.storage.sqlentity.responses.FeedbackMsqResponse;
import teammates.storage.sqlentity.responses.FeedbackNumericalScaleResponse;
import teammates.storage.sqlentity.responses.FeedbackRankOptionsResponse;
import teammates.storage.sqlentity.responses.FeedbackRankRecipientsResponse;
import teammates.storage.sqlentity.responses.FeedbackRubricResponse;
import teammates.storage.sqlentity.responses.FeedbackTextResponse;

/**
 * Represents the feedback submission page of the website.
 */
public class FeedbackSubmitPageSql extends AppPage {

    public FeedbackSubmitPageSql(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        if (isElementPresent(By.className("modal-content"))) {
            waitForConfirmationModalAndClickOk();
        }
        return getPageTitle().contains("Submit Feedback");
    }

    public void verifyFeedbackSessionDetails(FeedbackSession feedbackSession, Course course) {
        assertEquals(getCourseId(), feedbackSession.getCourseId());
        assertEquals(getCourseName(), course.getName());
        assertEquals(getCourseInstitute(), course.getInstitute());
        assertEquals(getFeedbackSessionName(), feedbackSession.getName());
        assertDateEquals(getOpeningTime(), feedbackSession.getStartTime(), course.getTimeZone());
        assertDateEquals(getClosingTime(), feedbackSession.getEndTime(), course.getTimeZone());
        assertEquals(getInstructions(), feedbackSession.getInstructions());
    }

    public void verifyNumQuestions(int expected) {
        assertEquals(expected, browser.driver.findElements(By.cssSelector("[id^='question-submission-form-qn-']")).size());
    }

    public void verifyQuestionDetails(int qnNumber, FeedbackQuestion feedbackQuestion) {
        assertEquals(getQuestionBrief(qnNumber), feedbackQuestion.getQuestionDetailsCopy().getQuestionText());
        verifyVisibilityList(qnNumber, feedbackQuestion);
        if (feedbackQuestion.getDescription() != null) {
            assertEquals(getQuestionDescription(qnNumber), feedbackQuestion.getDescription());
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

    public void verifyTextQuestion(int qnNumber, FeedbackTextQuestion question) {
        String recommendedLengthText = getQuestionForm(qnNumber).findElement(By.id("recommended-length")).getText();
        assertEquals(recommendedLengthText, "Recommended length for the answer: "
                + question.getFeedbackQuestionDetails().getRecommendedLength() + " words");
    }

    public void fillTextResponse(int qnNumber, String recipient, FeedbackResponse response) {
        FeedbackTextResponse responseEntity = (FeedbackTextResponse) response;
        String answer = responseEntity.getAnswer().getAnswer();
        writeToRichTextEditor(getTextResponseEditor(qnNumber, recipient), answer);
    }

    public void verifyTextResponse(int qnNumber, String recipient, FeedbackResponse response) {
        FeedbackTextResponse responseEntity = (FeedbackTextResponse) response;
        String answer = responseEntity.getAnswer().getAnswer();
        int responseLength = answer.split(" ").length;
        assertEquals(getEditorRichText(getTextResponseEditor(qnNumber, recipient)), answer);
        assertEquals(getResponseLengthText(qnNumber, recipient), "Response length: " + responseLength + " words");
    }

    public void verifyMcqQuestion(int qnNumber, String recipient, FeedbackMcqQuestion question) {
        List<String> mcqChoices = question.getFeedbackQuestionDetails().getMcqChoices();
        List<WebElement> optionTexts = getMcqOptions(qnNumber, recipient);

        for (int i = 0; i < mcqChoices.size(); i++) {
            assertEquals(mcqChoices.get(i), optionTexts.get(i).getText());
        }

        if (question.getFeedbackQuestionDetails().isOtherEnabled()) {
            assertEquals("Other", getMcqSection(qnNumber, recipient).findElement(By.id("other-option")).getText());
        }
    }

    public void verifyGeneratedMcqQuestion(int qnNumber, String recipient, List<String> options) {
        List<WebElement> optionTexts = getMcqOptions(qnNumber, recipient);
        for (int i = 0; i < options.size(); i++) {
            assertEquals(options.get(i), optionTexts.get(i).getText());
        }
    }

    public void fillMcqResponse(int qnNumber, String recipient, FeedbackResponse response) {
        FeedbackMcqResponse respEntity = (FeedbackMcqResponse) response;
        var responseDetails = respEntity.getAnswer();
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

    public void verifyMcqResponse(int qnNumber, String recipient, FeedbackResponse response) {
        FeedbackMcqResponse respEntity = (FeedbackMcqResponse) response;
        var responseDetails = respEntity.getAnswer();
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

    public void verifyMsqQuestion(int qnNumber, String recipient, FeedbackMsqQuestion question) {
        List<String> msqChoices = new java.util.ArrayList<>(question.getFeedbackQuestionDetails().getMsqChoices());
        if (question.getFeedbackQuestionDetails().isOtherEnabled()) {
            msqChoices.add("Other");
        }
        if (question.getFeedbackQuestionDetails().getMinSelectableChoices() == Const.POINTS_NO_VALUE) {
            msqChoices.add("None of the above");
        }
        List<WebElement> optionTexts = getMsqOptions(qnNumber, recipient);
        for (int i = 0; i < msqChoices.size(); i++) {
            assertEquals(msqChoices.get(i), optionTexts.get(i).getText());
        }
        verifyMsqSelectableOptionsMessage(qnNumber, question);
    }

    private void verifyMsqSelectableOptionsMessage(int qnNumber, FeedbackMsqQuestion question) {
        if (question.getFeedbackQuestionDetails().getMinSelectableChoices() != Const.POINTS_NO_VALUE) {
            assertEquals(getQuestionForm(qnNumber).findElement(By.id("min-options-message")).getText(),
                    "Choose at least " + question.getFeedbackQuestionDetails().getMinSelectableChoices() + " options.");
        }
        if (question.getFeedbackQuestionDetails().getMaxSelectableChoices() != Const.POINTS_NO_VALUE) {
            assertEquals(getQuestionForm(qnNumber).findElement(By.id("max-options-message")).getText(),
                    "Choose no more than " + question.getFeedbackQuestionDetails().getMaxSelectableChoices() + " options.");
        }
    }

    public void verifyGeneratedMsqQuestion(int qnNumber, String recipient, FeedbackMsqQuestion questionDetails,
                                           List<String> options) {
        List<WebElement> optionTexts = getMsqOptions(qnNumber, recipient);
        for (int i = 0; i < options.size(); i++) {
            assertEquals(options.get(i), optionTexts.get(i).getText());
        }
        verifyMsqSelectableOptionsMessage(qnNumber, questionDetails);
    }

    public void fillMsqResponse(int qnNumber, String recipient, FeedbackResponse response) {
        FeedbackMsqResponse respEntity = (FeedbackMsqResponse) response;
        var responseDetails = respEntity.getAnswer();
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

    public void verifyMsqResponse(int qnNumber, String recipient, FeedbackResponse response) {
        FeedbackMsqResponse respEntity = (FeedbackMsqResponse) response;
        var responseDetails = respEntity.getAnswer();
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
                                       FeedbackNumericalScaleQuestion question) {
        double step = question.getFeedbackQuestionDetails().getStep();
        double twoSteps = 2 * step;
        double min = question.getFeedbackQuestionDetails().getMinScale();
        double max = question.getFeedbackQuestionDetails().getMaxScale();
        String possibleValues = String.format("Possible values: [%s, %s, %s, ..., %s, %s, %s]",
                getDoubleString(min), getDoubleString(min + step), getDoubleString(min + twoSteps),
                getDoubleString(max - twoSteps), getDoubleString(max - step), getDoubleString(max));
        String actualValues = getNumScaleSection(qnNumber, recipient).findElement(By.id("possible-values")).getText();
        assertEquals(actualValues, possibleValues);
    }

    public void fillNumScaleResponse(int qnNumber, String recipient, FeedbackResponse response) {
        FeedbackNumericalScaleResponse respEntity = (FeedbackNumericalScaleResponse) response;
        double ans = respEntity.getAnswer().getAnswer();
        fillTextBox(getNumScaleInput(qnNumber, recipient), Double.toString(ans));
    }

    public void verifyNumScaleResponse(int qnNumber, String recipient, FeedbackResponse response) {
        FeedbackNumericalScaleResponse respEntity = (FeedbackNumericalScaleResponse) response;
        double ans = respEntity.getAnswer().getAnswer();
        assertEquals(getNumScaleInput(qnNumber, recipient).getAttribute("value"), getDoubleString(ans));
    }

    public void verifyConstSumQuestion(int qnNumber, String recipient,
                                       FeedbackConstantSumQuestion question) {
        var details = question.getFeedbackQuestionDetails();
        if (!details.isDistributeToRecipients()) {
            List<String> constSumOptions = details.getConstSumOptions();
            List<WebElement> optionTexts = getConstSumOptions(qnNumber, recipient);
            for (int i = 0; i < constSumOptions.size(); i++) {
                assertEquals(constSumOptions.get(i), optionTexts.get(i).getText());
            }
        }

        int totalPoints = details.getPoints();
        if (details.isPointsPerOption()) {
            totalPoints *= details.getNumOfConstSumOptions();
        }
        assertEquals(getQuestionForm(qnNumber).findElement(By.id("total-points-message")).getText(),
                "Total points distributed should add up to " + totalPoints + ".");

        if (details.isForceUnevenDistribution()) {
            String entityType = details.isDistributeToRecipients() ? "recipient" : "option";
            if ("All options".equals(details.getDistributePointsFor())) {
                assertEquals(getQuestionForm(qnNumber).findElement(By.id("all-uneven-message")).getText(),
                        "Every " + entityType + " should be allocated different number of points.");
            } else {
                assertEquals(getQuestionForm(qnNumber).findElement(By.id("one-uneven-message")).getText(),
                        "At least one " + entityType + " should be allocated different number of points.");
            }
        }
    }

    public void fillConstSumOptionResponse(int qnNumber, String recipient, FeedbackResponse response) {
        FeedbackConstantSumResponse respEntity = (FeedbackConstantSumResponse) response;
        List<Integer> answers = respEntity.getAnswer().getAnswers();
        List<WebElement> constSumInputs = getConstSumInputs(qnNumber, recipient);
        for (int i = 0; i < answers.size(); i++) {
            fillTextBox(constSumInputs.get(i), Integer.toString(answers.get(i)));
        }
    }

    public void verifyConstSumOptionResponse(int qnNumber, String recipient, FeedbackResponse response) {
        FeedbackConstantSumResponse respEntity = (FeedbackConstantSumResponse) response;
        List<Integer> answers = respEntity.getAnswer().getAnswers();
        List<WebElement> constSumInputs = getConstSumInputs(qnNumber, recipient);
        for (int i = 0; i < answers.size(); i++) {
            assertEquals(constSumInputs.get(i).getAttribute("value"), Integer.toString(answers.get(i)));
        }
    }

    public void fillConstSumRecipientResponse(int qnNumber, List<FeedbackResponse> responses) {
        List<WebElement> recipientInputs = getConstSumRecipientInputs(qnNumber);
        for (int i = 0; i < responses.size(); i++) {
            FeedbackConstantSumResponse respEntity = (FeedbackConstantSumResponse) responses.get(i);
            fillTextBox(recipientInputs.get(i), Integer.toString(respEntity.getAnswer().getAnswers().get(0)));
        }
    }

    public void verifyConstSumRecipientResponse(int qnNumber, List<FeedbackResponse> responses) {
        List<WebElement> recipientInputs = getConstSumRecipientInputs(qnNumber);
        for (int i = 0; i < responses.size(); i++) {
            FeedbackConstantSumResponse respEntity = (FeedbackConstantSumResponse) responses.get(i);
            assertEquals(recipientInputs.get(i).getAttribute("value"),
                    Integer.toString(respEntity.getAnswer().getAnswers().get(0)));
        }
    }

    public void verifyContributionQuestion(int qnNumber, FeedbackContributionQuestion question) {
        try {
            selectDropdownOptionByText(getContributionDropdowns(qnNumber).get(0), "Not Sure");
            assertTrue(question.getFeedbackQuestionDetails().isNotSureAllowed());
            assertFalse(question.getFeedbackQuestionDetails().isZeroSum());
        } catch (NoSuchElementException e) {
            assertFalse(question.getFeedbackQuestionDetails().isNotSureAllowed());
        }
    }

    public void fillContributionResponse(int qnNumber, List<FeedbackResponse> responses) {
        List<WebElement> dropdowns = getContributionDropdowns(qnNumber);
        for (int i = 0; i < responses.size(); i++) {
            FeedbackContributionResponse respEntity = (FeedbackContributionResponse) responses.get(i);
            selectDropdownOptionByText(dropdowns.get(i), getContributionString(respEntity.getAnswer().getAnswer()));
        }
    }

    public void verifyContributionResponse(int qnNumber, List<FeedbackResponse> responses) {
        List<WebElement> dropdowns = getContributionDropdowns(qnNumber);
        for (int i = 0; i < responses.size(); i++) {
            FeedbackContributionResponse respEntity = (FeedbackContributionResponse) responses.get(i);
            assertEquals(getSelectedDropdownOptionText(dropdowns.get(i)),
                    getContributionString(respEntity.getAnswer().getAnswer()));
        }
    }

    public void verifyRubricQuestion(int qnNumber, String recipient, FeedbackRubricQuestion question) {
        var details = question.getFeedbackQuestionDetails();
        List<String> choices = details.getRubricChoices();
        List<String> subQuestions = details.getRubricSubQuestions();
        List<List<String>> descriptions = details.getRubricDescriptions();

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

    public void fillRubricResponse(int qnNumber, String recipient, FeedbackResponse response) {
        FeedbackRubricResponse respEntity = (FeedbackRubricResponse) response;
        List<Integer> answers = respEntity.getAnswer().getAnswer();
        for (int i = 0; i < answers.size(); i++) {
            click(getRubricInputs(qnNumber, recipient, i + 2).get(answers.get(i)));
        }
    }

    public void verifyRubricResponse(int qnNumber, String recipient, FeedbackResponse response) {
        FeedbackRubricResponse respEntity = (FeedbackRubricResponse) response;
        List<Integer> answers = respEntity.getAnswer().getAnswer();
        for (int i = 0; i < answers.size(); i++) {
            assertTrue(getRubricInputs(qnNumber, recipient, i + 2).get(answers.get(i)).isSelected());
        }
    }

    public void verifyRankQuestion(int qnNumber, String recipient, FeedbackQuestion question) {
        if (question instanceof FeedbackRankOptionsQuestion) {
            var details = ((FeedbackRankOptionsQuestion) question).getFeedbackQuestionDetails();
            if (details.getMaxOptionsToBeRanked() != Const.POINTS_NO_VALUE) {
                assertEquals(getQuestionForm(qnNumber).findElement(By.id("max-options-message")).getText(),
                        "Rank no more than " + details.getMaxOptionsToBeRanked() + " options.");
            }
            if (details.getMinOptionsToBeRanked() != Const.POINTS_NO_VALUE) {
                assertEquals(getQuestionForm(qnNumber).findElement(By.id("min-options-message")).getText(),
                        "Rank at least " + details.getMinOptionsToBeRanked() + " options.");
            }
            List<String> options = details.getOptions();
            List<WebElement> optionTexts = getRankOptions(qnNumber, recipient);
            for (int i = 0; i < options.size(); i++) {
                assertEquals(options.get(i), optionTexts.get(i).getText());
            }
        } else if (question instanceof FeedbackRankRecipientsQuestion) {
            var details = ((FeedbackRankRecipientsQuestion) question).getFeedbackQuestionDetails();
            if (details.getMaxOptionsToBeRanked() != Const.POINTS_NO_VALUE) {
                assertEquals(getQuestionForm(qnNumber).findElement(By.id("max-options-message")).getText(),
                        "Rank no more than " + details.getMaxOptionsToBeRanked() + " options.");
            }
            if (details.getMinOptionsToBeRanked() != Const.POINTS_NO_VALUE) {
                assertEquals(getQuestionForm(qnNumber).findElement(By.id("min-options-message")).getText(),
                        "Rank at least " + details.getMinOptionsToBeRanked() + " options.");
            }
        }
    }

    public void fillRankOptionResponse(int qnNumber, String recipient, FeedbackResponse response) {
        FeedbackRankOptionsResponse respEntity = (FeedbackRankOptionsResponse) response;
        List<Integer> answers = respEntity.getAnswer().getAnswers();
        for (int i = 0; i < answers.size(); i++) {
            if (answers.get(i) == Const.POINTS_NOT_SUBMITTED) {
                selectDropdownOptionByText(getRankOptionsDropdowns(qnNumber, recipient).get(i), "");
            } else {
                selectDropdownOptionByText(getRankOptionsDropdowns(qnNumber, recipient).get(i),
                        Integer.toString(answers.get(i)));
            }
        }
    }

    public void verifyRankOptionResponse(int qnNumber, String recipient, FeedbackResponse response) {
        FeedbackRankOptionsResponse respEntity = (FeedbackRankOptionsResponse) response;
        List<Integer> answers = respEntity.getAnswer().getAnswers();
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

    public void fillRankRecipientResponse(int qnNumber, List<FeedbackResponse> responses) {
        List<WebElement> recipientDropdowns = getRankRecipientDropdowns(qnNumber);
        for (int i = 0; i < responses.size(); i++) {
            FeedbackRankRecipientsResponse respEntity = (FeedbackRankRecipientsResponse) responses.get(i);
            int answer = respEntity.getAnswer().getAnswer();
            if (answer == Const.POINTS_NOT_SUBMITTED) {
                selectDropdownOptionByText(recipientDropdowns.get(i), "");
            } else {
                selectDropdownOptionByText(recipientDropdowns.get(i), Integer.toString(answer));
            }
        }
    }

    public void verifyRankRecipientResponse(int qnNumber, List<FeedbackResponse> responses) {
        List<WebElement> recipientDropdowns = getRankRecipientDropdowns(qnNumber);
        for (int i = 0; i < responses.size(); i++) {
            FeedbackRankRecipientsResponse respEntity = (FeedbackRankRecipientsResponse) responses.get(i);
            int answer = respEntity.getAnswer().getAnswer();
            if (answer == Const.POINTS_NOT_SUBMITTED) {
                assertEquals(getSelectedDropdownOptionText(recipientDropdowns.get(i)), "");
            } else {
                assertEquals(getSelectedDropdownOptionText(recipientDropdowns.get(i)),
                        Integer.toString(answer));
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

    private void verifyVisibilityList(int qnNumber, FeedbackQuestion feedbackQuestion) {
        if (feedbackQuestion.getShowResponsesTo().isEmpty()) {
            verifyVisibilityStringPresent(qnNumber, "No-one can see your responses");
        }
        if (feedbackQuestion.getRecipientType().equals(FeedbackParticipantType.SELF)) {
            verifyVisibilityStringPresent(qnNumber, "You can see your own feedback in the results page later on.");
        }
        for (FeedbackParticipantType viewerType : feedbackQuestion.getShowResponsesTo()) {
            verifyVisibilityStringPresent(qnNumber, getVisibilityString(feedbackQuestion, viewerType));
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

    private String getVisibilityString(FeedbackQuestion feedbackQuestion,
                                       FeedbackParticipantType viewerType) {
        if (!feedbackQuestion.getShowResponsesTo().contains(viewerType)) {
            return "";
        }

        StringBuilder message = new StringBuilder(getViewerString(viewerType, feedbackQuestion.getRecipientType()));
        message.append(" can see your response");
        if (feedbackQuestion.getShowRecipientNameTo().contains(viewerType)) {
            message.append(", the name of the recipient");
            if (feedbackQuestion.getShowGiverNameTo().contains(viewerType)) {
                message.append(", and your name");
            } else {
                message.append(", but not your name");
            }
        } else {
            if (feedbackQuestion.getShowGiverNameTo().contains(viewerType)) {
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
