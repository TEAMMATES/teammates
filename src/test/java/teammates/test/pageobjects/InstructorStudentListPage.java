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
	
	@FindBy(id = "show_email")
	private WebElement showEmailLink;
	
	public InstructorStudentListPage(Browser browser) {
		super(browser);
	}

	@Override
	protected boolean containsExpectedPageContents() {
		return getPageSource().contains("<h1>Student List</h1>");
	}
	
	public InstructorCourseEnrollPage clickEnrollStudents(String courseId) {
		int courseNumber = getCourseNumber(courseId);
		getEnrollLink(courseNumber).click();
		waitForPageToLoad();
		return changePageType(InstructorCourseEnrollPage.class);
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
	
	public void setLiveSearchKey(String searchKey){
		searchBox.clear();
		searchBox.sendKeys(searchKey);
	}
	
	public void clickShowEmail() {
		showEmailLink.click();
	}
	
	private int getCourseNumber(String courseId) {
		int id = 0;
		while (isElementPresent(By.id("course-" + id))) {
			if (getElementText(
					By.xpath("//div[@id='course-" + id + "']/div[@class='courseTitle']/h2"))
					.startsWith("[" + courseId + "]")) {
				return id;
			}
			id++;
		}
		return -1;
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
		String xpath = "//tr[@class='student_row' and @id='student-"
				+ rowId	+ "']//td[@id='" + Const.ParamsNames.STUDENT_NAME + "']";
		return browser.driver.findElement(By.xpath(xpath)).getText();
	}
	
	private WebElement getEnrollLink(int courseNumber) {
		return browser.driver.findElement(By.className("t_course_enroll-" + courseNumber));
	}
	
	private WebElement getViewLink(int rowId) {
		return browser.driver.findElement(By.className("t_student_details-" + rowId));
	}
	
	private WebElement getEditLink(int rowId) {
		return browser.driver.findElement(By.className("t_student_edit-" + rowId));
	}
	
	private WebElement getViewRecordsLink(int rowId) {
		return browser.driver.findElement(By.className("t_student_records-" + rowId));
	}
	
	private WebElement getDeleteLink(int rowId) {
		return browser.driver.findElement(By.className("t_student_delete-" + rowId));
	}
	
	private String getElementText(By locator) {
		if (!isElementPresent(locator)){
			return "";
		}
		return browser.driver.findElement(locator).getText();
	}
}
