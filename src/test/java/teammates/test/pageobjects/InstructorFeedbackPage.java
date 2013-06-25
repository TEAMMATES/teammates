package teammates.test.pageobjects;

import java.util.Date;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.google.appengine.api.datastore.Text;

import teammates.common.Common;

public class InstructorFeedbackPage extends AppPage {
	
	@FindBy(id = "courseid")
	private WebElement courseIdDropdown;
	
	@FindBy(id = "fsname")
	private WebElement fsNameTextBox;
	
	@FindBy(id = "starttime")
	private WebElement startTimeDropdown;
	
	@FindBy(id = "endtime")
	private WebElement endTimeDropdown;
	
	@FindBy (id = "visibletime")
	private WebElement visibleTimeDropDown;
	
	@FindBy (id = "publishtime")
	private WebElement publishTimeDropDown;
	
	@FindBy (id = "timezone")
	private WebElement timezoneDropDown;
	
	@FindBy(id = "graceperiod")
	private WebElement gracePeriodDropdown;
	
	@FindBy(id = "instructions")
	private WebElement instructionsTextBox;
	
	@FindBy(id = Common.PARAM_FEEDBACK_SESSION_SESSIONVISIBLEBUTTON + "_custom")
	private WebElement customSessionVisibleTimeButton;
	
	@FindBy(id = Common.PARAM_FEEDBACK_SESSION_RESULTSVISIBLEBUTTON + "_custom")
	private WebElement customResultsVisibleTimeButton;
	
	@FindBy(id = Common.PARAM_FEEDBACK_SESSION_SESSIONVISIBLEBUTTON + "_atopen")
	private WebElement defaultSessionVisibleTimeButton;
	
	@FindBy(id = Common.PARAM_FEEDBACK_SESSION_RESULTSVISIBLEBUTTON + "_atvisible")
	private WebElement defaultResultsVisibleTimeButton;
	
	@FindBy(id = "button_submit")
	private WebElement submitButton;
	
	@FindBy(id = "button_sortname")
	private WebElement sortByNameIcon;
	
	@FindBy(id = "button_sortcourseid")
	private WebElement sortByIdIcon;
	

	public InstructorFeedbackPage(Browser browser) {
		super(browser);
	}

	@Override
	protected boolean containsExpectedPageContents() {
		return getPageSource().contains("<h1>Add New Feedback Session</h1>");
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
	
	public void clickDefaultVisibleTimeButton(){
		defaultSessionVisibleTimeButton.click();
	}
	
	public void clickDefaultPublishTimeButton(){
		defaultResultsVisibleTimeButton.click();
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
	
		selectDropdownByVisibleValue(courseIdDropdown, courseId);
		
		// Select start date
		JavascriptExecutor js = (JavascriptExecutor) browser.driver;
		js.executeScript("$('#" + Common.PARAM_FEEDBACK_SESSION_STARTDATE
				+ "')[0].value='" + Common.formatDate(startTime) + "';");
		selectDropdownByVisibleValue(startTimeDropdown,
				Common.convertToDisplayValueInTimeDropDown(startTime));
	
		// Select deadline date
		js.executeScript("$('#" + Common.PARAM_FEEDBACK_SESSION_ENDDATE
				+ "')[0].value='" + Common.formatDate(endTime) + "';");
		selectDropdownByVisibleValue(endTimeDropdown,
				Common.convertToDisplayValueInTimeDropDown(endTime));
		

		
		// Fill in instructions
		fillTextBox(instructionsTextBox, instructions.getValue());
	
		// Select grace period
		selectDropdownByVisibleValue(gracePeriodDropdown, Integer.toString(gracePeriod)+ " mins");		
	
		clickSubmitButton();
	}

	public WebElement getDeleteLink(String courseId, String evalName) {
		int evalRowId = getFeedbackSessionRowId(courseId, evalName);
		return browser.driver.findElement(By.className("t_fs_delete" + evalRowId));
	}
	
	public WebElement getEditLink(String courseId, String evalName) {
		int evalRowId = getFeedbackSessionRowId(courseId, evalName);
		return browser.driver.findElement(By.className("t_fs_edit" + evalRowId));
	}
	
	private int getFeedbackSessionRowId(String courseId, String evalName) {
		int i = 0;
		while (i < getFeedbackSessionsCount()) {
			if (getFeedbackSessionCourseId(i).equals(courseId)
					&& getFeedbackSessionName(i).equals(evalName)) {
				return i;
			}
			i++;
		}
		return -1;
	}
	
	private int getFeedbackSessionsCount() {
		return browser.driver.findElements(By.className("evaluations_row")).size();
	}
	
	private String getFeedbackSessionCourseId(int rowId) {
		return browser.selenium.getTable("class=dataTable." + (rowId + 1) + ".0");
	}

	private String getFeedbackSessionName(int rowId) {
		return browser.selenium.getTable("class=dataTable." + (rowId + 1) + ".1");
	}

	

}
