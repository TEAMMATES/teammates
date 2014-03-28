package teammates.test.pageobjects;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.fail;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import teammates.common.util.ThreadHelper;

public class InstructorFeedbackResultsPage extends AppPage {

	@FindBy(id = "button_sortgiver")
	private WebElement sortTableGiverButton;
	
	@FindBy(id = "button_sortrecipient")
	private WebElement sortTableRecipientButton;
	
	@FindBy(id = "button_sortanswer")
	private WebElement sortTableAnswerButton;
	
	@FindBy(id = "showResponseCommentAddFormButton-1-1-1")
	private WebElement showResponseCommentAddFormButton;
	
	@FindBy(id = "responseCommentAddForm-1-1-1")
	private WebElement addResponseCommentForm;
	
	
	public InstructorFeedbackResultsPage(Browser browser) {
		super(browser);
	}

	@Override
	protected boolean containsExpectedPageContents() {
		return getPageSource().contains("<h1>Feedback Results - Instructor</h1>");
	}
	
	public void displayByGiver() {
		WebElement button = browser.driver.findElement(By.xpath("//span[@class='label bold' and contains(text(),'Sort by giver')]"));
		button.click();
	}
	
	public void displayByRecipient() {
		WebElement button = browser.driver.findElement(By.xpath("//span[@class='label bold' and contains(text(),'Sort by recipient')]"));
		button.click();
	}
	
	public void displayByTable() {
		WebElement button = browser.driver.findElement(By.xpath("//span[@class='label bold' and contains(text(),'View as table')]"));
		button.click();
		waitForPageToLoad();
	}
	
	public AppPage sortTableByGiver() {
		sortTableGiverButton.click();
		return this;
	}
	
	public AppPage sortTableByRecipient() {
		sortTableRecipientButton.click();
		return this;
	}
	
	public AppPage sortTableByAnswer() {
		sortTableAnswerButton.click();
		return this;
	}
	
	public InstructorFeedbackEditPage clickEditLink() {
		WebElement button = browser.driver.findElement(By.linkText("[Edit]"));
		button.click();
		waitForPageToLoad();
		return changePageType(InstructorFeedbackEditPage.class);
	}
	
	public boolean clickQuestionAdditionalInfoButton(int qnNumber, String additionalInfoId){
		WebElement qnAdditionalInfoButton = browser.driver.findElement(By.id("questionAdditionalInfoButton-" + qnNumber + "-" + additionalInfoId));	
		qnAdditionalInfoButton.click();
		// Check if links toggle properly.
		WebElement qnAdditionalInfo = browser.driver.findElement(By.id("questionAdditionalInfo-" + qnNumber + "-" + additionalInfoId));	
		return qnAdditionalInfo.isDisplayed();
	}
	
	public String getQuestionAdditionalInfoButtonText(int qnNumber, String additionalInfoId){
		WebElement qnAdditionalInfoButton = browser.driver.findElement(By.id("questionAdditionalInfoButton-" + qnNumber + "-" + additionalInfoId));	
		return qnAdditionalInfoButton.getText();
	}
	
	public void addFeedbackResponseComment(String commentText) {
		showResponseCommentAddFormButton.findElement(By.tagName("div")).click();
		fillTextBox(addResponseCommentForm.findElement(By.tagName("textarea")), commentText);
		addResponseCommentForm.findElement(By.className("button")).click();
		ThreadHelper.waitFor(1000);
	}
	
	public void editFeedbackResponseComment(String commentIdSuffix, String newCommentText) {
		WebElement commentRow = browser.driver.findElement(By.id("responseCommentRow" + commentIdSuffix));
		commentRow.findElement(By.linkText("Edit")).click();
		
		WebElement commentEditForm = browser.driver.findElement(By.id("responseCommentEditForm" + commentIdSuffix));
		fillTextBox(commentEditForm.findElement(By.name("responsecommenttext")), newCommentText);
		commentEditForm.findElement(By.className("button")).click();
		ThreadHelper.waitFor(1000);
	}
	
	public void deleteFeedbackResponseComment(String commentIdSuffix) {
		WebElement commentRow = browser.driver.findElement(By.id("responseCommentRow" + commentIdSuffix));
		commentRow.findElement(By.linkText("Delete")).click();
		ThreadHelper.waitFor(1000);
	}
	
	public void verifyCommentRowContent(String commentRowIdSuffix, String commentText, String giverName) {
		WebElement commentRow = browser.driver.findElement(By.id("responseCommentRow" + commentRowIdSuffix));
		assertEquals(commentText, commentRow.findElement(By.className("feedbackResponseCommentText")).getText());
		assertEquals(giverName, commentRow.findElement(By.className("feedbackResponseCommentGiver")).getText());
	}
	
	public void verifyCommentFormErrorMessage(String commentTableIdSuffix, String errorMessage) {
		WebElement commentRow = browser.driver.findElement(By.id("responseCommentTable" + commentTableIdSuffix));
		assertEquals(errorMessage, commentRow.findElement(By.tagName("span")).getText());
	}
	
	public void verifyRowMissing(String rowIdSuffix) {
		try {
			verifyCommentRowContent(rowIdSuffix, "", "");
			fail("Row expected to be missing found.");
		} catch (NoSuchElementException e) {
			// row expected to be missing
		}
	}
}
