package teammates.test.pageobjects;

import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.common.util.TimeHelper;

import com.google.appengine.api.datastore.Text;

public class InstructorFeedbackEditPage extends AppPage {
    
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
    
    @FindBy(id = Const.ParamsNames.FEEDBACK_SESSION_SESSIONVISIBLEBUTTON + "_never")
    private WebElement neverSessionVisibleTimeButton;
    
    @FindBy(id = Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON + "_never")
    private WebElement neverResultsVisibleTimeButton;
    
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
    
    @FindBy(id = "questiontext--1")
    private WebElement questionTextBox;

    @FindBy(id = "mcqOtherOptionFlag--1")
    private WebElement addMcqOtherOptionCheckboxForNewQuestion;
    
    @FindBy(id = "msqOtherOptionFlag--1")
    private WebElement addMsqOtherOptionCheckboxForNewQuestion;
    
    @FindBy(id = "givertype--1")
    private WebElement giverDropdown;
    
    @FindBy(id = "recipienttype--1")
    private WebElement recipientDropdown;
    
    @FindBy(id = "givertype-1")
    private WebElement giverDropdownForQuestion1;
    
    @FindBy(id = "recipienttype-1")
    private WebElement recipientDropdownForQuestion1;
    
    @FindBy(id = "questionedittext-1")
    private WebElement questionEditForQuestion1;
    
    @FindBy(id = "questionsavechangestext-1")
    private WebElement questionSaveForQuestion1;
    
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
     * @return number of question edit forms + question add form
     */
    public int getNumberOfQuestionEditForms() {
        return browser.driver.findElements(By.className("questionTable")).size();
    }
    
    public boolean isCorrectPage(String courseId, String feedbackSessionName) {
        boolean isCorrectCourseId = this.getCourseId().equals(courseId);
        boolean isCorrectFeedbackSessionName = this.getFeedbackSessionName().equals(feedbackSessionName);
        return isCorrectCourseId && isCorrectFeedbackSessionName && containsExpectedPageContents();
    }
    
    public void fillNewQuestionBox(String qnText) {
        fillTextBox(questionTextBox, qnText);
    }
    
    public void fillEditQuestionBox(String qnText, int qnIndex) {
        WebElement questionEditTextBox = browser.driver.findElement(By.id("questiontext-" + qnIndex));
        fillTextBox(questionEditTextBox, qnText);
    }
    
    public void fillNewQuestionDescription(String qnDescription) {
        fillRichTextEditor(Const.ParamsNames.FEEDBACK_QUESTION_DESCRIPTION + "--1", qnDescription);
    }

    public void fillEditQuestionDescription(String qnDescription, int qnIndex) {
        fillRichTextEditor(Const.ParamsNames.FEEDBACK_QUESTION_DESCRIPTION + "-" + qnIndex, qnDescription);
    }

    public void fillNumOfEntitiesToGiveFeedbackToBoxForNewQuestion(String num) {
        WebElement questionForm = browser.driver.findElement(By.id("form_editquestion--1"));
        WebElement numberOfRecipients = questionForm.findElement(By.className("numberOfEntitiesBox"));
        fillTextBox(numberOfRecipients, num);
    }

    public String getQuestionBoxText(int qnIndex) {
        WebElement questionEditTextBox = browser.driver.findElement(By.id("questiontext-" + qnIndex));
        return getTextBoxValue(questionEditTextBox);
    }
    
    private String getIdSuffix(int qnNumber) {
        int newQuestionNumber = -1;
        boolean isValid = qnNumber > 0 || qnNumber == newQuestionNumber;
        return isValid ? "-" + qnNumber : "";
    }
    
    public void fillMinNumScaleBox(int minScale, int qnNumber) {
        String idSuffix = getIdSuffix(qnNumber);
        
        WebElement minScaleBox = browser.driver.findElement(By.id("minScaleBox" + idSuffix));
        fillTextBox(minScaleBox, Integer.toString(minScale));
        
        executeScript("$(arguments[0]).change();", minScaleBox);
    }
    
    public void fillMaxNumScaleBox(int maxScale, int qnNumber) {
        String idSuffix = getIdSuffix(qnNumber);
        
        WebElement maxScaleBox = browser.driver.findElement(By.id("maxScaleBox" + idSuffix));
        fillTextBox(maxScaleBox, Integer.toString(maxScale));
        
        executeScript("$(arguments[0]).change();", maxScaleBox);
    }
    
    public void fillMinNumScaleBox(String minScale, int qnNumber) {
        String idSuffix = getIdSuffix(qnNumber);
        
        WebElement minScaleBox = browser.driver.findElement(By.id("minScaleBox" + idSuffix));
        fillTextBox(minScaleBox, minScale);
        
        executeScript("$(arguments[0]).change();", minScaleBox);
    }
    
    public void fillMaxNumScaleBox(String maxScale, int qnNumber) {
        String idSuffix = getIdSuffix(qnNumber);
        
        WebElement maxScaleBox = browser.driver.findElement(By.id("maxScaleBox" + idSuffix));
        fillTextBox(maxScaleBox, maxScale);
        
        executeScript("$(arguments[0]).change();", maxScaleBox);
    }
    
    public String getMaxNumScaleBox(int qnNumber) {
        String idSuffix = getIdSuffix(qnNumber);
        WebElement maxScaleBox = browser.driver.findElement(By.id("maxScaleBox" + idSuffix));
        return maxScaleBox.getAttribute("value");
    }
    
    public void fillStepNumScaleBox(double step, int qnNumber) {
        String idSuffix = getIdSuffix(qnNumber);
        
        WebElement stepBox = browser.driver.findElement(By.id("stepBox" + idSuffix));
        fillTextBox(stepBox, StringHelper.toDecimalFormatString(step));
        
        executeScript("$(arguments[0]).change();", stepBox);
    }
    
    public void fillStepNumScaleBox(String step, int qnNumber) {
        String idSuffix = getIdSuffix(qnNumber);
        
        WebElement stepBox = browser.driver.findElement(By.id("stepBox" + idSuffix));
        fillTextBox(stepBox, step);
        
        executeScript("$(arguments[0]).change();", stepBox);
    }
    
    public String getNumScalePossibleValuesString(int qnNumber) {
        String idSuffix = getIdSuffix(qnNumber);
        WebElement possibleValuesSpan = browser.driver.findElement(By.id("numScalePossibleValues" + idSuffix));
        return possibleValuesSpan.getText();
    }
    
    public void fillConstSumPointsBox(String points, int qnNumber) {
        String idSuffix = getIdSuffix(qnNumber);
        
        WebElement pointsBox = browser.driver.findElement(By.id("constSumPoints" + idSuffix));
        // backspace to clear the extra 1 when box is cleared.
        fillTextBox(pointsBox, Keys.RIGHT + " " + Keys.BACK_SPACE + points);
        
        executeScript("$(arguments[0]).change();", pointsBox);
    }
    
    public String getConstSumPointsBox(int qnNumber) {
        String idSuffix = getIdSuffix(qnNumber);
        WebElement constSumPointsBox = browser.driver.findElement(By.id("constSumPoints" + idSuffix));
        return constSumPointsBox.getAttribute("value");
    }
    
    public void fillConstSumPointsForEachOptionBox(String points, int qnNumber) {
        String idSuffix = getIdSuffix(qnNumber);
        
        WebElement pointsBox = browser.driver.findElement(By.id("constSumPointsForEachOption" + idSuffix));
        // backspace to clear the extra 1 when box is cleared.
        fillTextBox(pointsBox, Keys.RIGHT + " " + Keys.BACK_SPACE + points);
        
        executeScript("$(arguments[0]).change();", pointsBox);
    }
    
    public String getConstSumPointsForEachOptionBox(int qnNumber) {
        String idSuffix = getIdSuffix(qnNumber);
        WebElement constSumPointsBox = browser.driver.findElement(By.id("constSumPointsForEachOption" + idSuffix));
        return constSumPointsBox.getAttribute("value");
    }
    
    public void fillConstSumPointsForEachRecipientBox(String points, int qnNumber) {
        String idSuffix = getIdSuffix(qnNumber);
        
        WebElement pointsBox = browser.driver.findElement(By.id("constSumPointsForEachRecipient" + idSuffix));
        // backspace to clear the extra 1 when box is cleared.
        fillTextBox(pointsBox, Keys.RIGHT + " " + Keys.BACK_SPACE + points);
        
        executeScript("$(arguments[0]).change();", pointsBox);
    }
    
    public String getConstSumPointsForEachRecipientBox(int qnNumber) {
        String idSuffix = getIdSuffix(qnNumber);
        WebElement constSumPointsBox = browser.driver.findElement(By.id("constSumPointsForEachRecipient" + idSuffix));
        return constSumPointsBox.getAttribute("value");
    }
    
    public void fillRubricSubQuestionBox(String subQuestion, int qnNumber, int subQnIndex) {
        String idSuffix = getIdSuffix(qnNumber);
        
        String elemId = Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_SUBQUESTION + idSuffix + "-" + subQnIndex;
        
        WebElement subQnBox = browser.driver.findElement(By.id(elemId));
        fillTextBox(subQnBox, subQuestion);
    }
    
    public void fillRubricChoiceBox(String choice, int qnNumber, int choiceIndex) {
        String idSuffix = getIdSuffix(qnNumber);
        
        String elemId = Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_CHOICE + idSuffix + "-" + choiceIndex;
        
        WebElement subQnBox = browser.driver.findElement(By.id(elemId));
        fillTextBox(subQnBox, choice);
    }

    public void fillRubricWeightBox(String weight, int qnNumber, int choiceIndex) {
        String idSuffix = getIdSuffix(qnNumber);
        
        String elemid = Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_WEIGHT + idSuffix + "-" + choiceIndex;
        
        WebElement weightBox = browser.driver.findElement(By.id(elemid));
        fillTextBox(weightBox, weight);
    }

    public void fillRubricDescriptionBox(String description, int qnNumber, int subQnIndex, int choiceIndex) {
        String idSuffix = getIdSuffix(qnNumber);
        
        String elemId = Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_DESCRIPTION
                        + idSuffix + "-" + subQnIndex + "-" + choiceIndex;
        
        WebElement subQnBox = browser.driver.findElement(By.id(elemId));
        fillTextBox(subQnBox, description);
    }
    
    public void clickQuestionEditForQuestion1() {
        waitForElementToBeClickable(questionEditForQuestion1);
        click(questionEditForQuestion1);
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
        waitForElementNotCovered(fscopyButton);
        click(fscopyButton);
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
     * @return true if submission button of the 'copy question' modal is enabled
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
    
    public void clickAddMsqOtherOptionCheckboxForNewQuestion() {
        click(addMsqOtherOptionCheckboxForNewQuestion);
    }
    
    public WebElement getDeleteSessionLink() {
        return fsDeleteLink;
    }
    
    public void clickDeleteQuestionLink(int qnIndex) {
        WebElement link = browser.driver.findElement(By.xpath("//a[@onclick='deleteQuestion(" + qnIndex + ")']"));
        click(link);
    }
    
    public void clickDiscardChangesLink(int qnIndex) {
        WebElement link = browser.driver.findElement(By.xpath("//a[@onclick='discardChanges(" + qnIndex + ")']"));
        click(link);
    }
    
    public boolean isDiscardChangesButtonVisible(int qnIndex) {
        WebElement discardChangesButton =
                browser.driver.findElement(By.xpath("//a[@onclick='discardChanges(" + qnIndex + ")']"));
        
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
    
    public void clickquestionSaveForQuestion1() {
        click(questionSaveForQuestion1);
        waitForPageToLoad();
    }

    public void enableOtherVisibilityOptions(int qnNumber) {
        clickVisibilityDropdown("OTHER", qnNumber);
    }

    public void clickVisibilityDropdown(String optionValue, int qnNumber) {
        click(browser.driver.findElement(By.cssSelector("#questionTable-" + qnNumber + " .visibility-options-dropdown "
                                                        + "a[data-option-name=\"" + optionValue + "\"]")));
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
    
    public void selectQuestionNumber(int qnNumber, int newQnNumber) {
        WebElement qnNumSelect = browser.driver.findElement(By.id("questionnum-" + qnNumber));
        selectDropdownByVisibleValue(qnNumSelect, String.valueOf(newQnNumber));
    }
    
    /**
     * 
     * @return {@code True} if all elements expected to be enabled
     * in the edit session frame are enabled after edit link is clicked.
     * {@code False} if not.
     */
    public boolean verifyEditSessionBoxIsEnabled() {
        boolean isEditSessionEnabled = fsSaveLink.isDisplayed() && timezoneDropDown.isEnabled()
                                       // && "Session visible from" radio buttons
                                       && neverSessionVisibleTimeButton.isEnabled()
                                       && defaultSessionVisibleTimeButton.isEnabled()
                                       && customSessionVisibleTimeButton.isEnabled()
                                       // && "Send emails for" checkboxes
                                       && closingSessionEmailReminderButton.isEnabled()
                                       && publishedSessionEmailReminderButton.isEnabled();
        
        if (isEditSessionEnabled && !neverSessionVisibleTimeButton.isSelected()) {
            isEditSessionEnabled = gracePeriodDropdown.isEnabled() // && Submission times inputs
                                   && startDateBox.isEnabled() && startTimeDropdown.isEnabled()
                                   && endDateBox.isEnabled() && endTimeDropdown.isEnabled()
                                   // && "Responses visible from" radio buttons
                                   && defaultResultsVisibleTimeButton.isEnabled()
                                   && customResultsVisibleTimeButton.isEnabled()
                                   && manualResultsVisibleTimeButton.isEnabled()
                                   && neverResultsVisibleTimeButton.isEnabled();
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

    public boolean areDatesOfPreviousCurrentAndNextMonthEnabled() throws ParseException {
        return areDatesOfPreviousCurrentAndNextMonthEnabled(startDateBox)
               && areDatesOfPreviousCurrentAndNextMonthEnabled(endDateBox);
    }

    /**
     * @param dateBox is a {@link WebElement} that triggers a datepicker
     * @return true if the dates of previous, current and next month are
     *         enabled, otherwise false
     * @throws ParseException if the string in {@code dateBox} cannot be parsed
     */
    private boolean areDatesOfPreviousCurrentAndNextMonthEnabled(WebElement dateBox) throws ParseException {

        Calendar previousMonth = Calendar.getInstance();
        previousMonth.add(Calendar.MONTH, -1);

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
     * Navigate the datepicker associated with {@code dateBox} to the specified {@code date}
     * 
     * @param dateBox is a {@link WebElement} that triggers a datepicker
     * @param date is a {@link Calendar} that specifies the date that needs to be navigated to
     * @return true if navigated to the {@code date} successfully, otherwise
     *         false
     * @throws ParseException if the string in {@code dateBox} cannot be parsed
     */
    private boolean navigate(WebElement dateBox, Calendar date) throws ParseException {

        click(dateBox);

        Calendar selectedDate = Calendar.getInstance();

        String month = date.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH);
        String year = Integer.toString(date.get(Calendar.YEAR));

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        selectedDate.setTime(dateFormat.parse(dateBox.getAttribute("value")));

        if (selectedDate.after(date)) {

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

    public void selectNewQuestionType(String questionType) {
        click(browser.driver.findElement(By.cssSelector("[data-questionType=" + questionType + "]")));
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
    
    public void selectConstSumPointsOptions(String pointsOption, int questionNumber) {
        markRadioButtonAsChecked(
                browser.driver.findElement(By.id("constSumPoints" + pointsOption + "-" + questionNumber)));
    }
    
    public String getGiverTypeForQuestion1() {
        return giverDropdownForQuestion1.getAttribute("value");
    }
    
    public String getRecipientTypeForQuestion1() {
        return recipientDropdownForQuestion1.getAttribute("value");
    }
    
    public void selectRecipientTypeForNewQuestion(String recipientType) {
        selectDropdownByVisibleValue(browser.driver.findElement(By.id("recipienttype--1")), recipientType);
    }
    
    /**
     * 
     * @return {@code True} if the button was clicked successfully and an element in the new question
     * frame is now visible. {@code False} if not.
     */
    public void clickNewQuestionButton() {
        click(openNewQuestionButton);
    }

    public boolean isAllFeedbackPathOptionsEnabledForNewQuestion() {
        List<WebElement> options = browser.driver.findElements(By.cssSelector("#givertype--1 option"));
        options.addAll(browser.driver.findElements(By.cssSelector("#recipienttype--1 option")));
        for (WebElement option : options) {
            if (!option.isEnabled()) {
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
        selectDropdownByActualValue(giverDropdown, giverType.toString());
    }

    public void selectRecipientToBe(FeedbackParticipantType recipientType, int questionNumber) {
        WebElement giverDropdown = browser.driver.findElement(By.id("recipienttype-" + questionNumber));
        selectDropdownByActualValue(giverDropdown, recipientType.toString());
    }

    public void selectGiverToBeStudents() {
        selectDropdownByVisibleValue(giverDropdown, "Students in this course");
    }
    
    public void selectGiverToBeInstructors() {
        selectDropdownByVisibleValue(giverDropdown, "Instructors in this course");
    }
    
    public void selectRecipientsToBeStudents() {
        selectDropdownByVisibleValue(recipientDropdown, "Other students in the course");
    }
    
    public void selectRecipientsToBeGiverTeamMembersAndGiver() {
        selectDropdownByVisibleValue(recipientDropdown, "Giver's team members and Giver");
    }

    public void selectRecipientsToBeInstructors() {
        selectDropdownByVisibleValue(recipientDropdown, "Instructors in the course");
    }

    public void selectRecipientsToBeStudents(int qnNumber) {
        WebElement recipientDropdown = browser.driver.findElement(By.id("recipienttype-" + qnNumber));
        selectDropdownByVisibleValue(recipientDropdown, "Other students in the course");
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
        WebElement questionTable = browser.driver.findElement(By.id("questionTable--1"));
        WebElement dropdownButton = questionTable.findElement(By.cssSelector(".feedback-path-dropdown > button"));
        WebElement otherOption = questionTable.findElement(
                                     By.className("feedback-path-dropdown-option-other"));
        click(dropdownButton);
        click(otherOption);
    }

    public void editFeedbackSession(Date startTime, Date endTime, Text instructions, int gracePeriod) {
        // Select start date
        executeScript("$('#" + Const.ParamsNames.FEEDBACK_SESSION_STARTDATE + "')[0].value='"
                      + TimeHelper.formatDate(startTime) + "';");
        selectDropdownByVisibleValue(startTimeDropdown,
                                     TimeHelper.convertToDisplayValueInTimeDropDown(startTime));
    
        // Select deadline date
        executeScript("$('#" + Const.ParamsNames.FEEDBACK_SESSION_ENDDATE + "')[0].value='"
                      + TimeHelper.formatDate(endTime) + "';");
        selectDropdownByVisibleValue(endTimeDropdown,
                                     TimeHelper.convertToDisplayValueInTimeDropDown(endTime));
        
        // Fill in instructions
        fillRichTextEditor("instructions", instructions.getValue());

        // Select grace period
        selectDropdownByVisibleValue(gracePeriodDropdown, Integer.toString(gracePeriod) + " mins");
    
        click(fsSaveLink);
        waitForPageToLoad();
    }
    
    public InstructorFeedbacksPage deleteSession() {
        clickAndConfirm(getDeleteSessionLink());
        waitForPageToLoad();
        return changePageType(InstructorFeedbacksPage.class);
    }
    
    public InstructorFeedbacksPage clickDoneEditingLink() {
        click(doneEditingButton);
        waitForPageToLoad();
        return changePageType(InstructorFeedbacksPage.class);
    }
    
    public void fillMcqOption(int optionIndex, String optionText) {
        WebElement optionBox = browser.driver.findElement(By.id("mcqOption-" + optionIndex + "--1"));
        fillTextBox(optionBox, optionText);
    }
    
    public void clickAddMoreMcqOptionLinkForNewQuestion() {
        WebElement addMoreOptionLink = browser.driver.findElement(By.id("mcqAddOptionLink--1"));
        click(addMoreOptionLink);
    }
    
    public void clickRemoveMcqOptionLink(int optionIndex, int qnIndex) {
        String idSuffix = getIdSuffix(qnIndex);
        
        WebElement mcqOptionRow = browser.driver.findElement(By.id("mcqOptionRow-" + optionIndex + idSuffix));
        WebElement removeOptionLink = mcqOptionRow.findElement(By.id("mcqRemoveOptionLink"));
        click(removeOptionLink);
    }
    
    public void clickGenerateOptionsCheckbox(int qnIndex) {
        String idSuffix = getIdSuffix(qnIndex);
        
        WebElement generateOptionsCheckbox = browser.driver.findElement(By.id("generateOptionsCheckbox" + idSuffix));
        click(generateOptionsCheckbox);
    }
    
    public void fillMsqOption(int optionIndex, String optionText) {
        WebElement optionBox = browser.driver.findElement(By.id("msqOption-" + optionIndex + "--1"));
        fillTextBox(optionBox, optionText);
    }
    
    public void clickAddMoreMsqOptionLink(int qnIndex) {
        WebElement addMoreOptionLink = browser.driver.findElement(By.id("msqAddOptionLink-" + qnIndex));
        click(addMoreOptionLink);
    }
    
    public void clickRemoveMsqOptionLink(int optionIndex, int qnIndex) {
        String idSuffix = getIdSuffix(qnIndex);
        
        WebElement msqOptionRow = browser.driver.findElement(By.id("msqOptionRow-" + optionIndex + idSuffix));
        WebElement removeOptionLink = msqOptionRow.findElement(By.id("msqRemoveOptionLink"));
        click(removeOptionLink);
    }
    
    // For new question frame
    public void fillConstSumOption(int optionIndex, String optionText) {
        WebElement optionBox = browser.driver.findElement(By.id("constSumOption-" + optionIndex + "--1"));
        fillTextBox(optionBox, optionText);
    }
    
    // For existing question edit frame
    public void fillConstSumOption(int optionIndex, String optionText, int qnIndex) {
        String idSuffix = getIdSuffix(qnIndex);
        
        WebElement optionBox = browser.driver.findElement(By.id("constSumOption-" + optionIndex + idSuffix));
        fillTextBox(optionBox, optionText);
    }
    
    public void clickAddMoreConstSumOptionLink(int qnIndex) {
        WebElement addMoreOptionLink = browser.driver.findElement(By.id("constSumAddOptionLink-" + qnIndex));
        click(addMoreOptionLink);
    }
    
    public void clickRemoveConstSumOptionLink(int optionIndex, int qnIndex) {
        String idSuffix = getIdSuffix(qnIndex);
        
        WebElement msqOptionRow = browser.driver.findElement(By.id("constSumOptionRow-" + optionIndex + idSuffix));
        WebElement removeOptionLink = msqOptionRow.findElement(By.id("constSumRemoveOptionLink"));
        click(removeOptionLink);
    }
    
    public void clickAssignWeightsCheckbox(int qnIndex) {
        By by = By.id(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_WEIGHTS_ASSIGNED + getIdSuffix(qnIndex));
        WebElement assignWeightsCheckbox = browser.driver.findElement(by);
        click(assignWeightsCheckbox);
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
        WebElement optionBox = browser.driver.findElement(By.id("rankOption-" + optionIndex + "--1"));
        assertFalse(optionBox.isDisplayed());
    }
    
    public void fillRankOptionForNewQuestion(int optionIndex, String optionText) {
        WebElement optionBox = browser.driver.findElement(By.id("rankOption-" + optionIndex + "--1"));
        fillTextBox(optionBox, optionText);
    }
    
    public void fillRankOptionForQuestion(int qnIndx, int optionIndex, String optionText) {
        WebElement optionBox = browser.driver.findElement(By.id("rankOption-" + optionIndex + "-" + qnIndx));
        fillTextBox(optionBox, optionText);
    }
    
    public void tickDuplicatesAllowedCheckboxForNewQuestion() {
        tickDuplicatesAllowedCheckboxForQuestion(-1);
    }
    
    public void tickDuplicatesAllowedCheckboxForQuestion(int qnIndex) {
        WebElement checkBox = toggleDuplicatesAllowedCheckBox(qnIndex);
        assertTrue(checkBox.isSelected());
    }
    
    public void untickDuplicatesAllowedCheckboxForQuestion(int qnIndex) {
        WebElement checkBox = toggleDuplicatesAllowedCheckBox(qnIndex);
        assertFalse(checkBox.isSelected());
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
    
    public void clickAddMoreRankOptionLinkForNewQn() {
        WebElement addMoreOptionLink = browser.driver.findElement(By.id("rankAddOptionLink--1"));
        click(addMoreOptionLink);
    }
    
    public void clickAddMoreRankOptionLink(int qnIndex) {
        WebElement addMoreOptionLink = browser.driver.findElement(By.id("rankAddOptionLink-" + qnIndex));
        click(addMoreOptionLink);
    }
    
    public void clickRemoveRankOptionLink(int qnIndex, int optionIndex) {
        String idSuffix = getIdSuffix(qnIndex);
        
        WebElement msqOptionRow = browser.driver.findElement(By.id("rankOptionRow-" + optionIndex + idSuffix));
        WebElement removeOptionLink = msqOptionRow.findElement(By.id("rankRemoveOptionLink"));
        click(removeOptionLink);
    }
    
    public int getNumOfOptionsInRankOptionsQuestion(int qnIndex) {
        WebElement rankOptionsTable = browser.driver.findElement(By.id("rankOptionTable-" + qnIndex));
        List<WebElement> optionInputFields = rankOptionsTable
                                                .findElements(
                                                     By.cssSelector("input[id^='rankOption-']"));
        return optionInputFields.size();
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
    
    public boolean verifyVisibilityMessageIsDisplayed(int questionNumber) {
        WebElement visibilityMessageDiv = getVisibilityMessageDiv(questionNumber);
        waitForElementVisibility(visibilityMessageDiv);
        List<WebElement> visibilityMessages = visibilityMessageDiv.findElements(By.cssSelector("ul > li"));
        boolean isLoadVisibilityMessageAjaxError =
                visibilityMessages.get(0).getText().equals("Error loading visibility hint. Click here to retry.");
        return !visibilityMessages.isEmpty() && !isLoadVisibilityMessageAjaxError;
    }

    public boolean verifyVisibilityOptionsIsDisplayed(int questionNumber) {
        return getVisibilityOptions(questionNumber).isDisplayed();
    }

    public WebElement getVisibilityOptionTableRow(int questionNumber, int optionRowNumber) {
        return getVisibilityOptions(questionNumber).findElement(
                By.xpath("(table/tbody/tr|table/tbody/hide)[" + optionRowNumber + "]"));
    }

    public WebElement getVisibilityMessageDiv(int questionNumber) {
        return browser.driver.findElement(By.id("visibilityMessage-" + questionNumber));
    }
    
    public String getVisibilityMessage(int questionNumber) {
        WebElement visibilityMessageDiv = getVisibilityMessageDiv(questionNumber);
        return visibilityMessageDiv.getText();
    }

    public WebElement getVisibilityOptions(int questionNumber) {
        return browser.driver.findElement(By.id("visibilityOptions-" + questionNumber));
    }
    
    public WebElement getNewQnVisibilityOptions() {
        return browser.driver.findElement(By.id("visibilityOptions--1"));
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
}
