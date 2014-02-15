package teammates.test.pageobjects;

import java.util.Date;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;

import com.google.appengine.api.datastore.Text;

import teammates.common.util.Const;
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
	private WebElement visibleTimeDropDown;
	
	@FindBy (id = "publishtime")
	private WebElement publishTimeDropDown;
	
	@FindBy (id = "timezone")
	private WebElement timezoneDropDown;
	
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
	
	public void toggleSendOpenEmailCheckbox() {
		sendOpenEmailCheckbox.click();
	}
	
	public void toggleSendClosingEmailCheckbox() {
		sendClosingEmailCheckbox.click();
	}
	
	public void toggleSendPublishedEmailCheckbox() {
		sendPublishedEmailCheckbox.click();
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
		
		// Select start date/time
		JavascriptExecutor js = (JavascriptExecutor) browser.driver;
		if (startTime != null) {
			js.executeScript("$('#" + Const.ParamsNames.FEEDBACK_SESSION_STARTDATE
					+ "')[0].value='" + TimeHelper.formatDate(startTime) + "';");
			selectDropdownByVisibleValue(startTimeDropdown,
					TimeHelper.convertToDisplayValueInTimeDropDown(startTime));
		}
	
		// Select deadline date/time
		if (endTime != null) {
			js.executeScript("$('#" + Const.ParamsNames.FEEDBACK_SESSION_ENDDATE
					+ "')[0].value='" + TimeHelper.formatDate(endTime) + "';");
			selectDropdownByVisibleValue(endTimeDropdown,
					TimeHelper.convertToDisplayValueInTimeDropDown(endTime));
		}
		
		// Select custom visible date/time
		if (visibleTime != null) {
			js.executeScript("$('#" + Const.ParamsNames.FEEDBACK_SESSION_VISIBLEDATE
					+ "')[0].value='" + TimeHelper.formatDate(visibleTime) + "';");
			selectDropdownByVisibleValue(visibleTimeDropDown,
					TimeHelper.convertToDisplayValueInTimeDropDown(visibleTime));
		}
	
		// Select custom publish date/time
		if (publishTime != null) {
			js.executeScript("$('#" + Const.ParamsNames.FEEDBACK_SESSION_PUBLISHDATE
					+ "')[0].value='" + TimeHelper.formatDate(publishTime) + "';");
			selectDropdownByVisibleValue(publishTimeDropDown,
				TimeHelper.convertToDisplayValueInTimeDropDown(publishTime));
		}	
		
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

	public WebElement getViewResponseLink(String courseId, String sessionName) {
		int sessionRowId = getFeedbackSessionRowId(courseId, sessionName);
		return browser.driver.findElement(By.xpath("//tbody/tr["+(int)(sessionRowId+2)+"]/td[contains(@class,'t_session_response')]/a"));
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

	

}
