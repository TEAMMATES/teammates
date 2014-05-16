package teammates.test.pageobjects;

import java.util.Date;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;

import com.google.appengine.api.datastore.Text;

import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.common.util.TimeHelper;

public class InstructorFeedbacksPage extends AppPage {
    
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
        
    @FindBy(id = "button_sortname")
    private WebElement sortByNameIcon;
    
    @FindBy(id = "button_sortcourseid")
    private WebElement sortByIdIcon;
    

    public InstructorFeedbacksPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("<h1>Add New Feedback Session</h1>");
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
        
        selectDropdownByVisibleValue(timezoneDropdown, StringHelper.toUtcFormat(timeZone));
        
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
    
    public void selectDateInDatePicker (String day) {
        WebElement dateWidget = browser.driver.findElement(By.id("ui-datepicker-div"));  
        List<WebElement> rows=dateWidget.findElements(By.tagName("tr"));  
        List<WebElement> columns=dateWidget.findElements(By.tagName("td"));  
          
        for (WebElement cell: columns){
             if (cell.getText().equals(day)) {  
             cell.findElement(By.linkText(day)).click();  
             break;  
             }  
        }  
    }
    
    public String getValueOfStartDate() {
        return browser.driver.findElement(By.id("startdate")).getAttribute("value");
    }
    
    public String getValueOfEndDate() {
        return browser.driver.findElement(By.id("enddate")).getAttribute("value");
    }
    
    public String getValueOfVisibleDate() {
        return browser.driver.findElement(By.id("visibledate")).getAttribute("value");
    }
    
    public String getValueOfPublishDate() {
        return browser.driver.findElement(By.id("publishdate")).getAttribute("value");
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
        return browser.driver.findElement(By.xpath("//tbody/tr["+(int)(sessionRowId+2)+"]/td[contains(@class,'t_session_response')]/a"));
    }
    
    public String getResponseValue(String courseId, String sessionName) {
        int sessionRowId = getFeedbackSessionRowId(courseId, sessionName);
        return browser.driver.findElement(By.xpath("//tbody/tr["+(int)(sessionRowId+2)+"]/td[contains(@class,'t_session_response')]")).getText();
    }
    
    public WebElement getViewResultsLink(String courseId, String sessionName) {
        int sessionRowId = getFeedbackSessionRowId(courseId, sessionName);
        return getLinkAtTableRow("t_session_view", sessionRowId);
    }
    
    public WebElement getEditLink(String courseId, String sessionName) {
        int sessionRowId = getFeedbackSessionRowId(courseId, sessionName);
        return getLinkAtTableRow("t_session_edit", sessionRowId);
    }
    
    public WebElement getDeleteLink(String courseId, String sessionName) {
        int sessionRowId = getFeedbackSessionRowId(courseId, sessionName);
        return getLinkAtTableRow("t_session_delete", sessionRowId);
    }
    
    public WebElement getSubmitLink(String courseId, String sessionName) {
        int sessionRowId = getFeedbackSessionRowId(courseId, sessionName);
        return getLinkAtTableRow("t_session_submit", sessionRowId);
    }
    
    public WebElement getPublishLink(String courseId, String sessionName) {
        int sessionRowId = getFeedbackSessionRowId(courseId, sessionName);
        return getLinkAtTableRow("t_session_publish", sessionRowId);
    }
    
    public WebElement getUnpublishLink(String courseId, String sessionName) {
        int sessionRowId = getFeedbackSessionRowId(courseId, sessionName);
        return getLinkAtTableRow("t_session_unpublish", sessionRowId);
    }
    
    public void verifyPublishLinkHidden(String courseId, String sessionName) {
        int sessionRowId = getFeedbackSessionRowId(courseId, sessionName);
        try {
            getLinkAtTableRow("t_session_publish", sessionRowId);
            Assert.fail("This element should be hidden.");
        } catch (NoSuchElementException e) {
            return;
        }
    }
    
    public void verifyUnpublishLinkHidden(String courseId, String sessionName) {
        int sessionRowId = getFeedbackSessionRowId(courseId, sessionName);
        try {
            getLinkAtTableRow("t_session_unpublish", sessionRowId);
            Assert.fail("This element should be hidden.");
        } catch (NoSuchElementException e) {
            return;
        }
    }
    
    public boolean verifyHidden (By locator) {
        return !browser.driver.findElement(locator).isDisplayed();
    }
    
    public boolean verifyInstructionsTextAreaIsHidden () {
        return !browser.driver.findElement(By.xpath("(//table[@id='instructionsTable']//tr)[1]")).isDisplayed();        
    }
    
    public InstructorFeedbackResultsPage loadViewResultsLink (String courseId, String fsName) {
        int sessionRowId = getFeedbackSessionRowId(courseId, fsName);
        String className = "t_session_view";
        return goToLinkInRow(By.xpath("//tbody/tr["+(int)(sessionRowId+2)+"]//a[contains(@class,'"+className+"')]")
                            ,InstructorFeedbackResultsPage.class);
    }
    
    public FeedbackSubmitPage loadSubmitLink (String courseId, String fsName) {
        int sessionRowId = getFeedbackSessionRowId(courseId, fsName);
        String className = "t_session_submit";
        return goToLinkInRow(By.xpath("//tbody/tr["+(int)(sessionRowId+2)+"]//a[contains(@class,'"+className+"')]")
                            ,FeedbackSubmitPage.class);
    }
    
    public InstructorFeedbackEditPage loadEditLink (String courseId, String fsName) {
        int sessionRowId = getFeedbackSessionRowId(courseId, fsName);
        String className = "t_session_edit";
        return goToLinkInRow(By.xpath("//tbody/tr["+(int)(sessionRowId+2)+"]//a[contains(@class,'"+className+"')]")
                            ,InstructorFeedbackEditPage.class);
    }
    
    public String getPageUrl() {
        return browser.driver.getCurrentUrl();
    }
    
    private WebElement getLinkAtTableRow(String className, int rowIndex) {
        return browser.driver.findElement(By.xpath("//tbody/tr["+(int)(rowIndex+2)+"]//a[contains(@class,'"+className+"')]"));
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
        return browser.driver.findElements(By.className("sessions_row")).size();
    }
    
    private String getFeedbackSessionCourseId(int rowId) {
        return browser.selenium.getTable("class=dataTable." + (rowId + 1) + ".0");
    }

    private String getFeedbackSessionName(int rowId) {
        return browser.selenium.getTable("class=dataTable." + (rowId + 1) + ".1");
    }

    private <T extends AppPage>T goToLinkInRow(By locator, Class<T> destinationPageType) {
        browser.driver.findElement(locator).click();
        waitForPageToLoad();
        return changePageType(destinationPageType);
    }
    

}
