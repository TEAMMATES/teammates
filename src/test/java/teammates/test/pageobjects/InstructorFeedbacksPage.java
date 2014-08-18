package teammates.test.pageobjects;

import static org.testng.AssertJUnit.fail;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

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
    
    public AppPage sortByDeadline() {
        sortByNameIcon.click();
        waitForPageToLoad();
        return this;
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

    public void fillSessionName(String name) {
        fillTextBox(fsNameTextBox, name);
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
        browser.selenium.waitForPageToLoad("15000");
    }
    
    
    public void clickViewResponseLink(String courseId, String sessionName) {
        getViewResponseLink(courseId,sessionName).click();
        browser.selenium.waitForPageToLoad("15000");
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
        
        if(fractionalPart == 0.0){
            timeZoneString = "" + (int)timeZone;
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
        
        this.waitForElementVisible(copiedFsNameTextBox);
        
        fillTextBox(copiedFsNameTextBox, feedbackSessionName);
        
        selectDropdownByVisibleValue(copiedCourseIdDropdown, courseId);
        
        clickCopyTableAtRow(0);
        
        clickCopySubmitButton();
    }
    
    public void clickCopyTableAtRow(int rowIndex) {
        WebElement row = browser.driver.findElement(By.id("copyTableModal")).findElements(By.tagName("tr")).get(rowIndex + 1);
        row.click();
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
    
    public void fillTimeValueIfNotNull(String timeId, Date timeValue, WebElement timeDropdown, JavascriptExecutor js) {
        if (timeValue != null) {
            js.executeScript("$('#" + timeId
                    + "')[0].value='" + TimeHelper.formatDate(timeValue) + "';");
            selectDropdownByVisibleValue(timeDropdown,
                    TimeHelper.convertToDisplayValueInTimeDropDown(timeValue));
        }
    }
    
    public void fillTimeValueForDatePickerTest (String timeId, Calendar newValue) throws ParseException {
        
        browser.driver.findElement(By.id(timeId)).click();
        browser.driver.manage().timeouts().implicitlyWait(200, TimeUnit.MILLISECONDS); 
        
        Calendar currentValue = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd yyyy");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));        
        
        String currentDateString = getValueOfDate(timeId);
        int numberOfMonthsToMove = 0;
        
        if (currentDateString != null) {
            currentValue.setTime(sdf.parse(currentDateString));
        } else {
            currentValue.setTime(new Date());
        }
        
        numberOfMonthsToMove = 12*(newValue.get(Calendar.YEAR) - currentValue.get(Calendar.YEAR));
        numberOfMonthsToMove += newValue.get(Calendar.MONTH) - currentValue.get(Calendar.MONTH);
        
        changeMonthInDatePickerBy(numberOfMonthsToMove);
        selectDayInDatePicker(newValue.get(Calendar.DATE));
        
    }
    
    public void changeMonthInDatePickerBy(int numberOfMonths) {
        if (numberOfMonths > 0) {
            for (int i = 0 ; i < numberOfMonths ; i ++) {
                browser.driver.findElement(By.id("ui-datepicker-div")).findElement(By.className("ui-datepicker-next")).click();
            }
        } else {
            for (int i = 0 ; i > numberOfMonths ; i --) {
                browser.driver.findElement(By.id("ui-datepicker-div")).findElement(By.className("ui-datepicker-prev")).click();
            }
        }
    }
    
    public void selectDayInDatePicker (int day) {
        WebElement dateWidget = browser.driver.findElement(By.id("ui-datepicker-div"));    
        List<WebElement> columns = dateWidget.findElements(By.tagName("td"));  
          
        for (WebElement cell: columns){
             if (cell.getText().equals(day+"")) {  
             cell.click();  
             break;  
             }  
        }  
    }
    
    public String getValueOfDate (String timeId) {
        JavascriptExecutor js = (JavascriptExecutor) browser.driver;
        return (String) js.executeScript("return $('#"+timeId+"').datepicker('getDate') == null ? "
                +"null : $('#"+timeId+"').datepicker('getDate').toDateString();");
    }
    
    public String getMinDateOf (String timeId) {
        JavascriptExecutor js = (JavascriptExecutor) browser.driver;
        return (String) js.executeScript("return $('#"+timeId+"').datepicker('option', 'minDate') == null ? "
                +"null : $('#"+timeId+"').datepicker('option', 'minDate').toDateString();");
    }
    
    public String getMaxDateOf (String timeId) {
        JavascriptExecutor js = (JavascriptExecutor) browser.driver;
        return (String) js.executeScript("return $('#"+timeId+"').datepicker('option', 'maxDate') == null ? "
                +"null : $('#"+timeId+"').datepicker('option', 'maxDate').toDateString();");
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
        
        addFeedbackSessionWithTimeZone(feedbackSessionName, courseId, 
                startTime, endTime, 
                visibleTime, publishTime, 
                instructions, gracePeriod, 
                8.0);
    }

    public WebElement getViewResponseLink(String courseId, String sessionName) {
        int sessionRowId = getFeedbackSessionRowId(courseId, sessionName);
        return browser.driver.findElement(By.xpath("//tbody/tr["+(int)(sessionRowId+1)+"]/td[contains(@class,'session-response-for-test')]/a"));
    }
    
    public String getResponseValue(String courseId, String sessionName) {
        int sessionRowId = getFeedbackSessionRowId(courseId, sessionName);
        return browser.driver.findElement(By.xpath("//tbody/tr["+(int)(sessionRowId+1)+"]/td[contains(@class,'session-response-for-test')]")).getText();
    }
    
    public void verifyResponseValue(String responseRate, String courseId, String sessionName){
        int sessionRowId = getFeedbackSessionRowId(courseId, sessionName);
        WebDriverWait wait = new WebDriverWait(browser.driver, 10);
        try {
            wait.until(ExpectedConditions.textToBePresentInElement(browser.driver.findElement(By.xpath("//tbody/tr["+(int)(sessionRowId+1)+"]/td[contains(@class,'session-response-for-test')]")), responseRate));
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
    
    public InstructorFeedbackResultsPage loadViewResultsLink (String courseId, String fsName) {
        int sessionRowId = getFeedbackSessionRowId(courseId, fsName);
        String className = "session-view-for-test";
        return goToLinkInRow(By.xpath("//tbody/tr["+(int)(sessionRowId+1)+"]//a[contains(@class,'"+className+"')]")
                            ,InstructorFeedbackResultsPage.class);
    }
    
    public FeedbackSubmitPage loadSubmitLink (String courseId, String fsName) {
        int sessionRowId = getFeedbackSessionRowId(courseId, fsName);
        String className = "session-submit-for-test";
        return goToLinkInRow(By.xpath("//tbody/tr["+(int)(sessionRowId+1)+"]//a[contains(@class,'"+className+"')]")
                            ,FeedbackSubmitPage.class);
    }
    
    public InstructorFeedbackEditPage loadEditLink (String courseId, String fsName) {
        int sessionRowId = getFeedbackSessionRowId(courseId, fsName);
        String className = "session-edit-for-test";
        return goToLinkInRow(By.xpath("//tbody/tr["+(int)(sessionRowId+1)+"]//a[contains(@class,'"+className+"')]")
                            ,InstructorFeedbackEditPage.class);
    }
    
    public String getPageUrl() {
        return browser.driver.getCurrentUrl();
    }
    
    private WebElement getLinkAtTableRow(String className, int rowIndex) {
        return browser.driver.findElement(By.xpath("//tbody/tr["+(int)(rowIndex+1)+"]//a[contains(@class,'"+className+"')]"));
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
        return browser.selenium.getTable("css=table[class~=table]." + (rowId + 1) + ".0");
    }

    private String getFeedbackSessionName(int rowId) {
        return browser.selenium.getTable("css=table[class~=table]." + (rowId + 1) + ".1");
    }

    private <T extends AppPage>T goToLinkInRow(By locator, Class<T> destinationPageType) {
        browser.driver.findElement(locator).click();
        waitForPageToLoad();
        return changePageType(destinationPageType);
    }
    

}
