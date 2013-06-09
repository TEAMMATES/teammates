package teammates.test.pageobjects;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;


public class InstructorCourseEditPage extends AppPage {
	
	@FindBy(id = "courseid")
	private WebElement courseIdTextBox;
	
	@FindBy(id = "courseName")
	private WebElement courseNameTextBox;
	
	@FindBy(id = "instructorlist")
	private WebElement instructorListTextBox;
	
	@FindBy(id = "button_submit")
	private WebElement submitButton;
	
	public InstructorCourseEditPage(Browser browser) {
		super(browser);
	}

	@Override
	protected boolean containsExpectedPageContents() {
		return getPageSource().contains("<h1>Edit Course Details</h1>");
	}

	public String getInstructorList() {
		return getTextBoxValue(instructorListTextBox);
	}

	public void fillInstructorList(String value) {
		fillTextBox(instructorListTextBox, value);
	}

	public void submitUnsuccessfully(){
		submitButton.click();
		waitForPageToLoad();
	}

	public InstructorCoursesPage submit() {
		submitButton.click();
		waitForPageToLoad();
		return changePageType(InstructorCoursesPage.class);
	}

	public InstructorCoursesPage submitUpdateToCourseInfo(String instructorList) {
		fillInstructorList(instructorList);
		return submit();
	}

	public InstructorCoursesPage clickSubmitButtonAndConfirm() {
		clickAndConfirm(submitButton);
		waitForPageToLoad();
		return changePageType(InstructorCoursesPage.class);
	}

	public void clickSubmitButtonAndCancel() {
		clickAndCancel(submitButton);
	}

}
