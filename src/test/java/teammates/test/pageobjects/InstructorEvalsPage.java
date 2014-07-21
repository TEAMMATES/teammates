package teammates.test.pageobjects;

import java.util.Date;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.common.util.TimeHelper;

public class InstructorEvalsPage extends AppPage {
    
    @FindBy(id = "courseid")
    private WebElement courseIdDropdown;
    
    @FindBy(id = "evaluationname")
    private WebElement evalNameTextBox;
    
    @FindBy(id = "starttime")
    private WebElement startTimeDropdown;
    
    @FindBy(id = "deadlinetime")
    private WebElement endTimeDropdown;
    
    @FindBy(id = "commentsstatus_enabled")
    private WebElement p2pEnabledOption;
    
    @FindBy(id = "commentsstatus_disabled")
    private WebElement p2pDisabledOption;
    
    @FindBy(id = "graceperiod")
    private WebElement gracePeriodDropdown;
    
    @FindBy(id = "timezone")
    private WebElement timeZoneDropdown;
    
    @FindBy(id = "instr")
    private WebElement instructionsTextBox;
    
    @FindBy(id = "button_submit")
    private WebElement submitButton;
    
    @FindBy(id = "button_sortname")
    private WebElement sortByNameIcon;
    
    @FindBy(id = "button_sortid")
    private WebElement sortByIdIcon;
    
    
    

    public InstructorEvalsPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("<h1>Add New Evaluation Session</h1>");
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

    public void fillEvalName(String name) {
        evalNameTextBox.clear();
        evalNameTextBox.sendKeys(name);
    }

    public void clickSubmitButton(){
        waitForPageToLoad();
        submitButton.click();
        waitForPageToLoad();
    }

    public void addEvaluation(
            String courseId, 
            String evalName, 
            Date startTime,
            Date endTime, 
            boolean p2pEnabled, 
            String instructions,
            int gracePeriod,
            double timeZone) {
        
        fillTextBox(evalNameTextBox, evalName);
    
        selectDropdownByVisibleValue(courseIdDropdown, courseId);
        
        // Select start date
        JavascriptExecutor js = (JavascriptExecutor) browser.driver;
        js.executeScript("$('#" + Const.ParamsNames.EVALUATION_START
                + "')[0].value='" + TimeHelper.formatDate(startTime) + "';");
        selectDropdownByVisibleValue(startTimeDropdown,
                TimeHelper.convertToDisplayValueInTimeDropDown(startTime));
    
        // Select deadline date
        js.executeScript("$('#" + Const.ParamsNames.EVALUATION_DEADLINE
                + "')[0].value='" + TimeHelper.formatDate(endTime) + "';");
        selectDropdownByVisibleValue(endTimeDropdown,
                TimeHelper.convertToDisplayValueInTimeDropDown(endTime));
    
        // Allow P2P comment
        if (p2pEnabled) {
            p2pEnabledOption.click();
        } else {
            p2pDisabledOption.click();
        }
    
        // Fill in instructions
        fillTextBox(instructionsTextBox, instructions);
    
        // Select grace period
        selectDropdownByVisibleValue(gracePeriodDropdown, Integer.toString(gracePeriod)+ " mins");
    
        // Select time zone
        selectDropdownByActualValue(timeZoneDropdown, "" + timeZone);

        clickSubmitButton();
    }
    
    public InstructorEvalResultsPage loadViewResultsLink(String courseId, String evalName) {
        getViewResultsLink(courseId, evalName).click();
        waitForPageToLoad();
        return changePageType(InstructorEvalResultsPage.class);
    }
    
    public StudentEvalEditPage loadPreviewLink(String courseId, String evalName) {
        getPreviewLink(courseId, evalName).click();
        waitForPageToLoad();
        switchToNewWindow();
        return changePageType(StudentEvalEditPage.class);
    }

    public WebElement getViewResponseLink(String courseId, String sessionName) {
        int sessionRowId = getEvaluationRowId(courseId, sessionName);
        return browser.driver.findElement(By.xpath("//tbody/tr["+(int)(sessionRowId+1)+"]/td[contains(@class,'session-response-for-test')]/a"));
    }
    
    public WebElement getPublishLink(String courseId, String evalName) {
        int evalRowId = getEvaluationRowId(courseId, evalName);
        return getLinkAtTableRow("session-publish-for-test", evalRowId);
    }
    
    public WebElement getUnpublishLink(String courseId, String evalName) {
        int evalRowId = getEvaluationRowId(courseId, evalName);
        return getLinkAtTableRow("session-unpublish-for-test", evalRowId);
    }

    public WebElement getRemindLink(String courseId, String evalName) {
        int evalRowId = getEvaluationRowId(courseId, evalName);
        return getLinkAtTableRow("session-remind-for-test", evalRowId);
    }
    
    public WebElement getDeleteLink(String courseId, String evalName) {
        int evalRowId = getEvaluationRowId(courseId, evalName);
        return getLinkAtTableRow("session-delete-for-test", evalRowId);
    }
    
    public WebElement getEditLink(String courseId, String evalName) {
        int evalRowId = getEvaluationRowId(courseId, evalName);
        return getLinkAtTableRow("session-edit-for-test", evalRowId);
    }
    
    public WebElement getViewResultsLink(String courseId, String evalName) {
        int evalRowId = getEvaluationRowId(courseId, evalName);
        return getLinkAtTableRow("session-view-for-test", evalRowId);
    }
    
    public WebElement getPreviewLink(String courseId, String evalName) {
        int evalRowId = getEvaluationRowId(courseId, evalName);
        return getLinkAtTableRow("session-preview-for-test", evalRowId);
    }
    
    private int getEvaluationRowId(String courseId, String evalName) {
        int i = 0;
        while (i < getEvaluationsCount()) {
            if (getEvaluationCourseId(i).equals(courseId)
                    && getEvaluationName(i).equals(evalName)) {
                return i;
            }
            i++;
        }
        return -1;
    }
    private WebElement getLinkAtTableRow(String className, int rowIndex) {
        return browser.driver.findElement(By.xpath("//tbody/tr["+(int)(rowIndex+1)+"]//a[contains(@class,'"+className+"')]"));
    }

    private int getEvaluationsCount() {
        return browser.driver.findElements(By.className("sessionsRow")).size();
    }
    
    private String getEvaluationCourseId(int rowId) {
        return browser.selenium.getTable("css=table[class~=table]." + (rowId + 1) + ".0");
    }

    private String getEvaluationName(int rowId) {
        return browser.selenium.getTable("css=table[class~=table]." + (rowId + 1) + ".1");
    }

    

}
