package teammates.test.pageobjects;

import java.util.Date;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.common.util.TimeHelper;

import com.google.appengine.api.datastore.Text;

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
	
	@FindBy(id = "givertype")
	private WebElement giverDropdown;
	
	@FindBy(id = "recipienttype")
	private WebElement recipientDropdown;
	
	@FindBy(id = "numofrecipients-")
	private WebElement numberOfRecipients;
	
	@FindBy(xpath = "//input[@name='numofrecipientstype' and @value='max']")
	private WebElement maxNumOfRecipients;
	
	@FindBy(xpath = "//input[@name='numofrecipientstype' and @value='custom']")
	private WebElement customNumOfRecipients;
	
	@FindBy(id = "button_preview_student")
	private WebElement previewAsStudentButton;
	
	@FindBy(id = "button_preview_instructor")
	private WebElement previewAsInstructorButton;
	
	@FindBy(id = "questiongetlink-1")
	private WebElement getLinkButton;
	
	
	public InstructorFeedbackEditPage(Browser browser) {
		super(browser);
	}

	@Override
	protected boolean containsExpectedPageContents() {
		return getPageSource().contains("<h1>Edit Feedback Session</h1>");
	}
	
	public void fillInstructionsBox(String instructions) {
		fillTextBox(instructionsTextBox, instructions);
	}
	
	public void fillQuestionBox(String qnText) {
		fillTextBox(questionTextBox, qnText);
	}
	
	public void fillEditQuestionBox(String qnText, int qnIndex) {
		WebElement questionEditTextBox = browser.driver.findElement(By.id("questiontext-" + qnIndex));
		fillTextBox(questionEditTextBox, qnText);
	}
	
	public void fillNumOfEntitiesToGiveFeedbackToBox(String num) {
		fillTextBox(numberOfRecipients, num);
	}
	
	public void fillMinNumScaleBox(int minScale, int qnNumber) {
		String idSuffix = qnNumber > 0 ? "-" + qnNumber : "";
		WebElement minScaleBox = browser.driver.findElement(By.id("minScaleBox" + idSuffix));
		fillTextBox(minScaleBox, Integer.toString(minScale));
	}
	
	public void fillMaxNumScaleBox(int maxScale, int qnNumber) {
		String idSuffix = qnNumber > 0 ? "-" + qnNumber : "";
		WebElement maxScaleBox = browser.driver.findElement(By.id("maxScaleBox" + idSuffix));
		fillTextBox(maxScaleBox, Integer.toString(maxScale));
	}
	
	public void fillStepNumScaleBox(double step, int qnNumber) {
		String idSuffix = qnNumber > 0 ? "-" + qnNumber : "";
		WebElement stepBox = browser.driver.findElement(By.id("stepBox" + idSuffix));
		fillTextBox(stepBox, StringHelper.toDecimalFormatString(step));
	}
	
	public String getNumScalePossibleValuesString(int qnNumber) {
		String idSuffix = qnNumber > 0 ? "-" + qnNumber : "";
		WebElement possibleValuesSpan = browser.driver.findElement(By.id("numScalePossibleValues" + idSuffix));
		return possibleValuesSpan.getText();
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
	
	public WebElement getDeleteQuestionLink(int qnIndex){
		return browser.driver.findElement(By.xpath("//a[@onclick='deleteQuestion(" + qnIndex + ")']"));
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
	
	public boolean clickEditQuestionButton(int qnNumber){
		WebElement qnEditLink = browser.driver.findElement(By.id("questionedittext-" + qnNumber));	
		qnEditLink.click();
		
		// Check if links toggle properly.
		WebElement qnSaveLink = browser.driver.findElement(By.id("questionsavechangestext-" + qnNumber));	
		return qnSaveLink.isDisplayed();
	}
	
	public void clickSaveExistingQuestionButton(int qnNumber){
		WebElement qnSaveLink = browser.driver.findElement(By.id("questionsavechangestext-" + qnNumber));
		qnSaveLink.click();
		waitForPageToLoad();
	}
	
	public void selectNewQuestionType(String questionType){
		selectDropdownByVisibleValue(browser.driver.findElement(By.id("questionTypeChoice")), questionType);
	}
	
	public void selectMcqGenerateOptionsFor(String generateFor, int questionNumber){
		selectDropdownByVisibleValue(browser.driver.findElement(By.id("mcqGenerateForSelect-" + questionNumber)), generateFor);
	}
	
	public void selectMsqGenerateOptionsFor(String generateFor, int questionNumber){
		selectDropdownByVisibleValue(browser.driver.findElement(By.id("msqGenerateForSelect-" + questionNumber)), generateFor);
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
	
	public void selectGiverToBeStudents() {
		selectDropdownByVisibleValue(giverDropdown, "Students in this course");
	}
	
	public void selectGiverToBeInstructors() {
		selectDropdownByVisibleValue(giverDropdown, "Instructors in this course");
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
	
	public void fillMcqOption(int optionIndex, String optionText){
		WebElement optionBox = browser.driver.findElement(By.id("mcqOption-" + optionIndex));
		fillTextBox(optionBox, optionText);
	}
	
	public void clickAddMoreMcqOptionLink(){
		WebElement addMoreOptionLink = browser.driver.findElement(By.id("mcqAddOptionLink"));
		addMoreOptionLink.click();
	}
	
	public void clickRemoveMcqOptionLink(int optionIndex, int qnIndex) {
		String idSuffix = qnIndex > 0 ? "-" + qnIndex : "";
		
		WebElement mcqOptionRow = browser.driver.findElement(By.id("mcqOptionRow-" + optionIndex + idSuffix));
		WebElement removeOptionLink = mcqOptionRow.findElement(By.id("mcqRemoveOptionLink"));
		removeOptionLink.click();
	}
	
	public void clickGenerateOptionsCheckbox(int qnIndex) {
		String idSuffix = qnIndex > 0 ? "-" + qnIndex : "";
		
		WebElement generateOptionsCheckbox = browser.driver.findElement(By.id("generateOptionsCheckbox" + idSuffix));
		generateOptionsCheckbox.click();
	}
	
	public void fillMsqOption(int optionIndex, String optionText){
		WebElement optionBox = browser.driver.findElement(By.id("msqOption-" + optionIndex));
		fillTextBox(optionBox, optionText);
	}
	
	public void clickAddMoreMsqOptionLink(){
		WebElement addMoreOptionLink = browser.driver.findElement(By.id("msqAddOptionLink"));
		addMoreOptionLink.click();
	}
	
	public void clickRemoveMsqOptionLink(int optionIndex, int qnIndex) {
		String idSuffix = qnIndex > 0 ? "-" + qnIndex : "";
		
		WebElement msqOptionRow = browser.driver.findElement(By.id("msqOptionRow-" + optionIndex + idSuffix));
		WebElement removeOptionLink = msqOptionRow.findElement(By.id("msqRemoveOptionLink"));
		removeOptionLink.click();
	}
	
	public FeedbackSubmitPage clickPreviewAsStudentButton() {
		previewAsStudentButton.click();
		waitForPageToLoad();
		switchToNewWindow();
		return changePageType(FeedbackSubmitPage.class);
	}
	
	public FeedbackSubmitPage clickPreviewAsInstructorButton() {
		previewAsInstructorButton.click();
		waitForPageToLoad();
		switchToNewWindow();
		return changePageType(FeedbackSubmitPage.class);
	}
	
	public void clickGetLinkButton() {
		getLinkButton.click();
	}
}
