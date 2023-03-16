package teammates.e2e.pageobjects;

import static org.junit.Assert.assertEquals;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;

/**
 * Represents the "Sessions" page for Instructors.
 */
public class InstructorFeedbackSessionsPage extends AppPage {

    @FindBy(id = "btn-add-session")
    private WebElement addSessionButton;

    @FindBy(id = "session-type")
    private WebElement sessionTypeDropdown;

    @FindBy(id = "add-course-id")
    private WebElement courseIdDropdown;

    @FindBy(id = "add-session-name")
    private WebElement sessionNameTextBox;

    @FindBy(id = "instructions")
    private WebElement instructionsEditor;

    @FindBy(id = "submission-start-date")
    private WebElement startDateBox;

    @FindBy(id = "submission-start-time")
    private WebElement startTimeDropdown;

    @FindBy(id = "submission-end-date")
    private WebElement endDateBox;

    @FindBy(id = "submission-end-time")
    private WebElement endTimeDropdown;

    @FindBy(id = "grace-period")
    private WebElement gracePeriodDropdown;

    @FindBy(id = "btn-change-visibility")
    private WebElement changeVisibilityButton;

    @FindBy(id = "session-visibility-custom")
    private WebElement customSessionVisibleTimeButton;

    @FindBy(id = "session-visibility-date")
    private WebElement sessionVisibilityDateBox;

    @FindBy(id = "session-visibility-time")
    private WebElement sessionVisibilityTimeDropdown;

    @FindBy(id = "session-visibility-at-open")
    private WebElement openSessionVisibleTimeButton;

    @FindBy(id = "response-visibility-custom")
    private WebElement customResponseVisibleTimeButton;

    @FindBy(id = "response-visibility-date")
    private WebElement responseVisibilityDateBox;

    @FindBy(id = "response-visibility-time")
    private WebElement responseVisibilityTimeDropdown;

    @FindBy(id = "response-visibility-immediately")
    private WebElement immediateResponseVisibleTimeButton;

    @FindBy(id = "response-visibility-manually")
    private WebElement manualResponseVisibleTimeButton;

    @FindBy(id = "btn-change-email")
    private WebElement changeEmailButton;

    @FindBy(id = "email-opening")
    private WebElement openingSessionEmailCheckbox;

    @FindBy(id = "email-closing")
    private WebElement closingSessionEmailCheckbox;

    @FindBy(id = "email-published")
    private WebElement publishedSessionEmailCheckbox;

    @FindBy(id = "btn-create-session")
    private WebElement createSessionButton;

    @FindBy(className = "sessions-table")
    private WebElement sessionsTable;

    @FindBy(id = "deleted-sessions-heading")
    private WebElement deleteTableHeading;

    @FindBy(id = "btn-restore-all")
    private WebElement restoreAllButton;

    @FindBy(id = "btn-delete-all")
    private WebElement deleteAllButton;

    @FindBy(id = "deleted-sessions-table")
    private WebElement deletedSessionsTable;

    public InstructorFeedbackSessionsPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageTitle().contains("Feedback Sessions");
    }

    public void verifySessionsTable(FeedbackSessionAttributes[] sessions) {
        String[][] expectedValues = new String[sessions.length][4];
        for (int i = 0; i < sessions.length; i++) {
            expectedValues[i] = getSessionDetails(sessions[i]);
        }
        verifyTableBodyValues(sessionsTable, expectedValues);
    }

    public void verifySessionDetails(FeedbackSessionAttributes session) {
        String[] expectedValues = getSessionDetails(session);
        int rowId = getFeedbackSessionRowId(session.getCourseId(), session.getFeedbackSessionName());
        verifyTableRowValues(sessionsTable.findElements(By.cssSelector("tbody tr")).get(rowId), expectedValues);
    }

    public void verifySoftDeletedSessionsTable(FeedbackSessionAttributes[] sessions) {
        showDeleteTable();
        String[][] expectedValues = new String[sessions.length][4];
        for (int i = 0; i < sessions.length; i++) {
            expectedValues[i] = getSoftDeletedSessionDetails(sessions[i]);
        }
        verifyTableBodyValues(deletedSessionsTable, expectedValues);
    }

    public void verifyNumSoftDeleted(int expected) {
        assertEquals(getNumSoftDeletedFeedbackSessions(), expected);
    }

    public void verifyResponseRate(FeedbackSessionAttributes session, String expectedResponseRate) {
        int rowId = getFeedbackSessionRowId(session.getCourseId(), session.getFeedbackSessionName());
        assertEquals(expectedResponseRate, getResponseRate(rowId));
    }

    public void addFeedbackSession(FeedbackSessionAttributes newSession, boolean isUsingTemplate) {
        clickAddSessionButton();
        waitForElementPresence(By.id("session-edit-form"));

        if (isUsingTemplate) {
            selectDropdownOptionByText(sessionTypeDropdown,
                    "session using template: team peer feedback (percentage-based)");
        } else {
            selectDropdownOptionByText(sessionTypeDropdown, "session with my own questions");
        }
        selectDropdownOptionByText(courseIdDropdown, newSession.getCourseId());
        fillTextBox(sessionNameTextBox, newSession.getFeedbackSessionName());
        setInstructions(newSession.getInstructions());
        setSessionStartDateTime(newSession.getStartTime(), newSession.getTimeZone());
        setSessionEndDateTime(newSession.getEndTime(), newSession.getTimeZone());
        selectGracePeriod(newSession.getGracePeriodMinutes());
        setVisibilitySettings(newSession);
        setEmailSettings(newSession);

        clickCreateSessionButton();
        waitForSessionEditPage();
    }

    public void addCopyOfSession(FeedbackSessionAttributes sessionToCopy, CourseAttributes copyToCourse,
                                 String newSessionName) {
        clickAddSessionButton();
        click(browser.driver.findElement(By.id("btn-copy-session")));

        selectCourseToCopy(copyToCourse.getId());
        fillTextBox(browser.driver.findElement(By.id("copy-session-name")), newSessionName);
        selectSessionToCopy(sessionToCopy.getCourseId(), sessionToCopy.getFeedbackSessionName());

        clickConfirmCopySessionButton();
        waitForConfirmationModalAndClickOk();
        waitForSessionEditPage();
    }

    public void copySession(FeedbackSessionAttributes sessionToCopy, CourseAttributes copyToCourse,
                            String newSessionName) {
        String copyFromCourse = sessionToCopy.getCourseId();
        String sessionName = sessionToCopy.getFeedbackSessionName();
        WebElement copyFsModal = clickCopyButtonInTable(copyFromCourse, sessionName);

        fillTextBox(copyFsModal.findElement(By.id("copy-session-name")), newSessionName);
        selectCourseToCopyToInModal(copyFsModal, copyToCourse.getId());

        click(browser.driver.findElement(By.id("btn-confirm-copy-course")));
    }

    public void moveToRecycleBin(FeedbackSessionAttributes sessionToDelete) {
        int rowId = getFeedbackSessionRowId(sessionToDelete.getCourseId(), sessionToDelete.getFeedbackSessionName());
        clickAndConfirm(browser.driver.findElement(By.className("btn-soft-delete-" + rowId)));
        waitUntilAnimationFinish();
    }

    public void restoreSession(FeedbackSessionAttributes sessionToRestore) {
        showDeleteTable();
        int rowId = getSoftDeletedFeedbackSessionRowId(sessionToRestore.getCourseId(),
                sessionToRestore.getFeedbackSessionName());
        click(browser.driver.findElement(By.id("btn-restore-" + rowId)));
        waitUntilAnimationFinish();
    }

    public void deleteSession(FeedbackSessionAttributes sessionToRestore) {
        showDeleteTable();
        int rowId = getSoftDeletedFeedbackSessionRowId(sessionToRestore.getCourseId(),
                sessionToRestore.getFeedbackSessionName());
        clickAndConfirm(browser.driver.findElement(By.id("btn-delete-" + rowId)));
        waitUntilAnimationFinish();
    }

    public void restoreAllSessions() {
        click(restoreAllButton);
        waitUntilAnimationFinish();
    }

    public void deleteAllSessions() {
        clickAndConfirm(deleteAllButton);
        waitUntilAnimationFinish();
    }

    public void showDeleteTable() {
        if (!isElementVisible(By.id("sort-deleted-course-id"))) {
            click(deleteTableHeading);
            waitUntilAnimationFinish();
        }
    }

    public void sendReminderEmailToSelectedStudent(FeedbackSessionAttributes session, StudentAttributes student) {
        int rowId = getFeedbackSessionRowId(session.getCourseId(), session.getFeedbackSessionName());

        click(browser.driver.findElement(By.className("btn-remind-" + rowId)));
        click(waitForElementPresence(By.className("btn-remind-selected-" + rowId)));
        selectStudentToEmail(student.getEmail());
        click(browser.driver.findElement(By.id("btn-confirm-send-reminder")));
    }

    public void sendReminderEmailToNonSubmitters(FeedbackSessionAttributes session) {
        int rowId = getFeedbackSessionRowId(session.getCourseId(), session.getFeedbackSessionName());

        click(browser.driver.findElement(By.className("btn-remind-" + rowId)));
        click(waitForElementPresence(By.className("btn-remind-all-" + rowId)));
        click(waitForElementPresence(By.id("btn-confirm-send-reminder")));
    }

    public void resendResultsLink(FeedbackSessionAttributes session, StudentAttributes student) {
        int rowId = getFeedbackSessionRowId(session.getCourseId(), session.getFeedbackSessionName());

        click(browser.driver.findElement(By.className("btn-results-" + rowId)));
        click(waitForElementPresence(By.className("btn-resend-" + rowId)));
        selectStudentToEmail(student.getEmail());

        click(browser.driver.findElement(By.id("btn-confirm-resend-results")));
    }

    public void publishSessionResults(FeedbackSessionAttributes sessionToPublish) {
        int rowId = getFeedbackSessionRowId(sessionToPublish.getCourseId(), sessionToPublish.getFeedbackSessionName());
        click(browser.driver.findElement(By.className("btn-results-" + rowId)));
        clickAndConfirm(waitForElementPresence(By.className("btn-publish-" + rowId)));
    }

    public void unpublishSessionResults(FeedbackSessionAttributes sessionToPublish) {
        int rowId = getFeedbackSessionRowId(sessionToPublish.getCourseId(), sessionToPublish.getFeedbackSessionName());
        click(browser.driver.findElement(By.className("btn-results-" + rowId)));
        clickAndConfirm(waitForElementPresence(By.className("btn-unpublish-" + rowId)));
    }

    public void downloadResults(FeedbackSessionAttributes session) {
        int rowId = getFeedbackSessionRowId(session.getCourseId(), session.getFeedbackSessionName());
        click(browser.driver.findElement(By.className("btn-results-" + rowId)));
        click(waitForElementPresence(By.className("btn-download-" + rowId)));
    }

    public void sortBySessionsName() {
        click(waitForElementPresence(By.className("sort-session-name")));
    }

    public void sortByCourseId() {
        click(waitForElementPresence(By.className("sort-course-id")));
    }

    private String[] getSessionDetails(FeedbackSessionAttributes session) {
        String[] details = new String[4];
        details[0] = session.getCourseId();
        details[1] = session.getFeedbackSessionName();
        if (session.isClosed()) {
            details[2] = "Closed";
        } else if (session.isVisible() && (session.isOpened() || session.isInGracePeriod())) {
            details[2] = "Open";
        } else {
            details[2] = "Awaiting";
        }
        details[3] = session.isPublished() ? "Published" : "Not Published";
        return details;
    }

    private String[] getSoftDeletedSessionDetails(FeedbackSessionAttributes session) {
        String[] details = new String[4];
        details[0] = session.getCourseId();
        details[1] = session.getFeedbackSessionName();
        details[2] = getSimpleDateString(session.getCreatedTime(), session.getTimeZone());
        details[3] = getSimpleDateString(session.getDeletedTime(), session.getTimeZone());
        return details;
    }

    private String getSimpleDateString(Instant instant, String timeZone) {
        return getDisplayedDateTime(instant, timeZone, "dd MMM, yyyy");
    }

    private String getTimeString(Instant instant, String timeZone) {
        ZonedDateTime dateTime = instant.atZone(ZoneId.of(timeZone));
        if (dateTime.getHour() == 0 && dateTime.getMinute() == 0) {
            return "23:59H";
        }
        return getDisplayedDateTime(instant, timeZone, "HH:00") + "H";
    }

    private String getResponseRate(int rowId) {
        By showButtonId = By.className("show-response-rate-" + rowId);
        if (isElementPresent(showButtonId)) {
            click(showButtonId);
        }
        return waitForElementPresence(By.className("response-rate-" + rowId)).getText();
    }

    private void clickAddSessionButton() {
        click(addSessionButton);
    }

    private void setInstructions(String newInstructions) {
        writeToRichTextEditor(instructionsEditor, newInstructions);
    }

    private void setSessionStartDateTime(Instant startInstant, String timeZone) {
        setDateTime(startDateBox, startTimeDropdown, startInstant, timeZone);
    }

    private void setSessionEndDateTime(Instant endInstant, String timeZone) {
        setDateTime(endDateBox, endTimeDropdown, endInstant, timeZone);
    }

    private void setVisibilityDateTime(Instant startInstant, String timeZone) {
        setDateTime(sessionVisibilityDateBox, sessionVisibilityTimeDropdown, startInstant, timeZone);
    }

    private void setResponseDateTime(Instant endInstant, String timeZone) {
        setDateTime(responseVisibilityDateBox, responseVisibilityTimeDropdown, endInstant, timeZone);
    }

    private void setDateTime(WebElement dateBox, WebElement timeBox, Instant startInstant, String timeZone) {
        fillDatePicker(dateBox, startInstant, timeZone);

        selectDropdownOptionByText(timeBox.findElement(By.tagName("select")), getTimeString(startInstant, timeZone));
    }

    private void selectGracePeriod(long gracePeriodMinutes) {
        selectDropdownOptionByText(gracePeriodDropdown, gracePeriodMinutes + " min");
    }

    private void setVisibilitySettings(FeedbackSessionAttributes newFeedbackSession) {
        showVisibilitySettings();

        setSessionVisibilitySettings(newFeedbackSession);
        setResponseVisibilitySettings(newFeedbackSession);
    }

    private void setSessionVisibilitySettings(FeedbackSessionAttributes newFeedbackSession) {
        Instant sessionDateTime = newFeedbackSession.getSessionVisibleFromTime();
        if (sessionDateTime.equals(Const.TIME_REPRESENTS_FOLLOW_OPENING)) {
            click(openSessionVisibleTimeButton);
        } else {
            click(customSessionVisibleTimeButton);
            setVisibilityDateTime(sessionDateTime, newFeedbackSession.getTimeZone());
        }
    }

    private void setResponseVisibilitySettings(FeedbackSessionAttributes newFeedbackSession) {
        Instant responseDateTime = newFeedbackSession.getResultsVisibleFromTime();
        if (responseDateTime.equals(Const.TIME_REPRESENTS_FOLLOW_VISIBLE)) {
            click(immediateResponseVisibleTimeButton);
        } else if (responseDateTime.equals(Const.TIME_REPRESENTS_LATER)) {
            click(manualResponseVisibleTimeButton);
        } else {
            click(customResponseVisibleTimeButton);
            setResponseDateTime(responseDateTime, newFeedbackSession.getTimeZone());
        }
    }

    private void setEmailSettings(FeedbackSessionAttributes newFeedbackSessionDetails) {
        showEmailSettings();
        if (newFeedbackSessionDetails.isOpeningEmailEnabled() != openingSessionEmailCheckbox.isSelected()) {
            click(openingSessionEmailCheckbox);
        }
        if (newFeedbackSessionDetails.isClosingEmailEnabled() != closingSessionEmailCheckbox.isSelected()) {
            click(closingSessionEmailCheckbox);
        }
        if (newFeedbackSessionDetails.isPublishedEmailEnabled() != publishedSessionEmailCheckbox.isSelected()) {
            click(publishedSessionEmailCheckbox);
        }
    }

    private void showVisibilitySettings() {
        if (isElementPresent(By.id("btn-change-visibility"))) {
            click(changeVisibilityButton);
        }
    }

    private void showEmailSettings() {
        if (isElementPresent(By.id("btn-change-email"))) {
            click(changeEmailButton);
        }
    }

    private void clickCreateSessionButton() {
        click(createSessionButton);
    }

    private void selectCourseToCopy(String courseToCopyId) {
        WebElement courseIdDropdown = waitForElementPresence(By.id("copy-course-id"));
        selectDropdownOptionByText(courseIdDropdown, courseToCopyId);
    }

    private void selectSessionToCopy(String copyFromCourse, String sessionNameToCopy) {
        WebElement table = browser.driver.findElement(By.id("copy-selection-table"));
        List<WebElement> rows = table.findElements(By.tagName("tr"));

        for (WebElement row : rows) {
            List<WebElement> cells = row.findElements(By.tagName("td"));
            if (cells.isEmpty()) {
                continue;
            }
            if (cells.get(1).getText().equals(copyFromCourse) && cells.get(2).getText().equals(sessionNameToCopy)) {
                click(cells.get(0).findElement(By.tagName("input")));
                break;
            }
        }
    }

    private void clickConfirmCopySessionButton() {
        click(browser.driver.findElement(By.id("btn-confirm-copy")));
    }

    private WebElement clickCopyButtonInTable(String courseId, String sessionName) {
        int rowId = getFeedbackSessionRowId(courseId, sessionName);
        click(browser.driver.findElement(By.className("btn-copy-" + rowId)));
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

    private int getFeedbackSessionRowId(String courseId, String sessionName) {
        int i = 0;
        while (i < getNumFeedbackSessions()) {
            if (getFeedbackSessionCourseId(i).equals(courseId)
                    && getFeedbackSessionName(i).equals(sessionName)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    private int getSoftDeletedFeedbackSessionRowId(String courseId, String sessionName) {
        int i = 0;

        while (i < getNumSoftDeletedFeedbackSessions()) {
            if (getSoftDeletedFeedbackSessionCourseId(i).equals(courseId)
                    && getSoftDeletedFeedbackSessionName(i).equals(sessionName)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    private int getNumFeedbackSessions() {
        return sessionsTable.findElements(By.cssSelector("tbody tr")).size();
    }

    private int getNumSoftDeletedFeedbackSessions() {
        if (!isElementPresent(By.id("deleted-sessions-table"))) {
            return 0;
        }
        return deletedSessionsTable.findElements(By.cssSelector("tbody tr")).size();
    }

    private String getFeedbackSessionCourseId(int rowId) {
        WebElement row = sessionsTable.findElements(By.cssSelector("tbody tr")).get(rowId);
        return row.findElements(By.tagName("td")).get(0).getText();
    }

    private String getSoftDeletedFeedbackSessionCourseId(int rowId) {
        WebElement row = deletedSessionsTable.findElements(By.cssSelector("tbody tr")).get(rowId);
        return row.findElements(By.tagName("td")).get(0).getText();
    }

    private String getFeedbackSessionName(int rowId) {
        WebElement row = sessionsTable.findElements(By.cssSelector("tbody tr")).get(rowId);
        return row.findElements(By.tagName("td")).get(1).getText();
    }

    private String getSoftDeletedFeedbackSessionName(int rowId) {
        WebElement row = deletedSessionsTable.findElements(By.cssSelector("tbody tr")).get(rowId);
        return row.findElements(By.tagName("td")).get(1).getText();
    }

    private void waitForSessionEditPage() {
        waitForElementPresence(By.id("btn-fs-edit"));
    }
}
