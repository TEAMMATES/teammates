package teammates.test.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import teammates.common.util.Const;

public class InstructorStudentListPage extends AppPage {
	
	@FindBy(id = "searchbox")
	private WebElement searchBox;
	
	@FindBy(id = "button_search")
	private WebElement searchButton;
	
	public InstructorStudentListPage(Browser browser) {
		super(browser);
	}

	@Override
	protected boolean containsExpectedPageContents() {
		return getPageSource().contains("<h1>Student List</h1>");
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
	
	public InstructorStudentRecordsPage clickViewRecordsStudent(String studentName) {
		int rowId = getStudentRowId(studentName);
		getViewRecordsLink(rowId).click();
		waitForPageToLoad();
		return changePageType(InstructorStudentRecordsPage.class);
	}
	
	public InstructorStudentListPage clickDeleteAndCancel(String studentName) {
		int rowId = getStudentRowId(studentName);
		clickAndCancel(getDeleteLink(rowId));
		return this;
	}
	
	public InstructorStudentListPage clickDeleteAndConfirm(String studentName) {
		int rowId = getStudentRowId(studentName);
		clickAndConfirm(getDeleteLink(rowId));
		return this;
	}
	
	public void setSearchKey(String searchKey){
		searchBox.clear();
		searchBox.sendKeys(searchKey);
		searchButton.click();
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
	
	private WebElement getViewLink(int rowId) {
		return browser.driver.findElement(By.className("t_student_details" + rowId));
	}
	
	private WebElement getEditLink(int rowId) {
		return browser.driver.findElement(By.className("t_student_edit" + rowId));
	}
	
	private WebElement getViewRecordsLink(int rowId) {
		return browser.driver.findElement(By.className("t_student_records" + rowId));
	}
	
	private WebElement getDeleteLink(int rowId) {
		return browser.driver.findElement(By.className("t_student_delete" + rowId));
	}
}
