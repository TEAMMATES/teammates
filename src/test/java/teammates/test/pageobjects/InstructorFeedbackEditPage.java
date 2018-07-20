package teammates.test.pageobjects;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.common.util.TimeHelper;
import teammates.test.driver.TestProperties;
import teammates.test.driver.TimeHelperExtension;

public class InstructorFeedbackEditPage extends AppPage {

    public static final int NEW_QUESTION_NUM = -1;

    @FindBy(id = "starttime")
    private WebElement startTimeDropdown;

    @FindBy(id = "startdate")
    private WebElement startDateBox;

    @FindBy(id = "endtime")
    private WebElement endTimeDropdown;

    @FindBy(id = "enddate")
    private WebElement endDateBox;

    @FindBy(id = "timezone")
    private WebElement timezoneDropDown;

    @FindBy(id = "graceperiod")
    private WebElement gracePeriodDropdown;

    @FindBy(id = "editUncommonSettingsSessionResponsesVisibleButton")
    private WebElement uncommonSettingsSessionResponsesVisibleButton;

    @FindBy(id = "editUncommonSettingsSendEmailsButton")
    private WebElement uncommonSettingsSendEmailsButton;

    @FindBy(id = Const.ParamsNames.FEEDBACK_SESSION_SESSIONVISIBLEBUTTON + "_custom")
    private WebElement customSessionVisibleTimeButton;

    @FindBy(id = Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON + "_custom")
    private WebElement customResultsVisibleTimeButton;

    @FindBy(id = Const.ParamsNames.FEEDBACK_SESSION_SESSIONVISIBLEBUTTON + "_atopen")
    private WebElement defaultSessionVisibleTimeButton;

    @FindBy(id = Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON + "_atvisible")
    private WebElement defaultResultsVisibleTimeButton;

    @FindBy(id = Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON + "_later")
    private WebElement manualResultsVisibleTimeButton;

    @FindBy(id = Const.ParamsNames.FEEDBACK_SESSION_SENDREMINDEREMAIL + "_closing")
    private WebElement closingSessionEmailReminderButton;

    @FindBy(id = Const.ParamsNames.FEEDBACK_SESSION_SENDREMINDEREMAIL + "_published")
    private WebElement publishedSessionEmailReminderButton;

    @FindBy(id = "fsEditLink")
    private WebElement fsEditLink;

    @FindBy(id = "fsSaveLink")
    private WebElement fsSaveLink;

    @FindBy(id = "fsDeleteLink")
    private WebElement fsDeleteLink;

    @FindBy(id = "button_openframe")
    private WebElement openNewQuestionButton;

    @FindBy(id = "button_submit_add")
    private WebElement addNewQuestionButton;

    @FindBy(id = "button_done_editing")
    private WebElement doneEditingButton;

    @FindBy(id = "questiontext-" + NEW_QUESTION_NUM)
    private WebElement questionTextBoxForNewQuestion;

    @FindBy(id = "mcqOtherOptionFlag-" + NEW_QUESTION_NUM)
    private WebElement addMcqOtherOptionCheckboxForNewQuestion;

    @FindBy(id = "msqOtherOptionFlag-" + NEW_QUESTION_NUM)
    private WebElement addMsqOtherOptionCheckboxForNewQuestion;

    @FindBy(id = "givertype-" + NEW_QUESTION_NUM)
    private WebElement giverDropdownForNewQuestion;

    @FindBy(id = "recipienttype-" + NEW_QUESTION_NUM)
    private WebElement recipientDropdownForNewQuestion;

    @FindBy(xpath = "//input[@name='numofrecipientstype' and @value='max']")
    private WebElement maxNumOfRecipients;

    @FindBy(xpath = "//input[@name='numofrecipientstype' and @value='custom']")
    private WebElement customNumOfRecipients;

    @FindBy(id = "button_fscopy")
    private WebElement fscopyButton;

    @FindBy(id = "button_copy")
    private WebElement copyQuestionLoadButton;

    @FindBy(id = "button_copy_submit")
    private WebElement copyQuestionSubmitButton;

    @FindBy(id = "button_preview_student")
    private WebElement previewAsStudentButton;

    @FindBy(id = "button_preview_instructor")
    private WebElement previewAsInstructorButton;

    private InstructorCopyFsToModal fsCopyToModal;

    public InstructorFeedbackEditPage(Browser browser) {
        super(browser);
        fsCopyToModal = new InstructorCopyFsToModal(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("<h1>Edit Feedback Session</h1>");
    }

    public InstructorCopyFsToModal getFsCopyToModal() {
        return fsCopyToModal;
    }

    public String getCourseId() {
        return browser.driver.findElement(By.name("courseid")).getAttribute("value");
    }

    public String getFeedbackSessionName() {
        return browser.driver.findElement(By.name("fsname")).getAttribute("value");
    }

    /**
     * Returns number of question edit forms + question add form.
     */
    public int getNumberOfQuestionEditForms() {
        return browser.driver.findElements(By.className("questionTable")).size();
    }

    public boolean isCorrectPage(String courseId, String feedbackSessionName) {
        boolean isCorrectCourseId = this.getCourseId().equals(courseId);
        boolean isCorrectFeedbackSessionName = this.getFeedbackSessionName().equals(feedbackSessionName);
        return isCorrectCourseId && isCorrectFeedbackSessionName && containsExpectedPageContents();
    }

    public void fillQuestionTextBox(String qnText, int qnIndex) {
        WebElement questionTextBox = browser.driver.findElement(By.id("questiontext-" + qnIndex));
        fillTextBox(questionTextBox, qnText);
    }

    public void fillQuestionTextBoxForNewQuestion(String qnText) {
        fillTextBox(questionTextBoxForNewQuestion, qnText);
    }

    public void fillQuestionDescription(String qnDescription, int qnIndex) {
        fillRichTextEditor(Const.ParamsNames.FEEDBACK_QUESTION_DESCRIPTION + "-" + qnIndex, qnDescription);
    }

    public void fillQuestionDescriptionForNewQuestion(String qnDescription) {
        fillQuestionDescription(qnDescription, NEW_QUESTION_NUM);
    }

    public void fillNumOfEntitiesToGiveFeedbackToBoxForNewQuestion(String num) {
        WebElement questionForm = browser.driver.findElement(By.id("form_editquestion-" + NEW_QUESTION_NUM));
        WebElement numberOfRecipients = questionForm.findElement(By.className("numberOfEntitiesBox"));
        fillTextBox(numberOfRecipients, num);
    }

    public String getQuestionBoxText(int qnIndex) {
        WebElement questionEditTextBox = browser.driver.findElement(By.id("questiontext-" + qnIndex));
        return getTextBoxValue(questionEditTextBox);
    }

    private String getIdSuffix(int qnNumber) {
        boolean isValid = qnNumber > 0 || qnNumber == NEW_QUESTION_NUM;
        return isValid ? "-" + qnNumber : "";
    }

    public void fillMinNumScaleBox(String minScale, int qnNumber) {
        String idSuffix = getIdSuffix(qnNumber);

        WebElement minScaleBox = browser.driver.findElement(By.id("minScaleBox" + idSuffix));
        fillTextBox(minScaleBox, minScale);

        executeScript("$(arguments[0]).change();", minScaleBox);
    }

    public void fillMinNumScaleBox(int minScale, int qnNumber) {
        fillMinNumScaleBox(Integer.toString(minScale), qnNumber);
    }

    public void fillMinNumScaleBoxForNewQuestion(String minScale) {
        fillMinNumScaleBox(minScale, NEW_QUESTION_NUM);
    }

    public void fillMinNumScaleBoxForNewQuestion(int minScale) {
        fillMinNumScaleBox(minScale, NEW_QUESTION_NUM);
    }

    public void fillMaxNumScaleBox(String maxScale, int qnNumber) {
        String idSuffix = getIdSuffix(qnNumber);

        WebElement maxScaleBox = browser.driver.findElement(By.id("maxScaleBox" + idSuffix));
        fillTextBox(maxScaleBox, maxScale);

        executeScript("$(arguments[0]).change();", maxScaleBox);
    }

    public void fillMaxNumScaleBox(int maxScale, int qnNumber) {
        fillMaxNumScaleBox(Integer.toString(maxScale), qnNumber);
    }

    public void fillMaxNumScaleBoxForNewQuestion(String maxScale) {
        fillMaxNumScaleBox(maxScale, NEW_QUESTION_NUM);
    }

    public void fillMaxNumScaleBoxForNewQuestion(int maxScale) {
        fillMaxNumScaleBox(maxScale, NEW_QUESTION_NUM);
    }

    public String getMaxNumScaleBox(int qnNumber) {
        String idSuffix = getIdSuffix(qnNumber);
        WebElement maxScaleBox = browser.driver.findElement(By.id("maxScaleBox" + idSuffix));
        return maxScaleBox.getAttribute("value");
    }

    public String getMaxNumScaleBoxForNewQuestion() {
        return getMaxNumScaleBox(NEW_QUESTION_NUM);
    }

    public void fillStepNumScaleBox(String step, int qnNumber) {
        String idSuffix = getIdSuffix(qnNumber);

        WebElement stepBox = browser.driver.findElement(By.id("stepBox" + idSuffix));
        fillTextBox(stepBox, step);

        executeScript("$(arguments[0]).change();", stepBox);
    }

    public void fillStepNumScaleBox(double step, int qnNumber) {
        fillStepNumScaleBox(StringHelper.toDecimalFormatString(step), qnNumber);
    }

    public void fillStepNumScaleBoxForNewQuestion(String step) {
        fillStepNumScaleBox(step, NEW_QUESTION_NUM);
    }

    public void fillStepNumScaleBoxForNewQuestion(double step) {
        fillStepNumScaleBox(step, NEW_QUESTION_NUM);
    }

    public String getNumScalePossibleValuesString(int qnNumber) {
        String idSuffix = getIdSuffix(qnNumber);
        WebElement possibleValuesSpan = browser.driver.findElement(By.id("numScalePossibleValues" + idSuffix));
        return possibleValuesSpan.getText();
    }

    public String getNumScalePossibleValuesStringForNewQuestion() {
        return getNumScalePossibleValuesString(-1);
    }

    public void fillConstSumPointsBox(String points, int qnNumber) {
        String idSuffix = getIdSuffix(qnNumber);

        WebElement pointsBox = browser.driver.findElement(By.id("constSumPoints" + idSuffix));
        // Backspace to clear the extra 100 when box is cleared.
        fillTextBox(pointsBox, Keys.RIGHT + " " + Keys.BACK_SPACE + Keys.BACK_SPACE + Keys.BACK_SPACE + points);

        executeScript("$(arguments[0]).change();", pointsBox);
    }

    public void fillConstSumPointsBoxForNewQuestion(String points) {
        fillConstSumPointsBox(points, NEW_QUESTION_NUM);
    }

    public String getConstSumPointsBox(int qnNumber) {
        String idSuffix = getIdSuffix(qnNumber);
        WebElement constSumPointsBox = browser.driver.findElement(By.id("constSumPoints" + idSuffix));
        return constSumPointsBox.getAttribute("value");
    }

    public String getConstSumPointsBoxForNewQuestion() {
        return getConstSumPointsBox(NEW_QUESTION_NUM);
    }

    public void fillConstSumPointsForEachOptionBox(String points, int qnNumber) {
        String idSuffix = getIdSuffix(qnNumber);

        WebElement pointsBox = browser.driver.findElement(By.id("constSumPointsForEachOption" + idSuffix));
        // Backspace to clear the extra 100 when box is cleared.
        fillTextBox(pointsBox, Keys.RIGHT + " " + Keys.BACK_SPACE + Keys.BACK_SPACE + Keys.BACK_SPACE + points);

        executeScript("$(arguments[0]).change();", pointsBox);
    }

    public void fillConstSumPointsForEachOptionBoxForNewQuestion(String points) {
        fillConstSumPointsForEachOptionBox(points, NEW_QUESTION_NUM);
    }

    public String getConstSumPointsForEachOptionBox(int qnNumber) {
        String idSuffix = getIdSuffix(qnNumber);
        WebElement constSumPointsBox = browser.driver.findElement(By.id("constSumPointsForEachOption" + idSuffix));
        return constSumPointsBox.getAttribute("value");
    }

    public String getConstSumPointsForEachOptionBoxForNewQuestion() {
        return getConstSumPointsForEachOptionBox(NEW_QUESTION_NUM);
    }

    public void fillConstSumPointsForEachRecipientBox(String points, int qnNumber) {
        String idSuffix = getIdSuffix(qnNumber);

        WebElement pointsBox = browser.driver.findElement(By.id("constSumPointsForEachRecipient" + idSuffix));
        // Backspace to clear the extra 100 when box is cleared.
        fillTextBox(pointsBox, Keys.RIGHT + " " + Keys.BACK_SPACE + Keys.BACK_SPACE + Keys.BACK_SPACE + points);

        executeScript("$(arguments[0]).change();", pointsBox);
    }

    public void fillConstSumPointsForEachRecipientBoxForNewQuestion(String points) {
        fillConstSumPointsForEachRecipientBox(points, NEW_QUESTION_NUM);
    }

    public String getConstSumPointsForEachRecipientBox(int qnNumber) {
        String idSuffix = getIdSuffix(qnNumber);
        WebElement constSumPointsBox = browser.driver.findElement(By.id("constSumPointsForEachRecipient" + idSuffix));
        return constSumPointsBox.getAttribute("value");
    }

    public String getConstSumPointsForEachRecipientBoxForNewQuestion() {
        return getConstSumPointsForEachRecipientBox(NEW_QUESTION_NUM);
    }

    public boolean isRubricColLeftMovable(int qnNumber, int colNumber) {
        String elemId = Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_MOVE_COL_LEFT + "-" + qnNumber + "-" + colNumber;
        WebElement moveColButton = browser.driver.findElement(By.id(elemId));

        return moveColButton.getAttribute("disabled") == null;
    }

    public boolean isRubricColRightMovable(int qnNumber, int colNumber) {
        String elemId = Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_MOVE_COL_RIGHT + "-" + qnNumber + "-" + colNumber;
        WebElement moveColButton = browser.driver.findElement(By.id(elemId));

        return moveColButton.getAttribute("disabled") == null;
    }

    /**
     * Returns true if button is enabled and click was successful.
     */
    public boolean moveRubricColLeft(int qnNumber, int colNumber) {
        return moveRubricCol(qnNumber, colNumber, true);
    }

    /**
     * Returns true if button is enabled and click was successful.
     */
    public boolean moveRubricColRight(int qnNumber, int colNumber) {
        return moveRubricCol(qnNumber, colNumber, false);
    }

    private boolean moveRubricCol(int qnNumber, int colNumber, boolean isMoveLeft) {
        String elemId;

        if (isMoveLeft) {
            elemId = Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_MOVE_COL_LEFT + "-" + qnNumber + "-" + colNumber;
        } else {
            elemId = Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_MOVE_COL_RIGHT + "-" + qnNumber + "-" + colNumber;
        }

        WebElement moveColButton = browser.driver.findElement(By.id(elemId));

        if (moveColButton.isEnabled()) {
            click(moveColButton);

            return true;
        }

        return false;
    }

    private String getRubricSubQuestionSelectorId(int qnNumber, int subQnIndex) {
        String idSuffix = getIdSuffix(qnNumber);

        return Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_SUBQUESTION + idSuffix + "-" + subQnIndex;
    }

    public WebElement getRubricSubQuestionBox(int qnNumber, int subQnIndex) {
        return browser.driver.findElement(By.id(getRubricSubQuestionSelectorId(qnNumber, subQnIndex)));
    }

    private String getRubricChoice(int qnNumber, int colNumber) {
        String idSuffix = getIdSuffix(qnNumber);
        String elemId = Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_CHOICE + idSuffix + "-" + colNumber;

        return browser.driver.findElement(By.id(elemId)).getAttribute("value");
    }

    private String getRubricWeight(int qnNumber, int colNumber) {
        String idSuffix = getIdSuffix(qnNumber);
        String elemId = Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_WEIGHT + idSuffix + "-" + colNumber;

        return browser.driver.findElement(By.id(elemId)).getAttribute("value");
    }

    private String getRubricDescription(int qnNumber, int subQnIndex, int choiceIndex) {
        return getRubricDescriptionBox(qnNumber, subQnIndex, choiceIndex).getAttribute("value");
    }

    private boolean isRubricWeightsEnabled(int qnNumber) {
        return browser.driver.findElement(By.id("rubricAssignWeights-" + qnNumber)).isSelected();
    }

    private boolean isRubricDescriptionBoxPresent(int qnNumber, int subQnIndex, int choiceIndex) {
        return !browser.driver.findElements(By.id(getRubricDescriptionBoxId(qnNumber, subQnIndex, choiceIndex))).isEmpty();
    }

    public String[] getRubricColValues(int qnNumber, int choiceIndex) {
        List<String> col = new ArrayList<>();

        col.add(getRubricChoice(qnNumber, choiceIndex));

        if (isRubricWeightsEnabled(qnNumber)) {
            col.add(getRubricWeight(qnNumber, choiceIndex));
        }

        int subQnIndex = 0;

        while (isRubricDescriptionBoxPresent(qnNumber, subQnIndex, choiceIndex)) {
            col.add(getRubricDescription(qnNumber, subQnIndex, choiceIndex));
            subQnIndex++;
        }

        return col.toArray(new String[0]);
    }

    /**
     * Checks if move left button of leftmost column is disabled.
     * Checks if move right button of rightmost column is disabled.
     * Checks if all other move column buttons are enabled.
     * @param qnNumber question number.
     * @param colIndexes An array containing column indexes in the order displayed in the UI.
     */
    public void verifyRubricColumnsMovability(int qnNumber, int[] colIndexes) {
        int leftMostColIndex = colIndexes[0];
        int rightMostColIndex = colIndexes[colIndexes.length - 1];

        // checking move buttons of leftmost column
        assertFalse(isRubricColLeftMovable(qnNumber, leftMostColIndex));
        assertTrue(isRubricColRightMovable(qnNumber, leftMostColIndex));

        // checking move buttons of last column
        assertTrue(isRubricColLeftMovable(qnNumber, rightMostColIndex));
        assertFalse(isRubricColRightMovable(qnNumber, rightMostColIndex));

        // checking move buttons of remaining columns
        for (int i = 1; i < colIndexes.length - 1; i++) {
            assertTrue(isRubricColLeftMovable(qnNumber, colIndexes[i]));
            assertTrue(isRubricColRightMovable(qnNumber, colIndexes[i]));
        }
    }

    /**
     * Checks if the values given in {@code columns} argument match with the
     * actual values displayed. Also checks if move left/right buttons for
     * each column is properly enabled or disabled. Assumes that there are
     * at least 2 columns.
     * @param qnNumber question number.
     * @param colIndexes An array containing column indexes in the order displayed in the UI.
     * @param columns Varargs parameter, where each parameter is {@code String[]} which denotes values
     *         of a rubric column. Column values must be given in the order displayed in the UI.
     */
    public void verifyRubricQuestion(int qnNumber, int[] colIndexes, String[]... columns) {
        // checking rubric column values - choice, weight, descriptions
        for (int i = 0; i < colIndexes.length; i++) {
            int colIndex = colIndexes[i];
            String[] colValues = columns[i];
            int rubDescriptionIndex = 1;

            assertTrue(getRubricChoice(qnNumber, colIndex).equals(colValues[0]));

            if (isRubricWeightsEnabled(qnNumber)) {
                assertTrue(getRubricWeight(qnNumber, colIndex).equals(colValues[1]));
                rubDescriptionIndex = 2; // rubric descriptions start from index 2
            }

            int subQnIndex = 0;

            while (rubDescriptionIndex < colValues.length) {
                assertTrue(getRubricDescription(qnNumber, subQnIndex, colIndex).equals(colValues[rubDescriptionIndex]));
                rubDescriptionIndex++;
                subQnIndex++;
            }
        }

        verifyRubricColumnsMovability(qnNumber, colIndexes);
    }

    /**
     * Fills a single rubric column of index given by
     * {@code choiceIndex}, with the values given in {@code values}.
     * @param qnNumber question number.
     * @param choiceIndex index of the column as in UI.
     * @param values {@code String[]} which holds all column values from top to bottom.
     */
    public void fillRubricColumn(int qnNumber, int choiceIndex, String[] values) {
        fillRubricChoiceBox(values[0], qnNumber, choiceIndex);

        int rubDescriptionIndex = 1;

        if (isRubricWeightsEnabled(qnNumber)) {
            fillRubricWeightBox(values[1], qnNumber, choiceIndex);
            rubDescriptionIndex = 2; // rubric descriptions start from index 2
        }

        int subQnIndex = 0;

        while (rubDescriptionIndex < values.length) {
            fillRubricDescriptionBox(values[rubDescriptionIndex], qnNumber, subQnIndex, choiceIndex);
            subQnIndex++;
            rubDescriptionIndex++;
        }
    }

    /**
     * Fills all rubric columns with respective indexes given in
     * {@code colIndexes}, with respective values given in @{code colValues}.
     * @param qnNumber question number.
     * @param colIndexes An array containing column indexes in the order displayed in the UI.
     * @param colValues Varargs parameter, where each parameter is {@code String[]} which denotes values
     *         of a rubric column. Column values must be given in the order displayed in the UI.
     */
    public void fillAllRubricColumns(int qnNumber, int[] colIndexes, String[]... colValues) {
        Assumption.assertEquals(colIndexes.length, colValues.length);

        for (int i = 0; i < colIndexes.length; i++) {
            fillRubricColumn(qnNumber, colIndexes[i], colValues[i]);
        }
    }

    public boolean isRubricSubQuestionBoxFocused(int qnNumber, int subQnIndex) {
        WebElement subQnBox = getRubricSubQuestionBox(qnNumber, subQnIndex);

        return subQnBox.equals(browser.driver.switchTo().activeElement());
    }

    public void fillRubricSubQuestionBox(String subQuestion, int qnNumber, int subQnIndex) {
        WebElement subQnBox = getRubricSubQuestionBox(qnNumber, subQnIndex);

        fillTextBox(subQnBox, subQuestion);
    }

    public void fillRubricChoiceBox(String choice, int qnNumber, int choiceIndex) {
        String idSuffix = getIdSuffix(qnNumber);

        String elemId = Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_CHOICE + idSuffix + "-" + choiceIndex;

        WebElement subQnBox = browser.driver.findElement(By.id(elemId));
        fillTextBox(subQnBox, choice);
    }

    public WebElement getRubricWeightBox(int qnNumber, int choiceIndex) {
        String idSuffix = getIdSuffix(qnNumber);
        String elementId = Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_WEIGHT + idSuffix + "-" + choiceIndex;

        return browser.driver.findElement(By.id(elementId));
    }

    public void fillRubricWeightBox(String weight, int qnNumber, int choiceIndex) {
        WebElement weightBox = getRubricWeightBox(qnNumber, choiceIndex);
        fillTextBox(weightBox, weight);
    }

    public boolean isRubricWeightBoxFocused(int qnNumber, int choiceIndex) {
        WebElement weightBox = getRubricWeightBox(qnNumber, choiceIndex);

        return weightBox.equals(browser.driver.switchTo().activeElement());
    }

    public void fillRubricWeightBoxForNewQuestion(String weight, int choiceIndex) {
        fillRubricWeightBox(weight, NEW_QUESTION_NUM, choiceIndex);
    }

    private String getRubricDescriptionBoxId(int qnNumber, int subQnIndex, int choiceIndex) {
        String idSuffix = getIdSuffix(qnNumber);

        return Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_DESCRIPTION + idSuffix + "-" + subQnIndex + "-" + choiceIndex;
    }

    private WebElement getRubricDescriptionBox(int qnNumber, int subQnIndex, int choiceIndex) {
        return browser.driver.findElement(By.id(getRubricDescriptionBoxId(qnNumber, subQnIndex, choiceIndex)));
    }

    public void fillRubricDescriptionBox(String description, int qnNumber, int subQnIndex, int choiceIndex) {
        WebElement descriptionBox = getRubricDescriptionBox(qnNumber, subQnIndex, choiceIndex);
        fillTextBox(descriptionBox, description);
    }

    public void clickQuestionEditForQuestion(int qnNumber) {
        WebElement qnEdit = browser.driver.findElement(By.id("questionedittext-" + qnNumber));
        waitForElementToBeClickable(qnEdit);
        click(qnEdit);
    }

    public void clickMaxNumberOfRecipientsButton() {
        click(maxNumOfRecipients);
    }

    public void clickCustomNumberOfRecipientsButton() {
        click(customNumOfRecipients);
    }

    public void clickEditUncommonSettingsSessionResponsesVisibleButton() {
        click(uncommonSettingsSessionResponsesVisibleButton);
    }

    public void clickEditUncommonSettingsSendEmailsButton() {
        click(uncommonSettingsSendEmailsButton);
    }

    public void clickDefaultVisibleTimeButton() {
        click(defaultSessionVisibleTimeButton);
    }

    public void clickDefaultPublishTimeButton() {
        click(defaultResultsVisibleTimeButton);
    }

    public void clickManualPublishTimeButton() {
        click(manualResultsVisibleTimeButton);
    }

    public void toggleClosingSessionEmailReminderCheckbox() {
        click(closingSessionEmailReminderButton);
    }

    public void clickFsCopyButton() {
        fscopyButton.click();
    }

    /**
     * Changes the value of actionlink of the copy question button.
     * @param actionLink value to change to
     */
    public void changeActionLinkOnCopyButton(String actionLink) {
        String selector = "$('#button_copy')";
        String action = ".data('actionlink', '" + actionLink + "')";
        executeScript(selector + action);
    }

    public void clickCopyButton() {
        click(copyQuestionLoadButton);
    }

    /**
     * Returns true if submission button of the 'copy question' modal is enabled.
     */
    public boolean isCopySubmitButtonEnabled() {
        return copyQuestionSubmitButton.isEnabled();
    }

    public void clickCopySubmitButton() {
        click(copyQuestionSubmitButton);
        waitForPageToLoad();
    }

    public void clickAddMcqOtherOptionCheckboxForNewQuestion() {
        click(addMcqOtherOptionCheckboxForNewQuestion);
    }

    public void clickAddMcqOtherOptionCheckbox(int qnNumber) {
        WebElement checkbox = browser.driver.findElement(By.id("mcqOtherOptionFlag-" + qnNumber));
        click(checkbox);
    }

    public boolean isMcqOtherOptionCheckboxChecked(int qnNumber) {
        return browser.driver.findElement(By.id("mcqOtherOptionFlag-" + qnNumber)).isSelected();
    }

    public void clickAddMsqOtherOptionCheckboxForNewQuestion() {
        click(addMsqOtherOptionCheckboxForNewQuestion);
    }

    public WebElement getDeleteSessionLink() {
        return fsDeleteLink;
    }

    public boolean verifyDuplicateButtonIsDisplayed(int qnIndex) {
        WebElement duplicateBn = browser.driver.findElement(
                By.xpath("//a[contains(@class, 'btn-duplicate-qn')][@data-qnnumber='" + qnIndex + "']"));
        return duplicateBn.isDisplayed();
    }

    public void clickDuplicateQuestionLink(int qnIndex) {
        WebElement link = browser.driver.findElement(
                By.xpath("//a[contains(@class, 'btn-duplicate-qn')][@data-qnnumber='" + qnIndex + "']"));
        click(link);
    }

    public void clickDeleteQuestionLink(int qnIndex) {
        WebElement link = browser.driver.findElement(
                By.xpath("//a[contains(@class, 'btn-delete-qn')][@data-qnnumber='" + qnIndex + "']"));
        click(link);
    }

    public void clickDiscardChangesLink(int qnIndex) {
        WebElement link = browser.driver.findElement(
                By.xpath("//a[contains(@class, 'btn-discard-changes')][@data-qnnumber='" + qnIndex + "']"));
        click(link);
    }

    public void clickAddMsqOtherOptionCheckbox(int qnNumber) {
        WebElement checkbox = browser.driver.findElement(By.id("msqOtherOptionFlag-" + qnNumber));
        click(checkbox);
    }

    public boolean isMsqOtherOptionCheckboxChecked(int qnNumber) {
        return browser.driver.findElement(By.id("msqOtherOptionFlag-" + qnNumber)).isSelected();
    }

    public void clickDiscardChangesLinkForNewQuestion() {
        clickDiscardChangesLink(NEW_QUESTION_NUM);
    }

    public boolean isDiscardChangesButtonVisible(int qnIndex) {
        WebElement discardChangesButton =
                browser.driver.findElement(
                        By.xpath("//a[contains(@class, 'btn-discard-changes')][@data-qnnumber='" + qnIndex + "']"));

        return discardChangesButton.isDisplayed();
    }

    public void clickEditSessionButton() {
        waitForElementVisibility(fsEditLink);
        click(fsEditLink);
    }

    public void clickSaveSessionButton() {
        click(fsSaveLink);
        waitForPageToLoad();
    }

    public void enableOtherVisibilityOptions(int qnNumber) {
        clickVisibilityDropdownAndWaitForVisibilityMessageToLoad("OTHER", qnNumber);
    }

    public void enableOtherVisibilityOptionsForNewQuestion() {
        enableOtherVisibilityOptions(NEW_QUESTION_NUM);
    }

    /**
     * Selects the visibility option and waits for the visibility message to load.
     * <b>Note: </b> The custom visibility options allow for more customization after the option is selected and
     * the visibility message would not be loaded until the custom options are modified, which implies that there is no need
     * to wait for the visibility message to load if the custom visibility options is selected.
     */
    public void clickVisibilityDropdownAndWaitForVisibilityMessageToLoad(String optionValue, int qnNumber) {
        String customVisibilityOptionsValue = "OTHER";

        // If the other option is selected, no AJAX request is made
        if (!optionValue.equals(customVisibilityOptionsValue)) {
            getjQueryAjaxHandler().registerHandlers();
        }

        WebElement visibilityOptionsDropdown =
                browser.driver.findElement(By.cssSelector("#questionTable-" + qnNumber + " .visibility-options-dropdown"));

        WebElement dropdownToggle = visibilityOptionsDropdown.findElement(By.tagName("button"));
        scrollElementToCenterAndClick(dropdownToggle);

        WebElement optionElement =
                visibilityOptionsDropdown.findElement(By.cssSelector("a[data-option-name=\"" + optionValue + "\"]"));

        // once the dropdown is assigned the class "open", the options should be visible unless not in the viewport
        waitForElementToBeMemberOfClass(visibilityOptionsDropdown, "open");

        // Scroll to the option because it may not be in the viewport
        scrollElementToCenterAndClick(optionElement);

        WebDriverWait wait = new WebDriverWait(browser.driver, TestProperties.TEST_TIMEOUT);
        wait.until(ExpectedConditions.textToBePresentInElement(dropdownToggle, optionElement.getText()));

        // If the other option is selected, no AJAX request is made
        if (!optionValue.equals(customVisibilityOptionsValue)) {
            getjQueryAjaxHandler().waitForRequestComplete();
        }
    }

    /**
     * Selects the visibility option and waits for the visibility message to load for the new question.
     *
     * @see InstructorFeedbackEditPage#clickVisibilityDropdownAndWaitForVisibilityMessageToLoad(String, int)
     */
    public void clickVisibilityDropdownForNewQuestionAndWaitForVisibilityMessageToLoad(String optionValue) {
        clickVisibilityDropdownAndWaitForVisibilityMessageToLoad(optionValue, NEW_QUESTION_NUM);
    }

    public String getVisibilityDropdownLabel(int qnNumber) {
        return browser.driver.findElement(By.cssSelector("#questionTable-" + qnNumber
                                                         + " .visibility-options-dropdown button")).getText().trim();
    }

    public boolean isStaticQnNumberVisible(int qnNumber) {
        try {
            WebElement selectQuestionNumberStatic =
                    browser.driver.findElement(By.id(Const.ParamsNames.FEEDBACK_QUESTION_NUMBER_STATIC + "-" + qnNumber));
            return selectQuestionNumberStatic.isDisplayed();

        } catch (NoSuchElementException nsee) {
            // new question doesn't have static question part
            return false;
        }
    }

    public boolean isMenuQnNumberVisible(int qnNumber) {
        if (qnNumber != NEW_QUESTION_NUM) {
            WebElement selectQuestionNumberDropdown =
                    browser.driver.findElement(By.id(Const.ParamsNames.FEEDBACK_QUESTION_NUMBER + "-" + qnNumber));
            return selectQuestionNumberDropdown.isDisplayed();
        }

        WebElement retrievedQnTable = browser.driver.findElement(By.id("questionTable-" + qnNumber));
        return retrievedQnTable.findElement(By.id(Const.ParamsNames.FEEDBACK_QUESTION_NUMBER)).isDisplayed();
    }

    public String getVisibilityDropdownLabelForNewQuestion() {
        return getVisibilityDropdownLabel(NEW_QUESTION_NUM);
    }

    public void clickAddQuestionButton() {
        click(addNewQuestionButton);
        waitForPageToLoad();
    }

    public void clickEditQuestionButton(int qnNumber) {
        WebElement qnEditLink = browser.driver.findElement(By.id("questionedittext-" + qnNumber));
        click(qnEditLink);
    }

    public boolean isQuestionEnabled(int qnNumber) {
        WebElement questionTextArea = browser.driver.findElement(By.id("questiontext-" + qnNumber));
        return questionTextArea.isEnabled();
    }

    public boolean isOptionForSelectingNumberOfEntitiesVisible(int qnNumber) {
        return isElementVisible(By.cssSelector("#form_editquestion-" + qnNumber + " .numberOfEntitiesElements"));
    }

    public void clickSaveExistingQuestionButton(int qnNumber) {
        WebElement qnSaveLink = browser.driver.findElement(By.id("button_question_submit-" + qnNumber));
        click(qnSaveLink);
        waitForPageToLoad();
    }

    public WebElement getSelectQuestionNumberDropdown(int qnNumber) {
        return browser.driver.findElement(By.id(Const.ParamsNames.FEEDBACK_QUESTION_NUMBER + "-" + qnNumber));
    }

    public WebElement getSelectQuestionNumberStatic(int qnNumber) {
        return browser.driver.findElement(By.id(Const.ParamsNames.FEEDBACK_QUESTION_NUMBER_STATIC + "-" + qnNumber));
    }

    public void selectQuestionNumber(int qnNumber, int newQnNumber) {
        WebElement qnNumSelect = getSelectQuestionNumberDropdown(qnNumber);
        selectDropdownByVisibleValue(qnNumSelect, String.valueOf(newQnNumber));
    }

    public boolean isSelectQuestionNumberEnabled(int qnNumber) {
        WebElement qnNumSelect = getSelectQuestionNumberDropdown(qnNumber);
        return qnNumSelect.isEnabled();
    }

    public int getSelectedQuestionNumber(int qnNumber) {
        if (isQuestionEnabled(qnNumber)) {
            Select qnNumSelect = new Select(getSelectQuestionNumberDropdown(qnNumber));
            return Integer.parseInt(qnNumSelect.getFirstSelectedOption().getText().trim());
        }
        //substring trims colon from the retrieved question number text
        String questionNumber = getSelectQuestionNumberStatic(qnNumber).getText();
        return Integer.parseInt(questionNumber.substring(0, questionNumber.length() - 1));
    }

    /**
     * Returns true if all elements expected to be enabled in the edit session frame are enabled
     * after edit link is clicked.
     */
    public boolean verifyEditSessionBoxIsEnabled() {
        boolean isEditSessionEnabled = fsSaveLink.isDisplayed() && timezoneDropDown.isEnabled()
                                       // && "Session visible from" radio buttons
                                       && defaultSessionVisibleTimeButton.isEnabled()
                                       && customSessionVisibleTimeButton.isEnabled()
                                       // && "Send emails for" checkboxes
                                       && closingSessionEmailReminderButton.isEnabled()
                                       && publishedSessionEmailReminderButton.isEnabled();

        if (isEditSessionEnabled) {
            isEditSessionEnabled = gracePeriodDropdown.isEnabled() // && Submission times inputs
                                   && startDateBox.isEnabled() && startTimeDropdown.isEnabled()
                                   && endDateBox.isEnabled() && endTimeDropdown.isEnabled()
                                   // && "Responses visible from" radio buttons
                                   && defaultResultsVisibleTimeButton.isEnabled()
                                   && customResultsVisibleTimeButton.isEnabled()
                                   && manualResultsVisibleTimeButton.isEnabled();
        }

        return isEditSessionEnabled;
    }

    public boolean verifyNewEssayQuestionFormIsDisplayed() {
        return addNewQuestionButton.isDisplayed();
    }

    public boolean verifyNewMcqQuestionFormIsDisplayed() {
        WebElement mcqForm = browser.driver.findElement(By.id("mcqForm"));
        return mcqForm.isDisplayed() && addNewQuestionButton.isDisplayed();
    }

    public boolean verifyNewMsqQuestionFormIsDisplayed() {
        WebElement mcqForm = browser.driver.findElement(By.id("msqForm"));
        return mcqForm.isDisplayed() && addNewQuestionButton.isDisplayed();
    }

    public boolean verifyNewNumScaleQuestionFormIsDisplayed() {
        WebElement mcqForm = browser.driver.findElement(By.id("numScaleForm"));
        return mcqForm.isDisplayed() && addNewQuestionButton.isDisplayed();
    }

    public boolean verifyNewConstSumQuestionFormIsDisplayed() {
        WebElement constSumForm = browser.driver.findElement(By.id("constSumForm"));
        return constSumForm.isDisplayed() && addNewQuestionButton.isDisplayed();
    }

    public boolean verifyNewContributionQuestionFormIsDisplayed() {
        // No contribForm to check for.
        return addNewQuestionButton.isDisplayed();
    }

    public boolean verifyNewRubricQuestionFormIsDisplayed() {
        WebElement contribForm = browser.driver.findElement(By.id("rubricForm"));
        return contribForm.isDisplayed() && addNewQuestionButton.isDisplayed();
    }

    public boolean verifyNewRankOptionsQuestionFormIsDisplayed() {
        WebElement contribForm = browser.driver.findElement(By.id("rankOptionsForm"));
        return contribForm.isDisplayed() && addNewQuestionButton.isDisplayed();
    }

    public boolean verifyNewRankRecipientsQuestionFormIsDisplayed() {
        WebElement contribForm = browser.driver.findElement(By.id("rankRecipientsForm"));
        return contribForm.isDisplayed() && addNewQuestionButton.isDisplayed();
    }

    /*
     * Checks if alert class is enabled on the visibility options div for the specified question number.
     */
    public boolean isAlertClassEnabledForVisibilityOptions(int questionNo) {
        final String visibilityOptionsDivXPath =
                "//div[@id='questionTable-" + questionNo + "']//div[@class='visibility-checkbox-delegate panel-body']"
                + "//b[@class='visibility-title']/../..";
        return browser.driver.findElement(By.xpath(visibilityOptionsDivXPath))
                .getAttribute("class").matches(".*\\balert alert-danger\\b.*");
    }

    public boolean areDatesOfPreviousCurrentAndNextMonthEnabled() throws ParseException {
        return areDatesOfPreviousCurrentAndNextMonthEnabled(startDateBox)
               && areDatesOfPreviousCurrentAndNextMonthEnabled(endDateBox);
    }

    /**
     * Returns true if the dates of previous, current and next month are enabled.
     *
     * @param dateBox is a {@link WebElement} that triggers a datepicker
     * @throws ParseException if the string in {@code dateBox} cannot be parsed
     */
    private boolean areDatesOfPreviousCurrentAndNextMonthEnabled(WebElement dateBox) throws ParseException {

        LocalDate previousMonth = LocalDate.now().minusMonths(1);

        // Navigate to the previous month
        if (!navigate(dateBox, previousMonth)) {
            return false;
        }

        // Check if the dates of previous, current and next month are enabled
        for (int i = 0; i < 3; i++) {

            List<WebElement> dates =
                    browser.driver.findElements(By.xpath("//div[@id='ui-datepicker-div']/table/tbody/tr/td"));

            for (WebElement date : dates) {

                boolean isDisabled = date.getAttribute("class").contains("ui-datepicker-unselectable ui-state-disabled");
                boolean isFromOtherMonth = date.getAttribute("class").contains("ui-datepicker-other-month");

                if (isDisabled && !isFromOtherMonth) {
                    return false;
                }
            }

            // Navigate to the next month
            click(browser.driver.findElement(By.className("ui-datepicker-next")));
        }

        return true;
    }

    /**
     * Navigate the datepicker associated with {@code dateBox} to the specified {@code date}.
     *
     * @param dateBox is a {@link WebElement} that triggers a datepicker
     * @param date is a {@link LocalDate} that specifies the date that needs to be navigated to
     * @return true if navigated to the {@code date} successfully, otherwise
     *         false
     * @throws ParseException if the string in {@code dateBox} cannot be parsed
     */
    private boolean navigate(WebElement dateBox, LocalDate date) throws ParseException {

        click(dateBox);
        LocalDate selectedDate = TimeHelper.parseDateFromSessionsForm(dateBox.getAttribute("value"));
        String month = date.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        String year = Integer.toString(date.getYear());

        if (selectedDate.isAfter(date)) {

            while (!getDatepickerMonth().equals(month) || !getDatepickerYear().equals(year)) {

                WebElement previousButton = browser.driver.findElement(By.className("ui-datepicker-prev"));
                if (previousButton.getAttribute("class").contains("ui-state-disabled")) {
                    return false;
                }
                click(previousButton);
            }

        } else {

            while (!getDatepickerMonth().equals(month) || !getDatepickerYear().equals(year)) {

                WebElement nextButton = browser.driver.findElement(By.className("ui-datepicker-next"));
                if (nextButton.getAttribute("class").contains("ui-state-disabled")) {
                    return false;
                }
                click(nextButton);
            }
        }

        return true;
    }

    private String getDatepickerYear() {
        By by = By.className("ui-datepicker-year");
        waitForElementPresence(by);
        return browser.driver.findElement(by).getText();
    }

    private String getDatepickerMonth() {
        By by = By.className("ui-datepicker-month");
        waitForElementPresence(by);
        return browser.driver.findElement(by).getText();
    }

    public void selectNewQuestionTypeAndWaitForNewQuestionPanelReady(String questionType) {
        click(browser.driver.findElement(By.cssSelector("[data-questionType=" + questionType + "]")));

        waitForNewQuestionPanelReady();
    }

    /**
     * Waits for the new question panel to be ready, that is, all states initialized and all animations complete.
     */
    private void waitForNewQuestionPanelReady() {
        // LEGACY IMPLEMENTATION: Coupled to the implementation instructorFeedbackEdit.js#showNewQuestionFrame
        // Creating a new question will result in scrolling, so it is assumed that all initialization is completed after
        // scrolling is complete.
        waitForScrollingToComplete();
    }

    public void selectMcqGenerateOptionsFor(String generateFor, int questionNumber) {
        selectDropdownByVisibleValue(
                browser.driver.findElement(By.id("mcqGenerateForSelect-" + questionNumber)),
                generateFor);
    }

    public void selectMsqGenerateOptionsFor(String generateFor, int questionNumber) {
        selectDropdownByVisibleValue(
                browser.driver.findElement(By.id("msqGenerateForSelect-" + questionNumber)),
                generateFor);
    }

    /**
     * Selects the points distribution scheme for a const sum question.
     *
     * @param pointsOption Value of "Total" will select <i>Total number of points for all options</i> points distribution
     *                     scheme, or "PerOption" will select <i>Number of points per option</i> points distribution scheme.
     * @param questionNumber question number of the const sum question.
     */
    public void selectConstSumPointsOptions(String pointsOption, int questionNumber) {
        markRadioButtonAsChecked(
                browser.driver.findElement(By.id("constSumPoints" + pointsOption + "-" + questionNumber)));
    }

    public void selectConstSumPointsOptionsForNewQuestion(String pointsOption) {
        selectConstSumPointsOptions(pointsOption, NEW_QUESTION_NUM);
    }

    public String getGiverTypeForQuestion(int qnNumber) {
        WebElement giverDropdownForQuestion = browser.driver.findElement(By.id("givertype-" + qnNumber));
        return giverDropdownForQuestion.getAttribute("value");
    }

    public String getRecipientTypeForQuestion(int qnNumber) {
        WebElement recipientDropdownForQuestion = browser.driver.findElement(By.id("recipienttype-" + qnNumber));
        return recipientDropdownForQuestion.getAttribute("value");
    }

    /**
     * Selects the recipient type in the feedback path and waits for the corresponding visibility message to load.
     */
    public void selectRecipientTypeForNewQuestionAndWaitForVisibilityMessageToLoad(String recipientType) {
        final WebElement selectElement = browser.driver.findElement(By.id("recipienttype-" + NEW_QUESTION_NUM));
        selectDropdownByVisibleValueAndWaitForAjaxRequestComplete(selectElement, recipientType);
    }

    public void clickNewQuestionButton() {
        scrollElementToCenterAndClick(openNewQuestionButton);
    }

    public boolean isAllFeedbackPathOptionsEnabledForNewQuestion() {
        List<WebElement> options = browser.driver.findElements(By.cssSelector("#givertype-" + NEW_QUESTION_NUM + " option"));
        options.addAll(browser.driver.findElements(By.cssSelector("#recipienttype-" + NEW_QUESTION_NUM + " option")));
        for (WebElement option : options) {
            if (!option.isEnabled()) {
                return false;
            }
        }
        return true;
    }

    public boolean isAllVisibilityOptionsEnabledForNewQuestion() {
        List<WebElement> visibilityDropdownItems = browser.driver.findElement(By.id("questionTable-" + NEW_QUESTION_NUM))
                .findElements(By.cssSelector(".visibility-options-dropdown .dropdown-menu li"));
        for (WebElement item : visibilityDropdownItems) {
            if (item.getAttribute("class").contains("hidden")) {
                return false;
            }
        }
        return true;
    }

    public boolean isAllRecipientOptionsDisplayed(int questionNumber) {
        List<WebElement> recipientOptions =
                browser.driver.findElements(By.cssSelector("#recipienttype-" + questionNumber + " option"));
        for (WebElement recipientOption : recipientOptions) {
            if ("none".equals(recipientOption.getCssValue("display"))) {
                return false;
            }
        }
        return true;
    }

    public boolean isRecipientOptionDisplayed(FeedbackParticipantType recipientType, int questionNumber) {
        WebElement recipientOption =
                browser.driver.findElement(
                        By.cssSelector("#recipienttype-" + questionNumber
                                       + " option[value='" + recipientType + "']"));
        return !"none".equals(recipientOption.getCssValue("display"));
    }

    public boolean isMaxNumOfRecipientsChecked() {
        return maxNumOfRecipients.isSelected();
    }

    public boolean isCustomNumOfRecipientsChecked() {
        return customNumOfRecipients.isSelected();
    }

    public void selectGiverToBe(FeedbackParticipantType giverType, int questionNumber) {
        WebElement giverDropdown = browser.driver.findElement(By.id("givertype-" + questionNumber));
        selectDropdownByActualValueAndWaitForAjaxRequestComplete(giverDropdown, giverType.toString());
    }

    public void selectRecipientToBe(FeedbackParticipantType recipientType, int questionNumber) {
        WebElement giverDropdown = browser.driver.findElement(By.id("recipienttype-" + questionNumber));
        selectDropdownByActualValueAndWaitForAjaxRequestComplete(giverDropdown, recipientType.toString());
    }

    /**
     * Selects the giver in the feedback path to be students in the course and waits for the corresponding visibility message
     * to load.
     */
    public void selectGiverToBeStudentsAndWaitForVisibilityMessageToLoad() {
        selectDropdownByVisibleValueAndWaitForAjaxRequestComplete(giverDropdownForNewQuestion, "Students in this course");
    }

    /**
     * Selects the giver in the feedback path to be instructors in the course and waits for the corresponding visibility
     * message to load.
     */
    public void selectGiverToBeInstructorsAndWaitForVisibilityMessageToLoad() {
        selectDropdownByVisibleValueAndWaitForAjaxRequestComplete(giverDropdownForNewQuestion, "Instructors in this course");
    }

    /**
     * Selects the recipients in the feedback path to be other students in the course and waits for the corresponding
     * visibility message to load.
     */
    public void selectRecipientsToBeStudentsAndWaitForVisibilityMessageToLoad() {
        selectDropdownByVisibleValueAndWaitForAjaxRequestComplete(
                recipientDropdownForNewQuestion, "Other students in the course");
    }

    /**
     * Selects the recipients in the feedback path to be the team members and giver and waits for the corresponding
     * visibility message to load.
     */
    public void selectRecipientsToBeGiverTeamMembersAndGiverAndWaitForVisibilityMessageToLoad() {
        selectDropdownByVisibleValueAndWaitForAjaxRequestComplete(
                recipientDropdownForNewQuestion, "Giver's team members and Giver");
    }

    /**
     * Selects the recipients in the feedback path to be instructors in the course and waits for the corresponding
     * visibility message to load.
     */
    public void selectRecipientsToBeInstructorsAndWaitForVisibilityMessageToLoad() {
        selectDropdownByVisibleValueAndWaitForAjaxRequestComplete(
                recipientDropdownForNewQuestion, "Instructors in the course");
    }

    /**
     * Selects the recipients in the feedback path to be other students in the course and waits for the corresponding
     * visibility message to load.
     */
    public void selectRecipientsToBeStudentsAndWaitForVisibilityMessageToLoad(int qnNumber) {
        WebElement recipientDropdown = browser.driver.findElement(By.id("recipienttype-" + qnNumber));
        selectDropdownByVisibleValueAndWaitForAjaxRequestComplete(recipientDropdown, "Other students in the course");
    }

    public void enableOtherFeedbackPathOptions(int qnNumber) {
        WebElement questionTable = browser.driver.findElement(By.id("questionTable-" + qnNumber));
        WebElement dropdownButton = questionTable.findElement(By.cssSelector(".feedback-path-dropdown > button"));
        WebElement otherOption = questionTable.findElement(
                                     By.className("feedback-path-dropdown-option-other"));
        click(dropdownButton);
        click(otherOption);
    }

    public void enableOtherFeedbackPathOptionsForNewQuestion() {
        enableOtherFeedbackPathOptions(NEW_QUESTION_NUM);
    }

    public void editFeedbackSession(LocalDateTime startTime, LocalDateTime endTime, Text instructions, long gracePeriod) {
        // Select start date
        executeScript("$('#" + Const.ParamsNames.FEEDBACK_SESSION_STARTDATE + "')[0].value='"
                      + TimeHelper.formatDateForSessionsForm(startTime) + "';");
        selectDropdownByVisibleValue(startTimeDropdown,
                TimeHelperExtension.convertToDisplayValueInTimeDropDown(startTime));

        // Select deadline date
        executeScript("$('#" + Const.ParamsNames.FEEDBACK_SESSION_ENDDATE + "')[0].value='"
                      + TimeHelper.formatDateForSessionsForm(endTime) + "';");
        selectDropdownByVisibleValue(endTimeDropdown,
                TimeHelperExtension.convertToDisplayValueInTimeDropDown(endTime));

        // Fill in instructions
        fillRichTextEditor("instructions", instructions.getValue());

        // Select grace period
        selectDropdownByVisibleValue(gracePeriodDropdown, Long.toString(gracePeriod) + " mins");

        click(fsSaveLink);
        waitForPageToLoad();
    }

    public String getFeedbackSessionEndTimeValue() {
        return getDropdownSelectedValue(endTimeDropdown);
    }

    public InstructorFeedbackSessionsPage deleteSession() {
        clickAndConfirm(getDeleteSessionLink());
        waitForPageToLoad();
        return changePageType(InstructorFeedbackSessionsPage.class);
    }

    public InstructorFeedbackSessionsPage clickDoneEditingLink() {
        click(doneEditingButton);
        waitForPageToLoad();
        return changePageType(InstructorFeedbackSessionsPage.class);
    }

    public boolean isMcqWeightBoxFocused(int qnNumber, int choiceIndex) {
        WebElement weightBox = getMcqWeightBox(qnNumber, choiceIndex);

        return weightBox.equals(browser.driver.switchTo().activeElement());
    }

    public boolean isMcqOtherWeightBoxFocused(int qnNumber) {
        WebElement weightBox = getMcqOtherWeightBox(qnNumber);
        return weightBox.equals(browser.driver.switchTo().activeElement());
    }

    public void fillMcqOptionForNewQuestion(int optionIndex, String optionText) {
        fillMcqOption(NEW_QUESTION_NUM, optionIndex, optionText);
    }

    public void fillMcqOption(int qnNumber, int optionIndex, String optionText) {
        WebElement optionBox = browser.driver.findElement(By.id("mcqOption-" + optionIndex + "-" + qnNumber));
        fillTextBox(optionBox, optionText);
    }

    public void clickAddMoreMcqOptionLink(int qnNumber) {
        WebElement addMoreOptionLink = browser.driver.findElement(By.id("mcqAddOptionLink-" + qnNumber));
        click(addMoreOptionLink);
    }

    public void clickAddMoreMcqOptionLinkForNewQuestion() {
        clickAddMoreMcqOptionLink(NEW_QUESTION_NUM);
    }

    public WebElement getMcqHasAssignWeightsCheckbox(int qnNumber) {
        return browser.driver.findElement(By.id("mcqHasAssignedWeights-" + qnNumber));
    }

    public void clickMcqHasAssignWeightsCheckbox(int qnNumber) {
        WebElement mcqAssignWeightCheckbox = getMcqHasAssignWeightsCheckbox(qnNumber);
        click(mcqAssignWeightCheckbox);
    }

    public void clickMcqAssignWeightCheckboxForNewQuestion() {
        clickMcqHasAssignWeightsCheckbox(NEW_QUESTION_NUM);
    }

    public boolean isMcqHasAssignWeightCheckboxChecked(int qnNumber) {
        WebElement checkbox = browser.driver.findElement(By.id("mcqHasAssignedWeights-" + qnNumber));
        return checkbox.isSelected();
    }

    /**
     * Get the specified Mcq weight WebElement.
     * @param qnNumber Question number
     * @param choiceIndex The index of the corresponding MCQ choice
     * @return The MCQ weight WebElement for the specified choice.
     */
    public WebElement getMcqWeightBox(int qnNumber, int choiceIndex) {
        String elementId = Const.ParamsNames.FEEDBACK_QUESTION_MCQ_WEIGHT + "-" + choiceIndex + "-" + qnNumber;

        return browser.driver.findElement(By.id(elementId));
    }

    /**
     * Returns the whole MCQ weight column.
     */
    public WebElement getMcqWeightsColumn(int qnNumber) {
        return browser.driver.findElement(By.id("mcqWeights-" + qnNumber));
    }

    public WebElement getMcqOtherWeightBox(int qnNumber) {
        return browser.driver.findElement(By.id("mcqOtherWeight-" + qnNumber));
    }

    public void fillMcqWeightBox(int qnNumber, int choiceIndex, String value) {
        WebElement weightBox = getMcqWeightBox(qnNumber, choiceIndex);
        fillTextBox(weightBox, value);
    }

    public void fillMcqOtherWeightBox(int qnNumber, String value) {
        WebElement otherWeightBox = getMcqOtherWeightBox(qnNumber);
        fillTextBox(otherWeightBox, value);
    }

    public void clickRemoveMcqOptionLink(int optionIndex, int qnIndex) {
        String idSuffix = getIdSuffix(qnIndex);

        WebElement mcqOptionRow = browser.driver.findElement(By.id("mcqOptionRow-" + optionIndex + idSuffix));
        WebElement removeOptionLink = mcqOptionRow.findElement(By.id("mcqRemoveOptionLink"));
        click(removeOptionLink);
    }

    public void clickRemoveMcqOptionLinkForNewQuestion(int optionIndex) {
        clickRemoveMcqOptionLink(optionIndex, NEW_QUESTION_NUM);
    }

    public void clickGenerateMcqOptionsCheckbox(int qnIndex) {
        clickGenerateOptionsCheckbox(qnIndex, "generateMcqOptionsCheckbox");
    }

    public void clickGenerateMsqOptionsCheckbox(int qnIndex) {
        clickGenerateOptionsCheckbox(qnIndex, "generateMsqOptionsCheckbox");
    }

    private void clickGenerateOptionsCheckbox(int qnIndex, String idPrefix) {
        String idSuffix = getIdSuffix(qnIndex);

        WebElement generateOptionsCheckbox = browser.driver.findElement(By.id(idPrefix + idSuffix));
        click(generateOptionsCheckbox);
    }

    public void fillMsqOptionForNewQuestion(int optionIndex, String optionText) {
        WebElement optionBox = browser.driver.findElement(By.id("msqOption-" + optionIndex + "-" + NEW_QUESTION_NUM));
        fillTextBox(optionBox, optionText);
    }

    public boolean isMsqWeightBoxFocused(int qnNumber, int choiceIndex) {
        WebElement weightBox = getMsqWeightBox(qnNumber, choiceIndex);
        return weightBox.equals(browser.driver.switchTo().activeElement());
    }

    public boolean isMsqOtherWeightBoxFocused(int qnNumber) {
        WebElement weightBox = getMsqOtherWeightBox(qnNumber);
        return weightBox.equals(browser.driver.switchTo().activeElement());
    }

    public void clickAddMoreMsqOptionLink(int qnIndex) {
        WebElement addMoreOptionLink = browser.driver.findElement(By.id("msqAddOptionLink-" + qnIndex));
        click(addMoreOptionLink);
    }

    public void clickAddMoreMsqOptionLinkForNewQuestion() {
        clickAddMoreMsqOptionLink(NEW_QUESTION_NUM);
    }

    public void clickRemoveMsqOptionLink(int optionIndex, int qnIndex) {
        String idSuffix = getIdSuffix(qnIndex);

        WebElement msqOptionRow = browser.driver.findElement(By.id("msqOptionRow-" + optionIndex + idSuffix));
        WebElement removeOptionLink = msqOptionRow.findElement(By.id("msqRemoveOptionLink"));
        click(removeOptionLink);
    }

    public void clickRemoveMsqOptionLinkForNewQuestion(int optionIndex) {
        clickRemoveMsqOptionLink(optionIndex, NEW_QUESTION_NUM);
    }

    private String getMsqMaxSelectableChoicesCheckboxId(int qnNumber) {
        return "msqEnableMaxSelectableChoices-" + qnNumber;
    }

    private String getMsqMinSelectableChoicesCheckboxId(int qnNumber) {
        return "msqEnableMinSelectableChoices-" + qnNumber;
    }

    private String getMsqMaxSelectableChoicesBoxId(int qnNumber) {
        return "msqMaxSelectableChoices-" + qnNumber;
    }

    private String getMsqMinSelectableChoicesBoxId(int qnNumber) {
        return "msqMinSelectableChoices-" + qnNumber;
    }

    public WebElement getMsqMaxSelectableChoicesElement(int qnNumber) {
        String id = getMsqMaxSelectableChoicesCheckboxId(qnNumber);

        return browser.driver.findElement(By.id(id));
    }

    private WebElement getMsqMinSelectableChoicesElement(int qnNumber) {
        String id = getMsqMinSelectableChoicesCheckboxId(qnNumber);

        return browser.driver.findElement(By.id(id));
    }

    public void toggleMsqMaxSelectableChoices(int qnNumber) {
        click(getMsqMaxSelectableChoicesElement(qnNumber));
    }

    public void toggleMsqMinSelectableChoices(int qnNumber) {
        click(getMsqMinSelectableChoicesElement(qnNumber));
    }

    public boolean isMsqMaxSelectableChoicesEnabled(int qnNumber) {
        String id = getMsqMaxSelectableChoicesCheckboxId(qnNumber);

        return browser.driver.findElement(By.id(id)).isSelected();
    }

    public boolean isMsqMinSelectableChoicesEnabled(int qnNumber) {
        String id = getMsqMinSelectableChoicesCheckboxId(qnNumber);

        return browser.driver.findElement(By.id(id)).isSelected();
    }

    public WebElement getMsqMaxSelectableChoicesBox(int qnNumber) {
        return browser.driver.findElement(By.id(getMsqMaxSelectableChoicesBoxId(qnNumber)));
    }

    public WebElement getMsqMinSelectableChoicesBox(int qnNumber) {
        return browser.driver.findElement(By.id(getMsqMinSelectableChoicesBoxId(qnNumber)));
    }

    public int getMinOfMsqMaxSelectableChoices(int qnNumber) {
        return Integer.parseInt(getMsqMaxSelectableChoicesBox(qnNumber).getAttribute("min"));
    }

    public int getMaxOfMsqMaxSelectableChoices(int qnNumber) {
        return Integer.parseInt(getMsqMaxSelectableChoicesBox(qnNumber).getAttribute("max"));
    }

    public int getMinOfMsqMinSelectableChoices(int qnNumber) {
        return Integer.parseInt(getMsqMinSelectableChoicesBox(qnNumber).getAttribute("min"));
    }

    public int getMaxOfMsqMinSelectableChoices(int qnNumber) {
        return Integer.parseInt(getMsqMinSelectableChoicesBox(qnNumber).getAttribute("max"));
    }

    public void fillMsqMinSelectableChoices(int qnNumber, String value) {
        fillTextBox(getMsqMinSelectableChoicesBox(qnNumber), value);
    }

    public void fillMsqMaxSelectableChoices(int qnNumber, String value) {
        fillTextBox(getMsqMaxSelectableChoicesBox(qnNumber), value);
    }

    public void fillMsqOption(int qnNumber, int optionIndex, String optionText) {
        WebElement optionBox = browser.driver.findElement(By.id("msqOption-" + optionIndex + "-" + qnNumber));
        fillTextBox(optionBox, optionText);
    }

    public WebElement getMsqHasAssignWeightsCheckbox(int qnNumber) {
        return browser.driver.findElement(By.id("msqHasAssignedWeights-" + qnNumber));
    }

    public void clickMsqHasAssignWeightsCheckbox(int qnNumber) {
        WebElement msqAssignWeightCheckbox = getMsqHasAssignWeightsCheckbox(qnNumber);
        click(msqAssignWeightCheckbox);
    }

    public void clickMsqAssignWeightCheckboxForNewQuestion() {
        clickMsqHasAssignWeightsCheckbox(NEW_QUESTION_NUM);
    }

    public boolean isMsqHasAssignWeightCheckboxChecked(int qnNumber) {
        WebElement checkbox = browser.driver.findElement(By.id("msqHasAssignedWeights-" + qnNumber));
        return checkbox.isSelected();
    }

    /**
     * Get the specified Msq weight WebElement.
     * @param qnNumber Question number
     * @param choiceIndex The index of the corresponding MSQ choice
     * @return The MSQ weight WebElement for the specified choice.
     */
    public WebElement getMsqWeightBox(int qnNumber, int choiceIndex) {
        String elementId = Const.ParamsNames.FEEDBACK_QUESTION_MSQ_WEIGHT + "-" + choiceIndex + "-" + qnNumber;

        return browser.driver.findElement(By.id(elementId));
    }

    /**
     * Returns the whole MSQ weight column.
     */
    public WebElement getMsqWeightsColumn(int qnNumber) {
        return browser.driver.findElement(By.id("msqWeights-" + qnNumber));
    }

    public WebElement getMsqOtherWeightBox(int qnNumber) {
        return browser.driver.findElement(By.id("msqOtherWeight-" + qnNumber));
    }

    public void fillMsqWeightBox(int qnNumber, int choiceIndex, String value) {
        WebElement weightBox = getMsqWeightBox(qnNumber, choiceIndex);
        fillTextBox(weightBox, value);
    }

    public void fillMsqOtherWeightBox(int qnNumber, String value) {
        WebElement otherWeightBox = getMsqOtherWeightBox(qnNumber);
        fillTextBox(otherWeightBox, value);
    }

    public int getNumOfStudentsForFs() {
        return Integer.parseInt(browser.driver.findElement(By.id("num-students")).getAttribute("value"));
    }

    public int getNumOfTeamsForFs() {
        return Integer.parseInt(browser.driver.findElement(By.id("num-teams")).getAttribute("value"));
    }

    public int getNumOfInstructorsForFs() {
        return Integer.parseInt(browser.driver.findElement(By.id("num-instructors")).getAttribute("value"));
    }

    private String getValue(WebElement elem) {
        return (String) executeScript(String.format("return $('#%s').val();", elem.getAttribute("id")));
    }

    public int getMsqMinSelectableChoices(int qnNumber) {
        assertTrue(isMsqMinSelectableChoicesEnabled(qnNumber));

        return Integer.parseInt(getValue(getMsqMinSelectableChoicesBox(qnNumber)));
    }

    public int getMsqMaxSelectableChoices(int qnNumber) {
        assertTrue(isMsqMaxSelectableChoicesEnabled(qnNumber));

        return Integer.parseInt(getValue(getMsqMaxSelectableChoicesBox(qnNumber)));
    }

    private boolean isGenerateOptionsForMsqChecked(int qnNumber) {
        return browser.driver.findElement(By.id("generateMsqOptionsCheckbox-" + qnNumber)).isSelected();
    }

    private String getGenerateOptionsForMsqValue(int qnNumber) {
        Select options = new Select(browser.driver.findElement(By.id("msqGenerateForSelect-" + qnNumber)));

        return options.getFirstSelectedOption().getText();
    }

    public int getNumOfMsqOptions(int qnNumber) {
        if (isGenerateOptionsForMsqChecked(qnNumber)) {
            String selectedOption = getGenerateOptionsForMsqValue(qnNumber);

            switch (selectedOption) {
            case "students":
                return getNumOfStudentsForFs();
            case "teams":
                return getNumOfTeamsForFs();
            case "instructors":
                return getNumOfInstructorsForFs();
            default:
                return 0;
            }
        }

        return browser.driver.findElements(
                By.cssSelector("#msqChoiceTable-" + qnNumber + " div[id*=\"msqOptionRow\"]")).size();
    }

    public void verifyMsqMinMaxSelectableChoices(int qnNumber) {
        boolean isMinEnabled = isMsqMinSelectableChoicesEnabled(qnNumber);
        boolean isMaxEnabled = isMsqMaxSelectableChoicesEnabled(qnNumber);

        if (!isMinEnabled && !isMaxEnabled) {
            return;
        }

        int numOfOptions = getNumOfMsqOptions(qnNumber);

        if (isMinEnabled) {
            int lowerLimit = getMinOfMsqMinSelectableChoices(qnNumber);
            int upperLimit = getMaxOfMsqMinSelectableChoices(qnNumber);
            int value = getMsqMinSelectableChoices(qnNumber);

            assertTrue(lowerLimit == 1);
            assertTrue(lowerLimit <= value);
            assertTrue(value <= upperLimit);
            assertTrue(upperLimit <= numOfOptions);
        }

        if (isMaxEnabled) {
            int lowerLimit = getMinOfMsqMaxSelectableChoices(qnNumber);
            int upperLimit = getMaxOfMsqMaxSelectableChoices(qnNumber);
            int value = getMsqMaxSelectableChoices(qnNumber);

            assertTrue(lowerLimit == 2);
            assertTrue(lowerLimit <= value);
            assertTrue(value <= upperLimit);
            assertTrue(upperLimit <= numOfOptions);
        }

        if (isMinEnabled && isMaxEnabled) {
            int minValue = getMsqMinSelectableChoices(qnNumber);
            int maxValue = getMsqMaxSelectableChoices(qnNumber);

            assertTrue(minValue <= maxValue);
            assertEquals(getMaxOfMsqMinSelectableChoices(qnNumber), maxValue);
        }
    }

    public void fillConstSumOption(int optionIndex, String optionText, int qnIndex) {
        String idSuffix = getIdSuffix(qnIndex);

        WebElement optionBox = browser.driver.findElement(By.id("constSumOption-" + optionIndex + idSuffix));
        fillTextBox(optionBox, optionText);
    }

    public void fillConstSumOptionForNewQuestion(int optionIndex, String optionText) {
        fillConstSumOption(optionIndex, optionText, NEW_QUESTION_NUM);
    }

    public void clickAddMoreConstSumOptionLink(int qnIndex) {
        WebElement addMoreOptionLink = browser.driver.findElement(By.id("constSumAddOptionLink-" + qnIndex));
        click(addMoreOptionLink);
    }

    public void clickAddMoreConstSumOptionLinkForNewQuestion() {
        clickAddMoreConstSumOptionLink(NEW_QUESTION_NUM);
    }

    public void clickRemoveConstSumOptionLink(int optionIndex, int qnIndex) {
        String idSuffix = getIdSuffix(qnIndex);

        WebElement msqOptionRow = browser.driver.findElement(By.id("constSumOptionRow-" + optionIndex + idSuffix));
        WebElement removeOptionLink = msqOptionRow.findElement(By.id("constSumRemoveOptionLink"));
        click(removeOptionLink);
    }

    public void clickRemoveConstSumOptionLinkForNewQuestion(int optionIndex) {
        clickRemoveConstSumOptionLink(optionIndex, NEW_QUESTION_NUM);
    }

    public void clickDistributePointsOptionsCheckbox(int qnIndex) {
        String idSuffix = getIdSuffix(qnIndex);

        WebElement distributePointsOptionsCheckbox =
                browser.driver.findElement(By.id("constSum_UnevenDistribution" + idSuffix));
        click(distributePointsOptionsCheckbox);
    }

    public void selectConstSumDistributePointsOptions(String distributePointsFor, int questionNumber) {
        selectDropdownByVisibleValue(
                browser.driver.findElement(By.id("constSumDistributePointsSelect-" + questionNumber)),
                distributePointsFor);
    }

    public void clickAssignWeightsCheckbox(int qnIndex) {
        By by = By.id(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_WEIGHTS_ASSIGNED + getIdSuffix(qnIndex));
        WebElement assignWeightsCheckbox = browser.driver.findElement(by);
        click(assignWeightsCheckbox);
    }

    public void clickAssignWeightsCheckboxForNewQuestion() {
        clickAssignWeightsCheckbox(NEW_QUESTION_NUM);
    }

    public void clickAddRubricRowLink(int qnIndex) {
        String idSuffix = getIdSuffix(qnIndex);
        WebElement addRubricRowLink = browser.driver.findElement(By.id("rubricAddSubQuestionLink" + idSuffix));
        click(addRubricRowLink);
    }

    public void clickAddRubricColLink(int qnIndex) {
        String idSuffix = getIdSuffix(qnIndex);
        WebElement addRubricColLink = browser.driver.findElement(By.id("rubricAddChoiceLink" + idSuffix));
        click(addRubricColLink);
    }

    public void clickRemoveRubricRowLinkAndConfirm(int qnIndex, int row) {
        String idSuffix = getIdSuffix(qnIndex);
        WebElement removeRubricRowLink =
                browser.driver.findElement(By.id("rubricRemoveSubQuestionLink" + idSuffix + "-" + row));
        //click(addRubricRowLink);
        clickAndConfirm(removeRubricRowLink);
    }

    public void clickRemoveRubricColLinkAndConfirm(int qnIndex, int col) {
        String idSuffix = getIdSuffix(qnIndex);
        WebElement removeRubricColLink =
                browser.driver.findElement(By.id("rubricRemoveChoiceLink" + idSuffix + "-" + col));
        clickAndConfirm(removeRubricColLink);
    }

    public void verifyRankOptionIsHiddenForNewQuestion(int optionIndex) {
        WebElement optionBox = browser.driver.findElement(By.id("rankOption-" + optionIndex + "-" + NEW_QUESTION_NUM));
        assertFalse(optionBox.isDisplayed());
    }

    public void fillRankOption(int qnIndx, int optionIndex, String optionText) {
        WebElement optionBox = browser.driver.findElement(By.id("rankOption-" + optionIndex + "-" + qnIndx));
        fillTextBox(optionBox, optionText);
    }

    public void fillRankOptionForNewQuestion(int optionIndex, String optionText) {
        fillRankOption(NEW_QUESTION_NUM, optionIndex, optionText);
    }

    public void tickDuplicatesAllowedCheckbox(int qnIndex) {
        WebElement checkBox = toggleDuplicatesAllowedCheckBox(qnIndex);
        assertTrue(checkBox.isSelected());
    }

    public void tickDuplicatesAllowedCheckboxForNewQuestion() {
        tickDuplicatesAllowedCheckbox(NEW_QUESTION_NUM);
    }

    public void untickDuplicatesAllowedCheckbox(int qnIndex) {
        WebElement checkBox = toggleDuplicatesAllowedCheckBox(qnIndex);
        assertFalse(checkBox.isSelected());
    }

    public void untickDuplicatesAllowedCheckboxForNewQuestion() {
        untickDuplicatesAllowedCheckbox(NEW_QUESTION_NUM);
    }

    private WebElement toggleDuplicatesAllowedCheckBox(int qnIndex) {
        WebElement checkBox = browser.driver.findElement(By.id("rankAreDuplicatesAllowed-" + qnIndex));
        click(checkBox);
        return checkBox;
    }

    public boolean isRankDuplicatesAllowedChecked(int qnIndex) {
        WebElement checkBox = browser.driver.findElement(By.id("rankAreDuplicatesAllowed-" + qnIndex));
        return checkBox.isSelected();
    }

    public boolean isRankDuplicatesAllowedCheckedForNewQuestion() {
        return isRankDuplicatesAllowedChecked(NEW_QUESTION_NUM);
    }

    public void clickAddMoreRankOptionLink(int qnIndex) {
        WebElement addMoreOptionLink = browser.driver.findElement(By.id("rankAddOptionLink-" + qnIndex));
        click(addMoreOptionLink);
    }

    public void clickAddMoreRankOptionLinkForNewQuestion() {
        clickAddMoreRankOptionLink(NEW_QUESTION_NUM);
    }

    public void clickRemoveRankOptionLink(int qnIndex, int optionIndex) {
        String idSuffix = getIdSuffix(qnIndex);

        WebElement msqOptionRow = browser.driver.findElement(By.id("rankOptionRow-" + optionIndex + idSuffix));
        WebElement removeOptionLink = msqOptionRow.findElement(By.id("rankRemoveOptionLink"));
        click(removeOptionLink);
    }

    public String getQuestionType(int qnNumber) {
        return browser.driver.findElement(By.cssSelector("#form_editquestion-" + qnNumber + " input[name='questiontype']"))
                .getAttribute("value").toString();
    }

    private boolean isRankOptionsQuestion(int qnIndex) {
        return "RANK_OPTIONS".equals(getQuestionType(qnIndex));
    }

    public int getNumOfOptionsInRankOptions(int qnIndex) {
        if (isRankOptionsQuestion(qnIndex)) {
            WebElement rankOptionsTable = browser.driver.findElement(By.id("rankOptionTable-" + qnIndex));
            List<WebElement> optionInputFields =
                    rankOptionsTable.findElements(By.cssSelector("input[id^='rankOption-']"));
            return optionInputFields.size();
        }

        // For rank recipients
        String recipient = getRecipientTypeForQuestion(qnIndex);

        switch (recipient) {
        case "STUDENTS":
        case "INSTRUCTORS":
        case "TEAMS":
            return Integer.parseInt(browser.driver.findElement(By.id("num-" + recipient.toLowerCase()))
                    .getAttribute("value").toString());
        case "OWN_TEAM_MEMBERS":
        case "OWN_TEAM_MEMBERS_INCLUDING_SELF":
            return Integer.MAX_VALUE;
        default:
            // For SELF, OWN_TEAM, NONE
            return 1;
        }
    }

    public int getNumOfOptionsInRankOptionsForNewQuestion() {
        return getNumOfOptionsInRankOptions(NEW_QUESTION_NUM);
    }

    private WebElement getMinOptionsToBeRankedCheckbox(int qnNumber) {
        if (isRankOptionsQuestion(qnNumber)) {
            return browser.driver.findElement(By.id("minOptionsToBeRankedEnabled-" + qnNumber));
        }

        return browser.driver.findElement(By.id("minRecipientsToBeRankedEnabled-" + qnNumber));
    }

    private WebElement getMaxOptionsToBeRankedCheckbox(int qnNumber) {
        if (isRankOptionsQuestion(qnNumber)) {
            return browser.driver.findElement(By.id("maxOptionsToBeRankedEnabled-" + qnNumber));
        }

        return browser.driver.findElement(By.id("maxRecipientsToBeRankedEnabled-" + qnNumber));
    }

    public void toggleMinOptionsToBeRankedCheckbox(int qnNumber) {
        click(getMinOptionsToBeRankedCheckbox(qnNumber));
    }

    public void toggleMaxOptionsToBeRankedCheckbox(int qnNumber) {
        click(getMaxOptionsToBeRankedCheckbox(qnNumber));
    }

    public WebElement getMinOptionsToBeRankedInputElement(int qnNumber) {
        if (isRankOptionsQuestion(qnNumber)) {
            return browser.driver.findElement(By.id("minOptionsToBeRanked-" + qnNumber));
        }

        return browser.driver.findElement(By.id("minRecipientsToBeRanked-" + qnNumber));
    }

    public WebElement getMaxOptionsToBeRankedInputElement(int qnNumber) {
        if (isRankOptionsQuestion(qnNumber)) {
            return browser.driver.findElement(By.id("maxOptionsToBeRanked-" + qnNumber));
        }

        return browser.driver.findElement(By.id("maxRecipientsToBeRanked-" + qnNumber));
    }

    public boolean isMinOptionsToBeRankedEnabled(int qnNumber) {
        return getMinOptionsToBeRankedCheckbox(qnNumber).isEnabled()
                && getMinOptionsToBeRankedInputElement(qnNumber).isEnabled();
    }

    public boolean isMaxOptionsToBeRankedEnabled(int qnNumber) {
        return getMaxOptionsToBeRankedCheckbox(qnNumber).isEnabled()
                && getMaxOptionsToBeRankedInputElement(qnNumber).isEnabled();
    }

    private int getMinOfMinOptionsToBeSelectedInput(int qnNumber) {
        WebElement elem = getMinOptionsToBeRankedInputElement(qnNumber);

        return Integer.parseInt(elem.getAttribute("min"));
    }

    private int getMinOfMaxOptionsToBeSelectedInput(int qnNumber) {
        WebElement elem = getMaxOptionsToBeRankedInputElement(qnNumber);

        return Integer.parseInt(elem.getAttribute("min"));
    }

    private int getMaxOfMinOptionsToBeSelectedInput(int qnNumber) {
        WebElement elem = getMinOptionsToBeRankedInputElement(qnNumber);

        return Integer.parseInt(elem.getAttribute("max"));
    }

    private int getMaxOfMaxOptionsToBeSelectedInput(int qnNumber) {
        WebElement elem = getMaxOptionsToBeRankedInputElement(qnNumber);

        return Integer.parseInt(elem.getAttribute("max"));
    }

    public int getMinOptionsToBeRanked(int qnNumber) {
        assertTrue(isMinOptionsToBeRankedEnabled(qnNumber));
        WebElement elem = getMinOptionsToBeRankedInputElement(qnNumber);

        return Integer.parseInt(elem.getAttribute("value"));
    }

    public int getMaxOptionsToBeRanked(int qnNumber) {
        assertTrue(isMaxOptionsToBeRankedEnabled(qnNumber));
        WebElement elem = getMaxOptionsToBeRankedInputElement(qnNumber);

        return Integer.parseInt(elem.getAttribute("value"));
    }

    public void fillMinOptionsToBeRanked(int qnNumber, String value) {
        WebElement rankMinOption = getMinOptionsToBeRankedInputElement(qnNumber);
        fillTextBox(rankMinOption, value);
    }

    public void fillMaxOptionsToBeRanked(int qnNumber, String value) {
        WebElement rankMaxOption = getMaxOptionsToBeRankedInputElement(qnNumber);
        fillTextBox(rankMaxOption, value);
    }

    public void verifyMinMaxOptionsToBeSelectedRestrictions(int qnNumber) {
        boolean isMinOptionsEnabled = isMinOptionsToBeRankedEnabled(qnNumber);
        boolean isMaxOptionsEnabled = isMaxOptionsToBeRankedEnabled(qnNumber);

        if (!isMinOptionsEnabled && !isMaxOptionsEnabled) {
            return;
        }

        if (isMinOptionsEnabled) {
            int value = getMinOptionsToBeRanked(qnNumber);
            int upperLimit = getMaxOfMinOptionsToBeSelectedInput(qnNumber);

            assertTrue(value <= upperLimit);
            assertEquals(1, getMinOfMinOptionsToBeSelectedInput(qnNumber));
        }

        if (isMaxOptionsEnabled) {
            int value = getMaxOptionsToBeRanked(qnNumber);
            int upperLimit = getMaxOfMaxOptionsToBeSelectedInput(qnNumber);

            assertTrue(value <= upperLimit);
            assertEquals(getNumOfOptionsInRankOptions(qnNumber), getMaxOfMaxOptionsToBeSelectedInput(qnNumber));
            assertEquals(1, getMinOfMaxOptionsToBeSelectedInput(qnNumber));
        }

        if (isMinOptionsEnabled && isMaxOptionsEnabled) {
            int maxValue = getMaxOptionsToBeRanked(qnNumber);
            int minValue = getMinOptionsToBeRanked(qnNumber);

            assertTrue(minValue <= maxValue);
        }
    }

    public FeedbackSubmitPage clickPreviewAsStudentButton() {
        click(previewAsStudentButton);
        waitForPageToLoad();
        switchToNewWindow();
        return changePageType(FeedbackSubmitPage.class);
    }

    public FeedbackSubmitPage clickPreviewAsInstructorButton() {
        waitForPageToLoad();
        click(previewAsInstructorButton);
        waitForPageToLoad();
        switchToNewWindow();
        return changePageType(FeedbackSubmitPage.class);
    }

    public void clickCopyTableAtRow(int rowIndex) {
        WebElement row = browser.driver.findElement(By.id("copyTableModal"))
                                                      .findElements(By.tagName("tr"))
                                                      .get(rowIndex + 1);
        click(row);
    }

    public void waitForCopyTableToLoad() {
        By tableRowSelector = By.cssSelector("#copyTableModal tr");
        waitForElementPresence(tableRowSelector);
        waitForElementVisibility(browser.driver.findElement(tableRowSelector));
    }

    public void waitForCopyErrorMessageToLoad() {
        By errorMessageSelector = By.cssSelector("#question-copy-modal-status.alert-danger");
        waitForElementPresence(errorMessageSelector);
        waitForElementVisibility(browser.driver.findElement(errorMessageSelector));
    }

    public String getCopyErrorMessageText() {
        return browser.driver.findElement(
                By.cssSelector("#question-copy-modal-status.alert-danger")).getText();
    }

    public boolean isVisibilityDropdownOptionHidden(String optionValue, int qnNumber) {
        return browser.driver.findElement(By.id("questionTable-" + qnNumber))
                             .findElement(By.className("visibility-options-dropdown-option"))
                             .findElement(By.xpath("//a[@data-option-name='" + optionValue + "']/.."))
                             .getAttribute("class").contains("hidden");
    }

    public boolean isVisibilityDropdownOptionHiddenForNewQuestion(String optionValue) {
        return isVisibilityDropdownOptionHidden(optionValue, NEW_QUESTION_NUM);
    }

    public boolean isVisibilityDropdownSeparatorHidden(int qnNumber) {
        return browser.driver.findElement(By.id("questionTable-" + qnNumber))
                             .findElement(By.cssSelector(".visibility-options-dropdown .divider"))
                             .getAttribute("class").contains("hidden");
    }

    public boolean isVisibilityDropdownSeparatorHiddenForNewQuestion() {
        return isVisibilityDropdownSeparatorHidden(NEW_QUESTION_NUM);
    }

    private By getVisibilityMessageBy(int questionNumber) {
        return By.id("visibilityMessage-" + questionNumber);
    }

    private WebElement waitForAndGetVisibilityMessage(int questionNumber) {
        WebElement visibilityMessage = waitForElementPresence(getVisibilityMessageBy(questionNumber));
        waitForElementVisibility(visibilityMessage);
        return visibilityMessage;
    }

    public void verifyVisibilityMessageContains(int questionNumber, String message) {
        waitForAndGetVisibilityMessage(questionNumber);
        waitForTextContainedInElementPresence(getVisibilityMessageBy(questionNumber), message);
    }

    public void verifyVisibilityMessageContainsForNewQuestion(String message) {
        verifyVisibilityMessageContains(NEW_QUESTION_NUM, message);
    }

    public void verifyVisibilityMessageDoesNotContain(int questionNumber, String message) {
        waitForAndGetVisibilityMessage(questionNumber);
        waitForTextContainedInElementAbsence(getVisibilityMessageBy(questionNumber), message);
    }

    public void verifyVisibilityMessageDoesNotContainForNewQuestion(String message) {
        verifyVisibilityMessageDoesNotContain(NEW_QUESTION_NUM, message);
    }

    public boolean verifyVisibilityMessageIsDisplayed(int questionNumber) {
        WebElement firstMessage = waitForAndGetVisibilityMessage(questionNumber).findElement(By.cssSelector("ul > li"));
        return !firstMessage.getText().equals("Error loading visibility hint. Click here to retry.");
    }

    public boolean verifyVisibilityMessageIsDisplayedForNewQuestion() {
        return verifyVisibilityMessageIsDisplayed(NEW_QUESTION_NUM);
    }

    public boolean verifyVisibilityOptionsIsDisplayed(int questionNumber) {
        return getVisibilityOptions(questionNumber).isDisplayed();
    }

    /**
     * Waits and verifies that the texts of user status messages in the page are equal to the expected error texts.
     * @see AppPage#waitForTextsForAllStatusMessagesToUserEquals(String, String...)
     */
    public void waitForTextsForAllStatusMessagesToUserEqualsErrorTexts(
            String firstExpectedErrorText, String... remainingExpectedErrorTexts) {
        // LEGACY IMPLEMENTATION: Status Message is scrolled to when we try to add an invalid question
        // so we wait for no scrolling before verifying status message. This allows verification of status message to be
        // faster because the text should already be visible. Any tests that follows will then also not get affected by the
        // scrolling behavior.
        // PROBABLE BETTER IMPLEMENTATION: All AJAX Status messages (not just error status messages) always cause scrolling
        // so waiting for the status messages will include waiting for no scrolling before verifying the status messages.
        waitForScrollingToComplete();
        waitForTextsForAllStatusMessagesToUserEquals(firstExpectedErrorText, remainingExpectedErrorTexts);
    }

    public WebElement getVisibilityOptionTableRow(int questionNumber, int optionRowNumber) {
        return getVisibilityOptions(questionNumber).findElement(
                By.xpath("(table/tbody/tr|table/tbody/hide)[" + optionRowNumber + "]"));
    }

    public String getVisibilityParamShowResponsesTo(int questionNumber) {
        return browser.driver.findElement(By.id("form_editquestion-" + questionNumber))
                             .findElement(By.cssSelector("input[name='showresponsesto']"))
                             .getAttribute("value");
    }

    public String getVisibilityParamShowResponsesToForNewQuestion() {
        return getVisibilityParamShowResponsesTo(NEW_QUESTION_NUM);
    }

    public String getVisibilityParamShowGiverTo(int questionNumber) {
        return browser.driver.findElement(By.id("form_editquestion-" + questionNumber))
                             .findElement(By.cssSelector("input[name='showgiverto']"))
                             .getAttribute("value");
    }

    public String getVisibilityParamShowGiverToForNewQuestion() {
        return getVisibilityParamShowGiverTo(NEW_QUESTION_NUM);
    }

    public String getVisibilityParamShowRecipientTo(int questionNumber) {
        return browser.driver.findElement(By.id("form_editquestion-" + questionNumber))
                             .findElement(By.cssSelector("input[name='showrecipientto']"))
                             .getAttribute("value");
    }

    public String getVisibilityParamShowRecipientToForNewQuestion() {
        return getVisibilityParamShowRecipientTo(NEW_QUESTION_NUM);
    }

    public WebElement getVisibilityOptions(int questionNumber) {
        return browser.driver.findElement(By.id("visibilityOptions-" + questionNumber));
    }

    public WebElement getVisibilityOptionsForNewQuestion() {
        return getVisibilityOptions(NEW_QUESTION_NUM);
    }

    public void toggleNotSureCheck(int questionNumber) {
        click(browser.driver.findElement(By.id(Const.ParamsNames.FEEDBACK_QUESTION_CONTRIBISNOTSUREALLOWED
                                               + "-" + questionNumber)));
    }

    public void changeQuestionTypeInForm(int questionNumber, String newQuestionType) {
        String selector = "$('#form_editquestion-" + questionNumber + "').find('[name=\"questiontype\"]')";
        String action = ".val('" + newQuestionType + "')";
        executeScript(selector + action);
    }

    public void waitForAjaxErrorOnVisibilityMessageButton(int questionNumber) {
        String errorMessage = "Error loading visibility hint. Click here to retry.";
        By buttonSelector = By.cssSelector("#visibilityMessage-" + questionNumber + " > ul > li");
        waitForTextContainedInElementPresence(buttonSelector, errorMessage);
    }

    public void clickResponseVisibilityCheckBox(String checkBoxValue, int questionNumber) {
        By responseVisibilitycheckBox = By.cssSelector("#questionTable-" + questionNumber + " input[value='" + checkBoxValue
                                                       + "'].answerCheckbox");
        WebElement checkbox = browser.driver.findElement(responseVisibilitycheckBox);
        waitForElementVisibility(checkbox);
        click(checkbox);
    }

    public void clickResponseVisibilityCheckBoxForNewQuestion(String checkBoxValue) {
        clickResponseVisibilityCheckBox(checkBoxValue, NEW_QUESTION_NUM);
    }

    public void clickGiverNameVisibilityCheckBox(String checkBoxValue, int questionNumber) {
        By giverNameVisibilitycheckBox = By.cssSelector("#questionTable-" + questionNumber + " input[value='" + checkBoxValue
                                                       + "'].giverCheckbox");
        WebElement checkbox = browser.driver.findElement(giverNameVisibilitycheckBox);
        waitForElementVisibility(checkbox);
        click(checkbox);
    }

    public boolean isCheckboxChecked(String checkboxClass, String checkboxValue, int questionNumber) {
        By checkboxSelector = By.cssSelector("#questionTable-" + questionNumber + " input[value='" + checkboxValue
                                                       + "']." + checkboxClass);
        WebElement checkbox = browser.driver.findElement(checkboxSelector);
        return checkbox.isSelected();
    }

    public void clickEnableMinRankOptions(int questionNumber) {
        WebElement minNumberOfOptionsToRankCheckbox = getMinOptionsToBeRankedCheckbox(questionNumber);

        minNumberOfOptionsToRankCheckbox.click();
    }

    public void clickMinRankOptions(int questionNumber) {
        WebElement minRecipientsToBeRankedInput = getMinOptionsToBeRankedInputElement(questionNumber);

        minRecipientsToBeRankedInput.click();
    }

    public void clearMinRankOptions(int questionNumber) {
        WebElement minRecipientsToBeRankedInput = getMinOptionsToBeRankedInputElement(questionNumber);

        minRecipientsToBeRankedInput.clear();
    }
}
