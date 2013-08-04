package teammates.test.pageobjects;

import java.util.Date;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.google.appengine.api.datastore.Text;

import teammates.common.util.Const;
import teammates.common.util.TimeHelper;

public class InstructorFeedbackEditPage extends AppPage {
	
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
	
	@FindBy(id = Const.ParamsNames.FEEDBACK_SESSION_SESSIONVISIBLEBUTTON + "_atopen")
	private WebElement defaultSessionVisibleTimeButton;
	
	@FindBy(id = Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON + "_atvisible")
	private WebElement defaultResultsVisibleTimeButton;
	
	@FindBy(id = Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON + "_later")
	private WebElement manualResultsVisibleTimeButton;
	
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
	
	@FindBy(id = "questiontext")
	private WebElement questionTextBox;
	
	@FindBy(id = "questionedittext-1")
	private WebElement qnEditLink;	
	
	@FindBy(id = "questionsavechangestext-1")
	private WebElement qnSaveLink;
	
	@FindBy(xpath = "//a[@onclick='deleteQuestion(1)']")
	private WebElement qnDeleteLink;
	
	@FindBy(id = "questiontext-1")
	private WebElement questionEditTextBox;
	
	@FindBy(id = "recipienttype")
	private WebElement recipientDropdown;
	
	@FindBy(id = "numofrecipients-")
	private WebElement numberOfRecipients;
	
	@FindBy(xpath = "//input[@name='numofrecipientstype' and @value='max']")
	private WebElement maxNumOfRecipients;
	
	@FindBy(xpath = "//input[@name='numofrecipientstype' and @value='custom']")
	private WebElement customNumOfRecipients;
	
	public InstructorFeedbackEditPage(Browser browser) {
		super(browser);
	}

	@Override
	protected boolean containsExpectedPageContents() {
		return getPageSource().contains("<h1>Edit Feedback Session</h1>");
	}
	
	public void fillInstructionsBox(String instructions){
		fillTextBox(instructionsTextBox, instructions);
	}
	
	public void fillQuestionBox(String qnText){
		fillTextBox(questionTextBox, qnText);
	}
	
	public void fillEditQuestionBox(String qnText){
		fillTextBox(questionEditTextBox, qnText);
	}
	
	public void fillNumOfEntitiesToGiveFeedbackToBox(String num){
		fillTextBox(numberOfRecipients, num);
	}
	
	public void clickMaxNumberOfRecipientsButton() {
		maxNumOfRecipients.click();
	}
	
	public void clickCustomNumberOfRecipientsButton() {
		customNumOfRecipients.click();
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
	
	public void clickManualPublishTimeButton(){
		manualResultsVisibleTimeButton.click();
	}
	
	public WebElement getDeleteSessionLink(){
		return fsDeleteLink;
	}
	
	public WebElement getDeleteQuestionLink(){
		return qnDeleteLink;
	}
	
	public boolean clickEditSessionButton(){
		fsEditLink.click();
		// Check if links toggle properly.
		return fsSaveLink.isDisplayed();
	}
	
	public void clickSaveSessionButton(){
		fsSaveLink.click();
		waitForPageToLoad();
	}
	
	public void clickAddQuestionButton(){
		addNewQuestionButton.click();
		waitForPageToLoad();
	}
	
	public boolean clickEditQuestionButton(){
		qnEditLink.click();
		// Check if links toggle properly.
		return qnSaveLink.isDisplayed();
	}
	
	public void clickSaveExistingQuestionButton(){
		qnSaveLink.click();
		waitForPageToLoad();
	}
	
	/**
	 * 
	 * @return {@code True} if the button was clicked successfully and an element in the new question
	 * frame is now visible. {@code False} if not.
	 */
	public boolean clickNewQuestionButton(){
		openNewQuestionButton.click();
		// Just check if an element in the new question frame is visible.
		return addNewQuestionButton.isDisplayed();
	}
	
	/**
	 * Empties the input box for the given {@code field}.
	 * @param field : the ID of the field to clear.
	 */
	public void clearField(String field){
		JavascriptExecutor js = (JavascriptExecutor) browser.driver;
		js.executeScript("$('#" + field
				+ "')[0].value='';");
	}
	
	public void selectRecipientsToBeStudents() {
		selectDropdownByVisibleValue(recipientDropdown, "Other students in the course");
	}
	
	public void editFeedbackSession(
			Date startTime,
			Date endTime,
			Text instructions,
			int gracePeriod) {
		
		// Select start date
		JavascriptExecutor js = (JavascriptExecutor) browser.driver;
		js.executeScript("$('#" + Const.ParamsNames.FEEDBACK_SESSION_STARTDATE
				+ "')[0].value='" + TimeHelper.formatDate(startTime) + "';");
		selectDropdownByVisibleValue(startTimeDropdown,
				TimeHelper.convertToDisplayValueInTimeDropDown(startTime));
	
		// Select deadline date
		js.executeScript("$('#" + Const.ParamsNames.FEEDBACK_SESSION_ENDDATE
				+ "')[0].value='" + TimeHelper.formatDate(endTime) + "';");
		selectDropdownByVisibleValue(endTimeDropdown,
				TimeHelper.convertToDisplayValueInTimeDropDown(endTime));
		
		// Fill in instructions
		fillTextBox(instructionsTextBox, instructions.getValue());
	
		// Select grace period
		selectDropdownByVisibleValue(gracePeriodDropdown, Integer.toString(gracePeriod)+ " mins");		
	
		fsSaveLink.click();
		
		waitForPageToLoad();
	}
	
	public InstructorFeedbacksPage deleteSession() {
		clickAndConfirm(getDeleteSessionLink());
		waitForPageToLoad();
		return changePageType(InstructorFeedbacksPage.class);
	}
}
