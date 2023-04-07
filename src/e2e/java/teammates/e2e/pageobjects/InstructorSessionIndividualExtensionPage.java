package teammates.e2e.pageobjects;

import static org.junit.Assert.assertEquals;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.TimeHelper;

/**
 * Represents the instructor session individual extension page.
 */
public class InstructorSessionIndividualExtensionPage extends AppPage {

    private static final int STUDENT_SECTION_INDEX = 1;
    private static final int STUDENT_TEAM_INDEX = 2;
    private static final int STUDENT_NAME_INDEX = 3;
    private static final int STUDENT_EMAIL_INDEX = 4;
    private static final int STUDENT_DEADLINE_INDEX = 5;
    private static final int INSTRUCTOR_NAME_INDEX = 1;
    private static final int INSTRUCTOR_EMAIL_INDEX = 2;
    private static final int INSTRUCTOR_ROLE_INDEX = 3;
    private static final int INSTRUCTOR_DEADLINE_INDEX = 4;
    private static final String DATETIME_DISPLAY_FORMAT = "EEE, dd MMM yyyy, hh:mm a X";

    @FindBy(id = "extend-btn")
    private WebElement extendDeadlinesButton;

    @FindBy(id = "delete-btn")
    private WebElement deleteDeadlinesButton;

    @FindBy(id = "student-list-table")
    private WebElement studentListTable;

    @FindBy(id = "instructor-list-table")
    private WebElement instructorListTable;

    public InstructorSessionIndividualExtensionPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageTitle().contains("Individual Deadline Extensions");
    }

    public void verifyDeadlineDetails(FeedbackSessionAttributes session,
            Collection<StudentAttributes> students, Collection<InstructorAttributes> instructors) {
        Map<String, String> studentDeadlines = getStudentDeadlines(session, students);
        Map<String, String> instructorDeadlines = getInstructorDeadlines(session, instructors);
        Map<String, StudentAttributes> studentMap = getStudentsMap(students);
        Map<String, InstructorAttributes> instructorMap = getInstructorsMap(instructors);

        verifyStudentDeadlines(studentDeadlines, studentMap);
        verifyInstructorDeadlines(instructorDeadlines, instructorMap);
    }

    private Map<String, String> getStudentDeadlines(FeedbackSessionAttributes session,
            Collection<StudentAttributes> students) {
        Map<String, Instant> deadlines = session.getStudentDeadlines();
        Map<String, String> deadlineStrings = new HashMap<>();
        students.forEach(student -> {
            Instant deadline = deadlines.getOrDefault(student.getEmail(), session.getEndTime());
            deadlineStrings.put(student.getEmail(), formatDeadline(deadline, session.getTimeZone()));
        });
        return deadlineStrings;
    }

    private Map<String, String> getInstructorDeadlines(FeedbackSessionAttributes session,
            Collection<InstructorAttributes> instructors) {
        Map<String, Instant> deadlines = session.getInstructorDeadlines();
        Map<String, String> deadlineStrings = new HashMap<>();
        instructors.forEach(instructor -> {
            Instant deadline = deadlines.getOrDefault(instructor.getEmail(), session.getEndTime());
            deadlineStrings.put(instructor.getEmail(), formatDeadline(deadline, session.getTimeZone()));
        });
        return deadlineStrings;
    }

    private String formatDeadline(Instant deadline, String timezone) {
        Instant adjustedDeadline = TimeHelper.getMidnightAdjustedInstantBasedOnZone(deadline, timezone, false);
        return TimeHelper.formatInstant(adjustedDeadline, timezone, DATETIME_DISPLAY_FORMAT);
    }

    private Map<String, StudentAttributes> getStudentsMap(Collection<StudentAttributes> students) {
        return students.stream().collect(Collectors.toMap(StudentAttributes::getEmail, Function.identity()));
    }

    private Map<String, InstructorAttributes> getInstructorsMap(Collection<InstructorAttributes> instructors) {
        return instructors.stream().collect(Collectors.toMap(InstructorAttributes::getEmail, Function.identity()));
    }

    private void verifyStudentDeadlines(Map<String, String> studentDeadlines,
            Map<String, StudentAttributes> students) {
        List<WebElement> studentRows = studentListTable.findElements(By.cssSelector("tbody tr"));

        assertEquals(students.size(), studentRows.size());

        for (var row : studentRows) {
            List<WebElement> cols = row.findElements(By.cssSelector("td"));
            String section = cols.get(STUDENT_SECTION_INDEX).getText();
            String team = cols.get(STUDENT_TEAM_INDEX).getText();
            String name = cols.get(STUDENT_NAME_INDEX).getText();
            String email = cols.get(STUDENT_EMAIL_INDEX).getText();
            String deadline = cols.get(STUDENT_DEADLINE_INDEX).getText();

            String expectedDeadline = studentDeadlines.get(email);
            StudentAttributes expectedStudent = students.get(email);

            if (expectedStudent.getSection().isEmpty()) {
                assertEquals("None", section);
            } else {
                assertEquals(expectedStudent.getSection(), section);
            }
            assertEquals(expectedStudent.getTeam(), team);
            assertEquals(expectedStudent.getName(), name);
            assertEquals(expectedStudent.getEmail(), email);
            assertEquals(expectedDeadline, deadline);
        }
    }

    private void verifyInstructorDeadlines(Map<String, String> instructorDeadlines,
            Map<String, InstructorAttributes> instructors) {
        List<WebElement> instructorRows = instructorListTable.findElements(By.cssSelector("tbody tr"));

        assertEquals(instructors.size(), instructorRows.size());

        for (var row : instructorRows) {
            List<WebElement> cols = row.findElements(By.cssSelector("td"));
            String name = cols.get(INSTRUCTOR_NAME_INDEX).getText();
            String email = cols.get(INSTRUCTOR_EMAIL_INDEX).getText();
            String role = cols.get(INSTRUCTOR_ROLE_INDEX).getText();
            String deadline = cols.get(INSTRUCTOR_DEADLINE_INDEX).getText();

            String expectedDeadline = instructorDeadlines.get(email);
            InstructorAttributes expectedInstructor = instructors.get(email);

            assertEquals(expectedInstructor.getName(), name);
            assertEquals(expectedInstructor.getEmail(), email);
            assertEquals(expectedInstructor.getRole(), role);
            assertEquals(expectedDeadline, deadline);
        }
    }

    public void selectStudents(int... indexes) {
        for (int index : indexes) {
            selectStudent(index);
        }
    }

    public void selectStudent(int index) {
        click(waitForElementPresence(By.id("student-checkbox-" + index)));
        waitUntilAnimationFinish();
    }

    public void selectInstructor(int index) {
        click(waitForElementPresence(By.id("instructor-checkbox-" + index)));
        waitUntilAnimationFinish();
    }

    public void selectAllStudents() {
        click(waitForElementPresence(By.id("select-all-student-btn")));
        waitUntilAnimationFinish();
    }

    public void selectAllInstructors() {
        click(waitForElementPresence(By.id("select-all-instructor-btn")));
        waitUntilAnimationFinish();
    }

    public void deleteDeadlines(boolean notifyUsers) {
        click(deleteDeadlinesButton);
        confirmChangesToDeadlineExtensions(notifyUsers);
    }

    public void extendDeadlineByTwelveHours(boolean notifyUsers) {
        extendDeadlineBy("12 hours", notifyUsers);
    }

    public void extendDeadlineByOneDay(boolean notifyUsers) {
        extendDeadlineBy("1 day", notifyUsers);
    }

    private void extendDeadlineBy(String by, boolean notifyUsers) {
        click(extendDeadlinesButton);
        WebElement dropdown = waitForElementPresence(By.id("extend-by-dropdown"));
        selectDropdownOptionByValue(dropdown, by);
        click(browser.driver.findElement(By.className("modal-btn-ok")));
        confirmChangesToDeadlineExtensions(notifyUsers);
    }

    public void extendDeadlineToOneDayAway(FeedbackSessionAttributes session, boolean notifyUsers) {
        click(extendDeadlinesButton);

        Instant extendedDeadline = session.getEndTime().plus(Duration.ofDays(1));
        extendedDeadline = TimeHelper.getMidnightAdjustedInstantBasedOnZone(extendedDeadline, session.getTimeZone(), false);
        click(waitForElementPresence(By.id("extend-deadline-to")));

        // set time
        WebElement timePicker = browser.driver.findElement(By.id("submission-end-time"));
        WebElement timePickerDropdown = timePicker.findElement(By.tagName("select"));
        selectDropdownOptionByText(timePickerDropdown, getTimeString(extendedDeadline, session.getTimeZone()));

        // set date
        WebElement datePicker = browser.driver.findElement(By.id("submission-end-date"));
        fillDatePicker(datePicker, extendedDeadline, session.getTimeZone());

        click(browser.driver.findElement(By.className("modal-btn-ok")));
        confirmChangesToDeadlineExtensions(notifyUsers);
    }

    private void confirmChangesToDeadlineExtensions(boolean notifyUsers) {
        waitUntilAnimationFinish();

        WebElement notifyUsersCheckbox = browser.driver.findElement(By.id("flexCheckChecked"));
        if (notifyUsers) {
            click(notifyUsersCheckbox);
        }

        WebElement okButton = browser.driver.findElement(By.className("modal-btn-ok"));
        clickDismissModalButtonAndWaitForModalHidden(okButton);
        waitForPageToLoad(true);
    }

    private String getTimeString(Instant instant, String timezone) {
        return TimeHelper.formatInstant(instant, timezone, "HH:mm") + "H";
    }

}
