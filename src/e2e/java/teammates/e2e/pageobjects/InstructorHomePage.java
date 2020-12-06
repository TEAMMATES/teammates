package teammates.e2e.pageobjects;

import static org.junit.Assert.assertEquals;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;

/**
 * Represents the instructor home page.
 */
public class InstructorHomePage extends AppPage {

    @FindBy(id = "search-input")
    private WebElement searchBar;

    @FindBy(id = "btn-search")
    private WebElement searchButton;

    public InstructorHomePage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageTitle().contains("Home");
    }

    public InstructorSearchPage searchKeyword(String keyword) {
        fillTextBox(searchBar, keyword);
        click(searchButton);
        waitUntilAnimationFinish();
        return changePageType(InstructorSearchPage.class);
    }

    public void verifyCourseTabDetails(int courseTabIndex, CourseAttributes course, FeedbackSessionAttributes[] sessions) {
        String expectedDetails = "[" + course.getId() + "]: " + course.getName();
        assertEquals(getCourseDetails(courseTabIndex), expectedDetails);

        String[][] expectedValues = new String[sessions.length][5];
        for (int i = 0; i < sessions.length; i++) {
            expectedValues[i] = getExpectedSessionDetails(sessions[i]);
        }
        verifyTableBodyValues(getSessionsTable(courseTabIndex), expectedValues);
    }

    public void verifySessionDetails(int courseTabIndex, int sessionIndex, FeedbackSessionAttributes session) {
        String[] expectedValues = getExpectedSessionDetails(session);
        WebElement sessionRow = getSessionsTable(courseTabIndex).findElements(By.cssSelector("tbody tr")).get(sessionIndex);
        verifyTableRowValues(sessionRow, expectedValues);
    }

    public void verifyNumCourses(int expectedNum) {
        assertEquals(getNumCourses(), expectedNum);
    }

    public void verifyResponseRate(int courseTabIndex, int sessionIndex, String expectedResponseRate) {
        assertEquals(expectedResponseRate, getResponseRate(courseTabIndex, sessionIndex));
    }

    public void copySession(int courseTabIndex, int sessionIndex, CourseAttributes copyToCourse, String newSessionName) {
        WebElement copyFsModal = clickCopyButtonInTable(courseTabIndex, sessionIndex);
        fillTextBox(copyFsModal.findElement(By.id("copy-session-name")), newSessionName);
        selectCourseToCopyToInModal(copyFsModal, copyToCourse.getId());
        click(browser.driver.findElement(By.id("btn-confirm-copy-course")));
    }

    public void publishSessionResults(int courseTabIndex, int sessionIndex) {
        WebElement courseTab = getCourseTab(courseTabIndex);
        click(courseTab.findElement(By.id("btn-results-" + sessionIndex)));
        clickAndConfirm(courseTab.findElement(By.id("btn-publish-" + sessionIndex)));
    }

    public void unpublishSessionResults(int courseTabIndex, int sessionIndex) {
        WebElement courseTab = getCourseTab(courseTabIndex);
        click(courseTab.findElement(By.id("btn-results-" + sessionIndex)));
        clickAndConfirm(courseTab.findElement(By.id("btn-unpublish-" + sessionIndex)));
    }

    public void sendReminderEmail(int courseTabIndex, int sessionIndex, StudentAttributes student) {
        WebElement courseTab = getCourseTab(courseTabIndex);
        click(courseTab.findElement(By.id("btn-remind-" + sessionIndex)));
        selectStudentToEmail(student.email);
        click(browser.driver.findElement(By.id("btn-confirm-send-reminder")));
    }

    public void resendResultsLink(int courseTabIndex, int sessionIndex, StudentAttributes student) {
        WebElement courseTab = getCourseTab(courseTabIndex);
        click(courseTab.findElement(By.id("btn-results-" + sessionIndex)));
        click(waitForElementPresence(By.id("btn-resend-" + sessionIndex)));
        selectStudentToEmail(student.email);
        click(browser.driver.findElement(By.id("btn-confirm-resend-results")));
    }

    public void downloadResults(int courseTabIndex, int sessionIndex) {
        WebElement courseTab = getCourseTab(courseTabIndex);
        click(courseTab.findElement(By.id("btn-results-" + sessionIndex)));
        click(waitForElementPresence(By.id("btn-download-" + sessionIndex)));
    }

    public void deleteSession(int courseTabIndex, int sessionIndex) {
        WebElement courseTab = getCourseTab(courseTabIndex);
        clickAndConfirm(courseTab.findElement(By.id("btn-soft-delete-" + sessionIndex)));
        waitUntilAnimationFinish();
    }

    public void archiveCourse(int courseTabIndex) {
        WebElement courseTab = getCourseTab(courseTabIndex);
        click(courseTab.findElement(By.id("btn-course")));
        clickAndConfirm(courseTab.findElement(By.id("btn-archive-course")));
        waitUntilAnimationFinish();
    }

    public void deleteCourse(int courseTabIndex) {
        WebElement courseTab = getCourseTab(courseTabIndex);
        click(courseTab.findElement(By.id("btn-course")));
        clickAndConfirm(courseTab.findElement(By.id("btn-delete-course")));
        waitUntilAnimationFinish();
    }

    public void sortCoursesById() {
        click(browser.driver.findElement(By.id("sort-course-id")));
        waitUntilAnimationFinish();
    }

    public void sortCoursesByName() {
        click(browser.driver.findElement(By.id("sort-course-name")));
        waitUntilAnimationFinish();
    }

    public void sortCoursesByCreationDate() {
        click(browser.driver.findElement(By.id("sort-course-date")));
        waitUntilAnimationFinish();
    }

    private int getNumCourses() {
        return browser.driver.findElements(By.id("course-tab")).size();
    }

    private WebElement getCourseTab(int courseTabIndex) {
        return browser.driver.findElements(By.id("course-tab")).get(courseTabIndex);
    }

    private String getCourseDetails(int courseTabIndex) {
        WebElement courseTab = getCourseTab(courseTabIndex);
        return courseTab.findElement(By.id("course-details")).getText();
    }

    private WebElement getSessionsTable(int courseTabIndex) {
        return getCourseTab(courseTabIndex).findElement(By.id("sessions-table"));
    }

    private String getDateString(Instant instant, ZoneId timeZone) {
        return DateTimeFormatter
                .ofPattern("d MMM h:mm a")
                .format(instant.atZone(timeZone));
    }

    private String[] getExpectedSessionDetails(FeedbackSessionAttributes session) {
        String[] details = new String[5];
        details[0] = session.getFeedbackSessionName();
        details[1] = getDateString(session.getStartTime(), session.getTimeZone());
        details[2] = getDateString(session.getEndTime(), session.getTimeZone());

        if (session.isClosed()) {
            details[3] = "Closed";
        } else if (session.isVisible() && (session.isOpened() || session.isInGracePeriod())) {
            details[3] = "Open";
        } else {
            details[3] = "Awaiting";
        }
        details[4] = session.isPublished() ? "Published" : "Not Published";
        return details;
    }

    private String getResponseRate(int courseTabIndex, int sessionIndex) {
        WebElement showButton = null;
        try {
            showButton = getCourseTab(courseTabIndex).findElement(By.id("show-response-rate-" + sessionIndex));
        } catch (NoSuchElementException e) {
            // continue
        }
        if (showButton != null) {
            click(showButton);
        }
        return waitForElementPresence(By.id("response-rate-" + sessionIndex)).getText();
    }

    private WebElement clickCopyButtonInTable(int courseTabIndex, int sessionIndex) {
        click(getCourseTab(courseTabIndex).findElement(By.id("btn-copy-" + sessionIndex)));
        return waitForElementPresence(By.id("copy-course-modal"));
    }

    private void selectCourseToCopyToInModal(WebElement copyFsModal, String courseToCopyId) {
        List<WebElement> options = copyFsModal.findElements(By.className("form-check"));
        for (WebElement option : options) {
            String courseId = option.findElement(By.cssSelector("label span")).getText();
            if (courseId.equals(courseToCopyId)) {
                click(option.findElement(By.tagName("input")));
                break;
            }
        }
    }

    private void selectStudentToEmail(String studentEmail) {
        WebElement studentList = waitForElementPresence(By.id("student-list-table"));

        List<WebElement> rows = studentList.findElements(By.tagName("tr"));
        for (WebElement row : rows) {
            List<WebElement> cells = row.findElements(By.cssSelector("td"));
            if (cells.isEmpty()) {
                continue;
            }
            if (cells.get(4).getText().equals(studentEmail)) {
                click(cells.get(0).findElement(By.tagName("input")));
                break;
            }
        }
    }
}
