package teammates.test.pageobjects;

import static org.testng.AssertJUnit.assertEquals;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/** Represents the "Courses" page for Instructors. */
public class InstructorRecoveryPage extends AppPage {

    @FindBy (id = "recovery_button_sortcoursename")
    private WebElement sortByCourseNameIcon;

    @FindBy (id = "recovery_button_sortcourseid")
    private WebElement sortByCourseIdIcon;

    @FindBy(id = "recovery_courseid")
    private WebElement courseIdTextBox;

    @FindBy(id = "recovery_coursename")
    private WebElement courseNameTextBox;

    @FindBy(id = "recovery_btnAddCourse")
    private WebElement submitButton;

    public InstructorRecoveryPage(Browser browser) {
        super(browser);
    }

    /** Used to check if the loaded page is indeed the 'Recovery' page. */
    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("<h1>Recycle Bin</h1>");
    }

    /**
     * If instructorsList is null, the current value in the page will be used instead.
     */
    public InstructorRecoveryPage addCourse(String courseId, String courseName) {
        fillTextBox(courseIdTextBox, courseId);
        fillTextBox(courseNameTextBox, courseName);

        click(submitButton);
        waitForPageToLoad();
        return this;
    }

    public InstructorRecoveryPage archiveCourse(String courseId) {
        click(getArchiveLink(courseId));
        waitForPageToLoad();
        return this;
    }

    public InstructorRecoveryPage unarchiveCourse(String courseId) {
        click(getUnarchiveLink(courseId));
        waitForPageToLoad();
        return this;
    }

    public String fillCourseIdTextBox(String value) {
        fillTextBox(courseIdTextBox, value);
        return getTextBoxValue(courseIdTextBox);
    }

    public String fillCourseNameTextBox(String value) {
        fillTextBox(courseNameTextBox, value);
        return getTextBoxValue(courseNameTextBox);
    }

    public void submitAndConfirm() {
        clickAndConfirm(submitButton);
        waitForPageToLoad();
    }

    public void submitAndCancel() {
        clickAndCancel(submitButton);
        waitForPageToLoad();
    }

    public WebElement getDeleteLink(String courseId) {
        int courseRowNumber = getRowNumberOfCourse(courseId);
        return getDeleteLinkInRow(courseRowNumber);
    }

    public WebElement getArchiveLink(String courseId) {
        int courseRowNumber = getRowNumberOfCourse(courseId);
        return getArchiveLinkInRow(courseRowNumber);
    }

    public WebElement getUnarchiveLink(String courseId) {
        int courseRowNumber = getRowNumberOfCourse(courseId);
        return getUnarchiveLinkInRow(courseRowNumber);
    }

    public InstructorRecoveryPage sortByCourseName() {
        click(sortByCourseNameIcon);
        return this;
    }

    public InstructorRecoveryPage sortByCourseId() {
        click(sortByCourseIdIcon);
        return this;
    }

    public InstructorCourseEnrollPage loadEnrollLink(String courseId) {
        int courseRowNumber = getRowNumberOfCourse(courseId);
        return goToLinkInRow(
                By.className("t_course_enroll" + courseRowNumber),
                InstructorCourseEnrollPage.class);
    }

    public InstructorCourseDetailsPage loadViewLink(String courseId) {
        int courseRowNumber = getRowNumberOfCourse(courseId);
        return goToLinkInRow(
                By.className("t_course_view" + courseRowNumber),
                InstructorCourseDetailsPage.class);
    }

    public InstructorCourseEditPage loadEditLink(String courseId) {
        int courseRowNumber = getRowNumberOfCourse(courseId);
        return goToLinkInRow(
                By.className("t_course_edit" + courseRowNumber),
                InstructorCourseEditPage.class);
    }

    public void changeUserIdInAjaxLoadCoursesForm(String newUserId) {
        By element = By.id("ajaxForCourses");
        waitForElementPresence(element);
        executeScript("$('#ajaxForCourses [name=\"user\"]').val('" + newUserId + "')");
    }

    public void changeHrefInAjaxLoadCourseStatsLink(String newLink) {
        By element = By.id("ajaxForCourses");
        waitForElementPresence(element);
        executeScript("$('td[id^=\"course-stats\"] > a').attr('href', '" + newLink + "')");
    }

    public void triggerAjaxLoadCourses() {
        By element = By.id("ajaxForCourses");
        waitForElementPresence(element);
        executeScript("$('#ajaxForCourses').trigger('submit')");
    }

    public void triggerAjaxLoadCourseStats(int rowIndex) {
        executeScript("$('.course-stats-link-" + rowIndex + "').first().trigger('click')");
    }

    public void waitForAjaxLoadCoursesError() {
        By element = By.id("retryAjax");
        waitForElementPresence(element);
        WebElement statusMessage =
                browser.driver.findElement(By.id("statusMessagesToUser")).findElement(By.className("statusMessage"));
        assertEquals("Courses could not be loaded. Click here to retry.", statusMessage.getText());
    }

    public void waitForAjaxLoadCoursesSuccess() {
        By element = By.id("tableActiveCourses");
        waitForElementPresence(element);
    }

    private int getCourseCount() {
        By activeCoursesTable = By.id("tableActiveCourses");
        waitForElementPresence(activeCoursesTable);
        return browser.driver.findElement(activeCoursesTable).findElements(By.tagName("tr")).size();
    }

    private int getRowNumberOfCourse(String courseId) {
        for (int i = 0; i < getCourseCount(); i++) {
            if (getCourseIdCell(i).getText().equals(courseId)) {
                return i;
            }
        }
        return -1;
    }

    private WebElement getCourseIdCell(int rowId) {
        return browser.driver.findElement(By.id("courseid" + rowId));
    }

    private WebElement getDeleteLinkInRow(int rowId) {
        By deleteLink = By.className("t_course_delete" + rowId);
        return browser.driver.findElement(deleteLink);
    }

    private WebElement getArchiveLinkInRow(int rowId) {
        By archiveLink = By.className("t_course_archive" + rowId);
        return browser.driver.findElement(archiveLink);
    }

    private WebElement getUnarchiveLinkInRow(int rowId) {
        By archiveLink = By.id("t_course_unarchive" + rowId);
        return browser.driver.findElement(archiveLink);
    }

    private <T extends AppPage> T goToLinkInRow(By locator, Class<T> destinationPageType) {
        click(browser.driver.findElement(locator));
        waitForPageToLoad();
        return changePageType(destinationPageType);
    }

}
