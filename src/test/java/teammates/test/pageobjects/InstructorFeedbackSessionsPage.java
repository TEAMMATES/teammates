package teammates.test.pageobjects;

import static org.testng.AssertJUnit.fail;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.google.appengine.api.datastore.Text;

import teammates.common.util.Const;
import teammates.common.util.TimeHelper;

public class InstructorFeedbackSessionsPage extends AppPage {

    @FindBy(id = "fstype")
    private WebElement fsType;

    @FindBy(id = "courseid")
    private WebElement courseIdDropdown;

    @FindBy(id = "fsname")
    private WebElement fsNameTextBox;

    @FindBy(id = "starttime")
    private WebElement startTimeDropdown;

    @FindBy(id = "endtime")
    private WebElement endTimeDropdown;

    @FindBy (id = "visibletime")
    private WebElement visibleTimeDropdown;

    @FindBy (id = "publishtime")
    private WebElement publishTimeDropdown;

    @FindBy (id = "timezone")
    private WebElement timezoneDropdown;

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

    @FindBy(id = Const.ParamsNames.FEEDBACK_SESSION_SESSIONVISIBLEBUTTON + "_never")
    private WebElement neverSessionVisibleTimeButton;

    @FindBy(id = Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON + "_never")
    private WebElement neverResultsVisibleTimeButton;

    @FindBy(id = Const.ParamsNames.FEEDBACK_SESSION_SESSIONVISIBLEBUTTON + "_atopen")
    private WebElement defaultSessionVisibleTimeButton;

    @FindBy(id = Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON + "_atvisible")
    private WebElement defaultResultsVisibleTimeButton;

    @FindBy(id = Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON + "_later")
    private WebElement manualResultsVisibleTimeButton;

    @FindBy(id = Const.ParamsNames.FEEDBACK_SESSION_SENDREMINDEREMAIL + "_open")
    private WebElement sendOpenEmailCheckbox;

    @FindBy(id = Const.ParamsNames.FEEDBACK_SESSION_SENDREMINDEREMAIL + "_closing")
    private WebElement sendClosingEmailCheckbox;

    @FindBy(id = Const.ParamsNames.FEEDBACK_SESSION_SENDREMINDEREMAIL + "_published")
    private WebElement sendPublishedEmailCheckbox;

    @FindBy(id = "button_submit")
    private WebElement submitButton;

    @FindBy(id = "button_copy")
    private WebElement copyButton;

    @FindBy(id = "button_copy_submit")
    private WebElement copySubmitButton;

    @FindBy(id = "modalCopiedCourseId")
    private WebElement copiedCourseIdDropdown;

    @FindBy(id = "modalCopiedSessionName")
    private WebElement copiedFsNameTextBox;

    @FindBy(id = "button_sortname")
    private WebElement sortByNameIcon;

    @FindBy(id = "button_sortid")
    private WebElement sortByIdIcon;

    private InstructorCopyFsToModal fsCopyToModal;

    public InstructorFeedbackSessionsPage(Browser browser) {
        super(browser);
        fsCopyToModal = new InstructorCopyFsToModal(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("<h1>Add New Feedback Session</h1>");
    }

    public InstructorCopyFsToModal getFsCopyToModal() {
        return fsCopyToModal;
    }

    public void selectSessionType(String visibleText) {
        selectDropdownByVisibleValue(fsType, visibleText);
    }

    public AppPage sortByName() {
        click(sortByNameIcon);
        waitForPageToLoad();
        return this;
    }

    public AppPage sortById() {
        click(sortByIdIcon);
        waitForPageToLoad();
        return this;
    }

    public void clickSubmitButton() {
        click(submitButton);
        waitForPageToLoad();
    }

    public void clickEditUncommonSettingsButtons() {
        clickEditUncommonSettingsSessionResponsesVisibleButton();
        clickEditUncommonSettingsSendEmailsButton();
    }

    public void clickEditUncommonSettingsSessionResponsesVisibleButton() {
        click(uncommonSettingsSessionResponsesVisibleButton);
    }

    public void clickEditUncommonSettingsSendEmailsButton() {
        click(uncommonSettingsSendEmailsButton);
    }

    public void clickCustomVisibleTimeButton() {
        click(customSessionVisibleTimeButton);
    }

    public void clickCustomPublishTimeButton() {
        click(customResultsVisibleTimeButton);
    }

    public void clickNeverVisibleTimeButton() {
        click(neverSessionVisibleTimeButton);
    }

    public void clickNeverPublishTimeButton() {
        click(neverResultsVisibleTimeButton);
    }

    public void clickManualPublishTimeButton() {
        click(manualResultsVisibleTimeButton);
    }

    public void clickDefaultVisibleTimeButton() {
        click(defaultSessionVisibleTimeButton);
    }

    public void clickDefaultPublishTimeButton() {
        click(defaultResultsVisibleTimeButton);
    }

    public void clickCopyButton() {
        click(copyButton);
    }

    public void clickCopySubmitButton() {
        click(copySubmitButton);
        waitForPageToLoad();
    }

    public void clickViewResponseLink(String courseId, String sessionName) {
        click(getViewResponseLink(courseId, sessionName));
        waitForPageToLoad();
    }

    public void toggleSendOpenEmailCheckbox() {
        click(sendOpenEmailCheckbox);
    }

    public void toggleSendClosingEmailCheckbox() {
        click(sendClosingEmailCheckbox);
    }

    public void toggleSendPublishedEmailCheckbox() {
        click(sendPublishedEmailCheckbox);
    }

    public void addFeedbackSession(
            String feedbackSessionName,
            String courseId,
            Date startTime,
            Date endTime,
            Date visibleTime,
            Date publishTime,
            Text instructions,
            int gracePeriod) {

        fillTextBox(fsNameTextBox, feedbackSessionName);

        waitForElementVisibility(courseIdDropdown);
        selectDropdownByVisibleValue(courseIdDropdown, courseId);

        // fill in time values
        fillStartTime(startTime);
        fillEndTime(endTime);
        fillVisibleTime(visibleTime);
        fillPublishTime(publishTime);

        // Fill in instructions
        if (instructions != null) {
            fillRichTextEditor("instructions", instructions.getValue());
        }

        // Select grace period
        if (gracePeriod != -1) {
            selectDropdownByVisibleValue(gracePeriodDropdown, Integer.toString(gracePeriod) + " mins");
        }

        clickSubmitButton();
    }

    public void addFeedbackSessionWithTimeZone(String feedbackSessionName, String courseId, Date startTime,
            Date endTime, Date visibleTime, Date publishTime, Text instructions, int gracePeriod, double timeZone) {

        selectTimeZone(timeZone);

        addFeedbackSession(
                feedbackSessionName, courseId, startTime, endTime, visibleTime, publishTime, instructions, gracePeriod);
    }

    public void addFeedbackSessionWithStandardTimeZone(String feedbackSessionName, String courseId, Date startTime,
            Date endTime, Date visibleTime, Date publishTime, Text instructions, int gracePeriod) {

        addFeedbackSessionWithTimeZone(
                feedbackSessionName, courseId, startTime, endTime, visibleTime, publishTime, instructions, gracePeriod, 8.0);
    }

    private void selectTimeZone(double timeZone) {
        String timeZoneString = Double.toString(timeZone);

        double fractionalPart = timeZone % 1;

        if (fractionalPart == 0.0) {
            timeZoneString = Integer.toString((int) timeZone);
        }

        selectDropdownByActualValue(timezoneDropdown, timeZoneString);
    }

    public void copyFeedbackSession(String feedbackSessionName, String courseId) {
        String copyButtonId = "button_copy";
        this.waitForTextContainedInElementPresence(
                By.id(copyButtonId), "Copy from previous feedback sessions");
        clickCopyButton();
        this.waitForElementVisibility(copiedFsNameTextBox);
        fillTextBox(copiedFsNameTextBox, feedbackSessionName);
        selectDropdownByVisibleValue(copiedCourseIdDropdown, courseId);

        clickCopyTableAtRow(0);
        clickCopySubmitButton();
    }

    public void copyFeedbackSessionTestButtons(String feedbackSessionName, String courseId) {
        clickCopyButton();
        this.waitForElementVisibility(copiedFsNameTextBox);
        fillTextBox(copiedFsNameTextBox, feedbackSessionName);
        selectDropdownByVisibleValue(copiedCourseIdDropdown, courseId);
    }

    public void clickCopyTableAtRow(int rowIndex) {
        WebElement row = browser.driver.findElement(By.id("copyTableModal"))
                                       .findElements(By.tagName("tr"))
                                       .get(rowIndex + 1);
        click(row);
    }

    public void clickCopyTableRadioButtonAtRow(int rowIndex) {
        WebElement button = browser.driver.findElement(By.id("copyTableModal"))
                                       .findElements(By.tagName("tr"))
                                       .get(rowIndex + 1).findElement(By.tagName("input"));
        click(button);
    }

    public void fillStartTime(Date startTime) {
        fillTimeValueIfNotNull(Const.ParamsNames.FEEDBACK_SESSION_STARTDATE, startTime, startTimeDropdown);
    }

    public void fillEndTime(Date endTime) {
        fillTimeValueIfNotNull(Const.ParamsNames.FEEDBACK_SESSION_ENDDATE, endTime, endTimeDropdown);
    }

    public void fillVisibleTime(Date visibleTime) {
        fillTimeValueIfNotNull(Const.ParamsNames.FEEDBACK_SESSION_VISIBLEDATE, visibleTime, visibleTimeDropdown);
    }

    public void fillPublishTime(Date publishTime) {
        fillTimeValueIfNotNull(Const.ParamsNames.FEEDBACK_SESSION_PUBLISHDATE, publishTime, publishTimeDropdown);
    }

    public void fillTimeValueIfNotNull(String dateId, Date datetimeValue, WebElement timeDropdown) {
        if (datetimeValue != null) {
            executeScript("$('#" + dateId + "').val('" + TimeHelper.formatDateForSessionsForm(datetimeValue) + "');");

            String timeDropdownId = timeDropdown.getAttribute("id");
            int timeDropdownVal = TimeHelper.convertToOptionValueInTimeDropDown(datetimeValue);
            executeScript("$('#" + timeDropdownId + "').val(" + timeDropdownVal + ")");
        }
    }

    /**
     * This method contains an intended mix of Selenium and JavaScript to ensure that the test
     * passes consistently, do not try to click on the datepicker element using Selenium as it will
     * result in a test that passes or fail randomly.
    */
    public void fillTimeValueForDatePickerTest(String timeId, Calendar newValue) {
        WebElement dateInputElement = browser.driver.findElement(By.id(timeId));
        click(dateInputElement);
        dateInputElement.clear();
        dateInputElement.sendKeys(TimeHelper.formatDateForSessionsForm(newValue.getTime()));

        List<WebElement> elements = browser.driver.findElements(By.className("ui-datepicker-current-day"));
        for (WebElement element : elements) {
            click(element);
        }
    }

    public String getValueOfDate(String timeId) {
        return (String) executeScript("return $('#" + timeId + "').datepicker('getDate') == null ? "
                                      + "null : "
                                      + "$('#" + timeId + "').datepicker('getDate').toDateString();");
    }

    public String getMinDateOf(String timeId) {
        return (String) executeScript("return $('#" + timeId + "').datepicker('option', 'minDate') == null ? "
                                      + "null : "
                                      + "$('#" + timeId + "').datepicker('option', 'minDate').toDateString();");
    }

    public String getMaxDateOf(String timeId) {
        return (String) executeScript("return $('#" + timeId + "').datepicker('option', 'maxDate') == null ? "
                                      + "null : "
                                      + "$('#" + timeId + "').datepicker('option', 'maxDate').toDateString();");
    }

    public String getSessionType() {
        return fsType.getAttribute("value");
    }

    public String getStartTime() {
        return startTimeDropdown.getAttribute("value");
    }

    public String getEndTime() {
        return endTimeDropdown.getAttribute("value");
    }

    public String getTimeZone() {
        return timezoneDropdown.getAttribute("value");
    }

    public String getInstructions() {
        return getRichTextEditorContent("instructions");
    }

    public boolean isRowSelected(int rowIndex) {
        WebElement row = browser.driver.findElement(By.id("copyTableModal"))
                                        .findElements(By.tagName("tr"))
                                        .get(rowIndex + 1);

        return row.getAttribute("class").contains("row-selected");
    }

    public boolean isRadioButtonChecked(int rowIndex) {
        WebElement button = browser.driver.findElement(By.id("copyTableModal"))
                                        .findElements(By.tagName("tr"))
                                        .get(rowIndex + 1).findElement(By.tagName("input"));

        return button.isSelected();
    }

    public boolean isCopySubmitButtonEnabled() {
        return copySubmitButton.isEnabled();
    }

    public String getClientTimeZone() {
        return (String) executeScript("return (-(new Date()).getTimezoneOffset() / 60).toString()");
    }

    public WebElement getViewResponseLink(String courseId, String sessionName) {
        int sessionRowId = getFeedbackSessionRowId(courseId, sessionName);
        return browser.driver.findElement(
                By.xpath("//tbody/tr[" + (sessionRowId + 1)
                + "]/td[contains(@class,'session-response-for-test')]/a"));
    }

    public String getResponseValue(String courseId, String sessionName) {
        int sessionRowId = getFeedbackSessionRowId(courseId, sessionName);
        return browser.driver.findElement(
                By.xpath("//tbody/tr[" + (sessionRowId + 1)
                + "]/td[contains(@class,'session-response-for-test')]")).getText();
    }

    public void verifyResponseValue(String responseRate, String courseId, String sessionName) {
        int sessionRowId = getFeedbackSessionRowId(courseId, sessionName);
        waitForTextContainedInElementPresence(
                By.xpath("//tbody/tr[" + (sessionRowId + 1) + "]/td[contains(@class,'session-response-for-test')]"),
                responseRate);
    }

    public WebElement getViewResultsLink(String courseId, String sessionName) {
        int sessionRowId = getFeedbackSessionRowId(courseId, sessionName);
        return getLinkAtTableRow("session-view-for-test", sessionRowId);
    }

    public WebElement getEditLink(String courseId, String sessionName) {
        int sessionRowId = getFeedbackSessionRowId(courseId, sessionName);
        return getLinkAtTableRow("session-edit-for-test", sessionRowId);
    }

    public WebElement getDeleteLink(String courseId, String sessionName) {
        int sessionRowId = getFeedbackSessionRowId(courseId, sessionName);
        return getLinkAtTableRow("session-delete-for-test", sessionRowId);
    }

    public WebElement getSubmitLink(String courseId, String sessionName) {
        int sessionRowId = getFeedbackSessionRowId(courseId, sessionName);
        return getLinkAtTableRow("session-submit-for-test", sessionRowId);
    }

    public WebElement getPublishLink(String courseId, String sessionName) {
        int sessionRowId = getFeedbackSessionRowId(courseId, sessionName);
        return getLinkAtTableRow("session-publish-for-test", sessionRowId);
    }

    public WebElement getUnpublishLink(String courseId, String sessionName) {
        int sessionRowId = getFeedbackSessionRowId(courseId, sessionName);
        return getLinkAtTableRow("session-unpublish-for-test", sessionRowId);
    }

    public void verifyPublishLinkHidden(String courseId, String sessionName) {
        int sessionRowId = getFeedbackSessionRowId(courseId, sessionName);
        try {
            getLinkAtTableRow("session-publish-for-test", sessionRowId);
            fail("This element should be hidden.");
        } catch (NoSuchElementException e) {
            return;
        }
    }

    public void verifyUnpublishLinkHidden(String courseId, String sessionName) {
        int sessionRowId = getFeedbackSessionRowId(courseId, sessionName);
        try {
            getLinkAtTableRow("session-unpublish-for-test", sessionRowId);
            fail("This element should be hidden.");
        } catch (NoSuchElementException e) {
            return;
        }
    }

    public boolean isSessionResultsOptionsCaretDisabled(String courseId, String sessionName) {
        int sessionRowId = getFeedbackSessionRowId(courseId, sessionName);
        return !browser.driver.findElement(
                By.xpath("//tbody/tr[" + (sessionRowId + 1)
                    + "]//button[contains(@class,'session-results-options')]")).isEnabled();
    }

    public boolean isHidden(By locator) {
        return !browser.driver.findElement(locator).isDisplayed();
    }

    public boolean isEnabled(By locator) {
        return browser.driver.findElement(locator).isEnabled();
    }

    public boolean isDisabled(By locator) {
        return !browser.driver.findElement(locator).isEnabled();
    }

    public boolean isVisible(By locator) {
        return browser.driver.findElement(locator).isDisplayed();
    }

    public boolean isContainingCssClass(By locator, String className) {
        return browser.driver.findElement(locator).getAttribute("class").matches(".*\\b" + className + "\\b.*");
    }

    public InstructorFeedbackResultsPage loadViewResultsLink(String courseId, String fsName) {
        int sessionRowId = getFeedbackSessionRowId(courseId, fsName);
        String className = "session-view-for-test";
        return goToLinkInRow(
                By.xpath("//tbody/tr[" + (sessionRowId + 1)
                + "]//a[contains(@class,'" + className + "')]"),
                InstructorFeedbackResultsPage.class);
    }

    public FeedbackSubmitPage loadSubmitLink(String courseId, String fsName) {
        int sessionRowId = getFeedbackSessionRowId(courseId, fsName);
        String className = "session-submit-for-test";
        return goToLinkInRow(
                By.xpath("//tbody/tr[" + (sessionRowId + 1)
                + "]//a[contains(@class,'" + className + "')]"),
                FeedbackSubmitPage.class);
    }

    public InstructorFeedbackEditPage loadEditLink(String courseId, String fsName) {
        int sessionRowId = getFeedbackSessionRowId(courseId, fsName);
        String className = "session-edit-for-test";
        return goToLinkInRow(
                By.xpath("//tbody/tr[" + (sessionRowId + 1)
                + "]//a[contains(@class,'" + className + "')]"),
                InstructorFeedbackEditPage.class);
    }

    public String getPageUrl() {
        return browser.driver.getCurrentUrl();
    }

    private WebElement getLinkAtTableRow(String className, int rowIndex) {
        return browser.driver.findElement(
                By.xpath("//table[contains(@id,'table-sessions')]//tbody/tr["
                + (rowIndex + 1) + "]//a[contains(@class,'" + className + "')]"));
    }

    private int getFeedbackSessionRowId(String courseId, String sessionName) {
        int i = 0;
        while (i < getFeedbackSessionsCount()) {
            if (getFeedbackSessionCourseId(i).equals(courseId)
                    && getFeedbackSessionName(i).equals(sessionName)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    private int getFeedbackSessionsCount() {
        return browser.driver.findElements(By.className("sessionsRow")).size();
    }

    private String getFeedbackSessionCourseId(int rowId) {
        return browser.driver.findElement(By.id("table-sessions"))
                             .findElements(By.xpath("tbody/tr")).get(rowId)
                             .findElements(By.xpath("td")).get(0)
                             .getText();
    }

    private String getFeedbackSessionName(int rowId) {
        return browser.driver.findElement(By.id("table-sessions"))
                             .findElements(By.xpath("tbody/tr")).get(rowId)
                             .findElements(By.xpath("td")).get(1)
                             .getText();
    }

    private <T extends AppPage> T goToLinkInRow(By locator, Class<T> destinationPageType) {
        click(browser.driver.findElement(locator));
        waitForPageToLoad();
        return changePageType(destinationPageType);
    }

    public void clickFsCopyButton(String courseId, String feedbackSessionName) {
        By fsCopyButtonElement = By.id("button_fscopy" + "-" + courseId + "-" + feedbackSessionName);

        // give it some time to load as it is loaded via AJAX
        waitForElementPresence(fsCopyButtonElement);

        WebElement fsCopyButton = browser.driver.findElement(fsCopyButtonElement);
        click(fsCopyButton);
    }

    public void changeUserIdInAjaxForSessionsForm(String newUserId) {
        executeScript("$('#ajaxForSessions [name=\"user\"]').val('" + newUserId + "');");
    }

    public void reloadSessionsList() {
        executeScript("setIsSessionsAjaxSendingFalse();");
        executeScript("$('#ajaxForSessions').submit();");
    }
}
