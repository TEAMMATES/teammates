package teammates.test.pageobjects;

import java.util.Date;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import teammates.common.util.Config;
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
	
	@FindBy(id = "instr")
	private WebElement instructionsTextBox;
	
	@FindBy(id = "button_submit")
	private WebElement submitButton;
	
	@FindBy(id = "button_sortname")
	private WebElement sortByNameIcon;
	
	@FindBy(id = "button_sortcourseid")
	private WebElement sortByIdIcon;
	
	
	

	public InstructorEvalsPage(Browser browser) {
		super(browser);
	}

	@Override
	protected boolean containsExpectedPageContents() {
		return getPageSource().contains("<h1>Add New Evaluation</h1>");
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
			int gracePeriod) {
		
		fillTextBox(evalNameTextBox, evalName);
	
		selectDropdownByVisibleValue(courseIdDropdown, courseId);
		
		// Select start date
		JavascriptExecutor js = (JavascriptExecutor) browser.driver;
		js.executeScript("$('#" + Config.PARAM_EVALUATION_START
				+ "')[0].value='" + TimeHelper.formatDate(startTime) + "';");
		selectDropdownByVisibleValue(startTimeDropdown,
				TimeHelper.convertToDisplayValueInTimeDropDown(startTime));
	
		// Select deadline date
		js.executeScript("$('#" + Config.PARAM_EVALUATION_DEADLINE
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
		
	
		clickSubmitButton();
	}

	public WebElement getPublishLink(String courseId, String evalName) {
		int evalRowId = getEvaluationRowId(courseId, evalName);
		return browser.driver.findElement(By.className("t_eval_publish" + evalRowId));
	}
	
	public WebElement getUnpublishLink(String courseId, String evalName) {
		int evalRowId = getEvaluationRowId(courseId, evalName);
		return browser.driver.findElement(By.className("t_eval_unpublish" + evalRowId));
	}

	public WebElement getRemindLink(String courseId, String evalName) {
		int evalRowId = getEvaluationRowId(courseId, evalName);
		return browser.driver.findElement(By.className("t_eval_remind" + evalRowId));
	}
	
	public WebElement getDeleteLink(String courseId, String evalName) {
		int evalRowId = getEvaluationRowId(courseId, evalName);
		return browser.driver.findElement(By.className("t_eval_delete" + evalRowId));
	}
	
	public WebElement getEditLink(String courseId, String evalName) {
		int evalRowId = getEvaluationRowId(courseId, evalName);
		return browser.driver.findElement(By.className("t_eval_edit" + evalRowId));
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
	
	private int getEvaluationsCount() {
		return browser.driver.findElements(By.className("evaluations_row")).size();
	}
	
	private String getEvaluationCourseId(int rowId) {
		return browser.selenium.getTable("class=dataTable." + (rowId + 1) + ".0");
	}

	private String getEvaluationName(int rowId) {
		return browser.selenium.getTable("class=dataTable." + (rowId + 1) + ".1");
	}

	

}
