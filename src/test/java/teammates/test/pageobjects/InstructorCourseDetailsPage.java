package teammates.test.pageobjects;

import static org.testng.AssertJUnit.assertEquals;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import teammates.common.util.Const;


public class InstructorCourseDetailsPage extends AppPage {
	
	@FindBy (id = "button_sortstudentstatus")
	private WebElement sortByStatusIcon;
	
	@FindBy (id = "button_sortstudentname")
	private WebElement sortByNameIcon;
	
	@FindBy (id = "button_sortstudentteam")
	private WebElement sortByTeamIcon;
	
	@FindBy (id = "button_remind")
	private WebElement remindAllButton;

	public InstructorCourseDetailsPage(Browser browser){
		super(browser);
	}

	@Override
	protected boolean containsExpectedPageContents() {
		return getPageSource().contains("<h1>Course Details</h1>");
	}

	public String getCourseId() {
		return browser.driver.findElement(By.id("courseid")).getText();
	}

	public InstructorCourseDetailsPage verifyIsCorrectPage(String courseId) {
		assertEquals(courseId, this.getCourseId());
		return this;
	}

	public InstructorCourseDetailsPage sortByStatus() {
		sortByStatusIcon.click();
		return this;
	}

	public InstructorCourseDetailsPage sortByName() {
		sortByNameIcon.click();
		return this;
	}
	
	public InstructorCourseDetailsPage sortByTeam() {
		sortByTeamIcon.click();
		return this;
	}

	public InstructorCourseDetailsPage clickRemindAllAndCancel() {
		clickAndCancel(remindAllButton);
		return this;
	}
	
	public InstructorCourseDetailsPage clickRemindAllAndConfirm() {
		clickAndConfirm(remindAllButton);
		return this;
	}
	
	public InstructorCourseStudentDetailsViewPage clickViewStudent(String studentName) {
		int rowId = getStudentRowId(studentName);
		getViewLink(rowId).click();
		waitForPageToLoad();
		return changePageType(InstructorCourseStudentDetailsViewPage.class);
	}
	
	public InstructorCourseStudentDetailsEditPage clickEditStudent(String studentName) {
		int rowId = getStudentRowId(studentName);
		getEditLink(rowId).click();
		waitForPageToLoad();
		return changePageType(InstructorCourseStudentDetailsEditPage.class);
	}
	
	public InstructorCourseDetailsPage clickRemindStudentAndCancel(String studentName) {
		int rowId = getStudentRowId(studentName);
		clickAndCancel(getRemindLink(rowId));
		return this;
	}
	
	public InstructorCourseDetailsPage clickRemindStudentAndConfirm(String studentName) {
		int rowId = getStudentRowId(studentName);
		clickAndConfirm(getRemindLink(rowId));
		return this;
	}
	
	public InstructorCourseDetailsPage clickDeleteAndCancel(String studentName) {
		int rowId = getStudentRowId(studentName);
		clickAndCancel(getDeleteLink(rowId));
		return this;
	}
	
	public InstructorCourseDetailsPage clickDeleteAndConfirm(String studentName) {
		int rowId = getStudentRowId(studentName);
		clickAndConfirm(getDeleteLink(rowId));
		return this;
	}

	private WebElement getRemindLink(int rowId) {
		return browser.driver.findElement(By.className("t_student_resend" + rowId));
	}
	
	private WebElement getViewLink(int rowId) {
		return browser.driver.findElement(By.className("t_student_details" + rowId));
	}
	
	private WebElement getEditLink(int rowId) {
		return browser.driver.findElement(By.className("t_student_edit" + rowId));
	}
	
	private WebElement getDeleteLink(int rowId) {
		return browser.driver.findElement(By.className("t_student_delete" + rowId));
	}
	
	private int getStudentRowId(String studentName) {
		int studentCount = browser.driver.findElements(By.className("student_row"))
				.size();
		for (int i = 0; i < studentCount; i++) {
			String studentNameInRow = getStudentNameInRow(i);
			if (studentNameInRow.equals(studentName)) {
				return i;
			}
		}
		return -1;
	}

	private String getStudentNameInRow(int rowId) {
		String xpath = "//tr[@class='student_row' and @id='student"
				+ rowId	+ "']//td[@id='" + Const.ParamsNames.STUDENT_NAME + "']";
		return browser.driver.findElement(By.xpath(xpath)).getText();
	}

}
