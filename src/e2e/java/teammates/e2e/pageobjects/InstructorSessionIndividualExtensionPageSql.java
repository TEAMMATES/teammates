package teammates.e2e.pageobjects;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

import teammates.common.util.TimeHelper;
import teammates.storage.sqlentity.DeadlineExtension;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;

/**
 * Represents the instructor session individual extension page.
 */
public class InstructorSessionIndividualExtensionPageSql extends AppPage {

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

    public InstructorSessionIndividualExtensionPageSql(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageTitle().contains("Individual Deadline Extensions");
    }

    public void verifyDeadlineDetails(FeedbackSession session,
                                      Collection<Student> students, Collection<Instructor> instructors) {
        Map<String, String> studentDeadlines = getStudentDeadlines(session, students);
        Map<String, String> instructorDeadlines = getInstructorDeadlines(session, instructors);
        Map<String, Student> studentMap = getStudentsMap(students);
        Map<String, Instructor> instructorMap = getInstructorsMap(instructors);

        verifyStudentDeadlines(studentDeadlines, studentMap);
        verifyInstructorDeadlines(instructorDeadlines, instructorMap);
    }

    private Map<String, String> getStudentDeadlines(FeedbackSession session,
                                                    Collection<Student> students) {
        Map<String, Instant> deadlines = getDeadlineExtensionsMap(session);
        Map<String, String> deadlineStrings = new HashMap<>();
        students.forEach(student -> {
            Instant deadline = deadlines.getOrDefault(student.getEmail(), session.getEndTime());
            deadlineStrings.put(student.getEmail(), formatDeadline(deadline, session.getCourse().getTimeZone()));
        });
        return deadlineStrings;
    }

    private Map<String, String> getInstructorDeadlines(FeedbackSession session,
                                                       Collection<Instructor> instructors) {
        Map<String, Instant> deadlines = getDeadlineExtensionsMap(session);
        Map<String, String> deadlineStrings = new HashMap<>();
        instructors.forEach(instructor -> {
            Instant deadline = deadlines.getOrDefault(instructor.getEmail(), session.getEndTime());
            deadlineStrings.put(instructor.getEmail(), formatDeadline(deadline, session.getCourse().getTimeZone()));
        });
        return deadlineStrings;
    }

    private Map<String, Instant> getDeadlineExtensionsMap(FeedbackSession session) {
        return session.getDeadlineExtensions().stream()
                .collect(Collectors.toMap(de -> de.getUser().getEmail(), DeadlineExtension::getEndTime));
    }

    private String formatDeadline(Instant deadline, String timezone) {
        Instant adjustedDeadline = TimeHelper.getMidnightAdjustedInstantBasedOnZone(deadline, timezone, false);
        return TimeHelper.formatInstant(adjustedDeadline, timezone, DATETIME_DISPLAY_FORMAT);
    }

    private Map<String, Student> getStudentsMap(Collection<Student> students) {
        return students.stream().collect(Collectors.toMap(Student::getEmail, Function.identity()));
    }

    private Map<String, Instructor> getInstructorsMap(Collection<Instructor> instructors) {
        return instructors.stream().collect(Collectors.toMap(Instructor::getEmail, Function.identity()));
    }

    private void verifyStudentDeadlines(Map<String, String> studentDeadlines,
                                        Map<String, Student> students) {
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
            Student expectedStudent = students.get(email);

            if (expectedStudent.getSectionName() == null || expectedStudent.getSectionName().isEmpty()) {
                assertEquals("None", section);
            } else {
                assertEquals(expectedStudent.getSectionName(), section);
            }
            assertEquals(expectedStudent.getTeamName(), team);
            assertEquals(expectedStudent.getName(), name);
            assertEquals(expectedStudent.getEmail(), email);
            assertEquals(expectedDeadline, deadline);
        }
    }

    private void verifyInstructorDeadlines(Map<String, String> instructorDeadlines,
                                           Map<String, Instructor> instructors) {
        List<WebElement> instructorRows = instructorListTable.findElements(By.cssSelector("tbody tr"));

        assertEquals(instructors.size(), instructorRows.size());

        for (var row : instructorRows) {
            List<WebElement> cols = row.findElements(By.cssSelector("td"));
            String name = cols.get(INSTRUCTOR_NAME_INDEX).getText();
            String email = cols.get(INSTRUCTOR_EMAIL_INDEX).getText();
            String role = cols.get(INSTRUCTOR_ROLE_INDEX).getText();
            String deadline = cols.get(INSTRUCTOR_DEADLINE_INDEX).getText();

            String expectedDeadline = instructorDeadlines.get(email);
            Instructor expectedInstructor = instructors.get(email);

            assertEquals(expectedInstructor.getName(), name);
            assertEquals(expectedInstructor.getEmail(), email);
            assertEquals(expectedInstructor.getRole().getRoleName(), role);
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

    public void extendDeadlineToOneDayAway(FeedbackSession session, boolean notifyUsers) {
        click(extendDeadlinesButton);

        Instant extendedDeadline = session.getEndTime().plus(Duration.ofDays(1));
        extendedDeadline = TimeHelper.getMidnightAdjustedInstantBasedOnZone(extendedDeadline, session.getCourse().getTimeZone(), false);
        click(waitForElementPresence(By.id("extend-deadline-to")));

        // set time
        WebElement timePicker = browser.driver.findElement(By.id("submission-end-time"));
        WebElement timePickerDropdown = timePicker.findElement(By.tagName("select"));
        selectDropdownOptionByText(timePickerDropdown, getTimeString(extendedDeadline, session.getCourse().getTimeZone()));

        // set date
        WebElement datePicker = browser.driver.findElement(By.id("submission-end-date"));
        fillDatePicker(datePicker, extendedDeadline, session.getCourse().getTimeZone());

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
