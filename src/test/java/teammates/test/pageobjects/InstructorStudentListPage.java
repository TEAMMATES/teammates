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
	
	@FindBy(id = "option_check")
	private WebElement showMoreOptions;
	
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
	
	public InstructorCourseStudentDetailsViewPage clickViewStudent(String courseId, String studentName) {
		String rowId = getStudentRowId(courseId, studentName);
		getViewLink(rowId).click();
		waitForPageToLoad();
		return changePageType(InstructorCourseStudentDetailsViewPage.class);
	}
	
	public InstructorCourseStudentDetailsEditPage clickEditStudent(String courseId, String studentName) {
		String rowId = getStudentRowId(courseId, studentName);
		getEditLink(rowId).click();
		waitForPageToLoad();
		return changePageType(InstructorCourseStudentDetailsEditPage.class);
	}
	
	public InstructorStudentRecordsPage clickViewRecordsStudent(String courseId, String studentName) {
		String rowId = getStudentRowId(courseId, studentName);
		getViewRecordsLink(rowId).click();
		waitForPageToLoad();
		return changePageType(InstructorStudentRecordsPage.class);
	}
	
	public InstructorStudentListPage clickDeleteAndCancel(String courseId, String studentName) {
		String rowId = getStudentRowId(courseId, studentName);
		clickAndCancel(getDeleteLink(rowId));
		return this;
	}
	
	public InstructorStudentListPage clickDeleteAndConfirm(String courseId, String studentName) {
		String rowId = getStudentRowId(courseId, studentName);
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
	
	public void clickShowMoreOptions(){
		showMoreOptions.click();
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

	private String getStudentRowId(String courseId, String studentName) {
		int studentCount = browser.driver.findElements(By.className("student_row"))
				.size();
		int courseNumber = getCourseNumber(courseId);
		for (int i = 0; i < studentCount; i++) {
			String studentNameInRow = getStudentNameInRow(courseNumber, i);
			if (studentNameInRow.equals(studentName)) {
				return ("c"+courseNumber+"."+i);
			}
		}
		return "";
	}
	
	private String getStudentNameInRow(int courseNumber, int rowId) {
		String xpath = "//tr[@class='student_row' and @id='student-c" + courseNumber + "."
				+ rowId	+ "']//td[@id='" + Const.ParamsNames.STUDENT_NAME + "-c" + courseNumber + "." + rowId + "']";
		return browser.driver.findElement(By.xpath(xpath)).getText();
	}
	
	private WebElement getEnrollLink(int courseNumber) {
		return browser.driver.findElement(By.className("t_course_enroll-" + courseNumber));
	}
	
	private WebElement getViewLink(String rowId) {
		return browser.driver.findElement(By.className("t_student_details-" + rowId));
	}
	
	private WebElement getEditLink(String rowId) {
		return browser.driver.findElement(By.className("t_student_edit-" + rowId));
	}
	
	private WebElement getViewRecordsLink(String rowId) {
		return browser.driver.findElement(By.className("t_student_records-" + rowId));
	}
	
	private WebElement getDeleteLink(String rowId) {
		return browser.driver.findElement(By.className("t_student_delete-" + rowId));
	}
	
	private String getElementText(By locator) {
		if (!isElementPresent(locator)){
			return "";
		}
		return browser.driver.findElement(locator).getText();
	}
}
