package teammates.test.pageobjects;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;


public class InstructorCourseEditPage extends AppPage {
	
	@FindBy(id = "courseid")
	private WebElement courseIdTextBox;
	
	@FindBy(id = "courseName")
	private WebElement courseNameTextBox;
	
	@FindBy(id = "courseDeleteLink")
	private WebElement deleteCourseLink;
	
	@FindBy(id = "instrEditLink1")
	private WebElement editInstructorLink;
	
	@FindBy(id = "instrDeleteLink1")
	private WebElement deleteInstructorLink;
	
	@FindBy(id = "instructorid1")
	private WebElement editInstructorIdTextBox;
	
	@FindBy(id = "instructorname1")
	private WebElement editInstructorNameTextBox;
	
	@FindBy(id = "instructoremail1")
	private WebElement editInstructorEmailTextBox;
	
	@FindBy(id = "btnSaveInstructor1")
	private WebElement saveInstructorButton;
	
	@FindBy(id = "btnShowNewInstructorForm")
	private WebElement showNewInstructorFormButton;
	
	@FindBy(id = "instructorid")
	private WebElement instructorIdTextBox;
	
	@FindBy(id = "instructorname")
	private WebElement instructorNameTextBox;
	
	@FindBy(id = "instructoremail")
	private WebElement instructorEmailTextBox;
	
	@FindBy(id = "btnAddInstructor")
	private WebElement addInstructorButton;
	
	public InstructorCourseEditPage(Browser browser) {
		super(browser);
	}

	@Override
	protected boolean containsExpectedPageContents() {
		return getPageSource().contains("<h1>Edit Course Details</h1>");
	}
	
	public void addNewInstructor(String id, String name, String email) {
		clickShowNewInstructorFormButton();
		
		fillInstructorGoogleId(id);
		fillInstructorName(name);
		fillInstructorEmail(email);
		
		addInstructorButton.click();
		waitForPageToLoad();
	}

	public void editInstructor(String id, String name, String email) {
		clickEditInstructorLink();
		
		editInstructorName(name);
		editInstructorEmail(email);
		
		saveInstructorButton.click();
		waitForPageToLoad();
	}
	
	public void editInstructorName(String value) {
		fillTextBox(editInstructorNameTextBox, value);
	}
	
	public void editInstructorEmail(String value) {
		fillTextBox(editInstructorEmailTextBox, value);
	}

	public void fillInstructorGoogleId(String value) {
		fillTextBox(instructorIdTextBox, value);
	}
	
	public void fillInstructorName(String value) {
		fillTextBox(instructorNameTextBox, value);
	}
	
	public void fillInstructorEmail(String value) {
		fillTextBox(instructorEmailTextBox, value);
	}
	
	public boolean clickEditInstructorLink() {
		editInstructorLink.click();
		
		boolean isEditable = editInstructorNameTextBox.isEnabled()
							&& editInstructorEmailTextBox.isEnabled()
							&& saveInstructorButton.isDisplayed();
		
		return isEditable;
	}
	
	public void clickSaveInstructorButton() {
		saveInstructorButton.click();
		waitForPageToLoad();
	}
	
	public boolean clickShowNewInstructorFormButton() {
		showNewInstructorFormButton.click();
		
		boolean isFormShownCorrectly = instructorIdTextBox.isEnabled()
				&& instructorNameTextBox.isEnabled()
				&& instructorEmailTextBox.isEnabled()
				&& addInstructorButton.isDisplayed();

		return isFormShownCorrectly;
	}
	
	public void clickAddInstructorButton() {
		addInstructorButton.click();
		waitForPageToLoad();
	}
	
	public InstructorCoursesPage clickDeleteCourseLinkAndConfirm() {
		clickAndConfirm(deleteCourseLink);
		waitForPageToLoad();
		return changePageType(InstructorCoursesPage.class);
	}

	public void clickDeleteCourseLinkAndCancel() {
		clickAndCancel(deleteCourseLink);
	}
	
	public void clickDeleteInstructorLinkAndConfirm() {
		clickAndConfirm(deleteInstructorLink);
		waitForPageToLoad();
	}
	
	public void clickDeleteInstructorLinkAndCancel() {
		clickAndCancel(deleteInstructorLink);
	}

}
