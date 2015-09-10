package teammates.test.pageobjects;

import static org.testng.AssertJUnit.fail;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import teammates.common.util.Const;
import teammates.common.util.TimeHelper;

import com.google.appengine.api.datastore.Text;

public class InstructorFeedbacksPage extends AppPage {
    

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
    
    @FindBy(id = "instructions")
    private WebElement instructionsTextBox;
    
    @FindBy(id = "editUncommonSettingsButton")
    private WebElement uncommonSettingsButton;
    
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
    

    public InstructorFeedbacksPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("<h1>Add New Feedback Session</h1>");
    }
    
    public void selectSessionType(String visibleText){
        selectDropdownByVisibleValue(fsType, visibleText);
    }

    public AppPage sortByName() {
        sortByNameIcon.click();
        waitForPageToLoad();
        return this;
    }
    
    public AppPage sortById() {
        sortByIdIcon.click();
        waitForPageToLoad();
        return this;
    }

    public void clickSubmitButton(){
        submitButton.click();
        waitForPageToLoad();
    }
    
    public void clickEditUncommonSettingsButton(){
        uncommonSettingsButton.click();
    }
    
    public void clickCustomVisibleTimeButton(){
        customSessionVisibleTimeButton.click();
    }

    public void clickCustomPublishTimeButton(){
        customResultsVisibleTimeButton.click();
    }
    
    public void clickNeverVisibleTimeButton(){
        neverSessionVisibleTimeButton.click();
    }
    
    public void clickNeverPublishTimeButton(){
        neverResultsVisibleTimeButton.click();
    }
    
    public void clickManualPublishTimeButton(){
        manualResultsVisibleTimeButton.click();
    }
    
    public void clickDefaultVisibleTimeButton(){
        defaultSessionVisibleTimeButton.click();
    }
    
    public void clickDefaultPublishTimeButton(){
        defaultResultsVisibleTimeButton.click();
    }
    
    public void clickCopyButton(){
        copyButton.click();
    }
    
    public void clickCopySubmitButton(){
        copySubmitButton.click();
        waitForPageToLoad();
    }
    
    
    public void clickViewResponseLink(String courseId, String sessionName) {
        getViewResponseLink(courseId,sessionName).click();
        waitForPageToLoad();
    }
    
    public void toggleSendOpenEmailCheckbox() {
        sendOpenEmailCheckbox.click();
    }
    
    public void toggleSendClosingEmailCheckbox() {
        sendClosingEmailCheckbox.click();
    }
    
    public void toggleSendPublishedEmailCheckbox() {
        sendPublishedEmailCheckbox.click();
    }
    
    public void addFeedbackSessionWithTimeZone(
            String feedbackSessionName,
            String courseId,
            Date startTime,
            Date endTime,
            Date visibleTime,
            Date publishTime,
            Text instructions,
            int gracePeriod,
            double timeZone) {
        
        fillTextBox(fsNameTextBox, feedbackSessionName);
        
        String timeZoneString = "" + timeZone;

        double fractionalPart = timeZone % 1;
        
        if (fractionalPart == 0.0){
            timeZoneString = "" + (int) timeZone;
        }
        
        selectDropdownByActualValue(timezoneDropdown, timeZoneString);
        
        selectDropdownByVisibleValue(courseIdDropdown, courseId);
        
        // fill in time values        
        fillStartTime(startTime);
        fillEndTime(endTime);
        fillVisibleTime(visibleTime);
        fillPublishTime(publishTime);
        
        // Fill in instructions
        if (instructions != null) {
            fillTextBox(instructionsTextBox, instructions.getValue());
        }
    
        // Select grace period
        if (gracePeriod != -1) {
            selectDropdownByVisibleValue(gracePeriodDropdown, Integer.toString(gracePeriod)+ " mins");
        }
    
        clickSubmitButton();
    }
    
    public void copyFeedbackSession(String feedbackSessionName, String courseId) {        
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
        row.click();
    }
    
    public void clickCopyTableRadioButtonAtRow(int rowIndex) {
        WebElement button = browser.driver.findElement(By.id("copyTableModal"))
                                       .findElements(By.tagName("tr"))
                                       .get(rowIndex + 1).findElement(By.tagName("input"));
        button.click();
    }
    
    public void fillStartTime (Date startTime) {
        JavascriptExecutor js = (JavascriptExecutor) browser.driver;
        fillTimeValueIfNotNull(Const.ParamsNames.FEEDBACK_SESSION_STARTDATE, startTime, startTimeDropdown, js);
    }
    
    public void fillEndTime (Date endTime) {
        JavascriptExecutor js = (JavascriptExecutor) browser.driver;
        fillTimeValueIfNotNull(Const.ParamsNames.FEEDBACK_SESSION_ENDDATE, endTime, endTimeDropdown, js);
    }
    
    public void fillVisibleTime (Date visibleTime) {
        JavascriptExecutor js = (JavascriptExecutor) browser.driver;
        fillTimeValueIfNotNull(Const.ParamsNames.FEEDBACK_SESSION_VISIBLEDATE, visibleTime, visibleTimeDropdown, js);
    }
    
    public void fillPublishTime (Date publishTime) {
        JavascriptExecutor js = (JavascriptExecutor) browser.driver;
        fillTimeValueIfNotNull(Const.ParamsNames.FEEDBACK_SESSION_PUBLISHDATE, publishTime, publishTimeDropdown, js);
    }
    
    public void fillTimeValueIfNotNull(String dateId, Date datetimeValue, WebElement timeDropdown, JavascriptExecutor js) {
        if (datetimeValue != null) {
            js.executeScript("$('#" + dateId + "').val('" + TimeHelper.formatDate(datetimeValue) + "');");
            
            String timeDropdownId = timeDropdown.getAttribute("id");
            String timeDropdownVal = TimeHelper.convertToOptionValueInTimeDropDown(datetimeValue);
            js.executeScript("$('#" + timeDropdownId + "').val(" + timeDropdownVal + ")");
        }
    }
    
    /** 
     * This method contains an intended mix of Selenium and JavaScript to ensure that the test
     * passes consistently, do not try to click on the datepicker element using Selenium as it will
     * result in a test that passes or fail randomly.
    */
    public void fillTimeValueForDatePickerTest (String timeId, Calendar newValue) throws ParseException {
        WebElement dateInputElement = browser.driver.findElement(By.id(timeId));
        JavascriptExecutor js = (JavascriptExecutor) browser.driver;

        dateInputElement.click();

        dateInputElement.clear();
        dateInputElement.sendKeys(newValue.get(Calendar.DATE) + "/" + (newValue.get(Calendar.MONTH) + 1)
                                  + "/" + newValue.get(Calendar.YEAR));

        js.executeScript("$('.ui-datepicker-current-day').click();");
    }
    
    public String getValueOfDate (String timeId) {
        JavascriptExecutor js = (JavascriptExecutor) browser.driver;
        return (String) js.executeScript("return $('#" + timeId + "').datepicker('getDate') == null ? "
                                         + "null : "
                                         + "$('#" + timeId + "').datepicker('getDate').toDateString();");
    }
    
    public String getMinDateOf (String timeId) {
        JavascriptExecutor js = (JavascriptExecutor) browser.driver;
        return (String) js.executeScript("return $('#" + timeId + "').datepicker('option', 'minDate') == null ? "
                                         + "null : "
                                         + "$('#" + timeId + "').datepicker('option', 'minDate').toDateString();");
    }
    
    public String getMaxDateOf (String timeId) {
        JavascriptExecutor js = (JavascriptExecutor) browser.driver;
        return (String) js.executeScript("return $('#" + timeId + "').datepicker('option', 'maxDate') == null ? "
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
        JavascriptExecutor js = (JavascriptExecutor) browser.driver;
        return (String) js.executeScript("return (-(new Date()).getTimezoneOffset() / 60).toString()");
    }
    
    public void addFeedbackSession(String feedbackSessionName, String courseId, Date startTime,
            Date endTime, Date visibleTime, Date publishTime, Text instructions, int gracePeriod) {
        
        addFeedbackSessionWithTimeZone(feedbackSessionName, courseId, startTime, endTime,
                visibleTime, publishTime, instructions, gracePeriod, 8.0);
    }

    public WebElement getViewResponseLink(String courseId, String sessionName) {
        int sessionRowId = getFeedbackSessionRowId(courseId, sessionName);
        return browser.driver.findElement(
                By.xpath("//tbody/tr[" + (int) (sessionRowId + 1)
                + "]/td[contains(@class,'session-response-for-test')]/a"));
    }
    
    public String getResponseValue(String courseId, String sessionName) {
        int sessionRowId = getFeedbackSessionRowId(courseId, sessionName);
        return browser.driver.findElement(
                By.xpath("//tbody/tr[" + (int) (sessionRowId + 1)
                + "]/td[contains(@class,'session-response-for-test')]")).getText();
    }
    
    public void verifyResponseValue(String responseRate, String courseId, String sessionName){
        int sessionRowId = getFeedbackSessionRowId(courseId, sessionName);
        WebDriverWait wait = new WebDriverWait(browser.driver, 10);
        try {
            wait.until(ExpectedConditions.textToBePresentInElement(
                    browser.driver.findElement(
                            By.xpath("//tbody/tr[" + (int) (sessionRowId + 1)
                            + "]/td[contains(@class,'session-response-for-test')]")),
                            responseRate));
        } catch (TimeoutException e){
            fail("Not expected message");
        }
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
            Assert.fail("This element should be hidden.");
        } catch (NoSuchElementException e) {
            return;
        }
    }
    
    public void verifyUnpublishLinkHidden(String courseId, String sessionName) {
        int sessionRowId = getFeedbackSessionRowId(courseId, sessionName);
        try {
            getLinkAtTableRow("session-unpublish-for-test", sessionRowId);
            Assert.fail("This element should be hidden.");
        } catch (NoSuchElementException e) {
            return;
        }
    }
    
    public boolean verifyHidden (By locator) {
        return !browser.driver.findElement(locator).isDisplayed();
    }
    
    public boolean verifyEnabled (By locator) {
        return browser.driver.findElement(locator).isEnabled();
    }
    
    public boolean verifyDisabled (By locator) {
        return !browser.driver.findElement(locator).isEnabled();
    }
    
    public boolean verifyVisible (By locator) {
        return browser.driver.findElement(locator).isDisplayed();
    }
    
    public boolean isContainingCssClass(By locator, String className) {
        return browser.driver.findElement(locator).getAttribute("class").matches(".*\\b" + className + "\\b.*");
    }
    
    
    public InstructorFeedbackResultsPage loadViewResultsLink (String courseId, String fsName) {
        int sessionRowId = getFeedbackSessionRowId(courseId, fsName);
        String className = "session-view-for-test";
        return goToLinkInRow(
                By.xpath("//tbody/tr[" + (int) (sessionRowId + 1)
                + "]//a[contains(@class,'" + className + "')]"),
                InstructorFeedbackResultsPage.class);
    }
    
    public FeedbackSubmitPage loadSubmitLink (String courseId, String fsName) {
        int sessionRowId = getFeedbackSessionRowId(courseId, fsName);
        String className = "session-submit-for-test";
        return goToLinkInRow(
                By.xpath("//tbody/tr[" + (int) (sessionRowId + 1)
                + "]//a[contains(@class,'" + className + "')]"),
                FeedbackSubmitPage.class);
    }
    
    public InstructorFeedbackEditPage loadEditLink (String courseId, String fsName) {
        int sessionRowId = getFeedbackSessionRowId(courseId, fsName);
        String className = "session-edit-for-test";
        return goToLinkInRow(
                By.xpath("//tbody/tr[" + (int) (sessionRowId + 1)
                + "]//a[contains(@class,'" + className + "')]"),
                InstructorFeedbackEditPage.class);
    }
    
    public String getPageUrl() {
        return browser.driver.getCurrentUrl();
    }
    
    private WebElement getLinkAtTableRow(String className, int rowIndex) {
        return browser.driver.findElement(
                By.xpath("//table[contains(@id,'table-sessions')]//tbody/tr["
                + (int) (rowIndex + 1)+ "]//a[contains(@class,'" + className + "')]"));
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
    
    @SuppressWarnings("deprecation")
    private String getFeedbackSessionCourseId(int rowId) {
        return browser.selenium.getTable("css=table[id=table-sessions]." + (rowId + 1) + ".0");
    }

    @SuppressWarnings("deprecation")
    private String getFeedbackSessionName(int rowId) {
        return browser.selenium.getTable("css=table[id=table-sessions]." + (rowId + 1) + ".1");
    }

    private <T extends AppPage>T goToLinkInRow(By locator, Class<T> destinationPageType) {
        browser.driver.findElement(locator).click();
        waitForPageToLoad();
        return changePageType(destinationPageType);
    }
    
    public void clickFsCopyButton(String courseId, String feedbackSessionName) {
        By fsCopyButtonElement = By.id("button_fscopy" + "-" + courseId + "-" + feedbackSessionName);
        
        // give it some time to load as it is loaded via AJAX
        waitForElementPresence(fsCopyButtonElement);
        
        WebElement fsCopyButton = browser.driver.findElement(fsCopyButtonElement);
        
        fsCopyButton.click();
    }
    
    public void waitForModalToLoad() {
        waitForElementPresence(By.id(Const.ParamsNames.COPIED_FEEDBACK_SESSION_NAME));
    }
    
    public void clickFsCopySubmitButton() {
        WebElement fsCopySubmitButton = browser.driver.findElement(By.id("fscopy_submit"));
        
        fsCopySubmitButton.click();
    }
    
    public void fillCopyToOtherCoursesForm(String newName) {
        WebElement fsCopyModal = browser.driver.findElement(By.id("fsCopyModal"));
        List<WebElement> coursesCheckBoxes =
                fsCopyModal.findElements(By.name(Const.ParamsNames.COPIED_COURSES_ID));
        
        for (WebElement e : coursesCheckBoxes) {
            markCheckBoxAsChecked(e);
        }
        
        WebElement fsNameInput =
                fsCopyModal.findElement(By.id(Const.ParamsNames.COPIED_FEEDBACK_SESSION_NAME));
        
        fillTextBox(fsNameInput, newName);
    }
}
