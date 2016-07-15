package teammates.test.pageobjects;

import static org.testng.AssertJUnit.assertEquals;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;

import teammates.common.util.Const;

public class InstructorStudentListPage extends AppPage {

    @FindBy(id = "searchbox")
    private WebElement searchBox;

    @FindBy(id = "buttonSearch")
    private WebElement searchButton;

    @FindBy(id = "displayArchivedCourses_check")
    private WebElement displayArchiveOptions;

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
        switchToNewWindow();
        return changePageType(InstructorCourseStudentDetailsViewPage.class);
    }

    public InstructorCourseStudentDetailsEditPage clickEditStudent(String courseId, String studentName) {
        String rowId = getStudentRowId(courseId, studentName);
        getEditLink(rowId).click();
        waitForPageToLoad();
        switchToNewWindow();
        return changePageType(InstructorCourseStudentDetailsEditPage.class);
    }

    public InstructorStudentRecordsPage clickViewRecordsStudent(String courseId, String studentName) {
        String rowId = getStudentRowId(courseId, studentName);
        getViewRecordsLink(rowId).click();
        waitForPageToLoad();
        switchToNewWindow();
        return changePageType(InstructorStudentRecordsPage.class);
    }

    public InstructorStudentListPage clickDeleteAndCancel(String courseId, String studentName) {
        String rowId = getStudentRowId(courseId, studentName);
        getDeleteLink(rowId).click();
        waitForConfirmationModalAndClickCancel();
        return this;
    }

    public InstructorStudentListPage clickDeleteAndConfirm(String courseId, String studentName) {
        String rowId = getStudentRowId(courseId, studentName);
        getDeleteLink(rowId).click();
        waitForConfirmationModalAndClickOk();
        return this;
    }

    public InstructorStudentListPage clickShowPhoto(String courseId, String studentName) {
        String rowId = getStudentRowId(courseId, studentName);
        WebElement photoCell = browser.driver.findElement(By.id("studentphoto-c" + rowId));
        WebElement photoLink = photoCell.findElement(By.tagName("a"));
        Actions actions = new Actions(browser.driver);
        actions.click(photoLink).build().perform();
        return this;
    }

    public void setSearchKey(String searchKey) {
        searchBox.clear();
        searchBox.sendKeys(searchKey);
        searchButton.click();
    }

    public void checkCourse(int courseIdx) {
        browser.driver.findElement(By.id("course_check-" + courseIdx)).click();
        waitForAjaxLoaderGifToDisappear();
    }

    public void clickDisplayArchiveOptions() {
        displayArchiveOptions.click();
    }

    public void verifyProfilePhoto(String courseId, String studentName, String profilePhotoSrc) {
        String rowId = getStudentRowId(courseId, studentName);
        assertEquals(profilePhotoSrc, browser.driver.findElement(By.id("studentphoto-c" + rowId))
                                                    .findElement(By.tagName("img"))
                                                    .getAttribute("src"));
        WebElement photo = browser.driver.findElement(By.id("studentphoto-c" + rowId))
                                         .findElement(By.cssSelector(".profile-pic-icon-click > img"));
        Actions action = new Actions(browser.driver);
        action.click(photo).build().perform();
        assertEquals(profilePhotoSrc, browser.driver.findElement(By.id("studentphoto-c" + rowId))
                                                    .findElement(By.cssSelector(".popover-content > .profile-pic"))
                                                    .getAttribute("src"));
    }

    private int getCourseNumber(String courseId) {
        int id = 0;
        while (isElementPresent(By.id("panelHeading-" + id))) {
            if (getElementText(By.xpath("//div[@id='panelHeading-" + id + "']//strong"))
                    .startsWith("[" + courseId + "]")) {
                return id;
            }
            id++;
        }
        return -1;
    }

    public String getStudentRowId(String courseId, String studentName) {
        int courseNumber = getCourseNumber(courseId);
        int studentCount = browser.driver.findElements(By.cssSelector("tr[id^='student-c" + courseNumber + "']"))
                                         .size();
        for (int i = 0; i < studentCount; i++) {
            String studentNameInRow = getStudentNameInRow(courseNumber, i);
            if (studentNameInRow.equals(studentName)) {
                return courseNumber + "." + i;
            }
        }
        return "";
    }

    private String getStudentNameInRow(int courseNumber, int rowId) {
        String xpath = "//tr[@id='student-c" + courseNumber + "." + rowId + "']"
                     + "//td[@id='" + Const.ParamsNames.STUDENT_NAME + "-c" + courseNumber + "." + rowId + "']";
        return browser.driver.findElement(By.xpath(xpath)).getText();
    }

    private WebElement getEnrollLink(int courseNumber) {
        return browser.driver.findElement(By.id("panelHeading-" + courseNumber))
                             .findElement(By.className("course-enroll-for-test"));
    }

    private WebElement getViewLink(String rowId) {
        WebElement studentRow = browser.driver.findElement(By.id("student-c" + rowId));
        return studentRow.findElement(By.cssSelector("td.no-print.align-center > a:nth-child(1)"));
    }

    private WebElement getEditLink(String rowId) {
        WebElement studentRow = browser.driver.findElement(By.id("student-c" + rowId));
        return studentRow.findElement(By.cssSelector("td.no-print.align-center > a:nth-child(2)"));
    }

    private WebElement getViewRecordsLink(String rowId) {
        WebElement studentRow = browser.driver.findElement(By.id("student-c" + rowId));
        WebElement fourthLink = studentRow.findElement(By.cssSelector("td.no-print.align-center > a:nth-child(4)"));
        
        if ("All Records".equals(fourthLink.getText())) {
            return fourthLink;
        }
        return studentRow.findElement(By.cssSelector("td.no-print.align-center > a:nth-child(5)"));
    }

    private WebElement getDeleteLink(String rowId) {
        WebElement studentRow = browser.driver.findElement(By.id("student-c" + rowId));
        WebElement thirdLink = studentRow.findElement(By.cssSelector("td.no-print.align-center > a:nth-child(3)"));
        
        if ("Delete".equals(thirdLink.getText())) {
            return thirdLink;
        }
        return studentRow.findElement(By.cssSelector("td.no-print.align-center > a:nth-child(4)"));
    }

    private String getElementText(By locator) {
        if (!isElementPresent(locator)) {
            return "";
        }
        return browser.driver.findElement(locator).getText();
    }

}
